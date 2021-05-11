package com.example.smart_mirror.LOGIN;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smart_mirror.R;

public class SplashActivity extends AppCompatActivity {

    Animation FadeIn_Anim;

    TextView namigation, explain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        namigation  = (TextView) findViewById(R.id.namigation);
        explain     = (TextView) findViewById(R.id.explain);

        FadeIn_Anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.linear_fadein);

        namigation  .setAnimation(FadeIn_Anim);
        explain     .setAnimation(FadeIn_Anim);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();

            }
        },3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
