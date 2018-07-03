package com.example.apkresource;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import dalvik.system.DexFile;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_WRITE_STORAGE = 0x100;
    private Button invokeBugActivity;
    private Button fixBugActivity;
    private Button parseDex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        invokeBugActivity = findViewById(R.id.invokeErrorActivity);
        fixBugActivity = findViewById(R.id.fixBugActivity);
        parseDex = findViewById(R.id.loadDex);
        invokeBugActivity.setOnClickListener(this);
        fixBugActivity.setOnClickListener(this);
        parseDex.setOnClickListener(this);
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
        } else if (v == parseDex) {
            String dexPath = Environment.getExternalStorageDirectory() + File.separator + "final.dex";
            String dexOptPath = getCacheDir().getAbsolutePath() + File.separator + "DEX";

            File patch = new File(dexPath);
            try {
                DexFile dexFile = DexFile.loadDex(dexPath, dexOptPath, 0);
                Enumeration<String> enumeration = dexFile.entries();
                while (enumeration.hasMoreElements()) {
                    Log.d("TEST", enumeration.nextElement());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void injectPatch() {
        try {
            PatchInjector.injectPatch(getApplicationContext());
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE && grantResults.length == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "授权成功", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
