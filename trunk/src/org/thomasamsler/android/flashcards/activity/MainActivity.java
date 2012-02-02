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

import org.thomasamsler.android.flashcards.AppConstants;
import org.thomasamsler.android.flashcards.R;
import org.thomasamsler.android.flashcards.conversion.FileToDbConversion;
import org.thomasamsler.android.flashcards.db.DataSource;
import org.thomasamsler.android.flashcards.dialog.HelpDialog;
import org.thomasamsler.android.flashcards.fragment.AboutFragment;
import org.thomasamsler.android.flashcards.fragment.ActionbarFragment;
import org.thomasamsler.android.flashcards.fragment.AddCardFragment;
import org.thomasamsler.android.flashcards.fragment.ArrayListFragment;
import org.thomasamsler.android.flashcards.fragment.SetupFragment;
import org.thomasamsler.android.flashcards.model.CardSet;
import org.thomasamsler.android.flashcards.pager.CardsPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements AppConstants {
	
	private ActionbarFragment mListActionbarFragment;
	private ArrayListFragment mArrayListFragment;
	private AddCardFragment mAddCardFragment;
	private SetupFragment mSetupFragment;
	private AboutFragment mAboutFragment;
	private CardsPager mCardsPager;
	private int mHelpContext;
	private DataSource mDataSource;
	private int mActiveFragmentReference;
	
	private LinearLayout mFragmentContainer;
	private ViewPager mViewPager;
	
	private boolean mExitOnBackPressed;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.list);        
        
        mDataSource = new DataSource(this);
        mDataSource.open();
        
        /*
         * Determine if we need to run the File to DB conversion
         */
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		boolean runConversion = sharedPreferences.getBoolean(PREFERENCE_RUN_CONVERSION, PREFERENCE_RUN_CONVERSION_DEFAULT);
		
		if(runConversion) {

			FileToDbConversion conversion = new FileToDbConversion();
			conversion.convert(this, mDataSource);
			
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean(PREFERENCE_RUN_CONVERSION, false);
			editor.commit();
		}
		
		
		mFragmentContainer = (LinearLayout)findViewById(R.id.fragmentContainer);
		mViewPager = (ViewPager)findViewById(R.id.viewpager);
		
        showArrayListFragment(false);
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
	public void onBackPressed() {

		/*
		 * Intercepting the back button press since we need 
		 * to handle the Cards view
		 */
		if(mExitOnBackPressed) {
			
			finish();
		}
		else {

			showArrayListFragment(true);
		}
	}

	public void doDeleteCard(long cardSetId) {
		
		mArrayListFragment.decrementCardCount(cardSetId);
	}

	public void setHelpContext(int context) {
		
		this.mHelpContext = context;
	}
	
	public void showArrayListFragment(boolean addToBackStack) {
		
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        
        if(null == mListActionbarFragment) {

        	mListActionbarFragment = ActionbarFragment.newInstance(LIST_FRAGMENT);
        	fragmentTransaction.replace(R.id.actionbarContainer, mListActionbarFragment);
        }
        else {
        	
        	mListActionbarFragment.configureForList();
        }
        
        if(null == mArrayListFragment) {
        	
        	mArrayListFragment = new ArrayListFragment();
        }

        fragmentTransaction.replace(R.id.fragmentContainer, mArrayListFragment);
        
        if(addToBackStack) {
        
        	fragmentTransaction.addToBackStack(null);
        }
        
        fragmentTransaction.commit();
        
        mFragmentContainer.setVisibility(View.VISIBLE);
		mViewPager.setVisibility(View.GONE);
        mHelpContext = HELP_CONTEXT_CARD_SET_LIST;
        mActiveFragmentReference = LIST_FRAGMENT;
        mExitOnBackPressed = true;
	}
	
	public void showAddCardFragment(CardSet cardSet) {
		
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        
        if(null == mListActionbarFragment) {

        	mListActionbarFragment = ActionbarFragment.newInstance(ADD_FRAGMENT);
        	fragmentTransaction.replace(R.id.actionbarContainer, mListActionbarFragment);
        }
        else {
        	
        	 mListActionbarFragment.configureForAdd();
        }
        
        if(null == mAddCardFragment) {
        	
        	mAddCardFragment = new AddCardFragment();
        }
        
        mAddCardFragment.setCardSet(cardSet);
        
        fragmentTransaction.replace(R.id.fragmentContainer, mAddCardFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        
        mHelpContext = HELP_CONTEXT_ADD_CARD;
        mActiveFragmentReference = ADD_FRAGMENT;
        mExitOnBackPressed = false;
	}
	
	public void showSetupFragment() {
		
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        
        if(null == mListActionbarFragment) {

        	mListActionbarFragment = ActionbarFragment.newInstance(SETUP_FRAGMENT);
        	fragmentTransaction.replace(R.id.actionbarContainer, mListActionbarFragment);
        }
        else {
        	
        	mListActionbarFragment.configureForSetup();
        }
        
        if(null == mSetupFragment) {
        	
        	mSetupFragment = new SetupFragment();
        }

        fragmentTransaction.replace(R.id.fragmentContainer, mSetupFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        
        mHelpContext = HELP_CONTEXT_SETUP;
        mActiveFragmentReference = SETUP_FRAGMENT;
        mExitOnBackPressed = false;
	}
	
	public void showAboutFragment() {
		
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        
        if(null == mListActionbarFragment) {

        	mListActionbarFragment = ActionbarFragment.newInstance(ABOUT_FRAGMENT);
        	fragmentTransaction.replace(R.id.actionbarContainer, mListActionbarFragment);
        }
        else {
        	
        	 mListActionbarFragment.configureForAbout();
        }
        
        if(null == mAboutFragment) {
        	
        	mAboutFragment = new AboutFragment();
        }

        fragmentTransaction.replace(R.id.fragmentContainer, mAboutFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        
        mHelpContext = HELP_CONTEXT_DEFAULT;
        mActiveFragmentReference = ABOUT_FRAGMENT;
        mExitOnBackPressed = false;
	}
	
	public void showCardsFragment(CardSet cardSet) {
		
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        
        if(null == mListActionbarFragment) {

        	mListActionbarFragment = ActionbarFragment.newInstance(CARDS_FRAGMENT);
        	fragmentTransaction.replace(R.id.actionbarContainer, mListActionbarFragment);
        }
        else {
        	
        	 mListActionbarFragment.configureForCards();
        }
        
        mCardsPager = new CardsPager(this, mDataSource, cardSet);
        
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        
        mFragmentContainer.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
        mHelpContext = HELP_CONTEXT_VIEW_CARD;
        mActiveFragmentReference = CARDS_FRAGMENT;
        mExitOnBackPressed = false;
	}
	
	public void showHelp() {
		
		HelpDialog helpDialog = new HelpDialog(this);
		
		switch(mHelpContext) {
		
			case HELP_CONTEXT_DEFAULT:
				helpDialog.setHelp(getResources().getString(R.string.help_content_default));
				break;
			
			case HELP_CONTEXT_SETUP:
				helpDialog.setHelp(getResources().getString(R.string.help_content_setup));
				break;
			
			case HELP_CONTEXT_CARD_SET_LIST:
				helpDialog.setHelp(getResources().getString(R.string.help_content_card_set_list));
				break;
			
			case HELP_CONTEXT_ADD_CARD:
				helpDialog.setHelp(getResources().getString(R.string.help_content_add_card));
				break;
			
			case HELP_CONTEXT_VIEW_CARD:
				helpDialog.setHelp(getResources().getString(R.string.help_content_view_card));
				break;
				
			default:
				helpDialog.setHelp(getResources().getString(R.string.help_content_default));
		}
		
		helpDialog.show();
	}
	
	public void getExternal() {
		
		if(mActiveFragmentReference == SETUP_FRAGMENT) {
			
			showArrayListFragment(true);
		}
		
		if(null == mArrayListFragment) {
    		
    		Toast.makeText(getApplicationContext(), R.string.external_data_message_error, Toast.LENGTH_SHORT).show();
    	}
    	else {
    		
    		mArrayListFragment.getFlashCardExchangeCardSets();
    	}
	}
	
	public void addCardSet(CardSet cardSet) {
		
		mArrayListFragment.addCardSet(cardSet);
	}
	
	public void editCard() {
		
		mCardsPager.editCard();
	}
	
	public void doZoomIn() {
	
		mCardsPager.zoom(ACTION_MAGNIFY_FONT);
	}
	
	public void doZoomOut() {
		
		mCardsPager.zoom(ACTION_REDUCE_FONT);
	}
	
	public void doCardInfo() {
		
		mCardsPager.showCardInformation();
	}
	
	public void doDeleteCard() {
		
		mCardsPager.deleteCard();
	}
	
	public void doUpdateCard(int index, String question, String answer) {
	
		mCardsPager.updateCard(index, question, answer);
	}
	
	public DataSource getDataSource() {
		
		return mDataSource;
	}
	
	/*
	 * Helper method to check if there is network connectivity
	 */
	public boolean hasConnectivity() {

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

		if(null == connectivityManager) {

			return false;
		}

		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if(null != networkInfo && networkInfo.isAvailable() && networkInfo.isConnected()) {

			return true;
		}
		else {

			return false;
		}
	}
}