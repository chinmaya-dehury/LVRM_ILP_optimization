package optimization;

/**
 * Here mapping of Virtual links followed by mapping of virtual nodes (VMS)
 * 
 * @author chinmaya
 * 
 */
public class MapByLink {
	int solCntr;
	int[][] demandLink; // Bandwidth demand of the virtual links
	int[] demandNode; // computing resource demand of the VMs

	int[][] availLinkRsc; // Bandwidth availability of the physical links
	int[] availNodeRsc; // Computing resrc availability of the PMs

	/**
	 * 
	 * @param dmndL : List of   
	 * @param dmndN : 
	 * @param avlL : 
	 * @param avlN :
	 */
	MapByLink(int[][] dmndL, int[] dmndN, int[][] avlL, int[] avlN) {
		solCntr = 0;
		demandLink = (int[][]) dmndL.clone();
		demandNode = (int[]) dmndN.clone();

		availLinkRsc = (int[][]) avlL.clone();
		availNodeRsc = (int[]) avlN.clone();
	}

	public void initMap() {

		int[] vms = new int[demandNode.length];
		for (int i = 0; i < demandNode.length; i++) {
			vms[i] = -1;
		}
		int vLnkIndex = 0;
		System.out.print("Initilizing the Embedding process...\n");
//		System.out.print("VM resource requirements; ");
//		for (int i = 0; i < vms.length; i++) {
//			System.out.print(demandNode[i]+" ");
//		}
//		System.out.print("\n");
//		System.out.print("bandwidth resource requirements; ");
//		for (int i = 0; i < demandLink.length; i++) {
//			System.out.print(demandLink[i][0]+" ");
//		}
		System.out.print("\n");
		recursiveMap((int[]) vms.clone(), vLnkIndex, (int[][]) availLinkRsc.clone(), (int[])availNodeRsc.clone() );
	}

