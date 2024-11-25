package com.jam_int.jt882_m8;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
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

public class Page_Test_IO extends Activity {
    /**
     * Table layouts
     */
    TableLayout stk_out;
    TableLayout stk_out_magnet;
    TableLayout stk_input;

    /**
     * Buttons on every table layout
     */
    ArrayList<Button> Lista_button_out = new ArrayList<Button>();
    ArrayList<Button> Lista_button_input = new ArrayList<Button>();
    ArrayList<Button> Lista_button_out_magneti = new ArrayList<Button>();

    /**
     * Shopping list for communicate with PLC
     */
    ShoppingList sl;

    int numero_out=64;
    int numero_in = 64;
    int[] vbOutList;

    /**
     * Thread
     */
    Thread t1;

    Double[] Stato_uscite;
    Double[] Stato_ingressi = new Double[numero_in];
    Boolean out_Background_Pausa = false,macchina_armata = false;
    Integer[] Elenco_Vb_output = new Integer[numero_out];


  //  TextView TextView_rotturaFilo_C1;


    private final View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            out_Background_Pausa = true; //metto in pausa la lettura dello stato delle uscite

            Button b = (Button) v;
            int button_id = b.getId();
            ColorDrawable buttonColor = (ColorDrawable) b.getBackground();
            int colorId = buttonColor.getColor();
            int id_vb_out = Elenco_Vb_output[button_id-1];

