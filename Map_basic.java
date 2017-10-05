package optimization;

public class Map_basic {
	
	public int cntr;
	public int[] demand;
	Map_basic(){
		cntr=0;
		demand = new int[] {2,3,5,6};
	}
	public int initMap(){	
		
		int[] avail = new int[] {8,10,7,10,15,25,13,12}; 
		
		int[] v = new int[] {-1,-1,-1,-1}; // set of VMs
		//int[] demand = new int[] {2,3,5};
		int vi=0; // VM index
		int totP=avail.length; // total number of PMs
		System.out.println("# of VMs: "+v.length+"\t\t# of PMs:"+avail.length);
		recurMap(totP,(int[]) v.clone(),vi, (int[]) avail.clone() );
		System.out.println("# of VMs: "+v.length+"\t\t# of PMs:"+avail.length);
		return 1;
} // END of temp1

	/**
	 * 
	 * @param totP	: number of PMs
	 * @param v	:set of VMs
	 * @param vi: current VM index	
	 * @param avail	: available resources of all PMs 
	 */
public void recurMap(int totP, int[] v, int vi, int[] avail) {				
			for(int p=0;p<totP;p++){
				//System.out.println("Mapping VL-"+(v[vi]-1)+" onto PL-"+p);
				if(avail[p] >= demand[vi]){ // check if there is enough resource for the current demand
					// map the vm to the PM
					v[vi]=p+1;
					// lease the resource
					avail[p] = avail[p] - demand[vi];
				}
				else continue;
				if(vi==v.length-1){
					cntr++;
					System.out.println(" Solution:"+cntr+" ; ("+v[0]+ " , "+v[1]+" , "+v[2]+")");
				}
				if((vi+1)<v.length)
					recurMap(totP,(int[]) v.clone(),(vi+1),(int[]) avail.clone());	
				// now release the leased resource form previous PM.
				avail[p] = avail[p] - demand[vi];
			}
			//System.out.println("------------ONE SOLUTION FINISHED -----"+v[0]+ " , "+v[1]);
			
}// END of temp2
}
