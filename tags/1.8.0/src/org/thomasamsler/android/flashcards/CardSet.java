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

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class CardSet implements Comparable<CardSet> {
	
	public static final int ADD_CARD_FRAGMENT = 1;
	public static final int CARDS_PAGER_FRAGMENT = 2;
	
	public static final String ID_KEY = "i";
	public static final String NAME_KEY = "n";
	public static final String FRAGMENT_KEY = "f";
	
	private String mName;
	private String mId;
	private int mFragmentId;
	
	public CardSet(String name) {
		
		this.mName = name;
	}
	
	public CardSet(String id, String name) {
		
		this.mId = id;
		this.mName = name;
	}
	
	public CardSet(String id, String name, int fragmentId) {
		
		this.mId = id;
		this.mName = name;
		this.mFragmentId = fragmentId;
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

	public int getFragmentId() {
		return mFragmentId;
	}

	public void setFragmentId(int fragmentId) {
		this.mFragmentId = fragmentId;
	}
	
	public boolean isRemote() {

		if(null != mId && !"".equals(mId)) {
			
			return true;
		}
		else {
			
			return false;
		}
	}

	@Override
	public String toString() {
		
		return mName;
	}

	public int compareTo(CardSet listItem) {
		
		return mName.compareTo(listItem.getName());
	}
	
	public JSONObject getJSON() {
		
		JSONObject json = new JSONObject();
		
		try {
		
			json.put(ID_KEY, mId);
			json.put(NAME_KEY, mName);
			json.put(FRAGMENT_KEY, mFragmentId);
			
		}
		catch(JSONException e) {
			
			Log.e(AppConstants.LOG_TAG, "JSONException", e);
		}
		
		return json;
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mName == null) ? 0 : mName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CardSet other = (CardSet) obj;
		if (mName == null) {
			if (other.mName != null)
				return false;
		} else if (!mName.equals(other.mName))
			return false;
		return true;
	}
}
