package communication;

import java.util.GregorianCalendar;

public class MSysFileInfo {
    public byte FAttributes;
    public GregorianCalendar FDate;
    public Integer FSize;
    public String FName;
    public String FPath;
    
	public static final byte  FATTRIBUTES_FOLDERS = 0x10;
	public static final byte  FATTRIBUTES_FILES = 0x20;

    
	public MSysFileInfo() {
		super();
	}

	
	
}
