package org.thomasamsler.android.flashcards;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListActivity extends FragmentActivity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the list fragment and add it as our sole content.
        if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            ArrayListFragment list = new ArrayListFragment();
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, list).commit();
        }
    }
	
	 public static class ArrayListFragment extends ListFragment {

	        @Override
	        public void onActivityCreated(Bundle savedInstanceState) {
	            super.onActivityCreated(savedInstanceState);
	            setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, WordSets.wordSetNames));
	        }

	        @Override
	        public void onListItemClick(ListView l, View v, int position, long id) {
	        	
	            Intent intent = new Intent(v.getContext(), FragmentPagerActivity.class);
	            Bundle bundle = new Bundle();
	            bundle.putInt(AppConstants.SELECTED_LIST_ITEM_KEY, position);
	            intent.putExtras(bundle);
	            startActivity(intent);
	        }
	    }
}
