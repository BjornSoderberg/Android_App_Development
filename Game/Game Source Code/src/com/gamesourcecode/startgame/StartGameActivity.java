package com.gamesourcecode.startgame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.gamesourcecode.R;
import com.gamesourcecode.gameoverview.GameOverviewActivity;
import com.gamesourcecode.home.HomeActivity;
import com.gamesourcecode.misc.JSONParser;
import com.gamesourcecode.misc.SessionManager;
import com.gamesourcecode.register.RegisterActivity;

public class StartGameActivity extends Activity {

	private SessionManager session;

	private String username = "";
	private String gameData = "";

	private int numberOfGames = 0;

	private Button startRandomGame;
	private ScrollView scrollView;

	private JSONParser jsonParser = new JSONParser();

	private static final String URL_CREATE_GAME = "http://192.168.60.49/android/database/startgame.php";
	private static final String URL_UPDATE_GAMES = "http://192.168.60.49/android/database/getgames.php";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_GAME = "game";
	private static final String TAG_NUMBER_OF_GAMES = "number_of_games";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.startgame);

		session = new SessionManager(getApplicationContext());

		HashMap<String, String> user = session.getUserDetails();
		username = user.get(SessionManager.KEY_USERNAME);

		startRandomGame = (Button) findViewById(R.id.startRandomGame);
		startRandomGame.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new StartGame().execute();
			}
		});

		scrollView = (ScrollView) findViewById(R.id.scrollView);

		new UpdateGames().execute();
		// Get all the local game data and start update async task

	}

	class UpdateGames extends AsyncTask<String, String, String> {

		int success;
		ProgressDialog pDialog;
		JSONObject json;

		protected void onPreExecute() {
			super.onPreExecute();

		}

		protected String doInBackground(String... string) {
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", username));

				json = jsonParser.makeHttpRequest(URL_UPDATE_GAMES, "POST", params);

				Log.i("START GAME - attempt", json.toString());

				success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					Log.i("START GAME", "Games updated!");

					numberOfGames = json.getInt(TAG_NUMBER_OF_GAMES);

					return json.toString();
				} else {
					Log.i("START GAME", "Failed to create game!");
					return json.getString(TAG_MESSAGE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			LinearLayout linear = (LinearLayout) findViewById(R.id.linear);

			for (int i = 0; i < numberOfGames; i++) {
				SessionManager session = new SessionManager(getApplicationContext());

				String username = session.getUserDetails().get(SessionManager.KEY_USERNAME);

				Button b = new Button(StartGameActivity.this);

				try {

					final JSONObject game = json.getJSONObject(TAG_GAME + i);
					
					String text = "";

					int me = -1;
					int opponent = -1;
					if (game.getString("username1").equalsIgnoreCase(username)) {
						opponent = 2;
						me = 1;
					}
					if (game.getString("username2").equalsIgnoreCase(username)) {
						opponent = 1;
						me = 2;
					}

					if (game.getInt("turn" + me) <= game.getInt("turn" + opponent)) {
						text = "Game with " + game.getString("username" + opponent) + "!\nU ken plei nao";
					} else {
						text = "Game with " + game.getString("username" + opponent) + "!\nMadur fakur, u kent plei nao";
					}
					

					text += "\nYou : " + game.getString("score" + me) + " - " + game.getString("score" + opponent) + " : " + game.getString("username" + opponent);
					
					b.setText(text);
					
					b.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							Intent i = new Intent(StartGameActivity.this, GameOverviewActivity.class);
							i.putExtra("jsonString", game.toString());
							startActivity(i);
							finish();
						}
					});

				} catch (JSONException e) {
					e.printStackTrace();
				}

				linear.addView(b);
			}

			Log.i("START GAME ACTIVITY", result);

		}
	}

	class StartGame extends AsyncTask<String, String, String> {

		int success;
		ProgressDialog pDialog;

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(StartGameActivity.this);
			pDialog.setMessage("Creating random game...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();

		}

		protected String doInBackground(String... strings) {
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username1", username));
				params.add(new BasicNameValuePair("username2", "Po"));

				JSONObject json = jsonParser.makeHttpRequest(URL_CREATE_GAME, "POST", params);

				Log.i("START GAME - attempt", json.toString());

				success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					Log.i("START GAME", "Game successfully created!");
					Intent i = new Intent(StartGameActivity.this, HomeActivity.class);
					startActivity(i);
					finish();
					return json.getString(TAG_MESSAGE);
				} else {
					Log.i("START GAME", "Failed to create game!");
					return json.getString(TAG_MESSAGE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(String toastMessage) {
			pDialog.dismiss();

			if (toastMessage != null) Toast.makeText(StartGameActivity.this, toastMessage, Toast.LENGTH_LONG).show();

		}

	}

}
