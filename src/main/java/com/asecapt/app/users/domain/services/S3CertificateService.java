package com.asecapt.app.users.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;

@Service
public class S3CertificateService {
    
    @Autowired
    private S3Client s3Client;
    
    @Value("${asecapt.aws.s3.bucket.name:${ASECAPT_AWS_S3_BUCKET_NAME}}")
    private String bucketName;
    
    @Value("${asecapt.aws.region:${ASECAPT_AWS_REGION}}")
    private String region;
    
    /**
     * Subir certificado a S3
     */
    public String uploadCertificate(String dni, String courseInitials, String certificateCode, 
                                  byte[] fileContent, String fileExtension) {
        String key = generateCertificateKey(dni, courseInitials, certificateCode, fileExtension);
        
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(getContentType(fileExtension))
                .build();
        
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileContent));
        
        System.out.println("✅ Certificado subido a S3: " + key);
        return key;
    }
    
    /**
     * Subir código QR a S3
     */
    public String uploadQRCode(String dni, String courseInitials, String certificateCode, 
                              byte[] qrContent) {
        String key = generateQRKey(dni, courseInitials, certificateCode);
        
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("image/png")
                .build();
        
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(qrContent));
        
        System.out.println("✅ Código QR subido a S3: " + key);
        return key;
    }
    
    /**
     * Descargar archivo desde S3
     */
    public byte[] downloadFile(String key) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        
        try (ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest)) {
            return responseInputStream.readAllBytes();
        }
    }
    
    /**
     * Eliminar archivo de S3
     */
    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        
        s3Client.deleteObject(deleteObjectRequest);
        System.out.println("🗑️ Archivo eliminado de S3: " + key);
    }
    
    /**
     * Generar key para certificado
     */
    private String generateCertificateKey(String dni, String courseInitials, 
                                        String certificateCode, String extension) {
        return String.format("certificates/%s/%s_%s.%s", 
                           dni, courseInitials, certificateCode, extension);
    }
    
    /**
     * Generar key para código QR
     */
    private String generateQRKey(String dni, String courseInitials, String certificateCode) {
        return String.format("certificates/%s/%s_%s_QR.png", 
                           dni, courseInitials, certificateCode);
    }
    
    /**
     * Obtener content type basado en extensión
     */
    private String getContentType(String extension) {
        switch (extension.toLowerCase()) {
            case "pdf": return "application/pdf";
            case "png": return "image/png";
            case "jpg":
            case "jpeg": return "image/jpeg";
            default: return "application/octet-stream";
        }
    }
    
    /**
     * Obtener URL pública del archivo (para acceso directo)
     */
    public String getPublicUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", 
                           bucketName, region, key);
    }
    
    /**
     * Verificar si un archivo existe en S3
     */
    public boolean fileExists(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            
            s3Client.getObject(getObjectRequest).close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
