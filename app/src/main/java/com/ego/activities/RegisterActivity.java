package com.ego.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ego.chatit.R;
import com.ego.exceptions.InvalidEmailException;
import com.ego.exceptions.PasswordMismatchException;
import com.ego.impl.CheckInput;
import com.ego.interfaces.OnRegister;
import com.ego.tasks.AsyncTaskRunner;
import com.ego.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends Activity implements OnClickListener {

	Button mRegister;

	EditText mEmail, mPass, mConfPass;

	TextView mTitle, tvFeedback;

	OnRegister register = new OnRegister() {

		@Override
		public void onRegister(final String data) {
			
			// On register callback method
			Log.v("Returned Data", data);

			runOnUiThread(new Runnable() {

				@Override
				public void run() {

					JSONObject j;
					try {
						j = new JSONObject(data);

						boolean success = j.getBoolean("success");

						if (!success) {
							
							String error = j.getString("error");

							throw new InvalidEmailException(error);
						}

						tvFeedback.setVisibility(View.VISIBLE);
                        tvFeedback.setTextColor(Color.parseColor("#00C957"));
						tvFeedback.setText("Verification Email Sent");


					} catch (JSONException e) {
						e.printStackTrace();
					} catch (InvalidEmailException e) {
						tvFeedback.setVisibility(View.VISIBLE);
                        tvFeedback.setTextColor(Color.parseColor("#EE0000"));
						tvFeedback.setText(e.getMessage());
					}
				}
			});

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_register);

		mEmail = (EditText) findViewById(R.id.etEmail);
		mPass = (EditText) findViewById(R.id.etPass);
		mConfPass = (EditText) findViewById(R.id.etConfPass);

		mRegister = (Button) findViewById(R.id.bRegister);
		mRegister.setOnClickListener(this);

		mTitle = (TextView) findViewById(R.id.tvTitle);

		tvFeedback = (TextView) findViewById(R.id.tvFeedback);
	}

	@Override
	protected void onStart() {
		super.onStart();
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
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bRegister:

			String email = mEmail.getText().toString();
			String pass = mPass.getText().toString();
			String confPass = mConfPass.getText().toString();

			if (email.equals("") || pass.equals("") || confPass.equals("")) {
				runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvFeedback.setVisibility(View.VISIBLE);
                        tvFeedback.setTextColor(Color.parseColor("#EE0000"));
                        tvFeedback.setText("All fields are required.");
                    }
                });

			} else {
				try {
					new CheckInput(email, pass, confPass).check();

					JSONObject credentials = new JSONObject();

					credentials.put("email", email);
					credentials.put("password", pass);

					AsyncTaskRunner runner = new AsyncTaskRunner(
							AsyncTaskRunner.POST_TASK, getApplicationContext(),
							RegisterActivity.this, Constants.ON_REGISTER,
							register);
					runner.dataAsJsonString(credentials.toString());
					runner.execute(Constants.REGISTER_URL);

				} catch (PasswordMismatchException e) {
					e.printStackTrace();
				} catch (InvalidEmailException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			break;
		}
	}

}
