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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity {

	private String word = "", link = "";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Makes the game go fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// word = getRandomWord();

		LoadBitmaps loader = new LoadBitmaps();
		loader.execute(this);

	}

	// Just for testing to swap between images
	// private String getRandomWord() {
	// Random random = new Random();
	// int i = random.nextInt(3);
	// if(i == 0) return "guitar";
	// if(i == 1) return "cat";
	// if(i == 2) return "sydney";
	// return "";
	// }

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

			Game game = new Game(activity, activity, result, word);

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
				while((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				
				is.close();
				result = sb.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				JSONArray jArray = new JSONArray(result);
				for(int i = 0; i < jArray.length(); i++) {
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