	/**
	 * 
	 * @param vms
	 *            : List of VMs. Used for mapping result.
	 * @param vLnkIndx
	 *            : Index of current Virtual Link
	 * @param avlLnkRsc
	 *            : Available Bandwidth resource of Phy. Links
	 * @param avlNodeRsc
	 *            : Available computing resource of PMs
	 */
	private void recursiveMap(int[] vms, int vLnkIndx, int[][] avlLnkRsc,
			int[] avlNodeRsc) {
		// START: Loop through each physical links 
		for (int pLnk = 0; pLnk < avlLnkRsc.length; pLnk++) {
			int isAllMapped=-1; //0: no VM mapped or the current Phy.Link is discarded;  1: one VM mapped;  2: Both VM mapped;
			// Mapping V.Link 'vLnkIndex' onto Phy.Link 'pLnk'
			System.out.print("\n  :: Processing vLink:"+(vLnkIndx+1)+" and pLink:"+(pLnk+1));
			// After leasing the node and network resource,
			// following four variable keep the current mapping solution
			int pm1Indx_sel=-1, pm2Indx_sel=-1, vm1Indx_sel=-1, vm2Indx_sel=-1;
			int isLinkMapped=0; // o: the link is not mapped;  1: the link is mapped.
		
			
			// check the bw resource constraint
			//System.out.print("\nChecking Resource constraint. pLinkIndex: "+pLnk+"  vLinkIndex: "+vLnkIndx);
			//System.out.print("\n");
			if (avlLnkRsc[pLnk][0] >= demandLink[vLnkIndx][0]) {
				// SUCCESS: availability is more than demand bw
				//System.out.print("\n PASS: Link resource constraint passed ");
				//get index of both VMs
				int vm1Indx = demandLink[vLnkIndx][1] - 1, vm2Indx = demandLink[vLnkIndx][2] - 1;
				//get index of both PMs
				int pm1Indx = avlLnkRsc[pLnk][1]-1, pm2Indx = avlLnkRsc[pLnk][2]-1;
				
				int pmOfVM1 = vms[vm1Indx]; // PM of VM1. -1 if the VM1 is not yet mapped
				int pmOfVM2 = vms[vm2Indx]; // PM of VM2. -1 if the VM2 is not yet mapped
				
				// check if the corresponding VMs are already mapped
				if (vms[vm1Indx] != -1) {// the first VM is ALREADY mapped
					// System.out.print("\n FIRST VM "+(vm1Indx+1)+" is already mapped to PM"+vms[vm1Indx]);
					// check if the Phy.Link is attached to the PM of first VM 
					if (vms[vm1Indx] != (pm1Indx+1)	&& vms[vm1Indx] != (pm2Indx+1)) {
						// The current Phy.Link is NOT attached to the PM of first VM
						// discard the current phy. Link
						isAllMapped=0; 
						//continue;
					}
					else { // First VM is mapped to any one of the PMs of current Phy. Link
						isAllMapped = 1;
						// If first VM is already mapped, check for the second VM
						if(vms[vm2Indx] == -1){
							// Second VM is NOT yet mapped to any PM
							
							// Check if second VM can be placed on 'pmOfVM1'
							if(demandNode[vm2Indx] <= availNodeRsc[pmOfVM1-1]){
								// the second VM can be placed onto the PM where VM1 is placed
								mapSingleVM(vm2Indx, pmOfVM1-1, vms, avlNodeRsc);
								vm2Indx_sel = vm2Indx;
								pm2Indx_sel = pmOfVM1-1;
								//allocate the virtual link
								mapLink(vLnkIndx, pLnk,(int[][]) avlLnkRsc.clone());
								isLinkMapped=1;
								isAllMapped=2;
								//continue; // continue to the next phy.Link
							}
							else {
								// place the VM2 onto the PM where VM1 is NOT placed
								int tmpPm=-1; // 
								if(pm1Indx == pmOfVM1-1)
									tmpPm = pm2Indx+1;
								else if(pm2Indx == pmOfVM1-1)
									tmpPm = pm1Indx + 1;
								else{
									System.out.println("\nERROR: Something went wrong !!! Exit Point - 1\n");
									System.exit(0);
									}
								if(demandNode[vm2Indx] <= availNodeRsc[tmpPm-1]){
									// the second VM can be placed onto the PM where VM1 is NOT placed
									mapSingleVM(vm2Indx, tmpPm-1, vms, avlNodeRsc);
									vm2Indx_sel = vm2Indx;
									pm2Indx_sel = tmpPm-1;
									mapLink(vLnkIndx, pLnk,(int[][]) avlLnkRsc.clone());
									isLinkMapped=1;
									isAllMapped=2;
									//continue; // continue to the next phy.Link
								}
								else{
									// VM2 can not be mapped due to unavailable node resource
									// So discard the current Phy.Link
									isAllMapped=0;
									//continue;
								}
							}								
						}
					}
				} // First VM cheked
				if (vms[vm2Indx] != -1) {// the second VM is ALREADY mapped
					// System.out.print("\n SECOND VM "+(vm1Indx+1)+" is already mapped to PM"+vms[vm1Indx]);
					if (vms[vm2Indx] != avlLnkRsc[pLnk][1] 	&& vms[vm2Indx] != avlLnkRsc[pLnk][2]) {
						// discard the current phy. Link
						isAllMapped=0;
						continue;
					}
					else{// Second VM is mapped to any one of the PMs of current Phy. Link
						// If second VM is already mapped, check for the first VM
						if(vms[vm1Indx] == -1){
							// The first VM is not yet mapped
							
							//check if the first VM can be mapped onto the 'pmofVM2'
							if(demandNode[vm1Indx] <= availNodeRsc[pmOfVM2-1]){
								// the first VM can be placed onto the 'pmOfVM2'
								mapSingleVM(vm1Indx, pmOfVM1-1, vms, avlNodeRsc);
								vm1Indx_sel = vm1Indx;
								pm1Indx_sel = pmOfVM1-1;
								mapLink(vLnkIndx, pLnk,(int[][]) avlLnkRsc.clone());
								isLinkMapped=1;
								isAllMapped=2;
								//continue; // continue to the next phy.Link
							}
							else{
								// place the VM1 onto the PM where VM2 is NOT placed
								int tmpPm=-1;
								if(pm1Indx == pmOfVM2-1)
									tmpPm = pm2Indx+1;
								else if(pm2Indx == pmOfVM2-1)
									tmpPm = pm1Indx+1;
								else {
									System.out.println("\nERROR: Something went wrong !!! Exit Point - 2\n");
									System.exit(0);
								}
								
								if(demandNode[vm2Indx] <= availNodeRsc[tmpPm-1]){
									// the first VM can be placed onto the PM where VM2 is NOT placed
									mapSingleVM(vm1Indx, tmpPm-1, vms, avlNodeRsc);
									vm1Indx_sel = vm1Indx;
									pm1Indx_sel = tmpPm-1;
									mapLink(vLnkIndx, pLnk,(int[][]) avlLnkRsc.clone());
									isLinkMapped=1;
									isAllMapped=2;
									//continue; // continue to the next phy.Link
								}
								else{
									// VM1 can not be mapped due to unavailable node resource
									// So discard the current Phy.Link
									isAllMapped=0;
									//continue;
								}
							}
						}
					}						
				} // Second VM checked
				
				if(vms[vm1Indx] == -1 && vms[vm2Indx] == -1){
					//System.out.print("\n Both VMs are NOT yet mapped");
					
					// Enable the following two 'if' conditions to embed both end VMs onto single PM 
					/*if(demandNode[vm1Indx]+demandNode[vm2Indx] <= availNodeRsc[pm1Indx]){
						// Both VMs can be placed on PM1
						mapSingleVM(vm1Indx, pm1Indx, vms, avlNodeRsc);
						vm1Indx_sel = vm1Indx;
						pm1Indx_sel = pm1Indx;
						mapSingleVM(vm2Indx, pm1Indx, vms, avlNodeRsc);
						vm2Indx_sel = vm2Indx;
						pm2Indx_sel = pm1Indx;
						mapLink(vLnkIndx, pLnk,(int[][]) avlLnkRsc.clone());
						isLinkMapped=1;
						isAllMapped=2;
					}
					else if(demandNode[vm1Indx]+demandNode[vm2Indx] <= availNodeRsc[pm2Indx]){
						// Both VMs can be Placed on PM2
						mapSingleVM(vm1Indx, pm2Indx, vms, avlNodeRsc);
						vm1Indx_sel = vm1Indx;
						pm1Indx_sel = pm2Indx;
						mapSingleVM(vm2Indx, pm2Indx, vms, avlNodeRsc);
						vm2Indx_sel = vm2Indx;
						pm2Indx_sel = pm2Indx;
						mapLink(vLnkIndx, pLnk,(int[][]) avlLnkRsc.clone());
						isLinkMapped=1;
						isAllMapped=2;
					}
					else{
						// Both VMs can not be placed on any of single PM
						 
					*/
						// Now place VM1 onto either PM1 or onto PM2
						if(demandNode[vm1Indx] <= availNodeRsc[pm1Indx] && demandNode[vm2Indx] <= availNodeRsc[pm2Indx]){
							// Map VM 'vm1Indx' onto PM 'pm1Indx'
							mapSingleVM(vm1Indx, pm1Indx, vms, avlNodeRsc);
							vm1Indx_sel = vm1Indx;
							pm1Indx_sel = pm1Indx;
							// Map VM 'vm2Indx' onto PM 'pm2Indx'
							mapSingleVM(vm2Indx, pm2Indx, vms, avlNodeRsc);
							vm2Indx_sel = vm2Indx;
							pm2Indx_sel = pm2Indx;
							mapLink(vLnkIndx, pLnk,(int[][]) avlLnkRsc.clone());
							isLinkMapped=1;
							isAllMapped=2;
						}
						else if(demandNode[vm1Indx] <= availNodeRsc[pm2Indx] && demandNode[vm2Indx] <= availNodeRsc[pm1Indx]) {
							// Map VM 'vm1Indx' onto PM 'pm2Indx'
							mapSingleVM(vm1Indx, pm2Indx, vms, avlNodeRsc);
							vm1Indx_sel = vm1Indx;
							pm1Indx_sel = pm2Indx;
							// Map VM 'vm2Indx' onto PM 'pm1Indx'
							mapSingleVM(vm2Indx, pm1Indx, vms, avlNodeRsc);
							vm2Indx_sel = vm2Indx;
							pm2Indx_sel = pm1Indx;
							mapLink(vLnkIndx, pLnk,(int[][]) avlLnkRsc.clone());
							isLinkMapped=1;
							isAllMapped=2;
						}
						else {
							//System.out.println("\nERROR: Something went wrong !!! Exit Point - 3\n");
							//System.exit(0);
						}							
					/*}*/
				}
				// Check if both VMs can be placed on
			} // END of BW_resrc_constraint check
			else continue;
			if (vLnkIndx == demandLink.length-1) {
				int isItaSol=1; // default is yes
				for (int iii = 0; iii < vms.length; iii++) {
					if(vms[iii] == -1){
						isItaSol=0; // this is not a solution
					}
				}
				if(isItaSol==1){
					solCntr++;
					System.out.print(" Solution #"+solCntr+" found ====> ");
					System.out.print("VMs ");				
					for (int iii = 0; iii < vms.length; iii++) {
						System.out.print(vms[iii]+" ");
					}
					System.out.println("\n");
				}
			}
			if((vLnkIndx+1) < demandLink.length){				
				recursiveMap((int[]) vms.clone(), vLnkIndx+1,(int[][]) avlLnkRsc.clone(), (int[]) avlNodeRsc.clone());
			}
									
			// release the resource
			
			if(isAllMapped!=0) {
				// atleast one VM is mapped
				// release the link (nw) resouce  
				if(isLinkMapped == 1)
					releaseLink(vLnkIndx, pLnk, avlLnkRsc);
				// release the node resources
				if(vm1Indx_sel != -1 && pm1Indx_sel != -1){
					releaseNode(avlNodeRsc, pm1Indx_sel, vm1Indx_sel, vms);						
				}
				if(vm2Indx_sel != -1 && pm2Indx_sel != -1){
					releaseNode(avlNodeRsc, pm2Indx_sel, vm2Indx_sel, vms);						
				}
					//System.out.print(arg0);
			}
			
			
		}// END: Loop through each physical links
	} // END of recursiveMap
	
