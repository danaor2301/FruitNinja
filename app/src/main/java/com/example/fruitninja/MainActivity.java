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
    static TextView score;
    int count = 0;
    static int n = 5, j = -1, halfCount = -1, score2 = 0;
    static boolean start = false, stop = false, sliced = false;
    Random rnd = new Random();
    Node<Bitmap> bitmapNode, firstBitmap;
    Node<Fruit> fruit, firstFruit, fruit2;

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
        score = findViewById(R.id.score);
        ready = findViewById(R.id.ready);
        set = findViewById(R.id.set);
        go = findViewById(R.id.go);

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
                                    score.setText("score: "+score2);
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
        firstFruit = DrawSurfaceView.firstFruit;
        fruit = DrawSurfaceView.firstFruit;
        bitmapNode = DrawSurfaceView.firstBitmap;
        sliced = false;

        for (int i=0; i<firstFruit.getLength(firstFruit); i++)
        {
            if (event.getX() < (fruit.GetValue().getX()+bitmapNode.GetValue().getWidth()) &&
                    event.getX() > fruit.GetValue().getX())
            {
                if (event.getY() > fruit.GetValue().getY() &&
                        (event.getY()) < fruit.GetValue().getY()+bitmapNode.GetValue().getHeight())
                {
                    //Toast.makeText(this, (fruit.GetValue().getY()+bitmapNode.GetValue().getHeight())+", "+
                            //(event.getY())+", "+ (fruit.GetValue().getY()), Toast.LENGTH_SHORT).show();
                    stop = true;
                    DrawSurfaceView.firstFruit.getNode(i, DrawSurfaceView.firstFruit).GetValue().sliced = true;
                    halfCount++;
                    DrawSurfaceView.firstFruit.getNode(i, DrawSurfaceView.firstFruit).GetValue().score = 1;
                    score2 = DrawSurfaceView.firstFruit.whatIsTheScore(DrawSurfaceView.firstFruit);
                    //if (fruit[i].isSliced() == 1)
                    //score2++;
                    score.setText("score: " + score2);
                }
            }
            fruit = fruit.GetNext();
            bitmapNode = bitmapNode.GetNext();
        }
        return false;
    }


}



