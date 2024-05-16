package communication;

import java.util.LinkedHashMap;


public class CommHub
{
    private final LinkedHashMap<String, ShoppingList> Items;

    private Integer PollerIndex = 1;


    public CommHub()
    {
        super();
        Items = new LinkedHashMap<String, ShoppingList>();
    }

    public synchronized Integer getPollerIndex()
    {
        return PollerIndex;
    }

    public synchronized void setPollerIndex(Integer pollerIndex)
    {
        PollerIndex = pollerIndex;
    }

    public synchronized LinkedHashMap<String, ShoppingList> getItems()
    {
        return Items;
    }

    public MultiCmdItem GetMCI (String mciKey)
    {
        Integer cn = Integer.parseInt(mciKey.split("T")[0].substring(2));

        return Items.get(cn.toString()).getItems().get(mciKey);
    }

    public void put(String key, ShoppingList p)
    {
        Items.put(key, p);
    }

    public ShoppingList get(Integer index)
    {
        return get(index.toString());
    }

    public ShoppingList get(String key)
    {
        if (Items.containsKey(key))
            return Items.get(key);
        else
            return null;
    }

    public void PollAll()
    {
        for (ShoppingList port: Items.values())
        {
            if (port.isEnabled()) 
                if (port.IsConnected())
                {
                    port.setMainTimeLatch(System.currentTimeMillis());
        
                    port.WriteQueued();
                    port.getUploadCue().Play();
        
                    port.Read();
        
                    port.setCommOffLatch(System.currentTimeMillis());
                    port.setCommOffLapse(0l);
                }
        }
    }

    public boolean PollNext()
    {
        boolean toBuild = false;
        boolean Done = false;

        do
        {
            PollerIndex++;
            if (PollerIndex > Items.size()) 
                PollerIndex = 1;
            if (PollerIndex < 1 ) 
                PollerIndex = 1;

            ShoppingList port = get(PollerIndex);

            if (port.isEnabled()) 
            {            
                if (!port.IsConnected())
                {
                    if ((port.getCommOffLapse() < 0) || (port.getCommOffLapse() > (port.getReConnectDelay())))
                    {
                        port.ClearCued();
                        port.Connect();
                        port.setCommOffLatch(System.currentTimeMillis());
                        toBuild = true;
                        Done = true;

                    }
                    port.setCommOffLapse(System.currentTimeMillis() - port.getCommOffLatch());
                }
                else
                {
                    if (port.IsConnected())
                    {
                        long elapsed = System.currentTimeMillis() - port.getMainTimeLatch();
                        if( (elapsed > port.getPollTime()) || (elapsed < 0))
                        {
                            port.setMainTimeLatch(System.currentTimeMillis());

                            port.WriteQueued();
                            port.getUploadCue().Play();

                            port.Read();

                            Done = true;

                            port.setCommOffLatch(System.currentTimeMillis());
                            port.setCommOffLapse(0l);
                        }
                    }

                }
                break;
            }
            else
            {
                Done = true;
                break;
            }

        }while(true);

//        if (toBuild)
//            BuildData("XXX");

        return Done;
    }

    public void UnBuildData(String User)
    {
        for (ShoppingList port: getItems().values())
            port.Clear(User);
    }

//    public void BuildData()
//    {
//        BuildData(Page);
//    }
//

    public void BuildData(String ID)
    {
          UnBuildData(ID);
          
          get(1).Add(ID, 1, MultiCmdItem.dtAL, 9, MultiCmdItem.dpAL_M32);
          
//
//        Page = p;
//
//
//        BuildHideMaskData(p);
//
//        BuildPageIdData(p);
//
//        BuildCNScriptRunnerData();
//
//        for (DroidBaseControl bc : p.getControls().Items.values())
//        {
//            DroidValueControl vc = new DroidValueControl(null);
//            DroidAlarmControl ac = new DroidAlarmControl(null);
//
//
//
//            if (bc.getClass().getName().endsWith("DroidAlarmControl"))
//            {
//                ac = (DroidAlarmControl) bc;
//                switch (ac.getAlarmMode())
//                {
//                case DroidAlarmControl.AlarmMode_Flag:
//                case DroidAlarmControl.AlarmMode_Line:
//                case DroidAlarmControl.AlarmMode_List:
//
//                    ac.setMCIKey(get(ac.getCN()).Add("PageData", ac.getCN(), MultiCmdItem.dtAL, 2, MultiCmdItem.dpAL_PLC).getKey());
//                    break;
//
//
//                case DroidAlarmControl.AlarmMode_M32_Full:
//                    ac.setMCIKey(get(ac.getCN()).Add("PageData", ac.getCN(), MultiCmdItem.dtAL, 9, MultiCmdItem.dpAL_M32).getKey());
//                    break;
//                }
//            }
//            else
//            {
//                if (vc.getClass().isAssignableFrom(bc.getClass()))
//                {
//                    vc = (DroidValueControl) bc;
//
//                    if (vc.getScriptVar() != null)
//                    {
//
//                    }
//                    else if (vc.getSysVarKey() != null)
//                    {
//
//                    }
//                    else if (vc.getScriptReadFun() != null)
//                    {
//
//                    }
//                    else
//                    {
//                        if (vc.getCN() != 0)
//                            vc.setMCIKey(get(vc.getCN()).Add("PageData", vc.getCN(), vc.getVarType(), vc.getVarIndex(), vc.getExtraPar()).getKey());
//                    }
//                }
//            }
//        }
//
//        for (ShoppingList port: getItems().values())
//            port.setMade(false);
    }

    public Boolean isConnected()
    {
        Boolean connected = true;
        for (ShoppingList port: getItems().values())
        {
            if (! port.IsConnected())
                connected = false;
        }

        return connected;
    }





    
    public void BuildCNScriptRunnerData()
    {
        for (ShoppingList port: getItems().values())
        {
          port.setCNScriptRunnerMCI(port.Add("PageData", 1, MultiCmdItem.dtGP, 3085, MultiCmdItem.dtNONE));
        }
    }

    public void WritePageId(Object value)
    {
        for (ShoppingList ptl: Items.values())
        {
            MultiCmdItem mci =  ptl.getPageIdMCI();

            if (mci != null)
            {
                ptl.WritePush(mci.getCN(), mci.getType(), mci.getIndex(), mci.getParam(), value);
                mci.setValue(value);
            }
        }

        this.PollAll();
    }

    public Integer TestPageId(Integer CurId)
    {

        for (ShoppingList ptl: Items.values())
        {
            if (ptl.IsConnected())
            {
                MultiCmdItem mci =  ptl.getPageIdMCI();

                if ((mci != null) && (CurId.doubleValue() != (Double)mci.getValue()))
                {
                    return ((Double)mci.getValue()).intValue();
                }
            }
        }

        this.PollNext();

        return CurId;
    }
}
