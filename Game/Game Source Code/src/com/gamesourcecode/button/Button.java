package com.gamesourcecode.button;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Button {

	protected int x, y, width = 0, height = 0;
	protected Bitmap bitmap;

	public Button(int x, int y, int width, int height, Bitmap bitmap) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		this.bitmap = bitmap;
		this.bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
	}

	public void render(Canvas screen, Paint paint) {
		screen.drawBitmap(bitmap, x, y, paint);
	}

	public void onClick() {
		// Make image darker when pressed
	}

	public void tick() {

	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setXY(int x, int y) {
		this.x = x - bitmap.getWidth() / 2;
		this.y = y - bitmap.getHeight() / 2;
	}

	public Rect getRect() {
		Rect r = new Rect(x, y, x + width, y + height);
		return r;
	}

	public Rect getCenter() {
		Rect r = new Rect(x + width / 2, y + height / 2, x + width / 2, y + height / 2);
		return r;
	}
}
