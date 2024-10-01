package com.jam_int.jt882_m8;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jamint.ricette.Element;
import com.jamint.ricette.Ricetta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import communication.MultiCmdItem;
import communication.ShoppingList;
import communication.VFKBook;

public class MainActivity extends Activity {

    /**
     * On activity result indexes
     */
    final private static int RESULT_PAGE_TOOLS = 100;
    final private static int RESULT_PAGE_LOAD_UDF_R_T1 = 102;
    final private static int RESULT_PAGE_LOAD_UDF_L_T1 = 103;
    final private static int RESULT_PAGE_TCP = 104;
    final private static int RESULT_PAGE_LOAD_UDF_R_T2 = 109;
    final private static int RESULT_PAGE_LOAD_UDF_L_T2 = 110;
    /**
     * Shopping list for communicate with PLC
     */
    ShoppingList sl;
    /**
     * Thread for communicate with PLC
     */
    Thread thread_Main;
    boolean Thread_Running = false, StopThread = false, first_cycle = true,UdfInSalvataggio = false;
    /**
     * Var used in this page that are in the PLC
     */
    MultiCmdItem[] mci_array_read_all, mci_array_read_all1, mci_array_read_one_shot,mci_array_read_882;
    MultiCmdItem MultiCmd_tasto_verde, MultiCmd_CH1_in_emergenza, MultiCmd_Vb4000_pulsante_Automatico, MultiCmd_Vb4507_stato_ManAut, MultiCmd_Vq1913_C1_UdfVelLavRPM,
             Multicmd_Vb1025_AppReloadParamC1,Multicmd_Vb2025_AppReloadParamC2, Multicmd_dtDB_prog_name, Multicmd_vb1019_Load_Prog,
            MultiCmd_Vb1006_StepPiu_singolo, MultiCmd_Vb1007_StepMeno_singolo, MultiCmd_Vb1022_StepPiu_multiplo, MultiCmd_Vb1023_StepMeno_multiplo, MultiCmd_Vb4802_Reset_Cuci, MultiCmd_Vq1951_punti_totali,MultiCmd_Vb4902_Reset_Cuci_C2,

            MultiCmd_Vb2006_StepPiu_singolo, MultiCmd_Vb2007_StepMeno_singolo, MultiCmd_Vb2022_StepPiu_multiplo, MultiCmd_Vb2023_StepMeno_multiplo,
            MultiCmd_Vq1952_punti_parziali, Multicmd_Vq3596_ContPuntiSpola, Multicmd_Vq3598_ContPuntiSpola_C2,Multicmd_Vq3597_ImpPuntiSpola,Multicmd_Vq3599_ImpPuntiSpola_C2, MultiCmd_Vn3804_pagina_touch, Multicmd_Vb4072_AllarmeContSpola, MultiCmd_posizione_X, MultiCmd_posizione_Y,
            MultiCmd_Vq1110_Speed, MultiCmd_Vb1034_Test_Cuci,MultiCmd_Vb2034_Test_Cuci_C2, MultiCmd_VQ1036_BufErrCode, MultiCmd_VQ1037_BufErrStepNum, MultiCmd_VQ1038_BufErrPar, MultiCmd_Vb1018_SbloccaAgo, MultiCmd_Vn2_allarmi_da_CN,
            MultiCmd_Debug14_prog_cn_in_esecuzione, MultiCmd_Debug8_riga_cn_in_esecuzione, Multicmd_Vq3591_CNT_CicliAutomaticoUser, MultiCmd_Vb1020_piu_Nmulti_step, MultiCmd_Vb1021_meno_Nmulti_step, MultiCmd_Vq1011_numero_multi_step,
            MultiCmd_Vb52_goPC,MultiCmd_Vb53_goPC_C2, MultiCmd_Vb4508_TastoPiegatore, MultiCmd_Vb4509_StatoTastoPiegatore, MultiCmd_Ch1_cucitrice1_Fermo, MultiCmd_Ch2_cucitrice2_Fermo, MultiCmd_Ch3_cicli_Fermo, MultiCmd_Vb4504_ok_per_automatico,
            MultiCmd_Vb4510_TastoCaricatore, MultiCmd_Vb4521_TastoTestPiegatore,MultiCmd_Vb4522_TastoTestTraslatore,MultiCmd_Vb4511_StatoTastoCaricatore, MultiCmd_Vb4514_TastoScaricatore, MultiCmd_Vb4515_StatoTastoScaricatore, MultiCmd_Vb4001_StatusTestPiegatore, MultiCmd_V4507_AppManAuto,MultiCmd_Vb4006_status_test_traslo,
            MultiCmd_Vb4157_C1_Cambio_Pinze_All, MultiCmd_Vb4139_C1_Cambio_Laterali, MultiCmd_Vb4138_C1_Cambio_Frontali, MultiCmd_Vb4137_C1_Cambio_Corpo, MultiCmd_Vb4140_Cambio_spigoli,
            Multicmd_C1_Udf_ValEletSopra, Multicmd_C1_Udf_ValEletSotto, Multicmd_Vb4061_AppTensAumenta,Multicmd_Vb4063_AppTensAumenta, Multicmd_Vb4062_AppTensDiminuisce,Multicmd_Vb4064_AppTensDiminuisce, MultiCmd_Vn_etichetta, MultiCmd_Vb21_DxSx_pocket,
            MultiCmd_Vn196_num_prog_right_C1, MultiCmd_Vn197_num_prog_left_C1, Multicmd_Vb4804_AppReloadParamC1, MultiCmd_i31_AsseX_error, MultiCmd_i32_AsseY_error, MultiCmd_Vn4_Warning, MultiCmd_Vb4806_AppPinzaAltaC1,
            Multicmd_Vb4807_PinzeAlteDopoPC,Multicmd_Vb4907_PinzeAlteDopoPC_C2, Multicmd_Vb4058_C1_AsseX_YFermo, MultiCmd_Vn11McStatoCuci1, MultiCmd_Vn132_DebugPie, MultiCmd_Vb30_C1_InCucitura, MultiCmd_Vb80ShowIconaTools,
            Multicmd_vn109, Multicmd_vn110, Multicmd_Vq3511_VelLavCaricatore, Multicmd_vb4519_ScaricoPinza,MultiCmd_i14_Rinforzo,MultiCmd_vb4060_SalvaUdf,
            Multicmd_C1_Udf_ValTensione,Multicmd_C2_Udf_ValTensione,MultiCmd_Vb4189_C2_Cambio_Pinze_All,MultiCmd_Vb2018_SbloccaAgo_T2,MultiCmd_Vb4512_TastoAbilitaTesta2,MultiCmd_Vb4904_AppPedaleHmiC2,MultiCmd_Vb4901_start_cucitura_T2,
            MultiCmd_Vb4513_StatoAbilitaTesta2,Multicmd_dtDB_progT2_Right_name,Multicmd_vb2019_Load_Prog_T2_Right,MultiCmd_Vq2913_C2_UdfVelLavRPM,MultiCmd_Vq2110_Speed_T2,MultiCmd_Vq2951_punti_totali_C2,
            MultiCmd_Vq2952_punti_parziali_C2,MultiCmd_posizione_X_C2, MultiCmd_posizione_Y_C2,Multicmd_Vb4074_AllarmeContSpola_C2,MultiCmd_Vn198_num_prog_right_C2, MultiCmd_Vn199_num_prog_left_C2,

            Multicmd_Vb4018_TrigrHMITascaCucita, Multicmd_Vb152_Pattina_OnOff,Multicmd_Vb157_Pattina_PassoPasso,MultiCmd_Vb151EnableCarPattine,MultiCmd_Vn180_Device_Rinforzo;
    Mci_write Mci_write_Vb4000_pulsante_Automatico = new Mci_write(),
            Mci_Vb4507_stato_ManAut = new Mci_write(),
            Mci_write_Vq1913_C1_UdfVelLavRPM = new Mci_write(),
           // Mci_write_Vq1914_C1_UdfPuntiVelIni = new Mci_write(),
           // Mci_write_Vq1915_C1_UdfVelIniRPM = new Mci_write(),
           // Mci_write_Vq1916_C1_UdfPuntiVelRall = new Mci_write(),
          //  Mci_write_Vq1917_C1_UdfVelRallRPM = new Mci_write(),
          //  Mci_write_Vq1918_C1_Udf_FeedG0 = new Mci_write(),
            Mci_write_Vb1025_AppReloadParamC1 = new Mci_write(),

            Mci_write_Vb2025_AppReloadParamC2 = new Mci_write(),
            Mci_write_dtDB_prog_name = new Mci_write(),
            Mci_write_vb1019_Load_Prog = new Mci_write(),
            Mci_write_Vb1006_StepPiu_singolo = new Mci_write(),
            Mci_write_Vb1007_StepMeno_singolo = new Mci_write(),
            Mci_write_Vb1022_StepPiu_multiplo = new Mci_write(),
            Mci_write_Vb1023_StepMeno_multiplo = new Mci_write(),
            Mci_write_Vb4802_Reset_Cuci = new Mci_write(),
            Mci_write_Vq1951_punti_totali = new Mci_write(),
            Mci_write_Vq1952_punti_parziali = new Mci_write(),
            Mci_write_Vq3596_ContPuntiSpola = new Mci_write(),
            Mci_write_Vq3597_ImpPuntiSpola = new Mci_write(),
            Mci_write_Vb4072_AllarmeContSpola = new Mci_write(),
            Mci_write_Vq1110_Speed = new Mci_write(),

            Mci_write_Vq2110_Speed = new Mci_write(),
            Mci_write_Vb1034_Test_Cuci = new Mci_write(),
            Mci_VQ1036_BufErrCode = new Mci_write(),
            Mci_write_Vb1018_SbloccaAgo = new Mci_write(),
            Mci_Vn2_allarmi_da_CN = new Mci_write(),
            Mci_write_Vq3591_CNT_CicliAutomaticoUser = new Mci_write(),
            Mci_write_Vb4157_C1_Cambio_Pinze_All = new Mci_write(),
            Mci_write_Vb4139_C1_Cambio_Laterali = new Mci_write(),
            Mci_write_Vb4138_C1_Cambio_Frontali = new Mci_write(),
            Mci_write_Vb4137_C1_Cambio_Corpo = new Mci_write(),
            Mci_write_Vb4140_Cambio_spigoli = new Mci_write(),
            Mci_write_C1_Udf_ValTensione = new Mci_write(),
            Mci_write_Vb4807_PinzeAlteDopoPC = new Mci_write(),

            Mci_write_Vb4907_PinzeAlteDopoPC_C2 = new Mci_write(),
            Mci_write_Vb1020_piu_Nmulti_step = new Mci_write(),
            Mci_write_Vb1021_meno_Nmulti_step = new Mci_write(),
            Mci_write_Vq1011_numero_Nmulti_step = new Mci_write(),
            Mci_write_Vb52_goPC = new Mci_write(),

            Mci_write_Vb53_goPC_C2= new Mci_write(),

            Mci_write_Vb1026_multiStep_salto = new Mci_write(),

            Mci_write_Vb4061_AppTensAumenta = new Mci_write(),
            Mci_write_Vb4062_AppTensDiminuisce = new Mci_write(),
            Mci_write_Vn_etichetta = new Mci_write(),
            Mci_write_Vn196_tasca_right_C1 = new Mci_write(),
            Mci_write_Vn197_tasca_left_C1 = new Mci_write(),
            Mci_write_Vb4804_AppReloadParamC1 = new Mci_write(),
            Mci_Vb4510_TastoCaricatore = new Mci_write(),
            Mci_Vb4511_StatoTastoCaricatore = new Mci_write(),
            Mci_Vb4508_TastoPiegatore = new Mci_write(),
            Mci_Vb4509_StatoTastoPiegatore = new Mci_write(),
            Mci_Vb4514_TastoScaricatore = new Mci_write(),
            Mci_Vb4515_StatoTastoStacker = new Mci_write(),
            Mci_Vb4001_StatusTestPiegatore = new Mci_write(),
            Mci_write_Vn109_sequenza_chiusura_piegatore = new Mci_write(),
            Mci_write_Vn110_sequenza_apertura_piegatore = new Mci_write(),
            Mci_write_Vq3511_VelLavCaricatore = new Mci_write(),
            Mci_write_vb4519_Scarico_pinza = new Mci_write(),
            Mci_Vb4521_TastoTestPiegatore = new Mci_write(),

            Mci_Vb4522_TastoTestTraslatore = new Mci_write(),
            Mci_write_Vb4189_C2_Cambio_Pinze_All = new Mci_write(),
            Mci_write_Vb2018_SbloccaAgo_T2 = new Mci_write(),
            Mci_write_Vb4512_TastoAbilitaTesta2 = new Mci_write(),

            Mci_write_Vb4901_start_cucitura_T2 = new Mci_write(),

            Mci_write_Vb4904_AppPedaleHmiC2= new Mci_write(),
            Mci_write_dtDB_progT2_Right_name = new Mci_write(),
            Mci_write_vb2019_Load_Prog_T2_Right = new Mci_write(),

            Mci_write_Vq2913_C2_UdfVelLavRPM = new Mci_write(),

            Mci_write_Vq2951_punti_totali_C2 = new Mci_write(),

            Mci_write_Vq2952_punti_parziali_C2 = new Mci_write(),

            Mci_write_Vb4074_AllarmeContSpola_C2 = new Mci_write(),

            Mci_write_C2_Udf_ValTensione = new Mci_write(),

            Mci_write_Vb4063_AppTensAumenta = new Mci_write(),

            Mci_write_Vb4064_AppTensDiminuisce = new Mci_write(),

            Mci_write_Vq3598_ContPuntiSpola_C2 = new Mci_write(),

            Mci_write_Vq3599_ImpPuntiSpola_C2 = new Mci_write(),

            Mci_write_Vb2006_StepPiu_singolo =  new Mci_write(),

            Mci_write_Vb2007_StepMeno_singolo = new Mci_write(),

            Mci_write_Vb2022_StepPiu_multiplo = new Mci_write(),

            Mci_write_Vb2023_StepMeno_multiplo = new Mci_write(),

            Mci_write_Vb4902_Reset_Cuci_C2 = new Mci_write(),

            Mci_write_Vb2034_Test_Cuci_C2 = new Mci_write(),

            Mci_write_Vn198_tasca_right_C2 = new Mci_write(),

            Mci_write_Vn199_tasca_left_C2 = new Mci_write(),
            Mci_Vb4006_StatusTestTraslatore = new Mci_write(),

            Mci_write_Vb4018_TrigrHMITascaCucita = new Mci_write(),
            Mci_write_Vb152_Pattina_OnOff = new Mci_write(),

            Mci_write_Vb157_Pattina_PassoPasso = new Mci_write();



    /**
     * UI components
     */
    TextView TextView_val_speed, TextView_punti_tot,TextView_punti_tot_C2, TextView_punti_parziale,TextView_punti_parziale_C2, TextView_Cnt_spola,TextView_Cnt_spola_C2, TextSpola_limite,TextSpola_limite_C2, TextView_errore, TextView_programma_in_esecuzione, TextView_riga_fork,
            TextView_cnt_thread, TextView_val_production, Text_punti_da_saltare, TextView_valore_tensione, TextView_percento,TextView_percento_C2,TextView_nomeprog2, TextView_folder2, TextView_nomeprog_L_val, TextView_folder_L_val,
            TextView_max_speed_value, TextView_Date, TextView_nomeprog_R_val,TextView_nomeprog_R_val_T2,
            TextView_val_speedT2,TextView_max_speed_value_C2,TextView_valore_tensione_C2,TextView_nomeprog_L2_C2,TextView_nomeprog_L_val_C2,TextView_folder_L2_C2,TextView_folder_L_val_C2;
    Button Button_pagina_tools, Button_step_piu,Button_step_piu_C2, Button_step_meno, Button_step_meno_C2, Button_reset,Button_reset_C2, Button_reset_spola,Button_reset_spola_C2, Button_test_cuci,Button_test_cuci_C2, Button_Sgancio_ago,Button_sgancio_ago_T2,

            Button_stato_folder, Button_stato_loader, Button_Stato_Stacker, Button_stato_test_folder,
            Button_step_piu100, Button_step_meno100, Button_Cambio_pinza_C1, Button_cambio_frontale, Button_cambio_laterali, Button_cambio_corpo, Button_cambio_spigoli, Button_tens_piu, Button_tens_meno,Button_tens_piu_C2, Button_tens_meno_C2,
            Button_etichetta, Button_tascaSX, Button_tascaDX, Button_load_R, Button_load_L, Button_loader, Button_Folder, Button_stacker, Button_test_piegatore,Button_test_traslatore, Button_All,Button_All_C2, Button_zoomPiu,Button_zoomPiu_C2,Button_zoomMeno,Button_zoomMeno_C2,
            Button_reset_allarme, Button_Scarico_Pinza,Button_rinforzo,Button_Cambio_pinza_C2,Button_testa_1_2,Button_pedale_singolo,Button_start_cucitura_T2,Button_tascaSX_C2,Button_tascaDX_C2,Button_load_L_C2,

            Button_load_R_C2,Button_Trasl_test_status,Button_4_0,Button_pattina_on_off,Button_pattina_test;

