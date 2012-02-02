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

import java.util.ArrayList;
import java.util.List;

import org.thomasamsler.android.flashcards.AppConstants;
import org.thomasamsler.android.flashcards.R;
import org.thomasamsler.android.flashcards.activity.MainActivity;
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

public class ActionbarFragment extends Fragment implements AppConstants {

	/*
	 * These values need to be in sync with values present in card_set_menu.xml
	 */
	private final int ACTION_SETUP = 0;
	private final int ACTION_ABOUT = 1;
	private final int ACTION_FCE = 2;
	private final int ACTION_HELP = 3;

	/*
	 * These values need to be in sync with values present in card_menu.xml
	 */
	private final int ACTION_ZOOM_IN = 0;
	private final int ACTION_ZOOM_OUT = 1;
	private final int ACTION_CARD_INFO = 2;
	private final int ACTION_DELETE_CARD = 3;
	private final int ACTION_HELP_CARD = 4;

	private DataSource mDataSource;
	private ListView mListViewOverflow;
	private List<String> mOverflowActions;
	private ArrayAdapter<String> mArrayAdapter;

	private ImageButton mImageButtonEdit;
	private ImageButton mImageButtonNewCardSet;
	private ImageButton mImageButtonList;
	private ImageButton mImageButtonOverflow;

	private int mFragmentType;

	public static ActionbarFragment newInstance(int fragmentType) {

		ActionbarFragment listActionbarFragment = new ActionbarFragment();
		listActionbarFragment.setFragmentType(fragmentType);

		return listActionbarFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return inflater.inflate(R.layout.actionbar_fragment, container, false);
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mDataSource = ((MainActivity)getActivity()).getDataSource();

		mImageButtonEdit = (ImageButton)getActivity().findViewById(R.id.imageButtonEdit);
		mImageButtonEdit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				((MainActivity)getActivity()).editCard();
			}
		});

		mImageButtonList = (ImageButton)getActivity().findViewById(R.id.imageButtonList);
		mImageButtonList.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				((MainActivity)getActivity()).showArrayListFragment(true);
			}
		});


		mImageButtonNewCardSet = (ImageButton)getActivity().findViewById(R.id.imageButtonNewCardSet);
		mImageButtonNewCardSet.setOnClickListener(new OnClickListener() {

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
								((MainActivity)getActivity()).addCardSet(cardSet);
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

		mImageButtonOverflow = (ImageButton)getActivity().findViewById(R.id.imageButtonOverflow);
		mImageButtonOverflow.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if(mListViewOverflow.getVisibility() == View.VISIBLE) {

					mListViewOverflow.setVisibility(View.GONE);
				}
				else {

					mListViewOverflow.setVisibility(View.VISIBLE);
				}
			}
		});

		mListViewOverflow = (ListView)getActivity().findViewById(R.id.listViewOverflow);
		mOverflowActions = new ArrayList<String>();
		mArrayAdapter = getArrayAdapter();
		mListViewOverflow.setAdapter(mArrayAdapter);
		//mListViewOverflow.setOnItemClickListener(getListFragmentActionListener());

		/*
		 * Now we configure the action bar for the associated fragment
		 */
		switch(mFragmentType) {

		case SETUP_FRAGMENT:
			configureForSetup();
			break;
		case LIST_FRAGMENT:
			configureForList();
			break;
		case ADD_FRAGMENT:
			configureForAdd();
			break;
		case ABOUT_FRAGMENT:
			configureForAbout();
			break;
		case CARDS_FRAGMENT:
			configureForCards();
			break;
		}
	}

	public void configureForAdd() {

		mImageButtonEdit.setVisibility(View.GONE);
		mImageButtonNewCardSet.setVisibility(View.GONE);
		mImageButtonList.setVisibility(View.VISIBLE);
		mImageButtonOverflow.setVisibility(View.VISIBLE);
		mFragmentType = ADD_FRAGMENT;
	}

	public void configureForList() {

		mImageButtonEdit.setVisibility(View.GONE);
		mImageButtonNewCardSet.setVisibility(View.VISIBLE);
		mImageButtonList.setVisibility(View.GONE);
		mImageButtonOverflow.setVisibility(View.VISIBLE);
		mFragmentType = LIST_FRAGMENT;
		mListViewOverflow.setOnItemClickListener(getListFragmentActionListener());
		addOverflowActions(getResources().getStringArray(R.array.card_set_actions));
	}

	public void configureForSetup() {

		mImageButtonEdit.setVisibility(View.GONE);
		mImageButtonNewCardSet.setVisibility(View.GONE);
		mImageButtonList.setVisibility(View.VISIBLE);
		mImageButtonOverflow.setVisibility(View.VISIBLE);
		mFragmentType = SETUP_FRAGMENT;
	}

	public void configureForAbout() {

		mImageButtonEdit.setVisibility(View.GONE);
		mImageButtonNewCardSet.setVisibility(View.GONE);
		mImageButtonList.setVisibility(View.VISIBLE);
		mImageButtonOverflow.setVisibility(View.VISIBLE);
		mFragmentType = ABOUT_FRAGMENT;
	}

	public void configureForCards() {

		mImageButtonEdit.setVisibility(View.VISIBLE);
		mImageButtonNewCardSet.setVisibility(View.GONE);
		mImageButtonList.setVisibility(View.VISIBLE);
		mImageButtonOverflow.setVisibility(View.VISIBLE);
		mFragmentType = CARDS_FRAGMENT;
		mListViewOverflow.setOnItemClickListener(getCardFragmentActionListener());
		addOverflowActions(getResources().getStringArray(R.array.card_actions));
	}

	public void setFragmentType(int fragmentType) {

		this.mFragmentType = fragmentType;
	}

	private ArrayAdapter<String> getArrayAdapter() {

		return new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mOverflowActions) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				View view = super.getView(position, convertView, parent);

				((TextView)view).setTextColor(Color.WHITE);

				return view;
			}
		};
	}

	private OnItemClickListener getCardFragmentActionListener() {

		return new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				mListViewOverflow.setVisibility(View.GONE);

				switch(position) {

				case ACTION_ZOOM_IN:
					((MainActivity)getActivity()).doZoomIn();
					break;

				case ACTION_ZOOM_OUT:
					((MainActivity)getActivity()).doZoomOut();
					break;

				case ACTION_CARD_INFO:
					((MainActivity)getActivity()).doCardInfo();
					break;

				case ACTION_DELETE_CARD:
					((MainActivity)getActivity()).doDeleteCard();
					break;
			
				case ACTION_HELP_CARD:
					((MainActivity)getActivity()).showHelp();
					break;
				}
			}
		};
	}

	private OnItemClickListener getListFragmentActionListener() {

		return new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				mListViewOverflow.setVisibility(View.GONE);

				switch(position) {

				case ACTION_SETUP:
					((MainActivity)getActivity()).showSetupFragment();
					break;

				case ACTION_ABOUT:
					((MainActivity)getActivity()).showAboutFragment();
					break;

				case ACTION_FCE:
					((MainActivity)getActivity()).getExternal();
					break;

				case ACTION_HELP:
					((MainActivity)getActivity()).showHelp();
					break;
				}
			}
		};
	}

	private void addOverflowActions(String[] actions) {

		mOverflowActions.clear();

		for(String action : actions) {

			mOverflowActions.add(action);
		}

		mArrayAdapter.notifyDataSetChanged();
	}
}
