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

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;

public class ListActivity extends FragmentActivity {
	
	private boolean mFetchExternal = true;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.list);
        
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("AFC", MODE_PRIVATE);
        mFetchExternal = sharedPreferences.getBoolean("fetchExternal", true);
        
        if(!mFetchExternal) {
        	
			ProgressBar progressBar =  (ProgressBar) findViewById(R.id.progressBar1);
			progressBar.setVisibility(ProgressBar.GONE);
        }
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
	    	// TODO : Launch setup intent
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("AFC", MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("fetchExternal", mFetchExternal);
		editor.commit();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("AFC", MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean("fetchExternal", true);
		editor.commit();
	}
	
	protected boolean canFetchExternal() {
		
		return mFetchExternal;
	}
	
	protected void disableFetchExternal() {
		
		Log.i("DEBUG", "called disableFetchExternal() ...");
		mFetchExternal = false;
	}
}
