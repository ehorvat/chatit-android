package com.ego.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ego.chatit.R;
import com.ego.interfaces.OnChangePassword;
import com.ego.singleton.User;
import com.ego.tasks.AsyncTaskRunner;
import com.ego.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends Activity implements View.OnClickListener{

    TextView tvFeedback;

    ImageView ivBack;

    EditText oldPassword, newPassword;

    Button bChangePassword;

    String mOld, mNew;

    RadioGroup rgDisplayNames;

    ViewGroup actionBarLayout;

    OnChangePassword ocp = new OnChangePassword() {
        @Override
        public void onChange(String data) {
            try {

                Log.v("Data", data);

                JSONObject j = new JSONObject(data);

                boolean success = j.getBoolean("success");

                if(success){
                    tvFeedback.setVisibility(View.VISIBLE);
                    tvFeedback.setTextColor(Color.parseColor("#00C957"));
                    tvFeedback.setText("Successfully changed password!");
                }else{

                    String error = j.getString("error");

                    tvFeedback.setVisibility(View.VISIBLE);
                    tvFeedback.setTextColor(Color.parseColor("#EE0000"));
                    tvFeedback.setText(error);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Log.v("address", User.getAddress());

        oldPassword = (EditText) findViewById(R.id.etOldPassword);
        newPassword = (EditText) findViewById(R.id.etNewPassword);

        bChangePassword = (Button) findViewById(R.id.bChangePassword);
        bChangePassword.setOnClickListener(this);

        tvFeedback = (TextView) findViewById(R.id.tvFeedback);

        // Inflate custom layout
        actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.action_bar_settings, null);

        ivBack = (ImageView) actionBarLayout.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);

        // Set up your ActionBar
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bChangePassword:

                //Send change password request


                if(!isEmpty(oldPassword) || !isEmpty(newPassword)){

                    // Retrieve the text entered from the EditText
                    mOld = oldPassword.getText().toString();
                    mNew = newPassword.getText().toString();

                    JSONObject credentials = new JSONObject();
                    try {

                        credentials.put("email", User.getEmail().trim());
                        credentials.put("new", mNew);
                        credentials.put("old", mOld);

                        Log.v("credentials", credentials.toString());

                        AsyncTaskRunner runner = new AsyncTaskRunner(
                                AsyncTaskRunner.POST_TASK, getApplicationContext(),
                                SettingsActivity.this, Constants.ON_CHANGE, ocp);
                        runner.dataAsJsonString(credentials.toString());
                        runner.execute(Constants.CHANGE_URL);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


                break;

            case R.id.ivBack:

                Intent i = new Intent(SettingsActivity.this, Home.class);
                startActivity(i);
                overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
                finish();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
    }
}
