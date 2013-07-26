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

	protected boolean containsLetter;
	protected boolean reset = false;

	protected char c;

	public GameButton(int x, int y, int width, int height, Game game, Bitmap bitmap) {
		super(x, y, width, height, bitmap);
		originalBitmap = bitmap;
		this.game = game;

		grabbed = false;
	}

	public void onClick() {

	}

	public void tick() {
		if (reset) {
			reset = false;
			if (this instanceof LetterButton) bitmap = game.getAlphabetBitmap(c, width, width);
			if (this instanceof WordButton) bitmap = game.getAlphabetBitmap(' ', width, width);
		}
	}

	public void reset() {
		reset = true;
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
}
