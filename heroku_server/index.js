const express = require('express')
const bodyParser = require('body-parser')
const PORT = process.env.PORT || 5000


// Firebase Admin SDK
const admin = require("firebase-admin");
const serviceAccount = require("./serviceAccountKey.json");

// For make network calls
const request = require('request-promise');

// Initialize Firebase Admin
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "<DATABASE_URL>"
});


// Initialize Express and create endpoint
express()
  .use(bodyParser.json()) // Parse json in request body
  .use(bodyParser.urlencoded({
    extended: true
  }))
  .post('/createCustomToken', (req, res) => {
     if (req.body.id_token === undefined) {
         // idToken is not find
         const ret = {
           error_message: 'id_token not found',
         };
         return res.status(400).send(ret);
       }

       // Verify idToken
       // Create new user on Firebase if user doesn't exist
       // Generate custom auth token
       // Return client
       return verifyHuaweiToken(req.body)
         .then((customAuthToken) => {
           const ret = {
             firebase_token: customAuthToken,
           };
           return res.status(200).send(ret);
         }).catch((err) => {
           return res.status(400).send(err);
         });
   })
  .listen(PORT, () => console.log(`Listening on ${ PORT }`));


// Verify idToken on Huawei Server
function verifyHuaweiToken(body) {
  return request({
    method: 'GET',
    uri: 'https://oauth-login.cloud.huawei.com/oauth2/v3/tokeninfo?id_token=' + body.id_token,
    json: true
  }).then((response) => {
    // Token invalid. Throw an error and stop process
    if(response.error !== undefined){
      return Promise.reject(new Error('Something went wrong'));
    }

    // Get user
    return getFirebaseUser(body);
  }).then((userRecord) => {

    // After user created on Firebase, create new custom token based on user uid
    return admin.auth().createCustomToken(userRecord.uid);
  }).then((token) => {

    // Return token to client
    return token;
  });
}

function getFirebaseUser(body) {
  const firebaseUid = 'huawei_' + body.uid;

  // Find user by user uid
  return admin.auth().getUser(firebaseUid).then(function(userRecord) {
    return userRecord;
  }).catch((error) => {
    // If user is not exist on Firebase, create new one
    if (error.code === 'auth/user-not-found') {
        return admin.auth().createUser({
          uid: firebaseUid,
          displayName: body.name,
          photoURL: body.picture,
          email: body.email
        });
    }
    return Promise.reject(error);
  });
}

