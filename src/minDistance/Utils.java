package minDistance;

import java.util.List;
import java.util.Random;

public class Utils {

	/*
	public void readExcelInput () { 
	}
	*/
	
	// Display the distance from passenger to passenger
	public static double calculateTotalDistance(Locations locs, Location driver, List<Location> passengerList) {
		double totalDistance = 0.0;
		Location fromLoc = driver;
		Location toLoc;
				
		for(int ind=0;ind<passengerList.size();ind++) { 
			toLoc = passengerList.get(ind);
			totalDistance += locs.tableDistanceByLoc.get(fromLoc, toLoc);
			fromLoc = toLoc;
		}
		toLoc = locs.destinationLoc;
		totalDistance += locs.tableDistanceByLoc.get(fromLoc, toLoc);
		
		return totalDistance;
	}
	
	// Display the resulting order in which passengers are picked up
	public static String printPath(List<Location> locList) { 
		String path=""; 
		for(Location st: locList) 
			path += st.locName + " - ";
		return path;
	}
	
	// Display the Base distance
	public static double getBaseTravelDistance(Locations locs) { 
		double baseDistance = 0;
		for(Location loc: locs.locList) { 
			baseDistance += locs.tableDistanceByLoc.get(loc, locs.destinationLoc);
		}
		return baseDistance;
	}
	
}


