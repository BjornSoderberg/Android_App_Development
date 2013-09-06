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

		LoadBitmaps loader = new LoadBitmaps();
		loader.execute();

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

		if (game != null) if (game.isRunning()) {

			new GameFinished(this, score, getIntent().getExtras().getInt("id"), getIntent().getExtras().getInt("mIndex"));

			game.recycle();

		}
	}

	protected void onRestart() {
		super.onRestart();
		Log.i("GAME", "RESTARTED");

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

			try {
				InputStream in = new java.net.URL(url).openStream();
				Bitmap bitmap = BitmapFactory.decodeStream(in);
				in.close();
				return bitmap;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
				for (int j = 0; j < 10; j++) {
					Log.i("GAME ACTIVITY", "OUT OF MEMORY!!!");
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
				e.printStackTrace();
			}

		}
	}

	// class GameFinished extends AsyncTask<String, String, String> {
	//
	// int success;
	// JSONObject json;
	// JSONParser jsonParser = new JSONParser();
	//
	// protected void onPreExecute() {
	// super.onPreExecute();
	// }
	//
	// protected String doInBackground(String... string) {
	// try {
	//
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair("id",
	// getIntent().getExtras().getInt("id") + ""));
	// params.add(new BasicNameValuePair("index",
	// getIntent().getExtras().getInt("mIndex") + ""));
	// params.add(new BasicNameValuePair("score", Integer.toString(0)));
	//
	// Log.i("GAME ACTIVITY - Params ", params.toString());
	//
	// json = jsonParser.makeHttpRequest(Game.URL_GAME_FINISHED, "POST",
	// params);
	//
	// Log.i("GAME  ACTIVITY- attempt", json.toString());
	//
	// success = json.getInt(Game.TAG_SUCCESS);
	//
	// if (success == 1) {
	// Log.i("GAME ACTIVITY", "Games updated!");
	//
	// return json.toString();
	// } else {
	// Log.i("GAME ACTIVITY", "Failed to submit score!");
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	//
	// return null;
	// }
	//
	// protected void onPostExecute(String result) {
	// super.onPostExecute(result);
	//
	// Log.i("GAME ACTIVITY - UPDATED FINISHED GAME", result + "");
	//
	// if (result != null) {
	// try {
	// JSONObject game = json.getJSONObject("game");
	//
	// Intent i = new Intent(GameActivity.this, GameOverviewActivity.class);
	// i.putExtra("jsonString", game.toString());
	// Log.i("Game activity", "starting game overview");
	// startActivity(i);
	// finish();
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// } else {
	// Intent i = new Intent(GameActivity.this, HomeActivity.class);
	// startActivity(i);
	// }
	//
	//
	// if (game != null) {
	// if (game.isRunning()) game.stop();
	// game = null;
	// }
	// }
	// }

}