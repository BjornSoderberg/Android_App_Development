package com.gamesourcecode.home;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.gamesourcecode.misc.SessionManager;

public class HomeActivity extends Activity {

	private Home home;
	private SessionManager session;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		session = new SessionManager(getApplicationContext());
		// Redirects the user to the login activity if he is not logged in
		session.checkLogin();

		if (session.isLoggedIn()) {
			HashMap<String, String> user = session.getUserDetails();

			String username = user.get(SessionManager.KEY_USERNAME);
			Toast.makeText(getApplicationContext(), "Welcome " + username + "! You are logged in", Toast.LENGTH_LONG).show();

			home = new Home(this, this);
			setContentView(home);

			Log.i("HOME", "CREATED");
		}
	}

	protected void onPause() {
		super.onPause();
		Log.i("HOME", "PAUSED");
	}

	protected void onResume() {
		super.onResume();
		setContentView(home);

		Log.i("HOME", "RESUMED");
		home.start();
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
		if (home == null) {
			home = new Home(this, this);
			setContentView(home);
		}
		super.onStart();
	}

	protected void onDestroy() {
		super.onDestroy();
		home.stop();
		home = null;
		finish();
	}

	public void onBackPressed() {
	}

	public int getWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	public int getHeight() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	public SessionManager getSession() {
		return session;
	}
}
