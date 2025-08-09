# Configuración de AWS S3 Bucket para Certificados ASECAPT

## Paso 1: Crear el Bucket S3

### 1.1 Acceder a AWS Console
1. Ingresar a [AWS Console](https://aws.amazon.com/console/)
2. Buscar "S3" en el buscador de servicios
3. Hacer clic en "S3"

### 1.2 Crear Nuevo Bucket
1. Hacer clic en "Create bucket"
2. **Bucket name**: `asecapt-certificates` (debe ser único globalmente)
3. **AWS Region**: Seleccionar `us-east-1` (Virginia del Norte) o la región más cercana
4. **Object Ownership**: Seleccionar "ACLs disabled (recommended)"
5. **Block Public Access settings**: Mantener todas las opciones marcadas (recomendado para seguridad)
6. **Bucket Versioning**: Habilitar (para mantener historial de archivos)
7. **Default encryption**: 
   - Encryption type: `Server-side encryption with Amazon S3 managed keys (SSE-S3)`
8. Hacer clic en "Create bucket"

## Paso 2: Configurar Política de Acceso

### 2.1 Crear Usuario IAM para Aplicación
1. Ir a IAM → Users → Create user
2. **User name**: `asecapt-certificates-user`
3. **Attach policies directly**: No seleccionar ninguna (configuraremos permisos personalizados)
4. Crear usuario

### 2.2 Crear Política Personalizada
1. Ir a IAM → Policies → Create policy
2. Seleccionar "JSON" tab
3. Pegar la siguiente política:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:PutObject",
                "s3:GetObject",
                "s3:DeleteObject",
                "s3:ListBucket"
            ],
            "Resource": [
                "arn:aws:s3:::asecapt-certificates",
                "arn:aws:s3:::asecapt-certificates/*"
            ]
        }
    ]
}
```

4. **Policy name**: `AsecaptCertificatesPolicy`
5. Crear política

### 2.3 Asignar Política al Usuario
1. Ir al usuario `asecapt-certificates-user`
2. Permissions → Add permissions → Attach policies directly
3. Buscar y seleccionar `AsecaptCertificatesPolicy`
4. Agregar permisos

### 2.4 Crear Access Keys
1. En el usuario `asecapt-certificates-user`
2. Security credentials → Create access key
3. **Use case**: Application running outside AWS
4. Guardar las credenciales:
   - `Access Key ID`
   - `Secret Access Key`

## Paso 3: Estructura de Carpetas en S3

### 3.1 Estructura Propuesta
```
asecapt-certificates-prod/
├── certificates/
│   ├── {DNI}/
│   │   ├── {INICIALES_CURSO}_{CERTIFICATE_ID}.pdf
│   │   ├── {INICIALES_CURSO}_{CERTIFICATE_ID}_QR.png
│   │   └── ...
│   └── ...
└── temp/
    └── (archivos temporales durante procesamiento)
```

### 3.2 Ejemplo de Nombres de Archivo
- **DNI**: `12345678`
- **Curso**: "Desarrollo Web Avanzado"
- **Iniciales**: `DWA`
- **Certificate ID**: `CERT001`

**Archivos resultantes**:
- Certificado: `certificates/12345678/DWA_CERT001.pdf`
- Código QR: `certificates/12345678/DWA_CERT001_QR.png`

## Paso 4: Configurar CORS (si se accede desde navegador)

### 4.1 Configurar CORS Policy
1. Ir al bucket creado
2. Permissions → Cross-origin resource sharing (CORS)
3. Agregar la siguiente configuración:

```json
[
    {
        "AllowedHeaders": [
            "*"
        ],
        "AllowedMethods": [
            "GET",
            "PUT",
            "POST",
            "DELETE"
        ],
        "AllowedOrigins": [
            "https://asecapt.com",
            "https://www.asecapt.com",
            "http://localhost:4200"
        ],
        "ExposeHeaders": []
    }
]
```

## Paso 5: Configuración en la Aplicación Spring Boot

### 5.1 Agregar Dependencias Maven
Agregar al `pom.xml`:

```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.20.26</version>
</dependency>
```

### 5.2 Variables de Entorno
Crear archivo `.env` o configurar variables del sistema:

```properties
# AWS S3 Configuration
AWS_ACCESS_KEY_ID=tu_access_key_aqui
AWS_SECRET_ACCESS_KEY=tu_secret_key_aqui
AWS_REGION=us-east-1
AWS_S3_BUCKET_NAME=asecapt-certificates-prod
```

### 5.3 Clase de Configuración S3

```java
@Configuration
public class S3Config {
    
    @Value("${aws.access.key.id}")
    private String accessKeyId;
    
    @Value("${aws.secret.access.key}")
    private String secretAccessKey;
    
    @Value("${aws.region}")
    private String region;
    
    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(region))
                .build();
    }
}
```

### 5.4 Servicio para Subir Archivos

```java
@Service
public class S3CertificateService {
    
