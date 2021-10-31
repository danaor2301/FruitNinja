package com.example.fruitninja;

        import android.annotation.SuppressLint;
        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.PorterDuff;
        import android.graphics.drawable.Drawable;
        import android.os.Handler;
        import android.view.MotionEvent;
        import android.view.SurfaceHolder;
        import android.view.SurfaceView;
        import android.widget.Toast;

        import java.util.Random;
        import java.util.logging.LogRecord;

        import static com.example.fruitninja.R.drawable.*;

public class DrawSurfaceView extends SurfaceView implements Runnable {
    int interval = 1;
    Context context;
    SurfaceHolder holder;
    boolean threadRunning = true;
    boolean isRunning = true;
    static Bitmap [] bitmaps = new Bitmap[5];
    String [] fruitString = new String[5];
    //Bitmap [] halfBitmap1 = new Bitmap[5];
    Bitmap [] halfBitmapArray = new Bitmap[5];
    Bitmap halfBitmap, halfBitmap2;
    int s = 0;
    static Fruit [] fruit = new Fruit[5];
    Random rnd = new Random();
    int count = 0;
    float startX, x, y, maxY, endX, midX, a, p, q, length;
    boolean leftToRight, falling;
    int i = 0, c2 = 4;
    int n = rnd.nextInt(4);

    public void start()
    {
        Random random=new Random();
        startX= random.nextInt((1500 - 500) + 1) + 500;
        length = random.nextInt(1000);
        endX = startX + length;
        switch (random.nextInt(2)){
            case 0:
                leftToRight=true;
                x=startX;
                break;
            case 1:
                leftToRight=false;
                x=endX;
                break;
        }
        y=900;
        midX=(startX+endX)/2;
        maxY= rnd.nextInt((300 - 100) + 1) + 100;
        q=-startX-endX;
        p=startX*endX;
        a=(900-maxY)/((midX*midX)+(q*midX)+(p));
        falling=false;
    }

    public DrawSurfaceView(Context context)
    {
        super(context);
        this.context = context;
        holder = getHolder();

        for (int i=0; i<bitmaps.length; i++)
        {
            start();
            n = rnd.nextInt(4);
            bitmaps[i] = returnBitmap(n, s);
            fruit[i] = new Fruit(startX, endX, x, y, midX, maxY, a, p, q, length, leftToRight);
            s++;
        }

        timer();
    }

    public Bitmap returnBitmap(int n, int s)
    {
        Bitmap bitmap = null;
        if (n == 0) {
            fruitString[s] = "watermelon";
            bitmap = BitmapFactory.decodeResource(getResources(), wm3);
        }
        if (n == 1) {
            fruitString[s] = "orange";
            bitmap = BitmapFactory.decodeResource(getResources(), orange3);
        }
        if (n == 2) {
            fruitString[s] = "banana";
            bitmap = BitmapFactory.decodeResource(getResources(), banana2);
        }
        if (n == 3) {
            fruitString[s] = "apple";
            bitmap = BitmapFactory.decodeResource(getResources(), apple3);
        }
        return bitmap;
    }


    @Override
    public void run() {
        while (threadRunning)
        {
            if(isRunning)
            {
                if(!holder.getSurface().isValid())
                    continue;

                Canvas c = null;
                try
                {
                    c = this.getHolder().lockCanvas();
                    synchronized (this.getHolder()){
                        c.drawRGB(2,33,47);
                        for (int i=0; i<bitmaps.length; i++)
                        {
                            c.drawBitmap(bitmaps[i],fruit[i].getX(),fruit[i].getY(),null);
                        }

                        if (count >= 4)
                        {
                            for (int i=0; i<bitmaps.length; i++)
                            {
                                automaticMove(i, c);
                            }
                        }
                    }
                    Thread.sleep(interval);
                }
                catch (Exception e)
                {

                }
                finally {
                    if(c!=null)
                    {
                        this.getHolder().unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }

    public void timer()
    {
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
                            @Override
                            public void run()
                            {
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

            private void runOnUiThread(Runnable runnable) {
            }
        };
        t.start();
    }


    public void automaticMove(int i, Canvas c){

        if (fruit[i].sliced == false)
        {
            if(fruit[i].getX()==fruit[i].getMidX()){
                fruit[i].setFalling(true);
            }
            fruit[i].setY(900- (fruit[i].getA()*fruit[i].getX()*fruit[i].getX()+
                    fruit[i].getA()*fruit[i].getQ()*fruit[i].getX() + fruit[i].getA()*fruit[i].getP()));

            if(fruit[i].isLeftToRight()){
                fruit[i].setX(fruit[i].getX() + 3);
            }
            else {
                fruit[i].setX(fruit[i].getX() - 3);
            }
        }
        else
        {
            bitmaps[MainActivity.j] = returnHalfBitmap1(MainActivity.j);
            //halfBitmapArray[MainActivity.halfCount] = returnHalfBitmap2(MainActivity.j);
            //c.drawBitmap(halfBitmapArray[MainActivity.halfCount], fruit[MainActivity.j].getX()+100, fruit[MainActivity.j].getY()-100, null);
            fruit[i].setY(fruit[i].getY()+10);
        }
    }

    public static void stop(int i)
    {
        fruit[i].setX(fruit[i].getX());
        fruit[i].setY(fruit[i].getY());
    }

    public Bitmap returnHalfBitmap1(int s)
    {
        if (fruitString[s] == "watermelon") {
            halfBitmap = BitmapFactory.decodeResource(getResources(), halfwatermelon1);
        }
        if (fruitString[s] == "orange") {
            halfBitmap = BitmapFactory.decodeResource(getResources(), halforange1);
        }
        if (fruitString[s] == "banana") {
            halfBitmap = BitmapFactory.decodeResource(getResources(), halfbanana1);
        }
        if (fruitString[s] == "apple") {
            halfBitmap = BitmapFactory.decodeResource(getResources(), halfapple1);
        }

        return halfBitmap;
    }

    public Bitmap returnHalfBitmap2(int s)
    {
        if (fruitString[s] == "watermelon")
            halfBitmap2 = BitmapFactory.decodeResource(getResources(), halfwatermelon2);
        if (fruitString[s] == "orange")
            halfBitmap2 = BitmapFactory.decodeResource(getResources(), halforange2);
        if (fruitString[s] == "banana")
            halfBitmap2 = BitmapFactory.decodeResource(getResources(), halfbanana2);
        if (fruitString[s] == "apple")
            halfBitmap2 = BitmapFactory.decodeResource(getResources(), halfapple2);

        return halfBitmap2;
    }


}
