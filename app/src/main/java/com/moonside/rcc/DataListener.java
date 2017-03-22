package com.moonside.rcc;

import android.graphics.Bitmap;
public interface DataListener {
	void onPicIn(Bitmap bufferedImage);
	void sendCommand(int t);
}
