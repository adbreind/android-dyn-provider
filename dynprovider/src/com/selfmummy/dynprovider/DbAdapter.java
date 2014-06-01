package com.selfmummy.dynprovider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DbAdapter extends SQLiteOpenHelper {    
    private static final int VERSION = 1; 	// TODO add support for schema migration: change in resource relative to config file
    										// could trigger updates in provider and here

    private String mTable; // TODO allow decoupling table, columns from entity, fields; reinstate read/write with column mapping
    private String[] mColumns;
    private String[] mTypes;
    
    private static final String COL_ID = BaseColumns._ID;    

    /*
    static Map<String, String> columnMap;

    static {
        columnMap = new HashMap<String, String>();
        columnMap.put(DynProvider.FIELD_ID, COL_ID + " AS " + DynProvider.FIELD_ID);
        columnMap.put(DynProvider.FIELD_MESSAGE, COL_MESSAGE + " AS " + DynProvider.FIELD_MESSAGE);        
        columnMap.put(DynProvider.FIELD_TIME, COL_TIME + " AS " + DynProvider.FIELD_TIME);
    }
    */

    public DbAdapter(Context ctx) {
        super(ctx, ctx.getString(R.string.p_authority) + ".db", null, VERSION);
        mTable = ctx.getString(R.string.p_entity);
        mColumns = ctx.getResources().getStringArray(R.array.p_field_names); //fix field-column coupling, see above
        mTypes = ctx.getResources().getStringArray(R.array.p_field_types); // fix see above
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
    	StringBuilder sql = new StringBuilder("CREATE TABLE " + mTable + " (" + COL_ID + " INTEGER PRIMARY KEY");
    	for (int i=0; i<mColumns.length; i++)
    		sql.append(", " + mColumns[i] + " " + mTypes[i]);
    	sql.append(");");
        db.execSQL(sql.toString());                
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + mTable);
        onCreate(db);
    }

    public long insert(ContentValues values) {
        return getWritableDatabase().insertWithOnConflict(mTable, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /*
    public long insertWithColumnMap(ContentValues values) {
        ContentValues mapped = new ContentValues();

        for (Map.Entry<String, Object> entry : values.valueSet()) {
            Object value = entry.getValue();
            if (value instanceof String)
                mapped.put(columnMap.get(entry.getKey()).split(" ")[0], (String)entry.getValue());
            else if (value instanceof Long)
                mapped.put(columnMap.get(entry.getKey()).split(" ")[0], (Long)entry.getValue());
        }
        return getWritableDatabase().insertWithOnConflict(TABLE, null, mapped, SQLiteDatabase.CONFLICT_REPLACE);
    }
    */

    public Cursor getAll(String sortOrder) {
        return getReadableDatabase().query(mTable, null, null, null, null, null, sortOrder);
    }
    
    /*
    public Cursor getAllWithColumnMap() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TABLE);
        builder.setProjectionMap(columnMap);
        return builder.query(db, null, null, null, null, null, COL_TIME + " DESC");
    }
    */
    
    public Cursor get(long id) {
    	return getReadableDatabase().query(mTable, null, COL_ID + " = ?", new String[] { Long.toString(id) }, null, null, null);
    }

	public int delete(long id) {
		SQLiteDatabase db = getWritableDatabase();
		return db.delete(mTable, COL_ID + "= ?", new String[] { Long.toString(id) });		
	}

	public int update(long id, ContentValues values) {
		SQLiteDatabase db = getWritableDatabase();
		return db.update(mTable, values, COL_ID + "= ?", new String[] { Long.toString(id) });		
	}
   
}
