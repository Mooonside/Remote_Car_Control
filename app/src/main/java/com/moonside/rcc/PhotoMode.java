package com.moonside.rcc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ListIterator;

public class PhotoMode extends Activity implements SurfaceHolder.Callback,DataListener {
    public static Context context = null;
    private SurfaceView surfaceview;
    private SurfaceHolder surfaceholder;
    private Camera camera = null;
    private ListIterator<Bitmap> it;
    private GestureDetector mGestureDetector;
    ImageButton BT_left,BT_right,BT_backword,BT_stop,BT_forword;
    public int ClickButton = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_photo_mode);


        BT_left = (ImageButton) findViewById(R.id.BT_VLEFT);
        BT_right = (ImageButton) findViewById(R.id.BT_VRIGHT);
        BT_forword = (ImageButton) findViewById(R.id.BT_VUP);
        BT_backword = (ImageButton) findViewById(R.id.BT_VDOWN);
        BT_stop = (ImageButton) findViewById(R.id.BT_VSTOP);

        context = this;
        surfaceview = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceholder = surfaceview.getHolder();
        surfaceholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceholder.addCallback(this);
        if(!Home.server.start) {
            Home.server.setOnDataListener(this);
            Home.server.start();
        }

        BT_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Home.BTThread.TurnLeft();
                ClickButton  = 3;
                setUI();
            }
        });

        BT_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Home.BTThread.TurnRight();
                ClickButton  = 4;
                setUI();
            }
        });

        BT_forword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Home.BTThread.Forward();
                ClickButton = 1;
                setUI();
            }
        });

        BT_backword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Home.BTThread.Back();
                ClickButton = 2;
                setUI();
            }
        });

        BT_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Home.BTThread.Stop();
                ClickButton = 0;
                setUI();
            }
        });
    }

    public void setUI(){
        switch (ClickButton){
            case(0):
                BT_forword.setBackgroundResource(R.drawable.notup);
                BT_backword.setBackgroundResource(R.drawable.notdown);
                BT_left.setBackgroundResource(R.drawable.notleft);
                BT_right.setBackgroundResource(R.drawable.notright);
                BT_stop.setBackgroundResource(R.drawable.stop);
                break;
            case(1):
                BT_forword.setBackgroundResource(R.drawable.up);
                BT_backword.setBackgroundResource(R.drawable.notdown);
                BT_left.setBackgroundResource(R.drawable.notleft);
                BT_right.setBackgroundResource(R.drawable.notright);
                BT_stop.setBackgroundResource(R.drawable.notstop);
                break;
            case(2):
                BT_backword.setBackgroundResource(R.drawable.down);
                BT_forword.setBackgroundResource(R.drawable.notup);
                BT_left.setBackgroundResource(R.drawable.notleft);
                BT_right.setBackgroundResource(R.drawable.notright);
                BT_stop.setBackgroundResource(R.drawable.notstop);
                break;
            case(3):
                BT_left.setBackgroundResource(R.drawable.left);
                BT_backword.setBackgroundResource(R.drawable.notdown);
                BT_forword.setBackgroundResource(R.drawable.notup);
                BT_right.setBackgroundResource(R.drawable.notright);
                BT_stop.setBackgroundResource(R.drawable.notstop);
                break;
            case(4):
                BT_right.setBackgroundResource(R.drawable.right);
                BT_backword.setBackgroundResource(R.drawable.notdown);
                BT_left.setBackgroundResource(R.drawable.notleft);
                BT_forword.setBackgroundResource(R.drawable.notup);
                BT_stop.setBackgroundResource(R.drawable.notstop);
                break;
        }
    }


    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(!Home.frameList.isEmpty()){
            paint(Home.frameList.getFirst());
            it = Home.frameList.listIterator();
            it.next();
        }else{
            it=null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        surfaceview.setVisibility(View.GONE);
    }
    @Override
    protected void onResume() {
        super.onResume();
        surfaceview.setVisibility(View.VISIBLE);
        Home.server.setOnDataListener(this);
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
    }
    public void paint(Bitmap m){
        Canvas c=surfaceholder.lockCanvas();
        if(c!=null){
            synchronized (surfaceholder) {
                Rect tmp=new Rect(0,0,c.getWidth(),c.getHeight());
                //Rect tmp=new Rect(0,0,720,405);
                c.drawBitmap(m,null,tmp,new Paint());
            }
            surfaceholder.unlockCanvasAndPost(c);
        }
    }

    private void updateUI(Bitmap bufferedImage) {
        synchronized (Home.mQueue) {
            if (Home.mQueue.size() == Home.MAX_BUFFER) {
                Home.mLastFrame = Home.mQueue.poll();
            }
            Home.mQueue.add(bufferedImage);
        }
        repaint();
    }

    public void repaint() {
        //get a frame from mQueue
        synchronized (Home.mQueue) {
            if (Home.mQueue.size() > 0) {
               Home.mLastFrame = Home.mQueue.poll();
            }
        }

        if (Home.mLastFrame != null) {
            Canvas c=surfaceholder.lockCanvas();
            if(c!=null){
                synchronized (surfaceholder) {
                    int height = c.getHeight();
                    int width = c.getWidth();
                    Rect tmp=new Rect(0,0,c.getWidth(),c.getHeight());
                    //Rect tmp=new Rect(0,0,720,405);
                    //draw this frame
                    c.drawBitmap(Home.mLastFrame,null,tmp,new Paint());
                }
                surfaceholder.unlockCanvasAndPost(c);
            }
        }
        else if (Home.mImage != null) {
            Canvas c=surfaceholder.lockCanvas();
            if(c!=null){
                synchronized (surfaceholder) {
                    Rect tmp=new Rect(0,0,c.getWidth(),c.getHeight());
                    //Rect tmp=new Rect(0,0,720,405);
                    c.drawBitmap(Home.mImage,null,tmp,new Paint());
                }
                surfaceholder.unlockCanvasAndPost(c);
            }
        }
    }



    @Override
    public void onPicIn(Bitmap bufferedImage) {
        updateUI(bufferedImage);
    }

    @Override
    public void sendCommand(int t) {

    }
}