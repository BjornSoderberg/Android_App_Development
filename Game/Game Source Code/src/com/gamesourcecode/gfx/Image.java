package com.gamesourcecode.gfx;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import com.gamesourcecode.Game;
import com.gamesourcecode.R;

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
		tickImage();
	}
	
	private void tickImage() {
		if (game.getTime() / game.getTotalTime() <= 1D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa22);
		else if (game.getTime() / game.getTotalTime() <= 2D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa21);
		else if (game.getTime() / game.getTotalTime() <= 3D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa20);
		else if (game.getTime() / game.getTotalTime() <= 4D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa19);
		else if (game.getTime() / game.getTotalTime() <= 5D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa18);
		else if (game.getTime() / game.getTotalTime() <= 6D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa17);
		else if (game.getTime() / game.getTotalTime() <= 7D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa16);
		else if (game.getTime() / game.getTotalTime() <= 8D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa15);
		else if (game.getTime() / game.getTotalTime() <= 9D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa14);
		else if (game.getTime() / game.getTotalTime() <= 10D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa13);
		else if (game.getTime() / game.getTotalTime() <= 11D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa12);
		else if (game.getTime() / game.getTotalTime() <= 12D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa11);
		else if (game.getTime() / game.getTotalTime() <= 13D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa10);
		else if (game.getTime() / game.getTotalTime() <= 14D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa9);
		else if (game.getTime() / game.getTotalTime() <= 15D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa8);
		else if (game.getTime() / game.getTotalTime() <= 16D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa7);
		else if (game.getTime() / game.getTotalTime() <= 17D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa6);
		else if (game.getTime() / game.getTotalTime() <= 18D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa5);
		else if (game.getTime() / game.getTotalTime() <= 19D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa4);
		else if (game.getTime() / game.getTotalTime() <= 20D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa3);
		else if (game.getTime() / game.getTotalTime() <= 21D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa2);
		else if (game.getTime() / game.getTotalTime() <= 22D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa1);
		else if (game.getTime() / game.getTotalTime() <= 23D / 24D) bitmap = BitmapFactory.decodeResource(game.getResources(), R.drawable.aaa0);
	}

	public void render(Canvas screen) {
		int padding = (game.getWidth() - width) / 2;
		screen.drawBitmap(bitmap, padding, padding, null);
	}

	private void blurImage() {
		bitmap.setPixels(filter.blurImage(radius), 0, width, 0, 0, width, height);
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
