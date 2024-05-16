package communication;

import java.util.Date;
import java.util.GregorianCalendar;

public class SmartAlarm
{
    public static final int satUndefined = 0;
    public static final int satEmergS = 1;
    public static final int satMainErrorS = 2;
    public static final int satPLCMessage = 3;
    public static final int satEmergM = 4;
    public static final int satUser = 5;
    public static final int satSystem = 6;

    public static final int slcHIStart = 1;
    public static final int slcHIStop = 2;


    private Integer CN = 0;
    private Integer AlarmType = satUndefined;
    private Integer [] Parameters;
    private Boolean Active = false;
    private String UserMessageKey = null;
    private String FingerPrint = null;
    private Date DateAndTime = null;
    private String Description = null;
    private Integer Index = null;

    static final short[] TabDay =
        {  0,  
                31,  
                29,  
                31,  
                30,  
                31,  
                30,  
                31,  
                31,  
                30,  
                31,  
                30,  
                31   
        };

    static final short[] TabDay1 =
        {  0,  
                31,  
                29,  
                31,  
                30,  
                31,  
                30,  
                31,  
                31,  
                30,  
                31,  
                30,  
                31   
        };


    public synchronized Integer getIndex()
    {
        return Index;
    }

    public synchronized void setIndex(Integer index)
    {
        Index = index;
    }

    public synchronized String getUserMessageKey()
    {
        return UserMessageKey;
    }

    public synchronized void setUserMessageKey(String userMessageKey)
    {
        UserMessageKey = userMessageKey;
    }

    public synchronized Integer getAlarmType()
    {
        return AlarmType;
    }

    public synchronized void setAlarmType(Integer alarmType)
    {
        AlarmType = alarmType;
    }

    public synchronized Integer getCN()
    {
        return CN;
    }

    public synchronized void setCN(Integer cN)
    {
        CN = cN;
    }

    public synchronized Integer[] getParameters()
    {
        return Parameters;
    }

    public synchronized void setParameters(Integer[] parameters)
    {
        Parameters = parameters;
    }

    public synchronized Date getDateAndTime()
    {
        return DateAndTime;
    }

    public synchronized void setDateAndTime(Date dateAndTime)
    {
        DateAndTime = dateAndTime;
    }

    public synchronized String getDescription()
    {
        return Description;
    }

    public synchronized void setDescription(String description)
    {
        Description = description;
    }

    public synchronized Boolean getActive()
    {
        return Active;
    }

    public synchronized void setActive(Boolean active)
    {
        Active = active;
    }

    public SmartAlarm(Integer cn, Integer alarmType,
            Integer[] parameters, boolean active, String description,
            String userMessageKey, Date dateAndTime)
    {
        super();
        CN = cn;
        AlarmType = alarmType;
        Parameters = parameters;
        Active = active;
        Description = description;
        UserMessageKey = userMessageKey;
        DateAndTime = dateAndTime;
    }
    
    public SmartAlarm()
    {
        super();
        CN = 0;
        AlarmType = 0;
        Parameters = new Integer[]{};
        Active = false;
        Description = "";
        UserMessageKey = "";
        DateAndTime = null;
    }


    public String getFingerPrint()
    {
        if (FingerPrint == null)
        {
            Integer hk = 0;
            hk = hk ^ AlarmType;
            if (DateAndTime != null)
                hk = hk ^ DateAndTime.toString().hashCode();

            for(Integer p: Parameters)
                hk = hk ^ Integer.toString(p).hashCode();

            FingerPrint = hk + Active.toString() + CN.toString();
        }

        return FingerPrint;
    }


    public Date GetCnDate(Integer SecTot)
    {
        Integer tm_mon;
        Integer HPerY;
        Integer tm_hour;
        Integer tm_yday;
        Integer tm_wday;
        Integer tm_mday;
        Integer tm_csec;
        Integer tm_sec;
        Integer tm_min;
        Integer tm_year;
        Integer CumDays;




        Integer INI_YEAR = 1980;
        Integer INI_W_DAY = 2;       // 01/01/1980 era martedi = 2   

        tm_csec = 0;

        tm_sec = SecTot % 60;
        SecTot /= 60;

        tm_min = SecTot % 60;
        SecTot /= 60;

        Integer Ip = SecTot / (1461 * 24);

        tm_year = INI_YEAR + ( Ip * 4 );

        CumDays = 1461 * Ip;

        SecTot = SecTot % ( 1461 * 24 );

        for ( ; ; )
        {
            HPerY = 365 * 24;

            if ( ( tm_year % 4 ) == 0 )
                HPerY += 24;


            if ( SecTot < HPerY )
                break;

            CumDays += HPerY / 24;

            tm_year++;

            SecTot -= HPerY;
        }

        tm_hour = SecTot % 24;

        SecTot /= 24;

        tm_yday = 1 + SecTot;

        CumDays += SecTot + INI_W_DAY;

        tm_wday = CumDays % 7;

        SecTot++;

        if ( ( tm_year % 4 ) == 0 )
        {
            if ( SecTot > 60 )
            {
                SecTot -= 1;
            }
            else
            {
                if ( SecTot == 60 )
                {
                    tm_mon = 2;
                    tm_mday = 29;


                    return new GregorianCalendar( tm_year, tm_mon, tm_mday, tm_hour, tm_min, tm_sec).getTime();
                }
            }
        }





        for ( tm_mon = 1; TabDay1[tm_mon] < SecTot; tm_mon++ )
            SecTot -= TabDay1[tm_mon];

        tm_mday = SecTot;

        return new GregorianCalendar( tm_year, tm_mon, tm_mday, tm_hour, tm_min, tm_sec).getTime();

    }
    
    
    public SmartAlarm getACopy()
    {
        return new SmartAlarm(CN, AlarmType, Parameters, Active, Description, UserMessageKey, DateAndTime);
    }
    
    
    
}
