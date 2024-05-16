package com.jam_int.jt882_m8;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jamint.ricette.Ricetta;

import java.io.File;
import java.io.IOException;

import communication.MultiCmdItem;
import communication.ShoppingList;

public class Piegatore_sequenza extends Activity {

    /**
     * TODO I don't know this page so i don't know how to reorder it
     */

    private final Handler handler = new Handler();
    /**
     * ShoppingList for communicate with PLC
     */
    ShoppingList sl;
    /**
     * Thread
     */
    Thread thread_Sequenza_piegatura;
    boolean Thread_Running = false, StopThread = false, read_daCN_tipo = true, rc_error = false, read_daCN_tempi = true, aggiorna_grafica = false,write_data_toCn=false,
            dati_cambiati = false;

    /**
     * PLC vars
     */
    MultiCmdItem Multicmd_vn109, Multicmd_vn110, MultiCmd_Vn3804_pagina_touch, Multicmd_vq3006, Multicmd_vq3008, Multicmd_vq3010, Multicmd_vq3012,
            Multicmd_vq3007, Multicmd_vq3009, Multicmd_vq3011, Multicmd_vq3013,MultiCmd_tasto_verde, MultiCmd_CH1_in_emergenza,
            Multicmd_Vq3106_RitPiegatore_1_ON, Multicmd_Vq3107_RitPiegatore_1_OFF, Multicmd_Vq3108_RitPiegatore_2_ON, Multicmd_Vq3109_RitPiegatore_2_OFF, Multicmd_Vq3110_RitPiegatore_3_ON, Multicmd_Vq3111_RitPiegatore_3_OFF, Multicmd_Vq3112_RitPiegatore_4_ON, Multicmd_Vq3113_RitPiegatore_4_OFF,
            Multicmd_Vq3114_RitPiegatore_1_ON, Multicmd_Vq3115_RitPiegatore_1_OFF, Multicmd_Vq3116_RitPiegatore_2_ON, Multicmd_Vq3117_RitPiegatore_2_OFF, Multicmd_Vq3118_RitPiegatore_3_ON, Multicmd_Vq3119_RitPiegatore_3_OFF, Multicmd_Vq3120_RitPiegatore_4_ON, Multicmd_Vq3121_RitPiegatore_4_OFF,
            Multicmd_Vq3122_RitPiegatore_1_ON, Multicmd_Vq3123_RitPiegatore_1_OFF, Multicmd_Vq3124_RitPiegatore_2_ON, Multicmd_Vq3125_RitPiegatore_2_OFF, Multicmd_Vq3126_RitPiegatore_3_ON, Multicmd_Vq3127_RitPiegatore_3_OFF, Multicmd_Vq3128_RitPiegatore_4_ON, Multicmd_Vq3129_RitPiegatore_4_OFF,
            Multicmd_Vq3130_RitPiegatore_1_ON, Multicmd_Vq3131_RitPiegatore_1_OFF, Multicmd_Vq3132_RitPiegatore_2_ON, Multicmd_Vq3133_RitPiegatore_2_OFF, Multicmd_Vq3134_RitPiegatore_3_ON, Multicmd_Vq3135_RitPiegatore_3_OFF, Multicmd_Vq3136_RitPiegatore_4_ON, Multicmd_Vq3137_RitPiegatore_4_OFF,
            Multicmd_Vq3138_RitPiegatore_1_ON, Multicmd_Vq3139_RitPiegatore_1_OFF, Multicmd_Vq3140_RitPiegatore_2_ON, Multicmd_Vq3141_RitPiegatore_2_OFF, Multicmd_Vq3142_RitPiegatore_3_ON, Multicmd_Vq3143_RitPiegatore_3_OFF, Multicmd_Vq3144_RitPiegatore_4_ON, Multicmd_Vq3145_RitPiegatore_4_OFF,
            Multicmd_Vn115_CopiaSeqPiegFree;
    Mci_write Mci_write_Vn109_sequenza_chiusura_piegatore = new Mci_write(),
            Mci_write_Vn110_sequenza_apertura_piegatore = new Mci_write(),
            Mci_write_vq3006 = new Mci_write(),
            Mci_write_vq3008 = new Mci_write(),
            Mci_write_vq3010 = new Mci_write(),
            Mci_write_vq3012 = new Mci_write(),
            Mci_write_vq3007 = new Mci_write(),
            Mci_write_vq3009 = new Mci_write(),
            Mci_write_vq3011 = new Mci_write(),
            Mci_write_vq3013 = new Mci_write(),
            Mci_write_vq3106 = new Mci_write(),
            Mci_write_vq3107 = new Mci_write(),
            Mci_write_vq3108 = new Mci_write(),
            Mci_write_vq3109 = new Mci_write(),
            Mci_write_vq3110 = new Mci_write(),
            Mci_write_vq3111 = new Mci_write(),
            Mci_write_vq3112 = new Mci_write(),
            Mci_write_vq3113 = new Mci_write(),
            Mci_write_vq3114 = new Mci_write(),
            Mci_write_vq3115 = new Mci_write(),
            Mci_write_vq3116 = new Mci_write(),
            Mci_write_vq3117 = new Mci_write(),
            Mci_write_vq3118 = new Mci_write(),
            Mci_write_vq3119 = new Mci_write(),
            Mci_write_vq3120 = new Mci_write(),
            Mci_write_vq3121 = new Mci_write(),
            Mci_write_vq3122 = new Mci_write(),
            Mci_write_vq3123 = new Mci_write(),
            Mci_write_vq3124 = new Mci_write(),
            Mci_write_vq3125 = new Mci_write(),
            Mci_write_vq3126 = new Mci_write(),
            Mci_write_vq3127 = new Mci_write(),
            Mci_write_vq3128 = new Mci_write(),
            Mci_write_vq3129 = new Mci_write(),
            Mci_write_vq3130 = new Mci_write(),
            Mci_write_vq3131 = new Mci_write(),
            Mci_write_vq3132 = new Mci_write(),
            Mci_write_vq3133 = new Mci_write(),
            Mci_write_vq3134 = new Mci_write(),
            Mci_write_vq3135 = new Mci_write(),
            Mci_write_vq3136 = new Mci_write(),
            Mci_write_vq3137 = new Mci_write(),
            Mci_write_vq3138 = new Mci_write(),
            Mci_write_vq3139 = new Mci_write(),
            Mci_write_vq3140 = new Mci_write(),
            Mci_write_vq3141 = new Mci_write(),
            Mci_write_vq3142 = new Mci_write(),
            Mci_write_vq3143 = new Mci_write(),
            Mci_write_vq3144 = new Mci_write(),
            Mci_write_vq3145 = new Mci_write(),
            Mci_write_Vn115_CopiaSeqPiegFree = new Mci_write();
    /**
     * UI components
     */
    TextView TextView_Close1, TextView_Close2, TextView_Close3, TextView_Close4,TextView_TimeAfterClose1,TextView_TimeAfterClose2,TextView_TimeAfterClose3,TextView_TimeAfterClose4,
            TextView_TimeAfterOpen4_vq3013,TextView_TimeAfterOpen3_vq3011,TextView_TimeAfterOpen2_vq3009,TextView_TimeAfterOpen1_vq3007,TextView_Open4,TextView_Open3,TextView_Open2,TextView_Open1;
    ImageView ImageView_lamell_angoli, ImageView_laterali, ImageView_frontale, ImageView_4e5, ImageView1, ImageView2, ImageView3, ImageView4, ImageView1_green, ImageView2_green, ImageView3_green, ImageView4_green,
            ImageView_lamell_angoli_green, ImageView_laterali_green, ImageView_frontale_green, ImageView_4e5_green;
    Button Button_play;
    Ricetta ricetta;
    SeekBar seekbar_simulazione;
    File file_udf;
    Double ritardo_grafica = 0.0d;
    int Id_sequenza_grafica = 0, seekbar_simulazione_value = 100,time_simulazione=100;
    Sequenza sequenza_standard, sequenza_Curve3, sequenza_Square, sequenza_Free, sequenza_Curve5;
    private RadioGroup radioGroup;
    private RadioButton Radio_Standard, Radio_Curve3, Radio_Square, Radio_Free, Radio_Curve5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_piegatore_sequenza);

        // Setup ShoppingList
        sl = SocketHandler.getSocket();
        sl.Clear("Io");

        // Read the file to load
        file_udf = new File(Values.File_XML_path_R);
        if (file_udf.exists()) {
            ricetta = new Ricetta(Values.plcType);
            try {
                ricetta.open(file_udf);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "error opening xml file on Inizializza_parametri", Toast.LENGTH_SHORT).show();
            }
        }

        ImageView_lamell_angoli = findViewById(R.id.imageView_lamell_angoli);
        ImageView_laterali = findViewById(R.id.imageView_laterali);
        ImageView_frontale = findViewById(R.id.imageView_frontale);
        ImageView_4e5 = findViewById(R.id.imageView_4e5);
        ImageView_lamell_angoli_green = findViewById(R.id.imageView_lamell_angoli_green);
        ImageView_laterali_green = findViewById(R.id.imageView_laterali_green);
        ImageView_frontale_green = findViewById(R.id.imageView_frontale_green);
        ImageView_4e5_green = findViewById(R.id.imageView_4e5_green);

        ImageView_lamell_angoli_green.setVisibility(View.GONE);
        ImageView_laterali_green.setVisibility(View.GONE);
        ImageView_frontale_green.setVisibility(View.GONE);
        ImageView_4e5_green.setVisibility(View.GONE);

        // TODO Why this??? i can use the old value (example: ImageView_laterali)
        ImageView1 = ImageView_laterali;
        ImageView2 = ImageView_frontale;
        ImageView3 = ImageView_lamell_angoli;
        ImageView4 = ImageView_4e5;
        ImageView1_green = ImageView_laterali_green;
        ImageView2_green = ImageView_frontale_green;
        ImageView3_green = ImageView_lamell_angoli_green;
        ImageView4_green = ImageView_4e5_green;

        TextView_Close1 = findViewById(R.id.textView_Close1);
        TextView_Close1.setText(" ");
        TextView_Close2 = findViewById(R.id.textView_Close2);
        TextView_Close2.setText(" ");
        TextView_Close3 = findViewById(R.id.textView_Close3);
        TextView_Close3.setText(" ");
        TextView_Close4 = findViewById(R.id.textView_Close4);
        TextView_Close4.setText(" ");

        TextView_Open4 = findViewById(R.id.textView_Open4);
        TextView_Open4.setText(" ");
        TextView_Open3 = findViewById(R.id.textView_Open3);
        TextView_Open3.setText(" ");
        TextView_Open2 = findViewById(R.id.textView_Open2);
        TextView_Open2.setText(" ");
        TextView_Open1 = findViewById(R.id.textView_Open1);
        TextView_Open1.setText(" ");


        TextView_TimeAfterClose1 = findViewById(R.id.textView_TimeAfterClose1);
        TextView_TimeAfterClose1.setText(" ");
        TextView_TimeAfterClose2 = findViewById(R.id.textView_TimeAfterClose2);
        TextView_TimeAfterClose2.setText(" ");
        TextView_TimeAfterClose3 = findViewById(R.id.textView_TimeAfterClose3);
        TextView_TimeAfterClose3.setText(" ");
        TextView_TimeAfterClose4 = findViewById(R.id.textView_TimeAfterClose4);
        TextView_TimeAfterClose4.setText(" ");

        TextView_TimeAfterOpen4_vq3013 = findViewById(R.id.textView_TimeAfterOpen4_vq3013);
        TextView_TimeAfterOpen4_vq3013.setText(" ");
        TextView_TimeAfterOpen3_vq3011 = findViewById(R.id.textView_TimeAfterOpen3_vq3011);
        TextView_TimeAfterOpen3_vq3011.setText(" ");
        TextView_TimeAfterOpen2_vq3009 = findViewById(R.id.textView_TimeAfterOpen2_vq3009);
        TextView_TimeAfterOpen2_vq3009.setText(" ");
        TextView_TimeAfterOpen1_vq3007 = findViewById(R.id.textView_TimeAfterOpen1_vq3007);
        TextView_TimeAfterOpen1_vq3007.setText(" ");
        seekbar_simulazione = findViewById(R.id.seekBar);
        BarSeekSpeed();

        Button_play = findViewById(R.id.button_play);

        radioGroup = findViewById(R.id.myRadioGroup);

        Radio_Standard = findViewById(R.id.Standard);
        Radio_Curve3 = findViewById(R.id.Curve3);
        Radio_Square = findViewById(R.id.Square);
        Radio_Free = findViewById(R.id.Free);
        Radio_Curve5 = findViewById(R.id.Curve5);

        radioGroup.check(R.id.Standard);

        // Init the sequences
        sequenza_standard = new Sequenza("Standard", false, 1, 2, 3, 0, 0, 3, 2, 1, 0.2d, 0.0d, 0.1d, 0.0d, 0.0d, 0.0d, 0.2d, 0.2d);
        sequenza_Curve3 = new Sequenza("Curve3", false, 2, 1, 3, 0, 0, 3, 1, 2, 0.2d, 0.2d, 0.1d, 0.0d, 0.0d, 0.0d, 0.0d, 0.2d);
        sequenza_Square = new Sequenza("Square", false, 1, 2, 3, 4, 4, 3, 2, 1, 0.2d, 0.1d, 0.0d, 0.1d, 0.0d, 0.2d, 0.2d, 0.2d);
        sequenza_Free = new Sequenza("Free", false, 1, 2, 3, 4, 4, 3, 2, 1, 0.2d, 0.2d, 0.2d, 0.2d, 0.2d, 0.2d, 0.2d, 0.2d);
        sequenza_Curve5 = new Sequenza("Curve5", false, 4, 2, 1, 3, 3, 1, 2, 4, 0.2d, 0.1d, 0.1d, 0.1d, 0.0d, 0.0d, 0.0d, 0.2d);

        // Init the radio group event
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected

                if (checkedId == R.id.Standard) {
                    AttivaSequenza(sequenza_standard);

                } else if (checkedId == R.id.Curve3) {
                    AttivaSequenza(sequenza_Curve3);

                } else if (checkedId == R.id.Square) {
                    AttivaSequenza(sequenza_Square);

                } else if (checkedId == R.id.Curve5) {
                    AttivaSequenza(sequenza_Curve5);

                } else if (checkedId == R.id.Free) {
                    AttivaSequenza(sequenza_Free);

                }
                read_daCN_tempi = true;
                aggiorna_grafica = true;
                dati_cambiati = true;
            }
        });
        MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 21, MultiCmdItem.dpNONE);
        MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
        Multicmd_vn109 = sl.Add("Io", 1, MultiCmdItem.dtVN, 109, MultiCmdItem.dpNONE);
        Multicmd_vn110 = sl.Add("Io", 1, MultiCmdItem.dtVN, 110, MultiCmdItem.dpNONE);
        MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);
        Multicmd_vq3006 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3006, MultiCmdItem.dpNONE);
        Multicmd_vq3008 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3008, MultiCmdItem.dpNONE);
        Multicmd_vq3010 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3010, MultiCmdItem.dpNONE);
        Multicmd_vq3012 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3012, MultiCmdItem.dpNONE);
        Multicmd_vq3007 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3007, MultiCmdItem.dpNONE);
        Multicmd_vq3009 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3009, MultiCmdItem.dpNONE);
        Multicmd_vq3011 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3011, MultiCmdItem.dpNONE);
        Multicmd_vq3013 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3013, MultiCmdItem.dpNONE);

        Multicmd_Vq3106_RitPiegatore_1_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3106, MultiCmdItem.dpNONE);
        Multicmd_Vq3107_RitPiegatore_1_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3107, MultiCmdItem.dpNONE);
        Multicmd_Vq3108_RitPiegatore_2_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3108, MultiCmdItem.dpNONE);
        Multicmd_Vq3109_RitPiegatore_2_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3109, MultiCmdItem.dpNONE);
        Multicmd_Vq3110_RitPiegatore_3_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3110, MultiCmdItem.dpNONE);
        Multicmd_Vq3111_RitPiegatore_3_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3111, MultiCmdItem.dpNONE);
        Multicmd_Vq3112_RitPiegatore_4_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3112, MultiCmdItem.dpNONE);
        Multicmd_Vq3113_RitPiegatore_4_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3113, MultiCmdItem.dpNONE);
        Multicmd_Vq3114_RitPiegatore_1_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3114, MultiCmdItem.dpNONE);
        Multicmd_Vq3115_RitPiegatore_1_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3115, MultiCmdItem.dpNONE);
        Multicmd_Vq3116_RitPiegatore_2_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3116, MultiCmdItem.dpNONE);
        Multicmd_Vq3117_RitPiegatore_2_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3117, MultiCmdItem.dpNONE);
        Multicmd_Vq3118_RitPiegatore_3_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3118, MultiCmdItem.dpNONE);
        Multicmd_Vq3119_RitPiegatore_3_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3119, MultiCmdItem.dpNONE);
        Multicmd_Vq3120_RitPiegatore_4_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3120, MultiCmdItem.dpNONE);
        Multicmd_Vq3121_RitPiegatore_4_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3121, MultiCmdItem.dpNONE);
        Multicmd_Vq3122_RitPiegatore_1_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3122, MultiCmdItem.dpNONE);
        Multicmd_Vq3123_RitPiegatore_1_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3123, MultiCmdItem.dpNONE);
        Multicmd_Vq3124_RitPiegatore_2_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3124, MultiCmdItem.dpNONE);
        Multicmd_Vq3125_RitPiegatore_2_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3125, MultiCmdItem.dpNONE);
        Multicmd_Vq3126_RitPiegatore_3_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3126, MultiCmdItem.dpNONE);
        Multicmd_Vq3127_RitPiegatore_3_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3127, MultiCmdItem.dpNONE);
        Multicmd_Vq3128_RitPiegatore_4_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3128, MultiCmdItem.dpNONE);
        Multicmd_Vq3129_RitPiegatore_4_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3129, MultiCmdItem.dpNONE);
        Multicmd_Vq3130_RitPiegatore_1_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3130, MultiCmdItem.dpNONE);
        Multicmd_Vq3131_RitPiegatore_1_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3131, MultiCmdItem.dpNONE);
        Multicmd_Vq3132_RitPiegatore_2_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3132, MultiCmdItem.dpNONE);
        Multicmd_Vq3133_RitPiegatore_2_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3133, MultiCmdItem.dpNONE);
        Multicmd_Vq3134_RitPiegatore_3_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3134, MultiCmdItem.dpNONE);
        Multicmd_Vq3135_RitPiegatore_3_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3135, MultiCmdItem.dpNONE);
        Multicmd_Vq3136_RitPiegatore_4_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3136, MultiCmdItem.dpNONE);
        Multicmd_Vq3137_RitPiegatore_4_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3137, MultiCmdItem.dpNONE);
        Multicmd_Vq3138_RitPiegatore_1_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3138, MultiCmdItem.dpNONE);
        Multicmd_Vq3139_RitPiegatore_1_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3139, MultiCmdItem.dpNONE);
        Multicmd_Vq3140_RitPiegatore_2_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3140, MultiCmdItem.dpNONE);
        Multicmd_Vq3141_RitPiegatore_2_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3141, MultiCmdItem.dpNONE);
        Multicmd_Vq3142_RitPiegatore_3_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3142, MultiCmdItem.dpNONE);
        Multicmd_Vq3143_RitPiegatore_3_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3143, MultiCmdItem.dpNONE);
        Multicmd_Vq3144_RitPiegatore_4_ON = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3144, MultiCmdItem.dpNONE);
        Multicmd_Vq3145_RitPiegatore_4_OFF = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3145, MultiCmdItem.dpNONE);
        Multicmd_Vn115_CopiaSeqPiegFree = sl.Add("Io", 1, MultiCmdItem.dtVN, 115, MultiCmdItem.dpNONE);


        Mci_write_Vn109_sequenza_chiusura_piegatore.mci = Multicmd_vn109;

        Mci_write_Vn110_sequenza_apertura_piegatore.mci = Multicmd_vn110;

        Mci_write_Vn115_CopiaSeqPiegFree.mci = Multicmd_Vn115_CopiaSeqPiegFree;

        Mci_write_vq3006.mci = Multicmd_vq3006;

        Mci_write_vq3008.mci = Multicmd_vq3008;

        Mci_write_vq3010.mci = Multicmd_vq3010;

        Mci_write_vq3012.mci = Multicmd_vq3012;

        Mci_write_vq3007.mci = Multicmd_vq3007;

        Mci_write_vq3009.mci = Multicmd_vq3009;

        Mci_write_vq3011.mci = Multicmd_vq3011;

        Mci_write_vq3013.mci = Multicmd_vq3013;


        Mci_write_vq3106.mci = Multicmd_Vq3106_RitPiegatore_1_ON;

        Mci_write_vq3107.mci = Multicmd_Vq3107_RitPiegatore_1_OFF;

        Mci_write_vq3108.mci = Multicmd_Vq3108_RitPiegatore_2_ON;

        Mci_write_vq3109.mci = Multicmd_Vq3109_RitPiegatore_2_OFF;

        Mci_write_vq3110.mci = Multicmd_Vq3110_RitPiegatore_3_ON;

        Mci_write_vq3111.mci = Multicmd_Vq3111_RitPiegatore_3_OFF;

        Mci_write_vq3112.mci = Multicmd_Vq3112_RitPiegatore_4_ON;

        Mci_write_vq3113.mci = Multicmd_Vq3113_RitPiegatore_4_OFF;


        Mci_write_vq3114.mci = Multicmd_Vq3114_RitPiegatore_1_ON;

        Mci_write_vq3115.mci = Multicmd_Vq3115_RitPiegatore_1_OFF;

        Mci_write_vq3116.mci = Multicmd_Vq3116_RitPiegatore_2_ON;

        Mci_write_vq3117.mci = Multicmd_Vq3117_RitPiegatore_2_OFF;

        Mci_write_vq3118.mci = Multicmd_Vq3118_RitPiegatore_3_ON;

        Mci_write_vq3119.mci = Multicmd_Vq3119_RitPiegatore_3_OFF;

        Mci_write_vq3120.mci = Multicmd_Vq3120_RitPiegatore_4_ON;

        Mci_write_vq3121.mci = Multicmd_Vq3121_RitPiegatore_4_OFF;


        Mci_write_vq3122.mci = Multicmd_Vq3122_RitPiegatore_1_ON;

        Mci_write_vq3123.mci = Multicmd_Vq3123_RitPiegatore_1_OFF;

        Mci_write_vq3124.mci = Multicmd_Vq3124_RitPiegatore_2_ON;

        Mci_write_vq3125.mci = Multicmd_Vq3125_RitPiegatore_2_OFF;

        Mci_write_vq3126.mci = Multicmd_Vq3126_RitPiegatore_3_ON;

        Mci_write_vq3127.mci = Multicmd_Vq3127_RitPiegatore_3_OFF;

        Mci_write_vq3128.mci = Multicmd_Vq3128_RitPiegatore_4_ON;

        Mci_write_vq3129.mci = Multicmd_Vq3129_RitPiegatore_4_OFF;


        Mci_write_vq3130.mci = Multicmd_Vq3130_RitPiegatore_1_ON;

        Mci_write_vq3131.mci = Multicmd_Vq3131_RitPiegatore_1_OFF;

        Mci_write_vq3132.mci = Multicmd_Vq3132_RitPiegatore_2_ON;

        Mci_write_vq3133.mci = Multicmd_Vq3133_RitPiegatore_2_OFF;

        Mci_write_vq3134.mci = Multicmd_Vq3134_RitPiegatore_3_ON;

        Mci_write_vq3135.mci = Multicmd_Vq3135_RitPiegatore_3_OFF;

        Mci_write_vq3136.mci = Multicmd_Vq3136_RitPiegatore_4_ON;

        Mci_write_vq3137.mci = Multicmd_Vq3137_RitPiegatore_4_OFF;


        Mci_write_vq3138.mci = Multicmd_Vq3138_RitPiegatore_1_ON;

        Mci_write_vq3139.mci = Multicmd_Vq3139_RitPiegatore_1_OFF;

        Mci_write_vq3140.mci = Multicmd_Vq3140_RitPiegatore_2_ON;

        Mci_write_vq3141.mci = Multicmd_Vq3141_RitPiegatore_2_OFF;

        Mci_write_vq3142.mci = Multicmd_Vq3142_RitPiegatore_3_ON;

        Mci_write_vq3143.mci = Multicmd_Vq3143_RitPiegatore_3_OFF;

        Mci_write_vq3144.mci = Multicmd_Vq3144_RitPiegatore_4_ON;

        Mci_write_vq3145.mci = Multicmd_Vq3145_RitPiegatore_4_OFF;

        // Start thread
        if (!Thread_Running) {
            Piegatore_sequenza.MyAndroidThread_Seq_Pieg myTask_Seq_Pieg = new Piegatore_sequenza.MyAndroidThread_Seq_Pieg(this);
            thread_Sequenza_piegatura = new Thread(myTask_Seq_Pieg, "Seq Piegatura myTask");
            thread_Sequenza_piegatura.start();
            Log.d("JAM TAG", "Start Seq Piegatura Thread");
        }
    }

    private void BarSeekSpeed() {


        seekbar_simulazione.setMax(1000);
        seekbar_simulazione.setProgress(1000);

        seekbar_simulazione.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbar_simulazione_value = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                time_simulazione = 100 + ( 1000 - seekbar_simulazione_value);
            }
        });
    }

    public void on_click_play(View view) {
        Button_play.setBackgroundResource(R.drawable.stop_black);
        int selectedId = radioGroup.getCheckedRadioButtonId();

        // find which radioButton is checked by id
        if (selectedId == Radio_Standard.getId()) {
            ImageView1 = ImageView_laterali;
            ImageView2 = ImageView_frontale;
            ImageView3 = ImageView_lamell_angoli;
            ImageView4 = null; //ImageView_4e5;
            ImageView1_green = ImageView_laterali_green;
            ImageView2_green = ImageView_frontale_green;
            ImageView3_green = ImageView_lamell_angoli_green;
            ImageView4_green = null;

        } else if (selectedId == Radio_Curve3.getId()) {
            ImageView1 = ImageView_frontale;;
            ImageView2 = ImageView_laterali;
            ImageView3 = ImageView_lamell_angoli;
            ImageView4 = null;
            ImageView1_green = ImageView_frontale_green;
            ImageView2_green = ImageView_laterali_green;
            ImageView3_green = ImageView_lamell_angoli_green;
            ImageView4_green = null;

        } else if (selectedId == Radio_Square.getId()) {
            ImageView1 = ImageView_laterali;
            ImageView2 = ImageView_frontale;
            ImageView3 = ImageView_lamell_angoli;
            ImageView4 = ImageView_4e5;
            ImageView1_green = ImageView_laterali_green;
            ImageView2_green = ImageView_frontale_green;
            ImageView3_green = ImageView_lamell_angoli_green;
            ImageView4_green = ImageView_4e5_green;

        } else if (selectedId == Radio_Curve5.getId()) {
            ImageView1 = ImageView_4e5;
            ImageView2 = ImageView_frontale;
            ImageView3 = ImageView_laterali;
            ImageView4 = ImageView_lamell_angoli;
            ImageView1_green = ImageView_4e5_green;
            ImageView2_green = ImageView_frontale_green;
            ImageView3_green = ImageView_laterali_green;
            ImageView4_green = ImageView_lamell_angoli_green;


        } else if (selectedId == Radio_Free.getId()) {
            if (TextView_Close1.getText().equals("A")) {
                ImageView1 = ImageView_laterali;
                ImageView1_green = ImageView_laterali_green;
            } else if (TextView_Close1.getText().equals("B")) {
                ImageView1 = ImageView_frontale;
                ImageView1_green = ImageView_frontale_green;
            } else if (TextView_Close1.getText().equals("C")) {
                ImageView1 = ImageView_lamell_angoli;
                ImageView1_green = ImageView_lamell_angoli_green;
            } else if (TextView_Close1.getText().equals("D")) {
                ImageView1 = ImageView_4e5;
                ImageView1_green = ImageView_4e5_green;
            } else if (TextView_Close1.getText().equals("----")) {
                ImageView1 = null;
                ImageView1_green = null;
            }

            if (TextView_Close2.getText().equals("A")) {
                ImageView2 = ImageView_laterali;
                ImageView2_green = ImageView_laterali_green;
            } else if (TextView_Close2.getText().equals("B")) {
                ImageView2 = ImageView_frontale;
                ImageView2_green = ImageView_frontale_green;
            } else if (TextView_Close2.getText().equals("C")) {
                ImageView2 = ImageView_lamell_angoli;
                ImageView2_green = ImageView_lamell_angoli_green;
            } else if (TextView_Close2.getText().equals("D")) {
                ImageView2 = ImageView_4e5;
                ImageView2_green = ImageView_4e5_green;
            } else if (TextView_Close2.getText().equals("----")) {
                ImageView2 = null;
                ImageView2_green = null;
            }

            if (TextView_Close3.getText().equals("A")) {
                ImageView3 = ImageView_laterali;
                ImageView3_green = ImageView_laterali_green;
            } else if (TextView_Close3.getText().equals("B")) {
                ImageView3 = ImageView_frontale;
                ImageView3_green = ImageView_frontale_green;
            } else if (TextView_Close3.getText().equals("C")) {
                ImageView3 = ImageView_lamell_angoli;
                ImageView3_green = ImageView_lamell_angoli_green;
            } else if (TextView_Close3.getText().equals("D")) {
                ImageView3 = ImageView_4e5;
                ImageView3_green = ImageView_4e5_green;
            } else if (TextView_Close3.getText().equals("----")) {
                ImageView3 = null;
                ImageView3_green = null;
            }

            if (TextView_Close4.getText().equals("A")) {
                ImageView4 = ImageView_laterali;
                ImageView4_green = ImageView_laterali_green;
            } else if (TextView_Close4.getText().equals("B")) {
                ImageView4 = ImageView_frontale;
                ImageView4_green = ImageView_frontale_green;
            } else if (TextView_Close4.getText().equals("C")) {
                ImageView4 = ImageView_lamell_angoli;
                ImageView4_green = ImageView_lamell_angoli_green;
            } else if (TextView_Close4.getText().equals("D")) {
                ImageView4 = ImageView_4e5;
                ImageView4_green = ImageView_4e5_green;
            } else if (TextView_Close4.getText().equals("----")) {
                ImageView4 = null;
                ImageView4_green = null;
            }


        }


        Id_sequenza_grafica = 0;
        startTimer();

    }

    /**
     * Button for set the default values
     *
     * @param view
     */
    public void on_click_default_time(View view) {

        // find which radioButton is checked by id
        if (sequenza_standard.attiva == true) {
            sequenza_standard = new Sequenza("Standard", false, 1, 2, 3, 0, 0, 3, 2, 1, 0.2d, 0.0d, 0.1d, 0.0d, 0.0d, 0.0d, 0.2d, 0.2d);
            sequenza_standard.attiva = true;
        }
        if (sequenza_Curve3.attiva == true) {
            sequenza_Curve3 = new Sequenza("Curve3", false, 2, 1, 3, 0, 0, 3, 1, 2, 0.2d, 0.2d, 0.1d, 0.0d, 0.0d, 0.0d, 0.0d, 0.2d);
            sequenza_Curve3.attiva = true;
        }
        if (sequenza_Square.attiva == true) {
            sequenza_Square = new Sequenza("Square", false, 1, 2, 3, 4, 4, 3, 2, 1, 0.2d, 0.1d, 0.0d, 0.1d, 0.0d, 0.2d, 0.2d, 0.2d);
            sequenza_Square.attiva = true;
        }
        if (sequenza_Curve5.attiva == true) {
            sequenza_Curve5 = new Sequenza("Curve5", false, 4, 2, 1, 3, 3, 1, 2, 4, 0.2d, 0.1d, 0.1d, 0.1d, 0.0d, 0.0d, 0.0d, 0.2d);
            sequenza_Curve5.attiva = true;
        }
        if (sequenza_Free.attiva == true) {
            sequenza_Free = new Sequenza("Free", false, 1, 2, 3, 4, 4, 3, 2, 1, 0.2d, 0.2d, 0.2d, 0.2d, 0.2d, 0.2d, 0.2d, 0.2d);
            sequenza_Free.attiva = true;
        }

        AggiornaGrafica_ABCD(GetSequenza_Attiva());  //rimetto ordine ABCD di default
        AggiornaGrafica_Tempi_chiusura_apertura(GetSequenza_Attiva());
        dati_cambiati = true;
    }



    private void Salva() {
        try {
            Invia_alCN_Sequenze_chiusura_apertura();
            Invia_alCN_Tempi();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Button for set the first free value
     *
     * @param view
     */
    public void on_click_Close1(View view) {
        String selectedtext = Get_Sselectedtext();
        if (selectedtext.equals("Free")) {
            if (TextView_Close1.getText().equals("A")) TextView_Close1.setText("B");
            else if (TextView_Close1.getText().equals("B")) TextView_Close1.setText("C");
            else if (TextView_Close1.getText().equals("C")) TextView_Close1.setText("D");
            else if (TextView_Close1.getText().equals("D"))
                TextView_Close1.setText("----");
            else if (TextView_Close1.getText().equals("----"))
                TextView_Close1.setText("A");

            dati_cambiati = true;
        }
    }

    /**
     * Button for set the second free value
     *
     * @param view
     */
    public void on_click_Close2(View view) {
        String selectedtext = Get_Sselectedtext();
        if (selectedtext.equals("Free")) {
            if (TextView_Close2.getText().equals("A")) TextView_Close2.setText("B");
            else if (TextView_Close2.getText().equals("B"))
                TextView_Close2.setText("C");
            else if (TextView_Close2.getText().equals("C"))
                TextView_Close2.setText("D");
            else if (TextView_Close2.getText().equals("D"))
                TextView_Close2.setText("----");
            else if (TextView_Close2.getText().equals("----"))
                TextView_Close2.setText("A");
            dati_cambiati = true;
        }
    }

    /**
     * Button for set the third free value
     *
     * @param view
     */
    public void on_click_Close3(View view) {
        String selectedtext = Get_Sselectedtext();
        if (selectedtext.equals("Free")) {
            if (TextView_Close3.getText().equals("A")) TextView_Close3.setText("B");
            else if (TextView_Close3.getText().equals("B")) TextView_Close3.setText("C");
            else if (TextView_Close3.getText().equals("C")) TextView_Close3.setText("D");
            else if (TextView_Close3.getText().equals("D"))
                TextView_Close3.setText("----");
            else if (TextView_Close3.getText().equals("----"))
                TextView_Close3.setText("A");
            dati_cambiati = true;
        }
    }

    /**
     * Button for set the fourth free value
     *
     * @param view
     */
    public void on_click_Close4(View view) {
        String selectedtext = Get_Sselectedtext();
        if (selectedtext.equals("Free")) {
            if (TextView_Close4.getText().equals("A")) TextView_Close4.setText("B");
            else if (TextView_Close4.getText().equals("B")) TextView_Close4.setText("C");
            else if (TextView_Close4.getText().equals("C")) TextView_Close4.setText("D");
            else if (TextView_Close4.getText().equals("D")) TextView_Close4.setText("----");
            else if (TextView_Close4.getText().equals("----")) TextView_Close4.setText("A");
            dati_cambiati = true;
        }
    }

    /**
     * Event on click on textview A
     * <p>
     * TODO This is not good, it starts a new receiver every time and is a click event on a textview
     *
     * @param view
     */
    public void on_click_TimeAfterClose1(View view) {
        if (TextView_TimeAfterClose1.getText() != "-") {
            KeyDialog.Lancia_KeyDialogo(null, Piegatore_sequenza.this, TextView_TimeAfterClose1, 5d, 0d, true, false, 1.0d, false, "Broadcast_keyDialog_return", false,"");
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_from_KeyDialog, new IntentFilter("Broadcast_keyDialog_return"));

        }
    }

    /**
     * Event on click on textview A open
     * <p>
     * TODO This is not good, it starts a new receiver every time and is a click event on a textview
     *
     * @param view
     */
    public void on_click_TimeAfterOpen1(View view) {
        if (TextView_TimeAfterOpen1_vq3007.getText() != "-") {
            KeyDialog.Lancia_KeyDialogo(null, Piegatore_sequenza.this, TextView_TimeAfterOpen1_vq3007, 5d, 0d, true, false, 1.0d, false, "Broadcast_keyDialog_return", false,"");
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_from_KeyDialog, new IntentFilter("Broadcast_keyDialog_return"));
        }
    }

    /**
     * Event on click on textview B
     * <p>
     * TODO This is not good, it starts a new receiver every time and is a click event on a textview
     *
     * @param view
     */
    public void on_click_TimeAfterClose2(View view) {
        if (TextView_TimeAfterClose2.getText() != "-") {
            KeyDialog.Lancia_KeyDialogo(null, Piegatore_sequenza.this, TextView_TimeAfterClose2, 5d, 0d, true, false, 1.0d, false, "Broadcast_keyDialog_return", false,"");
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_from_KeyDialog, new IntentFilter("Broadcast_keyDialog_return"));
        }
    }

    /**
     * Event on click on textview B open
     * <p>
     * TODO This is not good, it starts a new receiver every time and is a click event on a textview
     *
     * @param view
     */
    public void on_click_TimeAfterOpen2(View view) {
        if (TextView_TimeAfterOpen2_vq3009.getText() != "-") {
            KeyDialog.Lancia_KeyDialogo(null, Piegatore_sequenza.this, TextView_TimeAfterOpen2_vq3009, 5d, 0d, true, false, 1.0d, false, "Broadcast_keyDialog_return", false,"");
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_from_KeyDialog, new IntentFilter("Broadcast_keyDialog_return"));
        }
    }

    /**
     * Event on click on textview C
     * <p>
     * TODO This is not good, it starts a new receiver every time and is a click event on a textview
     *
     * @param view
     */
    public void on_click_TimeAfterClose3(View view) {
        if (TextView_TimeAfterClose3.getText() != "-") {
            KeyDialog.Lancia_KeyDialogo(null, Piegatore_sequenza.this, TextView_TimeAfterClose3, 5d, 0d, true, false, 1.0d, false, "Broadcast_keyDialog_return", false,"");
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_from_KeyDialog, new IntentFilter("Broadcast_keyDialog_return"));
        }
    }

    /**
     * Event on click on textview C open
     * <p>
     * TODO This is not good, it starts a new receiver every time and is a click event on a textview
     *
     * @param view
     */
    public void on_click_TimeAfterOpen3(View view) {
        if (TextView_TimeAfterOpen3_vq3011.getText() != "-") {
            KeyDialog.Lancia_KeyDialogo(null, Piegatore_sequenza.this, TextView_TimeAfterOpen3_vq3011, 5d, 0d, true, false, 1.0d, false, "Broadcast_keyDialog_return", false,"");
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_from_KeyDialog, new IntentFilter("Broadcast_keyDialog_return"));
        }
    }

    /**
     * Event on click on textview D
     * <p>
     * TODO This is not good, it starts a new receiver every time and is a click event on a textview
     *
     * @param view
     */
    public void on_click_TimeAfterClose4(View view) {
        if (TextView_TimeAfterClose4.getText() != "-") {
            KeyDialog.Lancia_KeyDialogo(null, Piegatore_sequenza.this, TextView_TimeAfterClose4, 5d, 0d, true, false, 1.0d, false, "Broadcast_keyDialog_return", false,"");
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_from_KeyDialog, new IntentFilter("Broadcast_keyDialog_return"));
        }
    }

    /**
     * Event on click on textview D open
     * <p>
     * TODO This is not good, it starts a new receiver every time and is a click event on a textview
     *
     * @param view
     */
    public void on_click_TimeAfterOpen4(View view) {
        if (TextView_TimeAfterOpen4_vq3013.getText() != "-") {
            KeyDialog.Lancia_KeyDialogo(null, Piegatore_sequenza.this, TextView_TimeAfterOpen4_vq3013, 5d, 0d, true, false, 1.0d, false, "Broadcast_keyDialog_return", false,"");
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_from_KeyDialog, new IntentFilter("Broadcast_keyDialog_return"));
        }
    }

    /**
     * Button for exit from this activity
     *
     * @param view
     * @throws IOException
     */
    public void onClick_exit(View view) {

        if (dati_cambiati)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm");
            builder.setMessage(R.string.ConfermaSalva);

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    write_data_toCn = true;
                    Salva();


                    dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    KillThread();
                    finish();
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }else
        {

            KillThread();
            finish();
        }

    }

    private void Exit() {
        KillThread();
        finish();
    }

    @Override
    public void onResume() {     // system calls this method as the first indication that the user is leaving your activity
        super.onResume();

        if (!Thread_Running) {
            Thread_Running = false;
            StopThread = false;
            read_daCN_tipo = true;
            read_daCN_tempi = true;
            Piegatore_sequenza.MyAndroidThread_Seq_Pieg myTask_Seq_Pieg = new Piegatore_sequenza.MyAndroidThread_Seq_Pieg(this);
            thread_Sequenza_piegatura = new Thread(myTask_Seq_Pieg, "Seq Piegatura myTask");
            thread_Sequenza_piegatura.start();
            Log.d("JAM TAG", "Start Seq Piegatura Thread resume");
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
        handler.removeCallbacks(runnable);
    }

    @Override                   //your activity is no longer visible to the user
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Function for start the timer
     */
    public void startTimer() {
        handler.postDelayed(runnable, time_simulazione);
    }

    /**
     * Function for stop the timer
     */
    public void cancelTimer() {
        handler.removeCallbacks(runnable);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Double rapporto = 10.0d;
            Sequenza sequenza_attiva = GetSequenza_Attiva();
            switch (Id_sequenza_grafica) {
                case 0:
                    if (ImageView1 != null)
                        ImageView1.setVisibility(View.GONE);
                    if (ImageView1_green != null)
                        ImageView1_green.setVisibility(View.VISIBLE);
                    Id_sequenza_grafica = 1;
                    ritardo_grafica = sequenza_attiva.time_close_1 * rapporto;
                    break;
                case 1:
                    ritardo_grafica--;
                    if (ritardo_grafica < 1) {
                        if (ImageView2 != null)
                            ImageView2.setVisibility(View.GONE);
                        if (ImageView2_green != null)
                            ImageView2_green.setVisibility(View.VISIBLE);
                        Id_sequenza_grafica = 2;
                        ritardo_grafica = sequenza_attiva.time_close_2 * rapporto;
                    }
                    break;
                case 2:
                    ritardo_grafica--;
                    if (ritardo_grafica < 1) {
                        if (ImageView3 != null)
                            ImageView3.setVisibility(View.GONE);
                        if (ImageView3_green != null)
                            ImageView3_green.setVisibility(View.VISIBLE);
                        Id_sequenza_grafica = 3;
                        ritardo_grafica = sequenza_attiva.time_close_3 * rapporto;
                    }
                    break;
                case 3:
                    ritardo_grafica--;
                    if (ritardo_grafica < 1) {
                        if (ImageView4 != null)
                            ImageView4.setVisibility(View.GONE);
                        if (ImageView4_green != null)
                            ImageView4_green.setVisibility(View.VISIBLE);
                        Id_sequenza_grafica = 4;
                        ritardo_grafica = sequenza_attiva.time_close_4 * rapporto;
                    }
                    break;
                case 4:
                    ritardo_grafica--;
                    if (ritardo_grafica < 1) {
                        if (ImageView4 != null)
                            ImageView4.setVisibility(View.VISIBLE);
                        if (ImageView4_green != null)
                            ImageView4_green.setVisibility(View.GONE);
                        Id_sequenza_grafica = 5;
                        ritardo_grafica = sequenza_attiva.time_open_4_vq3013 * rapporto;
                    }
                    break;
                case 5:
                    ritardo_grafica--;
                    if (ritardo_grafica < 1) {
                        if (ImageView3 != null)
                            ImageView3.setVisibility(View.VISIBLE);
                        if (ImageView3_green != null)
                            ImageView3_green.setVisibility(View.GONE);
                        Id_sequenza_grafica = 6;
                        ritardo_grafica = sequenza_attiva.time_open_3_vq3011 * rapporto;
                    }
                    break;
                case 6:
                    ritardo_grafica--;
                    if (ritardo_grafica < 1) {
                        if (ImageView2 != null)
                            ImageView2.setVisibility(View.VISIBLE);
                        if (ImageView2_green != null)
                            ImageView2_green.setVisibility(View.GONE);
                        Id_sequenza_grafica = 7;
                        ritardo_grafica = sequenza_attiva.time_open_2_vq3009 * rapporto;
                    }
                    break;
                case 7:
                    ritardo_grafica--;
                    if (ritardo_grafica < 1) {
                        if (ImageView1 != null)
                            ImageView1.setVisibility(View.VISIBLE);
                        if (ImageView1_green != null)
                            ImageView1_green.setVisibility(View.GONE);
                        Id_sequenza_grafica = 99;
                        ritardo_grafica = sequenza_attiva.time_open_1_vq3007 * rapporto;
                    }
                    break;
                case 99:
                    ritardo_grafica--;
                    if (ritardo_grafica < 1) {
                        if (ImageView1 != null)
                            ImageView1.setVisibility(View.VISIBLE);
                        if (ImageView1_green != null)
                            ImageView1_green.setVisibility(View.GONE);
                        if (ImageView2 != null)
                            ImageView2.setVisibility(View.VISIBLE);
                        if (ImageView2_green != null)
                            ImageView2_green.setVisibility(View.GONE);
                        if (ImageView3 != null)
                            ImageView3.setVisibility(View.VISIBLE);
                        if (ImageView3_green != null)
                            ImageView3_green.setVisibility(View.GONE);
                        if (ImageView4 != null)
                            ImageView4.setVisibility(View.VISIBLE);
                        if (ImageView4_green != null)
                            ImageView4_green.setVisibility(View.GONE);
                        cancelTimer();
                        Button_play.setBackgroundResource(R.drawable.play_black);
                        Id_sequenza_grafica = 999;
                    }
                    break;
                default:
                    break;
            }
            startTimer();
        }
    };

    /**
     * Read var from CN
     */
    private void Leggi_Tempi_da_CN() {
        sl.ReadItem(Multicmd_vq3006);
        sl.ReadItem(Multicmd_vq3007);
        sl.ReadItem(Multicmd_vq3008);
        sl.ReadItem(Multicmd_vq3009);
        sl.ReadItem(Multicmd_vq3010);
        sl.ReadItem(Multicmd_vq3011);
        sl.ReadItem(Multicmd_vq3012);
        sl.ReadItem(Multicmd_vq3013);

        sl.ReadItem(Multicmd_Vq3106_RitPiegatore_1_ON);
        sequenza_standard.time_close_1 = (Double) Multicmd_Vq3106_RitPiegatore_1_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3107_RitPiegatore_1_OFF);
        sequenza_standard.time_open_1_vq3007 = (Double)Multicmd_Vq3107_RitPiegatore_1_OFF .getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3108_RitPiegatore_2_ON);
        sequenza_standard.time_close_2 = (Double) Multicmd_Vq3108_RitPiegatore_2_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3109_RitPiegatore_2_OFF);
        sequenza_standard.time_open_2_vq3009 = (Double) Multicmd_Vq3109_RitPiegatore_2_OFF.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3110_RitPiegatore_3_ON);
        sequenza_standard.time_close_3 = (Double) Multicmd_Vq3110_RitPiegatore_3_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3111_RitPiegatore_3_OFF);
        sequenza_standard.time_open_3_vq3011 = (Double)Multicmd_Vq3111_RitPiegatore_3_OFF  .getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3112_RitPiegatore_4_ON);
        sequenza_standard.time_close_4 = (Double) Multicmd_Vq3112_RitPiegatore_4_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3113_RitPiegatore_4_OFF);
        sequenza_standard.time_open_4_vq3013 = (Double)Multicmd_Vq3113_RitPiegatore_4_OFF .getValue() / 1000;






        sl.ReadItem(Multicmd_Vq3114_RitPiegatore_1_ON);
        sequenza_Curve3.time_close_1 = (Double) Multicmd_Vq3114_RitPiegatore_1_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3115_RitPiegatore_1_OFF);
        sequenza_Curve3.time_open_1_vq3007 = (Double) Multicmd_Vq3115_RitPiegatore_1_OFF.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3116_RitPiegatore_2_ON);
        sequenza_Curve3.time_close_2 = (Double) Multicmd_Vq3116_RitPiegatore_2_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3117_RitPiegatore_2_OFF);
        sequenza_Curve3.time_open_2_vq3009 = (Double) Multicmd_Vq3117_RitPiegatore_2_OFF.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3118_RitPiegatore_3_ON);
        sequenza_Curve3.time_close_3 = (Double) Multicmd_Vq3118_RitPiegatore_3_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3119_RitPiegatore_3_OFF);
        sequenza_Curve3.time_open_3_vq3011 = (Double) Multicmd_Vq3119_RitPiegatore_3_OFF.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3120_RitPiegatore_4_ON);
        sequenza_Curve3.time_close_4 = (Double) Multicmd_Vq3120_RitPiegatore_4_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3121_RitPiegatore_4_OFF);
        sequenza_Curve3.time_open_4_vq3013 = (Double) Multicmd_Vq3121_RitPiegatore_4_OFF.getValue() / 1000;

        sl.ReadItem(Multicmd_Vq3122_RitPiegatore_1_ON);
        sequenza_Square.time_close_1 = (Double) Multicmd_Vq3122_RitPiegatore_1_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3123_RitPiegatore_1_OFF);
        sequenza_Square.time_open_1_vq3007 = (Double) Multicmd_Vq3123_RitPiegatore_1_OFF.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3124_RitPiegatore_2_ON);
        sequenza_Square.time_close_2 = (Double) Multicmd_Vq3124_RitPiegatore_2_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3125_RitPiegatore_2_OFF);
        sequenza_Square.time_open_2_vq3009 = (Double) Multicmd_Vq3125_RitPiegatore_2_OFF.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3126_RitPiegatore_3_ON);
        sequenza_Square.time_close_3 = (Double) Multicmd_Vq3126_RitPiegatore_3_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3127_RitPiegatore_3_OFF);
        sequenza_Square.time_open_3_vq3011 = (Double) Multicmd_Vq3127_RitPiegatore_3_OFF.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3128_RitPiegatore_4_ON);
        sequenza_Square.time_close_4 = (Double) Multicmd_Vq3128_RitPiegatore_4_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3129_RitPiegatore_4_OFF);
        sequenza_Square.time_open_4_vq3013 = (Double) Multicmd_Vq3129_RitPiegatore_4_OFF.getValue() / 1000;

        sl.ReadItem(Multicmd_Vq3130_RitPiegatore_1_ON);
        sequenza_Free.time_close_1 = (Double) Multicmd_Vq3130_RitPiegatore_1_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3131_RitPiegatore_1_OFF);
        sequenza_Free.time_open_1_vq3007 = (Double) Multicmd_Vq3131_RitPiegatore_1_OFF.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3132_RitPiegatore_2_ON);
        sequenza_Free.time_close_2 = (Double) Multicmd_Vq3132_RitPiegatore_2_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3133_RitPiegatore_2_OFF);
        sequenza_Free.time_open_2_vq3009 = (Double) Multicmd_Vq3133_RitPiegatore_2_OFF.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3134_RitPiegatore_3_ON);
        sequenza_Free.time_close_3 = (Double) Multicmd_Vq3134_RitPiegatore_3_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3135_RitPiegatore_3_OFF);
        sequenza_Free.time_open_3_vq3011 = (Double) Multicmd_Vq3135_RitPiegatore_3_OFF.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3136_RitPiegatore_4_ON);
        sequenza_Free.time_close_4 = (Double) Multicmd_Vq3136_RitPiegatore_4_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3137_RitPiegatore_4_OFF);
        sequenza_Free.time_open_4_vq3013 = (Double) Multicmd_Vq3137_RitPiegatore_4_OFF.getValue() / 1000;

        sl.ReadItem(Multicmd_Vq3138_RitPiegatore_1_ON);
        sequenza_Curve5.time_close_1 = (Double) Multicmd_Vq3138_RitPiegatore_1_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3139_RitPiegatore_1_OFF);
        sequenza_Curve5.time_open_1_vq3007 = (Double) Multicmd_Vq3139_RitPiegatore_1_OFF.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3140_RitPiegatore_2_ON);
        sequenza_Curve5.time_close_2 = (Double) Multicmd_Vq3140_RitPiegatore_2_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3141_RitPiegatore_2_OFF);
        sequenza_Curve5.time_open_2_vq3009 = (Double) Multicmd_Vq3141_RitPiegatore_2_OFF.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3142_RitPiegatore_3_ON);
        sequenza_Curve5.time_close_3 = (Double) Multicmd_Vq3142_RitPiegatore_3_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3143_RitPiegatore_3_OFF);
        sequenza_Curve5.time_open_3_vq3011 = (Double) Multicmd_Vq3143_RitPiegatore_3_OFF.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3144_RitPiegatore_4_ON);
        sequenza_Curve5.time_close_4 = (Double) Multicmd_Vq3144_RitPiegatore_4_ON.getValue() / 1000;
        sl.ReadItem(Multicmd_Vq3145_RitPiegatore_4_OFF);
        sequenza_Curve5.time_open_4_vq3013 = (Double) Multicmd_Vq3145_RitPiegatore_4_OFF.getValue() / 1000;
    }

    /**
     * Set the sequence type
     *
     * @param value
     */
    private void Set_Tipo(Double value) {
        if (value == 1230.0d) {
            AttivaSequenza(sequenza_standard);
        } else if (value == 2130.0d) {
            AttivaSequenza(sequenza_Curve3);
        } else if (value == 1234.0d) {
            AttivaSequenza(sequenza_Square);
        } else if (value == 4213.0d) {
            AttivaSequenza(sequenza_Curve5);
        } else {
            AttivaSequenza(sequenza_Free);
            Mci_write_Vn115_CopiaSeqPiegFree.valore = value;        //variabile copia
            Mci_write_Vn115_CopiaSeqPiegFree.write_flag = true;
        }
    }

    /**
     * Function for enable the current sequence and disable all the others
     *
     * @param sequenza
     */
    private void AttivaSequenza(Sequenza sequenza) {
        if (sequenza == sequenza_standard) {
            sequenza_standard.attiva = true;
            sequenza_Curve3.attiva = false;
            sequenza_Square.attiva = false;
            sequenza_Curve5.attiva = false;
            sequenza_Free.attiva = false;
        } else if (sequenza == sequenza_Curve3) {
            sequenza_standard.attiva = false;
            sequenza_Curve3.attiva = true;
            sequenza_Square.attiva = false;
            sequenza_Curve5.attiva = false;
            sequenza_Free.attiva = false;
        } else if (sequenza == sequenza_Square) {
            sequenza_standard.attiva = false;
            sequenza_Curve3.attiva = false;
            sequenza_Square.attiva = true;
            sequenza_Curve5.attiva = false;
            sequenza_Free.attiva = false;
        } else if (sequenza == sequenza_Curve5) {
            sequenza_standard.attiva = false;
            sequenza_Curve3.attiva = false;
            sequenza_Square.attiva = false;
            sequenza_Curve5.attiva = true;
            sequenza_Free.attiva = false;
        } else if (sequenza == sequenza_Free) {
            sequenza_standard.attiva = false;
            sequenza_Curve3.attiva = false;
            sequenza_Square.attiva = false;
            sequenza_Curve5.attiva = false;
            sequenza_Free.attiva = true;
        }
    }

    private String Get_Sselectedtext() {
        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(radioButtonID);
        int idx = radioGroup.indexOfChild(radioButton);

        RadioButton r = (RadioButton) radioGroup.getChildAt(idx);
        return r.getText().toString();
    }

    /**
     * TODO
     *
     * @param sequenza
     */
    private void AggiornaGrafica_ABCD(Sequenza sequenza) {
        if (sequenza.tipo == "Standard") {
            TextView_Close1.setText("A");
            TextView_Close2.setText("B");
            TextView_Close3.setText("C");
            TextView_Close4.setText("-");
            TextView_Close4.setVisibility(View.GONE);
            TextView_Open4.setText("-");
            TextView_Open4.setVisibility(View.GONE);
            TextView_Open3.setText("C");
            TextView_Open2.setText("B");
            TextView_Open1.setText("A");

            TextView_Close1.setTextColor(Color.RED);
            TextView_Close2.setTextColor(Color.RED);
            TextView_Close3.setTextColor(Color.RED);
            TextView_Close4.setTextColor(Color.RED);
            TextView_Open4.setTextColor(Color.RED);
            TextView_Open3.setTextColor(Color.RED);
            TextView_Open2.setTextColor(Color.RED);
            TextView_Open1.setTextColor(Color.RED);

            Radio_Standard.setChecked(true);
            Radio_Curve3.setChecked(false);
            Radio_Square.setChecked(false);
            Radio_Free.setChecked(false);
            Radio_Curve5.setChecked(false);
        } else if (sequenza.tipo == "Curve3") {
            TextView_Close1.setText("B");
            TextView_Close2.setText("A");
            TextView_Close3.setText("C");
            TextView_Close4.setText("-");
            TextView_Close4.setVisibility(View.GONE);
            TextView_Open4.setText("-");
            TextView_Open4.setVisibility(View.GONE);
            TextView_Open3.setText("C");
            TextView_Open2.setText("A");
            TextView_Open1.setText("B");
            TextView_Close1.setTextColor(Color.RED);
            TextView_Close2.setTextColor(Color.RED);
            TextView_Close3.setTextColor(Color.RED);
            TextView_Close4.setTextColor(Color.RED);
            TextView_Open4.setTextColor(Color.RED);
            TextView_Open3.setTextColor(Color.RED);
            TextView_Open2.setTextColor(Color.RED);
            TextView_Open1.setTextColor(Color.RED);
            Radio_Standard.setChecked(false);
            Radio_Curve3.setChecked(true);
            Radio_Square.setChecked(false);
            Radio_Free.setChecked(false);
            Radio_Curve5.setChecked(false);
        } else if (sequenza.tipo == "Square") {
            TextView_Close1.setText("A");
            TextView_Close2.setText("B");
            TextView_Close3.setText("C");
            TextView_Close4.setText("D");
            TextView_Close4.setVisibility(View.VISIBLE);
            TextView_Open4.setText("D");
            TextView_Open4.setVisibility(View.VISIBLE);
            TextView_Open3.setText("C");
            TextView_Open2.setText("B");
            TextView_Open1.setText("A");
            TextView_Close1.setTextColor(Color.RED);
            TextView_Close2.setTextColor(Color.RED);
            TextView_Close3.setTextColor(Color.RED);
            TextView_Close4.setTextColor(Color.RED);
            TextView_Open4.setTextColor(Color.RED);
            TextView_Open3.setTextColor(Color.RED);
            TextView_Open2.setTextColor(Color.RED);
            TextView_Open1.setTextColor(Color.RED);
            Radio_Standard.setChecked(false);
            Radio_Curve3.setChecked(false);
            Radio_Square.setChecked(true);
            Radio_Free.setChecked(false);
            Radio_Curve5.setChecked(false);
        } else if (sequenza.tipo == "Curve5") {
            TextView_Close1.setText("D");
            TextView_Close2.setText("B");
            TextView_Close3.setText("A");
            TextView_Close4.setText("C");
            TextView_Close4.setVisibility(View.VISIBLE);
            TextView_Open4.setText("C");
            TextView_Open4.setVisibility(View.VISIBLE);
            TextView_Open3.setText("A");
            TextView_Open2.setText("B");
            TextView_Open1.setText("D");
            TextView_Close1.setTextColor(Color.RED);
            TextView_Close2.setTextColor(Color.RED);
            TextView_Close3.setTextColor(Color.RED);
            TextView_Close4.setTextColor(Color.RED);
            TextView_Open4.setTextColor(Color.RED);
            TextView_Open3.setTextColor(Color.RED);
            TextView_Open2.setTextColor(Color.RED);
            TextView_Open1.setTextColor(Color.RED);
            Radio_Standard.setChecked(false);
            Radio_Curve3.setChecked(false);
            Radio_Square.setChecked(false);
            Radio_Free.setChecked(false);
            Radio_Curve5.setChecked(true);
        } else if (sequenza.tipo == "Free") {

            TextView_Close1.setTextColor(Color.BLUE);
            TextView_Close2.setTextColor(Color.BLUE);
            TextView_Close3.setTextColor(Color.BLUE);
            TextView_Close4.setTextColor(Color.BLUE);


            Double v = (Double) Multicmd_Vn115_CopiaSeqPiegFree.getValue();
            String str = String.valueOf(v);
            String Lettera1 = "----", Lettera2 = "----", Lettera3 = "----", Lettera4 = "----";
            String[] separated = str.split("\\.");
            if (separated.length > 1) {
                String vint = separated[0];
                if (vint.length() > 0) {
                    String v1 = Character.toString(vint.charAt(0));
                    if (v1.equals("1")) Lettera1 = "A";
                    if (v1.equals("2")) Lettera1 = "B";
                    if (v1.equals("3")) Lettera1 = "C";
                    if (v1.equals("4")) Lettera1 = "D";
                }
                if (vint.length() > 1) {
                    String v2 = Character.toString(vint.charAt(1));
                    if (v2.equals("1")) Lettera2 = "A";
                    if (v2.equals("2")) Lettera2 = "B";
                    if (v2.equals("3")) Lettera2 = "C";
                    if (v2.equals("4")) Lettera2 = "D";
                }
                if (vint.length() > 2) {
                    String v3 = Character.toString(vint.charAt(2));
                    if (v3.equals("1")) Lettera3 = "A";
                    if (v3.equals("2")) Lettera3 = "B";
                    if (v3.equals("3")) Lettera3 = "C";
                    if (v3.equals("4")) Lettera3 = "D";

                }
                if (vint.length() > 3) {
                    String v4 = Character.toString(vint.charAt(3));
                    if (v4.equals("1")) Lettera4 = "A";
                    if (v4.equals("2")) Lettera4 = "B";
                    if (v4.equals("3")) Lettera4 = "C";
                    if (v4.equals("4")) Lettera4 = "D";
                }
            }
            TextView_Close1.setText(Lettera1);
            TextView_Close2.setText(Lettera2);
            TextView_Close3.setText(Lettera3);
            TextView_Close4.setText(Lettera4);
            TextView_Close4.setVisibility(View.VISIBLE);

            TextView_Open4.setText(Lettera4);
            TextView_Open4.setVisibility(View.VISIBLE);
            TextView_Open3.setText(Lettera3);
            TextView_Open2.setText(Lettera2);
            TextView_Open1.setText(Lettera1);

            Radio_Standard.setChecked(false);
            Radio_Curve3.setChecked(false);
            Radio_Square.setChecked(false);
            Radio_Free.setChecked(true);
            Radio_Curve5.setChecked(false);

            TextView_TimeAfterClose1.setText(Mci_write_vq3006.valore.toString());
        }
    }

    /**
     * Function for update the values based on the sequence type
     *
     * @param sequenza
     */
    private void AggiornaGrafica_Tempi_chiusura_apertura(Sequenza sequenza) {
        if (sequenza.tipo == "Standard") {
            Set_Grafica_tim_close(sequenza_standard);
        } else if (sequenza.tipo == "Curve3") {
            Set_Grafica_tim_close(sequenza_Curve3);
        } else if (sequenza.tipo == "Square") {
            Set_Grafica_tim_close(sequenza_Square);
        } else if (sequenza.tipo == "Curve5") {
            Set_Grafica_tim_close(sequenza_Curve5);
        } else if (sequenza.tipo == "Free") {
            Set_Grafica_tim_close(sequenza_Free);
        }
    }

    /**
     * Function for update the values
     *
     * @param sequenza
     */
    private void Set_Grafica_tim_close(Sequenza sequenza) {



        if (sequenza_standard.attiva) {
            TextView_TimeAfterClose1.setText("" + sequenza_standard.time_close_1);
            TextView_TimeAfterClose2.setText("" + sequenza_standard.time_close_2);
            TextView_TimeAfterClose3.setText("" + sequenza_standard.time_close_3);
            TextView_TimeAfterClose4.setText("" + sequenza_standard.time_close_4 );
            TextView_TimeAfterClose4.setVisibility(View.GONE);
            TextView_TimeAfterOpen4_vq3013.setText("" + sequenza_standard.time_open_4_vq3013);
            TextView_TimeAfterOpen4_vq3013.setVisibility(View.GONE);
            TextView_TimeAfterOpen3_vq3011.setText("" + sequenza_standard.time_open_3_vq3011);
            TextView_TimeAfterOpen2_vq3009.setText("" + sequenza_standard.time_open_2_vq3009);
            TextView_TimeAfterOpen1_vq3007.setText("" + sequenza_standard.time_open_1_vq3007);

        }
        if (sequenza_Curve3.attiva) {
            TextView_TimeAfterClose1.setText("" + sequenza_Curve3.time_close_1);
            TextView_TimeAfterClose2.setText("" + sequenza_Curve3.time_close_2);
            TextView_TimeAfterClose3.setText("" + sequenza_Curve3.time_close_3);
            TextView_TimeAfterClose4.setText("" + sequenza_Curve3.time_close_4 );
            TextView_TimeAfterClose4.setVisibility(View.GONE);
            TextView_TimeAfterOpen4_vq3013.setText("" + sequenza_Curve3.time_open_4_vq3013);
            TextView_TimeAfterOpen4_vq3013.setVisibility(View.GONE);
            TextView_TimeAfterOpen3_vq3011.setText("" + sequenza_Curve3.time_open_3_vq3011);
            TextView_TimeAfterOpen2_vq3009.setText("" + sequenza_Curve3.time_open_2_vq3009);
            TextView_TimeAfterOpen1_vq3007.setText("" + sequenza_Curve3.time_open_1_vq3007);

        }
        if (sequenza_Square.attiva) {
            TextView_TimeAfterClose1.setText("" + sequenza_Square.time_close_1);
            TextView_TimeAfterClose2.setText("" + sequenza_Square.time_close_2);
            TextView_TimeAfterClose3.setText("" + sequenza_Square.time_close_3);
            TextView_TimeAfterClose4.setText("" + sequenza_Square.time_close_4 );
            TextView_TimeAfterClose4.setVisibility(View.VISIBLE);
            TextView_TimeAfterOpen4_vq3013.setText("" + sequenza_Square.time_open_4_vq3013);
            TextView_TimeAfterOpen4_vq3013.setVisibility(View.VISIBLE);
            TextView_TimeAfterOpen3_vq3011.setText("" + sequenza_Square.time_open_3_vq3011);
            TextView_TimeAfterOpen2_vq3009.setText("" + sequenza_Square.time_open_2_vq3009);
            TextView_TimeAfterOpen1_vq3007.setText("" + sequenza_Square.time_open_1_vq3007);

        }
        if (sequenza_Free.attiva) {
            TextView_TimeAfterClose1.setText("" + sequenza_Free.time_close_1);
            TextView_TimeAfterClose2.setText("" + sequenza_Free.time_close_2);
            TextView_TimeAfterClose3.setText("" + sequenza_Free.time_close_3);
            TextView_TimeAfterClose4.setText("" + sequenza_Free.time_close_4 );
            TextView_TimeAfterClose4.setVisibility(View.VISIBLE);
            TextView_TimeAfterOpen4_vq3013.setText("" + sequenza_Free.time_open_4_vq3013);
            TextView_TimeAfterOpen4_vq3013.setVisibility(View.VISIBLE);
            TextView_TimeAfterOpen3_vq3011.setText("" + sequenza_Free.time_open_3_vq3011);
            TextView_TimeAfterOpen2_vq3009.setText("" + sequenza_Free.time_open_2_vq3009);
            TextView_TimeAfterOpen1_vq3007.setText("" + sequenza_Free.time_open_1_vq3007);

        }
        if (sequenza_Curve5.attiva) {
            TextView_TimeAfterClose1.setText("" + sequenza_Curve5.time_close_1);
            TextView_TimeAfterClose2.setText("" + sequenza_Curve5.time_close_2);
            TextView_TimeAfterClose3.setText("" + sequenza_Curve5.time_close_3);
            TextView_TimeAfterClose4.setText("" + sequenza_Curve5.time_close_4 );
            TextView_TimeAfterClose4.setVisibility(View.VISIBLE);
            TextView_TimeAfterOpen4_vq3013.setText("" + sequenza_Curve5.time_open_4_vq3013);
            TextView_TimeAfterOpen4_vq3013.setVisibility(View.VISIBLE);
            TextView_TimeAfterOpen3_vq3011.setText("" + sequenza_Curve5.time_open_3_vq3011);
            TextView_TimeAfterOpen2_vq3009.setText("" + sequenza_Curve5.time_open_2_vq3009);
            TextView_TimeAfterOpen1_vq3007.setText("" + sequenza_Curve5.time_open_1_vq3007);

        }
        /*
        if (sequenza_Curve3.attiva) return sequenza_Curve3;
        if (sequenza_Square.attiva) return sequenza_Square;
        if (sequenza_Free.attiva) return sequenza_Free;
        if (sequenza_Curve5.attiva) return sequenza_Curve5;

        if (sequenza.seq_chiusura_1 == 1) TextView_TimeAfterClose1.setText("" + sequenza.time_close_1);
        if (sequenza.seq_chiusura_1 == 2) TextView_TimeAfterClose2.setText("" + sequenza.time_close_1);
        if (sequenza.seq_chiusura_1 == 3) TextView_TimeAfterClose3.setText("" + sequenza.time_close_1);
        if (sequenza.seq_chiusura_1 == 4) TextView_TimeAfterClose4.setText("" + sequenza.time_close_1);

        if (sequenza.seq_chiusura_2 == 1) TextView_TimeAfterClose1.setText("" + sequenza.time_close_2);
        if (sequenza.seq_chiusura_2 == 2) TextView_TimeAfterClose2.setText("" + sequenza.time_close_2);
        if (sequenza.seq_chiusura_2 == 3) TextView_TimeAfterClose3.setText("" + sequenza.time_close_2);
        if (sequenza.seq_chiusura_2 == 4) TextView_TimeAfterClose4.setText("" + sequenza.time_close_2);

        if (sequenza.seq_chiusura_3 == 1) TextView_TimeAfterClose1.setText("" + sequenza.time_close_3);
        if (sequenza.seq_chiusura_3 == 2) TextView_TimeAfterClose2.setText("" + sequenza.time_close_3);
        if (sequenza.seq_chiusura_3 == 3) TextView_TimeAfterClose3.setText("" + sequenza.time_close_3);
        if (sequenza.seq_chiusura_3 == 4) TextView_TimeAfterClose4.setText("" + sequenza.time_close_3);

        if (sequenza.seq_chiusura_4 == 1) TextView_TimeAfterClose1.setText("" + sequenza.time_close_4);
        if (sequenza.seq_chiusura_4 == 2) TextView_TimeAfterClose2.setText("" + sequenza.time_close_4);
        if (sequenza.seq_chiusura_4 == 3) TextView_TimeAfterClose3.setText("" + sequenza.time_close_4);
        if (sequenza.seq_chiusura_4 == 4) TextView_TimeAfterClose4.setText("" + sequenza.time_close_4);

        TextView_TimeAfterOpen4.setText("-");
        TextView_TimeAfterOpen3.setText("-");
        TextView_TimeAfterOpen2.setText("-");
        TextView_TimeAfterOpen1.setText("-");

        if (sequenza.seq_apertura_1 == 1) TextView_TimeAfterOpen4.setText("" + sequenza.time_open_1);
        if (sequenza.seq_apertura_1 == 2) TextView_TimeAfterOpen3.setText("" + sequenza.time_open_1);
        if (sequenza.seq_apertura_1 == 3) TextView_TimeAfterOpen2.setText("" + sequenza.time_open_1);
        if (sequenza.seq_apertura_1 == 4) TextView_TimeAfterOpen1.setText("" + sequenza.time_open_1);

        if (sequenza.seq_apertura_2 == 1) TextView_TimeAfterOpen4.setText("" + sequenza.time_open_2);
        if (sequenza.seq_apertura_2 == 2) TextView_TimeAfterOpen3.setText("" + sequenza.time_open_2);
        if (sequenza.seq_apertura_2 == 3) TextView_TimeAfterOpen2.setText("" + sequenza.time_open_2);
        if (sequenza.seq_apertura_2 == 4) TextView_TimeAfterOpen1.setText("" + sequenza.time_open_2);

        if (sequenza.seq_apertura_3 == 1) TextView_TimeAfterOpen4.setText("" + sequenza.time_open_3);
        if (sequenza.seq_apertura_3 == 2) TextView_TimeAfterOpen3.setText("" + sequenza.time_open_3);
        if (sequenza.seq_apertura_3 == 3) TextView_TimeAfterOpen2.setText("" + sequenza.time_open_3);
        if (sequenza.seq_apertura_3 == 4) TextView_TimeAfterOpen1.setText("" + sequenza.time_open_3);

        if (sequenza.seq_apertura_4 == 1) TextView_TimeAfterOpen4.setText("" + sequenza.time_open_4);
        if (sequenza.seq_apertura_4 == 2) TextView_TimeAfterOpen3.setText("" + sequenza.time_open_4);
        if (sequenza.seq_apertura_4 == 3) TextView_TimeAfterOpen2.setText("" + sequenza.time_open_4);
        if (sequenza.seq_apertura_4 == 4) TextView_TimeAfterOpen1.setText("" + sequenza.time_open_4);
*/

    }


     /* Function for get the current active sequence
     *
     * @return
     */
    private Sequenza GetSequenza_Attiva() {

        if (sequenza_standard.attiva) return sequenza_standard;
        if (sequenza_Curve3.attiva) return sequenza_Curve3;
        if (sequenza_Square.attiva) return sequenza_Square;
        if (sequenza_Free.attiva) return sequenza_Free;
        if (sequenza_Curve5.attiva) return sequenza_Curve5;

        return null;
    }

    /**
     * Function for send values to the CN
     */
    private void Invia_alCN_Tempi() {

        //tempi di esecuzione
        Sequenza sequenza_attiva = GetSequenza_Attiva();
        Mci_write_vq3006.valore = sequenza_attiva.time_close_1;
        Mci_write_vq3006.write_flag = true;
        Mci_write_vq3006.tipoVariabile = Mci_write.TipoVariabile.VQ;
        Mci_write_vq3008.valore = sequenza_attiva.time_close_2;
        Mci_write_vq3008.write_flag = true;
        Mci_write_vq3008.tipoVariabile = Mci_write.TipoVariabile.VQ;
        Mci_write_vq3010.valore = sequenza_attiva.time_close_3;
        Mci_write_vq3010.write_flag = true;
        Mci_write_vq3010.tipoVariabile = Mci_write.TipoVariabile.VQ;
        Mci_write_vq3012.valore = sequenza_attiva.time_close_4;
        Mci_write_vq3012.write_flag = true;
        Mci_write_vq3012.tipoVariabile = Mci_write.TipoVariabile.VQ;
        Mci_write_vq3007.valore = sequenza_attiva.time_open_1_vq3007;
        Mci_write_vq3007.write_flag = true;
        Mci_write_vq3007.tipoVariabile = Mci_write.TipoVariabile.VQ;
        Mci_write_vq3009.valore = sequenza_attiva.time_open_2_vq3009;
        Mci_write_vq3009.write_flag = true;
        Mci_write_vq3009.tipoVariabile = Mci_write.TipoVariabile.VQ;
        Mci_write_vq3011.valore = sequenza_attiva.time_open_3_vq3011;
        Mci_write_vq3011.write_flag = true;
        Mci_write_vq3011.tipoVariabile = Mci_write.TipoVariabile.VQ;
        Mci_write_vq3013.valore = sequenza_attiva.time_open_4_vq3013;
        Mci_write_vq3013.write_flag = true;
        Mci_write_vq3013.tipoVariabile = Mci_write.TipoVariabile.VQ;

        //tempi da ricordarsi
        if (sequenza_standard.attiva) {
            Mci_write_vq3106.valore = sequenza_standard.time_close_1;
            Mci_write_vq3106.write_flag = true;
            Mci_write_vq3106.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3108.valore = sequenza_standard.time_close_2;
            Mci_write_vq3108.write_flag = true;
            Mci_write_vq3108.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3110.valore = sequenza_standard.time_close_3;
            Mci_write_vq3110.write_flag = true;
            Mci_write_vq3110.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3112.valore = sequenza_standard.time_close_4;
            Mci_write_vq3112.write_flag = true;
            Mci_write_vq3112.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3107.valore = sequenza_standard.time_open_1_vq3007;
            Mci_write_vq3107.write_flag = true;
            Mci_write_vq3107.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3109.valore = sequenza_standard.time_open_2_vq3009;
            Mci_write_vq3109.write_flag = true;
            Mci_write_vq3109.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3111.valore = sequenza_standard.time_open_3_vq3011;
            Mci_write_vq3111.write_flag = true;
            Mci_write_vq3111.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3113.valore = sequenza_standard.time_open_4_vq3013;
            Mci_write_vq3113.write_flag = true;
            Mci_write_vq3113.tipoVariabile = Mci_write.TipoVariabile.VQ;
        }
        if (sequenza_Curve3.attiva) {
            Mci_write_vq3114.valore = sequenza_Curve3.time_close_1;
            Mci_write_vq3114.write_flag = true;
            Mci_write_vq3114.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3116.valore = sequenza_Curve3.time_close_2;
            Mci_write_vq3116.write_flag = true;
            Mci_write_vq3116.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3118.valore = sequenza_Curve3.time_close_3;
            Mci_write_vq3118.write_flag = true;
            Mci_write_vq3118.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3120.valore = sequenza_Curve3.time_close_4;
            Mci_write_vq3120.write_flag = true;
            Mci_write_vq3120.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3115.valore = sequenza_Curve3.time_open_1_vq3007;
            Mci_write_vq3115.write_flag = true;
            Mci_write_vq3115.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3117.valore = sequenza_Curve3.time_open_2_vq3009;
            Mci_write_vq3117.write_flag = true;
            Mci_write_vq3117.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3119.valore = sequenza_Curve3.time_open_3_vq3011;
            Mci_write_vq3119.write_flag = true;
            Mci_write_vq3119.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3121.valore = sequenza_Curve3.time_open_4_vq3013;
            Mci_write_vq3121.write_flag = true;
            Mci_write_vq3121.tipoVariabile = Mci_write.TipoVariabile.VQ;
        }
        if (sequenza_Square.attiva) {
            Mci_write_vq3122.valore = sequenza_Square.time_close_1;
            Mci_write_vq3122.write_flag = true;
            Mci_write_vq3122.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3124.valore = sequenza_Square.time_close_2;
            Mci_write_vq3124.write_flag = true;
            Mci_write_vq3124.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3126.valore = sequenza_Square.time_close_3;
            Mci_write_vq3126.write_flag = true;
            Mci_write_vq3126.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3128.valore = sequenza_Square.time_close_4;
            Mci_write_vq3128.write_flag = true;
            Mci_write_vq3128.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3123.valore = sequenza_Square.time_open_1_vq3007;
            Mci_write_vq3123.write_flag = true;
            Mci_write_vq3123.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3125.valore = sequenza_Square.time_open_2_vq3009;
            Mci_write_vq3125.write_flag = true;
            Mci_write_vq3125.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3127.valore = sequenza_Square.time_open_3_vq3011;
            Mci_write_vq3127.write_flag = true;
            Mci_write_vq3127.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3129.valore = sequenza_Square.time_open_4_vq3013;
            Mci_write_vq3129.write_flag = true;
            Mci_write_vq3129.tipoVariabile = Mci_write.TipoVariabile.VQ;
        }
        if (sequenza_Curve5.attiva) {
            Mci_write_vq3138.valore = sequenza_Curve5.time_close_1;
            Mci_write_vq3138.write_flag = true;
            Mci_write_vq3138.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3140.valore = sequenza_Curve5.time_close_2;
            Mci_write_vq3140.write_flag = true;
            Mci_write_vq3140.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3142.valore = sequenza_Curve5.time_close_3;
            Mci_write_vq3142.write_flag = true;
            Mci_write_vq3142.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3144.valore = sequenza_Curve5.time_close_4;
            Mci_write_vq3144.write_flag = true;
            Mci_write_vq3144.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3139.valore = sequenza_Curve5.time_open_1_vq3007;
            Mci_write_vq3139.write_flag = true;
            Mci_write_vq3139.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3141.valore = sequenza_Curve5.time_open_2_vq3009;
            Mci_write_vq3141.write_flag = true;
            Mci_write_vq3141.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3143.valore = sequenza_Curve5.time_open_3_vq3011;
            Mci_write_vq3143.write_flag = true;
            Mci_write_vq3143.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3145.valore = sequenza_Curve5.time_open_4_vq3013;
            Mci_write_vq3145.write_flag = true;
            Mci_write_vq3145.tipoVariabile = Mci_write.TipoVariabile.VQ;
        }
        if (sequenza_Free.attiva) {
            Mci_write_vq3130.valore = sequenza_Free.time_close_1;
            Mci_write_vq3130.write_flag = true;
            Mci_write_vq3130.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3132.valore = sequenza_Free.time_close_2;
            Mci_write_vq3132.write_flag = true;
            Mci_write_vq3132.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3134.valore = sequenza_Free.time_close_3;
            Mci_write_vq3134.write_flag = true;
            Mci_write_vq3134.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3136.valore = sequenza_Free.time_close_4;
            Mci_write_vq3136.write_flag = true;
            Mci_write_vq3136.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3131.valore = sequenza_Free.time_open_1_vq3007;
            Mci_write_vq3131.write_flag = true;
            Mci_write_vq3131.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3133.valore = sequenza_Free.time_open_2_vq3009;
            Mci_write_vq3133.write_flag = true;
            Mci_write_vq3133.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3135.valore = sequenza_Free.time_open_3_vq3011;
            Mci_write_vq3135.write_flag = true;
            Mci_write_vq3135.tipoVariabile = Mci_write.TipoVariabile.VQ;
            Mci_write_vq3137.valore = sequenza_Free.time_open_4_vq3013;
            Mci_write_vq3137.write_flag = true;
            Mci_write_vq3137.tipoVariabile = Mci_write.TipoVariabile.VQ;
        }
    }

    private void Invia_alCN_Sequenze_chiusura_apertura() throws IOException {
        int chiusura = 0;
        int apertura = 0;
        Sequenza sequenza_Attiva = GetSequenza_Attiva();

        if (!sequenza_Free.attiva) {
            chiusura = sequenza_Attiva.seq_chiusura_1 * 1000 + sequenza_Attiva.seq_chiusura_2 * 100 + sequenza_Attiva.seq_chiusura_3 * 10 + sequenza_Attiva.seq_chiusura_4;
            apertura = sequenza_Attiva.seq_apertura_1 * 1000 + sequenza_Attiva.seq_apertura_2 * 100 + sequenza_Attiva.seq_apertura_3 * 10 + sequenza_Attiva.seq_apertura_4;
        } else {
            chiusura = 0;
            apertura = 0;
            if (TextView_Close1.getText().equals("A")) {
                chiusura = chiusura + 1000;
                apertura = apertura + 1;
                sequenza_Attiva.seq_chiusura_1 = 1;
                sequenza_Attiva.seq_apertura_4 = 1;
            }
            if (TextView_Close1.getText().equals("B")) {
                chiusura = chiusura + 2000;
                apertura = apertura + 2;
                sequenza_Attiva.seq_chiusura_1 = 2;
                sequenza_Attiva.seq_apertura_4 = 2;
            }
            if (TextView_Close1.getText().equals("C")) {
                chiusura = chiusura + 3000;
                apertura = apertura + 3;
                sequenza_Attiva.seq_chiusura_1 = 3;
                sequenza_Attiva.seq_apertura_4 = 3;
            }
            if (TextView_Close1.getText().equals("D")) {
                chiusura = chiusura + 4000;
                apertura = apertura + 4;
                sequenza_Attiva.seq_chiusura_1 = 4;
                sequenza_Attiva.seq_apertura_4 = 4;
            }
            if (TextView_Close2.getText().equals("A")) {
                chiusura = chiusura + 100;
                apertura = apertura + 10;
                sequenza_Attiva.seq_chiusura_2 = 1;
                sequenza_Attiva.seq_apertura_3 = 1;
            }
            if (TextView_Close2.getText().equals("B")) {
                chiusura = chiusura + 200;
                apertura = apertura + 20;
                sequenza_Attiva.seq_chiusura_2 = 2;
                sequenza_Attiva.seq_apertura_3 = 2;
            }
            if (TextView_Close2.getText().equals("C")) {
                chiusura = chiusura + 300;
                apertura = apertura + 30;
                sequenza_Attiva.seq_chiusura_2 = 3;
                sequenza_Attiva.seq_apertura_3 = 3;
            }
            if (TextView_Close2.getText().equals("D")) {
                chiusura = chiusura + 400;
                apertura = apertura + 40;
                sequenza_Attiva.seq_chiusura_2 = 4;
                sequenza_Attiva.seq_apertura_3 = 4;
            }
            if (TextView_Close3.getText().equals("A")) {
                chiusura = chiusura + 10;
                apertura = apertura + 100;
                sequenza_Attiva.seq_chiusura_3 = 1;
                sequenza_Attiva.seq_apertura_2 = 1;
            }
            if (TextView_Close3.getText().equals("B")) {
                chiusura = chiusura + 20;
                apertura = apertura + 200;
                sequenza_Attiva.seq_chiusura_3 = 2;
                sequenza_Attiva.seq_apertura_2 = 2;
            }
            if (TextView_Close3.getText().equals("C")) {
                chiusura = chiusura + 30;
                apertura = apertura + 300;
                sequenza_Attiva.seq_chiusura_3 = 3;
                sequenza_Attiva.seq_apertura_2 = 3;
            }
            if (TextView_Close3.getText().equals("D")) {
                chiusura = chiusura + 40;
                apertura = apertura + 400;
                sequenza_Attiva.seq_chiusura_3 = 4;
                sequenza_Attiva.seq_apertura_2 = 4;
            }
            if (TextView_Close4.getText().equals("A")) {
                chiusura = chiusura + 1;
                apertura = apertura + 1000;
                sequenza_Attiva.seq_chiusura_4 = 1;
                sequenza_Attiva.seq_apertura_1 = 1;
            }
            if (TextView_Close4.getText().equals("B")) {
                chiusura = chiusura + 2;
                apertura = apertura + 2000;
                sequenza_Attiva.seq_chiusura_4 = 2;
                sequenza_Attiva.seq_apertura_1 = 2;
            }
            if (TextView_Close4.getText().equals("C")) {
                chiusura = chiusura + 3;
                apertura = apertura + 3000;
                sequenza_Attiva.seq_chiusura_4 = 3;
                sequenza_Attiva.seq_apertura_1 = 3;
            }
            if (TextView_Close4.getText().equals("D")) {
                chiusura = chiusura + 4;
                apertura = apertura + 4000;
                sequenza_Attiva.seq_chiusura_4 = 4;
                sequenza_Attiva.seq_apertura_1 = 4;
            }
        }

        if (chiusura == 0) {
            chiusura = 1234;
            apertura = 4321;
        }

        if (sequenza_Attiva.seq_chiusura_1 == sequenza_Attiva.seq_chiusura_2 ||
                sequenza_Attiva.seq_chiusura_1 == sequenza_Attiva.seq_chiusura_3 ||
                sequenza_Attiva.seq_chiusura_1 == sequenza_Attiva.seq_chiusura_4 ||
                sequenza_Attiva.seq_chiusura_2 == sequenza_Attiva.seq_chiusura_3 ||
                sequenza_Attiva.seq_chiusura_2 == sequenza_Attiva.seq_chiusura_4 ||
                sequenza_Attiva.seq_chiusura_3 == sequenza_Attiva.seq_chiusura_4
        ) {
            Toast.makeText(this, "saving interrupted, sequence with double movements", Toast.LENGTH_SHORT).show();
        } else {
            Mci_write_Vn109_sequenza_chiusura_piegatore.valore = Double.valueOf(chiusura);
            Mci_write_Vn109_sequenza_chiusura_piegatore.write_flag = true;
            Mci_write_Vn110_sequenza_apertura_piegatore.valore = Double.valueOf(apertura);
            Mci_write_Vn110_sequenza_apertura_piegatore.write_flag = true;
            if (sequenza_Free.attiva) {
                Mci_write_Vn115_CopiaSeqPiegFree.valore = Double.valueOf(chiusura);
                Mci_write_Vn115_CopiaSeqPiegFree.write_flag = true;
            }

            ricetta.Udf_SequenzaPiegatore_chiusura = (int) Math.round(Mci_write_Vn109_sequenza_chiusura_piegatore.valore);
            ricetta.Udf_SequenzaPiegatore_apetura = (int) Math.round(Mci_write_Vn110_sequenza_apertura_piegatore.valore);
            ricetta.save(file_udf);
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

    private void KillThread() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver_from_KeyDialog);
        StopThread = true;

        try {
            if (!Thread_Running)
                thread_Sequenza_piegatura.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("JAM TAG", "End Seq Piegatura Thread");
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

    class MyAndroidThread_Seq_Pieg implements Runnable {
        Activity activity;

        public MyAndroidThread_Seq_Pieg(Activity activity) {
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
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                }

                if (sl.IsConnected()) {
                    sl.Clear();
                    sl.ReadItem(MultiCmd_tasto_verde);
                    sl.ReadItem(MultiCmd_CH1_in_emergenza);


                    if (read_daCN_tipo) {
                        sl.ReadItem(Multicmd_vn109);
                        sl.ReadItem(Multicmd_Vn115_CopiaSeqPiegFree);
                        Set_Tipo((Double) Multicmd_vn109.getValue());
                        aggiorna_grafica = true;
                        read_daCN_tipo = false;
                    }

                    if (read_daCN_tempi) {
                        Leggi_Tempi_da_CN();
                        read_daCN_tempi = false;
                    }

                    if (Mci_write_Vn109_sequenza_chiusura_piegatore.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_Vn109_sequenza_chiusura_piegatore);

                    }

                    if (Mci_write_Vn110_sequenza_apertura_piegatore.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_Vn110_sequenza_apertura_piegatore);

                    }

                    if (Mci_write_Vn115_CopiaSeqPiegFree.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_Vn115_CopiaSeqPiegFree);

                    }

                    // TODO i can reduce the size of this by creating an array and executing the code for every item
                    //tempi CN chiusura apertura
                    if (Mci_write_vq3006.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3006);
                        sl.ReadItem(Multicmd_vq3006);

                    }

                    if (Mci_write_vq3008.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3008);
                        sl.ReadItem(Multicmd_vq3008);

                    }

                    if (Mci_write_vq3010.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3010);
                        sl.ReadItem(Multicmd_vq3010);

                    }

                    if (Mci_write_vq3012.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3012);
                        sl.ReadItem(Multicmd_vq3012);
                    }

                    if (Mci_write_vq3007.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3007);
                        sl.ReadItem(Multicmd_vq3007);

                    }

                    if (Mci_write_vq3009.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3009);
                        sl.ReadItem(Multicmd_vq3009);

                    }

                    if (Mci_write_vq3011.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3011);
                        sl.ReadItem(Multicmd_vq3011);
                    }

                    if (Mci_write_vq3013.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3013);
                        sl.ReadItem(Multicmd_vq3013);
                    }

                    //tempi "store" standard
                    if (Mci_write_vq3106.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3106);
                        sl.ReadItem(Multicmd_Vq3106_RitPiegatore_1_ON);
                    }

                    if (Mci_write_vq3108.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3108);
                        sl.ReadItem(Multicmd_Vq3108_RitPiegatore_2_ON);
                    }

                    if (Mci_write_vq3110.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3110);
                        sl.ReadItem(Multicmd_Vq3110_RitPiegatore_3_ON);
                    }

                    if (Mci_write_vq3112.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3112);
                        sl.ReadItem(Multicmd_Vq3112_RitPiegatore_4_ON);
                    }

                    if (Mci_write_vq3107.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3107);
                        sl.ReadItem(Multicmd_Vq3107_RitPiegatore_1_OFF);
                    }

                    if (Mci_write_vq3109.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3109);
                        sl.ReadItem(Multicmd_Vq3109_RitPiegatore_2_OFF);
                    }

                    if (Mci_write_vq3111.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3111);
                        sl.ReadItem(Multicmd_Vq3111_RitPiegatore_3_OFF);
                    }

                    if (Mci_write_vq3113.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3113);
                        sl.ReadItem(Multicmd_Vq3113_RitPiegatore_4_OFF);
                    }

                    //tempi "store" Curve3
                    if (Mci_write_vq3114.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3114);
                        sl.ReadItem(Multicmd_Vq3114_RitPiegatore_1_ON);
                    }

                    if (Mci_write_vq3116.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3116);
                        sl.ReadItem(Multicmd_Vq3116_RitPiegatore_2_ON);
                    }

                    if (Mci_write_vq3118.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3118);
                        sl.ReadItem(Multicmd_Vq3118_RitPiegatore_3_ON);
                    }

                    if (Mci_write_vq3120.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3120);
                        sl.ReadItem(Multicmd_Vq3120_RitPiegatore_4_ON);
                    }

                    if (Mci_write_vq3115.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3115);
                        sl.ReadItem(Multicmd_Vq3115_RitPiegatore_1_OFF);
                    }

                    if (Mci_write_vq3117.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3117);
                        sl.ReadItem(Multicmd_Vq3117_RitPiegatore_2_OFF);
                    }

                    if (Mci_write_vq3119.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3119);
                        sl.ReadItem(Multicmd_Vq3119_RitPiegatore_3_OFF);
                    }

                    if (Mci_write_vq3121.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3121);
                        sl.ReadItem(Multicmd_Vq3121_RitPiegatore_4_OFF);
                    }

                    //tempi "store" Square
                    if (Mci_write_vq3122.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3122);
                        sl.ReadItem(Multicmd_Vq3122_RitPiegatore_1_ON);
                    }

                    if (Mci_write_vq3124.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3124);
                        sl.ReadItem(Multicmd_Vq3124_RitPiegatore_2_ON);
                    }

                    if (Mci_write_vq3126.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3126);
                        sl.ReadItem(Multicmd_Vq3126_RitPiegatore_3_ON);
                    }

                    if (Mci_write_vq3128.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3128);
                        sl.ReadItem(Multicmd_Vq3128_RitPiegatore_4_ON);
                    }

                    if (Mci_write_vq3123.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3123);
                        sl.ReadItem(Multicmd_Vq3123_RitPiegatore_1_OFF);
                    }

                    if (Mci_write_vq3125.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3125);
                        sl.ReadItem(Multicmd_Vq3125_RitPiegatore_2_OFF);
                    }

                    if (Mci_write_vq3127.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3127);
                        sl.ReadItem(Multicmd_Vq3127_RitPiegatore_3_OFF);
                    }

                    if (Mci_write_vq3129.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3129);
                        sl.ReadItem(Multicmd_Vq3129_RitPiegatore_4_OFF);
                    }

                    //tempi "store" free
                    if (Mci_write_vq3130.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3130);
                        sl.ReadItem(Multicmd_Vq3130_RitPiegatore_1_ON);
                    }

                    if (Mci_write_vq3132.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3132);
                        sl.ReadItem(Multicmd_Vq3132_RitPiegatore_2_ON);
                    }

                    if (Mci_write_vq3134.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3134);
                        sl.ReadItem(Multicmd_Vq3134_RitPiegatore_3_ON);
                    }

                    if (Mci_write_vq3136.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3136);
                        sl.ReadItem(Multicmd_Vq3136_RitPiegatore_4_ON);
                    }

                    if (Mci_write_vq3131.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3131);
                        sl.ReadItem(Multicmd_Vq3131_RitPiegatore_1_OFF);
                    }

                    if (Mci_write_vq3133.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3133);
                        sl.ReadItem(Multicmd_Vq3133_RitPiegatore_2_OFF);
                    }

                    if (Mci_write_vq3135.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3135);
                        sl.ReadItem(Multicmd_Vq3135_RitPiegatore_3_OFF);
                    }

                    if (Mci_write_vq3137.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3137);
                        sl.ReadItem(Multicmd_Vq3137_RitPiegatore_4_OFF);
                    }

                    //tempi "store" Curve5
                    if (Mci_write_vq3138.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3138);
                        sl.ReadItem(Multicmd_Vq3138_RitPiegatore_1_ON);
                    }

                    if (Mci_write_vq3140.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3140);
                        sl.ReadItem(Multicmd_Vq3140_RitPiegatore_2_ON);
                    }

                    if (Mci_write_vq3142.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3142);
                        sl.ReadItem(Multicmd_Vq3142_RitPiegatore_3_ON);
                    }

                    if (Mci_write_vq3144.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3144);
                        sl.ReadItem(Multicmd_Vq3144_RitPiegatore_4_ON);
                    }

                    if (Mci_write_vq3139.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3139);
                        sl.ReadItem(Multicmd_Vq3139_RitPiegatore_1_OFF);
                    }

                    if (Mci_write_vq3141.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3141);
                        sl.ReadItem(Multicmd_Vq3141_RitPiegatore_2_OFF);
                    }

                    if (Mci_write_vq3143.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3143);
                        sl.ReadItem(Multicmd_Vq3143_RitPiegatore_3_OFF);
                    }

                    if (Mci_write_vq3145.write_flag) {
                        Utility.ScrivoVbVnVq(sl, Mci_write_vq3145);
                        sl.ReadItem(Multicmd_Vq3145_RitPiegatore_4_OFF);
                    }
                    if(write_data_toCn)
                        Exit();

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Emergenza(activity);
                            if (aggiorna_grafica) {
                                AggiornaGrafica_ABCD(GetSequenza_Attiva());
                                AggiornaGrafica_Tempi_chiusura_apertura(GetSequenza_Attiva());
                                aggiorna_grafica = false;
                            }

                        }


                    });
                } else
                    sl.Connect();
            }
        }
    }

    /**
     * Receiver for handle the textview values
     */
    private final BroadcastReceiver mMessageReceiver_from_KeyDialog = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                // TODO Why unregister the receiver?
                LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver_from_KeyDialog);
                String val = intent.getStringExtra("ret_valore");
                Double id_var = Double.parseDouble(val);

                String getid_textview = intent.getStringExtra("txtview_id");
                Integer id_texview = Integer.parseInt(getid_textview);

                Sequenza sequenza_attiva = GetSequenza_Attiva();

                if (id_texview == TextView_TimeAfterClose1.getId()) {
                    sequenza_attiva.time_close_1 = id_var;

                }
                if (id_texview == TextView_TimeAfterClose2.getId()) {
                    sequenza_attiva.time_close_2 = id_var;

                }
                if (id_texview == TextView_TimeAfterClose3.getId()) {
                    sequenza_attiva.time_close_3 = id_var;


                }
                if (id_texview == TextView_TimeAfterClose4.getId()) {
                    sequenza_attiva.time_close_4 = id_var;


                }

                if (id_texview == TextView_TimeAfterOpen4_vq3013.getId()) {
                    sequenza_attiva.time_open_4_vq3013 = id_var;

                }
                if (id_texview == TextView_TimeAfterOpen3_vq3011.getId()) {
                    sequenza_attiva.time_open_3_vq3011 = id_var;

                }
                if (id_texview == TextView_TimeAfterOpen2_vq3009.getId()) {
                    sequenza_attiva.time_open_2_vq3009 = id_var;

                }
                if (id_texview == TextView_TimeAfterOpen1_vq3007.getId()) {
                    sequenza_attiva.time_open_1_vq3007 = id_var;


                }

                dati_cambiati = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public class Sequenza {
        String tipo = "";
        boolean attiva = false;
        int seq_chiusura_1 = 0;
        int seq_chiusura_2 = 0;
        int seq_chiusura_3 = 0;
        int seq_chiusura_4 = 0;

        int seq_apertura_1 = 0;
        int seq_apertura_2 = 0;
        int seq_apertura_3 = 0;
        int seq_apertura_4 = 0;

        Double time_close_1 = 0.0d;
        Double time_close_2 = 0.0d;
        Double time_close_3 = 0.0d;
        Double time_close_4 = 0.0d;

        Double time_open_4_vq3013 = 0.0d;
        Double time_open_3_vq3011 = 0.0d;
        Double time_open_2_vq3009 = 0.0d;
        Double time_open_1_vq3007 = 0.0d;


        public Sequenza(String Tipo, boolean Attiva, int Seq_chiusura_1, int Seq_chiusura_2, int Seq_chiusura_3, int Seq_chiusura_4, int Seq_apertura_1, int Seq_apertura_2,
                        int Seq_apertura_3, int Seq_apertura_4,
                        Double Time_close_1, Double Time_close_2, Double Time_close_3, Double Time_close_4,
                        Double Time_open_4_vq3013, Double Time_open_3_vq3011, Double Time_open_2_vq3009, Double Time_open_1_vq3007) {

            this.tipo = Tipo;
            this.attiva = Attiva;
            this.seq_chiusura_1 = Seq_chiusura_1;
            this.seq_chiusura_2 = Seq_chiusura_2;
            this.seq_chiusura_3 = Seq_chiusura_3;
            this.seq_chiusura_4 = Seq_chiusura_4;


            this.seq_apertura_1 = Seq_apertura_1;
            this.seq_apertura_2 = Seq_apertura_2;
            this.seq_apertura_3 = Seq_apertura_3;
            this.seq_apertura_4 = Seq_apertura_4;


            this.time_close_1 = (Double) Time_close_1;
            this.time_close_2 = Time_close_2;
            this.time_close_3 = Time_close_3;
            this.time_close_4 = Time_close_4;

            this.time_open_4_vq3013 = Time_open_4_vq3013;
            this.time_open_3_vq3011 = Time_open_3_vq3011;
            this.time_open_2_vq3009 = Time_open_2_vq3009;
            this.time_open_1_vq3007 = Time_open_1_vq3007;
        }
    }


}
