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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PageFragment extends Fragment {
	
	private final static String WORD_KEY = "word";
	private final static String MAX_KEY = "max";
	private final static String CURRENT_KEY = "current";
	
	public static PageFragment newInstance(String word, int wordCount, int totalWords) {
		
		PageFragment pageFragment = new PageFragment();
		Bundle bundle = new Bundle();
		
		bundle.putString(WORD_KEY, word);
		bundle.putInt(CURRENT_KEY, wordCount);
		bundle.putInt(MAX_KEY, totalWords);
		pageFragment.setArguments(bundle);
		
		return pageFragment;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
	    View view = inflater.inflate(R.layout.fragment, container, false);
	    TextView textView = (TextView) view.findViewById(R.id.textView1);
	    textView.setText(getArguments().getString(WORD_KEY));
	    
	    TextView foo = (TextView) view.findViewById(R.id.textView2);
	    StringBuilder sb = new StringBuilder();
	    sb.append(getArguments().getInt(CURRENT_KEY));
	    sb.append(" of ");
	    sb.append(getArguments().getInt(MAX_KEY));
	    foo.setText(sb.toString());
	    
	    return view;
	}
}
