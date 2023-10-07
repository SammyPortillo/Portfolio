//	Project Based on the following:
//			Memory Size		Number of
//			Bytes			Pages
//	VM		16k	= 2^14		2^10			
//	RAM		1k	= 2^10		2^6		->  This number will vary
//	Page	16	= 2^4
//	Sector	16	= 2^4

package application;

import java.util.ArrayList;

public class Program {
	int pages; // Number of Program pages (Program Size in terms of pages)
	int vm = 1024; // Number of pages
	int ram; // Number of pages
	int sectorSize; // = 16;
	int hd = 1048576;
	int hdPages; // = hd / sectorSize; // 65536 - > 2^16
	int vmProgramBase; // Program starting address
	int ramProgramBase;
	int hdProgramBase;
	PTR ptr = new PTR(ram);
	int vmAllocation[]; // = new int[vm * sectorSize]; // Stores page use
	int[] ramAllocation;
	int pageHits[]; // Hits
	int hdAllocation[] = new int[hd];
	ArrayList<String> victims = new ArrayList<String>();
	ArrayList<String> hits = new ArrayList<String>();

	public Program(int pages, int ram, int sector) {
		this.pages = pages;
		this.ram = ram;
		this.sectorSize = sector;
		hdPages = hd / sectorSize; // 65536 - > 2^16
		vmAllocation = new int[vm * sectorSize]; // Stores page use
		hdProgramBase = (int) (Math.random() * vm * 10);
		ramAllocation = new int[ram]; // Stores page use
		pageHits = new int[pages]; // Hits
		System.out.println("Number of Program pages = " + pages);
		System.out.println("Number of RAM pages = " + ram);
		System.out.println("Page Table Register (PTR)");
		System.out.println("ID Page  Frame Sector V/I");
	}

	int cycle()
	{
		int logicalAddress = getLogicalAddress();
		int page = logicalAddress / sectorSize; // Truncate decimal
		int offset = logicalAddress % sectorSize;
		int sector = getSector();
		int frame;

		if (pageHits[page] == 0) {
			
			if (ptr.ptr.size() < ram) // Test if user space is full other wise need victim
			{
				frame = getFrame();
				int physicalAddress = frame * sectorSize + offset;
				Record r = new Record(logicalAddress, page, offset, frame, physicalAddress, sector);
				ptr.ptr.add(r);
				System.out.print(ptr.ptr.size() - 1 + " ");
				r.print();	
				pageHits[page] = ptr.ptr.size();	
				return 1;
			} else {
				int index = pageReplacement();
				System.out.println("Victim index " + index + " new Page " + page);
				ptr.ptr.get(index).logicalAddress = logicalAddress;
				ptr.ptr.get(index).page = page;
				ptr.ptr.get(index).offset = offset;
				ptr.ptr.get(index).sector = sector;
				System.out.print(index);
				ptr.ptr.get(index).print();
				pageHits[page] = index + 1;	
				return 2;				// Boolean value not being used
			}
		}
		else
		{
			// Page Hit
			//Record r = ptr.ptr.get( pageHits[page] );
			frame = ptr.ptr.get( pageHits[page] - 1 ).frame;
			int physicalAddress = frame * sectorSize + offset;
			Record r = new Record(logicalAddress, page, offset, frame, physicalAddress, sector);
			String s = String.format("%9d %9d %9d %18d %9d %9d %18d", r.logicalAddress, r.page, r.offset, r.physicalAddress,
					r.frame, r.offset, r.sector);
			hits.add(s);			
			//System.out.print(ptr.ptr.size() - 1 + " ");
			r.print();
			return 3;
		}
	}

	
	int getLogicalAddress() {
		int la;
		do {
			la = (int) (Math.random() * (pages * sectorSize)); // Every iteration increases chance of randomly finding a
																// free page
			System.out.println("LogicalAddress = " + la);
		} while (vmAllocation[la] == 1);

		vmAllocation[la] = 1;
		return la;
	}

	int getFrame() {
		int page;
		do {
			// page = ramProgramBase + (int) (Math.random() * ram);
			page = (int) (Math.random() * ram);
			System.out.println("getFrame " + page);
		} while (ramAllocation[page] == 1);

		ramAllocation[page] = 1;
		return page;
	}

	int pageReplacement() {
		// If ram is full - > every row in PTR is also full.
		// Need victim // Page 403 - 411
		int victim = (int) (Math.random() * ram); // victim PTR index - > victim Ram frame
		// if dirtyBit set - > write back to hard drive
		ptr.ptr.get(victim).valid = 0; // Set victim reference to available
		Record r = ptr.ptr.get(victim);
		String s = String.format("%9d %9d %9d %18d %9d %9d %18d", r.logicalAddress, r.page, r.offset, r.physicalAddress,
				r.frame, r.offset, r.sector);
		victims.add(s);
		int victimVM = ptr.ptr.get(victim).page; // This virtual memory now no longer valid
		vmAllocation[victimVM] = 0;
		// int victimRam = ptr.ptr.get(victim).frame;
		// ramAllocation[victim] = 0; // Replacing 0 -> 1 -> 0
		// Read in data from hard drive into ram.
		ptr.ptr.get(victim).valid = 1; // Set back to 1
		// ramAllocation[victim] = 1;
		return victim;
	}

	int getSector()
	{
		int page;
		do {
			page = hdProgramBase + (int) (Math.random() * 2 * vm);
			System.out.println("getSector " + page);
		} while (hdAllocation[page] == 1);

		hdAllocation[page] = 1;
		return page;
	}

	void print() {
		System.out.println("Program pages " + pages);
		System.out.println("VM Program Base Address " + vmProgramBase);
		System.out.println("RAM Program Base Address " + ramProgramBase);
		System.out.println("Page Table Register (PTR)");
		System.out.println("ID Page  Frame Sector V/I");
		int x = 0;
		for (Record r : ptr.ptr) {
			System.out.printf("%3d %4d %5d %5d %5d \n", x++, r.page, r.frame, r.sector, r.valid);
		}
	}

	public ArrayList<String> updatePage()
	{
		ArrayList<String> vmList = new ArrayList<String>();

		int x = 0;
		for (Record r : ptr.ptr) {
			String s = String.format("%3d %9d %9d %9d %18d %9d %9d %18d", x++, r.logicalAddress, r.page, r.offset,
					r.physicalAddress, r.frame, r.offset, r.sector);
			vmList.add(s);
		}
		return vmList;
	}	
}