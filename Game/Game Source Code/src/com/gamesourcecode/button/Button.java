package com.gamesourcecode.button;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Button {

	protected int x, y, xOrigin, yOrigin, width = 0, height = 0, xTouch = 0, yTouch = 0;
	protected Bitmap bitmap;

	protected boolean grabbed = false;

	public Button(int x, int y, int width, int height, Bitmap bitmap) {
		this.x = x;
		this.y = y;
		xOrigin = x;
		yOrigin = y;
		this.width = width;
		this.height = height;

		this.bitmap = bitmap;
		this.bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
	}

	public void render(Canvas screen, Paint paint) {
		if (getOriginRect().intersect(xTouch, yTouch, xTouch, yTouch) || isMoveable()) screen.drawBitmap(bitmap, x, y, paint);
		else screen.drawBitmap(bitmap, x, y, null);

	}

	public void onClick() {
	}

	public void tick() {

	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getOriginX() {
		return xOrigin;
	}

	public int getOriginY() {
		return yOrigin;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Rect getRect() {
		Rect r = new Rect(x, y, x + width, y + height);
		return r;
	}

	public Rect getOriginRect() {
		Rect r = new Rect(xOrigin, yOrigin, xOrigin + width, yOrigin + height);
		return r;
	}

	public Rect getCenter() {
		Rect r = new Rect(x + width / 2, y + height / 2, x + width / 2, y + height / 2);
		return r;
	}

	public boolean isGrabbed() {
		return grabbed;
	}

	public void grab() {
		grabbed = true;
	}

	public void release() {
		grabbed = false;
		x = xOrigin;
		y = yOrigin;
	}

	protected boolean isMoveable() {
		return false;
	}

	public void setTouchXY(int x, int y) {
		xTouch = x;
		yTouch = y;
	}

	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void recycle() {
		bitmap = null;
	}
}
