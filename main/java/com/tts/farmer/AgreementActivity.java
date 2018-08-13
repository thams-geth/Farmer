package com.tts.farmer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AgreementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.pay) {
            Intent intent = new Intent(AgreementActivity.this, SellActivity.class);
            startActivity(intent);
        }
    }
}
