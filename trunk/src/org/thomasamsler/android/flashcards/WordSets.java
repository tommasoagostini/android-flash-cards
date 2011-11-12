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