            if (colorId != Color.RED) {
                MultiCmdItem mci = sl.Add("Io", 1, MultiCmdItem.dtVB, id_vb_out, MultiCmdItem.dpNONE);
                Mci_output = new Mci_write();
                Mci_output.mci = mci;
                Mci_output.write_flag = true;
                Mci_output.valore = 1.0d;
            } else {
                MultiCmdItem mci = sl.Add("Io", 1, MultiCmdItem.dtVB, id_vb_out, MultiCmdItem.dpNONE);
                Mci_output = new Mci_write();
                Mci_output.mci = mci;
                Mci_output.write_flag = true;
                Mci_output.valore = 0.0d;
            }
        }
    };
    private final View.OnClickListener buttonClickListener_magnet = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            out_Background_Pausa = true; //metto in pausa la lettura dello stato delle uscite

            Button b = (Button) v;
            int button_id = b.getId();
            ColorDrawable buttonColor = (ColorDrawable) b.getBackground();
            int colorId = buttonColor.getColor();
            if (colorId != Color.RED) {
                switch (button_id) {
                    case 1:
                        Mci_Vb4208_AppMagneteTensione.valore = 1.0d;
                        Mci_Vb4208_AppMagneteTensione.write_flag = true;
                        break;
                    case 2:
                        Mci_Vb4209_AppMagneteTensioneT2.valore = 1.0d;
                        Mci_Vb4209_AppMagneteTensioneT2.write_flag = true;
                        break;
                    case 3:
                        Mci_Vb4211_AppMagneteSottoPiegatore.valore = 1.0d;
                        Mci_Vb4211_AppMagneteSottoPiegatore.write_flag = true;
                        break;
                    case 4:
                        Mci_Vb4210_AppMagneteSopraPiegatore.valore = 1.0d;
                        Mci_Vb4210_AppMagneteSopraPiegatore.write_flag = true;
                        break;
                }
            } else {
                switch (button_id) {

                    case 1:
                        Mci_Vb4208_AppMagneteTensione.valore = 0.0d;
                        Mci_Vb4208_AppMagneteTensione.write_flag = true;
                        break;
                    case 2:
                        Mci_Vb4209_AppMagneteTensioneT2.valore = 0.0d;
                        Mci_Vb4209_AppMagneteTensioneT2.write_flag = true;
                        break;
                    case 3:
                        Mci_Vb4211_AppMagneteSottoPiegatore.valore = 0.0d;
                        Mci_Vb4211_AppMagneteSottoPiegatore.write_flag = true;
                        break;
                    case 4:
                        Mci_Vb4210_AppMagneteSopraPiegatore.valore = 0.0d;
                        Mci_Vb4210_AppMagneteSopraPiegatore.write_flag = true;
                        break;
                }
            }
        }
    };
    Boolean Thread_Running = false, StopThread = false, Debug_mode = false, first_cycle = true;
    /**
     * PLC vars
     */
    MultiCmdItem[] multi_out, multi_in, mci_array_read_all;
    MultiCmdItem MultiCmd_tasto_verde, MultiCmd_CH1_in_emergenza, MultiCmd_Vb312_AzzOK, MultiCmd_Vb88, MultiCmd_Vb165, MultiCmd_Vb170, MultiCmd_Vb363_asse_elettrico,
            MultiCmd_Vb323_PID_crochet, MultiCmd_Vn3804_pagina_touch, MultiCmd_Vb4208_AppMagneteTensione, MultiCmd_Vb4209_AppMagneteTensioneT2, MultiCmd_Vb4210_AppMagneteSopraPiegatore,
            MultiCmd_Vb4211_AppMagneteSottoPiegatore, Multicmd_Vq3561_ContPuntiRottFiloC1,Multicmd_Vq3563_ContPuntiRottFiloC2,Multicmd_Vq3160_ContSensPiegatoreSu;
    Mci_write Mci_write_Vb165 = new Mci_write(),
            Mci_write_Vb170 = new Mci_write(),
            Mci_output = new Mci_write(),
            Mci_Vb4208_AppMagneteTensione = new Mci_write(),
            Mci_Vb4209_AppMagneteTensioneT2 = new Mci_write(),
            Mci_Vb4210_AppMagneteSopraPiegatore = new Mci_write(),
            Mci_Vb4211_AppMagneteSottoPiegatore = new Mci_write();
    Integer Timeout_emergenza = 5, timeout_counter;
    String chiamante = "",Machine_model;
    Handler HandlerDialog = new Handler();
    private final BroadcastReceiver MessagePasswordReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
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
                CallHandlerDialog();
            } else if (val.equals("")) {
            } else {
                Toast.makeText(getApplicationContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_io);

        try {
            Machine_model = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "MachineModel", "null", null, null, "Machine_model", getApplicationContext());
        } catch (IOException e) {
            Machine_model="";
        }

        if( Machine_model.equals("JT882M") || Machine_model.equals("JT882MA")) {
            numero_in = 64;
        }
        else {
            numero_in = 32;
        }

        if( Machine_model.equals("JT882M"))
            Elenco_Vb_output = new Integer[]{4133,4134,4135,4136,4137,4138,4139,4140,4141,4142,
                                             4143,4144,4145,4146,4147,4148,4149,4150,4151,4152,
                                             4153,4154,4155,4156,4157,4158,4159,4160,4161,4162,
                                             4163,4164,4165,4181,4182,4185,4186,4188,4189,4190,
                                             4187,4192,4193,4176,4177,4178,4195,4180,4166,4167,
                                             4183,4184,4170,4171,4173,4174,4175,4168,4169,4170,
                                             4172,4179,4191,4194


            };
        if( Machine_model.equals("JT882MA"))
            Elenco_Vb_output = new Integer[]{4133,4134,4135,4136,4137,4138,4139,4140,4141,4142,
                                            4143,4144,4145,4146,4147,4148,4149,4150,4151,4152,
                                            4153,4154,4155,4156,4157,4158,4159,4160,4161,4162,
                                            4163,4164,4165,4166,4167,4168,4169,4170,4171,4172,
                                            4173,4174,4175,4176,4177,4178,4179,4180,4181,4182,
                                            4183,4184,4185,4186,4187,4188,4189,4190,4191,4192,
                                            4193,4194,4195,4196


            };

        try {
            Bundle extras = getIntent().getExtras();
            chiamante = extras.getString("chiamante");
        } catch (Exception e) {
            e.printStackTrace();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(MessagePasswordReceiver, new IntentFilter("KeyDialog_password_ret"));

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //non fa apparire la tastiera

        stk_out = findViewById(R.id.TabletLayout_out);
        stk_input = findViewById(R.id.TabletLayout_in);
        stk_out_magnet = findViewById(R.id.TabletLayout_outMagnet);

        ScrollView ScrollBar_output = findViewById(R.id.scrollView_out);
        ScrollView ScrollBar_input = findViewById(R.id.scrollView_in);

        ScrollBar_output.setScrollbarFadingEnabled(false);    //faccio sempre vedere lo scrollbar verticale
        ScrollBar_input.setScrollbarFadingEnabled(false);    //faccio sempre vedere lo scrollbar verticale

        TextView TextView_info = findViewById(R.id.textView_info);
        TextView TextView_output = findViewById(R.id.textView_output);
        //TextView_rotturaFilo_C1 = findViewById(R.id.textView_rotturaFilo_C1);

        TextView_info.setText(R.string.Press_emergency);
        TextView_info.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        //se tengo premuto a lungo la scritta output faccio partire il keylog password per sganciare l'asse elettrico
        TextView_output.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                KeyDialog.Lancia_KeyDialogo(null, Page_Test_IO.this, null, 99999d, 0d, false, false, 0d, true, "KeyDialog_password_ret", false,"");
                return true;    // <- set to true
            }
        });

        // Setup ShoppingList
        if (sl != null) {
            sl.Clear("Io");
        } else {
            sl = SocketHandler.getSocket();
            sl.Clear("Io");
        }

        MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 21, MultiCmdItem.dpNONE);
        MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
        MultiCmd_Vb312_AzzOK = sl.Add("Io", 1, MultiCmdItem.dtVB, 312, MultiCmdItem.dpNONE);
        MultiCmd_Vb88 = sl.Add("Io", 1, MultiCmdItem.dtVB, 88, MultiCmdItem.dpNONE);
        MultiCmd_Vb165 = sl.Add("Io", 1, MultiCmdItem.dtVB, 165, MultiCmdItem.dpNONE);
        MultiCmd_Vb170 = sl.Add("Io", 1, MultiCmdItem.dtVB, 170, MultiCmdItem.dpNONE);
        MultiCmd_Vb363_asse_elettrico = sl.Add("Io", 1, MultiCmdItem.dtVB, 363, MultiCmdItem.dpNONE);
        MultiCmd_Vb323_PID_crochet = sl.Add("Io", 1, MultiCmdItem.dtVB, 323, MultiCmdItem.dpNONE);
        MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);

        MultiCmd_Vb4208_AppMagneteTensione = sl.Add("Io", 1, MultiCmdItem.dtVB, 4208, MultiCmdItem.dpNONE);
        MultiCmd_Vb4209_AppMagneteTensioneT2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4209, MultiCmdItem.dpNONE);
        MultiCmd_Vb4210_AppMagneteSopraPiegatore = sl.Add("Io", 1, MultiCmdItem.dtVB, 4210, MultiCmdItem.dpNONE);
        MultiCmd_Vb4211_AppMagneteSottoPiegatore = sl.Add("Io", 1, MultiCmdItem.dtVB, 4211, MultiCmdItem.dpNONE);
        Multicmd_Vq3561_ContPuntiRottFiloC1 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3561, MultiCmdItem.dpNONE);
        Multicmd_Vq3563_ContPuntiRottFiloC2  = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3563, MultiCmdItem.dpNONE);
        Multicmd_Vq3160_ContSensPiegatoreSu = sl.Add("Io", 1, MultiCmdItem.dtVQ, 3160, MultiCmdItem.dpNONE);

        mci_array_read_all = new MultiCmdItem[]{MultiCmd_tasto_verde, MultiCmd_CH1_in_emergenza, MultiCmd_Vb312_AzzOK, MultiCmd_Vb88, MultiCmd_Vb4208_AppMagneteTensione, MultiCmd_Vb4209_AppMagneteTensioneT2, MultiCmd_Vb4210_AppMagneteSopraPiegatore,
                MultiCmd_Vb4211_AppMagneteSottoPiegatore,Multicmd_Vq3561_ContPuntiRottFiloC1,Multicmd_Vq3563_ContPuntiRottFiloC2,Multicmd_Vq3160_ContSensPiegatoreSu};



        /*
        if(Machine_model.equals("JT882M")){
            vbOutList = new int[]{4133, 4134, 4135, 4136, 4137, 4138, 4139, 4140, 4141, 4142, 4143,
                    4144, 4145, 4146, 4147, 4148, 4149, 4150, 4151, 4152, 4153, 4154,
                    4155, 4156, 4157, 4158, 4159, 4160, 4161, 4162, 4163, 4164,
                    4165,4166,4167,4168,4169,4170,4171,4172,4173,4174,4175,4176,4177,4178,4179,
                    4180,4181,4182,4183,4184,4185,4186,4187,4188,4189,4190,4191,4192,4193,4194,4195,4196};
        }
        else{
            vbOutList = new int[]{4133, 4134, 4135, 4136, 4137, 4138, 4139, 4140, 4141, 4142, 4143,
                    4144, 4145, 4146, 4147, 4148, 4149, 4150, 4151, 4152, 4153, 4154,
                    4155, 4156, 4157, 4158, 4159, 4160, 4161, 4162, 4163, 4164};
        }

         */
       // multi_out = new MultiCmdItem[vbOutList.length];
      //  numero_out = vbOutList.length;
        multi_out = new MultiCmdItem[numero_out];
        Stato_uscite = new Double[numero_out];
        Stato_ingressi = new Double[numero_in];
        for (int i = 0; i < (numero_out); i = i + 1) {
            MultiCmdItem mci_stato_out = sl.Add("Io", 1, MultiCmdItem.dtVB, i+1+4133, MultiCmdItem.dpNONE);
            multi_out[i] = mci_stato_out;
        }
        try {
            init_Out(multi_out.length);
            init_Input(numero_in);
            init_Magnet(4);

        } catch (IOException e) {
            e.printStackTrace();
        }

        multi_in = new MultiCmdItem[numero_in];
        for (int i = 0; i < numero_in; i = i + 1) {
            MultiCmdItem mci_stato_input = sl.Add("Io", 1, MultiCmdItem.dtDI, i + 1, MultiCmdItem.dpNONE);
            multi_in[i] = mci_stato_input;
        }

        Mci_write_Vb165.mci = MultiCmd_Vb165;
        Mci_write_Vb165.write_flag = false;

        Mci_write_Vb170.mci = MultiCmd_Vb170;
        Mci_write_Vb170.write_flag = false;

        MultiCmdItem mci = sl.Add("Io", 1, MultiCmdItem.dtDO, 1, MultiCmdItem.dpNONE);    //instanzio una generica
        Mci_output.mci = mci;
        Mci_output.write_flag = false;    //instanzio una generica

        Mci_Vb4208_AppMagneteTensione.mci = MultiCmd_Vb4208_AppMagneteTensione;
        Mci_Vb4208_AppMagneteTensione.write_flag = false;

        Mci_Vb4209_AppMagneteTensioneT2.mci = MultiCmd_Vb4209_AppMagneteTensioneT2;
        Mci_Vb4209_AppMagneteTensioneT2.write_flag = false;

        Mci_Vb4210_AppMagneteSopraPiegatore.mci = MultiCmd_Vb4210_AppMagneteSopraPiegatore;
        Mci_Vb4210_AppMagneteSopraPiegatore.write_flag = false;

        Mci_Vb4211_AppMagneteSottoPiegatore.mci = MultiCmd_Vb4211_AppMagneteSottoPiegatore;
        Mci_Vb4211_AppMagneteSottoPiegatore.write_flag = false;

        // Start thread
        if (!Thread_Running) {
            StopThread = false;
            MyAndroidThread_TestIO myTask = new MyAndroidThread_TestIO(Page_Test_IO.this);
            t1 = new Thread(myTask, "Main myTask");
            t1.start();
        }
    }

    //#region ActivityStatus
    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
        KillThread();
    }

    @Override                   //your activity is no longer visible to the user
    public void onStop() {
        super.onStop();
    }

    //#endregion ActivityStatus

    @Override                   //your activity is no longer visible to the user
    public void onDestroy() {
        super.onDestroy();
        if (Thread_Running)
            KillThread();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        KillThread();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init_Magnet(int numero_out_magneti) {
        String[] Descrizioni = new String[]{};

        switch (Values.Machine_model) {
            case "JT882M":
            case "JT882MA":
                Descrizioni = getResources().getStringArray(R.array.output_magneti_JT882M);
                break;
            case "JT862M":
            case "JT862HM":


                break;
            default:
                break;
        }

        for (int i = 1; i <= numero_out_magneti; i = i + 3) {
            TableRow tbrow = new TableRow(this);        //creo il tableRow
            tbrow.setPadding(0, 0, 0, 7);    // spazio tra una riga e l'altra del tableRow

            TableRow.LayoutParams p = new TableRow.LayoutParams();
            p.rightMargin = 20; // imposta lo spazio tra una colonna di button e l'altra

            //Prima colonna
            Button btnTag = new Button(this);
            btnTag.setLayoutParams(p);
            btnTag.setBackgroundColor(Color.GREEN);
            btnTag.setGravity(Gravity.LEFT);
            //btnTag.setText("Output" + i);
            btnTag.setText("Out" + ": " + Descrizioni[i]);

            btnTag.setId(i);
            btnTag.setWidth(150);        //larghezza button
            btnTag.setHeight(15);        //altezza button
            btnTag.setPadding(15, 5, 0, 0);    //posizione scritta "outputxx" all'interno del button
            btnTag.setTextSize(10);
            btnTag.setOnClickListener(buttonClickListener_magnet);

            Lista_button_out_magneti.add(btnTag);

            tbrow.addView(btnTag);     //aggiungo la riga al TableRow

            //Seconda colonna
            if (i + 1 <= numero_out_magneti) {
                Button btnTag1 = new Button(this);
                btnTag1.setLayoutParams(p);
                btnTag1.setBackgroundColor(Color.GREEN);
                btnTag1.setGravity(Gravity.LEFT);
                //btnTag1.setText("Output" + (i + 1));
                btnTag1.setText("Out" + ": " + Descrizioni[i + 1]);
                btnTag1.setId(i + 1);
                btnTag1.setWidth(150);
                btnTag1.setHeight(15);
                btnTag1.setPadding(15, 5, 0, 0);
                btnTag1.setTextSize(10);
                btnTag1.setOnClickListener(buttonClickListener_magnet);
                Lista_button_out_magneti.add(btnTag1);

                tbrow.addView(btnTag1);    //aggiungo la riga al TableRow
            }
            //Terza colonna
            if (i + 2 <= numero_out_magneti) {
                Button btnTag2 = new Button(this);
                btnTag2.setLayoutParams(p);
                btnTag2.setBackgroundColor(Color.GREEN);
                btnTag2.setGravity(Gravity.LEFT);
                //btnTag2.setText("Output" + (i + 2));
                btnTag2.setText("Out" + ": " + Descrizioni[i + 2]);
                btnTag2.setId(i + 2);
                btnTag2.setWidth(150);
                btnTag2.setHeight(15);
                btnTag2.setPadding(15, 5, 0, 0);
                btnTag2.setTextSize(10);
                btnTag2.setOnClickListener(buttonClickListener_magnet);
                Lista_button_out_magneti.add(btnTag2);

                tbrow.addView(btnTag2);    //aggiungo la riga al TableRow
            }
            stk_out_magnet.addView(tbrow);    //aggiungo il tablerow al tableLayout
        }
    }

       private void init_Out(int numero_out) throws IOException {
        String[] Descrizioni = new String[]{};

        switch (Values.Machine_model) {
            case "JT882M":
                Descrizioni = getResources().getStringArray(R.array.output_JT882M_3SchedeIO);
                break;
            case "JT882MA":
                Descrizioni = getResources().getStringArray(R.array.output_JT882M_4SchedeIO);
                break;
            case "JT862M":
            case "JT862HM":

                break;
            default:
                Toast.makeText(getApplicationContext(), "Model machine error", Toast.LENGTH_SHORT).show();

                break;
        }

        for (int i = 1; i <= numero_out; i = i + 3) {
            TableRow tbrow = new TableRow(this);        //creo il tableRow
            tbrow.setPadding(0, 0, 0, 7);    // spazio tra una riga e l'altra del tableRow

            TableRow.LayoutParams p = new TableRow.LayoutParams();
            p.rightMargin = 20; // imposta lo spazio tra una colonna di button e l'altra

            //Prima colonna
            Button btnTag = new Button(this);
            btnTag.setLayoutParams(p);
            btnTag.setBackgroundColor(Color.GREEN);
            btnTag.setGravity(Gravity.LEFT);
            //btnTag.setText("Output" + i);
            btnTag.setText("Out" + ": " + Descrizioni[i]);

            btnTag.setId(i);
            btnTag.setWidth(150);        //larghezza button
            btnTag.setHeight(15);        //altezza button
            btnTag.setPadding(15, 5, 0, 0);    //posizione scritta "outputxx" all'interno del button
            btnTag.setTextSize(10);
            btnTag.setOnClickListener(buttonClickListener);

            Lista_button_out.add(btnTag);

            tbrow.addView(btnTag);     //aggiungo la riga al TableRow


            //Seconda colonna
            if (i + 1 <= numero_out) {
                Button btnTag1 = new Button(this);
                btnTag1.setLayoutParams(p);
                btnTag1.setBackgroundColor(Color.GREEN);
                btnTag1.setGravity(Gravity.LEFT);
                //btnTag1.setText("Output" + (i + 1));
                btnTag1.setText("Out" + ": " + Descrizioni[i + 1]);
                btnTag1.setId(i + 1);
                btnTag1.setWidth(150);
                btnTag1.setHeight(15);
                btnTag1.setPadding(15, 5, 0, 0);
                btnTag1.setTextSize(10);
                btnTag1.setOnClickListener(buttonClickListener);
                Lista_button_out.add(btnTag1);

                tbrow.addView(btnTag1);    //aggiungo la riga al TableRow
            }
            //Terza colonna
            if (i + 2 <= numero_out) {
                Button btnTag2 = new Button(this);
                btnTag2.setLayoutParams(p);
                btnTag2.setBackgroundColor(Color.GREEN);
                btnTag2.setGravity(Gravity.LEFT);
                //btnTag2.setText("Output" + (i + 2));
                btnTag2.setText("Out" + ": " + Descrizioni[i + 2]);
                btnTag2.setId(i + 2);
                btnTag2.setWidth(150);
                btnTag2.setHeight(15);
                btnTag2.setPadding(15, 5, 0, 0);
                btnTag2.setTextSize(10);
                btnTag2.setOnClickListener(buttonClickListener);
                Lista_button_out.add(btnTag2);

                tbrow.addView(btnTag2);    //aggiungo la riga al TableRow

            }
            stk_out.addView(tbrow);    //aggiungo il tablerow al tableLayout
        }
    }

    private void init_Input(int numero_in) {
        String[] Descrizioni = new String[]{};
        switch (Values.Machine_model) {
            case "JT882M":
                Descrizioni = getResources().getStringArray(R.array.input_JT882M_3SchedeIO);
                break;
            case "JT882MA":
                Descrizioni = getResources().getStringArray(R.array.input_JT882M_4SchedeIO);
                break;
            case "JT862M":
            case "JT862HM":
                //Descrizioni = getResources().getStringArray(R.array.input_JT862M);
                break;
            default:
                break;
        }

        for (int i = 1; i <= numero_in; i = i + 3) {
            TableRow tbrow = new TableRow(this);        //creo il tableRow
            tbrow.setPadding(0, 0, 0, 7);    // spazio tra una riga e l'altra del tableRow

            TableRow.LayoutParams p = new TableRow.LayoutParams();
            p.rightMargin = 20; // imposta lo spazio tra una colonna text e button e l'altra

            //Prima colonna
            Button btnTag = new Button(this);
            btnTag.setLayoutParams(p);
            btnTag.setBackgroundColor(Color.GRAY);
            btnTag.setGravity(Gravity.LEFT);
            btnTag.setText("In" + ": " + Descrizioni[i]);

            btnTag.setId(i);
            btnTag.setWidth(150);        //larghezza button
            btnTag.setHeight(15);        //altezza button
            btnTag.setPadding(15, 5, 0, 0);    //posizione scritta "outputxx" all'interno del button
            btnTag.setTextSize(10);
            Lista_button_input.add(btnTag);

            tbrow.addView(btnTag);     //aggiungo la riga al TableRow

            //Seconda colonna
            if (i + 1 <= numero_in) {
                Button btnTag1 = new Button(this);
                btnTag1.setLayoutParams(p);
                btnTag1.setBackgroundColor(Color.GRAY);
                btnTag1.setGravity(Gravity.LEFT);
                btnTag1.setText("In" + ": " + Descrizioni[i + 1]);
                btnTag1.setId(i + 1);
                btnTag1.setWidth(150);
                btnTag1.setHeight(15);
                btnTag1.setPadding(15, 5, 0, 0);
                btnTag1.setTextSize(10);
                Lista_button_input.add(btnTag1);

                tbrow.addView(btnTag1);    //aggiungo la riga al TableRow
            }
            //Terza colonna
            if (i + 2 <= numero_in) {
                Button btnTag2 = new Button(this);
                btnTag2.setLayoutParams(p);
                btnTag2.setBackgroundColor(Color.GRAY);
                btnTag2.setGravity(Gravity.LEFT);
                btnTag2.setText("In" + ": " + Descrizioni[i + 2]);
                btnTag2.setId(i + 2);
                btnTag2.setWidth(150);
                btnTag2.setHeight(15);
                btnTag2.setPadding(15, 5, 0, 0);
                btnTag2.setTextSize(10);
                Lista_button_input.add(btnTag2);

                tbrow.addView(btnTag2);    //aggiungo la riga al TableRow
            }

            stk_input.addView(tbrow);
        }
    }

    private void CallHandlerDialog() {
        HandlerDialog.post(new Runnable() {
            @Override
            public void run() {
                //if(dialog_restart){
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());

                // Setting Dialog Title
                alertDialog.setTitle("Restart machine");
                alertDialog.setMessage("Gauntry axis are off, restart machine!");
                alertDialog.show();
            }
        });
    }

    /**
     * Function for handle the emergency event
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

    private void KillThread() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(MessagePasswordReceiver);
        StopThread = true;
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class MyAndroidThread_TestIO implements Runnable {
        Activity activity;

        public MyAndroidThread_TestIO(Activity activity) {
            this.activity = activity;
            timeout_counter = Timeout_emergenza;
        }

        @Override
        public void run() {
            while (true) {
                Thread_Running = true;
                try {
                    Thread.sleep((long) 10d);
                    if (StopThread) {
                        Thread_Running = false;
                        MultiCmd_Vn3804_pagina_touch.setValue(0.0d);
                        sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "MyAndroidThread_TestIO catch", Toast.LENGTH_SHORT).show();
                }

                if (sl.IsConnected()) {
                    sl.ReadItems(mci_array_read_all);

                    if (first_cycle) {
                        first_cycle = false;

                        if ((Double) MultiCmd_tasto_verde.getValue() == 1.0d || (Double) MultiCmd_CH1_in_emergenza.getValue() == 0.0d) {
                            macchina_armata = true;
                        }


                    }

                    MultiCmd_Vn3804_pagina_touch.setValue(1005.0d);
                    sl.WriteItem(MultiCmd_Vn3804_pagina_touch);

                    if (!out_Background_Pausa) {
                        sl.WriteQueued();

                        if (sl.getReturnCode() == 0) {
                            for (int i = 0; i < numero_out; i = i + 1) {
                                int n_out = Elenco_Vb_output[i];
                                MultiCmdItem elem = sl.Add("Io", 1, MultiCmdItem.dtVB, n_out, MultiCmdItem.dpNONE);
                                sl.ReadItem(elem);
                                Double stato = (Double) elem.getValue();
                                Stato_uscite[i] = stato;
                            }
                        }
                        sl.ReadItems(multi_in);
                        if (sl.getReturnCode() == 0) {
                            for (int i = 0; i < numero_in; i = i + 1) {
                                MultiCmdItem elem = multi_in[i];
                                Double stato_input = (Double) elem.getValue();
                                Stato_ingressi[i] = stato_input;
                            }
                        }
                    }

                    if (Mci_write_Vb165.write_flag == true) {
                        Mci_write_Vb165.mci.setValue(Mci_write_Vb165.valore);
                        sl.WriteItem(Mci_write_Vb165.mci);
                        Mci_write_Vb165.write_flag = false;
                    }

                    if (Mci_write_Vb170.write_flag == true) {
                        Mci_write_Vb170.mci.setValue(Mci_write_Vb170.valore);
                        sl.WriteItem(Mci_write_Vb170.mci);
                        Mci_write_Vb170.write_flag = false;
                    }

                    if (Mci_output.write_flag == true) {
                        Mci_output.mci.setValue(Mci_output.valore);
                        sl.WriteItem(Mci_output.mci);
                        Mci_output.write_flag = false;
                        out_Background_Pausa = false; //tolgo la pausa la lettura dello stato delle uscite
                    }

                    if (Mci_Vb4208_AppMagneteTensione.write_flag == true) {
                        Mci_Vb4208_AppMagneteTensione.mci.setValue(Mci_Vb4208_AppMagneteTensione.valore);
                        sl.WriteItem(Mci_Vb4208_AppMagneteTensione.mci);
                        Mci_Vb4208_AppMagneteTensione.write_flag = false;
                        out_Background_Pausa = false; //tolgo la pausa la lettura dello stato delle uscite
                    }

                    if (Mci_Vb4209_AppMagneteTensioneT2.write_flag == true) {
                        Mci_Vb4209_AppMagneteTensioneT2.mci.setValue(Mci_Vb4209_AppMagneteTensioneT2.valore);
                        sl.WriteItem(Mci_Vb4209_AppMagneteTensioneT2.mci);
                        Mci_Vb4209_AppMagneteTensioneT2.write_flag = false;
                        out_Background_Pausa = false; //tolgo la pausa la lettura dello stato delle uscite
                    }

                    if (Mci_Vb4210_AppMagneteSopraPiegatore.write_flag == true) {
                        Mci_Vb4210_AppMagneteSopraPiegatore.mci.setValue(Mci_Vb4210_AppMagneteSopraPiegatore.valore);
                        sl.WriteItem(Mci_Vb4210_AppMagneteSopraPiegatore.mci);
                        Mci_Vb4210_AppMagneteSopraPiegatore.write_flag = false;
                        out_Background_Pausa = false; //tolgo la pausa la lettura dello stato delle uscite
                    }

                    if (Mci_Vb4211_AppMagneteSottoPiegatore.write_flag == true) {
                        Mci_Vb4211_AppMagneteSottoPiegatore.mci.setValue(Mci_Vb4211_AppMagneteSottoPiegatore.valore);
                        sl.WriteItem(Mci_Vb4211_AppMagneteSottoPiegatore.mci);
                        Mci_Vb4211_AppMagneteSottoPiegatore.write_flag = false;
                        out_Background_Pausa = false; //tolgo la pausa la lettura dello stato delle uscite
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Check if i need to display the connection problem or not
                            if (macchina_armata)
                                Emergenza();
                            else{

                                TextView TextView_info = findViewById(R.id.textView_info);
                                TextView_info.setVisibility(View.GONE);
                            }



                            for (int i = 0; i < Lista_button_input.size(); i = i + 1) {
                                Button button_stao_input;
                                button_stao_input = Lista_button_input.get(i);

                                try {
                                    if (Stato_ingressi[i] == 1) {
                                        button_stao_input.setBackgroundColor(Color.RED);
                                    } else {
                                        button_stao_input.setBackgroundColor(Color.GRAY);
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                            }

                            //output
                            for (int i = 0; i < Lista_button_out.size(); i = i + 1) {
                                Button button_out;
                                button_out = Lista_button_out.get(i);

                                try {
                                    if (Stato_uscite[i] == 1) {
                                        button_out.setBackgroundColor(Color.RED);
                                    } else {
                                        button_out.setBackgroundColor(Color.GREEN);
                                    }
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                            }

                            //magneti
                            if ((Double) MultiCmd_Vb4208_AppMagneteTensione.getValue() == 1.0d)
                                Lista_button_out_magneti.get(0).setBackgroundColor(Color.RED);
                            else Lista_button_out_magneti.get(0).setBackgroundColor(Color.GREEN);
                            if ((Double) MultiCmd_Vb4209_AppMagneteTensioneT2.getValue() == 1.0d)
                                Lista_button_out_magneti.get(1).setBackgroundColor(Color.RED);
                            else Lista_button_out_magneti.get(1).setBackgroundColor(Color.GREEN);
                            if ((Double) MultiCmd_Vb4211_AppMagneteSottoPiegatore.getValue() == 1.0d)
                                Lista_button_out_magneti.get(2).setBackgroundColor(Color.RED);
                            else Lista_button_out_magneti.get(2).setBackgroundColor(Color.GREEN);
                            if ((Double) MultiCmd_Vb4210_AppMagneteSopraPiegatore.getValue() == 1.0d)
                                Lista_button_out_magneti.get(3).setBackgroundColor(Color.RED);
                            else Lista_button_out_magneti.get(3).setBackgroundColor(Color.GREEN);

                            //IRQ1 rottura filoC1
                            double Cnt_rotturafilo_C1 = (double)Multicmd_Vq3561_ContPuntiRottFiloC1.getValue();
                            TextView TextView_IRQ1 = findViewById(R.id.textView_IRQ1);
                            TextView_IRQ1.setText("IRQ1: "+Cnt_rotturafilo_C1);
                            //IRQ2 rottura filoC2
                            double Cnt_rotturafilo_C2 = (double)Multicmd_Vq3563_ContPuntiRottFiloC2.getValue();
                            TextView TextView_IRQ2 = findViewById(R.id.textView_IRQ2);
                            TextView_IRQ2.setText("IRQ2: "+Cnt_rotturafilo_C2);
                            //IRQ3 sensore piegatore su
                            double ContSensPiegatoreSu = (double)Multicmd_Vq3160_ContSensPiegatoreSu.getValue();
                            TextView TextView_IRQ3 = findViewById(R.id.textView_IRQ3);
                            TextView_IRQ3.setText("IRQ3: "+ContSensPiegatoreSu);

                        }
                    });
                } else {
                    first_cycle = true;
                    sl.Connect();
                }
            }
        }
    }
}


