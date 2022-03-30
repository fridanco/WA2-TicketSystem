'use strict';

import loadtest from 'loadtest';
import fs from 'fs'
import {createRandomRequestBody} from './RandomRequestBodyGenerator.js'

const concurrencyValues = [1,2,4,8,16,32]

for (const concurrencyValue of concurrencyValues){
    let testResult = await runLoadTester(concurrencyValue)

    console.log("Done with "+concurrencyValue+" concurrent requests in "+testResult.totalTimeSeconds+"s")
    fs.writeFileSync('./LoadTestResults/result_'+concurrencyValue, JSON.stringify(testResult, null, 2))
}

console.log("-------------------------------------------------------------")
console.log("LOAD TEST DONE - results saved in ./LoadTestResults directory")
console.log("-------------------------------------------------------------")


async function runLoadTester(concurrencyLevel){

    const options = {
        url: 'http://localhost:8080',
        maxRequests: 10000,
        concurrency:concurrencyLevel,
        method: 'POST',
        body:'',
        requestGenerator: (params, options, client, callback) => {
            let reqBody = createRandomRequestBody();
            const message = reqBody;
            options.headers['Content-Length'] = message.length;
            options.headers['Content-Type'] = 'application/json';
            options.body = reqBody;
            options.path = '/validate';
            const request = client(options, callback);
            request.write(message);
            return request;
        }
    };

    return await new Promise(resolve =>  {
        loadtest.loadTest(options, (error, results) => {
            if (error) {
                return console.error('Got an error: %s', error);
            }
            resolve(results)
        })
    })
}





