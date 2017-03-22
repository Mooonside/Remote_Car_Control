package com.moonside.rcc;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BtThread extends Thread{
    private  final InputStream input;
    private  final OutputStream output;
    private  final BluetoothSocket msocket;


    public  BtThread(BluetoothSocket bs)  {
        msocket = bs;
        InputStream temp_input = null;
        OutputStream temp_output = null;
        try {
            temp_input = msocket.getInputStream();
            temp_output = msocket.getOutputStream();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        input = temp_input;
        output = temp_output;
    }

    public void write(String information) {
        if(msocket == null)
            return;
        try{
            output.write(information.getBytes());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String read(){
        if(input == null) return "no input!";
        try{
            byte bytes[]=new byte[30];
            input.read(bytes);
            String msg=new String(bytes,"UTF-8");
            return msg;
        } catch(IOException e){
            return "exception";
        }
    }

    public void Forward(){
        this.write(Constants.FORWARD);
        return;
    }

    public void Back(){
        this.write(Constants.BACK);
        return;
    }

    public void TurnLeft(){
        this.write(Constants.LEFT);
        return;
    }

    public void TurnRight(){
        this.write(Constants.RIGHT);
        return;
    }

    public void Stop(){
        this.write(Constants.STOP);
        return;
    }


}