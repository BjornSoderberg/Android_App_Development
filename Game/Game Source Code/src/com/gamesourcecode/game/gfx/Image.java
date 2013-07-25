package com.gamesourcecode.game.gfx;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.gamesourcecode.game.Game;

public class Image {

	private Bitmap bitmap;
	private Bitmap bitmap2;
	private Bitmap[] bitmaps;

	private int width, height;
	private Game game;
	private double opacity;
	private int index = 0;

	private int radius = 1;

	public Image(Game game, Bitmap[] bitmaps) {
		this.game = game;
		this.bitmaps = bitmaps;

		// Just for testing
		width = 500;
		height = 400;
	}

	public void tick() {
		if (!game.hasGuessedRight()) tickImage();
		else bitmap = bitmap2 = getBitmap(0);

		// Ticks opacity
		opacity = 24 - (game.getTime() / game.getTotalTime() * 24) - index;
	}

	private void tickImage() {
		if (index == (int) (24 - (game.getTime() / game.getTotalTime() * 24))) return;
		index = (int) (24 - (game.getTime() / game.getTotalTime() * 24));
		if (index > 23) index = 23;
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
		if (0 <= i && i < bitmaps.length) return bitmaps[i];

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
