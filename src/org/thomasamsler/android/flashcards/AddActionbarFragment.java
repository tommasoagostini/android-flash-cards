package org.thomasamsler.android.flashcards;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class AddActionbarFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.add_actionbar_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	
		ImageButton imageButtonShowList = (ImageButton)getActivity().findViewById(R.id.imageButtonAddShowList);
		imageButtonShowList.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {

				((ListActivity)getActivity()).showArrayListFragment(true);
			}
		});
	}
}
