package com.moonside.rcc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;


public class poleCtrl extends AppCompatActivity {
    ImageButton BT_left,BT_right,BT_backword,BT_stop,BT_forword;
    Button BT_return;
    SeekBar SB_speed;
    public int ClickButton = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pole_ctrl);
        BT_left = (ImageButton) findViewById(R.id.BT_LEFT);
        BT_right = (ImageButton) findViewById(R.id.BT_RIGHT);
        BT_forword = (ImageButton) findViewById(R.id.BT_FORW);
        BT_backword = (ImageButton) findViewById(R.id.BT_BACK);
        BT_stop = (ImageButton) findViewById(R.id.BT_STOP);

        SB_speed = (SeekBar) findViewById(R.id.SB_speed);
        BT_return = (Button) findViewById(R.id.BT_back);
        Toast.makeText(getApplicationContext(),"in",Toast.LENGTH_LONG).show();

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

        SB_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
               // Log.v("拖动过程中的值：", String.valueOf(progress) + ", " + String.valueOf(fromUser));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekbar) {
                //Log.v("开始滑动时的值：", String.valueOf(seekbar.getProgress()));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekbar) {
                double j = ((double)seekbar.getProgress())/seekbar.getMax();
                if(j<0.25)
                    Home.BTThread.write(Constants.SPEED_1);
                else if(j<0.5)
                    Home.BTThread.write(Constants.SPEED_2);
                else if(j<0.75)
                    Home.BTThread.write(Constants.SPEED_3);
                else
                    Home.BTThread.write(Constants.SPEED_4);
            }
        });


        BT_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"back",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(poleCtrl.this, Home.class);
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
                finish();
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
    protected void onDestroy() {
        super.onDestroy();
        Home.BTThread.Stop();
    }
}
