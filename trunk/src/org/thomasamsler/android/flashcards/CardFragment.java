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
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CardFragment extends Fragment {

	private final static String WORD_KEY = "word";
	private final static String MAX_KEY = "max";
	private final static String CURRENT_KEY = "current";
	private Integer mTag;
	private boolean mWordToggle = false;

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

		String[] words = getArguments().getString(WORD_KEY).split(":");
		
		// Set the main word 
		final TextView textViewWord = (TextView)view.findViewById(R.id.textViewWord);
		textViewWord.setTextSize(AppConstants.NORMAL_TEXT_SIZE);
		textViewWord.setText(words[0]);

		final TextView textViewWord2 = (TextView)view.findViewById(R.id.textViewWord2);
		textViewWord2.setTextSize(AppConstants.NORMAL_TEXT_SIZE);
		textViewWord2.setText(words[1]);

		final EditText editTextWord = (EditText)view.findViewById(R.id.editTextWord);
		editTextWord.setTextSize(AppConstants.NORMAL_TEXT_SIZE);

		final LinearLayout linearLayoutEditButtons = (LinearLayout)view.findViewById(R.id.linearLayoutEditButtons);

		final ImageButton imageButtonSave = (ImageButton)view.findViewById(R.id.imageButtonSave);
		imageButtonSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				editTextWord.setVisibility(View.INVISIBLE);
				linearLayoutEditButtons.setVisibility(View.INVISIBLE);

				/*
				 * Check if the user changed anything, and only update if there is a change
				 */
				if(!editTextWord.getText().toString().equals(textViewWord.getText().toString())) {

					textViewWord.setText(editTextWord.getText());
					textViewWord.setVisibility(View.VISIBLE);
					((CardsPagerActivity)getActivity()).updateWord(mTag, editTextWord.getText().toString());
				}
				else {

					textViewWord.setVisibility(View.VISIBLE);
				}
			}
		});

		final ImageButton imageButtonCancel = (ImageButton)view.findViewById(R.id.imageButtonCancel);
		imageButtonCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				editTextWord.setVisibility(View.INVISIBLE);
				textViewWord.setVisibility(View.VISIBLE);
				linearLayoutEditButtons.setVisibility(View.INVISIBLE);
			}
		});

		// Set the bottom word counter
		TextView counterTextView = (TextView) view.findViewById(R.id.textViewWordNumber);
		StringBuilder sb = new StringBuilder();
		sb.append(getArguments().getInt(CURRENT_KEY));
		sb.append(AppConstants._OF_);
		sb.append(getArguments().getInt(MAX_KEY));
		counterTextView.setText(sb.toString());

		view.setTag(mTag);
		view.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(final View v) {

				Animation flip1 = AnimationUtils.loadAnimation(v.getContext(), R.anim.flip1);
				final Animation flip2 = AnimationUtils.loadAnimation(v.getContext(), R.anim.flip2);
				flip1.setAnimationListener(new AnimationListener() {
					
					public void onAnimationStart(Animation animation) { /* Nothing to do here */ }
					
					public void onAnimationRepeat(Animation animation) { /* Nothing to do here */ }
					
					public void onAnimationEnd(Animation animation) {
						
						mWordToggle ^= true;

						if(mWordToggle) {

							textViewWord.setVisibility(View.INVISIBLE);
							textViewWord2.setVisibility(View.VISIBLE);
						}
						else {

							textViewWord.setVisibility(View.VISIBLE);
							textViewWord2.setVisibility(View.INVISIBLE);
						}
						
						v.startAnimation(flip2);
					}
				});
				
				v.startAnimation(flip1);

				return false;
			}
		});

		return view;
	}

	public void setTag(Integer obj) {

		mTag = obj;
	}
}
