package minDistance;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.awt.Color;
import java.awt.Desktop;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class GRASPforClustering {
	
	Locations locs;
	Location loc;
	Location driver;
	ArrayList<Location> passengers;
	boolean continueToRun=true;
	Random rand;
	
	private double driver1TotalDistance;
	private double driver2TotalDistance;
	private double driver1TotalDistanceNew;
	private double driver2TotalDistanceNew;

	private int numNonImpr = 0;


	public GRASPforClustering (Locations locs) { 
		this.locs = locs;
	}
	
	/*
	void addToBestRoutes(ArrayList<Location> route) {
		ArrayList<Location> BestRoute = new ArrayList<Location>();
		for(int i=0;i<route.size();i++) { 
			Location rtLoc = new Location();
			rtLoc = route.get(i);
			BestRoute.add(rtLoc);
		}
		driver.bestRoutes.add(BestRoute);
	}
	*/
	
	public void searchDrivers() {
		// initially optimize all drivers
		   //graspForSingleDriver(locs);
			for(Location driver: locs.driverList) { 
				GRASPheruristic gh = new GRASPheruristic(locs, driver, 100*driver.pickupList.size(), 500*driver.pickupList.size());
				// generate initial path by traveling to the closest pickup location
				gh.createClosesDistPath();
				// search by swapping and shuffling 
				gh.search();
			}
			
		// swap two customer for two drivers
			while(locs.driverList.size()>=2 && continueToRun){
		    // swap sequences of two drivers 
		   		int swapDFrom, swapDTo;
		   		Random rand = new Random();
		   		//rand.setSeed(31245);
		
		   		swapDFrom = rand.nextInt(locs.driverList.size());
		   		//makes sure same driver isn't selected
		   		swapDTo = swapDFrom; 
		   		while(swapDTo == swapDFrom)
		   			swapDTo = rand.nextInt(locs.driverList.size());
		
		   	//swap sequences of two passengers
		
		   		Location driver1 = locs.driverList.get(swapDFrom);
		   		Location driver2 = locs.driverList.get(swapDTo);
		   		
		   	// evaluate initial total distance init_total_distance
		   		driver1TotalDistance = Utils.calculateTotalDistance(locs, driver1, driver1.pickupList);
	            driver2TotalDistance = Utils.calculateTotalDistance(locs, driver2, driver2.pickupList);
		
		   	// pick a customer from each driver 
		   		int indexCustomerDriver1 = rand.nextInt(driver1.pickupList.size());
		   		int indexCustomerDriver2 = rand.nextInt(driver2.pickupList.size());
		
		   		Location customerDriver1 = driver1.pickupList.get(indexCustomerDriver1);
		   		Location customerDriver2 = driver2.pickupList.get(indexCustomerDriver2);
		
	   		// swap customers between drivers
				driver1.pickupList.remove(customerDriver1);
				driver1.pickupList.add(customerDriver2);
				
				driver2.pickupList.remove(customerDriver2);
				driver2.pickupList.add(customerDriver1);
				
		
				GRASPforTwoDrivers gh1 = new GRASPforTwoDrivers(locs, driver1, 100*driver1.pickupList.size(), 500*driver1.pickupList.size());
				gh1.createClosesDistPath();
				gh1.search();
				
				GRASPforTwoDrivers gh2 = new GRASPforTwoDrivers(locs, driver2, 100*driver2.pickupList.size(), 500*driver2.pickupList.size());
				gh2.createClosesDistPath();
				gh2.search();
				
				
//--------------------------------------------------------------------------------------------------------------------------
			// evaluate original total distance vs new total distance

				
				driver1TotalDistanceNew = Utils.calculateTotalDistance(locs, driver1, driver1.pickupList);
				driver2TotalDistanceNew = Utils.calculateTotalDistance(locs, driver2, driver2.pickupList);
				
			    if (driver1TotalDistanceNew + driver2TotalDistanceNew < driver1TotalDistance + driver2TotalDistance){
					// accept the new routes;			    	
			    	gh1.addToBestRoutes(driver1.pickupList);
			    	gh2.addToBestRoutes(driver2.pickupList);
					continueToRun= true;
					numNonImpr = 0;	
				}
				
				else {	
				// if not improved, then reverse back the modification
					driver1.pickupList.remove(customerDriver2);
					driver1.pickupList.add(customerDriver1);
					
					driver2.pickupList.remove(customerDriver1);
					driver2.pickupList.add(customerDriver2);
					
					
					GRASPforTwoDrivers ghx1 = new GRASPforTwoDrivers(locs, driver1, 100*driver1.pickupList.size(), 500*driver1.pickupList.size());
					ghx1.createClosesDistPath();
					ghx1.search();
					
					GRASPforTwoDrivers ghx2 = new GRASPforTwoDrivers(locs, driver2, 100*driver2.pickupList.size(), 500*driver2.pickupList.size());
					ghx2.createClosesDistPath();
					ghx2.search();
					
					numNonImpr ++ ; 
					
					// ends program if max bad iterations is reached
					if(numNonImpr>=5) {
							continueToRun=false;	
							numNonImpr =0;
					}
						
	
				}
			
			}
		} 	
	}



	