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
import android.widget.Toast;

public class CardFragment extends Fragment {

	private final static String WORD_KEY = "word";
	private final static String MAX_KEY = "max";
	private final static String CURRENT_KEY = "current";
	private boolean mWordToggle = false;
	private StringBuilder mCounterStringBuilder;
	private TextView mTextViewWord;
	private TextView mTextViewWord2;
	private TextView mCounterTextView;
	private EditText mEditTextWord;
	private LinearLayout mLinearLayoutEditButtons;
	private ImageButton mImageButtonSave;
	private ImageButton mImageButtonCancel;
	private int mWordIndex;

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

		mWordIndex = getArguments().getInt(CURRENT_KEY);
		
		String[] words = getArguments().getString(WORD_KEY).split(AppConstants.WORD_DELIMITER_TOKEN);
		
		mTextViewWord = (TextView)view.findViewById(R.id.textViewWord);
		mTextViewWord.setTextSize(AppConstants.NORMAL_TEXT_SIZE);
		mTextViewWord.setText(words[0]);

		mTextViewWord2 = (TextView)view.findViewById(R.id.textViewWord2);
		mTextViewWord2.setTextSize(AppConstants.NORMAL_TEXT_SIZE);
		mTextViewWord2.setText(words[1]);

		mEditTextWord = (EditText)view.findViewById(R.id.editTextWord);
		mEditTextWord.setTextSize(AppConstants.NORMAL_TEXT_SIZE);

		mLinearLayoutEditButtons = (LinearLayout)view.findViewById(R.id.linearLayoutEditButtons);

		mImageButtonSave = (ImageButton)view.findViewById(R.id.imageButtonSave);
		mImageButtonSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if(!isValid(mEditTextWord.getText().toString().trim())) {
					
					return;
				}
				
				mEditTextWord.setVisibility(View.INVISIBLE);
				mLinearLayoutEditButtons.setVisibility(View.INVISIBLE);

				/*
				 * Check if the user changed anything, and only update if there is a change
				 */
				
				if(!mWordToggle && !mEditTextWord.getText().toString().equals(mTextViewWord.getText().toString())) {

					mTextViewWord.setText(mEditTextWord.getText());
					mTextViewWord.setVisibility(View.VISIBLE);
					StringBuilder sb = new StringBuilder();
					sb.append(mEditTextWord.getText().toString());
					sb.append(AppConstants.WORD_DELIMITER_TOKEN);
					sb.append(mTextViewWord2.getText().toString());
					((CardsPagerActivity)getActivity()).updateWord(mWordIndex, sb.toString());
				}
				else if(mWordToggle && !mEditTextWord.getText().toString().equals(mTextViewWord2.getText().toString())) {
					
					mTextViewWord2.setText(mEditTextWord.getText());
					mTextViewWord2.setVisibility(View.VISIBLE);
					StringBuilder sb = new StringBuilder();
					sb.append(mTextViewWord.getText().toString());
					sb.append(AppConstants.WORD_DELIMITER_TOKEN);
					sb.append(mEditTextWord.getText().toString());
					((CardsPagerActivity)getActivity()).updateWord(mWordIndex, sb.toString());
				}
				else if(!mWordToggle) {

					mTextViewWord.setVisibility(View.VISIBLE);
				}
				else if(mWordToggle) {
					
					mTextViewWord2.setVisibility(View.VISIBLE);
				}
			}
		});

		mImageButtonCancel = (ImageButton)view.findViewById(R.id.imageButtonCancel);
		mImageButtonCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				mEditTextWord.setVisibility(View.INVISIBLE);
				
				if(mWordToggle) {
					
					mTextViewWord2.setVisibility(View.VISIBLE);
				}
				else {
					
					mTextViewWord.setVisibility(View.VISIBLE);
				}
				
				mLinearLayoutEditButtons.setVisibility(View.INVISIBLE);
			}
		});

		// Set the bottom word counter
		mCounterTextView = (TextView) view.findViewById(R.id.textViewWordNumber);
		mCounterStringBuilder = new StringBuilder();
		mCounterStringBuilder.append(mWordIndex + 1);
		mCounterStringBuilder.append(AppConstants._OF_);
		mCounterStringBuilder.append(getArguments().getInt(MAX_KEY));
		mCounterTextView.setText(mCounterStringBuilder.toString());
		mCounterTextView.append(AppConstants.FRONT);

		view.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(final View v) {

				/*
				 * If in edit mode, we don't allow the user to switch between the front and back page.
				 */
				if(mEditTextWord.isShown()) {
					
					return false;
				}
				
				final Animation flip1 = AnimationUtils.loadAnimation(v.getContext(), R.anim.flip1);
				final Animation flip2 = AnimationUtils.loadAnimation(v.getContext(), R.anim.flip2);
				
				flip1.setAnimationListener(new AnimationListener() {
					
					public void onAnimationStart(Animation animation) { /* Nothing to do here */ }
					
					public void onAnimationRepeat(Animation animation) { /* Nothing to do here */ }
					
					public void onAnimationEnd(Animation animation) {
						
						mWordToggle ^= true;

						if(mWordToggle) {

							mTextViewWord.setVisibility(View.INVISIBLE);
							mTextViewWord2.setVisibility(View.VISIBLE);
							mCounterTextView.setText(mCounterStringBuilder.toString());
							mCounterTextView.append(AppConstants.BACK);
						}
						else {

							mTextViewWord.setVisibility(View.VISIBLE);
							mTextViewWord2.setVisibility(View.INVISIBLE);
							mCounterTextView.setText(mCounterStringBuilder.toString());
							mCounterTextView.append(AppConstants.FRONT);
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

	public void onEdit() {
		
		mTextViewWord.setVisibility(View.INVISIBLE);
		mTextViewWord2.setVisibility(View.INVISIBLE);
		
		if(mWordToggle) {
			
			mEditTextWord.setText(mTextViewWord2.getText());
		}
		else {
		
			mEditTextWord.setText(mTextViewWord.getText());
		}
 		
		mEditTextWord.setVisibility(View.VISIBLE);
		mLinearLayoutEditButtons.setVisibility(View.VISIBLE);
	}
	
	public void onMagnifyFont() {
		
		mTextViewWord.setTextSize(AppConstants.LARGE_TEXT_SIZE);
		mTextViewWord2.setTextSize(AppConstants.LARGE_TEXT_SIZE);
	}
	
	public void onReduceFont() {
		
		mTextViewWord.setTextSize(AppConstants.NORMAL_TEXT_SIZE);
		mTextViewWord2.setTextSize(AppConstants.NORMAL_TEXT_SIZE);
	}
	
	private boolean isValid(String input) {
		
		if(null != input && input.contains(AppConstants.WORD_DELIMITER_TOKEN)) {
			
			Toast.makeText(getActivity().getApplicationContext(), R.string.input_validation_warning, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
}
