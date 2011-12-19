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
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class CardsPagerActivity extends FragmentActivity implements FlashCardExchangeData {

	private static final Integer NEG_ONE = Integer.valueOf(-1);
	
	private ViewPager mViewPager;
	private MyFragmentPagerAdapter mMyFragmentPagerAdapter;
	private Random mRandom;
	private List<String> mCards;
	private List<Integer> mRandomCardPositionList;
	private List<Integer> mAvailableCardPositionList;
	private String mCardSetName;
	private boolean mMagnify = false;
	private int mNumberOfCards;
	
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
				CardFragment cardFragment = mMyFragmentPagerAdapter.getFragment(currentIndex);
				
				if(null == cardFragment) {
					
					return;
				}
				
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
				CardFragment cardFragment = mMyFragmentPagerAdapter.getFragment(currentIndex);
				
				if(null != cardFragment) {
					
					cardFragment.onEdit();
				}
			}
		});
		
		// Get intent data
		Bundle bundle = getIntent().getExtras();
		mCardSetName = bundle.getString(AppConstants.CARD_SET_NAME_KEY);
		
		mCards = getCards(mCardSetName);
		
		if(0 == mCards.size()) {
			
			Toast.makeText(getApplicationContext(), R.string.view_cards_emtpy_set_message, Toast.LENGTH_SHORT).show();
		}
		
		mNumberOfCards = mCards.size();
		mRandomCardPositionList = new ArrayList<Integer>();
		mAvailableCardPositionList = new ArrayList<Integer>();
		
		// Initialize arrays
		for(int i = 0; i < mNumberOfCards; i++) {
			
			mRandomCardPositionList.add(NEG_ONE);
			mAvailableCardPositionList.add(Integer.valueOf(i));
		}
		
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mMyFragmentPagerAdapter);
		
		/*
		 * Use page change listener to magnify and reduce the word's font size
		 */
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageSelected(int currentIndex) {

				CardFragment cardFragment = mMyFragmentPagerAdapter.getFragment(currentIndex);
				
				if(null == cardFragment) {
					
					return;
				}
				
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

		switch (item.getItemId()) {
	    case R.id.menu_card_information:
	        showCardInformation();
	        return true;
	    case R.id.menu_card_delete:
	    	deleteCard();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	public void updateCard(int index, String card) {
		
		/*
		 * First, we update the in memory list of words
		 */
		mCards.set(mRandomCardPositionList.get(index), card);
		
		/*
		 * Then, we update the file
		 */
		saveCards(mCardSetName, mCards);
	}

	private void saveCards(String fileName, List<String> cards) {
		
		/*
		 * First, we delete the exiting file
		 */
		if(!getApplicationContext().deleteFile(fileName)) {
			
			Log.w(AppConstants.LOG_TAG, "Was not able to delete file with name = " + fileName);
			return;
		}
		
		FileOutputStream fos;
		PrintStream ps = null;
		
		try {

			fos = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
			ps = new PrintStream(fos);
			
			for(String card : cards) {

				if(null != card && !"".equals(card)) {
					
					ps.println(card);
				}
			}
		}
		catch(FileNotFoundException e) {

			Log.w(AppConstants.LOG_TAG, "FileNotFoundException: Was not able to create default file", e);
		}
		finally {
			
			ps.close();
		}
	}
	
	private ArrayList<String> getCards(String cardSetName) {

		ArrayList<String> cards = new ArrayList<String>();

		FileInputStream fis;
		BufferedReader reader = null;
		
		try {

			fis =  getApplicationContext().openFileInput(cardSetName);
			reader = new BufferedReader(new InputStreamReader(fis));
			String card;

			while((card = reader.readLine()) != null) {

				if(null != card && !"".equals(card) && AppConstants.MIN_DATA_LENGTH <= card.length()) {

					cards.add(card);
				}
			}
		}
		catch(FileNotFoundException e) {

			Log.w(AppConstants.LOG_TAG, "FileNotFoundException: while reading words from file", e);
		}
		catch(IOException e) {

			Log.w(AppConstants.LOG_TAG, "IOException: while reading words from file", e);
		}
		finally {
			
			try {
				
				reader.close();
			}
			catch (IOException e) {
				
				Log.e(AppConstants.LOG_TAG, "IOException", e);
			}
		}

		return cards;
	}
	
	/*
	 * Menu method
	 */
	private void showCardInformation() {
		
		String message = String.format(getResources().getString(R.string.card_information), mCardSetName);
		
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
	
	/*
	 * Menu method
	 */
	private void deleteCard() {
		
		// Get the current card index
		int currentIndex = mViewPager.getCurrentItem();
		
		// Reduce the card counter by one
		mNumberOfCards -=1;
		
		// Mark card as deleted. The saveCards(...) method ignores null or empty string cards
		mCards.set(mRandomCardPositionList.get(currentIndex), null);
		
		// Save cards
		saveCards(mCardSetName, mCards);
		
		// Remove the deleted card position
		mRandomCardPositionList.remove(currentIndex);

		/*
		 * Determine all remaining random card positions
		 */
		int randomNum;
		
		if(mAvailableCardPositionList.size() > 0) {
		
			for(int i = 0; i < mRandomCardPositionList.size(); i++) {

				if(NEG_ONE.compareTo(mRandomCardPositionList.get(i)) == 0 && mAvailableCardPositionList.size() > 0) {

					randomNum = mRandom.nextInt(mAvailableCardPositionList.size());
					mRandomCardPositionList.set(i, mAvailableCardPositionList.remove(randomNum));
				}
			}
		}

		mMyFragmentPagerAdapter.notifyDataSetChanged();
		
		// When we delete the last card in a card set, we return to the list
		if(mRandomCardPositionList.size() == 0) {
			
			String message = String.format(getResources().getString(R.string.delete_last_card_message), mCardSetName);
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
			
			finish();
		}
	}
	
	/*
	 * Classes
	 */
	private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

		private Map<Integer, CardFragment> mPageReferenceMap = new HashMap<Integer, CardFragment>();
		
		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {

			int randomNum;
			
			if(mRandomCardPositionList.get(index).compareTo(NEG_ONE) == 0) {
				
				randomNum = mRandom.nextInt(mAvailableCardPositionList.size());
				mRandomCardPositionList.set(index, mAvailableCardPositionList.remove(randomNum));
			}
			
			CardFragment cardFragment = CardFragment.newInstance(mCards.get(mRandomCardPositionList.get(index)), index, mNumberOfCards);
			mPageReferenceMap.put(Integer.valueOf(index), cardFragment);
			
			return cardFragment;
		}

		@Override
		public int getCount() {

			return mNumberOfCards;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
		
			super.destroyItem(container, position, object);
			mPageReferenceMap.remove(Integer.valueOf(position));
		}
		
		/*
		 * Overriding this method in conjunction with calling notifyDataSetChanged 
		 * removes a page from the pager.
		 */
		@Override
		public int getItemPosition(Object object) {
		 
			return POSITION_NONE;
		}
		
		public CardFragment getFragment(int key) {
			
			return mPageReferenceMap.get(key);
		}
	}
}
