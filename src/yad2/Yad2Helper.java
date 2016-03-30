package yad2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Igor on 6/19/2015.
 */
public class Yad2Helper {
	
	private static final String URL = "http://www.yad2.co.il";	
	private static final String FILE_DB_NAME = "yad2.helper.db";
	
	private final Map<String, AdInfo> 	mainDb = new HashMap<String, AdInfo>();
	private final File 							dbFile;
	
	

	public Yad2Helper() throws IOException, ParseException
	{
		dbFile = new File(FILE_DB_NAME);
		
		if(!dbFile.exists()) {
			dbFile.createNewFile();
		}
			
		BufferedReader br = new BufferedReader(new FileReader(dbFile));
		
		String line;
		while ((line = br.readLine()) != null)
		{
			String key = line.substring(0, line.indexOf(" "));
			AdInfo info = new AdInfo(line.substring(line.indexOf(" ")+1));
			mainDb.put(key, info) ;
		}
		br.close(); 
	}
	
	public Collection<AdInfo> getWatchedAds(){
		Set<AdInfo> watched = new HashSet<AdInfo>();
		for(AdInfo val :  mainDb.values()){
			if (val.isWatched()) watched.add(val);
		}
		return watched;
	}

	public void onFreshLinks(File folder) throws IOException, ParseException
	{
		Set<String> links = new HashSet<String>();
		if (folder != null){
			
			for (final File htmlFile : folder.listFiles()){
				links.addAll(getFreshLinks(htmlFile, URL));
			}
			
			for (String key : links) {	

				AdInfo info = mainDb.remove(key);
				if (info != null)
				{
					info.setUpdateDateToToday();
				}
				else {
					info = new AdInfo();
					info.setId(key);
				}
				mainDb.put(key,info);
			}
		}
	}
	
	
	public void updateNewAndWatched(List<AdInfo> list) {
		
		for (AdInfo e : mainDb.values()) 
		{
			e.setIsNew(false);
		}
		
		for (AdInfo ad : list) {
			AdInfo info = mainDb.get(ad.getId());
			info.setIsWatched(true);
		}
	}
	
	public void saveDbToDisk() throws IOException
	{
		 PrintWriter out = new PrintWriter(dbFile);
		 for (String key : mainDb.keySet())
		 {
			 out.println(key + " " + mainDb.get(key));
		 }
		 out.close();
	}
	
	
	private Set<String> getFreshLinks(File input, String baseUri) throws IOException
	{
	    Document doc = Jsoup.parse(input, "UTF-8", baseUri);

	    Set<String> set = new HashSet<String>();
	    Elements links = doc.select("a[href]"); // a with href
	   
	    for (Element elem : links) {
	    	String line = elem.attr("href");
	    	if (elem.attr("href").contains("rent_info.php?NadlanID") && !elem.attr("href").contains("tivrent")){	    		
	    		StringTokenizer tk = new StringTokenizer(line);
	    		tk.nextToken("=");
	    		String id = tk.nextToken("=");	    		
				set.add(id);					
	    	}	    	
	    }
	    return set;
	}
}
