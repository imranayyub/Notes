package com.example.imran.notes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.example.imran.notes.activities.HomeActivity;
import com.google.firebase.appindexing.Action;

import static com.example.imran.notes.activities.LoginActivity.loggedIn;


public class InternetConnectivityListener extends BroadcastReceiver {
    public InternetConnectivityListener() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (loggedIn == 1)
            checkInternetConnection(context);
    }

    private void checkInternetConnection(final Context context) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (HomeActivity.isInternetAvailable(context) == false) {
                    // retry once after 30 seconds
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (HomeActivity.isInternetAvailable(context) == false) {
                                Toast.makeText(context, "Internet Not available", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Internet available", Toast.LENGTH_SHORT).show();


                            }
                        }
                    }, 10 * 1000);
                } else {
                    Toast.makeText(context, "Internet available", Toast.LENGTH_SHORT).show();


                }
            }
        });
    }

}
