package com.paad.earthquake;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

public class EarthquakeSearch extends Activity {
	private static String QUERY_EXTRA_KEY = "QUERY_EXTRA_KEY";

	public EarthquakeSearch() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		parseIntent(intent);
	}

	private void parseIntent(Intent intent) {
		// If the Activity was started to service a Search request,
		// extract the search query.
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String searchQuery = intent.getStringExtra(SearchManager.QUERY);

			// Perform the search, passing in the search query as an argument
			// to the Cursor Loader
			Bundle args = new Bundle();
			args.putString(QUERY_EXTRA_KEY, searchQuery);

			// Restart the Cursor Loader to execute the new query.
			//getLoaderManager().restartLoader(0, args, this);
		}
	}

}
