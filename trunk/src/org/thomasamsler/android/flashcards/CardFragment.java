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
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class CardFragment extends Fragment {
	
	private final static String WORD_KEY = "word";
	private final static String MAX_KEY = "max";
	private final static String CURRENT_KEY = "current";
	private Integer mTag;
	
	public static CardFragment newInstance(String word, int wordCount, int totalWords) {
		
		CardFragment pageFragment = new CardFragment();
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
		
	    View view = inflater.inflate(R.layout.card, container, false);

	    // Set the main word 
	    final TextView wordTextView = (TextView)view.findViewById(R.id.textViewWord);
	    wordTextView.setText(getArguments().getString(WORD_KEY));
//	    wordTextView.setOnLongClickListener(new OnLongClickListener() {
//			
//			public boolean onLongClick(View v) {
//				// FIXME: Experimental 
//				Animation hyperspaceJump = AnimationUtils.loadAnimation(v.getContext(), R.anim.scale);
//				wordTextView.startAnimation(hyperspaceJump);
//				return false;
//			}
//		});
	    
	    final EditText editTextWord = (EditText)view.findViewById(R.id.editTextWord);
	    final ImageButton imageButtonSave = (ImageButton)view.findViewById(R.id.imageButtonSave);
	    final ImageButton imageButtonCancel = (ImageButton)view.findViewById(R.id.imageButtonCancel);
	    
	    imageButtonSave.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				editTextWord.setVisibility(View.INVISIBLE);
				wordTextView.setText(editTextWord.getText());
				wordTextView.setVisibility(View.VISIBLE);
				v.setVisibility(View.INVISIBLE);
				imageButtonCancel.setVisibility(View.INVISIBLE);
				
				((CardsPagerActivity)getActivity()).updateWord(mTag, editTextWord.getText().toString());
			}
		});
	    
	    imageButtonCancel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				editTextWord.setVisibility(View.INVISIBLE);
				wordTextView.setVisibility(View.VISIBLE);
				v.setVisibility(View.INVISIBLE);
				imageButtonSave.setVisibility(View.INVISIBLE);
			}
		});
	    
	    // Set the bottom word counter
	    TextView counterTextView = (TextView) view.findViewById(R.id.textViewWordNumber);
	    StringBuilder sb = new StringBuilder();
	    sb.append(getArguments().getInt(CURRENT_KEY));
	    sb.append(" of ");
	    sb.append(getArguments().getInt(MAX_KEY));
	    counterTextView.setText(sb.toString());
	    
	    view.setTag(mTag);
	    
	    return view;
	}
	
	public void setTag(Integer obj) {
		
		mTag = obj;
	}
}
