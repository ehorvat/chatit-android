package com.ego.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ego.chatit.R;
import com.ego.interfaces.OnRecoverPassword;
import com.ego.tasks.AsyncTaskRunner;
import com.ego.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class PasswordRecoveryActivity extends Activity implements android.view.View.OnClickListener{
	
	TextView tvTitle, tvFeedback;

    ImageView ivBackButton;
	
	Button bRecover;
	
	EditText etEmail;

    ViewGroup actionBarLayout;

	OnRecoverPassword orp = new OnRecoverPassword(){

		@Override
		public void onRecover(String data) {

			Log.v("data", data);
			
			JSONObject j;
			try {
				j = new JSONObject(data);
				
				boolean success = j.getBoolean("success");
				
				if(success){
					tvFeedback.setVisibility(View.VISIBLE);
                    tvFeedback.setTextColor(Color.parseColor("#00C957"));
                    tvFeedback.setText("Recovery Email Sent");
				}else{
					
					String error = j.getString("error");
					
					tvFeedback.setVisibility(View.VISIBLE);
                    tvFeedback.setTextColor(Color.parseColor("#EE0000"));
					tvFeedback.setText(error.trim());
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_recover_password);
		

		tvTitle = (TextView) findViewById(R.id.tvTitle);

		tvFeedback = (TextView) findViewById(R.id.tvFeedback);

		bRecover = (Button) findViewById(R.id.bRecoverPassword);
		bRecover.setOnClickListener(this);
		
		etEmail = (EditText) findViewById(R.id.etEmail);

        actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.action_bar_recover, null);

        ivBackButton = (ImageView) actionBarLayout.findViewById(R.id.ivBack);
        ivBackButton.setOnClickListener(this);

        // Set up your ActionBar
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);
		
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

		switch(v.getId()){
		
		case R.id.bRecoverPassword:
			
			tvFeedback.setVisibility(View.GONE);
			

            if(!isEmpty(etEmail)){
                String email = etEmail.getText().toString();

                try {

                    JSONObject data = new JSONObject();

                    data.put("email", email);

                    AsyncTaskRunner runner = new AsyncTaskRunner(AsyncTaskRunner.POST_TASK, getApplicationContext(),
                            PasswordRecoveryActivity.this, Constants.ON_RECOVER, orp);
                    runner.dataAsJsonString(data.toString());
                    runner.execute(Constants.RECOVER_URL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvFeedback.setVisibility(View.VISIBLE);
                        tvFeedback.setTextColor(Color.parseColor("#EE0000"));
                        tvFeedback.setText("Please provide an email.");
                    }
                });
            }


			
			
			break;

        case R.id.ivBack:

            Intent i = new Intent(PasswordRecoveryActivity.this, LoginActivity.class);
            startActivity(i);

            overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);


            break;
		
		}
		
	}
    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }


}
