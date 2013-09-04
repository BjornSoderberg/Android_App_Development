package com.gamesourcecode.game;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.gamesourcecode.gameoverview.GameOverviewActivity;
import com.gamesourcecode.home.HomeActivity;
import com.gamesourcecode.misc.JSONParser;

public class GameFinished extends AsyncTask<Integer, String, String> {

	private int success, score, gameID, mIndex;
	private JSONObject json;
	private JSONParser jsonParser = new JSONParser();
	
	private GameActivity activity;
	
	public GameFinished(GameActivity activity, int score, int gameID, int mIndex) {
		this.activity = activity;
		execute(score, gameID, mIndex);
	}

	public static final String URL_GAME_FINISHED = "http://192.168.60.49/android/database/gamefinished.php";
	public static final String TAG_SUCCESS = "success";

	protected void onPreExecute() {
		super.onPreExecute();
	}

	protected String doInBackground(Integer... ints) {
		score = ints[0];
		gameID = ints[1];
		mIndex = ints[2];
		
		try {				
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id", Integer.toString(gameID)));
			params.add(new BasicNameValuePair("index", Integer.toString(mIndex)));
			params.add(new BasicNameValuePair("score", Integer.toString((int) score)));

			Log.i("GAME - Params ", params.toString());

			json = jsonParser.makeHttpRequest(URL_GAME_FINISHED, "POST", params);

			Log.i("GAME - attempt", json.toString());

			success = json.getInt(TAG_SUCCESS);

			if (success == 1) {
				Log.i("GAME", "Games updated!");

				return json.toString();
			} else {
				Log.i("GAME", "Failed to submit score!");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected void onPostExecute(String result) {
		super.onPostExecute(result);

		Log.i("GAME - UPDATED FINISHED GAME", result + "");

		if (result != null) {
			try {
				JSONObject game = json.getJSONObject("game");

				Intent i = new Intent(activity, GameOverviewActivity.class);
				i.putExtra("jsonString", game.toString());
				Log.i("Game activity", "starting game overview");
				
				activity.startActivity(i);
				activity.finish();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Intent i = new Intent(activity, HomeActivity.class);
			activity.startActivity(i);
			activity.finish();
			activity = null;
		}
	}
}