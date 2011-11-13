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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class CardsPagerActivity extends FragmentActivity {

	private ViewPager mViewPager;
	private MyFragmentPagerAdapter mMyFragmentPagerAdapter;
	private Random random;
	private List<String> words;
	private int numberOfWords;
	private int wordCount;
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.cards);
		
		ImageButton imageButton = (ImageButton)findViewById(R.id.imageButtonList);
		imageButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				Intent intent = new Intent(v.getContext(), ListActivity.class);
				startActivity(intent);
			}
		});
		
		// Get intent data
		Bundle bundle = getIntent().getExtras();
		int wordsId = bundle.getInt(AppConstants.SELECTED_LIST_ITEM_KEY);
		
		words = new ArrayList<String>(Arrays.asList(getResources().getStringArray(WordSets.wordSets.get(Integer.valueOf(wordsId)))));
		numberOfWords = words.size();
		wordCount = 1;
		
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mMyFragmentPagerAdapter);
		
		random = new Random();
	}

	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {

			String word = null;
			int randomNum = random.nextInt(words.size());

			word = words.get(randomNum);
			words.remove(word);

			return CardFragment.newInstance(word, wordCount++, numberOfWords);
		}

		@Override
		public int getCount() {

			return numberOfWords;
		}
	}
}
