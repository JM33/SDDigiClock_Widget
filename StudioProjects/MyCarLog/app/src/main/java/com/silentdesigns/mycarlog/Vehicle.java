package com.silentdesigns.mycarlog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class Vehicle {
	private static final String TAG = "Vehicle";
	private String mName = "Default";
	private ArrayList<String> mEntries;
	private String mMake;
	private String mModel;
	private String mYear;
	
	
	private String averageMPG = "";
	private String averageTotalCost = "";
	private String averageTotalGallons = "";
	private String averagePricePerGallon = "";
	private String averageCostPerMile = "";
	private String averageDistance = "";
	private String averageTime = "";
	
	//private String totalMPG = "";
	private String totalTotalGallons = "";
	private String totalTotalCost = "";
	//private String totalPricePerGallon = "";
	//private String totalCostPerMile = "";
	private String totalDistance = "";
	private String totalTime = "";
	
	private String maximumMPG = "";
	private String maximumTotalGallons = "";
	private String maximumTotalCost = "";
	private String maximumPricePerGallon = "";
	private String maximumCostPerMile = "";
	private String maximumDistance = "";
	private String maximumTime = "";
	
	private String minimumMPG = "";
	private String minimumTotalGallons = "";
	private String minimumTotalCost = "";
	private String minimumPricePerGallon = "";
	private String minimumCostPerMile = "";
	private String minimumDistance = "";
	private String minimumTime = "";
	
	private String totalFillups = "";
	private String costperday = "";
	private String costpermonth = "";
	private String costperyear = "";
	
	private String milesperday = "";
	private String milespermonth = "";
	private String milesperyear = "";
	
	private String gallonsperday = "";
	private String gallonspermonth = "";
	private String gallonsperyear = "";
	
	public Vehicle(String name){
		mName = name;
		setEntries(new ArrayList<String>());
	}
	
	public Vehicle(String name, ArrayList<String> entries){
		mName = name;
		setEntries(entries);
	}
	

	public void setName(String name){
		mName = name;
	}
	
	public String name(){
		return mName;
	}

	public ArrayList<String> getEntries() {
		return mEntries;
	}

	public void setEntries(ArrayList<String> entries) {
		mEntries = entries;
	}
	
	public String toLog(){
		return "ENTRY VEHICLE " + mName + " " + mYear + " " + mMake 
				+ " " + mModel + " ; ";
	}
	
	public void renameDefault(String old, String name){
		mName = name();
		String log  = CarLogHome.prefs.getString("Log", "");
		String[] tmp = log.split(" ");
		for(int i=0; i < tmp.length; i++){
			if(tmp[i].equalsIgnoreCase("Default") && tmp[i-1].equalsIgnoreCase("NAME")){
				tmp[i] = name;
			}
		}
		String newlog = "";
		for(int i=0; i < tmp.length; i++){
			newlog += tmp[i];
		}
		CarLogHome.SaveStringPreferences("Log", log);
	}

	public String getMake() {
		return mMake;
	}

	public void setMake(String mMake) {
		this.mMake = mMake;
	}

	public String getModel() {
		return mModel;
	}

	public void setModel(String mModel) {
		this.mModel = mModel;
	}

	public String getYear() {
		return mYear;
	}

	public void setYear(String mYear) {
		this.mYear = mYear;
	}
	
	private double round(double d){
		return (double)Math.round(d * 100) / 100;
	}
	
	public int get_days_between_dates(Date date1, Date date2)
	{       
	    //if date2 is more in the future than date1 then the result will be negative
	    //if date1 is more in the future than date2 then the result will be positive.

	    return (int)((date2.getTime() - date1.getTime()) / (1000*60*60*24));
	}
	
	public void calcStats(){
		if (getEntries().size() == 0){
			return;
		}
		ArrayList<String> dates = new ArrayList<String>();
		ArrayList<String> odos = new ArrayList<String>();
		ArrayList<String> gals = new ArrayList<String>();
		ArrayList<String> ppgs = new ArrayList<String>();
		ArrayList<String> fulls = new ArrayList<String>();
		
		float min;
		float max;
		float min2;
		float max2;
		
		for (int i = 0; i<getEntries().size(); i ++){
			String[] tmp = getEntries().get(i).split(" ");
			if(tmp[1].equalsIgnoreCase("FILLUP")){
				dates.add(tmp[5]);
				odos.add(tmp[7]);
				gals.add(tmp[9]);
				ppgs.add(tmp[11]);
				fulls.add(tmp[13]);
			}
		}
		if(dates.size() == 0){
			return;
		}
		//Totals
		String totalfillups = String.valueOf(dates.size());
		setTotalFillups(totalfillups);

		//change dates from strings to Dates
		ArrayList<Date> fdates = new ArrayList<Date>();
		for(String date : dates){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date fdate = format.parse(date);
				fdates.add(fdate);
				//System.out.println(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			//change date string to format MM-dd-yyyy
			String[] datesplit = date.split("-");
			String newdate = datesplit[1] + "-" + datesplit[2] + "-" + datesplit[0];
			dates.set(dates.indexOf(date), newdate);
		}

		//Total time
		if(dates.size() > 1){
			Date lastdate = fdates.get(dates.size()-1);
			String[] ldate = dates.get(dates.size()-1).split("-");
			Date firstdate = fdates.get(0);
			String[] fdate = dates.get(0).split("-");
			
			//int totalyrs = Integer.parseInt(ldate[2]) - Integer.parseInt(fdate[2]);
			//int totalmos = Integer.parseInt(ldate[0]) - Integer.parseInt(fdate[0]);
			//int totaldays = Integer.parseInt(ldate[1]) - Integer.parseInt(fdate[1]);
			int[] alltime = getTotalTime(firstdate, lastdate);

			setTotalTime(String.valueOf(alltime[0])+ " years, "
							+ String.valueOf(alltime[1])+ " months, "
							 + String.valueOf(alltime[2])+ " days");
			
			//Total Distance
			min = 100000;
			max = 0;
			float totdist = 0;
			for (int i= 1; i< odos.size(); i++){
				float thisdist = Float.parseFloat(odos.get(i)) - Float.parseFloat(odos.get(i-1)) ;
				totdist += thisdist;
				if(thisdist > max){
					max = thisdist;
				}
				if(thisdist < min){
					min = thisdist;
				}
				
			}
			
			setTotalDistance(String.valueOf(round(totdist)));
			setMaximumDistance(String.valueOf(round(max)));
			setMinimumDistance(String.valueOf(round(min)));
			float avgdist = totdist / (odos.size() - 1);
			setAverageDistance(String.valueOf(round(avgdist)));
			
			//total gallons
			min = 100000;
			max = 0;
			float totgal = 0;
			for (int i= 0; i< gals.size(); i++){
				float thisgal = Float.parseFloat(gals.get(i));
				totgal += thisgal;
				if(thisgal > max){
					max = thisgal;
				}
				if(thisgal < min){
					min = thisgal;
				}
				
			}
			setTotalTotalGallons(String.valueOf(round(totgal)));
			setMaximumTotalGallons(String.valueOf(round(max)));
			setMinimumTotalGallons(String.valueOf(round(min)));
			float avggal = totgal / gals.size();
			setAverageTotalGallons(String.valueOf(round(avggal)));
			
			
			min = 100000;
			max = 0;
			min2 = 100000;
			max2 = 0;
			float allcpms = 0;
			float allmpgs = 0;
			int allmpgscount = 0;
			
			int partialcount = 0;
			for (int i= 1; i< gals.size(); i++){
				boolean isfull = Boolean.parseBoolean(fulls.get(i));
				if(!isfull){
					//find next full tank
					partialcount +=1;
					Log.i(TAG, "Partial Fill - " + fulls.get(i));
				}else{
					//get fillups since tank was last full
					if(partialcount > 0){
						float galSF = 0;
						float distSF = 0;
						float costSF = 0;
						for(int j = 0; j < partialcount; j++){
							distSF += Float.parseFloat(odos.get(i - partialcount))-Float.parseFloat(odos.get(i - partialcount - 1));
							galSF += Float.parseFloat(gals.get(i - partialcount));
							costSF += Float.parseFloat(gals.get(i - partialcount)) * Float.parseFloat(ppgs.get(i - partialcount));
						}
						
						float thismpg = (distSF/galSF);
						Log.i(TAG, "This MPG = " + String.valueOf(thismpg));
						allmpgs += thismpg;
						allmpgscount += 1;
						if(thismpg > max){
							max = thismpg;
						}
						if(thismpg < min){
							min = thismpg;
						}
						
						
						float thiscpm = (costSF / distSF);
						allcpms += thiscpm;
						if(thiscpm > max){
							max = thiscpm;
						}
						if(thiscpm < min){
							min = thiscpm;
						}
						
						partialcount = 0;
						
					}
					else{
						float thismpg = (Float.parseFloat(odos.get(i))-Float.parseFloat(odos.get(i-1)) )/Float.parseFloat(gals.get(i));
						allmpgs += thismpg;
						Log.i(TAG, "This f2f MPG = " + String.valueOf(thismpg));
						allmpgscount +=1;
						if(thismpg > max){
							max = thismpg;
						}
						if(thismpg < min){
							min = thismpg;
						}
						
						
						float thiscpm = (Float.parseFloat(gals.get(i)) * Float.parseFloat(ppgs.get(i))) / (Float.parseFloat(odos.get(i))-Float.parseFloat(odos.get(i-1)));
						allcpms += thiscpm;
						if(thiscpm > max){
							max = thiscpm;
						}
						if(thiscpm < min){
							min = thiscpm;
						}
					}
				}
				
				
				
			}
			setMaximumMPG(String.valueOf(round(max)));
			setMinimumMPG(String.valueOf(round(min)));
			float avgmpg = allmpgs / allmpgscount;
			setAverageMPG(String.valueOf(round(avgmpg)));
			
			setMaximumCostPerMile(String.valueOf(round(max2)));
			setMinimumCostPerMile(String.valueOf(round(min2)));
			float avgcpm = allcpms / (allmpgscount);
			setAverageCostPerMile(String.valueOf(round(avgcpm)));
			
			max = 0;
			min = 100000;
			max2 = 0;
			min2 = 100000;
			float allppgs = 0;
			//Total Cost
			float totcost = 0;
			for (int i= 0; i< ppgs.size(); i++){
				float thisppg = Float.parseFloat(ppgs.get(i));
				float thiscost = Float.parseFloat(gals.get(i)) * Float.parseFloat(ppgs.get(i));
				totcost += thiscost;
				if(thisppg > max){
					max = thisppg;
				}
				if(thisppg < min){
					min = thisppg;
				}
				if(thiscost > max2){
					max2 = thiscost;
				}
				if(thiscost < min){
					min2 = thiscost;
				}
				allppgs+=thisppg;
			}
			
			setTotalTotalCost(String.valueOf(round(totcost)));
			setMaximumTotalCost(String.valueOf(round(max2)));
			setMinimumTotalCost(String.valueOf(round(min2)));
			setMaximumPricePerGallon(String.valueOf(round(max)));
			setMinimumPricePerGallon(String.valueOf(round(min)));
			float avgppg = allppgs / ppgs.size();
			setAveragePricePerGallon(String.valueOf(round(avgppg)));
			float avgcost = totcost / ppgs.size();
			setAverageTotalCost(String.valueOf(round(avgcost)));
			
			
			
			
			ArrayList<Integer> tweentimes = new ArrayList<Integer>();
			
			max=0;
			min=100000;

			int alldays = 0;
			for(int i = 1; i < dates.size(); i++){
				//lastdate = dates.get(i-1);
				ldate = dates.get(i-1).split("-");
				//firstdate = dates.get(i);
				fdate = dates.get(i).split("-");
				
				Date date1 = fdates.get(i-1);
				//date1.setMonth(Integer.parseInt(ldate[0]));
				//date1.setDate(Integer.parseInt(ldate[1]));
				//date1.setYear(Integer.parseInt(ldate[2]));
				
				Date date2 = fdates.get(i);
				//date2.setMonth(Integer.parseInt(fdate[0]));
				//date2.setDate(Integer.parseInt(fdate[1]));
				//date2.setYear(Integer.parseInt(fdate[2]));
				
				int daysbetween = get_days_between_dates(date1, date2);
				if(max < daysbetween){
					max = daysbetween;
				}
				if(min > daysbetween){
					min = daysbetween;
				}
				tweentimes.add(daysbetween);
				alldays+=daysbetween;
				
				setMaximumTime(String.valueOf(max));
				
				setMinimumTime(String.valueOf(min));
				
				double aveDaysTween = alldays / tweentimes.size();
				aveDaysTween = round(aveDaysTween);
				setAverageTime(String.valueOf(aveDaysTween));
				
				
			}
			
			
			float costperday = Float.parseFloat(getTotalTotalCost()) / alldays ;
			float costpermonth = costperday * 30;
			float costperyear = costperday * 365;
			setCostperyear(String.valueOf(round(costperyear)));
			setCostpermonth(String.valueOf(round(costpermonth)));
			setCostperday(String.valueOf(round(costperday)));
			
			float milesperday = Float.parseFloat(getTotalDistance()) / alldays ;
			float milespermonth = milesperday * 30;
			float milesperyear = milesperday * 365;
			setMilesperyear(String.valueOf(round(milesperyear)));
			setMilespermonth(String.valueOf(round(milespermonth)));
			setMilesperday(String.valueOf(round(milesperday)));
			
			float galsperday = Float.parseFloat(getTotalTotalGallons()) / alldays ;
			float galspermonth = galsperday * 30;
			float galsperyear = galsperday * 365;
			setGallonsperyear(String.valueOf(round(galsperyear)));
			setGallonspermonth(String.valueOf(round(galspermonth)));
			setGallonsperday(String.valueOf(round(galsperday)));
			
		}
		
		
	}

	public String getAverageMPG() {
		return averageMPG;
	}

	public void setAverageMPG(String averageMPG) {
		this.averageMPG = averageMPG;
	}
	
	public String getAverageTotaGallons() {
		return averageTotalGallons;
	}

	public void setAverageTotalGallons(String averageTotalGallons) {
		this.averageTotalGallons = averageTotalGallons;
	}

	public String getAverageTotalCost() {
		return averageTotalCost;
	}

	public void setAverageTotalCost(String averageTotalCost) {
		this.averageTotalCost = averageTotalCost;
	}

	public String getAveragePricePerGallon() {
		return averagePricePerGallon;
	}

	public void setAveragePricePerGallon(String averagePricePerGallon) {
		this.averagePricePerGallon = averagePricePerGallon;
	}

	public String getAverageCostPerMile() {
		return averageCostPerMile;
	}

	public void setAverageCostPerMile(String averageCostPerMile) {
		this.averageCostPerMile = averageCostPerMile;
	}

	public String getAverageDistance() {
		return averageDistance;
	}

	public void setAverageDistance(String averageDistance) {
		this.averageDistance = averageDistance;
	}

	public String getAverageTime() {
		return averageTime;
	}

	public void setAverageTime(String averageTime) {
		this.averageTime = averageTime;
	}

	/*
	public String getTotalMPG() {
		return totalMPG;
	}

	public void setTotalMPG(String totalMPG) {
		this.totalMPG = totalMPG;
	}
	*/
	
	
	public String getTotalTotalGallons() {
		return totalTotalGallons;
	}

	public void setTotalTotalGallons(String totalGallons) {
		this.totalTotalGallons = totalGallons;
	}
	/*
	public String getTotalPricePerGallon() {
		return totalPricePerGallon;
	}
	
	public void setTotalPricePerGallon(String totalPricePerGallon) {
		this.totalPricePerGallon = totalPricePerGallon;
	}
	 */
	public String getTotalTotalCost() {
		return totalTotalCost;
	}

	public void setTotalTotalCost(String totalTotalCost) {
		this.totalTotalCost = totalTotalCost;
	}
	/*
	public String getTotalCostPerMile() {
		return totalCostPerMile;
	}

	public void setTotalCostPerMile(String totalCostPerMile) {
		this.totalCostPerMile = totalCostPerMile;
	}
	 */
	public String getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(String totalDistance) {
		this.totalDistance = totalDistance;
	}

	public String getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}

	public String getMaximumMPG() {
		return maximumMPG;
	}

	public void setMaximumMPG(String maximumMPG) {
		this.maximumMPG = maximumMPG;
	}

	public String getMaximumTotalCost() {
		return maximumTotalCost;
	}

	public void setMaximumTotalCost(String maximumTotalCost) {
		this.maximumTotalCost = maximumTotalCost;
	}

	public String getMaximumPricePerGallon() {
		return maximumPricePerGallon;
	}

	public void setMaximumPricePerGallon(String maximumPricePerGallon) {
		this.maximumPricePerGallon = maximumPricePerGallon;
	}

	public String getMaximumCostPerMile() {
		return maximumCostPerMile;
	}

	public void setMaximumCostPerMile(String maximumCostPerMile) {
		this.maximumCostPerMile = maximumCostPerMile;
	}

	public String getMaximumDistance() {
		return maximumDistance;
	}

	public void setMaximumDistance(String maximumDistance) {
		this.maximumDistance = maximumDistance;
	}

	public String getMaximumTime() {
		return maximumTime;
	}

	public void setMaximumTime(String maximumTime) {
		this.maximumTime = maximumTime;
	}

	public String getMinimumMPG() {
		return minimumMPG;
	}

	public void setMinimumMPG(String minimumMPG) {
		this.minimumMPG = minimumMPG;
	}

	public String getMaximumTotalGallons() {
		return maximumTotalGallons;
	}

	public void setMaximumTotalGallons(String maximumTotalGallons) {
		this.maximumTotalGallons = maximumTotalGallons;
	}
	
	public String getMinimumTotalCost() {
		return minimumTotalCost;
	}

	public void setMinimumTotalCost(String minimumTotalCost) {
		this.minimumTotalCost = minimumTotalCost;
	}

	public String getMinimumPricePerGallon() {
		return minimumPricePerGallon;
	}

	public void setMinimumPricePerGallon(String minimumPricePerGallon) {
		this.minimumPricePerGallon = minimumPricePerGallon;
	}

	public String getMinimumCostPerMile() {
		return minimumCostPerMile;
	}

	public void setMinimumCostPerMile(String minimumCostPerMile) {
		this.minimumCostPerMile = minimumCostPerMile;
	}

	public String getMinimumDistance() {
		return minimumDistance;
	}

	public void setMinimumDistance(String minimumDistance) {
		this.minimumDistance = minimumDistance;
	}

	public String getMinimumTime() {
		return minimumTime;
	}

	public void setMinimumTime(String minimumTime) {
		this.minimumTime = minimumTime;
	}

	

	public String getMinimumTotalGallons() {
		return minimumTotalGallons;
	}

	public void setMinimumTotalGallons(String minimumTotalGallons) {
		this.minimumTotalGallons = minimumTotalGallons;
	}

	public String getTotalFillups() {
		return totalFillups;
	}

	public void setTotalFillups(String totalFillups) {
		this.totalFillups = totalFillups;
	}

	public String getCostperday() {
		return costperday;
	}

	public void setCostperday(String costperday) {
		this.costperday = costperday;
	}

	public String getCostpermonth() {
		return costpermonth;
	}

	public void setCostpermonth(String costpermonth) {
		this.costpermonth = costpermonth;
	}

	public String getCostperyear() {
		return costperyear;
	}

	public void setCostperyear(String costperyear) {
		this.costperyear = costperyear;
	}

	public String getMilesperday() {
		return milesperday;
	}

	public void setMilesperday(String milesperday) {
		this.milesperday = milesperday;
	}

	public String getMilespermonth() {
		return milespermonth;
	}

	public void setMilespermonth(String milespermonth) {
		this.milespermonth = milespermonth;
	}

	public String getMilesperyear() {
		return milesperyear;
	}

	public void setMilesperyear(String milesperyear) {
		this.milesperyear = milesperyear;
	}

	public String getGallonsperday() {
		return gallonsperday;
	}

	public void setGallonsperday(String gallonsperday) {
		this.gallonsperday = gallonsperday;
	}

	public String getGallonspermonth() {
		return gallonspermonth;
	}

	public void setGallonspermonth(String gallonspermonth) {
		this.gallonspermonth = gallonspermonth;
	}

	public String getGallonsperyear() {
		return gallonsperyear;
	}

	public void setGallonsperyear(String gallonsperyear) {
		this.gallonsperyear = gallonsperyear;
	}
	public static int[] getTotalTime(Date first, Date last) {
		Calendar a = getCalendar(first);
		Calendar b = getCalendar(last);
		int ydiff = b.get(YEAR) - a.get(YEAR);
		if (a.get(MONTH) > b.get(MONTH) ||
				(a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
			ydiff--;
		}

		int mdiff = b.get(MONTH) - a.get(MONTH);
		if(a.get(DATE) > b.get(DATE)){
			mdiff--;
		}
		if(mdiff < 0){
			mdiff += 12;
		}

		int ddiff = b.get(DATE) - a.get(DATE);
		if(a.get(DATE) > b.get(DATE)){
			ddiff = b.get(DATE);
			// Get the number of days in that month
			int daysInMonth = a.getActualMaximum(Calendar.DAY_OF_MONTH);
			ddiff += (daysInMonth - a.get(DATE));
		}


		return new int[]{ydiff, mdiff, ddiff};
	}



	public static Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		cal.setTime(date);
		return cal;
	}
}
