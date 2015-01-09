package com.ego.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ego.interfaces.OnChangePassword;
import com.ego.interfaces.OnLogin;
import com.ego.interfaces.OnRecoverPassword;
import com.ego.interfaces.OnRegister;
import com.ego.util.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AsyncTaskRunner extends AsyncTask<String, String, String>{
	// connection timeout, in milliseconds (waiting to connect)
	private static final int CONN_TIMEOUT = 10000;

	// socket timeout, in milliseconds (waiting for data)
	private static final int SOCKET_TIMEOUT = 10000;
	private static final String CONN_TIMEOUT_MSG = "timeout";

	private int taskType = GET_TASK;
	private Context mContext;
	private Activity a;
	public static final int POST_TASK = 1;
	public static final int GET_TASK = 2;
	private final String method;
	Object listener;
	private String data;

	String result = "";


	public AsyncTaskRunner(int taskType, Context mContext, Activity a, String method, Object listener) {

		this.taskType = taskType;
		this.mContext = mContext;
		this.a = a;
		this.method = method;
		this.listener = listener;
	}

	public void dataAsJsonString(String data) {

		this.data = data;
	}
	
	protected String doInBackground(String... urls) {
		String url = urls[0];
		int l = url.length();

		HttpResponse response;
		try {
			response = doResponse(url);

			if (response == null) {

				return result;
			} else {
				if (response.getEntity() != null) {
					
					result = inputStreamToString(response.getEntity()
							.getContent());
					

					}

				}

		} catch (Exception e) {
			result = "error";
			e.printStackTrace();
		}
		return result;
	}

	protected void onPostExecute(String result) {
		
		if(method.equals(Constants.ON_LOGIN)){
			((OnLogin) listener).onLogin(result);
		}
		
		if(method.equals(Constants.ON_REGISTER)){
			((OnRegister) listener).onRegister(result);
		}
		
		if(method.equals(Constants.ON_RECOVER)){
			((OnRecoverPassword) listener).onRecover(result);
		}

        if(method.equals(Constants.ON_CHANGE)){
            ((OnChangePassword) listener).onChange(result);
        }

	}

	@Override
	protected void onPreExecute() {
	
	}

	// Establish connection and socket (data retrieval) timeouts
	private HttpParams getHttpParams() {

		HttpParams htpp = new BasicHttpParams();

		HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
		HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);

		return htpp;
	}

	private HttpResponse doResponse(String url) throws ClientProtocolException,
			IOException, ConnectTimeoutException {
		// Use our connection and data timeouts as parameters for our
		// DefaultHttpClient
		Log.v("DO RESPONSE", url);
		HttpClient httpclient = new DefaultHttpClient(getHttpParams());
		HttpPost httppost = null;
		StringEntity entity;
		HttpResponse response = null;

		switch (taskType) {

		case POST_TASK:
			Log.v("HERE", "HERE");
			httppost = new HttpPost(url);
			// Add parameters
			entity = new StringEntity(data);
			httppost.setHeader("Content-type", "application/json");
			httppost.setEntity(entity);
			response = httpclient.execute(httppost);
			break;

		case GET_TASK:
			HttpGet httpget = new HttpGet(url);
			response = httpclient.execute(httpget);
			break;
		}

		return response;
	}

	private String inputStreamToString(InputStream is) {

		String line = "";
		StringBuilder total = new StringBuilder();

		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		try {
			// Read response until the end
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			Log.e("ASYNC CLASS", e.getLocalizedMessage(), e);
		}

		// Return full string
		return total.toString();
	}

	
}
