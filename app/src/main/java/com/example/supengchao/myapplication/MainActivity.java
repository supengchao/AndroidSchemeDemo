package com.example.supengchao.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import Utils.ActivityController;
import scheme.base.SchemeConstants;
import scheme.base.SchemeDispatcher;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityController.getInstance().addAcitivty(this);
        Intent intent = getIntent();
        if(intent!=null){
            dispatchUriToBusiness(intent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent==null){
            return;
        }
        dispatchUriToBusiness(intent);
    }
    private void dispatchUriToBusiness(Intent intent){
        if (intent!=null) {
            String dataString = intent.getStringExtra(SchemeConstants.ORIGINAL_URI);
            Log.d("TAG","============="+dataString);
            if (dataString!=null && dataString.startsWith(SchemeConstants.SPC_TEST_SCHEME_NAME + "://")) {
                Uri uri = Uri.parse(dataString);
                SchemeDispatcher schemeDispatcher = new SchemeDispatcher(this);
                schemeDispatcher.dispatchRequest(uri);
                Log.d("TAG","=============");
            }
        }
    }

}
