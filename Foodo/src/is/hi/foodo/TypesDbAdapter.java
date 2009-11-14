package is.hi.foodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TypesDbAdapter {

	private static final String TAG = "TypesDbAdapter";
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_TYPE = "type";
	
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    /**
     * Database creation SQL statement
     */
    private static final String DATABASE_CREATE =
            "create table types (_id integer primary key, "
                    + "type text not null);";
       
    private static final String DATABASE_EMPTY = "DELETE FROM types;";
    
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "types";
    private static final int DATABASE_VERSION = 7;
    
    private final Context mCtx;
    
    public static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
    
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public TypesDbAdapter(Context ctx) {
        mCtx = ctx;
    }
    
    /**
     * Open the restaurant database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public TypesDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    /**
     * Create a new type using the data provided. If the type is
     * successfully created return the new rowId for that type, 
     * otherwise return a -1 to indicate failure.
     *
     * @param id the id of the type
     * @param type a type of a restaurant
     * @return rowId or -1 if failed
     */
    public long createType(long id, String type) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, id);
        initialValues.put(KEY_TYPE, type);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
       
    /**
     * Delete the type with the given rowId
     * 
     * @param rowId id of type to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteType(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Return a Cursor over the list of all types in the database
     * 
     * @return Cursor over all types
     */
    public Cursor fetchAllTypes() {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TYPE}, null, 
                null, null, null, null);
    }
    
    /**
     * Return a Cursor positioned at the type that matches the given rowId
     * 
     * @param rowId id of type to retrieve
     * @return Cursor positioned to matching type, if found
     * @throws SQLException if type could not be found/retrieved
     */
    public Cursor fetchType(long rowId) throws SQLException {

        Cursor mCursor =
                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TYPE},
                		KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
       
    public void emptyDatabase() {
    	mDb.execSQL(DATABASE_EMPTY);
    }
}
