# Script para copiar recursos modificados al target/classes

Write-Host "Copiando archivos de recursos modificados..." -ForegroundColor Green

# Copiar archivos de mensajes
Write-Host "Copiando archivos de mensajes..." -ForegroundColor Yellow
Copy-Item -Path "src\main\resources\mensajes.properties" -Destination "target\classes\" -Force
Copy-Item -Path "src\main\resources\mensajes_en.properties" -Destination "target\classes\" -Force
Copy-Item -Path "src\main\resources\mensajes_de.properties" -Destination "target\classes\" -Force
Copy-Item -Path "src\main\resources\mensajes_fr.properties" -Destination "target\classes\" -Force
Copy-Item -Path "src\main\resources\mensajes_pt.properties" -Destination "target\classes\" -Force

# Copiar data.sql
Write-Host "Copiando data.sql..." -ForegroundColor Yellow
Copy-Item -Path "src\main\resources\data.sql" -Destination "target\classes\" -Force

# Copiar templates
Write-Host "Copiando templates..." -ForegroundColor Yellow
Copy-Item -Path "src\main\resources\templates\*" -Destination "target\classes\templates\" -Recurse -Force

# Copiar archivos estáticos (imágenes, CSS, JS)
Write-Host "Copiando archivos estáticos (imágenes, CSS, JS)..." -ForegroundColor Yellow
Copy-Item -Path "src\main\resources\static\*" -Destination "target\classes\static\" -Recurse -Force

Write-Host "`n¡Recursos copiados exitosamente!" -ForegroundColor Green
Write-Host "  ✓ Mensajes (5 idiomas)" -ForegroundColor Cyan
Write-Host "  ✓ data.sql con portadas" -ForegroundColor Cyan
Write-Host "  ✓ Templates actualizados" -ForegroundColor Cyan
Write-Host "  ✓ Imágenes de portadas" -ForegroundColor Cyan
Write-Host "`nAhora reinicia la aplicación desde IntelliJ IDEA" -ForegroundColor Yellow

