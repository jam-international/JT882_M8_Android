package com.jam_int.jt882_m8;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jamint.ricette.Ricetta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import communication.MultiCmdItem;
import communication.ShoppingList;

public class Parametri_page extends Activity {
    /**
     * Activity results indexes
     */
    final private static int PAGE_UDF_T1_DX = 200;
    final private static int PAGE_UDF_T1_SX = 201;
    final private static int PAGE_C1_PARAM = 204;
    final private static int PAGE_C2_PARAM = 205;
    final private static int PAGE_PARAM_PIEGATORE = 206;
    final private static int PAGE_PARAM_SCARICATORE = 207;
    final private static int PAGE_UDF_T2_DX = 209;
    final private static int PAGE_UDF_T2_SX = 210;
    final private static int PAGE_PARAM_TRASLATORE = 211;
    /**
     * ShoppingList for communicate with PLC
     */
    ShoppingList sl;
    /**
     * Table for display all the params
     */
    TableLayout stk;
    /**
     * Thread for communicate with PLC and read/write params
     */
    Thread thread_parametri;
    boolean Thread_Running = false, StopThread = false;
    /**
     * List of params to display
     */
    ArrayList<Parametro_mac> Lista_parametri = new ArrayList<>();
    int Chiamante = 0;
    Ricetta ricetta;
    /**
     * File for change udf params
     */
    File file_udf;
    /**
     * Bool that indicate if at least 1 value as been changed
     */
    boolean Cambiato_dato = false, Macchina_armata= false;
    // TODO i don't know why use this. I think is useless
    Intent databack_parametri = new Intent();
    Boolean Aggiorna_lista_su_schermo = true, trigger_read_da_cn = false, Leggi_dati_da_Cn = true, Dati_CN_letti = false, first_cycle = true, Aggiorna_parametri_da_Load = false, AggiornaSchermoDopoLoad = false;
    int IdFocus = 0;
    String Machine_model = "JT862M";
    /**
     * Var that indicate if the password for ulock all the params as been inserted
     */
    Boolean Password_inserita = false;
    /**
     * UI components
     */
    ScrollView sv;
    ImageButton Button_exit, Button_Load, Button_Save;
    LinearLayout LinearLayoutSaveLoad;
    /**
     * PLC vars
     */
    MultiCmdItem MultiCmd_CH1_in_emergenza;
    MultiCmdItem MultiCmd_Vb1012, MultiCmd_Vq1701, MultiCmd_Vq1702, MultiCmd_Vq1703, MultiCmd_Vq1704, MultiCmd_Vq1705, MultiCmd_Vq1706, MultiCmd_Vq1707,
            MultiCmd_Vq1708, MultiCmd_Vq1709, MultiCmd_Vq1710, MultiCmd_Vq1711, MultiCmd_Vq1712, MultiCmd_Vq1713, MultiCmd_Vq1714,mci_null,
            MultiCmd_Vq1715, MultiCmd_Vq1716, MultiCmd_Vq1717,
            MultiCmd_Vb2012, MultiCmd_Vq2701, MultiCmd_Vq2702, MultiCmd_Vq2703, MultiCmd_Vq2704, MultiCmd_Vq2705, MultiCmd_Vq2706, MultiCmd_Vq2707,
            MultiCmd_Vq2708, MultiCmd_Vq2709, MultiCmd_Vq2710, MultiCmd_Vq2711, MultiCmd_Vq2712, MultiCmd_Vq2713, MultiCmd_Vq2714,
            MultiCmd_Vq2715, MultiCmd_Vq2716, MultiCmd_Vq2717, MultiCmd_Vb4090_DisableRotturaFiloC1, MultiCmd_Vb4091_DisableRotturaFiloC2,
            MultiCmd_Vb4071_DisableContSpolaC1, MultiCmd_Vb4073_DisableContSpolaC2, Multicmd_vq1718, Multicmd_vq1719, Multicmd_vq1720, Multicmd_vq1721,
            Multicmd_vq2718, Multicmd_vq2719, Multicmd_vq2720, Multicmd_vq2721, Multicmd_vn14, Multicmd_vb4095, Multicmd_vb4518, Multicmd_null,
            Multicmd_vb20, MultiCmd_Vq3592, MultiCmd_Vq3593, MultiCmd_Vq3053, MultiCmd_Vq3063, MultiCmd_Vq3050, MultiCmd_Vq3051, MultiCmd_Vq3052,
            Multicmd_vn109, Multicmd_vn110, Multicmd_Vq1921, Multicmd_Vq1922, Multicmd_Vq3501, Multicmd_Vq3502, Multicmd_Vq3510, Multicmd_Vq3511,
            Multicmd_Vq3000, Multicmd_Vq3001, Multicmd_Vq3002, Multicmd_Vq3003, Multicmd_Vq3004, Multicmd_Vq3005, Multicmd_Vq3006, Multicmd_Vq3007,
            Multicmd_Vq3008, Multicmd_Vq3009, Multicmd_Vq3010, Multicmd_Vq3011, Multicmd_Vq3012, Multicmd_Vq3013, Multicmd_Vq3014, Multicmd_Vq3015,
            Multicmd_Vq3016, Multicmd_Vq3017, Multicmd_Vq3018, Multicmd_Vq3019, Multicmd_Vq3020, Multicmd_Vq3021, Multicmd_Vq3022, Multicmd_Vq3023,
            Multicmd_Vq3540, Multicmd_Vq3080, Multicmd_Vq3081, Multicmd_Vq3082, Multicmd_Vq3083, Multicmd_Vq3520, Multicmd_Vq3521, Multicmd_Vq3522,
            Multicmd_Vq3523, Multicmd_Vq3525, Multicmd_Vq3530, Multicmd_Vq3531, Multicmd_Vq3070, Multicmd_Vq1913, Multicmd_Vq1914, Multicmd_Vq1915,
            Multicmd_Vq1916, Multicmd_Vq1917, Multicmd_Vq1918, Multicmd_Vq1919, Multicmd_Vq2913, Multicmd_Vq2914, Multicmd_Vq2915,
            Multicmd_Vq2916, Multicmd_Vq2917, Multicmd_Vq2918, Multicmd_Vq2919, MultiCmd_Vb21,MultiCmd_Vb22, MultiCmd_Vn3804_pagina_touch, Multicmd_vn101, Multicmd_vn102,
            Multicmd_vn103, Multicmd_vn104, MultiCmd_Vq3071, MultiCmd_Vq3085,Multicmd_Vq3054, Multicmd_Vb4034_ScaricOnSuRasafilo, Multicmd_Vq3084_SpostamentoPinzaPezziCorti,
            Multicmd_HMI_C1_UdfVelLavRPM,Multicmd_Vb23,Multicmd_Vn120_ClampMode,Multicmd_Vb170KitTascaCargo,Multicmd_Vb151EnableCarPattine,MultiCmd_Vq3079,MultiCmd_Vq3092,MultiCmd_Vq3093,
            MultiCmd_Vq3091,MultiCmd_Vn180,Multicmd_Vq3201,Multicmd_Vq3202,Multicmd_Vq3203,Multicmd_Vq3205;
    //Multicmd_HMI_C1_UdfPuntiVelIni, Multicmd_HMI_C1_UdfVelIniRPM, Multicmd_HMI_C1_UdfPuntiVelRall,
      //      Multicmd_HMI_C1_UdfVelRallRPM, Multicmd_HMI_C1_Udf_FeedG0,Multicmd_HMI_C1_Udf_ValTensione,Multicmd_HMI_C1_Udf_ValEletSopra,Multicmd_HMI_C1_Udf_ValEletSotto,
        //    Multicmd_HMI_C1_VelocitaCaricLavoro;




    ;
    Mci_write Mci_write_Vq1913_C1_UdfVelLavRPM = new Mci_write(),
            Mci_write_Vq1914_C1_UdfPuntiVelIni = new Mci_write(),
            Mci_write_Vq1915_C1_UdfVelIniRPM = new Mci_write(),
            Mci_write_Vq1916_C1_UdfPuntiVelRall = new Mci_write(),
            Mci_write_Vq1917_C1_UdfVelRallRPM = new Mci_write(),
            Mci_write_Vq1918_C1_Udf_FeedG0 = new Mci_write(),
            Mci_write_Vq1919_C1_Udf_ValTensioneT1 = new Mci_write(),
            Mci_write_Vq1920_C1_Udf_20 = new Mci_write(),
            Mci_write_Vq1921_C1_Udf_ValElettrocalamitaSopra = new Mci_write(),
            Mci_write_Vq1922_C1_Udf_ValElettrocalamitaSotto = new Mci_write(),
            Mci_write_Vn109_sequenza_chiusura_piegatore = new Mci_write(),
            Mci_write_Vn110_sequenza_apertura_piegatore = new Mci_write(),
            Mci_write_Vq3511_VelLavCaricatore = new Mci_write(),
            Mci_write_Vq1923 = new Mci_write(),
            Mci_write_Vq1924 = new Mci_write(),
            Mci_write_Vq1925 = new Mci_write(),
            Mci_write_Vq1926 = new Mci_write(),
            Mci_write_Vq1927 = new Mci_write(),
            Mci_write_Vq1928 = new Mci_write(),
            Mci_write_Vq1929 = new Mci_write(),
            Mci_write_Vq1930 = new Mci_write(),
            Mci_write_Vn101 = new Mci_write(),
            Mci_write_Vn102 = new Mci_write(),
            Mci_write_Vn103 = new Mci_write(),
            Mci_write_Vn104 = new Mci_write(),
            Mci_write_Vq2913_C2_UdfVelLavRPM = new Mci_write(),
            Mci_write_Vq2914_C2_UdfPuntiVelIni = new Mci_write(),
            Mci_write_Vq2915_C2_UdfVelIniRPM = new Mci_write(),
            Mci_write_Vq2916_C2_UdfPuntiVelRall = new Mci_write(),
            Mci_write_Vq2917_C2_UdfVelRallRPM = new Mci_write(),
            Mci_write_Vq2918_C2_Udf_FeedG0 = new Mci_write(),
            Mci_write_Vq2919_C1_Udf_ValTensioneT1 = new Mci_write(),
            Mci_write_Vq2920_C2_Udf_20 = new Mci_write(),
            Mci_write_Vq2921_C2_Udf_ValElettrocalamitaSopra = new Mci_write(),
            Mci_write_Vq2922_C2_Udf_ValElettrocalamitaSotto = new Mci_write(),
            Mci_write_Vq2923 = new Mci_write(),
            Mci_write_Vq2924 = new Mci_write(),
            Mci_write_Vq2925 = new Mci_write(),
            Mci_write_Vq2926 = new Mci_write(),
            Mci_write_Vq2927 = new Mci_write(),
            Mci_write_Vq2928 = new Mci_write(),
            Mci_write_Vq2929 = new Mci_write(),
            Mci_write_Vq2930 = new Mci_write(),


