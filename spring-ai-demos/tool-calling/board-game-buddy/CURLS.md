
#Linux o macosx

curl -X POST http://localhost:8080/ask \
-H "Content-Type: application/json" \
-d '{
"question": "What is the complexity?",
"gameTitle": "Puerto Rico"
}'


#Windows powershell



$body = @{
question  = "What is the complexity?"
gameTitle = "Puerto Rico"
} | ConvertTo-Json

Invoke-RestMethod -Method POST `
    -Uri "http://localhost:8080/ask" `
-ContentType "application/json" `
-Body $body