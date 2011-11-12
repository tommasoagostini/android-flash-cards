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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WordSets {

	public static Map<Integer, Integer> wordSets = new HashMap<Integer, Integer>();
	public static List<String> wordSetNames = new ArrayList<String>();

	private static final String KINDERGARTEN_SET_1 = "Kindergarten Set 1";
	private static final String KINDERGARTEN_SET_2 = "Kindergarten Set 2";
	
	static {
		
		wordSetNames.add(KINDERGARTEN_SET_1);
		wordSetNames.add(KINDERGARTEN_SET_2);
		
		wordSets.put(Integer.valueOf(0), Integer.valueOf(R.array.kindergarten_set1));
		wordSets.put(Integer.valueOf(1), Integer.valueOf(R.array.kindergarten_set2));
	}
}
