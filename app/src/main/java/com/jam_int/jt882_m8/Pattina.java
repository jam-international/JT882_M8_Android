package com.jam_int.jt882_m8;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jamint.ricette.Element;
import com.jamint.ricette.Ricetta;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import communication.MultiCmdItem;
import communication.ShoppingList;
import communication.SmartAlarm;

public class Pattina extends Activity {
    ShoppingList sl;
    /**
     * Thread
     */
    Thread thread_pattina;
    Handler UpdateHandler = new Handler();
    Boolean Thread_Running = false, StopThread = false, first_cycle = true;
    MultiCmdItem MultiCmd_tasto_verde,MultiCmd_CH1_in_emergenza,MultiCmd_Vn3804_pagina_touch;
    MultiCmdItem[] mci_array_read_all;
    FrameLayout frame_canvas;
    Dynamic_view myView;
    Ricetta ricetta;
    ArrayList<Element> List_entita = new ArrayList<>();
    CoordPosPinza Coord_Pinza = new CoordPosPinza();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattina);

        frame_canvas = findViewById(R.id.frameLayout_pattina);


        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/ricette");
        dir.mkdirs();
        File file = new File(Values.File_XML_path_T2_R);
        int i = file.getName().lastIndexOf('.');
        String name = file.getName().substring(0, i);
        File file1 = new File(file.getParent() + "/" + name + ".xml");


        ricetta = new Ricetta(Values.plcType);
        try {
            ricetta.open(file1);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "error opening xml file ", Toast.LENGTH_SHORT).show();
        }
        List_entita = (ArrayList<Element>) ricetta.elements;

        myView = new Dynamic_view(this, 500, 500, List_entita, 1.45F, Coord_Pinza, false, -510, 35, null, 500, 500);

        frame_canvas.addView(myView);
        myView.Ricalcola_entità_canvas(List_entita);
        myView.Center_Bitmap_Main(1.25F, 380, 25);

        sl = SocketHandler.getSocket();
        sl.Clear("Io");

        MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 21, MultiCmdItem.dpNONE);
        MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
        MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);

        mci_array_read_all = new MultiCmdItem[]{ MultiCmd_tasto_verde, MultiCmd_CH1_in_emergenza,};

        if (!Thread_Running) {
            StopThread = false;
            MyAndroidThread_Pattina myTask = new MyAndroidThread_Pattina(Pattina.this);
            thread_pattina = new Thread(myTask, "Pattina myTask");
            thread_pattina.setName("Thread_Pattina");
            thread_pattina.start();
            Log.d("JAM TAG", "Pattina Thread from OnCreate");
        }
    }
    /**
     * Button for exit from this activity
     *
     * @param view
     */
    public void onclick_button_Exit(View view) {
        StopThread = true;
        KillThread();

        finish();
    }
    private void KillThread() {


        StopThread = true;

        try {
            //  Thread.sleep((long) 200d);
            if (!Thread_Running)
                thread_pattina.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("JAM TAG", "Stop Pattina Thread");
    }

    /**
     * TODO
     */
    private void Emergenza() {
        if ((Double) MultiCmd_tasto_verde.getValue() == 0.0d || (Double) MultiCmd_CH1_in_emergenza.getValue() == 1.0d) {
            KillThread();
            Utility.ClearActivitiesTopToEmergencyPage(getApplicationContext());
        }
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
    class MyAndroidThread_Pattina implements Runnable {
        Activity activity;


        public MyAndroidThread_Pattina(Activity activity) {
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
                        MultiCmd_Vn3804_pagina_touch.setValue(0.0d);
                        sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                        Thread_Running = false;
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                }

                if (sl.IsConnected()) {

                    if (first_cycle) {
                        first_cycle = false;
                        MultiCmd_Vn3804_pagina_touch.setValue(1010.0d);
                        sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                    }
                    rc_error = false;
                    sl.Clear();
                    try{

                        sl.ReadItems(mci_array_read_all);
                        rc_error = sl.getReturnCode() != 0;

                        if (sl.getReturnCode() != 0) {
                            //se non riceve bene i valori provo a chiudere e riaprire il Socket
                            sl.Close();
                            Thread.sleep((long) 300d);
                            sl.Connect();
                            Thread.sleep((long) 300d);
                            //
                            rc_error = true;
                        }
                    }catch (Exception err){
                        rc_error = true;
                    }

                    if (!rc_error) {


                    }



                    AggiornaGuiDaThread();

                }else {
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
                    try{
                        Emergenza();




                    } catch (Exception e) {

                    }
                }
            });
        }
    }
}
