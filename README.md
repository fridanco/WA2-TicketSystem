# WA2-TicketSystem

## Group members
- Deda Mario - s292645
- Di Franco Francesco - s284590
- Galliano Giacomo - s292482
- Gulotta Dario Paolo - s292474

## Project Structure
```
WA2-TICKETSYSTEM
.
|__benchmark
|    |__LoadTestResults
|    
|server
   |__src
       |__main
       |    |__kotliln
       |    |   |__...
       |    |      |__csv_creator
       |    |      |__server
       |    |
       |    |__resources
       |     
       |__test
```

## Test
### Unit tests
Their aim is to test individual modules of the application in isolation to confirm that the code is doing things right.

We've implemented the following Unit Tests:
- `fun rejectInvalidJWT()`: tests the validation module sending an invalid JWT signature
- `fun rejectExpiredJWT()`: tests the validation module sending an expired JWT token
- `fun rejectInvalidValidityZone()`: tests the validation module sending a JWT token with an invalid validity zone
- `fun rejectEmptyValidityZoneToken()`: tests the validation module sending a JWT token with an empty validity zone
- `fun acceptValidJWT()`: tests the validation module sending a valid JWT token without checking on a database
- `fun acceptUniqueTicket()`: tests the validation module sending a valid JWT token. Using a database checks if that `sub` identifier  is unique
- `fun rejectDuplicateTicket()`: tests the validation module sending a pair of equals JWT tokens. After checking on a database, it rejects them if they've the same `sub` identifier.

### Integration tests
Their aim is to check if different modules are working fine when combined together as a group.

We've implemented the following Integration Tests:
- `fun rejectInvalidJWT()`: tests the validation module sending an HTTP post request with an invalid JWT signature
- `fun rejectExpiredJWT()`: tests the validation module sending an HTTP post request with an expired JWT token
- `fun rejectInvalidValidityZone()`: tests the validation module sending an HTTP post request with a JWT token with an invalid validity zone
- `fun rejectEmptyZone()`: tests the validation module sending an HTTP post request with a JWT token with an empty validity zone
- `fun rejectEmptyJWT()`: tests the validation module sending an HTTP post request with an empty token
- `fun rejectEmptyRequest()`: tests the validation module sending an empty HTTP post request
- `fun acceptValidJWT()`: tests the validation module sending an HTTP post request without checking on a database
- `fun acceptUniqueTicket()`: tests the validation module sending an HTTP post request. Using a database checks if that `sub` identifier is unique
- `fun rejectDuplicateTicket()`: tests the validation module sending a pair of equals HTTP post requests. After checking on a database, it rejects them if they've the same `sub` identifier.

