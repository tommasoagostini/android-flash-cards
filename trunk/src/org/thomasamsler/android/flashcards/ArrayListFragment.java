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
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ArrayListFragment extends ListFragment {

	private String[] mFileNames;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mFileNames = getActivity().getApplicationContext().fileList();
		
		if(0 == mFileNames.length) {
			
			createDefaultFiles();
		}

		mFileNames = getActivity().getApplicationContext().fileList();
		
		Arrays.sort(mFileNames);

		setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mFileNames));
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		Intent intent = new Intent(v.getContext(), CardsPagerActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(AppConstants.SELECTED_LIST_ITEM_KEY, position);
		bundle.putStringArray(AppConstants.FILE_NAMES_KEY, mFileNames);
		intent.putExtras(bundle);
		startActivity(intent);
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
