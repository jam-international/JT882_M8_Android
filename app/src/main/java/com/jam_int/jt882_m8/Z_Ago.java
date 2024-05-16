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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import communication.MultiCmdItem;
import communication.ShoppingList;

public class Z_Ago extends Activity {

    /**
     * Receiver for display more settings
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

            if (val.equals(linea2)|| val.equals("67874")) {
                Button_Fai_Z.setVisibility(View.VISIBLE);
                TextView_quota_maxerror.setVisibility(View.VISIBLE);
                Button_reset_error.setVisibility(View.VISIBLE);

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
    Thread t_Z_ago;
    Boolean Thread_Running = false, StopThread = false, first_cycle = true, Tacca_Z_done = false;
    /**
     * UI components
     */
    TextView TextView_status, TextView_quota_asseX, TextView_quota_asseY, TextView_quota_Ago, TextView_quota_Crochet, TextView_quota_errore, TextView_quota_maxerror, TextView_Vel_ago, TextView_RealVel_ago, TextView_quota_asseLoader;
    Button Button_Xpiu, Button_Xmeno, Button_Ypiu, Button_Ymeno, Button_Agopiu, Button_Agomeno, Button_reset_error, Button_Fai_Z, Button_Loaderpiu, Button_Loadermeno;
    SeekBar seekBar_speed;
    Switch Switch_run_ago;
    int mc_stati_exit = 0;
    /**
     * PLC vars
     */
    MultiCmdItem MultiCmd_Stato_azzeramento, MultiCmd_contatore_tacche_Z, MultiCmd_comando_calcola_offset, MultiCmd_OffsetAgoTacca, MultiCmd_JogAgoPiu,
            MultiCmd_AxAgoFermo, MultiCmd_QuotaAsseX, MultiCmd_QuotaAsseCrochet, MultiCmd_QuotaAsseY, MultiCmd_QuotaAsseAgo, MultiCmd_JogXpiu, MultiCmd_JogXmeno, MultiCmd_JogYpiu, MultiCmd_JogYmeno,
            MultiCmd_JogAgomeno, MultiCmd_error_Gantry, MultiCmd_MaxErrorGrantry, MultiCmd_ResetErrorGantry, MultiCmd_tasto_verde, MultiCmd_CH1_in_emergenza, MultiCmd_VelAgoReale, MultiCmd_Vel_manualeAgo,
            Multicmd_SetVelManualeAsseAgo, MultiCmd_JogLoaderPiu, MultiCmd_JogLoaderMeno, MultiCmd_QuotaAsseLoader, MultiCmd_CmdSaveParam, Multicmd_QuotaSemiAutomatico_X,
            Multicmd_StartSemiAutomatico_X, Multicmd_QuotaSemiAutomatico_Y, Multicmd_StartSemiAutomatico_Y, Multicmd_QuotaSemiAutomatico_Ago, Multicmd_StartSemiAutomatico_Ago, Multicmd_QuotaSemiAutomatico_Car,
            Multicmd_StartSemiAutomatico_Car , MultiCmd_Vn3804_pagina_touch;




    Mci_write Mci_write_Stato_azzeramento = new Mci_write(),
            Mci_write_JogXpiu = new Mci_write(),
            Mci_write_JogXmeno = new Mci_write(),
            Mci_write_JogYpiu = new Mci_write(),
            Mci_write_JogYmeno = new Mci_write(),
            Mci_write_JogAgopiu = new Mci_write(),
            Mci_write_JogAgomeno = new Mci_write(),
            Mci_write_JogAgo = new Mci_write(),
            Mci_write_ResetError = new Mci_write(),
            Mci_Vel_manualeAgo = new Mci_write(),
            Mci_SetVelManualeAsseAgo = new Mci_write(),
            Mci_write_JogLoaderpiu = new Mci_write(),
            Mci_write_JogLoadermeno = new Mci_write(),
		
