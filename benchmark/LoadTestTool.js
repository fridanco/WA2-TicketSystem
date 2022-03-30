'use strict';

import loadtest from 'loadtest';
import createRandomRequestBody from './RandomRequestBodyGenerator.js'


export default async function runLoadTester(concurrencyLevel, runWithDB, maxSeconds, timeout, agentKeepAlive){

    const options = {
        url: 'http://localhost:8080',
        maxRequests: 10000,
        concurrency:concurrencyLevel,
        method: 'POST',
        body:'',
        requestGenerator: (params, options, client, callback) => {
            let randomReqBody = createRandomRequestBody(runWithDB);
            const message = randomReqBody;
            options.headers['Content-Length'] = message.length;
            options.headers['Content-Type'] = 'application/json';
            options.body = randomReqBody;
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





