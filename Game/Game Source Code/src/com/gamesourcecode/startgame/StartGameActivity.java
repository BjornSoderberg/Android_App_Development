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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gamesourcecode.R;
import com.gamesourcecode.gameoverview.GameOverviewActivity;
import com.gamesourcecode.home.HomeActivity;
import com.gamesourcecode.misc.JSONParser;
import com.gamesourcecode.misc.SessionManager;

public class StartGameActivity extends Activity implements OnClickListener {

	private SessionManager session;

	private String username = "";
	private String gameData = "";

	private int numberOfGames = 0;

	private Button startRandomGame, back;

	private JSONParser jsonParser = new JSONParser();

	private static final String URL_CREATE_GAME = "http://192.168.60.49/android/database/startgame.php";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_GAME = "game";
	private static final String TAG_NUMBER_OF_GAMES = "number_of_games";
	private static final String TAG_GAME_DATA = "gameData";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.startgame);

		session = new SessionManager(getApplicationContext());

		HashMap<String, String> user = session.getUserDetails();
		username = user.get(SessionManager.KEY_USERNAME);

		startRandomGame = (Button) findViewById(R.id.startRandomGame);
		startRandomGame.setOnClickListener(this);

		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
	}

	protected void onPause() {
		super.onPause();
		Log.i("START GAME", "PAUSED");
	}

	protected void onResume() {
		super.onResume();

		Log.i("START GAME", "RESUMED");
	}

	protected void onStop() {
		super.onStop();
		Log.i("START GAME", "STOPPED");
	}

	protected void onStart() {
		Log.i("START GAME", "STARTED");
		super.onStart();

		// Updates the scroll view according to the data stored in the shared
		// preferences
		SharedPreferences prefs = getSharedPreferences(SessionManager.PREFERENCES_NAME, Context.MODE_PRIVATE);
		String s = prefs.getString(TAG_GAME_DATA, null);
		if (s != null) updateScrollView(s);

		new UpdateGames(username, this);
		// Get all the local game data and start update async task
	}

	protected void onDestroy() {
		Log.i("START GAME", "DESTROYED");
		super.onStart();
	}

	protected void onRestart() {
		super.onResume();
		Log.i("START GAME", "RESTARTED");

		// Just temporary
		LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
		linear.removeAllViews();

		new UpdateGames(username, this);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.startRandomGame) {
			new StartGame().execute();
			Log.i("START GAME ACTIVITY", "Should create game");
		}

		if (v.getId() == R.id.back) {
			Intent i = new Intent(StartGameActivity.this, HomeActivity.class);
			startActivity(i);
			finish();
		}
	}

	private void updateScrollView(String result) {
		LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
		// Removes everything that is inside the layout so that no
		// duplicates can be shown
		linear.removeAllViews();

		SessionManager session = new SessionManager(getApplicationContext());
		String username = session.getUserDetails().get(SessionManager.KEY_USERNAME);

		JSONObject json;
		try {
			json = new JSONObject(result);

			for (int i = 0; i < json.getInt(TAG_NUMBER_OF_GAMES); i++) {

				Button b = new Button(StartGameActivity.this);

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

				text = "Game with " + game.getString("username" + opponent);

				// text += "\nYou : " + getGameScore(me, game) + " - " +
				// getGameScore(opponent, game) + " : " +
				// game.getString("username" + opponent);
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

				linear.addView(b);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Log.i("UPDATED SCROLL VIEW", ":)");

	}

	class UpdateGames extends com.gamesourcecode.misc.UpdateGames {

		public UpdateGames(String username, Context context) {
			super(username, context);
		}

		protected String doInBackground(String... string) {
			if (!isNetworkAvailable()) return null;

			return super.doInBackground(string);
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (result == null) Toast.makeText(StartGameActivity.this, "Could Not Update Games", Toast.LENGTH_LONG).show();
			else {
				Toast.makeText(StartGameActivity.this, "Successfully Updated Games", Toast.LENGTH_LONG).show();
				updateScrollView(result);
			}
		}

	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
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

				success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					new UpdateGames(username, StartGameActivity.this);
					return json.getString(TAG_MESSAGE);
				} else {
					Log.i("START GAME", "Failed to create game!");
					Intent i = new Intent(StartGameActivity.this, HomeActivity.class);
					startActivity(i);
					finish();
					return json.getString(TAG_MESSAGE);
				}
			} catch (JSONException e) {
				return null;
			}
		}

		protected void onPostExecute(String result) {
			pDialog.dismiss();

			if(result == null || success == 0) Toast.makeText(StartGameActivity.this, "There was an error when trying to create the game!", Toast.LENGTH_LONG).show();

		}

	}

}
