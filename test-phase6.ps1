$stamp = Get-Date -Format "MMddHHmmss"

Write-Host "0. Forcing all known old test agents offline (ignore errors, expected)..."
foreach ($email in @("agent2@test.com","agent3@test.com","agent4@test.com","agent6@test.com")) {
    try {
        $old = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body "{`"email`":`"$email`",`"password`":`"agent123`"}"
        Invoke-RestMethod -Uri "http://localhost:8080/api/delivery/go-offline" -Method Post -Headers @{Authorization="Bearer $($old.token)"} | Out-Null
    } catch {}
}

Write-Host "1. Logging in as owner2, creating fresh restaurant+menu..."
$ownerLogin = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email":"owner2@test.com","password":"owner123"}'
$token = $ownerLogin.token
$restaurant = Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/restaurants" -Method Post -Headers @{Authorization="Bearer $token"} -ContentType "application/json" -Body "{`"name`":`"Pizza Palace $stamp`",`"description`":`"Test`",`"cuisineType`":`"Italian`",`"addressLine`":`"123 MG Road`",`"city`":`"Delhi`",`"pincode`":`"110001`",`"openingTime`":`"09:00`",`"closingTime`":`"23:00`",`"latitude`":28.6139,`"longitude`":77.2090}"
Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/restaurants/$($restaurant.id)/status" -Method Patch -Headers @{Authorization="Bearer $token"} -ContentType "application/json" -Body '{"status":"OPEN"}' | Out-Null
$category = Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/restaurants/$($restaurant.id)/categories" -Method Post -Headers @{Authorization="Bearer $token"} -ContentType "application/json" -Body '{"name":"Main Course"}'
$foodItem = Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/restaurants/$($restaurant.id)/food-items" -Method Post -Headers @{Authorization="Bearer $token"} -ContentType "application/json" -Body "{`"name`":`"Margherita Pizza`",`"description`":`"Classic`",`"price`":299,`"veg`":true,`"categoryId`":$($category.id)}"
Write-Host "Restaurant ID: $($restaurant.id) | Food Item ID: $($foodItem.id)"

Write-Host "2. Registering BRAND NEW customer + agent (unique emails, cannot conflict)..."
$custEmail = "cust_$stamp@test.com"
$agentEmail = "agent_$stamp@test.com"
$custResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -ContentType "application/json" -Body "{`"name`":`"Cust $stamp`",`"email`":`"$custEmail`",`"password`":`"cust123`",`"role`":`"CUSTOMER`"}"
$custToken = $custResponse.token
$agentResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method Post -ContentType "application/json" -Body "{`"name`":`"Agent $stamp`",`"email`":`"$agentEmail`",`"password`":`"agent123`",`"role`":`"DELIVERY_AGENT`"}"
$agentToken = $agentResponse.token
Write-Host "Customer: $custEmail | Agent: $agentEmail"

Write-Host "3. Agent going online (guaranteed to be the ONLY one online now)..."
Invoke-RestMethod -Uri "http://localhost:8080/api/delivery/go-online" -Method Post -Headers @{Authorization="Bearer $agentToken"} -ContentType "application/json" -Body '{"latitude":28.6200,"longitude":77.2100}' | Out-Null

