package com.gamesourcecode.game;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.gamesourcecode.gameoverview.GameOverviewActivity;
import com.gamesourcecode.home.HomeActivity;
import com.gamesourcecode.startgame.StartGameActivity;

public class GameActivity extends Activity {

	private String word = "", link = "";

	private Game game;
	private int round, score;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Makes the game go fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Gets the round. Round is set to 0 when called from the game overview
		// class
		try {
			round = getIntent().getExtras().getInt("round");
		} catch (NullPointerException e) {
			onRestart();
			return;
		}
		if (round != 0) score = getIntent().getExtras().getInt("score");

		new LoadBitmaps().execute();

	}

	protected void onResume() {
		super.onResume();
		if (game != null) game.resume();
	}

	protected void onPause() {
		super.onPause();
		Log.i("GAME", "PAUSED");
		if (game != null) game.pause();
	}

	protected void onStop() {
		super.onStop();
		Log.i("GAME", "STOPPED");

		finishGame();
	}

	private void finishGame() {
		if (game != null) if (game.isRunning()) {

			// the last parameter prevents being redirected to another activity
			new GameFinished(this, score, getIntent().getExtras().getInt("id"), getIntent().getExtras().getInt("mIndex"), false);

			game.recycle();

		}
	}

	protected void onRestart() {
		super.onRestart();
		Log.i("GAME", "RESTARTED");

		startStartGameActivity();
	}

	private void startStartGameActivity() {

		Intent i = new Intent(this, StartGameActivity.class);
		startActivity(i);
		finish();

		if (game != null) {
			if (game.isRunning()) game.stop();
			game = null;
		}
	}

	protected void onStart() {
		super.onStart();
		Log.i("GAME", "STARTED");
	}

	protected void onDestroy() {
		super.onDestroy();
		Log.i("GAME", "DESTROYED");
		if (game != null) {
			if (game.isRunning()) game.stop();
			game = null;
		}
	}

	public void onBackPressed() {
	}

	public void nextRound(int score) {
		Intent i = new Intent(this, GameActivity.class);

		i.putExtra("imageData", getIntent().getStringExtra("imageData"));
		i.putExtra("round", round + 1);
		i.putExtra("score", this.score + score);
		i.putExtra("id", getIntent().getExtras().getInt("id"));
		i.putExtra("mIndex", getIntent().getExtras().getInt("mIndex"));

		startActivity(i);
		finish();

	}

	public int getScore() {
		return score;
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

	private class LoadBitmaps extends AsyncTask<Void, Integer, Bitmap[]> {

		ProgressDialog pDialog;

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(GameActivity.this);
			pDialog.setMessage("Loading images...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected Bitmap[] doInBackground(Void... nothing) {
			initWordAndLink();

			Bitmap[] bitmaps = new Bitmap[24];

			for (int i = 0; i < bitmaps.length; i++) {
				bitmaps[i] = getBitmap(i);
			}

			return bitmaps;
		}

		protected void onPostExecute(Bitmap[] result) {
			super.onPostExecute(result);

			pDialog.dismiss();
			game = new Game(GameActivity.this, GameActivity.this, result, word, round);

			GameActivity.this.setContentView(game);
		}

		private Bitmap getBitmap(int i) {
			String url = "http://192.168.60.49/android/images/" + link + i + ".jpg";

			boolean retry = true;
			int count = 0;

			while (retry) {
				try {
					retry = false;
					count++;

					InputStream in = new java.net.URL(url).openStream();
					Bitmap bitmap = BitmapFactory.decodeStream(in);
					in.close();
					return bitmap;
				} catch (MalformedURLException e) {
					retry = true;
				} catch (IOException e) {
					retry = true;
				} catch (OutOfMemoryError e) {
					retry = true;
					Toast.makeText(GameActivity.this, "Oops! You ran out of memory!", Toast.LENGTH_LONG).show();
					count = 101;
				}

				if (count > 100) {
					Toast.makeText(GameActivity.this, "There was an error loading the images!", Toast.LENGTH_LONG).show();
					finishGame();
					startStartGameActivity();
					
					break;
				}
			}

			return null;
		}

		private void initWordAndLink() {
			try {
				Intent i = getIntent();
				String imageData = i.getStringExtra("imageData");

				JSONObject j = new JSONObject(imageData);
				JSONObject o = j.getJSONObject("round" + round);

				word = o.getString("name");
				link = o.getString("link");

			} catch (JSONException e) {
				Toast.makeText(GameActivity.this, "There was an error loading the data!", Toast.LENGTH_LONG).show();
				startStartGameActivity();
			}

		}
	}
}