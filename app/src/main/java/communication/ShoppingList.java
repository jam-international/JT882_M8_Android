package communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;


public class ShoppingList extends Protocol {
    private Map<String, MultiCmdItem> Items;
    public Poller Poll;
    private final List<MultiCmdItem> WriteCue;
    private byte[] Cmd;
    private byte[] Ans;
    private List<MultiCmdItem> ShopQuest;
    private SortedSet<String> SortedKeys; 
    private Integer WatchDogPeriod = 0;
    private Integer WatchDogIndex = 0;
    private UploadStack UploadCue = null;
    private Long CommOffLatch = 0l;
    private Long CommOffLapse = 0l;
    private boolean Made = false;

    private Integer WDogLatch = null;
    private boolean Enabled = false;
    
    private MultiCmdItem PageIdMCI = null;
    private MultiCmdItem HideMaskValueMCI1 = null;
    private MultiCmdItem HideMaskValueMCI2 = null;
    private MultiCmdItem CNScriptRunnerMCI = null;
    private SmartAlarms Alarms = null;
    private Boolean WatchAlarms = true;
    


    public synchronized MultiCmdItem getCNScriptRunnerMCI()
    {
        return CNScriptRunnerMCI;
    }

    public synchronized void setCNScriptRunnerMCI(MultiCmdItem cNScriptRunnerMCI)
    {
        CNScriptRunnerMCI = cNScriptRunnerMCI;
    }

    public synchronized Boolean getWatchAlarms()
    {
        return WatchAlarms;
    }

    public synchronized void setWatchAlarms(Boolean watchAlarms)
    {
        WatchAlarms = watchAlarms;
    }



    public synchronized SmartAlarms getAlarms()
    {
        return Alarms;
    }

    public synchronized void setAlarms(SmartAlarms alarms)
    {
        Alarms = alarms;
    }

    public synchronized MultiCmdItem getHideMaskValueMCI1()
    {
        return HideMaskValueMCI1;
    }

    public synchronized void setHideMaskValueMCI1(MultiCmdItem hideMaskValueMCI1)
    {
        HideMaskValueMCI1 = hideMaskValueMCI1;
    }

    public synchronized MultiCmdItem getHideMaskValueMCI2()
    {
        return HideMaskValueMCI2;
    }

    public synchronized void setHideMaskValueMCI2(MultiCmdItem hideMaskValueMCI2)
    {
        HideMaskValueMCI2 = hideMaskValueMCI2;
    }

    public synchronized MultiCmdItem getPageIdMCI()
    {
        return PageIdMCI;
    }

    public synchronized void setPageIdMCI(MultiCmdItem pageIdMCI)
    {
        PageIdMCI = pageIdMCI;
    }

    public synchronized boolean isEnabled()
    {
        return Enabled;
    }

    public synchronized void setEnabled(boolean enabled)
    {
        Enabled = enabled;
    }

    public synchronized Long getCommOffLapse()
    {
        return CommOffLapse;
    }

    public synchronized void setCommOffLapse(Long commOffLapse)
    {
        CommOffLapse = commOffLapse;
    }

    public synchronized Long getCommOffLatch()
    {
        if (!isEnabled())
            return 0l;

        return CommOffLatch;
    }

    public synchronized void setCommOffLatch(Long commOffLatch)
    {
        CommOffLatch = commOffLatch;
    }

    public synchronized UploadStack getUploadCue()
    {
        return UploadCue;
    }

    public synchronized void setUploadCue(UploadStack uploadCue)
    {
        UploadCue = uploadCue;
    }

    public synchronized Integer getWatchDogPeriod() {
        return WatchDogPeriod;
    }

    public synchronized void setWatchDogPeriod(Integer watchDogPeriod) {
        WatchDogPeriod = watchDogPeriod;
    }

    public synchronized Integer getWatchDogIndex() {
        return WatchDogIndex;
    }

    public synchronized void setWatchDogIndex(Integer watchDogIndex) {
        WatchDogIndex = watchDogIndex;
    }

