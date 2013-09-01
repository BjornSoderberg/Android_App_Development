package com.gamesourcecode.game;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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

import com.gamesourcecode.gameoverview.GameOverviewActivity;
import com.gamesourcecode.home.HomeActivity;
import com.gamesourcecode.misc.JSONParser;
import com.gamesourcecode.startgame.StartGameActivity;

public class GameActivity extends Activity {

	private String word = "", link = "";

	private Game game;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Makes the game go fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		LoadBitmaps loader = new LoadBitmaps();
		loader.execute(this);

	}

	public void startGameOverview(String name, String data) {
		Intent i = new Intent(this, GameOverviewActivity.class);
		i.putExtra("jsonString", data);
		Log.i("Game activity", "starting game overview");
		startActivity(i);
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
	}

	protected void onRestart() {
		super.onRestart();
		Log.i("GAME", "RESTARTED");
		
		Intent i = new Intent(this, StartGameActivity.class);
		startActivity(i);
		
		new GameFinished().execute();
		
//		if (game != null) {
//			if (game.isRunning()) game.stop();
//			game = null;
//		}
		game.setRunning(false);

	}

	protected void onStart() {
		super.onStart();
		Log.i("GAME", "STARTED");
	}

	protected void onDestroy() {
		super.onDestroy();
		Log.i("GAME", "DESTROYED");
		if (game.isRunning()) game.stop();
		game = null;
	}

	public void onBackPressed() {
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

		ProgressDialog pDialog;

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(GameActivity.this);
			pDialog.setMessage("Loading images...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected Bitmap[] doInBackground(GameActivity... params) {
			initWordAndLink();

			activity = params[0];

			Bitmap[] bitmaps = new Bitmap[24];

			for (int i = 0; i < bitmaps.length; i++) {
				bitmaps[i] = getBitmap(i);
			}

			return bitmaps;
		}

		protected void onPostExecute(Bitmap[] result) {
			super.onPostExecute(result);

			pDialog.dismiss();
			game = new Game(activity, activity, result, word);

			activity.setContentView(game);
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

			Intent i = getIntent();
			word = i.getStringExtra("word");
			link = i.getStringExtra("link");

		}
	}
	
	class GameFinished extends AsyncTask<String, String, String> {

		int success;
		JSONObject json;
		JSONParser jsonParser = new JSONParser();

		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected String doInBackground(String... string) {
			try {
				
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", getIntent().getExtras().getInt("id") + ""));
				params.add(new BasicNameValuePair("index", getIntent().getExtras().getInt("mIndex") + ""));
				params.add(new BasicNameValuePair("score", Integer.toString(0)));

				Log.i("GAME ACTIVITY - Params ", params.toString());

				json = jsonParser.makeHttpRequest(Game.URL_GAME_FINISHED, "POST", params);

				Log.i("GAME  ACTIVITY- attempt", json.toString());

				success = json.getInt(Game.TAG_SUCCESS);

				if (success == 1) {
					Log.i("GAME ACTIVITY", "Games updated!");

					return json.toString();
				} else {
					Log.i("GAME ACTIVITY", "Failed to submit score!");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			Log.i("GAME ACTIVITY - UPDATED FINISHED GAME", result + "");

			if (result != null) {
				try {
					JSONObject game = json.getJSONObject("game");

					startGameOverview("jsonString", game.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Intent i = new Intent(GameActivity.this, HomeActivity.class);
				startActivity(i);
			}
			

			if (game != null) {
				if (game.isRunning()) game.stop();
				game = null;
			}
		}
	}
	
}