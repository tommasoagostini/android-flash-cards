/*
 * Copyright 2011, 2012 Thomas Amsler
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

package org.thomasamsler.android.flashcards.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.thomasamsler.android.flashcards.AppConstants;
import org.thomasamsler.android.flashcards.R;
import org.thomasamsler.android.flashcards.db.DataSource;
import org.thomasamsler.android.flashcards.dialog.HelpDialog;
import org.thomasamsler.android.flashcards.external.FlashCardExchangeData;
import org.thomasamsler.android.flashcards.fragment.CardFragment;
import org.thomasamsler.android.flashcards.model.Card;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class CardsActivity extends FragmentActivity implements AppConstants, FlashCardExchangeData {

	private static final Integer NEG_ONE = Integer.valueOf(-1);
	
	private ViewPager mViewPager;
	private MyFragmentPagerAdapter mMyFragmentPagerAdapter;
	private Random mRandom;
	private List<Card> mCards;
	private List<Integer> mRandomCardPositionList;
	private List<Integer> mAvailableCardPositionList;
	private long mCardSetId;
	private String mCardSetTitle;
	private int mNumberOfCards;
	private int mHelpContext;
	private int mFontSize;
	
	private DataSource mDataSource;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.cards);
		
		mDataSource = new DataSource(this);
        mDataSource.open();
		
		mHelpContext = HELP_CONTEXT_VIEW_CARD;

		ImageButton imageButtonList = (ImageButton)findViewById(R.id.imageButtonList);
		imageButtonList.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				finish();
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
		mCardSetId = bundle.getLong(AppConstants.CARD_SET_ID_KEY);
		mCardSetTitle = bundle.getString(AppConstants.CARD_SET_TITLE_KEY);
		
		mCards = mDataSource.getCards(mCardSetId);
		
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
				
				cardFragment.setFontSize(mFontSize);
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) { /* Nothing to do here */ }
			
			public void onPageScrollStateChanged(int state) { /* Nothing to do here */ }

		});
		
		mRandom = new Random();
	}
	
	@Override
	protected void onResume() {
		mDataSource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		mDataSource.close();
		super.onPause();
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
	    case R.id.menu_card_help:
	    	showHelp();
	    	return true;
	    case R.id.menu_card_magnify:
	    	zoom(ACTION_MAGNIFY_FONT);
	    	return true;
	    case R.id.menu_card_reduce:
	    	zoom(ACTION_REDUCE_FONT);
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	protected DataSource getDataSource() {
		
		return mDataSource;
	}
	
	protected void showHelp() {

		HelpDialog helpDialog = new HelpDialog(this);

		switch(mHelpContext) {

		case HELP_CONTEXT_DEFAULT:
			helpDialog.setHelp(getResources().getString(R.string.help_content_default));
			break;

		case HELP_CONTEXT_VIEW_CARD:
			helpDialog.setHelp(getResources().getString(R.string.help_content_view_card));
			break;

		default:
			helpDialog.setHelp(getResources().getString(R.string.help_content_default));
		}

		helpDialog.show();
	}

	public void updateCard(int index, String question, String answer) {

		/*
		 * First, we update the in memory list of words
		 */
		Card card = mCards.get(mRandomCardPositionList.get(index));
		card.setQuestion(question);
		card.setAnswer(answer);
		
		mDataSource.updateCard(card);
	}

	/*
	 * Menu method
	 */
	private void zoom(int action) {
		
		int currentIndex = mViewPager.getCurrentItem();
		CardFragment cardFragment = mMyFragmentPagerAdapter.getFragment(currentIndex);
	
		if(null == cardFragment) {
			
			return;
		}
		
		if(ACTION_MAGNIFY_FONT == action) {
			
			mFontSize += FONT_SIZE_ZOOM_CHANGE;
		}
		else if(ACTION_REDUCE_FONT == action) {
			
			mFontSize -= FONT_SIZE_ZOOM_CHANGE;
		}
		
		cardFragment.setFontSize(mFontSize);
	}
	
	/*
	 * Menu method
	 */
	private void showCardInformation() {
		
		String message = String.format(getResources().getString(R.string.card_information), mCardSetTitle);
		
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}
	
	/*
	 * Menu method
	 */
	private void deleteCard() {
		
		// Get the current card index
		int currentIndex = mViewPager.getCurrentItem();
		
		// Reduce the card counter by one
		mNumberOfCards -= 1;
		
		// Mark card as deleted. The saveCards(...) method ignores null or empty string cards
		Card card = mCards.set(mRandomCardPositionList.get(currentIndex), null);
		
		// Delete card
		mDataSource.deleteCard(card);
		
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
			
			String message = String.format(getResources().getString(R.string.delete_last_card_message), mCardSetTitle);
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
			
			/* 
			 * Since there are no more cards, show the CardSet list activity.
			 * We also notify the CardSetActivity hat it needs to update the 
			 * related CardSet's card count.
			 */
			Intent resultIntent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putLong(CARD_SET_ID, card.getCardSetId());
			resultIntent.putExtras(bundle);
			setResult(Activity.RESULT_OK, resultIntent);
			finish();
		}
	}
	
	/*
	 * Classes
	 */
	private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

		private Map<Integer, CardFragment> mPageReferenceMap = new HashMap<Integer, CardFragment>();
		
		SharedPreferences mSharedPreferences = null;
		
		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
			
			mSharedPreferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
			int fontSizePreference = mSharedPreferences.getInt(PREFERENCE_FONT_SIZE, PREFERENCE_NORMAL_FONT_SIZE);
			
			switch(fontSizePreference) {
			case PREFERENCE_SMALL_FONT_SIZE:
				mFontSize = SMALL_FONT_SIZE;
				break;
			case PREFERENCE_NORMAL_FONT_SIZE:
				mFontSize = NORMAL_FONT_SIZE;
				break;
			case PREFERENCE_LARGE_FONT_SIZE:
				mFontSize = LARGE_FONT_SIZE;
				break;
			default:
				mFontSize = NORMAL_FONT_SIZE;
				break;
			}
		}

		@Override
		public Fragment getItem(int index) {

			int randomNum;
			
			if(mRandomCardPositionList.get(index).compareTo(NEG_ONE) == 0) {
				
				randomNum = mRandom.nextInt(mAvailableCardPositionList.size());
				mRandomCardPositionList.set(index, mAvailableCardPositionList.remove(randomNum));
			}
			
			CardFragment cardFragment = CardFragment.newInstance(mCards.get(mRandomCardPositionList.get(index)), index, mNumberOfCards, mFontSize);
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
