# Script para configurar Java 25 Corretto y ejecutar el proyecto
# Ajusta la ruta al JDK 25 que instalaste

# Ejemplo de rutas comunes donde puede estar Java 25 Corretto:
# C:\Program Files\Amazon Corretto\jdk25.0.1_7
# C:\Program Files\Java\jdk-25
# Ajusta esta ruta a donde instalaste Java 25

# OPCION 1: Si tienes Java 25 en Program Files
$JAVA_HOME_25 = "C:\Program Files\Amazon Corretto\jdk25.0.1_7"

# OPCION 2: Si lo instalaste en otro lugar, cambia la ruta aqui
# $JAVA_HOME_25 = "TU_RUTA_AQUI"

if (Test-Path $JAVA_HOME_25) {
    Write-Host "✓ Java 25 encontrado en: $JAVA_HOME_25" -ForegroundColor Green

    # Configurar variables de entorno para esta sesion
    $env:JAVA_HOME = $JAVA_HOME_25
    $env:PATH = "$JAVA_HOME_25\bin;$env:PATH"

    Write-Host "✓ JAVA_HOME configurado" -ForegroundColor Green

    # Verificar version
    java -version

    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host "Compilando el proyecto..." -ForegroundColor Cyan
    Write-Host "========================================`n" -ForegroundColor Cyan

    # Compilar
    .\mvnw clean install -DskipTests

    if ($LASTEXITCODE -eq 0) {
        Write-Host "`n========================================" -ForegroundColor Green
        Write-Host "✓ COMPILACION EXITOSA" -ForegroundColor Green
        Write-Host "========================================`n" -ForegroundColor Green

        Write-Host "Para ejecutar la aplicacion:" -ForegroundColor Yellow
        Write-Host "  .\mvnw spring-boot:run`n" -ForegroundColor Yellow

        Write-Host "Endpoints disponibles:" -ForegroundColor Cyan
        Write-Host "  API REST:    http://localhost:3000/api/v1/albumes" -ForegroundColor White
        Write-Host "  Swagger:     http://localhost:3000/swagger-ui.html" -ForegroundColor White
        Write-Host "  H2 Console:  http://localhost:3000/h2-console" -ForegroundColor White
        Write-Host "  GraphiQL:    http://localhost:3000/graphiql" -ForegroundColor White
        Write-Host "  WebSocket:   ws://localhost:3000/ws/v1/albumes`n" -ForegroundColor White

        Write-Host "Usuarios de prueba:" -ForegroundColor Cyan
        Write-Host "  admin/Admin1   (ADMIN)" -ForegroundColor White
        Write-Host "  oli/User1      (USER - Manager BMTH)" -ForegroundColor White
        Write-Host "  noah/Test1     (USER - Manager Bad Omens)" -ForegroundColor White
        Write-Host "  gerard/Otro1   (USER - Manager MCR)`n" -ForegroundColor White
    } else {
        Write-Host "`n✗ ERROR EN LA COMPILACION" -ForegroundColor Red
        Write-Host "Revisa los errores arriba`n" -ForegroundColor Red
    }
} else {
    Write-Host "✗ Java 25 NO encontrado en: $JAVA_HOME_25" -ForegroundColor Red
    Write-Host "`nPor favor:" -ForegroundColor Yellow
    Write-Host "1. Abre este archivo: run-with-java25.ps1" -ForegroundColor White
    Write-Host "2. Cambia la linea 9 con la ruta correcta de tu JDK 25" -ForegroundColor White
    Write-Host "3. Guarda y ejecuta de nuevo`n" -ForegroundColor White
}

