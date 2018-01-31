package com.example.imran.notes.activities;

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

import com.example.imran.notes.interfaces.ApiInterface;
import com.example.imran.notes.R;
import com.example.imran.notes.model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.imran.notes.notification.MyFirebaseInstanceIDService.recent_token;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    public static SharedPreferences loginPref;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 007;
    public GoogleApiClient mGoogleApiClient;
    public static GoogleSignInClient googleSignInClient;
    private ProgressDialog mProgressDialog;
    private Button gmailSigninButton;
    public static String userName, email, userPic, serverToken;
    public static Boolean login = false;


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

        //checks if user is already loggedin and perform suitable action.
        try {
            loginPref = getApplicationContext().getSharedPreferences("MyPref", 0);
            if (loginPref.getBoolean("login", false) == true) {
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

            //putting data in SharedPreferences.
            loginPref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = loginPref.edit();
            login = true;
            editor.putBoolean("login", login);
            editor.commit();


            //to get the Idtoken.
            final String token = acct.getId();
            //HttpCLient to Add Authorization Header.
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS).build();

            //Retrofit to retrieve JSON data from server.
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ApiInterface.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())     //Using GSON to Convert JSON into POJO.
                    .build();

            ApiInterface apiService = retrofit.create(ApiInterface.class);
            try {
                //initializing User constructor to send data to server.
                User user = new User(email, token, FirebaseInstanceId.getInstance().getToken());
                user.setEmail(email);
                user.setToken(token);
                user.setFcm_token(FirebaseInstanceId.getInstance().getToken());
                //calls function loginUser of ApiInterface and enquques(sends request asynchronously)request and and notify callback of its response or if an error occurred talking to the server, creating the request, or processing the response.
                apiService.loginUser(user).enqueue(new Callback<User>() {
                    //in case server responds.
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        mProgressDialog.dismiss();
                        //if responsse is successful/
                        if (response.isSuccessful()) {
                            Log.i("here:", "post submitted to API." + response.body().toString());
                            User user = response.body();
                            serverToken = user.getToken(); //getting server token to be used for authentication purpose.
                            Log.i("token : ", user.getToken());
                            Toast.makeText(getApplicationContext(), "Login Successful..!! ", Toast.LENGTH_SHORT).show();
                            Intent main = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(main);
                            finish();

                        } else if (response.code() == 500) {
                            Toast.makeText(getApplicationContext(), "Some Error occured(Iternal ", Toast.LENGTH_SHORT).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(getApplicationContext(), "Wrong Email or Password..", Toast.LENGTH_SHORT).show();
                        }

                    }

                    //in case fails to connect to server.
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        t.printStackTrace();
                        mProgressDialog.dismiss();
                        Log.e("here", "Unable to submit post to API.");
                        Toast.makeText(getApplicationContext(), "Login failed ", Toast.LENGTH_SHORT).show();

                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
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

}
