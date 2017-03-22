package com.moonside.rcc;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class SocketServer extends Thread {
	private ServerSocket mServer;
	public DataListener mDataListener;
	public LinkedList<Integer> msgs;
	public static boolean start = false;
	public Bitmap curFrame = null;

	public BufferedInputStream inputStream = null;
	public BufferedOutputStream outputStream = null;
	public Socket socket = null;
	public ByteArrayOutputStream byteArray = null;


	public SocketServer() {
		msgs=new LinkedList<Integer>();
		start = false;
	}
	public static int byte2int(byte[] src, int offset) {
		int value;
		value = (int) ( ((src[offset] & 0xFF)<<24)
				|((src[offset+1] & 0xFF)<<16)
				|((src[offset+2] & 0xFF)<<8)
				|(src[offset+3] & 0xFF));
		return value;
	}
	public void add(int t){
		synchronized (msgs){
			msgs.push(t);
		}
	}

	private Bitmap rotateBitmap(Bitmap origin, float alpha) {
		if (origin == null) {
			return null;
		}
		int width = origin.getWidth();
		int height = origin.getHeight();
		Matrix matrix = new Matrix();
		matrix.setRotate(alpha);
		// 围绕原地进行旋转
		Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
		if (newBM.equals(origin)) {
			return newBM;
		}
		origin.recycle();
		return newBM;
	}

	void initial(){
		try {
			if (byteArray != null)
				byteArray.reset();
			else
				byteArray = new ByteArrayOutputStream();
			socket = new Socket();
			try {
				socket.connect(new InetSocketAddress("192.168.43.1", 8888), 10000); // hard-code server address
			} catch (Exception e) {
//					Toast.makeText(PhotoMode.context,"连接失败",Toast.LENGTH_SHORT).show();
				System.out.println("bad connected");
			}
//				Toast.makeText(PhotoMode.context,"连接成功",Toast.LENGTH_SHORT).show();
			System.out.println("success connected");
			inputStream = new BufferedInputStream(socket.getInputStream());
			outputStream = new BufferedOutputStream(socket.getOutputStream());
		}catch (Exception e){

		}
	}

	public void run() {
		super.run();
		start = true;

		try {
			while (!Thread.currentThread().isInterrupted()) {
				initial();
				byte[] cBuffer = new byte[256];
				byte[] fBuffer = null;
				int inputBufferLen = 0;
				String parseString = null;

				while (!Thread.currentThread().isInterrupted()&&(inputBufferLen = inputStream.read(cBuffer)) != -1) {
					parseString = new String(cBuffer, 0, inputBufferLen);
					JsonParser parser = new JsonParser();
					boolean isJSON = true;
					JsonElement element = null;
					try {
						element =  parser.parse(parseString);
					}
					catch (JsonParseException e){
						System.out.println("exception: " + e);
						isJSON = false;
					}
					if (isJSON && element != null) {
						JsonObject obj = element.getAsJsonObject();
						element = obj.get("type");
						if (element != null && element.getAsString().equals("data")) {
							element = obj.get("length");
							int length = element.getAsInt();
							element = obj.get("width");
							int width = element.getAsInt();
							element = obj.get("height");
							int height = element.getAsInt();
							fBuffer = new byte[length];
							break;
						}
					}
					else {
						byteArray.write(cBuffer, 0, inputBufferLen
						);
						break;
					}
				}
				int fp = 0;
				if (fBuffer != null) {
					JsonObject jsonObj = new JsonObject();
					jsonObj.addProperty("state", "ok");
					//write
					outputStream.write(jsonObj.toString().getBytes());
					outputStream.flush();
					while(!Thread.currentThread().isInterrupted()){
						synchronized (msgs){
							while(!msgs.isEmpty()){
								int t=msgs.poll();
								outputStream.write(t);
								outputStream.flush();
							}
						}
						//read in the size , 4 bits
						fBuffer = new byte[4];
						for(int i=0;i<4;++i) {
							while(inputStream.read(fBuffer, i, 1)!=1);
						}
						// transform tmp into int
						int flength = byte2int(fBuffer,0);
						//use the length to read in the picture
						fBuffer = new byte[flength];
						int pointer = 0;
						while(pointer < flength){
							int t = inputStream.read(fBuffer,pointer,flength - pointer);
							pointer = pointer + t;
						}
						//turn the bin data flow into bitmap
						Bitmap frame = BitmapFactory.decodeByteArray(fBuffer,0,fBuffer.length);
						//new framw
						fp = (fp + 1) % 80;
						curFrame = rotateBitmap(frame,90);
						//send uuv to datalistner
						mDataListener.onPicIn(curFrame);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (outputStream != null) {
					outputStream.close();
					outputStream = null;
				}
				if (inputStream != null) {
					inputStream.close();
					inputStream = null;
				}
				if (socket != null) {
					socket.close();
					socket = null;
				}
				if (byteArray != null) {
					byteArray.close();
				}
			} catch (Exception e) {
			}
		}
	}
	public void setOnDataListener(DataListener listener) {
		mDataListener = listener;
	}
}

