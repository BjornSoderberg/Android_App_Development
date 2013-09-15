package com.gamesourcecode.misc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

public class UpdateGames extends AsyncTask<String, String, String> {

	private int success;
	private ProgressDialog pDialog;
	
	private JSONObject json;
	private JSONParser jsonParser = new JSONParser();
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_GAME_DATA = "gameData";
	private static final String URL_UPDATE_GAMES = "http://192.168.60.49/android/database/getgames.php";
	
	private String username;
	
	private Context context;
	
	public UpdateGames(String username, Context context) {
		this.username = username;
		this.context = context;
		execute();
	}

	protected void onPreExecute() {
		super.onPreExecute();

	}

	protected String doInBackground(String... string) {
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", username));

			json = jsonParser.makeHttpRequest(URL_UPDATE_GAMES, "POST", params);

			success = json.getInt(TAG_SUCCESS);

			if (success == 1) {
				Log.i("START GAME", "%%%%%%%%%%%%%%%%%% Games updated! %%%%%%%%%%%%%%%%%%%%%%%");
				return json.toString();
			} else {
				Log.i("START GAME", "Failed to create game!");
				return json.getString(TAG_MESSAGE);
			}
		} catch (JSONException e) {
			return null;
		}
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		if (result != null) {
			SharedPreferences prefs = context.getSharedPreferences(SessionManager.PREFERENCES_NAME, Context.MODE_PRIVATE);
			Editor e = prefs.edit();
			e.putString(TAG_GAME_DATA, result);
			e.commit();
		}
		
		context = null;
	}
}
