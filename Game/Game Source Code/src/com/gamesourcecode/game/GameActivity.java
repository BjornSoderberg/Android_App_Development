package com.gamesourcecode.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
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
import com.gamesourcecode.login.LoginActivity;

public class GameActivity extends Activity {

	private String word = "", link = "";

	private Game game = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Makes the game go fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		LoadBitmaps loader = new LoadBitmaps();
		loader.execute(this);

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
		Log.i("GAME", "STARTING HOME ACTIVITY");
		Intent i = new Intent(this, HomeActivity.class);
		game.setRunning(false);
		startActivity(i);

	}
	
	protected void onStart() {
		super.onStart();
		Log.i("GAME", "STARTED");
	}

	protected void onDestroy() {
		super.onDestroy();
		Log.i("GAME", "DESTROYED");
		game.stop();
		game = null;
		finish();
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

		String url = "http://192.168.60.49/android/database/getimage.php";

		private void initWordAndLink() {
			String result = "";
			InputStream is = null;

			try {

				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(url);
				HttpResponse response = client.execute(post);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}

				is.close();
				result = sb.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				JSONArray jArray = new JSONArray(result);
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject jData = jArray.getJSONObject(i);
					word = jData.getString("name");
					link = jData.getString("link");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}
}