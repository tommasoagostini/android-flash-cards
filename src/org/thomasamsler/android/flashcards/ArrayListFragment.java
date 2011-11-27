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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class ArrayListFragment extends ListFragment {

	private static final int MENU_ITEM_ADD = 1;
	private static final int MENU_ITEM_DELETE = 2;
	
	private ArrayList<String> mFileNames;
	private ArrayAdapter<String> mArrayAdapter;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		registerForContextMenu(getListView());
		
		mFileNames = new ArrayList<String>(Arrays.asList(getActivity().getApplicationContext().fileList()));
		
		if(0 == mFileNames.size()) {
			
			createDefaultFiles();
			mFileNames = new ArrayList<String>(Arrays.asList(getActivity().getApplicationContext().fileList()));
		}

		Collections.sort(mFileNames);

		mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mFileNames);

		setListAdapter(mArrayAdapter);
		
		ImageButton imageButtonNewCardSet = (ImageButton)getActivity().findViewById(R.id.imageButtonNewCardSet);
		imageButtonNewCardSet.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
				dialog.setTitle(R.string.new_card_set_title);
				
				final EditText input = new EditText(v.getContext());
				input.setPadding(10, 0, 10, 0);
				dialog.setView(input);
				dialog.setPositiveButton(R.string.new_card_set_save_button, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						
						String newFileName = input.getText().toString().trim();
						
						if(null == newFileName || "".equals(newFileName)) {
							
							Toast.makeText(getActivity().getApplicationContext(), R.string.new_card_set_dialog_message_warning2, Toast.LENGTH_LONG).show();
						}
						else {
							
							String [] fileNames = getActivity().getApplicationContext().fileList();
							boolean fileNameExists = false;
							
							for(String fileName : fileNames) {
								
								if(newFileName.equals(fileName)) {
									
									fileNameExists = true;
									break;
								}
							}
							
							if(fileNameExists) {
								
								Toast.makeText(getActivity().getApplicationContext(), R.string.new_card_set_dialog_message_warning1, Toast.LENGTH_LONG).show();
							}
							else {
								
								try {
									
									FileOutputStream fos = getActivity().getApplicationContext().openFileOutput(newFileName, Context.MODE_PRIVATE);
									PrintStream ps = new PrintStream(fos);
									ps.close();
									
								} catch (FileNotFoundException e) {
									
									Log.w(AppConstants.LOG_TAG, "FileNotFoundException: Was not able to create new file", e);
								}
								
								mFileNames.add(newFileName);
								mArrayAdapter.notifyDataSetChanged();
							}
						}
					}
				});
				
				dialog.setNegativeButton(R.string.new_card_set_cancel_button, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) { /* Cancel: nothing to do */ }
				});
				
				dialog.show();
			}
		});
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		Intent intent = new Intent(v.getContext(), CardsPagerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(AppConstants.FILE_NAME_KEY, mFileNames.get(position));
		intent.putExtras(bundle);
		startActivity(intent);
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

	private void addCard(int listItemPosition) {
		
		Intent intent = new Intent(getActivity().getApplicationContext(), AddCardActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(AppConstants.FILE_NAME_KEY, mFileNames.get(listItemPosition));
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	private void deleteCardSet(int listItemPosition) {
		
		boolean isDeleted = getActivity().getApplicationContext().deleteFile(mFileNames.get(listItemPosition));
		
		if(isDeleted) {
			
			mFileNames.remove(listItemPosition);
			mArrayAdapter.notifyDataSetChanged();
		}
		else {

			Log.w(AppConstants.LOG_TAG, "Was not able to delete card set");
		}
	}
	
	private void createDefaultFiles() {

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
			catch (FileNotFoundException e) {

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
}
