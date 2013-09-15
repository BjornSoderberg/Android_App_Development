package com.gamesourcecode.gameoverview;

import java.util.ArrayList;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gamesourcecode.R;
import com.gamesourcecode.R.id;
import com.gamesourcecode.game.GameActivity;
import com.gamesourcecode.misc.JSONParser;
import com.gamesourcecode.misc.SessionManager;
import com.gamesourcecode.misc.UpdateGames;
import com.gamesourcecode.startgame.StartGameActivity;

public class GameOverviewActivity extends Activity implements OnClickListener {

	private Intent intent;
	private JSONObject json;
	private SessionManager session;

	TextView title, mScoreTV, oScoreTV, mGameScoreTV, oGameScoreTV, prevScoreTV;
	Button back, play;

	// oIndex - opponent index, mIndex - me index
	private int oIndex = -1, mIndex = -1;

	private String oName, mName;
	private String imageData = "";

	private int mScore, oScore, mGameScore, oGameScore, mPrevScore, oPrevScore, ID;
	private int gameState = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i("GAME OVERVIEW", "CREATED");

		setContentView(R.layout.gameoverview);

		session = new SessionManager(getApplicationContext());
		intent = getIntent();

		getDataFromJSONString(intent.getStringExtra("jsonString"));

		title = (TextView) findViewById(id.title);

		initScoreTVs();

		back = (Button) findViewById(id.back);
		back.setOnClickListener(this);

		play = (Button) findViewById(id.play);
		play.setOnClickListener(this);
		
		setTexts();
	}
	
	private void getDataFromJSONString(String data) {
		try {
			json = new JSONObject(intent.getStringExtra("jsonString"));

			if (session.getUserDetails().get(SessionManager.KEY_USERNAME).equalsIgnoreCase(json.getString("username1"))) {
				mIndex = 1;
				oIndex = 2;
			} else {
				oIndex = 1;
				mIndex = 2;
			}

			mName = json.getString("username" + mIndex);
			oName = json.getString("username" + oIndex);

			mScore = Integer.parseInt(json.getString("score" + mIndex));
			oScore = Integer.parseInt(json.getString("score" + oIndex));

			mGameScore = Integer.parseInt(json.getString("game_score" + mIndex));
			oGameScore = Integer.parseInt(json.getString("game_score" + oIndex));

			mPrevScore = Integer.parseInt(json.getString("prev_game_score" + mIndex));
			oPrevScore = Integer.parseInt(json.getString("prev_game_score" + oIndex));

			// determines whether username1 or username2 should play
			gameState = Integer.parseInt(json.getString("game_state"));

			String s = json.getString("images");
			for (int i = 0; i < s.length(); i++) {
				if (!s.substring(i, i + 1).equals("\\") && !(s.substring(i, i + 1) == null)) imageData += s.substring(i, i + 1);
			}

			ID = json.getInt("id");

		} catch (JSONException e) {
			Toast.makeText(GameOverviewActivity.this, "There was an error loading the data!", Toast.LENGTH_LONG).show();
			onBackPressed();
		}
	}
	
	private void setTexts() {
		play.setEnabled(playButtonEnabled());

		title.setText(mName + " vs. " + oName);
		
		mScoreTV.setText(mScore + " wins");
		oScoreTV.setText(oScore + " wins");
		
		if (gameState == mIndex) {
			mGameScoreTV.setText("It is your turn!");
			oGameScoreTV.setText("-");
		} else {
			if (mGameScore != -1) mGameScoreTV.setText(mGameScore + "");
			else mGameScoreTV.setText("Not able to play yet!");
			oGameScoreTV.setText("Waiting...");
		}

		if (mPrevScore != -1 && oPrevScore != -1) {
			String s = "";
			// Updates the prev score text view to show if the player won or
			// lost the last round
			if (mPrevScore < oPrevScore) s = "You lost!";
			if (mPrevScore > oPrevScore) s = "You won!";
			if (mPrevScore == oPrevScore) s = "It was a draw!";

			prevScoreTV.setText("In the last round you got " + mPrevScore + " points! \n" + oName + " got " + oPrevScore + " points! \n" + s);
		}
	}

	private boolean playButtonEnabled() {
		if (gameState == mIndex) return true;
		else return false;
	}

	private void initScoreTVs() {
		// inits top text views with larger text
		mScoreTV = (TextView) findViewById(id.mScore);

		oScoreTV = (TextView) findViewById(id.oScore);

		// inits "score / waiting..." text views
		mGameScoreTV = (TextView) findViewById(id.mGameScore);
		oGameScoreTV = (TextView) findViewById(id.oGameScore);


		prevScoreTV = (TextView) findViewById(id.prevScore);
	}

	public void onBackPressed() {
		Intent i = new Intent(GameOverviewActivity.this, StartGameActivity.class);
		startActivity(i);
		finish();
	}

	public void onClick(View v) {

		if (v.getId() == R.id.back) {
			Intent i = new Intent(GameOverviewActivity.this, StartGameActivity.class);
			startActivity(i);
			finish();
		}

		if (v.getId() == R.id.play) {
			new CheckGameState().execute();
		}

	}

	protected void onStart() {
		super.onStart();
		Log.i("GAME OVERVIEW", "STARTED");
	}

	protected void onRestart() {
		super.onRestart();
		Log.i("GAME OVERVIEW", "RESTARTED");
	}

	protected void onResume() {
		super.onResume();
		Log.i("GAME OVERVIEW", "RESUMED");
	}

	protected void onDestroy() {
		super.onDestroy();
		Log.i("GAME OVERVIEW", "DESTROYED");
	}

	protected void onStop() {
		super.onStop();
		Log.i("GAME OVERVIEW", "STOPPED");
	}

	class CheckGameState extends AsyncTask<String, String, String> {
		int success;
		JSONParser jsonParser = new JSONParser();
		ProgressDialog pDialog;

		private static final String URL_CHECK_GAME_STATE = "http://192.168.60.49/android/database/checkgamestate.php";
		private final static String TAG_SUCCESS = "success";
		private final static String TAG_MESSAGE = "message";
		private final static String TAG_GAME = "game";

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(GameOverviewActivity.this);
			pDialog.setMessage("Checking if it is your turn...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();

		}

		protected String doInBackground(String... strings) {
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id", ID + ""));
				params.add(new BasicNameValuePair("mIndex", mIndex + ""));

				JSONObject json = jsonParser.makeHttpRequest(URL_CHECK_GAME_STATE, "POST", params);

				success = json.getInt(TAG_SUCCESS);
				
			} catch (JSONException e) {
				return e.getMessage();
			}

			return null;
		}

		protected void onPostExecute(String result) {
			pDialog.dismiss();

			Log.i("RESULT FROM CHECK GAME STATE", success + ": "+result + "");
			
			try {
			if (success == 1) {
				 Intent i = new Intent(GameOverviewActivity.this,
				 GameActivity.class);
				
				 Log.i("GAME OVERVIEW", "Starting Game Activity");
				
				 i.putExtra("imageData", imageData);
				 // 0 because it's the first game
				 i.putExtra("round", 0);
				 i.putExtra("id", ID);
				 i.putExtra("mIndex", mIndex);
				
				 startActivity(i);
				 finish();
			// This is true if it is not the player's turn and the data is getting updated
			} else if (success == 2){
				getDataFromJSONString(json.getString(TAG_GAME));
				setTexts();
				new UpdateGames(mName, GameOverviewActivity.this);
			}
			} catch (JSONException e) {

			}
		}
	}
}
