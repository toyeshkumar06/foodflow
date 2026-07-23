# seed-restaurants.ps1
# Creates 6 fully-branded restaurants with distinct cuisines, keyword-matched food
# photos (via LoremFlickr, a free keyword-based stock photo service), and 10-12
# menu items each. Run this once your backend is up.
# Uses owner2@test.com (created back in earlier phases).

$ownerLogin = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"email":"owner2@test.com","password":"owner123"}'
$token = $ownerLogin.token
$headers = @{ Authorization = "Bearer $token" }

function Get-Img($text, $bg, $fg) {
    $clean = ($text -replace '[()]', '') -replace ' ', '+'
    return "https://placehold.co/500x350/$bg/$fg`?text=$clean"
}

$restaurants = @(
    @{
        name = "Kyoto Nights"; bg = "C0392B"; fg = "FFFFFF"; cuisine = "Japanese"; desc = "Authentic Japanese dining - sushi, ramen, and more"
        address = "12 Sakura Lane"; city = "Delhi"; pincode = "110011"; lat = 28.6139; lng = 77.2090
        imgKeyword = "japanese,sushi,restaurant"
        categories = @(
            @{ name = "Sushi and Rolls"; items = @(
                @{ name = "Salmon Nigiri (6pc)"; desc = "Fresh salmon over seasoned rice"; price = 320; veg = $false; img = "sushi,salmon" },
                @{ name = "California Roll"; desc = "Crab, avocado, cucumber"; price = 280; veg = $false; img = "sushi,roll" },
                @{ name = "Vegetable Tempura Roll"; desc = "Crispy veg tempura, avocado"; price = 260; veg = $true; img = "sushi,tempura" },
                @{ name = "Spicy Tuna Roll"; desc = "Tuna, spicy mayo, scallion"; price = 340; veg = $false; img = "sushi,tuna" },
                @{ name = "Avocado Cucumber Roll"; desc = "Simple, fresh, clean"; price = 220; veg = $true; img = "sushi,avocado" }
            )},
            @{ name = "Ramen and Mains"; items = @(
                @{ name = "Tonkotsu Ramen"; desc = "Rich pork-bone broth, chashu, egg"; price = 380; veg = $false; img = "ramen,noodles" },
                @{ name = "Miso Ramen"; desc = "Fermented soybean broth, corn, scallion"; price = 350; veg = $true; img = "ramen,soup" },
                @{ name = "Chicken Katsu Curry"; desc = "Crispy chicken cutlet, Japanese curry"; price = 360; veg = $false; img = "katsu,curry" },
                @{ name = "Teriyaki Chicken Don"; desc = "Grilled chicken over rice"; price = 340; veg = $false; img = "teriyaki,chicken" },
                @{ name = "Vegetable Gyoza (6pc)"; desc = "Pan-fried dumplings"; price = 220; veg = $true; img = "dumplings,gyoza" },
                @{ name = "Miso Soup"; desc = "Classic starter soup"; price = 120; veg = $true; img = "miso,soup" }
            )}
        )
    },
    @{
        name = "Seoul Garden"; bg = "D35400"; fg = "FFFFFF"; cuisine = "Korean"; desc = "Modern Korean comfort food"
        address = "45 Hanok Street"; city = "Delhi"; pincode = "110012"; lat = 28.6200; lng = 77.2150
        imgKeyword = "korean,restaurant,food"
        categories = @(
            @{ name = "Korean Favorites"; items = @(
                @{ name = "Bibimbap"; desc = "Mixed rice bowl, vegetables, gochujang"; price = 340; veg = $true; img = "bibimbap,korean" },
                @{ name = "Bulgogi Beef"; desc = "Marinated grilled beef"; price = 420; veg = $false; img = "beef,korean" },
                @{ name = "Korean Fried Chicken"; desc = "Double-fried, sweet-spicy glaze"; price = 380; veg = $false; img = "friedchicken,korean" },
                @{ name = "Kimchi Jjigae"; desc = "Spicy kimchi stew with tofu"; price = 320; veg = $true; img = "kimchi,stew" },
                @{ name = "Japchae"; desc = "Glass noodles, vegetables, sesame"; price = 300; veg = $true; img = "noodles,korean" }
            )},
            @{ name = "Sides and Snacks"; items = @(
                @{ name = "Kimchi"; desc = "Traditional fermented cabbage"; price = 100; veg = $true; img = "kimchi" },
                @{ name = "Tteokbokki"; desc = "Spicy rice cakes"; price = 260; veg = $true; img = "ricecake,korean" },
                @{ name = "Korean Corn Cheese"; desc = "Sweetcorn, mozzarella, mayo"; price = 240; veg = $true; img = "corn,cheese" },
                @{ name = "Mandu (Dumplings)"; desc = "Pan-fried pork dumplings"; price = 280; veg = $false; img = "dumplings,korean" },
                @{ name = "Banana Milk"; desc = "Classic Korean sweet drink"; price = 90; veg = $true; img = "milk,drink" }
            )}
        )
    },
    @{
        name = "Dragon Wok"; bg = "B71C1C"; fg = "FFD700"; cuisine = "Chinese"; desc = "Sichuan and Cantonese classics"
        address = "88 Dragon Court"; city = "Delhi"; pincode = "110013"; lat = 28.6100; lng = 77.2050
        imgKeyword = "chinese,restaurant,food"
        categories = @(
            @{ name = "Starters"; items = @(
                @{ name = "Chilli Paneer"; desc = "Crispy paneer, peppers, soy-chilli sauce"; price = 260; veg = $true; img = "paneer,chinese" },
                @{ name = "Chicken Manchurian"; desc = "Fried chicken, tangy sauce"; price = 300; veg = $false; img = "chinese,chicken" },
                @{ name = "Spring Rolls (4pc)"; desc = "Vegetable-stuffed crispy rolls"; price = 200; veg = $true; img = "springrolls" },
                @{ name = "Dragon Chicken"; desc = "Spicy Sichuan-style fried chicken"; price = 320; veg = $false; img = "friedchicken,spicy" }
            )},
            @{ name = "Mains"; items = @(
                @{ name = "Kung Pao Chicken"; desc = "Peanuts, dry chillies, sichuan pepper"; price = 340; veg = $false; img = "kungpao,chicken" },
                @{ name = "Veg Hakka Noodles"; desc = "Stir-fried noodles, mixed vegetables"; price = 260; veg = $true; img = "noodles,vegetables" },
                @{ name = "Schezwan Fried Rice"; desc = "Spicy fried rice"; price = 240; veg = $true; img = "friedrice" },
                @{ name = "Sweet and Sour Fish"; desc = "Battered fish, tangy sauce"; price = 380; veg = $false; img = "fish,chinese" },
                @{ name = "Mapo Tofu"; desc = "Silken tofu, spicy minced sauce"; price = 280; veg = $true; img = "tofu,chinese" },
                @{ name = "Chow Mein"; desc = "Classic stir-fried noodles"; price = 250; veg = $true; img = "noodles,chowmein" }
            )}
        )
    },
    @{
        name = "Punjab Tadka"; bg = "E67E22"; fg = "FFFFFF"; cuisine = "North Indian"; desc = "Rich, buttery North Indian classics"
        address = "21 Dhaba Road"; city = "Delhi"; pincode = "110014"; lat = 28.6250; lng = 77.2200
        imgKeyword = "indian,restaurant,curry"
        categories = @(
            @{ name = "Curries"; items = @(
                @{ name = "Butter Chicken"; desc = "Creamy tomato curry, tandoori chicken"; price = 360; veg = $false; img = "butterchicken,curry" },
                @{ name = "Paneer Butter Masala"; desc = "Cottage cheese in rich tomato gravy"; price = 300; veg = $true; img = "paneer,curry" },
                @{ name = "Dal Makhani"; desc = "Slow-cooked black lentils, cream"; price = 240; veg = $true; img = "dal,lentils" },
                @{ name = "Rajma Chawal"; desc = "Kidney bean curry with rice"; price = 220; veg = $true; img = "rajma,rice" },
                @{ name = "Chicken Tikka Masala"; desc = "Grilled chicken in spiced gravy"; price = 340; veg = $false; img = "curry,chicken" }
            )},
            @{ name = "Breads and Sides"; items = @(
                @{ name = "Butter Naan"; desc = "Soft tandoori bread"; price = 60; veg = $true; img = "naan,bread" },
                @{ name = "Garlic Naan"; desc = "Naan topped with garlic and butter"; price = 70; veg = $true; img = "naan,garlic" },
                @{ name = "Tandoori Roti"; desc = "Whole wheat tandoori bread"; price = 40; veg = $true; img = "roti,bread" },
                @{ name = "Jeera Rice"; desc = "Cumin-flavoured basmati rice"; price = 180; veg = $true; img = "rice,indian" },
                @{ name = "Amritsari Kulcha"; desc = "Stuffed leavened bread"; price = 90; veg = $true; img = "kulcha,bread" },
                @{ name = "Mango Lassi"; desc = "Sweet yogurt mango drink"; price = 110; veg = $true; img = "lassi,mango" }
            )}
        )
    },
    @{
        name = "Malabar Spice"; bg = "27AE60"; fg = "FFFFFF"; cuisine = "South Indian"; desc = "Coastal South Indian flavours"
        address = "9 Coconut Grove"; city = "Delhi"; pincode = "110015"; lat = 28.6050; lng = 77.2000
        imgKeyword = "southindian,dosa,food"
        categories = @(
            @{ name = "Tiffin"; items = @(
                @{ name = "Masala Dosa"; desc = "Crispy rice crepe, potato filling"; price = 160; veg = $true; img = "dosa" },
                @{ name = "Plain Dosa"; desc = "Classic crispy dosa"; price = 130; veg = $true; img = "dosa,crepe" },
                @{ name = "Idli Sambar (4pc)"; desc = "Steamed rice cakes, lentil soup"; price = 120; veg = $true; img = "idli" },
                @{ name = "Medu Vada (3pc)"; desc = "Crispy lentil doughnuts"; price = 110; veg = $true; img = "vada,indian" },
                @{ name = "Uttapam"; desc = "Thick savoury pancake, onion and tomato"; price = 150; veg = $true; img = "uttapam,dosa" }
            )},
            @{ name = "Mains"; items = @(
                @{ name = "Kerala Fish Curry"; desc = "Tangy coconut fish curry"; price = 340; veg = $false; img = "fishcurry" },
                @{ name = "Chicken Chettinad"; desc = "Spicy Tamil-style chicken curry"; price = 320; veg = $false; img = "chicken,curry" },
                @{ name = "Curd Rice"; desc = "Comforting yogurt rice"; price = 140; veg = $true; img = "rice,yogurt" },
                @{ name = "Sambar Rice"; desc = "Lentil-vegetable stew over rice"; price = 160; veg = $true; img = "sambar,rice" },
                @{ name = "Filter Coffee"; desc = "Traditional South Indian coffee"; price = 60; veg = $true; img = "coffee" },
                @{ name = "Payasam"; desc = "Sweet rice and milk dessert"; price = 100; veg = $true; img = "dessert,indian" }
            )}
        )
    },
    @{
        name = "Golden Bun Diner"; bg = "F1C40F"; fg = "2C3E50"; cuisine = "American"; desc = "Classic burgers, fries, and shakes"
        address = "3 Route 66 Plaza"; city = "Delhi"; pincode = "110016"; lat = 28.6300; lng = 77.2100
        imgKeyword = "diner,burger,restaurant"
        categories = @(
            @{ name = "Burgers"; items = @(
                @{ name = "Classic Cheeseburger"; desc = "Beef patty, cheddar, pickles"; price = 260; veg = $false; img = "burger,cheese" },
                @{ name = "Crispy Chicken Burger"; desc = "Fried chicken fillet, slaw"; price = 240; veg = $false; img = "burger,chicken" },
                @{ name = "Veggie Supreme Burger"; desc = "Grilled veg patty, all the fixings"; price = 210; veg = $true; img = "burger,veggie" },
                @{ name = "Double Bacon Smash"; desc = "Double patty, bacon, smoked cheese"; price = 320; veg = $false; img = "burger,bacon" }
            )},
            @{ name = "Sides and Shakes"; items = @(
                @{ name = "Classic Fries"; desc = "Golden, crispy, salted"; price = 120; veg = $true; img = "fries" },
                @{ name = "Loaded Cheese Fries"; desc = "Fries, cheese sauce, jalapenos"; price = 180; veg = $true; img = "fries,cheese" },
                @{ name = "Onion Rings"; desc = "Crispy battered onion rings"; price = 150; veg = $true; img = "onionrings" },
                @{ name = "Chocolate Milkshake"; desc = "Thick and creamy"; price = 160; veg = $true; img = "milkshake,chocolate" },
                @{ name = "Strawberry Milkshake"; desc = "Classic fruity shake"; price = 160; veg = $true; img = "milkshake,strawberry" },
                @{ name = "Coleslaw"; desc = "Creamy cabbage slaw"; price = 90; veg = $true; img = "coleslaw,salad" }
            )}
        )
    }
)

