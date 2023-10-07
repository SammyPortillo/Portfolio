package application;

public class Record
{
	//public static int count = 0;
    //public int id;
	public int logicalAddress;
    public int page;
    public int offset;
    public int frame;
    public int physicalAddress;
    public int sector;
    public int valid;			// Page 375
    int dirtyBit;

    public Record(int logicalAddress, int page, int offset, int frame, int physicalAddress, int sector)
    {
    	this.logicalAddress = logicalAddress;
    	this.page = page;
    	this.offset = offset;
    	this.frame = frame;
    	this.physicalAddress = physicalAddress;
    	this.sector = sector;
    	dirtyBit = 0;			//0 -> Not Dirty
    	valid = 1;
    }
    
	void print()
	{
		System.out.printf("%4d %5d %5d %5d \n", page, frame, sector, valid);
	}
}