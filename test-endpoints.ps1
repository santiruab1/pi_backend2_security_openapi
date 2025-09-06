# Script de PowerShell para probar todos los endpoints de la API
# Asegúrate de que el servidor esté ejecutándose en http://localhost:8080

# Configuración
$baseUrl = "http://localhost:8080"
$headers = @{
    "Content-Type" = "application/json"
    "Accept" = "application/json"
}

# Función para mostrar resultados
function Show-Result {
    param(
        [string]$TestName,
        [object]$Response,
        [int]$StatusCode
    )
    Write-Host "\n=== $TestName ===" -ForegroundColor Cyan
    Write-Host "Status Code: $StatusCode" -ForegroundColor $(if($StatusCode -ge 200 -and $StatusCode -lt 300) { "Green" } else { "Red" })
    if ($Response) {
        Write-Host "Response:" -ForegroundColor Yellow
        $Response | ConvertTo-Json -Depth 3 | Write-Host
    }
    Write-Host "" # Línea en blanco
}

# Función para realizar peticiones HTTP
function Invoke-ApiRequest {
    param(
        [string]$Method,
        [string]$Url,
        [object]$Body = $null,
        [string]$TestName
    )
    
    try {
        $params = @{
            Uri = $Url
            Method = $Method
            Headers = $headers
        }
        
        if ($Body) {
            $params.Body = ($Body | ConvertTo-Json -Depth 3)
        }
        
        $response = Invoke-RestMethod @params
        Show-Result -TestName $TestName -Response $response -StatusCode 200
        return $response
    }
    catch {
        $statusCode = if ($_.Exception.Response) { $_.Exception.Response.StatusCode.value__ } else { 0 }
        Show-Result -TestName $TestName -Response $_.Exception.Message -StatusCode $statusCode
        return $null
    }
}

Write-Host "🚀 Iniciando pruebas de endpoints de la API" -ForegroundColor Green
Write-Host "Base URL: $baseUrl" -ForegroundColor Yellow
Write-Host "=" * 50 -ForegroundColor Gray

# Variables para almacenar IDs creados
$createdUserId = $null
$createdItemId = $null
$createdLoanId = $null
$createdLoanHistoryId = $null

# ========================================
# PRUEBAS DE ACTUATOR (HEALTH & INFO)
# ========================================

Write-Host "\n🏥 PRUEBAS DE ACTUATOR" -ForegroundColor Magenta

# Health endpoint
Invoke-ApiRequest -Method "GET" -Url "$baseUrl/actuator/health" -TestName "Actuator - Health Check"

# Info endpoint
Invoke-ApiRequest -Method "GET" -Url "$baseUrl/actuator/info" -TestName "Actuator - Application Info"

# ========================================
# PRUEBAS DE USUARIOS
# ========================================

Write-Host "\n👥 PRUEBAS DE USUARIOS" -ForegroundColor Magenta

# Listar todos los usuarios
Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/users" -TestName "Users - Get All"

# Crear un nuevo usuario
$newUser = @{
    username = "usuario_test"
    email = "test@example.com"
}
$createdUser = Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/users" -Body $newUser -TestName "Users - Create"
if ($createdUser -and $createdUser.id) {
    $createdUserId = $createdUser.id
}

# Obtener usuario por ID (si se creó exitosamente)
if ($createdUserId) {
    Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/users/$createdUserId" -TestName "Users - Get By ID"
    
    # Actualizar usuario
    $updatedUser = @{
        id = $createdUserId
        username = "usuario_actualizado"
        email = "updated@example.com"
    }
    Invoke-ApiRequest -Method "PUT" -Url "$baseUrl/api/users/$createdUserId" -Body $updatedUser -TestName "Users - Update"
}

# ========================================
# PRUEBAS DE ITEMS
# ========================================

Write-Host "\n📦 PRUEBAS DE ITEMS" -ForegroundColor Magenta

# Listar todos los items
Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/items" -TestName "Items - Get All"

# Crear un nuevo item
$newItem = @{
    name = "Laptop Test"
    description = "Laptop para pruebas"
    quantity = 5
}
$createdItem = Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/items" -Body $newItem -TestName "Items - Create"
if ($createdItem -and $createdItem.id) {
    $createdItemId = $createdItem.id
}

# Obtener item por ID (si se creó exitosamente)
if ($createdItemId) {
    Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/items/$createdItemId" -TestName "Items - Get By ID"
    
    # Actualizar item
    $updatedItem = @{
        id = $createdItemId
        name = "Laptop Actualizada"
        description = "Laptop actualizada para pruebas"
        quantity = 3
    }
    Invoke-ApiRequest -Method "PUT" -Url "$baseUrl/api/items/$createdItemId" -Body $updatedItem -TestName "Items - Update"
}

# ========================================
# PRUEBAS DE PRÉSTAMOS
# ========================================

Write-Host "\n📋 PRUEBAS DE PRÉSTAMOS" -ForegroundColor Magenta

# Listar todos los préstamos
Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/loans" -TestName "Loans - Get All"

# Crear un nuevo préstamo (solo si tenemos usuario e item)
if ($createdUserId -and $createdItemId) {
    $newLoan = @{
        itemId = $createdItemId
        userId = $createdUserId
        loanDate = (Get-Date).ToString("yyyy-MM-dd")
        returnDate = (Get-Date).AddDays(7).ToString("yyyy-MM-dd")
        returned = $false
    }
    $createdLoan = Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/loans" -Body $newLoan -TestName "Loans - Create"
    if ($createdLoan -and $createdLoan.id) {
        $createdLoanId = $createdLoan.id
    }
}

