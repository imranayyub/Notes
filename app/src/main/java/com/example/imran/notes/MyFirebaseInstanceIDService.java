package com.example.imran.notes;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by imran on 29/1/18.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    public static String recent_token;
    @Override
    public void onTokenRefresh() {
    recent_token = FirebaseInstanceId.getInstance().getToken();
    }
}