    SeekBar seekBar_speed,seekBar_speed_C2;
    int mc_stati_tasca_dx_sx = 0, seekbar_value = 100, seekbar_value_C2 = 100, cnt_comunicazione = 0;
    String  File_XML_path_R, File_XML_path_L,File_XML_path_R_T2, File_XML_path_L_T2;
    Integer[] Array_foto_allarmi;
    /**
     * Current Ricetta
     */
    Ricetta ricetta_T1,ricetta_T2;
    String[] tab_names = new String[]{};
    CoordPosPinza Coord_Pinza = new CoordPosPinza();
    CoordPosPinza Coord_Pinza_C2 = new CoordPosPinza();
    /**
     * Draw where the ricetta_T1 is represented
     */
    FrameLayout frame_canvas_T1,frame_canvas_T2;
    Dynamic_view myView_T1,myView_T2;
    Double warning_old = 0.0d;
boolean test = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_main);
        // Read the machine model
      //  try {
      //      Machine_model = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "MachineModel", "null", null, null, "Machine_model", getApplicationContext());
       // } catch (IOException e) {
      //      Machine_model="";
      //  }

       // if( Machine_model.equals("JT882M"))
            setContentView(R.layout.activity_main882);
       // else
       //     setContentView(R.layout.activity_main);

        // Setup the ShoppingList
        sl = SocketHandler.getSocket();
        sl.Clear("Io");

        VFKBook.Load(this);     //serve per usare variabili MultiCmdItem.dtDB
        sl.setVFK(VFKBook.getVFK());    //serve per usare variabili MultiCmdItem.dtDB

        frame_canvas_T1 = findViewById(R.id.frameLayout);
        frame_canvas_T2 = findViewById(R.id.frameLayout_C2);

        // Check if write and read permission are guaranteed
        // TODO This is useless, i already check this on Emergency page
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

        Init_TextView();
        Init_mci();
        Init_Button();
        Init_XML();
        Init_Eventi();
        Foto_Allarmi();
        BarSeekSpeed();
        Init_Tcp();

        tab_names = getResources().getStringArray(R.array.allarmi);

        // Start the thread
        if (!Thread_Running) {
            MyAndroidThread_Main myTask_main = new MyAndroidThread_Main(this);
            thread_Main = new Thread(myTask_main, "Main myTask");
            thread_Main.start();
            Log.d("JAM TAG", "Start MainActivity Thread from OnCreate");
        }
    }

    private void Init_Tcp() {
        if(Values.Tcp_enable_status.equals("true")){
            Button_4_0.setVisibility(View.VISIBLE);
            //leggo se il pulsante allo spegnimento precedente della macchina era acceso
            try {
                if(Values.TcpButton.equals("false")) {
                    int image = this.getResources().getIdentifier("ic_4e0", "drawable", this.getPackageName());
                    Button_4_0.setBackground(this.getResources().getDrawable((image)));
                    Button_4_0.setTag(R.drawable.ic_4e0);
                }else {

                    int image = this.getResources().getIdentifier("ic_4e0_press", "drawable", this.getPackageName());
                    Button_4_0.setBackground(this.getResources().getDrawable((image)));
                    Button_4_0.setTag(R.drawable.ic_4e0_press);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else
            Button_4_0.setVisibility(View.GONE);
    }
    //#region Init

    /**
     * Function for init the bar used for the speed
     */
    private void BarSeekSpeed() {
        seekBar_speed = findViewById(R.id.seekBar_speed);
        seekBar_speed.setMax(100);
        seekBar_speed.setProgress(100);

        seekBar_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbar_value = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Double vel = (Double) MultiCmd_Vq1913_C1_UdfVelLavRPM.getValue();
                Double vel_modificata;
                vel_modificata = vel;
                if (seekbar_value < 100) {
                    vel_modificata = vel * (100 - seekbar_value) / 100;
                    vel_modificata = vel - vel_modificata;
                    int IntValue = (int) Math.round(vel_modificata);
                    vel_modificata = Double.valueOf(IntValue);
                }
                vel_modificata = vel_modificata / 1000;
                if (vel_modificata < 100) vel_modificata = 100.0d;
                Mci_write_Vq1110_Speed.valore = vel_modificata;
                Mci_write_Vq1110_Speed.write_flag = true;
                Mci_write_Vb1025_AppReloadParamC1.valore = 1.0d;
                Mci_write_Vb1025_AppReloadParamC1.write_flag = true;
            }
        });

        if(Values.Machine_model.equals("JT882M")){
            seekBar_speed_C2 = findViewById(R.id.seekBar_speed_C2);
            seekBar_speed_C2.setMax(100);
            seekBar_speed_C2.setProgress(100);

            seekBar_speed_C2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    seekbar_value_C2 = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    Double vel = (Double) MultiCmd_Vq2913_C2_UdfVelLavRPM.getValue();
                    Double vel_modificata;
                    vel_modificata = vel;
                    if (seekbar_value_C2 < 100) {
                        vel_modificata = vel * (100 - seekbar_value_C2) / 100;
                        vel_modificata = vel - vel_modificata;
                        int IntValue = (int) Math.round(vel_modificata);
                        vel_modificata = Double.valueOf(IntValue);
                    }
                    vel_modificata = vel_modificata / 1000;
                    if (vel_modificata < 100) vel_modificata = 100.0d;
                    Mci_write_Vq2110_Speed.valore = vel_modificata;
                    Mci_write_Vq2110_Speed.write_flag = true;
                    Mci_write_Vb2025_AppReloadParamC2.valore = 1.0d;
                    Mci_write_Vb2025_AppReloadParamC2.write_flag = true;
                }
            });


        }


    }

    /**
     * Function for init an array that contains all the alarms photo
     */
    private void Foto_Allarmi() {
        // Fill the array of pictures for alarms
        Array_foto_allarmi = new Integer[]{
                0,
                R.drawable.foto_spola,
                R.drawable.foto_sfilatura,
                3,
                4,
                5,
                6,
                7,
                8,
                9,
                10,
                11,
                12,
                13,
                14,
                15,
                16,
                R.drawable.foto_sensore_caricatore1,
                R.drawable.foto_sensore_piegatore_dietro,
                R.drawable.foto_sensore_lancia1,
                R.drawable.foto_sensore_interna1,
                R.drawable.foto_sensore_esterna1,
                22,
                R.drawable.foto_sensore_esterna_not_on,
                R.drawable.foto_sensore_esterna_not_off,
                R.drawable.foto_sensore_interna_not_on,
                R.drawable.foto_sensore_interna_not_off,
                R.drawable.foto_sensore_caricatore1,         //27
                R.drawable.foto_sensore_pieg_avanti,        //28
                R.drawable.foto_sensore_piegatore_dietro,         //29
                R.drawable.foto_sensore_lancia1,         //30
                R.drawable.pieg_giu,                     //31
                R.drawable.pieg_su,                          //32
                R.drawable.sens_pinza_inter_c2,         //33
                R.drawable.sens_pinza_inter_c2,         //34
                R.drawable.sens_traslatore,      //35
                R.drawable.sens_traslatore,      //36
                R.drawable.foto_spola,          //37
                R.drawable.foto_sfilatura,       //38
                39

        };
    }

    /**
     * Function for init the button events
     */
    private void Init_Eventi() {
        CreaEventoStepPiuMeno(Button_step_piu,Button_step_meno,Mci_write_Vb1006_StepPiu_singolo,Mci_write_Vb1022_StepPiu_multiplo,Mci_write_Vb1007_StepMeno_singolo,Mci_write_Vb1023_StepMeno_multiplo);
        CreaEventoStepPiuMeno(Button_step_piu_C2,Button_step_meno_C2,Mci_write_Vb2006_StepPiu_singolo,Mci_write_Vb2022_StepPiu_multiplo,Mci_write_Vb2007_StepMeno_singolo,Mci_write_Vb2023_StepMeno_multiplo);


        EdgeButton.CreaEdgeButton(Mci_write_Vb4802_Reset_Cuci, Button_reset, "ic_tasto_reset_premuto", "ic_tasto_reset", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_write_Vb4072_AllarmeContSpola, Button_reset_spola, "ic_spola_reset_red", "ic_spola_reset_yellow", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_Vb4508_TastoPiegatore, Button_Folder, "ic_folder_press", "ic_folder", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_Vb4510_TastoCaricatore, Button_loader, "ic_loader_press", "ic_loader", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_Vb4514_TastoScaricatore, Button_stacker, "ic_stacker_press", "ic_stacker", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_Vb4521_TastoTestPiegatore, Button_test_piegatore, "ic_test_piegatore_press", "ic_test_piegatore", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_write_Vb4512_TastoAbilitaTesta2, Button_testa_1_2, "ic_una_testa", "ic_due_teste", getApplicationContext());


        Toggle_Button.CreaToggleButton(Mci_write_Vb1018_SbloccaAgo, Button_Sgancio_ago, "ic_sblocca_ago_press", "ic_sblocca_ago", getApplicationContext(), sl);
        Toggle_Button.CreaToggleButton(Mci_write_Vb2018_SbloccaAgo_T2, Button_sgancio_ago_T2,"ic_sblocca_ago_t2_press","ic_sblocca_ago_t2", getApplicationContext(), sl);
        Toggle_Button.CreaToggleButton(Mci_write_Vb1034_Test_Cuci, Button_test_cuci, "ic_test_cucitura_press", "ic_test_cucitura", getApplicationContext(), sl);
        Toggle_Button.CreaToggleButton(Mci_write_vb4519_Scarico_pinza, Button_Scarico_Pinza, "ic_scaricatore_pinza_p", "ic_scaricatore_pinza", getApplicationContext(), sl);

        Toggle_Button.CreaToggleButton(Mci_write_Vb152_Pattina_OnOff, Button_pattina_on_off, "ic_pattina_onoff_press", "ic_pattina_onoff", getApplicationContext(), sl);
        Toggle_Button.CreaToggleButton(Mci_write_Vb157_Pattina_PassoPasso, Button_pattina_test, "ic_pattina_test_press", "ic_pattina_test", getApplicationContext(), sl);


       // Toggle_Button.CreaToggleButton(Mci_write_Vb4512_TastoAbilitaTesta2, Button_testa_1_2, "ic_una_testa", "ic_due_teste", getApplicationContext(), sl);


        CreaEventoEditText(Mci_write_Vq3597_ImpPuntiSpola, TextSpola_limite, 50000d, 0d, false, false, false);
        CreaEventoEditText(Mci_write_C1_Udf_ValTensione, TextView_valore_tensione, 100d, 0d, false, false, false);
        if( Values.Machine_model.equals("JT862HM"))
            CreaEventoEditText(Mci_write_Vq1110_Speed, TextView_val_speed, 3500d, 100d, false, false, false);
        if( Values.Machine_model.equals("JT862M") ||  Values.Machine_model.equals("JT882M"))
            CreaEventoEditText(Mci_write_Vq1110_Speed, TextView_val_speed, 4200d, 100d, false, false, false);
        if( Values.Machine_model.equals("JT882M")) {
            CreaEventoEditText(Mci_write_Vq2110_Speed, TextView_val_speedT2, 4200d, 100d, false, false, false);
            EdgeButton.CreaEdgeButton(Mci_write_Vb4074_AllarmeContSpola_C2, Button_reset_spola_C2, "ic_spola_reset_red", "ic_spola_reset_yellow", getApplicationContext());
            CreaEventoEditText(Mci_write_C2_Udf_ValTensione, TextView_valore_tensione_C2, 100d, 0d, false, false, false);
            CreaEventoEditText(Mci_write_Vq3599_ImpPuntiSpola_C2, TextSpola_limite_C2, 50000d, 0d, false, false, false);
            EdgeButton.CreaEdgeButton(Mci_write_Vb4902_Reset_Cuci_C2, Button_reset_C2, "ic_tasto_reset_premuto", "ic_tasto_reset", getApplicationContext());
            Toggle_Button.CreaToggleButton(Mci_write_Vb2034_Test_Cuci_C2, Button_test_cuci_C2, "ic_test_cucitura_c2_press", "ic_test_cucitura_c2", getApplicationContext(), sl);
            EdgeButton.CreaEdgeButton(Mci_write_Vb4904_AppPedaleHmiC2, Button_pedale_singolo, "pedale_singolo_press", "pedale_singolo", getApplicationContext());
            EdgeButton.CreaEdgeButton(Mci_write_Vb4901_start_cucitura_T2, Button_start_cucitura_T2, "ic_start_cucitura_press", "ic_start_cucitura_c2", getApplicationContext());
            EdgeButton.CreaEdgeButton(Mci_Vb4522_TastoTestTraslatore, Button_test_traslatore, "ic_loader2_test_press", "ic_loader2_test", getApplicationContext());

        }
        CreoEventoButtonEtichetta(Button_etichetta);
    }

    /**
     * Function for init the textview elements
     * <p>
     * NOTE:
     * This need always to be after the seekbar init
     */
    private void Init_TextView() {
        TextView_val_speed = findViewById(R.id.textView_speed_val);
        TextView_val_speedT2 = (TextView)findViewById(R.id.textView_speed_val_C2);
        TextView_punti_tot = findViewById(R.id.textView_punti_tot);
        TextView_punti_tot_C2 = findViewById(R.id.textView_punti_tot_C2);
        TextView_punti_parziale = findViewById(R.id.textView_punti_parziale);
        TextView_punti_parziale_C2 = findViewById(R.id.textView_punti_parziale_C2);
        TextView_Cnt_spola = findViewById(R.id.textView_Cnt_spola);
        TextView_Cnt_spola_C2 = findViewById(R.id.textView_Cnt_spola_C2);
        TextSpola_limite = findViewById(R.id.textSpola_limite);
        TextSpola_limite_C2 = findViewById(R.id.textSpola_limite_C2);
        TextView_errore = findViewById(R.id.textView_errore);
        TextView_programma_in_esecuzione = findViewById(R.id.textView_programma_in_esecuzione);
        TextView_riga_fork = findViewById(R.id.textView_riga_fork);
        TextView_cnt_thread = findViewById(R.id.textView_cnt_thread);
        TextView_val_production = findViewById(R.id.textView_val_production);
        TextView_valore_tensione = findViewById(R.id.textView_valore_tensione);
        TextView_valore_tensione_C2 = findViewById(R.id.textView_valore_tensione_C2);
        TextView_nomeprog2 = findViewById(R.id.textView_nomeprog_L2);
        TextView_folder2 = findViewById(R.id.textView_folder_L2);
        TextView_nomeprog_L_val = findViewById(R.id.textView_nomeprog_L_val);
        TextView_nomeprog_R_val_T2 = findViewById(R.id.textView_nomeprog_R_val_C2);
        TextView_folder_L_val = findViewById(R.id.textView_folder_L_val);
        TextView_max_speed_value = findViewById(R.id.textView_max_speed_value);
        TextView_max_speed_value_C2 = findViewById(R.id.textView_max_speed_value_C2);
        TextView_Date = findViewById(R.id.textView_data);
        TextView_nomeprog_R_val = findViewById(R.id.textView_nomeprog_R_val);
        TextView_nomeprog_L2_C2 = findViewById(R.id.textView_nomeprog_L2_C2);
        TextView_nomeprog_L_val_C2 = findViewById(R.id.textView_nomeprog_L_val_C2);
        TextView_folder_L2_C2 = findViewById(R.id.textView_folder_L2_C2);
        TextView_folder_L_val_C2 = findViewById(R.id.textView_folder_L_val_C2);
        TextView_percento = findViewById(R.id.textView_percento);
        TextView_percento_C2 = findViewById(R.id.textView_percento_C2);


        TextView_errore.setText("");
        TextView_punti_parziale.setText("0");
        if(Values.Machine_model.equals("JT882M"))
            TextView_punti_parziale_C2.setText("0");

        // Setup the event when the text change (put the same value on the seekbar)
        TextView_val_speed.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double speed = 0.0d;
                double speedmax = 0.0d;

                try {
                    String speed_touch = String.valueOf(s);
                    speed = Float.parseFloat(speed_touch);
                    String speed_max = TextView_max_speed_value.getText().toString();
                    speedmax = Float.parseFloat(speed_max);

                    double delta = speed / speedmax;
                    seekBar_speed.setProgress((100 * (int) (delta * 100)) / 100);
                    Mci_write_Vb1025_AppReloadParamC1.valore = 1.0d;
                    Mci_write_Vb1025_AppReloadParamC1.write_flag = true;



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        TextView_val_speedT2.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double speed = 0.0d;
                double speedmax = 0.0d;

                try {
                    String speed_touch = String.valueOf(s);
                    speed = Float.parseFloat(speed_touch);
                    String speed_max = TextView_max_speed_value_C2.getText().toString();
                    speedmax = Float.parseFloat(speed_max);

                    double delta = speed / speedmax;
                    seekBar_speed_C2.setProgress((100 * (int) (delta * 100)) / 100);
                    Mci_write_Vb2025_AppReloadParamC2.valore = 1.0d;
                    Mci_write_Vb2025_AppReloadParamC2.write_flag = true;



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Function for init the XMLs
     */
    private void Init_XML() {
        try {
             // Read last program right
            File_XML_path_R = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram_R", getApplicationContext());

            // Check if last program is initialized
            if (File_XML_path_R != "" && File_XML_path_R != null) {
                // Set the program as active program
                Values.File_XML_path_R = File_XML_path_R;

                File file = new File(File_XML_path_R);
                int i = file.getName().lastIndexOf('.');
                String name = file.getName().substring(0, i);
                // Display the name on the textview
                TextView_nomeprog_R_val.setText(name);

                TextView TextView_folder_val = findViewById(R.id.textView_folder_R_val);
                i = file.getPath().lastIndexOf('/');
                name = file.getPath().substring(0, i);
                // Display the folder in the textview
                TextView_folder_val.setText(name);
            }

            // Read last program left
            File_XML_path_L = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram_L", getApplicationContext());
            // Check if last program is initialized
            if (File_XML_path_L != "" && File_XML_path_L != null) {
                // Set the program as active program
                Values.File_XML_path_L = File_XML_path_L;

                File file_L = new File(File_XML_path_L);
                int i_L = file_L.getName().lastIndexOf('.');
                String name_L = file_L.getName().substring(0, i_L);
                // Display the name on the textview
                TextView_nomeprog_L_val.setText(name_L);

                i_L = file_L.getPath().lastIndexOf('/');
                name_L = file_L.getPath().substring(0, i_L);
                // Display the folder in the textview
                TextView_folder_L_val.setText(name_L);
            }

            // Load the right program
            Carica_programma(File_XML_path_R,"T1", "DX");

            if(Values.Machine_model.equals("JT882M")){

                File_XML_path_R_T2 = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram_R_T2", getApplicationContext());

                // Check if last program is initialized
                if (File_XML_path_R_T2 != "" && File_XML_path_R_T2 != null) {
                    // Set the program as active program
                    Values.File_XML_path_T2_R = File_XML_path_R_T2;

                    File file = new File(File_XML_path_R_T2);
                    int i = file.getName().lastIndexOf('.');
                    String name = file.getName().substring(0, i);
                    // Display the name on the textview
                    TextView_nomeprog_R_val_T2.setText(name);

                    TextView TextView_folder_val = findViewById(R.id.textView_folder_R_val_C2);
                    i = file.getPath().lastIndexOf('/');
                    name = file.getPath().substring(0, i);
                    // Display the folder in the textview
                    TextView_folder_val.setText(name);
                }

                // Read last program left
                File_XML_path_L_T2 = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram_L_T2", getApplicationContext());
                // Check if last program is initialized
                if (File_XML_path_L_T2 != "" && File_XML_path_L_T2 != null) {
                    // Set the program as active program
                    Values.File_XML_path_T2_L = File_XML_path_L_T2;

                    File file_L = new File(File_XML_path_L_T2);
                    int i_L = file_L.getName().lastIndexOf('.');
                    String name_L = file_L.getName().substring(0, i_L);
                    // Display the name on the textview
                    TextView_nomeprog_L_val_C2.setText(name_L);

                    i_L = file_L.getPath().lastIndexOf('/');
                    name_L = file_L.getPath().substring(0, i_L);
                    // Display the folder in the textview
                    TextView_folder_L_val_C2.setText(name_L);
                }

                // Load the right program
                Carica_programma(File_XML_path_R_T2,"T2", "DX");

            }


        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "info_Jam.txt is missing", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Function for init the buttons
     */
    private void Init_Button() {
        Button_pagina_tools = findViewById(R.id.btn_pagina_tools);
        Button_step_piu = findViewById(R.id.button_step_piu);
        Button_step_piu_C2 = findViewById(R.id.button_step_piu_C2);
        Button_step_meno_C2 = findViewById(R.id.button_step_meno_C2);
        Button_step_meno = findViewById(R.id.button_step_meno);
        Button_reset = findViewById(R.id.button_reset);
        Button_reset_C2 = findViewById(R.id.button_reset_C2);
        Button_reset_spola = findViewById(R.id.button_reset_spola);
        Button_reset_spola_C2 = findViewById(R.id.button_reset_spola_C2);
        Button_test_cuci = findViewById(R.id.btn_test_cuci);
        Button_test_cuci_C2 = findViewById(R.id.btn_test_cuci_C2);
        Button_Sgancio_ago = findViewById(R.id.btn_sgancio_ago);
        Button_sgancio_ago_T2  = (Button)findViewById(R.id.btn_sgancio_ago_T2);
        Button_stato_folder = findViewById(R.id.button_stato_folder);
        Button_stato_loader = findViewById(R.id.button_stato_loader);
        Button_Stato_Stacker = findViewById(R.id.button_stato_stacker);
        Button_stato_test_folder = findViewById(R.id.button_stato_test_folder);
        Button_Cambio_pinza_C1 = findViewById(R.id.btn_Cambio_pinza);
        Button_Cambio_pinza_C2 = (Button)findViewById(R.id.btn_Cambio_pinza_C2);
        Button_cambio_frontale = findViewById(R.id.btn_cambio_frontale);
        Button_cambio_laterali = findViewById(R.id.btn_cambio_laterali);
        Button_cambio_corpo = findViewById(R.id.btn_cambio_corpo);
        Button_cambio_spigoli = findViewById(R.id.btn_cambio_spigoli);
        Button_tens_piu = findViewById(R.id.button_tens_piu);
        Button_tens_piu_C2 = findViewById(R.id.button_tens_piu_C2);
        Button_tens_meno = findViewById(R.id.button_tens_meno);
        Button_tens_meno_C2 = findViewById(R.id.button_tens_meno_C2);
        Button_etichetta = findViewById(R.id.btn_etichetta);
        Button_tascaSX = findViewById(R.id.button_tascaSX);
        Button_tascaSX_C2 = findViewById(R.id.button_tascaSX_C2);
        Button_tascaDX = findViewById(R.id.button_tascaDX);
        Button_tascaDX_C2 = findViewById(R.id.button_tascaDX_C2);
        Button_load_R = findViewById(R.id.button_load_R);
        Button_load_L = findViewById(R.id.button_load_L);
        Button_load_L_C2 = findViewById(R.id.button_load_L_C2);
        Button_load_R_C2 = findViewById(R.id.button_load_R_C2);
        Button_Trasl_test_status = findViewById(R.id.btn_Trasl_test_status);
        Button_loader = findViewById(R.id.btn_Loader);
        Button_Folder = findViewById(R.id.btn_Folder);
        Button_stacker = findViewById(R.id.btn_stacker);
        Button_test_piegatore = findViewById(R.id.button_test_piegatore);
        Button_test_traslatore = findViewById(R.id.btn_Trasl_test);
        Button_All = findViewById(R.id.btn_All);
        Button_All_C2= findViewById(R.id.btn_All_C2);
        Button_zoomPiu = findViewById(R.id.btn_zoomPiu);
        Button_zoomPiu_C2 = findViewById(R.id.btn_zoomPiu_C2);
        Button_zoomMeno = findViewById(R.id.btn_zoomMeno);
        Button_zoomMeno_C2 = findViewById(R.id.btn_zoomMeno_C2);
        Button_reset_allarme = findViewById(R.id.button_reset_allarme);
        Button_reset_allarme.setVisibility(View.GONE);
        Button_Scarico_Pinza = findViewById(R.id.btn_Scarico_pinza);
        Button_rinforzo = findViewById(R.id.btn_rinforzo);
        Button_testa_1_2 = (Button)findViewById(R.id.btn_testa_1_2);
        Button_start_cucitura_T2   = (Button)findViewById(R.id.btn_start_cucitura_T2);
        Button_pedale_singolo = (Button)findViewById(R.id.btn_pedale_singolo);
        Button_4_0 = findViewById(R.id.btn_4_0);
        Button_pattina_on_off = findViewById(R.id.btn_pattina_on_off);
        Button_pattina_test= findViewById(R.id.btn_pattina_test);
    }

    /**
     * Function for init the multi cmd
     */
    private void Init_mci() {
        MultiCmd_Vq1110_Speed = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1110, MultiCmdItem.dpNONE);
        MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 21, MultiCmdItem.dpNONE);
        MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
        MultiCmd_Vb4507_stato_ManAut = sl.Add("Io", 1, MultiCmdItem.dtVB, 4507, MultiCmdItem.dpNONE);
        MultiCmd_Vb4000_pulsante_Automatico = sl.Add("Io", 1, MultiCmdItem.dtVB, 4000, MultiCmdItem.dpNONE);
        MultiCmd_Vq1913_C1_UdfVelLavRPM = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1913, MultiCmdItem.dpNONE);
        MultiCmd_Vq2913_C2_UdfVelLavRPM = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2913, MultiCmdItem.dpNONE);
        Multicmd_Vb1025_AppReloadParamC1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 1025, MultiCmdItem.dpNONE);
        Multicmd_Vb2025_AppReloadParamC2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 2025, MultiCmdItem.dpNONE);
        Multicmd_dtDB_prog_name = sl.Add("Io", 1, MultiCmdItem.dtDB, 30, MultiCmdItem.dpDB_MAIN1);
        MultiCmd_Debug14_prog_cn_in_esecuzione = sl.Add("Io", 1, MultiCmdItem.dtDB, 14, MultiCmdItem.dpDB_MAIN1);
        MultiCmd_Debug8_riga_cn_in_esecuzione = sl.Add("Io", 1, MultiCmdItem.dtDB, 8, MultiCmdItem.dpDB_MAIN1);
        Multicmd_vb1019_Load_Prog = sl.Add("Io", 1, MultiCmdItem.dtVB, 1019, MultiCmdItem.dpNONE);
        MultiCmd_Vb1006_StepPiu_singolo = sl.Add("Io", 1, MultiCmdItem.dtVB, 1006, MultiCmdItem.dpNONE);
        MultiCmd_Vb1007_StepMeno_singolo = sl.Add("Io", 1, MultiCmdItem.dtVB, 1007, MultiCmdItem.dpNONE);
        MultiCmd_Vb1022_StepPiu_multiplo = sl.Add("Io", 1, MultiCmdItem.dtVB, 1022, MultiCmdItem.dpNONE);
        MultiCmd_Vb1023_StepMeno_multiplo = sl.Add("Io", 1, MultiCmdItem.dtVB, 1023, MultiCmdItem.dpNONE);
        MultiCmd_Vb2006_StepPiu_singolo = sl.Add("Io", 1, MultiCmdItem.dtVB, 2006, MultiCmdItem.dpNONE);
        MultiCmd_Vb2007_StepMeno_singolo = sl.Add("Io", 1, MultiCmdItem.dtVB, 2007, MultiCmdItem.dpNONE);
        MultiCmd_Vb2022_StepPiu_multiplo = sl.Add("Io", 1, MultiCmdItem.dtVB, 2022, MultiCmdItem.dpNONE);
        MultiCmd_Vb2023_StepMeno_multiplo = sl.Add("Io", 1, MultiCmdItem.dtVB, 2023, MultiCmdItem.dpNONE);
        MultiCmd_Vb4802_Reset_Cuci = sl.Add("Io", 1, MultiCmdItem.dtVB, 4802, MultiCmdItem.dpNONE);
        MultiCmd_Vb4902_Reset_Cuci_C2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4902, MultiCmdItem.dpNONE);
        MultiCmd_Vq1951_punti_totali = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1951, MultiCmdItem.dpNONE);
        MultiCmd_Vq2951_punti_totali_C2 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2951, MultiCmdItem.dpNONE);
        MultiCmd_Vq1952_punti_parziali = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1952, MultiCmdItem.dpNONE);
        MultiCmd_Vq2952_punti_parziali_C2 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2952, MultiCmdItem.dpNONE);
        Multicmd_Vq3596_ContPuntiSpola = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3596, MultiCmdItem.dpNONE);
        Multicmd_Vq3597_ImpPuntiSpola = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3597, MultiCmdItem.dpNONE);
        Multicmd_Vq3598_ContPuntiSpola_C2 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3598, MultiCmdItem.dpNONE);
        Multicmd_Vq3599_ImpPuntiSpola_C2 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3599, MultiCmdItem.dpNONE);
        MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);
        Multicmd_Vb4072_AllarmeContSpola = sl.Add("Io", 1, MultiCmdItem.dtVB, 4072, MultiCmdItem.dpNONE);
        Multicmd_Vb4074_AllarmeContSpola_C2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4074, MultiCmdItem.dpNONE);
        MultiCmd_posizione_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 51, MultiCmdItem.dpNONE);
        MultiCmd_posizione_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 52, MultiCmdItem.dpNONE);
        MultiCmd_posizione_X_C2 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 56, MultiCmdItem.dpNONE);
        MultiCmd_posizione_Y_C2 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 57, MultiCmdItem.dpNONE);
        MultiCmd_Vb1034_Test_Cuci = sl.Add("Io", 1, MultiCmdItem.dtVB, 1034, MultiCmdItem.dpNONE);
        MultiCmd_Vb2034_Test_Cuci_C2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 2034, MultiCmdItem.dpNONE);
        MultiCmd_VQ1036_BufErrCode = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1036, MultiCmdItem.dpNONE);
        MultiCmd_VQ1037_BufErrStepNum = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1037, MultiCmdItem.dpNONE);
        MultiCmd_VQ1038_BufErrPar = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1038, MultiCmdItem.dpNONE);
        MultiCmd_Vb1018_SbloccaAgo = sl.Add("Io", 1, MultiCmdItem.dtVB, 1018, MultiCmdItem.dpNONE);
        MultiCmd_Vn2_allarmi_da_CN = sl.Add("Io", 1, MultiCmdItem.dtVN, 2, MultiCmdItem.dpNONE);
        Multicmd_Vq3591_CNT_CicliAutomaticoUser = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3591, MultiCmdItem.dpNONE);
        MultiCmd_Vb1020_piu_Nmulti_step = sl.Add("Io", 1, MultiCmdItem.dtVB, 1020, MultiCmdItem.dpNONE);
        MultiCmd_Vb1021_meno_Nmulti_step = sl.Add("Io", 1, MultiCmdItem.dtVB, 1021, MultiCmdItem.dpNONE);
        MultiCmd_Vq1011_numero_multi_step = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1011, MultiCmdItem.dpNONE);
        MultiCmd_Vb52_goPC = sl.Add("Io", 1, MultiCmdItem.dtVB, 52, MultiCmdItem.dpNONE);
        MultiCmd_Vb53_goPC_C2= sl.Add("Io", 1, MultiCmdItem.dtVB, 53, MultiCmdItem.dpNONE);
        MultiCmd_Vb4508_TastoPiegatore = sl.Add("Io", 1, MultiCmdItem.dtVB, 4508, MultiCmdItem.dpNONE);
        MultiCmd_Vb4509_StatoTastoPiegatore = sl.Add("Io", 1, MultiCmdItem.dtVB, 4509, MultiCmdItem.dpNONE);
        MultiCmd_Ch1_cucitrice1_Fermo = sl.Add("Io", 1, MultiCmdItem.dtVB, 7908, MultiCmdItem.dpNONE);
        MultiCmd_Ch2_cucitrice2_Fermo = sl.Add("Io", 1, MultiCmdItem.dtVB, 7928, MultiCmdItem.dpNONE);
        MultiCmd_Ch3_cicli_Fermo = sl.Add("Io", 1, MultiCmdItem.dtVB, 7948, MultiCmdItem.dpNONE);
        MultiCmd_Vb4504_ok_per_automatico = sl.Add("Io", 1, MultiCmdItem.dtVB, 4504, MultiCmdItem.dpNONE);
        MultiCmd_Vb4510_TastoCaricatore = sl.Add("Io", 1, MultiCmdItem.dtVB, 4510, MultiCmdItem.dpNONE);
        MultiCmd_Vb4521_TastoTestPiegatore  = sl.Add("Io", 1, MultiCmdItem.dtVB, 4521, MultiCmdItem.dpNONE);
        MultiCmd_Vb4522_TastoTestTraslatore= sl.Add("Io", 1, MultiCmdItem.dtVB, 4522, MultiCmdItem.dpNONE);
        MultiCmd_Vb4511_StatoTastoCaricatore = sl.Add("Io", 1, MultiCmdItem.dtVB, 4511, MultiCmdItem.dpNONE);
        MultiCmd_Vb4514_TastoScaricatore = sl.Add("Io", 1, MultiCmdItem.dtVB, 4514, MultiCmdItem.dpNONE);
        MultiCmd_Vb4515_StatoTastoScaricatore = sl.Add("Io", 1, MultiCmdItem.dtVB, 4515, MultiCmdItem.dpNONE);
        MultiCmd_Vb4001_StatusTestPiegatore = sl.Add("Io", 1, MultiCmdItem.dtVB, 4001, MultiCmdItem.dpNONE);
        MultiCmd_Vb4006_status_test_traslo = sl.Add("Io", 1, MultiCmdItem.dtVB, 4006, MultiCmdItem.dpNONE);
        MultiCmd_V4507_AppManAuto = sl.Add("Io", 1, MultiCmdItem.dtVB, 4507, MultiCmdItem.dpNONE);
        MultiCmd_Vb4157_C1_Cambio_Pinze_All = sl.Add("Io", 1, MultiCmdItem.dtVB, 4157, MultiCmdItem.dpNONE);
        MultiCmd_Vb4139_C1_Cambio_Laterali = sl.Add("Io", 1, MultiCmdItem.dtVB, 4139, MultiCmdItem.dpNONE);
        MultiCmd_Vb4138_C1_Cambio_Frontali = sl.Add("Io", 1, MultiCmdItem.dtVB, 4138, MultiCmdItem.dpNONE);
        MultiCmd_Vb4137_C1_Cambio_Corpo = sl.Add("Io", 1, MultiCmdItem.dtVB, 4137, MultiCmdItem.dpNONE);
        MultiCmd_Vb4140_Cambio_spigoli = sl.Add("Io", 1, MultiCmdItem.dtVB, 4140, MultiCmdItem.dpNONE);
        Multicmd_C1_Udf_ValTensione = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1919, MultiCmdItem.dpNONE);
        Multicmd_C2_Udf_ValTensione = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2919, MultiCmdItem.dpNONE);
        Multicmd_Vb4061_AppTensAumenta = sl.Add("Io", 1, MultiCmdItem.dtVB, 4061, MultiCmdItem.dpNONE);
        Multicmd_Vb4063_AppTensAumenta = sl.Add("Io", 1, MultiCmdItem.dtVB, 4063, MultiCmdItem.dpNONE);
        Multicmd_Vb4062_AppTensDiminuisce = sl.Add("Io", 1, MultiCmdItem.dtVB, 4062, MultiCmdItem.dpNONE);
        Multicmd_Vb4064_AppTensDiminuisce = sl.Add("Io", 1, MultiCmdItem.dtVB, 4064, MultiCmdItem.dpNONE);
        MultiCmd_Vn_etichetta = sl.Add("Io", 1, MultiCmdItem.dtVN, 111, MultiCmdItem.dpNONE);
        MultiCmd_Vb21_DxSx_pocket = sl.Add("Io", 1, MultiCmdItem.dtVB, 21, MultiCmdItem.dpNONE);
        MultiCmd_Vn196_num_prog_right_C1 = sl.Add("Io", 1, MultiCmdItem.dtVN, 196, MultiCmdItem.dpNONE);    //JT862 JT882Master
        MultiCmd_Vn197_num_prog_left_C1 = sl.Add("Io", 1, MultiCmdItem.dtVN, 197, MultiCmdItem.dpNONE);
        Multicmd_Vb4804_AppReloadParamC1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4804, MultiCmdItem.dpNONE);   //vecchio firmware da togliere
        MultiCmd_i31_AsseX_error = sl.Add("Io", 1, MultiCmdItem.dtDI, 31, MultiCmdItem.dpNONE);
        MultiCmd_i32_AsseY_error = sl.Add("Io", 1, MultiCmdItem.dtDI, 32, MultiCmdItem.dpNONE);
        MultiCmd_Vn4_Warning = sl.Add("Io", 1, MultiCmdItem.dtVN, 4, MultiCmdItem.dpNONE);
        MultiCmd_Vb4806_AppPinzaAltaC1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4806, MultiCmdItem.dpNONE);
        Multicmd_Vb4807_PinzeAlteDopoPC = sl.Add("Io", 1, MultiCmdItem.dtVB, 4807, MultiCmdItem.dpNONE);
        Multicmd_Vb4907_PinzeAlteDopoPC_C2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4907, MultiCmdItem.dpNONE);
        Multicmd_Vb4058_C1_AsseX_YFermo = sl.Add("Io", 1, MultiCmdItem.dtVB, 4058, MultiCmdItem.dpNONE);
        MultiCmd_Vn11McStatoCuci1 = sl.Add("Io", 1, MultiCmdItem.dtVN, 11, MultiCmdItem.dpNONE);
        MultiCmd_Vn132_DebugPie = sl.Add("Io", 1, MultiCmdItem.dtVN, 132, MultiCmdItem.dpNONE);
        MultiCmd_Vb30_C1_InCucitura = sl.Add("Io", 1, MultiCmdItem.dtVB, 30, MultiCmdItem.dpNONE);
        MultiCmd_Vb80ShowIconaTools = sl.Add("Io", 1, MultiCmdItem.dtVB, 80, MultiCmdItem.dpNONE);
        Multicmd_vn109 = sl.Add("Io", 1, MultiCmdItem.dtVN, 109, MultiCmdItem.dpNONE);
        Multicmd_vn110 = sl.Add("Io", 1, MultiCmdItem.dtVN, 110, MultiCmdItem.dpNONE);
        Multicmd_Vq3511_VelLavCaricatore = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3511, MultiCmdItem.dpNONE);
        Multicmd_vb4519_ScaricoPinza = sl.Add("Io", 1, MultiCmdItem.dtVB, 4519, MultiCmdItem.dpNONE);
        MultiCmd_i14_Rinforzo = sl.Add("Io", 1, MultiCmdItem.dtDI, 14, MultiCmdItem.dpNONE);
        MultiCmd_vb4060_SalvaUdf = sl.Add("Io", 1, MultiCmdItem.dtVB, 4060, MultiCmdItem.dpNONE);
        MultiCmd_Vb4189_C2_Cambio_Pinze_All  = sl.Add("Io", 1, MultiCmdItem.dtVB, 4189, MultiCmdItem.dpNONE);
        MultiCmd_Vb2018_SbloccaAgo_T2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 2018, MultiCmdItem.dpNONE);
        MultiCmd_Vb4512_TastoAbilitaTesta2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4512, MultiCmdItem.dpNONE);
        MultiCmd_Vb4513_StatoAbilitaTesta2  = sl.Add("Io", 1, MultiCmdItem.dtVB, 4513, MultiCmdItem.dpNONE);
        MultiCmd_Vb4904_AppPedaleHmiC2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4904, MultiCmdItem.dpNONE);
        MultiCmd_Vb4901_start_cucitura_T2  = sl.Add("Io", 1, MultiCmdItem.dtVB, 4901, MultiCmdItem.dpNONE);
        Multicmd_dtDB_progT2_Right_name = sl.Add("Io", 1, MultiCmdItem.dtDB, 30, MultiCmdItem.dpDB_MAIN2);
        Multicmd_vb2019_Load_Prog_T2_Right = sl.Add("Io", 1, MultiCmdItem.dtVB, 2019, MultiCmdItem.dpNONE);
        MultiCmd_Vq2110_Speed_T2 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2110, MultiCmdItem.dpNONE);
        MultiCmd_Vn198_num_prog_right_C2 = sl.Add("Io", 1, MultiCmdItem.dtVN, 198, MultiCmdItem.dpNONE);    //JT862 JT882Master
        MultiCmd_Vn199_num_prog_left_C2 = sl.Add("Io", 1, MultiCmdItem.dtVN, 199, MultiCmdItem.dpNONE);
        Multicmd_Vb4018_TrigrHMITascaCucita = sl.Add("Io", 1, MultiCmdItem.dtVB, 4018, MultiCmdItem.dpNONE);
        Multicmd_Vb152_Pattina_OnOff = sl.Add("Io", 1, MultiCmdItem.dtVB, 152, MultiCmdItem.dpNONE);
        Multicmd_Vb157_Pattina_PassoPasso = sl.Add("Io", 1, MultiCmdItem.dtVB, 157, MultiCmdItem.dpNONE);
        MultiCmd_Vb151EnableCarPattine = sl.Add("Io", 1, MultiCmdItem.dtVB, 151, MultiCmdItem.dpNONE);
        MultiCmd_Vn180_Device_Rinforzo = sl.Add("Io", 1, MultiCmdItem.dtVN, 180, MultiCmdItem.dpNONE);


        Mci_write_Vq1110_Speed.mci = MultiCmd_Vq1110_Speed;Mci_write_Vq1110_Speed.tipoVariabile = Mci_write.TipoVariabile.VQ;
        Mci_write_Vq2110_Speed.mci = MultiCmd_Vq2110_Speed_T2;Mci_write_Vq2110_Speed.tipoVariabile = Mci_write.TipoVariabile.VQ;
        Mci_write_Vb4000_pulsante_Automatico.mci = MultiCmd_Vb4000_pulsante_Automatico;
        Mci_write_Vq1913_C1_UdfVelLavRPM.mci = MultiCmd_Vq1913_C1_UdfVelLavRPM;Mci_write_Vq1913_C1_UdfVelLavRPM.tipoVariabile = Mci_write.TipoVariabile.VQ;
        Mci_write_Vb1025_AppReloadParamC1.mci = Multicmd_Vb1025_AppReloadParamC1;
        Mci_write_Vb2025_AppReloadParamC2.mci = Multicmd_Vb2025_AppReloadParamC2;
        Mci_write_dtDB_prog_name.mci = Multicmd_dtDB_prog_name;
        Mci_write_vb1019_Load_Prog.mci = Multicmd_vb1019_Load_Prog;
        Mci_write_Vb1006_StepPiu_singolo.mci = MultiCmd_Vb1006_StepPiu_singolo;
        Mci_write_Vb1007_StepMeno_singolo.mci = MultiCmd_Vb1007_StepMeno_singolo;
        Mci_write_Vb1022_StepPiu_multiplo.mci = MultiCmd_Vb1022_StepPiu_multiplo;
        Mci_write_Vb1023_StepMeno_multiplo.mci = MultiCmd_Vb1023_StepMeno_multiplo;
        Mci_write_Vb2006_StepPiu_singolo.mci = MultiCmd_Vb2006_StepPiu_singolo;
        Mci_write_Vb2022_StepPiu_multiplo.mci = MultiCmd_Vb2022_StepPiu_multiplo;
        Mci_write_Vb2007_StepMeno_singolo.mci = MultiCmd_Vb2007_StepMeno_singolo;
        Mci_write_Vb2023_StepMeno_multiplo.mci = MultiCmd_Vb2023_StepMeno_multiplo;
        Mci_write_Vb4802_Reset_Cuci.mci = MultiCmd_Vb4802_Reset_Cuci;
        Mci_write_Vq1951_punti_totali.mci = MultiCmd_Vq1951_punti_totali;
        Mci_write_Vq2951_punti_totali_C2.mci = MultiCmd_Vq2951_punti_totali_C2;
        Mci_write_Vq1952_punti_parziali.mci = MultiCmd_Vq1952_punti_parziali;
        Mci_write_Vq2952_punti_parziali_C2.mci = MultiCmd_Vq2952_punti_parziali_C2;
        Mci_write_Vq3596_ContPuntiSpola.mci = Multicmd_Vq3596_ContPuntiSpola;Mci_write_Vq3596_ContPuntiSpola.valore_precedente = 0.0d;
        Mci_write_Vq3597_ImpPuntiSpola.mci = Multicmd_Vq3597_ImpPuntiSpola;Mci_write_Vq3597_ImpPuntiSpola.valore_precedente = 0.0d;
        Mci_write_Vb4072_AllarmeContSpola.mci = Multicmd_Vb4072_AllarmeContSpola;Mci_write_Vb4072_AllarmeContSpola.write_flag = false;Mci_write_Vb4072_AllarmeContSpola.valore_precedente = 0.0d;Mci_write_Vb4072_AllarmeContSpola.valore = 0.0d;
        Mci_write_Vb1034_Test_Cuci.mci = MultiCmd_Vb1034_Test_Cuci;
        Mci_VQ1036_BufErrCode.mci = MultiCmd_VQ1036_BufErrCode;Mci_VQ1036_BufErrCode.Fronte_positivo = false;
        Mci_write_Vb1018_SbloccaAgo.mci = MultiCmd_Vb1018_SbloccaAgo;
        Mci_Vn2_allarmi_da_CN.mci = MultiCmd_Vn2_allarmi_da_CN;Mci_Vn2_allarmi_da_CN.valore_precedente = -1.0d;Mci_Vn2_allarmi_da_CN.valore = 0.0d;
        Mci_write_Vq3591_CNT_CicliAutomaticoUser.mci = Multicmd_Vq3591_CNT_CicliAutomaticoUser;Mci_write_Vq3591_CNT_CicliAutomaticoUser.valore_precedente = 0.0d;
        Mci_write_Vq3511_VelLavCaricatore.mci = Multicmd_Vq3511_VelLavCaricatore;Mci_write_Vq3511_VelLavCaricatore.valore_precedente = 0.0d;Mci_write_Vq3511_VelLavCaricatore.tipoVariabile = Mci_write.TipoVariabile.VQ;
        Mci_write_Vb1020_piu_Nmulti_step.mci = MultiCmd_Vb1020_piu_Nmulti_step;
        Mci_write_Vb1021_meno_Nmulti_step.mci = MultiCmd_Vb1021_meno_Nmulti_step;
        Mci_write_Vq1011_numero_Nmulti_step.mci = MultiCmd_Vq1011_numero_multi_step;
        Mci_write_Vb52_goPC.mci = MultiCmd_Vb52_goPC;
        Mci_write_Vb53_goPC_C2.mci = MultiCmd_Vb53_goPC_C2;
        Mci_write_Vb4157_C1_Cambio_Pinze_All.mci = MultiCmd_Vb4157_C1_Cambio_Pinze_All;
        Mci_write_Vb4189_C2_Cambio_Pinze_All.mci = MultiCmd_Vb4189_C2_Cambio_Pinze_All;
        Mci_write_Vb4139_C1_Cambio_Laterali.mci = MultiCmd_Vb4139_C1_Cambio_Laterali;
        Mci_write_Vb4138_C1_Cambio_Frontali.mci = MultiCmd_Vb4138_C1_Cambio_Frontali;
        Mci_write_Vb4137_C1_Cambio_Corpo.mci = MultiCmd_Vb4137_C1_Cambio_Corpo;
        Mci_write_Vb4140_Cambio_spigoli.mci = MultiCmd_Vb4140_Cambio_spigoli;
        Mci_write_C1_Udf_ValTensione.mci = Multicmd_C1_Udf_ValTensione;Mci_write_C1_Udf_ValTensione.valore_precedente = 0.0d;
        Mci_write_Vb4061_AppTensAumenta.mci = Multicmd_Vb4061_AppTensAumenta;
        Mci_write_Vb4062_AppTensDiminuisce.mci = Multicmd_Vb4062_AppTensDiminuisce;
        Mci_write_Vn_etichetta.mci = MultiCmd_Vn_etichetta;Mci_write_Vn_etichetta.write_flag = false;Mci_write_Vn_etichetta.valore_precedente = 0.0d;Mci_write_Vn_etichetta.valore = 0.0d;
        Mci_write_Vn196_tasca_right_C1.mci = MultiCmd_Vn196_num_prog_right_C1;Mci_write_Vn196_tasca_right_C1.write_flag = false;Mci_write_Vn196_tasca_right_C1.valore_precedente = 0.0d;Mci_write_Vn196_tasca_right_C1.valore = 0.0d;
        Mci_write_Vn197_tasca_left_C1.mci = MultiCmd_Vn197_num_prog_left_C1;Mci_write_Vn197_tasca_left_C1.write_flag = false;Mci_write_Vn197_tasca_left_C1.valore_precedente = 0.0d;Mci_write_Vn197_tasca_left_C1.valore = 0.0d;
        Mci_write_Vb4804_AppReloadParamC1.mci = Multicmd_Vb4804_AppReloadParamC1;
        Mci_write_Vb4807_PinzeAlteDopoPC.mci = Multicmd_Vb4807_PinzeAlteDopoPC;
        Mci_write_Vb4907_PinzeAlteDopoPC_C2.mci = Multicmd_Vb4907_PinzeAlteDopoPC_C2;
        Mci_Vb4510_TastoCaricatore.mci = MultiCmd_Vb4510_TastoCaricatore;
        Mci_Vb4521_TastoTestPiegatore.mci = MultiCmd_Vb4521_TastoTestPiegatore;
        Mci_Vb4522_TastoTestTraslatore.mci = MultiCmd_Vb4522_TastoTestTraslatore;
        Mci_Vb4511_StatoTastoCaricatore.mci = MultiCmd_Vb4511_StatoTastoCaricatore;
        Mci_Vb4508_TastoPiegatore.mci = MultiCmd_Vb4508_TastoPiegatore;
        Mci_Vb4509_StatoTastoPiegatore.mci = MultiCmd_Vb4509_StatoTastoPiegatore;
        Mci_Vb4514_TastoScaricatore.mci = MultiCmd_Vb4514_TastoScaricatore;
        Mci_write_vb4519_Scarico_pinza.mci = Multicmd_vb4519_ScaricoPinza;
        Mci_Vb4515_StatoTastoStacker.mci = MultiCmd_Vb4515_StatoTastoScaricatore;
        Mci_Vb4001_StatusTestPiegatore.mci = MultiCmd_Vb4001_StatusTestPiegatore;
        Mci_write_Vn109_sequenza_chiusura_piegatore.mci = Multicmd_vn109;
        Mci_write_Vn110_sequenza_apertura_piegatore.mci = Multicmd_vn110;
        Mci_write_Vb2018_SbloccaAgo_T2.mci = MultiCmd_Vb2018_SbloccaAgo_T2;
        Mci_write_Vb4512_TastoAbilitaTesta2.mci = MultiCmd_Vb4512_TastoAbilitaTesta2;
        Mci_write_Vb4901_start_cucitura_T2.mci = MultiCmd_Vb4901_start_cucitura_T2;
        Mci_write_Vb4904_AppPedaleHmiC2.mci = MultiCmd_Vb4904_AppPedaleHmiC2;
        Mci_write_dtDB_progT2_Right_name.mci = Multicmd_dtDB_progT2_Right_name;
        Mci_write_vb2019_Load_Prog_T2_Right.mci = Multicmd_vb2019_Load_Prog_T2_Right;
        Mci_write_Vq2913_C2_UdfVelLavRPM.mci = MultiCmd_Vq2913_C2_UdfVelLavRPM;
        Mci_write_Vb4074_AllarmeContSpola_C2.mci = Multicmd_Vb4074_AllarmeContSpola_C2 ;Mci_write_Vb4074_AllarmeContSpola_C2.write_flag = false;Mci_write_Vb4074_AllarmeContSpola_C2.valore_precedente = 0.0d;Mci_write_Vb4074_AllarmeContSpola_C2.valore = 0.0d;
        Mci_write_C2_Udf_ValTensione.mci = Multicmd_C2_Udf_ValTensione;Mci_write_C2_Udf_ValTensione.valore_precedente = 0.0d;
        Mci_write_Vb4063_AppTensAumenta.mci = Multicmd_Vb4063_AppTensAumenta;
        Mci_write_Vb4064_AppTensDiminuisce.mci = Multicmd_Vb4064_AppTensDiminuisce;
        Mci_write_Vq3598_ContPuntiSpola_C2.mci = Multicmd_Vq3598_ContPuntiSpola_C2;Mci_write_Vq3598_ContPuntiSpola_C2.valore_precedente = 0.0d;
        Mci_write_Vq3599_ImpPuntiSpola_C2.mci = Multicmd_Vq3599_ImpPuntiSpola_C2;Mci_write_Vq3599_ImpPuntiSpola_C2.valore_precedente = 0.0d;
        Mci_write_Vb4902_Reset_Cuci_C2.mci = MultiCmd_Vb4902_Reset_Cuci_C2;
        Mci_write_Vb2034_Test_Cuci_C2.mci = MultiCmd_Vb2034_Test_Cuci_C2;
        Mci_write_Vn198_tasca_right_C2.mci = MultiCmd_Vn198_num_prog_right_C2;Mci_write_Vn198_tasca_right_C2.write_flag = false;Mci_write_Vn198_tasca_right_C2.valore_precedente = 0.0d;Mci_write_Vn198_tasca_right_C2.valore = 0.0d;
        Mci_write_Vn199_tasca_left_C2.mci = MultiCmd_Vn199_num_prog_left_C2;Mci_write_Vn199_tasca_left_C2.write_flag = false;Mci_write_Vn199_tasca_left_C2.valore_precedente = 0.0d;Mci_write_Vn199_tasca_left_C2.valore = 0.0d;
        Mci_Vb4006_StatusTestTraslatore.mci = MultiCmd_Vb4006_status_test_traslo;
        Mci_write_Vb4018_TrigrHMITascaCucita.mci = Multicmd_Vb4018_TrigrHMITascaCucita;
        Mci_write_Vb152_Pattina_OnOff.mci = Multicmd_Vb152_Pattina_OnOff;
        Mci_write_Vb157_Pattina_PassoPasso.mci = Multicmd_Vb157_Pattina_PassoPasso;

        mci_array_read_all = new MultiCmdItem[]{MultiCmd_Vq1110_Speed, MultiCmd_CH1_in_emergenza, MultiCmd_Vb4000_pulsante_Automatico, MultiCmd_tasto_verde, MultiCmd_Vq1913_C1_UdfVelLavRPM, MultiCmd_Vq1951_punti_totali,
                MultiCmd_Vq1952_punti_parziali, Multicmd_Vq3596_ContPuntiSpola, Multicmd_Vb4072_AllarmeContSpola, MultiCmd_posizione_X, MultiCmd_posizione_Y, MultiCmd_VQ1036_BufErrCode,
                MultiCmd_VQ1037_BufErrStepNum, MultiCmd_VQ1038_BufErrPar, MultiCmd_Vn2_allarmi_da_CN, MultiCmd_Debug14_prog_cn_in_esecuzione, MultiCmd_Debug8_riga_cn_in_esecuzione,
                Multicmd_Vq3591_CNT_CicliAutomaticoUser,MultiCmd_vb4060_SalvaUdf//,Multicmd_C1_Udf_ValTensione
        };
        mci_array_read_all1 = new MultiCmdItem[]{MultiCmd_Vb1034_Test_Cuci, MultiCmd_Ch1_cucitrice1_Fermo, MultiCmd_Ch2_cucitrice2_Fermo, MultiCmd_Ch3_cicli_Fermo, MultiCmd_Vb4504_ok_per_automatico, MultiCmd_Vb4509_StatoTastoPiegatore,
                MultiCmd_Vb4511_StatoTastoCaricatore, MultiCmd_Vb4515_StatoTastoScaricatore, MultiCmd_Vb4001_StatusTestPiegatore, MultiCmd_Vb4157_C1_Cambio_Pinze_All, MultiCmd_Vn_etichetta,
                MultiCmd_Vb21_DxSx_pocket, MultiCmd_Vn196_num_prog_right_C1, MultiCmd_Vn197_num_prog_left_C1, Multicmd_dtDB_prog_name, MultiCmd_Vn4_Warning, MultiCmd_Vb4806_AppPinzaAltaC1,
                Multicmd_Vb4058_C1_AsseX_YFermo, MultiCmd_Vn11McStatoCuci1, MultiCmd_Vb4507_stato_ManAut, MultiCmd_Vn132_DebugPie, MultiCmd_Vb30_C1_InCucitura, MultiCmd_Vb80ShowIconaTools,
                Multicmd_vb4519_ScaricoPinza,MultiCmd_i14_Rinforzo,MultiCmd_Vb4006_status_test_traslo,Multicmd_Vb4018_TrigrHMITascaCucita
        };
        mci_array_read_882 =new MultiCmdItem[]{
                MultiCmd_Vb4189_C2_Cambio_Pinze_All,MultiCmd_Vb2018_SbloccaAgo_T2,MultiCmd_Vb4512_TastoAbilitaTesta2,MultiCmd_Vb4904_AppPedaleHmiC2,MultiCmd_Vb4901_start_cucitura_T2,MultiCmd_Vq2913_C2_UdfVelLavRPM,MultiCmd_Vq2110_Speed_T2,
                MultiCmd_Vq2951_punti_totali_C2,MultiCmd_Vq2952_punti_parziali_C2,MultiCmd_posizione_X_C2,MultiCmd_posizione_Y_C2,Multicmd_C2_Udf_ValTensione,Multicmd_Vq3598_ContPuntiSpola_C2,
                MultiCmd_Vb2034_Test_Cuci_C2,MultiCmd_Vn198_num_prog_right_C2,MultiCmd_Vn199_num_prog_left_C2,MultiCmd_Vb4513_StatoAbilitaTesta2,Multicmd_Vb4074_AllarmeContSpola_C2,
                Multicmd_Vb152_Pattina_OnOff, Multicmd_Vb157_Pattina_PassoPasso,MultiCmd_Vb151EnableCarPattine,MultiCmd_Vn180_Device_Rinforzo
        };
        mci_array_read_one_shot = new MultiCmdItem[]{};
    }

    /**
     * Function for init the event for go to the next or previous step
     */
    private void CreaEventoStepPiuMeno(Button button_step_piu,Button button_step_meno, Mci_write StepPiu_singolo,Mci_write StepPiu_multiplo, Mci_write StepMeno_singolo, Mci_write StepMeno_multiplo ) {
        button_step_piu.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    StepPiu_singolo.Fronte_positivo = true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    StepPiu_multiplo.valore = 0.0d;
                    StepPiu_multiplo.write_flag = true;
                    StepPiu_singolo.Fronte_negativo = true;
                }
                return false;
            }
        });
        button_step_piu.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                StepPiu_multiplo.valore = 1.0d;
                StepPiu_multiplo.write_flag = true;
                return true;    // <- set to true
            }
        });

        button_step_meno.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    StepMeno_singolo.Fronte_positivo = true;
                    if ((Double) MultiCmd_Vn2_allarmi_da_CN.getValue() == 2.0d || (Double) MultiCmd_Vn2_allarmi_da_CN.getValue() == 38.0d) {
                        Mci_Vn2_allarmi_da_CN.valore = 0.0d;
                        Mci_Vn2_allarmi_da_CN.write_flag = true;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    StepMeno_multiplo.valore = 0.0d;
                    StepMeno_multiplo.write_flag = true;

                    StepMeno_singolo.Fronte_negativo = true;
                }
                return false;
            }
        });
        button_step_meno.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                StepMeno_multiplo.valore = 1.0d;
                StepMeno_multiplo.write_flag = true;
                return true;    // <- set to true
            }
        });
    }

    /**
     * Init the etichetta event
     *
     * @param button
     */
    private void CreoEventoButtonEtichetta(Button button) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        if ((Double) Mci_write_Vn_etichetta.mci.getValue() == 0) {
                            Mci_write_Vn_etichetta.valore = 1.0d;
                            Mci_write_Vn_etichetta.write_flag = true;
                        }
                        if ((Double) Mci_write_Vn_etichetta.mci.getValue() == 1) {
                            Mci_write_Vn_etichetta.valore = 2.0d;
                            Mci_write_Vn_etichetta.write_flag = true;
                        }
                        if ((Double) Mci_write_Vn_etichetta.mci.getValue() == 2) {
                            Mci_write_Vn_etichetta.valore = 0.0d;
                            Mci_write_Vn_etichetta.write_flag = true;
                        }
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });
    }

    /**
     * Function for create the edit text event that open the KeyDialog
     *
     * @param mciWrite
     * @param textView
     * @param max_value
     * @param min_value
     * @param decimale
     * @param negativo
     * @param x1000
     */
    private void CreaEventoEditText(final Mci_write mciWrite, final TextView textView, final double max_value, final double min_value, boolean decimale, boolean negativo, final boolean x1000) {
        textView.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        Thread.sleep((long) 300d);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    KeyDialog.Lancia_KeyDialogo(mciWrite, MainActivity.this, textView, max_value, min_value, false, false, 0d, false, "", x1000,"");
                }
                return false;
            }
        });
    }

    //#endregion Init

    @Override
    public void onResume() {
        super.onResume();
        first_cycle = true;
        if (!Thread_Running) {
            StopThread = false;

            MyAndroidThread_Main myTask_main = new MyAndroidThread_Main(this);
            thread_Main = new Thread(myTask_main, "Main myTask");
            thread_Main.start();
            Log.d("JAM TAG", "Start MainActivity Thread from onResume");
        }
    }

    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
        if (Thread_Running) {
            try {
                Log.d("JAM TAG", "End MainActivity Thread from on Pause");
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

    /**
     * Function for draw the pocket
     */
    private void DrawTasca(String N_Testa) {

        if( !Values.Machine_model.equals("JT882M")){
            try {
                ArrayList List_entità_T1 = (ArrayList<Element>) ricetta_T1.elements;

                myView_T1 = new Dynamic_view(this, 433, 350, List_entità_T1, 1.1F, Coord_Pinza, false, -300, 5, null, getResources().getDimension(R.dimen.main_activity_framelayout_width), getResources().getDimension(R.dimen.main_activity_framelayout_height));
                frame_canvas_T1.addView(myView_T1);
                myView_T1.setBackgroundColor(Color.LTGRAY);
                myView_T1.Disegna_entità(List_entità_T1);
                myView_T1.Center_Bitmap();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Unable to draw pocket canvas", Toast.LENGTH_SHORT).show();
            }
        }else {
            switch (N_Testa){

                case "T1":
                    ArrayList List_entità_T1 = (ArrayList<Element>) ricetta_T1.elements;
                    myView_T1 = new Dynamic_view(this, 433, 350, List_entità_T1, 1.1F, Coord_Pinza, false, -300, 10, null, getResources().getDimension(R.dimen.main_activity_framelayout_width), getResources().getDimension(R.dimen.main_activity_framelayout_height));
                    frame_canvas_T1.addView(myView_T1);
                    myView_T1.setBackgroundColor(Color.LTGRAY);
                    myView_T1.Disegna_entità(List_entità_T1);
                    myView_T1.Center_Bitmap();

                    break;


                case "T2":
                    ArrayList List_entità_T2 = (ArrayList<Element>) ricetta_T2.elements;
                    myView_T2 = new Dynamic_view(this, 433, 350, List_entità_T2, 1.1F, Coord_Pinza_C2, false, -300, 10, null, getResources().getDimension(R.dimen.main_activity_framelayout_width), getResources().getDimension(R.dimen.main_activity_framelayout_height));
                    frame_canvas_T2.addView(myView_T2);
                    myView_T2.setBackgroundColor(Color.LTGRAY);
                    myView_T2.Disegna_entità(List_entità_T2);
                    myView_T2.Center_Bitmap();

                    break;


                default:
                    break;
            }

        }









    }

    /**
     * Function for load a XML
     *
     * @param percorso_file
     */
    private void Load_XML(String percorso_file,String N_Testa) {
        try {

            File file = new File(percorso_file);
            // If the file doesn't exist create a new empty program
            if (!file.exists() || file.length() <= 0) {

                Utility.CreaProgCucituraVuoto(this);
                percorso_file = Environment.getExternalStorageDirectory() + "/JamData/file_empty.xml";

            }

            file = new File(percorso_file);
            File root = Environment.getExternalStorageDirectory();
            // TODO useless, i create it at startup
            File dir = new File(root.getAbsolutePath() + "/ricette");
            dir.mkdirs();
            switch (N_Testa) {
                case "T1":
                    ricetta_T1 = new Ricetta(Values.plcType);
                    try {
                        ricetta_T1.open(file);
                        Values.UdfPuntiVelIni_T1 = Math.round(Double.valueOf(ricetta_T1.UdfPuntiVelIni) * 1000.0) / 1000.0;
                        Values.UdfVelIniRPM_T1 = ricetta_T1.UdfVelIniRPM;
                        Values.UdfPuntiVelRall_T1 = Math.round(Double.valueOf(ricetta_T1.UdfPuntiVelRall) * 1000.0) / 1000.0;
                        Values.UdfVelRallRPM_T1 = ricetta_T1.UdfVelRallRPM;
                        Values.Udf_FeedG0_T1 = ricetta_T1.Udf_FeedG0;
                        Values.Udf_ValTensione_T1 = ricetta_T1.Udf_ValTensioneT1;
                        Values.Udf_ValElettrocalamitaSopra_T1 = ricetta_T1.Udf_ValElettrocalamitaSopra;
                        Values.Udf_ValElettrocalamitaSotto_T1 = ricetta_T1.Udf_ValElettrocalamitaSotto;
                        Values.Udf_VelocitaCaricLavoro_T1 = ricetta_T1.Udf_VelocitaCaricLavoro;
                        Values.Udf_SequenzaPiegatore_chiusura_T1 = ricetta_T1.Udf_SequenzaPiegatore_chiusura;
                        Values.Udf_SequenzaPiegatore_apetura_T1 = ricetta_T1.Udf_SequenzaPiegatore_apetura;
                        Mci_write_Vq3511_VelLavCaricatore.valore = Double.valueOf(ricetta_T1.Udf_VelocitaCaricLavoro);
                        Mci_write_Vq3511_VelLavCaricatore.write_flag = true;
                        Mci_write_Vn109_sequenza_chiusura_piegatore.valore = Double.valueOf(ricetta_T1.Udf_SequenzaPiegatore_chiusura);
                        Mci_write_Vn109_sequenza_chiusura_piegatore.write_flag = true;
                        Mci_write_Vn110_sequenza_apertura_piegatore.valore = Double.valueOf(ricetta_T1.Udf_SequenzaPiegatore_apetura);
                        Mci_write_Vn110_sequenza_apertura_piegatore.write_flag = true;


                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                    }
                    if (ricetta_T1.elements.size() != 0) {
                        DrawTasca("T1");

                    } else {
                        Toast.makeText(getApplicationContext(), "xml file problem", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case "T2":
                    ricetta_T2 = new Ricetta(Values.plcType);
                    try {
                        ricetta_T2.open(file);
                        Values.UdfPuntiVelIni_T2 = Math.round(Double.valueOf(ricetta_T2.UdfPuntiVelIni) * 1000.0) / 1000.0;
                        Values.UdfVelIniRPM_T2 = ricetta_T2.UdfVelIniRPM;
                        Values.UdfPuntiVelRall_T2 = Math.round(Double.valueOf(ricetta_T2.UdfPuntiVelRall) * 1000.0) / 1000.0;
                        Values.UdfVelRallRPM_T2 = ricetta_T2.UdfVelRallRPM;
                        Values.Udf_FeedG0_T2 = ricetta_T2.Udf_FeedG0;
                        Values.Udf_ValTensione_T2 = ricetta_T2.Udf_ValTensioneT1;
                        Values.Udf_ValElettrocalamitaSopra_T2 = ricetta_T2.Udf_ValElettrocalamitaSopra;
                        Values.Udf_ValElettrocalamitaSotto_T2 = ricetta_T2.Udf_ValElettrocalamitaSotto;
                        Values.Udf_VelocitaCaricLavoro_T2 = ricetta_T2.Udf_VelocitaCaricLavoro;
                        Values.Udf_SequenzaPiegatore_chiusura_T2 = ricetta_T2.Udf_SequenzaPiegatore_chiusura;
                        Values.Udf_SequenzaPiegatore_apetura_T2 = ricetta_T2.Udf_SequenzaPiegatore_apetura;

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                    }
                    if (ricetta_T2.elements.size() != 0) {
                        DrawTasca("T2");

                    } else {
                        Toast.makeText(getApplicationContext(), "xml file problem", Toast.LENGTH_SHORT).show();
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Unable to draw pocket canvas", Toast.LENGTH_SHORT).show();
        }
    }


    private void GestiscoWarning() {
        double Warning = (Double) MultiCmd_Vn4_Warning.getValue();
        try {
            if (Warning > 0 && Warning != warning_old) {
                warning_old = Warning;

                tab_names = getResources().getStringArray(R.array.warning_vn4);
                String warning_string = tab_names[(int) Warning];

                AlertDialog.Builder warningDialog = new AlertDialog.Builder(getApplicationContext());
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
                alert.show();
                Warning = 0;        //cancello chiamata
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Gestione_tascaDxSX() {
        if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 1.0d) {
            try {
                switch (mc_stati_tasca_dx_sx) {

                    case 0: //inizio
                        Toggle_Button.Disabilita_Imagebutton(Button_tascaSX, "ic_tasca_sx_disable", getApplicationContext());
                        Toggle_Button.Disabilita_Imagebutton(Button_tascaDX, "ic_tasca_dx_disable", getApplicationContext());
                        Toggle_Button.Disabilita_Imagebutton(Button_load_L, "ic_open_file_disable", getApplicationContext());
                        TextView_nomeprog2.setVisibility(View.GONE);
                        TextView_nomeprog_L_val.setVisibility(View.GONE);
                        TextView_folder2.setVisibility(View.GONE);
                        TextView_folder_L_val.setVisibility(View.GONE);
                        if (Values.Machine_model.equals("JT882M")) {

                            Toggle_Button.Disabilita_Imagebutton(Button_tascaSX_C2, "ic_tasca_sx_disable", getApplicationContext());
                            Toggle_Button.Disabilita_Imagebutton(Button_tascaDX_C2, "ic_tasca_dx_disable", getApplicationContext());
                            Toggle_Button.Disabilita_Imagebutton(Button_load_L_C2, "ic_open_file_disable", getApplicationContext());
                            TextView_nomeprog_L2_C2.setVisibility(View.GONE);
                            TextView_nomeprog_L_val_C2.setVisibility(View.GONE);
                            TextView_folder_L2_C2.setVisibility(View.GONE);
                            TextView_folder_L_val_C2.setVisibility(View.GONE);

                        }

                        mc_stati_tasca_dx_sx = 10;  //
                        break;
                    case 10:    //attivo
                        if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 1.0d) {
                            Toggle_Button.Abilita_Imagebutton(Button_tascaSX, "ic_tasca_sx", getApplicationContext());
                            Toggle_Button.Abilita_Imagebutton(Button_tascaDX, "ic_tasca_dx", getApplicationContext());
                            Toggle_Button.Abilita_Imagebutton(Button_load_L, "ic_open_file", getApplicationContext());
                            TextView_nomeprog2.setVisibility(View.VISIBLE);
                            TextView_nomeprog_L_val.setVisibility(View.VISIBLE);
                            TextView_folder2.setVisibility(View.VISIBLE);
                            TextView_folder_L_val.setVisibility(View.VISIBLE);
                            mc_stati_tasca_dx_sx = 20;

                            if (Values.Machine_model.equals("JT882M")) {

                                Toggle_Button.Abilita_Imagebutton(Button_tascaSX_C2, "ic_tasca_sx", getApplicationContext());
                                Toggle_Button.Abilita_Imagebutton(Button_tascaDX_C2, "ic_tasca_dx", getApplicationContext());
                                Toggle_Button.Abilita_Imagebutton(Button_load_L_C2, "ic_open_file", getApplicationContext());
                                TextView_nomeprog_L2_C2.setVisibility(View.VISIBLE);
                                TextView_nomeprog_L_val_C2.setVisibility(View.VISIBLE);
                                TextView_folder_L2_C2.setVisibility(View.VISIBLE);
                                TextView_folder_L_val_C2.setVisibility(View.VISIBLE);

                            }

                        }
                        break;
                    case 20:
                        if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 0.0d) {
                            mc_stati_tasca_dx_sx = 0;
                        }
                        if (Double.compare(Mci_write_Vn196_tasca_right_C1.valore_precedente, (Double) MultiCmd_Vn196_num_prog_right_C1.getValue()) != 0
                                || Double.compare(Mci_write_Vn197_tasca_left_C1.valore_precedente, (Double) MultiCmd_Vn197_num_prog_left_C1.getValue()) != 0) {
                            if ((Double) MultiCmd_Vn196_num_prog_right_C1.getValue() == 1.0d) {
                                Button_tascaSX.setBackground(getApplicationContext().getResources().getDrawable((R.drawable.ic_tasca_sx)));
                                Button_tascaDX.setBackground(getApplicationContext().getResources().getDrawable((R.drawable.ic_tasca_dx_press)));

                                Carica_programma(Values.File_XML_path_R, "T1", "DX");

                                //   try {
                                //       Thread.sleep((long) 300d);

                                //   } catch (InterruptedException e) {
                                //       e.printStackTrace();
                                //   }
                            } else {
                                Button_tascaSX.setBackground(getApplicationContext().getResources().getDrawable((R.drawable.ic_tasca_sx_press)));
                                Button_tascaDX.setBackground(getApplicationContext().getResources().getDrawable((R.drawable.ic_tasca_dx)));
                                Carica_programma(Values.File_XML_path_L, "T1", "SX");
                                //  try {
                                //      Thread.sleep((long) 300d);

                                //  } catch (InterruptedException e) {
                                //      e.printStackTrace();
                                //   }
                            }
                            Mci_write_Vn196_tasca_right_C1.valore_precedente = (Double) MultiCmd_Vn196_num_prog_right_C1.getValue();
                            Mci_write_Vn197_tasca_left_C1.valore_precedente = (Double) MultiCmd_Vn197_num_prog_left_C1.getValue();
                        }

                        if (Values.Machine_model.equals("JT882M")) {
                            if (Double.compare(Mci_write_Vn198_tasca_right_C2.valore_precedente, (Double) MultiCmd_Vn198_num_prog_right_C2.getValue()) != 0
                                    || Double.compare(Mci_write_Vn199_tasca_left_C2.valore_precedente, (Double) MultiCmd_Vn199_num_prog_left_C2.getValue()) != 0) {
                                if ((Double) MultiCmd_Vn198_num_prog_right_C2.getValue() == 1.0d) {
                                    Button_tascaSX_C2.setBackground(getApplicationContext().getResources().getDrawable((R.drawable.ic_tasca_sx)));
                                    Button_tascaDX_C2.setBackground(getApplicationContext().getResources().getDrawable((R.drawable.ic_tasca_dx_press)));

                                    Carica_programma(Values.File_XML_path_T2_R, "T2", "DX");

                                    //   try {
                                    //       Thread.sleep((long) 300d);

                                    //   } catch (InterruptedException e) {
                                    //        e.printStackTrace();
                                    //    }
                                } else {
                                    Button_tascaSX_C2.setBackground(getApplicationContext().getResources().getDrawable((R.drawable.ic_tasca_sx_press)));
                                    Button_tascaDX_C2.setBackground(getApplicationContext().getResources().getDrawable((R.drawable.ic_tasca_dx)));

                                    Carica_programma(Values.File_XML_path_T2_L, "T2", "SX");
                                    //   try {
                                    //       Thread.sleep((long) 300d);

                                    //    } catch (InterruptedException e) {
                                    //        e.printStackTrace();
                                    //    }
                                }
                                Mci_write_Vn198_tasca_right_C2.valore_precedente = (Double) MultiCmd_Vn198_num_prog_right_C2.getValue();
                                Mci_write_Vn199_tasca_left_C2.valore_precedente = (Double) MultiCmd_Vn199_num_prog_left_C2.getValue();
                            }
                        }


                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void Riga_CN_Esecuzione() {
        String prog = (String) MultiCmd_Debug14_prog_cn_in_esecuzione.getValue();
        Double riga = (Double) MultiCmd_Debug8_riga_cn_in_esecuzione.getValue();
        String st1 = getString(R.string.Prog_esecuzione);
        String st2 = getString(R.string.Riga_esecuzione);
        TextView_programma_in_esecuzione.setText(st1 + prog + st2 + riga);

        TextView_riga_fork.setText("Vn132DebugFold: " + MultiCmd_Vn132_DebugPie.getValue() + " mcCicloAuto: " + MultiCmd_Vn11McStatoCuci1.getValue());
    }

    private void ControlloErrori() {
        int i = 0;
        try {
            Double err = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();
            i = err.intValue();

            switch (i) {
                case 0:
                    int errore_precedente = Mci_Vn2_allarmi_da_CN.valore_precedente.intValue();

                    switch (errore_precedente) {    //se ora l'errore dal CN è tornato a 0 controllo qual'era l'errore precedente e poi agisco , in pratica è un fronte negativo dell'errore
                        case 0:
                            break;
                        case 1://fronte negativo del conta spola
                            if ((Double) Mci_Vn2_allarmi_da_CN.mci.getValue() == 0 && Mci_Vn2_allarmi_da_CN.Fronte_positivo) {
                                if ((Double) Multicmd_Vb4072_AllarmeContSpola.getValue() == 0.0d) {
                                    Mci_Vn2_allarmi_da_CN.Fronte_positivo = false;
                                    Double val_prec = Mci_Vn2_allarmi_da_CN.valore_precedente;
                                    Double val_CN = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();

                                    if (Double.compare(val_prec, val_CN) != 0) {
                                        Mci_Vn2_allarmi_da_CN.valore_precedente = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();
                                        DrawTasca("T1");
                                        TextView_errore.setText("");
                                    }
                                }
                            }
                            break;
                        case 2://fronte negativo rottura filo
                        case 17: //
                        case 18: //
                        case 19: //
                        case 20: //
                        case 21: //
                            if ((Double) Mci_Vn2_allarmi_da_CN.mci.getValue() == 0 && Mci_Vn2_allarmi_da_CN.Fronte_positivo) {

                                Mci_Vn2_allarmi_da_CN.Fronte_positivo = false;
                                Double val_prec = Mci_Vn2_allarmi_da_CN.valore_precedente;
                                Double val_CN = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();

                                if (Double.compare(val_prec, val_CN) != 0) {
                                    Mci_Vn2_allarmi_da_CN.valore_precedente = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();
                                    DrawTasca("T1");
                                    TextView_errore.setText("");
                                }
                            }
                            break;
                        case 37://fronte negativo del conta spola
                            if ((Double) Mci_Vn2_allarmi_da_CN.mci.getValue() == 0 && Mci_Vn2_allarmi_da_CN.Fronte_positivo) {
                                if ((Double) Multicmd_Vb4074_AllarmeContSpola_C2.getValue() == 0.0d) {
                                    Mci_Vn2_allarmi_da_CN.Fronte_positivo = false;
                                    Double val_prec = Mci_Vn2_allarmi_da_CN.valore_precedente;
                                    Double val_CN = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();

                                    if (Double.compare(val_prec, val_CN) != 0) {
                                        Mci_Vn2_allarmi_da_CN.valore_precedente = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();
                                        DrawTasca("T2");
                                        TextView_errore.setText("");
                                    }
                                }
                            }
                            break;
                        case 38://fronte negativo rottura filo
                            if ((Double) Mci_Vn2_allarmi_da_CN.mci.getValue() == 0 && Mci_Vn2_allarmi_da_CN.Fronte_positivo) {

                                Mci_Vn2_allarmi_da_CN.Fronte_positivo = false;
                                Double val_prec = Mci_Vn2_allarmi_da_CN.valore_precedente;
                                Double val_CN = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();

                                if (Double.compare(val_prec, val_CN) != 0) {
                                    Mci_Vn2_allarmi_da_CN.valore_precedente = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();
                                    DrawTasca("T2");
                                    TextView_errore.setText("");
                                }
                            }
                            break;
                        case 39://spengo scritta
                            if ((Double) Mci_Vn2_allarmi_da_CN.mci.getValue() == 0 && Mci_Vn2_allarmi_da_CN.Fronte_positivo) {

                                Mci_Vn2_allarmi_da_CN.Fronte_positivo = false;
                                Double val_prec = Mci_Vn2_allarmi_da_CN.valore_precedente;
                                Double val_CN = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();

                                if (Double.compare(val_prec, val_CN) != 0) {
                                    Mci_Vn2_allarmi_da_CN.valore_precedente = (Double) Mci_Vn2_allarmi_da_CN.mci.getValue();
                                    TextView_errore.setText("");
                                }
                            }

                        break;

                default:
                            break;
                    }
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:

                    if (Mci_Vn2_allarmi_da_CN.valore_precedente == 0.0d || Mci_Vn2_allarmi_da_CN.valore_precedente == -1.0d) {        //entro solo una volta
                        if (i != 1 && i != 2) {
                            Button_step_piu.setVisibility(View.GONE);
                            Button_step_piu_C2.setVisibility(View.GONE);
                            Button_step_meno.setVisibility(View.GONE);
                            Button_step_meno_C2.setVisibility(View.GONE);
                            Button_reset.setVisibility(View.GONE);
                            Button_reset_C2.setVisibility(View.GONE);
                            Button_All.setVisibility(View.GONE);
                            Button_All_C2.setVisibility(View.GONE);
                            Button_zoomPiu.setVisibility(View.GONE);
                            Button_zoomPiu_C2.setVisibility(View.GONE);
                            Button_zoomMeno.setVisibility(View.GONE);
                            Button_zoomMeno_C2.setVisibility(View.GONE);
                            Button_reset_spola.setVisibility(View.GONE);
                            Button_reset_spola_C2.setVisibility(View.GONE);
                            Button_tens_meno.setVisibility(View.GONE);
                            Button_tens_piu.setVisibility(View.GONE);
                            Button_tens_meno_C2.setVisibility(View.GONE);
                            Button_tens_piu_C2.setVisibility(View.GONE);
                            TextView_Cnt_spola.setVisibility(View.GONE);
                            TextView_Cnt_spola_C2.setVisibility(View.GONE);
                            TextSpola_limite.setVisibility(View.GONE);
                            TextSpola_limite_C2.setVisibility(View.GONE);
                            TextView_valore_tensione.setVisibility(View.GONE);
                            TextView_valore_tensione_C2.setVisibility(View.GONE);
                            TextView_percento.setVisibility(View.GONE);
                            TextView_percento_C2.setVisibility(View.GONE);
                            Button_reset_allarme.setVisibility(View.VISIBLE);
                        }

                        Mci_Vn2_allarmi_da_CN.valore_precedente = Double.valueOf(i);
                        Mci_Vn2_allarmi_da_CN.Fronte_positivo = true;
                        String[] tab_names = new String[]{};
                        String Stringa_allarme = "";

                        tab_names = getResources().getStringArray(R.array.allarmi_vn2);
                        Stringa_allarme = tab_names[i];

                        TextView_errore.setText(Stringa_allarme);
                        TextView_errore.setTextColor(Color.RED);
                        try {

                            ImageView imageView = new ImageView(getApplicationContext());
                            imageView.setBackground(getApplicationContext().getResources().getDrawable(Array_foto_allarmi[i]));
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT));

                            frame_canvas_T1.addView(imageView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 37:    //rottura filo C2
                    Mci_Vn2_allarmi_da_CN.valore_precedente = Double.valueOf(i);
                    Mci_Vn2_allarmi_da_CN.Fronte_positivo = true;
                    String[] tab_names = new String[]{};
                    String Stringa_allarme = "";

                    tab_names = getResources().getStringArray(R.array.allarmi_vn2);
                    Stringa_allarme = tab_names[i];

                    TextView_errore.setText(Stringa_allarme);
                    TextView_errore.setTextColor(Color.RED);
                    try {

                        ImageView imageView = new ImageView(getApplicationContext());
                        imageView.setBackground(getApplicationContext().getResources().getDrawable(Array_foto_allarmi[i]));
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT));

                        frame_canvas_T2.addView(imageView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 38:
                    Mci_Vn2_allarmi_da_CN.valore_precedente = Double.valueOf(i);
                    Mci_Vn2_allarmi_da_CN.Fronte_positivo = true;
                     tab_names = new String[]{};
                     Stringa_allarme = "";

                    tab_names = getResources().getStringArray(R.array.allarmi_vn2);
                    Stringa_allarme = tab_names[i];

                    TextView_errore.setText(Stringa_allarme);
                    TextView_errore.setTextColor(Color.RED);
                    try {

                        ImageView imageView = new ImageView(getApplicationContext());
                        imageView.setBackground(getApplicationContext().getResources().getDrawable(Array_foto_allarmi[i]));
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT));

                        frame_canvas_T2.addView(imageView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case 39:
                        Mci_Vn2_allarmi_da_CN.valore_precedente = Double.valueOf(i);
                        Mci_Vn2_allarmi_da_CN.Fronte_positivo = true;
                        tab_names = new String[]{};
                        Stringa_allarme = "";

                        tab_names = getResources().getStringArray(R.array.allarmi_vn2);
                        Stringa_allarme = tab_names[i];

                        TextView_errore.setText(Stringa_allarme);
                        TextView_errore.setTextColor(Color.RED);
                        break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Allarmi_Vn2 catch numero: " + i, Toast.LENGTH_SHORT).show();
        }
    }

    private void AggiornoMciValoriAccensione() {
        Mci_write_Vn197_tasca_left_C1.valore = 0.0d;
        Mci_write_Vn199_tasca_left_C2.valore = 0.0d;

        sl.ReadItem(Multicmd_Vq3597_ImpPuntiSpola);
        sl.ReadItem(Multicmd_Vq3599_ImpPuntiSpola_C2);
        sl.ReadItem(MultiCmd_Vq1011_numero_multi_step);
        Mci_write_Vn_etichetta.valore = (Double) MultiCmd_Vn_etichetta.getValue();
        MultiCmd_Vn2_allarmi_da_CN.setValue(0.0d);
        sl.WriteItem(MultiCmd_Vn2_allarmi_da_CN);
    }

    private void GestiscoVisualizzazioneStatusButton(Mci_write mci_write, Button button_status) {
        if (Double.compare(mci_write.valore_precedente, (Double) mci_write.mci.getValue()) != 0) {
            EdgeButton.Visualizza_stato_VB(mci_write, button_status, "stato_rosso", "stato_verde", getApplicationContext());
            mci_write.valore_precedente = (Double) mci_write.mci.getValue();
        }
    }

    private void Visualizza_contatore(TextView textView, Mci_write mci_write, boolean numero_intero, boolean variabile_VQ, boolean Div1000) {
        try {
            if (Double.compare(mci_write.valore_precedente, (Double) mci_write.mci.getValue()) != 0) {
                if (Div1000) {
                    Double val = (Double) mci_write.mci.getValue();
                    val = val / 1000;
                    textView.setText(SubString.Before(val.toString(), "."));
                } else {

                    if (numero_intero)
                        textView.setText(SubString.Before(mci_write.mci.getValue().toString(), "."));

                    else if (variabile_VQ)
                        textView.setText(SubString.Before(mci_write.mci.getValue().toString(), "000."));
                    else
                        textView.setText(mci_write.mci.getValue().toString());
                }

                mci_write.valore_precedente = (Double) mci_write.mci.getValue();
                mci_write.valore = mci_write.valore_precedente;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Visualizza_TensioneFilo(TextView textView, Mci_write mci_write, String file_XML_path, Ricetta ricetta) {
        try {
            if (Double.compare(mci_write.valore_precedente, (Double) mci_write.valore) != 0)
            {

                Double val = (Double) mci_write.valore;
                textView.setText(SubString.Before(val.toString(), ".") );
                mci_write.valore_precedente = (Double) mci_write.valore;
                ricetta.Udf_ValTensioneT1 = (int) Math.round(val);
                ricetta.save(new File(file_XML_path));      //salxo xml

               // String path = Values.File_XML_path_R.replace(".xml", ".udf");
                String path = file_XML_path.replace(".xml", ".udf");
                File file = new File(path);
                ricetta.exportToUdf(file);  //salvo udf

                mci_write.write_flag = true;



            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void ScrivoStringaDB(Mci_write mci_write) {
        if (mci_write.write_flag) {
            mci_write.mci.setValue(mci_write.path_file);
            sl.WriteItem(mci_write.mci);
            mci_write.write_flag = false;
        }
    }

    private void Gestione_icona_etichetta(Mci_write mci_write) {
        if (Double.compare(mci_write.valore_precedente, (Double) mci_write.mci.getValue()) != 0) {
            if ((Double) mci_write.mci.getValue() == 0.0d) {
                Button_etichetta.setBackground(getResources().getDrawable(R.drawable.ic_etichetta));
                Mci_write_Vn_etichetta.valore_precedente = 0.0d;
            }
            if ((Double) mci_write.mci.getValue() == 1.0d) {
                Button_etichetta.setBackground(getResources().getDrawable(R.drawable.ic_etichetta111));
                Mci_write_Vn_etichetta.valore_precedente = 1.0d;
            }
            if ((Double) mci_write.mci.getValue() == 2.0d) {
                Button_etichetta.setBackground(getResources().getDrawable(R.drawable.ic_etichetta101));
                Mci_write_Vn_etichetta.valore_precedente = 2.0d;
            }
        }
    }

    /**
     * Function for handle the emergency button event
     *
     * @param activity
     */
    private void Emergenza(Activity activity) {
        if ((Double) MultiCmd_tasto_verde.getValue() == 0.0d || (Double) MultiCmd_CH1_in_emergenza.getValue() == 1.0d) {
            KillThread();

            Utility.ClearActivitiesTopToEmergencyPage(activity);
        }
    }
    private void GestioneButtonPattina() {

        if ((Double) MultiCmd_Vb151EnableCarPattine.getValue() == 1.0d) {
            Button_pattina_on_off.setVisibility(View.VISIBLE);
            Button_pattina_test.setVisibility(View.VISIBLE);
        }else {
            Button_pattina_on_off.setVisibility(View.GONE);
            Button_pattina_test.setVisibility(View.GONE);
        }
    }

    private void GestioneButtonRinforzo() {

        if ((Double) MultiCmd_Vn180_Device_Rinforzo.getValue() == 1.0d) {
            Button_rinforzo.setVisibility(View.VISIBLE);
        }else {
            Button_rinforzo.setVisibility(View.GONE);

        }
    }
    /**
     * Function for set tool button visible/hidden
     */
    private void GestioneButtonTools() {
        if ((Double) MultiCmd_Vb80ShowIconaTools.getValue() == 1.0d) {
            Button_pagina_tools.setVisibility(View.VISIBLE);
            Button_load_R.setVisibility(View.VISIBLE);
            Button_load_L.setVisibility(View.VISIBLE);
            if(Values.Machine_model.equals("JT882M")){
                Button_load_L_C2.setVisibility(View.VISIBLE);
                Button_load_R_C2.setVisibility(View.VISIBLE);
            }
        } else {
            Button_pagina_tools.setVisibility(View.GONE);
            Button_load_R.setVisibility(View.GONE);
            Button_load_L.setVisibility(View.GONE);
            if(Values.Machine_model.equals("JT882M")){
                Button_load_L_C2.setVisibility(View.GONE);
                Button_load_R_C2.setVisibility(View.GONE);
            }
        }
    }
    /**
     * Se attivo Tcp 4.0 e arriva un triggger dal PLC che la tasca è stata cucita completamente,
     * apro il file Commesse in esecuzione e lo incremento di 1
     */
    private void Tcp4_0Counter() throws IOException {
        if(Values.Tcp_enable_status.equals("true")){
            if(Mci_write_Vb4018_TrigrHMITascaCucita.valore_precedente == 0.0d && (Double)Multicmd_Vb4018_TrigrHMITascaCucita.getValue() == 1.0d){

                Mci_write_Vb4018_TrigrHMITascaCucita.valore_precedente = 1.0d;
                try {
                    String Commessa_selezionata = Info_file.Leggi_campo("storage/emulated/0/JamData/Tcp.txt", "InfoJAM", "Tcp", null, null, "TcpNomeCommessa", getApplicationContext());
                    if (!Commessa_selezionata.equals("") && Commessa_selezionata != null) {
                        String pathFile = "/storage/emulated/0/JamData/Commesse/" + Commessa_selezionata + ".txt";

                        //Instantiating the File class
                        String filePath = "/storage/emulated/0/JamData/Commesse/" + Commessa_selezionata + ".txt";
                        //Instantiating the Scanner class to read the file
                        Scanner sc = new Scanner(new File(filePath));


                        StringBuffer buffer = new StringBuffer();
                        //Reading lines of the file and appending them to StringBuffer
                        while (sc.hasNextLine()) {
                            buffer.append(sc.nextLine() + System.lineSeparator());
                        }
                        String fileContents = buffer.toString();
                        //System.out.println("Contents of the file: "+fileContents);
                        //closing the Scanner object
                        sc.close();

                        String[] st_split = fileContents.split("\n");
                        if (st_split.length > 6) {
                            if (st_split[6].equals("&end&")) {
                                String cnt = st_split[5];
                                if (cnt.equals("no")) cnt = "0";
                                int cnt_int = Integer.parseInt(cnt);
                                cnt_int++;
                                cnt = String.valueOf(cnt_int);
                                st_split[5] = cnt;

                                FileWriter writer = new FileWriter(filePath);
                                for(int i = 0; i< st_split.length; i++){
                                    writer.append(st_split[i]+"\n");

                                }
                                writer.flush();
                            }

                        }

                    }
                }
                catch (Exception e){}

            }
            if(Mci_write_Vb4018_TrigrHMITascaCucita.valore_precedente ==1.0d && (Double)Multicmd_Vb4018_TrigrHMITascaCucita.getValue() == 0.0d){
                Mci_write_Vb4018_TrigrHMITascaCucita.valore_precedente = 0.0d;
            }
        }
    }
    /**
     * TODO ????
     *
     * @param path_xml
     * @param Dx_Sx
     */
    private void Carica_programma(String path_xml, String testa, String Dx_Sx) {
        if(path_xml!= null && !path_xml.equals("")) {
            File file = new File(path_xml);
            int i = file.getName().lastIndexOf('.');
            String name = file.getName().substring(0, i);
            String path_file_udf = "c:\\cnc\\userdata\\" + name + ".udf";

            // Check which program to load
            if (testa.equals("T1") && Dx_Sx.equals("DX")) {
                TextView_nomeprog_R_val.setText(name);
                TextView TextView_folder_R_val1_T1 = findViewById(R.id.textView_folder_R_val);
                i = file.getPath().lastIndexOf('/');
                String folder = file.getPath().substring(0, i);
                TextView_folder_R_val1_T1.setText(folder);
                Load_XML(path_xml, "T1");


            }
            if (testa.equals("T1") && Dx_Sx.equals("SX")) {

                TextView_nomeprog_L_val.setText(name);
                TextView TextView_folder_L_val1_T1 = findViewById(R.id.textView_folder_L_val);
                i = file.getPath().lastIndexOf('/');
                String folder = file.getPath().substring(0, i);
                TextView_folder_L_val1_T1.setText(folder);
                Load_XML(path_xml, "T1");

            }
            if (testa.equals("T2") && Dx_Sx.equals("DX")) {

                TextView_nomeprog_R_val_T2.setText(name);
                TextView TextView_folder_R_val1_T2 = (TextView) findViewById(R.id.textView_folder_R_val_C2);
                i = file.getPath().lastIndexOf('/');
                String folder = file.getPath().substring(0, i);
                TextView_folder_R_val1_T2.setText(folder);
                Load_XML(path_xml, "T2");

            }
            if (testa.equals("T2") && Dx_Sx.equals("SX")) {

                TextView_nomeprog_L_val_C2.setText(name);
                TextView TextView_folder_L_val1_T2 = (TextView) findViewById(R.id.textView_folder_L_val_C2);
                i = file.getPath().lastIndexOf('/');
                String folder = file.getPath().substring(0, i);
                TextView_folder_L_val1_T2.setText(folder);
                Load_XML(path_xml, "T2");
                Values.File_XML_path_T2_L = path_xml;

            }
            // Send the program to PLC


            if (testa.equals("T1")) {
                Mci_write_dtDB_prog_name.path_file = path_file_udf;     //invio il path al CN
                Mci_write_dtDB_prog_name.write_flag = true;

                Mci_write_vb1019_Load_Prog.valore = 1.0d;               //comando di forzatura load profilo
                Mci_write_vb1019_Load_Prog.write_flag = true;

                Mci_write_Vb1025_AppReloadParamC1.valore = 1.0d;        //serve rileggere il file parametri (non il file udf programma)
                Mci_write_Vb1025_AppReloadParamC1.write_flag = true;

                Mci_write_C1_Udf_ValTensione.valore = Double.valueOf(ricetta_T1.Udf_ValTensioneT1);
                Mci_write_C1_Udf_ValTensione.valore_precedente = Mci_write_C1_Udf_ValTensione.valore;  //altrimenti chiama Visualizza_TensioneFilo

                Double val = (Double) Mci_write_C1_Udf_ValTensione.valore;
                TextView_valore_tensione.setText(SubString.Before(val.toString(), "."));

            } else  //T2
            {
                Mci_write_dtDB_progT2_Right_name.path_file = path_file_udf;     //invio il path al CN
                Mci_write_dtDB_progT2_Right_name.write_flag = true;


                Mci_write_vb2019_Load_Prog_T2_Right.valore = 1.0d;
                Mci_write_vb2019_Load_Prog_T2_Right.write_flag = true;

                Mci_write_Vb2025_AppReloadParamC2.valore = 1.0d;        //serve rileggere il file parametri (non il file udf programma)
                Mci_write_Vb2025_AppReloadParamC2.write_flag = true;

                Mci_write_C2_Udf_ValTensione.valore = Double.valueOf(ricetta_T2.Udf_ValTensioneT1);
                Mci_write_C2_Udf_ValTensione.valore_precedente = Mci_write_C2_Udf_ValTensione.valore;//altrimenti chiama Visualizza_TensioneFilo
                Double val = (Double) Mci_write_C2_Udf_ValTensione.valore;
                TextView_valore_tensione_C2.setText(SubString.Before(val.toString(), "."));

            }
        }

    }

    /**
     * Button for unlock all pliers
     *
     * @param view
     */
    public void On_click_Pinze_All_C1(View view) {
        if ((Double) MultiCmd_V4507_AppManAuto.getValue() == 0.0d && ((Double) MultiCmd_Vb4509_StatoTastoPiegatore.getValue() == 0.0d)
                 && ((Double) Mci_write_Vb4138_C1_Cambio_Frontali.mci.getValue() == 0.0d)
                && ((Double) Mci_write_Vb4139_C1_Cambio_Laterali.mci.getValue() == 0.0d)
                && ((Double) Mci_write_Vb4137_C1_Cambio_Corpo.mci.getValue() == 0.0d)
                && ((Double) Mci_write_Vb4140_Cambio_spigoli.mci.getValue() == 0.0d)
                && (Double) MultiCmd_Vb80ShowIconaTools.getValue() == 0.0d) {
            Mci_write_Vb4157_C1_Cambio_Pinze_All.Fronte_positivo = true;
            Mci_write_Vb4157_C1_Cambio_Pinze_All.valore = 1.0d;
            Mci_write_Vb4157_C1_Cambio_Pinze_All.write_flag = true;
        }
    }
    //*************************************************************************************************
    // On_click_Pinze_All_C2
    //*************************************************************************************************
    public void On_click_Pinze_All_C2(View view) throws IOException
    {

        if((Double)MultiCmd_V4507_AppManAuto.getValue() == 0.0d && (Double) MultiCmd_V4507_AppManAuto.getValue() == 0.0d &&
                ((Double) MultiCmd_Vb4509_StatoTastoPiegatore.getValue() == 0.0d)&& (Double) MultiCmd_Vb80ShowIconaTools.getValue() == 0.0d){
            Mci_write_Vb4189_C2_Cambio_Pinze_All.Fronte_positivo = true;
            Mci_write_Vb4189_C2_Cambio_Pinze_All.valore = 1.0d;
            Mci_write_Vb4189_C2_Cambio_Pinze_All.write_flag = true;
        }



    }
    /**
     * Button for on_click_button_tens_piu all pliers
     *
     * @param view
     */
    public void on_click_button_tens_piu(View view) {
        double val_tens = Mci_write_C1_Udf_ValTensione.valore;
        val_tens++;
        Mci_write_C1_Udf_ValTensione.valore = val_tens;
        Mci_write_Vb4061_AppTensAumenta.valore = 1.0d;
        Mci_write_Vb4061_AppTensAumenta.write_flag = true;


    }
    /**
     * Button for on_click_button_tens_meno all pliers
     *
     * @param view
     */
    public void on_click_button_tens_meno(View view) {
        double val_tens = Mci_write_C1_Udf_ValTensione.valore;
        val_tens--;
        Mci_write_C1_Udf_ValTensione.valore = val_tens;
        Mci_write_Vb4062_AppTensDiminuisce.valore = 1.0d;
        Mci_write_Vb4062_AppTensDiminuisce.write_flag = true;
    }

    /**
     * Button for on_click_button_tens_piu all pliers
     *
     * @param view
     */
    public void on_click_button_tens_piu_C2(View view) {
        double val_tens = Mci_write_C2_Udf_ValTensione.valore;
        val_tens++;
        Mci_write_C2_Udf_ValTensione.valore = val_tens;
        Mci_write_Vb4063_AppTensAumenta.valore = 1.0d;
        Mci_write_Vb4063_AppTensAumenta.write_flag = true;


    }
    /**
     * Button for on_click_button_tens_meno all pliers
     *
     * @param view
     */
    public void on_click_button_tens_meno_C2(View view) {
        double val_tens = Mci_write_C2_Udf_ValTensione.valore;
        val_tens--;
        Mci_write_C2_Udf_ValTensione.valore = val_tens;
        Mci_write_Vb4064_AppTensDiminuisce.valore = 1.0d;
        Mci_write_Vb4064_AppTensDiminuisce.write_flag = true;
    }
    //#region UnlockPliers

    /**
     * Button for unlock frontal pliers
     *
     * @param view
     */
    public void On_click_Cambio_frontale(View view) {
        if ((Double) MultiCmd_V4507_AppManAuto.getValue() == 0.0d && (Double) MultiCmd_Vb4157_C1_Cambio_Pinze_All.getValue() == 1.0d) {
            Mci_write_Vb4138_C1_Cambio_Frontali.Fronte_positivo = true;
            Mci_write_Vb4138_C1_Cambio_Frontali.valore = 1.0d;
            Mci_write_Vb4138_C1_Cambio_Frontali.write_flag = true;
        }
    }

    /**
     * Button for unlock side pliers
     *
     * @param view
     */
    public void On_click_Cambio_laterali(View view) {
        if ((Double) MultiCmd_V4507_AppManAuto.getValue() == 0.0d && (Double) MultiCmd_Vb4157_C1_Cambio_Pinze_All.getValue() == 1.0d) {
            Mci_write_Vb4139_C1_Cambio_Laterali.Fronte_positivo = true;
            Mci_write_Vb4139_C1_Cambio_Laterali.valore = 1.0d;
            Mci_write_Vb4139_C1_Cambio_Laterali.write_flag = true;
        }
    }

    /**
     * Button for unlock body pliers
     *
     * @param view
     */
    public void On_click_Cambio_corpo(View view) {
        if ((Double) MultiCmd_V4507_AppManAuto.getValue() == 0.0d && (Double) MultiCmd_Vb4157_C1_Cambio_Pinze_All.getValue() == 1.0d) {
            Mci_write_Vb4137_C1_Cambio_Corpo.Fronte_positivo = true;
            Mci_write_Vb4137_C1_Cambio_Corpo.valore = 1.0d;
            Mci_write_Vb4137_C1_Cambio_Corpo.write_flag = true;
        }
    }

    /**
     * Button for unlock angle pliers
     *
     * @param view
     */
    public void On_click_Cambio_spigoli(View view) {
        if ((Double) MultiCmd_V4507_AppManAuto.getValue() == 0.0d && (Double) MultiCmd_Vb4157_C1_Cambio_Pinze_All.getValue() == 1.0d) {
            Mci_write_Vb4140_Cambio_spigoli.Fronte_positivo = true;
            Mci_write_Vb4140_Cambio_spigoli.valore = 1.0d;
            Mci_write_Vb4140_Cambio_spigoli.write_flag = true;
        }
    }

    /**
     * Button for center the image and reset the zoom
     *
     * @param view
     */
    public void btn_Center_on_click(View view) {
        myView_T1.Center_Bitmap_Main(1.05F, 317, 10);
        myView_T1.AggiornaCanvas(true);
    }
    /**
     * Button for center the image and reset the zoom
     *
     * @param view
     */
    public void btn_Center_on_click_C2(View view) {
        myView_T2.Center_Bitmap_Main(1.05F, 317, 10);
        myView_T2.AggiornaCanvas(true);
    }




    //#endregion UnlockPliers

    //#region Zoom

    /**
     * Button for zoom in
     *
     * @param view
     */
    public void btn_ZoomPiu_on_click(View view) {
        myView_T1.Zoom(0.1F);
        myView_T1.AggiornaCanvas(true);
    }
    /**
     * Button for zoom in
     *
     * @param view
     */
    public void btn_ZoomPiu_on_click_C2(View view) {
        myView_T2.Zoom(0.1F);
        myView_T2.AggiornaCanvas(true);
    }


    /**
     * Button for zoom out
     *
     * @param view
     */
    public void btn_ZoomMeno_on_click(View view) {
        myView_T1.Zoom(-0.1F);
        myView_T1.AggiornaCanvas(true);
    }
    /**
     * Button for zoom out
     *
     * @param view
     */
    public void btn_ZoomMeno_on_click_C2(View view) {
        myView_T2.Zoom(-0.1F);
        myView_T2.AggiornaCanvas(true);
    }



    //#endregion Zoom

    /**
     * Button for make right pocket active
     *
     * @param view
     */
    public void onclickTascaDXButton(View view) {
        if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 1.0d) {
            Mci_write_Vn197_tasca_left_C1.valore = 0.0d;
            Mci_write_Vn196_tasca_right_C1.valore = 1.0d;
            Mci_write_Vn197_tasca_left_C1.write_flag = true;
            Mci_write_Vn196_tasca_right_C1.write_flag = true;
            Carica_programma(Values.File_XML_path_R,"T1", "DX");
        }
    }
    /**
     * Button 4.0
     *
     *
     */
    public void on_click_tcp4e0(View view) throws IOException {
        int background = (int) Button_4_0.getTag();
        if(background == R.drawable.ic_4e0){
            int image_Premuto = this.getResources().getIdentifier("ic_4e0_press", "drawable", this.getPackageName());
            Button_4_0.setBackground(this.getResources().getDrawable((image_Premuto)));
            Button_4_0.setTag(R.drawable.ic_4e0_press);
            Values.TcpButton = "true";


            KillThread();

            Intent intent_for_tcp = new Intent(getApplicationContext(), Tcp4_0.class);
            startActivityForResult(intent_for_tcp, RESULT_PAGE_TCP);

        }else {
            int image_Premuto = this.getResources().getIdentifier("ic_4e0", "drawable", this.getPackageName());
            Button_4_0.setBackground(this.getResources().getDrawable((image_Premuto)));
            Button_4_0.setTag(R.drawable.ic_4e0);
            Values.TcpButton = "false";
        }
        Info_file.Scrivi_campo("storage/emulated/0/JamData/Tcp.txt", "InfoJAM", "Tcp", null, null, "TcpButton", Values.TcpButton, getApplicationContext());




    }
    /**
     * Button for make left pocket active
     *
     * @param view
     */
    public void onclickTascaSXButton(View view) {
        if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 1.0d) {
            Mci_write_Vn197_tasca_left_C1.valore = 1.0d;
            Mci_write_Vn196_tasca_right_C1.valore = 0.0d;
            Mci_write_Vn197_tasca_left_C1.write_flag = true;
            Mci_write_Vn196_tasca_right_C1.write_flag = true;
            Carica_programma(Values.File_XML_path_L,"T1", "SX");
        }
    }
    /**
     * Button for make right pocket active
     *
     * @param view
     */
    public void onclickTascaDXButton_C2(View view) {
        if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 1.0d) {
            Mci_write_Vn199_tasca_left_C2.valore = 0.0d;
            Mci_write_Vn198_tasca_right_C2.valore = 1.0d;
            Mci_write_Vn199_tasca_left_C2.write_flag = true;
            Mci_write_Vn198_tasca_right_C2.write_flag = true;
            Carica_programma(Values.File_XML_path_T2_R, "T2", "DX");
        }
    }
    /**
     * Button for make left pocket active
     *
     * @param view
     */
    public void onclickTascaSXButton_C2(View view) {
        if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 1.0d) {
            Mci_write_Vn199_tasca_left_C2.valore = 1.0d;
            Mci_write_Vn198_tasca_right_C2.valore = 0.0d;
            Mci_write_Vn199_tasca_left_C2.write_flag = true;
            Mci_write_Vn198_tasca_right_C2.write_flag = true;
            Carica_programma(Values.File_XML_path_T2_L, "T2", "SX");
        }
    }


    /**
     * Button for reset alarms
     *
     * @param view
     */
    public void onClick_ResetAllarme(View view) {
        Button_step_piu.setVisibility(View.VISIBLE);
        Button_step_piu_C2.setVisibility(View.VISIBLE);
        Button_step_meno.setVisibility(View.VISIBLE);
        Button_step_meno_C2.setVisibility(View.VISIBLE);
        Button_reset.setVisibility(View.VISIBLE);
        Button_reset_C2.setVisibility(View.VISIBLE);
        Button_All.setVisibility(View.VISIBLE);
        Button_All_C2.setVisibility(View.VISIBLE);
        Button_zoomPiu.setVisibility(View.VISIBLE);
        Button_zoomPiu_C2.setVisibility(View.VISIBLE);
        Button_zoomMeno.setVisibility(View.VISIBLE);
        Button_zoomMeno_C2.setVisibility(View.VISIBLE);
        Button_reset_spola.setVisibility(View.VISIBLE);
        Button_reset_spola_C2.setVisibility(View.VISIBLE);
        Button_tens_meno.setVisibility(View.VISIBLE);
        Button_tens_piu.setVisibility(View.VISIBLE);
        Button_tens_meno_C2.setVisibility(View.VISIBLE);
        Button_tens_piu_C2.setVisibility(View.VISIBLE);
        TextView_Cnt_spola.setVisibility(View.VISIBLE);
        TextView_Cnt_spola_C2.setVisibility(View.VISIBLE);
        TextSpola_limite.setVisibility(View.VISIBLE);
        TextSpola_limite_C2.setVisibility(View.VISIBLE);
        TextView_valore_tensione.setVisibility(View.VISIBLE);
        TextView_valore_tensione_C2.setVisibility(View.VISIBLE);
        TextView_percento.setVisibility(View.VISIBLE);
        TextView_percento_C2.setVisibility(View.VISIBLE);
        Button_reset_allarme.setVisibility(View.VISIBLE);
        TextView_errore.setText("");
        TextView_errore.setTextColor(Color.BLACK);

        Mci_Vn2_allarmi_da_CN.valore = 0.0d;
        Mci_Vn2_allarmi_da_CN.valore_precedente = 0.0d;
        Mci_Vn2_allarmi_da_CN.write_flag = true;
        DrawTasca("T1");

        Button_reset_allarme.setVisibility(View.GONE);
    }



    /**
     * Button for open the debug page
     *
     * @param view
     */
    public void on_click_debug(View view) {
        // I register the receiver but it will be unregistered inside it so i don't need it in KillThread()
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_debug, new IntentFilter("KeyDialog_parameter_ret"));
        KeyDialog.Lancia_KeyDialogo(null, MainActivity.this, null, 99999d, 0d, false, false, 0d, true, "KeyDialog_parameter_ret", false,"");
    }

    /**
     * Button for open the tool page and handle result
     *
     * @param view
     */
    public void onclickPaginaTools(View view) {
       // AggiornaUdf();
        Intent intent_for_tools = new Intent(getApplicationContext(), Tool_page.class);

        if ((Double) MultiCmd_Vn196_num_prog_right_C1.getValue() == 1.0d)
            intent_for_tools.putExtra("Lato_tasca_T1", "DX");
        else
            intent_for_tools.putExtra("Lato_tasca_T1", "SX");

        if ((Double) MultiCmd_Vn198_num_prog_right_C2 .getValue() == 1.0d)
            intent_for_tools.putExtra("Lato_tasca_T2", "DX");
        else
            intent_for_tools.putExtra("Lato_tasca_T2", "SX");

        intent_for_tools.putExtra("chiamante", "MainPage");

        KillThread();
        startActivityForResult(intent_for_tools, RESULT_PAGE_TOOLS);
    }

    /**
     * Button for set the left pocket as active
     *
     * @param view
     */
    public void On_click_Load_L(View view) {
        On_click_Load("L");
    }

    /**
     * Button for set the right pocket as active
     *
     * @param view
     */
    public void On_click_Load_R(View view) {
        On_click_Load("R");
    }
    //*************************************************************************************************
    // On_click_Load_R_T2
    //*************************************************************************************************
    public void On_click_Load_R_C2(View view) throws IOException
    {

        On_click_Load("T2_R");


    }
    //*************************************************************************************************
    // On_click_Load_L_C2
    //*************************************************************************************************
    public void On_click_Load_L_C2(View view) throws IOException
    {

        On_click_Load("T2_L");


    }





    /**
     * Button for open the page for send programs to the CN
     *
     * @param r
     */
    private void On_click_Load(String r) {
        KillThread();
        Intent intent = new Intent(getApplicationContext(), Select_file_to_CN.class);
        switch (r) {
            case "R":
                intent.putExtra("operazione", "Loading....");
                intent.putExtra("Chiamante", "T1_R");
                intent.putExtra("FilePath_Da_Tasca_Quote_r1", "");
                intent.putExtra("FilePath_Da_Tasca_Quote_r2", "");
                startActivityForResult(intent, RESULT_PAGE_LOAD_UDF_R_T1);
                break;
            case "L":
                intent.putExtra("operazione", "Loading....");
                intent.putExtra("Chiamante", "T1_L");
                intent.putExtra("FilePath_Da_Tasca_Quote_r1", "");
                intent.putExtra("FilePath_Da_Tasca_Quote_r2", "");
                startActivityForResult(intent, RESULT_PAGE_LOAD_UDF_L_T1);
                break;
            case "T2_R":
                intent.putExtra("operazione", "Loading....");
                intent.putExtra("Chiamante", "T2_R");
                intent.putExtra("FilePath_Da_Tasca_Quote_r1", "");
                intent.putExtra("FilePath_Da_Tasca_Quote_r2", "");
                startActivityForResult(intent, RESULT_PAGE_LOAD_UDF_R_T2);
                break;
            case "T2_L":
                intent.putExtra("operazione", "Loading....");
                intent.putExtra("Chiamante", "T2_L");
                intent.putExtra("FilePath_Da_Tasca_Quote_r1", "");
                intent.putExtra("FilePath_Da_Tasca_Quote_r2", "");
                startActivityForResult(intent, RESULT_PAGE_LOAD_UDF_L_T2);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent databack) {
        super.onActivityResult(requestCode, resultCode, databack);

        ThreadGroup currentGroup1 = Thread.currentThread().getThreadGroup();
        int noThreads1 = currentGroup1.activeCount();
        Thread[] lstThreads1 = new Thread[noThreads1];
        currentGroup1.enumerate(lstThreads1);


        String returnedResult = "";
        try {
            returnedResult = databack.getData().toString();
        } catch (Exception e) {
            e.printStackTrace();
            returnedResult = "";
        }
        StopThread = false;
        if (!Thread_Running) {
            StopThread = false;

            MyAndroidThread_Main myTask_main = new MyAndroidThread_Main(this);
            thread_Main = new Thread(myTask_main, "Main myTask");
            thread_Main.start();
            Log.d("JAM TAG", "Start MainActivity Thread from onActivity Result");
        }


        try {
            if(Values.Chiamante == "CREATO_TASCA_DA_QUOTE_1" ||   Values.Chiamante =="CREATO_TASCA_DA_QUOTE_2" || Values.Chiamante =="CREATO_TASCA_DA_QUOTE_12"){
                requestCode = RESULT_PAGE_TOOLS;
                returnedResult = "CREATO_TASCA_DA_QUOTE";
                Values.Chiamante = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            Values.Chiamante = "";
        }

        switch (requestCode) {
            case RESULT_PAGE_TOOLS:
                switch (returnedResult){

                    case "PAGE_UDF_T1_DX":
                    case "CARICATO_T1_DX":
                        try {
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.txt", "InfoJAM", "LastProgram", null, null, "LastProgram_R", Values.File_XML_path_R, getApplicationContext());
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.bak", "InfoJAM", "LastProgram", null, null, "LastProgram_R", Values.File_XML_path_R, getApplicationContext());

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "info_Jam.txt file error", Toast.LENGTH_SHORT).show();
                        }
                        Carica_programma(Values.File_XML_path_R,"T1", "DX");
                        if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 1.0d) {
                            Mci_write_Vn196_tasca_right_C1.valore= 1.0d;
                            Mci_write_Vn196_tasca_right_C1.write_flag = true;
                            Mci_write_Vn197_tasca_left_C1.valore = 0.0d;
                            Mci_write_Vn197_tasca_left_C1.write_flag = true;

                        }


                        Mci_write_Vb4807_PinzeAlteDopoPC.valore = 1.0d;
                        Mci_write_Vb4807_PinzeAlteDopoPC.write_flag = true;

                        Mci_write_Vb52_goPC.valore = 1.0d;
                        Mci_write_Vb52_goPC.write_flag = true;

                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.valore = 1.0d;
                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.write_flag = true;

                        Mci_write_Vb53_goPC_C2.valore = 1.0d;
                        Mci_write_Vb53_goPC_C2.write_flag = true;
                        break;
                    case "PAGE_UDF_T1_SX":
                    case "CARICATO_T1_SX":
                        try {
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.txt", "InfoJAM", "LastProgram", null, null, "LastProgram_L", Values.File_XML_path_L, getApplicationContext());
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.bak", "InfoJAM", "LastProgram", null, null, "LastProgram_L", Values.File_XML_path_L, getApplicationContext());

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "info_Jam.txt file error", Toast.LENGTH_SHORT).show();
                        }
                        Carica_programma(Values.File_XML_path_L, "T1","SX");
                        if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 1.0d) {
                            Mci_write_Vn196_tasca_right_C1.valore= 0.0d;
                            Mci_write_Vn196_tasca_right_C1.write_flag = true;
                            Mci_write_Vn197_tasca_left_C1.valore = 1.0d;
                            Mci_write_Vn197_tasca_left_C1.write_flag = true;

                        }



                        Mci_write_Vb4807_PinzeAlteDopoPC.valore = 1.0d;
                        Mci_write_Vb4807_PinzeAlteDopoPC.write_flag = true;

                        Mci_write_Vb52_goPC.valore = 1.0d;
                        Mci_write_Vb52_goPC.write_flag = true;

                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.valore = 1.0d;
                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.write_flag = true;

                        Mci_write_Vb53_goPC_C2.valore = 1.0d;
                        Mci_write_Vb53_goPC_C2.write_flag = true;
                        break;

                    case "PAGE_UDF_T2_DX":
                    case "CARICATO_T2_DX":
                        try {
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.txt", "InfoJAM", "LastProgram", null, null, "LastProgram_R_T2", Values.File_XML_path_T2_R, getApplicationContext());
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.bak", "InfoJAM", "LastProgram", null, null, "LastProgram_R_T2", Values.File_XML_path_T2_R, getApplicationContext());

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "info_Jam.txt file error", Toast.LENGTH_SHORT).show();
                        }
                        Carica_programma(Values.File_XML_path_T2_R,"T2", "DX");

                        if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 1.0d) {
                            Mci_write_Vn198_tasca_right_C2.valore = 1.0d;
                            Mci_write_Vn198_tasca_right_C2.write_flag = true;
                            Mci_write_Vn199_tasca_left_C2.valore = 0.0d;
                            Mci_write_Vn199_tasca_left_C2.write_flag = true;

                        }

                        Mci_write_Vb4807_PinzeAlteDopoPC.valore = 1.0d;
                        Mci_write_Vb4807_PinzeAlteDopoPC.write_flag = true;

                        Mci_write_Vb52_goPC.valore = 1.0d;
                        Mci_write_Vb52_goPC.write_flag = true;

                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.valore = 1.0d;
                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.write_flag = true;

                        Mci_write_Vb53_goPC_C2.valore = 1.0d;
                        Mci_write_Vb53_goPC_C2.write_flag = true;
                        break;
                    case "PAGE_UDF_T2_SX":
                    case "CARICATO_T2_SX":
                        try {
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.txt", "InfoJAM", "LastProgram", null, null, "LastProgram_L_T2", Values.File_XML_path_T2_L, getApplicationContext());
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.bak", "InfoJAM", "LastProgram", null, null, "LastProgram_L_T2", Values.File_XML_path_T2_L, getApplicationContext());

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "info_Jam.txt file error", Toast.LENGTH_SHORT).show();
                        }
                        Carica_programma(Values.File_XML_path_T2_L, "T2","SX");

                        if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 1.0d) {
                            Mci_write_Vn198_tasca_right_C2.valore = 0.0d;
                            Mci_write_Vn198_tasca_right_C2.write_flag = true;
                            Mci_write_Vn199_tasca_left_C2.valore = 1.0d;
                            Mci_write_Vn199_tasca_left_C2.write_flag = true;
                        }

                        Mci_write_Vb4807_PinzeAlteDopoPC.valore = 1.0d;
                        Mci_write_Vb4807_PinzeAlteDopoPC.write_flag = true;

                        Mci_write_Vb52_goPC.valore = 1.0d;
                        Mci_write_Vb52_goPC.write_flag = true;

                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.valore = 1.0d;
                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.write_flag = true;

                        Mci_write_Vb53_goPC_C2.valore = 1.0d;
                        Mci_write_Vb53_goPC_C2.write_flag = true;
                        break;
                    case "CREATO_TASCA_DA_QUOTE":
                        Carica_programma(Values.File_XML_path_R,"T1", "DX");
                        Mci_write_Vb4807_PinzeAlteDopoPC.valore = 1.0d;
                        Mci_write_Vb4807_PinzeAlteDopoPC.write_flag = true;

                        Mci_write_Vb52_goPC.valore = 1.0d;
                        Mci_write_Vb52_goPC.write_flag = true;

                        Carica_programma(Values.File_XML_path_T2_R,"T2", "DX");
                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.valore = 1.0d;
                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.write_flag = true;

                        Mci_write_Vb53_goPC_C2.valore = 1.0d;
                        Mci_write_Vb53_goPC_C2.write_flag = true;

                        break;



                    default:
                        Mci_write_Vb4807_PinzeAlteDopoPC.valore = 1.0d;
                        Mci_write_Vb4807_PinzeAlteDopoPC.write_flag = true;

                        Mci_write_Vb52_goPC.valore = 1.0d;
                        Mci_write_Vb52_goPC.write_flag = true;

                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.valore = 1.0d;
                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.write_flag = true;

                        Mci_write_Vb53_goPC_C2.valore = 1.0d;
                        Mci_write_Vb53_goPC_C2.write_flag = true;
                        break;


                }

            case RESULT_PAGE_LOAD_UDF_R_T1:     //ho caricato un programma dalla pagina MAIN
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

                        Carica_programma(Values.File_XML_path_R, "T1","DX");
                        if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 1.0d) {
                            Mci_write_Vn196_tasca_right_C1.valore= 1.0d;
                            Mci_write_Vn196_tasca_right_C1.write_flag = true;
                            Mci_write_Vn197_tasca_left_C1.valore = 0.0d;
                            Mci_write_Vn197_tasca_left_C1.write_flag = true;

                        }

                        Mci_write_Vb4807_PinzeAlteDopoPC.valore = 1.0d;
                        Mci_write_Vb4807_PinzeAlteDopoPC.write_flag = true;

                        Mci_write_Vb52_goPC.valore = 1.0d;
                        Mci_write_Vb52_goPC.write_flag = true;

                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.valore = 1.0d;
                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.write_flag = true;

                        Mci_write_Vb53_goPC_C2.valore = 1.0d;
                        Mci_write_Vb53_goPC_C2.write_flag = true;
                    }
                }
                break;
            case RESULT_PAGE_LOAD_UDF_L_T1:  //ho caricato un programma dalla pagina MAIN
                if (returnedResult.equals("CARICATO")) {
                    try {
                        Thread.sleep((long) 300d);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                    }

                    if (Values.File_XML_path_L != null) {
                        try {
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.txt", "InfoJAM", "LastProgram", null, null, "LastProgram_L", Values.File_XML_path_L, getApplicationContext());
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.bak", "InfoJAM", "LastProgram", null, null, "LastProgram_L", Values.File_XML_path_L, getApplicationContext());

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "info_Jam_.txt file error", Toast.LENGTH_SHORT).show();
                        }

                        Carica_programma(Values.File_XML_path_L,"T1", "SX");

                        if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 1.0d) {
                            Mci_write_Vn196_tasca_right_C1.valore= 0.0d;
                            Mci_write_Vn196_tasca_right_C1.write_flag = true;
                            Mci_write_Vn197_tasca_left_C1.valore = 1.0d;
                            Mci_write_Vn197_tasca_left_C1.write_flag = true;

                        }

                        Mci_write_Vb4807_PinzeAlteDopoPC.valore = 1.0d;
                        Mci_write_Vb4807_PinzeAlteDopoPC.write_flag = true;

                        Mci_write_Vb52_goPC.valore = 1.0d;
                        Mci_write_Vb52_goPC.write_flag = true;

                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.valore = 1.0d;
                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.write_flag = true;

                        Mci_write_Vb53_goPC_C2.valore = 1.0d;
                        Mci_write_Vb53_goPC_C2.write_flag = true;
                    }
                }
                break;
            case RESULT_PAGE_LOAD_UDF_R_T2:   //ho caricato un programma dalla pagina MAIN
                if(returnedResult.equals("CARICATO")) {
                    try {
                        Thread.sleep((long) 300d);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                    }


                    if (Values.File_XML_path_T2_R != null) {

                        try {
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.txt", "InfoJAM", "LastProgram", null, null, "LastProgram_R_T2", Values.File_XML_path_T2_R, getApplicationContext());
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.bak", "InfoJAM", "LastProgram", null, null, "LastProgram_R_T2", Values.File_XML_path_T2_R, getApplicationContext());

                        } catch (IOException e) {
                            e.printStackTrace();

                            Toast.makeText(this,
                                    "info_Jam.txt file error",
                                    Toast.LENGTH_SHORT).show();

                        }

                        Carica_programma(Values.File_XML_path_T2_R, "T2", "DX");

                        if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 1.0d) {
                            Mci_write_Vn198_tasca_right_C2.valore = 1.0d;
                            Mci_write_Vn198_tasca_right_C2.write_flag = true;
                            Mci_write_Vn199_tasca_left_C2.valore = 0.0d;
                            Mci_write_Vn199_tasca_left_C2.write_flag = true;

                        }

                        Mci_write_Vb4807_PinzeAlteDopoPC.valore = 1.0d;
                        Mci_write_Vb4807_PinzeAlteDopoPC.write_flag = true;

                        Mci_write_Vb52_goPC.valore = 1.0d;
                        Mci_write_Vb52_goPC.write_flag = true;

                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.valore = 1.0d;
                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.write_flag = true;

                        Mci_write_Vb53_goPC_C2.valore = 1.0d;
                        Mci_write_Vb53_goPC_C2.write_flag = true;

                    }
                }

                break;

            case RESULT_PAGE_LOAD_UDF_L_T2:   //ho caricato un programma dalla pagina MAIN
                if(returnedResult.equals("CARICATO")) {
                    try {
                        Thread.sleep((long) 300d);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                    }


                    if (Values.File_XML_path_T2_L != null) {
                        try {
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.txt", "InfoJAM", "LastProgram", null, null, "LastProgram_L_T2", Values.File_XML_path_T2_L, getApplicationContext());
                            Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.bak", "InfoJAM", "LastProgram", null, null, "LastProgram_L_T2", Values.File_XML_path_T2_L, getApplicationContext());

                        } catch (IOException e) {
                            e.printStackTrace();

                            Toast.makeText(this,
                                    "info_Jam.txt file error",
                                    Toast.LENGTH_SHORT).show();

                        }

                        Carica_programma(Values.File_XML_path_T2_L, "T2", "SX");

                        if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 1.0d) {
                            Mci_write_Vn198_tasca_right_C2.valore = 0.0d;
                            Mci_write_Vn198_tasca_right_C2.write_flag = true;
                            Mci_write_Vn199_tasca_left_C2.valore = 1.0d;
                            Mci_write_Vn199_tasca_left_C2.write_flag = true;
                        }

                        Mci_write_Vb4807_PinzeAlteDopoPC.valore = 1.0d;
                        Mci_write_Vb4807_PinzeAlteDopoPC.write_flag = true;

                        Mci_write_Vb52_goPC.valore = 1.0d;
                        Mci_write_Vb52_goPC.write_flag = true;

                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.valore = 1.0d;
                        Mci_write_Vb4907_PinzeAlteDopoPC_C2.write_flag = true;

                        Mci_write_Vb53_goPC_C2.valore = 1.0d;
                        Mci_write_Vb53_goPC_C2.write_flag = true;
                    }
                }

                break;

            case RESULT_PAGE_TCP:

                break;
            default:
                Mci_write_Vb4807_PinzeAlteDopoPC.valore = 1.0d;
                Mci_write_Vb4807_PinzeAlteDopoPC.write_flag = true;

                Mci_write_Vb52_goPC.valore = 1.0d;
                Mci_write_Vb52_goPC.write_flag = true;

                Mci_write_Vb4907_PinzeAlteDopoPC_C2.valore = 1.0d;
                Mci_write_Vb4907_PinzeAlteDopoPC_C2.write_flag = true;

                Mci_write_Vb53_goPC_C2.valore = 1.0d;
                Mci_write_Vb53_goPC_C2.write_flag = true;
                break;
        }
    }

    private void GestioneVisualizzazioneToggleButton_da_Ingresso(MultiCmdItem Multicmd_in, Button button, String ic_press, String ic_unpress) {
        if ((Double) Multicmd_in.getValue() == 1.0d) {
            int image_Premuto = this.getResources().getIdentifier(ic_press, "drawable", this.getPackageName());
            button.setBackground(this.getResources().getDrawable((image_Premuto)));
        } else {
            int image_Premuto = this.getResources().getIdentifier(ic_unpress, "drawable", this.getPackageName());
            button.setBackground(this.getResources().getDrawable((image_Premuto)));
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

    private void KillThread() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver_debug);
        StopThread = true;

        try {
            if (!Thread_Running)
                thread_Main.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /*
        if(myView_T1 != null) {
            myView_T1.release();    //rilascio la memoria delle bitmap
            myView_T1 = null;
        }
        if(myView_T2 != null) {
            myView_T2.release();    //rilascio la memoria delle bitmap
            myView_T2 = null;
        }

         */
        Log.d("JAM TAG", "End MainActivity Thread");
    }

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

            if (val.equals(linea2) || val.equals("67874")) {
                KillThread();

                Intent settings = new Intent(getApplicationContext(), Debug.class);
                startActivity(settings);

            } else {
                Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
            }
        }
    };

    class MyAndroidThread_Main implements Runnable {
        Activity activity;
        boolean rc_error;

        public MyAndroidThread_Main(Activity activity) {
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
                        MultiCmd_Vn3804_pagina_touch.setValue(0.0d);
                        sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                }

                if (sl.IsConnected()) {

                    if (first_cycle) {
                        first_cycle = false;
                        MultiCmd_Vn4_Warning.setValue(0.0d);
                        sl.WriteItem(MultiCmd_Vn4_Warning);

                        sl.ReadItem(MultiCmd_Vq1110_Speed);
                        sl.ReadItem(MultiCmd_Vq1913_C1_UdfVelLavRPM);
                        double speed = (double) MultiCmd_Vq1110_Speed.getValue();
                        double speedmax = (double) MultiCmd_Vq1913_C1_UdfVelLavRPM.getValue();
                        if (speedmax < 100000)
                            speedmax = speedmax * 1000;
                        double delta = speed / speedmax;
                        seekBar_speed.setProgress((100 * (int) (delta * 100)) / 100);

                        if (Values.Machine_model.equals("JT882M")){

                            sl.ReadItem(MultiCmd_Vq2110_Speed_T2);
                            sl.ReadItem(MultiCmd_Vq2913_C2_UdfVelLavRPM);
                            double speed_C2 = (double) MultiCmd_Vq2110_Speed_T2.getValue();
                            double speedmax_C2 = (double) MultiCmd_Vq2913_C2_UdfVelLavRPM.getValue();
                            if (speedmax_C2 < 100000)
                                speedmax_C2 = speedmax_C2 * 1000;
                            double delta_C2 = speed_C2 / speedmax_C2;
                            seekBar_speed_C2.setProgress((100 * (int) (delta_C2 * 100)) / 100);
                        }

                        AggiornoMciValoriAccensione();
                    }
                    rc_error = false;
                    sl.Clear();

                    // ------------------------ RX -------------------------------
                    sl.ReadItems(mci_array_read_all);
                    if (sl.getReturnCode() != 0) {
                        rc_error = true;
                    }
                    sl.ReadItems(mci_array_read_all1);
                    if (sl.getReturnCode() != 0) {
                        rc_error = true;
                    }
                    if( Values.Machine_model.equals("JT882M")){
                        sl.ReadItems(mci_array_read_882);
                        if (sl.getReturnCode() != 0) {
                            rc_error = true;
                        }
                    }




                    if (!rc_error) {
                        MultiCmd_Vn3804_pagina_touch.setValue(1001.0d);
                        sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                        if ((Double) MultiCmd_Vq1110_Speed.getValue() < 50000) {     //50000 = 50rpm
                            Mci_write_Vq1110_Speed.valore = 100d;
                            Mci_write_Vq1110_Speed.write_flag = true;
                        }
                        if ((Double) MultiCmd_Vq2110_Speed_T2.getValue() < 50000) {     //50000 = 50rpm
                            Mci_write_Vq2110_Speed.valore = 100d;
                            Mci_write_Vq2110_Speed.write_flag = true;
                        }

                        ScrivoStringaDB(Mci_write_dtDB_prog_name);
                        ScrivoStringaDB(Mci_write_dtDB_progT2_Right_name);

                        Utility.ScrivoVbVnVq(sl,Mci_write_Vq1110_Speed);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vq2110_Speed);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vq1913_C1_UdfVelLavRPM);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vb1025_AppReloadParamC1);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vb2025_AppReloadParamC2);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vb4807_PinzeAlteDopoPC);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vb4907_PinzeAlteDopoPC_C2);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vb52_goPC);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vb53_goPC_C2);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vq3511_VelLavCaricatore);
                        Utility.ScrivoVbVnVq(sl,Mci_write_vb2019_Load_Prog_T2_Right);


                        ScrivoStringaDB(Mci_write_dtDB_prog_name);
                        Utility.ScrivoVbVnVq(sl,Mci_write_vb1019_Load_Prog);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb1006_StepPiu_singolo);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb1007_StepMeno_singolo);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vb1022_StepPiu_multiplo);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vb1023_StepMeno_multiplo);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb2006_StepPiu_singolo);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb2007_StepMeno_singolo);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vb2022_StepPiu_multiplo);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vb2023_StepMeno_multiplo);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb4802_Reset_Cuci);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb4902_Reset_Cuci_C2);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vq3597_ImpPuntiSpola);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vq3599_ImpPuntiSpola_C2);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vq1011_numero_Nmulti_step);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb4072_AllarmeContSpola);
                        Utility.GestiscoMci_Out_Toggle(sl,Mci_write_Vb1034_Test_Cuci);
                        Utility.ScrivoVbVnVq(sl,Mci_Vn2_allarmi_da_CN);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vb4061_AppTensAumenta);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vb4063_AppTensAumenta);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vb4062_AppTensDiminuisce);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vb4064_AppTensDiminuisce);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb4901_start_cucitura_T2);


                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb4074_AllarmeContSpola_C2);
                        Utility.GestiscoMci_Out_Toggle(sl, Mci_write_Vb2034_Test_Cuci_C2);

                        if((Double)MultiCmd_Ch2_cucitrice2_Fermo.getValue() == 0.0d) {

                            Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb4904_AppPedaleHmiC2);
                        }


                        Utility.GestiscoMci_Out_Toggle(sl,Mci_write_Vb4157_C1_Cambio_Pinze_All);
                        Utility.GestiscoMci_Out_Toggle(sl,Mci_write_Vb4189_C2_Cambio_Pinze_All);
                        Utility.GestiscoMci_Out_Toggle(sl,Mci_write_Vb4139_C1_Cambio_Laterali);
                        Utility.GestiscoMci_Out_Toggle(sl,Mci_write_Vb4138_C1_Cambio_Frontali);
                        Utility.GestiscoMci_Out_Toggle(sl,Mci_write_Vb4137_C1_Cambio_Corpo);
                        Utility.GestiscoMci_Out_Toggle(sl,Mci_write_Vb4140_Cambio_spigoli);
                        Utility.GestiscoMci_Out_Toggle(sl,Mci_write_Vb1018_SbloccaAgo);
                        Utility.GestiscoMci_Out_Toggle(sl,Mci_write_Vb2018_SbloccaAgo_T2);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vn_etichetta);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vn196_tasca_right_C1);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vn197_tasca_left_C1);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vn198_tasca_right_C2);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vn199_tasca_left_C2);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vb4804_AppReloadParamC1);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb4512_TastoAbilitaTesta2);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_Vb4510_TastoCaricatore);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_Vb4508_TastoPiegatore);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_Vb4514_TastoScaricatore);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_Vb4521_TastoTestPiegatore);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_Vb4522_TastoTestTraslatore);
                        Utility.GestiscoMci_Out_Toggle(sl,Mci_write_vb4519_Scarico_pinza);
                        Utility.GestiscoMci_Out_Toggle(sl,Mci_write_Vb152_Pattina_OnOff);
                        Utility.GestiscoMci_Out_Toggle(sl,Mci_write_Vb157_Pattina_PassoPasso);



                       // Utility.GestiscoMci_Out_Toggle(sl,Mci_write_Vb4512_TastoAbilitaTesta2);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vn109_sequenza_chiusura_piegatore);
                        Utility.ScrivoVbVnVq(sl,Mci_write_Vn110_sequenza_apertura_piegatore);

                        double X = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                        double Y = (Double) MultiCmd_posizione_Y.getValue() / 1000d;
                        double X_C2 = (Double) MultiCmd_posizione_X_C2.getValue() / 1000d;
                        double Y_C2 = (Double) MultiCmd_posizione_Y_C2.getValue() / 1000d;

                        //aggiorno C1_Udf_ValTensione per poter cambiare il valore della tensione mentre cuce
                        if(Mci_write_C1_Udf_ValTensione.write_flag){
                            double v = (double)Mci_write_C1_Udf_ValTensione.valore ;
                            v = v * 1000;
                            Multicmd_C1_Udf_ValTensione.setValue(v);
                            sl.WriteItem(Multicmd_C1_Udf_ValTensione);
                        }

                        Coord_Pinza.set(X, Y, ricetta_T1);
                        if(Values.Machine_model.equals("JT882M")) {
                            Coord_Pinza_C2.set(X_C2, Y_C2, ricetta_T2);
                            if (Mci_write_C2_Udf_ValTensione.write_flag) {
                                double v = (double) Mci_write_C2_Udf_ValTensione.valore;
                                v = v * 1000;
                                Multicmd_C2_Udf_ValTensione.setValue(v);
                                sl.WriteItem(Multicmd_C2_Udf_ValTensione);
                            }

                        }

                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int max_speed = (int) ((double) MultiCmd_Vq1913_C1_UdfVelLavRPM.getValue() / 1000);
                            if(Values.Machine_model.equals("JT862HM") && max_speed > 3500) max_speed =3500;
                            TextView_max_speed_value.setText(""+max_speed); //scrivo la massima velelocità associata al programma

                           Emergenza(activity);

                            if ((Double) MultiCmd_Vb1034_Test_Cuci.getValue() == 0.0d)
                                Button_test_cuci.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.ic_test_cucitura));

                            Visualizza_contatore(TextView_val_speed, Mci_write_Vq1110_Speed, false, true, false);     //velocità
                            Visualizza_contatore(TextView_punti_tot, Mci_write_Vq1951_punti_totali, true, false, false);     //punti totale programma
                            Visualizza_contatore(TextView_punti_parziale, Mci_write_Vq1952_punti_parziali, true, false, false);     //punti parziali programma
                            Visualizza_contatore(TextView_Cnt_spola, Mci_write_Vq3596_ContPuntiSpola, true, false, false);     //conta spola
                            Visualizza_contatore(TextSpola_limite, Mci_write_Vq3597_ImpPuntiSpola, true, false, false);     //conta spola limite
                            Visualizza_contatore(TextView_val_production, Mci_write_Vq3591_CNT_CicliAutomaticoUser, true, false, false);     //produzione

                            GestiscoVisualizzazioneStatusButton(Mci_Vb4511_StatoTastoCaricatore, Button_stato_loader);
                            GestiscoVisualizzazioneStatusButton(Mci_Vb4509_StatoTastoPiegatore, Button_stato_folder);
                            GestiscoVisualizzazioneStatusButton(Mci_Vb4515_StatoTastoStacker, Button_Stato_Stacker);
                            GestiscoVisualizzazioneStatusButton(Mci_Vb4001_StatusTestPiegatore, Button_stato_test_folder);
                            GestiscoVisualizzazioneStatusButton(Mci_Vb4006_StatusTestTraslatore,Button_Trasl_test_status);

                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb4157_C1_Cambio_Pinze_All, Button_Cambio_pinza_C1, "ic_cambio_all1_press", "ic_cambio_all1");
                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb4139_C1_Cambio_Laterali, Button_cambio_laterali, "ic_cambio_laterale_press", "ic_cambio_laterale");
                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb4138_C1_Cambio_Frontali, Button_cambio_frontale, "ic_cambio_frontale_press", "ic_cambio_frontale");
                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb4137_C1_Cambio_Corpo, Button_cambio_corpo, "ic_cambio_corpo_press", "ic_cambio_corpo");
                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb4140_Cambio_spigoli, Button_cambio_spigoli, "ic_cambio_spigoli_press", "ic_cambio_spigoli");
                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb1018_SbloccaAgo, Button_Sgancio_ago, "ic_sblocca_ago_press", "ic_sblocca_ago");
                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb4072_AllarmeContSpola, Button_reset_spola, "ic_spola_reset_red", "ic_spola_reset_yellow");
                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb1034_Test_Cuci, Button_test_cuci, "ic_test_cucitura_press", "ic_test_cucitura");
                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_vb4519_Scarico_pinza, Button_Scarico_Pinza, "ic_scaricatore_pinza_p", "ic_scaricatore_pinza");
                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_vb4519_Scarico_pinza, Button_Scarico_Pinza, "ic_scaricatore_pinza_p", "ic_scaricatore_pinza");
                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_vb4519_Scarico_pinza, Button_Scarico_Pinza, "ic_scaricatore_pinza_p", "ic_scaricatore_pinza");
                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb152_Pattina_OnOff, Button_pattina_on_off, "ic_pattina_onoff_press", "ic_pattina_onoff");
                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb157_Pattina_PassoPasso, Button_pattina_test, "ic_pattina_test_press", "ic_pattina_test");


                            GestioneVisualizzazioneToggleButton_da_Ingresso(MultiCmd_i14_Rinforzo, Button_rinforzo, "rinforzo_auto_press", "rinforzo_auto");

                            Gestione_icona_etichetta(Mci_write_Vn_etichetta);


                            //gestione valore tensione
                            if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 0.0d || (Double) MultiCmd_Vn196_num_prog_right_C1.getValue() == 1.0d)
                                Visualizza_TensioneFilo(TextView_valore_tensione, Mci_write_C1_Udf_ValTensione,Values.File_XML_path_R,ricetta_T1);
                            //gestione valore tensione
                            if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 1.0d && (Double) MultiCmd_Vn197_num_prog_left_C1.getValue() == 1.0d)
                                Visualizza_TensioneFilo(TextView_valore_tensione, Mci_write_C1_Udf_ValTensione,Values.File_XML_path_L,ricetta_T1);





                            if(Values.Machine_model.equals("JT882M")){
                                Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb4189_C2_Cambio_Pinze_All, Button_Cambio_pinza_C2, "ic_cambio_all2_press", "ic_cambio_all2");
                                Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb2018_SbloccaAgo_T2, Button_sgancio_ago_T2, "ic_sblocca_ago_t2_press", "ic_sblocca_ago_t2");
                                  Visualizza_contatore(TextView_val_speedT2, Mci_write_Vq2110_Speed, false, true, false);     //velocità
                          //      Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb4901_start_cucitura_T2, Button_start_cucitura_T2, "ic_start_cucitura_press", "ic_start_cucitura_c2");

                                Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb4904_AppPedaleHmiC2, Button_pedale_singolo, "pedale_singolo_press", "pedale_singolo");

                                int max_speed_C2 = (int) ((double) MultiCmd_Vq2913_C2_UdfVelLavRPM.getValue() / 1000);
                                TextView_max_speed_value_C2.setText(""+max_speed_C2); //scrivo la massima velelocità associata al programma
                                Visualizza_contatore(TextView_punti_tot_C2, Mci_write_Vq2951_punti_totali_C2, true, false, false);     //punti totale programma
                                Visualizza_contatore(TextView_punti_parziale_C2, Mci_write_Vq2952_punti_parziali_C2, true, false, false);     //punti parziali programma
                                Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb4074_AllarmeContSpola_C2, Button_reset_spola_C2, "ic_spola_reset_red", "ic_spola_reset_yellow");
                                Visualizza_contatore(TextView_Cnt_spola_C2, Mci_write_Vq3598_ContPuntiSpola_C2, true, false, false);     //conta spola
                                Visualizza_contatore(TextSpola_limite_C2, Mci_write_Vq3599_ImpPuntiSpola_C2, true, false, false);     //conta spola limite
                                Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb2034_Test_Cuci_C2, Button_test_cuci_C2, "ic_test_cucitura_c2_press", "ic_test_cucitura_c2");

                                if((Double)MultiCmd_Vb4513_StatoAbilitaTesta2.getValue() == 0.0d) {
                                    Button_testa_1_2.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.ic_una_testa));
                                }else
                                    Button_testa_1_2.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.ic_due_teste));



                               if ((Double) MultiCmd_Vb2034_Test_Cuci_C2.getValue() == 0.0d)
                                   Button_test_cuci_C2.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.ic_test_cucitura_c2));

                                //gestione valore tensione
                                if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 0.0d || (Double) MultiCmd_Vn198_num_prog_right_C2.getValue() == 1.0d) {
                                    Visualizza_TensioneFilo(TextView_valore_tensione_C2, Mci_write_C2_Udf_ValTensione, Values.File_XML_path_T2_R, ricetta_T2);
                                }
                                //gestione valore tensione
                                if ((Double) MultiCmd_Vb21_DxSx_pocket.getValue() == 1.0d && (Double) MultiCmd_Vn199_num_prog_left_C2.getValue() == 1.0d){


                                    Visualizza_TensioneFilo(TextView_valore_tensione_C2, Mci_write_C2_Udf_ValTensione,Values.File_XML_path_T2_L,ricetta_T2);

                                }


                            }


                            // Print the DateTime
                            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss    dd-MM-yyyy ");
                            TextView_Date.setText(dateFormat.format(new Date()));

                            Riga_CN_Esecuzione();
                            Gestione_tascaDxSX();
                            ControlloErrori();
                            GestiscoWarning();
                            GestioneButtonTools();
                            GestioneButtonPattina();
                            GestioneButtonRinforzo();
                            try {
                                Tcp4_0Counter();
                            } catch (IOException e) {}

                            // Write the Machine log in the file
                         MachineLog.write("0", (Double) MultiCmd_Vb30_C1_InCucitura.getValue(), (Double) Multicmd_Vq3591_CNT_CicliAutomaticoUser.getValue(), (Double) MultiCmd_Vn2_allarmi_da_CN.getValue(), (Double) MultiCmd_Vn4_Warning.getValue(), (Double) MultiCmd_Vq1110_Speed.getValue() / 1000, TextView_nomeprog_R_val.getText().toString(), TextView_nomeprog_L_val.getText().toString());

                            int thread_cnt = Utility.ContaThread();
                            cnt_comunicazione++;
                            if (cnt_comunicazione > 1000) cnt_comunicazione = 0;

                            TextView_cnt_thread.setText(getString(R.string.cnt_thread) + thread_cnt + "  Cnt: " + cnt_comunicazione);

                            if (Coord_Pinza.XCoord_precedente != Coord_Pinza.XCoordPosPinza || Coord_Pinza.YCoord_precedente != Coord_Pinza.YCoordPosPinza) {
                                if (myView_T1 != null)
                                    myView_T1.AggiornaCanvas(true);
                                Coord_Pinza.XCoord_precedente = Coord_Pinza.XCoordPosPinza;
                                Coord_Pinza.YCoord_precedente = Coord_Pinza.YCoordPosPinza;
                            }
                            if(Values.Machine_model.equals("JT882M")){
                                if (Coord_Pinza_C2.XCoord_precedente != Coord_Pinza_C2.XCoordPosPinza || Coord_Pinza_C2.YCoord_precedente != Coord_Pinza_C2.YCoordPosPinza) {
                                    if (myView_T2 != null)
                                        myView_T2.AggiornaCanvas(true);
                                    Coord_Pinza_C2.XCoord_precedente = Coord_Pinza_C2.XCoordPosPinza;
                                    Coord_Pinza_C2.YCoord_precedente = Coord_Pinza_C2.YCoordPosPinza;
                                }


                            }


                        }


                    });
                } else
                    sl.Connect();
            }
        }
    }




}

