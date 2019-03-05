package minDistance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashBasedTable;

public class Locations {
	
	int busCapacity = 50;
	double busLat = 30.471152;
	double busLon = -97.787432;
	
	// Sorted list of locs
	List<Location> locList = new ArrayList<Location>();
	List<Location> driverList = new ArrayList<Location>();
	List<Location> passengerList = new ArrayList<Location>();
	Location destinationLoc;
	
	// Table for fromLoc - toLoc: Distance 
	HashBasedTable<Location, Location, Double> tableDistanceByLoc = HashBasedTable.create();
	
	
	// The function of this method is to read the text file 
		// and call addLoc function, returning three values per line
	void readLocInfo(String fileName) {  
		try {
		    BufferedReader in = new BufferedReader(new FileReader("./input/"+fileName));
		    String str;
		   
		    while ((str = in.readLine()) != null){
		    	// split each line by tabs
		    	String strArray[] = str.split("\t");
		    	// String locName,  latitude,  longtitute, Integer pickupCapacity
		    	addLoc(strArray[0],Float.valueOf(strArray[3]),Float.valueOf(strArray[4]),Integer.parseInt(strArray[6]));
		    }
		    in.close();
		} catch (IOException e) {
			System.out.println(e);
		} 
	}
	
	
	// Method to add new loc to the locs list "locList"
	// and to the loc map "stMap"
	void addLoc(String locName, double lat, double lon, Integer pickupCapacity) { 
		Location loc = new Location();
		loc.locId=""+locList.size()+1;
		loc.locName=locName;
		loc.lat=lat;
		loc.lon=lon;
		loc.pickupCapacity=pickupCapacity;
		loc.remainedCapacity=pickupCapacity;
		
		// just looking at capcaity > 1, because if it is 1, it will drive itself
		if(pickupCapacity>=1) { 
			loc.locType="driver";
			loc.locId="D_"+(driverList.size()+1);
			driverList.add(loc);
			loc.assignedTo=loc;
		}
		// assign a negative capacity to the destination point as identifier
		else if(pickupCapacity<0) {
			loc.locType="destination";
			loc.locId="END";
			destinationLoc = loc;
			loc.assignedTo=loc;
			System.out.println("destination location " + loc.locName);
		}
		// people with 0 pickup capacity - needing pickup
		else {
			loc.locType="passenger";
			loc.locId="P_"+(passengerList.size()+1);
			passengerList.add(loc);
		}
		
		locList.add(loc);
	}

	void addBus() {
		// if total capacity is not enough, add school busses with given capacities
		int totalCapacity = 0;
		int numPassengers = passengerList.size();
		
		// calculate total capacity of the available drivers 
		for(Location driver: driverList){
			totalCapacity += driver.pickupCapacity;
		}
		
		// if total capacity is not enough to pickup all passengers, then add bus as needed
		int numBus = 0;
		while(totalCapacity < numPassengers) { 
			// addLoc(String locName, Integer lat, Integer lon, Integer pickupCapacity)
			numBus++;
			addLoc("bus_"+numBus, busLat, busLon, busCapacity);
			totalCapacity += busCapacity;
		}
	}
	
	//Calculate distance function
	void calculateDistanceMatrix () { 
		double distance;
		// radiants of lon and lats
		double fromLon, toLon, fromLat, toLat; 
		// parameters used in Haversine formula
		double a, c;
		// radius of the earth (miles)
		double r = 3958.76;
		
		for(Location fromLoc: locList){
			for(Location toLoc: locList)  {
				fromLon = fromLoc.lon*Math.PI/180;
				fromLat = fromLoc.lat*Math.PI/180;
				toLon = toLoc.lon*Math.PI/180;
				toLat = toLoc.lat*Math.PI/180;
				a = Math.pow(Math.sin((fromLat-toLat)/2), 2) + Math.cos(fromLat)*Math.cos(toLat)*Math.pow(Math.sin((fromLon-toLon)/2), 2);
				//lower end: x = 0.5, weight = x += 0.25
				//normal: no x, no weight
				//higher end: x = 1.0, weight = x += 0.25
				double x = 1.0;
				double weight = x += 0.25;
				distance = weight*2*r*Math.asin(Math.pow(a, 0.5));			
				tableDistanceByLoc.put(fromLoc, toLoc, (double) (Math.round(distance*10)/10));		
			}
		}
	}

	
}
