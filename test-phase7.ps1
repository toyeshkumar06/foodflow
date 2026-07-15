Write-Host "1. Getting admin token (recreate if needed)..."
try {
    $adminLogin = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email":"admin@test.com","password":"admin123"}'
} catch {
    Write-Host "Admin login failed - if this is a fresh DB, recreate admin manually via MySQL Workbench (see Phase 5 instructions) then rerun this script."
}
$adminToken = $adminLogin.token

Write-Host "2. Admin overview..."
$overview = Invoke-RestMethod -Uri "http://localhost:8080/api/admin/analytics/overview" -Headers @{Authorization="Bearer $adminToken"}
Write-Host "Total Revenue: $($overview.totalRevenue) | Total Orders: $($overview.totalOrders)"
Write-Host "Most Popular Restaurant: $($overview.mostPopularRestaurant)"
Write-Host "Most Popular Food: $($overview.mostPopularFood)"
Write-Host "Most Active Customer: $($overview.mostActiveCustomer)"

Write-Host "3. Restaurant-level analytics (using owner2's most recent restaurant)..."
$ownerLogin = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email":"owner2@test.com","password":"owner123"}'
$ownerToken = $ownerLogin.token
$myRestaurants = Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/restaurants/mine" -Headers @{Authorization="Bearer $ownerToken"}
$latestRestaurantId = ($myRestaurants | Sort-Object id -Descending | Select-Object -First 1).id
Write-Host "Checking restaurant ID: $latestRestaurantId"

$restAnalytics = Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/restaurants/$latestRestaurantId/analytics" -Headers @{Authorization="Bearer $ownerToken"}
Write-Host "Restaurant Revenue: $($restAnalytics.totalRevenue) | Orders: $($restAnalytics.totalOrders)"
Write-Host "Top items:"
$restAnalytics.topSellingItems | ForEach-Object { Write-Host " - $($_.name): $($_.quantitySold) sold" }
Write-Host "Last 7 days:"
$restAnalytics.last7Days | ForEach-Object { Write-Host " - $($_.date): Revenue $($_.revenue), Orders $($_.orderCount)" }

Write-Host ""
Write-Host "ALL STEPS COMPLETED."