package com.example.supengchao.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import Utils.ActivityController;
import scheme.base.SchemeConstants;
import scheme.base.SchemeDispatcher;
import scheme.base.SpcSchemeDeal;

/**
 * Created by supengchao on 2015/10/22.
 * 该Aty是所有涉及到外部打开app的中转类，因为只有该Aty
 */
public class WebToAty extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            Uri uri = getIntent().getData();
                if(SchemeConstants.SPC_TEST_SCHEME_NAME.equals(uri.getScheme())){
                    if(SchemeConstants.SPC_TEST_HOST_NAME.equalsIgnoreCase(uri.getHost())){
                        if(ActivityController.getInstance().contains(MainActivity.class)){
                            SchemeDispatcher dispatcher = new SchemeDispatcher(this);
                            dispatcher.dispatchRequest(uri);
                        }else{
                            Intent intent = new Intent(WebToAty.this, MainActivity.class);
                            intent.putExtra(SchemeConstants.ORIGINAL_URI, getIntent().getDataString());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                    }
                }
        }
//        finish();
    }
}
