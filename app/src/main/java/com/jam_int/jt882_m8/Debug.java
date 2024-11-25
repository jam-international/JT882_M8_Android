package com.jam_int.jt882_m8;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import communication.MultiCmdItem;
import communication.ShoppingList;

public class Debug extends Activity {

    /**
     * Thread
     */
    boolean Thread_Running = false, StopThread = false, cmd_leggi_variabili_da_cn = false, cmd_show_variabili = false;
    Thread thread_Debug;
    MultiCmdItem[] mci_array_read_all_debug;

    /**
     * Shopping list for communicate with PLC
     */
    ShoppingList sl;

    /**
     * The list of mci to display
     */
    ArrayList<MultiCmdItem> Lista_Mci = new ArrayList<MultiCmdItem>();
    MultiCmdItem[] mci_array_read_all;

    MultiCmdItem MultiCmd_Vb76EnableCucitureInfinite_C1,MultiCmd_Debug14_Main1, MultiCmd_Debug15_Main1,MultiCmd_Debug8_Main1,MultiCmd_Debug14_Main2, MultiCmd_Debug15_Main2,MultiCmd_Debug8_Main2,
            MultiCmd_Debug14_Main3, MultiCmd_Debug15_Main3,
                    MultiCmd_Debug8_Main3, MultiCmd_Debug14_Fork1, MultiCmd_Debug15_Fork1, MultiCmd_Debug8_Fork1, MultiCmd_Debug14_Fork2, MultiCmd_Debug15_Fork2, MultiCmd_Debug8_Fork2, MultiCmd_VnStatoCuci1_vn11,
            MultiCmd_VnStatoCuci2_vn12,MultiCmd_Vn132_DebugPie,MultiCmd_Vq3564_debug_sposta_cucitura,MultiCmd_Vb300,MultiCmd_Vb301,MultiCmd_Vb302,MultiCmd_Vb303,MultiCmd_Vb304,
            MultiCmd_Vb305,MultiCmd_Vb306,MultiCmd_Vb307,MultiCmd_Vb308,MultiCmd_Vb309,MultiCmd_Vb310,MultiCmd_Vb4513,MultiCmd_Vn120,MultiCmd_Vb4006,MultiCmd_Vb4509,MultiCmd_Vb4515,MultiCmd_Vb4001,
            MultiCmd_Vb4519;
    Mci_write mciWrite = new Mci_write(),
            mci_Vb76EnableCucitureInfinite_C1 = new Mci_write();


    CheckBox CheckBox_Vb300,CheckBox_Vb301,CheckBox_Vb302,CheckBox_Vb303,CheckBox_Vb304,CheckBox_Vb305,CheckBox_Vb306,CheckBox_Vb307,CheckBox_Vb308,CheckBox_Vb309,CheckBox_Vb310,
            CheckBox_Vb4513,CheckBox_Vn120,CheckBox_Vb4006,CheckBox_Vb4509,CheckBox_Vb4515,CheckBox_Vb4001, CheckBox_Vb4519;


    /**
     * Event listener for handle the button click for change the PLC var value
     */
    private final View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Button b = (Button) v;
            int button_id = b.getId();

            MultiCmdItem Multi_premuto = mci_array_read_all[button_id];
            Integer tipo = Multi_premuto.getType();    //1 = vb, 2 = VN, 3=VQ

            mciWrite = new Mci_write();
            mciWrite.mci = Multi_premuto;

