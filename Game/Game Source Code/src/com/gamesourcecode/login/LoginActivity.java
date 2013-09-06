package com.gamesourcecode.login;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gamesourcecode.R;
import com.gamesourcecode.home.HomeActivity;
import com.gamesourcecode.misc.JSONParser;
import com.gamesourcecode.misc.SessionManager;
import com.gamesourcecode.register.RegisterActivity;

public class LoginActivity extends Activity implements OnClickListener {

	private EditText user, pass;
	private Button login, register;

	private ProgressDialog pDialog;

	private JSONParser jsonParser = new JSONParser();

	private static final String URL = "http://192.168.60.49/android/database/login.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_USERNAME = "username";
	private static final String TAG_MESSAGE = "message";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.login);

		user = (EditText) findViewById(R.id.username);
		pass = (EditText) findViewById(R.id.password);

		login = (Button) findViewById(R.id.login);
		login.setOnClickListener(this);
		register = (Button) findViewById(R.id.register);
		register.setOnClickListener(this);

		Log.i("LOGIN", "CREATED");
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.login:
			new AttemptLogin().execute();
			break;
		case R.id.register:
			Intent i = new Intent(this, RegisterActivity.class);
			finish();
			startActivity(i);
			finish();
			break;
		default:
			break;
		}
	}

	class AttemptLogin extends AsyncTask<String, String, String> {

		int success;
		String username, password;

		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("Attempting login...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... strings) {
			username = user.getText().toString();
			password = pass.getText().toString();

			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", username));
				params.add(new BasicNameValuePair("password", password));

				JSONObject json = jsonParser.makeHttpRequest(URL, "POST", params);

				Log.i("LOGIN - attempt", json.toString());
				
				success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					Log.i("LOGIN", "Login successful");
					Intent i = new Intent(LoginActivity.this, HomeActivity.class);
					SessionManager session = new SessionManager(getApplicationContext());
					session.createLoginSession(json.getString(TAG_USERNAME));
					startActivity(i);
					finish();
					return json.getString(TAG_MESSAGE);
				} else {
					Log.i("LOGIN", "Login failed!");
					return json.getString(TAG_MESSAGE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return "Oop! Something went wrong!";
		}

		protected void onPostExecute(String toastMessage) {
			pDialog.dismiss();

			InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(user.getWindowToken(), 0);

			if (toastMessage != null) {
				Toast.makeText(LoginActivity.this, toastMessage, Toast.LENGTH_LONG).show();
				if(success == 1) 
					Toast.makeText(getApplicationContext(), "Welcome " + username + "! You are logged in", Toast.LENGTH_LONG).show();
			}
		}

	}

	// protected void onPause() {
	// super.onPause();
	// Log.i("LOGIN", "PAUSED");
	// }
	//
	// protected void onResume() {
	// super.onResume();
	// setContentView(R.layout.login);
	//
	// Log.i("LOGIN", "RESUMED");
	// }
	//
	// protected void onStop() {
	// super.onStop();
	// Log.i("LOGIN", "STOPPED");
	// }
	//
	// protected void onRestart() {
	// Log.i("LOGIN", "RESTARTED");
	// super.onStart();
	// }
	//
	// protected void onStart() {
	// Log.i("LOGIN", "STARTED");
	// super.onStart();
	// }
	//
	// protected void onDestroy() {
	// super.onDestroy();
	// finish();
	// }
	//
	public void onBackPressed() {
	}
	//
	// public int getWidth() {
	// DisplayMetrics dm = new DisplayMetrics();
	// getWindowManager().getDefaultDisplay().getMetrics(dm);
	// return dm.widthPixels;
	// }
	//
	// public int getHeight() {
	// DisplayMetrics dm = new DisplayMetrics();
	// getWindowManager().getDefaultDisplay().getMetrics(dm);
	// return dm.heightPixels;
	// }
}
