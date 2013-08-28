package com.gamesourcecode.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.gamesourcecode.R;
import com.gamesourcecode.misc.SessionManager;
import com.gamesourcecode.startgame.StartGameActivity;

public class HomeActivity extends Activity implements OnClickListener {

	private SessionManager session;

	private Button startGame, logout;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		session = new SessionManager(this);
		// Redirects the user to the login activity if he is not logged in
		session.checkLogin();

		if (session.isLoggedIn()) {
			setContentView(R.layout.home);

			startGame = (Button) findViewById(R.id.startGame);
			startGame.setOnClickListener(this);
			
			logout = (Button) findViewById(R.id.logout);
			logout.setOnClickListener(this);

			Log.i("HOME", "CREATED");
		}
	}

	public void onClick(View v) {
		if(v.getId() == R.id.startGame) {
			Intent intent = new Intent(HomeActivity.this, StartGameActivity.class);
			startActivity(intent);
		}
		if(v.getId() == R.id.logout) {
			session.logoutUser();
			session.checkLogin();
			Log.i("HOME - ON CLICK", "Logged out");
		}
	}

	protected void onPause() {
		super.onPause();
		Log.i("HOME", "PAUSED");
	}

	protected void onResume() {
		super.onResume();

		Log.i("HOME", "RESUMED");
	}

	protected void onStop() {
		super.onStop();
		Log.i("HOME", "STOPPED");
	}

	protected void onRestart() {
		Log.i("HOME", "RESTARTED");
		super.onStart();
	}

	protected void onStart() {
		Log.i("HOME", "STARTED");
		super.onStart();
	}
}
