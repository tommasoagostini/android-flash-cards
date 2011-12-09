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

import android.content.Intent;

public class CardSet implements Comparable<CardSet> {
	
	public static final int ADD_CARD_FRAGMENT = 1;
	public static final int CARDS_PAGER_FRAGMENT = 2;
	
	private String mName;
	private String mId;
	private Intent intent;
	private int showFragment;
	
	public CardSet(String name) {
		
		this.mName = name;
	}
	
	public CardSet(String id, String name) {
		
		this.mName = name;
		this.mId = id;
	}
	
	public String getName() {
		return mName;
	}
	
	public void setName(String name) {
		this.mName = name;
	}
	
	public String getId() {
		return mId;
	}
	
	public void setId(String id) {
		this.mId = id;
	}
	
	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public int getShowFragment() {
		return showFragment;
	}

	public void setShowFragment(int showFragment) {
		this.showFragment = showFragment;
	}

	@Override
	public String toString() {
		
		return mName;
	}

	public int compareTo(CardSet listItem) {
		
		return mName.compareTo(listItem.getName());
	}
}
