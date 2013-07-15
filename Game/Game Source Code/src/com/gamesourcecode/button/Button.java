package com.gamesourcecode.button;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.gamesourcecode.Game;
import com.gamesourcecode.R;

public class Button {

	protected Game game;
	protected int xOriginal, yOriginal, x, y, width = 0, height = 0;
	protected Bitmap bitmap;
	protected boolean containsLetter;
	protected char c;

	protected boolean grabbed;

	public Button(int x, int y, int width, int height, Game game) {
		xOriginal = x;
		yOriginal = y;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.game = game;

		grabbed = false;

		setOriginalBitmap(width, height);
	}

	public void render(Canvas screen) {
		screen.drawBitmap(bitmap, x, y, null);
	}

	public void onClick() {

	}

	public void tick() {

	}

	public boolean containsLetter() {
		return containsLetter;
	}

	public int getX() {
		return x;
	}

	public int getOriginalX() {
		return xOriginal;
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

	public Rect getOriginalRect() {
		Rect r = new Rect(xOriginal, yOriginal, xOriginal + width, yOriginal + height);
		return r;
	}

	public Rect getCenter() {
		Rect r = new Rect(x + width / 2, y + height / 2, x + width / 2, y + height / 2);
		return r;
	}

	protected void setOriginalBitmap(int width, int height) {
		bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.androidlogo);
		bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
	}

	public void setLetter(char c) {
		containsLetter = true;
		this.c = c;
		bitmap = game.getAlphabetBitmap(c, width, height);
	}

	public void grabbed() {
		grabbed = true;
	}

	public void released() {
		grabbed = false;
		x = xOriginal;
		y = yOriginal;
	}

	public boolean isGrabbed() {
		return grabbed;
	}

	public char getChar() {
		return c;
	}
}
