package com.gamesourcecode.register;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gamesourcecode.R;
import com.gamesourcecode.home.HomeActivity;
import com.gamesourcecode.misc.JSONParser;
import com.gamesourcecode.misc.SessionManager;

public class RegisterActivity extends Activity implements OnClickListener {

	private EditText user, email, pass1, pass2;
	private Button register;

	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();

	private static final String URL = "http://192.168.60.49/android/database/register.php";

	public static final String TAG_SUCCESS = "success";
	public static final String TAG_MESSAGE = "message";
	public static final String TAG_USERNAME = "username";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		user = (EditText) findViewById(R.id.username);
		email = (EditText) findViewById(R.id.email);
		pass1 = (EditText) findViewById(R.id.password1);
		pass2 = (EditText) findViewById(R.id.password2);

		register = (Button) findViewById(R.id.register);
		register.setOnClickListener(this);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.register) {
			if (user.getText().toString() != null && email.getText().toString() != null && pass1.getText().toString() != null && pass2.getText().toString() != null) {
				if (pass1.getText().toString().equals(pass2.getText().toString())) new CreateUser().execute();
				else Toast.makeText(RegisterActivity.this, "Your Passwords Did Not Match", Toast.LENGTH_SHORT).show();
			} else Toast.makeText(RegisterActivity.this, "Please Enter Every Field!", Toast.LENGTH_SHORT).show();
		}
	}

	class CreateUser extends AsyncTask<String, String, String> {

		boolean failure = false;

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(RegisterActivity.this);
			pDialog.setMessage("Creating User...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		protected String doInBackground(String... strings) {
			int success;

			String username = user.getText().toString();
			String mail = email.getText().toString();
			String password1 = pass1.getText().toString();
			// String password2 = pass2.getText().toString();

			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("username", username));
				params.add(new BasicNameValuePair("password", password1));
				params.add(new BasicNameValuePair("email", mail));

				JSONObject json = jsonParser.makeHttpRequest(URL, "POST", params);

				Log.i("REGISTER - Create User", json.toString());

				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					Log.i("REGISTER", "Registration Successful");
					Intent i = new Intent(RegisterActivity.this, HomeActivity.class);
					SessionManager session = new SessionManager(getApplicationContext());
					session.createLoginSession(json.getString(TAG_USERNAME));
					startActivity(i);
					finish();
					return json.getString(TAG_MESSAGE);
				} else {
					return json.getString(TAG_MESSAGE);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExcecute(String toastMessage) {
			pDialog.dismiss();
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

			if (toastMessage != null) Toast.makeText(RegisterActivity.this, toastMessage, Toast.LENGTH_LONG).show();
		}

	}

}
