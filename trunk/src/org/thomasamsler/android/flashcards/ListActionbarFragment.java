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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ListActionbarFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.list_actionbar_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
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
						
						String newFileName = editText.getText().toString().trim();
						
						if(null == newFileName || "".equals(newFileName)) {
							
							Toast.makeText(getActivity().getApplicationContext(), R.string.new_card_set_dialog_message_warning2, Toast.LENGTH_LONG).show();
						}
						else {
							
							String [] fileNames = getActivity().getApplicationContext().fileList();
							boolean fileNameExists = false;
							
							for(String fileName : fileNames) {
								
								if(newFileName.equals(fileName)) {
									
									fileNameExists = true;
									break;
								}
							}
							
							if(fileNameExists) {
								
								Toast.makeText(getActivity().getApplicationContext(), R.string.new_card_set_dialog_message_warning1, Toast.LENGTH_LONG).show();
							}
							else {
								
								FileOutputStream fos;
								PrintStream ps = null;
								
								try {
									
									fos = getActivity().getApplicationContext().openFileOutput(newFileName, Context.MODE_PRIVATE);
									ps = new PrintStream(fos);
									
								}
								catch(FileNotFoundException e) {
									
									Log.w(AppConstants.LOG_TAG, "FileNotFoundException: Was not able to create new file", e);
								}
								catch(IllegalArgumentException e) {
									
									Log.w(AppConstants.LOG_TAG, "IllegalArgumentException: Was not able to create new file", e);
									Toast.makeText(getActivity().getApplicationContext(), R.string.new_card_set_dialog_message_warning2, Toast.LENGTH_SHORT).show();
								}
								finally {
									
									if(null != ps) {
										
										ps.close();
									}
								}
								
								((ListActivity)getActivity()).addCardSet(new CardSet(newFileName));
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
	}
}
