package com.example.dialog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.kotlindemo.R;

public class DialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        findViewById(R.id.btn).setOnClickListener(
                v -> {
                    Intent c = new Intent();
                    c.setClassName("com.chehejia.car.fmradio",
                            "com.chehejia.car.fmradio.MainActivity");
                    startActivity(c);
                }
        );
    }
}
