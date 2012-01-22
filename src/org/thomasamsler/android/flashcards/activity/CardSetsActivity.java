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
import org.thomasamsler.android.flashcards.db.DataSource;
import org.thomasamsler.android.flashcards.dialog.HelpDialog;
import org.thomasamsler.android.flashcards.fragment.AboutFragment;
import org.thomasamsler.android.flashcards.fragment.AddActionbarFragment;
import org.thomasamsler.android.flashcards.fragment.AddCardFragment;
import org.thomasamsler.android.flashcards.fragment.ArrayListFragment;
import org.thomasamsler.android.flashcards.fragment.ListActionbarFragment;
import org.thomasamsler.android.flashcards.fragment.SetupActionbarFragment;
import org.thomasamsler.android.flashcards.fragment.SetupFragment;
import org.thomasamsler.android.flashcards.model.CardSet;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class CardSetsActivity extends FragmentActivity implements AppConstants {
	
	private ListActionbarFragment mListActionbarFragment;
	private SetupActionbarFragment mSetupActionbarFragment;
	private AddActionbarFragment mAddActionbarFragment;
	private ArrayListFragment mArrayListFragment;
	private AddCardFragment mAddCardFragment;
	private SetupFragment mSetupFragment;
	private AboutFragment mAboutFragment;

	private int mHelpContext;
	
	private DataSource mDataSource;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.list);        
        
        mDataSource = new DataSource(this);
        mDataSource.open();
        
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
	public boolean onCreateOptionsMenu(Menu menu) {
		
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.card_set_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
	    // Handle item selection
	    switch (item.getItemId()) {
	    
	    case R.id.menu_card_set_setup:
	    	showSetupFragment();
	        return true;

	    case R.id.menu_card_set_external:
	    	mArrayListFragment.getFlashCardExchangeCardSets();
	    	return true;
	    	
	    case R.id.menu_card_set_about:
	    	showAboutFragment();
	    	return true;
	    	
	    case R.id.menu_card_set_help:
	    	showHelp();
	    	return true;
	    	
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		/*
		 * If the user deleted the last Card we make sure that we update the
		 * CardSet accordingly 
		 */
		if(ACTIVITY_RESULT == requestCode && null != data && null != mArrayListFragment) {

			long cardSetId = data.getLongExtra(CARD_SET_ID, INVALID_CARD_SET_ID);
			mArrayListFragment.setCardSetCardCountToZero(cardSetId);
		}
	}

	public void setHelpContext(int context) {
		
		this.mHelpContext = context;
	}
	
	public void showArrayListFragment(boolean addToBackStack) {
		
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        
        if(null == mArrayListFragment) {
        	
        	mArrayListFragment = new ArrayListFragment();
        }

        if(null == mListActionbarFragment) {

        	mListActionbarFragment = new ListActionbarFragment();
        }

        fragmentTransaction.replace(R.id.actionbarContainer, mListActionbarFragment);
        fragmentTransaction.replace(R.id.fragmentContainer, mArrayListFragment);
        
        if(addToBackStack) {
        
        	fragmentTransaction.addToBackStack(null);
        }
        
        fragmentTransaction.commit();
        
        mHelpContext = HELP_CONTEXT_CARD_SET_LIST;
	}
	
	public void showAddCardFragment(CardSet cardSet) {
		
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        
        if(null == mAddCardFragment) {
        	
        	mAddCardFragment = new AddCardFragment();
        }
        
        if(null == mAddActionbarFragment) {
        	
        	mAddActionbarFragment = new AddActionbarFragment();
        }
        
        mAddCardFragment.setCardSet(cardSet);
        fragmentTransaction.replace(R.id.actionbarContainer, mAddActionbarFragment);
        fragmentTransaction.replace(R.id.fragmentContainer, mAddCardFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        
        mHelpContext = HELP_CONTEXT_ADD_CARD;
	}
	
	public void showSetupFragment() {
		
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        
        if(null == mSetupFragment) {
        	
        	mSetupFragment = new SetupFragment();
        }

        if(null == mSetupActionbarFragment) {

        	mSetupActionbarFragment = new SetupActionbarFragment();
        }
        
        fragmentTransaction.replace(R.id.actionbarContainer, mSetupActionbarFragment);
        fragmentTransaction.replace(R.id.fragmentContainer, mSetupFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        
        mHelpContext = HELP_CONTEXT_SETUP;
	}
	
	protected void showAboutFragment() {
		
		FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        
        if(null == mAboutFragment) {
        	
        	mAboutFragment = new AboutFragment();
        }

        if(null == mSetupActionbarFragment) {

        	mSetupActionbarFragment = new SetupActionbarFragment();
        }
        
        fragmentTransaction.replace(R.id.actionbarContainer, mSetupActionbarFragment);
        fragmentTransaction.replace(R.id.fragmentContainer, mAboutFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        
        mHelpContext = HELP_CONTEXT_DEFAULT;
	}
	
	public void showCardsPagerActivity(CardSet cardSet) {
		
		Log.i("DEBUG", "showCardsPagerActivity id " + cardSet.getId());
		Intent intent = new Intent(getApplicationContext(), CardsActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong(AppConstants.CARD_SET_ID_KEY, cardSet.getId());
		bundle.putString(AppConstants.CARD_SET_TITLE_KEY, cardSet.getTitle());
		intent.putExtras(bundle);
		//startActivity(intent);
		startActivityForResult(intent, 0);
	}
	
	protected void showHelp() {
		
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
			
			default:
				helpDialog.setHelp(getResources().getString(R.string.help_content_default));
		}
		
		helpDialog.show();
	}
	
	public void addCardSet(CardSet cardSet) {
		
		mArrayListFragment.addCardSet(cardSet);
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
