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

	TextView title, mScoreTV, oScoreTV;

	// text views for displaying the scores for the different rounds
	// M stands for "me" (to the left) and O stands for "opponent" (to the
	// right)
	TextView[] tvScoreM, tvScoreO;
	Button back, play;

	// oIndex - opponent index, mIndex - me index
	private int oIndex = -1, mIndex = -1;

	private String oName, mName;
	private String imageData = "";

	private int mScore, oScore, ID;
	private int numGames = 5;
	private int[] scoreM, scoreO;

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
			
			String s = json.getString("images");
			for(int i = 0; i < s.length(); i++) {
				if(!s.substring(i , i + 1).equals("\\") && !(s.substring(i, i + 1) == null)) imageData += s.substring(i, i+1);
			}

			initScores(json);

			int tot = 0;
			for (int i = 0; i < numGames; i++) {
				if (scoreM[i] != -1) tot += scoreM[i];
			}
			mScore = tot;
			tot = 0;
			for (int i = 0; i < numGames; i++) {
				if (scoreO[i] != -1) tot += scoreO[i];
			}
			oScore = tot;

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

		setTVText();
	}

	private boolean playButtonEnabled() {
		for (int i = numGames - 1; i >= 0; i--) {
			if (scoreO[i] != -1) {
				if (i - 1 >= 0) if (scoreM[i - 1] == -1) return true;
				if (scoreM[i] == 0) return true;
				if (i + 1 < scoreM.length) if (scoreM[i + 1] == -1) return true;
			}
			if (i == 0) {
				if (scoreM[i] == -1) return true;
			}
			if(i == numGames) {
				// If the fifth round's score is set, the button should not be clickable
				if(scoreM[i] != -1) return false;
			}
		}

		return false;
	}
	
	private int getRound() {
		for (int i = 0; i < numGames; i++) {
			if(scoreM[i] == -1) return i;
		}
		return -1;
	}

	private void initScores(JSONObject game) {
		scoreM = new int[numGames];
		scoreO = new int[numGames];

		try {

			for (int i = 0; i < numGames; i++) {
				scoreM[i] = Integer.parseInt(game.getString("score" + mIndex + "_" + i));
				scoreO[i] = Integer.parseInt(game.getString("score" + oIndex + "_" + i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initScoreTVs() {
		mScoreTV = (TextView) findViewById(id.mScore);
		oScoreTV = (TextView) findViewById(id.oScore);

		mScoreTV.setText(mScore + " points");
		oScoreTV.setText(oScore + " points");

		tvScoreM = new TextView[numGames];
		tvScoreO = new TextView[numGames];

		tvScoreM[0] = (TextView) findViewById(id.scoreM_0);
		tvScoreM[1] = (TextView) findViewById(id.scoreM_1);
		tvScoreM[2] = (TextView) findViewById(id.scoreM_2);
		tvScoreM[3] = (TextView) findViewById(id.scoreM_3);
		tvScoreM[4] = (TextView) findViewById(id.scoreM_4);

		tvScoreO[0] = (TextView) findViewById(id.scoreO_0);
		tvScoreO[1] = (TextView) findViewById(id.scoreO_1);
		tvScoreO[2] = (TextView) findViewById(id.scoreO_2);
		tvScoreO[3] = (TextView) findViewById(id.scoreO_3);
		tvScoreO[4] = (TextView) findViewById(id.scoreO_4);

		for (int i = 0; i < tvScoreM.length; i++) {
			tvScoreM[i].setOnClickListener(this);
			tvScoreO[i].setOnClickListener(this);
		}
	}

	private void setTVText() {
		for (int i = 0; i < tvScoreM.length; i++) {
			if (scoreM[i] != -1) tvScoreM[i].setText(scoreM[i] + "");
			else tvScoreM[i].setText("Not played yet!");

			if (scoreO[i] != -1) tvScoreO[i].setText(scoreO[i] + "");
			else tvScoreO[i].setText("Not played yet!");
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
				
				JSONObject json = new JSONObject(imageData);
				JSONObject o = json.getJSONObject("round" + getRound());
				
				i.putExtra("word", o.getString("name"));
				i.putExtra("link", o.getString("link"));
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