            Mci_write_HMI_C1_UdfVelLavRPM   = new Mci_write(),
            Mci_write_HMI_C1_UdfPuntiVelIni  = new Mci_write(),
            Mci_write_HMI_C1_UdfVelIniRPM  = new Mci_write(),
            Mci_write_HMI_C1_UdfPuntiVelRall  = new Mci_write(),
            Mci_write_HMI_C1_UdfVelRallRPM  = new Mci_write(),
            Mci_write_HMI_C1_Udf_FeedG0  = new Mci_write(),
            Mci_write_HMI_C1_Udf_ValTensione  = new Mci_write(),
            Mci_write_HMI_C1_Udf_ValEletSopra  = new Mci_write(),
            Mci_write_HMI_C1_Udf_ValEletSotto  = new Mci_write(),
            Mci_write_HMI_C1_VelocitaCaricLavoro  = new Mci_write(),

            Mci_write_HMI_C2_UdfVelLavRPM   = new Mci_write(),
            Mci_write_HMI_C2_UdfPuntiVelIni  = new Mci_write(),
            Mci_write_HMI_C2_UdfVelIniRPM  = new Mci_write(),
            Mci_write_HMI_C2_UdfPuntiVelRall  = new Mci_write(),
            Mci_write_HMI_C2_UdfVelRallRPM  = new Mci_write(),
            Mci_write_HMI_C2_Udf_FeedG0  = new Mci_write(),
            Mci_write_HMI_C2_Udf_ValTensione  = new Mci_write();










    /**
     * Receiver for unlock all the parameters
     */
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            String val = intent.getStringExtra("ret_valore");

