package yad2;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Hyperlink;

public class AdInfo implements Comparable<AdInfo> {
	
	private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private static final String FULL_URL = "http://www.yad2.co.il/Nadlan/rent_info.php?NadlanID=";
	
	private String 					id;
	private boolean 				isNew;
	private SimpleBooleanProperty	isWatched;
	private Date 					updateDate;
	private Date 					publishDate;		
	private SimpleStringProperty	notes;

	public AdInfo() {
		id 			= new String("");
		isNew 		= true;
		isWatched 	= new SimpleBooleanProperty(true);
		updateDate 	= Calendar.getInstance().getTime();
		publishDate = Calendar.getInstance().getTime();
		notes 		= new SimpleStringProperty("");
	}
	
	public AdInfo(String str) throws ParseException
	{
		String[] info = str.split(" ");
		id 			= new String(info[0]);
		isNew  		= Boolean.parseBoolean(info[1]);
		isWatched 	= new SimpleBooleanProperty(Boolean.parseBoolean(info[2]));
		
		updateDate 	= df.parse(info[3]);
		publishDate = df.parse(info[4]);		
		
		notes 		= new SimpleStringProperty("");
		
		if (info.length == 5) return;
		notes.set(info[5]);
		for (int i = 6; i < info.length; i++) {
			notes.set(String.join(" ",notes.get(), info[i]));
		}		
	}
	
	public String getId()
	{
		return id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public boolean isNew(){
		return isNew;
	}
	
	public void setIsNew(boolean value)
	{
		isNew = value;
	}
	
	public boolean isWatched() {
		return isWatched.get();
	}
	
	public void setIsWatched(boolean value)
	{
		isWatched.set(value);
	}
	
	public void setUpdateDateToToday(){
		updateDate = Calendar.getInstance().getTime();
	}
	
	@Override
	public String toString() {		
		return String.format("%s %s %s %s %s %s", id, isNew, isWatched.get(), df.format(updateDate), df.format(publishDate), notes.get());
	}

	@Override
	public int compareTo(AdInfo info) {
		if (updateDate.after(info.updateDate)) return -1;
		else if (updateDate.before(info.updateDate)) return 1;
		else {
			if(publishDate.after(info.publishDate)) return -1;
			else if(publishDate.before(info.publishDate)) return 1;
			else return (id.compareTo(info.id));
		}		
	}
	
	
	
	//TableView update functions
		
	public Hyperlink getLink()
	{
		return new Hyperlink(String.format("%s%s", FULL_URL, id));
	}
	
	public String getIsNew()
	{
		return Boolean.toString(isNew);
	}
	
	public BooleanProperty getIsWatched()
	{
		return isWatched;
	}
	
	public String getPublishDate()
	{
		return df.format(publishDate);
	}

	public String getUpdateDate()
	{
		return df.format(updateDate);
	}
		
	public String getNotes() {
		return notes.get();
	}
	
	public void setNotes(String notes) {
		this.notes.set(notes);		
	}
}
