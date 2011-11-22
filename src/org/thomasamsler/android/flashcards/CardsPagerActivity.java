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
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class CardsPagerActivity extends FragmentActivity {

	private ViewPager mViewPager;
	private MyFragmentPagerAdapter mMyFragmentPagerAdapter;
	private Random mRandom;
	private List<String> mWords;
	private Integer[] mRandomWordsIndex;
	private String mFileName;
	private List<Integer> mWordsIndex = new ArrayList<Integer>();
	private boolean mMagnify = false;
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.cards);
		
		ImageButton imageButtonList = (ImageButton)findViewById(R.id.imageButtonList);
		imageButtonList.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				Intent intent = new Intent(v.getContext(), ListActivity.class);
				startActivity(intent);
			}
		});
		
		/*
		 * When the user taps on the magnify image button, it will increase the 
		 * text size of the words.
		 */
		final ImageButton imageButtonMagnify = (ImageButton)findViewById(R.id.imageButtonMagnify);
		imageButtonMagnify.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {

				// Create a boolean toggle
				mMagnify ^= true;

				// Change button image between magnify and reduce
				if(mMagnify) {
				
					imageButtonMagnify.setImageResource(R.drawable.ic_action_reduce);
				}
				else {
					
					imageButtonMagnify.setImageResource(R.drawable.ic_action_magnify);
				}

				// Get TextView and Magnify or reduce its font size
				int currentIndex = mViewPager.getCurrentItem();
				Integer tag = mRandomWordsIndex[currentIndex];
				((TextView)mViewPager.findViewWithTag(tag).findViewById(R.id.textViewWord)).setTextSize(mMagnify ? AppConstants.LARGE_TEXT_SIZE : AppConstants.NORMAL_TEXT_SIZE);
			}
		});
		
		ImageButton imageButtonEdit = (ImageButton)findViewById(R.id.imageButtonEdit);
		imageButtonEdit.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				int currentIndex = mViewPager.getCurrentItem();
				
				Integer tag = mRandomWordsIndex[currentIndex];
				
				View card = mViewPager.findViewWithTag(tag);
				
				TextView textView = ((TextView)card.findViewById(R.id.textViewWord));
				EditText editText = ((EditText)card.findViewById(R.id.editTextWord));
				((ImageButton)card.findViewById(R.id.imageButtonCancel)).setVisibility(View.VISIBLE);
				((ImageButton)card.findViewById(R.id.imageButtonSave)).setVisibility(View.VISIBLE);
				
				textView.setVisibility(View.INVISIBLE);
				editText.setText(textView.getText());
				editText.setVisibility(View.VISIBLE);
			}
		});
		
		// Get intent data
		Bundle bundle = getIntent().getExtras();
		int fileId = bundle.getInt(AppConstants.SELECTED_LIST_ITEM_KEY);
		String[] files = bundle.getStringArray(AppConstants.FILE_NAMES_KEY);
		
		/*
		 * Save file name so that we can update the file if the user changes words
		 */
		mFileName = files[fileId];
		
		mWords = getWords(mFileName);
		mRandomWordsIndex = new Integer[mWords.size()];
		// Initialize arrays
		for(int i = 0; i < mWords.size(); i++) {
			
			mRandomWordsIndex[i] = Integer.valueOf(-1);
			mWordsIndex.add(Integer.valueOf(i));
		}
		
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mMyFragmentPagerAdapter);
		
		/*
		 * Use page change listener to magnify and reduce the word's font size
		 */
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageSelected(int currentIndex) {

				Integer tag = mRandomWordsIndex[currentIndex];
				((TextView)mViewPager.findViewWithTag(tag).findViewById(R.id.textViewWord)).setTextSize(mMagnify ? AppConstants.LARGE_TEXT_SIZE : AppConstants.NORMAL_TEXT_SIZE);
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) { /* Nothing to do here */ }
			
			public void onPageScrollStateChanged(int arg0) { /* Nothing to do here */ }
		});
		
		mRandom = new Random();
	}
	
	public void updateWord(int index, String word) {
		
		/*
		 * First, we update the in memory list of words
		 */
		mWords.set(index, word);
		
		/*
		 * Then, we update the file
		 */
		saveWords(mFileName, mWords);
	}

	private void saveWords(String fileName, List<String> words) {
		
		/*
		 * First, we delete the exiting file
		 */
		if(!getApplicationContext().deleteFile(fileName)) {
			
			Log.w(AppConstants.LOG_TAG, "Was not able to delete file with name = " + fileName);
			return;
		}
		
		try {

			FileOutputStream fos = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
			PrintStream ps = new PrintStream(fos);
			
			for(String word : words) {

				ps.println(word);
			}
			
			ps.close();
		}
		catch (FileNotFoundException e) {

			Log.w(AppConstants.LOG_TAG, "FileNotFoundException: Was not able to create default file", e);
		}
	}
	
	private ArrayList<String> getWords(String fileName) {
		
		ArrayList<String> words = new ArrayList<String>();
		
		try {
			
			FileInputStream fis =  getApplicationContext().openFileInput(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			String word;
			while((word = reader.readLine()) != null) {
				
				words.add(word);
			}
			
			reader.close();
		}
		catch (FileNotFoundException e) {
			
			Log.w(AppConstants.LOG_TAG, "FileNotFoundException: while reading words from file", e);
		}
		catch (IOException e) {
			
			Log.w(AppConstants.LOG_TAG, "IOException: while reading words from file", e);
		}
		
		return words;
	}
	
	private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {

			int randomNum;
			
			if(0 == mRandomWordsIndex[index].compareTo(Integer.valueOf(-1))) {
				
				randomNum = mRandom.nextInt(mWordsIndex.size());
				mRandomWordsIndex[index] = mWordsIndex.get(randomNum);
				mWordsIndex.remove(randomNum);
			}
			
			CardFragment cardFragment = CardFragment.newInstance(mWords.get(mRandomWordsIndex[index]), (index + 1), mWords.size());
			cardFragment.setTag(mRandomWordsIndex[index]);
			
			return cardFragment;
		}

		@Override
		public int getCount() {

			return mWords.size();
		}
	}
}
