package minDistance;

import java.util.ArrayList;
import java.util.List;

class Location {
	String locId;
	String locName;
	String locType; //driver, passenger, destination
	double lat;
	double lon;
	int pickupCapacity;
	int remainedCapacity;
	Location assignedTo = null;
	ArrayList<Location> pickupList = new ArrayList<Location>();
	ArrayList<Location> route = new ArrayList<Location>();
	ArrayList<ArrayList<Location>> bestRoutes= new ArrayList<ArrayList<Location>>();
	Object vertex;	
}
