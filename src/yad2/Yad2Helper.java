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
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;

/**
 * Created by Igor on 6/19/2015.
 */
public class Yad2Helper {
	
	static final String URL = "http://www.yad2.co.il";
	static final String FULL_URL = "http://www.yad2.co.il/Nadlan/rent_info.php?NadlanID=";
	
	public Map<String, ApartmentInfo> db = new HashMap<String, ApartmentInfo>();
	
	public static void main(String[] args) throws IOException {
		Yad2Helper yad2help = new Yad2Helper();
		//String uriString  = "http://csb.stanford.edu/class/public/pages/sykes_webdesign/05_simple.html";		
		//String uriString  = "http://www.yad2.co.il/Nadlan/rent.php?AreaID=5&City=&HomeTypeID=&fromRooms=&untilRooms=&fromPrice=3500&untilPrice=3500&PriceType=1&FromFloor=&ToFloor=&EnterDate=&Info=";
		
		//File htmlFile = new File("yad2.example.html");
				
		File dbFile = new File("yad2.helper.db");
		if(!dbFile.exists()) {
			dbFile.createNewFile();
		}
			
		
		
		//pd.downloadPage(uriString, htmlFile);
		
		yad2help.loadDb(dbFile);
		
		JFileChooser dirChooser = new JFileChooser();
		//fd.setDirectory(System.getProperty("user.dir"));
		dirChooser.setCurrentDirectory(new java.io.File(System.getProperty("user.dir")));
		dirChooser.setDialogTitle("Choose folder containg HTML files");
		dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dirChooser.showOpenDialog(null);
		File folder = dirChooser.getSelectedFile();	
		
		//File folder = new File("./html");
		
		Set<String> links = new HashSet<String>();
		
		for (final File htmlFile : folder.listFiles()){
			links.addAll(yad2help.getFreshLinks(htmlFile, URL));
		}
		
		
		
		//output file
		File outFile = new File("yad2.new.links");
		if(!outFile.exists()) {
			outFile.createNewFile();
		}
		PrintWriter out = new PrintWriter(outFile);
		for (String link : links)
			if (!yad2help.db.containsKey(link))
				out.println(FULL_URL + link);	
		out.close();
		
		yad2help.addToDb(links);
		yad2help.saveDbToFile(dbFile);		
	}
	


	private void loadDb(File dbFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(dbFile));
		String line;
		while ((line = br.readLine()) != null)
		{
			StringTokenizer tk = new StringTokenizer(line);
			String key = tk.nextToken();
			ApartmentInfo info = new ApartmentInfo();
			info.publishDate = tk.nextToken();
			info.recentDate = tk.nextToken();
			//info.notes = tk.nextToken();
			db.put(key, info) ;
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

	
	private void addToDb(Set<String> links) throws IOException {	
		for (String key : links) {	
			Calendar cal = Calendar.getInstance();
			String date = cal.get(Calendar.YEAR) + "/"
					+ cal.get(Calendar.MONTH) + "/" 
					+cal.get(Calendar.DAY_OF_MONTH);
			ApartmentInfo info = db.remove(key);
			if (info != null)
			{
				info.recentDate = date;
			}
			else {
				info = new ApartmentInfo();
				info.publishDate = date;
				info.recentDate = date;
			}
			db.put(key,info);
		}
	}
	
	public void saveDbToFile(File dbFile) throws IOException
	{
		 PrintWriter out = new PrintWriter(dbFile);
		 for (String key : db.keySet())
		 {
			 ApartmentInfo info = db.get(key);
			 out.println(key + "  " + info.publishDate + " " + info.recentDate);
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
}
