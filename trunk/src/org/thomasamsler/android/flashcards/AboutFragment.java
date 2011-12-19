package org.thomasamsler.android.flashcards;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class AboutFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.about_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		String version = null;
		
		 // Get version information from AndroidManifest.xml
        try {
                
                version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        }
        catch (NameNotFoundException e) {
                
                Log.w(AppConstants.LOG_TAG, "Exception accessing version information", e);
        }
        
        TextView versionTextView = (TextView)getActivity().findViewById(R.id.textViewAboutVersion);
        versionTextView.append(version);
        
        ImageButton imageButtonCancel = (ImageButton)getActivity().findViewById(R.id.imageButtonAboutClose);
		imageButtonCancel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				((ListActivity)getActivity()).showArrayListFragment(true);
			}
		});
	}
}
