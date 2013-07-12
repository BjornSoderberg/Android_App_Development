package com.gamesourcecode.gfx;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;


import com.gamesourcecode.Game;

public class Image {

	private Bitmap bitmap;
	private int width, height;
	private int[] pixels;
	private Game game;
	private double time;
	private double average;
	private BlurFilter filter;
	
	private int radius = 1;

	public Image(Bitmap bitmap, Game game) {
		this.game = game;
		this.bitmap = bitmap;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		pixels = new int[width * height];

		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
		
		filter = new BlurFilter(bitmap);
	}

	public void tick() {
		blurImage();
	}

	public void render(Canvas screen) {
		int padding = (game.getWidth() - width) / 2;
		screen.drawBitmap(bitmap, padding, padding, null);
	}

	private void blurImage() {
		bitmap.setPixels(filter.blurImage(radius), 0, width, 0, 0, width, height);
	}
}