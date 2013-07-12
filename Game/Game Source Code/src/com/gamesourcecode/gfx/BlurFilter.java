package com.gamesourcecode.gfx;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class BlurFilter {

	private Bitmap bitmap;

	private int width = 0, height = 0, divide = 0;
	private int[] pixels;

	private double[] red, green, blue;
	private double[] r, g, b;

	public BlurFilter(Bitmap bitmap) {
		this.bitmap = bitmap;
		width = bitmap.getWidth();
		height = bitmap.getHeight();

		initArrays();

		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		for (int i = 0; i < pixels.length; i++) {
			red[i] = (pixels[i] >> 16) & 0xff;
			green[i] = (pixels[i] >> 8) & 0xff;
			blue[i] = (pixels[i] >> 0) & 0xff;
		}
	}

	private void initArrays() {
		pixels = new int[width * height];

		red = new double[width * height];
		green = new double[width * height];
		blue = new double[width * height];

		r = new double[width * height];
		g = new double[width * height];
		b = new double[width * height];
	}

	public int[] blurImage(int radius) {
		double[] matrixR = new double[(radius * 2 + 1) * (radius * 2 + 1)];
		double[] matrixG = new double[(radius * 2 + 1) * (radius * 2 + 1)];
		double[] matrixB = new double[(radius * 2 + 1) * (radius * 2 + 1)];


		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				matrixR = getMatrix(red, x, y, radius);
				matrixG = getMatrix(green, x, y, radius);
				matrixB = getMatrix(blue, x, y, radius);
				
				double rr = 0, gg = 0, bb = 0;
				rr = averageColor(matrixR);
				gg = averageColor(matrixG);
				bb = averageColor(matrixB);
				
				r[x + y * width] = rr;
				g[x + y * width] = gg;
				b[x + y * width] = bb;
			}
		}
		
		for(int i = 0; i < pixels.length; i++) {
			red[i] = r[i];
			green[i] = g[i];
			blue[i] = b[i];
			
			pixels[i] = (int)(r[i])*256*256 + (int)(g[i])*256 + (int)b[i];
			Log.i("Tagafsaf", pixels[i] + "");
		}
		
		for(int i = 0; i < pixels.length; i++) {
			pixels[i] += 0xff000000;
		}

		return pixels;
	}
	
	private double[] getMatrix(double[] pixels, int x, int y, int radius) {
		double[] matrix = new double[(2 * radius + 1) * (2 * radius + 1)];
		int k = 0;
		divide = 0;

		for (int i = -radius; i <= radius; i++) {
			for (int j = -radius; j <= radius; j++) {
				if ((x + j) + (y + i) * width >= 0 && (x + j) + (y + i) * width < pixels.length) {
					matrix[k] = pixels[(x + j) + (y + i) * width];
					divide++;
				} 
				k++;
			}
		}

		return matrix;
	}
	
	private double averageColor(double[] matrix) {
		double color = 0;

		for (int i = 0; i < matrix.length; i++) {
			color += matrix[i];
		}

		return color / divide;
	}
}