package communication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class SmartAlarms
{
    private Map<String, SmartAlarm> SEList;
    private Map<String, SmartAlarm> PLCList;
    private Integer SECount = 0;
    private ShoppingList SL = null;
    private final String SLUserKey  = "";
    private MultiCmdItem ECntMCI = null;
    private MultiCmdItem ETotMCI = null;    
    private MultiCmdItem PlcMCI = null;

    private boolean SetM32AlarmsDone = false;

    public synchronized Map<String, SmartAlarm> getSEList()
    {
        return SEList;
    }

    public synchronized void setSEList(Map<String, SmartAlarm> sEList)
    {
        SEList = sEList;
    }

    public synchronized Map<String, SmartAlarm> getPLCList()
    {
        return PLCList;
    }

    public synchronized void setPLCList(Map<String, SmartAlarm> pLCList)
    {
        PLCList = pLCList;
    }

    private final List<OnAlarmListener> Listeners = new ArrayList<OnAlarmListener>();

    //    public synchronized Map<String, SmartAlarm> getSEList()
    //    {
    //        return SEList;
    //    }
    //
    //    public synchronized void setSEList(Map<String, SmartAlarm> sEList)
    //    {
    //        SEList = sEList;
    //    }

    public interface OnAlarmListener {
        void onAlarm(SmartAlarm al);
    }

    public void registerAlarmListener(OnAlarmListener listener) {
        Listeners.add(listener);
    }   

    public void unregisterAlarmListener(OnAlarmListener listener) {
        Listeners.remove(listener);
    }


    public SmartAlarms(ShoppingList sl)
    {
        super();

        SL = sl;
        SEList = Collections.synchronizedMap(new LinkedHashMap<String, SmartAlarm>());
        PLCList = Collections.synchronizedMap(new LinkedHashMap<String, SmartAlarm>());
    }

    public void Init()
    {
        ECntMCI = SL.Add("AlarmSurveyCN", 1, MultiCmdItem.dtAL, 7, MultiCmdItem.dpAL_M32);
        ETotMCI = SL.Add("AlarmSurveyCN", 1, MultiCmdItem.dtAL, 8, MultiCmdItem.dpAL_M32);
        PlcMCI = SL.Add("AlarmSurveyCN", 1, MultiCmdItem.dtAL, 2, MultiCmdItem.dpAL_PLC);
    }

    public void Check()
    {
        try
        {
            if(!SetM32AlarmsDone)
                return;

            //Controllo se ci sono variazioni tramite il contatore Emergenze
            Integer cnt = (Integer)ECntMCI.getValue();

            //MyLog.D("M32Alarms", "EmeCnt: " + cnt.toString());

            if (!SECount.equals(cnt))
            {
                SECount = cnt;

                //Se ci sono emergenze attive le leggo
                int[] emebuf = {};

                //emebuf = SL.ReadCNData(1, MultiCmdItem.dtAL, 9, MultiCmdItem.dpAL_M32);
                MultiCmdItem mci = new MultiCmdItem(1, MultiCmdItem.dtAL, 9, MultiCmdItem.dpAL_M32, SL);
                SL.ReadItem(mci);
                emebuf = (int[])mci.getValue();

                Map<String, SmartAlarm> eme = new LinkedHashMap<String, SmartAlarm>();

                Integer idx2 = 0;
                for (Integer idx = 1; idx < emebuf.length; idx +=4)
                {
                    idx2++;
                    Integer[] p = {emebuf[idx], emebuf[idx + 1], emebuf[idx + 2], emebuf[idx + 3]};

                    SmartAlarm al = new SmartAlarm(1, SmartAlarm.satEmergM, p, true, "", "", null);

                    al.setIndex(idx2);

                    eme.put(al.getFingerPrint(), al);

                   // MyLog.D("M32Alarms", al.getFingerPrint());
                }

                for (SmartAlarm al: eme.values())
                {
                    if (!SEList.containsKey(al.getFingerPrint()))
                    {
                        //Scattata Emergenza
                        SEList.put(al.getFingerPrint(), al);

                        al.setActive(true);

                        MultiCmdItem descmci = new MultiCmdItem(1, MultiCmdItem.dtAL, al.getIndex(), MultiCmdItem.dpAL_M32_Description, SL);
                        SL.ReadItem(descmci);
                        String d = (String)descmci.getValue();
                        if (d.equals(""))
                            d = al.getParameters()[0].toString() + ": " + al.getParameters()[1].toString()+", "+ al.getParameters()[2].toString();

                        al.setDescription(d);
                        RaiseAlarmEvent(al);
                    }
                }

                if (false)
                {
                    Boolean Finished = false;
                    do
                    {
                        Finished = true;
                        for (SmartAlarm al: SEList.values())
                        {
                            if (!eme.containsKey(al.getFingerPrint()))
                            {
                                SmartAlarm aa = SEList.remove(al.getFingerPrint());

                                aa.setActive(false);

                                //Rientrata Emergenza
                                RaiseAlarmEvent(aa);
                                Finished = false;
                                break;
                            }
                        }
                    } while (!Finished);
                }
                else
                {
                    Iterator<Entry<String, SmartAlarm>> it = SEList.entrySet().iterator();
                    while(it.hasNext()){
                        Entry<String, SmartAlarm> pair = it.next();

                        SmartAlarm al = pair.getValue();
                        if (!eme.containsKey(al.getFingerPrint()))
                        {
                            it.remove();
                            al.setActive(false);
                            RaiseAlarmEvent(al);
                        }
                    }
                }
            }

            //Gestione allarmi PLC
            //Crea la collezione provvisoria degli allarmi attivi
            int[] Als = (int[])PlcMCI.getValue();
            Map<String, SmartAlarm> eme = new HashMap<String, SmartAlarm>();
            if (Als != null)
            {
                for (int a: Als)
                {
                    SmartAlarm al = new SmartAlarm(1, SmartAlarm.satPLCMessage, new Integer[]{a}, true, "", "", null);
                    eme.put(al.getFingerPrint(), al);
                }
            }

            //Verifica allarmi rientrati rispetto alla collezione Memorizzata
            if (false)
            {
                boolean Again = true;

                while (Again)
                {
                    Again = false;
                    for (SmartAlarm a: PLCList.values())
                    {
                        if (!eme.containsKey(a.getFingerPrint()))
                        {
                            a.setActive(false);
                            FillPlcMessageTextAndKey(a);
                            PLCList.remove(a.getFingerPrint());
                            RaiseAlarmEvent(a);
                            Again = true;
                        }
                    }
                }
            }
            else
            {
                Iterator<Entry<String, SmartAlarm>> it = PLCList.entrySet().iterator();
                while(it.hasNext()){
                    Entry<String, SmartAlarm> pair = it.next();

                    SmartAlarm al = pair.getValue();
                    if (!eme.containsKey(al.getFingerPrint()))
                    {
                        it.remove();
                        al.setActive(false);
                        RaiseAlarmEvent(al);
                    }
                }
            }

            //Verifica allarmi Scattati
            for (SmartAlarm a: eme.values())
            {
                if (!PLCList.containsKey(a.getFingerPrint()))
                {
                    a.setActive(true);
                    FillPlcMessageTextAndKey(a);
                    PLCList.put(a.getFingerPrint(), a);

                    RaiseAlarmEvent(a);
                }
            }
        } catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
           // MyLog.D("M32Alarms", "Error on Check !!! \r\n" +  DroidTools.getStackTrace(e));
        }
    }

    public void RaiseAlarmEvent(SmartAlarm al)
    {
        for (OnAlarmListener l: Listeners)
        {
            l.onAlarm(al);
        }
    }

    public void FillPlcMessageTextAndKey(SmartAlarm a)
    {
        Integer num = a.getParameters()[0];
        String d = "PLC Alarm " + num.toString();
        String k = "";
        /*
        Integer num = a.getParameters()[0];
        String d = "PLC Alarm " + num.toString();
        String k = "";
        DroidProject prj = SL.getProject();
        if (prj != null)
        {
            DroidLanguage lng = prj.getTextDB();
            DroidSwitch swt = prj.getSwitchBag().Item(prj.getOptions().getAlarmSwitchKey());

            if (swt != null)
            {
                DroidSwitchItem itm = swt.getItemByValue(num.toString());
                if (itm != null)
                {
                    d = itm.getDefaultText();

                    if ((itm.getTextKey() != null) && (itm.getTextKey() != ""))
                    {
                        d = lng.Item(itm.getTextKey());
                        k = itm.getTextKey();
                    }
                }
            }
        }
        */
        a.setDescription(d);
        a.setUserMessageKey(k);
    }



    public void SetM32Alarms()
    {
        try
        {
            if (SL.IsConnected())
            {
                MultiCmdItem mci = new MultiCmdItem(1, MultiCmdItem.dtGP, 3081, MultiCmdItem.dpNONE, SL);
                String cul = "it-IT";
                mci.setValue(cul);
                SL.WriteItem(mci);

                Thread.sleep(200);

                while(true)
                {
                    mci = new MultiCmdItem(1, MultiCmdItem.dtGP, 1011, MultiCmdItem.dpNONE, SL);
                    mci.setValue(0d);      
                    SL.ReadItem(mci);
                    if ((Double)mci.getValue() <= 1)
                        break;
                }

                SetM32AlarmsDone = true;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

/*
        try
        {
            DroidProject prj = SL.getProject();
            if (SL.IsConnected())
            {
                MyLog.D("M32Alarms", "Init...");
                File f = new File(prj.getPrjPath(), "M32ALARMS/" + prj.getTextDB().getCultureName() + ".txt");

                SL.FolderCreate("C:\\cnc\\$sysgraph");
                SL.FolderCreate("C:\\cnc\\$sysgraph\\messages");
                SL.FolderCreate("C:\\cnc\\$sysgraph\\messages\\" + prj.getTextDB().getCultureName());
                String PN = "C:\\cnc\\$sysgraph\\messages\\" + prj.getTextDB().getCultureName() + "\\M32Alarms.txt";

                if(SL.FileDir(PN, (byte)0x20) == null)
                    SL.FileUpload(PN , f.getAbsolutePath(), null);

                f = new File(prj.getPrjPath(), "M32ALARMS/confgraph.txt");
                if (SL.FileDir("C:\\cnc\\$sysgraph\\confgraph.txt", (byte)0x20) == null)
                    SL.FileUpload("C:\\cnc\\$sysgraph\\confgraph.txt" , f.getAbsolutePath(), null);

                Thread.sleep(200);

                MultiCmdItem mci = new MultiCmdItem(1, MultiCmdItem.dtGP, 3081, MultiCmdItem.dpNONE, SL);
                String cul = prj.getTextDB().getCultureName();
                mci.setValue(cul);
                SL.WriteItem(mci);



                Thread.sleep(200);

                while(true)
                {
                    MyLog.D("M32Alarms", "Waiting for Init Completion...");

                    mci = new MultiCmdItem(1, MultiCmdItem.dtGP, 1011, MultiCmdItem.dpNONE, SL);
                    mci.setValue(0d);
                    SL.ReadItem(mci);
                    if ((Double)mci.getValue() <= 1)
                        break;

                }


                //Aggiorna le descrizioni
                int idx = 0;
                for (SmartAlarm al: SEList.values())
                {
                    idx++;

                    MultiCmdItem descmci = new MultiCmdItem(1, MultiCmdItem.dtAL, idx, MultiCmdItem.dpAL_M32_Description, SL);
                    SL.ReadItem(descmci);
                    String d = (String)descmci.getValue();
                    if (d.equals(""))
                        d = al.getParameters()[0].toString() + ": " + al.getParameters()[1].toString()+", "+ al.getParameters()[2].toString();

                    al.setDescription(d);

                }

                SetM32AlarmsDone = true;

                MyLog.D("M32Alarms", "Init OK");
            }
        } catch (Exception e)
        {
            MyLog.D("M32Alarms", e.getStackTrace().toString());
        }
        */
    }
}
