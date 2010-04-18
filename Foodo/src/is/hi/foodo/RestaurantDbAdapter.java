package is.hi.foodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RestaurantDbAdapter {

	private static final String TAG = "RestaurantsDbAdapter";

	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_LAT = "lat";
	public static final String KEY_LNG = "lng";
	public static final String KEY_RATING = "rating";
	public static final String KEY_RATING_COUNT = "rating_count";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_ZIP = "zip";
	public static final String KEY_CITY = "city";
	public static final String KEY_WEBSITE = "website";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PHONE = "phone";
	public static final String KEY_PRICEGROUP = "pricegroup";

	public static final String KEY_TROWID = "_id";
	public static final String KEY_TYPE = "type";

	public static final String KEY_RID = "rid";
	public static final String KEY_TID = "tid";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Database creation SQL statement
	 */
	private static final String DATABASE_CREATE =
		"create table restaurants (_id integer primary key, "
		+ "name text not null, lat integer, lng integer, "
		+ "rating double, rating_count long, address text, " 
		+ "zip integer, city text, website text, email text, phone text," 
		+ " pricegroup integer);";

	private static final String DATABASE_T_CREATE =
		"create table types (_id integer primary key, "
		+ "type text not null);";

	private static final String RT_DATABASE_CREATE = 
		"create table restaurantstypes (rid integer not null, " 
		+ "tid integer not null, PRIMARY KEY (rid, tid));";


	private static final String DATABASE_EMPTY = "DELETE FROM restaurants;";
	private static final String DATABASE_T_EMPTY = "DELETE FROM types;";
	private static final String RT_DATABASE_EMPTY = "DELETE FROM restaurantstypes";


	private static final String DATABASE_NAME = "foodo";
	private static final String DATABASE_TABLE = "restaurants";
	private static final String DATABASE_T_TABLE = "types";
	private static final String RT_DATABASE_TABLE = "restaurantstypes";
	private static final int DATABASE_VERSION = 11;

	private final Context mCtx;


	public static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d(TAG, DATABASE_CREATE);
			db.execSQL(DATABASE_CREATE);
			Log.d(TAG, DATABASE_T_CREATE);
			db.execSQL(DATABASE_T_CREATE);
			Log.d(TAG, RT_DATABASE_CREATE);
			db.execSQL(RT_DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_T_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + RT_DATABASE_TABLE);
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
	 * 
	 */
	public long createRestaurantsTypes(long rid, long tid){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_RID, rid);
		initialValues.put(KEY_TID, tid);

		return mDb.insert(RT_DATABASE_TABLE, null, initialValues);
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

		return mDb.insert(DATABASE_T_TABLE, null, initialValues);
	}

	/**
	 * Create a new restaurant using the data provided. If the restaurant is
	 * successfully created return the new rowId for that restaurant, 
	 * otherwise return a -1 to indicate failure.
	 *
	 * @param id the id of the restaurant
	 * @param name the name of the restaurant
	 * @param lat restaurant GPS latitude
	 * @param lng restaurant GPS longitude
	 * @param rating restaurants rating
	 * @param address
	 * @param zip
	 * @param city
	 * @param website
	 * @param email
	 * @param phone
	 * @return rowId or -1 if failed
	 */
	public long createRestaurant(long id, String name, int lat, int lng, double rating, long rating_count, 
			String address, int zip, String city, String website, String email, String phone,
			int pricegroup) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ROWID, id);
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_LAT, lat);
		initialValues.put(KEY_LNG, lng);
		initialValues.put(KEY_RATING, rating);
		initialValues.put(KEY_RATING_COUNT, rating_count);
		initialValues.put(KEY_ADDRESS, address);
		initialValues.put(KEY_ZIP, zip);
		initialValues.put(KEY_CITY, city);
		initialValues.put(KEY_WEBSITE, website);
		initialValues.put(KEY_EMAIL, email);
		initialValues.put(KEY_PHONE, phone);
		initialValues.put(KEY_PRICEGROUP, pricegroup);

		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	/**
	 * Create a new restaurant using the data provided. If the restaurant is
	 * successfully created return the new rowId for that restaurant, 
	 * otherwise return a -1 to indicate failure.
	 *
	 * @param name the name of the restaurant
	 * @param lat restaurant GPS latitude
	 * @param lng restaurant GPS longitude
	 * @param rating restaurants rating
	 * @return rowId or -1 if failed
	 * @deprecated Restaurants are loaded from web service
	 */
	@Deprecated
	public long createRestaurant(String name, int lat, int lng, double rating, long rating_count) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_LAT, lat);
		initialValues.put(KEY_LNG, lng);
		initialValues.put(KEY_RATING, rating);
		initialValues.put(KEY_RATING_COUNT, rating_count);

		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	/**
	 * Delete the type with the given rowId
	 * 
	 * @param rowId id of type to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteType(long rowId) {

		return mDb.delete(DATABASE_T_TABLE, KEY_TROWID + "=" + rowId, null) > 0;
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
	 * Delete the relation between restaurants 
	 * and types with the given rowId
	 * 
	 * @param rId id of a restaurant to delete
	 * @param tid id of a type to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteRestaurantsTypes(long rId, long tId){
		return mDb.delete(RT_DATABASE_TABLE, 
				KEY_RID + "=" + rId + " AND " + KEY_TID + "=" + tId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all types in the database
	 * 
	 * @return Cursor over all types
	 */
	public Cursor fetchAllTypes() {
		return mDb.query(DATABASE_T_TABLE, new String[] {KEY_TROWID, KEY_TYPE}, null, 
				null, null, null, null);
	}

	/**
	 * Return a Cursor over the list of all restaurants in the database
	 * 
	 * @return Cursor over all restaurants
	 */
	public Cursor fetchAllRestaurants(CharSequence ratingFrom, 
			CharSequence ratingTo,
			boolean lowPrice,
			boolean mediumPrice,
			boolean highPrice,
			boolean[] checkedType,
			int[] typeId) {
		String sql = "SELECT " + KEY_ROWID + ", " + KEY_ADDRESS + ", " + KEY_NAME + ", " + KEY_LAT
		+ ", " + KEY_ZIP + ", " + KEY_CITY + ", " + KEY_LNG + ", " + KEY_RATING + ", " + KEY_RATING_COUNT +
		" FROM " + DATABASE_TABLE + 
		" INNER JOIN " + RT_DATABASE_TABLE + 
		" ON " + DATABASE_TABLE + "." + KEY_ROWID + "=" + 
		RT_DATABASE_TABLE + "." + KEY_RID +
		" WHERE " + KEY_RATING + ">= " + ratingFrom + " AND " + 
		KEY_RATING + "<=" + ratingTo + " " + 
		this.getPricegroup(lowPrice, mediumPrice, highPrice) + 
		" " + this.getTypes(checkedType, typeId) +
		" GROUP BY " + KEY_ROWID + 
		" ORDER BY " + KEY_NAME;
		Log.d(TAG,sql);
		return mDb.rawQuery(sql, null);
		/* return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME,
                KEY_LAT, KEY_LNG, KEY_RATING, KEY_RATING_COUNT}, KEY_RATING + ">= " +
                Filter.ratingFrom + " AND " + KEY_RATING + "<=" + Filter.ratingTo, 
                null, null, null, null);*/
	}

	private String getPricegroup(boolean lowPrice,boolean mediumPrice,boolean highPrice){
		String result = "";
		if(lowPrice && mediumPrice && !highPrice) {
			result = " AND (" + KEY_PRICEGROUP + "=1 OR " + KEY_PRICEGROUP + "=2)";
		}
		if(lowPrice && !mediumPrice && highPrice) {
			result = " AND (" + KEY_PRICEGROUP + "=1 OR " + KEY_PRICEGROUP + "=3)";
		}
		if(!lowPrice && mediumPrice && highPrice) {
			result = " AND (" + KEY_PRICEGROUP + "=2 OR " + KEY_PRICEGROUP + "=3)";
		}
		if(lowPrice && !mediumPrice && !highPrice) {
			result = " AND (" + KEY_PRICEGROUP + "=1)";
		}
		if(!lowPrice && mediumPrice && !highPrice) {
			result = " AND (" + KEY_PRICEGROUP + "=2)";
		}
		if(!lowPrice && !mediumPrice && highPrice) {
			result = " AND (" + KEY_PRICEGROUP + "=3)";
		}
		return result;
	}

	private String getTypes(boolean[] checkedType, int[] typesId){
		boolean first = true;
		String result = "";
		for(int i = 0; i != typesId.length; i++)
		{
			if (checkedType[i])
			{
				if (!first) {
					result += " OR ";
				} 
				result += KEY_TID + "=" + typesId[i];

				first = false;
			}
		}
		if(!first){ // Items found
			result = "AND (" + result + ")";
		}
		return result;
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
					KEY_LAT, KEY_LNG, KEY_RATING, KEY_RATING_COUNT, KEY_ADDRESS, 
					KEY_ZIP, KEY_CITY, KEY_WEBSITE, KEY_EMAIL, KEY_PHONE, 
					KEY_PRICEGROUP}, KEY_ROWID + "=" + rowId, null,
					null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchRestaurantTypes(long rowId) throws SQLException {
		String sql = "SELECT " + KEY_TYPE + " FROM " + DATABASE_T_TABLE + ", " + RT_DATABASE_TABLE + 
		" WHERE " + KEY_TID + " = " + KEY_TROWID + " AND " + KEY_RID + " = " + rowId;
		Log.d(TAG,sql);
		return mDb.rawQuery(sql, null);
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
	 * @param rating value to set rating to
	 * @param address
	 * @param zip
	 * @param city
	 * @param website
	 * @param email
	 * @param phone
	 * @return true if the restaurant was successfully updated, false otherwise
	 */
	public boolean updateRestaurant(long rowId, String name, int lat, int lng, double rating, double rating_count,
			String address, int zip, String city, String website, String email, String phone,
			int pricegroup) {
		ContentValues args = new ContentValues();


		args.put(KEY_NAME, name);
		args.put(KEY_LAT, lat);
		args.put(KEY_LNG, lng);
		args.put(KEY_RATING, rating);
		args.put(KEY_RATING_COUNT, rating_count);
		args.put(KEY_ADDRESS, address);
		args.put(KEY_ZIP, zip);
		args.put(KEY_CITY, city);
		args.put(KEY_WEBSITE, website);
		args.put(KEY_EMAIL, email);
		args.put(KEY_PHONE, phone);
		args.put(KEY_PRICEGROUP, pricegroup);

		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}   

	/**
	 * Clears all restaurants and types relations
	 */
	public void emptyDatabase() {
		mDb.execSQL(DATABASE_EMPTY);
		mDb.execSQL(RT_DATABASE_EMPTY);
	}
	/**
	 * Clears all types from database
	 */
	public void emptyTypesTable(){
		mDb.execSQL(DATABASE_T_EMPTY);
	}

	public void updateRating(long rowId, float rating) {
		ContentValues args = new ContentValues();
		args.put(KEY_RATING, rating);

		mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null);
	}

	public boolean hasRestaurant(Long rowId) {
		Cursor c = this.fetchRestaurant(rowId);
		return (c.getCount() > 0);
	}
}
