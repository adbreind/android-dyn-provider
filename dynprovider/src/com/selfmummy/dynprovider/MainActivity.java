package com.selfmummy.dynprovider;

import java.util.Date;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

public class MainActivity extends Activity {

	protected static final String TAG = "Main Activity and Fragment";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
    	
		private Uri allEntitiesUri;
		private String[] fields;
		private int FIELD_TIME = 0;
		private int FIELD_MESSAGE = 1;

    	public PlaceholderFragment() {        	
        }               
    	
    	@Override
    	public void onAttach(Activity activity) {
    		super.onAttach(activity);
    		allEntitiesUri = Uri.parse("content://" + getString(R.string.p_authority) + "/" + getString(R.string.p_entity));    
    		fields = getResources().getStringArray(R.array.p_field_names);
    	}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {        	
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            
            rootView.findViewById(R.id.butRead).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {								
					Cursor c = getActivity().getContentResolver().query(allEntitiesUri, null, null, null, fields[FIELD_TIME] + " DESC");
					Log.d(TAG, "Found " + c.getCount() + " records");
				}
			});
            
            final EditText editMessage = (EditText)rootView.findViewById(R.id.editMessage);
            
            rootView.findViewById(R.id.butWrite).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {								
					ContentValues values = new ContentValues();
					values.put(fields[FIELD_MESSAGE], editMessage.getText().toString());
					values.put(fields[FIELD_TIME], new Date().getTime());
					getActivity().getContentResolver().insert(allEntitiesUri, values);
				}
			});
            
            return rootView;
        }
    }

}
