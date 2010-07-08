package uk.co.seesaw.android.whatsonseesaw;

import java.util.Collections;
import java.util.List;

import uk.co.seesaw.android.whatsonseesaw.SeesawSeachHelper.ApiException;
import uk.co.seesaw.android.whatsonseesaw.SeesawSeachHelper.ParseException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class WhatsOnSeesaw extends Activity {
	final static String TAG = "WhatsOnSeesaw";
    private EditText searchEntry;
	private ListView searchResults;
	private Context context;
	private Animation magnify;
	private ProgressBar searchProgressBar;
	private SearchResultListHandler searchResultsListHandler;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        searchEntry = (EditText)findViewById(R.id.searchEntry);
        searchResults = (ListView)findViewById(R.id.searchResults);
        searchEntry.addTextChangedListener(new SearchEntryTextWatcher());
        magnify = AnimationUtils.loadAnimation( this, R.anim.magnify );
        searchResultsListHandler = new SearchResultListHandler();
        searchProgressBar = (ProgressBar) findViewById(R.id.progress);
        
        context = this;
        SeesawSeachHelper.prepareUserAgent(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about: {
                showAbout();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Show an about dialog that cites data sources.
     */
    protected void showAbout() {
        // Inflate the about message contents
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

        // When linking text, force to always use default color. This works
        // around a pressed color state bug.
        TextView textView = (TextView) messageView.findViewById(R.id.about_credits);
        int defaultColor = textView.getTextColors().getDefaultColor();
        textView.setTextColor(defaultColor);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.seesaw_logo);
        builder.setTitle(R.string.app_name);
        builder.setView(messageView);
        builder.create();
        builder.show();
    }

	private class SearchEntryTextWatcher implements TextWatcher {

		//private List<SearchResult> results;

		@Override
		public void afterTextChanged(Editable text) {
			Log.i(TAG, "called afterTextChanged");
			String textToSearch = text.toString();
			Log.i(TAG, "textToSeach: " + textToSearch );
			searchProgressBar.setVisibility(View.VISIBLE);
			new LookupTask().execute(textToSearch);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			Log.i(TAG, "called beforeTextChanged");
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			Log.i(TAG, "called onTextChanged");
		}
    }
	
	private class SearchResultListHandler implements AdapterView.OnItemSelectedListener, OnItemClickListener {

		private List<SearchResult> results;
		
		public void update(List<SearchResult> results) {
			this.results = results;

			ArrayAdapter resultsAdapter = new ArrayAdapter( context, R.layout.row, results );
			searchResults.setAdapter(resultsAdapter);
			searchResults.setOnItemSelectedListener( searchResultsListHandler );
			searchResults.setOnItemClickListener(searchResultsListHandler );

		}
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
			Log.i(TAG, "called onItemSelected");
			v.startAnimation(magnify);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			Log.i(TAG, "called onNothingSelected");
			
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.i(TAG, "called onItemClick");
			if( position < results.size()) {
				SearchResult result = results.get(position);
				Log.i(TAG, "clicked on " + result);
				Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.url));
				startActivity(myIntent);
			} else {
				Log.i(TAG, "I don't have an item at poisition " + position);
			}
		}		
	}
	
	//TODO async task
	private class LookupTask extends AsyncTask<String, Void, Void> {

		private List<SearchResult> results = Collections.EMPTY_LIST;
		
		@Override
		protected Void doInBackground(String... text) {
			String textToSearch = text[0];
			
			try{
				results = SeesawSeachHelper.getResults(textToSearch);
			} catch (ApiException e) {
				Log.e(TAG, "api exception", e);
			} catch (ParseException e) {
				Log.e(TAG, "parse exception", e);
			} 

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			searchProgressBar.setVisibility(View.INVISIBLE);
			searchResultsListHandler.update(results);
		}
	}
}