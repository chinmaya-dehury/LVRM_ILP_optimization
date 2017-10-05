package optimization;

import java.util.Date;
//import java.util.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Main {

	public static void main(String[] args) {
		/**
		 * Virtual Link information
		 */
		// Virtual link or bandwidth demand of the virtual link
		int[][] dmndL={{4,1,2},{2,1,4},{7,1,3},{3,2,3},{1,2,4}}; 
		// computing resource demand
		int[] dmndN={9,5,7,4};
		
		/**
		 * physical network information (info abt available resource)
		 */
		// physical links, available bandwidth
		// {Bandwidth, 1stPM, 2ndPM}
		int[][] avlL={{9,1,2},{13,1,3},{10,1,5},{13,2,3},{9,2,4},{14,3,4},{12,3,5},{11,3,8},{10,2,10},{11,4,10},{9,4,8},{11,5,7},{5,6,8},{6,7,9},{10,9,10},{9,4,6}};
		//avail computing resource of physical machines
		int[] avlN={12,14,20,10,15,16,12,11,17,13};

		
		//Map_basic m = new Map_basic();		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date1 = new Date();		
		//System.out.println(dateFormat.format(date1)); //2016/11/16 12:08:43
		
		MapByLink vne1 = new MapByLink(dmndL, dmndN, avlL, avlN);
		vne1.systemInfo();
		vne1.initMap();
		
		Date date2 = new Date();
		//System.out.println(dateFormat.format(date2));
		long difference = date2.getTime() - date1.getTime();
		System.out.println("Time in ms: "+difference);
		System.out.println("Time in sec: "+difference/1000);
		//m.temp1();
		
	
		
		
		
		
		
	}

}
