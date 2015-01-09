package com.ego.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ego.adapters.BaseInflaterAdapter;
import com.ego.adapters.MessageInflater;
import com.ego.chatit.R;
import com.ego.model.Message;
import com.ego.singleton.User;
import com.ego.util.Constants;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Home extends Activity implements OnClickListener {

	Button bSend;

	EditText etMessage;

	ListView lvMessages;
	
	TextView tvOccupancy, tvLogout;

    ImageView ivChangeName, ivSettings;

	BaseInflaterAdapter<Message> adapter;
		
	Pubnub pubnub;
	
	Callback callback;
	
	Message msg;

	ViewGroup actionBarLayout;
	
	Typeface noteworthy;
	
	int chosen = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

        Log.v("create","create");

		setContentView(R.layout.layout_home);

		noteworthy = Typeface.createFromAsset(getAssets(),"fonts/noteworthy.ttf");

        Log.d("subscribed?", User.isSubscribed()+"");
		
		bSend = (Button) findViewById(R.id.bSend);
		bSend.setOnClickListener(this);
		
		etMessage = (EditText) findViewById(R.id.etMessage);
        etMessage.setHint(User.getDisplayName() + "@" + User.getAddress() + Constants.HINT_ENDING);
		
		lvMessages = (ListView) findViewById(R.id.lvMessages);
		adapter = new BaseInflaterAdapter<Message>(new MessageInflater(getApplicationContext()));
		lvMessages.setAdapter(adapter);


		
		// Inflate your custom layout
		actionBarLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.action_bar_home, null);
		
		ivChangeName = (ImageView) actionBarLayout.findViewById(R.id.ivChangeName);
		ivChangeName.setOnClickListener(this);

        ivSettings = (ImageView) actionBarLayout.findViewById(R.id.ivSettings);
        ivSettings.setOnClickListener(this);
		
		tvOccupancy = (TextView) actionBarLayout.findViewById(R.id.tvOccupancy);

        tvLogout = (TextView) actionBarLayout.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(this);

		// Set up your ActionBar
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(actionBarLayout);

		final int actionBarColor = getResources().getColor(R.color.white);
		actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));
				
		pubnub = new Pubnub("pub-c-6cbb5ce6-ee72-4cd1-80aa-6cc3ca1d97d3", "sub-c-3bb42a44-8527-11e4-b769-02ee2ddab7fe", "sec-c-NzA0M2YxOWEtNjhkNy00NDA5LWE3MmUtYWY5MTQzNjRiMWE5");

        Log.v("subscribed", User.isSubscribed()+"");

        if(!User.isSubscribed()){
            User.setIsSubscribed(true);
            try {
                pubnub.subscribe(User.getAddress(), new Callback(){

                    @Override
                    public void connectCallback(String arg0, Object arg1) {
                        Log.v("SUBSCRIBED1: ", arg0 + " " + arg1.toString());
                        User.setIsSubscribed(true);
                        Log.v("User subscribed", "deehht");
                    }

                    @Override
                    public void disconnectCallback(String arg0, Object arg1) {
                        Log.v("SUBSCRIBED2: ", arg0 + " " + arg1.toString());
                    }

                    @Override
                    public void reconnectCallback(String arg0, Object arg1) {
                        Log.v("SUBSCRIBED3: ", arg0 + " " + arg1.toString());
                    }

                    @Override
                    public void successCallback(String arg0, Object arg1) {

                        try {
                            JSONObject j = new JSONObject(arg1.toString());


                            final Message msg = new Message(j.getString("name"), j.getString("message"), generateTstamp(System.currentTimeMillis(), false));

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    adapter.addItem(0, msg, true);
                                }
                            });

                            Log.v("SUBSCRIBED4: ", arg0 + " " + j.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                });


            } catch (PubnubException e1) {
                e1.printStackTrace();
            }

        }





		pubnub.history(User.getAddress(), true, 100,  new Callback(){

			@Override
			public void connectCallback(String arg0, Object arg1) {
				super.connectCallback(arg0, arg1);
			}

			@Override
			public void disconnectCallback(String arg0, Object arg1) {
				super.disconnectCallback(arg0, arg1);
			}

			@Override
			public void errorCallback(String arg0, PubnubError arg1) {
				super.errorCallback(arg0, arg1);
			}

			@Override
			public void reconnectCallback(String arg0, Object arg1) {
				super.reconnectCallback(arg0, arg1);
			}

			@Override
			public void successCallback(String channel, Object message) {

				JSONArray obj;

				Log.v("message", message.toString());

				try {

					obj = new JSONArray(message.toString());

					JSONArray innerArray = obj.getJSONArray(0);

					final List<Message> messages = new ArrayList<Message>();

					for(int i=innerArray.length()-1; i>=0; i--){

						JSONObject j = innerArray.getJSONObject(i);

						Long timeStamp = j.getLong("timetoken");

						JSONObject j2 = j.getJSONObject("message");

						if(j2.has("name")){

							final Message msg = new Message(j2.getString("name"), j2.getString("message"), generateTstamp(timeStamp, true));

							messages.add(msg);
						}
					}

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							adapter.addItems(messages,true);
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}			}
			
		});
		
		callback = new Callback(){

			@Override
			public void connectCallback(String arg0, Object arg1) {
				super.connectCallback(arg0, arg1);
			}

			@Override
			public void disconnectCallback(String arg0, Object arg1) {
				super.disconnectCallback(arg0, arg1);
			}

			@Override
			public void reconnectCallback(String arg0, Object arg1) {
				super.reconnectCallback(arg0, arg1);
			}

			@Override
			public void successCallback(String channel, Object message) {

			}

			@Override
			public void errorCallback(String arg0, PubnubError arg1) {
				Log.v("HERE", arg0 + " " + arg1.toString());
			}
			
			
		};
		


		Callback cb = new Callback() {

		public void successCallback(String channel, Object response) {

			try {
				
				JSONObject presence = new JSONObject(response.toString());
				final int num = presence.getInt("occupancy");
				
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						tvOccupancy.setText(String.valueOf(num));
					}
					
				});
				

				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		
		}
		public void errorCallback(String channel, PubnubError error) {
			Log.d("PUBNUB",error.toString());
		}
		@Override
		public void connectCallback(String arg0, Object response) {

			Log.d("PUBNUB1",response.toString());
		}
		@Override
		public void disconnectCallback(String arg0, Object response) {
			Log.d("PUBNUB2",response.toString());

		}
		@Override
		public void reconnectCallback(String arg0, Object response) {
			Log.d("PUBNUB3",response.toString());
		}
		
		
	};
		try {
			pubnub.presence(User.getAddress(), cb);
		} catch (PubnubException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	@Override
	protected void onStart() {
		super.onStart(); Log.v("start", "start");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
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
        Log.v("OnDestroy", "Dead");
		pubnub.unsubscribe(User.getAddress());
	}

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("resume", "resume");
    }

    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.bSend:
				
			try {
				
				if(!isEmpty(etMessage)){
					
					String newMsg = etMessage.getText().toString();
					etMessage.setText("");
					
					msg = new Message(User.getUsername(), newMsg);
					
					JSONObject jsonMsg = new JSONObject();
					jsonMsg.put("name", User.getDisplayName());
					jsonMsg.put("message", newMsg);
					
					jsonMsg.toString().replace("\"","'");
								
					pubnub.publish(User.getAddress(), jsonMsg , callback);
				}			
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;
			
		case R.id.ivChangeName:
			
			showDialog(null);
			
			break;

        case R.id.ivSettings:

            Intent i = new Intent(Home.this, SettingsActivity.class);
            startActivity(i);


            overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);

            break;

        case R.id.tvLogout:

            showExitDialog(null);

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
    public void onBackPressed(){

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final AlertDialog alert = builder.create();
        builder.setTitle("Are you sure you want to exit?");
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                pubnub.unsubscribe(User.getAddress());
                finish();
                System.exit(0);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.cancel();
            }
        });
        builder.show();

    }
	
	private String generateTstamp(long time, boolean pn){
		
		String formattedDate = null;
		
		Date date = null;
		
		long t = time;
		
		//If a pubnub timestamp, divide by 10,000
		if(pn){			
			t = time/10000;
		}
		
		date = new Date(t);
		
		SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a");
		formattedDate = sdf.format(date);



        return formattedDate;
	}
	
	 public void showDialog(View v){
		 
		 	
		 	
	    	final CharSequence[] usernames={Constants.NAME_ANONYMOUS, User.getUsername()};
	    	AlertDialog.Builder builder=new AlertDialog.Builder(this);
	    	builder.setTitle("Post as...");
	    	builder.setPositiveButton("Set Username", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(Constants.NAME_ANONYMOUS.trim().equals(usernames[chosen])){
						User.setDisplayName(Constants.NAME_ANONYMOUS);
                        etMessage.setHint(Constants.NAME_ANONYMOUS + "@" + User.getAddress() + Constants.HINT_ENDING);
					}
					else if(User.getUsername().trim().equals(usernames[chosen])){
						User.setDisplayName(User.getUsername());
                        etMessage.setHint(User.getUsername() + "@" + User.getAddress() + Constants.HINT_ENDING);
                    }
				}
			});
	    	
	     	builder.setSingleChoiceItems(usernames,chosen, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {	
					Log.v("chosen", chosen+"");
					chosen = which;	
				}
			});
	    	builder.show();
	    }

    public void showExitDialog(View v){


        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        final AlertDialog alert = builder.create();
        builder.setTitle("Are you sure you want to exit?");
        builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                pubnub.unsubscribe(User.getAddress());
                finish();
                System.exit(0);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.cancel();
            }
        });
        builder.show();
    }

}
