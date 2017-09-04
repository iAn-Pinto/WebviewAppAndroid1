package com.cirtru.androidApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends Activity {
private static final String TAG = "SplashActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = getIntent().getExtras();
        Intent intent = new Intent(this, MainActivity.class);
        if(mBundle!=null) {
            Log.d(TAG, bundleToString(mBundle, this));
            intent.putExtras(mBundle);
        }
        startActivity(intent);
        finish();
    }
    private String bundleToString(final Bundle bundle, final Context mContext) {
        String string = "Bundle{";
        for (String key : bundle.keySet()) {
            string += " " + key + " => " + bundle.get(key) + ";";
        }
        string += " }Bundle";
        return string;
    }
}
