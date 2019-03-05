
package minDistance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class GRASPforTwoDrivers {
	Locations locs;
	Location driver;
	ArrayList<Location> passengers;
	int maxNonImpMove; 
	int maxShuffle;
	int numOfLoc;
	Random rand;
	
	public GRASPforTwoDrivers (Locations locs, Location driver, Integer maxNonImpMove, Integer maxShuffle) { 
		this.locs = locs;
		this.driver = driver;
		this.passengers = driver.pickupList;
		this.maxNonImpMove = maxNonImpMove;
		this.maxShuffle = maxShuffle;
		this.numOfLoc = passengers.size();
		rand = new Random(12345);
	}
	
	// Generate an initial path, by traveling to the closest pickup point
	void createClosesDistPath() {
		System.out.println("initial path for driver " + driver.locName + " : " + Utils.printPath(driver.pickupList));
		System.out.println("initial path distance : " + Utils.calculateTotalDistance(locs, driver, driver.pickupList));
		
		ArrayList<Location> sortedPassengers = new ArrayList<Location>();
		ArrayList<Location> remainingPassengers = new ArrayList<Location>();

		remainingPassengers=passengers;
		
		Location fromLoc = driver;
		Location toLoc=null;
		double dist;
		// search for the closest student in the remaining list
		// to the last student in the ordered list
		while(remainingPassengers.size()>0) { 
			double minDist = Double.MAX_VALUE;
			for(Location loc: remainingPassengers) {
				dist = locs.tableDistanceByLoc.get(fromLoc, loc);
				if(dist<minDist){
					toLoc = loc;
					minDist = dist;
				}
			}
			sortedPassengers.add(toLoc);
			remainingPassengers.remove(remainingPassengers.indexOf(toLoc));
			//System.out.println("remaining " + locs.printPath(remainingStList));
			
			fromLoc=toLoc;
			//System.out.println("sorted " + locs.printPath(sortedStList));
		}
		driver.pickupList=sortedPassengers;
		driver.route=sortedPassengers;
		System.out.println("sorted path for driver " + driver.locName + " : " + Utils.printPath(driver.pickupList));
		System.out.println("sorted path distance : " + Utils.calculateTotalDistance(locs, driver, driver.pickupList)); 
	}
	
	void addToBestRoutes(ArrayList<Location> route) {
		ArrayList<Location> BestRoute = new ArrayList();
		for(int i=0;i<route.size();i++) { 
			Location rtLoc = new Location();
			rtLoc = route.get(i);
			BestRoute.add(rtLoc);
		}
		driver.bestRoutes.add(BestRoute);
	}
	
	// Improve the path by doing random search (swapping and shuffling)
	void search() {
		try {
			
			// create a file "GeneticAlgorithm.out" to keep output
			String outFile = "./ouput/GreedyHeuristic_" + driver.locName +  ".out";
			File file = new File(outFile);
			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
		
			bw.write("Base distance: " +  Utils.getBaseTravelDistance(locs));
			
			int swapFrom, swapTo; 
			boolean continueToRun=true;
			// initial travel distance for given travel order
			double travelDistance = Utils.calculateTotalDistance(locs, driver, driver.pickupList);
			//System.out.println(locs.printPath(locs.stList) + travelDistance);
			
			// initial optimal distance is equal to initial travel distance 
			double bestDistance = travelDistance;
			double globalBestDistance = travelDistance;
			// add this route the list of best routes 
			addToBestRoutes(driver.pickupList);
			
			// bw.write(Utils.printPath(driver.pickupList) + bestDistance + "\n");
			int numNonImpr = 0;
			int numShuffle = 0;
			while(continueToRun){		
				
				// swap sequences of two passengers 
				swapFrom = rand.nextInt(numOfLoc);
				swapTo = rand.nextInt(numOfLoc);
			    
				Collections.swap(driver.pickupList, swapFrom, swapTo);

				travelDistance = Utils.calculateTotalDistance(locs, driver, driver.pickupList);

				if (travelDistance < bestDistance) {
					bestDistance = travelDistance;
					if(bestDistance < globalBestDistance) {
						globalBestDistance = bestDistance;
						addToBestRoutes(driver.pickupList);
						// bw.write("global best found \n");
					}
					// bw.write(bestDistance + " : " + Utils.printPath(driver.pickupList) +  "\n");
					continueToRun= true;
					numNonImpr= 0;	
				}
				else {	
					// if not improved, then reverse back the modification
					Collections.swap(driver.pickupList, swapTo, swapFrom);
					numNonImpr ++ ; 
					if(numNonImpr>=maxNonImpMove) {
						if(numShuffle>=maxShuffle) 
							continueToRun=false;
						else {
							numShuffle++;
							numNonImpr =0;
							
							// System.out.println("Shuffling ------------ \n");
							// bw.write("Shuffling ------------------------\n");
							Collections.shuffle(driver.pickupList, rand);
							bestDistance = Utils.calculateTotalDistance(locs, driver, driver.pickupList);
							// bw.write(bestDistance + " : " + Utils.printPath(driver.pickupList) +  "\n");
						}
					}
				}
			}
			
			
			bw.write("\n\n SUMMARY (Distance, Route) :\n");
			for(Location driver: locs.driverList){
				for(ArrayList<Location> route: driver.bestRoutes){
					double routeDistance = Utils.calculateTotalDistance(locs, driver, route);
					bw.write(routeDistance + " : " + Utils.printPath(route)  +  "\n");
				}
			}
			
			bw.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}	
	
	
		
}
