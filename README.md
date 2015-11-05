# yad2helper

 This Java application can help you to track ads from www.yad2.co.il
 and help you find the product you want effectively.
 
 Currently supports apartments and car sections
 
 Instructios:
 
- clone the code project to eclipse and run it from there.
 
- Open a search in www.yad2.co.il (cars or apartments) . Open as muach tabs as you interested according to dates.
 Save the tabs as raw html in a directory on your drive . (= download directory)

- run the yad2 helper. In opening dialog pass the the download directory to it.
- It will scan all the ads links and produce the watchlist file (and will promt you with dialog where to save it)
- Watchlist file is just a list of links to ad with dates they appeared on. You can delete ads that you not intersted in.

- Next time you run tge yad2 helper, it scans the download directory and the watchlist file.
- It ads to inner data base fresh links (but not the ads that were already seen before) and also scans the watchlist file for ads that still remain there (it means that you are interested to keep tracking these ads). After that a new watchlist file is produced. It contains fresh (unseen) ads and ads you keep for tracking. The watchlist file is ordered by dates of last time the ads had appear on yad2 website. Now you can click on links in watchlist and to open them in your brouser.
- Note: you can write notes near ads (same line) in watchlist file. They will be kept for next time
- So the basic routine to use this helper : 
1. Each day go to yad2 website and do your search
2. save all pages with ads that appeared in last 24 hours or so in directory of this date name
3. run yad2helper and pass to it this directory
4. use a created watchlist to filter only ads you interested in

 
