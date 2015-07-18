package yad2;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.ParseException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;

/**
 * Created by Igor on 6/19/2015.
 */
public class Yad2Helper {
	
	private static final String URL = "http://www.yad2.co.il";
	
	private static final String FULL_URL = "http://www.yad2.co.il/Nadlan/rent_info.php?NadlanID=";
	
	public Map<String, ApartmentInfo> mainDb = new HashMap<String, ApartmentInfo>();
	
	
/*	private static Map<String,ApartmentInfo> sortByValue(Map<String,ApartmentInfo> map)
	{
		LinkedList<Entry<String, ApartmentInfo>> list = new LinkedList<>(map.entrySet());
		
		Collections.sort(list, new Comparator<Entry<String, ApartmentInfo>>() {

			@Override
			public int compare(Entry<String, ApartmentInfo> o1, Entry<String, ApartmentInfo> o2) {
				o1.getValue().publishDate
				return 0;
			}
			
		});
		
	}
*/


	private void loadDbFromFile(File dbFile) throws IOException, ParseException {
		BufferedReader br = new BufferedReader(new FileReader(dbFile));
		String line;
		while ((line = br.readLine()) != null)
		{
			String key = line.substring(0, line.indexOf(" "));
			ApartmentInfo info = new ApartmentInfo(line.substring(line.indexOf(" ")+1));
			info.isNew = false;
			mainDb.put(key, info) ;
		}
		br.close(); 
	}


	
	public Set<String> getFreshLinks(File input, String baseUri) throws IOException
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
	
	private void parseWatchlistFile(File watchlistFile, List<ApartmentInfo> list) throws IOException, ParseException {
		BufferedReader br = new BufferedReader(new FileReader(watchlistFile));
		String line;
		while ((line = br.readLine()) != null)
		{
			ApartmentInfo info = new ApartmentInfo(line.substring(line.indexOf("=")+1));
			list.add(info);
		}	
		br.close();
	}

	
	private void addFreshLinksToDb(Set<String> links) throws IOException, ParseException {	
		for (String key : links) {	

			ApartmentInfo info = mainDb.remove(key);
			if (info != null)
			{
				info.setUpdateDateToToday();
			}
			else {
				info = new ApartmentInfo();
				info.setId(key);
			}
			mainDb.put(key,info);
		}
	}
	
	private void updateWatchedAdsAndNotes(List<ApartmentInfo> list) {
		for (String key : mainDb.keySet()) 
		{
			ApartmentInfo info = mainDb.get(key);
			if(!info.isNew) info.isWatched = false;
			info.updateNote("");
		}
		for (ApartmentInfo apartmentInfo : list) {
			ApartmentInfo info = mainDb.get(apartmentInfo.getId());
			info.isWatched = true;
			info.updateNote(apartmentInfo.getNote());
		}
	}
	
	public void saveDbToFile(File file) throws IOException
	{
		 PrintWriter out = new PrintWriter(file);
		 for (String key : mainDb.keySet())
		 {
			 out.println(key + " " + mainDb.get(key));
		 }
		 out.close();
	}
	
	public void addNewApartmentsToWatchlist(List<ApartmentInfo> list)
	{
		for (String key : mainDb.keySet())
		{
			ApartmentInfo info = mainDb.get(key);
			if (info.isNew || info.isWatched) 
				list.add(info);
			//info.isNew = false;
		}
	}
	
	public void generateWatchlistFile(List<ApartmentInfo> list, String start, File file) throws IOException
	{
		 	 
		 
		 Collections.sort(list,new Comparator<ApartmentInfo>() {
			@Override
			public int compare(ApartmentInfo o1, ApartmentInfo o2) {
				return o1.compareTo(o2);
			}
		}.reversed());
		 
		 PrintWriter out = new PrintWriter(file);
		 
		 for (ApartmentInfo info : list)
		 {
			 out.println(start + info);
		 }
		 out.close();
	}

	
	public File downloadPage(String urlString, File htmlFile) throws IOException
	{

		
		URL url = new URL(urlString);
		
		/*
		ReadableByteChannel rbc = Channels.newChannel(url.openStream());
		FileOutputStream fos = new FileOutputStream(htmlFile);
		fos.getChannel().transferFrom(rbc, 0, Integer.MAX_VALUE);		
		fos.flush();
		fos.close();
		*/
		FileUtils.copyURLToFile(url, htmlFile);
		
		return htmlFile;
	}		
	
	
	
	public static void main(String[] args) throws IOException, ParseException, InterruptedException {
		Yad2Helper yad2help = new Yad2Helper();

		//System.setProperty("user.dir",System.getProperty("user.dir") + "/example" );
		
		
		File dbFile = new File("yad2.helper.db");
		if(!dbFile.exists()) {
			dbFile.createNewFile();
		}
		
		//pd.downloadPage(uriString, htmlFile);
		
		yad2help.loadDbFromFile(dbFile);
		
		JFileChooser dirChooser = new JFileChooser();
		//fd.setDirectory(System.getProperty("user.dir"));
		dirChooser.setCurrentDirectory(new java.io.File(System.getProperty("user.dir")));
		dirChooser.setDialogTitle("Choose folder containg HTML files");
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirChooser.showOpenDialog(null);
		File folder = dirChooser.getSelectedFile();	
		
		Set<String> links = new HashSet<String>();
		
		if (folder != null){
			for (final File htmlFile : folder.listFiles()){
				links.addAll(yad2help.getFreshLinks(htmlFile, URL));
			}
			yad2help.addFreshLinksToDb(links);
		}
		
		Thread.sleep(100);
		
		dirChooser = new JFileChooser();
		dirChooser.setCurrentDirectory(new java.io.File(System.getProperty("user.dir")));
		dirChooser.setDialogTitle("Choose watchlist file");
		dirChooser.showOpenDialog(null);
		File watchlistFile = dirChooser.getSelectedFile();
		if (watchlistFile == null) {
			watchlistFile = new File("yad2.new.links");

			if(!watchlistFile.exists()) {
				watchlistFile.createNewFile();
			}			
		}
		
		List<ApartmentInfo> list = new LinkedList<>();
				
		//parse watch-list file into watch-list
		yad2help.parseWatchlistFile(watchlistFile, list);
								
		//update is_watched ads in main DB according watch-list
		yad2help.updateWatchedAdsAndNotes(list);
		
		yad2help.saveDbToFile(dbFile);
				
		//add all new ads to watch-list
		 list = new LinkedList<>();
		yad2help.addNewApartmentsToWatchlist(list);
		
		//generate watch-list file
		yad2help.generateWatchlistFile(list, FULL_URL,watchlistFile);	
				
	}

}
