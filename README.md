

# Firebase Auth with Huawei ID

<img src="https://github.com/ekrmh/firebase-auth-with-huawei-id/blob/master/demo.gif" data-canonical-src="https://github.com/ekrmh/firebase-auth-with-huawei-id/blob/master/demo.gif" width="300" height="600" />

![Firebase Auth](https://github.com/ekrmh/firebase-auth-with-huawei-id/blob/master/demo2.png)

## Introduction
In this repo contains that sample to use custom auth tokens to Sign In Firebase with Huawei ID. 

As is known, Firebase Auth is a GMS dependent service, so it cannot work on non-GMS devices. Instead, we will use [ Firebase Auth Rest API](https://firebase.google.com/docs/reference/rest/auth).

I used heroku as a backend server but instead, also you can use Firebase Cloud Functions, HMS Cloud Functions or your own backend server.

You can see flow at the below.

![Flow](https://github.com/ekrmh/firebase-auth-with-huawei-id/blob/master/flow.png)

**1. Request Authorization Code**<br/>
 Send an authorization request to Huawei Account Server and start login flow.<br/>
**2. Obtain ID Token**<br/>
After sign-in authorization is successfully , call the HuaweiAuthManager.parseAuthResultFromIntent method of onActivityResult and obtain ID Token, display name, profile picture etc.<br/>
**3. Verify ID Token** (Optional*)<br/>
This is an optional step. If you want, you can verify user ID token locally on client or verify on Huawei server on your backend.<br/>
 
 [Verify ID Token](https://developer.huawei.com/consumer/en/doc/development/HMS-Guides/account-guide-v4#h2-1-2-sending-an-authorization-request-to-obtain-an-id-token)<br/>
 
**4. Get user or create new user**<br/>
Bring the user from Firebase according to the uid or create a new user.<br/>
**5. Create Firebase Auth Custom Token**<br/>
Create custom token using the Firebase Admin SDK on your backend server.<br/>

[Firebase Custom Tokens](https://firebase.google.com/docs/auth/admin/create-custom-tokens?authuser=0#create_custom_tokens_using_the_firebase_admin_sdk)<br/>

**6. Login with custom token using Firebase Auth**<br/>
 Exchange custom token for an ID and refresh token.<br/>

[Firebase Auth Rest Custom Token](https://firebase.google.com/docs/reference/rest/auth#section-verify-custom-token)<br/>

## Setup 

### AppGallery & Android
- Enable Account Kit
- Add **agconnect-services.json** into the your android project. ( android\app )

### Firebase & Android
- Enable Firebase Auth
- [Configure Firebase Admin SDK](https://firebase.google.com/docs/admin/setup#add_firebase_to_your_app) and add it into your server project. 
- Add **serviceAccountKey.json** into the your server project. ( heroku_server )
- Add **google-services.json** into the your android project. ( android\app )
- Configure WEB **<API_KEY>.**(android\app\src\main\java\com\ekrmh\firebasehuaweiauth\util\NetworkSingleton.kt)
 - 
 
 ### Backend & Android

 - Configure  **<DATABASE_URL>** (heroku_server/index.js)
 -  Configure your server url. **<YOUR_SERVER_URL>** (android\app\src\main\java\com\ekrmh\firebasehuaweiauth\util\NetworkSingleton.kt)

### Author
Ekrem Hatipoglu
ekrem.hatipoglu@huawei.com

If you encounter any problems, you can always contact me.
