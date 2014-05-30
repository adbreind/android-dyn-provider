package com.selfmummy.dynprovider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class DynProvider extends ContentProvider {
    private static final String TAG = "DynProvider";
    
    private static String sAuthority;
    private static String sEntity;    
    private static String sSingleEntityMimeType;
    private static String sMultiEntityMimeType;

    public final String FIELD_ID = BaseColumns._ID;

    private static final int URI_UNKNOWN = 0;
    private static final int URI_MULTIPLE_ENTRY = 1;
    private static final int URI_SINGLE_ENTRY = 2;

    private static UriMatcher sUriMatcher = new UriMatcher(URI_UNKNOWN);
    private DbAdapter mAdapter;
    
    private static JSONObject sConfig;
    private static final String AUTH_CONFIG = "authority";
    private static final String ENTITY_CONFIG = "entity";
    private static final String ENTITY_MIME_MINOR_CONFIG = "mimeMinor";           

    private void config() throws IOException {    	
    	Context ctx = getContext();
    	String path =  ctx.getApplicationInfo().dataDir + "/" + ctx.getString(R.string.p_authority) + ".config";
    	try {
    		readConfigFromFile(path);
    	} catch (FileNotFoundException fileNotFound) {
    		Log.d(TAG, "No config file. Writing one from resources to " + path);
    		sConfig = new JSONObject();    		
    		try {
				sConfig.put(AUTH_CONFIG, ctx.getString(R.string.p_authority));    	    
				sConfig.put(ENTITY_CONFIG, ctx.getString(R.string.p_entity));    		    		    	
				sConfig.put(ENTITY_MIME_MINOR_CONFIG, ctx.getString(R.string.p_mime_minor));
			} catch (JSONException e) {
				//should never happen since all Strings are constants, right? :)
				e.printStackTrace();
			}
    		boolean ok = writeConfigToFile(path);
    		if (ok) {    			
    			config();
    			return;
    		} else {
    			throw new IOException("Could not write new default config");
    		}
    	}    	    	    	
    }
    
    private void readConfigFromFile(String path) throws FileNotFoundException {   
    	BufferedReader reader = new BufferedReader(new FileReader(path));
    	try {
			sConfig = new JSONObject(reader.readLine());
			reader.close();		
			Log.d(TAG, "Loaded config from " + path);
			sAuthority = sConfig.getString(AUTH_CONFIG);
	    	sEntity = sConfig.getString(ENTITY_CONFIG);
	    	sSingleEntityMimeType = "vnd.android.cursor.item/" + sConfig.getString(ENTITY_MIME_MINOR_CONFIG);
			sMultiEntityMimeType = "vnd.android.cursor.dir/" + sConfig.getString(ENTITY_MIME_MINOR_CONFIG);
		} catch (JSONException | IOException e) {				
			e.printStackTrace();
			throw new IllegalArgumentException("Cannot config from " + path + " -- file or JSON is bad.");
		}    	
    			  
        sUriMatcher.addURI(sAuthority, sEntity, URI_MULTIPLE_ENTRY);
        sUriMatcher.addURI(sAuthority, sEntity + "/#", URI_SINGLE_ENTRY);    	    	
    }
    
    private boolean writeConfigToFile(String path) {
    	Writer output = null;
    	try {
			output = new BufferedWriter(new FileWriter(path));
			output.write(sConfig.toString());
	    	output.close();
	    	Log.d(TAG, "Wrote config to " + path);
	    	return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return false;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        int type = sUriMatcher.match(uri);
        if (type == URI_MULTIPLE_ENTRY)
            return sMultiEntityMimeType;
        else if (type == URI_SINGLE_ENTRY)
            return sSingleEntityMimeType;
        else throw new IllegalArgumentException("Unknown URI!");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = mAdapter.insert(values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public boolean onCreate() {    	
    	try {    	
			config();
		} catch (IOException e) { 
			e.printStackTrace();
			return false;
		}        
    	Context ctx = getContext();
        mAdapter = new DbAdapter(ctx);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        Cursor c = mAdapter.getAll(sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
