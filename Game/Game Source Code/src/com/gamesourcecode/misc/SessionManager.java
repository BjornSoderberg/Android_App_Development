package com.gamesourcecode.misc;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.gamesourcecode.login.LoginActivity;

public class SessionManager {

	private SharedPreferences preferences;
	private Editor editor;
	
	private Context context;
	
	int PRIVATE_MODE = 0;
	
	public static final String PREFERENCES_NAME = "Game Source Code";
	
	// Keys for setting and getting preferences
	public static final String KEY_USERNAME = "username";
	public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
	public static final String KEY_GAME = "game";
	
	public SessionManager(Context context) {
		this.context = context;
		preferences = context.getSharedPreferences(PREFERENCES_NAME, PRIVATE_MODE);
		editor = preferences.edit();
	}
	
	// Create login session
	public void createLoginSession(String username) {
		editor.putBoolean(KEY_IS_LOGGED_IN, true);
		editor.putString(KEY_USERNAME, username);
		
		editor.commit();
	}
	
	public void checkLogin() {
		// If the user is not logged in, the app redirects the user to the login page
		if(!isLoggedIn()) {
			Intent i = new Intent(context, LoginActivity.class);
			
			// Closes all the other activities so that the user cannot get into the app without being logged in
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			context.startActivity(i);
		}
	}
	
	// Gets stored session data
	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();
		user.put(KEY_USERNAME, preferences.getString(KEY_USERNAME, null));

		return user;
	}
	
	// Clears session details
	public void logoutUser() {
		editor.clear();
		editor.commit();
		
		// After logout the user is redirected to the login activity and all the other activities are closed
		Intent i = new Intent(context, LoginActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		context.startActivity(i);
	}
	
	// Checks if the user is logged in
	public boolean isLoggedIn() {
		return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
	}
	
}