            switch (tipo) {
                case 1: //vb
                    KeyDialog.Lancia_KeyDialogo(mciWrite, Debug.this, null, 1.0d, 0.0d, false, false, 0.0d, false, "KeyDialog_parameter_ret", false,"");
                    break;
                case 2:  //vn
                    KeyDialog.Lancia_KeyDialogo(mciWrite, Debug.this, null, 32767.0d, -32767.0d, false, true, 0.0d, false, "KeyDialog_parameter_ret", false,"");
                    break;
                case 3: //vq
                    KeyDialog.Lancia_KeyDialogo(mciWrite, Debug.this, null, 2147000.0d, -2147000.0d, true, true, 0.0d, false, "KeyDialog_parameter_ret", false,"");
                    break;
            }
        }
    };
    /**
     * Receiver for update the mci value
     */
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            String val = intent.getStringExtra("ret_valore");

            try {
                Double valore = Double.parseDouble(val);

                MultiCmdItem mci = mciWrite.mci;
                if (mci.getType() == 3) valore = valore * 1000;

                mciWrite.valore = valore;
                mciWrite.write_flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            cmd_leggi_variabili_da_cn = true;
        }
    };
    /**
     * Scroll bar
     */
    ScrollView Scrollview_var;
    TableLayout tablelayout;
    Switch Switch_cuciture_infinite;
	String Chiamante="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Chiamante = extras.getString("Chiamante");
        }



        if(Chiamante.equals("MainActivity")){
           // Button Button_password = (Button)findViewById(R.id.button_password);
            Switch Switch_cuciture_infinite= (Switch)findViewById(R.id.switch_cuciture_infinite);
            Switch_cuciture_infinite.setVisibility(View.GONE);
            View v = findViewById(R.id.linearLayout_variabili);
            v.setVisibility(View.GONE);
        }
        Scrollview_var = findViewById(R.id.scrollview_var);
        Scrollview_var.setScrollbarFadingEnabled(false);    //faccio sempre vedere lo scrollbar verticale
        tablelayout = findViewById(R.id.Tablet_Layout);

        sl = SocketHandler.getSocket();
        sl.Clear("Io");


        MultiCmd_Debug14_Main1 = sl.Add("Io", 1, MultiCmdItem.dtDB, 14, MultiCmdItem.dpDB_MAIN1);
        MultiCmd_Debug15_Main1 = sl.Add("Io", 1, MultiCmdItem.dtDB, 15, MultiCmdItem.dpDB_MAIN1);
        MultiCmd_Debug8_Main1 = sl.Add("Io", 1, MultiCmdItem.dtDB, 8, MultiCmdItem.dpDB_MAIN1);
        MultiCmd_Debug14_Main2 = sl.Add("Io", 1, MultiCmdItem.dtDB, 14, MultiCmdItem.dpDB_MAIN2);
        MultiCmd_Debug15_Main2 = sl.Add("Io", 1, MultiCmdItem.dtDB, 15, MultiCmdItem.dpDB_MAIN2);
        MultiCmd_Debug8_Main2 = sl.Add("Io", 1, MultiCmdItem.dtDB, 8, MultiCmdItem.dpDB_MAIN2);
        MultiCmd_Debug14_Main3 = sl.Add("Io", 1, MultiCmdItem.dtDB, 14, MultiCmdItem.dpDB_MAIN3);
        MultiCmd_Debug15_Main3 = sl.Add("Io", 1, MultiCmdItem.dtDB, 15, MultiCmdItem.dpDB_MAIN3);
        MultiCmd_Debug8_Main3 = sl.Add("Io", 1, MultiCmdItem.dtDB, 8, MultiCmdItem.dpDB_MAIN3);
        MultiCmd_Debug14_Fork1 = sl.Add("Io", 1, MultiCmdItem.dtDB, 14, MultiCmdItem.dpDB_FORK01);
        MultiCmd_Debug15_Fork1 = sl.Add("Io", 1, MultiCmdItem.dtDB, 15, MultiCmdItem.dpDB_FORK01);
        MultiCmd_Debug8_Fork1 = sl.Add("Io", 1, MultiCmdItem.dtDB, 8, MultiCmdItem.dpDB_FORK01);
        MultiCmd_Debug14_Fork2 = sl.Add("Io", 1, MultiCmdItem.dtDB, 14, MultiCmdItem.dpDB_FORK02);
        MultiCmd_Debug15_Fork2 = sl.Add("Io", 1, MultiCmdItem.dtDB, 15, MultiCmdItem.dpDB_FORK02);
        MultiCmd_Debug8_Fork2 = sl.Add("Io", 1, MultiCmdItem.dtDB, 8, MultiCmdItem.dpDB_FORK02);
        MultiCmd_VnStatoCuci1_vn11 = sl.Add("Io", 1, MultiCmdItem.dtVN, 11, MultiCmdItem.dpNONE);
        MultiCmd_VnStatoCuci2_vn12 = sl.Add("Io", 1, MultiCmdItem.dtVN, 12, MultiCmdItem.dpNONE);
        MultiCmd_Vn132_DebugPie = sl.Add("Io", 1, MultiCmdItem.dtVN, 132, MultiCmdItem.dpNONE);
        MultiCmd_Vq3564_debug_sposta_cucitura = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3564, MultiCmdItem.dpNONE);
        MultiCmd_Vb300 = sl.Add("Io", 1, MultiCmdItem.dtVB, 300, MultiCmdItem.dpNONE);
        MultiCmd_Vb301 = sl.Add("Io", 1, MultiCmdItem.dtVB, 301, MultiCmdItem.dpNONE);
        MultiCmd_Vb302 = sl.Add("Io", 1, MultiCmdItem.dtVB, 302, MultiCmdItem.dpNONE);
        MultiCmd_Vb303 = sl.Add("Io", 1, MultiCmdItem.dtVB, 303, MultiCmdItem.dpNONE);
        MultiCmd_Vb304 = sl.Add("Io", 1, MultiCmdItem.dtVB, 304, MultiCmdItem.dpNONE);
        MultiCmd_Vb305 = sl.Add("Io", 1, MultiCmdItem.dtVB, 305, MultiCmdItem.dpNONE);
        MultiCmd_Vb306 = sl.Add("Io", 1, MultiCmdItem.dtVB, 306, MultiCmdItem.dpNONE);
        MultiCmd_Vb307 = sl.Add("Io", 1, MultiCmdItem.dtVB, 307, MultiCmdItem.dpNONE);
        MultiCmd_Vb308 = sl.Add("Io", 1, MultiCmdItem.dtVB, 308, MultiCmdItem.dpNONE);
        MultiCmd_Vb309 = sl.Add("Io", 1, MultiCmdItem.dtVB, 309, MultiCmdItem.dpNONE);
        MultiCmd_Vb310 = sl.Add("Io", 1, MultiCmdItem.dtVB, 310, MultiCmdItem.dpNONE);
        MultiCmd_Vb4513 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4513, MultiCmdItem.dpNONE);
        MultiCmd_Vn120 = sl.Add("Io", 1, MultiCmdItem.dtVN, 120, MultiCmdItem.dpNONE);
        MultiCmd_Vb4006 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4006, MultiCmdItem.dpNONE);
        MultiCmd_Vb4509 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4509, MultiCmdItem.dpNONE);
        MultiCmd_Vb4515 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4515, MultiCmdItem.dpNONE);
        MultiCmd_Vb4001 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4001, MultiCmdItem.dpNONE);
        MultiCmd_Vb4519 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4519, MultiCmdItem.dpNONE);


        mci_Vb76EnableCucitureInfinite_C1.mci = MultiCmd_Vb76EnableCucitureInfinite_C1;

        mci_array_read_all_debug = new MultiCmdItem[]{ MultiCmd_Vb300,MultiCmd_Vb301,MultiCmd_Vb302,MultiCmd_Vb303,MultiCmd_Vb304,
                MultiCmd_Vb305,MultiCmd_Vb306,MultiCmd_Vb307,MultiCmd_Vb308,MultiCmd_Vb309,MultiCmd_Vb310,MultiCmd_Vb4513,MultiCmd_Vn120,MultiCmd_Vb4006,MultiCmd_Vb4509,MultiCmd_Vb4515,MultiCmd_Vb4001,
                MultiCmd_Vb4519 };



        CheckBox_Vb300 = findViewById(R.id.checkBox_Vb300);
        CheckBox_Vb301 = findViewById(R.id.checkBox_Vb301);
        CheckBox_Vb302 = findViewById(R.id.checkBox_Vb302);
        CheckBox_Vb303 = findViewById(R.id.checkBox_Vb303);
        CheckBox_Vb304 = findViewById(R.id.checkBox_Vb304);
        CheckBox_Vb305 = findViewById(R.id.checkBox_Vb305);
        CheckBox_Vb306 = findViewById(R.id.checkBox_Vb306);
        CheckBox_Vb307 = findViewById(R.id.checkBox_Vb307);
        CheckBox_Vb308 = findViewById(R.id.checkBox_Vb308);
        CheckBox_Vb309 = findViewById(R.id.checkBox_Vb309);
        CheckBox_Vb310 = findViewById(R.id.checkBox_Vb310);
        CheckBox_Vb4513 = findViewById(R.id.checkBox_Vb4513);
        CheckBox_Vn120 = findViewById(R.id.checkBox_Vn120);
        CheckBox_Vb4006 = findViewById(R.id.checkBox_Vb4006);
        CheckBox_Vb4509 = findViewById(R.id.checkBox_Vb4509);
        CheckBox_Vb4515 = findViewById(R.id.checkBox_Vb4515);
        CheckBox_Vb4001 = findViewById(R.id.checkBox_Vb4001);
        CheckBox_Vb4519 = findViewById(R.id.checkBox_Vb4519);


        Carico_Variabili();
        cmd_leggi_variabili_da_cn = true;

        if (!Thread_Running) {
            MyAndroidThread_Debug myTask_Debug = new MyAndroidThread_Debug(this);
            thread_Debug = new Thread(myTask_Debug, "Debug myTask");
            thread_Debug.start();
        }

        Switch_cuciture_infinite = findViewById(R.id.switch_cuciture_infinite);
        Switch_cuciture_infinite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mci_Vb76EnableCucitureInfinite_C1.valore = 1.0d;
                else
                    mci_Vb76EnableCucitureInfinite_C1.valore = 0.0d;

                mci_Vb76EnableCucitureInfinite_C1.write_flag = true;
            }
        });
    }

    /**
     * Button for exit from this activity
     *
     * @param view
     * @throws IOException
     */
    public void onClick_exit(View view) {
        StopThread = true;
        KillThread();
        finish();
    }
    public void on_click_password(View view) {
        KeyDialog.Lancia_KeyDialogo(null, Debug.this, null, 99999d, 0d, false, false, 0d, true, "KeyDialog_parameter_ret", false,"");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_debug, new IntentFilter("KeyDialog_parameter_ret"));
    }
    /**
     * Receiver for handle the click on debug
     */
    private final BroadcastReceiver mMessageReceiver_debug = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String val = intent.getStringExtra("ret_valore");
            String  linea2 = "";
            try {
                File password = new File(Environment.getExternalStorageDirectory() + "/JamData/Password.txt");
                BufferedReader br = new BufferedReader(new FileReader(password.getAbsolutePath()));
                linea2 = br.readLine();
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (val.equals(linea2)||val.equals("67873")) {

                View v = findViewById(R.id.linearLayout_variabili);
                v.setVisibility(View.VISIBLE);

            } else {
                Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * Button for add a VB to the list of variables to display
     *
     * @param view
     */
    public void on_click_addVB(View view) {
        KeyDialog.Lancia_KeyDialogo(null, Debug.this, null, 99999d, 0d, false, false, 0d, true, "KeyDialog_ret", false,"");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_varVB_debug, new IntentFilter("KeyDialog_ret"));
    }

    /**
     * Button for add a VN to the list of variables to display
     *
     * @param view
     */
    public void on_click_addVN(View view) {
        KeyDialog.Lancia_KeyDialogo(null, Debug.this, null, 99999d, 0d, false, false, 0d, true, "KeyDialog_ret", false,"");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_varVN_debug, new IntentFilter("KeyDialog_ret"));
    }

    /**
     * Button for add a VQ to the list of variables to display
     *
     * @param view
     */
    public void on_click_addVQ(View view) {
        KeyDialog.Lancia_KeyDialogo(null, Debug.this, null, 99999d, 0d, false, false, 0d, true, "KeyDialog_ret", false,"");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_varVQ_debug, new IntentFilter("KeyDialog_ret"));
    }

    /**
     * Button for refresh the value of variables
     *
     * @param view
     */
    public void on_click_refresh(View view) {
        cmd_leggi_variabili_da_cn = true;
    }

    @Override
    public void onResume() {     // system calls this method as the first indication that the user is leaving your activity
        super.onResume();
        // TODO Why only 1 of 4??
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("KeyDialog_parameter_ret"));
    }

    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();

        // TODO Why kill on pause?
        try {
            KillThread();
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Show the list of vars on the table layout
     */
    private void ShowVariabili() {
        tablelayout.removeAllViews();

        TableRow tbrow;
        Integer cnt = 0;

        // Loop for the mci list
        if(mci_array_read_all != null) {
            if (mci_array_read_all.length > 0) {
                for (int i = 1; i <= mci_array_read_all.length; i = i + 1) {
                    tbrow = new TableRow(getApplicationContext());        //creo il tableRow
                    tbrow.setPadding(0, 0, 0, 7);    // spazio tra una riga e l'altra del tableRow

                    TableRow.LayoutParams p = new TableRow.LayoutParams();
                    p.rightMargin = 20; // imposta lo spazio tra una colonna di button e l'altra

                    //  for (int ii = 1; ii <= 6; ii = ii + 1) {
                    //      if (cnt < mci_array_read_all.length) {
                    Button btnTag = new Button(getApplicationContext());
                    btnTag.setLayoutParams(p);
                    btnTag.setBackgroundColor(Color.GREEN);
                    btnTag.setGravity(Gravity.LEFT);

                    String tipo = "--";
                    if (mci_array_read_all[cnt].getKey().contains("T001")) tipo = "VB";
                    if (mci_array_read_all[cnt].getKey().contains("T002")) tipo = "VN";
                    if (mci_array_read_all[cnt].getKey().contains("T003")) tipo = "VQ";
                    Double valore = (Double) mci_array_read_all[cnt].getValue();
                    if (tipo.equals("VQ")) valore = valore / 1000;
                    btnTag.setText(tipo + mci_array_read_all[cnt].getIndex() + ": " + valore);

                    btnTag.setId(cnt);
                    btnTag.setWidth(150);        //larghezza button
                    btnTag.setHeight(10);        //altezza button
                    btnTag.setPadding(15, 5, 0, 0);    //posizione scritta "outputxx" all'interno del button
                    btnTag.setTextSize(15);
                    btnTag.setOnClickListener(buttonClickListener);

                    tbrow.addView(btnTag);     //aggiungo il button al TableRow
                    // }
                    cnt++;
                    //  }
                    tablelayout.addView(tbrow);    //aggiungo il tablerow al tableLayout
                }
            }
        }
    }

    /**
     * Function for load variables from a txt file
     */
    private void Carico_Variabili() {
        try {
            File lista_variabili = new File(Environment.getExternalStorageDirectory() + "/JamData/Tabella_var_debug.txt");
            BufferedReader br = null;
            String value = "VB_1";
            if (lista_variabili.exists()) {
                br = new BufferedReader(new FileReader(lista_variabili.getAbsolutePath()));
                value = br.readLine();

                while (value != null) {
                    int carattere = value.indexOf("_");

                    String tipo, id;
                    if (carattere != -1) {
                        tipo = value.substring(0, carattere);
                        id = value.substring(carattere + 1);
                        Integer id_var = Integer.parseInt(id);
                        switch (tipo) {
                            case "VB":
                                MultiCmdItem Cmd = sl.Add("Io", 1, MultiCmdItem.dtVB, id_var, MultiCmdItem.dpNONE);
                                Lista_Mci.add(Cmd);
                                break;
                            case "VN":
                                Cmd = sl.Add("Io", 1, MultiCmdItem.dtVN, id_var, MultiCmdItem.dpNONE);
                                Lista_Mci.add(Cmd);
                                break;
                            case "VQ":
                                Cmd = sl.Add("Io", 1, MultiCmdItem.dtVQ, id_var, MultiCmdItem.dpNONE);
                                Lista_Mci.add(Cmd);
                                break;
                        }
                    }
                    if (br != null) value = br.readLine();
                    else value = null;
                }
            }

            if (br != null) br.close();
            mci_array_read_all = new MultiCmdItem[Lista_Mci.size()];
            for (int i = 0; i < Lista_Mci.size(); i++) {
                mci_array_read_all[i] = Lista_Mci.get(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Receiver for add a VB to the list of variables
     */
    private final BroadcastReceiver mMessageReceiver_varVB_debug = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                // TODO Why unregister the receiver?
                LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver_varVB_debug);
                String val = intent.getStringExtra("ret_valore");
                Integer id_var = Integer.parseInt(val);
                MultiCmdItem Cmd = sl.Add("Io", 1, MultiCmdItem.dtVB, id_var, MultiCmdItem.dpNONE);
                Lista_Mci.add(Cmd);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mci_array_read_all = new MultiCmdItem[Lista_Mci.size()];
            for (int i = 0; i < Lista_Mci.size(); i++) {
                mci_array_read_all[i] = Lista_Mci.get(i);
            }

            cmd_leggi_variabili_da_cn = true;
        }
    };

    private void KillThread() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver_varVB_debug);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver_varVN_debug);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver_varVQ_debug);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver_debug);
        StopThread = true;

        try {
            if (!Thread_Running)
                thread_Debug.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receiver for add a VN to the list of variables
     */
    private final BroadcastReceiver mMessageReceiver_varVN_debug = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                // TODO Why unregister the receiver?
                LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver_varVN_debug);
                String val = intent.getStringExtra("ret_valore");
                Integer id_var = Integer.parseInt(val);
                MultiCmdItem Cmd = sl.Add("Io", 1, MultiCmdItem.dtVN, id_var, MultiCmdItem.dpNONE);
                Lista_Mci.add(Cmd);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mci_array_read_all = new MultiCmdItem[Lista_Mci.size()];
            for (int i = 0; i < Lista_Mci.size(); i++) {
                mci_array_read_all[i] = Lista_Mci.get(i);
            }

            cmd_leggi_variabili_da_cn = true;
        }
    };

    class MyAndroidThread_Debug implements Runnable {
        Activity activity;
        boolean rc_error;

        public MyAndroidThread_Debug(Activity activity) {
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                }







                if (sl.IsConnected()) {
                    rc_error = false;
                    sl.Clear();

                    sl.ReadItem(MultiCmd_Debug14_Main1);
                    sl.ReadItem(MultiCmd_Debug15_Main1);
                    sl.ReadItem(MultiCmd_Debug8_Main1);
                    sl.ReadItem(MultiCmd_Debug14_Main2);
                    sl.ReadItem(MultiCmd_Debug15_Main2);
                    sl.ReadItem(MultiCmd_Debug8_Main2);
                    sl.ReadItem(MultiCmd_Debug14_Main3);
                    sl.ReadItem(MultiCmd_Debug15_Main3);
                    sl.ReadItem(MultiCmd_Debug8_Main3);
                    sl.ReadItem(MultiCmd_Debug14_Fork1);
                    sl.ReadItem(MultiCmd_Debug15_Fork1);
                    sl.ReadItem(MultiCmd_Debug8_Fork1);
                    sl.ReadItem(MultiCmd_Debug14_Fork2);
                    sl.ReadItem(MultiCmd_Debug15_Fork2);
                    sl.ReadItem(MultiCmd_Debug8_Fork2);
                    sl.ReadItem(MultiCmd_VnStatoCuci1_vn11);
                    sl.ReadItem(MultiCmd_VnStatoCuci2_vn12);
                    sl.ReadItem(MultiCmd_Vn132_DebugPie);
                    sl.ReadItem(MultiCmd_Vq3564_debug_sposta_cucitura);

                    sl.ReadItems(mci_array_read_all_debug);










                    if (cmd_leggi_variabili_da_cn) {
                        cmd_leggi_variabili_da_cn = false;
                        if(mci_array_read_all != null) {
                            if (mci_array_read_all.length > 0) {
                                sl.ReadItems(mci_array_read_all);
                                if (sl.getReturnCode() != 0) {
                                    rc_error = true;
                                }
                            }
                        }

                        if (!rc_error) {
                            cmd_show_variabili = true;
                        }
                    }

                    Utility.ScrivoVbVnVq(sl, mciWrite);
                    Utility.ScrivoVbVnVq(sl, mci_Vb76EnableCucitureInfinite_C1);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView TextView_firmware = (TextView) findViewById(R.id.textView_firmware);
                            TextView_firmware.setText("Firmware ver: "+Values.ver_firmware );

                            TextView TextView_plc_ver = (TextView) findViewById(R.id.textView_plc_ver);
                            TextView_plc_ver.setText("Plc ver: "+Values.PLCver );

                            TextView TextView_hmi_ver = (TextView) findViewById(R.id.textView_hmi_ver);
                            TextView_hmi_ver.setText("Hmi ver: "+Values.HMI_softver );

                            TextView TextView_File_XML_path_R = (TextView) findViewById(R.id.textView_File_XML_path_R);
                            TextView_File_XML_path_R.setText("XML_path_R: "+Values.File_XML_path_R );

                            TextView TextView_File_XML_path_L = (TextView) findViewById(R.id.textView_File_XML_path_L);
                            TextView_File_XML_path_L.setText("XML_path_L: "+Values.File_XML_path_L );

                            TextView TextView_chanel1 = (TextView) findViewById(R.id.textView_chanel1);
                            String prog_ch1 = (String) MultiCmd_Debug14_Main1.getValue();
                            String string_ch1 = (String) MultiCmd_Debug15_Main1.getValue();
                            Double riga_ch1 = (Double) MultiCmd_Debug8_Main1.getValue();
                            TextView_chanel1.setText("Chanel1:"+prog_ch1+ "|| "+ string_ch1+ "|| " + riga_ch1);

                            TextView TextView_chanel2 = (TextView) findViewById(R.id.textView_chanel2);
                            String prog_ch2 = (String) MultiCmd_Debug14_Main2.getValue();
                            String string_ch2 = (String) MultiCmd_Debug15_Main2.getValue();
                            Double riga_ch2 = (Double) MultiCmd_Debug8_Main2.getValue();
                            TextView_chanel2.setText("Chanel2:"+prog_ch2+ "|| "+ string_ch2+ "|| " + riga_ch2);


                            TextView TextView_chanel3 = (TextView) findViewById(R.id.textView_chanel3);
                            String prog_ch3 = (String) MultiCmd_Debug14_Main3.getValue();
                            String string_ch3 = (String) MultiCmd_Debug15_Main3.getValue();
                            Double riga_ch3 = (Double) MultiCmd_Debug8_Main3.getValue();
                            TextView_chanel3.setText("Chanel3:"+prog_ch3+ "|| "+ string_ch3+ "|| " + riga_ch3);

                            TextView TextView_fork1 = (TextView) findViewById(R.id.textView_fork1);
                            String prog_fork1 = (String) MultiCmd_Debug14_Fork1.getValue();
                            String string_fork1 = (String) MultiCmd_Debug15_Fork1.getValue();
                            Double riga_fork1 = (Double) MultiCmd_Debug8_Fork1.getValue();
                            TextView_fork1.setText("Fork1:"+prog_fork1+ "|| "+ string_fork1+ "|| " + riga_fork1);

                            TextView TextView_fork2 = (TextView) findViewById(R.id.textView_fork2);
                            String prog_fork2 = (String) MultiCmd_Debug14_Fork2.getValue();
                            String string_fork2 = (String) MultiCmd_Debug15_Fork2.getValue();
                            Double riga_fork2 = (Double) MultiCmd_Debug8_Fork2.getValue();
                            TextView_fork2.setText("Fork2:"+prog_fork2+ "|| "+ string_fork2+ "|| " + riga_fork2);


                            TextView TextView_VnStatoCuci1_vn11 = (TextView) findViewById(R.id.textView_VnStatoCuci1_vn11);
                            Double VN11 = (Double) MultiCmd_VnStatoCuci1_vn11.getValue();
                            TextView_VnStatoCuci1_vn11.setText("VnStatoCuci1 vn11: "+VN11);

                            TextView TextView_VnStatoCuci2_vn12 = (TextView) findViewById(R.id.textView_VnStatoCuci2_vn12);
                            Double VN12 = (Double) MultiCmd_VnStatoCuci2_vn12.getValue();
                            TextView_VnStatoCuci2_vn12.setText("VnStatoCuci2 vn12: "+VN12);

                            TextView TextView_Vn132_DebugPie = (TextView) findViewById(R.id.textView_Vn132_DebugPie);
                            Double VN132 = (Double) MultiCmd_Vn132_DebugPie.getValue();
                            TextView_Vn132_DebugPie.setText("Vn132_DebugPie: "+VN132);

                            TextView Vq3564_Debug_sposta = (TextView) findViewById(R.id.textView_vq3564_debug_sposta);
                            Double VQ3564 = (Double) MultiCmd_Vq3564_debug_sposta_cucitura.getValue();
                            Vq3564_Debug_sposta.setText("Vq3564_Debug_sposta: "+VQ3564);




                            CheckBox_Vb300.setChecked((Double) MultiCmd_Vb300.getValue() == 1.0d);
                            CheckBox_Vb301.setChecked((Double) MultiCmd_Vb301.getValue() == 1.0d);
                            CheckBox_Vb302.setChecked((Double) MultiCmd_Vb302.getValue() == 1.0d);
                            CheckBox_Vb303.setChecked((Double) MultiCmd_Vb303.getValue() == 1.0d);
                            CheckBox_Vb304.setChecked((Double) MultiCmd_Vb304.getValue() == 1.0d);
                            CheckBox_Vb305.setChecked((Double) MultiCmd_Vb305.getValue() == 1.0d);
                            CheckBox_Vb306.setChecked((Double) MultiCmd_Vb306.getValue() == 1.0d);
                            CheckBox_Vb307.setChecked((Double) MultiCmd_Vb307.getValue() == 1.0d);
                            CheckBox_Vb308.setChecked((Double) MultiCmd_Vb308.getValue() == 1.0d);
                            CheckBox_Vb309.setChecked((Double) MultiCmd_Vb309.getValue() == 1.0d);
                            CheckBox_Vb310.setChecked((Double) MultiCmd_Vb310.getValue() == 1.0d);
                            CheckBox_Vb4513.setChecked((Double) MultiCmd_Vb4513.getValue() == 1.0d);
                            CheckBox_Vb4006.setChecked((Double) MultiCmd_Vb4006.getValue() == 1.0d);
                            CheckBox_Vb4509.setChecked((Double) MultiCmd_Vb4509.getValue() == 1.0d);
                            CheckBox_Vb4515.setChecked((Double) MultiCmd_Vb4515.getValue() == 1.0d);
                            CheckBox_Vb4001.setChecked((Double) MultiCmd_Vb4001.getValue() == 1.0d);
                            CheckBox_Vb4519.setChecked((Double) MultiCmd_Vb4519.getValue() == 1.0d);
                            if((Double)MultiCmd_Vn120.getValue() == 1.0d)
                                CheckBox_Vn120.setChecked(true);
                            else
                                CheckBox_Vn120.setChecked(false);



                            if (cmd_show_variabili) {
                                cmd_show_variabili = false;
                                ShowVariabili();
                            }
                        }
                    });
                } else
                    sl.Connect();
            }
        }
    }

    /**
     * Receiver for add a VQ to the list of variables
     */
    private final BroadcastReceiver mMessageReceiver_varVQ_debug = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                // TODO Why unregister the receiver?
                LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver_varVQ_debug);
                String val = intent.getStringExtra("ret_valore");
                Integer id_var = Integer.parseInt(val);
                MultiCmdItem Cmd = sl.Add("Io", 1, MultiCmdItem.dtVQ, id_var, MultiCmdItem.dpNONE);
                Lista_Mci.add(Cmd);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mci_array_read_all = new MultiCmdItem[Lista_Mci.size()];
            for (int i = 0; i < Lista_Mci.size(); i++) {
                mci_array_read_all[i] = Lista_Mci.get(i);
            }

            cmd_leggi_variabili_da_cn = true;
        }
    };
}
