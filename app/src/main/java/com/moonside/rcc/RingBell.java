package com.moonside.rcc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class RingBell extends Activity {
    private boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring_bell);
        ImageButton BT_RING = (ImageButton)findViewById(R.id.BT_RING);
        BT_RING.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!flag) {
                    Toast.makeText(getApplicationContext(),"响铃中",Toast.LENGTH_SHORT).show();
                    Home.server.add(1);
                    ImageButton BT_RING = (ImageButton)findViewById(R.id.BT_RING);
                    BT_RING.setBackgroundResource(R.drawable.ring);
                }else {
                    Toast.makeText(getApplicationContext(),"关闭响铃",Toast.LENGTH_SHORT).show();
                    Home.server.add(0);
                    ImageButton BT_RING = (ImageButton)findViewById(R.id.BT_RING);
                    BT_RING.setBackgroundResource(R.drawable.notring);
                }
                flag = !flag;
            }
        });
    }
}
