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
