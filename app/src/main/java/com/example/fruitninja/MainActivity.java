package com.example.fruitninja;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    DrawSurfaceView ds;
    Thread thread;
    FrameLayout frame;
    TextView ready, set, go, gameOverTv;
    ImageView watermelonIv, cross1, cross2, cross3;
    ImageButton btnPause;
    static TextView score;
    static int count = 0, hitCount = 0, loseCount = 0;
    static int n = 5, halfCount = -1, score2 = 0, losses2 = 0, finalScore = 0;
    static boolean start = false, stop = false, sliced = false, gameOver = false;
    Dialog dialogPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        frame = findViewById(R.id.frame);
        ds = new DrawSurfaceView(this);
        frame.addView(ds);

        OpeningActivity.themeSound.pause();
        if (OpeningActivity.soundCheck == true){
            OpeningActivity.mp_startGame.start();
        }

        score = findViewById(R.id.score);
        ready = findViewById(R.id.ready);
        set = findViewById(R.id.set);
        go = findViewById(R.id.go);
        gameOverTv = findViewById(R.id.gameOverTv);
        watermelonIv = findViewById(R.id.watermelonIv);
        cross1 = findViewById(R.id.cross1);
        cross2 = findViewById(R.id.cross2);
        cross3 = findViewById(R.id.cross3);
        btnPause = findViewById(R.id.btnPause);

        loseCount = 0; hitCount = 0; score2 = 0; count = 0; stop = false; gameOver = false;
        thread = new Thread(ds);
        thread.start();

        dialogPause = new Dialog(MainActivity.this);
        createDialog(dialogPause);
        btnPause.setOnClickListener(this);

        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                while (!isInterrupted())
                {
                    try
                    {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable()
                        {
                            @RequiresApi(api = Build.VERSION_CODES.P)
                            @Override
                            public void run()
                            {
                                if (count > 1 && count <= 2) {
                                    ready.setText("READY...");
                                    ready.setVisibility(View.GONE);
                                    ready.setAlpha(0.0f);
                                    ready.animate().translationY(300).alpha(1.0f).setListener(null);
                                    ready.setVisibility(View.VISIBLE);
                                }
                                if (count > 2 && count <= 3) {
                                    ready.setText("");
                                    set.setText("SET...");
                                    set.setVisibility(View.GONE);
                                    set.setAlpha(0.0f);
                                    set.animate().translationY(300).alpha(1.0f).setListener(null);
                                    set.setVisibility(View.VISIBLE);
                                }
                                if (count > 3 && count <= 4)
                                {
                                    ready.setText("");
                                    set.setText("");
                                    go.setText("GO!");
                                    go.setVisibility(View.GONE);
                                    go.setAlpha(0.0f);
                                    go.animate().translationY(300).alpha(1.0f).setListener(null);
                                    go.setVisibility(View.VISIBLE);
                                }
                                if (count >= 5)
                                {
                                    ready.setText("");
                                    set.setText("");
                                    go.setText("");
                                    start = true;
                                    score.setText(""+score2);
                                    losses2 = numOfLosses(DrawSurfaceView.fruitArr, DrawSurfaceView.fruitArrString);
                                    if (losses2==1){
                                        cross1.setImageResource(R.drawable.red_cross);
                                    }
                                    if (losses2==2){
                                        cross1.setImageResource(R.drawable.red_cross);
                                        cross2.setImageResource(R.drawable.red_cross);
                                    }
                                    if (losses2==3){
                                        cross1.setImageResource(R.drawable.red_cross);
                                        cross2.setImageResource(R.drawable.red_cross);
                                        cross3.setImageResource(R.drawable.red_cross);
                                    }
                                    if (DrawSurfaceView.gameOver == true)
                                    {
                                        if (gameOver == false)
                                        {
                                            cross1.setImageResource(R.drawable.red_cross);
                                            cross2.setImageResource(R.drawable.red_cross);
                                            cross3.setImageResource(R.drawable.red_cross);
                                            Intent intent = new Intent(MainActivity.this, gameOverActivity.class);
                                            finalScore = score2;
                                            intent.putExtra("finalScore", finalScore);

                                            if (OpeningActivity.soundCheck == true)
                                                OpeningActivity.mp_gameOver.start();
                                            for (int i=0; i<OpeningActivity.mpList.size(); i++){
                                                OpeningActivity.mpList.get(i).pause();
                                            }
                                            gameOverTv.setText("GAME OVER!");
                                            gameOverTv.setVisibility(View.GONE);
                                            gameOverTv.setAlpha(0.0f);
                                            gameOverTv.animate().translationY(300).alpha(1.0f).setListener(null);
                                            gameOverTv.setVisibility(View.VISIBLE);
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }, 2000);
                                        }
                                        gameOver = true;
                                    }
                                }
                            }
                        });
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    count++;
                }
            }
        };
        t.start();
    }

    private void createDialog(Dialog dialogPause)
    {
        dialogPause.setContentView(R.layout.dialog_pause);
        dialogPause.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_pause_background));
        dialogPause.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogPause.setCancelable(false);
        ImageButton btnCancel = dialogPause.findViewById(R.id.btn_cancel);
        ImageButton btnRestart = dialogPause.findViewById(R.id.btn_restart);
        ImageButton btnContinue = dialogPause.findViewById(R.id.btn_continue);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0; i<OpeningActivity.mpList.size(); i++){
                    OpeningActivity.mpList.get(i).pause();
                }
                if (OpeningActivity.musicCheck == true)
                    OpeningActivity.themeSound.start();
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                startActivity(new Intent(MainActivity.this, gameActivity.class));
                dialogPause.dismiss();
            }
        });
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (OpeningActivity.soundCheck == true)
                    OpeningActivity.mp_click.start();
                Intent intent = getIntent();
                finish();
                count = 0;
                startActivity(intent);
                dialogPause.dismiss();
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop = false;
                if (OpeningActivity.soundCheck == true){
                    OpeningActivity.mp_unPause.start();
                }
                dialogPause.dismiss();
            }
        });
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        sliced = false;

        for (int i=0; i<DrawSurfaceView.fruitArr.size(); i++)
        {
            if (event.getX() < (DrawSurfaceView.fruitArr.get(i).getX()+DrawSurfaceView.bitmapArr.get(i).getWidth()) &&
                    event.getX() > DrawSurfaceView.fruitArr.get(i).getX())
            {
                if (event.getY() > DrawSurfaceView.fruitArr.get(i).getY()+DrawSurfaceView.bitmapArr.get(i).getHeight() &&
                        (event.getY()) < DrawSurfaceView.fruitArr.get(i).getY()+(DrawSurfaceView.bitmapArr.get(i).getHeight()*2))
                {
                    DrawSurfaceView.fruitArr.get(i).sliced = true;
                    DrawSurfaceView.fruitArr2.get(i).sliced = true;
                    DrawSurfaceView.fruitArr.get(i).score = 1;
                    halfCount++;
                    score2 = whatIsTheScore(DrawSurfaceView.fruitArr);
                    if (DrawSurfaceView.fruitArrString.get(i) == "bomb")
                        score2--;

                    score.setText(""+score2);
                }
            }
        }
        return false;
    }

    public static int numOfLosses(ArrayList<Fruit> fruitArr, ArrayList<String> fruitArrString)
    {
        for (int i=0; i<fruitArr.size(); i++)
        {
            if (fruitArr.get(i).getY() > 950 && fruitArr.get(i).sliced == false &&
                    fruitArr.get(i).missCounter == false){
                if (!fruitArrString.get(i).equals("bomb")){
                    if (OpeningActivity.soundCheck == true){
                        OpeningActivity.mp_lose.start();
                    }
                    loseCount++;
                    fruitArr.get(i).missCounter = true;
                } else {
                    OpeningActivity.mp_wooshBomb.pause();
                }
            }
        }
        return loseCount;
    }

    public int whatIsTheScore(ArrayList<Fruit> fruitArr)
    {
        for (int i=0; i<fruitArr.size(); i++){
            if (fruitArr.get(i).sliced == true && fruitArr.get(i).hitCounter == false){
                hitCount++;
                fruitArr.get(i).hitCounter = true;
            }
        }
        return hitCount;
    }

    @Override
    public void onClick(View view) {
        if (view == btnPause){
            stop = true;
            if (OpeningActivity.soundCheck == true){
                OpeningActivity.mp_pause.start();
            }
            dialogPause.show();
        }
    }
}



