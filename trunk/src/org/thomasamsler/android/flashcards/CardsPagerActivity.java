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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class CardsPagerActivity extends FragmentActivity implements FlashCardExchangeData {

	private ViewPager mViewPager;
	private MyFragmentPagerAdapter mMyFragmentPagerAdapter;
	private Random mRandom;
	private List<String> mWords;
	private Integer[] mRandomWordsIndex;
	private String mCardSetName;
	private List<Integer> mWordsIndex = new ArrayList<Integer>();
	private boolean mMagnify = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.cards);
		
		ImageButton imageButtonList = (ImageButton)findViewById(R.id.imageButtonList);
		imageButtonList.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				finish();
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

				// Get CardFragment and magnify or reduce its font size
				int currentIndex = mViewPager.getCurrentItem();
				CardFragment cardFragment = ((MyFragmentPagerAdapter)mViewPager.getAdapter()).getFragment(currentIndex);
				if(mMagnify) {
					
					cardFragment.onMagnifyFont();
				}
				else {
					
					cardFragment.onReduceFont();
				}
			}
		});
		
		ImageButton imageButtonEdit = (ImageButton)findViewById(R.id.imageButtonEdit);
		imageButtonEdit.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				int currentIndex = mViewPager.getCurrentItem();
				CardFragment cardFragment = ((MyFragmentPagerAdapter)mViewPager.getAdapter()).getFragment(currentIndex);
				cardFragment.onEdit();
			}
		});
		
		// Get intent data
		Bundle bundle = getIntent().getExtras();
		mCardSetName = bundle.getString(AppConstants.CARD_SET_NAME_KEY);
		
		mWords = getWords(mCardSetName);
		
		if(0 == mWords.size()) {
			
			Toast.makeText(getApplicationContext(), R.string.view_cards_emtpy_set_message, Toast.LENGTH_LONG).show();
		}
		
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

				CardFragment cardFragment = ((MyFragmentPagerAdapter)mViewPager.getAdapter()).getFragment(currentIndex);
				
				if(mMagnify) {
					
					cardFragment.onMagnifyFont();
				}
				else {
					
					cardFragment.onReduceFont();
				}
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) { /* Nothing to do here */ }
			
			public void onPageScrollStateChanged(int arg0) { /* Nothing to do here */ }
		});
		
		mRandom = new Random();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.card_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.menu_card_information:
	        showCardInformation();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	public void updateWord(int index, String word) {
		
		/*
		 * First, we update the in memory list of words
		 */
		mWords.set(mRandomWordsIndex[index], word);
		
		/*
		 * Then, we update the file
		 */
		saveWords(mCardSetName, mWords);
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
		catch(FileNotFoundException e) {

			Log.w(AppConstants.LOG_TAG, "FileNotFoundException: Was not able to create default file", e);
		}
	}
	
	private ArrayList<String> getWords(String cardSetName) {

		ArrayList<String> words = new ArrayList<String>();

		try {

			FileInputStream fis =  getApplicationContext().openFileInput(cardSetName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			String word;

			while((word = reader.readLine()) != null) {

				if(null != word && !"".equals(word) && AppConstants.MIN_DATA_LENGTH <= word.length()) {

					words.add(word);
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

		return words;
	}
	
	private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

		private Map<Integer, CardFragment> mPageReferenceMap = new HashMap<Integer, CardFragment>();
		
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
			
			CardFragment cardFragment = CardFragment.newInstance(mWords.get(mRandomWordsIndex[index]), index, mWords.size());
			mPageReferenceMap.put(Integer.valueOf(index), cardFragment);
			
			return cardFragment;
		}

		@Override
		public int getCount() {

			return mWords.size();
		}
		
		@Override
		public void destroyItem(View container, int position, Object object) {
		
			super.destroyItem(container, position, object);
			
			mPageReferenceMap.remove(Integer.valueOf(position));
		}
		
		public CardFragment getFragment(int key) {
			
			return mPageReferenceMap.get(key);
		}
	}
	
	/*
	 * Menu methods
	 */
	
	public void showCardInformation() {
		
		String message = String.format(getResources().getString(R.string.card_information), mCardSetName);
		
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
}
