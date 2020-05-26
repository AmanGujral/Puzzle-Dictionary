package com.faicess.puzzledictionary;

import android.animation.Animator;
import android.content.Intent;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class LogoActivity extends AppCompatActivity {

    ImageView logoImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        //change statusBar color
        final Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        logoImgView = findViewById(R.id.logoImgView);

        logoImgView.setAlpha(0f);
        logoImgView.animate()
                .alpha(1f)
                .setDuration(2000)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {

                        Handler handler = new Handler();
                        Runnable runnable  = new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(LogoActivity.this, HomeScreenActivity.class);
                                startActivity(intent);
                            }
                        };
                        handler.postDelayed(runnable, 300);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
    }
}
