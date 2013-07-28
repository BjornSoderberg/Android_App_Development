package com.gamesourcecode.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.gamesourcecode.game.GameActivity;

public class HomeActivity extends Activity {

	private Home home;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		home = new Home(this, this);
		setContentView(home);

		
		Log.i("HOME", "CREATED");
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
}
