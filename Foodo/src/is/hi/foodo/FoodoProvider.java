package is.hi.foodo;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class FoodoProvider extends ContentProvider {
	
    private static final String TAG = "FoodoProvider";
    
    private static final int RESTAURANTS = 1;
    private static final int RESTAURANT_ID = 2;
    public static final Uri CONTENT_URI = Uri.parse("content://is.hi.foodo.foodoprovider");
    
    private static final UriMatcher sUriMatcher;
    
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		switch (sUriMatcher.match(uri)) {

		}
		
		return null;
	}


	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	}

}
