package com.gamesourcecode.gameoverview;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gamesourcecode.R;
import com.gamesourcecode.R.id;
import com.gamesourcecode.game.GameActivity;
import com.gamesourcecode.misc.SessionManager;
import com.gamesourcecode.startgame.StartGameActivity;

public class GameOverviewActivity extends Activity implements OnClickListener {

	private Intent intent;
	private JSONObject json;
	private SessionManager session;

	TextView title, mScoreTV, oScoreTV, mGameScoreTV, oGameScoreTV;
	Button back, play;

	// oIndex - opponent index, mIndex - me index
	private int oIndex = -1, mIndex = -1;

	private String oName, mName;
	private String imageData = "";

	private int mScore, oScore, mGameScore, oGameScore, ID;
	private int gameState = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i("GAME OVERVIEW", "CREATED");

		setContentView(R.layout.gameoverview);

		session = new SessionManager(getApplicationContext());

		intent = getIntent();

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

			// determines whether username1 or username2 should play
			gameState = Integer.parseInt(json.getString("game_state"));

			String s = json.getString("images");
			for (int i = 0; i < s.length(); i++) {
				if (!s.substring(i, i + 1).equals("\\") && !(s.substring(i, i + 1) == null)) imageData += s.substring(i, i + 1);
			}

			ID = json.getInt("id");

		} catch (JSONException e) {
			e.printStackTrace();
		}

		title = (TextView) findViewById(id.title);

		initScoreTVs();

		back = (Button) findViewById(id.back);
		back.setOnClickListener(this);

		play = (Button) findViewById(id.play);
		play.setOnClickListener(this);
		play.setEnabled(playButtonEnabled());

		title.setText(mName + " vs. " + oName);
	}

	private boolean playButtonEnabled() {
		if(gameState == mIndex) return true;
		else return false;
	}

	private void initScoreTVs() {
		// inits top text views with larger text
		mScoreTV = (TextView) findViewById(id.mScore);
		mScoreTV.setText(mScore + " wins");

		oScoreTV = (TextView) findViewById(id.oScore);
		oScoreTV.setText(oScore + " wins");

		// inits "score / waiting..." text views
		mGameScoreTV = (TextView) findViewById(id.mGameScore);
		oGameScoreTV = (TextView) findViewById(id.oGameScore);
		
		if (gameState == mIndex) {
			mGameScoreTV.setText("It is your turn!");
			oGameScoreTV.setText("Your opponents score is secret until you play!");
		} else {
			if(mGameScore != -1) mGameScoreTV.setText(mGameScore + "");
			else mGameScoreTV.setText("Not able to play yet!");
			oGameScoreTV.setText("Waiting...");
		}

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
			try {
				Intent i = new Intent(GameOverviewActivity.this, GameActivity.class);

				Log.i("GAME OVERVIEW", "Starting Game Activity");

				Log.i("GAME OVERVIEW - image data", imageData);

				i.putExtra("imageData", imageData);
				// 0 because it's the first game
				i.putExtra("round", 0);
				i.putExtra("id", ID);
				i.putExtra("mIndex", mIndex);

				startActivity(i);
				finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
}
