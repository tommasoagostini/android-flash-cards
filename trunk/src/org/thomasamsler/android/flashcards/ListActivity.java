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

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ListActivity extends FragmentActivity {
	
	private ArrayListFragment mArrayListFragment;
	private SetupFragment mSetupFragment;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.list);
        
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mArrayListFragment = new ArrayListFragment();
        fragmentTransaction.add(R.id.fragmentContainer, mArrayListFragment);
        fragmentTransaction.commit();
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
	    	FragmentManager fragmentManager = getSupportFragmentManager();
	        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
	        mSetupFragment = new SetupFragment();
	        fragmentTransaction.replace(R.id.fragmentContainer, mSetupFragment);
	        fragmentTransaction.addToBackStack(null);
	        fragmentTransaction.commit();
	        return true;
	    case R.id.menu_card_set_external:
	    	
	    	mArrayListFragment.getFlashCardExchangeCardSets();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}
