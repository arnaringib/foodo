package is.hi.foodo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class FoodoList extends ListActivity {
	ArrayList<String> mList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		
		getList();
		
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				R.layout.listrow,
				R.id.restaurant,
				mList);
		setListAdapter(listAdapter);
		getListView().setTextFilterEnabled(true); //Let user search by typing the name.
	}
	
	public void getList(){
		this.mList = new ArrayList<String>();
		try {
			JSONObject result = getResponse("http://foodo.siggijons.net/api/restaurants.json");
			
			JSONArray list = result.getJSONArray("Restaurants");
			for (int i = 0; i < list.length(); i++)
			{
				JSONObject o = list.getJSONObject(i);
				mList.add(o.getString("name"));
			}
		}
		catch(Exception e)
		{
			
		}
	}
	
	private JSONObject getResponse(String service_url) {
		try {
			URL url = new URL(service_url + "");
			URLConnection connection = url.openConnection();
			
			BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line;
			while (( line = reader.readLine()) != null)
			{
				builder.append(line);
			}
			
			JSONObject json;
			
			json = new JSONObject(builder.toString());
			return json.getJSONObject("responseData");
		}
		catch (Exception e) {
			//TODO something
			return null;
		}
	}
	
}
