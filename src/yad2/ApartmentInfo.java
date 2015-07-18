package yad2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ApartmentInfo implements Comparable<ApartmentInfo> {
	
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	private String id = "";
	public boolean isNew = false;
	public boolean isWatched = false;
	private Date updateDate;
	private Date publishDate;		
	private String notes = "";
	


	public ApartmentInfo() {
		isNew = true;
		isWatched = true;
		updateDate = Calendar.getInstance().getTime();
		publishDate = Calendar.getInstance().getTime(); 
	}
	
	ApartmentInfo(String str) throws ParseException
	{
		String[] info = str.split(" ");
		id = info[0];
		isNew = Boolean.parseBoolean(info[1]);
		isWatched = Boolean.parseBoolean(info[2]);
		
		updateDate = df.parse(info[3]);
		publishDate = df.parse(info[4]);		
			
		if (info.length == 5) return;
		
		notes = info[5];
		for (int i = 6; i < info.length; i++) {
			notes = String.join(" ",notes, info[i]);
		}
		
	}
	
	void setUpdateDateToToday(){
		updateDate = Calendar.getInstance().getTime();
	}
	
	public Object getId() {
		return id;
	}
	
	void setId(String id){
		this.id = id;
	}
	
	public String getNote() {
		return notes;
	}

	
	public void updateNote(String notes) {
		this.notes = notes;		
	}
	
	@Override
	public String toString() {		
		return String.format("%s %s %s %s %s %s", id, isNew, isWatched, df.format(updateDate), df.format(publishDate), notes);
	}

	@Override
	public int compareTo(ApartmentInfo info) {
		if (updateDate.after(info.updateDate)) return 1;
		if (updateDate.before(info.updateDate)) return -1;
		
		if(publishDate.after(info.publishDate)) return 1;
		if(publishDate.before(info.publishDate)) return -1;
		
		return 0;
	}
	
	public static void main(String[] args) {
		ApartmentInfo info = null;
		ApartmentInfo info1 = null;
		
		try {
			info = new ApartmentInfo("1234");
			info1 = new ApartmentInfo("1234 2014-12-30 2014-12-26 'best! street'");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		System.out.println( info.toString());
		System.out.println( info1.toString());
		
		System.out.println(String.format("[%s] %s [%s]", info1, (info1.compareTo(info)>0)?" newer than":"older than", info));
	}



}