	/**
	 * 
	 * @param avlNodeRsc : list of PMs with their resource
	 * @param pm1Indx_sel: from which PM to release the computing resource
	 * @param vm1Indx_sel: The index of the VM to release
	 * @param vms		 : reset the solution to -1
	 */
	private void releaseNode(int[] avlNodeRsc, int pmIndx_sel,
			int vmIndx_sel, int[] vms) {
		// System.out.print("\nReleasing VM "+(vmIndx_sel+1)+" from the PM "+(pmIndx_sel+1));
		avlNodeRsc[pmIndx_sel] = avlNodeRsc[pmIndx_sel]+demandNode[vmIndx_sel];
		vms[vmIndx_sel] = -1;
		
	}

	/**
	 * Release the bandwidth resources of the physical link at 'pLnkIndx' that are leased/allocated to virtual link at 'vLnkIndx' 
	 * @param vLinkIndx : index of the virtual link
	 * @param pLinkIndx	: index of the physical link
	 * @param avlLnkRsc	: set of physical links and their available bandwidth resources
	 */
	private void releaseLink(int vLinkIndx, int pLinkIndx, int[][] avlLnkRsc){
		avlLnkRsc[pLinkIndx][0] = avlLnkRsc[pLinkIndx][0] + demandLink[vLinkIndx][0]; 
	}
	
