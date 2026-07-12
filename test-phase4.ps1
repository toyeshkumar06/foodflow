Write-Host "0. Cleaning up any old online agents (ignore errors here, that's expected on first run)..."
try {
    $old2 = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email":"agent2@test.com","password":"agent123"}'
    Invoke-RestMethod -Uri "http://localhost:8080/api/delivery/go-offline" -Method Post -Headers @{Authorization="Bearer $($old2.token)"} | Out-Null
} catch {}
try {
    $old3 = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email":"agent3@test.com","password":"agent123"}'
    Invoke-RestMethod -Uri "http://localhost:8080/api/delivery/go-offline" -Method Post -Headers @{Authorization="Bearer $($old3.token)"} | Out-Null
} catch {}

Write-Host "1. Logging in as owner..."
$ownerLogin = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email":"owner2@test.com","password":"owner123"}'
$token = $ownerLogin.token

Write-Host "2. Creating restaurant..."
$restaurant = Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/restaurants" -Method Post -Headers @{Authorization="Bearer $token"} -ContentType "application/json" -Body '{"name":"Pizza Palace 4","description":"Test","cuisineType":"Italian","addressLine":"123 MG Road","city":"Delhi","pincode":"110001","openingTime":"09:00","closingTime":"23:00","latitude":28.6139,"longitude":77.2090}'
Write-Host "Restaurant ID: $($restaurant.id)"

Write-Host "3. Setting restaurant OPEN..."
Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/restaurants/$($restaurant.id)/status" -Method Patch -Headers @{Authorization="Bearer $token"} -ContentType "application/json" -Body '{"status":"OPEN"}' | Out-Null

Write-Host "4. Creating category..."
$category = Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/restaurants/$($restaurant.id)/categories" -Method Post -Headers @{Authorization="Bearer $token"} -ContentType "application/json" -Body '{"name":"Main Course"}'

Write-Host "5. Creating food item..."
$foodItem = Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/restaurants/$($restaurant.id)/food-items" -Method Post -Headers @{Authorization="Bearer $token"} -ContentType "application/json" -Body "{`"name`":`"Margherita Pizza`",`"description`":`"Classic cheese pizza`",`"price`":299,`"veg`":true,`"categoryId`":$($category.id)}"

Write-Host "6. Registering fresh customer..."
$custResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -ContentType "application/json" -Body '{"name":"Cust Four","email":"cust4@test.com","password":"cust123","role":"CUSTOMER"}'
$custToken = $custResponse.token

Write-Host "7. Registering fresh delivery agent..."
$agentResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -ContentType "application/json" -Body '{"name":"Agent Four","email":"agent4@test.com","password":"agent123","role":"DELIVERY_AGENT"}'
$agentToken = $agentResponse.token

Write-Host "8. Adding customer address..."
$address = Invoke-RestMethod -Uri "http://localhost:8080/api/addresses" -Method Post -Headers @{Authorization="Bearer $custToken"} -ContentType "application/json" -Body '{"label":"Home","addressLine":"45 Park Street","city":"Delhi","pincode":"110002","isDefault":true,"latitude":28.7041,"longitude":77.1025}'

Write-Host "9. Agent Four going online (the ONLY online agent now)..."
Invoke-RestMethod -Uri "http://localhost:8080/api/delivery/go-online" -Method Post -Headers @{Authorization="Bearer $agentToken"} -ContentType "application/json" -Body '{"latitude":28.6200,"longitude":77.2100}' | Out-Null

Write-Host "10. Adding food to cart..."
Invoke-RestMethod -Uri "http://localhost:8080/api/cart/items" -Method Post -Headers @{Authorization="Bearer $custToken"} -ContentType "application/json" -Body "{`"foodItemId`":$($foodItem.id),`"quantity`":2}" | Out-Null

Write-Host "11. Placing order..."
$order = Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Headers @{Authorization="Bearer $custToken"} -ContentType "application/json" -Body "{`"addressId`":$($address.id)}"
Write-Host "Order ID: $($order.id) | ETA: $($order.etaMinutes) min | Distance: $($order.distanceKm) km | Surge: $($order.surgeMultiplier)"

Write-Host "12. Owner accepting..."
Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/orders/$($order.id)/status" -Method Patch -Headers @{Authorization="Bearer $token"} -ContentType "application/json" -Body '{"status":"ACCEPTED"}' | Out-Null

Write-Host "13. Owner starting preparation (triggers assignment)..."
$order2 = Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/orders/$($order.id)/status" -Method Patch -Headers @{Authorization="Bearer $token"} -ContentType "application/json" -Body '{"status":"PREPARING"}'
Write-Host "Assigned agent: $($order2.deliveryAgentName)  <-- should say Agent Four"

Write-Host "14. Marking ready..."
Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/orders/$($order.id)/status" -Method Patch -Headers @{Authorization="Bearer $token"} -ContentType "application/json" -Body '{"status":"READY_FOR_PICKUP"}' | Out-Null

Write-Host "15. Agent Four picking up..."
Invoke-RestMethod -Uri "http://localhost:8080/api/delivery/orders/$($order.id)/status" -Method Patch -Headers @{Authorization="Bearer $agentToken"} -ContentType "application/json" -Body '{"status":"PICKED_UP"}' | Out-Null

Write-Host "16. On the way..."
Invoke-RestMethod -Uri "http://localhost:8080/api/delivery/orders/$($order.id)/status" -Method Patch -Headers @{Authorization="Bearer $agentToken"} -ContentType "application/json" -Body '{"status":"ON_THE_WAY"}' | Out-Null

Write-Host "17. Delivered..."
Invoke-RestMethod -Uri "http://localhost:8080/api/delivery/orders/$($order.id)/status" -Method Patch -Headers @{Authorization="Bearer $agentToken"} -ContentType "application/json" -Body '{"status":"DELIVERED"}' | Out-Null

Write-Host "18. Earnings check..."
$earnings = Invoke-RestMethod -Uri "http://localhost:8080/api/delivery/earnings" -Headers @{Authorization="Bearer $agentToken"}
Write-Host "Agent earnings: $earnings  <-- should be non-zero"

Write-Host ""
Write-Host "ALL STEPS COMPLETED SUCCESSFULLY."