# ========================================================================
# INSTRUCCIONES PARA EJECUTAR DESDE INTELLIJ
# ========================================================================

## ✅ ARCHIVOS ADAPTADOS (COMPLETADOS):

### 1. ✅ .editorconfig
- Creado con configuración del profesor
- Indentación 2 espacios para Java, XML, HTML, SQL

### 2. ✅ pom.xml  
- Idéntico al del profesor
- Java 25 configurado
- Todas las dependencias correctas

### 3. ✅ Código completo adaptado (2 espacios)

---

## 🚀 PARA EJECUTAR DESDE INTELLIJ:

### PASO 1: Configurar SDK del Proyecto

1. **File → Project Structure (Ctrl+Alt+Shift+S)**
2. **Project Settings → Project**
3. **SDK:** Seleccionar **Java 25** (Corretto, Oracle, o el que tengas)
   - Si no aparece, haz clic en "Add SDK → Download JDK"
   - Vendor: Amazon Corretto
   - Version: 25
4. **Language Level:** 25 (Preview)
5. **Apply → OK**

### PASO 2: Configurar Maven con Java 25

1. **File → Settings (Ctrl+Alt+S)**
2. **Build, Execution, Deployment → Build Tools → Maven**
3. **Maven home path:** Debe estar configurado
4. **Runner:**
   - ✅ Delegate IDE build/run actions to Maven
   - **JRE:** Seleccionar **Java 25**
5. **Apply → OK**

### PASO 3: Invalidar Caché (Recomendado)

1. **File → Invalidate Caches**
2. ✅ Clear file system cache and Local History
3. ✅ Clear VCS Log caches and indexes  
4. ✅ Clear downloaded shared indexes
5. **Invalidate and Restart**

### PASO 4: Ejecutar desde Albumes2Application

1. Abrir `src/main/java/org/example/Albumes2Application.java`
2. Click derecho en la clase o en el método `main`
3. **Run 'Albumes2Application.main()'**
4. O usar el botón verde ▶️ al lado del método main

---

## 🔧 SI AÚN NO FUNCIONA:

### Verificar que IntelliJ use Java 25:

**Opción A: Configurar JAVA_HOME en IntelliJ**
1. Run → Edit Configurations
2. Seleccionar "Albumes2Application"
3. Environment variables: `JAVA_HOME=C:\ruta\a\tu\jdk-25`
4. Apply → OK

**Opción B: Desde Terminal de IntelliJ**
```powershell
# Abrir terminal en IntelliJ (Alt+F12)
# Configurar Java 25 (ajusta la ruta):
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Verificar:
java -version  # Debe mostrar: java version "25"

# Compilar:
.\mvnw clean install -DskipTests

# Ejecutar:
.\mvnw spring-boot:run
```

---

## 🎯 ENCONTRAR TU INSTALACIÓN DE JAVA 25:

### Buscar todas las instalaciones de Java:
```powershell
Get-ChildItem -Path "C:\Program Files\Java\" -ErrorAction SilentlyContinue
Get-ChildItem -Path "C:\Program Files\Amazon Corretto\" -ErrorAction SilentlyContinue
Get-ChildItem -Path "C:\Program Files\Eclipse Adoptium\" -ErrorAction SilentlyContinue
Get-ChildItem -Path "$env:USERPROFILE\.jdks\" -ErrorAction SilentlyContinue
```

### O usar comando where:
```powershell
where.exe java
```

---

## 📍 RUTAS COMUNES DE JAVA 25:

```
C:\Program Files\Java\jdk-25
C:\Program Files\Amazon Corretto\jdk25.0.1_7
C:\Program Files\Eclipse Adoptium\jdk-25.0.1.12-hotspot
C:\Users\<TU_USUARIO>\.jdks\corretto-25.0.1
```

---

## ✅ CUANDO FUNCIONE:

### Endpoints disponibles:
```
http://localhost:3000/api/v1/albumes
http://localhost:3000/swagger-ui.html
http://localhost:3000/h2-console
http://localhost:3000/graphiql
```

### Credenciales de prueba:
```
admin / Admin1   (ADMIN)
oli / User1      (USER)
noah / Test1     (USER)
gerard / Otro1   (USER)
```

---

## 🆘 TROUBLESHOOTING:

### Error: "No compiler is provided"
→ IntelliJ está usando JRE en lugar de JDK
→ File → Project Structure → SDK → Cambiar a JDK 25

### Error: "release version 25 not supported"
→ Maven no está usando Java 25
→ Settings → Maven → Runner → JRE: seleccionar Java 25

### Puerto 3000 ocupado:
→ Cambiar en application.properties: `server.port=8080`

---

✨ **Una vez configurado IntelliJ con Java 25, solo haz clic en el botón ▶️ verde!** ✨

