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

public interface AppConstants {
	
	public static final String PREFERENCE_NAME = "FlashCardsPrefsFile";
	public static final String PREFERENCE_FCEX_USER_NAME = "fcexun";
	
	
	public static final String SELECTED_LIST_ITEM_KEY = "words";
	public static final String LOG_TAG = "Flash Cards";
	public static final String FILE_NAMES_KEY = "files";
	public static final String CARD_SET_NAME_KEY = "csnk";
	public static final String CARD_SET_ID_KEY = "csik";
	
	public static final int NORMAL_TEXT_SIZE = 60;
	public static final int LARGE_TEXT_SIZE = 80;
	
	public static final String _OF_ = " of ";
	public static final String BACK = " Back";
	public static final String FRONT = " Front";
	
	public static final String WORD_DELIMITER_TOKEN = ":";
	
	/*
	 * A card set entry has to have a minimum length of 3
	 * e.g.
	 * <front>:<back>
	 * a:b
	 */
	public static final int MIN_DATA_LENGTH = 3;
	
	
	public static final int HELP_CONTEXT_DEFAULT = 0;
	public static final int HELP_CONTEXT_SETUP = 1;
	public static final int HELP_CONTEXT_CARD_SET_LIST = 2;
	public static final int HELP_CONTEXT_ADD_CARD = 3;
}
