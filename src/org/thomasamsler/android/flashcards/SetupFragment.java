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
import android.view.ViewGroup;
import android.widget.ImageButton;

public class SetupFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.setup_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		ImageButton imageButtonSave = (ImageButton)getActivity().findViewById(R.id.imageButtonSetupSave);
		imageButtonSave.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				// TODO
				/*
				 * Test user name via flash card exchange get_user call
				 */
			}
		});
		
		ImageButton imageButtonCancel = (ImageButton)getActivity().findViewById(R.id.imageButtonSetupCancel);
		imageButtonCancel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				((ListActivity)getActivity()).showArrayListFragment(true);
			}
		});
	}
}
