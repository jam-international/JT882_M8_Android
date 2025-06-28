package com.jam_int.jt882_m8;

import static android.content.Intent.ACTION_MEDIA_MOUNTED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import communication.MultiCmdItem;
import communication.ShoppingList;
import me.jahnen.libaums.core.UsbMassStorageDevice;
import me.jahnen.libaums.core.fs.FileSystem;
import me.jahnen.libaums.core.fs.UsbFile;

public class Tool_page extends Activity {
    /**
     * On activity result indexes
     */
    final private static int PAGE_UDF_T1_DX = 200;
    final private static int  PAGE_UDF_T1_SX = 201;
    final private static int PAGE_MODIFICA_PROG = 202;
    final private static int PAGE_C1_PARAM = 204;
    final private static int PAGE_C2_PARAM = 205;
    final private static int PAGE_PARAM_PIEGATORE = 206;
    final private static int PAGE_PARAM_SCARICATORE = 207;
    final private static int PAGE_CREA_TASCA = 208;
    final private static int PAGE_UDF_T2_DX = 209;
    final private static int PAGE_UDF_T2_SX = 210;
    final private static int PAGE_PARAM_TRASLATORE = 211;
    final private static int PAGE_CREA_TASCA_QUOTE = 212;

    final private static int RESULT_PAGE_LOAD_UDF_R_T1 = 102;
    final private static int RESULT_PAGE_LOAD_UDF_L_T1 = 103;
    final private static int RESULT_PAGE_C1_PARAM = 105;
    final private static int RESULT_PAGE_C2_PARAM = 106;
    final private static int RESULT_PAGE_PARAM_PIEGATORE = 107;
    final private static int RESULT_PAGE_PARAM_SCARICATORE = 108;
    final private static int RESULT_PAGE_LOAD_UDF_R_T2 = 109;
    final private static int RESULT_PAGE_LOAD_UDF_L_T2 = 110;
    final private static int RESULT_PAGE_PARAM_TRASLATORE = 111;
    final private static int PAGE_DELTA = 303;
    final private static int PAGE_Z_AGO = 304;
    final private static int RESULT_PAGE_UPGRADE = 106;
    final private static int RESULT_PAGE_PATTINA = 120;
    final private static int TESTA_C1 = 300;
    final private static int TESTA_C2 = 301;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    String Lato_tasca_T1 = "", Lato_tasca_T2 = "",Machine_model="";
    /**
     * ShoppingList for communicate with PLC
     */
    ShoppingList sl;
    /**
     * Thread
     */
    Thread thread_Tool;
    boolean Thread_Running = false, StopThread = false, first_cycle = true, rc_error = false;
    /**
     * UI components
     */
    Button Button_Delta, Button_Z_ago,Button_Z_ago_C2, Button_setting, Button_test_io, Button_upgrade_hmi, Button_upgrade_plc, Button_report_to_usb, Button_disegna, Button_password, Button_Sgancio_ago,
            Button_Fai_spola,Button_pagina_punto_carico,Button_pattina;
    /**
     * PLC vars
     */
    MultiCmdItem MultiCmd_Vn3804_pagina_touch, MultiCmd_tasto_verde, MultiCmd_CH1_in_emergenza, MultiCmd_Sblocca_Ago, MultiCmd_Vb1002_Init_CAM,MultiCmd_Vb2002_Init_CAM, MultiCmd_Vb4075_GiraAgoFaiSpolaC1,
            MultiCmd_Vb151EnableCarPattine;
    Mci_write Mci_Sblocca_Ago = new Mci_write(),
            Mci_Vb1002_Init_CAM = new Mci_write(),
            Mci_Vb2002_Init_CAM = new Mci_write(),
            Mci_Vb4075_GiraAgoFaiSpolaC1 = new Mci_write();
    MultiCmdItem[] mci_array_read_all;
    /**
     * USB
     */
    UsbMassStorageDevice device_usb;
    FileSystem currentFs;
    UsbFile root;
    Intent databack = new Intent();
    String databack_text = "";  //serve per indicare alla Mainactivity quali file XML vanno ricaricati
    String chiamante = "",activity_tasca_quote="";
    private boolean isReceiverRegistered = false;
    ProgressDialog progress;
    Intent databack_toMainActivity;
    TextView TextView_status_Report_to_usb;
    boolean Read_Syslog = false,ret_read_syslog=false, Finito_lettura_Syslog = false;
    int mc_stati_Report_to_usb = 0;
    UsbDevice device_prova;
    /**
     * Receiver for handle the unlock of all the buttons
     */
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            String val = intent.getStringExtra("ret_valore");

