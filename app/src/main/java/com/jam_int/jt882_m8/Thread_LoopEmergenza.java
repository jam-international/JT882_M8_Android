package com.jam_int.jt882_m8;

import android.app.Activity;
import android.util.Log;

import communication.MultiCmdItem;
import communication.ShoppingList;

public class Thread_LoopEmergenza {
    ShoppingList sl;
    boolean Thread_Running = false, StopThread = false, Macchina_armata = false,first_cycle = true;
    Thread thread_LoopEmergenza;
    MultiCmdItem MultiCmd_tasto_verde, MultiCmd_CH1_in_emergenza;
    MultiCmdItem[] mci_array_read_all;
    String activity_name;

    public void thread_LoopEmergenza_Start(Activity activity) {
        activity_name = activity.getLocalClassName();
        // Setup ShoppingList
        sl = SocketHandler.getSocket();
        sl.Clear("Io");
        if (!sl.IsConnected()) {
            sl.Connect();
        }
        MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 21, MultiCmdItem.dpNONE);
        MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);

        mci_array_read_all = new MultiCmdItem[]{ MultiCmd_CH1_in_emergenza,  MultiCmd_tasto_verde};

        // Start the thread
        if (!Thread_Running) {
            LoopEmergenza_Main myTask_LoopEmergenza = new LoopEmergenza_Main(activity);
            thread_LoopEmergenza = new Thread(myTask_LoopEmergenza, "LoopEmergenza myTask");
            thread_LoopEmergenza.start();

            Log.d("JAM TAG", "Start "+activity_name+ " LoopEmergenza Thread from OnCreate");
        }

        
    }


    /**
     * Function for handle the emergency button event
     *
     * @param activity
     */
    private void Emergenza(Activity activity) {
        if (((Double) MultiCmd_tasto_verde.getValue() == 0.0d || (Double) MultiCmd_CH1_in_emergenza.getValue() == 1.0d) && Macchina_armata) {
            KillThread();
            Utility.ClearActivitiesTopToEmergencyPage(activity);
        }
    }

    /**
     *
     */
    public void KillThread() {
        StopThread = true;
        try {
            if (!Thread_Running)
                thread_LoopEmergenza.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Log.d("JAM TAG", "End "+activity_name + " LoopEmergenza Thread");
    }
    // Getter
    public boolean getThreadStatus() {
        return Thread_Running;
    }


    /***
     * Thread per ascoltare l'emergenza dal M8
     *
     */
    class LoopEmergenza_Main implements Runnable {
        Activity activity;
        boolean rc_error;

        public LoopEmergenza_Main(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            while (true) {
                Thread_Running = true;
                try {
                    Thread.sleep((long) 100d);
                    if (StopThread) {
                        Thread_Running = false;

                        return;
                    }

                    sl.Clear();

                    // ------------------------ RX -------------------------------
                    sl.ReadItems(mci_array_read_all);
                    if (sl.getReturnCode() != 0) {
                        rc_error = true;
                    }

                    if (first_cycle) {
                        first_cycle = false;

                        if ((Double) MultiCmd_CH1_in_emergenza.getValue() == 0.0d) Macchina_armata = true;  //se sono entrato è non sono in emergenza inizio a controllarla
                    }



                    } catch (InterruptedException e) {
                    e.printStackTrace();
                   // Toast.makeText(getApplicationContext(), "StartSelectFileThread catch", Toast.LENGTH_SHORT).show();
                }

                if (sl.IsConnected()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!rc_error)  Emergenza(activity);
                        }
                    });
                } else
                    sl.Connect();
            }
        }

    }
}
