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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class CardsPagerActivity extends FragmentActivity {

	private final int NORMAL_TEXT_SIZE = 80;
	private final int LARGE_TEXT_SIZE = 100;
	
	private ViewPager mViewPager;
	private MyFragmentPagerAdapter mMyFragmentPagerAdapter;
	private Random mRandom;
	private List<String> mWords;
	private int mNumberOfWords;
	private int mWordCount;
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
				
				for(int i = 0; i < mViewPager.getChildCount(); i++) {
					
					try {
						
						((TextView) mViewPager.getChildAt(i).findViewById(R.id.textViewWord)).setTextSize(mMagnify ? LARGE_TEXT_SIZE : NORMAL_TEXT_SIZE);
					}
					catch(Exception e) {
						
						Log.w(AppConstants.LOG_TAG, "WARN: Was not able to set text size", e);
					}
				}
			}
		});
		
		// Get intent data
		Bundle bundle = getIntent().getExtras();
		int wordsId = bundle.getInt(AppConstants.SELECTED_LIST_ITEM_KEY);
		
		mWords = new ArrayList<String>(Arrays.asList(getResources().getStringArray(WordSets.mWordSets.get(Integer.valueOf(wordsId)))));
		mNumberOfWords = mWords.size();
		mWordCount = 1;
		
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mMyFragmentPagerAdapter);
		
		/*
		 * Use page change listener to magnify the words 
		 */
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			public void onPageSelected(int arg0) {
				
				if(mMagnify) {
				
					for(int i = 0; i < mViewPager.getChildCount(); i++) {

						try {
						
							((TextView) mViewPager.getChildAt(i).findViewById(R.id.textViewWord)).setTextSize(LARGE_TEXT_SIZE);
						}
						catch(Exception e) {
							
							Log.w(AppConstants.LOG_TAG, "WARN: Was not able to set text size", e);
						}
					}
				}
			}
			
			public void onPageScrolled(int arg0, float arg1, int arg2) { /* Nothing to do here */ }
			
			public void onPageScrollStateChanged(int arg0) { /* Nothing to do here */ }
		});
		
		mRandom = new Random();
	}

	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {

			String word = null;
			int randomNum = mRandom.nextInt(mWords.size());

			word = mWords.get(randomNum);
			mWords.remove(word);

			return CardFragment.newInstance(word, mWordCount++, mNumberOfWords);
		}

		@Override
		public int getCount() {

			return mNumberOfWords;
		}
	}
}