            String linea1 = "", linea2 = "";
            try {
                File password = new File(Environment.getExternalStorageDirectory() + "/JamData/Password.txt");
                BufferedReader br = new BufferedReader(new FileReader(password.getAbsolutePath()));
                linea1 = br.readLine();
                linea2 = br.readLine();
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (val.equals(linea1) || val.equals("67874")) {
                if (!chiamante.equals("Pagina_emergenza")) {
                    Button_Z_ago.setVisibility(View.VISIBLE);
                    if( Machine_model.equals("JT882M") || Machine_model.equals("JT882MA") ||Machine_model.equals("JT882MB"))
                        Button_Z_ago_C2.setVisibility(View.VISIBLE);
                }
                Button_upgrade_hmi.setVisibility(View.VISIBLE);
                Button_upgrade_plc.setVisibility(View.VISIBLE);
                Button_test_io.setVisibility(View.VISIBLE);
                Button_report_to_usb.setVisibility(View.VISIBLE);
            }
            if (val.equals(linea2)|| val.equals("67874")) {
                Button_Delta.setVisibility(View.VISIBLE);
            }
            if(!val.equals(linea2) && !val.equals(linea1)&& !val.equals("67874")){
                if (!val.equals("")) {
                    Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Machine_model = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "MachineModel", "null", null, null, "Machine_model", getApplicationContext());
        } catch (IOException e) {
            Machine_model="";
        }

        setContentView(R.layout.activity_tools);
        Button_Delta = findViewById(R.id.button_Delta);
        Button_Z_ago = findViewById(R.id.button_Z_ago);
        Button_Z_ago_C2 = findViewById(R.id.button_Z_ago2);
        Button_setting = findViewById(R.id.button_setting);
        Button_test_io = findViewById(R.id.button_test_io);
        Button_upgrade_hmi = findViewById(R.id.button_upgrade_usb);
        Button_upgrade_plc = findViewById(R.id.button_upgrade_plc);

        Button_report_to_usb = findViewById(R.id.button_report_to_usb);
        Button_disegna = findViewById(R.id.button_disegna);
        Button_password = findViewById(R.id.button_password);
        Button_Delta.setVisibility(View.GONE);
        Button_Z_ago.setVisibility(View.GONE);
        Button_Z_ago_C2.setVisibility(View.GONE);
        Button_setting.setVisibility(View.GONE);
        Button_upgrade_hmi.setVisibility(View.GONE);
        Button_upgrade_plc.setVisibility(View.GONE);
        Button_test_io.setVisibility(View.GONE);

        Button_report_to_usb.setVisibility(View.GONE);
        Button_Sgancio_ago = findViewById(R.id.btn_sgancio_ago);
        Button_Fai_spola = (Button) findViewById(R.id.btn_fai_spola);
        Button_pagina_punto_carico = (Button) findViewById(R.id.button_pagina_punto_carico);
        Button_pattina= (Button) findViewById(R.id.btn_pattina);


        TextView_status_Report_to_usb = (TextView)findViewById(R.id.textView_status_Report_to_usb) ;
        TextView_status_Report_to_usb.setVisibility(View.GONE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

        if (GetUSBConnectionStatus()) {
            UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(getApplicationContext());
            if (devices.length > 0) {
                device_usb = devices[0];

                PendingIntent permissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                registerReceiver(usbReceiver, filter);
                UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                manager.requestPermission(device_usb.getUsbDevice(), permissionIntent);
            }
        }

        // Register Usb events
        IntentFilter filter_attached = new IntentFilter(ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(usbReceiver, filter_attached);
        IntentFilter filter_mounted = new IntentFilter(ACTION_MEDIA_MOUNTED);
        registerReceiver(usbReceiver, filter_mounted);
        IntentFilter filter_permission = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter_permission);
        IntentFilter filter_detached = new IntentFilter(ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbReceiver, filter_detached);

        Bundle extras = getIntent().getExtras();
        databack_toMainActivity = getIntent();



        if (extras != null) {
            Lato_tasca_T1 = extras.getString("Lato_tasca_T1");
            Lato_tasca_T2 = extras.getString("Lato_tasca_T2");
            chiamante = extras.getString("chiamante");
            activity_tasca_quote =  extras.getString("ACTIVITY_TASCA_QUOTE");

            if(activity_tasca_quote != null && !activity_tasca_quote.equals("")){

                if(Values.Chiamante == "CREATO_TASCA_DA_QUOTE_1" ||   Values.Chiamante =="CREATO_TASCA_DA_QUOTE_2" || Values.Chiamante =="CREATO_TASCA_DA_QUOTE_12"){

                    finish();
                }


             //   databack.setData(Uri.parse("CARICA_T1_R_DA_QUOTE"));
              //  setResult(RESULT_OK, databack);
              //  finish();
            }
        }

        if (chiamante != null){
            if(chiamante.equals("Pagina_emergenza"))
                Button_disegna.setVisibility(View.GONE);
        } else
            chiamante = "";



        sl = SocketHandler.getSocket();
        sl.Clear("Io");

        MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);
        MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 21, MultiCmdItem.dpNONE);
        MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
        MultiCmd_Sblocca_Ago = sl.Add("Io", 1, MultiCmdItem.dtVB, 1018, MultiCmdItem.dpNONE);
        MultiCmd_Vb1002_Init_CAM = sl.Add("Io", 1, MultiCmdItem.dtVB, 1002, MultiCmdItem.dpNONE);
        MultiCmd_Vb2002_Init_CAM = sl.Add("Io", 1, MultiCmdItem.dtVB, 2002, MultiCmdItem.dpNONE);
        MultiCmd_Vb4075_GiraAgoFaiSpolaC1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4075, MultiCmdItem.dpNONE);
        MultiCmd_Vb151EnableCarPattine = sl.Add("Io", 1, MultiCmdItem.dtVB, 151, MultiCmdItem.dpNONE);

        Mci_Sblocca_Ago.mci = MultiCmd_Sblocca_Ago;
        Mci_Sblocca_Ago.write_flag = false;

        Mci_Vb4075_GiraAgoFaiSpolaC1.mci = MultiCmd_Vb4075_GiraAgoFaiSpolaC1;
        Mci_Vb4075_GiraAgoFaiSpolaC1.write_flag = false;

        Mci_Vb1002_Init_CAM.mci = MultiCmd_Vb1002_Init_CAM;
        Mci_Vb1002_Init_CAM.write_flag = false;

        Mci_Vb2002_Init_CAM.mci = MultiCmd_Vb2002_Init_CAM;
        Mci_Vb2002_Init_CAM.write_flag = false;


        mci_array_read_all = new MultiCmdItem[]{MultiCmd_tasto_verde, MultiCmd_CH1_in_emergenza,MultiCmd_Vb151EnableCarPattine};

        Toggle_Button.CreaToggleButton(Mci_Sblocca_Ago, Button_Sgancio_ago, "ic_sblocca_ago_press", "ic_sblocca_ago", getApplicationContext(), sl);
        Toggle_Button.CreaToggleButton(Mci_Vb4075_GiraAgoFaiSpolaC1, Button_Fai_spola, "ic_fai_spola_premuto", "ic_fai_spola", getApplicationContext(), sl);







        // Start the thread
        if (!Thread_Running) {
            Tool_page.MyAndroidThread_Tool myTask_tool = new Tool_page.MyAndroidThread_Tool(this);
            thread_Tool = new Thread(myTask_tool, "Tool myTask");
            thread_Tool.start();
            Log.d("JAM TAG", "Start Toolpage Thread");
        }
    }

    private void Registra_USB() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

        if (GetUSBConnectionStatus()) {
            UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(getApplicationContext());
            if (devices.length > 0) {
                device_usb = devices[0];

                PendingIntent permissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                registerReceiver(usbReceiver, filter);
                UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                manager.requestPermission(device_usb.getUsbDevice(), permissionIntent);
            }
        }

        // Register Usb events
        IntentFilter filter_attached = new IntentFilter(ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(usbReceiver, filter_attached);
        IntentFilter filter_mounted = new IntentFilter(ACTION_MEDIA_MOUNTED);
        registerReceiver(usbReceiver, filter_mounted);
        IntentFilter filter_permission = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter_permission);
        IntentFilter filter_detached = new IntentFilter(ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbReceiver, filter_detached);


        isReceiverRegistered = true;
    }

    /**
     * Button for open copy files activity
     *
     * @param view
     */
    public void on_click_copia_file(View view) {
        KillThread();
        Log.d("JAM TAG", "Toolpage OnclickCopyFilePage");

        Intent intent_par = new Intent(getApplicationContext(), Copy_Files_Activity.class);
        startActivity(intent_par);
    }
    /**
     * Button for on_click_pattina
     *
     * @param view
     */
    public void on_click_pattina(View view) {

        KillThread();

        Intent intent_for_tcp = new Intent(getApplicationContext(), Pattina.class);
        startActivityForResult(intent_for_tcp, RESULT_PAGE_PATTINA);
    }
    /**
     * Button for open punto carico activity
     *
     * @param view
     */
    public void onClick_pagina_punto_carico_t1(View view) {
        KillThread();
        Intent intent_punto_carico = new Intent(getApplicationContext(), Punto_carico_page.class);
        if (Values.File_XML_path_R != null) {
            if (!Lato_tasca_T1.equals("SX")) {
                intent_punto_carico.putExtra("Chiamante", PAGE_UDF_T1_DX);
                startActivityForResult(intent_punto_carico, PAGE_UDF_T1_DX);
            } else {
                intent_punto_carico.putExtra("Chiamante", PAGE_UDF_T1_SX);
                startActivityForResult(intent_punto_carico, PAGE_UDF_T1_SX);
            }
        }
    }
    public void onClick_pagina_punto_carico_t2(View view) throws IOException
    {
        KillThread();
        Intent intent_punto_carico = new Intent(getApplicationContext(), Punto_carico_page.class);
        if(Values.File_XML_path_T2_R != null) {

            if (Lato_tasca_T2 == null || Lato_tasca_T2.equals("")) Lato_tasca_T2 = "DX";
            if(Lato_tasca_T2.equals("SX")) {
                intent_punto_carico.putExtra("Chiamante", PAGE_UDF_T2_SX);
                startActivityForResult(intent_punto_carico, PAGE_UDF_T2_SX);


            }else {
                intent_punto_carico.putExtra("Chiamante", PAGE_UDF_T2_DX);
                startActivityForResult(intent_punto_carico, PAGE_UDF_T2_DX);
            }
        }

    }
    /**
     * Button for open modifica programma activity
     *
     * @param view
     */
    public void on_click_modifica_programma(View view) {
        int chiamante = 0;

        if (Values.File_XML_path_R != null) {

            if (Lato_tasca_T1 == null || Lato_tasca_T1.equals("")) Lato_tasca_T1 = "DX";
            if (Lato_tasca_T1.equals("DX")) {
                chiamante = PAGE_UDF_T1_DX;

            } else {
                chiamante = PAGE_UDF_T1_SX;

            }
        }
        KillThread();
        Intent intent_for_parametri = new Intent(getApplicationContext(), Modifica_programma.class);
        intent_for_parametri.putExtra("Chiamante", chiamante);
        startActivityForResult(intent_for_parametri, PAGE_MODIFICA_PROG);
    }
    public void on_click_modifica_programma_C2(View view) {
        int chiamante = 0;

        if (Values.File_XML_path_T2_R != null) {

            if (Lato_tasca_T2 == null || Lato_tasca_T2.equals("")) Lato_tasca_T2 = "DX";
            if (Lato_tasca_T2.equals("DX")) {
                chiamante = PAGE_UDF_T2_DX;

            } else {
                chiamante = PAGE_UDF_T2_SX;

            }
        }
        KillThread();
        Intent intent_for_parametri = new Intent(getApplicationContext(), Modifica_programma.class);
        intent_for_parametri.putExtra("Chiamante", chiamante);
        startActivityForResult(intent_for_parametri, PAGE_MODIFICA_PROG);
    }
    /**
     * Button for open the key dialog and insert a password for unlock all buttons
     *
     * @param view
     */
    public void on_click_password(View view) {
        KeyDialog.Lancia_KeyDialogo(null, Tool_page.this, null, 99999d, 0d, false, false, 0d, true, "KeyDialog_password_ret", false,"");
    }

    /**
     * Button for open the settings page
     * <p>
     * TODO
     *
     * @param view
     */
    public void on_click_setting(View view) {
    }

    /**
     * Button for copy every crash files, the info_jam file and machine log to the usb
     *
     * @param view
     */
    public void on_click_report_to_usb(View view) {
        Log.d("JAM TAG", "Toolpage OnclickCrashReport");
        mc_stati_Report_to_usb = 10;
    }

    /**
     * Button for open the page test input output
     *
     * @param view
     */
    public void onClick_test_io(View view) {
        KillThread();
        Intent intent_io = new Intent(getApplicationContext(), Page_Test_IO.class);
        intent_io.putExtra("chiamante", "Pagina_Tool");
        startActivity(intent_io);
    }

    /**
     * TODO
     *
     * @param view
     */
    public void on_click_backup_cn_su_hmi(View view) {
        KillThread();
        Intent intent_par = new Intent(getApplicationContext(), Upgrade_activity.class);
        intent_par.putExtra("chiamante_stringa", "Backup_su_hmi");
        startActivityForResult(intent_par, RESULT_PAGE_UPGRADE);
    }

    /**
     * Button for update the plc version
     *
     * @param view
     */
    public void Button_update_plc_click(View view) {
        KillThread();
        Intent intent_par = new Intent(getApplicationContext(), Upgrade_activity.class);
        intent_par.putExtra("chiamante_stringa", "Upgrade");
        startActivityForResult(intent_par, RESULT_PAGE_UPGRADE);
    }

    /**
     * Button for open the head parameters
     *
     * @param view
     */
    public void On_click_par_Testa(View view) {
        KillThread();
        Intent intent_for_parametri = new Intent(getApplicationContext(), Parametri_page.class);
        intent_for_parametri.putExtra("Chiamante", PAGE_C1_PARAM);
        startActivityForResult(intent_for_parametri, RESULT_PAGE_C1_PARAM);
    }
    /**
     * Button for open the head parameters
     *
     * @param view
     */
    public void On_click_par_Testa_C2(View view) {
        KillThread();
        Intent intent_for_parametri = new Intent(getApplicationContext(), Parametri_page.class);
        intent_for_parametri.putExtra("Chiamante", PAGE_C2_PARAM);
        startActivityForResult(intent_for_parametri, RESULT_PAGE_C2_PARAM);
    }

    public void On_click_par_traslat(View view) throws IOException
    {
        Intent intent_for_parametri = new Intent(getApplicationContext(), Parametri_page.class);
        intent_for_parametri.putExtra("Chiamante", PAGE_PARAM_TRASLATORE);
        startActivityForResult(intent_for_parametri, RESULT_PAGE_PARAM_TRASLATORE);


    }



    /**
     * Button for open delta parameters
     *
     * @param view
     */
    public void on_click_Delta(View view) {
        KillThread();
        Intent intent_for_Delta = new Intent(getApplicationContext(), Delta_parametri.class);
        startActivity(intent_for_Delta);
    }

    /**
     * Button for open the TODO piegatore sequence
     *
     * @param view
     */
    public void On_click_par_piegatore(View view) {
        KillThread();
        Intent intent_for_parametri = new Intent(getApplicationContext(), Piegatore_sequenza.class);
        intent_for_parametri.putExtra("Chiamante", PAGE_PARAM_PIEGATORE);
        startActivityForResult(intent_for_parametri, RESULT_PAGE_PARAM_PIEGATORE);
    }

    /**
     * Button for open the tasca frecce activity (create pocket from points)
     *
     * @param v
     */
    public void BtnTascaFrecce(View v) {
        KillThread();
        Log.d("JAM TAG", "Toolpage BtnTascaFrecce");
        Intent intent_par = new Intent(getApplicationContext(), TascaFrecceActivity.class);
        startActivity(intent_par);
    }

    /**
     * Button for open the fdraw activity (create pocket from values)
     *
     * @param view
     */
    public void onClick_tascaQuote(View view) {
        KillThread();
        Log.d("JAM TAG", "Toolpage onClick_tascaQuote");
        Intent intent_par = new Intent(getApplicationContext(), FDrawActivity.class);
        intent_par.putExtra("Chiamante", PAGE_CREA_TASCA);
        startActivityForResult(intent_par, PAGE_CREA_TASCA);
    }

    /**
    /**
     * Button for update the HMI software version from an APK in the root folder of the Usb
     *
     * @param view
     */
    public void Button_update_click(View view) {
        try {
            if (GetUSBConnectionStatus()) {
                root = currentFs.getRootDirectory();
                UsbFile[] files = root.listFiles();
                UsbFile file;
                for (UsbFile itemfile : files) {

                    if (!itemfile.isDirectory()) {
                        String name = itemfile.getName();
                        String estensione = "";
                        try {
                            estensione = name.substring(name.lastIndexOf(".") + 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (name.contains("JamPocket") && estensione.equalsIgnoreCase("apk")) {

                            file = itemfile;
                            File Hmiroot = android.os.Environment.getExternalStorageDirectory();
                            File file_hmi = new File(Hmiroot.getAbsolutePath() + "/Upgrade/" + file.getName());      //creo file su hmi

                            copyFileToHmiAndUpgrade(file,"storage/emulated/0/JamData/" + file.getName());


                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Button for open the scaric parameters activity
     *
     * @param view
     */
    public void On_click_par_scaric(View view) {
        KillThread();
        Intent intent_for_parametri = new Intent(getApplicationContext(), Parametri_page.class);
        intent_for_parametri.putExtra("Chiamante", PAGE_PARAM_SCARICATORE);
        startActivityForResult(intent_for_parametri, RESULT_PAGE_PARAM_SCARICATORE);
    }

    /**
     * Button for open the usb copy files activity
     *
     * @param view
     */
    public void OnclickUsbPage(View view) {
        KillThread();
        Intent intent_par = new Intent(getApplicationContext(), Usb_Files_Activity.class);
        startActivity(intent_par);
    }

    /**
     * Button for open the settings page with the clock settings
     *
     * @param view
     */
    public void OnclickOrologio(View view) {
        KillThread();
        Log.d("JAM TAG", "Toolpage OnclickOrologio");

        startActivity(new Intent(android.provider.Settings.ACTION_DATE_SETTINGS));
    }

    /**
     * Button for open the statistics activity
     *
     * @param view
     */
    public void OnclickGrafico(View view) {
        KillThread();
        Log.d("JAM TAG", "Toolpage OnclickGrafico");

        Intent intent_par = new Intent(getApplicationContext(), Statistiche.class);
        startActivity(intent_par);
    }

    /**
     * Button for open the z ago activity
     *
     * @param view
     */
    public void Onclick_Z_Ago(View view) {
        KillThread();
        Intent intent_Z_Ago = new Intent(getApplicationContext(), Z_Ago.class);
        intent_Z_Ago.putExtra("Chiamante", TESTA_C1);
        startActivity(intent_Z_Ago);
    }
    /**
     * Button for open the z ago activity
     *
     * @param view
     */
    public void Onclick_Z_Ago_C2(View view) {
        KillThread();
        Intent intent_Z_Ago = new Intent(getApplicationContext(), Z_Ago.class);
        intent_Z_Ago.putExtra("Chiamante", TESTA_C2);
        startActivity(intent_Z_Ago);
    }

    /**
     * Button for open the udf parameters activity
     *
     * @param view
     */
    public void On_click_par_udf(View view) {
        KillThread();
        Intent intent_for_parametri = new Intent(getApplicationContext(), Parametri_page.class);

        if (Values.File_XML_path_R != null) {
            if (Lato_tasca_T1 == null || Lato_tasca_T1.equals("")) Lato_tasca_T1 = "DX";
            if (Lato_tasca_T1.equals("DX")) {
                intent_for_parametri.putExtra("Chiamante", PAGE_UDF_T1_DX);
                startActivityForResult(intent_for_parametri, PAGE_UDF_T1_DX);
            } else {
                intent_for_parametri.putExtra("Chiamante", PAGE_UDF_T1_SX);
                startActivityForResult(intent_for_parametri, PAGE_UDF_T1_SX);
            }
        }
    }
    //*************************************************************************************************
    // On_click_par_udf_C2
    //*************************************************************************************************
    public void On_click_par_udf_C2(View view) throws IOException
    {
        Intent intent_for_parametri = new Intent(getApplicationContext(), Parametri_page.class);


        if(Values.File_XML_path_T2_R != null) {
            if (Lato_tasca_T2 == null || Lato_tasca_T2.equals("")) Lato_tasca_T2 = "DX";
            if(Lato_tasca_T2.equals("DX")) {
                intent_for_parametri.putExtra("Chiamante", PAGE_UDF_T2_DX);
                startActivityForResult(intent_for_parametri, PAGE_UDF_T2_DX);

            }else {
                intent_for_parametri.putExtra("Chiamante", PAGE_UDF_T2_SX);
                startActivityForResult(intent_for_parametri, PAGE_UDF_T2_SX);
            }
        }

    }
    /**
     * Button for close this activity
     *
     * @param view
     */
    public void onClick_exit(View view) {
        KillThread();
        if(activity_tasca_quote != null && activity_tasca_quote.equals("CARICATO_T1_DX_DA_QUOTE"))
        {
            Intent databack = new Intent();
            databack.putExtra("yourKeyName", "hello");
            setResult(900, databack);

           // Intent databack = new Intent();
          //  databack.setData(Uri.parse("CARICATO_T1_DX"));
          //  setResult(RESULT_OK, databack);

        }

        finish();
    }

    @Override
    public void onResume() {     // system calls this method as the first indication that the user is leaving your activity
        super.onResume();
        Toggle_Button.Disabilita_Imagebutton(Button_upgrade_hmi, "ic_upgrade_disable", getApplicationContext());
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("KeyDialog_password_ret"));

            if (!isReceiverRegistered)
                Registra_USB();

        if (!Thread_Running) {
            Thread_Running = false;
            StopThread = false;
            first_cycle = true;
            Tool_page.MyAndroidThread_Tool myTask_tool = new Tool_page.MyAndroidThread_Tool(this);
            thread_Tool = new Thread(myTask_tool, "Tool myTask");
            thread_Tool.start();
            Log.d("JAM TAG", "Start Toolpage Thread resume");
        }
    }

    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
    //!!!! non mettere qui killeThread altrimenti la finestrella del permesso della USB lo chiama
    }

    @Override                   //your activity is no longer visible to the user
    public void onStop() {
        super.onStop();
    }

    @Override                   //your activity is no longer visible to the user
    public void onDestroy() {
        super.onDestroy();
        try {
            KillThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Function for check if at least 1 usb is connected
     *
     * @return
     */
    private boolean GetUSBConnectionStatus() {
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(getApplicationContext());
        return devices.length > 0;
    }

    /**
     * Function for handle emergency button
     *
     */
    private void Emergenza() {
        if ((Double) MultiCmd_tasto_verde.getValue() == 0.0d || (Double) MultiCmd_CH1_in_emergenza.getValue() == 1.0d) {
            KillThread();
            Utility.ClearActivitiesTopToEmergencyPage(getApplicationContext());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String Result_ricarica_XML = "SI";   //di default rileggo sempre udf tranne se da una specifica pagina mi arriva di non ricaricare
        try {
            if(data !=null)
                Result_ricarica_XML = data.getData().toString();
        } catch (Exception e) {
            e.printStackTrace();
            Result_ricarica_XML = "SI";
        }

        if (Result_ricarica_XML.equals("SI") || Result_ricarica_XML.equals("CARICATO_T1_DX") || Result_ricarica_XML.equals("CARICATO_T1_SX")
            || Result_ricarica_XML.equals("CARICATO_T2_DX")|| Result_ricarica_XML.equals("CARICATO_T2_SX")) {

            Intent intent = new Intent(getApplicationContext(), Select_file_to_CN.class);

            switch (requestCode) {
                case PAGE_MODIFICA_PROG:
                    // preparo intent databack che verrà passato al onActivityResult quando tornerò alla pagina mainactivity
                    // nella Mainactivity ricaricherò XML relativo che ha subito la modifica


                    /*
                    Lato_tasca_T1 // = extras.getString("Lato_tasca_T1");
                    Lato_tasca_T2 //= extras.getString("Lato_tasca_T2");

                    databack.setData(Uri.parse("CARICATO_T1_DX"));  break;
                case PAGE_UDF_T1_SX:
                    databack.setData(Uri.parse("CARICATO_T1_SX"));  break;
                case PAGE_UDF_T2_DX:
                    databack.setData(Uri.parse("CARICATO_T2_DX"));  break;
                case PAGE_UDF_T2_SX:
                    databack.setData(Uri.parse("CARICATO_T2_SX"));  break;

                    */

                    databack_text = databack_text + "PAGE_MODIFICA_PROG";
                    databack.setData(Uri.parse(Result_ricarica_XML));
                    setResult(RESULT_OK, databack);
                    break;
                case PAGE_UDF_T1_DX:
                    // preparo intent databack che verrà passato al onActivityResult quando tornerò alla pagina mainactivity
                    // nella Mainactivity ricaricherò XML relativo che ha subito la modifica
                    databack_text = databack_text + "PAGE_UDF_T1_DX";
                    databack.setData(Uri.parse(databack_text));
                    setResult(RESULT_OK, databack);

                    //invio file modificato al CN se ho almeno cambiato un dato

                    if (resultCode == RESULT_OK) {
                        //ho cambiato almeno un dato nella pagina parametri
                        intent.putExtra("operazione", "Saving....");
                        intent.putExtra("Chiamante", "T1_R");
                        intent.putExtra("File_path", Values.File_XML_path_R);
                        startActivityForResult(intent, RESULT_PAGE_LOAD_UDF_R_T1);
                    }
                    break;
                case PAGE_UDF_T1_SX:
                    // preparo intent databack che verrà passato al onActivityResult quando tornerò alla pagina mainactivity
                    // nella Mainactivity ricaricherò XML relativo che ha subito la modifica
                    databack_text = databack_text + "PAGE_UDF_T1_SX";
                    databack.setData(Uri.parse(databack_text));
                    setResult(RESULT_OK, databack);

                    //invio file modificato al CN se ho almeno cambiato un dato

                    if (resultCode == RESULT_OK) {
                        //ho cambiato almeno un dato nella pagina parametri
                        intent.putExtra("operazione", "Saving....");
                        intent.putExtra("Chiamante", "T1_L");
                        intent.putExtra("File_path", Values.File_XML_path_L);
                        startActivityForResult(intent, RESULT_PAGE_LOAD_UDF_L_T1);
                    }
                    break;
                case PAGE_UDF_T2_DX:
                    // preparo intent databack che verrà passato al onActivityResult quando tornerò alla pagina mainactivity
                    // nella Mainactivity ricaricherò XML relativo che ha subito la modifica
                    databack_text = databack_text + "PAGE_UDF_T2_DX";
                    databack.setData(Uri.parse(databack_text));
                    setResult(RESULT_OK, databack);

                    //invio file modificato al CN se ho almeno cambiato un dato

                    if (resultCode == RESULT_OK) {
                        //ho cambiato almeno un dato nella pagina parametri
                        intent.putExtra("operazione", "Saving....");
                        intent.putExtra("Chiamante", "T2_R");
                        intent.putExtra("File_path", Values.File_XML_path_T2_R);
                        startActivityForResult(intent, RESULT_PAGE_LOAD_UDF_R_T2);
                    }
                    break;
                case PAGE_UDF_T2_SX:
                    // preparo intent databack che verrà passato al onActivityResult quando tornerò alla pagina mainactivity
                    // nella Mainactivity ricaricherò XML relativo che ha subito la modifica
                    databack_text = databack_text + "PAGE_UDF_T2_SX";
                    databack.setData(Uri.parse(databack_text));
                    setResult(RESULT_OK, databack);

                    //invio file modificato al CN se ho almeno cambiato un dato

                    if (resultCode == RESULT_OK) {
                        //ho cambiato almeno un dato nella pagina parametri
                        intent.putExtra("operazione", "Saving....");
                        intent.putExtra("Chiamante", "T2_L");
                        intent.putExtra("File_path", Values.File_XML_path_T2_L);
                        startActivityForResult(intent, RESULT_PAGE_LOAD_UDF_L_T2);
                    }
                    break;
                default:
                    break;
            }
        } else {
            switch (requestCode) {
                case RESULT_PAGE_C1_PARAM:
                    Mci_Vb1002_Init_CAM.valore = 1.0d;          //faccio ricalcolare le camme rasafilo/tensione nel caso ho cambiato gli angoli
                    Mci_Vb1002_Init_CAM.write_flag = true;
                    break;

                case RESULT_PAGE_C2_PARAM:
                    Mci_Vb2002_Init_CAM.valore = 1.0d;          //faccio ricalcolare le camme rasafilo/tensione nel caso ho cambiato gli angoli
                    Mci_Vb2002_Init_CAM.write_flag = true;
                    break;

                case RESULT_PAGE_PARAM_PIEGATORE:
                    break;
                case RESULT_PAGE_PARAM_SCARICATORE:
                    break;
                case PAGE_DELTA:
                    break;
                case PAGE_Z_AGO:
                    break;
                case RESULT_PAGE_PARAM_TRASLATORE:
                    break;
                case PAGE_CREA_TASCA_QUOTE:
                    int pippo = 1;
                    break;

                default:
                    break;
            }
        }
    }
    /**
     * Se attivo FLAP mostro icona
     */
    private void GestionePattina() {
        if((Double) MultiCmd_Vb151EnableCarPattine.getValue()==1.0d){
            Button_pattina.setVisibility(View.VISIBLE);
        }else
            Button_pattina.setVisibility(View.GONE);
    }
    private void KillThread() {
        StopThread = true;
        if(isReceiverRegistered){
            unregisterReceiver(usbReceiver);
            isReceiverRegistered = false;// set it back to false.
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

        try {
            if (!Thread_Running)
                thread_Tool.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("JAM TAG", "End Toolpage Thread");
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
    public void copyFileToHmiAndUpgrade(final UsbFile sourceFile, final String Destination) {
        try {
            //Deprecato in android oreo, bisognerà cambiarlo da Android 8.0
            progress = new ProgressDialog(this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();

            int[] count = new int[2];
            AsyncTask<UsbFile, String, Boolean> task = new AsyncTask<UsbFile, String, Boolean>() {
                @Override
                protected Boolean doInBackground(UsbFile... usbFiles) {
                    try {
                        File f = new File(Destination);
                        if (f != null) f.delete();

                        String nome_file = sourceFile.getName();
                        int size_buffer = (int) sourceFile.getLength();

                        String[] memory = Utility.logHeap();
                        Log.d("tag", "copyFileToHmiAndUpgrade top");
                        Log.d("tag", memory[0]);
                        Log.d("tag", memory[1]);

                        int max_size_buffer = 8192;

                        int cicli = size_buffer / max_size_buffer;
                        int resto = size_buffer - (cicli * max_size_buffer);
                        int cnt = 0;
                        boolean append = true;
                        File outputFile = new File(Destination);
                        FileOutputStream fos = new FileOutputStream(outputFile, append);
                        FileChannel fileChannel = fos.getChannel();
                        byte[] array = new byte[size_buffer];
                        for (
                                int i = 0;
                                i < cicli; i++) {

                            ByteBuffer bbuf = ByteBuffer.allocate(max_size_buffer);
                            sourceFile.read(i * max_size_buffer, bbuf);
                            System.arraycopy(bbuf.array(), 0, array, i * max_size_buffer, bbuf.array().length);

                            memory = Utility.logHeap();
                            String pass = ""+i+"/"+cicli+" "+memory[0];
                            publishProgress(pass);

                        }
                        if (resto > 0) {
                            ByteBuffer bbuf = ByteBuffer.allocate(resto);
                            sourceFile.read(cicli * max_size_buffer, bbuf);
                            System.arraycopy(bbuf.array(), 0, array, cicli * max_size_buffer, bbuf.array().length);
                        }

                        try {

                            FileOutputStream outputStream = new FileOutputStream(f);
                            outputStream.write(array);
                            outputStream.close();

                        } catch (
                                Exception e) {

                            progress.dismiss();
                            return false;
                        }
                    } catch (
                            Exception e) {

                        progress.dismiss();
                        return false;
                    }
                    return true;
                }
                @Override
                protected void onProgressUpdate(String... values) {

                    progress.setMessage("Wait while loading..."+values[0]);

                }

                @Override
                protected void onPostExecute(Boolean result) { 
                    super.onPostExecute(result);
                    try {
                        KillThread();
                        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);

                        intent.setData(Uri.fromFile(new File(Destination)));

                        if (Build.VERSION.SDK_INT >= 24) {
                            Method met = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                            met.invoke(null);
                        }

                        startActivity(intent);
                    } catch (Exception e) {
                        //Toast.makeText(getApplicationContext(), "OutOfMemory, restart machine e try again", Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }

                    progress.dismiss();
                }

            };

            task.execute(sourceFile);
        } catch (Exception e) {
        }
    }
    class MyAndroidThread_Tool implements Runnable {
        Activity activity;

        public MyAndroidThread_Tool(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            while (true) {
                Thread_Running = true;

                try {
                    Thread.sleep((long) 300d);
                    if (StopThread) {
                        Thread_Running = false;
                        MultiCmd_Vn3804_pagina_touch.setValue(0.0d);
                        sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                }
                if (sl.IsConnected()) {
                    sl.Clear();
                    try{
                        if (first_cycle) {
                            first_cycle = false;
                        }

                        if (Read_Syslog) {
                            Read_Syslog = false;
                            ret_read_syslog = sl.FileDownload("c:\\cnc\\param\\par2kax.txt", "storage/emulated/0/JamData/par2kax.txt", null);
                            ret_read_syslog = sl.FileDownload("S:\\syslog.txt", "storage/emulated/0/JamData/syslog.txt", null);
                            Finito_lettura_Syslog = true;


                        }

                        MultiCmd_Vn3804_pagina_touch.setValue(1002.0d);
                        sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                        sl.ReadItems(mci_array_read_all);
                        if (sl.getReturnCode() != 0) {
                            rc_error = true;
                        }
                        if (!rc_error) {
                            Utility.GestiscoMci_Out_Toggle(sl, Mci_Sblocca_Ago);
                            Utility.GestiscoMci_Out_Toggle(sl, Mci_Vb4075_GiraAgoFaiSpolaC1);
                        }

                        Utility.ScrivoVbVnVq(sl, Mci_Vb1002_Init_CAM);
                        Utility.ScrivoVbVnVq(sl, Mci_Vb2002_Init_CAM);
                    } catch (Exception e) {

                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                            if (!chiamante.equals("Pagina_emergenza"))
                                Emergenza();
                            Report_to_usb(mc_stati_Report_to_usb);
                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_Sblocca_Ago, Button_Sgancio_ago, "ic_sblocca_ago_press", "ic_sblocca_ago");
                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_Vb4075_GiraAgoFaiSpolaC1, Button_Fai_spola, "ic_fai_spola_premuto", "ic_fai_spola");
                            } catch (Exception e) {

                            }
                            /**
                             * Se attivo FLAP mostro icona
                             */
                            GestionePattina();

                        }
                    });
                } else {
                    sl.Connect();
                }
            }
        }
    }
    private void Report_to_usb(int mcStatiReportToUsb) {
        switch (mcStatiReportToUsb){

            case 0:
                break;
            case 10:
                if (!isReceiverRegistered)
                    Registra_USB();
                TextView_status_Report_to_usb.setVisibility(View.VISIBLE);
                TextView_status_Report_to_usb.setText("Syslog reading....");

                Read_Syslog = true;
                mc_stati_Report_to_usb = 20;
                break;
            case 20:    //aspetto risultato della lettura del syslog
                if(Finito_lettura_Syslog) {
                    Finito_lettura_Syslog = false;
                    mc_stati_Report_to_usb = 30;
                }
                break;
            case 30:
                if(ret_read_syslog)
                    TextView_status_Report_to_usb.setText("Syslog read OK");

                else
                    TextView_status_Report_to_usb.setText("Syslog read error");

                mc_stati_Report_to_usb = 35;
                break;

                case 35:
                    TextView_status_Report_to_usb.setText("Writing USB....");
                    mc_stati_Report_to_usb = 40;
                    break;
            case 40:
                try {

                    root = currentFs.getRootDirectory();
                    UsbManager m = (UsbManager) getApplicationContext().getSystemService(USB_SERVICE);
                    HashMap<String, UsbDevice> devices = m.getDeviceList();
                    Collection<UsbDevice> ite = devices.values();
                    UsbDevice[] usbs = ite .toArray(new UsbDevice[]{});
                    // Check if at least 1 usb is connected
                    if (usbs.length > 0) {

                        TextView_status_Report_to_usb.setVisibility(View.GONE);
                        File rootFolder = android.os.Environment.getExternalStorageDirectory();
                        File dir = new File(rootFolder.getAbsolutePath() + "/JamData");
                        final String extensions = "deb";
                        File[] files = dir.listFiles(new FilenameFilter() {
                            public boolean accept(final File a_directory,
                                                  final String a_name) {
                                return a_name.endsWith(extensions);
                            }
                        });

                        // Copy every deb file
                        for (File file : files) {
                            Utility.copyFileToUsb(file, root);
                        }

                        // Copy the info_Jam file
                        try {
                            File file_info_Jam = new File(rootFolder.getAbsolutePath() + "/JamData/info_Jam.txt");
                            Utility.copyFileToUsb(file_info_Jam, root);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // Copy the MachineLog file
                        try {
                            File file_MachineLog = new File(rootFolder.getAbsolutePath() + "/JamData/MachineLog.txt");
                            Utility.copyFileToUsb(file_MachineLog, root);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        // Copy the syslog file
                        try {
                            File file_syslog = new File(rootFolder.getAbsolutePath() + "/JamData/syslog.txt");

                            Utility.copyFileToUsb(file_syslog, root);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // Copy the par2kax file
                        try {
                            File file_par2kax = new File(rootFolder.getAbsolutePath() + "/JamData/par2kax.txt");
                            Utility.copyFileToUsb(file_par2kax, root);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }



                        // File file_xml_path_R = new File(Values.File_XML_path_R);
                        try {
                            File file_xml_path_R = new File(Values.File_XML_path_R);

                            Utility.copyFileToUsb(file_xml_path_R, root);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // File file_xml_path_R udf;
                        try {
                            String path_file_udf = Values.File_XML_path_R;
                            path_file_udf = path_file_udf.replace(".xml",".udf");

                            File file_xml_path_R_udf = new File(path_file_udf);

                            Utility.copyFileToUsb(file_xml_path_R_udf, root);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(device_usb != null)
                           device_usb.close(); //chiudo la chiavetta USB

                       isReceiverRegistered = false;

                       // UsbManager.ACTION_USB_ACCESSORY_DETACHED;
                        //and UsbManager.ACTION_USB_ACCESSORY_ATTACHED)

                        Toast.makeText(getApplicationContext(), "Files successfully copied", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.UsbXmlUnmounted), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), getString(R.string.UsbXmlUnmounted), Toast.LENGTH_LONG).show();
                }

                mc_stati_Report_to_usb = 0;

                break;
            default:
                break;
        }
    }
    /**
     * Receiver for handle the usb events
     */
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (ACTION_USB_DEVICE_ATTACHED.equals(action)) {

                UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(getApplicationContext());
                if (devices.length > 0) {
                    device_usb = devices[0];

                    PendingIntent permissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
                    IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                    registerReceiver(usbReceiver, filter);
                    UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                    manager.requestPermission(device_usb.getUsbDevice(), permissionIntent);

                    device_prova = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                }
            }

            if (ACTION_MEDIA_MOUNTED.equals(action)) {
            }

            if (ACTION_USB_DEVICE_DETACHED.equals(action)) {
                try {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1);
                    adapter.notifyDataSetChanged();
                    Toggle_Button.Disabilita_Imagebutton(Button_upgrade_hmi, "ic_upgrade_disable", getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null || device_prova != null) {
                            try {
                                device_usb.init();

                                // Only uses the first partition on the device
                                 currentFs = device_usb.getPartitions().get(0).getFileSystem();

                                Log.d("TAG", "Capacity: " + currentFs.getCapacity());
                                Log.d("TAG", "Occupied Space: " + currentFs.getOccupiedSpace());
                                Log.d("TAG", "Free Space: " + currentFs.getFreeSpace());
                                Log.d("TAG", "Chunk size: " + currentFs.getChunkSize());
                                root = currentFs.getRootDirectory();

                                UsbFile[] files = root.listFiles();



                                Toggle_Button.Abilita_Imagebutton(Button_upgrade_hmi, "ic_upgrade", getApplicationContext());
                                isReceiverRegistered = true;

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Log.d("TAG", "permission denied for device " + device);
                    }
                }
            }
        }
    };


}
