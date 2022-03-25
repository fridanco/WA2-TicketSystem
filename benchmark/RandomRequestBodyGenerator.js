'use strict'

import jwt from 'jsonwebtoken'

export default function createRandomRequestBody() {

    let jwt_payload = {
        exp: Math.floor(Date.now() / 1000) + randomIntFromInterval(-1,3) * 30,
        vz: randomString(),
        sub: randomTicketIdentifier()
    }

    let token = jwt.sign(jwt_payload,Buffer.from("asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4", 'base64'))

    let zone = randomZone(jwt_payload.vz)

    let requestBody = JSON.stringify({
        zone: zone,
        token: token
    })

    //console.log(requestBody)

    return requestBody
}


function randomIntFromInterval(min, max) { // min and max included 
    return Math.floor(Math.random() * (max - min + 1) + min)
}

function randomString() {

    let randomStr = ""
    let possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    let randomInt = randomIntFromInterval(0,10)
    for(let i=0;i<randomInt;i++){
        randomStr += possible.charAt(Math.floor(Math.random() * possible.length));
    }

    return randomStr
}

function randomTicketIdentifier() {

    let randomTicketID = ""
    let possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    for(let i=0;i<15;i++){
        randomTicketID += possible.charAt(Math.floor(Math.random() * possible.length));
    }

    return randomTicketID
}

function randomZone(jwtValidityZones) {
    let possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    if(randomIntFromInterval(0,1)){
        return possible.charAt(Math.floor(Math.random() * possible.length));
    }
    let zone = jwtValidityZones.charAt(Math.floor(Math.random() * jwtValidityZones.length));
    return zone ? zone : ""


}