Write-Host "4. Address, cart, place order..."
$address = Invoke-RestMethod -Uri "http://localhost:8080/api/addresses" -Method Post -Headers @{Authorization="Bearer $custToken"} -ContentType "application/json" -Body '{"label":"Home","addressLine":"45 Park Street","city":"Delhi","pincode":"110002","isDefault":true,"latitude":28.7041,"longitude":77.1025}'
Invoke-RestMethod -Uri "http://localhost:8080/api/cart/items" -Method Post -Headers @{Authorization="Bearer $custToken"} -ContentType "application/json" -Body "{`"foodItemId`":$($foodItem.id),`"quantity`":1}" | Out-Null
$order = Invoke-RestMethod -Uri "http://localhost:8080/api/orders" -Method Post -Headers @{Authorization="Bearer $custToken"} -ContentType "application/json" -Body "{`"addressId`":$($address.id)}"
Write-Host "Order ID: $($order.id)"

Write-Host "5. Driving through the full status flow..."
Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/orders/$($order.id)/status" -Method Patch -Headers @{Authorization="Bearer $token"} -ContentType "application/json" -Body '{"status":"ACCEPTED"}' | Out-Null
$prep = Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/orders/$($order.id)/status" -Method Patch -Headers @{Authorization="Bearer $token"} -ContentType "application/json" -Body '{"status":"PREPARING"}'
Write-Host "Assigned agent: $($prep.deliveryAgentName)  <-- should be Agent $stamp"
Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/orders/$($order.id)/status" -Method Patch -Headers @{Authorization="Bearer $token"} -ContentType "application/json" -Body '{"status":"READY_FOR_PICKUP"}' | Out-Null
Invoke-RestMethod -Uri "http://localhost:8080/api/delivery/orders/$($order.id)/status" -Method Patch -Headers @{Authorization="Bearer $agentToken"} -ContentType "application/json" -Body '{"status":"PICKED_UP"}' | Out-Null
Invoke-RestMethod -Uri "http://localhost:8080/api/delivery/orders/$($order.id)/status" -Method Patch -Headers @{Authorization="Bearer $agentToken"} -ContentType "application/json" -Body '{"status":"ON_THE_WAY"}' | Out-Null
Invoke-RestMethod -Uri "http://localhost:8080/api/delivery/orders/$($order.id)/status" -Method Patch -Headers @{Authorization="Bearer $agentToken"} -ContentType "application/json" -Body '{"status":"DELIVERED"}' | Out-Null
Write-Host "Order genuinely delivered."

Write-Host "6. Checking customer's notifications..."
$notifications = Invoke-RestMethod -Uri "http://localhost:8080/api/notifications" -Headers @{Authorization="Bearer $custToken"}
Write-Host "Notification count: $($notifications.Count)  <-- should be 5"
$notifications | ForEach-Object { Write-Host " - $($_.title): $($_.message)" }

Write-Host "7. Rating restaurant 5 stars..."
Invoke-RestMethod -Uri "http://localhost:8080/api/orders/$($order.id)/ratings/restaurant" -Method Post -Headers @{Authorization="Bearer $custToken"} -ContentType "application/json" -Body '{"stars":5,"reviewText":"Excellent!"}' | Out-Null

Write-Host "8. Rating food item 4 stars..."
Invoke-RestMethod -Uri "http://localhost:8080/api/orders/$($order.id)/ratings/food/$($foodItem.id)" -Method Post -Headers @{Authorization="Bearer $custToken"} -ContentType "application/json" -Body '{"stars":4,"reviewText":"Tasty"}' | Out-Null

Write-Host "9. Rating delivery agent 5 stars..."
Invoke-RestMethod -Uri "http://localhost:8080/api/orders/$($order.id)/ratings/delivery-agent" -Method Post -Headers @{Authorization="Bearer $custToken"} -ContentType "application/json" -Body '{"stars":5,"reviewText":"Fast delivery"}' | Out-Null

Write-Host "10. Checking averages..."
$restaurantCheck = Invoke-RestMethod -Uri "http://localhost:8080/api/restaurants/$($restaurant.id)"
Write-Host "Restaurant average rating: $($restaurantCheck.averageRating)  <-- should be 5"

Write-Host "11. Duplicate rating rejection test..."
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/orders/$($order.id)/ratings/restaurant" -Method Post -Headers @{Authorization="Bearer $custToken"} -ContentType "application/json" -Body '{"stars":3}'
    Write-Host "UNEXPECTED: should have failed"
} catch {
    Write-Host "Correctly rejected duplicate rating."
}

Write-Host ""
Write-Host "ALL STEPS COMPLETED."