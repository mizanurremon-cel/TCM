package com.cel.tcm.View.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.cel.tcm.R;
import com.cel.tcm.Sessions.SessionManager;
import com.cel.tcm.databinding.ActivitySplashBinding;

public class Splash_activity extends AppCompatActivity {

    SessionManager sessionManager;

    ActivitySplashBinding binding;

    private static final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        init_view();


        //Toast.makeText(this, sessionManager.getToken(), Toast.LENGTH_SHORT).show();

        if (sessionManager.getToken().equals("-1") || TextUtils.isEmpty(sessionManager.getToken())) {


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    binding.titleText.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));

                    startActivity(new Intent(getApplicationContext(), Login_activity.class));
                    finish();


                }
            }, SPLASH_DISPLAY_LENGTH);

        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    private void init_view() {

        sessionManager = new SessionManager(getApplicationContext());
    }
}