    @Autowired
    private S3Client s3Client;
    
    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    
    public String uploadCertificate(String dni, String courseInitials, String certificateId, 
                                  byte[] fileContent, String fileExtension) {
        String key = generateCertificateKey(dni, courseInitials, certificateId, fileExtension);
        
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(getContentType(fileExtension))
                .build();
        
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileContent));
        
        return key;
    }
    
    public String uploadQRCode(String dni, String courseInitials, String certificateId, 
                              byte[] qrContent) {
        String key = generateQRKey(dni, courseInitials, certificateId);
        
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("image/png")
                .build();
        
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(qrContent));
        
        return key;
    }
    
    private String generateCertificateKey(String dni, String courseInitials, 
                                        String certificateId, String extension) {
        return String.format("certificates/%s/%s_%s.%s", 
                           dni, courseInitials, certificateId, extension);
    }
    
    private String generateQRKey(String dni, String courseInitials, String certificateId) {
        return String.format("certificates/%s/%s_%s_QR.png", 
                           dni, courseInitials, certificateId);
    }
    
    private String getContentType(String extension) {
        switch (extension.toLowerCase()) {
            case "pdf": return "application/pdf";
            case "png": return "image/png";
            case "jpg":
            case "jpeg": return "image/jpeg";
            default: return "application/octet-stream";
        }
    }
    
    public String getPublicUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", 
                           bucketName, region, key);
    }
}
```

## Paso 6: Generar Iniciales de Curso

### 6.1 Método para Generar Iniciales

```java
@Service
public class CourseInitialsService {
    
    public String generateCourseInitials(String courseTitle) {
        if (courseTitle == null || courseTitle.trim().isEmpty()) {
            return "GEN"; // General por defecto
        }
        
        // Remover palabras comunes y generar iniciales
        String[] commonWords = {"de", "del", "la", "el", "en", "para", "con", "por", "y", "o"};
        String[] words = courseTitle.trim().split("\\s+");
        
        StringBuilder initials = new StringBuilder();
        
        for (String word : words) {
            if (word.length() > 0 && !Arrays.asList(commonWords).contains(word.toLowerCase())) {
                initials.append(word.substring(0, 1).toUpperCase());
                if (initials.length() >= 3) break; // Máximo 3 iniciales
            }
        }
        
        // Si no hay suficientes iniciales, usar las primeras letras
        if (initials.length() < 2) {
            initials = new StringBuilder();
            String cleanTitle = courseTitle.replaceAll("[^a-zA-Z]", "").toUpperCase();
            for (int i = 0; i < Math.min(3, cleanTitle.length()); i++) {
                initials.append(cleanTitle.charAt(i));
            }
        }
        
        return initials.length() > 0 ? initials.toString() : "GEN";
    }
}
```

## Paso 7: Ejemplos de Uso

### 7.1 Ejemplos de Generación de Iniciales
- "Desarrollo Web Avanzado" → `DWA`
- "Marketing Digital para Empresas" → `MDE`
- "Gestión de Proyectos" → `GDP`
- "Inteligencia Artificial" → `IAR`

### 7.2 Estructura Final en S3
```
asecapt-certificates-prod/
├── certificates/
│   ├── 12345678/
│   │   ├── DWA_CERT001.pdf
│   │   ├── DWA_CERT001_QR.png
│   │   ├── MDE_CERT002.pdf
│   │   └── MDE_CERT002_QR.png
│   ├── 87654321/
│   │   ├── GDP_CERT003.pdf
│   │   └── GDP_CERT003_QR.png
│   └── 11223344/
│       ├── IAR_CERT004.pdf
│       └── IAR_CERT004_QR.png
```

## Paso 8: Consideraciones de Seguridad

### 8.1 Mejores Prácticas
1. **Never hardcode credentials** en el código
2. Usar **IAM roles** en EC2 en lugar de access keys cuando sea posible
3. Rotar **access keys** regularmente
4. Monitorear accesos con **CloudTrail**
5. Configurar **bucket policies** restrictivas

### 8.2 Monitoreo
1. Habilitar **S3 Access Logging**
2. Configurar **CloudWatch** para monitorear uso
3. Configurar **alertas** para accesos inusuales

## Paso 9: Costos Estimados

### 9.1 Estimación Mensual (ejemplo)
- **Storage**: 10GB de certificados = ~$0.25/mes
- **Requests**: 1000 PUT requests = ~$0.01/mes
- **Data Transfer**: 5GB salida = ~$0.45/mes
- **Total estimado**: ~$0.71/mes

### 9.2 Optimización de Costos
1. Usar **S3 Intelligent Tiering** para archivos antiguos
2. Configurar **lifecycle policies** para archivar después de 1 año
3. Comprimir archivos cuando sea posible

## Paso 10: Respaldo y Recuperación

### 10.1 Configurar Replicación Cross-Region
1. Crear bucket secundario en otra región
2. Configurar **Cross-Region Replication**
3. Automatizar respaldos con **Lambda functions**

### 10.2 Versionado
- **S3 Versioning** ya está habilitado
- Permite recuperar versiones anteriores de archivos
- Configurar **lifecycle policies** para gestionar versiones antiguas

---

## Resumen de Configuración Rápida

1. ✅ Crear bucket `asecapt-certificates-prod`
2. ✅ Crear usuario IAM `asecapt-certificates-user`
3. ✅ Asignar política personalizada
4. ✅ Generar access keys
5. ✅ Configurar variables de entorno
6. ✅ Implementar servicio S3 en Spring Boot
7. ✅ Probar subida de archivos
8. ✅ Configurar monitoreo básico

**¡Listo para usar!** 🚀
