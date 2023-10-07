package application;

import java.util.ArrayList;

public class PTR
{
    public ArrayList<Record> ptr;
    int ram;

    public PTR(int ram)
    {
    	this.ram = ram;
    	ptr = new ArrayList<Record>();
    }

    public Record getRecord(int page)
    {
        for (Record record : ptr)
        {
            if (record.page == page)
            {
                return record;
            }
        }
        return null;
    }
}