            String linea = "";
            try {
                File password = new File(Environment.getExternalStorageDirectory() + "/JamData/Password.txt");
                BufferedReader br = new BufferedReader(new FileReader(password.getAbsolutePath()));
                linea = br.readLine();
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (val.equals(linea) || val.equals("67874")) {
                Password_inserita = true;
                ImageButton SendCmd = findViewById(R.id.imageButton_send_command);
                switch (Chiamante) {
                    case PAGE_C1_PARAM:
                    case PAGE_C2_PARAM:
                    case PAGE_PARAM_PIEGATORE:
                    case PAGE_PARAM_SCARICATORE:
                    case PAGE_PARAM_TRASLATORE:
                        LinearLayoutSaveLoad.setVisibility(View.VISIBLE);
                        SendCmd.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }

                // Sblocco tutti i parametri
                for (int i = 0; i < Lista_parametri.size(); i++) {
                    Lista_parametri.get(i).password = false;
                }

                Inizializza_TableRow();
                Show_parametri();

            } else if (val.equals("")) {
            } else {
                Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
            }
        }
    };
    MultiCmdItem[] mci_array_read_all, mci_array_read_all_1, mci_array_da_aggiornare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametri);

        try {
            Machine_model = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "MachineModel", "null", null, null, "Machine_model", getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button_exit = findViewById(R.id.imageButton_exit);
        Button_Load = findViewById(R.id.imageButton_load);
        Button_Save = findViewById(R.id.imageButton_save);

        sv = findViewById(R.id.ScrollView);

        LinearLayoutSaveLoad = findViewById(R.id.LinearLayoutSaveLoad);

        LinearLayoutSaveLoad.setVisibility(View.GONE);

        // Setup ShoppingList
        sl = SocketHandler.getSocket();
        sl.Clear();
        stk = findViewById(R.id.tableLayout_punti);

        // Get the activity that called this page
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Chiamante = extras.getInt("Chiamante");
        }

        Init_Mci();

        MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);

        // Start thread
        if (!Thread_Running) {
            MyAndroidThread_Parametri myTask_Param = new MyAndroidThread_Parametri(Parametri_page.this);
            thread_parametri = new Thread(myTask_Param, "Parameter myTask");
            thread_parametri.start();
            Log.d("JAM TAG", "Start Parametri_page Thread from Create");
        }

        try {
            Load_parametri_udf();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Inizializza_TableRow();

        databack_parametri.setData(Uri.parse("NO"));
        setResult(RESULT_OK, databack_parametri);   //indico che non ho cambiato almeno un parametro
    }

    /**
     * Function for init all the mci
     */
    private void Init_Mci() {
        mci_null = sl.Add("Io", 1, MultiCmdItem.dtVQ, 8191, MultiCmdItem.dpNONE);   //serve per non lasciare punti vuoti nel array dei parametri ma non lo leggo o scrivo mai
        MultiCmd_Vb1012 = sl.Add("Io", 1, MultiCmdItem.dtVB, 1012, MultiCmdItem.dpNONE);
        MultiCmd_Vq1701 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1701, MultiCmdItem.dpNONE);
        MultiCmd_Vq1702 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1702, MultiCmdItem.dpNONE);
        MultiCmd_Vq1703 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1703, MultiCmdItem.dpNONE);
        MultiCmd_Vq1704 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1704, MultiCmdItem.dpNONE);
        MultiCmd_Vq1705 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1705, MultiCmdItem.dpNONE);
        MultiCmd_Vq1706 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1706, MultiCmdItem.dpNONE);
        MultiCmd_Vq1707 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1707, MultiCmdItem.dpNONE);
        MultiCmd_Vq1708 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1708, MultiCmdItem.dpNONE);
        MultiCmd_Vq1709 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1709, MultiCmdItem.dpNONE);
        MultiCmd_Vq1710 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1710, MultiCmdItem.dpNONE);
        MultiCmd_Vq1711 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1711, MultiCmdItem.dpNONE);
        MultiCmd_Vq1712 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1712, MultiCmdItem.dpNONE);
        MultiCmd_Vq1713 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1713, MultiCmdItem.dpNONE);
        MultiCmd_Vq1714 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1714, MultiCmdItem.dpNONE);
        MultiCmd_Vq1715 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1715, MultiCmdItem.dpNONE);
        MultiCmd_Vq1716 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1716, MultiCmdItem.dpNONE);
        MultiCmd_Vq1717 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1717, MultiCmdItem.dpNONE);

        MultiCmd_Vb2012 = sl.Add("Io", 1, MultiCmdItem.dtVB, 2012, MultiCmdItem.dpNONE);
        MultiCmd_Vq2701 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2701, MultiCmdItem.dpNONE);
        MultiCmd_Vq2702 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2702, MultiCmdItem.dpNONE);
        MultiCmd_Vq2703 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2703, MultiCmdItem.dpNONE);
        MultiCmd_Vq2704 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2704, MultiCmdItem.dpNONE);
        MultiCmd_Vq2705 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2705, MultiCmdItem.dpNONE);
        MultiCmd_Vq2706 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2706, MultiCmdItem.dpNONE);
        MultiCmd_Vq2707 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2707, MultiCmdItem.dpNONE);
        MultiCmd_Vq2708 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2708, MultiCmdItem.dpNONE);
        MultiCmd_Vq2709 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2709, MultiCmdItem.dpNONE);
        MultiCmd_Vq2710 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2710, MultiCmdItem.dpNONE);
        MultiCmd_Vq2711 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2711, MultiCmdItem.dpNONE);
        MultiCmd_Vq2712 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2712, MultiCmdItem.dpNONE);
        MultiCmd_Vq2713 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2713, MultiCmdItem.dpNONE);
        MultiCmd_Vq2714 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2714, MultiCmdItem.dpNONE);
        MultiCmd_Vq2715 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2715, MultiCmdItem.dpNONE);
        MultiCmd_Vq2716 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2716, MultiCmdItem.dpNONE);
        MultiCmd_Vq2717 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2717, MultiCmdItem.dpNONE);
        MultiCmd_Vb4090_DisableRotturaFiloC1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4090, MultiCmdItem.dpNONE);
        MultiCmd_Vb4091_DisableRotturaFiloC2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4091, MultiCmdItem.dpNONE);
        MultiCmd_Vb4071_DisableContSpolaC1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4071, MultiCmdItem.dpNONE);
        MultiCmd_Vb4073_DisableContSpolaC2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4073, MultiCmdItem.dpNONE);
        Multicmd_vq1718 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1718, MultiCmdItem.dpNONE);
        Multicmd_vq1719 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1719, MultiCmdItem.dpNONE);
        Multicmd_vq1720 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1720, MultiCmdItem.dpNONE);
        Multicmd_vq1721 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1721, MultiCmdItem.dpNONE);
        Multicmd_vq2718 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2718, MultiCmdItem.dpNONE);
        Multicmd_vq2719 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2719, MultiCmdItem.dpNONE);
        Multicmd_vq2720 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2720, MultiCmdItem.dpNONE);
        Multicmd_vq2721 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2721, MultiCmdItem.dpNONE);
        Multicmd_vn14 = sl.Add("Io", 1, MultiCmdItem.dtVN, 14, MultiCmdItem.dpNONE);
        Multicmd_vb4095 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4095, MultiCmdItem.dpNONE);
        Multicmd_vb4518 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4518, MultiCmdItem.dpNONE);
        Multicmd_null = sl.Add("Io", 1, MultiCmdItem.dtVB, 1, MultiCmdItem.dpNONE);
        Multicmd_vb20 = sl.Add("Io", 1, MultiCmdItem.dtVB, 20, MultiCmdItem.dpNONE);
        MultiCmd_Vq3592 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3592, MultiCmdItem.dpNONE);
        MultiCmd_Vq3593 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3593, MultiCmdItem.dpNONE);
        MultiCmd_Vq3053 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3053, MultiCmdItem.dpNONE);
        MultiCmd_Vq3063 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3063, MultiCmdItem.dpNONE);
        MultiCmd_Vq3050 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3050, MultiCmdItem.dpNONE);
        MultiCmd_Vq3051 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3051, MultiCmdItem.dpNONE);
        MultiCmd_Vq3052 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3052, MultiCmdItem.dpNONE);
        Multicmd_vn109 = sl.Add("Io", 1, MultiCmdItem.dtVN, 109, MultiCmdItem.dpNONE);
        Multicmd_vn110 = sl.Add("Io", 1, MultiCmdItem.dtVN, 110, MultiCmdItem.dpNONE);
        Multicmd_Vq1921 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1921, MultiCmdItem.dpNONE);
        Multicmd_Vq1922 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1922, MultiCmdItem.dpNONE);
        Multicmd_Vq3501 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3501, MultiCmdItem.dpNONE);
        Multicmd_Vq3502 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3502, MultiCmdItem.dpNONE);
        Multicmd_Vq3510 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3510, MultiCmdItem.dpNONE);
        Multicmd_Vq3511 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3511, MultiCmdItem.dpNONE);
        Multicmd_Vq3000 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3000, MultiCmdItem.dpNONE);
        Multicmd_Vq3001 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3001, MultiCmdItem.dpNONE);
        Multicmd_Vq3002 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3002, MultiCmdItem.dpNONE);
        Multicmd_Vq3003 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3003, MultiCmdItem.dpNONE);
        Multicmd_Vq3004 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3004, MultiCmdItem.dpNONE);
        Multicmd_Vq3005 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3005, MultiCmdItem.dpNONE);
        Multicmd_Vq3006 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3006, MultiCmdItem.dpNONE);
        Multicmd_Vq3007 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3007, MultiCmdItem.dpNONE);
        Multicmd_Vq3008 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3008, MultiCmdItem.dpNONE);
        Multicmd_Vq3009 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3009, MultiCmdItem.dpNONE);
        Multicmd_Vq3010 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3010, MultiCmdItem.dpNONE);
        Multicmd_Vq3011 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3011, MultiCmdItem.dpNONE);
        Multicmd_Vq3012 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3012, MultiCmdItem.dpNONE);
        Multicmd_Vq3013 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3013, MultiCmdItem.dpNONE);
        Multicmd_Vq3014 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3014, MultiCmdItem.dpNONE);
        Multicmd_Vq3015 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3015, MultiCmdItem.dpNONE);
        Multicmd_Vq3016 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3016, MultiCmdItem.dpNONE);
        Multicmd_Vq3017 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3017, MultiCmdItem.dpNONE);
        Multicmd_Vq3018 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3018, MultiCmdItem.dpNONE);
        Multicmd_Vq3019 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3019, MultiCmdItem.dpNONE);
        Multicmd_Vq3020 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3020, MultiCmdItem.dpNONE);
        Multicmd_Vq3021 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3021, MultiCmdItem.dpNONE);
        Multicmd_Vq3022 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3022, MultiCmdItem.dpNONE);
        Multicmd_Vq3023 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3023, MultiCmdItem.dpNONE);
        Multicmd_Vq3540 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3540, MultiCmdItem.dpNONE);
        Multicmd_Vq3080 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3080, MultiCmdItem.dpNONE);
        Multicmd_Vq3081 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3081, MultiCmdItem.dpNONE);
        Multicmd_Vq3082 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3082, MultiCmdItem.dpNONE);
        Multicmd_Vq3083 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3083, MultiCmdItem.dpNONE);
        Multicmd_Vq3520 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3520, MultiCmdItem.dpNONE);
        Multicmd_Vq3521 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3521, MultiCmdItem.dpNONE);
        Multicmd_Vq3522 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3522, MultiCmdItem.dpNONE);
        Multicmd_Vq3523 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3523, MultiCmdItem.dpNONE);
        Multicmd_Vq3525 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3525, MultiCmdItem.dpNONE);
        Multicmd_Vq3530 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3530, MultiCmdItem.dpNONE);
        Multicmd_Vq3531 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3531, MultiCmdItem.dpNONE);
        Multicmd_Vq3070 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3070, MultiCmdItem.dpNONE);
        Multicmd_Vq1913 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1913, MultiCmdItem.dpNONE);
        Multicmd_Vq1914 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1914, MultiCmdItem.dpNONE);
        Multicmd_Vq1915 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1915, MultiCmdItem.dpNONE);
        Multicmd_Vq1916 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1916, MultiCmdItem.dpNONE);
        Multicmd_Vq1917 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1917, MultiCmdItem.dpNONE);
        Multicmd_Vq1918 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1918, MultiCmdItem.dpNONE);
        Multicmd_Vq1919 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1919, MultiCmdItem.dpNONE);
        Multicmd_Vq2913 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2913, MultiCmdItem.dpNONE);
        Multicmd_Vq2914 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2914, MultiCmdItem.dpNONE);
        Multicmd_Vq2915 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2915, MultiCmdItem.dpNONE);
        Multicmd_Vq2916 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2916, MultiCmdItem.dpNONE);
        Multicmd_Vq2917 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2917, MultiCmdItem.dpNONE);
        Multicmd_Vq2918 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2918, MultiCmdItem.dpNONE);
        Multicmd_Vq2919 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2919, MultiCmdItem.dpNONE);
        MultiCmd_Vb21 = sl.Add("Io", 1, MultiCmdItem.dtVB, 21, MultiCmdItem.dpNONE);
        MultiCmd_Vb22 = sl.Add("Io", 1, MultiCmdItem.dtVB, 22, MultiCmdItem.dpNONE);
        Multicmd_Vb23 = sl.Add("Io", 1, MultiCmdItem.dtVB, 23, MultiCmdItem.dpNONE);
        MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);
        Multicmd_vn101 = sl.Add("Io", 1, MultiCmdItem.dtVN, 101, MultiCmdItem.dpNONE);
        Multicmd_vn102 = sl.Add("Io", 1, MultiCmdItem.dtVN, 102, MultiCmdItem.dpNONE);
        Multicmd_vn103 = sl.Add("Io", 1, MultiCmdItem.dtVN, 103, MultiCmdItem.dpNONE);
        Multicmd_vn104 = sl.Add("Io", 1, MultiCmdItem.dtVN, 104, MultiCmdItem.dpNONE);
        MultiCmd_Vq3071 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3071, MultiCmdItem.dpNONE);
        MultiCmd_Vq3085 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3085, MultiCmdItem.dpNONE);
        Multicmd_Vq3054 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3054, MultiCmdItem.dpNONE);
        MultiCmd_Vq3079 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3079, MultiCmdItem.dpNONE);
        MultiCmd_Vq3092 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3092, MultiCmdItem.dpNONE);
        MultiCmd_Vq3093 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3093, MultiCmdItem.dpNONE);
        MultiCmd_Vq3091  = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3091, MultiCmdItem.dpNONE);
        Multicmd_Vb4034_ScaricOnSuRasafilo = sl.Add("Io", 1, MultiCmdItem.dtVB, 4034, MultiCmdItem.dpNONE);
        MultiCmd_Vn180 = sl.Add("Io", 1, MultiCmdItem.dtVN, 180, MultiCmdItem.dpNONE);
        Multicmd_Vq3084_SpostamentoPinzaPezziCorti = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3084, MultiCmdItem.dpNONE);
        Multicmd_Vn120_ClampMode = sl.Add("Io", 1, MultiCmdItem.dtVN, 120, MultiCmdItem.dpNONE);
        Multicmd_Vb170KitTascaCargo = sl.Add("Io", 1, MultiCmdItem.dtVB, 170, MultiCmdItem.dpNONE);
        Multicmd_Vb151EnableCarPattine = sl.Add("Io", 1, MultiCmdItem.dtVB, 151, MultiCmdItem.dpNONE);
        Multicmd_Vq3201  = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3201, MultiCmdItem.dpNONE);
        Multicmd_Vq3202  = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3202, MultiCmdItem.dpNONE);
        Multicmd_Vq3203  = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3203, MultiCmdItem.dpNONE);
        Multicmd_Vq3205  = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3205, MultiCmdItem.dpNONE);

