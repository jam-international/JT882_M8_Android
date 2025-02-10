package com.jam_int.jt882_m8;

import static android.content.Intent.ACTION_MEDIA_MOUNTED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import communication.MSysFileInfo;
import communication.MultiCmdItem;
import communication.Protocol;
import communication.ShoppingList;
import me.jahnen.libaums.core.UsbMassStorageDevice;
import me.jahnen.libaums.core.fs.FileSystem;
import me.jahnen.libaums.core.fs.UsbFile;

public class Upgrade_activity extends Activity {

    /**
     * ShoppingList for communicate with PLC
     */
    ShoppingList sl;
    /**
     * UI components
     */
    ListView listview;
    ProgressBar Progress_Bar;
    TextView TextView_barra_sotto, TextView_intestazione;
    CheckBox CheckBox_userdata, CheckBox_fw, CheckBox_param, CheckBox_plc, CheckBox_Prog, CheckBox_Sys, CheckBox_Var;
    ImageButton ImageUsb, ImagePlay, ImageStop, Button_Exit;
    Thread_LoopEmergenza thread_LoopEmergenza;



    /**
     * USB
     */
    static UsbFile root;
    UsbMassStorageDevice device_usb;
    FileSystem currentFs;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    ArrayAdapter<String> adapter_upgrade;
    ArrayList<ArrayList<String>> list_varibili;
    String Chiamante = "";
    Boolean Stop_DoinBackground = false;
    ArrayList<File> File_list_usedata_da_inviare, File_list_fw_da_inviare, File_list_param_da_inviare, File_list_plc_da_inviare, File_list_prog_da_inviare, File_list_sys_da_inviare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);


        thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
        thread_LoopEmergenza.thread_LoopEmergenza_Start(this);

        // Setup ShoppingList
        sl = SocketHandler.getSocket();

        listview = findViewById(R.id.ListView_upgrade);

        Progress_Bar = findViewById(R.id.progressBar);

        TextView_barra_sotto = findViewById(R.id.textView_barra_sotto);
        TextView_intestazione = findViewById(R.id.textView_intestazione);

        ImageUsb = findViewById(R.id.imageUsb);
        ImagePlay = findViewById(R.id.imagePlay);

        CheckBox_userdata = (CheckBox) findViewById(R.id.checkBox_userdata);
        CheckBox_fw = (CheckBox) findViewById(R.id.checkBox_fw);
        CheckBox_param = (CheckBox) findViewById(R.id.checkBox_param);
        CheckBox_plc = (CheckBox) findViewById(R.id.checkBox_plc);
        CheckBox_Prog = (CheckBox) findViewById(R.id.checkBox_Prog);
        CheckBox_Sys = (CheckBox) findViewById(R.id.checkBox_Sys);
        CheckBox_Var = (CheckBox) findViewById(R.id.checkBox_Var);

        CheckBox_userdata.setClickable(false);
        CheckBox_fw.setClickable(false);
        CheckBox_param.setClickable(false);
        CheckBox_plc.setClickable(false);
        CheckBox_Prog.setClickable(false);
        CheckBox_Sys.setClickable(false);
        CheckBox_Var.setClickable(false);

        Button_Exit = findViewById(R.id.imageButton_exit);

        Progress_Bar.setMax(100);
        Progress_Bar.getProgressDrawable().setColorFilter(
                Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);

        TextView_barra_sotto.setText("Usb not insert");

        ImagePlay.setVisibility(View.GONE);
        ImageStop = findViewById(R.id.imageStop);
        ImageStop.setVisibility(View.GONE);

        PreparaHmiFolder();

        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        Chiamante = (String) b.get("chiamante_stringa");
        switch (Chiamante) {
            case "Upgrade":
                TextView_intestazione.setText("Upgrade software from USB memory");
                break;
            case "Backup_su_hmi":
                TextView_intestazione.setText("Backup software to HMI memory");
                break;
            case "Backup_su_usb":
                TextView_intestazione.setText("Backup software to USB memory");
                break;
            case "Restore_da_hmi":
                TextView_intestazione.setText("Restore software from HMI memory");
                break;
            case "Restore_da_usb":
                TextView_intestazione.setText("Restore software from USB memory");
                break;
            default:
                break;
        }

        if (GetUSBConnectionStatus()) {
            UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(getApplicationContext());
            if (devices.length > 0) {
                device_usb = devices[0];

                PendingIntent permissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                registerReceiver(usbReceiver, filter);
                UsbManager manager = (UsbManager) getSystemService(getApplicationContext().USB_SERVICE);
                manager.requestPermission(device_usb.getUsbDevice(), permissionIntent);
            } else {
                TextView_barra_sotto.setText("Insert USB!");
            }
        } else {
            TextView_barra_sotto.setText("Insert USB!");
        }

        // Register usb events
        IntentFilter filter_attached = new IntentFilter(ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(usbReceiver, filter_attached);
        IntentFilter filter_mounted = new IntentFilter(ACTION_MEDIA_MOUNTED);
        registerReceiver(usbReceiver, filter_mounted);
        IntentFilter filter_permission = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter_permission);
        IntentFilter filter_detached = new IntentFilter(ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbReceiver, filter_detached);

        switch (Chiamante) {
            case "Upgrade":
                break;
            case "Backup_su_hmi":
                String HMI_Folder_Backup = "storage/emulated/0/JamData/Backup/";    //senza data

                Utility.Crea_cartella(HMI_Folder_Backup);
                Utility.Crea_cartella(HMI_Folder_Backup + "/plc");
                Utility.Crea_cartella(HMI_Folder_Backup + "/userdata");
                Utility.Crea_cartella(HMI_Folder_Backup + "/param");
                Utility.Crea_cartella(HMI_Folder_Backup + "/prog");
                Utility.Crea_cartella(HMI_Folder_Backup + "/sys");
                break;
            case "Backup_su_usb":
                break;
            case "Restore_da_hmi":
                break;
            case "Restore_da_usb":
                break;
            default:
                break;
        }
    }

    /**
     * Button for exit from the activity
     *
     * @param view
     */
    public void onclick_button_Exit(View view) {
        unregisterReceiver(usbReceiver);
       // LocalBroadcastManager.getInstance(this).unregisterReceiver(usbReceiver);
     //   Intent intent = new Intent();
     //   setResult(RESULT_OK, intent);

        this.finish();
    }

    /**
     * Button for stop the upgrade
     *
     * @param view
     */
    public void onclick_stop(View view) {
        Stop_DoinBackground = true;
        ImageStop.setVisibility(View.GONE);
        ImagePlay.setVisibility(View.GONE);

        Button_Exit.setVisibility(View.VISIBLE);
    }
    /**
     * Button for open the key dialog and insert a password for unlock all buttons
     *
     * @param view
     */
    public void on_click_password(View view) {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("KeyDialog_password_ret"));
        KeyDialog.Lancia_KeyDialogo(null, Upgrade_activity.this, null, 99999d, 0d, false, false, 0d, true, "KeyDialog_password_ret", false,"");
    }
    /**
     * Button for start the upgrade
     *
     * @param view
     */
    public void onclick_play(View view) {
        ImagePlay.setImageResource(R.drawable.ic_button_play_press);
        ImageStop.setVisibility(View.VISIBLE);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        try {
            thread_LoopEmergenza.KillThread();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Chiamante.equals("Upgrade")) {
            ArrayList<File> File_list_da_inviare = new ArrayList<>();

            if (CheckBox_userdata.isChecked()) {
                for (File file : File_list_usedata_da_inviare) {
                    File_list_da_inviare.add(file); //aggiungo alla lista dei file da inviare il file
                }
            }

            if (CheckBox_fw.isChecked()) {
                for (File file : File_list_fw_da_inviare) {
                    File_list_da_inviare.add(file); //aggiungo alla lista dei file da inviare il file
                }
            }

            if (CheckBox_param.isChecked()) {
                for (File file : File_list_param_da_inviare) {
                    File_list_da_inviare.add(file); //aggiungo alla lista dei file da inviare il file
                }
            }

            if (CheckBox_plc.isChecked()) {
                for (File file : File_list_plc_da_inviare) {
                    File_list_da_inviare.add(file); //aggiungo alla lista dei file da inviare il file
                }
            }

            if (CheckBox_Prog.isChecked()) {
                for (File file : File_list_prog_da_inviare) {
                    File_list_da_inviare.add(file); //aggiungo alla lista dei file da inviare il file
                }
            }

            if (CheckBox_Sys.isChecked()) {
                for (File file : File_list_sys_da_inviare) {
                    File_list_da_inviare.add(file); //aggiungo alla lista dei file da inviare il file
                }
            }

            if (File_list_da_inviare.size() > 0 || CheckBox_Var.isChecked()) {
                if (sl.IsConnected()) {
                    TextView_barra_sotto.setText("Downloading..............Please wait");
                    new Scrivi_listafiles_dentro_CN(Upgrade_activity.this).execute(File_list_da_inviare);
                }
            } else {
                Toast.makeText(getApplicationContext(), "there are no files to download", Toast.LENGTH_SHORT).show();
                ImageStop.setVisibility(View.GONE);
                ImagePlay.setImageResource(R.drawable.ic_button_play);
            }
        }
    }
    /**
     * Receiver for handle the unlock of all the buttons
     */
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            String val = intent.getStringExtra("ret_valore");


            if ( val.equals("666")) {

                CheckBox_userdata.setClickable(true);
                CheckBox_fw.setClickable(true);
                CheckBox_param.setClickable(true);
                CheckBox_plc.setClickable(true);
                CheckBox_Prog.setClickable(true);
                CheckBox_Sys.setClickable(true);
                CheckBox_Var.setClickable(true);

            }
            else{

                    Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();

            }

        }
    };
    /**
     * Function for create all the folders for upgrade on the HMI and delete old files
     */
    private void PreparaHmiFolder() {
        File HmiRoot = android.os.Environment.getExternalStorageDirectory();
        File Folder = new File(HmiRoot.getAbsolutePath() + "/Upgrade");

        if (!Folder.exists()) {
            Folder.mkdir();
        }
        //userdata
        Folder = new File(HmiRoot.getAbsolutePath() + "/Upgrade/userdata");
        if (!Folder.exists()) {
            Folder.mkdir();
        } else {
            for (File file : Folder.listFiles())
                file.delete();
        }
        //fw
        Folder = new File(HmiRoot.getAbsolutePath() + "/Upgrade/fw");
        if (!Folder.exists()) {
            Folder.mkdir();
        } else {
            for (File file : Folder.listFiles())
                file.delete();
        }
        //param
        Folder = new File(HmiRoot.getAbsolutePath() + "/Upgrade/param");
        if (!Folder.exists()) {
            Folder.mkdir();
        } else {
            for (File file : Folder.listFiles())
                file.delete();
        }
        //plc
        Folder = new File(HmiRoot.getAbsolutePath() + "/Upgrade/plc");
        if (!Folder.exists()) {
            Folder.mkdir();
        } else {
            for (File file : Folder.listFiles())
                file.delete();
        }
        //prog
        Folder = new File(HmiRoot.getAbsolutePath() + "/Upgrade/prog");
        if (!Folder.exists()) {
            Folder.mkdir();
        } else {
            for (File file : Folder.listFiles())
                file.delete();
        }
        //sys
        Folder = new File(HmiRoot.getAbsolutePath() + "/Upgrade/sys");
        if (!Folder.exists()) {
            Folder.mkdir();
        } else {
            for (File file : Folder.listFiles())
                file.delete();
        }
        //variabili
        Folder = new File(HmiRoot.getAbsolutePath() + "/Upgrade/Variabili");
        if (!Folder.exists()) {
            Folder.mkdir();
        } else {
            for (File file : Folder.listFiles())
                file.delete();
        }

        String HMI_Folder_Backup = "storage/emulated/0/JamData/Backup/";    //senza data

        Utility.Crea_cartella(HMI_Folder_Backup);
        Utility.Crea_cartella(HMI_Folder_Backup + "/plc");
        Utility.Crea_cartella(HMI_Folder_Backup + "/userdata");
        Utility.Crea_cartella(HMI_Folder_Backup + "/param");
        Utility.Crea_cartella(HMI_Folder_Backup + "/prog");
        Utility.Crea_cartella(HMI_Folder_Backup + "/sys");
        Utility.Crea_cartella(HMI_Folder_Backup + "/Variabili");
    }

    /**
     * Function for check if at least 1 usb is attached
     *
     * @return
     */
    private boolean GetUSBConnectionStatus() {
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(getApplicationContext());
        return devices.length > 0;
    }

    /**
     * Function for copy the upgrade folder on the usb to the HMI
     *
     * @throws IOException
     */
    private void Leggi_USB() throws IOException {
        root = currentFs.getRootDirectory();
        UsbFile pathFolder_upgrade = null;
        UsbFile pathFolder_userdata = null;
        UsbFile pathFolder_fw = null;
        UsbFile pathFolder_param = null;
        UsbFile pathFolder_plc = null;
        UsbFile pathFolder_prog = null;
        UsbFile pathFolder_sys = null;
        UsbFile file_variabili = null;
        UsbFile[] files_userdata = null;
        UsbFile[] files_fw = null;
        UsbFile[] files_param = null;
        UsbFile[] files_plc = null;
        UsbFile[] files_prog = null;
        UsbFile[] files_sys = null;
        UsbFile[] files = root.listFiles();
        for (UsbFile file : files) {
            if (file.isDirectory()) {
                if (file.getName().contains("Upgrade"))
                    pathFolder_upgrade = file;
            }
        }

        if (pathFolder_upgrade != null) {
            UsbFile[] files1 = pathFolder_upgrade.listFiles();
            for (UsbFile file : files1) {
                if (file.isDirectory()) {
                    if (file.getName().equals("userdata"))
                        pathFolder_userdata = file;
                    if (file.getName().equals("fw"))
                        pathFolder_fw = file;
                    if (file.getName().equals("param"))
                        pathFolder_param = file;
                    if (file.getName().equals("plc"))
                        pathFolder_plc = file;
                    if (file.getName().equals("prog"))
                        pathFolder_prog = file;
                    if (file.getName().equals("sys"))
                        pathFolder_sys = file;
                } else {
                    if (file.getName().equals("VarDump.TXT"))
                        file_variabili = file;
                }
            }
            TextView_barra_sotto.setText("Ready to start");
        } else
            Toast.makeText(getApplicationContext(), "Upgrade folder is missing", Toast.LENGTH_SHORT).show();

        File_list_usedata_da_inviare = new ArrayList<>();
        File_list_fw_da_inviare = new ArrayList<>();
        File_list_param_da_inviare = new ArrayList<>();
        File_list_plc_da_inviare = new ArrayList<>();
        File_list_prog_da_inviare = new ArrayList<>();
        File_list_sys_da_inviare = new ArrayList<>();

        File Hmiroot = android.os.Environment.getExternalStorageDirectory();

        //userdata
        if (pathFolder_userdata != null) {
            files_userdata = pathFolder_userdata.listFiles();
            if (files_userdata.length > 0) {

                CheckBox_userdata.setChecked(true);
                for (UsbFile file : files_userdata) {
                    File file_hmi = new File(Hmiroot.getAbsolutePath() + "/Upgrade/userdata/" + file.getName());      //creo file su hmi
                    boolean result = Utility.copyFileToHMI(file, file_hmi, currentFs);   //copio file da usb a hmi
                    if (result)
                        File_list_usedata_da_inviare.add(file_hmi); //aggiungo alla lista dei file da inviare il file
                }
            } else
                CheckBox_userdata.setChecked(false);
        }
        //fw
        if (pathFolder_fw != null) {
            files_fw = pathFolder_fw.listFiles();
            if (files_fw.length > 0) {
                CheckBox_fw.setChecked(true);
                for (UsbFile file : files_fw) {
                    File file_hmi = new File(Hmiroot.getAbsolutePath() + "/Upgrade/fw/" + file.getName());      //creo file su hmi
                    boolean result = Utility.copyFileToHMI(file, file_hmi, currentFs);   //copio file da usb a hmi
                    if (result)
                        File_list_fw_da_inviare.add(file_hmi); //aggiungo alla lista dei file da inviare il file
                }
            } else
                CheckBox_fw.setChecked(false);
        }
        //param
        if (pathFolder_param != null) {
            files_param = pathFolder_param.listFiles();
            if (files_param.length > 0) {
                CheckBox_param.setChecked(true);
                for (UsbFile file : files_param) {
                    File file_hmi = new File(Hmiroot.getAbsolutePath() + "/Upgrade/param/" + file.getName());      //creo file su hmi
                    boolean result = Utility.copyFileToHMI(file, file_hmi, currentFs);   //copio file da usb a hmi
                    if (result)
                        File_list_param_da_inviare.add(file_hmi); //aggiungo alla lista dei file da inviare il file
                }
            } else
                CheckBox_param.setChecked(false);
        }
        //plc
        if (pathFolder_plc != null) {
            files_plc = pathFolder_plc.listFiles();
            if (files_plc.length > 0) {
                CheckBox_plc.setChecked(true);
                for (UsbFile file : files_plc) {
                    File file_hmi = new File(Hmiroot.getAbsolutePath() + "/Upgrade/plc/" + file.getName());      //creo file su hmi
                  //  boolean result = Utility.copyFileToHMI(file, file_hmi, currentFs);   //copio file da usb a hmi
                    boolean result = Utility.copyFileToHMI(file, file_hmi, currentFs);   //copio file da usb a hmi

                    if (result)
                        File_list_plc_da_inviare.add(file_hmi); //aggiungo alla lista dei file da inviare il file
                }
            } else
                CheckBox_plc.setChecked(false);
        }

        //prog
        if (pathFolder_prog != null) {
            files_prog = pathFolder_prog.listFiles();
            if (files_prog.length > 0) {
                CheckBox_Prog.setChecked(true);
                for (UsbFile file : files_prog) {
                    File file_hmi = new File(Hmiroot.getAbsolutePath() + "/Upgrade/prog/" + file.getName());      //creo file su hmi
                    boolean result = Utility.copyFileToHMI(file, file_hmi, currentFs);   //copio file da usb a hmi
                    if (result)
                        File_list_prog_da_inviare.add(file_hmi); //aggiungo alla lista dei file da inviare il file
                }
            } else
                CheckBox_Prog.setChecked(false);
        }
        //sys
        if (pathFolder_sys != null) {
            files_sys = pathFolder_sys.listFiles();
            if (files_sys.length > 0) {
                CheckBox_Sys.setChecked(true);
                for (UsbFile file : files_sys) {
                    File file_hmi = new File(Hmiroot.getAbsolutePath() + "/Upgrade/sys/" + file.getName());      //creo file su hmi
                    boolean result = Utility.copyFileToHMI(file, file_hmi, currentFs);   //copio file da usb a hmi
                    if (result)
                        File_list_sys_da_inviare.add(file_hmi); //aggiungo alla lista dei file da inviare il file
                }
            } else
                CheckBox_Sys.setChecked(false);
        }
        //variabili
        if (file_variabili != null) {
            CheckBox_Var.setChecked(true);
            File file_var_hmi = new File(Hmiroot.getAbsolutePath() + "/Upgrade/Variabili/" + file_variabili.getName());      //creo file su hmi
            boolean result = Utility.copyFileToHMI(file_variabili, file_var_hmi, currentFs);   //copio file da usb a hmi
            if (result) {

                list_varibili = new ArrayList<ArrayList<String>>();
                list_varibili = getListaVaribiliDaUSB(file_var_hmi);
            }
        } else
            CheckBox_Var.setChecked(false);
    }

    /**
     * Function for get the var list from the file
     *
     * @param file_var_hmi
     * @return
     * @throws IOException
     */
    private ArrayList<ArrayList<String>> getListaVaribiliDaUSB(File file_var_hmi) throws IOException {
        ArrayList<ArrayList<String>> ret_list_varibili = new ArrayList<ArrayList<String>>();

        BufferedReader br = null;
        String value = "VB_1";
        if (file_var_hmi.exists()) {
            br = new BufferedReader(new FileReader(file_var_hmi.getAbsolutePath()));
            value = br.readLine();

            while (value != null) {
                int carattere = value.indexOf("=");

                String variabile, tipo, id, valore;
                if (carattere != -1) {

                    variabile = value.substring(0, carattere);  //es: VB125
                    variabile = variabile.replaceAll("\\s+", "");      //elimino spazi
                    tipo = variabile.substring(0, 2);  //ottengo solo VB
                    id = variabile.substring(2, variabile.length());  //ottengo solo 125

                    valore = value.substring(carattere + 1);
                    valore = valore.replaceAll("\\s+", "");      //elimino spazi

                    ArrayList<String> var = new ArrayList<String>();
                    var.add(tipo);
                    var.add(id);
                    var.add(valore);
                    ret_list_varibili.add(var);
                }

                if (br != null) value = br.readLine();
                else value = null;
            }
        }
        return ret_list_varibili;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!thread_LoopEmergenza.getThreadStatus()){
            thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
            thread_LoopEmergenza.thread_LoopEmergenza_Start(this);
            Log.d("JAM TAG", "ResultActivity");

        }
    }

    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        try {
            thread_LoopEmergenza.KillThread();
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
        try {
            unregisterReceiver(usbReceiver);
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

                // Code below is to handle presses of Volume up or Volume down.
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
    }    /**
     * Receiver for the Usb
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
                }
            }

            if (ACTION_MEDIA_MOUNTED.equals(action)) {
            }

            if (ACTION_USB_DEVICE_DETACHED.equals(action)) {
                try {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Upgrade_activity.this, android.R.layout.simple_list_item_1);
                    adapter.notifyDataSetChanged();

                    ImageUsb.setImageResource(R.drawable.ic_usb_x);
                    ImagePlay.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            try {
                                device_usb.init();

                                // Only uses the first partition on the device
                                currentFs = device_usb.getPartitions().get(0).getFileSystem();
                                Log.d("TAG", "Capacity: " + currentFs.getCapacity());
                                Log.d("TAG", "Occupied Space: " + currentFs.getOccupiedSpace());
                                Log.d("TAG", "Free Space: " + currentFs.getFreeSpace());
                                Log.d("TAG", "Chunk size: " + currentFs.getChunkSize());

                                ImageUsb.setImageResource(R.drawable.ic_usb_ok);
                                ImagePlay.setVisibility(View.VISIBLE);
                                if (Chiamante.equals("Upgrade")) {
                                    Leggi_USB();
                                }

                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), getString(R.string.ErroreUSB_FAT32), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                            //call method to set up device communication
                        }
                    } else {
                        Log.d("TAG", "permission denied for device " + device);
                    }
                }
            }
        }


    };

    class Scrivi_listafiles_dentro_CN extends AsyncTask<ArrayList<File>, ArrayList<String>, ArrayList<Integer>> {
        private final ProgressDialog dialog;
        ImageButton btn_Exit;


        public Scrivi_listafiles_dentro_CN(Context context) {
            this.dialog = new ProgressDialog(context);
            this.dialog.setTitle("Titel");
            this.dialog.setMessage("Message");
            btn_Exit = findViewById(R.id.imageButton_exit);
        }


        @Override
        protected ArrayList<Integer> doInBackground(ArrayList<File>... params) {
            boolean r = false;
            String File_path_da_inviare = "";
            String File_name = "";
            String CN_file_path = "";
            ArrayList<String> val_progress = new ArrayList<>();
            String Font_path_file = "";
            ArrayList<Integer> res = new ArrayList();
            Integer numero_file = 0;
            Integer numero_done = 0;
            Integer numero_errori = 0;
            Integer numero_file_totali = params[0].size();
            Integer cnt_visualizzazione = 0;
            Stop_DoinBackground = false;
          //  btn_Exit.setVisibility(View.GONE);

            // disabilito CN
            MultiCmdItem Disabilita_CN = sl.Add("Io", 1, MultiCmdItem.dtVB, 7808, MultiCmdItem.dpNONE);
            Disabilita_CN.setValue(0.0d);
            sl.WriteItem(Disabilita_CN);
            // fermo plc
            MultiCmdItem VN_SYS_RELOAD_PROG = sl.Add("Io", 1, MultiCmdItem.dtVN, 3821, MultiCmdItem.dpNONE);
            VN_SYS_RELOAD_PROG.setValue(129.0d);
            sl.WriteItem(VN_SYS_RELOAD_PROG);





            for (File file_item : params[0]) {

                if (Stop_DoinBackground) break;

                if (cnt_visualizzazione > 4) {
                    val_progress = new ArrayList<>();
                    cnt_visualizzazione = 0;
                } else cnt_visualizzazione++;

                Protocol.OnProgressListener pl = new Protocol.OnProgressListener() {
                    @Override
                    public void onProgressUpdate(int Completion) {

                        // Display Progress value (Completion of 100)
                        Progress_Bar.setProgress(Completion);

                    }
                };
                Boolean result_delete = false;
                File_path_da_inviare = file_item.getPath();
                File_name = file_item.getName();

                if (File_path_da_inviare.contains("plc")) {
                    CN_file_path = "C:\\cnc\\plc\\" + File_name;


                    MSysFileInfo fi = new MSysFileInfo();
                    String path_folder = "C:\\cnc\\plc\\*.*";
                    fi = sl.FileDir(path_folder, (byte) 0x20);//0x10 = FOLDER , 0X20=FILE
                    if (fi != null)    //se la cartella contiene almeno un file
                    {
                        result_delete = sl.FileDelete(CN_file_path);
                        val_progress.add(CN_file_path + " Erasing ....");
                        publishProgress(val_progress);
                    } else
                        result_delete = true;   //non c'è nessun file, lo metto true così mi permette comunque di scaricare il fw

                }
                if (File_path_da_inviare.contains("fw")) {
                    CN_file_path = "B:\\fw\\" + File_name;
                    try {

                        MSysFileInfo fi = new MSysFileInfo();
                        String path_folder = "B:\\fw\\*.*";
                        fi = sl.FileDir(path_folder, (byte) 0x20);//0x10 = FOLDER , 0X20=FILE
                        if (fi != null)    //se la cartella contiene almeno un file
                        {
                            result_delete = sl.FileDelete("B:\\fw\\" + fi.FName);
                        } else
                            result_delete = true;   //non c'è nessun file, lo metto true così mi permette comunque di scaricare il fw
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    publishProgress(val_progress);
                }


                if (File_path_da_inviare.contains("param")) {
                    //controllo se nella chiavetta c'è un file ecat da scrivere, se si sotto cancello quello nel CN per poi sostituirlo
                    boolean del_file_ecat = false;
                    for (File file : File_list_param_da_inviare) {
                        if(file.getName().contains("ecat")){
                            del_file_ecat = true;
                        }
                    }

                    if(del_file_ecat)
                    {
                        MSysFileInfo fi = new MSysFileInfo();
                        String path_folder = "C:\\cnc\\param\\*.*";
                        try {
                            fi = sl.FileDir(path_folder, (byte) 0x20);//0x10 = FOLDER , 0X20=FILE
                            if (fi != null)    //se la cartella contiene almeno un file
                            {

                                while (true) {
                                    fi = sl.FileDir();
                                    if (fi != null)    //se la cartella contiene almeno un file
                                    {
                                        if(fi.FName.contains("ecat") && fi.FName.contains("xml")) {
                                            //cancello il file ecat
                                            result_delete = sl.FileDelete("C:\\cnc\\param\\" + fi.FName);
                                            val_progress.add(CN_file_path + " Deleting ecat file");
                                            publishProgress(val_progress);
                                        }
                                    } else
                                        break;
                                }
                            }
                        } catch (Exception ex) {}

                    }

                    CN_file_path = "C:\\cnc\\param\\" + File_name;
                    result_delete = true;
                }
                if (File_path_da_inviare.contains("sys")) {
                    CN_file_path = "C:\\cnc\\sys\\" + File_name;
                    result_delete = true;
                }
                if (File_path_da_inviare.contains("userdata")) {
                    CN_file_path = "C:\\cnc\\userdata\\" + File_name;
                    result_delete = true;
                }
                if (File_path_da_inviare.contains("prog")) {
                    CN_file_path = "C:\\cnc\\prog\\" + File_name;
                    result_delete = true;
                }

                CN_file_path = CN_file_path.replace("/", "\\");

                val_progress.add(CN_file_path + " Downloading ....");
                publishProgress(val_progress);

                if (result_delete) {
                    numero_file++;
                    r = sl.FileUpload(CN_file_path, File_path_da_inviare, pl);
                }

                // val_progress.add(CN_file_path);
                if (r)
                    val_progress.add(".......OK" + "  " + numero_file + "/" + numero_file_totali);
                else {
                    numero_errori++;
                    val_progress.add(".......error");
                }
                publishProgress(val_progress);
            }

            ///invio variabili
            Integer cnt_var = 0;
            if (CheckBox_Var.isChecked()) {
                if(list_varibili.size()>0) {
                    for (ArrayList<String> var : list_varibili) {

                        if (var.size() == 3) {
                            String tipo = var.get(0);
                            String id = var.get(1);
                            String valore = var.get(2);
                            try {
                                Integer numero_variabile = Integer.parseInt(id);

                                switch (tipo) {
                                    case "VB":
                                        Double valore_int = Double.parseDouble(valore);
                                        MultiCmdItem Cmd = sl.Add("Io", 1, MultiCmdItem.dtVB, numero_variabile, MultiCmdItem.dpNONE);
                                        Cmd.setValue(valore_int);
                                        sl.WriteItem(Cmd);
                                        break;
                                    case "VN":
                                        valore_int = Double.parseDouble(valore);
                                        Cmd = sl.Add("Io", 1, MultiCmdItem.dtVN, numero_variabile, MultiCmdItem.dpNONE);
                                        Cmd.setValue(valore_int);
                                        sl.WriteItem(Cmd);
                                        break;
                                    case "VQ":
                                        valore_int = Double.parseDouble(valore);
                                        Cmd = sl.Add("Io", 1, MultiCmdItem.dtVQ, numero_variabile, MultiCmdItem.dpNONE);
                                        Cmd.setValue(valore_int);
                                        sl.WriteItem(Cmd);
                                        break;
                                    case "VA":
                                        Cmd = sl.Add("Io", 1, MultiCmdItem.dtVA, numero_variabile, MultiCmdItem.dpNONE);

                                        String v = valore.replaceAll("&h", "");      //elimino &h
                                        StringBuilder output = new StringBuilder();
                                        for (int i = 0; i < v.length(); i += 2) {
                                            String str = v.substring(i, i + 2);
                                            output.append((char) Integer.parseInt(str, 16));
                                        }
                                        String vq = output.toString();  //serve per pulire bene la stringa

                                        Cmd.setValue(vq);
                                        sl.WriteItem(Cmd);
                                        break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                ArrayList<String> var1 = var;
                            }
                            cnt_var++;
                            val_progress = new ArrayList<>();//cancello listView
                            val_progress.add("Download variables " + cnt_var + "/" + list_varibili.size());
                            publishProgress(val_progress);
                        }
                    }
                }
            }


/*
            // faccio partire plc
            VN_SYS_RELOAD_PROG = sl.Add("Io", 1, MultiCmdItem.dtVN, 3821, MultiCmdItem.dpNONE);
            VN_SYS_RELOAD_PROG.setValue(128.0d);
            sl.WriteItem(VN_SYS_RELOAD_PROG);
            // abilito CN
            Disabilita_CN = sl.Add("Io", 1, MultiCmdItem.dtVB, 7808, MultiCmdItem.dpNONE);
            Disabilita_CN.setValue(1.0d);
            sl.WriteItem(Disabilita_CN);
*/


            res.add(numero_file);
            res.add(numero_done);
            res.add(numero_errori);

            return res;
        }

        protected void onPostExecute(ArrayList<Integer> ret) {
            super.onPostExecute(ret);
            this.dialog.dismiss();

            Integer numero_file = 0;
            Integer numero_done = 0;
            Integer numero_errori = 0;
            if (ret.size() > 1) {
                numero_file = ret.get(0);
                numero_done = ret.get(1);
                numero_errori = ret.get(2);
            }

            btn_Exit.setVisibility(View.VISIBLE);
            ImagePlay.setVisibility(View.GONE);
            ImageStop.setVisibility(View.GONE);

            TextView_barra_sotto.setText("End: Total files: " + numero_file + ", Errors: " + numero_errori);

        }

        @Override
        protected void onProgressUpdate(ArrayList<String>... values) {
            ArrayList<String> val = values[0];
            ArrayList<String> myObject = new ArrayList<String>(val);    //facendo la copia evito crash se nel frattempo qualcuno cambia i dati

            adapter_upgrade = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    myObject);
            adapter_upgrade.notifyDataSetChanged();

            listview.setAdapter(adapter_upgrade);
        }
    }


}
