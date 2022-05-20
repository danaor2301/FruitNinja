package com.example.fruitninja;

        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Matrix;
        import android.media.MediaPlayer;
        import android.view.SurfaceHolder;
        import android.view.SurfaceView;
        import android.widget.Toast;

        import java.util.ArrayList;
        import java.util.Random;

        import static com.example.fruitninja.R.drawable.*;

public class DrawSurfaceView extends SurfaceView implements Runnable {
    int interval = 1, n, time = 1500, count = 0, count2 = 4000;
    float startX, x, y, maxY, endX, midX, a, p, q, length;
    static ArrayList<Bitmap> bitmapArr;
    static ArrayList<Fruit> fruitArr, fruitArr2;
    static ArrayList<String> fruitArrString;
    boolean threadRunning = true, isRunning = true, leftToRight, falling;
    static boolean gameOver = false;
    Context context;
    SurfaceHolder holder;
    Random rnd = new Random();
    Canvas c;

    public void start()
    {
        Random random=new Random();
        startX= random.nextInt((1500 - 300) + 1) + 300;
        length = random.nextInt((1200 - 100) + 1) + 100;
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
        start();
        n = rnd.nextInt(5);
        bitmapArr.add(returnBitmap(n));
        fruitArr.add(new Fruit(startX, endX, x, 900, midX, maxY, a, p, q, length, leftToRight));
        fruitArr2.add(new Fruit(startX, endX, x, maxY, midX, maxY, a, p, q, length, leftToRight));
        fruitArrString.add(returnFruit(n));
    }

    public DrawSurfaceView(Context context)
    {
        super(context);
        this.context = context;
        holder = getHolder();

        bitmapArr = new ArrayList<>();
        fruitArr = new ArrayList<>();
        fruitArr2 = new ArrayList<>();
        fruitArrString = new ArrayList<>();

        count = 0;
        count2 = 4000;
        time = 1500;
        gameOver = false;

        timer();
        start();
        n = rnd.nextInt(5);

        bitmapArr.add(returnBitmap(n));
        fruitArr.add(new Fruit(startX, endX, x, 900, midX, maxY, a, p, q, length, leftToRight));
        fruitArr2.add(new Fruit(startX, endX, x, maxY, midX, 900, a, p, q, length, leftToRight));
        fruitArrString.add(returnFruit(n));

    }

