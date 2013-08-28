package com.gamesourcecode.gameoverview;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gamesourcecode.R;
import com.gamesourcecode.R.id;
import com.gamesourcecode.game.GameActivity;
import com.gamesourcecode.misc.SessionManager;

public class GameOverviewActivity extends Activity {
	
	private Intent intent;
	private JSONObject json;
	private SessionManager session;
	
	TextView title, mScoreTV, oScoreTV;
	Button play;
	
	// oIndex - opponent index, mIndex - me index
	private int oIndex = -1, mIndex = -1;
	private String oName, mName;
	private int mScore, oScore, ID;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.gameoverview);
		
		session = new SessionManager(getApplicationContext());
		
		intent = getIntent();
		
		try {
			json = new JSONObject(intent.getStringExtra("jsonString"));

			if(session.getUserDetails().get(SessionManager.KEY_USERNAME).equalsIgnoreCase(json.getString("username1"))) {
				mIndex = 1;
				oIndex = 2;
			} else {
				oIndex = 1;
				mIndex = 2;
			}
			
			mName = json.getString("username" + mIndex);
			oName = json.getString("username" + oIndex);
			
			mScore = json.getInt("score" + mIndex);
			oScore = json.getInt("score" + oIndex);
			
			ID = json.getInt("id");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		title = (TextView) findViewById(id.title);
		mScoreTV = (TextView) findViewById(id.mScore);
		oScoreTV = (TextView) findViewById(id.oScore);
		
		play = (Button) findViewById(id.play);
		play.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(GameOverviewActivity.this, GameActivity.class);
				i.putExtra("id", ID);
				i.putExtra("mIndex", mIndex);
				startActivityForResult(i, 1);
			}
			
		});
		
		title.setText(mName + " vs. " + oName);
		mScoreTV.setText(mScore + " points");
		oScoreTV.setText(oScore + " points");
	}
}
