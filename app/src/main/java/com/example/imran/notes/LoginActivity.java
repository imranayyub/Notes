package com.example.imran.notes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import static com.example.imran.notes.HomeActivity.databaseNote;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    public static SharedPreferences pref;

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;
    public static GoogleApiClient mGoogleApiClient;
    private Context context;
    public static GoogleSignInClient googleSignInClient;
    private ProgressDialog mProgressDialog;
    private Button gmailSigninButton;
    public static String userName, email, userPic;
    public static Boolean login = false;

    public DatabaseReference databaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gmailSigninButton = (Button) findViewById(R.id.gmailSigninButton);
        gmailSigninButton.setOnClickListener(this);
        isNetworkAvailable();
//Configures sign-in to request the user's ID, email address, and basic profile.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("155298904057-h9ilq6caimp9j3uuptogiah5cmpv1u18.apps.googleusercontent.com")
                .build();
// Builds a GoogleSignInClient with the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this).addOnConnectionFailedListener(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        databaseUser = FirebaseDatabase.getInstance().getReference("User");

        //checks if user is already loggedin and perform suitable action.
        try {
            pref = getApplicationContext().getSharedPreferences("MyPref", 0);
            if (pref.getBoolean("login", false) == true) {
                signIn();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //oncClick method to perform action according to the button being clicked.
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.gmailSigninButton: {
                signIn();
                break;
            }
        }
    }

    //google sign in function
    private void signIn() {
        boolean connected = isNetworkAvailable();
        //If network is Available then perform Signin.
        if (connected == true) {
//            c.setApp("Google");
//            dbhelp.insert(c);
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    //sign out  function for google
    public void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }


    //function to fetch the google log in data(Name , Email and profile) for current profile logged in
    public void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (!result.isSuccess()) {

        } else {
            // Signed in successfully, show authenticated UI.
            mProgressDialog = ProgressDialog.show(this, "", "Please Wait...", true);
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e(TAG, "display name: " + acct.getDisplayName());
            userName = acct.getDisplayName();
            email = acct.getEmail();
            if (acct.getPhotoUrl() != null) {
                userPic = acct.getPhotoUrl().toString();
            } else {
                userPic = "Nopic";
            }

//trying to get the token.
            String token=acct.getIdToken();


//            final String SCOPES = " https://www.googleapis.com/auth/plus.profile.emails.read ";
//
//            try {
//                token = GoogleAuthUtil.getToken(
//                        getApplicationContext(), acct.getEmail(),
//                        "oauth2:" + SCOPES);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (GoogleAuthException e) {
//                e.printStackTrace();
//            }

            //putting data in SharedPreferences.
            pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            login = true;
            editor.putBoolean("login", login);
            editor.commit();

            //Creating intent to HomeActivity.
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            addUser();
            mProgressDialog.dismiss();
//            Starting HomeActivity.
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            finish();
        }

    }

    //In case Connnection is Failed.
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //Receives result from the function being called in MainActivity.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }

    //Funtion to check Network Availability.
    boolean connected = false;

    private boolean isNetworkAvailable() {
        //ConnnectivityManager queries about connectivity like mobile data, wifi and Gprs.
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //Network info define Status of network.
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            connected = false;
        } else {
            connected = true;
        }
        return connected;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public static String userId = "asdsa";

    //adds note in the Firebase realtime database.
    public void addUser() {
        userId = databaseUser.push().getKey();
        User user = new User(userId, userName, email);
        databaseUser.child(userId).setValue(user);
        Toast.makeText(this, "User Realtime Database!!!", Toast.LENGTH_SHORT).show();

    }
}