    public Bitmap returnBitmap(int n)
    {
        Bitmap bitmap = null;
        if (n == 0) {
            bitmap = BitmapFactory.decodeResource(getResources(), watermelon_full);
        }
        if (n == 1) {
            bitmap = BitmapFactory.decodeResource(getResources(), orange_full);
        }
        if (n == 2) {
            bitmap = BitmapFactory.decodeResource(getResources(), banana_full);
        }
        if (n == 3) {
            bitmap = BitmapFactory.decodeResource(getResources(), apple_full);
        }
        if (n == 4) {
            bitmap = BitmapFactory.decodeResource(getResources(), bomb);
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
        if (n == 4) {
            s = "bomb";
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

                c = null;
                try
                {
                    c = this.getHolder().lockCanvas();
                    synchronized (this.getHolder()){
                        c.drawRGB( 255,191,105);

                        if (count >= 4000 && MainActivity.stop == false)
                        {
                            if (count > count2+time && count < count2+time+30)
                            {
                                createNewBitmaps();
                            }
                            if (count > count2+time+30 && count < count2+time+60)
                            {
                                count2 = count2 + time + 60;
                            }

                            deleteFromArray(fruitArr, fruitArr2, bitmapArr, fruitArrString);

                            for (int i=0; i<fruitArr.size(); i++)
                            {
                                if (fruitArr.get(i).getY() < 1000)
                                {
                                    c.drawBitmap(bitmapArr.get(i), fruitArr.get(i).getX(),
                                            fruitArr.get(i).getY(),null);
                                    if (fruitArr.get(i).sliced == true)
                                    {
                                        c.drawBitmap(returnHalfBitmap2(fruitArrString, i), fruitArr.get(i).getX()+100,
                                                fruitArr.get(i).getY()+100,null);
                                    }
                                }

                            }

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



    public void automaticMove(){
        {
            for (int i=0; i<fruitArr.size(); i++){
                if (MainActivity.numOfLosses(fruitArr, fruitArrString) >= 3)
                {
                    gameOver = true;
                }
                if (gameOver == true){
                    fruitArr.get(i).setY(fruitArr.get(i).getY()+20);
                } else {
                    if (!fruitArr.get(i).sliced){
                        if(fruitArr.get(i).getX()==fruitArr.get(i).getMidX()){
                            fruitArr.get(i).setFalling(true);
                        }
                        fruitArr.get(i).setY(900- (fruitArr.get(i).getA()*fruitArr.get(i).getX()*fruitArr.get(i).getX()+
                                fruitArr.get(i).getA()*fruitArr.get(i).getQ()*fruitArr.get(i).getX() +
                                fruitArr.get(i).getA()*fruitArr.get(i).getP()));

                        if (fruitArrString.get(i) != "bomb"){
                            if (fruitArr.get(i).getY() > 910){
                                OpeningActivity.mp_lose.start();
                            }
                        }

                        if (fruitArr.get(i).getY() <= 890 && fruitArr.get(i).getY() >= 850){
                            if (fruitArrString.get(i) == "bomb"){
                                OpeningActivity.mp_wooshBomb.start();
                            }
                            else {
                                OpeningActivity.mp_woosh.start();
                            }
                        }

                        if(fruitArr.get(i).isLeftToRight()){
                            fruitArr.get(i).setX(fruitArr.get(i).getX() + 3);
                        }
                        else {
                            fruitArr.get(i).setX(fruitArr.get(i).getX() - 3);
                        }
                    }
                    else
                    {
                        fruitArr.get(i).setFalling(true);
                        fruitArr2.get(i).setFalling(true);
                        if (fruitArrString.get(i) == "bomb")
                        {
                            OpeningActivity.mp_bomb.start();
                            gameOver = true;
                        }
                        else
                        {
                            OpeningActivity.mp_slice.start();
                            bitmapArr.set(i, returnHalfBitmap1(fruitArrString, i));
                            fruitArr.get(i).setY(900- (fruitArr.get(i).getA()*fruitArr.get(i).getX()*
                                    fruitArr.get(i).getX()+ fruitArr.get(i).getA()*fruitArr.get(i).getQ()*
                                    fruitArr.get(i).getX() + fruitArr.get(i).getA()*fruitArr.get(i).getP()));
                            fruitArr.get(i).setX(fruitArr.get(i).getX() + 10);
                            fruitArr2.get(i).setY(fruitArr2.get(i).getY() + 20);
                        }
                    }
                }
            }
        }
    }

    public void deleteFromArray(ArrayList<Fruit>fruitList1, ArrayList<Fruit>fruitList2,
                                     ArrayList<Bitmap>bitmapList, ArrayList<String>stringList){
        for (int i=0; i<fruitList1.size(); i++)
        {
            if (fruitList1.get(i).getY() > 1000){
                fruitList1.remove(i);
                fruitList2.remove(i);
                bitmapList.remove(i);
                stringList.remove(i);
            }
        }
    }

    public Bitmap returnHalfBitmap1(ArrayList<String> fruitArrString, int i)
    {
        if (fruitArrString.get(i) == "watermelon") {
            return BitmapFactory.decodeResource(getResources(), watermelon_half1);
        }
        if (fruitArrString.get(i) == "orange") {
            return BitmapFactory.decodeResource(getResources(), orange_half1);
        }
        if (fruitArrString.get(i) == "banana") {
            return BitmapFactory.decodeResource(getResources(), banana_half1);
        }
        if (fruitArrString.get(i) == "apple") {
            return BitmapFactory.decodeResource(getResources(), apple_half1);
        }
        if (fruitArrString.get(i) == "bomb") {
            return BitmapFactory.decodeResource(getResources(), explotion);
        }

        return null;
    }

    public Bitmap returnHalfBitmap2(ArrayList<String> fruitArrString, int i)
    {
        if (fruitArrString.get(i) == "watermelon") {
            return BitmapFactory.decodeResource(getResources(), watermelon_half2);
        }
        if (fruitArrString.get(i) == "orange") {
            return BitmapFactory.decodeResource(getResources(), orange_half2);
        }
        if (fruitArrString.get(i) == "banana") {
            return BitmapFactory.decodeResource(getResources(), banana_half2);
        }
        if (fruitArrString.get(i) == "apple") {
            return BitmapFactory.decodeResource(getResources(), apple_half2);
        }
        if (fruitArrString.get(i) == "bomb") {
            return BitmapFactory.decodeResource(getResources(), explotion);
        }

        return null;
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
                        if (MainActivity.stop == false){
                            Thread.sleep(1);
                        } else {
                            Thread.sleep(0);
                        }

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
                    if (MainActivity.stop == false)
                        count++;
                }
            }

            private void runOnUiThread(Runnable runnable) {
            }
        };
        t.start();
    }

}