Write-Host "Seeding $($restaurants.Count) restaurants..." -ForegroundColor Cyan

foreach ($r in $restaurants) {
    Write-Host "`nCreating: $($r.name)" -ForegroundColor Yellow

    $createBody = @{
        name = $r.name; description = $r.desc; cuisineType = $r.cuisine
        addressLine = $r.address; city = $r.city; pincode = $r.pincode
        openingTime = "09:00"; closingTime = "23:00"
        latitude = $r.lat; longitude = $r.lng; imageUrl = (Get-Img $r.name $r.bg $r.fg)
    } | ConvertTo-Json

    $restaurant = Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/restaurants" -Method Post -Headers $headers -ContentType "application/json" -Body $createBody

    Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/restaurants/$($restaurant.id)/status" -Method Patch -Headers $headers -ContentType "application/json" -Body '{"status":"OPEN"}' | Out-Null

    $itemCount = 0
    foreach ($cat in $r.categories) {
        $catBody = @{ name = $cat.name } | ConvertTo-Json
        $category = Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/restaurants/$($restaurant.id)/categories" -Method Post -Headers $headers -ContentType "application/json" -Body $catBody

        foreach ($item in $cat.items) {
            $itemBody = @{
                name = $item.name; description = $item.desc; price = $item.price
                veg = $item.veg; categoryId = $category.id; imageUrl = (Get-Img $item.name $r.bg $r.fg)
            } | ConvertTo-Json

            Invoke-RestMethod -Uri "http://localhost:8080/api/restaurant-owner/restaurants/$($restaurant.id)/food-items" -Method Post -Headers $headers -ContentType "application/json" -Body $itemBody | Out-Null
            $itemCount++
        }
    }
    Write-Host "  -> $itemCount items added" -ForegroundColor Green
}

Write-Host "`nDone. $($restaurants.Count) restaurants seeded with full menus and keyword-matched images." -ForegroundColor Cyan