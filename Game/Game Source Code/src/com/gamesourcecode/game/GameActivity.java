package com.gamesourcecode.game;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Random;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {

	private String word = "";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Makes the game go fullscreen		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		word = getRandomWord();
		
		LoadBitmaps loader = new LoadBitmaps();
		loader.execute(this);

	}
	
	// Just for testing to swap between images
	private String getRandomWord() {
		Random random = new Random();
		int i = random.nextInt(3);
		if(i == 0) return "guitar";
		if(i == 1) return "cat";
		if(i == 2) return "sydney";
		return "";
	}

	public int getWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	public int getHeight() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	private class LoadBitmaps extends AsyncTask<GameActivity, Integer, Bitmap[]> {

		private GameActivity activity;

		protected void onPreExecute() {
			super.onPreExecute();
			GameActivity.this.setProgressBarIndeterminateVisibility(true);
		}

		protected Bitmap[] doInBackground(GameActivity... params) {
			activity = params[0];

			Bitmap[] bitmaps = new Bitmap[24];

			for (int i = 0; i < bitmaps.length; i++) {
				bitmaps[i] = getBitmap(i);
			}

			return bitmaps;
		}

		protected void onPostExecute(Bitmap[] result) {
			super.onPostExecute(result);

			Game game = new Game(activity, activity, result, word);

			activity.setContentView(game);

			GameActivity.this.setProgressBarIndeterminateVisibility(false);

		}

		private Bitmap getBitmap(int i) {
			String url = "http://192.168.60.49/android/images/" + word + i + ".jpg";

			try {
				InputStream in = new java.net.URL(url).openStream();
				Bitmap bitmap = BitmapFactory.decodeStream(in);
				in.close();
				return bitmap;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}
	}
}
