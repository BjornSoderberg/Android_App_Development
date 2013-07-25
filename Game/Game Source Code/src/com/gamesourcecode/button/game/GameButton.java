package com.gamesourcecode.button.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.gamesourcecode.button.Button;
import com.gamesourcecode.game.Game;

public class GameButton extends Button {

	protected Game game;
	protected Bitmap originalBitmap;
	protected int xOrigin, yOrigin;

	protected boolean containsLetter;
	protected boolean grabbed;
	protected boolean hasBeenOutside = false;

	protected char c;

	public GameButton(int x, int y, int width, int height, Game game, Bitmap bitmap) {
		super(x, y, width, height, bitmap);
		xOrigin = x;
		yOrigin = y;
		originalBitmap = bitmap;
		this.game = game;

		grabbed = false;
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

	public Rect getOriginRect() {
		Rect r = new Rect(xOrigin, yOrigin, xOrigin + width, yOrigin + height);
		return r;
	}

	public Rect getCenter() {
		Rect r = new Rect(x + width / 2, y + height / 2, x + width / 2, y + height / 2);
		return r;
	}

	public void setChar(char c) {
		containsLetter = true;
		this.c = c;
		bitmap = game.getAlphabetBitmap(c, width, height);
	}

	public char getChar() {
		return c;
	}

	public void grabbed() {
		grabbed = true;
	}

	public void released() {
		grabbed = hasBeenOutside = false;
		x = xOrigin;
		y = yOrigin;
	}

	public boolean isGrabbed() {
		return grabbed;
	}

	public void outside() {
		hasBeenOutside = true;
	}

	public boolean hasBeenOutside() {
		return hasBeenOutside;
	}
}
