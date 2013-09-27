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
	private boolean redirect = true;

	private JSONObject json;
	private JSONParser jsonParser = new JSONParser();

	private GameActivity activity;

	public GameFinished(GameActivity activity, int score, int gameID, int mIndex) {
		this.activity = activity;
		execute(score, gameID, mIndex);
	}

	public GameFinished(GameActivity activity, int score, int gameID, int mIndex, boolean redirect) {
		this.activity = activity;
		this.redirect = redirect;
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

			Log.i("GAME FINISHED - Params ", params.toString());

			json = jsonParser.makeHttpRequest(URL_GAME_FINISHED, "POST", params);

			success = json.getInt(TAG_SUCCESS);

			if (success == 1) {
				Log.i("GAME", "Games updated!");

				return json.toString();
			} else {
				Log.i("GAME", "Failed to submit score!");
			}
		} catch (JSONException e) {
			redirect = true;
		}

		return null;
	}

	protected void onPostExecute(String result) {
		super.onPostExecute(result);

		try {
			Log.i("GAME Fin - UPDATED FINISHED GAME", json.getString("message") + "");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (!redirect) {
			activity = null;
			return;
		}

		if (result != null) {
			try {
				JSONObject game = json.getJSONObject("game");

				Intent i = new Intent(activity, GameOverviewActivity.class);
				i.putExtra("jsonString", game.toString());
				Log.i("Game activity", "starting game overview");

				activity.startActivity(i);
				activity.finish();
			} catch (JSONException e) {
			}
		} else {
			Intent i = new Intent(activity, HomeActivity.class);
			activity.startActivity(i);
			activity.finish();
		}

		activity = null;
	}
}