    public ShoppingList(String the_ip, int port, Double readTimeOut, Double connectTimeOut) {
        super(the_ip, port, readTimeOut, connectTimeOut);


        Items = Collections.synchronizedMap(new HashMap<String, MultiCmdItem>());

        WriteCue = Collections.synchronizedList(new ArrayList<MultiCmdItem>());
        UploadCue = new UploadStack(this);
        SortedKeys = new TreeSet<String>(Items.keySet());


        Alarms = new SmartAlarms(this);

        //Watch Sempre attivo
        //WatchAlarms = (prj.getOptions().getCronologEvents() != 0);

        
        if (WatchAlarms)
            Alarms.Init();

        setMade(false);

    }
    
    public ShoppingList(int rs232Port, Double readTimeOut) {
        super(rs232Port, readTimeOut);


        Items = Collections.synchronizedMap(new HashMap<String, MultiCmdItem>());

        WriteCue = Collections.synchronizedList(new ArrayList<MultiCmdItem>());
        UploadCue = new UploadStack(this);
        SortedKeys = new TreeSet<String>(Items.keySet());

        
        Alarms = new SmartAlarms(this);
        
        //Watch Sempre attivo
        //WatchAlarms = (prj.getOptions().getCronologEvents() != 0);
        

        if (WatchAlarms)
            Alarms.Init();
        
        setMade(false);

    }

    public String GetVerboseStatus()
    {
        String s = "IP " + getIP() + ":" + getPort() + ": ";

        if (isEnabled())
        {        
            if (IsConnected())
                if (getReturnCode() == rcOK)
                    s += "OK"; 
                else
                    s += "Error: " +
                            getReturnCode() + "]";
            else
                s += "[!] Not Connected";


        }
        else
        {
            s += "Disabled";
        }

        return s;
    }

    public synchronized Map<String, MultiCmdItem> getItems() {
        return Items;
    }

    public synchronized void setItems(Map<String, MultiCmdItem> items) {
        Items = items;
    }

    //	public ShoppingList(Protocol port)
    //	{
    //		//Items =  Collections.synchronizedList(new ArrayList<MultiCmdItem>());
    //		Items = Collections.synchronizedMap(new HashMap<String, MultiCmdItem>());
    //		
    //		WriteCue = Collections.synchronizedList(new ArrayList<MultiCmdItem>());
    //		SortedKeys = new TreeSet<String>(Items.keySet());
    //		
    ////		Poll = new Poller(this);
    ////		Port = port;
    ////		Start();
    //		setMade(false);
    //
    //	}

    public synchronized boolean isMade() {
        return Made;
    }

    public synchronized void setMade(boolean made) {
        Made = made;
        if (!made)
            SortedKeys.clear();
    }

    public MultiCmdItem Add (String User, int cn, int type, int index, int parameter)
    {
        MultiCmdItem mci = new MultiCmdItem(cn, type, index, parameter, this);

        if (Items.containsKey(mci.getKey()))
            mci = Items.get(mci.getKey());
        else
            Items.put(mci.getKey(), mci);

        if (!mci.Users.contains(User))
            mci.Users.add(User);

        setMade(false);

        return mci;
    }


    public void Clear()
    {
        Items.clear();
        setMade(false);
    }

    public void  Clear(String User)
    {
        synchronized (Items)
        {
            Boolean Again = true;
            while(Again)
            {
                Again = false;
                for (MultiCmdItem mci : Items.values())
                    if (mci.Users.contains(User))
                    {
                        mci.Users.remove(User);
                        if (mci.Users.size()==0)
                        {
                            Items.remove(mci.getKey());
                            Again = true;
                            break;
                        }
                    }
            }
        }

        setMade(false);
    }



