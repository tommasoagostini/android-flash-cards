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

package org.thomasamsler.android.flashcards;

public interface AppConstants {
	
	public static final String PREFERENCE_NAME = "FlashCardsPrefsFile";
	public static final String PREFERENCE_FCEX_USER_NAME = "fcexun";
	public static final String PREFERENCE_SHOW_SAMPLE = "showSample";
	public static final String PREFERENCE_RUN_CONVERSION = "convert";
	
	public static final boolean PREFERENCE_SHOW_SAMPLE_DEFAULT = true;
	public static final boolean PREFERENCE_RUN_CONVERSION_DEFAULT = true;
	
	
	public static final String SELECTED_LIST_ITEM_KEY = "words";
	public static final String LOG_TAG = "Flash Cards";
	public static final String FILE_NAMES_KEY = "files";
	public static final String CARD_SET_TITLE_KEY = "csnk";
	public static final String CARD_SET_ID_KEY = "csik";
	
	public static final int NORMAL_TEXT_SIZE = 60;
	public static final int LARGE_TEXT_SIZE = 80;
	
	public static final String _OF_ = " of ";
	public static final String BACK = " Back";
	public static final String FRONT = " Front";
	
	// ListActivity
	public static final int HELP_CONTEXT_DEFAULT = 0;
	public static final int HELP_CONTEXT_SETUP = 1;
	public static final int HELP_CONTEXT_CARD_SET_LIST = 2;
	public static final int HELP_CONTEXT_ADD_CARD = 3;
	
	// CardsPagerActivity
	public static final int HELP_CONTEXT_VIEW_CARD = 4;
	
	// CardFragemnt Actions
	public static final int ACTION_SHOW_FOLD_PAGE = 0;
	public static final int ACTION_HIDE_FOLD_PAGE = 1;
	public static final int ACTION_MAGNIFY_FONT = 2;
	public static final int ACTION_REDUCE_FONT = 3;
	
	// Activity Result
	public static final int ACTIVITY_RESULT = 0;
	public static final String CARD_SET_ID = "csi";
	
	public static final int INVALID_CARD_SET_ID = -1;
	
}
