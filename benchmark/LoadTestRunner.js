import runLoadTester from "./LoadTestTool.js";
import fs from 'fs'

const concurrencyValues = [1,2,4,8,16,32]

for (const concurrencyValue of concurrencyValues){
    let testResult = await runLoadTester(concurrencyValue)

    console.log("Done with "+concurrencyValue+" concurrent requests in "+testResult.totalTimeSeconds+"s")
    fs.writeFileSync('./LoadTestResults/result_'+concurrencyValue, JSON.stringify(testResult, null, 2))
}

console.log("-------------------------------------------------------------")
console.log("LOAD TEST DONE - results saved in ./LoadTestResults directory")
console.log("-------------------------------------------------------------")