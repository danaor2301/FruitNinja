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
        import android.widget.TextView;
        import android.widget.Toast;

        import java.util.Iterator;
        import java.util.Random;
        import java.util.logging.LogRecord;

        import static com.example.fruitninja.R.drawable.*;

public class DrawSurfaceView extends SurfaceView implements Runnable {
    int interval = 1;
    Context context;
    SurfaceHolder holder;
    boolean threadRunning = true;
    boolean isRunning = true;
    static int bitmapsLength = 5, score = 0;
    String [] fruitString = new String[bitmapsLength];
    Bitmap [] halfBitmapArray = new Bitmap[bitmapsLength];
    Bitmap halfBitmap, halfBitmap2;
    Random rnd = new Random();
    int count = 0, count2 = 4000;
    float startX, x, y, maxY, endX, midX, a, p, q, length;
    boolean leftToRight, falling;
    int i = 0;
    int n, rndnum = rnd.nextInt((2300-1000)+1)+1000, time = 0;
    TextView TvScore = MainActivity.score;
    static Node<Bitmap> bitmapNode, firstBitmap;
    static Node<Fruit> fruit, firstFruit, fruit2;
    boolean lose = false;

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

    public void createNewBitmaps()
    {
        fruit = fruit.getLast(fruit);
        bitmapNode = bitmapNode.getLast(bitmapNode);

        start();
        n = rnd.nextInt(4);
        bitmapNode.SetNext(new Node(returnBitmap(n/*, i*/)));
        bitmapNode = bitmapNode.GetNext();
        fruit.SetNext(new Node(new Fruit(startX, endX, x, y, midX, maxY, a, p, q, length, leftToRight)));
        fruit = fruit.GetNext();
        i++;
        //bitmapNode = firstBitmap;
        //fruit = firstFruit;
    }

    public DrawSurfaceView(Context context)
    {
        super(context);
        this.context = context;
        holder = getHolder();

        timer();

        start();
        n = rnd.nextInt(4);
        bitmapNode = new Node(returnBitmap(n/*, 0*/));
        firstBitmap = bitmapNode;
        fruit = new Node(new Fruit(startX, endX, x, y, midX, maxY, a, p, q, length, leftToRight));
        firstFruit = fruit;
        /*
        if (rndnum > 1)
        {
            for (int i=1; i<rndnum; i++)
            {
                start();
                n = rnd.nextInt(4);
                bitmapNode.SetNext(new Node(returnBitmap(n, i)));
                bitmapNode = bitmapNode.GetNext();
                fruit.SetNext(new Node(new Fruit(startX, endX, x, y, midX, maxY, a, p, q, length, leftToRight)));
                fruit = fruit.GetNext();
            }
        }*/
    }

    public Bitmap returnBitmap(int n)
    {
        Bitmap bitmap = null;
        if (n == 0) {
            //fruitString[s] = "watermelon";
            bitmap = BitmapFactory.decodeResource(getResources(), wm3);
        }
        if (n == 1) {
            //fruitString[s] = "orange";
            bitmap = BitmapFactory.decodeResource(getResources(), orange3);
        }
        if (n == 2) {
            //fruitString[s] = "banana";
            bitmap = BitmapFactory.decodeResource(getResources(), banana2);
        }
        if (n == 3) {
            //fruitString[s] = "apple";
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
                        c.drawRGB(250,208,147);

                        /*fruit = firstFruit;
                        bitmapNode = firstBitmap;
                        for (int i=0; i<firstFruit.getLength(firstFruit); i++)
                        {
                            c.drawBitmap(bitmapNode.GetValue(), fruit.GetValue().getX(),
                                    fruit.GetValue().getY(),null);
                            bitmapNode = bitmapNode.GetNext();
                            fruit = fruit.GetNext();
                        }*/
                        /*rndnum = rnd.nextInt((3000-1000)+1)+1000;
                        if (count == (4000 + rndnum))
                        {
                            createNewBitmaps();
                        }*/

                        if (count >= 4000)
                        {
                            if (count < 14000)
                            {
                                if (count > count2+rndnum && count < count2+rndnum+20)
                                {
                                    fruit = firstFruit;
                                    bitmapNode = firstBitmap;
                                    createNewBitmaps();
                                }
                                if (count > count2+rndnum+20 && count < count2+rndnum+40)
                                {
                                    count2 = count2 + rndnum + 40;
                                    rndnum = rnd.nextInt((1800-800)+1)+800;
                                }
                            }
                            if (count >= 14000 && count < 24000)
                            {
                                if (count > count2+rndnum && count < count2+rndnum+20)
                                {
                                    fruit = firstFruit;
                                    bitmapNode = firstBitmap;
                                    createNewBitmaps();
                                }
                                if (count > count2+rndnum+20 && count < count2+rndnum+40)
                                {
                                    count2 = count2 + rndnum + 40;
                                    rndnum = rnd.nextInt((1500-700)+1)+700;
                                }
                            }
                            fruit = firstFruit;
                            bitmapNode = firstBitmap;
                            for (int i=0; i<firstFruit.getLength(firstFruit); i++)
                            {
                                c.drawBitmap(bitmapNode.GetValue(), fruit.GetValue().getX(),
                                        fruit.GetValue().getY(),null);
                                bitmapNode = bitmapNode.GetNext();
                                fruit = fruit.GetNext();
                            }
                            fruit = firstFruit;
                            automaticMove();
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
                        Thread.sleep(1);
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

    public void automaticMove()
    {
        for (int h=0; h<firstFruit.getLength(firstFruit); h++)
        {
            if (fruit.GetValue().sliced == false)
            {
                if(fruit.GetValue().getX()==fruit.GetValue().getMidX()){
                    fruit.GetValue().setFalling(true);
                }
                fruit.GetValue().setY(900- (fruit.GetValue().getA()*fruit.GetValue().getX()*fruit.GetValue().getX()+
                        fruit.GetValue().getA()*fruit.GetValue().getQ()*fruit.GetValue().getX() +
                        fruit.GetValue().getA()*fruit.GetValue().getP()));

                if(fruit.GetValue().isLeftToRight()){
                    fruit.GetValue().setX(fruit.GetValue().getX() + 3);
                }
                else {
                    fruit.GetValue().setX(fruit.GetValue().getX() - 3);
                }
            }
            else
            {
                //bitmapNode.SetValue(returnHalfBitmap1(MainActivity.j));
                //halfBitmapArray[MainActivity.halfCount] = returnHalfBitmap2(MainActivity.j);
                //c.drawBitmap(halfBitmapArray[MainActivity.halfCount], fruit[MainActivity.j].getX()+100, fruit[MainActivity.j].getY()-100, null);
                //fruit.GetValue().setY(fruit.GetValue().getY()+10);
            }
            fruit = fruit.GetNext();
        }
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

    public void checkScore()
    {
        Node<Fruit> f = new Node(fruit.getNode(i, fruit));
        for (int i=0; i<fruit.getLength(fruit); i++)
        {
            if (f.GetValue().isSliced() == 1)
                score++;
            MainActivity.score.setText("score: " + score);
        }
    }


}