# Obtener préstamo por ID (si se creó exitosamente)
if ($createdLoanId) {
    Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/loans/$createdLoanId" -TestName "Loans - Get By ID"
    
    # Actualizar préstamo
    $updatedLoan = @{
        id = $createdLoanId
        itemId = $createdItemId
        userId = $createdUserId
        loanDate = (Get-Date).ToString("yyyy-MM-dd")
        returnDate = (Get-Date).AddDays(14).ToString("yyyy-MM-dd")
        returned = $true
    }
    Invoke-ApiRequest -Method "PUT" -Url "$baseUrl/api/loans/$createdLoanId" -Body $updatedLoan -TestName "Loans - Update"
}

# ========================================
# PRUEBAS DE HISTORIAL DE PRÉSTAMOS
# ========================================

Write-Host "\n📚 PRUEBAS DE HISTORIAL DE PRÉSTAMOS" -ForegroundColor Magenta

# Listar todo el historial
Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/loanhistories" -TestName "Loan History - Get All"

# Crear un nuevo registro de historial (solo si tenemos un préstamo)
if ($createdLoanId) {
    $newLoanHistory = @{
        loanId = $createdLoanId
        actionDate = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss")
        action = "PRESTAMO_CREADO"
    }
    $createdLoanHistory = Invoke-ApiRequest -Method "POST" -Url "$baseUrl/api/loanhistories" -Body $newLoanHistory -TestName "Loan History - Create"
    if ($createdLoanHistory -and $createdLoanHistory.id) {
        $createdLoanHistoryId = $createdLoanHistory.id
    }
}

# Obtener historial por ID (si se creó exitosamente)
if ($createdLoanHistoryId) {
    Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/loanhistories/$createdLoanHistoryId" -TestName "Loan History - Get By ID"
    
    # Actualizar historial
    $updatedLoanHistory = @{
        id = $createdLoanHistoryId
        loanId = $createdLoanId
        actionDate = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss")
        action = "PRESTAMO_DEVUELTO"
    }
    Invoke-ApiRequest -Method "PUT" -Url "$baseUrl/api/loanhistories/$createdLoanHistoryId" -Body $updatedLoanHistory -TestName "Loan History - Update"
}

# ========================================
# PRUEBAS DE ELIMINACIÓN
# ========================================

Write-Host "\n🗑️ PRUEBAS DE ELIMINACIÓN" -ForegroundColor Magenta

# Eliminar en orden inverso para evitar problemas de dependencias
if ($createdLoanHistoryId) {
    Invoke-ApiRequest -Method "DELETE" -Url "$baseUrl/api/loanhistories/$createdLoanHistoryId" -TestName "Loan History - Delete"
}

if ($createdLoanId) {
    Invoke-ApiRequest -Method "DELETE" -Url "$baseUrl/api/loans/$createdLoanId" -TestName "Loans - Delete"
}

if ($createdItemId) {
    Invoke-ApiRequest -Method "DELETE" -Url "$baseUrl/api/items/$createdItemId" -TestName "Items - Delete"
}

if ($createdUserId) {
    Invoke-ApiRequest -Method "DELETE" -Url "$baseUrl/api/users/$createdUserId" -TestName "Users - Delete"
}

# ========================================
# PRUEBAS DE ENDPOINTS INEXISTENTES
# ========================================

Write-Host "\n❌ PRUEBAS DE ENDPOINTS INEXISTENTES" -ForegroundColor Magenta

# Probar endpoint que no existe
Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/nonexistent" -TestName "Non-existent Endpoint"

# Probar ID que no existe
Invoke-ApiRequest -Method "GET" -Url "$baseUrl/api/users/99999" -TestName "Non-existent User ID"

# ========================================
# RESUMEN FINAL
# ========================================

Write-Host "\n" + "=" * 50 -ForegroundColor Gray
Write-Host "✅ Pruebas completadas" -ForegroundColor Green
Write-Host "\n📋 Resumen de endpoints probados:" -ForegroundColor Yellow
Write-Host "• Actuator: 2 endpoints (GET /actuator/health, GET /actuator/info)" -ForegroundColor White
Write-Host "• Users: 5 endpoints (GET, POST, GET/{id}, PUT/{id}, DELETE/{id})" -ForegroundColor White
Write-Host "• Items: 5 endpoints (GET, POST, GET/{id}, PUT/{id}, DELETE/{id})" -ForegroundColor White
Write-Host "• Loans: 5 endpoints (GET, POST, GET/{id}, PUT/{id}, DELETE/{id})" -ForegroundColor White
Write-Host "• Loan History: 5 endpoints (GET, POST, GET/{id}, PUT/{id}, DELETE/{id})" -ForegroundColor White
Write-Host "• Error cases: 2 endpoints" -ForegroundColor White
Write-Host "\n📊 Total: 24 pruebas realizadas" -ForegroundColor Cyan
Write-Host "\n💡 Nota: Asegúrate de que el servidor esté ejecutándose en $baseUrl" -ForegroundColor Yellow
Write-Host "💡 Para iniciar el servidor: ./mvnw spring-boot:run" -ForegroundColor Yellow