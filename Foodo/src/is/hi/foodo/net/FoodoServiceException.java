package is.hi.foodo.net;

import android.app.AlertDialog;
import android.content.Context;

/* 
Usage: 
	try {
		// ...
	} catch (FoodoServiceException e) {
		e.alertUser(this);
	}
*/
public class FoodoServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FoodoServiceException(String message) {
		super(message);
	}
	
    public void alertUser(Context context){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context); 
        dialog.setTitle("WARNING"); //TODO add to strings.xml
        dialog.setMessage(this.toString());
        dialog.setNeutralButton("Ok", null); //TODO add to string.xml
        dialog.create().show();
    }
    
}
