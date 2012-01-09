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

public class CardFragment extends Fragment implements AppConstants {

	private final static String CARD_KEY = "c";
	private final static String MAX_KEY = "m";
	private final static String CARD_POSITION_KEY = "p";
	
	private boolean mWordToggle = false;
	private StringBuilder mCounterStringBuilder;
	private TextView mTextViewWord;
	private TextView mTextViewWord2;
	private TextView mCounterTextView;
	private EditText mEditTextWord;
	private LinearLayout mLinearLayoutEditButtons;
	private ImageButton mImageButtonSave;
	private ImageButton mImageButtonCancel;
	private ImageButton mImageButtonFoldPage;
	private int mCardPosition;
	
	private View mCardView;

	public static CardFragment newInstance(String word, int wordIndex, int totalWords) {

		CardFragment pageFragment = new CardFragment();
		Bundle bundle = new Bundle();

		bundle.putString(CARD_KEY, word);
		bundle.putInt(CARD_POSITION_KEY, wordIndex);
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

		mCardView = inflater.inflate(R.layout.card, container, false);

		mCardPosition = getArguments().getInt(CARD_POSITION_KEY);
		
		String[] words = getArguments().getString(CARD_KEY).split(WORD_DELIMITER_TOKEN);
		
		mTextViewWord = (TextView)mCardView.findViewById(R.id.textViewWord);
		mTextViewWord.setTextSize(NORMAL_TEXT_SIZE);
		
		if(1 <= words.length) {
		
			mTextViewWord.setText(words[0]);
		}
		else {
			
			mTextViewWord.setText("");
		}

		mTextViewWord2 = (TextView)mCardView.findViewById(R.id.textViewWord2);
		mTextViewWord2.setTextSize(NORMAL_TEXT_SIZE);
		
		if(2 == words.length) {
			
			mTextViewWord2.setText(words[1]);
		}
		else {
			
			mTextViewWord2.setText("");
		}

		mEditTextWord = (EditText)mCardView.findViewById(R.id.editTextWord);
		mEditTextWord.setTextSize(NORMAL_TEXT_SIZE);

		mLinearLayoutEditButtons = (LinearLayout)mCardView.findViewById(R.id.linearLayoutEditButtons);

		mImageButtonSave = (ImageButton)mCardView.findViewById(R.id.imageButtonSave);
		mImageButtonSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				/*
				 * General input validation
				 */
				if(!isValid(mEditTextWord.getText().toString().trim())) {
					
					return;
				}
				
				/*
				 * User has to enter non empty string for front of card
				 */
				String editText = mEditTextWord.getText().toString();
				if(!mWordToggle && (null == editText || "".equals(editText))) {
					
					Toast.makeText(getActivity().getApplicationContext(), R.string.input_validation_warning2, Toast.LENGTH_SHORT).show();
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
					sb.append(WORD_DELIMITER_TOKEN);
					sb.append(mTextViewWord2.getText().toString());
					((CardsPagerActivity)getActivity()).updateCard(mCardPosition, sb.toString());
				}
				else if(mWordToggle && !mEditTextWord.getText().toString().equals(mTextViewWord2.getText().toString())) {
					
					mTextViewWord2.setText(mEditTextWord.getText());
					mTextViewWord2.setVisibility(View.VISIBLE);
					StringBuilder sb = new StringBuilder();
					sb.append(mTextViewWord.getText().toString());
					sb.append(WORD_DELIMITER_TOKEN);
					sb.append(mEditTextWord.getText().toString());
					((CardsPagerActivity)getActivity()).updateCard(mCardPosition, sb.toString());
				}
				else if(!mWordToggle) {

					mTextViewWord.setVisibility(View.VISIBLE);
				}
				else if(mWordToggle) {
					
					mTextViewWord2.setVisibility(View.VISIBLE);
				}
			}
		});

		mImageButtonCancel = (ImageButton)mCardView.findViewById(R.id.imageButtonCancel);
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

		mImageButtonFoldPage = (ImageButton)mCardView.findViewById(R.id.imageButtonWordFoldPage);
		
		/*
		 * We show the fold page button for the first card and hide if for the 
		 * other ones. The view pager's onPageScrollStateChanged() event will 
		 * hide/show the button as needed
		 */
		if(0 != mCardPosition) {
		
			mImageButtonFoldPage.setVisibility(View.INVISIBLE);
		}
		
		mImageButtonFoldPage.setOnClickListener(new OnClickListener() {
			
			public void onClick(final View v) {
				
				turnPage(mCardView);
			}
		});
		
		// Set the bottom word counter
		mCounterTextView = (TextView) mCardView.findViewById(R.id.textViewWordNumber);
		mCounterStringBuilder = new StringBuilder();
		mCounterStringBuilder.append(mCardPosition + 1);
		mCounterStringBuilder.append(_OF_);
		mCounterStringBuilder.append(getArguments().getInt(MAX_KEY));
		mCounterTextView.setText(mCounterStringBuilder.toString());
		mCounterTextView.append(FRONT);

		mCardView.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(final View v) {

				return turnPage(mCardView);
			}
		});
		
		return mCardView;
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
	
	private void onMagnifyFont() {
		
		mTextViewWord.setTextSize(LARGE_TEXT_SIZE);
		mTextViewWord2.setTextSize(LARGE_TEXT_SIZE);
	}
	
	private void onReduceFont() {
		
		mTextViewWord.setTextSize(NORMAL_TEXT_SIZE);
		mTextViewWord2.setTextSize(NORMAL_TEXT_SIZE);
	}
	
	public void doAction(int action) {
		
		switch(action) {
		
		case ACTION_HIDE_FOLD_PAGE:
			mImageButtonFoldPage.setVisibility(View.INVISIBLE);
			break;
			
		case ACTION_SHOW_FOLD_PAGE:
			mImageButtonFoldPage.setVisibility(View.VISIBLE);
			break;
			
		case ACTION_MAGNIFY_FONT:
			onMagnifyFont();
			break;
			
		case ACTION_REDUCE_FONT:
			onReduceFont();
			break;
		}
	}
	
	private boolean turnPage(final View view) {
	
		/*
		 * If in edit mode, we don't allow the user to switch between the front and back page.
		 */
		if(mEditTextWord.isShown()) {
			
			return false;
		}
		
		mImageButtonFoldPage.setVisibility(View.INVISIBLE);
		
		final Animation flip1 = AnimationUtils.loadAnimation(view.getContext(), R.anim.flip1);
		final Animation flip2 = AnimationUtils.loadAnimation(view.getContext(), R.anim.flip2);
		
		flip1.setAnimationListener(new AnimationListener() {
			
			public void onAnimationStart(Animation animation) { /* Nothing to do here */ }
			
			public void onAnimationRepeat(Animation animation) { /* Nothing to do here */ }
			
			public void onAnimationEnd(Animation animation) {
				
				mWordToggle ^= true;

				if(mWordToggle) {

					mTextViewWord.setVisibility(View.INVISIBLE);
					mTextViewWord2.setVisibility(View.VISIBLE);
					mCounterTextView.setText(mCounterStringBuilder.toString());
					mCounterTextView.append(BACK);
				}
				else {

					mTextViewWord.setVisibility(View.VISIBLE);
					mTextViewWord2.setVisibility(View.INVISIBLE);
					mCounterTextView.setText(mCounterStringBuilder.toString());
					mCounterTextView.append(FRONT);
				}
				
				view.startAnimation(flip2);
			}
		});
		
		view.startAnimation(flip1);
		
		flip2.setAnimationListener(new AnimationListener() {
			
			public void onAnimationStart(Animation animation) { /* Nothing to do here */}
			
			public void onAnimationRepeat(Animation animation) { /* Nothing to do here */}
			
			public void onAnimationEnd(Animation animation) {
				
				mImageButtonFoldPage.setVisibility(View.VISIBLE);
			}
		});

		return false;
	}
	
	private boolean isValid(String input) {
		
		if(null != input && input.contains(WORD_DELIMITER_TOKEN)) {
			
			Toast.makeText(getActivity().getApplicationContext(), R.string.input_validation_warning, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
}
