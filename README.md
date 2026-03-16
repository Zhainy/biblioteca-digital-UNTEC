<!-- Badges -->
![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java)
![Maven](https://img.shields.io/badge/Maven-3.9-C71585?style=for-the-badge&logo=apache-maven)
![Tomcat](https://img.shields.io/badge/Tomcat-11-F8DC75?style=for-the-badge&logo=apache)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-7952B3?style=for-the-badge&logo=bootstrap)
![Security](https://img.shields.io/badge/Security-CSRF%20%2B%20BCrypt-green?style=for-the-badge)

# 📚 BibliotecaDigitalUNTEC

> **Plataforma de gestión de biblioteca digital** para la Universidad Nacional Tecnológica del Chocó (UNTEC).
> Una aplicación web segura y profesional para catalogar y administrar préstamos de libros.

---

## ✨ Características

- ✅ **Autenticación Segura** con BCrypt
- ✅ **Protección CSRF** en todas las acciones
- ✅ **Filtro de Autenticación** centralizado
- ✅ **Validación de Datos** en servidor
- ✅ **Logging Estructurado**
- ✅ **Patrón Singleton** en BD
- ✅ **Interfaz Responsiva** Bootstrap 5.3
- ✅ **9 Pruebas Unitarias** automatizadas

---

## 📦 Requisitos Previos

| Herramienta | Versión |
|-------------|---------|
| **JDK** | 21+ |
| **Maven** | 3.9+ |
| **Tomcat** | 11 |
| **MySQL** | 8.0+ |

---

## 🚀 Guía Rápida de Inicio (5 minutos)

### ⚙️ Paso 1: Descargar/Clonar el proyecto

```powershell
# Si tienes Git
git clone <repositorio>
cd BibliotecaDigitalUNTEC
```

### 🗄️ Paso 2: Crear base de datos

Ejecuta en MySQL:

```powershell
mysql -u root -p < sql/init.sql
```

✅ **Se crea automáticamente:**
- Base de datos `biblioteca_untec`
- Tablas `usuarios` y `libros`
- Usuario semilla: `admin@untec.com` / `1234`

### 🔐 Paso 3: Configurar credenciales

Copia la plantilla:

```powershell
Copy-Item "database.properties.example" "src/main/resources/database.properties"
```

Edita el archivo y agrega tus credenciales MySQL:

```properties
db.url=jdbc:mysql://localhost:3306/biblioteca_untec
db.user=root
db.password=tu_contraseña_aqui
```

### 🏗️ Paso 4: Compilar

```powershell
mvn clean package
```

✅ Genera `target/BibliotecaDigitalUNTEC.war`

### 🚀 Paso 5: Desplegar en Tomcat

**Opción A: Manual**
```powershell
Copy-Item "target/BibliotecaDigitalUNTEC.war" "C:\servers\Tomcat 11.0\webapps\"
cd "C:\servers\Tomcat 11.0\bin" && .\startup.bat
```

**Opción B: Con script (Recomendado)**
```powershell
.\redeploy.ps1 -TomcatPath "C:\servers\Tomcat 11.0" -Port 9090
```

### 🎉 Paso 6: ¡Abre la aplicación!

```
http://localhost:9090/BibliotecaDigitalUNTEC/login
```

**Ingresa con:**
- 📧 Email: `admin@untec.com`
- 🔑 Password: `1234`

---

## ✅ Prueba las Funcionalidades

| Acción | Resultado Esperado |
|--------|-------------------|
| Crear libro | ✅ Validación de título, autor, ISBN |
| Ver detalles | ✅ Muestra info del libro |
| Editar libro | ✅ Carga datos previos en formulario |
| Eliminar libro | ✅ Modal de confirmación con CSRF |
| Logout | ✅ Cierra sesión (requiere CSRF válido) |

---

## 🔐 Seguridad Implementada

### BCrypt Hash de Contraseñas
```
Contraseña guardada: $2a$10$5v1VYY3xTPuWYjQ6HgpQye6byUlnRtQ6iAPV3To8x3y6OJg.iryga
Verificación: BCrypt.checkpw("1234", hash) ✅
```

### Protección CSRF (Cross-Site Request Forgery)
- ✅ Token generado por sesión
- ✅ Validado en `logout`, `guardar`, `actualizar`, `eliminar`
- ✅ Respuesta `403` si token inválido

### Filtro de Autenticación Centralizado
- ✅ Redirige a login si no hay sesión activa
- ✅ Protege rutas: `/libros`, `listaLibros.jsp`, etc.

### POST para Acciones Críticas
- ❌ ANTES: `GET /libros?accion=eliminar&id=1`
- ✅ AHORA: `POST /libros` con parámetros y CSRF

---

## 📝 Ejecutar Pruebas Unitarias

```powershell
mvn test
```

**Resultado esperado:**
```
Tests run: 9, Failures: 0, Errors: 0 ✅
```

**Cobertura:**
- `CsrfTokenManagerTest` - 4 tests
- `AuthFilterTest` - 2 tests
- `LogoutServletTest` - 2 tests
- `LibroServletCsrfTest` - 1 test

---

## 🔧 Configuración Avanzada

### 🔐 Configuración Segura sin Credenciales en Git

Este repositorio **no incluye** credenciales reales en el historio.

#### Opción A: Desarrollo Local (Recomendado)

```powershell
Copy-Item "database.properties.example" "src/main/resources/database.properties"
```

Edita `database.properties` con tus credenciales. El archivo está ignorado en `.gitignore`.

#### Opción B: Archivo Externo en Producción

```powershell
# Crear carpeta de config
mkdir "C:\config\biblioteca"

# Crear archivo de credenciales
$config = @"
db.url=jdbc:mysql://localhost:3306/biblioteca_untec
db.user=root
db.password=tu_contraseña
"@

$config | Out-File "C:\config\biblioteca\database.properties"

# Configurar variable de entorno
setx APP_DB_CONFIG "C:\config\biblioteca\database.properties"

# Reiniciar Tomcat
```

#### Opción C: Variables de Entorno Globales

```powershell
setx DB_URL "jdbc:mysql://localhost:3306/biblioteca_untec"
setx DB_USER "root"
setx DB_PASSWORD "tu_contraseña"
```

**Orden de resolución (primera coincidencia gana):**
1. Propiedades JVM (`-Ddb.url`, `-Ddb.user`, `-Ddb.password`)
2. Variables de entorno del SO (`DB_URL`, `DB_USER`, `DB_PASSWORD`)
3. Archivo externo (`APP_DB_CONFIG` o `-Dapp.db.config`)
4. Archivo local `src/main/resources/database.properties`

---

## 🎓 Mejora Futura: Sistema de Registro de Cuentas

Como siguiente iteración del proyecto, se recomienda implementar un **sistema de auto-registro** (Sign Up) que permita a nuevos usuarios crear sus propias cuentas.

### Especificación Técnica Propuesta

#### 1. Formulario de Registro (`register.jsp`)
```jsp
- Email (validación única en BD)
- Nombre completo
- Contraseña (mínimo 8 caracteres)
- Confirmar contraseña
- Aceptar términos y condiciones
```

#### 2. Endpoint `RegisterServlet`
```java
POST /register
- Validar email formato
- Validar email duplicado
- Hash de contraseña con BCrypt
- Crear usuario en tabla `usuarios`
- Redirigir a login con mensaje de éxito
- Manejo de errores con mensajes claros
```

#### 3. Modelo de Datos
```sql
ALTER TABLE usuarios ADD COLUMN fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE usuarios ADD COLUMN estado ENUM('activo', 'inactivo', 'bloqueado') DEFAULT 'activo';
```

#### 4. Validaciones Server-Side
- Email válido y único
- Contraseña > 8 caracteres
- Confirmación de contraseña coincide
- Nombre no vacío
- Términos aceptados

#### 5. Seguridad Adicional
- CSRF token en formulario
- Rate limiting de registros por IP
- Email de confirmación (mejora futura)
- Captcha (mejora futura)

#### 6. Flujo de Usuario
```
1. Usuario accede a /register
2. Completa formulario con validación cliente
3. POST a /register con CSRF token
4. Servidor valida y hashea contraseña
5. Crea registro en `usuarios`
6. Redirige a login con mensaje ✅
7. Usuario puede iniciar sesión
```

---

## 🗂️ Estructura del Proyecto

```
BibliotecaDigitalUNTEC/
├── src/
│   ├── main/
│   │   ├── java/com/untec/
│   │   │   ├── controller/      # Servlets (LoginServlet, LibroServlet, etc.)
│   │   │   ├── dao/             # Acceso a datos
│   │   │   ├── model/           # Entidades (Usuario, Libro)
│   │   │   ├── filter/          # AuthFilter
│   │   │   └── utils/           # Conexion (Singleton), CsrfTokenManager
│   │   ├── webapp/
│   │   │   ├── index.jsp        # Login
│   │   │   ├── listaLibros.jsp  # Catálogo
│   │   │   ├── nuevoLibro.jsp   # Alta/edición
│   │   │   ├── detalleLibro.jsp # Detalle
│   │   │   └── assets/css/      # Estilos
│   │   └── resources/           # database.properties (ignorado en Git)
│   └── test/
│       └── java/com/untec/      # Tests unitarios
├── sql/
│   └── init.sql                 # Script de inicialización BD
├── pom.xml                       # Configuración Maven
├── database.properties.example   # Plantilla de credenciales
└── README.md                     # Este archivo
```

---

## 🐛 Solución de Problemas

| Problema | Solución |
|----------|----------|
| **Error: No se encuentra database.properties** | Copia `database.properties.example` a `src/main/resources/database.properties` |
| **Error 404 en login** | Verifica que Tomcat está corriendo en puerto 9090 |
| **Error 403 al guardar/eliminar** | Token CSRF inválido, recarga la página |
| **Contraseña incorrecta** | Usa `admin@untec.com` / `1234` |
| **Maven no compila** | Ejecuta `mvn clean install` |

---

## 🎯 Próximas Mejoras

- 👤 **Sistema de Registro de Cuentas** (Sign Up / Creación de usuarios)
- 🔄 DataSource con pool de conexiones (HikariCP)
- 🔐 Recuperación de contraseña por email
- 🚫 Bloqueo por intentos fallidos de login
- 📊 Dashboard de estadísticas
- 📱 Interfaz móvil nativa
- 🔔 Notificaciones de préstamos vencidos

---

## 📚 Tecnologías Utilizadas

- **Backend:** Java 21, Servlets, JSP, JSTL
- **Base de datos:** MySQL 8.0+
- **Build:** Maven 3.9+
- **Servidor:** Tomcat 11
- **Frontend:** Bootstrap 5.3, Bootstrap Icons
- **Seguridad:** BCrypt, CSRF Token
- **Testing:** JUnit 4, Mockito 5.12

---

## 📚 Contexto del Proyecto

Este proyecto es parte del **Portafolio ABP 5 - Fullstack Java** del programa **Talento Digital para Chile**, ejecutado en colaboración con la **Universidad Nacional Tecnológica del Chocó (UNTEC)**.

> ⚠️ **Nota:** Este proyecto es **únicamente con fines educativos y de simulación** para demostrar competencias en desarrollo web con Java. No debe utilizarse en producción sin implementar mejoras de seguridad adicionales.

---

## 📄 Licencia

Proyecto académico desarrollado como parte de los requisitos del programa **Talento Digital para Chile - ABP 5 Fullstack Java**.

- **Institución:** Universidad Nacional Tecnológica del Chocó (UNTEC)
- **Programa:** Talento Digital para Chile
- **Módulo:** ABP 5 - Fullstack Java
- **Propósito:** Educativo y de simulación

---

## 👨‍💻 Autor

**Nicole Fernández**  
Estudiante - Programa Talento Digital para Chile  
UNTEC - Departamento de Tecnología Educativa

---

## 🙏 Agradecimientos

- **Talento Digital para Chile** - Programa de capacitación
- **UNTEC** - Institución educativa colaboradora
- **Comunidad Open Source** - Librerías y herramientas utilizadas
