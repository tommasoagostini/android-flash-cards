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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

public class AddCardFragment extends Fragment {

	private CardSet mCardSet;
	private String mFrontPageWord;
	private String mBackPageWord;
	
	private View mView;
	private EditText mEditText;
	private TextView mTextViewTitle;
	
	private boolean mWordToggle;
	private boolean mIsSaved;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.add_fragment, container, false);
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mFrontPageWord = "";
		mBackPageWord = "";
		mWordToggle = false;
		mIsSaved = false;
		
		mEditText = (EditText)getActivity().findViewById(R.id.editTextAdd);
		
		mTextViewTitle = (TextView)getActivity().findViewById(R.id.textViewAddTitle);
		
		ImageButton imageButtonAddCancel = (ImageButton)getActivity().findViewById(R.id.imageButtonAddCancel);
		imageButtonAddCancel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				((ListActivity)getActivity()).showArrayListFragment(true);
			}
		});
		
		ImageButton imageButtonAddSave = (ImageButton)getActivity().findViewById(R.id.imageButtonAddSave);
		imageButtonAddSave.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {

				if(!isValid(mEditText.getText().toString().trim())) {
					
					return;
				}
				
				if(mWordToggle) { // Back side
				
					mBackPageWord = mEditText.getText().toString().trim();
					
					if(null == mFrontPageWord || "".equals(mFrontPageWord)) {
						
						Toast.makeText(getActivity().getApplicationContext(), R.string.add_card_save_message_warning1, Toast.LENGTH_LONG).show();
						return;
					}
					else if(null == mBackPageWord || "".equals(mBackPageWord)) {
						
						Toast.makeText(getActivity().getApplicationContext(), R.string.add_card_save_message_warning2, Toast.LENGTH_LONG).show();
						return;
					}
				}
				else { // Front side
					
					mFrontPageWord = mEditText.getText().toString().trim();
					
					if(null == mFrontPageWord || "".equals(mFrontPageWord)) {

						Toast.makeText(getActivity().getApplicationContext(), R.string.add_card_save_message_warning2, Toast.LENGTH_LONG).show();
						return;
					}
					else if(null == mBackPageWord || "".equals(mBackPageWord)) {
						
						Toast.makeText(getActivity().getApplicationContext(), R.string.add_card_save_message_front_page, Toast.LENGTH_SHORT).show();
						
						flipCard(mView);
						return;
					}
				}

				mIsSaved = addCard(mCardSet.getName(), mFrontPageWord, mBackPageWord);

				if(mIsSaved) {

					Toast.makeText(getActivity().getApplicationContext(), R.string.add_card_save_message_success, Toast.LENGTH_SHORT).show();
					((ListActivity)getActivity()).showArrayListFragment(true);
				}
				else {

					Toast.makeText(getActivity().getApplicationContext(), R.string.add_card_save_message_error, Toast.LENGTH_LONG).show();
				}
				
				mEditText.setText("");
			}
		});

		mView = getActivity().findViewById(R.id.relativeLayoutAdd);
		mView.setOnLongClickListener(new OnLongClickListener() {
			
			public boolean onLongClick(View v) {
				
				flipCard(v);

				return false;
			}
		});
	}
	
	protected void setCardSet(CardSet cardSet) {
		
		this.mCardSet = cardSet;
	}
	
	private void flipCard(final View v) {
		
		final Animation flip1 = AnimationUtils.loadAnimation(v.getContext(), R.anim.flip1);
		final Animation flip2 = AnimationUtils.loadAnimation(v.getContext(), R.anim.flip2);
		flip1.setAnimationListener(new AnimationListener() {
			
			public void onAnimationStart(Animation animation) { /* Nothing to do here */ }
			
			public void onAnimationRepeat(Animation animation) { /* Nothing to do here */ }
			
			public void onAnimationEnd(Animation animation) {
				
				mWordToggle ^= true;

				if(mWordToggle) { // Back side
					
					// Save front side word
					mFrontPageWord = mEditText.getText().toString().trim();
					
					// Restore back side word
					if(null == mBackPageWord || "".equals(mBackPageWord)) {
						
						mEditText.setText("");
					}
					else {
						
						mEditText.setText(mBackPageWord);
					}
					
					mTextViewTitle.setText(R.string.add_card_back_title);
				}
				else { // Front side
					
					// Save back side word
					mBackPageWord = mEditText.getText().toString().trim();
					
					// Restore front side word
					if(null == mFrontPageWord || "".equals(mFrontPageWord)) {
						
						mEditText.setText("");
					}
					else {
						
						mEditText.setText(mFrontPageWord);
					}
					
					mTextViewTitle.setText(R.string.add_card_front_title);
				}
				
				v.startAnimation(flip2);
			}
		});
		
		v.startAnimation(flip1);
	}
	
	private boolean addCard(String fileName, String frontSideWord, String backSideWord) {
		
		try {
			
			FileOutputStream fos = getActivity().getApplicationContext().openFileOutput(fileName, Context.MODE_APPEND);
			PrintStream ps = new PrintStream(fos);
			
			StringBuilder sb = new StringBuilder();
			sb.append(frontSideWord);
			sb.append(AppConstants.WORD_DELIMITER_TOKEN);
			sb.append(backSideWord);
			ps.print(sb.toString());
			
			ps.println();
			ps.close();
		}
		catch(FileNotFoundException e) {

			Log.w(AppConstants.LOG_TAG, "FileNotFoundException: Was not able to open file", e);
			return false;
		}
		catch(Exception e) {
			
			Log.w(AppConstants.LOG_TAG, "Exception: Was not able to write to file", e);
			return false;
		}
		
		return true;
	}
	
	private boolean isValid(String input) {
		
		if(null != input && input.contains(AppConstants.WORD_DELIMITER_TOKEN)) {
			
			Toast.makeText(getActivity().getApplicationContext(), R.string.input_validation_warning, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
}
