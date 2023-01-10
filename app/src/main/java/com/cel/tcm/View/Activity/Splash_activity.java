package com.cel.tcm.View.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.cel.tcm.R;
import com.cel.tcm.Sessions.SessionManager;
import com.cel.tcm.databinding.ActivitySplashBinding;

public class Splash_activity extends AppCompatActivity {

    SessionManager sessionManager;

    ActivitySplashBinding binding;

    private static final int SPLASH_DISPLAY_LENGTH = 2000;

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 41;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init_view();


        //Toast.makeText(this, sessionManager.getToken(), Toast.LENGTH_SHORT).show();

        if (sessionManager.getUserID().equals("-1") || TextUtils.isEmpty(sessionManager.getUserID())) {


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    binding.titleText.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));

                    startActivity(new Intent(getApplicationContext(), Login_activity.class));
                    finish();


                }
            }, SPLASH_DISPLAY_LENGTH);

        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();


                }
            }, SPLASH_DISPLAY_LENGTH);

        }

        if (!checkStoragePermission()) {
            try {
                requestPermissionForReadExtertalStorage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void init_view() {

        sessionManager = new SessionManager(getApplicationContext());
    }


    public boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() throws Exception {
        try {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}