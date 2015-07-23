package com.iiitd.esya.app;

import android.content.Intent;
import android.util.Log;

/**
 * Created by darkryder on 23/7/15.
 */
public class InstanceIDListenerService extends com.google.android.gms.iid.InstanceIDListenerService
{
    private static String TAG = InstanceIDListenerService.class.getSimpleName();
    @Override
    public void onTokenRefresh() {
        Log.d(TAG, "In onTokenRefresh");
        startService(new Intent(this, RegistrationIntentService.class));
    }
}
