package com.ego.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.ego.chatit.R;
import com.ego.interfaces.OnLogin;
import com.ego.singleton.User;
import com.ego.tasks.AsyncTaskRunner;
import com.ego.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity implements OnClickListener, OnCheckedChangeListener {

	Button bLogin, bRegister;

	EditText etEmail, etPass;

	TextView tvTitle, tvRecover, tvFeedback;

	String email, pass;

	Switch rememberMe;


	public static final String PREFS = "SharedPrefs";

	public static final String PREF_EMAIL = "email";

	public static final String PREF_PASSWORD = "password";

	public static final String PREF_REMEMBER = "remember";

	boolean remember;

	Intent loginIntent;

	OnLogin login = new OnLogin() {

		@Override
		public void onLogin(String data) {

			try {

                Log.v("Data",data);

				JSONObject object = new JSONObject(data);

				boolean success = object.getBoolean("success");

				if (success) {

					if (remember) {
						getSharedPreferences(PREFS, MODE_PRIVATE).edit()
								.putString(PREF_EMAIL, email)
								.putString(PREF_PASSWORD, pass)
                                .putBoolean(PREF_REMEMBER, true).commit();
					}if(!remember){
                        getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                                .putString("PREF_EMAIL", null)
                                .putString("PREF_PASSWORD", null)
                                .putBoolean(PREF_REMEMBER, false).commit();
                    }

					String[] splitted = email.split("\\@");

					User.setUserDetails(email, splitted[0].trim(), splitted[1].trim(),
							Constants.NAME_ANONYMOUS);

                    User.setIsSubscribed(false);

					startActivity(loginIntent);
				}else{
                    tvFeedback.setVisibility(View.VISIBLE);
                    tvFeedback.setText("Invalid Credentials");
                }

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_login);
		loginIntent = new Intent(LoginActivity.this, Home.class);

        Typeface bubblegum = Typeface.createFromAsset(getAssets(),"fonts/bubblegum.ttf");

        SharedPreferences pref = getSharedPreferences(PREFS, MODE_PRIVATE);
		String email = pref.getString(PREF_EMAIL, null);
		String password = pref.getString(PREF_PASSWORD, null);
		remember = pref.getBoolean(PREF_REMEMBER, false);


		bRegister = (Button) findViewById(R.id.bRegister);
		bLogin = (Button) findViewById(R.id.bLogin);

		bRegister.setOnClickListener(this);
		bLogin.setOnClickListener(this);

		etEmail = (EditText) findViewById(R.id.etEmail);

		etPass = (EditText) findViewById(R.id.etPass);

		tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setTypeface(bubblegum);

        tvFeedback = (TextView) findViewById(R.id.tvFeedback);

		tvRecover = (TextView) findViewById(R.id.tvPassRecover);
		tvRecover.setOnClickListener(this);

		rememberMe = (Switch) findViewById(R.id.sRememberMe);
		rememberMe.setOnCheckedChangeListener(this);

		if (email != null && password != null && remember) {

            rememberMe.setChecked(true);

			etEmail.setText(email);
			etPass.setText(password);
			
		}else{
            rememberMe.setChecked(false);
            etEmail.setText("");
            etPass.setText("");
        }


    }

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
	public void onClick(View v) {

		Intent i = null;
		
		switch (v.getId()) {

		case R.id.bLogin:
			// Login button pressed

            tvFeedback.setVisibility(View.GONE);

			// Retrieve the text entered from the EditText
			email = etEmail.getText().toString();
			pass = etPass.getText().toString();

			JSONObject credentials = new JSONObject();
			try {

				credentials.put("email", email);
				credentials.put("pass", pass);

				AsyncTaskRunner runner = new AsyncTaskRunner(
						AsyncTaskRunner.POST_TASK, getApplicationContext(),
						LoginActivity.this, Constants.ON_LOGIN, login);
				runner.dataAsJsonString(credentials.toString());
				runner.execute(Constants.LOGIN_URL);

			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;

		case R.id.bRegister:
			// Register button pressed

            tvFeedback.setVisibility(View.GONE);


            i = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(i);
			overridePendingTransition(R.anim.right_slide_in,
					R.anim.left_slide_out);

			break;
			
		case R.id.tvPassRecover:
			
			//Password recovery button pressed
            tvFeedback.setVisibility(View.GONE);


            i = new Intent(LoginActivity.this, PasswordRecoveryActivity.class);
			startActivity(i);
			
			overridePendingTransition(R.anim.right_slide_in,
					R.anim.left_slide_out);
			

		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		remember = isChecked;

        if(!remember){
            getSharedPreferences(PREFS, MODE_PRIVATE).edit()
                    .remove("PREF_EMAIL")
                    .remove("PREF_PASSWORD").putBoolean(PREF_REMEMBER, false).commit();
        }
	}


}
