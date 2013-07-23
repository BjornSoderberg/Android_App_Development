package com.gamesourcecode.gfx;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.gamesourcecode.Game;

public class Image {

	private Bitmap bitmap;
	private Bitmap bitmap2;
	private int width, height;
	private Game game;
	private double opacity;
	private int index = 0;

	private int radius = 1;

	public Image(Bitmap bitmap, Game game) {
		this.game = game;
		this.bitmap = bitmap;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
	}

	public void tick() {
		tickImage();
		//Ticks opacity
		opacity = 23 - (game.getTime() / game.getTotalTime() * 23) - index;
	}

	private void tickImage() {
		if (index == (int) (23 - (game.getTime() / game.getTotalTime() * 23))) return;
		index = (int) (23 - (game.getTime() / game.getTotalTime() * 23));
		if (index > 22) index = 22;

		bitmap = getBitmap(index + 1);
		bitmap2 = getBitmap(index);

	}

	public void render(Canvas screen) {
		int padding = (game.getWidth() - width) / 2;
		Paint alpha = new Paint();
		alpha.setAlpha((int) (255 * (1 - opacity)));
		if (bitmap != null) screen.drawBitmap(bitmap, padding, padding, null);
		if (bitmap2 != null) screen.drawBitmap(bitmap2, padding, padding, alpha);
	}

	private Bitmap getBitmap(int i) {
//		Resources r = game.getResources();
//		if (i == 0) return BitmapFactory.decodeResource(r, R.drawable.aaa0);
//		if (i == 1) return BitmapFactory.decodeResource(r, R.drawable.aaa1);
//		if (i == 2) return BitmapFactory.decodeResource(r, R.drawable.aaa2);
//		if (i == 3) return BitmapFactory.decodeResource(r, R.drawable.aaa3);
//		if (i == 4) return BitmapFactory.decodeResource(r, R.drawable.aaa4);
//		if (i == 5) return BitmapFactory.decodeResource(r, R.drawable.aaa5);
//		if (i == 6) return BitmapFactory.decodeResource(r, R.drawable.aaa6);
//		if (i == 7) return BitmapFactory.decodeResource(r, R.drawable.aaa7);
//		if (i == 8) return BitmapFactory.decodeResource(r, R.drawable.aaa8);
//		if (i == 9) return BitmapFactory.decodeResource(r, R.drawable.aaa9);
//		if (i == 10) return BitmapFactory.decodeResource(r, R.drawable.aaa10);
//		if (i == 11) return BitmapFactory.decodeResource(r, R.drawable.aaa11);
//		if (i == 12) return BitmapFactory.decodeResource(r, R.drawable.aaa12);
//		if (i == 13) return BitmapFactory.decodeResource(r, R.drawable.aaa13);
//		if (i == 14) return BitmapFactory.decodeResource(r, R.drawable.aaa14);
//		if (i == 15) return BitmapFactory.decodeResource(r, R.drawable.aaa15);
//		if (i == 16) return BitmapFactory.decodeResource(r, R.drawable.aaa16);
//		if (i == 17) return BitmapFactory.decodeResource(r, R.drawable.aaa17);
//		if (i == 18) return BitmapFactory.decodeResource(r, R.drawable.aaa18);
//		if (i == 19) return BitmapFactory.decodeResource(r, R.drawable.aaa19);
//		if (i == 20) return BitmapFactory.decodeResource(r, R.drawable.aaa20);
//		if (i == 21) return BitmapFactory.decodeResource(r, R.drawable.aaa21);
//		if (i == 22) return BitmapFactory.decodeResource(r, R.drawable.aaa22);
//		if (i == 23) return BitmapFactory.decodeResource(r, R.drawable.aaa23);
		
		String url = "http://192.168.60.49/android/aaa" + i + ".jpg";
		
		try {
			InputStream in = new java.net.URL(url).openStream();
			Bitmap bitmap = BitmapFactory.decodeStream(in);
			bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
			return bitmap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		return null;
	}

	public int getX() {
		return (game.getWidth() - width) / 2;
	}

	public int getY() {
		return (game.getWidth() - width) / 2;
	}

	public int getWidth() {
		return bitmap.getWidth();
	}

	public int getHeight() {
		return bitmap.getHeight();
	}
}
