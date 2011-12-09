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

public interface FlashCardExchangeData {
	
	public static final String TEST_USER_NAME = "";
	public static final String API_GET_USER = "http://api.flashcardexchange.com/v1/get_user?user_login=";
	public static final String API_GET_CARD_SET = "http://api.flashcardexchange.com/v1/get_card_set?card_set_id=";
	public static final String API_KEY = "&api_key=";
		
}