    public synchronized void Make()
    {
        if (!isMade())
        {
            SortedKeys = new TreeSet<String>(Items.keySet());

            ShopQuest = new ArrayList<MultiCmdItem>();
            MultiCmdItem first = null;
            MultiCmdItem mci = null;

            for (String key : SortedKeys)
            {
                mci = new MultiCmdItem(Items.get(key));
                if (first == null)
                    first = mci;
                else
                {
                    if (first.getPrefix().equals(mci.getPrefix()) && 
                            ((mci.getIndex() == first.getIndex() + first.getMulti() + 1)) && 
                            first.getMulti() < 7)
                    {
                        first.setMulti(first.getMulti() + 1);
                    }
                    else
                    {
                        ShopQuest.add(first);
                        first = mci;
                    }
                }


            }

            if (first != null)
                ShopQuest.add(first);



            //Computa comando di lettura (Slot 1)
            Cmd = new byte[]{0, 0, (byte) 0xF0, 1, 0x34 , (byte)155};
            for (MultiCmdItem qmci : ShopQuest)
            {
                Cmd = Protocol.ByteArrayCat(Cmd, qmci.GetRequestTern());
            }

            first = null;
            mci = null;
            setMade(true);
        }
        else
        {
            Cmd = new byte[]{0, 0, (byte) 0xF1, 1};
        }
    }




    public void Start()
    {
        Poll.running = true;
        Poll.start();
    }




    void Stop()
    {
        // tell the thread to shut down and wait for it to finish
        // this is a clean shutdown
        boolean retry = true;
        while (retry) 
        {
            try 
            {
                Poll.join();
                retry = false;
            } catch (InterruptedException e) 
            {
                e.printStackTrace();
                // try again shutting down the thread
            }
        }

    }



    public void WritePush (int cn, int Typ, int Idx, int Par, Object value)
    {
        MultiCmdItem mci = new MultiCmdItem(cn, Typ, Idx, Par, this);
        mci.setValue(value);
        WriteCue.add(mci);
    }
    
    public void WritePush (MultiCmdItem mciToWrite)
    {
        MultiCmdItem mci = new MultiCmdItem(mciToWrite.getCN(), mciToWrite.getType(), mciToWrite.getIndex(), mciToWrite.getParam(), this);
        mci.setValue(mciToWrite.getValue());
        WriteCue.add(mci);
    }

    public MultiCmdItem WritePull()
    {
        if (!WriteCue.isEmpty())
        {
            MultiCmdItem mci = WriteCue.get(0);
            WriteCue.remove(0);

            //return new MultiCmdItem(mci.Type, mci.Index, mci.Param, mci.Value);
            return mci;
        }
        else
            return null;
    }

    public void WriteQueued()
    {
        if (WatchDogIndex != 0)
            if (WDogLatch != null)
            {
                int Elapsed = (int) System.currentTimeMillis() - WDogLatch; 
                if (Elapsed >= WatchDogPeriod)
                {
                    WritePush(1, 1, WatchDogIndex, 0, 1d );
                    WDogLatch = (int) System.currentTimeMillis();
                }
            }
            else
                WDogLatch = (int) System.currentTimeMillis();



        if(!WriteCue.isEmpty())
        {
            byte[] b = new byte[]{0, 0, 0x34 , (byte)27};

            while (true)
            {
                MultiCmdItem mci = WritePull();
                if (mci == null)
                    break;

                b = Protocol.ByteArrayCat(b, mci.GetWriteBlock());
            }

            b = Play(b);
        }
    }

    public void ClearCued()
    {
    }


    public synchronized void Read()
    {

        Make();



        Ans = new byte[]{};

        byte[] buf = Play(Cmd);

        if (getReturnCode() != rcOK)
        {
            Close();
            return;
        }
        
        while(true)
        {
            if (buf.length == 0)
                Close();

            if (getReturnCode() != rcOK)
            {
                Close();
                break;
            }


            if (buf.length > 4)
                Ans = Protocol.ByteArrayCat(Ans, buf, 3, buf.length - 4);

            if ((buf.length < 3) || (buf.length > 3) && ((buf[2] & 0x20)!=0 ))
            {
                break;
            }

            buf = new byte[]{(byte)0xF2};

            Play(buf);
        }



        Parse();
        
        if (WatchAlarms)
            Alarms.Check();
    }

    public synchronized void Parse()
    {
        int ptr = 0;

        MultiCmdItem mci;

        for (String key : SortedKeys)
        {
            if (ptr >= Ans.length)
                break;

            mci = Items.get(key);

            try {
                ptr += mci.ParseValue(Ans, ptr);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                break;
            }
        }
    }
}
