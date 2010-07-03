package uk.co.seesaw.android.whatsonseesaw;

import java.util.List;

import uk.co.seesaw.android.whatsonseesaw.SeesawSeachHelper.ApiException;
import uk.co.seesaw.android.whatsonseesaw.SeesawSeachHelper.ParseException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class WhatsOnSeesaw extends Activity {
	final static String TAG = "WhatsOnSeesaw";
    private EditText searchEntry;
	private TableLayout searchResult;
	private Context context;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        searchEntry = (EditText)findViewById(R.id.searchEntry);
        searchResult = (TableLayout)findViewById(R.id.searchResults);
        searchEntry.addTextChangedListener(new SearchEntryTextWatcher());
        
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

		@Override
		public void afterTextChanged(Editable text) {
			Log.i(TAG, "called afterTextChanged");
			String textToSearch = text.toString();
			Log.i(TAG, "textToSeach: " + textToSearch );
			
			try{
				List<SearchResult> results = SeesawSeachHelper.getResults(textToSearch);
				searchResult.removeAllViews();
				
				for( SearchResult result : results) {
					TextView resultView = new TextView(context);
					resultView.setText(result.title);
					
					TableRow resultRow = new TableRow(context);
					resultRow.addView(resultView);
					//resultRow.setOnClickListener()
					
					searchResult.addView(resultRow);					
				}

			} catch (ApiException e) {
				Log.e(TAG, "api exception", e);
			} catch (ParseException e) {
				Log.e(TAG, "parse exception", e);
			} 

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
    
}