# 📚 Mantenedor de Cursos - Guía de Configuración

## 🚀 **Cómo Acceder al Mantenedor de Cursos**

### **1. 🛠️ Configurar la Base de Datos**

Ejecuta el script SQL para actualizar las tablas:

```bash
mysql -u root -p asecapt < update_course_maintainer_schema.sql
```

### **2. 🚀 Iniciar los Servicios**

#### **Backend (Puerto 8081)**
```bash
cd /Users/i339972/Documents/asecapt
mvn spring-boot:run
```

#### **Frontend (Puerto 4200)**
```bash
cd /Users/i339972/Documents/asecapt/asecapt-final-clean
ng serve
```

### **3. 🔐 Hacer Login**

1. **URL:** `http://localhost:4200/virtual-classroom`
2. **Credenciales:**
   - **Usuario:** `admin@asecapt.com`
   - **Contraseña:** `admin2024`

### **4. 📚 Acceder al Mantenedor**

1. Después del login → **Dashboard:** `http://localhost:4200/dashboard`
2. En el **sidebar izquierdo** → Click en **"Cursos"**

---

## 🎯 **Funcionalidades Disponibles**

### **📋 Vista Lista de Programas**
- ✅ **Grid responsive** con tarjetas de programas
- ✅ **Filtros:** Búsqueda, tipo, estado
- ✅ **Estadísticas:** Total, activos, borradores, módulos
- ✅ **Acciones:** Ver detalles, editar, duplicar, archivar

### **🔍 Vista Detalles del Programa**
- ✅ **Información completa** del programa
- ✅ **Gestión de contenidos** asignados
- ✅ **Agregar/remover módulos** dinámicamente
- ✅ **Reordenamiento** de contenidos

### **📚 Vista Biblioteca de Contenidos**
- ✅ **CRUD completo** de módulos/contenidos
- ✅ **Tipos:** Module, Lesson, Assignment, Exam, Resource
- ✅ **Asignación** a múltiples programas
- ✅ **Plantillas** por tipo de contenido

---

## 🎨 **Navegación del Sistema**

### **🏠 Dashboard Principal**
```
Sidebar:
├── 🏠 Dashboard
├── 📜 Generar Certificados
├── 🔍 Buscar Certificados  
├── 👥 Alumnos
└── 📚 Cursos ← ¡AQUÍ ESTÁ EL MANTENEDOR!
```

### **📚 Vistas del Mantenedor de Cursos**
```
Cursos Component:
├── 📋 Lista (currentView = 'list')
├── 🔍 Detalles (currentView = 'details')
└── 📚 Biblioteca (currentView = 'content-library')
```

---

## 🛠️ **Flujo de Uso Completo**

### **Paso 1: Crear Contenidos/Módulos**
```
1. Cursos → "Nuevo Contenido"
2. Llenar formulario:
   - Título: "Introducción a JavaScript"
   - Tipo: "Módulo"
   - Duración: "8 horas"
   - Descripción: "Fundamentos del lenguaje"
3. Guardar
```

### **Paso 2: Crear Programa/Curso**
```
1. Cursos → "Nuevo Programa"
2. Llenar formulario:
   - Título: "Curso Full Stack"
   - Tipo: "Course"
   - Categoría: "Programación"
   - Estado: "Draft"
   - Instructor: "Prof. García"
3. Guardar
```

### **Paso 3: Agregar Módulos al Programa**
```
1. Click en programa → "Ver Detalles"
2. Panel derecho → "Agregar"
3. Seleccionar contenidos de la biblioteca
4. Módulos aparecen en la lista del programa
```

### **Paso 4: Gestionar Contenidos**
```
- Reordenar: Drag & drop (futuro)
- Remover: Botón X en cada módulo
- Editar: Biblioteca → Editar contenido
- Requerido: Toggle obligatorio/opcional
```

---

## 🎯 **Botones de Navegación**

### **📋 En Vista Lista**
- **[Nuevo Programa]** → Modal crear programa
- **[Nuevo Contenido]** → Modal crear contenido
- **[Biblioteca]** → Vista biblioteca de contenidos

### **🔍 En Vista Detalles**
- **[Volver]** → Regresa a vista lista
- **[Agregar]** → Va a biblioteca para asignar módulos

### **📚 En Vista Biblioteca**
- **[Nuevo Contenido]** → Modal crear contenido
- **[Volver]** → Regresa a vista anterior (lista o detalles)

---

## 📊 **Datos de Prueba Incluidos**

El script SQL incluye **datos de ejemplo**:

### **🎓 Programas**
- Fundamentos de Programación (Course)
- Especialización en Data Science (Specialization)
- Certificación en Ciberseguridad (Certification)
- Diseño Web Avanzado (Course - Draft)

### **📚 Contenidos**
- Introducción a JavaScript (Module)
- Python para Principiantes (Module)
- HTML y CSS Básico (Lesson)
- Proyecto Final (Assignment)
- Examen de Certificación (Exam)

### **🔗 Relaciones**
- "Fundamentos de Programación" tiene 3 módulos asignados
- Orden secuencial y todos marcados como requeridos

---

## 🚨 **Solución de Problemas**

### **❌ No veo el mantenedor**
1. ✅ Verificar que estés en: `http://localhost:4200/dashboard`
2. ✅ Verificar que hayas hecho login correctamente
3. ✅ Click en "Cursos" en el sidebar izquierdo

### **❌ Errores de base de datos**
1. ✅ Ejecutar el script: `update_course_maintainer_schema.sql`
2. ✅ Verificar que el backend está corriendo en puerto 8081
3. ✅ Revisar logs del backend para errores SQL

### **❌ API no responde**
1. ✅ Backend corriendo: `http://localhost:8081`
2. ✅ Verificar endpoints: `http://localhost:8081/api/programs`
3. ✅ Revisar CORS y configuración de puertos

---

## 🎉 **¡El mantenedor está 100% funcional!**

Una vez completada la configuración, tendrás acceso a:
- ✅ **CRUD completo** de programas y contenidos
- ✅ **Gestión de relaciones** program-content
- ✅ **Interface moderna** y responsive
- ✅ **Búsquedas y filtros** en tiempo real
- ✅ **Estadísticas dinámicas**
- ✅ **Navegación intuitiva** entre vistas

---

**📱 Acceso directo:** `http://localhost:4200/dashboard` → **Sidebar** → **"Cursos"** 