package minDistance;

import java.awt.Desktop;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

	public class Visualization extends JFrame
	{
		private static final long serialVersionUID = -2707712944901661771L;
		Locations locs; 
		String backgroundMap; 
		int w = 25; // width of each node
		int h = 15; // heigth of each node
		double mapLeftUpperLat = 30.484306;
		double mapLeftUpperLon = -97.805642;
		double mapRightBottomLat = 30.354488;
		double mapRightBottomLon = -97.538472;
		int mapXsize = 1553;
		int mapYsize = 881; 
		
		mxGraph graph; 
		Object parent; 
		
		public Visualization(Locations locs, String title)
		{
			super("Heuristic Algorithm for Delivery/Transportation Optimization - " + title);
			this.locs = locs;
			backgroundMap = "./input/newmap.png";	
			graph = new mxGraph();
			parent = graph.getDefaultParent();
		}
		
		public void setBackground() { 
			mxGraphComponent graphComponent = new mxGraphComponent(graph);
			getContentPane().add(graphComponent);
			graphComponent.getViewport().setOpaque(true);
			graphComponent.setBackgroundImage(new ImageIcon(backgroundMap));
		}
		
		public int getMapX(double lon) {	
			return (int) Math.round(mapXsize*(mapLeftUpperLon-lon)/(mapLeftUpperLon - mapRightBottomLon));
		}
		
		public int getMapY(double lat) {	
			return (int) Math.round(mapYsize*(mapLeftUpperLat-lat)/(mapLeftUpperLat - mapRightBottomLat));
		}
		
		public void showNodes() { 
			for(Location loc: locs.driverList){
				// mxGraph.insertVertex(parent, id, value, x, y, width, height, style) 
				// creates and inserts a new vertex into the model, within a begin/end update call.
				loc.vertex = graph.insertVertex(parent, loc.locName, loc.locName, 
						getMapX(loc.lon), getMapY(loc.lat), w, h, "ROUNDED;strokeColor=black;fillColor=green"); 
			}
			for(Location loc: locs.passengerList){
				loc.vertex = graph.insertVertex(parent, loc.locName, loc.locName, 
						getMapX(loc.lon), getMapY(loc.lat), w, h, "ROUNDED;strokeColor=black;fillColor=white");
			}
			Location loc = locs.destinationLoc;
				loc.vertex = graph.insertVertex(parent, loc.locName, loc.locName, 
					getMapX(loc.lon), getMapY(loc.lat), w, h, "ROUNDED;strokeColor=black;fillColor=red");
			
			setBackground();
		}
		
		public void showClustering() {
			showNodes();
			
			//ADD CODE TO SHOW PATH TO NEXT LOCATION AS CODE RUNS
			
			for(Location driver: locs.driverList) {
				Location fromLoc = driver;
				Location toLoc = null;
				
				for(Location lc: driver.pickupList) { 
					toLoc = lc;
					// mxGraph.insertEdge(parent, id, value, source, target, style) 
					// creates and inserts a new edge into the model, within a begin/end update call.
					graph.insertEdge(parent, fromLoc.locId+toLoc.locId,
							locs.tableDistanceByLoc.get(fromLoc, toLoc), 
							fromLoc.vertex, toLoc.vertex);	
				}
			}
		}
		
		//ADD SHOW ROUTE TO NEXT LOCATION AS CODE RUNS AFTER +1 SECOND INTERVAL
		public void showRoute() throws MalformedURLException {
			showNodes();
			List<String> colorList = new ArrayList<String>();
			String edgeColor;
			String lineStyle;
			
			colorList.add("red");
			colorList.add("blue");
			colorList.add("green");
			colorList.add("black");
			colorList.add("pink");
			colorList.add("brown");
			colorList.add("yellow");
			colorList.add("white");
			colorList.add("purple");
			
			// To show in web browser
			String pathString = "https://www.google.com/maps/dir/";
			
			for(Location driver: locs.driverList) {
				edgeColor = colorList.get(locs.driverList.indexOf(driver)%5);
				lineStyle = "dashed=1;fontColor=black;strokeColor="+edgeColor;
				Location fromLoc = driver;
				Location toLoc = null;
				int travelOrder = 1; 
				for(Location lc: driver.bestRoutes.get(driver.bestRoutes.size()-1)) { 
					toLoc = lc;
					String latAsString = Double.toString(toLoc.lat);
					String lonAsString = Double.toString(toLoc.lon);
					pathString = pathString + "\'" + latAsString + "," + lonAsString + "\'/";
					System.out.println(pathString);
					
					graph.insertEdge(parent, fromLoc.locId+toLoc.locId,
							travelOrder, fromLoc.vertex, toLoc.vertex, lineStyle);
					fromLoc = toLoc;
					travelOrder++;
				}
				toLoc = locs.destinationLoc;
				
				//For Google Maps
				String latAsString = Double.toString(toLoc.lat);
				String lonAsString = Double.toString(toLoc.lon);
				pathString = pathString + "\'" + latAsString + "," + lonAsString + "\'/";
				System.out.println(pathString);
				//URL pathURL = new URL(pathString);
				//openWebpage(pathURL);
				// mxGraph.insertEdge(parent, id, value, source, target, style) 
				// creates and inserts a new edge into the model, within a begin/end update call.
				graph.insertEdge(parent, fromLoc.locId+toLoc.locId,
						travelOrder, fromLoc.vertex, toLoc.vertex, lineStyle);
			}	
			showStatistics();
			
			//command to print final google maps url
			URL pathURL = new URL(pathString);
			openWebpage(pathURL);
			
		}
		
		
		//ADD DYNAMIC REROUTING WITH DRAG 'N' DROP RECALCULATION
		public void showStatistics() {
			double travelDistance = 0;
			double totalTravelDistance = 0; 
			
			Object info = graph.insertVertex(parent, "info", "", 
					0, 0, 210, 210, "ROUNDED;strokeColor=black;fillColor=white");

			int verticalSpace = 10;
			graph.insertVertex(info, "TravelTime", "Travel Distance:", 
					5, verticalSpace+=20, 200, 20, "ROUNDED;strokeColor=white;fillColor=white");

			graph.insertVertex(info, "cost", "------------------------------", 
					5, verticalSpace+=20, 200, 20, "ROUNDED;strokeColor=white;fillColor=white");

			for(Location driver: locs.driverList) {
				travelDistance = Utils.calculateTotalDistance(locs, driver, driver.bestRoutes.get(driver.bestRoutes.size()-1));
				totalTravelDistance += travelDistance;
				
				graph.insertVertex(info, "cost", "Driver - "+ driver.locId + " : " +  travelDistance, 
						5, verticalSpace+=20, 200, 20, "ROUNDED;strokeColor=white;fillColor=white");
			}
			graph.insertVertex(info, "cost", "------------------------------", 
					5, verticalSpace+=20, 200, 20, "ROUNDED;strokeColor=white;fillColor=white");
			
			graph.insertVertex(info, "cost", "TOTAL : " +  totalTravelDistance, 
					5, verticalSpace+=20, 200, 20, "ROUNDED;strokeColor=white;fillColor=white");
			
		}
		
		
		
		// to Open a web page for given URL
		public static void openWebpage(URI uri) {
		    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
		        try {
		            desktop.browse(uri);
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		}

		public static void openWebpage(URL url) {
		    try {
		        openWebpage(url.toURI());
		    } catch (URISyntaxException e) {
		        e.printStackTrace();
		    }
		}
	}

	
	

