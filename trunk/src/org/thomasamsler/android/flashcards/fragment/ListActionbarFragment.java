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

package org.thomasamsler.android.flashcards.fragment;

import java.util.List;

import org.thomasamsler.android.flashcards.R;
import org.thomasamsler.android.flashcards.activity.CardSetsActivity;
import org.thomasamsler.android.flashcards.db.DataSource;
import org.thomasamsler.android.flashcards.model.CardSet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListActionbarFragment extends Fragment {
	
	/*
	 * These values need to be in sync with values present in card_set_menu.xml
	 */
	private final int ACTION_SETUP = 0;
	private final int ACTION_ABOUT = 1;
	private final int ACTION_FCE = 2;
	private final int ACTION_HELP = 3;
	
	private DataSource mDataSource;
	private ListView mListViewOverflow;
	private String[] mOverflowActions;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.list_actionbar_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mDataSource = ((CardSetsActivity)getActivity()).getDataSource();
		
		ImageButton imageButtonNewCardSet = (ImageButton)getActivity().findViewById(R.id.imageButtonNewCardSet);
		imageButtonNewCardSet.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				builder.setCancelable(false);
				
				LayoutInflater inflater = getLayoutInflater(savedInstanceState);
				View layout = inflater.inflate(R.layout.dialog, (ViewGroup) getActivity().findViewById(R.id.layout_root));
				final EditText editText = (EditText)layout.findViewById(R.id.editTextDialogAdd);
				
				builder.setView(layout);
				builder.setPositiveButton(R.string.new_card_set_save_button, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						
						String newTitle = editText.getText().toString().trim();
						
						if(null == newTitle || "".equals(newTitle)) {
							
							Toast.makeText(getActivity().getApplicationContext(), R.string.new_card_set_dialog_message_warning2, Toast.LENGTH_LONG).show();
						}
						else {
							
							boolean titleExists = false;
							List<CardSet> cardSets = mDataSource.getCardSets();
							for(CardSet cardSet : cardSets) {
								
								if(newTitle.equals(cardSet.getTitle())) {
									
									titleExists = true;
									break;
								}
							}
							
							if(titleExists) {
								
								Toast.makeText(getActivity().getApplicationContext(), R.string.new_card_set_dialog_message_warning1, Toast.LENGTH_LONG).show();
							}
							else {
								
								CardSet cardSet = mDataSource.createCardSet(newTitle);
								((CardSetsActivity)getActivity()).addCardSet(cardSet);
							}
						}
					}
				});
				
				builder.setNegativeButton(R.string.new_card_set_cancel_button, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						
						dialog.cancel();
					}
				});
				
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
		
		ImageButton imageButtonOverflow = (ImageButton)getActivity().findViewById(R.id.imageButtonOverflow);
		imageButtonOverflow.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				if(mListViewOverflow.getVisibility() == View.VISIBLE) {
					
					mListViewOverflow.setVisibility(View.GONE);
				}
				else {
					
					mListViewOverflow.setVisibility(View.VISIBLE);
				}
			}
		});
		
		mOverflowActions = getResources().getStringArray(R.array.card_set_actions);
		
		mListViewOverflow = (ListView)getActivity().findViewById(R.id.listViewOverflow);
		mListViewOverflow.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mOverflowActions) {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				View view = super.getView(position, convertView, parent);
				
				((TextView)view).setTextColor(Color.WHITE);
				
				return view;
			}
		});
		mListViewOverflow.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				mListViewOverflow.setVisibility(View.GONE);
				
				switch(position) {
				
				case ACTION_SETUP:
					((CardSetsActivity)getActivity()).showSetupFragment();
					break;
					
				case ACTION_ABOUT:
					((CardSetsActivity)getActivity()).showAboutFragment();
					break;
					
				case ACTION_FCE:
					((CardSetsActivity)getActivity()).getExternal();
					break;
					
				case ACTION_HELP:
					((CardSetsActivity)getActivity()).showHelp();
					break;
					
				default:
					// TODO:
					break;
				}
			}
		});
	}
}
