# Mini Banking service
_______________________________________

This service implement a RESTful API for money transfers between accounts.
(without Spring and Google Injections)

## API

Description    | Method | URL          | Request        | Success Response 
---------------|--------|--------------|----------------|---------
Create account | POST   |/create       | Account        | Account created with id: {id}
Get account    | GET    |/account/{id} |                | Account
Delete account | DELETE |/account/{id} |                | Account deleted with id: {id}
Transfer mooney| POST   |/transfer     | TransferRequest| Transfer success

## Data type
Account
```json
{
    "id": 1,
    "balance": 200
}
```

TransferRequest
```json
{
    "idFrom": 1,
    "idTo": 2,
    "amount": 200
}
```

## Run test

To run tests `mvn test`.

In the me.boblocker.ApplicationTestIT class there are integration tests. 
Application start on port 8081 for integration tests. 
You can change default port in application.properties file in resources for test. 

## Build an executable JAR

To build a single executable JAR file that contains all the necessary dependencies,
classes, and resources run `mvn clean package`.

Application start on port 8080. You can change default port in application.properties file.

## Run application

Run the JAR file:
`java -jar target/mini-banking-1.0-SNAPSHOT-with-dependencies.jar`

