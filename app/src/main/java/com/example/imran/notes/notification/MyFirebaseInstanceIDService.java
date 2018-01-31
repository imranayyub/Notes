package com.example.imran.notes.notification;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.imran.notes.activities.HomeActivity;
import com.example.imran.notes.activities.LoginActivity;
import com.example.imran.notes.interfaces.ApiInterface;
import com.example.imran.notes.model.User;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.imran.notes.activities.LoginActivity.email;

/**
 * Created by imran on 29/1/18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    public static String recent_token;

    //fetches recent Firebase Token to send firebase Notification.
    @Override
    public void onTokenRefresh() {
        recent_token = FirebaseInstanceId.getInstance().getToken();
        updateFcmToken(email, recent_token);

    }

    private void updateFcmToken(String email, String recent_token) {


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
            User user = new User(email, "",recent_token);
            user.setEmail(email);
            user.setFcm_token(FirebaseInstanceId.getInstance().getToken());
            //calls function loginUser of ApiInterface and enquques(sends request asynchronously)request and and notify callback of its response or if an error occurred talking to the server, creating the request, or processing the response.
            apiService.updateFcmToken(user).enqueue(new Callback<User>() {
                //in case server responds.
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    //if responsse is successful/
                    if (response.isSuccessful()) {
                        Log.i("here:", "post submitted to API." + response.body().toString());
                        User user = response.body();

                    } else if (response.code() == 500) {
                    } else if (response.code() == 404) {
                   }

                }

                //in case fails to connect to server.
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    t.printStackTrace();
                   Log.e("here", "Unable to submit post to API.");
//                    Toast.makeText(getApplicationContext(), "Token update failed ", Toast.LENGTH_SHORT).show();

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
