package com.jam_int.jt882_m8;
//2.4 risolto problema che si rallentava il touch
//2.5 ora salva i valori della tensione anche della seconda testa.
//2.6 se copio un file ecat da chiavetta prima cancella quelli esistenti
//2.7 sistemati valori default Nipper
//2.8 messi alcuni try catch
//2.9 aggiunti allarmi Driver X2 Y2
//3.1 se la comunicazione da errore chiudo e riapro il socket
//3.2 riapro il socket anche in ModificaProgrammi.
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.validation.Validator;

import communication.MSysFileInfo;
import communication.MultiCmdItem;
import communication.ShoppingList;
import communication.SmartAlarm;
import communication.SmartAlarms;
import communication.VFKBook;

public class Emergency_page extends Activity {

    /**
     * On activity result indexes
     */
    final private static int RESULT_PAGE_TOOLS = 100;
    final private static int RESULT_PAGE_LOAD_UDF = 102;
    /**
     * Shopping list for communicate with PLC
     */
    ShoppingList sl;

    SocketTCP socketTCP;
    /**
     * Thread
     */
    boolean Thread_Running = false, StopThread = false, first_cycle = true,Leggi_Emergenze = false;
    Thread thread_emerg;
    /**
     * TODO This seems like a listener that write all the alarms inside the list but the list is never used so is useless
     * <p>
     * TODO This create leak too on ShoppingList.
     *  I think is created because the SL is always alive inside the SocketHandler so this will keep the smart alarms alive
     *  <p>
     *  I've found that SL already have alarms, why create them here?? removing them here will remove the leak
     */
    SmartAlarms.OnAlarmListener AlarmListener = null;
    ArrayList<SmartAlarm> ListaAllarmiCN = new ArrayList<SmartAlarm>();
    /**
     * Components
     */
    TextView TView_barra_bassa, TextView_allarmi, TextView_testo_PLC_ver, ver_softwareHMI, TextView_testo_Firmware, TextView_macchina, TextView_lingua, Allarm_textView,
            TextView_programma_in_esecuzione, TextView_riga_in_esecuzione, TextView_cnt_comunicazione;
    Button btn_connection_status, Button_verde, Btn_eth_operational,Button_load_T1,Button_load_T2,Btn_TCP_status;
    ImageView ImageView_battery;
    /**
     * PLC vars
     */
    MultiCmdItem[] mci_array_read_all,mci_array_read_882;
    MultiCmdItem mci_tasto_verde, mci_Vb7903_Reset_Ch1, mci_CH1_in_emergenza, mc1_Vb50_macchina_azzerata, Multicmd_vb4503_Cn_allarme, Multicmd_in_Pressostato,
            Multicmd_vb7013_ax1_home, Multicmd_vb7033_ax2_home, Multicmd_vb7053_ax3_home, Multicmd_vb7073_ax4_home, Multicmd_vb7093_ax5_home, MultiCmd_VA31_Ver_PLC,
            MultiCmd_livello_batteria, MultiCmd_Vn3910_udf_error, Multicmd_dtDB_prog_name,Multicmd_dtDB_prog_name_T2, MultiCmd_Vn198_num_prog_right, MultiCmd_Vn199_num_prog_left, MultiCmd_Vn2_allarmi_da_CN,
            MultiCmd_Vb7814_Eth_operational, MultiCmd_Debug14_prog_cn_in_esecuzione, MultiCmd_Debug8_riga_cn_in_esecuzione, Multicmd_i5_loader_up, Multicmd_i8_folder_back,
            Multicmd_i11_Lancia_back, MultiCmd_Vn4_Warning, Multicmd_Vb4807_PinzeAlteDopoPC, MultiCmd_ver_macchine,Multicmd_i23_interna_bassa,Multicmd_i24_esterna_alta,
            Multicmd_i31_motoreX_ready,Multicmd_i32_motoreY_ready,Multicmd_in_trasl_alto,Multicmd_in_interna_bassa,Multicmd_i47_C2_ReadyAsseX, Multicmd_i48_C2_ReadyAsseY,Multicmd_vb7193_ax10_home,
            Multicmd_vb7153_ax8_home,Multicmd_vb7173_ax9_home,Multicmd_vb7113_ax6_home,Multicmd_vb7133_ax7_home,Multicmd_i1_pulsanti_start,MultiCmd_Vn3804_pagina_touch,Multicmd_Vb98_mcInclinata;
    Mci_write Mci_write_dtDB_prog_name = new Mci_write(),Mci_write_dtDB_prog_name_T2 = new Mci_write(), Mci_write_Vn4_Warning = new Mci_write();
    List<String> list_allarmi = new ArrayList<>();
    List<String> list_allarmi_rec = new ArrayList<>();

    Boolean path_udf_presente = false;
    int mc_stati_riarmo = 0,mc_stati_riarmo_prec = -1, cnt_comunicazione = 0, alarm_CN_cnt = 0, mc_stati_visualizzazione_allarmi = 0,mc_stati_visualizzazione_allarmi_prec = -1,warning_debug = -1;
    String[] tab_names = new String[]{};
    Double warning_old = 0.0d;
    String info = "", str_allarmi = "", Machine_model = "", str_allarmi_udf = "", str_allarmi_old = "x", str_allarmi_print = "",str_allarmi_more = "";

    Animation anim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



     //   try {
     //       Machine_model = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "MachineModel", "null", null, null, "Machine_model", getApplicationContext());
     //   } catch (IOException e) {
     //       Machine_model="";
     //   }

     //   if( Machine_model.equals("JT882M"))
            setContentView(R.layout.activity_emergenza882);
     //   else
     //       setContentView(R.layout.activity_emergenza);


        // This create Leaks on profiler because will be attached to the page so the garbage collector can't release this page memory
        // For avoid this i moved the ExceptionHandler class in a Utility file (much better than here) and start it from here so this java page can be released
        // This with the flag clear top could resolve the slow app
        // (It was creating Emergency pages on top of other Emergency pages and start this thread that creates leaks, after N Emergency pages the app started to slow down)

        //scrive in un file "*.stacktrace" eventuale cause di crash
        // THIS WAS ALWAYS TRUE BECAUSE THE CLASS WAS IN THIS PAGE BUT THE INSTANCEOF CLASS WAS IN MainActivity. SAME ERROR WAS IN Modifica_programma PAGE
        // THE RESULT WAS THAT EVERY EMERGENCY BUTTON THE APP STARTED A NEW Emergency PAGE WITH A NEW HANDLER THREAD THAT REPLACE THE OLD ONE
        // (But i don't know if the old one was stopped)

