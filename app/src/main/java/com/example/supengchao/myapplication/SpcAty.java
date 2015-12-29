package com.example.supengchao.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by supengchao on 2015/10/22.
 */
public class SpcAty extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spc);

        Intent intent = getIntent();
        String  id = intent.getStringExtra("id");

        Toast.makeText(SpcAty.this,id,Toast.LENGTH_SHORT).show();
    }


}
