package org.thomasamsler.android.flashcards;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class FragmentPagerActivity extends FragmentActivity {

	private ViewPager mViewPager;
	private MyFragmentPagerAdapter mMyFragmentPagerAdapter;
	private Random random;
	private Set<String> words;
	private int numberOfWords;
	private int wordCount;
	
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		ImageButton imageButton = (ImageButton)findViewById(R.id.imageButtonList);
		imageButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				Intent intent = new Intent(v.getContext(), ListActivity.class);
				startActivity(intent);
			}
		});
		
		// Get intent data
		Bundle bundle = getIntent().getExtras();
		int wordsId = bundle.getInt(AppConstants.SELECTED_LIST_ITEM_KEY);
		
		words = new LinkedHashSet<String>(Arrays.asList(getResources().getStringArray(WordSets.wordSets.get(Integer.valueOf(wordsId)))));
		numberOfWords = words.size();
		wordCount = 1;
		
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mMyFragmentPagerAdapter);
		
		random = new Random();
	}

	private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {

			String word = null;
			int randomNum = random.nextInt(words.size()) + 1;
			
			for(Iterator<String> iter = words.iterator(); iter.hasNext();) {
				
				if(0 < randomNum) {
					
					word = iter.next();
					randomNum--;
				}
				else {
					
					break;
				}
			}
			
			words.remove(word);
			
			return PageFragment.newInstance(word, wordCount++, numberOfWords);
		}

		@Override
		public int getCount() {

			return numberOfWords;
		}
	}
}
