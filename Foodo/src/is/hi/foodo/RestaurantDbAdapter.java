package is.hi.foodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RestaurantDbAdapter {
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_LAT = "lat";
	public static final String KEY_LNG = "lng";
	
    private static final String TAG = "RestaurantsDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    /**
     * Database creation SQL statement
     */
    private static final String DATABASE_CREATE =
            "create table restaurants (_id integer primary key autoincrement, "
                    + "name text not null, lat integer, lng integer);";
    
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "restaurants";
    private static final int DATABASE_VERSION = 2;
    
    private final Context mCtx;
    
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

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
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
            onCreate(db);
        }
    }
    
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public RestaurantDbAdapter(Context ctx) {
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
    public RestaurantDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    /**
     * Create a new restaurant using the data provided. If the restaurant is
     * successfully created return the new rowId for that restaurant, 
     * otherwise return a -1 to indicate failure.
     * 
     * @param name the name of the restaurant
     * @param lat restaurant GPS latitude
     * @param lng restaurant GPS longitude
     * @return rowId or -1 if failed
     */
    public long createRestaurant(String name, int lat, int lng) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_LAT, lat);
        initialValues.put(KEY_LNG, lng);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }
    
    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteRestaurant(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    /**
     * Return a Cursor over the list of all restaurants in the database
     * 
     * @return Cursor over all restaurants
     */
    public Cursor fetchAllRestaurants() {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME,
                KEY_LAT, KEY_LNG}, null, null, null, null, null);
    }
    
    /**
     * Return a Cursor positioned at the restaurants that matches the given rowId
     * 
     * @param rowId id of restaurant to retrieve
     * @return Cursor positioned to matching restaurant, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchRestaurant(long rowId) throws SQLException {

        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME,
                        KEY_LAT, KEY_LNG}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    /**
     * Update the restaurant using the details provided. The restaurant to 
     * be updated is specified using the rowId, and it is altered to use 
     * the values passed in
     * 
     * @param rowId id of restaurant to update
     * @param name value to set name to
     * @param lat value to set latitude to
     * @param lng value to set longitude to
     * @return true if the restaurant was successfully updated, false otherwise
     */
    public boolean updateRestaurant(long rowId, String name, int lat, int lng) {
        ContentValues args = new ContentValues();
        
        
        args.put(KEY_NAME, name);
        args.put(KEY_LAT, lat);
        args.put(KEY_LNG, lng);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

}
