package com.example.apkresource;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button invokeBugActivity;
    private Button fixBugActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        invokeBugActivity = findViewById(R.id.invokeErrorActivity);
        fixBugActivity = findViewById(R.id.fixBugActivity);
        invokeBugActivity.setOnClickListener(this);
        fixBugActivity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == invokeBugActivity) {
            try {
                Intent intent = new Intent(this, BugActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else if (v == fixBugActivity) {
            injectPatch();
        }
    }

    private void injectPatch() {

    }
}
