package org.thomasamsler.android.flashcards;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PageFragment extends Fragment {
	
	private final static String WORD_KEY = "word";
	private final static String MAX_KEY = "max";
	private final static String CURRENT_KEY = "current";
	
	public static PageFragment newInstance(String word, int wordCount, int totalWords) {
		
		PageFragment pageFragment = new PageFragment();
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
		
	    View view = inflater.inflate(R.layout.fragment, container, false);
	    TextView textView = (TextView) view.findViewById(R.id.textView1);
	    textView.setText(getArguments().getString(WORD_KEY));
	    
	    TextView foo = (TextView) view.findViewById(R.id.textView2);
	    StringBuilder sb = new StringBuilder();
	    sb.append(getArguments().getInt(CURRENT_KEY));
	    sb.append(" of ");
	    sb.append(getArguments().getInt(MAX_KEY));
	    foo.setText(sb.toString());
	    
	    return view;
	}
}
