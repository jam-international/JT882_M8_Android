package com.jam_int.jt882_m8;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import communication.MultiCmdItem;
import communication.ShoppingList;


public class Rinforzo extends Activity {
    ShoppingList sl;
    Thread thread_Rinforzo;
    boolean Thread_Running = false, StopThread = false, first_cycle = true;
    Handler UpdateHandler = new Handler();
    MultiCmdItem MultiCmd_tasto_verde,MultiCmd_CH1_in_emergenza,MultiCmd_Vb116RinforzoGiu_HMI,MultiCmd_Vb115CricchettoRinforzo,MultiCmd_Vn3804_pagina_touch;
    MultiCmdItem[] mci_array_read_all;
    Button Button_rinforzo_giu,Button_rinforzo_un_ciclo;
    Mci_write Mci_write_b116RinforzoGiu_HMI = new Mci_write(),Mci_write_Vb115CricchettoRinforzo = new Mci_write();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rinforzo);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = -20;
        params.height = 100;
        params.width = 550;
        params.y = -10;

        this.getWindow().setAttributes(params);

        Button_rinforzo_giu = findViewById(R.id.btn_rinforzo_giu);
        Button_rinforzo_un_ciclo = findViewById(R.id.btn_rinforzo_un_ciclo);



        sl = SocketHandler.getSocket();
        sl.Clear();

        MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
        MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 21, MultiCmdItem.dpNONE);
        MultiCmd_Vb116RinforzoGiu_HMI = sl.Add("Io", 1, MultiCmdItem.dtVB, 116, MultiCmdItem.dpNONE);
        MultiCmd_Vb115CricchettoRinforzo = sl.Add("Io", 1, MultiCmdItem.dtVB, 115, MultiCmdItem.dpNONE);
        MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);

        Mci_write_b116RinforzoGiu_HMI.mci = MultiCmd_Vb116RinforzoGiu_HMI;
        Mci_write_Vb115CricchettoRinforzo.mci = MultiCmd_Vb115CricchettoRinforzo;

        mci_array_read_all = new MultiCmdItem[]{    MultiCmd_tasto_verde, MultiCmd_CH1_in_emergenza,MultiCmd_Vb116RinforzoGiu_HMI};

        Toggle_Button.CreaToggleButton(Mci_write_b116RinforzoGiu_HMI, Button_rinforzo_giu, "rinforzo_tipo2_giu_press", "rinforzo_tipo2_giu", getApplicationContext(),sl);
        //EdgeButton.CreaEdgeButton(Mci_write_b116RinforzoGiu_HMI, Button_rinforzo_giu, "rinforzo_tipo2_giu_press", "rinforzo_tipo2_giu", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_write_Vb115CricchettoRinforzo, Button_rinforzo_un_ciclo, "rinforzo_tipo2_1ciclo_press", "rinforzo_tipo2_1ciclo", getApplicationContext());


        if (!Thread_Running) {
            StopThread = false;
            Rinforzo_Thread myTask = new Rinforzo_Thread(Rinforzo.this);
            thread_Rinforzo = new Thread(myTask, "Rinforzo myTask");
            thread_Rinforzo.setName("Rinforzo");
            thread_Rinforzo.start();
            Log.d("JAM TAG", "Modifica programma Thread from OnCreate");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        first_cycle = true;
        if (!Thread_Running) {
            StopThread = false;

            Rinforzo_Thread myTask = new Rinforzo_Thread(Rinforzo.this);
            thread_Rinforzo = new Thread(myTask, "Rinforzo myTask");
            thread_Rinforzo.setName("Rinforzo");
            thread_Rinforzo.start();
            Log.d("JAM TAG", "Start Rinforzo Thread from onResume");
        }
    }

    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
        if (Thread_Running) {
            try {
                Log.d("JAM TAG", "End Rinforzo Thread from on Pause");
                KillThread();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override                   //your activity is no longer visible to the user
    public void onStop() {
        super.onStop();
    }

    @Override                   //your activity is no longer visible to the user
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            // This work only for android 4.4+
            int currentApiVersion;
            currentApiVersion = Build.VERSION.SDK_INT;
            if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {

                getWindow().getDecorView().setSystemUiVisibility(flags);

                // JamPointCode below is to handle presses of Volume up or Volume down.
                // Without this, after pressing volume buttons, the navigation bar will
                // show up and won't hide
                final View decorView = getWindow().getDecorView();
                decorView
                        .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                            @Override
                            public void onSystemUiVisibilityChange(int visibility) {
                                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                    decorView.setSystemUiVisibility(flags);
                                }
                            }
                        });
            }
        }
    }

    public void on_click_button_exit(View view) {
        KillThread();
        finish();
    }

    private void KillThread() {

        StopThread = true;
        try {


        } catch (IllegalArgumentException e) {

            e.printStackTrace();
        }

        try {

            thread_Rinforzo.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("JAM TAG", "Stop Rinforzo Thread");

    }
    /**
     * Function for handle the emergency button event
     */
    private void Emergenza() {
        if ((Double) MultiCmd_tasto_verde.getValue() == 0.0d || (Double) MultiCmd_CH1_in_emergenza.getValue() == 1.0d) {
            KillThread();
            Utility.ClearActivitiesTopToEmergencyPage(getApplicationContext());
        }
    }
    class Rinforzo_Thread implements Runnable {
        Activity activity;


        public Rinforzo_Thread(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            while (true) {
                Thread_Running = true;
                Boolean rc_error;
                try {
                    Thread.sleep((long) 10d);
                    if (StopThread) {

                        Thread_Running = false;


                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Rinforzo Thread catch", Toast.LENGTH_SHORT).show();
                }

                if (sl.IsConnected()) {
                    if (first_cycle) {

                        first_cycle = false;


                    }

                    MultiCmd_Vn3804_pagina_touch.setValue(1009.0d);
                    sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                    sl.WriteQueued();
                    sl.ReadItems(mci_array_read_all);
                    rc_error = sl.getReturnCode() != 0;

                    if (rc_error == false) { //se ho avuto un errore di ricezione salto


                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb115CricchettoRinforzo);
                        Utility.GestiscoMci_Out_Toggle(sl,Mci_write_b116RinforzoGiu_HMI);

                    }
                    AggiornaGuiDaThread();
                } else {
                    sl.Connect();
                }
            }
        }
        /**
         * THIS IS THE ONLY FUNCTION THAT CAN BE INSIDE THREAD
         * <p>
         * THERE IS NO REASON FOR PUT FUNCTIONS HERE, I ONLY HAVE MORE ERRORS
         */
        private void AggiornaGuiDaThread() {

            UpdateHandler.post(new Runnable() {
                @Override
                public void run() {
                    Emergenza();
                    Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_b116RinforzoGiu_HMI, Button_rinforzo_giu, "rinforzo_tipo2_giu_press", "rinforzo_tipo2_giu");


                }
            });
        }
    }

}
