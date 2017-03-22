package com.moonside.rcc;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import java.io.IOException;
import java.util.LinkedList;

public class Home extends AppCompatActivity {
    BluetoothAdapter HomeBTAdapter = null;
    ImageButton BT_setCon;
    ImageButton BT_poleCtrl;
    ImageButton BT_gravCtrl;
    ImageButton BT_voiceCtrl;
    ImageButton BT_video;
    ImageButton BT_Bell;
    BluetoothSocket BTSocket = null;
    BluetoothDevice BTDevice = null;

    public static LinkedList<Bitmap> frameList;
    public static LinkedList<Bitmap> mQueue = new LinkedList<Bitmap>();
    public static final int MAX_BUFFER = 15;
    public static SocketServer server;
    public static BtThread BTThread = null;
    public static Bitmap mImage, mLastFrame;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BT_setCon = (ImageButton) findViewById(R.id.BT_setBT);
        BT_poleCtrl = (ImageButton) findViewById(R.id.BT_poleCtrl);
        BT_gravCtrl = (ImageButton) findViewById(R.id.BT_gravCtrl);
        BT_voiceCtrl = (ImageButton) findViewById(R.id.BT_voiceCtrl);
        BT_video = (ImageButton) findViewById(R.id.BT_video);
        BT_Bell = (ImageButton) findViewById(R.id.BT_bell);

        frameList = new LinkedList<Bitmap>();
        HomeBTAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!HomeBTAdapter.isEnabled()){
            Intent EnableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // this constant if > 0 then this code will be returned in OnActivityResult()
            startActivityForResult(EnableBt,RESULT_OK);
        }

        server = new SocketServer();


        BT_setCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"设置连接",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Home.this, bConnector.class);
                intent.setAction(Intent.ACTION_VIEW);
                startActivityForResult(intent, 0);
            }
        });

        BT_poleCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"按键控制",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Home.this, poleCtrl.class);
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
            }
        });

        BT_gravCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"重力感应",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Home.this, GravityControl.class);
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
            }
        });

        BT_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"视频通话",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Home.this, PhotoMode.class);
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
            }
        });

        BT_voiceCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"语音识别",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Home.this, VoiceCtrl.class);
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
            }
        });

        BT_Bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"响铃模式",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Home.this, RingBell.class);
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
            }
        });
    }

   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
       Bundle t = data.getExtras();
       String device_name = t.getString("nameInfo");
       String device_mac = t.getString("macInfo");
//       connInfo.setText("连接设备：" + device_mac);
//       stateInfo.setText("开始创建device");
       try {
           BTDevice = HomeBTAdapter.getRemoteDevice(device_mac);
//           stateInfo.setText("创建device成功");
       } catch (Exception e) {
//           stateInfo.setText("创建device失败");
       }
       try {
           setupSocket(BTDevice);
           BTThread.write("s");

       } catch (IOException e) {
           e.printStackTrace();
       }
   }

    void setupSocket(BluetoothDevice BTdevice) throws IOException {
            if (BTSocket != null) {
                BTSocket.close();
            }
//            stateInfo.setText("开始创建socket");
            try{
                BTSocket = BTdevice.createRfcommSocketToServiceRecord(Constants.MY_UUID);
//                stateInfo.setText("创建socket成功");
            }catch (Exception e){
//                stateInfo.setText("创建socket失败");
            }
            HomeBTAdapter.cancelDiscovery();
            try{
//                stateInfo.setText("开始连接socket");
                BTSocket.connect();
//                stateInfo.setText("连接socket成功");
            }catch(Exception e){
//                stateInfo.setText("连接socket失败");
            }
            Toast.makeText(getApplicationContext(),"SUCCESS CONNECT!",Toast.LENGTH_SHORT).show();

            BTThread = new BtThread(BTSocket);
            BTThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(BTSocket != null){
            try{
                BTSocket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        if(BTThread != null)
            BTThread= null;
    }
}