	/**
	 * map the virtual link at 'vLnkIndx' onto the physical link at 'pLinkIndx'
	 * @param vLinkIndx : index of the virtual link
	 * @param pLinkIndx	: index of the physical link
	 * @param avlLnkRsc	: set of physical links and their available bandwidth resources
	 */
	private void mapLink(int vLinkIndx, int pLinkIndx, int[][] avlLnkRsc){
		// System.out.print("\n PASS: Mapping the vLink:"+(vLinkIndx+1)+" onto pLink:"+(pLinkIndx+1)+"\n");
		avlLnkRsc[pLinkIndx][0] = avlLnkRsc[pLinkIndx][0] - demandLink[vLinkIndx][0]; 
	}
	/**
	 * 
	 * @param vmIndx : Index of virtual machine
	 * @param pmOfVM : PM Index where the VM is to be placed
	 * @param vms 	 : List of vms. Store the current mapping information 
	 * @param avlNodeRsc : Available PM resources.
	 */
	private void mapSingleVM(int vmIndx, int pmIndx, int[] vms, int[] avilNodeRsc) {
		// System.out.print("\n PASS: Mapping VM-"+(vmIndx+1)+" onto PM-"+(pmIndx+1));
		vms[vmIndx] = pmIndx+1;
		avilNodeRsc[pmIndx] = avilNodeRsc[pmIndx] - demandNode[vmIndx];
	}

	/**
	 * bandwidth resource constraint is already checked
	 * @param pmOfVM1
	 * @param pmOfVM2
	 * @param vm1Indx
	 * @param vm2Indx
	 * @param vms
	 * @param availNodeRsc
	 * @return 1: if mapping success , 0: if ERROR (no enough resource)
	 */
	public int mapBothVMs(int pmOfVM1, int pmOfVM2, int vm1Indx, int vm2Indx, int[] vms, int[][] availNodeRsc){
		
		return 0;
	}

	public void systemInfo(){
		System.out.print("\n=============================================================");
		System.out.print("\n======   DISPLAYING  SYSTEM  INFORMATION   ==================");
		System.out.print("\n=============================================================\n");
		// Disp PMs information
		System.out.print("PM    Resource\n");
		System.out.print("--------------\n");
		for(int c=0 ; c<availNodeRsc.length ; c++){
			System.out.print((c+1)+"    "+availNodeRsc[c]+"\n");
		}
		// Disp Physical Links information
		System.out.print("pLink  BW    FromPM    ToPM\n");
		System.out.print("-----------------------\n");
		for(int c=0 ; c<availLinkRsc.length ; c++){
			System.out.print((c+1)+"    "+availLinkRsc[c][0]+"    "+availLinkRsc[c][1]+"        "+availLinkRsc[c][2]+"\n");
		}
		System.out.print("\n");
		
		// disp Virtual machines information
		System.out.print("VM    Demand\n");
		System.out.print("------------\n");
		for(int c=0 ; c<demandNode.length ; c++){
			System.out.print((c+1)+"    "+demandNode[c]+"\n");
		}
		// Disp Physical Links information
		System.out.print("vLink  BW_demnd    FromVM    ToVM\n");
		System.out.print("---------------------------------\n");
		for(int c=0 ; c<demandLink.length ; c++){
				System.out.print((c+1)+"    "+demandLink[c][0]+"    "+demandLink[c][1]+"        "+demandLink[c][2]+"\n");
		}
	}
} // END of class
