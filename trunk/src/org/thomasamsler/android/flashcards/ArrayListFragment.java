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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ArrayListFragment extends ListFragment implements FlashCardExchangeData {

	private static final int MENU_ITEM_ADD = 1;
	private static final int MENU_ITEM_DELETE = 2;
	
	private ArrayList<CardSet> mCardSets;
	private ArrayAdapter<CardSet> mArrayAdapter;
	
	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		registerForContextMenu(getListView());
		
		mCardSets = getCardSetItems();
		
		if(0 == mCardSets.size()) {
			
			createDefaultCardSets();
			mCardSets = getCardSetItems();
		}

		Collections.sort(mCardSets);

		mArrayAdapter = new ArrayAdapter<CardSet>(getActivity(), android.R.layout.simple_list_item_1, mCardSets);

		setListAdapter(mArrayAdapter);
	
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		
		CardSet cardSet = mCardSets.get(position);

		if((null == cardSet.getId() || "".equals(cardSet.getId())) && !hasCards(cardSet.getName())) {
		
			Toast.makeText(getActivity().getApplicationContext(), R.string.view_cards_emtpy_set_message, Toast.LENGTH_SHORT).show();
			return;
		}
		
		Intent intent = new Intent(v.getContext(), CardsPagerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(AppConstants.CARD_SET_NAME_KEY, mCardSets.get(position).getName());
		intent.putExtras(bundle);
		
		if(null != cardSet.getId() && !"".equals(cardSet.getId())) {

			ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar1);
			progressBar.setVisibility(ProgressBar.VISIBLE);
			
			cardSet.setIntent(intent);
			cardSet.setShowFragment(CardSet.CARDS_PAGER_FRAGMENT);
			new GetExternalCardsTask().execute(cardSet);
		}
		else {

			startActivity(intent);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {

		menu.add(MENU_ITEM_ADD, MENU_ITEM_ADD, 1, R.string.list_menu_add);
		menu.add(MENU_ITEM_DELETE, MENU_ITEM_DELETE, 2, R.string.list_meanu_delete);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
			
		AdapterView.AdapterContextMenuInfo info =  (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int listItemPosition = (int) getListAdapter().getItemId(info.position);
		
		switch(item.getGroupId()) {

		case MENU_ITEM_ADD:
			addCard(listItemPosition);
			break;
		case MENU_ITEM_DELETE:
			deleteCardSet(listItemPosition);
			break;
		default:
			Log.w(AppConstants.LOG_TAG, "List context menu selection not recognized.");
		}

		return false;
	}

	protected void addCardSet(CardSet cardSet) {
		
		mCardSets.add(cardSet);
		Collections.sort(mCardSets);
		mArrayAdapter.notifyDataSetChanged();
	}
	
	protected void getFlashCardExchangeCardSets() {
		
		ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar1);
		progressBar.setVisibility(ProgressBar.VISIBLE);
		
		SharedPreferences preferences = getActivity().getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE);
		String userName = preferences.getString(AppConstants.PREFERENCE_FCEX_USER_NAME, "");
		
		if(null != userName && !"".equals(userName)) {
		
			new GetExternalCardSetsTask().execute(userName);
		}
		else {
			
			Toast.makeText(getActivity().getApplicationContext(), R.string.setup_no_user_name_defined, Toast.LENGTH_SHORT).show();
			((ListActivity)getActivity()).showSetupFragment();
		}
	}
	
	private boolean hasCards(String cardSetName) {

		boolean hasCards = false;
		
		try {

			FileInputStream fis =  getActivity().getApplicationContext().openFileInput(cardSetName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			String word;

			while((word = reader.readLine()) != null) {

				if(null != word && !"".equals(word) && AppConstants.MIN_DATA_LENGTH <= word.length()) {

					hasCards = true;
					break;
				}
			}

			reader.close();
		}
		catch(FileNotFoundException e) {

			Log.w(AppConstants.LOG_TAG, "FileNotFoundException: while reading words from file", e);
		}
		catch(IOException e) {

			Log.w(AppConstants.LOG_TAG, "IOException: while reading words from file", e);
		}

		return hasCards;
	}
	
	private void addCard(int listItemPosition) {
		
		CardSet cardSet = mCardSets.get(listItemPosition);
		
		if(null != cardSet.getId() && !"".equals(cardSet.getId())) {
			
			ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar1);
			progressBar.setVisibility(ProgressBar.VISIBLE);
			
			cardSet.setShowFragment(CardSet.ADD_CARD_FRAGMENT);
			new GetExternalCardsTask().execute(cardSet);
		}
		else {

			((ListActivity)getActivity()).showAddCardFragment(cardSet);
		}
	}
	
	private void deleteCardSet(final int listItemPosition) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(R.string.delete_card_set_dialog_message);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.delete_card_set_dialog_ok, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				// Check if we have the card set stored in the file system
				String[] fileNames = getActivity().getApplicationContext().fileList();
				
				for(String fileName : fileNames) {
					
					if(fileName.equals(mCardSets.get(listItemPosition).getName())) {
						
						boolean isDeleted = getActivity().getApplicationContext().deleteFile(mCardSets.get(listItemPosition).getName());
						
						if(!isDeleted) {
							
							Log.w(AppConstants.LOG_TAG, "Was not able to delete card set");
						}
						
						break;
					}
				}
				
				mCardSets.remove(listItemPosition);
				Collections.sort(mCardSets);
				mArrayAdapter.notifyDataSetChanged();
			}
		});
		
		builder.setNegativeButton(R.string.delete_card_set_dialog_cancel, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) { 
				
				dialog.cancel();
			}
		});
		
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void createDefaultCardSets() {

		// Get a list of files if there are any
		String[] files = getActivity().getApplicationContext().fileList();

		if(0 == files.length) {

			// Get the words from resources
			List<String> words = new ArrayList<String>(Arrays.asList(getResources().getStringArray(WordSets.mWordSets.get(Integer.valueOf(0)))));
			FileOutputStream fos = null;
			PrintStream ps = null;
			
			try {

				fos = getActivity().getApplicationContext().openFileOutput(WordSets.mWordSetNames.get(0), Context.MODE_PRIVATE);
				ps = new PrintStream(fos);
				
				for(String word : words) {

					ps.println(word);
				}
				
				ps.close();
			}
			catch(FileNotFoundException e) {

				Log.w(AppConstants.LOG_TAG, "FileNotFoundException: Was not able to create default file", e);
			}
			
			words = new ArrayList<String>(Arrays.asList(getResources().getStringArray(WordSets.mWordSets.get(Integer.valueOf(1)))));
			
			try {

				fos = getActivity().getApplicationContext().openFileOutput(WordSets.mWordSetNames.get(1), Context.MODE_PRIVATE);
				ps = new PrintStream(fos);
				
				for(String word : words) {

					ps.println(word);
				}
				
				ps.close();
			}
			catch (FileNotFoundException e) {

				Log.w(AppConstants.LOG_TAG, "FileNotFoundException: Was not able to create default file", e);
			}
		}
	}
	
	/*
	 * Helper method:
	 * Taking an array of file names, and turn them into a list of ListItem
	 */
	private ArrayList<CardSet> getCardSetItems() {
		
		ArrayList<CardSet> fileNameList = new ArrayList<CardSet>() ;
		String[] fileNames = getActivity().getApplicationContext().fileList();
		
		for(String fileName : fileNames) {
			
			fileNameList.add(new CardSet(fileName));
		}
		
		return fileNameList;
	}
	
	private class GetExternalCardSetsTask extends AsyncTask<String, Void, List<CardSet>> {

		@Override
		protected List<CardSet> doInBackground(String... params) {
			
			String userName = params[0].trim();
			
			StringBuilder uriBuilder = new StringBuilder();
			uriBuilder.append(API_GET_USER).append(userName).append(API_KEY);
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(uriBuilder.toString());
			HttpResponse response;
			List<CardSet> cardSets = null;

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

					JSONObject jsonObject = new JSONObject(content.toString());
					JSONArray jsonArray = jsonObject.getJSONObject(FIELD_RESULT).getJSONArray(FILED_SETS);

					cardSets = new ArrayList<CardSet>();

					for(int i = 0; i < jsonArray.length(); i++) {

						JSONObject data = jsonArray.getJSONObject(i);
						cardSets.add(new CardSet(data.getString(FIELD_CARD_SET_ID), data.getString(FIELD_TITLE)));
					}
				}
			}
			catch(ClientProtocolException e) {

				Log.e(AppConstants.LOG_TAG, "ClientProtocolException", e);
			}
			catch(IOException e) {

				Log.e(AppConstants.LOG_TAG, "IOException", e);
			}
			catch(Exception e) {

				Log.i(AppConstants.LOG_TAG, "General Exception", e);
			}

			return cardSets;
		}

		@Override
		protected void onPostExecute(List<CardSet> cardSets) {
			
			if(null != cardSets) {
				
				ArrayList<String> existingCardSetNames = new ArrayList<String>(Arrays.asList(getActivity().getApplicationContext().fileList()));
				
				for(CardSet cardSet : cardSets) {
					
					if(!existingCardSetNames.contains(cardSet.getName())) {
						
						mCardSets.add(cardSet);
					}
				}

				Collections.sort(mCardSets);
				mArrayAdapter.notifyDataSetChanged();
			}

			ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar1);
			progressBar.setVisibility(ProgressBar.GONE);
		}
	}
	
	private class GetExternalCardsTask extends AsyncTask<CardSet, Void, CardSet> {

		@Override
		protected CardSet doInBackground(CardSet... cardSets) {
			
			StringBuilder uriBuilder = new StringBuilder();
			uriBuilder.append(API_GET_CARD_SET).append(cardSets[0].getId()).append(API_KEY);
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(uriBuilder.toString());
			HttpResponse response;

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

					JSONObject jsonObject = new JSONObject(content.toString());
					JSONArray jsonArray = jsonObject.getJSONObject(FIELD_RESULT).getJSONArray(FIELD_FLASHCARDS);
					FileOutputStream fos = null;
					PrintStream ps = null;

					try {
					
						fos = getActivity().getApplicationContext().openFileOutput(cardSets[0].getName(), Context.MODE_PRIVATE);
					}
					catch(FileNotFoundException e) {

						Log.w(AppConstants.LOG_TAG, "FileNotFoundException: Was not able to create default file", e);
					}
					
					ps = new PrintStream(fos);

					for(int i = 0; i < jsonArray.length(); i++) {
						
						JSONObject data = jsonArray.getJSONObject(i);
						ps.print(data.getString(FIELD_QUESTION));
						ps.print(AppConstants.WORD_DELIMITER_TOKEN);
						ps.println(data.getString(FIELD_ANSWER));
					}
					
					ps.close();
				}
			}
			catch(ClientProtocolException e) {

				Log.e(AppConstants.LOG_TAG, "ClientProtocolException", e);
			}
			catch(IOException e) {

				Log.e(AppConstants.LOG_TAG, "IOException", e);
			}
			catch(Exception e) {

				Log.i(AppConstants.LOG_TAG, "General Exception", e);
			}

			return cardSets[0];
		}
		
		@Override
		protected void onPostExecute(CardSet cardSet) {
			
			ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.progressBar1);
			progressBar.setVisibility(ProgressBar.GONE);
			
			// Now that we have the cards, we indicate that we don't need to get them anymore
			cardSet.setId("");
			
			switch(cardSet.getShowFragment()) {
			
			case CardSet.ADD_CARD_FRAGMENT:
				((ListActivity)getActivity()).showAddCardFragment(cardSet);
				return;
			}
			
			startActivity(cardSet.getIntent());
		}
	}
}
