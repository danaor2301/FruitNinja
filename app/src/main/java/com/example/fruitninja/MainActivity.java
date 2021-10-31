package com.example.fruitninja;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {

    DrawSurfaceView ds;
    Thread thread;
    FrameLayout frame;
    TextView timer, ready, set, go;
    int count = 0;
    static int n = 5, j = -1, halfCount = -1;
    static boolean start = false, stop = false;
    Random rnd = new Random();
    Bitmap [] bitmaps = new Bitmap[5];
    Fruit [] fruit = new Fruit[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        n = rnd.nextInt(4);
        frame = findViewById(R.id.frame);
        ds = new DrawSurfaceView(this);
        frame.addView(ds);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        thread = new Thread(ds);
        thread.start();
        timer = findViewById(R.id.timer);
        ready = findViewById(R.id.ready);
        set = findViewById(R.id.set);
        go = findViewById(R.id.go);

        for (int i=0; i<bitmaps.length; i++)
        {
            bitmaps[i] = DrawSurfaceView.bitmaps[i];
            fruit[i] = DrawSurfaceView.fruit[i];
        }

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
                                if (count == 1) {
                                    ready.setText("READY...");
                                    ready.setVisibility(View.GONE);
                                    ready.setAlpha(0.0f);
                                    ready.animate().translationY(300).alpha(1.0f).setListener(null);
                                    ready.setVisibility(View.VISIBLE);
                                }
                                if (count == 2) {
                                    ready.setText("");
                                    set.setText("SET...");
                                    set.setVisibility(View.GONE);
                                    set.setAlpha(0.0f);
                                    set.animate().translationY(300).alpha(1.0f).setListener(null);
                                    set.setVisibility(View.VISIBLE);
                                }
                                if (count == 3)
                                {
                                    set.setText("");
                                    go.setText("GO!");
                                    go.setVisibility(View.GONE);
                                    go.setAlpha(0.0f);
                                    go.animate().translationY(300).alpha(1.0f).setListener(null);
                                    go.setVisibility(View.VISIBLE);
                                }
                                if (count >= 4)
                                {
                                    go.setText("");
                                    timer.setText("time: " + String.valueOf(count-3) + " s'");
                                    start = true;
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

    public boolean onTouchEvent(MotionEvent event)
    {
        for (int i=0; i<bitmaps.length; i++)
        {
            if (event.getX() <= (fruit[i].getX()+bitmaps[i].getWidth()) && event.getX() >= fruit[i].getX())
            {
                j = i;
                stop = true;
                fruit[i].sliced = true;
                halfCount++;
            }
        }
        return false;
    }

}



