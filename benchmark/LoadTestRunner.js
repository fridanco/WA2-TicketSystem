import runLoadTester from "./LoadTestTool.js";
import fs from 'fs'
import csvGenerator from 'server.it.polito.wa2.g17.csv_creator.generateCSV()'

const concurrencyValues = [1,2,4,8,16,32]

const runArgs = process.argv[0];
let runWithDB, ttl, maxSeconds, timeout;
if(runArgs.useDB){
    runWithDB = runArgs.useDB;
}
else{
    runWithDB = null;
}

if(runArgs.ttl){
    ttl = runArgs.ttl;
}
else{
    ttl = null;
}

if(runArgs.maxSeconds){
    maxSeconds = runArgs.maxSeconds;
}
else{
    maxSeconds = null;
}

if(runArgs.timeout){
    timeout = runArgs.timeout;
}
else{
    timeout = null;
}


for (const concurrencyValue of concurrencyValues){
    let testResult = await runLoadTester(concurrencyValue, runWithDB, ttl, maxSeconds, timeout)

    console.log("Done with "+concurrencyValue+" concurrent requests in "+testResult.totalTimeSeconds+"s")
    fs.writeFileSync('./LoadTestResults/result_'+concurrencyValue, JSON.stringify(testResult, null, 2))
}

csvGenerator.generateCSV()

console.log("-------------------------------------------------------------")
console.log("LOAD TEST DONE - results saved in ./LoadTestResults directory")
console.log("-------------------------------------------------------------")