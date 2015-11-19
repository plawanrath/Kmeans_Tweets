import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.*;

public class readData {

	HashMap<Long,String> tweetDataList = new HashMap<Long,String>();
	
	String filename;
	public readData(String filename)
	{
		this.filename = filename;
	}
	
	public void createJsonObjects()
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();
			while(line != null)
			{
				this.storeInObject(line);
				line = br.readLine();
			}
		}	catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void storeInObject(String jsonString) throws JSONException
	{
		JSONObject ObjValue = new JSONObject(jsonString);
		String tweet = ObjValue.getString("text");
		long idF = ObjValue.getLong("id");
		this.tweetDataList.put(idF, tweet);
	}
	
	public ArrayList<Long> parseInitialCentroids(String filename)
	{
		ArrayList<Long> result = new ArrayList<Long>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();
			int i=0;
			while(line != null)
			{
				String[] value = line.split(",");
				result.add(Long.parseLong(value[0]));
				line = br.readLine();
			}			
		}	catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
}
