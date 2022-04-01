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

## Project structure
### Server endpoints
The server exposes a single endpoint (`/validate`) which accepts exclusively POST requests by the client.

The endpoint will first validate the request body and make sure that both the zone and the token are not null nor not empty. If the check succeeds the request is mapped into a TicketDTO object.

After the validation succeeds, the TicketValidationService is responsible for performing all the various conformity checks such as: JWT signature validation, JWT expiration validation, ticket validity zone checks and ticket uniqueness checks (more on this below).

The JWT validation is performed using the `jjwt` library, which not only checks that signature of the token is valid but it also checks that the token has not expired. The JWT signature/key is stored in the *application.properties* file. This signature is `base64 encoded`, otherwise it will be considered invalid.

An additional JWT expiration check is performed after the above check to make sure that expiration constraint is properly enforced if for some reason the above check fails. To perform such check the expiry time of the JWT is compared with the current system time by converting both in the Milliseconds since Unix Epoch format.  

The ticket validity zone, which makes sure that the zone received in the request body matches the valid zones of the ticket, is case sensitive.

Ticket uniqueness is enforced by querying through Spring JPA a PostreSQL DB. In the DB a Ticket table having only 1 column (TicketID) is used. First a findById query is executed to make sure that the received and decoded TicketID (sub field in the JWT) is unique and if so it is inserted in the DB through a save query.

### DB connection properties
The PostgreSQL connection details and operation modes are specified in the *application.properties* file. Please make sure that the credentials match those configured in the PostgreSQL Docker container otherwise the connection is refused.

### Why perform 2 separate queries on the db?
While it is true that a save operation will fail if the primary key alredy exists (duplicate PK), by default PostgreSQL will not throw any exception if this situation is presented, since a throwing ON CONFLICT behaviour must be specified. Since this behaviour must be enabled in the PostgreSQL Docker container we decided that it is better to implement a more inclusive solution by performing 2 queries, the first to check if the ticket is unique and then a save is performed.

### How to switch on the additional DB check?
In order to simplify the testing & loadtesting procedures we have implemented the following logic to decide whether the DB check needs to performed or not:
- if the `sub` field of the JWT is empty: NO DB CHECK
- if the `sub` field of the JWT is not empty: DB CHECK IS PERFORMED

### Endpoint reply codes
If the received JWT passes all the validity checks a code 200 (HTTP OK) is returned, otherwise if any check fails a code 403 (HTTP FORBIDDEN) is returned.

All the validity checks (except the request body validation) are performed in the TicketValidationService.

The TicketValidationService will throw an custom and appropriate exception if any of the checks fails. By implementing a ControllerAdvisor we are able to manage such situations by telling the Controller to return a code 403 whenever such exceptions are thrown.


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

## Loadtest results

### Without DB checks

Keep-alive = false, timeout = default
![Graph](./server/src/main/kotlin/it/polito/wa2/g17/csv_creator/throughput_db=false.svg)


Keep-alive = false, timeout = 100ms

0% of requests timed out. This was expected since the mean latency of the system is just 33ms. 10% increase in throughput.
![Graph](server/src/main/kotlin/it/polito/wa2/g17/csv_creator/throughput_db=false_timeout=100.svg)


Keep-alive = false, timeout = 10ms

45% of requests timed out, 33ms mean latency. 10% increase in throughput.
![Graph](server/src/main/kotlin/it/polito/wa2/g17/csv_creator/throughput_db=false_timeout=10.svg)


Keep-alive = true, timeout = default

Considerable increase of 110% in throughput. Expected since the same TCP connections are reused for the different requests instead of creating one for each request. This optimization removes the TCP connection open/closing overheads.
![Graph](server/src/main/kotlin/it/polito/wa2/g17/csv_creator/throughput_db=false_keepalive=true.svg)


### With DB checks

Keep-alive = false, timeout = default
![Graph](server/src/main/kotlin/it/polito/wa2/g17/csv_creator/throughput_db=true.svg)


Keep-alive = false, timeout = 100ms

Error rate increased from 50% to 70%, no significant increase in throughput since the timeout most likely occurs while the DB check is in progress so resource contention does not improve.
![Graph](server/src/main/kotlin/it/polito/wa2/g17/csv_creator/throughput_db=true_timeout=100.svg)


Keep-alive = false, timeout = 50ms

Error rate increased from 50% to 75%, no significant increase in throughput since the timeout most likely occurs while the DB check is in progress so resource contention does not improve.
![Graph](server/src/main/kotlin/it/polito/wa2/g17/csv_creator/throughput_db=true_timeout=50.svg)


Keep-alive = false, timeout = 25ms

Error rate increased from 50% to 95%, no significant increase in throughput since the timeout most likely occurs while the DB check is in progress so resource contention does not improve.
![Graph](server/src/main/kotlin/it/polito/wa2/g17/csv_creator/throughput_db=true_timeout=25.svg)


Keep-alive = true, timeout = default

Considerable increase of 35% in throughput. Expected since the same TCP connections are reused for the different requests instead of creating one for each request. This optimization removes the TCP connection open/closing overheads. Even though a boost of 35% in throughput is considerable, due to involvment of the DB in additional checks it still remains much lower the throughput increase observed in the "without DB checks" reported above.

![Graph](server/src/main/kotlin/it/polito/wa2/g17/csv_creator/throughput_db=true_keepalive=true.svg)
