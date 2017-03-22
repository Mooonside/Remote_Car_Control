package com.moonside.rcc;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;


public class GravityControl extends Activity implements SensorEventListener {
    private SensorManager manager;
    private Sensor sensor_grav;
    private Sensor sensor_acce;
    String last_command = Constants.STOP;
    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
//    public TextView gravInfo;
    public ImageView IMGV;
    public int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gravity_control);
        IMGV = (ImageView)findViewById(R.id.IMG_status);
        manager = (SensorManager) getSystemService(this.SENSOR_SERVICE);

        sensor_grav = manager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensor_acce = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //int delay = 1000;
        //manager.registerListener(this,sensor_grav,delay);
        manager.registerListener(this,sensor_grav,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Home.BTThread.Stop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterListener(this, sensor_grav);
//        manager.unregisterListener(this, sensor_acce);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == sensor_grav) {
//            TextView gravInfo = (TextView)findViewById(R.id.TX_gravInfo);
            final float alpha = (float) 0.8;
            final float gravity[] = { 0, 0, 0 };
            final float linear_acceleration[] = { 0, 0, 0 };

            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            if(gravity[0] < - 0.5){
                if(last_command!=Constants.RIGHT){
                    Home.BTThread.Stop();
                    Home.BTThread.TurnRight();
//                    gravInfo.setText(last_command+"右转");
                    status = 4;
                    setUI();
                }
                last_command = Constants.RIGHT;
            }else if(gravity [0]>0.5 ){
                if(last_command!=Constants.LEFT){
                    Home.BTThread.Stop();
                    Home.BTThread.TurnLeft();
//                    gravInfo.setText(last_command+"左转");
                    status = 3;
                    setUI();
                }
                last_command = Constants.LEFT;
            }else if(gravity[1]>1.8){
                if(last_command != Constants.BACK) {
                    Home.BTThread.Stop();
                    Home.BTThread.Back();
//                    gravInfo.setText(last_command + "后退");
                    status = 2;
                    setUI();
                }
                last_command = Constants.BACK;
            }else if(gravity[1]<1.5){
                if(last_command!=Constants.FORWARD) {
                    Home.BTThread.Stop();
                    Home.BTThread.Forward();
//                    gravInfo.setText(last_command + "前进");
                    status = 1;
                    setUI();
                }
                last_command = Constants.FORWARD;
            }else{
                if(last_command!=Constants.STOP) {
                    Home.BTThread.Stop();
//                    gravInfo.setText(last_command + "前进");
                    last_command = Constants.STOP;
                    status = 0;
                    setUI();
                }
                last_command = Constants.STOP;
            }

            new Thread(){
                public void run(){
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
//        if (event.sensor == sensor_acce){
//            accelerometerValues = event.values;
//        }
//        calculateOrientation();
    }

//    private void calculateOrientation() {
//        TextView gravInfo = (TextView)findViewById(R.id.TX_gravInfo);
//        float[] values = new float[3];
//        float[] R = new float[9];
//        SensorManager.getRotationMatrix(R, null, accelerometerValues,
//                magneticFieldValues);
//        SensorManager.getOrientation(R, values);
//        values[0] = (float) Math.toDegrees(values[0]);
//
//        if (values[0] >= -5 && values[0] < 5) {
//            gravInfo.setText("方向是："+"正北");
//        } else if (values[0] >= 5 && values[0] < 85) {
//            gravInfo.setText("方向是："+"东北");
//        } else if (values[0] >= 85 && values[0] <= 95) {
//            gravInfo.setText("方向是："+"正东");
//        } else if (values[0] >= 95 && values[0] < 175) {
//            gravInfo.setText("方向是："+"东南");
//        } else if ((values[0] >= 175 && values[0] <= 180)
//                || (values[0]) >= -180 && values[0] < -175) {
//            gravInfo.setText("方向是："+"正南");
//        } else if (values[0] >= -175 && values[0] < -95) {
//            gravInfo.setText("方向是："+"西南");
//        } else if (values[0] >= -95 && values[0] < -85) {
//            gravInfo.setText("方向是："+"正西");
//        } else if (values[0] >= -85 && values[0] < -5) {
//            gravInfo.setText("方向是："+"西北");
//        }
//    }


    public void setUI(){
        switch (status){
            case 0:
                IMGV.setBackgroundResource(R.drawable.stop);
                break;
            case 1:
                IMGV.setBackgroundResource(R.drawable.gup);
                break;
            case 2:
                IMGV.setBackgroundResource(R.drawable.gdown);
                break;
            case 3:
                IMGV.setBackgroundResource(R.drawable.gleft);
                break;
            case 4:
                IMGV.setBackgroundResource(R.drawable.gright);
                break;
        }
    }

}
