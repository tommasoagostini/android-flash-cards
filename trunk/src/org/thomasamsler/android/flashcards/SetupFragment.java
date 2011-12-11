/*
 * Copyright 2011 Thomas Amsler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package org.thomasamsler.android.flashcards;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SetupFragment extends Fragment implements FlashCardExchangeData {

	private EditText mEditTextUserName;
	private String mPreferenceUserName;
	private ProgressBar mProgressBar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.setup_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		SharedPreferences preferences = getActivity().getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
		mPreferenceUserName = preferences.getString(AppConstants.PREFERENCE_FCEX_USER_NAME, "");
		
		mEditTextUserName = (EditText)getActivity().findViewById(R.id.editTextSetupUserName);
		mEditTextUserName.setText(mPreferenceUserName);
		
		mProgressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar1);
		
		ImageButton imageButtonSave = (ImageButton)getActivity().findViewById(R.id.imageButtonSetupSave);
		imageButtonSave.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				mProgressBar.setVisibility(ProgressBar.VISIBLE);
				
				String userName = mEditTextUserName.getText().toString();
				
				if(null != userName && !"".equals(userName) && !mPreferenceUserName.equals(userName)) {
					
					if(hasConnectivity()) {
					
						new GetExternalCardSetsTask().execute(userName);
					}
					else {
						
						mProgressBar.setVisibility(ProgressBar.GONE);
						Toast.makeText(getActivity().getApplicationContext(), R.string.util_connectivity_error, Toast.LENGTH_SHORT).show();
					}
				}
				else {
					
					mProgressBar.setVisibility(ProgressBar.GONE);
				}
			}
		});
		
		ImageButton imageButtonCancel = (ImageButton)getActivity().findViewById(R.id.imageButtonSetupCancel);
		imageButtonCancel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				((ListActivity)getActivity()).showArrayListFragment(true);
			}
		});
	}
	
	
	/*
     * Helper method to check if there is network connectivity
     */
	private boolean hasConnectivity() {
		
		return ((ListActivity)getActivity()).hasConnectivity();
	}
	
	private class GetExternalCardSetsTask extends AsyncTask<String, Void, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... params) {
			
			String userName = params[0].trim();
			
			StringBuilder uriBuilder = new StringBuilder();
			uriBuilder.append(API_GET_USER).append(userName).append(API_KEY);
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(uriBuilder.toString());
			HttpResponse response;

			JSONObject jsonObject = null;
			
			try {
				
				response = httpclient.execute(httpget);
				HttpEntity entity = response.getEntity();

				if (entity != null) {

					InputStream inputStream = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
					StringBuilder content = new StringBuilder();

					String line = null;

					try {

						while((line = reader.readLine()) != null) {

							content.append(line);
						}
					}
					catch(IOException e) {

						Log.e(AppConstants.LOG_TAG, "IOException", e);
					}
					finally {

						try {

							reader.close();

						}
						catch(IOException e) {

							Log.e(AppConstants.LOG_TAG, "IOException", e);
						}
					}

					jsonObject = new JSONObject(content.toString());
					jsonObject.put(FIELD_FC_ARG, userName);
				}
			}
			catch(ClientProtocolException e) {

				Log.e(AppConstants.LOG_TAG, "ClientProtocolException", e);
			}
			catch(IOException e) {

				Log.e(AppConstants.LOG_TAG, "IOException", e);
			}
			catch(Exception e) {

				Log.e(AppConstants.LOG_TAG, "General Exception", e);
			}

			return jsonObject;
		}

		@Override
		protected void onPostExecute(JSONObject jsonObject) {

			mProgressBar.setVisibility(ProgressBar.GONE);

			try {
				
				String responseType = jsonObject.getString(FIELD_RESPONSE_TYPE);
				
				if(null != responseType && RESPONSE_OK.equals(responseType)) {
					
					Toast.makeText(getActivity().getApplicationContext(), R.string.setup_save_user_name_success, Toast.LENGTH_SHORT).show();
					SharedPreferences settings = getActivity().getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(AppConstants.PREFERENCE_FCEX_USER_NAME, jsonObject.getString(FIELD_FC_ARG));
                    editor.commit();
                    
                    ((ListActivity)getActivity()).showArrayListFragment(true);
				}
				else if(null != responseType && RESPONSE_ERROR.equals(responseType)) {
					
					Toast.makeText(getActivity().getApplicationContext(), R.string.setup_save_user_name_error, Toast.LENGTH_LONG).show();
				}
				else {
					
					Toast.makeText(getActivity().getApplicationContext(), R.string.setup_save_user_name_failure, Toast.LENGTH_LONG).show();
				}
			}
			catch(JSONException e) {

				Log.e(AppConstants.LOG_TAG, "JSONException", e);
			}
		}
	}
}
