# Simple Bank API - Kotlin + Spring Boot

### I took the opportunity to learn more about Kotlin using Spring Boot to help me handle all dependencies

### I choose to use Spring Boot to create an API instead of a `regular` Kotlin project

## ENDPOINTS

### There are 9 Endpoints

### GET /api/v1/accounts/

- Get All Accounts

### GET /api/v1/accounts?type=checking,savings

- Get Accounts of specifics types

### GET /api/v1/account/history/{iban}

- Get Account all the Transaction History

### GET /api/v1/account/balance/{iban}

- Get The Balance of an Account

### POST /api/v1/account/

- Create a new Account
- Json Body:
    - `{"name": "Evandro","type": "checking"}`

### POST /api/v1/account/deposit

- Deposit an amount of money to an Account
- Json Body:
    - `{"iban": "DE28622854175757817555","amount": 200,"type": "checking"}`

### POST /api/v1/account/transfer

- Transfer money from an Account to another
- Json Body:
    - `{"fromIban": "DE00510472303813552056","toIban": "DE58587756655379707273","amount": 10 }`

### POST /api/v1/account/lock

- Lock an Account
- Json Body:
    - `{"iban":"DE49824259205216866183"}`

### POST /api/v1/account/unlock

- Unlock an Account
- Json Body:
    - `{"iban":"DE49824259205216866183"}`

## Test

### Please go to `test` folder and take a look into the integration tests

## Fell free to play and improve it

#### I also added an Insomnia export file to help with the Endpoints
