package com.jam_int.jt882_m8;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jamint.ricette.Element;
import com.jamint.ricette.MathGeoTri;
import com.jamint.ricette.Ricetta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import communication.MultiCmdItem;
import communication.ShoppingList;

public class Punto_carico_page extends Activity {

    /**
     * TODO i don't know well this page
     */

    /**
     * On activity result indexes
     */
    final private static int PAGE_UDF_T1_DX = 200;
    final private static int PAGE_UDF_T1_SX = 201;
    final private static int PAGE_UDF_T2_DX = 209;
    final private static int PAGE_UDF_T2_SX = 210;
    boolean SaveProgrammaInUscita = false;
    File file_exti;
    /**
     * Receiver for check if the password is right
     */
    private final BroadcastReceiver mMessagePasswordReceiver = new BroadcastReceiver() {

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

            if (val.equals(linea)|| val.equals("67874")) {

                Mci_write_Vq3535_PosAsseX_TaraturaPinzaAgo.valore = 150d;        //0.15
                Mci_write_Vq3535_PosAsseX_TaraturaPinzaAgo.write_flag = true;
                Mci_write_Vq3536_PosAsseY_TaraturaPinzaAgo.valore = 150d;        //0.15
                Mci_write_Vq3536_PosAsseY_TaraturaPinzaAgo.write_flag = true;

                Button_reset.setVisibility(View.VISIBLE);


               //quando si campiona la macchina servono delle frecce per muovere la pinza cucitura C1 quando si centra la pinza del traslatore
                if(Chiamante == PAGE_UDF_T2_DX || Chiamante == PAGE_UDF_T2_SX ) {
                    Button_arrow_up_T1_x_Traslo.setVisibility(View.VISIBLE);
                    Button_freccia_giu_T1_x_Traslo.setVisibility(View.VISIBLE);
                }



            } else if (val.equals("")) {
            } else {
                Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
            }
        }
    };
    /**
     * ShoppingList for communicate with PLC
     */
    ShoppingList sl;
    /**
     * Thread
     */
    Thread thread_puntoCarico;
    boolean Thread_Running = false, StopThread = false, first_cycle = true, nascondiIcone = false, mostraIcone = false;
    /**
     * PLC vars
     */
    MultiCmdItem[] mci_array_read_all;
    MultiCmdItem MultiCmd_posizione_X, MultiCmd_posizione_Y, MultiCmd_JogXMeno, MultiCmd_JogXPiu, MultiCmd_JogYMeno, MultiCmd_JogYPiu, MultiCmd_Vn3804_pagina_touch, MultiCmd_tasto_verde,
            MultiCmd_CH1_in_emergenza, Multicmd_azzera_su_sensori_poi_PC, MultiCmd_Sblocca_Ago, Multicmd_Vb4082_TestCaricatoreLanciaC1, Multicmd_Vb4081_TestCaricatorePinzaC1,
            Multicmd_Vb4083_TestCaricatoreC2CaricoC1, Multicmd_Vb4084_TestCaricatoreC2ScaricoC2, Multicmd_vb7084_freccia_caric_piu,Multicmd_vb7184_freccia_caric_piu, Multicmd_vb7083_freccia_caric_meno,Multicmd_vb7183_freccia_caric_meno, Multicmd_Vb4086_SavePosizioni,
            Multicmd_Vq7081_spost_frecce_car,Multicmd_Vq7181_spost_frecce_car, MultiCmd_Vq1911_C1_QuoHomeX, MultiCmd_Vq1912_C1_QuoHomeY, Multicmd_Vq3535_PosAsseX_TaraturaPinzaAgo, Multicmd_Vq3536_PosAsseY_TaraturaPinzaAgo,
            Multicmd_go_to_PC, Multicmd_Load_Prog, Multicmd_Vb4807_PinzeAlteDopoPC, MultiCmd_PosAx_C1_AsseCAR,MultiCmd_PosAx_C2_AsseCAR,Multicmd_Vb4907_PinzeAlteDopoPC_C2,
            MultiCmd_JogYMeno_T1_x_Traslo, MultiCmd_JogYPiu_T1_x_Traslo;

    Mci_write Mci_write_JogXMeno = new Mci_write(),
            Mci_write_JogXPiu = new Mci_write(),
            Mci_write_JogYMeno = new Mci_write(),
            Mci_write_JogYPiu = new Mci_write(),
            Mci_write_azzera_su_sensori_poi_PC = new Mci_write(),
            Mci_Sblocca_Ago = new Mci_write(),
            Mci_write_Vb4081_TestCaricatorePinzaC1 = new Mci_write(),
            Mci_write_Vb4082_TestCaricatoreLanciaC1 = new Mci_write(),
            Mci_write_Vq7081_spost_frecce_car = new Mci_write(),
            Mci_write_Vq7181_spost_frecce_car = new Mci_write(),
            Mci_write_Vq3535_PosAsseX_TaraturaPinzaAgo = new Mci_write(),
            Mci_write_Vq3536_PosAsseY_TaraturaPinzaAgo = new Mci_write(),
            Mci_write_Vb4083_TestCaricatoreC2CaricoC1 = new Mci_write(),
            Mci_write_Vb4084_TestCaricatoreC2ScaricoC2 = new Mci_write(),
            Mci_write_vb7084_freccia_caric_piu = new Mci_write(),
            Mci_write_vb7184_freccia_caric_piu = new Mci_write(),
            Mci_write_vb7083_freccia_caric_meno = new Mci_write(),
            Mci_write_vb7183_freccia_caric_meno = new Mci_write(),
            Mci_write_Vb4086_SavePosizioni = new Mci_write(),
            Mci_write_go_to_PC = new Mci_write(),
            Mci_write_Load_Prog = new Mci_write(),
            Mci_write_JogYPiu_T1_x_Traslo= new Mci_write(),
            Mci_write_JogYMeno_T1_x_Traslo= new Mci_write();


    /**
     * UI components
     */
    TextView TextView_QuotaX, TextView_QuotaY, TextView_DeltaX, TextView_DeltaY, TextView_QuotaLoader;
    Button Button_arrow_up, Button_freccia_giu, Button_arrow_right, Button_arrow_left, Button_Sgancio_ago, Button_arrow_left_caric, Button_save_corse, Button_arrow_right_caric, Button_reset, Button_reset_Pc,
            Button_save_punto_carico, Button_exit, Button_password,Button_arrow_up_T1_x_Traslo,Button_freccia_giu_T1_x_Traslo;
    ToggleButton ToggleButton_caricatore_P1, ToggleButton_caricatore_P2,ToggleButton_traslatore_P1,ToggleButton_traslatore_P2;
    CheckBox CheckBox_decimi, CheckBox_1mm;
    /**
     * Program loaded
     */
    Ricetta ricetta;
    String File_Xml_path,Machine_model;
    int step_azzera_sensori = 0, step_azzera_poi_PC = 0;
    int Chiamante = 0;
    Handler UpdateHandler = new Handler();
    Info_modifica info_modifica = new Info_modifica();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punto_carico);

        try {
            Machine_model = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "MachineModel", "null", null, null, "Machine_model", getApplicationContext());
        } catch (IOException e) {
            Machine_model="";
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Chiamante = extras.getInt("Chiamante");
        }

        Button_arrow_up = findViewById(R.id.button_arrow_up);
        Button_freccia_giu = findViewById(R.id.button_freccia_giu);
        Button_arrow_right = findViewById(R.id.button_arrow_right);
        Button_arrow_left = findViewById(R.id.button_arrow_left);
        Button_arrow_left_caric = findViewById(R.id.button_arrow_left_caric);
        Button_arrow_right_caric = findViewById(R.id.button_arrow_right_caric);

        Button_arrow_up_T1_x_Traslo = findViewById(R.id.button_arrow_up_T1);
        Button_freccia_giu_T1_x_Traslo = findViewById(R.id.button_freccia_giu_T1);

        Button_arrow_up_T1_x_Traslo.setVisibility(View.GONE);
        Button_freccia_giu_T1_x_Traslo.setVisibility(View.GONE);



        Button_reset = findViewById(R.id.button_reset);
        Button_Sgancio_ago = findViewById(R.id.btn_sgancio_ago);
        Button_save_corse = findViewById(R.id.button_save_corse);
        Button_reset_Pc = findViewById(R.id.button_reset_Pc);
        Button_save_punto_carico = findViewById(R.id.button_save_punto_carico);
        Button_exit = findViewById(R.id.button_exit);
        Button_password = findViewById(R.id.btn_password);

        CheckBox_decimi = findViewById(R.id.checkBox_decimi);
        CheckBox_1mm = findViewById(R.id.checkBox_1mm);

        ToggleButton_caricatore_P1 = findViewById(R.id.toggleButton_caricatore_P1);
        ToggleButton_caricatore_P2 = findViewById(R.id.toggleButton_caricatore_P2);
        ToggleButton_traslatore_P1 = (ToggleButton) findViewById(R.id.toggleButton_traslatore_P1);
        ToggleButton_traslatore_P2 = (ToggleButton) findViewById(R.id.toggleButton_traslatore_P2);

        TextView_QuotaX = findViewById(R.id.textView_QuotaX);
        TextView_QuotaY = findViewById(R.id.textView_QuotaY);
        TextView_DeltaX = findViewById(R.id.textView_DeltaX);
        TextView_DeltaY = findViewById(R.id.textView_DeltaY);
        TextView_QuotaLoader = findViewById(R.id.textView_QuotaLoader);

        Button_reset.setVisibility(View.GONE);
        Button_arrow_left_caric.setVisibility(View.GONE);
        Button_save_corse.setVisibility(View.GONE);
        Button_arrow_right_caric.setVisibility(View.GONE);

        CheckBox_decimi.setChecked(false);
        CheckBox_1mm.setChecked(true);

        if(!Machine_model.equals("JT882M")) {
            ToggleButton_traslatore_P1.setVisibility(View.GONE);
            ToggleButton_traslatore_P2.setVisibility(View.GONE);
        }

        // Setup ShoppingList
        sl = SocketHandler.getSocket();

        // Check which page called this activity
        switch (Chiamante) {
            case PAGE_UDF_T1_DX:
                try {
                    File_Xml_path = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram_R", getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MultiCmd_posizione_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 51, MultiCmdItem.dpNONE);
                MultiCmd_posizione_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 52, MultiCmdItem.dpNONE);
                MultiCmd_JogXMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 62, MultiCmdItem.dpNONE);
                MultiCmd_JogXPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 63, MultiCmdItem.dpNONE);
                MultiCmd_JogYMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 65, MultiCmdItem.dpNONE);
                MultiCmd_JogYPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 64, MultiCmdItem.dpNONE);
                Multicmd_azzera_su_sensori_poi_PC = sl.Add("Io", 1, MultiCmdItem.dtVB, 4080, MultiCmdItem.dpNONE);
                MultiCmd_Sblocca_Ago = sl.Add("Io", 1, MultiCmdItem.dtVB, 1018, MultiCmdItem.dpNONE);
                Multicmd_go_to_PC = sl.Add("Io", 1, MultiCmdItem.dtVB, 52, MultiCmdItem.dpNONE);
                Multicmd_Load_Prog = sl.Add("Io", 1, MultiCmdItem.dtVB, 1019, MultiCmdItem.dpNONE);

                ToggleButton_traslatore_P1.setVisibility(View.GONE);
                ToggleButton_traslatore_P2.setVisibility(View.GONE);
                break;
            case PAGE_UDF_T1_SX:
                try {
                    File_Xml_path = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram_L", getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MultiCmd_posizione_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 51, MultiCmdItem.dpNONE);
                MultiCmd_posizione_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 52, MultiCmdItem.dpNONE);
                MultiCmd_JogXMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 62, MultiCmdItem.dpNONE);
                MultiCmd_JogXPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 63, MultiCmdItem.dpNONE);
                MultiCmd_JogYMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 65, MultiCmdItem.dpNONE);
                MultiCmd_JogYPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 64, MultiCmdItem.dpNONE);
                Multicmd_azzera_su_sensori_poi_PC = sl.Add("Io", 1, MultiCmdItem.dtVB, 4080, MultiCmdItem.dpNONE);
                MultiCmd_Sblocca_Ago = sl.Add("Io", 1, MultiCmdItem.dtVB, 1018, MultiCmdItem.dpNONE);
                Multicmd_go_to_PC = sl.Add("Io", 1, MultiCmdItem.dtVB, 52, MultiCmdItem.dpNONE);
                Multicmd_Load_Prog = sl.Add("Io", 1, MultiCmdItem.dtVB, 1019, MultiCmdItem.dpNONE);

                ToggleButton_traslatore_P1.setVisibility(View.GONE);
                ToggleButton_traslatore_P2.setVisibility(View.GONE);
                break;
            case PAGE_UDF_T2_DX:
                try {
                    File_Xml_path = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram_R_T2", getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MultiCmd_posizione_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 56, MultiCmdItem.dpNONE);
                MultiCmd_posizione_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 57, MultiCmdItem.dpNONE);
                MultiCmd_JogXMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 66, MultiCmdItem.dpNONE);
                MultiCmd_JogXPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 67, MultiCmdItem.dpNONE);
                MultiCmd_JogYMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 69, MultiCmdItem.dpNONE);
                MultiCmd_JogYPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 68, MultiCmdItem.dpNONE);
                Multicmd_azzera_su_sensori_poi_PC = sl.Add("Io", 1, MultiCmdItem.dtVB, 4085, MultiCmdItem.dpNONE);
                MultiCmd_Sblocca_Ago = sl.Add("Io", 1, MultiCmdItem.dtVB, 2018, MultiCmdItem.dpNONE);
                Multicmd_go_to_PC = sl.Add("Io", 1, MultiCmdItem.dtVB, 53, MultiCmdItem.dpNONE);
                Multicmd_Load_Prog = sl.Add("Io", 1, MultiCmdItem.dtVB, 2019, MultiCmdItem.dpNONE);

                ToggleButton_caricatore_P1.setVisibility(View.GONE);
                ToggleButton_caricatore_P2.setVisibility(View.GONE);
                break;
            case PAGE_UDF_T2_SX:
                try {
                    File_Xml_path = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram_L_T2", getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MultiCmd_posizione_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 56, MultiCmdItem.dpNONE);
                MultiCmd_posizione_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 57, MultiCmdItem.dpNONE);
                MultiCmd_JogXMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 66, MultiCmdItem.dpNONE);
                MultiCmd_JogXPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 67, MultiCmdItem.dpNONE);
                MultiCmd_JogYMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 69, MultiCmdItem.dpNONE);
                MultiCmd_JogYPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 68, MultiCmdItem.dpNONE);
                Multicmd_azzera_su_sensori_poi_PC = sl.Add("Io", 1, MultiCmdItem.dtVB, 4085, MultiCmdItem.dpNONE);
                MultiCmd_Sblocca_Ago = sl.Add("Io", 1, MultiCmdItem.dtVB, 2018, MultiCmdItem.dpNONE);
                Multicmd_go_to_PC = sl.Add("Io", 1, MultiCmdItem.dtVB, 53, MultiCmdItem.dpNONE);
                Multicmd_Load_Prog = sl.Add("Io", 1, MultiCmdItem.dtVB, 2019, MultiCmdItem.dpNONE);
                break;
            default:
                break;
        }

        MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);
        MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 21, MultiCmdItem.dpNONE);
        MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
        Multicmd_Vb4082_TestCaricatoreLanciaC1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4082, MultiCmdItem.dpNONE);
        Multicmd_Vb4081_TestCaricatorePinzaC1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4081, MultiCmdItem.dpNONE);
        Multicmd_Vb4083_TestCaricatoreC2CaricoC1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4083, MultiCmdItem.dpNONE);
        Multicmd_Vb4084_TestCaricatoreC2ScaricoC2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4084, MultiCmdItem.dpNONE);
        Multicmd_vb7084_freccia_caric_piu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7084, MultiCmdItem.dpNONE);
        Multicmd_vb7083_freccia_caric_meno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7083, MultiCmdItem.dpNONE);
        Multicmd_vb7184_freccia_caric_piu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7184, MultiCmdItem.dpNONE);
        Multicmd_vb7183_freccia_caric_meno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7183, MultiCmdItem.dpNONE);
        Multicmd_Vb4086_SavePosizioni = sl.Add("Io", 1, MultiCmdItem.dtVB, 4086, MultiCmdItem.dpNONE);
        Multicmd_Vq7081_spost_frecce_car = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7081, MultiCmdItem.dpNONE);
        Multicmd_Vq7181_spost_frecce_car = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7181, MultiCmdItem.dpNONE);
        MultiCmd_Vq1911_C1_QuoHomeX = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1911, MultiCmdItem.dpNONE);
        MultiCmd_Vq1912_C1_QuoHomeY = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1912, MultiCmdItem.dpNONE);
        Multicmd_Vq3535_PosAsseX_TaraturaPinzaAgo = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3535, MultiCmdItem.dpNONE);
        Multicmd_Vq3536_PosAsseY_TaraturaPinzaAgo = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3536, MultiCmdItem.dpNONE);
        Multicmd_Vb4807_PinzeAlteDopoPC = sl.Add("Io", 1, MultiCmdItem.dtVB, 4807, MultiCmdItem.dpNONE);
        Multicmd_Vb4907_PinzeAlteDopoPC_C2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4907, MultiCmdItem.dpNONE);
        MultiCmd_PosAx_C1_AsseCAR = sl.Add("Io", 1, MultiCmdItem.dtVQ, 55, MultiCmdItem.dpNONE);
        MultiCmd_PosAx_C2_AsseCAR= sl.Add("Io", 1, MultiCmdItem.dtVQ, 60, MultiCmdItem.dpNONE);
        MultiCmd_JogYMeno_T1_x_Traslo = sl.Add("Io", 1, MultiCmdItem.dtVB, 65, MultiCmdItem.dpNONE);
        MultiCmd_JogYPiu_T1_x_Traslo = sl.Add("Io", 1, MultiCmdItem.dtVB, 64, MultiCmdItem.dpNONE);

        Mci_write_JogXMeno.mci = MultiCmd_JogXMeno;
        Mci_write_JogXMeno.write_flag = false;

        Mci_write_JogXPiu.mci = MultiCmd_JogXPiu;
        Mci_write_JogXPiu.write_flag = false;

        Mci_write_JogYMeno.mci = MultiCmd_JogYMeno;
        Mci_write_JogYMeno.write_flag = false;

        Mci_write_JogYMeno_T1_x_Traslo.mci = MultiCmd_JogYMeno_T1_x_Traslo;
        Mci_write_JogYMeno_T1_x_Traslo.write_flag = false;

        Mci_write_JogYPiu.mci = MultiCmd_JogYPiu;
        Mci_write_JogYPiu.write_flag = false;

        Mci_write_JogYPiu_T1_x_Traslo.mci = MultiCmd_JogYPiu_T1_x_Traslo;
        Mci_write_JogYPiu_T1_x_Traslo.write_flag = false;

        Mci_write_azzera_su_sensori_poi_PC.mci = Multicmd_azzera_su_sensori_poi_PC;
        Mci_write_azzera_su_sensori_poi_PC.write_flag = false;

        Mci_Sblocca_Ago.mci = MultiCmd_Sblocca_Ago;
        Mci_Sblocca_Ago.write_flag = false;

        Mci_write_Vb4081_TestCaricatorePinzaC1.mci = Multicmd_Vb4081_TestCaricatorePinzaC1;
        Mci_write_Vb4081_TestCaricatorePinzaC1.write_flag = false;

        Mci_write_Vb4082_TestCaricatoreLanciaC1.mci = Multicmd_Vb4082_TestCaricatoreLanciaC1;
        Mci_write_Vb4082_TestCaricatoreLanciaC1.write_flag = false;

        Mci_write_Vb4083_TestCaricatoreC2CaricoC1.mci = Multicmd_Vb4083_TestCaricatoreC2CaricoC1;
        Mci_write_Vb4083_TestCaricatoreC2CaricoC1.write_flag = false;

        Mci_write_Vb4084_TestCaricatoreC2ScaricoC2.mci = Multicmd_Vb4084_TestCaricatoreC2ScaricoC2;
        Mci_write_Vb4084_TestCaricatoreC2ScaricoC2.write_flag = false;

        Mci_write_vb7084_freccia_caric_piu.mci = Multicmd_vb7084_freccia_caric_piu;
        Mci_write_vb7084_freccia_caric_piu.write_flag = false;

        Mci_write_vb7083_freccia_caric_meno.mci = Multicmd_vb7083_freccia_caric_meno;
        Mci_write_vb7083_freccia_caric_meno.write_flag = false;

        Mci_write_vb7184_freccia_caric_piu.mci = Multicmd_vb7184_freccia_caric_piu;
        Mci_write_vb7184_freccia_caric_piu.write_flag = false;

        Mci_write_vb7183_freccia_caric_meno.mci = Multicmd_vb7183_freccia_caric_meno;
        Mci_write_vb7183_freccia_caric_meno.write_flag = false;


        Mci_write_Vb4086_SavePosizioni.mci = Multicmd_Vb4086_SavePosizioni;
        Mci_write_Vb4086_SavePosizioni.write_flag = false;

        Mci_write_Vq7081_spost_frecce_car.mci = Multicmd_Vq7081_spost_frecce_car;
        Mci_write_Vq7081_spost_frecce_car.write_flag = false;
        Mci_write_Vq7181_spost_frecce_car.mci = Multicmd_Vq7181_spost_frecce_car;
        Mci_write_Vq7181_spost_frecce_car.write_flag = false;

        Mci_write_Vq3535_PosAsseX_TaraturaPinzaAgo.mci = Multicmd_Vq3535_PosAsseX_TaraturaPinzaAgo;

        Mci_write_Vq3536_PosAsseY_TaraturaPinzaAgo.mci = Multicmd_Vq3536_PosAsseY_TaraturaPinzaAgo;

        Mci_write_go_to_PC.mci = Multicmd_go_to_PC;

        Mci_write_Load_Prog.mci = Multicmd_Load_Prog;

        Mci_write_Vq7081_spost_frecce_car.valore = 1000.0d;
        Mci_write_Vq7081_spost_frecce_car.write_flag = true;
        Mci_write_Vq7181_spost_frecce_car.valore = 1000.0d;
        Mci_write_Vq7181_spost_frecce_car.write_flag = true;

        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogYPiu, Button_arrow_up, "ic_up_press", "ic_up", getApplicationContext(), 100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogYMeno, Button_freccia_giu, "ic_down_press", "ic_down", getApplicationContext(), 100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogXMeno, Button_arrow_right, "ic_right_press", "ic_right", getApplicationContext(), 100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogXPiu, Button_arrow_left, "ic_left_press", "ic_left", getApplicationContext(), 100);

        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogYPiu_T1_x_Traslo, Button_arrow_up_T1_x_Traslo, "ic_up_press", "ic_up", getApplicationContext(), 100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogYMeno_T1_x_Traslo, Button_freccia_giu_T1_x_Traslo, "ic_down_press", "ic_down", getApplicationContext(), 100);




        if(Chiamante == PAGE_UDF_T1_DX || Chiamante == PAGE_UDF_T1_SX ) {
            EdgeButton.CreaEdgeButton_Frecce(Mci_write_vb7084_freccia_caric_piu, Button_arrow_left_caric, "ic_left_press", "ic_left", getApplicationContext(), 100);
            EdgeButton.CreaEdgeButton_Frecce(Mci_write_vb7083_freccia_caric_meno, Button_arrow_right_caric, "ic_right_press", "ic_right", getApplicationContext(), 100);
        }
        if(Chiamante == PAGE_UDF_T2_DX || Chiamante == PAGE_UDF_T2_SX ) {
            EdgeButton.CreaEdgeButton_Frecce(Mci_write_vb7183_freccia_caric_meno , Button_arrow_left_caric, "ic_left_press", "ic_left", getApplicationContext(), 100);
            EdgeButton.CreaEdgeButton_Frecce(Mci_write_vb7184_freccia_caric_piu, Button_arrow_right_caric, "ic_right_press", "ic_right", getApplicationContext(), 100);
           

        }

        EdgeButton.CreaEdgeButton_Frecce(Mci_write_Vb4086_SavePosizioni, Button_save_corse, "ic_button_save_p", "ic_button_save", getApplicationContext(), 100);




        Toggle_Button.CreaToggleButton(Mci_Sblocca_Ago, Button_Sgancio_ago, "ic_sblocca_ago_press", "ic_sblocca_ago", getApplicationContext(), sl);

        // Show the program name
        int i = File_Xml_path.lastIndexOf('/');
        String name = File_Xml_path.substring(i + 1);
        TextView TextView_nomeprog = findViewById(R.id.textView_nprog);
        TextView_nomeprog.setText(name);

        try {
            boolean ret = Load_XML1(File_Xml_path);
            if (ret) {
                float pcX = ricetta.pcX;
                float pcY = ricetta.pcY;
                TextView_QuotaX.setText("" + pcX);
                TextView_QuotaY.setText("" + pcY);
                TextView_DeltaX.setText("0.0");
                TextView_DeltaY.setText("0.0");
            } else
                Toast.makeText(getApplicationContext(), "Errore XML in ingresso", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Errore XML in ingresso", Toast.LENGTH_SHORT).show();
        }

        mci_array_read_all = new MultiCmdItem[]{
                MultiCmd_posizione_X, MultiCmd_posizione_Y, MultiCmd_tasto_verde, MultiCmd_CH1_in_emergenza, MultiCmd_PosAx_C1_AsseCAR,MultiCmd_PosAx_C2_AsseCAR, MultiCmd_Sblocca_Ago,
                Multicmd_Vb4082_TestCaricatoreLanciaC1,Multicmd_Vb4081_TestCaricatorePinzaC1,Multicmd_azzera_su_sensori_poi_PC

        };

        sl.Clear("Io");

        // Setup the thread
        if (!Thread_Running) {
            Thread_punto_carico myTask_main = new Thread_punto_carico(this);
            thread_puntoCarico = new Thread(myTask_main, "Punto carico Task");
            thread_puntoCarico.start();
        }
    }

    public void on_click_reset_pc(final View view) {
        step_azzera_poi_PC = 5;
        Mci_Sblocca_Ago.valore = 0.0d;
        Mci_Sblocca_Ago.write_flag = true;
    }

    public void on_click_checkBox_1mm(final View view) {
        CheckBox_decimi.setChecked(false);
        CheckBox_1mm.setChecked(true);
        Mci_write_Vq7081_spost_frecce_car.valore = 1000.0d;
        Mci_write_Vq7081_spost_frecce_car.write_flag = true;
        Mci_write_Vq7181_spost_frecce_car.valore = 1000.0d;
        Mci_write_Vq7181_spost_frecce_car.write_flag = true;
    }

    public void on_click_checkBox_decimi(final View view) {
        CheckBox_decimi.setChecked(true);
        CheckBox_1mm.setChecked(false);
        Mci_write_Vq7081_spost_frecce_car.valore = 100d;
        Mci_write_Vq7081_spost_frecce_car.write_flag = true;
        Mci_write_Vq7181_spost_frecce_car.valore = 100d;
        Mci_write_Vq7181_spost_frecce_car.write_flag = true;
    }

    public void on_click_loader_P1(final View view) {
        boolean checked = ((ToggleButton) view).isChecked();
        if (checked) {
            Mci_write_Vb4082_TestCaricatoreLanciaC1.valore = 1.0d;
            Mci_write_Vb4082_TestCaricatoreLanciaC1.write_flag = true;
            ToggleButton_caricatore_P1.setVisibility(View.VISIBLE);
            ToggleButton_caricatore_P2.setVisibility(View.GONE);
            ToggleButton_traslatore_P1.setVisibility(View.GONE);
            ToggleButton_traslatore_P2.setVisibility(View.GONE);

            Button_arrow_left_caric.setVisibility(View.VISIBLE);
            Button_save_corse.setVisibility(View.VISIBLE);
            Button_arrow_right_caric.setVisibility(View.VISIBLE);
            Button_exit.setVisibility(View.GONE);
            Button_password.setVisibility(View.GONE);
        } else {
            Mci_write_Vb4082_TestCaricatoreLanciaC1.valore = 0.0d;
            Mci_write_Vb4082_TestCaricatoreLanciaC1.write_flag = true;
            ToggleButton_caricatore_P1.setVisibility(View.VISIBLE);
            ToggleButton_caricatore_P2.setVisibility(View.VISIBLE);
            ToggleButton_traslatore_P1.setVisibility(View.GONE);
            ToggleButton_traslatore_P2.setVisibility(View.GONE);
            Button_arrow_left_caric.setVisibility(View.GONE);
            Button_save_corse.setVisibility(View.GONE);
            Button_arrow_right_caric.setVisibility(View.GONE);
            Button_exit.setVisibility(View.VISIBLE);
            Button_password.setVisibility(View.VISIBLE);
        }
    }

    public void on_click_loader_P2(final View view) {
        boolean checked = ((ToggleButton) view).isChecked();
        if (checked) {
            Mci_write_Vb4081_TestCaricatorePinzaC1.valore = 1.0d;
            Mci_write_Vb4081_TestCaricatorePinzaC1.write_flag = true;
            ToggleButton_caricatore_P1.setVisibility(View.GONE);
            ToggleButton_caricatore_P2.setVisibility(View.VISIBLE);
            ToggleButton_traslatore_P1.setVisibility(View.GONE);
            ToggleButton_traslatore_P2.setVisibility(View.GONE);

            Button_arrow_left_caric.setVisibility(View.VISIBLE);
            Button_save_corse.setVisibility(View.VISIBLE);
            Button_arrow_right_caric.setVisibility(View.VISIBLE);
            Button_exit.setVisibility(View.GONE);
            Button_password.setVisibility(View.GONE);
        } else {
            Mci_write_Vb4081_TestCaricatorePinzaC1.valore = 0.0d;
            Mci_write_Vb4081_TestCaricatorePinzaC1.write_flag = true;
            ToggleButton_caricatore_P1.setVisibility(View.VISIBLE);
            ToggleButton_caricatore_P2.setVisibility(View.VISIBLE);
            ToggleButton_traslatore_P1.setVisibility(View.GONE);
            ToggleButton_traslatore_P2.setVisibility(View.GONE);
            Button_arrow_left_caric.setVisibility(View.GONE);
            Button_save_corse.setVisibility(View.GONE);
            Button_arrow_right_caric.setVisibility(View.GONE);
            Button_exit.setVisibility(View.VISIBLE);
            Button_password.setVisibility(View.VISIBLE);
        }
    }

    public void on_click_traslatore_P1(final View view) {
        boolean checked =   ((ToggleButton)view).isChecked();
        if(checked){

            Mci_write_Vb4084_TestCaricatoreC2ScaricoC2.valore = 1.0d;
            Mci_write_Vb4084_TestCaricatoreC2ScaricoC2.write_flag = true;
            ToggleButton_caricatore_P1.setVisibility(View.GONE);
            ToggleButton_caricatore_P2.setVisibility(View.GONE);
            ToggleButton_traslatore_P1.setVisibility(View.VISIBLE);
            ToggleButton_traslatore_P2.setVisibility(View.GONE);
            Button_arrow_left_caric.setVisibility(View.VISIBLE);
            Button_save_corse.setVisibility(View.VISIBLE);
            Button_arrow_right_caric.setVisibility(View.VISIBLE);
            Button_exit.setVisibility(View.GONE);
            Button_password.setVisibility(View.GONE);
        }
        else{
            Mci_write_Vb4084_TestCaricatoreC2ScaricoC2.valore = 0.0d;
            Mci_write_Vb4084_TestCaricatoreC2ScaricoC2.write_flag = true;
            ToggleButton_caricatore_P1.setVisibility(View.GONE);
            ToggleButton_caricatore_P2.setVisibility(View.GONE);
            ToggleButton_traslatore_P1.setVisibility(View.VISIBLE);
            ToggleButton_traslatore_P2.setVisibility(View.VISIBLE);
            Button_arrow_left_caric.setVisibility(View.GONE);
            Button_save_corse.setVisibility(View.GONE);
            Button_arrow_right_caric.setVisibility(View.GONE);
            Button_exit.setVisibility(View.VISIBLE);
            Button_password.setVisibility(View.VISIBLE);

        }
    }

    public void on_click_traslatore_P2(final View view) {
        boolean checked =   ((ToggleButton)view).isChecked();
        if(checked){

            Mci_write_Vb4083_TestCaricatoreC2CaricoC1.valore = 1.0d;
            Mci_write_Vb4083_TestCaricatoreC2CaricoC1.write_flag = true;
            ToggleButton_caricatore_P1.setVisibility(View.GONE);
            ToggleButton_caricatore_P2.setVisibility(View.GONE);
            ToggleButton_traslatore_P1.setVisibility(View.GONE);
            ToggleButton_traslatore_P2.setVisibility(View.VISIBLE);
            Button_arrow_left_caric.setVisibility(View.VISIBLE);
            Button_save_corse.setVisibility(View.VISIBLE);
            Button_arrow_right_caric.setVisibility(View.VISIBLE);
            Button_exit.setVisibility(View.GONE);
            Button_password.setVisibility(View.GONE);
        }
        else{
            Mci_write_Vb4083_TestCaricatoreC2CaricoC1.valore = 0.0d;
            Mci_write_Vb4083_TestCaricatoreC2CaricoC1.write_flag = true;
            ToggleButton_caricatore_P1.setVisibility(View.GONE);
            ToggleButton_caricatore_P2.setVisibility(View.GONE);
            ToggleButton_traslatore_P1.setVisibility(View.VISIBLE);
            ToggleButton_traslatore_P2.setVisibility(View.VISIBLE);
            Button_arrow_left_caric.setVisibility(View.GONE);
            Button_save_corse.setVisibility(View.GONE);
            Button_arrow_right_caric.setVisibility(View.GONE);
            Button_exit.setVisibility(View.VISIBLE);
            Button_password.setVisibility(View.VISIBLE);
        }
    }

    public void on_click_salva_punto_carico(final View view) throws IOException {
        double XAttuale = (Double) MultiCmd_posizione_X.getValue() / 1000d;
        double YAttuale = (Double) MultiCmd_posizione_Y.getValue() / 1000d;
        ricetta.pcX = (float) XAttuale;
        ricetta.pcY = (float) YAttuale;

        Element element = ricetta.elements.get(0);  //cambio coordinate inizio anche del primo elemento
        element.pStart.x = ricetta.pcX;
        element.pStart.y = ricetta.pcY;

        File file;
        file = new File(File_Xml_path);

        ricetta.save(file);

        String path_udf = File_Xml_path.replace(".xml", ".udf");

        File file_udf = new File(path_udf);
        try {
            ricetta.exportToUsr(file_udf);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "error Usr export ", Toast.LENGTH_SHORT).show();
        }
        if((Double)Multicmd_Vb4081_TestCaricatorePinzaC1.getValue()==1.0d || (Double)Multicmd_Vb4082_TestCaricatoreLanciaC1.getValue()==1.0d
        || (Double) Multicmd_Vb4083_TestCaricatoreC2CaricoC1.getValue()==1.0d || (Double) Multicmd_Vb4084_TestCaricatoreC2ScaricoC2 .getValue()==1.0d ){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm");
            builder.setMessage(R.string.SpostoCarZero);

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    Mci_write_Vb4081_TestCaricatorePinzaC1.valore = 0.0d;
                    Mci_write_Vb4081_TestCaricatorePinzaC1.write_flag = true;

                    Mci_write_Vb4082_TestCaricatoreLanciaC1.valore = 0.0d;
                    Mci_write_Vb4082_TestCaricatoreLanciaC1.write_flag = true;

                    Mci_write_Vb4083_TestCaricatoreC2CaricoC1.valore = 0.0d;
                    Mci_write_Vb4083_TestCaricatoreC2CaricoC1.write_flag = true;

                    Mci_write_Vb4084_TestCaricatoreC2ScaricoC2 .valore = 0.0d;
                    Mci_write_Vb4084_TestCaricatoreC2ScaricoC2.write_flag = true;

                    SaveProgrammaInUscita = true;
                    file_exti = file;
                    Button_exit.setVisibility(View.VISIBLE);
                   // SendFileToCn(file);
                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                 //   SendFileToCn(file);
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
        else
            SendFileToCn(file);

    }

    private void SendFileToCn(File file) {

        KillThread();

        Intent intent = new Intent(getApplicationContext(), Select_file_to_CN.class);
        switch (Chiamante) {
            case PAGE_UDF_T1_DX:
                intent.putExtra("operazione", "Saving....");
                intent.putExtra("Chiamante", "T1_R");
                intent.putExtra("File_path", file.getPath());
                startActivityForResult(intent, 999);
                break;
            case PAGE_UDF_T1_SX:
                intent.putExtra("operazione", "Saving....");
                intent.putExtra("Chiamante", "T1_L");
                intent.putExtra("File_path", file.getPath());
                startActivityForResult(intent, 999);
                break;
            case PAGE_UDF_T2_DX:
                intent.putExtra("operazione", "Saving....");
                intent.putExtra("Chiamante", "T2_R");
                intent.putExtra("File_path", file.getPath());
                startActivityForResult(intent, 999);
                break;
            case PAGE_UDF_T2_SX:
                intent.putExtra("operazione", "Saving....");
                intent.putExtra("Chiamante", "T2_L");
                intent.putExtra("File_path", file.getPath());
                startActivityForResult(intent, 999);
                break;
            default:
                break;
        }
    }

    /**
     * Button for set password
     * <p>
     * TODO i don't know if it's right because this always start a new receiver
     *
     * @param view
     */
    public void on_click_password(final View view) {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessagePasswordReceiver, new IntentFilter("KeyDialog_punto_carico_ret"));
        KeyDialog.Lancia_KeyDialogo(null, Punto_carico_page.this, null, 99999d, 0d, false, false, 0d, true, "KeyDialog_punto_carico_ret", false,"");
    }

    public void on_click_reset(final View view) {
        Mci_write_azzera_su_sensori_poi_PC.valore = 0.0d;
        Mci_write_azzera_su_sensori_poi_PC.write_flag = true;
        step_azzera_sensori = 5;
    }

    /**
     * Button for exit from this activity
     *
     * @param view
     */
    public void onClick_exit(final View view) {
         if(SaveProgrammaInUscita) SendFileToCn(file_exti);
         else {
             KillThread();
             finish();
         }
    }

    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
    }

    @Override                   //your activity is no longer visible to the user
    public void onStop() {
        super.onStop();
    }

    @Override                   //your activity is no longer visible to the user
    public void onDestroy() {
        super.onDestroy();
        KillThread();
    }

    /**
     * Function for load an Xml
     * <p>
     * TODO There are a lot of this function inside the code, i think i can merge them and put it in Utility
     *
     * @param file_xml_path
     * @return
     */
    private boolean Load_XML1(String file_xml_path) {
        try {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/ricette");
            dir.mkdirs();
            File file = new File(file_xml_path);
            int i = file.getName().lastIndexOf('.');
            String name = file.getName().substring(0, i);
            File file1 = new File(file.getParent() + "/" + name + ".xml");

            ricetta = new Ricetta(Values.plcType);
            try {
                ricetta.open(file1);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Unable to draw pocket canvas", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void Ciclo_reset_su_PC() {
        switch (step_azzera_poi_PC) {

            case 0:
                break;
            case 5:
                nascondiIcone = true;
                sl.ReadItem(Multicmd_go_to_PC);
                if ((Double) Multicmd_go_to_PC.getValue() == 0.0d) {

                    Multicmd_go_to_PC.setValue(1.0d);
                    sl.WriteItem(Multicmd_go_to_PC);
                    step_azzera_poi_PC = 10;
                }
                break;
            case 10:
                sl.ReadItem(Multicmd_go_to_PC);
                if ((Double) Multicmd_go_to_PC.getValue() == 0.0d) {
                    Double x = (Double) MultiCmd_posizione_X.getValue() / 1000.0d;
                    Double y = (Double) MultiCmd_posizione_Y.getValue() / 1000.0d;

                    info_modifica.DeltaX_inizio = x.floatValue();
                    info_modifica.DeltaY_inizio = y.floatValue();

                    info_modifica.QuoteRelativeAttive = true;
                    mostraIcone = true;
                    step_azzera_poi_PC = 0;
                }
                break;
            default:
                break;
        }
    }

    private void AggiornaGuiDaThread() {
        UpdateHandler.post(new Runnable() {
            @Override
            public void run() {
                Emergenza();

                Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_Sblocca_Ago, Button_Sgancio_ago, "ic_sblocca_ago_press", "ic_sblocca_ago");

                Double X = (Double) MultiCmd_posizione_X.getValue() / 1000.0d;
                Double Y = (Double) MultiCmd_posizione_Y.getValue() / 1000.0d;
                Double Load = 0.0d;
                if(Chiamante == PAGE_UDF_T1_DX || Chiamante == PAGE_UDF_T1_SX ) {
                    Load = (Double) MultiCmd_PosAx_C1_AsseCAR.getValue() / 1000.0d;
                }
                if(Chiamante == PAGE_UDF_T2_DX || Chiamante == PAGE_UDF_T2_SX ) {

                    Load = (Double) MultiCmd_PosAx_C2_AsseCAR.getValue() / 1000.0d;
                }
                Float X_round = MathGeoTri.round5cent(X.floatValue());
                Float Y_round = MathGeoTri.round5cent(Y.floatValue());
                Float Load_round = MathGeoTri.round5cent(Load.floatValue());
                TextView_QuotaX.setText(String.valueOf(X_round));
                TextView_QuotaY.setText(String.valueOf(Y_round));
                String load = String.valueOf(Load_round);
                int load_p = load.indexOf(".");
                String load1 = load.substring(0, load_p + 2);
                TextView_QuotaLoader.setText(load1);
                ShowQuoteRelative();
                NascondiMostraIcone();
            }
        });
    }

    private void NascondiMostraIcone() {
        if (nascondiIcone) {

            Button_arrow_up.setVisibility(View.GONE);
            Button_arrow_left.setVisibility(View.GONE);
            Button_arrow_right.setVisibility(View.GONE);
            Button_freccia_giu.setVisibility(View.GONE);
            Button_reset_Pc.setVisibility(View.GONE);
            Button_save_punto_carico.setVisibility(View.GONE);
            ToggleButton_caricatore_P1.setVisibility(View.GONE);
            ToggleButton_caricatore_P2.setVisibility(View.GONE);
            ToggleButton_traslatore_P1.setVisibility(View.GONE);
            ToggleButton_traslatore_P2.setVisibility(View.GONE);
            Button_exit.setVisibility(View.GONE);
            Button_password.setVisibility(View.GONE);

            nascondiIcone = false;
        }
        if (mostraIcone) {
            Button_arrow_up.setVisibility(View.VISIBLE);
            Button_arrow_left.setVisibility(View.VISIBLE);
            Button_arrow_right.setVisibility(View.VISIBLE);
            Button_freccia_giu.setVisibility(View.VISIBLE);
            Button_reset_Pc.setVisibility(View.VISIBLE);
            Button_save_punto_carico.setVisibility(View.VISIBLE);
            if(Chiamante == PAGE_UDF_T1_DX || Chiamante == PAGE_UDF_T1_SX ) {
                ToggleButton_caricatore_P1.setVisibility(View.VISIBLE);
                ToggleButton_caricatore_P2.setVisibility(View.VISIBLE);
            }
            if(Chiamante == PAGE_UDF_T2_DX || Chiamante == PAGE_UDF_T2_SX ) {
                ToggleButton_traslatore_P1.setVisibility(View.VISIBLE);
                ToggleButton_traslatore_P2.setVisibility(View.VISIBLE);
            }
            Button_exit.setVisibility(View.VISIBLE);
            Button_password.setVisibility(View.VISIBLE);


            mostraIcone = false;
        }
    }

    private void ShowQuoteRelative() {
        try {
            if (info_modifica.QuoteRelativeAttive) {
                TextView_DeltaX.setVisibility(View.VISIBLE);
                TextView_DeltaY.setVisibility(View.VISIBLE);
                Double X = (Double) MultiCmd_posizione_X.getValue() / 1000.0d;
                Double Y = (Double) MultiCmd_posizione_Y.getValue() / 1000.0d;

                float XAttuale = X.floatValue();
                float YAttuale = Y.floatValue();
                float XPartenza = info_modifica.DeltaX_inizio;
                float YPartenza = info_modifica.DeltaY_inizio;

                float DeltaX = MathGeoTri.round5cent(XAttuale - XPartenza);
                DeltaX = (float) Math.floor(DeltaX * 100) / 100;
                float DeltaY = MathGeoTri.round5cent(YAttuale - YPartenza);
                DeltaY = (float) Math.floor(DeltaY * 100) / 100;

                TextView_DeltaX.setText("" + DeltaX);
                TextView_DeltaY.setText("" + DeltaY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Function for call the emergency page
     */
    private void Emergenza() {
        if ((Double) MultiCmd_tasto_verde.getValue() == 0.0d || (Double) MultiCmd_CH1_in_emergenza.getValue() == 1.0d) {
            KillThread();
            Utility.ClearActivitiesTopToEmergencyPage(getApplicationContext());
        }
    }

    private void GestiscoFreccia(Mci_write Mci_write) {
        switch (Mci_write.mc_stati) {
            case 0:
                if (Mci_write.write_flag == true && Mci_write.valore == 1.0d) {

                    Mci_write.mci.setValue(1.0d);
                    sl.WriteItem(Mci_write.mci);

                    if (sl.getReturnCode() == 0) {
                        Mci_write.mc_stati = 10;
                    }
                }
                break;
            case 10:    //leggo se il PLC ha cambiato altrimenti aspetto

                if ((Double) Mci_write.mci.getValue() == 1.0d)
                    Mci_write.mc_stati = 20;
                else {
                    Mci_write.mci.setValue(1.0d);
                    sl.WriteItem(Mci_write.mci);
                }
                break;
            case 20:
                if (Mci_write.valore == 0.0d) {
                    Mci_write.mci.setValue(0.0d);
                    sl.WriteItem(Mci_write.mci);
                    if (sl.getReturnCode() == 0) {
                        Mci_write.mc_stati = 30;
                    }
                }
                break;
            case 30:    //leggo se il PLC ha cambiato altrimenti aspetto
                if ((Double) Mci_write.mci.getValue() == 0.0d)
                    Mci_write.mc_stati = 0;         //riparto
                else {
                    Mci_write.mci.setValue(0.0d);
                    sl.WriteItem(Mci_write.mci);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 999:
                finish();
                break;
            default:
                break;
        }
    }

    private void Ciclo_reset_sensori() {
        switch (step_azzera_sensori) {
            case 0:
                break;
            case 5:
                nascondiIcone = true;

                if ((Double) Multicmd_azzera_su_sensori_poi_PC.getValue() == 0.0d) {

                    Multicmd_azzera_su_sensori_poi_PC.setValue(1.0d);
                    sl.WriteItem(Multicmd_azzera_su_sensori_poi_PC);
                    step_azzera_sensori = 10;
                }else
                    step_azzera_sensori = 0;
                break;
            case 10:
                sl.ReadItem(Multicmd_azzera_su_sensori_poi_PC);
                if ((Double) Multicmd_azzera_su_sensori_poi_PC.getValue() == 0.0d) {
                    sl.ReadItem(MultiCmd_posizione_X);
                    sl.ReadItem(MultiCmd_posizione_Y);
                    Double X = (Double) MultiCmd_posizione_X.getValue() / 1000.0d;
                    Double Y = (Double) MultiCmd_posizione_Y.getValue() / 1000.0d;

                    info_modifica.DeltaX_inizio = X.floatValue();
                    info_modifica.DeltaY_inizio = Y.floatValue();
                    info_modifica.QuoteRelativeAttive = true;
                    mostraIcone = true;
                    step_azzera_sensori = 0;
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

    private void KillThread() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessagePasswordReceiver);
        StopThread = true;

        try {
            if (!Thread_Running)
                thread_puntoCarico.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class Thread_punto_carico implements Runnable {
        Activity activity;
        boolean rc_error;

        public Thread_punto_carico(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            while (true) {
                Thread_Running = true;
                try {
                    Thread.sleep((long) 100d);
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
                        Multicmd_Vb4807_PinzeAlteDopoPC.setValue(0.0d);
                        sl.WriteItem(Multicmd_Vb4807_PinzeAlteDopoPC);
                        Multicmd_Vb4907_PinzeAlteDopoPC_C2.setValue(0.0d);
                        sl.WriteItem(Multicmd_Vb4907_PinzeAlteDopoPC_C2);

                        Mci_write_Load_Prog.valore = 1.0d;
                        Mci_write_Load_Prog.write_flag = true;
                        step_azzera_poi_PC = 5;
                    }

                    MultiCmd_Vn3804_pagina_touch.setValue(1004.0d);
                    sl.WriteItem(MultiCmd_Vn3804_pagina_touch);


                    rc_error = false;
                    sl.Clear();

                    // ------------------------ RX -------------------------------
                    sl.ReadItems(mci_array_read_all);
                    if (sl.getReturnCode() != 0) {
                        rc_error = true;
                    }
                    if (!rc_error) {
                        GestiscoFreccia(Mci_write_JogYMeno);
                        GestiscoFreccia(Mci_write_JogYPiu);
                        GestiscoFreccia(Mci_write_JogXPiu);
                        GestiscoFreccia(Mci_write_JogXMeno);
                        GestiscoFreccia(Mci_write_JogYMeno_T1_x_Traslo);
                        GestiscoFreccia(Mci_write_JogYPiu_T1_x_Traslo);

                        Utility.GestiscoMci_Out_Toggle(sl, Mci_Sblocca_Ago);
                        Utility.GestiscoMci_Out_Toggle(sl, Mci_write_azzera_su_sensori_poi_PC);

                        Utility.ScrivoVbVnVq(sl, Mci_write_Vb4081_TestCaricatorePinzaC1);
                        Utility.ScrivoVbVnVq(sl, Mci_write_Vb4082_TestCaricatoreLanciaC1);
                        Utility.ScrivoVbVnVq(sl, Mci_write_Vb4083_TestCaricatoreC2CaricoC1);
                        Utility.ScrivoVbVnVq(sl, Mci_write_Vb4084_TestCaricatoreC2ScaricoC2);
                        Utility.ScrivoVbVnVq(sl, Mci_write_Vq7081_spost_frecce_car);
                        Utility.ScrivoVbVnVq(sl, Mci_write_Vq7181_spost_frecce_car);

                        Utility.ScrivoVbVnVq(sl, Mci_write_vb7084_freccia_caric_piu);
                        Utility.ScrivoVbVnVq(sl, Mci_write_vb7083_freccia_caric_meno);
                        Utility.ScrivoVbVnVq(sl, Mci_write_vb7184_freccia_caric_piu);
                        Utility.ScrivoVbVnVq(sl, Mci_write_vb7183_freccia_caric_meno);
                        Utility.ScrivoVbVnVq(sl, Mci_write_Vb4086_SavePosizioni);
                        Utility.ScrivoVbVnVq(sl, Mci_write_Vq3535_PosAsseX_TaraturaPinzaAgo);
                        Utility.ScrivoVbVnVq(sl, Mci_write_Vq3536_PosAsseY_TaraturaPinzaAgo);
                        Utility.ScrivoVbVnVq(sl, Mci_write_Load_Prog);

                        Ciclo_reset_su_PC();
                        Ciclo_reset_sensori();


                    }
                    AggiornaGuiDaThread();
                } else
                    sl.Connect();
            }
        }
    }
}