        // if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof MainActivity.CustomExceptionHandler)) {
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof Utility.CustomExceptionHandler)) {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/JamData");

            Thread.setDefaultUncaughtExceptionHandler(new Utility.CustomExceptionHandler(dir.getAbsolutePath(), "null"));
        }

        // Request permissions for read and write files
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //non fa apparire la tastiera

        if (Btn_eth_operational == null) {
            try {
                Init_Activity_Emergenza();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Function that contains all the FTP code
     */
    private void Init_FTP() {
        /******** FTP ****************
         try {

         FtpServerFactory serverFactory = new FtpServerFactory();
         ListenerFactory factory = new ListenerFactory();
         //factory.setServerAddress("127.0.0.1");

         // set the port of the listener
         factory.setPort(3232);
         factory.setIdleTimeout(3000);

         // replace the default listener
         serverFactory.addListener("default", factory.createListener());

         Log.d("FTPServer","Adding Users Now");
         PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
         File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/JamData/FTPUsers");
         dir.mkdir();
         File file = new File(dir.getPath() + "/users.properties");
         file.createNewFile();
         userManagerFactory.setFile(file);

         userManagerFactory.setPasswordEncryptor(new PasswordEncryptor() {//We store clear-text passwords in this example

         @Override public String encrypt(String password) {
         return password;
         }

         @Override public boolean matches(String passwordToCheck, String storedPassword) {
         return passwordToCheck.equals(storedPassword);
         }
         });

         BaseUser user1 = new BaseUser();
         user1.setName("cm32");
         user1.setPassword("cm32");
         user1.setHomeDirectory("/storage/emulated/0");
         List<Authority> authorities = new ArrayList<Authority>();
         authorities.add(new WritePermission());
         user1.setAuthorities(authorities);
         UserManager um = userManagerFactory.createUserManager();
         try {
         um.save(user1);//Save the user to the user list on the filesystem
         } catch (FtpException e1) {
         e1.printStackTrace();
         }

         serverFactory.setUserManager(um);


         Map<String, Ftplet> m = new HashMap<String, Ftplet>();
         m.put("miaFtplet", new Ftplet() {

         @Override public void init(FtpletContext ftpletContext) throws FtpException {
         System.out.println("init");
         //System.out.println("Thread #" + Thread.currentThread().getId());
         }

         @Override public void destroy() {
         System.out.println("destroy");
         //System.out.println("Thread #" + Thread.currentThread().getId());
         }

         @Override public FtpletResult beforeCommand(FtpSession session, FtpRequest request) throws FtpException, IOException {
         //System.out.println("beforeCommand " + session.getUserArgument() + " : " + session.toString() + " | " + request.getArgument() + " : " + request.getCommand() + " : " + request.getRequestLine());
         //System.out.println("Thread #" + Thread.currentThread().getId());

         //do something
         return FtpletResult.DEFAULT;//...or return accordingly
         }

         @Override public FtpletResult afterCommand(FtpSession session, FtpRequest request, FtpReply reply) throws FtpException, IOException {
         //System.out.println("afterCommand " + session.getUserArgument() + " : " + session.toString() + " | " + request.getArgument() + " : " + request.getCommand() + " : " + request.getRequestLine() + " | " + reply.getMessage() + " : " + reply.toString());
         //System.out.println("Thread #" + Thread.currentThread().getId());

         //do something
         return FtpletResult.DEFAULT;//...or return accordingly
         }

         @Override public FtpletResult onConnect(FtpSession session) throws FtpException, IOException {
         //System.out.println("onConnect " + session.getUserArgument() + " : " + session.toString());
         //System.out.println("Thread #" + Thread.currentThread().getId());

         //do something
         return FtpletResult.DEFAULT;//...or return accordingly
         }

         @Override public FtpletResult onDisconnect(FtpSession session) throws FtpException, IOException {
         //System.out.println("onDisconnect " + session.getUserArgument() + " : " + session.toString());
         //System.out.println("Thread #" + Thread.currentThread().getId());

         //do something
         return FtpletResult.DEFAULT;//...or return accordingly
         }
         });
         serverFactory.setFtplets(m);

         // start the server
         FtpServer server = serverFactory.createServer();

         Log.d("FTPServer","Server Starting " + factory.getPort());
         try {
         server.start();
         } catch (Exception e2) {
         e2.printStackTrace();
         }

         }catch (Exception e)
         {
         e.printStackTrace();
         }
         */
    }

    /**
     * Function for init the activity and the communication with the PLC
     */
    private void Init_Activity_Emergenza() throws IOException {


        TextView_allarmi = findViewById(R.id.textView_allarmi);
        TextView_testo_PLC_ver = findViewById(R.id.textView_testo_PLC_ver);
        TextView_programma_in_esecuzione = findViewById(R.id.textView_programma_in_esecuzione);
        TextView_riga_in_esecuzione = findViewById(R.id.textView_riga_fork);
        TextView_cnt_comunicazione = findViewById(R.id.textView_cnt_comunicazione);
        TextView_testo_Firmware = findViewById(R.id.textView_testo_Firmware);
        TextView_macchina = findViewById(R.id.textView_macchina);
        Allarm_textView = findViewById(R.id.allarm_textView);

        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
       // Allarm_textView.startAnimation(anim);

        ver_softwareHMI = findViewById(R.id.textView_testo_HMI_ver);

        btn_connection_status = findViewById(R.id.btn_connection_status);
        Button_verde = findViewById(R.id.button_verde);
        Btn_eth_operational = findViewById(R.id.btn_eth_operational);
        Button_load_T1 = findViewById(R.id.button_load_T1);
        Button_load_T1.setVisibility(View.GONE);
        Button_load_T2 = findViewById(R.id.button_load_T2);
        Button_load_T2.setVisibility(View.GONE);
        Btn_TCP_status = (Button) findViewById(R.id.btn_TCP_status);

        ImageView_battery = findViewById(R.id.imageView_battery);

        ImageView_battery.setImageResource(R.drawable.battery_full);
        ImageView_battery.setVisibility(View.GONE);

        // Load the warning array
        tab_names = getResources().getStringArray(R.array.warning_vn4);

        info = "Info device: " + android.os.Build.MODEL + " brand = " + android.os.Build.BRAND + " OS version = " + android.os.Build.VERSION.RELEASE + " SDK version = " + android.os.Build.VERSION.SDK_INT;

        AlarmListener = new SmartAlarms.OnAlarmListener() {
            @Override
            public void onAlarm(SmartAlarm al) {
                try {
                    ListaAllarmiCN.add(al);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // Get the current version of the HMI software
      //  int versionCode = com.android. BuildConfig.VERSION_CODE;
        ver_softwareHMI.setText(Values.HMI_softver);

        // Setup the ShoppingList
        try {
            sl = SocketHandler.getSocket();
            if (sl == null) {    //se è la prima accensione entro altrimenti se provengo da un'altra pagina non c'è bisogno di instanziare nnuovamente
                sl = new ShoppingList("192.168.0.92", 12001, 0.1d, 2d);
                sl.getAlarms().registerAlarmListener(AlarmListener);
                SocketHandler.setSocket(sl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        sl.Clear("Io");
        VFKBook.Load(this);     //serve per usare variabili MultiCmdItem.dtDB
        sl.setVFK(VFKBook.getVFK());    //serve per usare variabili MultiCmdItem.dtDB

        mci_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 21, MultiCmdItem.dpNONE);
        mci_Vb7903_Reset_Ch1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 7903, MultiCmdItem.dpNONE);
        mci_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
        mc1_Vb50_macchina_azzerata = sl.Add("Io", 1, MultiCmdItem.dtVB, 50, MultiCmdItem.dpNONE);
        Multicmd_vb4503_Cn_allarme = sl.Add("Io", 1, MultiCmdItem.dtVB, 4503, MultiCmdItem.dpNONE);

        Multicmd_vb7013_ax1_home = sl.Add("Io", 1, MultiCmdItem.dtVB, 7013, MultiCmdItem.dpNONE);
        Multicmd_vb7033_ax2_home = sl.Add("Io", 1, MultiCmdItem.dtVB, 7033, MultiCmdItem.dpNONE);
        Multicmd_vb7053_ax3_home = sl.Add("Io", 1, MultiCmdItem.dtVB, 7053, MultiCmdItem.dpNONE);
        Multicmd_vb7073_ax4_home = sl.Add("Io", 1, MultiCmdItem.dtVB, 7073, MultiCmdItem.dpNONE);
        Multicmd_vb7093_ax5_home = sl.Add("Io", 1, MultiCmdItem.dtVB, 7093, MultiCmdItem.dpNONE);


        Multicmd_i47_C2_ReadyAsseX = sl.Add("Io", 1, MultiCmdItem.dtDI, 47, MultiCmdItem.dpNONE);
        Multicmd_i48_C2_ReadyAsseY = sl.Add("Io", 1, MultiCmdItem.dtDI, 48, MultiCmdItem.dpNONE);
        Multicmd_vb7193_ax10_home = sl.Add("Io", 1, MultiCmdItem.dtVB, 7193, MultiCmdItem.dpNONE);
        Multicmd_vb7153_ax8_home = sl.Add("Io", 1, MultiCmdItem.dtVB, 7153, MultiCmdItem.dpNONE);
        Multicmd_vb7173_ax9_home = sl.Add("Io", 1, MultiCmdItem.dtVB, 7173, MultiCmdItem.dpNONE);
        Multicmd_vb7113_ax6_home = sl.Add("Io", 1, MultiCmdItem.dtVB, 7113, MultiCmdItem.dpNONE);
        Multicmd_vb7133_ax7_home = sl.Add("Io", 1, MultiCmdItem.dtVB, 7133, MultiCmdItem.dpNONE);
        MultiCmd_VA31_Ver_PLC = sl.Add("Io", 1, MultiCmdItem.dtVA, 31, MultiCmdItem.dpNONE);
        MultiCmd_livello_batteria = new MultiCmdItem(1, MultiCmdItem.dtGP, 6, MultiCmdItem.dpNONE, sl);
        MultiCmd_Vn3910_udf_error = sl.Add("Io", 1, MultiCmdItem.dtVN, 3910, MultiCmdItem.dpNONE);
        Multicmd_dtDB_prog_name = sl.Add("Io", 1, MultiCmdItem.dtDB, 30, MultiCmdItem.dpDB_MAIN1);
        Multicmd_dtDB_prog_name_T2 = sl.Add("Io", 1, MultiCmdItem.dtDB, 30, MultiCmdItem.dpDB_MAIN2);
        MultiCmd_Vn198_num_prog_right = sl.Add("Io", 1, MultiCmdItem.dtVN, 198, MultiCmdItem.dpNONE);    //JT862 JT882Master
        MultiCmd_Vn199_num_prog_left = sl.Add("Io", 1, MultiCmdItem.dtVN, 199, MultiCmdItem.dpNONE);
        MultiCmd_Vn2_allarmi_da_CN = sl.Add("Io", 1, MultiCmdItem.dtVN, 2, MultiCmdItem.dpNONE);
        MultiCmd_Vb7814_Eth_operational = sl.Add("Io", 1, MultiCmdItem.dtVB, 7814, MultiCmdItem.dpNONE);
        MultiCmd_Debug14_prog_cn_in_esecuzione = sl.Add("Io", 1, MultiCmdItem.dtDB, 14, MultiCmdItem.dpDB_MAIN1);
        MultiCmd_Debug8_riga_cn_in_esecuzione = sl.Add("Io", 1, MultiCmdItem.dtDB, 8, MultiCmdItem.dpDB_MAIN1);
        Multicmd_i5_loader_up = sl.Add("Io", 1, MultiCmdItem.dtDI, 5, MultiCmdItem.dpNONE);
        Multicmd_i8_folder_back = sl.Add("Io", 1, MultiCmdItem.dtDI, 8, MultiCmdItem.dpNONE);
        Multicmd_i11_Lancia_back = sl.Add("Io", 1, MultiCmdItem.dtDI, 11, MultiCmdItem.dpNONE);
        MultiCmd_Vn4_Warning = sl.Add("Io", 1, MultiCmdItem.dtVN, 4, MultiCmdItem.dpNONE);
        Multicmd_Vb4807_PinzeAlteDopoPC = sl.Add("Io", 1, MultiCmdItem.dtVB, 4807, MultiCmdItem.dpNONE);
        MultiCmd_ver_macchine = sl.Add("Io", 1, MultiCmdItem.dtVN, 320, MultiCmdItem.dpNONE);
        Multicmd_i23_interna_bassa= sl.Add("Io", 1, MultiCmdItem.dtDI, 23, MultiCmdItem.dpNONE);
        Multicmd_i24_esterna_alta= sl.Add("Io", 1, MultiCmdItem.dtDI, 24, MultiCmdItem.dpNONE);
        Multicmd_i31_motoreX_ready= sl.Add("Io", 1, MultiCmdItem.dtDI, 31, MultiCmdItem.dpNONE);
        Multicmd_i32_motoreY_ready= sl.Add("Io", 1, MultiCmdItem.dtDI, 32, MultiCmdItem.dpNONE);
        Multicmd_i1_pulsanti_start= sl.Add("Io", 1, MultiCmdItem.dtDI, 1, MultiCmdItem.dpNONE);
        MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);
        Multicmd_Vb98_mcInclinata = sl.Add("Io", 1, MultiCmdItem.dtVB, 98, MultiCmdItem.dpNONE);


        switch (Machine_model) {
            case "JT882M":
            default:
                Values.Machine_model = "JT882M";  //per la prima accensione
                Multicmd_in_Pressostato = sl.Add("Io", 1, MultiCmdItem.dtDI, 44, MultiCmdItem.dpNONE);
                Multicmd_in_trasl_alto = sl.Add("Io", 1, MultiCmdItem.dtDI, 42, MultiCmdItem.dpNONE);
                Multicmd_in_interna_bassa= sl.Add("Io", 1, MultiCmdItem.dtDI, 41, MultiCmdItem.dpNONE);
                break;
            case "JT882MA": //Argentina con 4 schede IO Belli
            case "JT882MB": //macchina inclinata con moduli IO Sipro
                Multicmd_in_Pressostato = sl.Add("Io", 1, MultiCmdItem.dtDI, 35, MultiCmdItem.dpNONE);
                Multicmd_in_trasl_alto = sl.Add("Io", 1, MultiCmdItem.dtDI, 38, MultiCmdItem.dpNONE);
                Multicmd_in_interna_bassa= sl.Add("Io", 1, MultiCmdItem.dtDI, 55, MultiCmdItem.dpNONE);
                break;



        }

        Mci_write_dtDB_prog_name.mci = Multicmd_dtDB_prog_name;
        Mci_write_dtDB_prog_name_T2.mci = Multicmd_dtDB_prog_name_T2;
        Mci_write_Vn4_Warning.mci = MultiCmd_Vn4_Warning;

        mci_array_read_all = new MultiCmdItem[]{
                Multicmd_vb4503_Cn_allarme, Multicmd_in_Pressostato, Multicmd_vb7013_ax1_home, Multicmd_vb7033_ax2_home, Multicmd_vb7053_ax3_home,Multicmd_i1_pulsanti_start,
                Multicmd_vb7073_ax4_home, Multicmd_vb7093_ax5_home, MultiCmd_VA31_Ver_PLC, MultiCmd_Vb7814_Eth_operational,
                MultiCmd_Debug14_prog_cn_in_esecuzione, MultiCmd_Debug8_riga_cn_in_esecuzione, Multicmd_i5_loader_up, Multicmd_i8_folder_back, Multicmd_i11_Lancia_back,
                MultiCmd_Vn4_Warning, MultiCmd_ver_macchine,Multicmd_i23_interna_bassa, Multicmd_i24_esterna_alta, Multicmd_i31_motoreX_ready, Multicmd_i32_motoreY_ready,
        };
        mci_array_read_882 = new MultiCmdItem[]{
                Multicmd_in_trasl_alto,Multicmd_i47_C2_ReadyAsseX, Multicmd_i48_C2_ReadyAsseY,Multicmd_vb7193_ax10_home,
                Multicmd_vb7153_ax8_home,Multicmd_vb7173_ax9_home,Multicmd_vb7113_ax6_home,Multicmd_vb7133_ax7_home,Multicmd_in_interna_bassa
        };

        Inizializzo_dati_macchina();
        TextView_macchina.setText(Values.Machine_model);

        try {
            Values.File_XML_path_R = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram_R", getApplicationContext());
            Values.File_XML_path_T2_R = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram_R_T2", getApplicationContext());
            TextView TextView_File_XML_path_R = (TextView) findViewById(R.id.textView_File_XML_path_R);
            TextView TextView_File_XML_path_T2_R = (TextView) findViewById(R.id.textView_File_XML_path_T2_R);
            TextView_File_XML_path_R.setText("XML path T1 R: "+Values.File_XML_path_R);
            TextView_File_XML_path_T2_R.setText("XML path T2 R: "+Values.File_XML_path_T2_R);


        } catch (IOException e) {
            e.printStackTrace();
        }

        Values.Tcp_enable_status = Init_TCP();
        if(Values.Tcp_enable_status.equals("true")){
            Btn_TCP_status.setVisibility(View.VISIBLE);
        }else
            Btn_TCP_status.setVisibility(View.GONE);



      //  Init_FTP();

        // Thread
        if (!Thread_Running) {
            MyAndroidThread_Emg myTask_emg = new MyAndroidThread_Emg(Emergency_page.this);
            thread_emerg = new Thread(myTask_emg, "Emg myTask");
            thread_emerg.start();
            Log.d("JAM TAG", "Start Emergency Thread from Create");
        }
    }

    private String Init_TCP() {
        String ret = "false";
        try {
            File Tcp = new File(Environment.getExternalStorageDirectory() + "/JamData/Tcp.txt");

            if (!Tcp.exists()) {
                try {
                     Utility.copyFileFromAssets(this, "JamData/Tcp.txt", Environment.getExternalStorageDirectory() + "/");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            Values.Tcp_enable_status = Info_file.Leggi_campo("storage/emulated/0/JamData/Tcp.txt", "InfoJAM", "Tcp", null, null, "TcpEnable", getApplicationContext());
            Values.TcpButton = Info_file.Leggi_campo("storage/emulated/0/JamData/Tcp.txt", "InfoJAM", "Tcp", null, null, "TcpButton", getApplicationContext());
            Values.TcpNomeCommessa = Info_file.Leggi_campo("storage/emulated/0/JamData/Tcp.txt", "InfoJAM", "Tcp", null, null, "TcpNomeCommessa", getApplicationContext());

            if(Values.Tcp_enable_status.equals("true"))
            {
                Utility.Crea_cartella("storage/emulated/0/JamData/Commesse");
                socketTCP = new SocketTCP();
                ret = "true";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String getFirmware() {
        return Values.ver_firmware;
    }
    public static String getPLCver() {
        return Values.PLCver;
    }
    /**
     * Function for init the machine files (JamData, ricette, info_jam ....)
     */
    private void Inizializzo_dati_macchina() throws IOException {

        // Check if the info_Jam file exist, otherwise copy it from assets
        File file = new File(Environment.getExternalStorageDirectory() + "/JamData/info_Jam.txt");
        if (!file.exists()) {
            try {
                Utility.copyFileFromAssetsRecursively(this, "JamData", Environment.getExternalStorageDirectory() + "/");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //sposto il file empty nella cartella ricette
        File file_empty_su_ricette = new File(Environment.getExternalStorageDirectory() + "/ricette/file_empty.xml");
        File file_empty_su_JamData = new File(Environment.getExternalStorageDirectory() + "/JamData/file_empty.xml");

        if (!file_empty_su_ricette.exists() && file_empty_su_JamData.exists()) {
            try {
                Utility.copyFileFromAssets(this, "JamData/file_empty.xml", Environment.getExternalStorageDirectory() + "/");
              //  Utility.copyFile(file_empty_su_JamData,file_empty_su_ricette);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }




        // Read the machine model from the info_Jam file
        if (file.exists()){
            Machine_model = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "MachineModel", "null", null, null, "Machine_model", getApplicationContext());
            File file_bak = new File(Environment.getExternalStorageDirectory() + "/JamData/info_Jam.bak");
            File file_txt = new File(Environment.getExternalStorageDirectory() + "/JamData/info_Jam.txt");
            if(!file_bak.exists()){
                Utility.copyFile(file_txt, file_bak);
            }else {
                if (Machine_model.equals("")) { //nel caso in cui il file info_Jam.txt si è rovinato ci copio sopra il file info_Jam_bak
                    file_txt.delete();
                    Utility.copyFile(file_bak, file_txt);
                }
            }
        }

        // If the file info_Jam is not initialized let the user chose the machine model
        if (!file.exists() || (file.exists() && Machine_model.equals("null"))) {
            try {
                // Copy the info_Jam from assets
                Utility.copyFileFromAssetsRecursively(this, "JamData", Environment.getExternalStorageDirectory() + "/");

                // Alert dialog for chose the machine model
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.Save));

                // Setting Dialog Message
                alertDialog.setMessage("Machine:");
                final Spinner input = new Spinner(this);
                input.setFocusable(false);
                String[] arraySpinner = new String[]{
                        "JT862M", "JT863M","JT862HM","JT882M","JT882MA","JT882MB","JT882HM"
                };
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                input.setAdapter(adapter);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton(getResources().getString(R.string.Save),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                                String modello_selezionato = input.getSelectedItem().toString();

                                // Save the machine model value into the info_Jam file
                                try {
                                    Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.txt", "InfoJAM", "MachineModel", null, null, "Machine_model", modello_selezionato, getApplicationContext());
                                    Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.bak", "InfoJAM", "MachineModel", null, null, "Machine_model", modello_selezionato, getApplicationContext());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // Check if the ricette folder exist
                                // TODO The ricette folder always exist because i create it outside of this funcion. The ricette folder in assets doesn't exist
                                File file_ricette = new File(Environment.getExternalStorageDirectory() + "/ricette");
                                if (!file_ricette.exists()) {
                                    // If ricette folder doesn't exist copy it from assets
                                    Utility.copyFileFromAssetsRecursively(Emergency_page.this, "ricette", Environment.getExternalStorageDirectory() + "/");
                                }

                                // Write the machine model on the textview component
                                TextView_macchina.setText(modello_selezionato);
                            }
                        });
                alertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Machine_model = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "MachineModel", "null", null, null, "Machine_model", getApplicationContext());
        if (Machine_model.equals("")) Machine_model = "null";
        // Save the machine model on Values for have it in every activity
        Values.Machine_model = Machine_model;


        File MachineLog = new File(Environment.getExternalStorageDirectory() + "/JamData/MachineLog.txt");
        // If log file doesn't exist create it
        if (!MachineLog.exists()) {
            try {
                new FileOutputStream(MachineLog, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        // Check the machine log file size
        long filesize = MachineLog.length();
        if (filesize > 10000000) {       //se la dimensione supera 10Mega, cancello i primi 5 Mega portando i secondi 5Mega in cima al file
            try {
                Utility.DimezzaFileLog(MachineLog);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Check if the tutorial folder exist otherwise create it
        File tutorial_folder = new File(Environment.getExternalStorageDirectory() + "/Tutorial");
        if (!tutorial_folder.exists()) {
            if (!tutorial_folder.mkdirs()) {
                Toast.makeText(getApplicationContext(), "Can't create tutorial folder", Toast.LENGTH_SHORT).show();
            }
        }

        // Chek if the ricette folder exist otherwise create it
        if (!new File(Environment.getExternalStorageDirectory() + "/ricette").exists()) {
            new File(Environment.getExternalStorageDirectory() + "/ricette").mkdir();
        }

        // Init the password file
        File password = new File(Environment.getExternalStorageDirectory() + "/JamData/Password.txt");
        File password_bak = new File(Environment.getExternalStorageDirectory() + "/JamData/Password.bak");
        if (!password.exists()) {
            try {
                password.createNewFile();

                BufferedWriter bw = new BufferedWriter(new FileWriter(password));

                bw.write(String.format("%s%n", "67872"));
                bw.write(String.format("%s%n", "67873"));

                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                password_bak.createNewFile();

                BufferedWriter bw_bak = new BufferedWriter(new FileWriter(password_bak));

                bw_bak.write(String.format("%s%n", "67872"));
                bw_bak.write(String.format("%s%n", "67873"));

                bw_bak.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            BufferedReader brTest = new BufferedReader(new FileReader(password));
            String text = brTest .readLine();
            brTest.close();
            if(text == null || text.equals("")){
                File file_psw = new File(Environment.getExternalStorageDirectory() + "/JamData/Password.txt");
                File file_psw_bak = new File(Environment.getExternalStorageDirectory() + "/JamData/Password.bak");
                file_psw.delete();
                Utility.copyFile(file_psw_bak,file_psw);
            }



        }
        if(!password_bak.exists()){
            File file_psw = new File(Environment.getExternalStorageDirectory() + "/JamData/Password.txt");
            File file_psw_bak = new File(Environment.getExternalStorageDirectory() + "/JamData/Password.bak");
            Utility.copyFile(file_psw,file_psw_bak);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        StopThread = false;

    //    String filePath = Environment.getExternalStorageDirectory() + "/logcat.txt";
    //    try {
     //      Runtime.getRuntime().exec(new String[]{"logcat", "-f", filePath, "com.jam_int.jt882_m8 :V", "*:W,*:E,*:D,*:I"});
     //        } catch (IOException e) {
     //       throw new RuntimeException(e);
      //  }

        if (Btn_eth_operational == null) {
            try {
                Init_Activity_Emergenza();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Restart the thread
        if (!Thread_Running) {
            MyAndroidThread_Emg myTask_emg = new MyAndroidThread_Emg(Emergency_page.this);
            thread_emerg = new Thread(myTask_emg, "Emg myTask");
            thread_emerg.start();
            Log.d("JAM TAG", "Start Emergency Thread from Resume");
        }
    }

    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
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

    private boolean CheckPresenzaProgramma_udf(String file_path) {
        MSysFileInfo fi = new MSysFileInfo();

        fi = sl.FileDir(file_path, (byte) 0x20);//0x10 = FOLDER , 0X20=FILE
        return fi != null;
    }

    private void ScrivoStringaDB(Mci_write mci_write) {
        if (mci_write.write_flag) {
            mci_write.mci.setValue(mci_write.path_file);
            sl.WriteItem(mci_write.mci);
            mci_write.write_flag = false;
        }
    }

    /**
     * Function for check the battery status
     */
    private void Verifica_batteria() {
        if ((Double) MultiCmd_livello_batteria.getValue() == 2.0d) {
            ImageView_battery.setVisibility(View.VISIBLE);
            ImageView_battery.setImageResource(R.drawable.battery_low);
        }
    }

    /**
     * Function for display the PLC version
     */
    private void ShowFirmwareVersion() {
        try {
            if (!Values.ver_firmware.equals("")) {
                String ver1 = Values.ver_firmware.substring(6, (Values.ver_firmware.length() - 4));
                TextView_testo_Firmware.setText(ver1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            TextView_testo_Firmware.setText("missing firmware");
        }
    }

    /**
     * Function for write the current running CN row
     */
    private void Riga_CN_Esecuzione() {
        String prog = (String) MultiCmd_Debug14_prog_cn_in_esecuzione.getValue();
        String st1 = getString(R.string.Prog_esecuzione);
        TextView_programma_in_esecuzione.setText(st1 + prog);
        Double riga = (Double) MultiCmd_Debug8_riga_cn_in_esecuzione.getValue();
        String st2 = getString(R.string.Riga_esecuzione);
        TextView_riga_in_esecuzione.setText(st2 + riga);
    }

    /**
     * Function for get the PLC version
     *
     * @return
     */
    private String get_ver_firmware() {
        String ret = "";
        try {
            ArrayList<String> Folder_and_file = new ArrayList<String>();
            MSysFileInfo fi = new MSysFileInfo();
            String path_folder = "B:\\fw\\*.*";
            fi = sl.FileDir(path_folder, (byte) 0x20);//0x10 = FOLDER , 0X20=FILE
            if (fi != null)    //se la cartella contiene almeno un file
            {
                Folder_and_file.add("B:\\fw\\" + fi.FName);
                return Folder_and_file.get(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    /**
     * Update the Checkboxes status
     */
    private void UpdateCheckBox() {
        CheckBox CheckBox_status_cn = findViewById(R.id.checkBox_status_cn);
        CheckBox CheckBox_pressostato = findViewById(R.id.checkBox_pressostato);
        CheckBox CheckBox_ax1_home = findViewById(R.id.checkBox_ax1_home);
        CheckBox CheckBox_ax2_home = findViewById(R.id.checkBox_ax2_home);
        CheckBox CheckBox_ax3_home = findViewById(R.id.checkBox_ax3_home);
        CheckBox CheckBox_ax4_home = findViewById(R.id.checkBox_ax4_home);
        CheckBox CheckBox_ax5_home = findViewById(R.id.checkBox_ax5_home);
        CheckBox CheckBox_loader_up = findViewById(R.id.checkBox_loader_up);
        CheckBox CheckBox_blade_back = findViewById(R.id.checkBox_blade_back);
        CheckBox CheckBox_folder_back = findViewById(R.id.checkBox_folder_back);

        CheckBox CheckBox_pinza_esterna_alta = findViewById(R.id.checkBox_pinza_esterna_alta);
        CheckBox CheckBox_MotorX_ready = findViewById(R.id.checkBox_MotorX_ready);
        CheckBox CheckBox_MotorY_ready = findViewById(R.id.checkBox_MotorY_ready);

        CheckBox_status_cn.setChecked((Double) Multicmd_vb4503_Cn_allarme.getValue() == 0.0d);
        CheckBox_pressostato.setChecked((Double) Multicmd_in_Pressostato.getValue() == 0.0d);
        CheckBox_ax1_home.setChecked((Double) Multicmd_vb7013_ax1_home.getValue() == 1.0d);
        CheckBox_ax2_home.setChecked((Double) Multicmd_vb7033_ax2_home.getValue() == 1.0d);
        CheckBox_ax3_home.setChecked((Double) Multicmd_vb7053_ax3_home.getValue() == 1.0d);
        CheckBox_ax4_home.setChecked((Double) Multicmd_vb7073_ax4_home.getValue() == 1.0d);
        CheckBox_ax5_home.setChecked((Double) Multicmd_vb7093_ax5_home.getValue() == 1.0d);
        CheckBox_loader_up.setChecked((Double) Multicmd_i5_loader_up.getValue() == 1.0d);
        CheckBox_folder_back.setChecked((Double) Multicmd_i8_folder_back.getValue() == 1.0d);
        CheckBox_blade_back.setChecked((Double) Multicmd_i11_Lancia_back.getValue() == 1.0d);

        CheckBox_pinza_esterna_alta.setChecked((Double) Multicmd_i24_esterna_alta.getValue() == 0.0d);

        CheckBox_MotorX_ready.setChecked((Double) Multicmd_i31_motoreX_ready.getValue() == 1.0d);
        CheckBox_MotorY_ready.setChecked((Double) Multicmd_i32_motoreY_ready.getValue() == 1.0d);

        //882
        if( Machine_model.equals("JT882M") ||Machine_model.equals("JT882MA") ||Machine_model.equals("JT882MB")) {
            CheckBox CheckBox_loader_up_C2 = findViewById(R.id.checkBox_loader_up_C2);
            CheckBox CheckBox_inner_clamp_C2 = findViewById(R.id.checkBox_inner_clamp_C2);
            CheckBox CheckBox_MotorX_ready_C2 = findViewById(R.id.checkBox_MotorX_ready_C2);
            CheckBox CheckBox_MotorY_ready_C2 = findViewById(R.id.checkBox_MotorY_ready_C2);
            CheckBox CheckBox_ax10_home2 = findViewById(R.id.checkBox_ax10_home2);
            CheckBox CheckBox_ax8_home2 = findViewById(R.id.checkBox_ax8_home2);
            CheckBox CheckBox_ax9_home2 = findViewById(R.id.checkBox_ax9_home2);
            CheckBox CheckBox_ax6_home2 = findViewById(R.id.checkBox_ax6_home2);
            CheckBox CheckBox_ax7_home2 = findViewById(R.id.checkBox_ax7_home2);


            CheckBox_loader_up_C2.setChecked((Double) Multicmd_in_trasl_alto.getValue() == 1.0d);
            CheckBox_inner_clamp_C2.setChecked((Double) Multicmd_in_interna_bassa.getValue() == 1.0d);
            CheckBox_MotorX_ready_C2.setChecked((Double) Multicmd_i47_C2_ReadyAsseX.getValue() == 1.0d);
            CheckBox_MotorY_ready_C2.setChecked((Double) Multicmd_i48_C2_ReadyAsseY.getValue() == 1.0d);
            CheckBox_ax10_home2.setChecked((Double) Multicmd_vb7193_ax10_home.getValue() == 1.0d);
            CheckBox_ax8_home2.setChecked((Double) Multicmd_vb7153_ax8_home.getValue() == 1.0d);
            CheckBox_ax9_home2.setChecked((Double) Multicmd_vb7173_ax9_home.getValue() == 1.0d);
            CheckBox_ax6_home2.setChecked((Double) Multicmd_vb7113_ax6_home.getValue() == 1.0d);
            CheckBox_ax7_home2.setChecked((Double) Multicmd_vb7133_ax7_home.getValue() == 1.0d);
        }



    }

    private void GestiscoWarning() {
        double Warning = (Double) MultiCmd_Vn4_Warning.getValue();
        try {
            if (Warning > 0 && Warning != warning_old) {
                warning_old = Warning;

                String warning_string = tab_names[(int) Warning];

                AlertDialog.Builder warningDialog = new AlertDialog.Builder(Emergency_page.this);
                // Setting Dialog Title
                warningDialog.setTitle("Warning");

                warningDialog.setMessage(warning_string)
                        .setCancelable(false)
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alert = warningDialog.create();
                warningDialog.show();

               // Warning = 0;        //cancello chiamata
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Selector for the status of the green button
     */
    private void Icona_tasto_verde() {
        switch (mc_stati_riarmo) {
            case 0:
                Button_verde.setBackgroundResource(R.drawable.tasto_verde);

                break;
            case 10:
            case 20:
                Button_verde.setBackgroundResource(R.drawable.dito1);
                mc_stati_visualizzazione_allarmi = 20;  //scrivo che bisogna premere il touch screen
                break;
            case 30:
                Button_verde.setBackgroundResource(R.drawable.casetta);

                Button_load_T1.setVisibility(View.VISIBLE);
                Button_load_T2.setVisibility(View.VISIBLE);



                break;
            default:
                break;
        }
    }

    private void mc_stati_riarmo() {
        switch (mc_stati_riarmo) {
            case 0: //arrivo qui quando parte questa activity, guardo come è messo il tasto verde, se non è premuto allora è una prima accensione oppure è stato premuto emergenza
                //se invece è ancora acceso vuol dire che sono arrivato da una emergenza CN
                sl.ReadItem(mci_tasto_verde);
                if (sl.getReturnCode() == 0) {
                    if ((Double) mci_tasto_verde.getValue() == 1.0d)    //se premo pulsante verde salto a 10
                    {
                        mc_stati_riarmo = 1;    //emergenza da CN
                    }else
                        mc_stati_riarmo = 5;    //tasto verde no premuto
                }
                break;
            //Inizio ZONA Emergenza CN
            case 1: //arrivo da una emergenza che non ha sganciato il tasto verde = emergenza da CN

                Leggi_Emergenze = true;     //faccio leggere glia allarmi CN dal Thread di comunicazione
                mc_stati_riarmo = 2;        //salto e aspetto la fine della lettura degli allarmi CN
                break;

            case 2:
                if(!Leggi_Emergenze) {      //se ho finito di leggere gli allarmi CN...
                    if (list_allarmi.size() > 0) {   //se ho almeno un allarme..
                        mc_stati_visualizzazione_allarmi = 60;  //allora chiamo la visualizzazione dalla procedura di visualizzazione del Thread GUI

                    }else
                    {
                        mc_stati_visualizzazione_allarmi = 70;
                    }
                    mc_stati_riarmo = 32;   //se sono arrivato qui vuol dire che il tasto verde è acceso allora vado ad aspettare la pressione dell'emergenza
                }
                break;
            //fine ZONA Emergenza CN

            //Inizio ZONA gestione tasto Verde spento
            case 5:
                mc_stati_visualizzazione_allarmi = 10;  //scrivo che bisogna premere il tasto verde
                sl.ReadItem(mci_tasto_verde);
                if (sl.getReturnCode() == 0) {
                    if ((Double) mci_tasto_verde.getValue() == 1.0d)    //se premo pulsante verde salto a 10
                    {

                        mc_stati_riarmo = 10;
                    }
                }
                break;
            //Fine ZONA gestione tasto Verde spento

            //Inizio ZONA premere su touch screen
            case 10: //aspetto click touch
                sl.ReadItem(mci_tasto_verde);
                if (sl.getReturnCode() == 0) {
                    if ((Double) mci_tasto_verde.getValue() == 0.0d)    //mentre aspetto onclick dal touch che imposterà 20..
                    {
                        mc_stati_riarmo = 0;
                    }
                }
                break;
            case 20:    //aspetto che premo il touch ( vedi case 20 della procedura Icona_tasto_verde())
                mci_Vb7903_Reset_Ch1.setValue(1.0);     //ho premuto il touch, resetto CH1 e vado in 30
                sl.WriteItem(mci_Vb7903_Reset_Ch1);

                mc_stati_riarmo = 30;

                break;
            //fine ZONA premere su touch screen

            //Inizio ZONA azzeramento assi
            case 30:    //avendo richiesto il reset del CH1, qui verifico se è avvenuto senza allarmi, se ci sono allarmi li leggo dal CN e vado a visualizzarli,
                //se non ci sono allarmi aspetto l'azzeramento e poi lancio la MainActivity

                sl.ReadItem(mci_tasto_verde);           //controllo se si spegne pulsante verde (emergenza hardware)
                sl.ReadItem(mci_CH1_in_emergenza);      //controllo se il CH1 è in emergenza
                if (sl.getReturnCode() == 0) {
                    if ((Double) mci_tasto_verde.getValue() == 0.0d || (Double) mci_CH1_in_emergenza.getValue() == 1.0d) {
                        //ho premuto il tasto verde oppure il CH1 è ancora in errore (per esempio durante azzeramento)
                        Leggi_Emergenze = true;
                        mc_stati_riarmo = 31;
                        break;
                    }
                    else {
                        //CH1 resettato e non ci sono errori

                        if((Double)Multicmd_i1_pulsanti_start.getValue() == 1.0d){
                            mc_stati_visualizzazione_allarmi = 101; //controllo se per fare l'azzeramento mancano sensori ed eventualmente lo scrivo a schermo
                        }else
                            mc_stati_visualizzazione_allarmi = 100; //scrivo a schermo di premere i pulsanti di start

                        sl.ReadItem(mc1_Vb50_macchina_azzerata);      //controllo se la macchina è azzerata
                        if (sl.getReturnCode() == 0) {
                            if ((Double) mc1_Vb50_macchina_azzerata.getValue() == 1.0d && (Double) Multicmd_in_Pressostato.getValue() == 0.0d) {
                                StopThread = true;
                                Intent intent_par = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent_par);
                            }
                        }
                    }
                }

                break;
            //Fine ZONA azzeramento assi

            // inizio ZONA ci sono stati allarmi dopo che ho tentato di resettare il CH1 oppure mentre stava facendo l'azzeramento
            case 31://arrivo qui se durante l'azzeramento succede un errore
                if(!Leggi_Emergenze) {      //se ho finito di leggere le emergenze avanzo
                    if (list_allarmi.size() > 0) {
                        mc_stati_visualizzazione_allarmi = 50;
                        mc_stati_riarmo = 32;
                    }else
                        mc_stati_riarmo = 0;
                }
                break;

            case 32://aspetto pressione emergenza
                sl.ReadItem(mci_tasto_verde);

                if (sl.getReturnCode() == 0) {
                    if (!((Double) mci_tasto_verde.getValue() ==1.0d))    //se verde ancora premu to torno al 32 altrimenti riparto 0
                    {
                        mc_stati_riarmo = 0;
                    }
                }
                Leggi_Emergenze = true;
                if (list_allarmi.size() > 0) {
                    mc_stati_visualizzazione_allarmi = 50;

                }

                break;
            // fine ZONA ci sono stati allarmi dopo che ho tentato di resettare il CH1 oppure mentre stava facendo l'azzeramento
            default:
                break;
        }
    }

    /**
     * Function for handle the IP icon state
     *
     * @param sl
     */
    private void Icona_IP(ShoppingList sl) {
        String IP = sl.getIP();
        if (sl.IsConnected()) {
            btn_connection_status.setBackgroundColor(Color.GREEN);
            btn_connection_status.setText(IP);
        } else {
            btn_connection_status.setBackgroundColor(Color.RED);
            btn_connection_status.setText("No Connect");
        }

        if ((Double) MultiCmd_Vb7814_Eth_operational.getValue() == 0.0d)
            Btn_eth_operational.setBackgroundColor(Color.RED);
        else
            Btn_eth_operational.setBackgroundColor(Color.GREEN);
    }
    /**
     * Mostra se TCP è collegato ad un PC client
     *
     */
    private void Icona_TCP() {
        if(socketTCP != null) {
            boolean connected = socketTCP.getPcConnection();
            if (connected)
                Btn_TCP_status.setBackgroundColor(Color.GREEN);
            else
                Btn_TCP_status.setBackgroundColor(Color.RED);
        }

    }
    /**
     * Function for go to the next step when user click the button
     *
     * @param view
     */
    public void onclick_buttonv_dito(View view) {
        if (mc_stati_riarmo == 10) {
            mc_stati_riarmo = 20;
        }
        //mia scorciatoia
        if (mc_stati_riarmo == 32) {
            mc_stati_riarmo = 5;
        }

    }

    /**
     * Button for open the tool page
     *
     * @param view
     */
    public void onclick_pagina_tools(View view) {
        KillThread();
        Intent intent_par = new Intent(getApplicationContext(), Tool_page.class);
        intent_par.putExtra("chiamante", "Pagina_emergenza");
        startActivity(intent_par);
    }

    // TODO Why 2?? This is never used
    /*public void onclickPaginaTools(View view) {
        KillThread();
        Intent intent_for_tools = new Intent(getApplicationContext(), Tool_page.class);
        intent_for_tools.putExtra("Lato_tasca_T1", "DX");
        intent_for_tools.putExtra("Lato_tasca_T2", "DX");

        startActivityForResult(intent_for_tools, RESULT_PAGE_TOOLS);
    }*/

    /**
     * Button for load a program (This was for unlock from some stuck situations)
     *
     * @param view
     */
    public void On_click_Load_T1(View view) {
        KillThread();

        Intent intent = new Intent(getApplicationContext(), Select_file_to_CN.class);
        intent.putExtra("operazione", "Loading....");
        intent.putExtra("Chiamante", "T1_R");
        startActivityForResult(intent, RESULT_PAGE_LOAD_UDF);
    }
    /**
     * Button for load a program (This was for unlock from some stuck situations)
     *
     * @param view
     */
    public void On_click_Load_T2(View view) {
        KillThread();

        Intent intent = new Intent(getApplicationContext(), Select_file_to_CN.class);
        intent.putExtra("operazione", "Loading....");
        intent.putExtra("Chiamante", "T2_R");
        startActivityForResult(intent, RESULT_PAGE_LOAD_UDF);
    }



    /**
     * Button for open the debug page
     *
     * @param view
     */
    public void onclick_debug(View view) {
        KeyDialog.Lancia_KeyDialogo(null, Emergency_page.this, null, 99999d, 0d, false, false, 0d, true, "KeyDialog_parameter_ret", false,"");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_debug, new IntentFilter("KeyDialog_parameter_ret"));
    }
    /**
     * Button for open alarm page
     *
     * @param view
     */
    public void onclick_alarm_more(View view) {
        KillThread();
        Intent PopUpAlarm = new Intent(getApplicationContext(), PopUpAlarm.class);
        PopUpAlarm.putExtra("stringAlarm", str_allarmi_more);
        startActivity(PopUpAlarm);
    }
    /**
     * Button for open machine settings
     *
     * @param view
     */
    public void on_click_machine_model(View view) {
        KeyDialog.Lancia_KeyDialogo(null, Emergency_page.this, null, 99999d, 0d, false, false, 0d, true, "KeyDialog_parameter_ret", false,"");
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_mac, new IntentFilter("KeyDialog_parameter_ret"));
    }

    /**
     * Function for handle the result of other actions
     *
     * @param requestCode
     * @param resultCode
     * @param databack
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent databack) {
        super.onActivityResult(requestCode, resultCode, databack);
        String returnedResult = "";
        try {
            returnedResult = databack.getData().toString();
        } catch (Exception e) {
            e.printStackTrace();
            returnedResult = "";
        }
        switch (requestCode) {

            case RESULT_PAGE_LOAD_UDF:
                if (returnedResult.equals("CARICATO")) {
                    try {
                        Thread.sleep((long) 300d);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                    }

                    if (Values.File_XML_path_R != null) {
                        try {
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.txt", "InfoJAM", "LastProgram", null, null, "LastProgram_R", Values.File_XML_path_R, getApplicationContext());
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.bak", "InfoJAM", "LastProgram", null, null, "LastProgram_R", Values.File_XML_path_R, getApplicationContext());

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "info_Jam_.txt file error", Toast.LENGTH_SHORT).show();
                        }

                        File file = new File(Values.File_XML_path_R);
                        int i = file.getName().lastIndexOf('.');
                        String name = file.getName().substring(0, i);

                        String path_file_udf = "c:\\cnc\\userdata\\" + name + ".udf";

                        Mci_write_dtDB_prog_name.path_file = path_file_udf;     //invio il path al CN
                        Mci_write_dtDB_prog_name.write_flag = true;
                    }

                    if (Values.File_XML_path_T2_R != null) {
                        try {
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.txt", "InfoJAM", "LastProgram", null, null, "LastProgram_R_T2", Values.File_XML_path_T2_R, getApplicationContext());
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.bak", "InfoJAM", "LastProgram", null, null, "LastProgram_R_T2", Values.File_XML_path_T2_R, getApplicationContext());

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "info_Jam_.txt file error", Toast.LENGTH_SHORT).show();
                        }

                        File file = new File(Values.File_XML_path_T2_R);
                        int i = file.getName().lastIndexOf('.');
                        String name = file.getName().substring(0, i);

                        String path_file_udf = "c:\\cnc\\userdata\\" + name + ".udf";

                        Mci_write_dtDB_prog_name_T2.path_file = path_file_udf;     //invio il path al CN
                        Mci_write_dtDB_prog_name_T2.write_flag = true;
                    }
                }
                break;
            default:
                break;
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
     * Function for stop every running thread and receiver
     */
    private void KillThread() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver_mac);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver_debug);
        StopThread = true;
        try {
            if (Thread_Running)
                thread_emerg.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("JAM TAG", "End Emergency Thread");
    }

    /**
     * Receiver for handle the click of machine name for open the pop up settings page
     */
    private final BroadcastReceiver mMessageReceiver_mac = new BroadcastReceiver() {
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
                KillThread();
                Intent settings = new Intent(getApplicationContext(), PopUpSettings.class);
                startActivity(settings);
            } else {
                Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
            }
        }
    };

    class MyAndroidThread_Emg implements Runnable {
        Activity activity;
        boolean rc_error;
        public MyAndroidThread_Emg(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            while (true) {
                Thread_Running = true;

                try {
                    Thread.sleep((long) 200d);
                    if (StopThread) {
                        Thread_Running = false;
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                }

                // Check if the sl is connected
                if (sl.IsConnected()) {
                    rc_error = false;
                    sl.Clear();
                    try{
                        if (first_cycle) {
                            first_cycle = false;
                            sl.ReadItem(MultiCmd_livello_batteria);
                            CaricaProgrammi();


                            MultiCmd_Vn3804_pagina_touch.setValue(0.0d);
                            sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                            // Get the current PLC version
                            Values.ver_firmware = get_ver_firmware();

                            // Write the machine model on PLC
                            switch (Machine_model) {
                                case "JT882M":
                                    MultiCmd_ver_macchine.setValue(8823.0d);
                                    Multicmd_Vb98_mcInclinata.setValue(0.0d);
                                    break;
                                case "JT882MA":
                                    MultiCmd_ver_macchine.setValue(8824.0d);
                                    Multicmd_Vb98_mcInclinata.setValue(0.0d);
                                    break;
                                case "JT882MB":
                                    MultiCmd_ver_macchine.setValue(8824.0d);
                                    Multicmd_Vb98_mcInclinata.setValue(1.0d);
                                    break;

                                default:
                                    break;
                            }
                            sl.WriteItem(MultiCmd_ver_macchine);
                            sl.WriteItem(Multicmd_Vb98_mcInclinata);

                            MultiCmd_Vn4_Warning.setValue(0.0d);
                            sl.WriteItem(MultiCmd_Vn4_Warning);

                            MultiCmd_Vn198_num_prog_right.setValue(1.0d);
                            MultiCmd_Vn199_num_prog_left.setValue(0.0d);
                            sl.WriteItem(MultiCmd_Vn198_num_prog_right);
                            sl.WriteItem(MultiCmd_Vn199_num_prog_left);
                            MultiCmd_Vn2_allarmi_da_CN.setValue(0.0d);
                            sl.WriteItem(MultiCmd_Vn2_allarmi_da_CN);
                            Multicmd_Vb4807_PinzeAlteDopoPC.setValue(1.0d);
                            sl.WriteItem(Multicmd_Vb4807_PinzeAlteDopoPC);



                                // Controllo se esiste il file udf da caricare
                                path_udf_presente = Utility.CheckPresenzaProgramma_xml(Values.File_XML_path_R);

                                if(path_udf_presente){
                                    String path_filename_T1_xml = SubString.After(Values.File_XML_path_R, "/");
                                    String filenameT1_udf = path_filename_T1_xml.replace(".xml", ".udf");
                                    path_udf_presente = CheckPresenzaProgramma_udf("C:\\cnc\\userdata\\" + filenameT1_udf);
                                }
                                if(path_udf_presente){
                                    path_udf_presente = Utility.CheckPresenzaProgramma_xml(Values.File_XML_path_T2_R);
                                    if(path_udf_presente){
                                        String filename_T2_xml = SubString.After(Values.File_XML_path_T2_R, "/");
                                        String filenameT2_udf = filename_T2_xml.replace(".xml", ".udf");
                                        path_udf_presente = CheckPresenzaProgramma_udf("C:\\cnc\\userdata\\" + filenameT2_udf);
                                    }

                                }







                        }

                        ScrivoStringaDB(Mci_write_dtDB_prog_name);
                        ScrivoStringaDB(Mci_write_dtDB_prog_name_T2);

                        try{
                        sl.ReadItems(mci_array_read_all);
                        }catch (Exception e){}
                        if (sl.getReturnCode() != 0) {
                            //se non riceve bene i valori provo a chiudere e riaprire il Socket
                            sl.Close();
                            Thread.sleep((long) 300d);
                            sl.Connect();
                            Thread.sleep((long) 300d);
                            //
                            rc_error = true;
                        }
                        if(Machine_model.equals("JT882M") || Machine_model.equals("JT882MA")|| Machine_model.equals("JT882MB"))
                            sl.ReadItems(mci_array_read_882);
                        if (sl.getReturnCode() != 0) {
                            rc_error = true;
                        }


                        if (!rc_error) {
                            mc_stati_riarmo();

                            //Lettura delle emergenze attive

                            if (Leggi_Emergenze) LeggoEmergenze(str_allarmi);


                        }
                    } catch (Exception e) {
                        rc_error = true;
                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                cnt_comunicazione++;
                                if (cnt_comunicazione > 1000) cnt_comunicazione = 0;
                                TextView_cnt_comunicazione.setText("Cnt: " + cnt_comunicazione);


                                ShowFirmwareVersion();

                                Verifica_batteria();
                                Icona_tasto_verde();
                                Icona_IP(sl);
                                UpdateCheckBox();
                                Riga_CN_Esecuzione();
                                GestiscoWarning();
                                ShowWarningDebug();
                                if(Values.Tcp_enable_status.equals("true")) Icona_TCP();



                                //controllo se all'accensione il file udf di cucitura è presente dentro al CN
                                if (!path_udf_presente) {
                                    str_allarmi = str_allarmi + "Missing sewing program file";

                                }

                                ScriviEmergenza();


                                Values.PLCver = MultiCmd_VA31_Ver_PLC.getValue().toString();
                                TextView_testo_PLC_ver.setText(Values.PLCver);


                                if(mc_stati_visualizzazione_allarmi != mc_stati_visualizzazione_allarmi_prec){

                                    mc_stati_visualizzazione_allarmi_prec =mc_stati_visualizzazione_allarmi;
                                    Log.d("JAM TAG", "mc_stati_visualizzazione_allarmi =  " + mc_stati_visualizzazione_allarmi);
                                }
                                if(mc_stati_riarmo != mc_stati_riarmo_prec){

                                    mc_stati_riarmo_prec =mc_stati_riarmo;
                                    Log.d("JAM TAG", "mc_stati_riarmo =  " + mc_stati_riarmo + " list_allarmi.size()"+ list_allarmi.size());
                                }

                            } catch (Exception e) {

                            }

                        }
                    });
                } else {
                    sl.Connect();

                    if (sl.IsConnected()) {
                        // Inizializzazione della lingua sul CN (Una sola volta dopo la connessione)
                        MultiCmdItem mci = new MultiCmdItem(1, MultiCmdItem.dtGP, 3081, MultiCmdItem.dpNONE, sl);
                        String lingua_device = Locale.getDefault().getLanguage();

                        switch (lingua_device) {
                            case "it":
                                mci.setValue("it-IT");
                                break;
                            case "en":
                                mci.setValue("gb-GB");
                                break;
                            case "tr":
                                mci.setValue("tr-TR");
                                break;
                            default:
                                mci.setValue("gb-GB");
                                break;
                        }

                        sl.WriteItem(mci);
                    }
                }
            }
        }

        private void ScriviEmergenza() {

            switch (mc_stati_visualizzazione_allarmi){

                case 0:
                    Allarm_textView.setText("");    //non faccio vedere la scritta rossa Alarm

                    break;
                case 10: //scrivo che bisogna premere il tasto verde
                    Allarm_textView.setText("");     //non faccio vedere la scritta rossa Alarm
                    TextView_allarmi.setTextSize(25);
                    TextView_allarmi.setTextColor(Color.BLUE);
                    TextView_allarmi.setText(R.string.PremiTastoVerde);
                    mc_stati_visualizzazione_allarmi = 0;
                    break;

                case 20: //scrivo che bisogna premere il touch screen
                    Allarm_textView.setText("");     //non faccio vedere la scritta rossa Alarm
                    TextView_allarmi.setTextSize(25);
                    TextView_allarmi.setTextColor(Color.BLUE);
                    TextView_allarmi.setText(R.string.ToccaTouch);
                    mc_stati_visualizzazione_allarmi = 0;

                    break;

                case 30:

                    break;

                case 50:    //mostro gli errori
                    if(list_allarmi.size()>0 && !list_allarmi_rec.equals(list_allarmi)){
                        Allarm_textView.setText("Alarm:");     // faccio vedere la scritta rossa Allarm
                        TextView_allarmi.setTextSize(15);
                        TextView_allarmi.setTextColor(Color.RED);
                        TextView_allarmi.setText("");
                        String testo = "";
                        List<String> listAllarmiDecodificati = new ArrayList<>();
                        listAllarmiDecodificati =  DecodificaCodiceErrore();
                        for (String s : listAllarmiDecodificati) {
                            testo = testo + s;
                        }
                        TextView_allarmi.setText(testo);
                        mc_stati_visualizzazione_allarmi = 999;
                    }
                    break;

                case 60:
                    if(list_allarmi.size()>0){
                        Allarm_textView.setText("Alarm:");     // faccio vedere la scritta rossa Allarm
                        TextView_allarmi.setTextSize(15);
                        TextView_allarmi.setTextColor(Color.RED);
                        TextView_allarmi.setText("");
                        String testo = "";
                        List<String> listAllarmiDecodificati = new ArrayList<>();
                        listAllarmiDecodificati =  DecodificaCodiceErrore();
                        for (String s : listAllarmiDecodificati) {
                            testo = testo + s;
                        }
                        TextView_allarmi.setText(testo);
                        mc_stati_visualizzazione_allarmi = 1000;
                    }
                    break;

                case 70:
                    Allarm_textView.setText("Alarm:");     // faccio vedere la scritta rossa Allarm
                    TextView_allarmi.setTextColor(Color.RED);
                    TextView_allarmi.setTextSize(25);
                    TextView_allarmi.setText(R.string.PremiEmergenza);
                    mc_stati_visualizzazione_allarmi = 0;

                    break;
                case 100:  //non ci sono errori, aspetto lo start per azzerare
                    Allarm_textView.setText("");     // faccio vedere la scritta rossa Allarm
                    TextView_allarmi.setTextColor(Color.BLUE);
                    TextView_allarmi.setText(R.string.PremiStartAzz);

                    mc_stati_visualizzazione_allarmi = 101;
                    break;
                case 101:


                    if((Double) Multicmd_in_Pressostato.getValue() == 1.0d){
                        Allarm_textView.setText("Alarm:");     // faccio vedere la scritta rossa Allarm
                        TextView_allarmi.setTextColor(Color.RED);
                        TextView_allarmi.setText(R.string.MancaAria);
                    }
                    if((Double) Multicmd_i8_folder_back.getValue() == 0.0d){

                        Allarm_textView.setText("Alarm:");     // faccio vedere la scritta rossa Allarm
                        TextView_allarmi.setTextColor(Color.RED);
                        TextView_allarmi.setText(R.string.MancaSensPiegDietro);
                    }

                    if((Double) Multicmd_i11_Lancia_back.getValue() == 0.0d){
                        Allarm_textView.setText("Alarm:");     // faccio vedere la scritta rossa Allarm
                        TextView_allarmi.setTextColor(Color.RED);
                        TextView_allarmi.setText(R.string.MancaSensLanciaDietro);
                    }
                    if((Double) Multicmd_i24_esterna_alta.getValue() == 1.0d){
                        Allarm_textView.setText("Alarm:");     // faccio vedere la scritta rossa Allarm
                        TextView_allarmi.setTextColor(Color.RED);
                        TextView_allarmi.setText(R.string.SensEsternaAltaAcceso);
                    }

                    if((Double) Multicmd_i31_motoreX_ready.getValue() == 0.0d){
                        Allarm_textView.setText("Alarm:");     // faccio vedere la scritta rossa Allarm
                        TextView_allarmi.setTextColor(Color.RED);
                        TextView_allarmi.setText(R.string.DriverXAllarme);
                    }
                    if((Double) Multicmd_i32_motoreY_ready.getValue() == 0.0d){
                        Allarm_textView.setText("Alarm:");     // faccio vedere la scritta rossa Allarm
                        TextView_allarmi.setTextColor(Color.RED);
                        TextView_allarmi.setText(R.string.DriverYAllarme);
                    }
                    if((Double) Multicmd_i5_loader_up.getValue() == 0.0d){
                        Allarm_textView.setText("Alarm:");     // faccio vedere la scritta rossa Allarm
                        TextView_allarmi.setTextColor(Color.RED);
                        TextView_allarmi.setText(R.string.MancaSensCaricAlto);
                    }
                    //   }
                    break;
                default:
                    break;

            }


        }

        private void  LeggoEmergenze(String extra_allarme) {
            // String str_allarmi_return ="";
            boolean findJAMCode = false;
            MultiCmdItem mci = new MultiCmdItem(1, MultiCmdItem.dtAL, 9, MultiCmdItem.dpAL_M32, sl);
            sl.ReadItem(mci);
            int[] emebuf = (int[]) mci.getValue();

            Map<String, SmartAlarm> eme = new LinkedHashMap<String, SmartAlarm>();

            Integer idx2 = 0;

            try {
                for (Integer idx = 1; idx < emebuf.length; idx += 4) {
                    idx2++;

                    Integer[] p = {emebuf[idx], emebuf[idx + 1], emebuf[idx + 2], emebuf[idx + 3]};

                    SmartAlarm al = new SmartAlarm(1, SmartAlarm.satEmergM, p, true, "", "", null);

                    al.setIndex(idx2);

                    eme.put(al.getFingerPrint(), al);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }

            //Lettura delle descrizioni delle emergenze attive
            try {

                list_allarmi = new ArrayList<>();
                for (SmartAlarm al : eme.values()) {
                    MultiCmdItem descmci = new MultiCmdItem(1, MultiCmdItem.dtAL, al.getIndex(), MultiCmdItem.dpAL_M32_Description, sl);
                    sl.ReadItem(descmci);
                    String d = (String) descmci.getValue();
                    list_allarmi.add(d);
                }
                if(extra_allarme!=null && extra_allarme.length()>0) {
                    list_allarmi.add(extra_allarme);
                    str_allarmi ="";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            Leggi_Emergenze = false;
        }
    }

    private void ShowWarningDebug() {
        if(warning_debug >0){

            Toast.makeText(getApplicationContext(), "WarningDebug Err"+warning_debug, Toast.LENGTH_LONG).show();
            warning_debug = -1;
        }
    }

    private void CaricaProgrammi() {
        String path_xml_T1 = Values.File_XML_path_R;
        try {
            if(path_xml_T1.contains(" ")){
                warning_debug = 5;
            }else {
                if (path_xml_T1 != null && !path_xml_T1.equals("")) {
                    File file = new File(path_xml_T1);
                    int i = file.getName().lastIndexOf('.');
                    String name = file.getName().substring(0, i);
                    String path_file_udf = "c:\\cnc\\userdata\\" + name + ".udf";
                    Multicmd_dtDB_prog_name = sl.Add("Io", 1, MultiCmdItem.dtDB, 30, MultiCmdItem.dpDB_MAIN1);
                    Multicmd_dtDB_prog_name.setValue(path_file_udf);
                    sl.WriteItem(Multicmd_dtDB_prog_name);

                } else
                    warning_debug = 1;
            }
        } catch (Exception e) {
            warning_debug = 2;

        }
        try {
            String path_xml_T2 = Values.File_XML_path_T2_R;
            if(path_xml_T2.contains(" ")){
                warning_debug = 6;
            }else {
                if (path_xml_T2 != null && !path_xml_T2.equals("") ) {
                    File file = new File(path_xml_T2);
                    int i = file.getName().lastIndexOf('.');
                    String name = file.getName().substring(0, i);
                    String path_file_udf = "c:\\cnc\\userdata\\" + name + ".udf";
                    Multicmd_dtDB_prog_name_T2 = sl.Add("Io", 1, MultiCmdItem.dtDB, 30, MultiCmdItem.dpDB_MAIN2);
                    Multicmd_dtDB_prog_name_T2.setValue(path_file_udf);
                    sl.WriteItem(Multicmd_dtDB_prog_name_T2);

                } else
                    warning_debug = 3;
            }
        } catch (Exception e) {
            warning_debug = 4;

        }
    }


    private List<String> DecodificaCodiceErrore() {

        List<String> ret = new ArrayList<>();
        List<String> listAllarmiDecodificati = new ArrayList<>();
        List<String> listAllarmiNonDecodificati = new ArrayList<>();

        String[] Descrizioni = new String[]{};
        Descrizioni = getResources().getStringArray(R.array.AllarmiCN_Descrizione_JAM);
        String testo = "";
        str_allarmi_more = "";
        for (String d : list_allarmi) {
            str_allarmi_more = str_allarmi_more + d +"\n"+"\n"; //compilo la stringa che mostrerò se premo + nel display
            testo = "";
            boolean findJAMCode = false;
            if (d.contains("30800001")) {
                findJAMCode = true;
            } //0 //emergenza all'accensione, non scrivo niente
            if (d.contains("30800002")) {
                findJAMCode = true;
            } //1
            if (d.contains("30800003")) {
                d = Descrizioni[2];
                testo = testo + d ;
                findJAMCode = true;
            } //2
            if (d.contains("30800004")) {
                d = Descrizioni[3];
                testo = testo + d ;
                findJAMCode = true;
            } //3
            if (d.contains("30800005")) {
                d = Descrizioni[4];
                testo = testo + d ;
                findJAMCode = true;
            } //4
            if (d.contains("30800006")) {
                d = Descrizioni[5];
                testo = testo + d ;
                findJAMCode = true;
            } //5
            if (d.contains("30800007")) {
                d = Descrizioni[6];
                testo = testo + d ;
                findJAMCode = true;
            } //6
            if (d.contains("30003801")) {
                d = Descrizioni[7];
                testo = testo + d ;
                findJAMCode = true;
            }  //7
            if (d.contains("30004801")) {
                d = Descrizioni[8];
                testo = testo + d ;
                findJAMCode = true;
            } //8
            if (d.contains("30005801")) {
                d = Descrizioni[9];
                testo = testo + d ;
                findJAMCode = true;
            }  //9
            if (d.contains("30008801")) {
                d = Descrizioni[10];
                testo = testo + d ;
                findJAMCode = true;
            }  //10
            if (d.contains("30009801")) {
                d = Descrizioni[11];
                testo = testo + d ;
                findJAMCode = true;
            } //11
            if (d.contains("30010801")) {
                d = Descrizioni[12];
                testo = testo + d ;
                findJAMCode = true;
            }//12
            if (d.contains("150001632")) {
                d = Descrizioni[13];
                testo = testo + d ;
                findJAMCode = true;
            }  //13
            if (d.contains("150002632")) {
                d = Descrizioni[14];
                testo = testo + d ;
                findJAMCode = true;
            }  //14
            if (d.contains("150003632")) {
                d = Descrizioni[15];
                testo = testo + d ;
                findJAMCode = true;
            } //15
            if (d.contains("150004632")) {
                d = Descrizioni[16];
                testo = testo + d ;
                findJAMCode = true;
            } //16
            if (d.contains("150005632")) {
                d = Descrizioni[17];
                testo = testo + d ;
                findJAMCode = true;
            }  //17
            if (d.contains("150006632")) {
                d = Descrizioni[18];
                testo = testo + d ;
                findJAMCode = true;
            }  //18
            if (d.contains("150007632")) {
                d = Descrizioni[19];
                testo = testo + d ;
                findJAMCode = true;
            }  //19
            if (d.contains("150008632")) {
                d = Descrizioni[20];
                testo = testo + d ;
                findJAMCode = true;
            }  //20
            if (d.contains("150009632")) {
                d = Descrizioni[21];
                testo = testo + d ;
                findJAMCode = true;
            } //21
            if (d.contains("150010632")) {
                d = Descrizioni[22];
                testo = testo + d ;
                findJAMCode = true;
            } //22
            if (d.contains("150001332") || d.contains("150001333") || d.contains("150001334")) {
                d = Descrizioni[23];
                testo = testo + d ;
                findJAMCode = true;
            }  //23
            if (d.contains("150002132") || d.contains("150002332") || d.contains("150002333") || d.contains("150002334")) {
                d = Descrizioni[24];
                testo = testo + d ;
                findJAMCode = true;
            }   //24
            if (d.contains("150003132") || d.contains("150003332") || d.contains("150003333") || d.contains("150003334")) {
                d = Descrizioni[25];
                testo = testo + d ;
                findJAMCode = true;
            }  //25
            if (d.contains("150004132") || d.contains("150004332") || d.contains("150004333") || d.contains("150004334")) {
                d = Descrizioni[26];
                testo = testo + d ;
                findJAMCode = true;
            } //26
            if (d.contains("350005132") || d.contains("350005332") || d.contains("350005333") || d.contains("350005334")) {
                d = Descrizioni[27];
                testo = testo + d ;
                findJAMCode = true;
            }  //27
            if (d.contains("250006332") || d.contains("250006333") || d.contains("250006334")) {
                d = Descrizioni[28];
                testo = testo + d ;
                findJAMCode = true;
            }  //28
            if (d.contains("250007332") || d.contains("250007333") || d.contains("250007334")) {
                d = Descrizioni[29];
                testo = testo + d ;
                findJAMCode = true;
            }  //29
            if (d.contains("250008332") || d.contains("250008333") || d.contains("250008334")) {
                d = Descrizioni[30];
                testo = testo + d ;
                findJAMCode = true;
            }   //30
            if (d.contains("250009332") || d.contains("250009333") || d.contains("250009334")) {
                d = Descrizioni[31];
                testo = testo + d ;
                findJAMCode = true;
            } //31
            if (d.contains("250010332") || d.contains("250010333") || d.contains("250010334")) {
                d = Descrizioni[32];
                testo = testo + d ;
                findJAMCode = true;
            } //31
            if (d.contains("30003850")) {
                d = Descrizioni[33];
                testo = testo + d ;
                findJAMCode = true;
            } //33
            if (d.contains("30004850")) {
                d = Descrizioni[34];
                testo = testo + d ;
                findJAMCode = true;
            } //34
            if (d.contains("30005850")) {
                d = Descrizioni[35];
                testo = testo + d ;
                findJAMCode = true;
            } //35
            if (d.contains("30008850")) {
                d = Descrizioni[36];
                testo = testo + d ;
                findJAMCode = true;
            } //36
            if (d.contains("30009850")) {
                d = Descrizioni[37];
                testo = testo + d ;
                findJAMCode = true;
            } //37
            if (d.contains("30010850")) {
                d = Descrizioni[38];
                testo = testo + d ;
                findJAMCode = true;
            } //38
            if (d.contains("62050101")) {
                d = Descrizioni[39];
                testo = testo + d ;
                findJAMCode = true;
            } //39  Emergenza da nodo Ethercat Ago
            if (d.contains("62050102")) {
                d = Descrizioni[40];
                testo = testo + d ;
                findJAMCode = true;
            } //40  Emergenza da nodo Ethercat Hook
            if (d.contains("62050103")) {
                d = Descrizioni[41];
                testo = testo + d ;
                findJAMCode = true;
            } //41  Emergenza da nodo Ethercat CAricatore
            if (d.contains("62050104")) {
                d = Descrizioni[42];
                testo = testo + d + "\n";
                findJAMCode = true;
            } //42  Emergenza da nodo Ethercat Ago
            if (d.contains("62050105")) {
                d = Descrizioni[43];
                testo = testo + d + "\n";
                findJAMCode = true;
            } //43  Emergenza da nodo Ethercat Hook
            if (d.contains("62050106")) {
                d = Descrizioni[44];
                testo = testo + d + "\n";
                findJAMCode = true;
            } //44  Emergenza da nodo Ethercat CAricatore
            //45 inizio
            if (d.contains("30800008")) {
                d = Descrizioni[45];
                testo = testo + d;
                findJAMCode = true;
                //45 fine
            }
            //46 inizio
            if (d.contains("30800009")) {
                d = Descrizioni[46];
                testo = testo + d;
                findJAMCode = true;
                //46 fine
            }

            if (d.contains("140045900") || d.contains("240045900") || d.contains("340045900")) {  //"Automatic cycle interrupted due to an emergency
                d = getString(R.string.ErroreCicloAuto);
                findJAMCode = false;
            }

            if(findJAMCode) {
                boolean giàPresente = false;
                for (String s:listAllarmiDecodificati) {
                    if(s.contains(testo))
                        giàPresente = true;
                }
                if(!giàPresente)
                    listAllarmiDecodificati.add(testo + "\n");
            }
            else {
                boolean giàPresente = false;
                for (String s:listAllarmiNonDecodificati) {
                    if(s.contains(testo))
                        giàPresente = true;
                }
                if(!giàPresente)
                    listAllarmiNonDecodificati.add(d + "\n");
            }
        }

        //aggiungo in coda ai codici decodificati quelli non decodificati
        for (String s:listAllarmiDecodificati) {
            ret.add(s);
        }
        for (String s:listAllarmiNonDecodificati) {
            ret.add(s);
        }
        return ret;

    }
    /**
     * Receiver for handle the click on debug
     */
    private final BroadcastReceiver mMessageReceiver_debug = new BroadcastReceiver() {
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

            if (val.equals(linea2)||val.equals("67874")) {
                KillThread();
                Intent settings = new Intent(getApplicationContext(), Debug.class);
                startActivity(settings);
            } else {
                Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
            }
        }
    };


}