/*
        Multicmd_HMI_C1_UdfVelLavRPM = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3413, MultiCmdItem.dpNONE);
        Multicmd_HMI_C1_UdfPuntiVelIni = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3414, MultiCmdItem.dpNONE);
        Multicmd_HMI_C1_UdfVelIniRPM = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3415, MultiCmdItem.dpNONE);
        Multicmd_HMI_C1_UdfPuntiVelRall = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3416, MultiCmdItem.dpNONE);
        Multicmd_HMI_C1_UdfVelRallRPM = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3417, MultiCmdItem.dpNONE);
        Multicmd_HMI_C1_Udf_FeedG0 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3418, MultiCmdItem.dpNONE);
        Multicmd_HMI_C1_Udf_ValTensione = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3419, MultiCmdItem.dpNONE);
        Multicmd_HMI_C1_Udf_ValEletSopra = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3421, MultiCmdItem.dpNONE);
        Multicmd_HMI_C1_Udf_ValEletSotto = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3422, MultiCmdItem.dpNONE);
        Multicmd_HMI_C1_VelocitaCaricLavoro = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3431, MultiCmdItem.dpNONE);
*/

        Mci_write_Vn101.mci = Multicmd_vn101;
        Mci_write_Vn102.mci = Multicmd_vn102;
        Mci_write_Vn103.mci = Multicmd_vn103;
        Mci_write_Vn104.mci = Multicmd_vn104;
        Mci_write_HMI_C1_VelocitaCaricLavoro.mci = Multicmd_Vq3511;
       // Mci_write_Vq3511_VelLavCaricatore.mci = Multicmd_Vq3511;

        mci_array_read_all = new MultiCmdItem[]{
                MultiCmd_Vq1701, MultiCmd_Vq1702, MultiCmd_Vq1703, MultiCmd_Vq1704, MultiCmd_Vq1705, MultiCmd_Vq1706, MultiCmd_Vq1707,
                MultiCmd_Vq1708, MultiCmd_Vq1709, MultiCmd_Vq1710, MultiCmd_Vq1711, MultiCmd_Vq1712, MultiCmd_Vq1713, MultiCmd_Vq1714, MultiCmd_Vq1715,
                MultiCmd_Vq1716, MultiCmd_Vq1717,
                MultiCmd_Vq2701, MultiCmd_Vq2702, MultiCmd_Vq2703, MultiCmd_Vq2704, MultiCmd_Vq2705, MultiCmd_Vq2706, MultiCmd_Vq2707,
                MultiCmd_Vq2708, MultiCmd_Vq2709, MultiCmd_Vq2710, MultiCmd_Vq2711, MultiCmd_Vq2712, MultiCmd_Vq2713, MultiCmd_Vq2714, MultiCmd_Vq2715,
                MultiCmd_Vq2716, MultiCmd_Vq2717, MultiCmd_Vb4090_DisableRotturaFiloC1, MultiCmd_Vb4091_DisableRotturaFiloC2, MultiCmd_Vb4071_DisableContSpolaC1,
                MultiCmd_Vb4073_DisableContSpolaC2, Multicmd_vq1718, Multicmd_vq1719, Multicmd_vq1720, Multicmd_vq1721, Multicmd_vq2718, Multicmd_vq2719,
                Multicmd_vq2720, Multicmd_vq2721, Multicmd_vn14, Multicmd_vb4095, Multicmd_vb4518, MultiCmd_Vq3592, MultiCmd_Vq3593, MultiCmd_Vq3053, MultiCmd_Vq3063,
                Multicmd_Vb4034_ScaricOnSuRasafilo
        };
        mci_array_read_all_1 = new MultiCmdItem[]{
                MultiCmd_Vq3050, MultiCmd_Vq3051, MultiCmd_Vq3052, Multicmd_vn109, Multicmd_vn110, Multicmd_Vq1921, Multicmd_Vq1922, Multicmd_Vq3501, Multicmd_Vq3502,
                Multicmd_Vq3510, Multicmd_Vq3511, Multicmd_Vq3000, Multicmd_Vq3001, Multicmd_Vq3002, Multicmd_Vq3003, Multicmd_Vq3004, Multicmd_Vq3005, Multicmd_Vq3006, Multicmd_Vq3007,
                Multicmd_Vq3008, Multicmd_Vq3009, Multicmd_Vq3010, Multicmd_Vq3011, Multicmd_Vq3012, Multicmd_Vq3013, Multicmd_Vq3014, Multicmd_Vq3015,
                Multicmd_Vq3016, Multicmd_Vq3017, Multicmd_Vq3018, Multicmd_Vq3019, Multicmd_Vq3020, Multicmd_Vq3021, Multicmd_Vq3022, Multicmd_Vq3023, Multicmd_Vq3540, Multicmd_Vq3080,
                Multicmd_Vq3081, Multicmd_Vq3082, Multicmd_Vq3083, Multicmd_Vq3520, Multicmd_Vq3521, Multicmd_Vq3522,
                Multicmd_Vq3523, Multicmd_Vq3525, Multicmd_Vq3530, Multicmd_Vq3531, Multicmd_Vq3070, Multicmd_Vq1913, Multicmd_Vq1914, Multicmd_Vq1915,
                Multicmd_Vq1916, Multicmd_Vq1917, Multicmd_Vq1918, Multicmd_Vq1919, Multicmd_Vq2913, Multicmd_Vq2914, Multicmd_Vq2915,
                Multicmd_Vq2916, Multicmd_Vq2917, Multicmd_Vq2918, Multicmd_Vq2919, MultiCmd_Vb21,MultiCmd_Vb22, Multicmd_vn101, Multicmd_vn102, Multicmd_vn103, Multicmd_vn104,
                MultiCmd_Vq3071, MultiCmd_Vq3085, Multicmd_Vq3054, Multicmd_Vq3084_SpostamentoPinzaPezziCorti,Multicmd_vb20,Multicmd_Vb23,Multicmd_Vn120_ClampMode,Multicmd_Vb170KitTascaCargo,
                Multicmd_Vb151EnableCarPattine,MultiCmd_Vq3079,MultiCmd_Vq3092,MultiCmd_Vq3093,MultiCmd_Vq3091,MultiCmd_Vn180,Multicmd_Vq3201,Multicmd_Vq3202,Multicmd_Vq3203,Multicmd_Vq3205

        };
    }

    /**
     * Function for init the params list based on the activity that opened this activity
     */
    private void Inizializza_parametri() {


        switch (Chiamante) {
            case PAGE_UDF_T1_DX:
            case PAGE_UDF_T1_SX:

                if(Machine_model.equals("JT862HM")){
                    Lista_parametri.add(new Parametro_mac(Multicmd_HMI_C1_UdfVelLavRPM, 1, 100, 3500, 3500, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C1_UdfVelLavRPM, true, 0.0d, false,0));
                    if((Double)Multicmd_Vq1913.getValue() > 3500.d)
                        Multicmd_Vq1913.setValue(3500.0d);
                            }
                else //tutte le altre macchine M
                    Lista_parametri.add(new Parametro_mac(Multicmd_HMI_C1_UdfVelLavRPM, 1, 100, 4000, 4000, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C1_UdfVelLavRPM, true, 0.0d,false,0));



                Lista_parametri.add(new Parametro_mac(null, 2, 0, 10, 2, false, false, false, true, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C1_UdfPuntiVelIni, true, 0.0d,true,0));
                Lista_parametri.add(new Parametro_mac(null, 3, 100, 4000, 500, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C1_UdfVelIniRPM, true, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(null, 4, 0, 10, 1, false, false, false, true, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C1_UdfPuntiVelRall, true, 0.0d,true,0));
                Lista_parametri.add(new Parametro_mac(null, 5, 100, 500, 200, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C1_UdfVelRallRPM, true, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(null, 6, 10, 100, 80, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C1_Udf_FeedG0, true, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(null, 7, 10, 100, 20, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C1_Udf_ValTensione, true, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(null, 8, 10, 100, 50, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C1_Udf_ValEletSopra, true, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(null, 9, 10, 100, 50, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C1_Udf_ValEletSotto, true, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(null, 10, 1, 100, 90, false, false, true, true, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C1_VelocitaCaricLavoro, true, 100.0d,false,0));


                break;

            case PAGE_UDF_T2_DX:
            case PAGE_UDF_T2_SX:
                Lista_parametri.add(new Parametro_mac(null, 1, 100, 4000, 3000, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C2_UdfVelLavRPM, true, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(null, 2, 0, 10, 3, false, false, false, true, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C2_UdfPuntiVelIni, true, 0.0d,true,0));
                Lista_parametri.add(new Parametro_mac(null, 3, 0, 4000, 500, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C2_UdfVelIniRPM, true, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(null, 4, 0, 10, 3, false, false, false, true, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C2_UdfPuntiVelRall, true, 0.0d,true,0));
                Lista_parametri.add(new Parametro_mac(null, 5, 100, 500, 200, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C2_UdfVelRallRPM, true, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(null, 6, 10, 100, 80, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C2_Udf_FeedG0, true, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(null, 7, 10, 100, 90, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), Mci_write_HMI_C2_Udf_ValTensione, true, 0.0d,false,0));
                break;

            case PAGE_C1_PARAM:
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vb4090_DisableRotturaFiloC1, 1, 0, 1, 1, true, false, false, false, true, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vb4071_DisableContSpolaC1, 2, 0, 1, 1, true, false, false, false, true, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1703, 3, -360, 360, -40, false, false, true, false, true, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1704, 4, 0, 360, 110, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1705, 5, 0, 360, 355, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1706, 6, 0, 360, 115, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1707, 7, 0, 360, 355, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1708, 8, 0.1f, 3, 0.1f, false, true, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1709, 9, 0.1f, 3, 0.1f, false, true, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                if(Machine_model.equals("JT862M"))
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1710, 10, 0, 4000, 4000, false, false, true, false, false, true, true, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                else
                    Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1710, 10, 0, 3500, 3500, false, false, true, false, false, true, true, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_vq1718, 11, 0, 360, 110, false, false, true, true, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_vq1719, 12, 0, 360, 250, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_vq1720, 13, 0, 360, 210, false, false, true, true, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_vq1721, 14, 0, 360, 350, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_vb20, 15, 0, 1, 0, true, false, false, false, true, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3592, 16, 0, 100000, 0, false, false, false, false, false, false, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3593, 17, 0, 100000, 0, false, false, false, false, false, false, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vb21, 18, 0, 1, 0, true, false, false, false, true, true, true, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(mci_null, 19, 0, 0, 0, false, false, false, false, false, false,true, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3510, 20, 1, 100, 80, false, false, true, false, false, true, true, new ArrayList<>(), new ArrayList<>(), null, false, 100.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vb23, 21, 0, 1, 0, true, false, false, false, true, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vb22, 22, 0, 1, 0, true, false, false, false, true, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq1701, 23, -25, 25, 174, false, false, true, false, true, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,-170));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vn120_ClampMode, 24, 0, 1, 1, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vb170KitTascaCargo, 25, 0, 1, 0, true, false, false, false, true, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3092, 26, 20, 270, 30, false, false, true, false, false, true, true, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3091, 27, 0, 5, 0.2f, false, true, true, false, false, true, true, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vn180, 28, 0, 3, 0, false, false, false, false, false, true, true, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));




                break;
            case PAGE_C2_PARAM:
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vb4091_DisableRotturaFiloC2, 1, 0, 1, 1, true, false, false, false, true, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vb4073_DisableContSpolaC2, 2, 0, 1, 1, true, false, false, false, true, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq2703, 3, -360, 360, -40, false, false, true, false, true, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq2704, 4, 0, 360, 90, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq2705, 5, 0, 360, 250, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq2706, 6, 0, 360, 160, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq2707, 7, 0, 360, 355, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq2708, 8, 0, 100, 0.1f, false, true, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq2709, 9, 0, 100, 0.1f, false, true, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq2710, 10, 0, 4000, 4000, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_vq2718, 11, 0, 360, 174, false, false, true, true, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_vq2719, 12, 0, 360, 41, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_vq2720, 13, 0, 360, 210, false, false, true, true, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_vq2721, 14, 0, 360, 350, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq2701, 15, -25, 25, 174, false, false, true, false, true, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,-170));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vb151EnableCarPattine, 16, 0, 1, 0, true, false, false, false, true, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3093, 17, 20, 270, 30, false, false, true, false, false, true, true, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3201, 18, 0, 2000, 300, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3203, 19, 0, 2000, 300, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3205, 20, 0, 2000, 300, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));


                break;
            case PAGE_PARAM_PIEGATORE:
                /*
                Lista_parametri.add( new Parametro_mac(Multicmd_Vq3510,1,1,100,80,false,false,true,false,false,true,true,new ArrayList<>(), new ArrayList<>(),null,false,100.0d));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3006, 2, 0, 100, 0.1f, false, true, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false,0.0d));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3007, 3, 0, 100, 0.1f, false, true, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false,0.0d));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3008, 4, 0, 100, 0.1f, false, true, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false,0.0d));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3009, 5, 0, 100, 0.1f, false, true, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false,0.0d));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3010, 6, 0, 100, 0.1f, false, true, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false,0.0d));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3011, 7, 0, 100, 0.1f, false, true, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false,0.0d));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3012, 8, 0, 100, 0.1f, false, true, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false,0.0d));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3013, 9, 0, 100, 0.1f, false, true, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false,0.0d));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3071, 10, 0, 5, 0.1f, false, true, true,false, false, true, true, new ArrayList<>(), new ArrayList<>(),null,false,0.0d));
*/
                break;
            case PAGE_PARAM_SCARICATORE:
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3540, 1, 0, 150, 20, false, false, false, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3080, 2, 0, 100, 2.0f, false, true, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3082, 3, 0, 100, 0.2f, false, true, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3083, 4, 0, 100, 0.5f, false, true, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3081, 5, 0, 100, 2.0f, false, true, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vb4034_ScaricOnSuRasafilo, 6, 0, 1, 0, true, false, false, false, true, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3084_SpostamentoPinzaPezziCorti, 7, 0, 300, 200, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3071, 8, 0, 5, 1.0f, false, true, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3085, 9, 0, 5, 1.0f, false, true, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(MultiCmd_Vq3079, 10, 0, 5, 1.0f, false, true, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));

                break;
            case PAGE_PARAM_TRASLATORE:
                Lista_parametri.add(new Parametro_mac(Multicmd_vb4518, 1, 0, 1, 1, true, false, false, false, true, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
              //  Lista_parametri.add(new Parametro_mac(Multicmd_Vq3520, 2, 0, 1000, -10, false, false, true, false, true, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
              //  Lista_parametri.add(new Parametro_mac(Multicmd_Vq3521, 3, 0, 1000, 580, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false));
             //   Lista_parametri.add(new Parametro_mac(Multicmd_Vq3522, 4, 0, 1000, 937, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false));
             //   Lista_parametri.add(new Parametro_mac(Multicmd_Vq3523, 5, 0, 1000, 400, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3525, 2, 0, 1000, 500, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3530, 3, 0, 66, 50, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3531, 4, 0, 66, 60, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3070, 5, 0, 100, 0.3f, false, true, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3521, 6, 0, 1000, 580, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));
                Lista_parametri.add(new Parametro_mac(Multicmd_Vq3523, 7, 0, 1000, 400, false, false, true, false, false, true, false, new ArrayList<>(), new ArrayList<>(), null, false, 0.0d,false,0));

                break;
            default:
                break;
        }
        Show_parametri();
    }

    /**
     * Function for init the table row
     */
    private void Inizializza_TableRow() {

        stk.removeAllViews();   //pulisco da eventuali righe

        TableRow tbrow = new TableRow(this);

        // intestazione No.
        TextView tv0 = new TextView(this);
        tv0.setText("No.  ");
        tv0.setTextColor(Color.RED);
        tv0.setTextSize(20);
        tbrow.addView(tv0);
        // intestazione Descrizione
        TextView tv1 = new TextView(this);
        tv1.setText("Description       "
                + "		        	                          ");
        tv1.setTextColor(Color.RED);
        tv1.setTextSize(20);
        tbrow.addView(tv1);

        // intestazione valore
        TextView tv6 = new TextView(this);
        tv6.setText("Value     ");
        tv6.setTextColor(Color.RED);
        tv6.setTextSize(20);
        tbrow.addView(tv6);

        stk.addView(tbrow);
    }

    /**
     * Button for unlock all the params
     *
     * @param view
     */
    public void onclick_send_command(View view) {
        KeyDialog.Lancia_KeyDialogo(null, Parametri_page.this, null, 99999d, 0d, false, false, 0d, true, "KeyDialog_parameter_ret", false,"");
    }

    /**
     * Button for exit from this activity
     *
     * @param view
     */
    public void onclick_button_Exit(View view) {
        Esci();
    }

    /**
     * Button for save the params values in a file
     *
     * @param view
     */
    public void onclick_button_save(View view) {
        SaveParametriTofile();
    }

    /**
     * Button for load the saved params values from the file
     *
     * @param view
     */
    public void onclick_button_load(View view) {

        ArrayList<String> str = new ArrayList<String>();
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "JamData");
            if (!root.exists()) {
                root.mkdirs();
            }
            String nomeFile = "null";
            switch (Chiamante) {

                case PAGE_C1_PARAM:
                    nomeFile = "C1_PARAM";
                    break;
                case PAGE_C2_PARAM:
                    nomeFile = "C2_PARAM";
                    break;
                case PAGE_PARAM_PIEGATORE:
                    nomeFile = "PARAM_PIEGATORE";
                    break;
                case PAGE_PARAM_SCARICATORE:
                    nomeFile = "PARAM_SCARICATORE";
                    break;
                case PAGE_PARAM_TRASLATORE:
                    nomeFile = "PARAM_TRASLATORE";
                    break;

                default:
                    break;


            }
            File file = new File(root, nomeFile + ".pts");
            // append text
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null)
                str.add(line);

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        mci_array_da_aggiornare = new MultiCmdItem[str.size()];
        for (int i = 0; i < str.size(); i++) {
            try {
                String[] dati = str.get(i).split("=");
                for (Parametro_mac parametro_lista : Lista_parametri) {
                    String Key = parametro_lista.mci.getKey();
                    if (Key.equals(dati[0])) {
                        MultiCmdItem mci = parametro_lista.mci;
                        mci.setValue(Double.parseDouble(dati[1]));
                        mci_array_da_aggiornare[i] = mci;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "error par:" + i, Toast.LENGTH_LONG).show();
            }
        }


        Aggiorna_parametri_da_Load = true;
    }

    @Override
    public void onResume() {     // system calls this method as the first indication that the user is leaving your activity
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("KeyDialog_parameter_ret"));
    }

    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
        if (Thread_Running)
            KillThread();
    }

    @Override                   //your activity is no longer visible to the user
    public void onStop() {
        super.onStop();
    }

    @Override                   //your activity is no longer visible to the user
    public void onDestroy() {
        super.onDestroy();
        if (Thread_Running)
            KillThread();
    }

    /**
     * Function for load params from udf
     */
    private void Load_parametri_udf() {
        switch (Chiamante) {

            case PAGE_UDF_T1_DX:
                file_udf = new File(Values.File_XML_path_R);
                if (file_udf.exists()) {

                    ricetta = new Ricetta(Values.plcType);
                    try {
                        ricetta.open(file_udf);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "error opening xml file on Inizializza_parametri", Toast.LENGTH_SHORT).show();
                    }
                    if (ricetta.elements.size() != 0) {

                        Mci_write_HMI_C1_UdfVelLavRPM.valore = Double.valueOf(ricetta.UdfVelLavRPM);
                        Mci_write_HMI_C1_UdfPuntiVelIni.valore = Double.valueOf(ricetta.UdfPuntiVelIni);
                        Mci_write_HMI_C1_UdfVelIniRPM.valore = Double.valueOf(ricetta.UdfVelIniRPM);
                        Mci_write_HMI_C1_UdfPuntiVelRall.valore = Double.valueOf(ricetta.UdfPuntiVelRall);
                        Mci_write_HMI_C1_UdfVelRallRPM.valore = Double.valueOf(ricetta.UdfVelRallRPM);
                        Mci_write_HMI_C1_Udf_FeedG0.valore = Double.valueOf(ricetta.Udf_FeedG0)/1000;
                        Mci_write_HMI_C1_Udf_ValTensione.valore = Double.valueOf(ricetta.Udf_ValTensioneT1);
                        Mci_write_HMI_C1_Udf_ValEletSopra.valore = Double.valueOf(ricetta.Udf_ValElettrocalamitaSopra);
                        Mci_write_HMI_C1_Udf_ValEletSotto.valore = Double.valueOf(ricetta.Udf_ValElettrocalamitaSotto);
                        Mci_write_HMI_C1_VelocitaCaricLavoro.valore = Double.valueOf(ricetta.Udf_VelocitaCaricLavoro);
                        Mci_write_HMI_C1_VelocitaCaricLavoro.write_flag = true;

                        if(Machine_model.equals("JT862HM")){
                            if(Mci_write_HMI_C1_UdfVelLavRPM.valore > 3500.0d) Mci_write_HMI_C1_UdfVelLavRPM.valore = 3500.0d;
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "xml file problem", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Values.File_XML_path_T1_R missing", Toast.LENGTH_SHORT).show();
                }
                break;
            case PAGE_UDF_T1_SX:
                file_udf = new File(Values.File_XML_path_L);
                if (file_udf.exists()) {
                    ricetta = new Ricetta(Values.plcType);

                    try {
                        ricetta.open(file_udf);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "error opening xml file on Inizializza_parametri", Toast.LENGTH_SHORT).show();
                    }

                    if (ricetta.elements.size() != 0) {
                        Mci_write_HMI_C1_UdfVelLavRPM.valore = Double.valueOf(ricetta.UdfVelLavRPM);
                        Mci_write_HMI_C1_UdfPuntiVelIni.valore = Double.valueOf(ricetta.UdfPuntiVelIni);
                        Mci_write_HMI_C1_UdfVelIniRPM.valore = Double.valueOf(ricetta.UdfVelIniRPM);
                        Mci_write_HMI_C1_UdfPuntiVelRall.valore = Double.valueOf(ricetta.UdfPuntiVelRall);
                        Mci_write_HMI_C1_UdfVelRallRPM.valore = Double.valueOf(ricetta.UdfVelRallRPM);
                        Mci_write_HMI_C1_Udf_FeedG0.valore = Double.valueOf(ricetta.Udf_FeedG0)/1000;
                        Mci_write_HMI_C1_Udf_ValTensione.valore = Double.valueOf(ricetta.Udf_ValTensioneT1);
                        Mci_write_HMI_C1_Udf_ValEletSopra.valore = Double.valueOf(ricetta.Udf_ValElettrocalamitaSopra);
                        Mci_write_HMI_C1_Udf_ValEletSotto.valore = Double.valueOf(ricetta.Udf_ValElettrocalamitaSotto);
                        Mci_write_HMI_C1_VelocitaCaricLavoro.valore = Double.valueOf(ricetta.Udf_VelocitaCaricLavoro);
                        Mci_write_HMI_C1_VelocitaCaricLavoro.write_flag = true;

                        if(Machine_model.equals("JT862HM")){
                            if(Mci_write_HMI_C1_UdfVelLavRPM.valore > 3500.0d) Mci_write_HMI_C1_UdfVelLavRPM.valore = 3500.0d;
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "xml file problem", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Values.File_XML_path_T1_L missing", Toast.LENGTH_SHORT).show();
                }
                break;
            case PAGE_UDF_T2_DX:
                file_udf = new File(Values.File_XML_path_T2_R);
                if (file_udf.exists()) {

                    ricetta = new Ricetta(Values.plcType);
                    try {
                        ricetta.open(file_udf);
                    } catch (Exception e) {
                        Toast.makeText(this, "error opening xml file on Inizializza_parametri", Toast.LENGTH_SHORT).show();
                    }
                    if (ricetta.elements.size() != 0) {


                        Mci_write_HMI_C2_UdfVelLavRPM.valore = Double.valueOf(ricetta.UdfVelLavRPM);
                        Mci_write_HMI_C2_UdfPuntiVelIni.valore = Double.valueOf(ricetta.UdfPuntiVelIni);
                        Mci_write_HMI_C2_UdfVelIniRPM.valore = Double.valueOf(ricetta.UdfVelIniRPM);
                        Mci_write_HMI_C2_UdfPuntiVelRall.valore = Double.valueOf(ricetta.UdfPuntiVelRall);
                        Mci_write_HMI_C2_UdfVelRallRPM.valore = Double.valueOf(ricetta.UdfVelRallRPM);
                        Mci_write_HMI_C2_Udf_FeedG0.valore = Double.valueOf(ricetta.Udf_FeedG0)/1000;
                        Mci_write_HMI_C2_Udf_ValTensione.valore = Double.valueOf(ricetta.Udf_ValTensioneT1);


                    } else {

                        Toast.makeText(getApplicationContext(), "xml file problem", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Values.File_XML_path_T2_R missing", Toast.LENGTH_SHORT).show();
                }

                break;
            case PAGE_UDF_T2_SX:
                file_udf = new File(Values.File_XML_path_T2_L);
                if (file_udf.exists()) {

                    ricetta = new Ricetta(Values.plcType);
                    try {
                        ricetta.open(file_udf);
                    } catch (Exception e) {
                        Toast.makeText(this, "error opening xml file on Inizializza_parametri", Toast.LENGTH_SHORT).show();
                    }
                    if (ricetta.elements.size() != 0) {


                        Mci_write_HMI_C2_UdfVelLavRPM.valore = Double.valueOf(ricetta.UdfVelLavRPM);
                        Mci_write_HMI_C2_UdfPuntiVelIni.valore = Double.valueOf(ricetta.UdfPuntiVelIni);
                        Mci_write_HMI_C2_UdfVelIniRPM.valore = Double.valueOf(ricetta.UdfVelIniRPM);
                        Mci_write_HMI_C2_UdfPuntiVelRall.valore = Double.valueOf(ricetta.UdfPuntiVelRall);
                        Mci_write_HMI_C2_UdfVelRallRPM.valore = Double.valueOf(ricetta.UdfVelRallRPM);
                        Mci_write_HMI_C2_Udf_FeedG0.valore = Double.valueOf(ricetta.Udf_FeedG0/1000);
                        Mci_write_HMI_C2_Udf_ValTensione.valore = Double.valueOf(ricetta.Udf_ValTensioneT1);


                    } else {

                        Toast.makeText(getApplicationContext(), "xml file problem", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Values.File_XML_path_T2_L missing", Toast.LENGTH_SHORT).show();
                }
                break;
            case PAGE_C1_PARAM:
                break;
            case PAGE_C2_PARAM:
                break;
            case PAGE_PARAM_PIEGATORE:
                break;
            case PAGE_PARAM_SCARICATORE:
                break;
            case PAGE_PARAM_TRASLATORE:
                break;
        }
    }

    /**
     * Function for display the list of params
     */
    private void Show_parametri() {
        Button_exit.setClickable(false);

        for (int i = 0; i < Lista_parametri.size(); i++) {
            TableRow tbrow = new TableRow(this);
            final Parametro_mac item = Lista_parametri.get(i);

            //no.
            TextView t1v = new TextView(this);
            t1v.setText("" + item.numero_par);
            t1v.setTextSize(15);
            t1v.setTextColor(Color.BLACK);
            t1v.setGravity(Gravity.LEFT);
            t1v.setPadding(0, 15, 0, 15);        //cambiando il padding del testo all'interno del TextView riesco a aumentare lo spazione tra le righe
            tbrow.addView(t1v);

            //Descrizione parametro
            String Stringa_descrizione = "manca";
            try {
                String[] Descrizioni = new String[]{};

                switch (Chiamante) {
                    case PAGE_UDF_T1_DX:
                    case PAGE_UDF_T1_SX:
                        Descrizioni = getResources().getStringArray(R.array.Descrizione_parametri_Udf_Cucitura_T1);
                        Stringa_descrizione = Descrizioni[item.numero_par];
                        break;
                    case PAGE_UDF_T2_DX:
                    case PAGE_UDF_T2_SX:
                        Descrizioni = getResources().getStringArray(R.array.Descrizione_parametri_Udf_Cucitura_T2);
                        Stringa_descrizione = Descrizioni[item.numero_par];
                        break;
                    case PAGE_C1_PARAM:
                        Descrizioni = getResources().getStringArray(R.array.Descrizione_parametri_C1_Param);
                        Stringa_descrizione = Descrizioni[item.numero_par];
                        break;
                    case PAGE_C2_PARAM:
                        Descrizioni = getResources().getStringArray(R.array.Descrizione_parametri_C2_Param);
                        Stringa_descrizione = Descrizioni[item.numero_par];
                        break;
                    case PAGE_PARAM_PIEGATORE:
                        Descrizioni = getResources().getStringArray(R.array.Descrizione_parametri_Piegatore);
                        Stringa_descrizione = Descrizioni[item.numero_par];
                        break;
                    case PAGE_PARAM_SCARICATORE:
                        Descrizioni = getResources().getStringArray(R.array.Descrizione_parametri_Scaricatore);
                        Stringa_descrizione = Descrizioni[item.numero_par];
                        break;
                    case PAGE_PARAM_TRASLATORE:
                        Descrizioni = getResources().getStringArray(R.array.Descrizione_parametri_Traslatore);
                        Stringa_descrizione = Descrizioni[item.numero_par];
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            TextView t2v = new TextView(this);
            t2v.setText("" + Stringa_descrizione);
            t2v.setTextSize(15);
            t2v.setTextColor(Color.BLACK);
            t2v.setGravity(Gravity.LEFT);
            tbrow.addView(t2v);

            //Value mci (probabile proveniente da valori letti dalla shoppinList)
            String valore_string = "";
            Double valore_double = -1d;

            if (!item.udf_valore) {

                if (item.max_percento == 0.0d)
                    valore_double = (Double) item.mci.getValue();
                else {
                    Double v = (Double) item.mci.getValue();
                    valore_double = 1 / (item.max_percento / v) * 100;
                }
            } else {

                if (item.max_percento == 0.0d)
                    valore_double = item.mci_write.valore;
                else {
                    valore_double = 1 / (item.max_percento / item.mci_write.valore) * 100;
                }




            }

            try {
                if (item.lista_str.size() > 0) {
                    try {
                        int idx_attuale = 0;
                        for (int ii = 0; ii < item.lista_str.size(); ii++) {

                            if (Double.compare(valore_double, item.ret_lista_str.get(ii)) == 0) {
                                idx_attuale = ii;
                                break;
                            }
                        }

                        valore_string = item.lista_str.get(idx_attuale);
                    } catch (Exception e) {
                        e.printStackTrace();
                        valore_string = "null";
                    }

                } else {
                    if (item.var_bool == true) {
                        if (valore_double == 1.0d) {
                            valore_string = "ON";
                        } else {
                            valore_string = "OFF";
                        }
                    } else {
                        if (item.decimal == false && item.x1000 == false && item.Div1000 == false) {

                            String val = String.valueOf(valore_double);
                            valore_string = SubString.Before(val, ".");         //tolgo la parte decimale

                        }
                        if (item.x1000 == true  ) {
                            Double valore = (valore_double / 1000) + item.offset;
                            valore_string = String.valueOf(valore);
                            if (item.decimal == false) {
                                valore_string = SubString.Before(valore_string, ".");         //tolgo la parte decimale
                            }
                        }
                        if(item.displayx1000){
                            valore_double = valore_double*1000;
                            String val = String.valueOf(valore_double);
                            valore_string = SubString.Before(val, ".");         //tolgo la parte decimale
                        }
                  //      if(item.displayDIv1000){
                   //         valore_double = valore_double/1000;
                   //         String val = String.valueOf(valore_double);
                   //         valore_string = SubString.Before(val, ".");         //tolgo la parte decimale
                   //     }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            final TextView edt = new TextView(this);
            if (!item.editabile) edt.setTextColor(Color.BLACK);
            else if (!item.password || Password_inserita) edt.setTextColor(Color.BLUE);
            else edt.setTextColor(Color.RED);

            edt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            edt.setText(valore_string);
            edt.setId(item.numero_par);

            //eventi del edittext

            edt.setOnTouchListener(new View.OnTouchListener() {

                //	@SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        if (item.editabile == true && (!(item.password == true) || Password_inserita)) { //!(item.password == true && Values.UserLevel == 0)
                            if (item.lista_str.size() > 0) {
                                try {
                                    String edt_text_attuale = edt.getText().toString();
                                    int idx_attuale = 0;
                                    for (int i = 0; i < item.lista_str.size(); i++) {

                                        if (edt_text_attuale.equals(item.lista_str.get(i))) {
                                            if (i == item.lista_str.size() - 1)
                                                idx_attuale = 0;
                                            else
                                                idx_attuale = i + 1;
                                            break;
                                        }
                                    }

                                    if (item.mci != null)
                                        item.mci.setValue(item.ret_lista_str.get(idx_attuale));
                                    if (item.mci_write != null)
                                        item.mci_write.valore = item.ret_lista_str.get(idx_attuale);
                                    Cambiato_dato = true;
                                    Leggi_dati_da_Cn = true;
                                    Aggiorna_lista_su_schermo = true;
                                    Gestisci_valore_inserito(edt);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    edt.setText("null");
                                }
                            } else {
                                if (item.var_bool) {
                                    try {
                                        String stato = edt.getText().toString();
                                        if (stato.contains("OFF")) {
                                            edt.setText("ON");
                                        } else {
                                            edt.setText("OFF");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        PrintToast("Parameter parse error", Color.BLACK, Color.RED, 50);
                                    }
                                } else {
                                    //KeyDialog.Lancia_KeyDialogo(null, Parametri_page.this, edt, item.max, item.min, item.decimal, item.negativo, item.val_default, item.password, "");
                                    KeyDialog.Lancia_KeyDialogo(null, Parametri_page.this, edt, item.max, item.min, item.decimal, item.negativo, item.val_default, false, "", false,"");
                                }
                            }
                        }
                    }

                    return false;
                }
            });

            edt.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Cambiato_dato = true;
                    Gestisci_valore_inserito(edt);
                }
            });

            tbrow.addView(edt);

            stk.addView(tbrow);
        }
        if (IdFocus != 0) {
            stk.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sv.scrollTo(0, IdFocus * 50);
                    IdFocus = 0;
                }
            }, 100);
        }
        Button_exit.setClickable(true);
    }

    /**
     * Function for print a Toast
     *
     * @param Testo
     * @param sfondo
     * @param colore
     * @param size
     */
    private void PrintToast(String Testo, int sfondo, int colore, float size) {
        Toast toast = Toast.makeText(Parametri_page.this, Testo, Toast.LENGTH_LONG);
        View view = toast.getView();

        //To change the Background of Toast
        view.setBackgroundColor(sfondo);    //Color.TRANSPARENT
        TextView text = view.findViewById(android.R.id.message);

        //Shadow of the Of the Text Color
        text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        text.setTextColor(colore);
        text.setTextSize(size);
        toast.show();
    }

    /**
     * Function for exit from this page
     */
    private void Esci() {
        StopThread = true;
        KillThread();

        finish();
    }

    /**
     * Function for sae params in a file
     *
     * @return
     */
    public boolean SaveParametriTofile() {
        boolean ret = false;
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "JamData");
            if (!root.exists()) {
                root.mkdirs();
            }

            String nomeFile = "null";
            switch (Chiamante) {
                case PAGE_C1_PARAM: {
                    nomeFile = "C1_PARAM";
                }
                break;
                case PAGE_C2_PARAM: {
                    nomeFile = "C2_PARAM";
                }
                break;
                case PAGE_PARAM_PIEGATORE: {
                    nomeFile = "PARAM_PIEGATORE";
                }
                break;
                case PAGE_PARAM_SCARICATORE: {
                    nomeFile = "PARAM_SCARICATORE";
                }
                break;
                case PAGE_PARAM_TRASLATORE: {
                    nomeFile = "PARAM_TRASLATORE";
                }
                break;
                default:
                    break;
            }
            File gpxfile = new File(root, nomeFile + ".pts");
            // append text
            BufferedWriter out = new BufferedWriter(new FileWriter(gpxfile.toString(), false));
            for (Parametro_mac item : Lista_parametri) {
                MultiCmdItem mci = item.mci;
                out.write(mci.getKey() + "=" + mci.getValue().toString() + "\n");
            }
            out.close();
            Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
            ret = true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        return ret;
    }

    /**
     * Function for handle the emergency button pressed
     * <p>
     * THIS IS WRONG:
     * Intent intent_par = new Intent(getApplicationContext(), Emergency_page.class);
     * startActivity(intent_par);
     */
    private void Chiamo_Pagina_emergenza() {
        KillThread();
        Utility.ClearActivitiesTopToEmergencyPage(getApplicationContext());
    }

    /**
     * TODO ???
     *
     * @param edt
     * @throws IOException
     */
    private void Gestisci_valore_inserito(TextView edt) {

        int ID = edt.getId(); //perndo ID del textview modificato
        IdFocus = edt.getId();
        String new_valore = edt.getText().toString();

        Parametro_mac item = Lista_parametri.get(ID - 1);

        if (item.mci != null) {   //controllo se è un dato ShoppingList
            MultiCmdItem mci = item.mci;



            if (item.lista_str.size() <= 0) {
                if (item.var_bool == true) {
                    if (new_valore.equals("OFF")) {
                        mci.setValue(0.0d);
                    } else {
                        mci.setValue(1.0d);
                    }
                } else {
                    try {
                        Double double_valoreNew = Double.parseDouble(new_valore);
                        double_valoreNew = double_valoreNew - item.offset;
                        if (item.x1000) {
                            if (item.max_percento == 0)
                                mci.setValue(double_valoreNew * 1000d);
                            else {
                                Double v = item.max_percento / (100 / double_valoreNew) * 1000;
                                mci.setValue(v);
                            }
                        } else {
                            if (item.max_percento == 0)
                                mci.setValue(double_valoreNew);
                            else {
                                Double v = item.max_percento / (100 / double_valoreNew);
                                mci.setValue(v);
                            }
                        }
                        Inizializza_TableRow();
                    } catch (Exception e) {
                        e.printStackTrace();
                        PrintToast("Parameter parse error", Color.BLACK, Color.RED, 50);
                    }
                }
            }
        }

        if (Chiamante == PAGE_UDF_T1_DX || Chiamante == PAGE_UDF_T1_SX || Chiamante == PAGE_UDF_T2_DX || Chiamante == PAGE_UDF_T2_SX) {   //controllo se è un dato da file udf
            if (item.udf_valore) {
                if (item.var_bool == true) {
                    if (new_valore.equals("OFF")) {
                        item.mci_write.valore = 0.0d;
                    } else {
                        item.mci_write.valore = 1.0d;
                    }
                } else {
                    try {
                        if (item.lista_str.size() == 0) {
                            Double double_valoreNew = Double.parseDouble(new_valore);
                            if (item.x1000) {
                                item.mci_write.valore = (double_valoreNew * 1000d);
                            } else {
                                if (item.Div1000) {
                                    item.mci_write.valore = (double_valoreNew / 1000d);
                                } else {
                                    if (item.max_percento == 0)
                                        item.mci_write.valore = (double_valoreNew);
                                    else {
                                        Double v = item.max_percento / (100 / double_valoreNew);
                                        item.mci_write.valore = v;
                                    }
                                }
                            }
                        }

                        if (Chiamante == PAGE_UDF_T1_DX || Chiamante == PAGE_UDF_T1_SX) {
                            ricetta.UdfVelLavRPM = (int) Math.round(Mci_write_HMI_C1_UdfVelLavRPM.valore);
                            ricetta.UdfPuntiVelIni = Mci_write_HMI_C1_UdfPuntiVelIni.valore;
                            ricetta.UdfVelIniRPM = (int) Math.round(Mci_write_HMI_C1_UdfVelIniRPM.valore);
                            ricetta.UdfPuntiVelRall = Mci_write_HMI_C1_UdfPuntiVelRall.valore;
                            ricetta.UdfVelRallRPM = (int) Math.round(Mci_write_HMI_C1_UdfVelRallRPM.valore);
                            ricetta.Udf_FeedG0 = (int) (Mci_write_HMI_C1_Udf_FeedG0.valore *1000.0d); // (int)vv;
                            ricetta.Udf_ValTensioneT1 = (int) Math.round(Mci_write_HMI_C1_Udf_ValTensione.valore);
                            ricetta.Udf_20 = (int) Math.round(Mci_write_Vq1920_C1_Udf_20.valore);
                            ricetta.Udf_ValElettrocalamitaSopra = (int) Math.round(Mci_write_HMI_C1_Udf_ValEletSopra.valore);
                            ricetta.Udf_ValElettrocalamitaSotto = (int) Math.round(Mci_write_HMI_C1_Udf_ValEletSotto.valore);
                            //ricetta.Udf_SequenzaPiegatore_chiusura = (int) Math.round(Mci_write_Vn109_sequenza_chiusura_piegatore.valore);
                            //ricetta.Udf_SequenzaPiegatore_apetura = (int) Math.round(Mci_write_Vn110_sequenza_apertura_piegatore.valore);
                            ricetta.Udf_VelocitaCaricLavoro = (int) Math.round(Mci_write_HMI_C1_VelocitaCaricLavoro.valore);
                            Mci_write_HMI_C1_VelocitaCaricLavoro.write_flag = true;

                            //aggiorno anche i valori in Values per poi essere eventualmente usati se creo un programma in automatico
                            Values.UdfPuntiVelIni_T1 = Math.round(Double.valueOf(ricetta.UdfPuntiVelIni)*1000.0)/1000.0;
                            Values.UdfVelIniRPM_T1 = ricetta.UdfVelIniRPM;
                            Values.UdfPuntiVelRall_T1 = Math.round(Double.valueOf(ricetta.UdfPuntiVelRall)*1000.0)/1000.0;
                            Values.UdfVelRallRPM_T1 = ricetta.UdfVelRallRPM;
                            Values.Udf_FeedG0_T1 = ricetta.Udf_FeedG0;
                            Values.Udf_ValTensione_T1 = ricetta.Udf_ValTensioneT1;
                            Values.Udf_ValElettrocalamitaSopra_T1 = ricetta.Udf_ValElettrocalamitaSopra;
                            Values.Udf_ValElettrocalamitaSotto_T1 = ricetta.Udf_ValElettrocalamitaSotto;
                            Values.Udf_VelocitaCaricLavoro_T1 = ricetta.Udf_VelocitaCaricLavoro;
                            Values.Udf_SequenzaPiegatore_chiusura_T1 = ricetta.Udf_SequenzaPiegatore_chiusura;
                            Values.Udf_SequenzaPiegatore_apetura_T1 = ricetta.Udf_SequenzaPiegatore_apetura;
                        } else {
                            ricetta.UdfVelLavRPM = (int) Math.round(Mci_write_HMI_C2_UdfVelLavRPM.valore);
                            ricetta.UdfPuntiVelIni = Mci_write_HMI_C2_UdfPuntiVelIni.valore;
                            ricetta.UdfVelIniRPM = (int) Math.round(Mci_write_HMI_C2_UdfVelIniRPM.valore);
                            ricetta.UdfPuntiVelRall = Mci_write_HMI_C2_UdfPuntiVelRall.valore;
                            ricetta.UdfVelRallRPM = (int) Math.round(Mci_write_HMI_C2_UdfVelRallRPM.valore);
                            ricetta.Udf_FeedG0 = (int) Math.round(Mci_write_HMI_C2_Udf_FeedG0.valore *1000.0d);
                            ricetta.Udf_ValTensioneT1 = (int) Math.round(Mci_write_HMI_C2_Udf_ValTensione.valore);


                            Values.UdfPuntiVelIni_T2 = Math.round(Double.valueOf(ricetta.UdfPuntiVelIni)*1000.0)/1000.0;
                            Values.UdfVelIniRPM_T2 = ricetta.UdfVelIniRPM;
                            Values.UdfPuntiVelRall_T2 = Math.round(Double.valueOf(ricetta.UdfPuntiVelRall)*1000.0)/1000.0;
                            Values.UdfVelRallRPM_T2 = ricetta.UdfVelRallRPM;
                            Values.Udf_FeedG0_T2 = ricetta.Udf_FeedG0;
                            Values.Udf_ValTensione_T2 = ricetta.Udf_ValTensioneT1;

                        }

                        ricetta.save(file_udf);
                        databack_parametri.setData(Uri.parse("SI"));
                        setResult(RESULT_OK, databack_parametri);   //indico che ho cambiato almeno un parametro
                    } catch (Exception e) {
                        e.printStackTrace();
                        PrintToast("Parameter parse error", Color.BLACK, Color.RED, 50);
                    }
                }
            }
        }

        Leggi_dati_da_Cn = true;
        Aggiorna_lista_su_schermo = true;
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
    }

    private void KillThread() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        StopThread = true;

        try {
            //  Thread.sleep((long) 200d);
            if (!Thread_Running)
                thread_parametri.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("JAM TAG", "Stop Parametri_page Thread");
    }

    class MyAndroidThread_Parametri implements Runnable {
        Activity activity;

        public MyAndroidThread_Parametri(Activity activity) {
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
                if (sl.IsConnected()) {
                    sl.Clear();
                    sl.ReadItem(MultiCmd_CH1_in_emergenza);
                    if (first_cycle) {
                        first_cycle = false;
                        sl.ReadItems(mci_array_read_all_1);
                        if ((Double) MultiCmd_CH1_in_emergenza.getValue() == 0.0d) Macchina_armata = true;  //se sono entrato è non sono in emergenza inizio a controllarla
                    /*
                        Mci_write_Vq1913_C1_UdfVelLavRPM.valore =(Double) Multicmd_Vq1913.getValue() /1000;
                        Mci_write_Vq1914_C1_UdfPuntiVelIni.valore =(Double) Multicmd_Vq1914.getValue() /1000;
                        Mci_write_Vq1915_C1_UdfVelIniRPM.valore =(Double) Multicmd_Vq1915.getValue()/1000;
                        Mci_write_Vq1916_C1_UdfPuntiVelRall.valore =(Double) Multicmd_Vq1916.getValue()/1000;
                        Mci_write_Vq1917_C1_UdfVelRallRPM.valore =(Double) Multicmd_Vq1917.getValue()/1000;
                        Mci_write_Vq1918_C1_Udf_FeedG0.valore =(Double) Multicmd_Vq1918.getValue();
                        Mci_write_Vq1919_C1_Udf_ValTensioneT1.valore =(Double) Multicmd_Vq1919.getValue()/1000;
                        Mci_write_Vq1921_C1_Udf_ValElettrocalamitaSopra.valore =(Double) Multicmd_Vq1921.getValue()/1000;
                        Mci_write_Vq1922_C1_Udf_ValElettrocalamitaSotto.valore =(Double) Multicmd_Vq1922.getValue()/1000;
                        Mci_write_Vn109_sequenza_chiusura_piegatore.valore =(Double) Multicmd_vn109.getValue()/1000;
                        Mci_write_Vn110_sequenza_apertura_piegatore.valore =(Double) Multicmd_vn110.getValue()/1000;
                        Mci_write_Vq3511_VelLavCaricatore.valore = (Double) Multicmd_Vq3511.getValue();
*/

                    }


                    MultiCmd_Vn3804_pagina_touch.setValue(1006.0d);
                    sl.WriteItem(MultiCmd_Vn3804_pagina_touch);

                    Utility.ScrivoVbVnVq(sl, Mci_write_Vn101);
                    Utility.ScrivoVbVnVq(sl, Mci_write_Vn102);
                    Utility.ScrivoVbVnVq(sl, Mci_write_Vn103);
                    Utility.ScrivoVbVnVq(sl, Mci_write_Vn104);
                    Utility.ScrivoVbVnVq(sl, Mci_write_HMI_C1_VelocitaCaricLavoro);


                    if (Aggiorna_parametri_da_Load) {     //se ho premuto LAOD invio al CN tutti i parametri
                        Aggiorna_parametri_da_Load = false;
                        AggiornaSchermoDopoLoad = true;
                        if (mci_array_da_aggiornare != null && mci_array_da_aggiornare.length > 1)
                            sl.WriteItems(mci_array_da_aggiornare);
                    }

                    if (Cambiato_dato) {

                        sl.WriteItems(mci_array_read_all);
                        sl.WriteItems(mci_array_read_all_1);
                        Cambiato_dato = false;




                        if (Chiamante == PAGE_C1_PARAM) {
                            MultiCmd_Vb1012.setValue(1.0d); //Vb per cambiare file C1_Param e C2_Param
                            sl.WriteItem(MultiCmd_Vb1012);
                        }

                        if (Chiamante == PAGE_C2_PARAM) {
                            MultiCmd_Vb2012.setValue(1.0d); //Vb per cambiare file C1_Param e C2_Param
                            sl.WriteItem(MultiCmd_Vb2012);
                        }
                        trigger_read_da_cn = true;
                    } else {
                        if (Leggi_dati_da_Cn) {
                            String d = "", d1 = "";
                            try {
                                sl.ReadItems(mci_array_read_all);
                            } catch (Exception e) {
                                e.printStackTrace();
                                d = e.toString();
                            }
                            try {
                                sl.ReadItems(mci_array_read_all_1);
                            } catch (Exception e) {
                                e.printStackTrace();
                                d1 = e.toString();
                            }
                            if (sl.getReturnCode() == 0 && d.equals("") && d1.equals("")) {
                                Dati_CN_letti = true;
                            }
                        }
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if ((Double) MultiCmd_CH1_in_emergenza.getValue() == 1.0d && Macchina_armata)
                                Chiamo_Pagina_emergenza();


                            if ((Aggiorna_lista_su_schermo && Dati_CN_letti) || AggiornaSchermoDopoLoad) {

                                Aggiorna_lista_su_schermo = false;
                                Leggi_dati_da_Cn = false;
                                Dati_CN_letti = false;

                                try {
                                    Lista_parametri = new ArrayList<>();
                                    Inizializza_TableRow();
                                    Inizializza_parametri();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            if (AggiornaSchermoDopoLoad) {
                                AggiornaSchermoDopoLoad = false;
                                PrintToast("Load Done", Color.WHITE, Color.RED, 50);
                            }
                        }
                    });
                } else
                    sl.Connect();
            }
        }
    }

    class Parametro_mac {

        public MultiCmdItem mci;
        public int numero_par;
        public float min, max, val_default,offset;
        public boolean var_bool, decimal, x1000, negativo, editabile, password, Div1000, udf_valore,displayx1000;
        public Mci_write mci_write;
        public Double max_percento;
        ArrayList<String> lista_str = new ArrayList<>();
        ArrayList<Double> ret_lista_str = new ArrayList<>();

        public Parametro_mac(MultiCmdItem mci, int numero_par, float min, float max, float val_default, boolean var_bool, boolean decimal, boolean x1000, boolean Div1000, boolean negativo, boolean editabile, boolean password, ArrayList lista_str, ArrayList ret_lista_str, Mci_write mci_write, boolean udf_valore, Double max_percento,boolean displayx1000,float offset) {
            this.mci = mci;
            this.numero_par = numero_par;
            this.min = min;
            this.max = max;
            this.val_default = val_default;
            this.var_bool = var_bool;
            this.decimal = decimal;
            this.x1000 = x1000;
            this.negativo = negativo;
            this.editabile = editabile;
            this.password = password;
            this.lista_str = lista_str;
            this.mci_write = mci_write;
            this.Div1000 = Div1000;
            this.ret_lista_str = ret_lista_str;
            this.udf_valore = udf_valore;
            this.max_percento = max_percento;
            this.displayx1000 = displayx1000;
            this.offset = offset;
        }
    }
}



