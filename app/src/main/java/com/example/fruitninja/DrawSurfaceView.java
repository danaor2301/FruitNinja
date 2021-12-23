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
    //String [] fruitString = new String[bitmapsLength];
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
    static Node<Fruit> fruit, firstFruit, fruit2, firstFruit2;
    Node<String> fruitString, firstFruitString;
    boolean lose = false;

    public void start()
    {
        Random random=new Random();
        startX= random.nextInt((1500 - 300) + 1) + 300;
        length = random.nextInt(1200);
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

    public void start2(float x2, float y2)
    {
        Random random=new Random();
        startX = x2;
        length = random.nextInt(1200);
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
        midX=x2;
        maxY= y2;
        q=-startX-endX;
        p=startX*endX;
        a=(900-maxY)/((midX*midX)+(q*midX)+(p));
        falling=true;
    }

    public void createNewBitmaps()
    {
        fruit = fruit.getLast(fruit);
        bitmapNode = bitmapNode.getLast(bitmapNode);
        fruitString = fruitString.getLast(fruitString);
        fruit2 = fruit2.getLast(fruit2);

        start();
        n = rnd.nextInt(4);
        bitmapNode.SetNext(new Node(returnBitmap(n/*, i*/)));
        bitmapNode = bitmapNode.GetNext();
        fruit.SetNext(new Node(new Fruit(startX, endX, x, y, midX, maxY, a, p, q, length, leftToRight)));
        fruit = fruit.GetNext();
        fruitString.SetNext(new Node(returnFruit(n)));
        fruitString = fruitString.GetNext();
        i++;
        fruit2.SetNext(new Node(new Fruit(startX, endX, x, y, midX, maxY, a, p, q, length, leftToRight)));
        fruit2 = fruit2.GetNext();

        //start2(fruit.GetValue().getX(), fruit.GetValue().getY());
        start();
        fruit2.SetNext(new Node(new Fruit(fruit.GetValue().getX(), endX, x, y,
                fruit.GetValue().getX(), fruit.GetValue().getY(), a, p, q, length, leftToRight)));
        fruit2 = fruit2.GetNext();
    }

    public DrawSurfaceView(Context context)
    {
        super(context);
        this.context = context;
        holder = getHolder();

        timer();

        start();
        n = rnd.nextInt(4);
        bitmapNode = new Node(returnBitmap(n));
        firstBitmap = bitmapNode;
        fruit = new Node(new Fruit(startX, endX, x, y, midX, maxY, a, p, q, length, leftToRight));
        firstFruit = fruit;
        fruitString = new Node(returnFruit(n));
        firstFruitString = fruitString;
        fruit2 = new Node(new Fruit(startX, endX, x, y, midX, maxY, a, p, q, length, leftToRight));
        firstFruit2 = fruit2;

        start();
        fruit2 = new Node(new Node(new Fruit(fruit.GetValue().getX(), endX, x, y,
                fruit.GetValue().getX(), fruit.GetValue().getY(), a, p, q, length, leftToRight)));
        firstFruit2 = fruit2;
    }

    public Bitmap returnBitmap(int n)
    {
        Bitmap bitmap = null;
        if (n == 0) {
            bitmap = BitmapFactory.decodeResource(getResources(), wm3);
        }
        if (n == 1) {
            bitmap = BitmapFactory.decodeResource(getResources(), orange3);
        }
        if (n == 2) {
            bitmap = BitmapFactory.decodeResource(getResources(), banana2);
        }
        if (n == 3) {
            bitmap = BitmapFactory.decodeResource(getResources(), apple3);
        }
        return bitmap;
    }

    public String returnFruit(int n)
    {
        String s = null;
        if (n == 0) {
            s = "watermelon";
        }
        if (n == 1) {
            s = "orange";
        }
        if (n == 2) {
            s = "banana";
        }
        if (n == 3) {
            s = "apple";
        }
        return s;
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

                        if (count >= 4000)
                        {
                            if (count < 14000)
                            {
                                if (count > count2+rndnum && count < count2+rndnum+20)
                                {
                                    fruit = firstFruit;
                                    bitmapNode = firstBitmap;
                                    fruitString = firstFruitString;
                                    fruit2 = firstFruit2;
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
                                    fruitString = firstFruitString;
                                    fruit2 = firstFruit2;
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
                            fruitString = firstFruitString;
                            fruit2 = firstFruit2;
                            for (int i=0; i<firstFruit.getLength(firstFruit); i++)
                            {
                                c.drawBitmap(bitmapNode.GetValue(), fruit.GetValue().getX(),
                                        fruit.GetValue().getY(),null);

                                if (fruit.GetValue().sliced == true)
                                {
                                    c.drawBitmap(returnHalfBitmap2(fruitString), fruit.GetValue().getX()+100,
                                            fruit.GetValue().getY()+100,null);
                                }

                                bitmapNode = bitmapNode.GetNext();
                                fruit = fruit.GetNext();
                                fruitString = fruitString.GetNext();
                                fruit2 = fruit2.GetNext();
                            }
                            fruit = firstFruit;
                            bitmapNode = firstBitmap;
                            fruitString = firstFruitString;
                            fruit2 = firstFruit2;
                            automaticMove(c);
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

    public void automaticMove(Canvas c)
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
                fruit.GetValue().setFalling(true);
                bitmapNode.SetValue(returnHalfBitmap1(fruitString));
                //c.drawBitmap(returnHalfBitmap2(fruitString),
                        //fruit.GetValue().getX(), fruit.GetValue().getY(), null);

                fruit.GetValue().setY(900- (fruit.GetValue().getA()*fruit.GetValue().getX()*fruit.GetValue().getX()+
                        fruit.GetValue().getA()*fruit.GetValue().getQ()*fruit.GetValue().getX() +
                        fruit.GetValue().getA()*fruit.GetValue().getP()));
                fruit.GetValue().setX(fruit.GetValue().getX() + 10);

                /*fruit2.GetValue().setY(900- (fruit2.GetValue().getA()*fruit2.GetValue().getX()*fruit2.GetValue().getX()+
                        fruit2.GetValue().getA()*fruit2.GetValue().getQ()*fruit2.GetValue().getX() +
                        fruit2.GetValue().getA()*fruit2.GetValue().getP()));
                fruit2.GetValue().setX(fruit2.GetValue().getX() + 10);*/
            }
            fruit = fruit.GetNext();
            bitmapNode = bitmapNode.GetNext();
            fruitString = fruitString.GetNext();
            fruit2 = fruit2.GetNext();
        }
    }

    public Bitmap returnHalfBitmap1(Node<String> fruitString)
    {
        if (fruitString.GetValue() == "watermelon") {
            return BitmapFactory.decodeResource(getResources(), halfwatermelon1);
        }
        if (fruitString.GetValue() == "orange") {
            return BitmapFactory.decodeResource(getResources(), halforange1);
        }
        if (fruitString.GetValue() == "banana") {
            return BitmapFactory.decodeResource(getResources(), halfbanana1);
        }
        if (fruitString.GetValue() == "apple") {
            return BitmapFactory.decodeResource(getResources(), halfapple1);
        }

       return null;
    }

    public Bitmap returnHalfBitmap2(Node<String> fruitString)
    {
        if (fruitString.GetValue() == "watermelon") {
            return BitmapFactory.decodeResource(getResources(), halfwatermelon2);
        }
        if (fruitString.GetValue() == "orange") {
            return BitmapFactory.decodeResource(getResources(), halforange2);
        }
        if (fruitString.GetValue() == "banana") {
            return BitmapFactory.decodeResource(getResources(), halfbanana2);
        }
        if (fruitString.GetValue() == "apple") {
            return BitmapFactory.decodeResource(getResources(), halfapple2);
        }

        return null;
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
