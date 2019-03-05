package minDistance;

import java.util.List;

public class Clustering {
	Locations locs;
	List<Location> passengers;
	List<Location> drivers;
	Location destination;
	
	public Clustering(Locations locs) { 
		this.locs = locs;
		this.passengers = locs.passengerList;
		this.drivers = locs.driverList;
		this.destination = locs.destinationLoc;
	}
	
/*
	void assignToClosestPath (){
		double distance;
		for (Location passenger: passengers) {
			double closestPathDist = Double.MAX_VALUE;
			Location closestPath = passenger;
			for (Location driver: drivers) {
				if (driver.remainedCapacity > 0){
					distance = locs.tableDistanceByLoc.get(passenger, driver);
					if (distance < closestPathDist) {
						closestPathDist = distance;
						closestPath=driver;
					}
				}
			}
			passenger.assignedTo=closestPath;
			closestPath.pickupList.add(passenger);
			closestPath.remainedCapacity=closestPath.remainedCapacity-1;
		}
			
	}
	*/
	
	// cluster passengers by assigning them to the closest available driver
	void assignToClosestDriver (){
		double distance;
		for(Location passenger: passengers) { 
			double closestDriverDist = Double.MAX_VALUE;
			Location closestDriver = passenger;
			for(Location driver: drivers) { 
				if (driver.remainedCapacity > 0){
					distance = locs.tableDistanceByLoc.get(passenger, driver);
					if (distance < closestDriverDist) {
						closestDriverDist = distance;
						closestDriver=driver;		
					}
				}
			}
			passenger.assignedTo=closestDriver;
			closestDriver.pickupList.add(passenger);
			closestDriver.remainedCapacity=closestDriver.remainedCapacity-1;
		}
	}	

}