			Mci_write_QuotaSemiAutomatico_X = new Mci_write(),
            Mci_write_StartSemiAutomatico_X = new Mci_write(),
            Mci_write_QuotaSemiAutomatico_Y = new Mci_write(),
            Mci_write_StartSemiAutomatico_Y = new Mci_write(),
            Mci_write_QuotaSemiAutomatico_Ago = new Mci_write(),
            Mci_write_StartSemiAutomatico_Ago = new Mci_write(),
            Mci_write_QuotaSemiAutomatico_Car = new Mci_write(),
            Mci_write_StartSemiAutomatico_Car = new Mci_write();
    MultiCmdItem[] mci_array_read_all;
    Double cnt_z_iniziale = 0.0d,PosX_inEntrata= 0.0d,PosY_inEntrata= 0.0d,PosAgo_inEntrata= 0.0d,PosCar_inEntrata= 0.0d ;
    Handler UpdateHandler = new Handler();
    int step_avanzamento = 0, seekbar_value = 100;

    final private static int TESTA_C1 = 300;
    final private static int TESTA_C2 = 301;
    int Chiamante = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_z_ago);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //non fa apparire la tastiera

        // Setup ShoppingList
        sl = SocketHandler.getSocket();
        sl.Clear();

        Button_Xpiu = findViewById(R.id.button_Xpiu);
        Button_Xmeno = findViewById(R.id.button_Xmeno);
        Button_Ypiu = findViewById(R.id.button_Ypiu);
        Button_Ymeno = findViewById(R.id.button_Ymeno);
        Button_Agopiu = findViewById(R.id.button_Agopiu);
        Button_Agomeno = findViewById(R.id.button_Agomeno);
        Button_reset_error = findViewById(R.id.button_reset_error);
        Button_Fai_Z = findViewById(R.id.button_Fai_Z);
        Button_Loaderpiu = findViewById(R.id.button_Loaderpiu);
        Button_Loadermeno = findViewById(R.id.button_Loadermeno);

        TextView_quota_asseX = findViewById(R.id.textView_quota_asseX);
        TextView_quota_asseY = findViewById(R.id.textView_quota_asseY);
        TextView_quota_Ago = findViewById(R.id.textView_quota_Ago);
        TextView_status = findViewById(R.id.textView_status);
        TextView_quota_Crochet = findViewById(R.id.textView_quota_Crochet);
        TextView_quota_errore = findViewById(R.id.textView_quota_errore);
        TextView_quota_maxerror = findViewById(R.id.textView_quota_maxerror);
        TextView_Vel_ago = findViewById(R.id.textView_Vel_ago);
        TextView_RealVel_ago = findViewById(R.id.textView_RealVel_ago);
        TextView_quota_asseLoader = findViewById(R.id.textView_quota_asseLoader);

        Switch_run_ago = findViewById(R.id.switch_run_ago);

        Button_Fai_Z.setVisibility(View.GONE);
        Button_reset_error.setVisibility(View.GONE);

        TextView_quota_maxerror.setVisibility(View.GONE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Chiamante = extras.getInt("Chiamante");
        }

        Switch_run_ago.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    Mci_write_JogAgopiu.valore = 1.0d;
                else
                    Mci_write_JogAgopiu.valore = 0.0d;

                Mci_write_JogAgopiu.write_flag = true;
            }
        });

        seekBar_speed = findViewById(R.id.seekBar_speed);
        seekBar_speed.setMax(100);
        seekBar_speed.setProgress(1);
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
                int vel_modificata = seekbar_value;
                if (vel_modificata > 100) vel_modificata = 100;
                if (vel_modificata == 0) vel_modificata = 5;

                TextView_Vel_ago.setText("Target Speed: " + 45 * seekbar_value);

                Mci_Vel_manualeAgo.valore = (Double.valueOf(vel_modificata) * 1000.0d);
                Mci_Vel_manualeAgo.write_flag = true;

                Mci_SetVelManualeAsseAgo.valore = 1.0d;
                Mci_SetVelManualeAsseAgo.write_flag = true;
            }
        });



        MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 21, MultiCmdItem.dpNONE);
        MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
        MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);
        if(Chiamante==TESTA_C1) {
            MultiCmd_Stato_azzeramento = sl.Add("Io", 1, MultiCmdItem.dtVB, 1032, MultiCmdItem.dpNONE);
            MultiCmd_comando_calcola_offset = sl.Add("Io", 1, MultiCmdItem.dtVB, 1017, MultiCmdItem.dpNONE);
            MultiCmd_contatore_tacche_Z = sl.Add("Io", 1, MultiCmdItem.dtVN, 1953, MultiCmdItem.dpNONE);
            MultiCmd_OffsetAgoTacca = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1716, MultiCmdItem.dpNONE);
            MultiCmd_AxAgoFermo = sl.Add("Io", 1, MultiCmdItem.dtVB, 4047, MultiCmdItem.dpNONE);

            MultiCmd_QuotaAsseX = sl.Add("Io", 1, MultiCmdItem.dtVQ, 51, MultiCmdItem.dpNONE);
            MultiCmd_QuotaAsseY = sl.Add("Io", 1, MultiCmdItem.dtVQ, 52, MultiCmdItem.dpNONE);
            MultiCmd_QuotaAsseAgo = sl.Add("Io", 1, MultiCmdItem.dtVQ, 53, MultiCmdItem.dpNONE);
            MultiCmd_QuotaAsseCrochet = sl.Add("Io", 1, MultiCmdItem.dtVQ, 54, MultiCmdItem.dpNONE);
            MultiCmd_JogXpiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7001, MultiCmdItem.dpNONE);
            MultiCmd_JogXmeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7002, MultiCmdItem.dpNONE);
            MultiCmd_JogYpiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7021, MultiCmdItem.dpNONE);
            MultiCmd_JogYmeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7022, MultiCmdItem.dpNONE);
            MultiCmd_JogAgoPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7041, MultiCmdItem.dpNONE);
            MultiCmd_JogAgomeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7042, MultiCmdItem.dpNONE);
            MultiCmd_error_Gantry = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1030, MultiCmdItem.dpNONE);
            MultiCmd_MaxErrorGrantry = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1031, MultiCmdItem.dpNONE);
            MultiCmd_ResetErrorGantry = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1032, MultiCmdItem.dpNONE);
            MultiCmd_VelAgoReale = sl.Add("Io", 1, MultiCmdItem.dtVQ, 1955, MultiCmdItem.dpNONE);
            MultiCmd_Vel_manualeAgo = sl.Add("Io", 1, MultiCmdItem.dtVQ, 108, MultiCmdItem.dpNONE);
            Multicmd_SetVelManualeAsseAgo = sl.Add("Io", 1, MultiCmdItem.dtVB, 78, MultiCmdItem.dpNONE);
            MultiCmd_JogLoaderPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7081, MultiCmdItem.dpNONE);
            MultiCmd_JogLoaderMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7082, MultiCmdItem.dpNONE);
            MultiCmd_QuotaAsseLoader = sl.Add("Io", 1, MultiCmdItem.dtVQ, 55, MultiCmdItem.dpNONE);
            MultiCmd_CmdSaveParam = sl.Add("Io", 1, MultiCmdItem.dtVB, 1012, MultiCmdItem.dpNONE);
            Multicmd_SetVelManualeAsseAgo = sl.Add("Io", 1, MultiCmdItem.dtVB, 78, MultiCmdItem.dpNONE);

            Multicmd_QuotaSemiAutomatico_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7002, MultiCmdItem.dpNONE);
            Multicmd_StartSemiAutomatico_X = sl.Add("Io", 1, MultiCmdItem.dtVB, 7005, MultiCmdItem.dpNONE);
            Multicmd_QuotaSemiAutomatico_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7022, MultiCmdItem.dpNONE);
            Multicmd_StartSemiAutomatico_Y = sl.Add("Io", 1, MultiCmdItem.dtVB, 7025, MultiCmdItem.dpNONE);
            Multicmd_QuotaSemiAutomatico_Ago = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7042, MultiCmdItem.dpNONE);
            Multicmd_StartSemiAutomatico_Ago = sl.Add("Io", 1, MultiCmdItem.dtVB, 7045, MultiCmdItem.dpNONE);
            Multicmd_QuotaSemiAutomatico_Car = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7082, MultiCmdItem.dpNONE);
            Multicmd_StartSemiAutomatico_Car = sl.Add("Io", 1, MultiCmdItem.dtVB, 7085, MultiCmdItem.dpNONE);




        }else { //TESTA_C2
            MultiCmd_Stato_azzeramento = sl.Add("Io", 1, MultiCmdItem.dtVB, 2032, MultiCmdItem.dpNONE);
            MultiCmd_comando_calcola_offset = sl.Add("Io", 1, MultiCmdItem.dtVB, 2017, MultiCmdItem.dpNONE);
            MultiCmd_contatore_tacche_Z = sl.Add("Io", 1, MultiCmdItem.dtVN, 2953, MultiCmdItem.dpNONE);
            MultiCmd_OffsetAgoTacca = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2716, MultiCmdItem.dpNONE);
            MultiCmd_AxAgoFermo = sl.Add("Io", 1, MultiCmdItem.dtVB, 4052, MultiCmdItem.dpNONE);
            MultiCmd_QuotaAsseX = sl.Add("Io", 1, MultiCmdItem.dtVQ, 56, MultiCmdItem.dpNONE);
            MultiCmd_QuotaAsseY = sl.Add("Io", 1, MultiCmdItem.dtVQ, 57, MultiCmdItem.dpNONE);
            MultiCmd_QuotaAsseAgo = sl.Add("Io", 1, MultiCmdItem.dtVQ, 58, MultiCmdItem.dpNONE);
            MultiCmd_QuotaAsseCrochet = sl.Add("Io", 1, MultiCmdItem.dtVQ, 59, MultiCmdItem.dpNONE);
            MultiCmd_JogXpiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7101, MultiCmdItem.dpNONE);
            MultiCmd_JogXmeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7102, MultiCmdItem.dpNONE);
            MultiCmd_JogYpiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7121, MultiCmdItem.dpNONE);
            MultiCmd_JogYmeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7122, MultiCmdItem.dpNONE);
            MultiCmd_JogAgoPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7141, MultiCmdItem.dpNONE);
            MultiCmd_JogAgomeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7142, MultiCmdItem.dpNONE);
            MultiCmd_error_Gantry = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2030, MultiCmdItem.dpNONE);
            MultiCmd_MaxErrorGrantry = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2031, MultiCmdItem.dpNONE);
            MultiCmd_ResetErrorGantry = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2032, MultiCmdItem.dpNONE);
            MultiCmd_VelAgoReale = sl.Add("Io", 1, MultiCmdItem.dtVQ, 2955, MultiCmdItem.dpNONE);
            MultiCmd_Vel_manualeAgo = sl.Add("Io", 1, MultiCmdItem.dtVQ, 109, MultiCmdItem.dpNONE);
            Multicmd_SetVelManualeAsseAgo = sl.Add("Io", 1, MultiCmdItem.dtVB, 79, MultiCmdItem.dpNONE);
            MultiCmd_JogLoaderPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7181, MultiCmdItem.dpNONE);
            MultiCmd_JogLoaderMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7182, MultiCmdItem.dpNONE);
            MultiCmd_QuotaAsseLoader = sl.Add("Io", 1, MultiCmdItem.dtVQ, 60, MultiCmdItem.dpNONE);
            MultiCmd_CmdSaveParam = sl.Add("Io", 1, MultiCmdItem.dtVB, 2012, MultiCmdItem.dpNONE);

            Multicmd_QuotaSemiAutomatico_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7102, MultiCmdItem.dpNONE);
            Multicmd_StartSemiAutomatico_X = sl.Add("Io", 1, MultiCmdItem.dtVB, 7105, MultiCmdItem.dpNONE);
            Multicmd_QuotaSemiAutomatico_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7122, MultiCmdItem.dpNONE);
            Multicmd_StartSemiAutomatico_Y = sl.Add("Io", 1, MultiCmdItem.dtVB, 7125, MultiCmdItem.dpNONE);
            Multicmd_QuotaSemiAutomatico_Ago = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7142, MultiCmdItem.dpNONE);
            Multicmd_StartSemiAutomatico_Ago = sl.Add("Io", 1, MultiCmdItem.dtVB, 7145, MultiCmdItem.dpNONE);
            Multicmd_QuotaSemiAutomatico_Car = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7182, MultiCmdItem.dpNONE);
            Multicmd_StartSemiAutomatico_Car = sl.Add("Io", 1, MultiCmdItem.dtVB, 7185, MultiCmdItem.dpNONE);
        }

        Mci_write_Stato_azzeramento.mci = MultiCmd_Stato_azzeramento;

        Mci_write_JogXpiu.mci = MultiCmd_JogXpiu;

        Mci_write_JogXmeno.mci = MultiCmd_JogXmeno;

        Mci_write_JogYpiu.mci = MultiCmd_JogYpiu;

        Mci_write_JogYmeno.mci = MultiCmd_JogYmeno;

        Mci_write_JogAgopiu.mci = MultiCmd_JogAgoPiu;

        Mci_write_JogAgomeno.mci = MultiCmd_JogAgomeno;

        Mci_write_JogAgo.mci = MultiCmd_JogAgoPiu;

        Mci_write_ResetError.mci = MultiCmd_ResetErrorGantry;

        Mci_Vel_manualeAgo.mci = MultiCmd_Vel_manualeAgo;

        Mci_SetVelManualeAsseAgo.mci = Multicmd_SetVelManualeAsseAgo;

        Mci_write_JogLoaderpiu.mci = MultiCmd_JogLoaderPiu;

        Mci_write_JogLoadermeno.mci = MultiCmd_JogLoaderMeno;


        Mci_write_QuotaSemiAutomatico_X.mci = Multicmd_QuotaSemiAutomatico_X;
        Mci_write_StartSemiAutomatico_X.mci = Multicmd_StartSemiAutomatico_X;
        Mci_write_QuotaSemiAutomatico_Y.mci =Multicmd_QuotaSemiAutomatico_Y;
        Mci_write_StartSemiAutomatico_Y.mci =Multicmd_StartSemiAutomatico_Y;
        Mci_write_QuotaSemiAutomatico_Ago.mci =Multicmd_QuotaSemiAutomatico_Ago;
        Mci_write_StartSemiAutomatico_Ago.mci =Multicmd_StartSemiAutomatico_Ago;
        Mci_write_QuotaSemiAutomatico_Car.mci =Multicmd_QuotaSemiAutomatico_Car;
        Mci_write_StartSemiAutomatico_Car.mci =Multicmd_StartSemiAutomatico_Car;




        mci_array_read_all = new MultiCmdItem[]{MultiCmd_Stato_azzeramento, MultiCmd_contatore_tacche_Z, MultiCmd_OffsetAgoTacca, MultiCmd_QuotaAsseX,
                MultiCmd_QuotaAsseY, MultiCmd_QuotaAsseAgo, MultiCmd_QuotaAsseCrochet, MultiCmd_error_Gantry, MultiCmd_MaxErrorGrantry, MultiCmd_ResetErrorGantry,
                MultiCmd_tasto_verde, MultiCmd_CH1_in_emergenza, MultiCmd_VelAgoReale, MultiCmd_QuotaAsseLoader};

        EdgeButton.CreaEdgeButton(Mci_write_JogXpiu, Button_Xpiu, "tasto_piu_b", "tasto_piu_a", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_write_JogXmeno, Button_Xmeno, "tasto_meno_b", "tasto_meno_a", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_write_JogYpiu, Button_Ypiu, "tasto_piu_b", "tasto_piu_a", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_write_JogYmeno, Button_Ymeno, "tasto_meno_b", "tasto_meno_a", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_write_JogAgopiu, Button_Agopiu, "tasto_piu_b", "tasto_piu_a", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_write_JogAgomeno, Button_Agomeno, "tasto_meno_b", "tasto_meno_a", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_write_ResetError, Button_reset_error, "tasto_meno_b", "tasto_meno_a", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_write_JogLoaderpiu, Button_Loaderpiu, "tasto_piu_b", "tasto_piu_a", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_write_JogLoadermeno, Button_Loadermeno, "tasto_meno_b", "tasto_meno_a", getApplicationContext());

        // Start the thread
        if (!Thread_Running) {
            StopThread = false;
            Thread_Z_ago thread_A_ago = new Thread_Z_ago(Z_Ago.this);
            t_Z_ago = new Thread(thread_A_ago, "Z_ago Task");
            t_Z_ago.setName("Thread Delta");
            t_Z_ago.start();
            Log.d("JAM TAG", "Start Z_Ago Thread");
        }
    }

    @Override
    public void onResume() {     // system calls this method as the first indication that the user is leaving your activity
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("KeyDialog_password_ret"));
    }

    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
        KillThread();
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
     * Button for exit from this activity
     *
     * @param view
     */
    public void onClick_exit(View view) {
              if ( Math.abs((Double)MultiCmd_QuotaAsseX.getValue() - PosX_inEntrata) >500d ||
                Math.abs((Double)MultiCmd_QuotaAsseY.getValue() - PosY_inEntrata) >500d ||
                Math.abs((Double)MultiCmd_QuotaAsseAgo.getValue() - PosAgo_inEntrata) >200d ||
                Math.abs((Double)MultiCmd_QuotaAsseLoader.getValue() - PosCar_inEntrata) >500d

        )
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm");
            builder.setMessage(R.string.WarningMoceMotor);

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                //    write_data_toCn = true;
                //    Salva();

                    mc_stati_exit = 1;
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

    /**
     * Button for open the key dialog for unlock settings
     *
     * @param view
     */
    public void on_click_password(View view) {
        KeyDialog.Lancia_KeyDialogo(null, Z_Ago.this, null, 99999d, 0d, false, false, 0d, true, "KeyDialog_password_ret", false,"");
    }

    /**
     * Button for start the needle rotation
     *
     * @param view
     */
    public void onClickRun(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Warning");
        builder.setMessage(getString(R.string.togliere_ago));

        builder.setPositiveButton("Run", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Premuto run
                try {
                    cnt_z_iniziale = (Double) MultiCmd_contatore_tacche_Z.getValue();
                    step_avanzamento = 10;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Premuto no
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Function for the emergency event
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

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        StopThread = true;

        try {
            t_Z_ago.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("JAM TAG", "End Z_Ago Thread");
    }

    /**
     * Class for communicate with the PLC
     */
    class Thread_Z_ago implements Runnable {
        Activity activity;

        public Thread_Z_ago(Activity activity) {
            this.activity = activity;
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
                    Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                }

                if (sl.IsConnected()) {


                    sl.WriteQueued();
                    sl.ReadItems(mci_array_read_all);

                    MultiCmd_Vn3804_pagina_touch.setValue(1008.0d);
                    sl.WriteItem(MultiCmd_Vn3804_pagina_touch);


                    if (first_cycle) {
                        sl.ReadItem(MultiCmd_Vel_manualeAgo);
                        PosX_inEntrata = (Double) MultiCmd_QuotaAsseX.getValue();
                        PosY_inEntrata = (Double) MultiCmd_QuotaAsseY.getValue();
                        PosAgo_inEntrata = (Double) MultiCmd_QuotaAsseAgo.getValue();
                        PosCar_inEntrata = (Double) MultiCmd_QuotaAsseLoader.getValue();
                    }

                    if (sl.getReturnCode() == 0) {
                        Utility.GestiscoMci_Edge_Out(sl, Mci_write_JogXpiu);
                        Utility.GestiscoMci_Edge_Out(sl, Mci_write_JogXmeno);
                        Utility.GestiscoMci_Edge_Out(sl, Mci_write_JogYpiu);
                        Utility.GestiscoMci_Edge_Out(sl, Mci_write_JogYmeno);
                        Utility.GestiscoMci_Edge_Out(sl, Mci_write_JogAgopiu);
                        Utility.GestiscoMci_Edge_Out(sl, Mci_write_JogAgomeno);
                        Utility.GestiscoMci_Edge_Out(sl, Mci_write_ResetError);
                        Utility.ScrivoVbVnVq(sl, Mci_Vel_manualeAgo);
                        Utility.ScrivoVbVnVq(sl, Mci_SetVelManualeAsseAgo);
                        Utility.ScrivoVbVnVq(sl, Mci_write_JogAgopiu);
                        Utility.GestiscoMci_Edge_Out(sl, Mci_write_JogLoaderpiu);
                        Utility.GestiscoMci_Edge_Out(sl, Mci_write_JogLoadermeno);


                        Utility.ScrivoVbVnVq(sl,Mci_write_QuotaSemiAutomatico_X);
                        Utility.ScrivoVbVnVq(sl,Mci_write_StartSemiAutomatico_X);
                        Utility.ScrivoVbVnVq(sl,Mci_write_QuotaSemiAutomatico_Y);
                        Utility.ScrivoVbVnVq(sl,Mci_write_StartSemiAutomatico_Y);
                        Utility.ScrivoVbVnVq(sl,Mci_write_QuotaSemiAutomatico_Ago);
                        Utility.ScrivoVbVnVq(sl,Mci_write_StartSemiAutomatico_Ago);
                        Utility.ScrivoVbVnVq(sl,Mci_write_QuotaSemiAutomatico_Car);
                        Utility.ScrivoVbVnVq(sl,Mci_write_StartSemiAutomatico_Car);

                        switch (step_avanzamento) {
                            case 0:
                                break;
                            case 10:
                                MultiCmd_Vel_manualeAgo.setValue(7000.0d);
                                sl.WriteItem(MultiCmd_Vel_manualeAgo);
                                Multicmd_SetVelManualeAsseAgo.setValue(1.0d);
                                sl.WriteItem(Multicmd_SetVelManualeAsseAgo);
                                step_avanzamento = 15;
                                break;
                            case 15:
                                MultiCmd_JogAgoPiu.setValue(1.0d);
                                sl.WriteItem(MultiCmd_JogAgoPiu);
                                step_avanzamento = 20;
                                break;
                            case 20:
                                if (((Double) MultiCmd_contatore_tacche_Z.getValue() - cnt_z_iniziale) > 5) {
                                    MultiCmd_JogAgoPiu.setValue(0.0d);
                                    sl.WriteItem(MultiCmd_JogAgoPiu);
                                    step_avanzamento = 30;
                                }
                                break;
                            case 30:
                                sl.ReadItem(MultiCmd_AxAgoFermo);
                                if ((Double) MultiCmd_AxAgoFermo.getValue() == 1.0d) {
                                    Mci_write_JogAgo.Fronte_negativo = true;
                                    MultiCmd_comando_calcola_offset.setValue(1.0d);
                                    sl.WriteItem(MultiCmd_comando_calcola_offset);
                                    step_avanzamento = 40;
                                }
                                break;
                            case 40:
                                MultiCmd_CmdSaveParam.setValue(1.0d);
                                sl.WriteItem(MultiCmd_CmdSaveParam);
                                Tacca_Z_done = true;
                                step_avanzamento = 0;
                                break;
                            default:
                                break;
                        }
                        AggiornaGuiDaThread();
                    }
                } else {
                    sl.Connect();
                }
            }
        }

        /**
         * THIS IS THE ONLY FUNCTION THAT CAN BE INSIDE THREAD
         * <p>
         * THERE IS NO REASON FOR PUT FUNCTIONS HERE, I ONLY HAVE MORE ERRORS
         */
        private void AggiornaGuiDaThread() {
            UpdateHandler.post(new Runnable() {
                @Override
                public void run() {
                    if ((Double) MultiCmd_Stato_azzeramento.getValue() == 1.0d && !Mci_write_Stato_azzeramento.Fronte_positivo) {
                        Mci_write_Stato_azzeramento.Fronte_positivo = true;
                        TextView_status.setText(getString(R.string.Ready));
                    }
                    if (Tacca_Z_done) {
                        Double v = ((Double) MultiCmd_OffsetAgoTacca.getValue()) / 1000;
                        TextView_status.setText(getString(R.string.Done) + ": " + v + "°");
                        Tacca_Z_done = false;
                    }

                    Double quotaX = (Double) MultiCmd_QuotaAsseX.getValue() / 1000;
                    TextView_quota_asseX.setText("X: " + quotaX);
                    Double quotaY = (Double) MultiCmd_QuotaAsseY.getValue() / 1000;
                    TextView_quota_asseY.setText("Y: " + quotaY);
                    Double quotaAgo = (Double) MultiCmd_QuotaAsseAgo.getValue() / 1000;
                    TextView_quota_Ago.setText("Needle: " + quotaAgo);
                    Double quotaCrochet = (Double) MultiCmd_QuotaAsseCrochet.getValue() / 1000;
                    TextView_quota_Crochet.setText("Hook: " + quotaCrochet);
                    Double quotaError = (Double) MultiCmd_error_Gantry.getValue() / 1000;
                    TextView_quota_errore.setText("Error: " + quotaError);
                    Double quotaMaxError = (Double) MultiCmd_MaxErrorGrantry.getValue() / 1000;
                    TextView_quota_maxerror.setText("MaxError: " + quotaMaxError);

                    Double VelRealeAgo = (Double) MultiCmd_VelAgoReale.getValue();
                    TextView_RealVel_ago.setText("Real Speed: " + VelRealeAgo);

                    Double quotaLoader = (Double) MultiCmd_QuotaAsseLoader.getValue() / 1000;
                    TextView_quota_asseLoader.setText("Loader: " + quotaLoader);

                    if (first_cycle) {
                        double vel = (double) MultiCmd_Vel_manualeAgo.getValue() / 1000;
                        seekBar_speed.setProgress((int) vel);
                        TextView_Vel_ago.setText("Target Speed: " + 45 * vel);
                        first_cycle = false;
                    }
                    ExitConAzzeramentiMotori();
                    Emergenza();
                }

                private void ExitConAzzeramentiMotori() {

                    switch (mc_stati_exit) {
                        case 0:
                            break;
                        case 1:
                            Mci_write_QuotaSemiAutomatico_X.valore = PosX_inEntrata;
                            Mci_write_QuotaSemiAutomatico_X.write_flag = true;
                            Mci_write_StartSemiAutomatico_X.valore = 1.0d;
                            Mci_write_StartSemiAutomatico_X.write_flag = true;

                            Mci_write_QuotaSemiAutomatico_Y.valore = PosY_inEntrata;
                            Mci_write_QuotaSemiAutomatico_Y.write_flag = true;
                            Mci_write_StartSemiAutomatico_Y.valore = 1.0d;
                            Mci_write_StartSemiAutomatico_Y.write_flag = true;

                            //se l'asse ago ha fatto tanti giri, non lo riporto all'inizio ma nella stessa posizione ma facendo solo un giro
                            Double pAgo  = (Double) MultiCmd_QuotaAsseAgo.getValue();
                            int  pAgo2  =(int) (pAgo / 360000.0d);
                            int pAgo1 = pAgo2 * 360000;
                            Double pAgo3 = Double.valueOf( pAgo1  ) ;
                            PosAgo_inEntrata = pAgo3;


                            Mci_write_QuotaSemiAutomatico_Ago.valore = pAgo3;
                            Mci_write_QuotaSemiAutomatico_Ago.write_flag = true;
                            Mci_write_StartSemiAutomatico_Ago.valore = 1.0d;
                            Mci_write_StartSemiAutomatico_Ago.write_flag = true;

                            Mci_write_QuotaSemiAutomatico_Car.valore = PosCar_inEntrata;
                            Mci_write_QuotaSemiAutomatico_Car.write_flag = true;
                            Mci_write_StartSemiAutomatico_Car.valore = 1.0d;
                            Mci_write_StartSemiAutomatico_Car.write_flag = true;





                            mc_stati_exit = 0;


                            break;



                        default:
                            break;
                    }
                }
            });
        }
    }
}
