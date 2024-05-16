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
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
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

    /**
     * Shopping list for communicate with PLC
     */
    ShoppingList sl;

    /**
     * The list of mci to display
     */
    ArrayList<MultiCmdItem> Lista_Mci = new ArrayList<MultiCmdItem>();
    MultiCmdItem[] mci_array_read_all;

    MultiCmdItem MultiCmd_Vb76EnableCucitureInfinite_C1;
    Mci_write mciWrite = new Mci_write(),
            mci_Vb76EnableCucitureInfinite_C1 = new Mci_write();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);
        Scrollview_var = findViewById(R.id.scrollview_var);
        Scrollview_var.setScrollbarFadingEnabled(false);    //faccio sempre vedere lo scrollbar verticale
        tablelayout = findViewById(R.id.Tablet_Layout);

        sl = SocketHandler.getSocket();
        sl.Clear("Io");

        MultiCmd_Vb76EnableCucitureInfinite_C1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 76, MultiCmdItem.dpNONE);

        mci_Vb76EnableCucitureInfinite_C1.mci = MultiCmd_Vb76EnableCucitureInfinite_C1;

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
        for (int i = 1; i <= mci_array_read_all.length; i = i + 6) {
            tbrow = new TableRow(getApplicationContext());        //creo il tableRow
            tbrow.setPadding(0, 0, 0, 7);    // spazio tra una riga e l'altra del tableRow

            TableRow.LayoutParams p = new TableRow.LayoutParams();
            p.rightMargin = 20; // imposta lo spazio tra una colonna di button e l'altra

            for (int ii = 1; ii <= 6; ii = ii + 1) {
                if (cnt < mci_array_read_all.length) {
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
                }
                cnt++;
            }
            tablelayout.addView(tbrow);    //aggiungo il tablerow al tableLayout
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

                    if (cmd_leggi_variabili_da_cn) {
                        cmd_leggi_variabili_da_cn = false;
                        sl.ReadItems(mci_array_read_all);
                        if (sl.getReturnCode() != 0) {
                            rc_error = true;
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
