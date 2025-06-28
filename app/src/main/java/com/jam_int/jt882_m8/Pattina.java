package com.jam_int.jt882_m8;

import static com.jam_int.jt882_m8.Values.Machine_model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jamint.ricette.CodeValueType;
import com.jamint.ricette.Element;
import com.jamint.ricette.ElementZigZag;
import com.jamint.ricette.JamPointCode;
import com.jamint.ricette.JamPointStep;
import com.jamint.ricette.MathGeoTri;
import com.jamint.ricette.Ricetta;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import communication.MultiCmdItem;
import communication.ShoppingList;
import communication.SmartAlarm;

public class Pattina extends Activity {
    ShoppingList sl;
    /**
     * Thread
     */
    Thread thread_pattina;
    Handler UpdateHandler = new Handler();
    Boolean Thread_Running = false, StopThread = false, first_cycle = true,  rc_error = false;
    MultiCmdItem MultiCmd_tasto_verde,MultiCmd_CH1_in_emergenza,MultiCmd_Vn3804_pagina_touch,Multicmd_Vb4370_Motore_Pattina_Giu,Multicmd_Vb4371_Motore_Pattina_Su,Multicmd_Vb161ZeroMotoreSuGiu,
            Multicmd_Vb152_Pattina_OnOff,Multicmd_Vb157_Pattina_PassoPasso,MultiCmd_Vn2_allarmi_da_CN,MultiCmd_Vb4904_AppPedaleHmiC2,MultiCmd_Vb4901_start_cucitura_T2,
            MultiCmd_XY_fermi_C2, MultiCmd_quota_destinazione_X_C2, MultiCmd_quota_destinazione_Y_C2, MultiCmd_Vb_OutPiedino_su_C2, MultiCmd_Vb_OutPiedino_giu_C2, MultiCmd_Status_Piedino_C2,
            MultiCmd_Start_movimento_X_C2, MultiCmd_Start_movimento_Y_C2, MultiCmd_posizione_X_C2, MultiCmd_posizione_Y_C2,MultiCmd_RichiestaPiedinoSu_C2,MultiCmd_HmiMoveXY_C2,
            MultiCmd_Vb4906_AppPinzaIntAltaC2,  MultiCmd_JogXMeno , MultiCmd_JogXPiu, MultiCmd_JogYMeno , MultiCmd_JogYPiu,MultiCmd_go_Home_c2,Multicmd_Vb4907_PinzeAlteDopoPC_C2,
            MultiCmd_Vb162PattinaPassoAvanti,Multicmd_Vn164_mc_stati_Pattina,MultiCmd_Vb99Test,MultiCmd_Vb4902_Reset_Cuci_C2,Multicmd_Vb165HoPremutoPiuMeno;
    Mci_write   Mci_write_Vb4370_Motore_Pattina_Giu = new Mci_write(),Mci_write_Vb4371_Motore_Pattina_Su = new Mci_write(),Mci_write_Vb161ZeroMotoreSuGiu = new Mci_write(),Mci_write_Vb152_Pattina_OnOff = new Mci_write(),
            Mci_write_Vb157_Pattina_PassoPasso = new Mci_write(),Mci_Vn2_allarmi_da_CN= new Mci_write(),Mci_write_Vb4904_AppPedaleHmiC2 = new Mci_write(),Mci_write_Vb4901_start_cucitura_T2 = new Mci_write(),
            Mci_write_JogXMeno = new Mci_write() , Mci_write_JogXPiu = new Mci_write(), Mci_write_JogYMeno = new Mci_write(), Mci_write_JogYPiu = new Mci_write(),Mci_Vb_OutPiedino_su = new Mci_write(),
            Mci_write_AppPinzaIntAltaC2 = new Mci_write(),Mci_write_Vb162PattinaPassoAvanti = new Mci_write(),Mci_write_Vb4902_Reset_Cuci_C2 = new Mci_write() ,Mci_write_Vb165HoPremutoPiuMeno = new Mci_write();
    MultiCmdItem[] mci_array_read_all;
    FrameLayout frame_canvas;
    Dynamic_view myView;
    Ricetta ricetta;
    ArrayList<Element> List_entita = new ArrayList<>();
    CoordPosPinza Coord_Pinza = new CoordPosPinza();
    TextView TextView_errore,TextView_info,TextView_xRel ,TextView_YRel,TextView_Dist_Val,TextView_cnt_punti, TextView_tot_punti,TextView_Code ,TextView_Debug_number,
            TextView_cnt_errori_connessione;
    Button Button_motore_pattina_giu,Button_motore_pattina_su,Button_motore_pattina_zero,Button_pattina_on_off,Button_pattina_test,Button_pedale_singolo,
            Button_start_cucitura_T2,ButtonPuntoPiu, ButtonPuntoMeno,Button_sposta,Button_arrow_up, Button_freccia_giu, Button_arrow_right, Button_arrow_left,
            Button_tasto_enter,Button_tasto_home,Button_esc,Button_pattina_step_avanti,Button_reset_C2;
    Info_StepPiuMeno info_StepPiuMeno = new Info_StepPiuMeno();
    Info_modifica info_modifica = new Info_modifica();
    String Folder;
    int cnt_errori_connessione = 0;

    int step_Home = 0,step_Fai_Esci=0,cnt_Emergenza = 0,step_NacondoTastiDaPinzaAlta=0;
    final private static int RESULT_PAGE_LOAD_EEP = 102;
    final private static int RESULT_PAGE_CODE = 103;
    final private static int POPUPFOLDER = 104;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattina);

        frame_canvas = findViewById(R.id.frameLayout_pattina);



        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/ricette");
        dir.mkdirs();
        File file = new File(Values.File_XML_path_T2_R);
        int i = file.getName().lastIndexOf('.');
        String name = file.getName().substring(0, i);
        File file1 = new File(file.getParent() + "/" + name + ".xml");


        ricetta = new Ricetta(Values.plcType);
        ricetta.activeStepIndex = 0;
        Coord_Pinza.XCoordPosPinza = ricetta.pcX;
        Coord_Pinza.YCoordPosPinza = ricetta.pcY;
        try {
            ricetta.open(file1);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "error opening xml file ", Toast.LENGTH_SHORT).show();
        }
        List_entita = (ArrayList<Element>) ricetta.elements;

        myView = new Dynamic_view(this, 500, 500, List_entita, 1.45F, Coord_Pinza, false, -510, 35, null, 500, 500);

        frame_canvas.addView(myView);
        myView.Ricalcola_entità_canvas(List_entita);
        myView.Center_Bitmap_Main(1.25F, 380, 25);

        sl = SocketHandler.getSocket();
        sl.Clear("Io");

        Multicmd_Vb157_Pattina_PassoPasso = sl.Add("Io", 1, MultiCmdItem.dtVB, 157, MultiCmdItem.dpNONE);
        Multicmd_Vb4370_Motore_Pattina_Giu = sl.Add("Io", 1, MultiCmdItem.dtVB, 4370, MultiCmdItem.dpNONE);
        Multicmd_Vb4371_Motore_Pattina_Su = sl.Add("Io", 1, MultiCmdItem.dtVB, 4371, MultiCmdItem.dpNONE);
        MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 21, MultiCmdItem.dpNONE);
        MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
        MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);
        Multicmd_Vb161ZeroMotoreSuGiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 161, MultiCmdItem.dpNONE);
        Multicmd_Vb152_Pattina_OnOff = sl.Add("Io", 1, MultiCmdItem.dtVB, 152, MultiCmdItem.dpNONE);
        MultiCmd_Vn2_allarmi_da_CN = sl.Add("Io", 1, MultiCmdItem.dtVN, 2, MultiCmdItem.dpNONE);
        MultiCmd_Vb4904_AppPedaleHmiC2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4904, MultiCmdItem.dpNONE);
        MultiCmd_Vb4901_start_cucitura_T2  = sl.Add("Io", 1, MultiCmdItem.dtVB, 4901, MultiCmdItem.dpNONE);
        MultiCmd_XY_fermi_C2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4059, MultiCmdItem.dpNONE);
        MultiCmd_Vb_OutPiedino_su_C2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 2003, MultiCmdItem.dpNONE);
        MultiCmd_Vb_OutPiedino_giu_C2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 2004, MultiCmdItem.dpNONE);
        MultiCmd_quota_destinazione_X_C2 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7102, MultiCmdItem.dpNONE);
        MultiCmd_quota_destinazione_Y_C2 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7122, MultiCmdItem.dpNONE);
        switch (Machine_model) {
            case "JT882M":
                MultiCmd_Status_Piedino_C2 = sl.Add("Io", 1, MultiCmdItem.dtDO, 34, MultiCmdItem.dpNONE);
                break;
            case "JT882MA": //Argentina con 4 schede IO Belli
            case "JT882MB": //macchina inclinata con moduli IO Sipro
                MultiCmd_Status_Piedino_C2 = sl.Add("Io", 1, MultiCmdItem.dtDO, 49, MultiCmdItem.dpNONE);
                break;

            default:
                break;
        }

        MultiCmd_Start_movimento_X_C2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 7105, MultiCmdItem.dpNONE);
        MultiCmd_Start_movimento_Y_C2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 7125, MultiCmdItem.dpNONE);
        MultiCmd_posizione_X_C2 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 56, MultiCmdItem.dpNONE);
        MultiCmd_posizione_Y_C2 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 57, MultiCmdItem.dpNONE);
        MultiCmd_RichiestaPiedinoSu_C2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 75, MultiCmdItem.dpNONE);
        MultiCmd_HmiMoveXY_C2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 73, MultiCmdItem.dpNONE);
        MultiCmd_Vb4906_AppPinzaIntAltaC2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4906, MultiCmdItem.dpNONE);
        MultiCmd_go_Home_c2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 53, MultiCmdItem.dpNONE);
        Multicmd_Vb4907_PinzeAlteDopoPC_C2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4907, MultiCmdItem.dpNONE);
        MultiCmd_Vb162PattinaPassoAvanti = sl.Add("Io", 1, MultiCmdItem.dtVB, 162, MultiCmdItem.dpNONE);
        Multicmd_Vn164_mc_stati_Pattina = sl.Add("Io", 1, MultiCmdItem.dtVN, 164, MultiCmdItem.dpNONE);


        MultiCmd_JogXMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 66, MultiCmdItem.dpNONE);
        MultiCmd_JogXPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 67, MultiCmdItem.dpNONE);
        MultiCmd_JogYMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 68, MultiCmdItem.dpNONE);
        MultiCmd_JogYPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 69, MultiCmdItem.dpNONE);
        MultiCmd_Vb99Test = sl.Add("Io", 1, MultiCmdItem.dtVB, 99, MultiCmdItem.dpNONE);
        MultiCmd_Vb4902_Reset_Cuci_C2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4902, MultiCmdItem.dpNONE);
        Multicmd_Vb165HoPremutoPiuMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 165, MultiCmdItem.dpNONE);
        mci_array_read_all = new MultiCmdItem[]{ MultiCmd_tasto_verde, MultiCmd_CH1_in_emergenza,Multicmd_Vb152_Pattina_OnOff,Multicmd_Vb157_Pattina_PassoPasso,MultiCmd_Vn2_allarmi_da_CN,MultiCmd_Vb4904_AppPedaleHmiC2,
                MultiCmd_Vb4901_start_cucitura_T2,MultiCmd_XY_fermi_C2, MultiCmd_Status_Piedino_C2, MultiCmd_posizione_X_C2, MultiCmd_posizione_Y_C2,MultiCmd_Vb4906_AppPinzaIntAltaC2,
                Multicmd_Vn164_mc_stati_Pattina,MultiCmd_Vb99Test};

        Mci_write_Vb4370_Motore_Pattina_Giu.mci = Multicmd_Vb4370_Motore_Pattina_Giu;
        Mci_write_Vb4371_Motore_Pattina_Su.mci = Multicmd_Vb4371_Motore_Pattina_Su;
        Mci_write_Vb161ZeroMotoreSuGiu.mci = Multicmd_Vb161ZeroMotoreSuGiu;
        Mci_write_Vb152_Pattina_OnOff.mci = Multicmd_Vb152_Pattina_OnOff;
        Mci_write_Vb157_Pattina_PassoPasso.mci = Multicmd_Vb157_Pattina_PassoPasso;
        Mci_Vn2_allarmi_da_CN.mci = MultiCmd_Vn2_allarmi_da_CN;Mci_Vn2_allarmi_da_CN.valore_precedente = -1.0d;Mci_Vn2_allarmi_da_CN.valore = 0.0d;
        Mci_write_Vb4904_AppPedaleHmiC2.mci = MultiCmd_Vb4904_AppPedaleHmiC2;
        Mci_write_Vb4901_start_cucitura_T2.mci = MultiCmd_Vb4901_start_cucitura_T2;
        Mci_write_JogXMeno.mci = MultiCmd_JogXMeno;
        Mci_write_JogXMeno.write_flag = false;

        Mci_write_AppPinzaIntAltaC2.mci = MultiCmd_Vb4906_AppPinzaIntAltaC2;
        Mci_write_AppPinzaIntAltaC2.Fronte_negativo = false;
        Mci_write_AppPinzaIntAltaC2.Fronte_positivo = false;
        Mci_write_AppPinzaIntAltaC2.write_flag = false;

        Mci_write_Vb162PattinaPassoAvanti.mci = MultiCmd_Vb162PattinaPassoAvanti;
        Mci_write_Vb4902_Reset_Cuci_C2.mci = MultiCmd_Vb4902_Reset_Cuci_C2;;

        Mci_write_Vb165HoPremutoPiuMeno.mci = Multicmd_Vb165HoPremutoPiuMeno;

        Mci_write_JogXPiu.mci = MultiCmd_JogXPiu;
        Mci_write_JogXPiu.write_flag = false;

        Mci_write_JogYMeno.mci = MultiCmd_JogYMeno;
        Mci_write_JogYMeno.write_flag = false;

        Mci_write_JogYPiu.mci = MultiCmd_JogYPiu;
        Mci_write_JogYPiu.write_flag = false;

        Mci_Vb_OutPiedino_su.mci = MultiCmd_Vb_OutPiedino_su_C2;
        Mci_Vb_OutPiedino_su.write_flag = false;

        TextView_errore = findViewById(R.id.textView_errore);
        TextView_info = findViewById(R.id.textView_info);
        TextView_xRel = findViewById(R.id.textView_delta_x);
        TextView_YRel = findViewById(R.id.textView_delta_y);
        TextView_Dist_Val = findViewById(R.id.textView_delta_y);
        TextView_cnt_punti = findViewById(R.id.textView_cnt_punti);
        TextView_tot_punti = findViewById(R.id.textView_tot_punti);
        TextView_Code = findViewById(R.id.textView_Code);
        TextView_Debug_number = findViewById(R.id.textView_Debug_number);
        TextView_cnt_errori_connessione = findViewById(R.id.textView_cnt_errori_connessione);

        Button_pattina_on_off = findViewById(R.id.btn_pattina_on_off);
        Button_motore_pattina_giu = findViewById(R.id.btn_motore_pattina_giu);
        Button_motore_pattina_su = findViewById(R.id.btn_motore_pattina_su);
        Button_motore_pattina_zero = findViewById(R.id.btn_motore_pattina_zero);
        Button_pattina_test = findViewById(R.id.btn_pattina_test);
        Button_pedale_singolo = (Button)findViewById(R.id.btn_pedale_singolo);
        Button_start_cucitura_T2   = (Button)findViewById(R.id.btn_start_cucitura_T2);
        ButtonPuntoPiu = findViewById(R.id.button_piu);
        ButtonPuntoMeno = findViewById(R.id.button_meno);
        Button_sposta = findViewById(R.id.button_sposta);
        Button_arrow_up = findViewById(R.id.button_arrow_up);
        Button_freccia_giu = findViewById(R.id.button_freccia_giu);
        Button_arrow_right = findViewById(R.id.button_arrow_right);
        Button_arrow_left = findViewById(R.id.button_arrow_left);
        Button_tasto_enter = findViewById(R.id.button_tasto_enter);
        Button_tasto_home = findViewById(R.id.button_tasto_home);
        Button_esc = findViewById(R.id.button_esc);
        Button_pattina_step_avanti = findViewById(R.id.btn_pattina_step_avanti);
        Button_reset_C2 = findViewById(R.id.button_reset_C2);



        Init_Eventi();
        TextView_tot_punti.setText(String.valueOf(ricetta.getStepsCount()));

        EdgeButton.CreaEdgeButton(Mci_write_Vb4370_Motore_Pattina_Giu, Button_motore_pattina_giu, "ic_pattina_motor_down_press", "ic_pattina_motor_down", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_write_Vb4371_Motore_Pattina_Su, Button_motore_pattina_su, "ic_pattina_motor_up_press", "ic_pattina_motor_up", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_write_Vb161ZeroMotoreSuGiu, Button_motore_pattina_zero, "ic_pattina_motore_zero_press", "ic_pattina_motore_zero", getApplicationContext());
        Toggle_Button.CreaToggleButton(Mci_write_Vb152_Pattina_OnOff, Button_pattina_on_off, "ic_pattina_onoff_press", "ic_pattina_onoff", getApplicationContext(), sl);
        Toggle_Button.CreaToggleButton(Mci_write_Vb157_Pattina_PassoPasso, Button_pattina_test, "ic_pattina_test_press", "ic_pattina_test", getApplicationContext(), sl);
        EdgeButton.CreaEdgeButton(Mci_write_Vb4904_AppPedaleHmiC2, Button_pedale_singolo, "pedale_singolo_press", "pedale_singolo", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_write_Vb4901_start_cucitura_T2, Button_start_cucitura_T2, "ic_start_cucitura_press", "ic_start_cucitura_c2", getApplicationContext());

        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogYPiu, Button_arrow_up, "ic_up_press", "ic_up", getApplicationContext(), 100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogYMeno, Button_freccia_giu, "ic_down_press", "ic_down", getApplicationContext(), 100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogXPiu, Button_arrow_right, "ic_right_press", "ic_right", getApplicationContext(), 100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogXMeno, Button_arrow_left, "ic_left_press", "ic_left", getApplicationContext(), 100);
        EdgeButton.CreaEdgeButton(Mci_write_Vb162PattinaPassoAvanti, Button_pattina_step_avanti, "ic_pattina_step_avanti_press", "ic_pattina_step_avanti", getApplicationContext());
        EdgeButton.CreaEdgeButton(Mci_write_Vb4902_Reset_Cuci_C2, Button_reset_C2, "ic_tasto_reset_premuto", "ic_tasto_reset", getApplicationContext());


        if (!Thread_Running) {
            StopThread = false;
            MyAndroidThread_Pattina myTask = new MyAndroidThread_Pattina(Pattina.this);
            thread_pattina = new Thread(myTask, "Pattina myTask");
            thread_pattina.setName("Thread_Pattina");
            thread_pattina.start();
            Log.d("JAM TAG", "Pattina Thread from OnCreate");
        }
    }

    @Override                   //your activity is no longer visible to the user
    public void onDestroy() {
        super.onDestroy();

        KillThread();
    }
    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
    }

    @Override                   //your activity is no longer visible to the user
    public void onStop() {
        super.onStop();
    }

    private void Init_Eventi() {
        ButtonPuntoPiu.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Mci_write_Vb165HoPremutoPiuMeno.valore = 1.0d;
                    Mci_write_Vb165HoPremutoPiuMeno.write_flag = true;
                    if (info_StepPiuMeno.tipo_spostamento != Info_StepPiuMeno.Tipo_spostamento.N_SALTO) {   //se ripremo + durante lo spostamento di n punti allora lo fermo
                        if (info_StepPiuMeno.MacStati_StepSingolo == 0) {
                            info_StepPiuMeno.MacStati_StepVeloce = 0; //pulisco

                            if ((Double) MultiCmd_XY_fermi_C2.getValue() == 1.0d) {





                                info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.SINGOLO;
                                info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.GO;
                                info_StepPiuMeno.direzione = Info_StepPiuMeno.Direzione.AVANTI;

                                info_StepPiuMeno.MacStati_StepSingolo = 10;

                                info_modifica.QuoteRelativeAttive = true;

                                info_modifica.DeltaX_inizio = ((Double) MultiCmd_posizione_X_C2.getValue()).floatValue() / 1000f;
                                info_modifica.DeltaY_inizio = ((Double) MultiCmd_posizione_Y_C2.getValue()).floatValue() / 1000f;
                            }

                        }
                    } else {
                        Info_StepPiuMeno.numeroRipetuto = 1;    //se arrivo sta spostamenti SINGOLO_RIPETUTO faccio fare un altro passo e poi si ferma
                        info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.NULL;
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.STOP;
                }

                return false;
            }
        });
        ButtonPuntoPiu.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {

                if (info_StepPiuMeno.MacStati_StepVeloce == 0 && info_StepPiuMeno.MacStati_StepSingolo == 0) {
                    if (info_StepPiuMeno.MacStati_StepSingolo == 0) {

                        info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.VELOCE;
                        info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.GO;
                        info_StepPiuMeno.direzione = Info_StepPiuMeno.Direzione.AVANTI;

                        info_StepPiuMeno.MacStati_StepSingolo = 10;


                    }
                }

                return true;    // <- set to true
            }
        });

        ButtonPuntoMeno.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Mci_write_Vb165HoPremutoPiuMeno.valore = 1.0d;
                    Mci_write_Vb165HoPremutoPiuMeno.write_flag = true;
                    if (info_StepPiuMeno.tipo_spostamento != Info_StepPiuMeno.Tipo_spostamento.N_SALTO) {   //se ripremo + durante lo spostamento di n punti allora lo fermo
                        if (info_StepPiuMeno.MacStati_StepSingolo == 0) {
                            info_StepPiuMeno.MacStati_StepVeloce = 0; //pulisco

                            if ((Double) MultiCmd_XY_fermi_C2.getValue() == 1.0d) {

                                info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.SINGOLO;
                                info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.GO;
                                info_StepPiuMeno.direzione = Info_StepPiuMeno.Direzione.DIETRO;

                                info_StepPiuMeno.MacStati_StepSingolo = 10;

                                info_modifica.QuoteRelativeAttive = true;

                                info_modifica.DeltaX_inizio = ((Double) MultiCmd_posizione_X_C2.getValue()).floatValue() / 1000f;
                                info_modifica.DeltaY_inizio = ((Double) MultiCmd_posizione_Y_C2.getValue()).floatValue() / 1000f;

                            }

                        }
                    } else {
                        Info_StepPiuMeno.numeroRipetuto = 1;    //se arrivo sta spostamenti SINGOLO_RIPETUTO faccio fare un altro passo e poi si ferma
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.STOP;
                }
                return false;
            }
        });
        ButtonPuntoMeno.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {

                if (info_StepPiuMeno.MacStati_StepVeloce == 0 && info_StepPiuMeno.MacStati_StepSingolo == 0) {
                    if (info_StepPiuMeno.MacStati_StepSingolo == 0) {

                        info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.VELOCE;
                        info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.GO;
                        info_StepPiuMeno.direzione = Info_StepPiuMeno.Direzione.DIETRO;

                        info_StepPiuMeno.MacStati_StepSingolo = 10;


                    }
                }

                return true;    // <- set to true
            }
        });



    }
    /**
     * TODO
     */
    private void Run_Alert_dialog() {
        // Creating alert Dialog with one Button
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setCancelable(false);
        // Setting Dialog Title
        alertDialog.setTitle("File Name:");

        // Setting Dialog Message
        try {
            //Tolgo la parte di storage, emulated, 0 ecc....
            String newFolder = "";
            String[] str = new String[]{};
            if (Folder != null) {
                str = Folder.split("/");
            }

            int i = 0;
            for (String st : str) {
                if (i == 1) {
                    newFolder = newFolder + "/" + st;
                }
                if (st.equals("ricette")) {
                    i = 1;
                }
            }
            alertDialog.setMessage("Folder: " + newFolder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final EditText input = new EditText(getApplicationContext());

        File file = new File(Values.File_XML_path_T2_R);

        int i = file.getName().lastIndexOf('.');
        String name = file.getName().substring(0, i);
        input.setText(name);
        input.setFocusable(false);
        input.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog_lettere.Lancia_KeyDialogo_lettere(Pattina.this, input, "");
                }
                return false;
            }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //  Esci_con_azzeramento = true;
                        //Fai_Home_primo_Home();
                        if (!input.getText().toString().matches("")) {
                            dialog.cancel();
                            File root = android.os.Environment.getExternalStorageDirectory();
                            File dir;
                            //prendo la cartella dove è contenuto il file
                            File f = new File(Values.File_XML_path_T2_R);
                             Folder = f.getParent();

                            if (Folder == null) {
                                dir = new File(root.getAbsolutePath() + "/ricette");
                            } else {
                                dir = new File(Folder);
                            }
                            dir.mkdirs();
                            File file = new File(dir, input.getText().toString() + ".xml");
                            File file1 = new File(dir, input.getText().toString() + ".usr");
                            if (file.exists() || file1.exists()) {
                                final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(Pattina.this);

                                // Setting Dialog Title
                                alertDialog1.setTitle("overWrite");

                                // Setting Dialog Message
                                alertDialog1.setMessage("overWrite?");

                                // Setting Positive "Yes" Button
                                alertDialog1.setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                File root = android.os.Environment.getExternalStorageDirectory();
                                                File dir;
                                                if (Folder == null) {
                                                    dir = new File(root.getAbsolutePath() + "/ricette");
                                                } else {
                                                    dir = new File(Folder);
                                                }
                                                dir.mkdirs();
                                                File file = new File(dir, input.getText().toString() + ".xml");
                                                File file1 = new File(dir, input.getText().toString() + ".usr");
                                                try {
                                                    ricetta.save(file);

                                                    try {
                                                        ricetta.exportToUsr(file1);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Toast.makeText(getApplicationContext(), "error Usr export ", Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getApplicationContext(), "error saving xml file ", Toast.LENGTH_SHORT).show();
                                                }

                                                CaricaPaginaSendToCn(file.getPath());
                                            }
                                        });

                                alertDialog1.setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                Mostra_Tutte_Icone();
                                            }
                                        });
                                alertDialog1.show();

                            } else {
                                try {
                                    ricetta.save(file);
                                    try {
                                        ricetta.exportToUsr(file1);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(), "error Usr export ", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "error saving xml file ", Toast.LENGTH_SHORT).show();
                                }
                                CaricaPaginaSendToCn(file.getPath());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Please insert Text", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();


                        info_modifica.comando = Info_modifica.Comando.FAIHOME_AND_EXIT;
                        //        KillThread();
                        //        finish();
                    }
                });

        alertDialog.setNeutralButton("Folder",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent_par = new Intent(getApplicationContext(), PopUpSelectFolder.class);
                        startActivityForResult(intent_par, POPUPFOLDER);
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }
    /**
     * TODO
     *
     * @param path
     */
    private void CaricaPaginaSendToCn(String path) {
        StopThread = true;
        KillThread();

        Intent intent_par = new Intent(getApplicationContext(), Select_file_to_CN.class);
        intent_par.putExtra("File_path", path);
        intent_par.putExtra("operazione", "Saving....");

        intent_par.putExtra("Chiamante", "T2_R");



        startActivityForResult(intent_par, RESULT_PAGE_LOAD_EEP);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_PAGE_LOAD_EEP:
                Intent databack = new Intent();
                databack.setData(Uri.parse("CARICATO_T2_DX"));
                setResult(RESULT_OK, databack);
                KillThread();
                finish();
                break;
            case POPUPFOLDER:
                if (resultCode == 0) {
                    Mostra_Tutte_Icone();
                    if (!Thread_Running) {
                        StopThread = false;
                        MyAndroidThread_Pattina myTask = new MyAndroidThread_Pattina(Pattina.this);

                        Thread t1 = new Thread(myTask, "Main myTask");
                        t1.start();
                        Log.d("JAM TAG", "Start Modifica_programma Thread");
                    }
                } else {
                    Folder = data.getExtras().getString("FolderPath");
                    try {
                        View v = new View(this);
                        onclick_button_Exit(v);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case RESULT_PAGE_CODE:
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Button for move elements or steps
     *
     * @param view
     */
    public void on_click_sposta(View view) {
        //move steps

        ricetta.selectionStepStart();


        info_modifica = new Info_modifica();
        info_modifica.QuoteRelativeAttive = true;
        info_modifica.id_punto_inizio_modifica = ricetta.getActiveStepIndex();
        info_modifica.comando = Info_modifica.Comando.SPOSTA1;
        info_modifica.id_punto_inizio_modifica = ricetta.getActiveStepIndex();
        info_modifica.DeltaX_inizio = ((Double) MultiCmd_posizione_X_C2.getValue()).floatValue() / 1000f;
        info_modifica.DeltaY_inizio = ((Double) MultiCmd_posizione_Y_C2.getValue()).floatValue() / 1000f;

        TextView_info.setText(getString(R.string.StringMove1));  //Premi tasti + - fino ultimo punto da spostare

    }
    /**
     * Button for confirm a command operation
     *
     * @param view
     */
    public void onclick_button_tasto_enter(View view) {
        boolean risultato;

        try {
            if (info_modifica.comando != Info_modifica.Comando.Null) {
                switch (info_modifica.comando) {

                    case SPOSTA1:
                        int step_index = ricetta.activeStepIndex;
                        ricetta.selectionStepEnd();

                        info_modifica.DeltaX_inizio = ((Double) MultiCmd_posizione_X_C2.getValue()).floatValue() / 1000f;
                        info_modifica.DeltaY_inizio = ((Double) MultiCmd_posizione_Y_C2.getValue()).floatValue() / 1000f;
                        info_modifica.comando = Info_modifica.Comando.SPOSTA2;
                        info_modifica.id_punto_fine_modifica = ricetta.getActiveStepIndex();
                        TextView_info.setText(getString(R.string.StringMove2));  //Muovere usando le frecce poi premi Enter
                        info_modifica.QuoteRelativeAttive = true;
                        break;
                    case SPOSTA2:
                        int ret_move=-999;
                        try {
                            float Xfinale = (float)Coord_Pinza.XCoordPosPinza;
                            float Yfinale = (float)Coord_Pinza.YCoordPosPinza;
                            info_modifica.QuoteRelativeAttive = false;
                            if (Xfinale != info_modifica.DeltaX_inizio || Yfinale != info_modifica.DeltaY_inizio) {
                                double DeltaX = Xfinale - info_modifica.DeltaX_inizio;
                                double DeltaY = Yfinale - info_modifica.DeltaY_inizio;


                                if (info_modifica.puntoCarico == true) {   //sono sul punto di carico?
                                    ret_move=998;
                                    info_modifica.puntoCarico = false;
                                    ricetta.pcX = ricetta.pcX + (float) DeltaX;
                                    ricetta.pcY = ricetta.pcY + (float) DeltaY;
                                    if (ricetta.elements.size() > 0) {
                                        Element element = ricetta.elements.get(0);
                                        element.pStart.x = ricetta.pcX;
                                        element.pStart.y = ricetta.pcY;

                                        TextView_info.setText("Loader Point Move Done");

                                    }

                                } else {  //non sono sul punto di carico

                                    if (info_modifica.id_punto_fine_modifica == info_modifica.id_punto_inizio_modifica) {
                                        ricetta.moveActiveStep((float) DeltaX, (float) DeltaY);    //sposto solo un punto
                                        info_modifica.comando = Info_modifica.Comando.Null;
                                        ret_move=997;
                                        TextView_info.setText("Move Done (type:" +ret_move+")");
                                        info_modifica.comando = Info_modifica.Comando.Null;
                                    } else {
                                        ret_move = ricetta.moveSelectedSteps((float) DeltaX, (float) DeltaY);     //sposto tutti i punti
                                        if(ret_move == 0) TextView_info.setText("Move Error: Not Possible move this element");
                                        else
                                            TextView_info.setText("Move Done (type:" +ret_move+")");

                                        info_modifica.comando = Info_modifica.Comando.Null;

                                    }

                                    ricetta.selectionStepClear();

                                    List<JamPointCode> ListCodici = new ArrayList<>();
                              //      ListCodici = ricetta.checkInvalidCodes(true);
                              //      if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                    ricetta.clearInvalidCodes();

                                }




                                ricetta.repair();  //ripara la ricetta nel caso ci siano degli errori di continuità o di coordinate

                                Aggiorna_canvas();

                            //    UpdateButtonsVisibility(Modifica_programma.ButtonsVisibilityStatus.VISIBLE, Modifica_programma.ButtonsVisibilityStatus.NULL, Modifica_programma.ButtonsVisibilityStatus.NULL, Modifica_programma.ButtonsVisibilityStatus.NULL);


                            }
                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            TextView_info.setText(getString(R.string.Errore)+":"+ret_move);
                        }
                        break;

                    default:

                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
  * Refresh the canvas
     */
    private void Aggiorna_canvas() {
        myView.Ricalcola_entità_canvas(ricetta.elements);
        myView.AggiornaCanvas(true);
    }
    /**
     * Button for reset the needle position
     *
     * @param view
     */
    public void on_click_move_to_home(View view) {
        ricetta.clearActiveStep();  //imposta indice step a -1
        info_StepPiuMeno.MacStati_StepSingolo = 0; //pulisco
        SpegniTutteIcone();
        info_modifica.comando = Info_modifica.Comando.HOME;
    }
    /**
     * Button for exit from this activity
     *
     * @param view
     */
    public void onclick_button_Exit(View view) {
        SpegniTutteIcone();
       // Button_exit.setVisibility(View.GONE);
        if (Values.Debug_mode) info_modifica.comando = Info_modifica.Comando.ESCI_DONE_AZZERAMENTO;
        else
            info_modifica.comando = Info_modifica.Comando.ESCI;



       // StopThread = true;
       // KillThread();

        //finish();
    }
    /**
     * Button for undo an operation
     *
     * @param view
     */
    public void onclick_Undo(View view) {
        try {
            Mci_Vb_OutPiedino_su.valore = 1.0d;
            Mci_Vb_OutPiedino_su.write_flag = true;

            JamPointStep step = ricetta.getActiveStep();

            if (step == null) {
                try {
                    ricetta.undo();
                    ricetta.clearActiveStep();  //imposta indice step a -1
                } catch (Exception e) {
                    e.printStackTrace();
                }

                info_modifica.comando = Info_modifica.Comando.HOME;
                SpegniTutteIcone();
            } else {
                try {
                    PointF p = new PointF(step.p.x, step.p.y);
                    ricetta.undo();

                    JamPointStep step_risultato = ricetta.goToNearestStep(p, 12.7d);
                    if (step_risultato == null) {

                        ricetta.clearActiveStep();  //imposta indice step a -1
                        info_modifica.comando = Info_modifica.Comando.HOME;
                        SpegniTutteIcone();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Aggiorna_canvas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Button for redo an operation
     *
     * @param view
     */
    public void onclick_Redo(View view) {

        Mci_Vb_OutPiedino_su.valore = 1.0d;
        Mci_Vb_OutPiedino_su.write_flag = true;

        try {
            ricetta.redo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Aggiorna_canvas();
    }

    /**
     * Button for stop the execution of a command
     *
     * @param view
     */
    public void onclick_Esc(View view) {
        ricetta.selectionStepClear();
        info_StepPiuMeno.MacStati_StepSingolo = 0;
        info_modifica.comando = Info_modifica.Comando.Null;
        //UpdateButtonsVisibility(Modifica_programma.ButtonsVisibilityStatus.VISIBLE, Modifica_programma.ButtonsVisibilityStatus.NULL, Modifica_programma.ButtonsVisibilityStatus.NULL, Modifica_programma.ButtonsVisibilityStatus.NULL);
        TextView_info.setText("");
        Mci_Vb_OutPiedino_su.valore = 1.0d;
        Mci_Vb_OutPiedino_su.write_flag = true;
    }
    private void KillThread() {


        StopThread = true;

        try {
            Thread.sleep((long) 200d);
            if (!Thread_Running)
                thread_pattina.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("JAM TAG", "Stop Pattina Thread");
    }

    /**
     * TODO
     */
    private void Emergenza() {
        if ((Double) MultiCmd_tasto_verde.getValue() == 0.0d || (Double) MultiCmd_CH1_in_emergenza.getValue() == 1.0d) {
            cnt_Emergenza++;

        }
        if(cnt_Emergenza>100) {
            KillThread();
            Utility.ClearActivitiesTopToEmergencyPage(getApplicationContext());
            cnt_Emergenza = 0;
        }
    }

    /**
     * TODO
     *
     * @param nPunti
     */
    private void StepSingolo(int nPunti) {
        switch (info_StepPiuMeno.MacStati_StepSingolo) {
            case 10:

                if ((Double) MultiCmd_XY_fermi_C2.getValue() == 1.0d) { //se X e Y sono fermi
                    info_StepPiuMeno.MacStati_StepSingolo = 20;
                }//mofifica Mantini dalla Cina
                int idx = ricetta.getActiveStepIndex();
                if (ricetta.getActiveStepIndex() == -1 || ricetta.getActiveStepIndex() == 0 || info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.N_SALTO) {     //se sono sul punto di carico
                    //alzo piedino nel caso faccio un psostamento dal punto di carico oppure faccio un aslto di n punti
                    MultiCmd_RichiestaPiedinoSu_C2.setValue(1.0d);
                    sl.WriteItem(MultiCmd_RichiestaPiedinoSu_C2);
                }
               // ElemSelezionati.clear();
                break;
            case 20:
                JamPointStep step = new JamPointStep();
                if (info_StepPiuMeno.direzione == Info_StepPiuMeno.Direzione.AVANTI) {
                    try {
                        //alzo il piedino se è basso e se trovo un feed
                        if ((ricetta.isNextElementFeed()) && (double) MultiCmd_Status_Piedino_C2.getValue() == 0.0d) {
                            MultiCmd_RichiestaPiedinoSu_C2.setValue(1.0d);
                            sl.WriteItem(MultiCmd_RichiestaPiedinoSu_C2);
                        }

                        for (int i = 0; i < nPunti; i++)
                            step = ricetta.goToNextStep();

                       // if (ElemSelezionati != null)
                        //    ElemSelezionati.clear();    //se il LIST è vuoto allora indica che ho scorso il programma come steps altrimenti con selectNextEntity

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "getStepAfter catch Singolo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        //alzo il piedino se è basso e se trovo un feed
                        if ((ricetta.isPreviousElementFeed()) && (double) MultiCmd_Status_Piedino_C2.getValue() == 0.0d) {
                            MultiCmd_RichiestaPiedinoSu_C2.setValue(1.0d);
                            sl.WriteItem(MultiCmd_RichiestaPiedinoSu_C2);
                        }
                        //modifica per passa sul punto di carico quando faccio step- sul primo punto di cucitura
                        if (ricetta.getActiveStepIndex() == 1) {  //se devo andare al punto di carico
                            JamPointStep step1 = ricetta.goToPreviousStep();    //lo uso per decrementare ActiveStepIndex

                            step.p.x = ricetta.pcX;
                            step.p.y = ricetta.pcY;

                        } else {   //procedura normale per trovare lo step precedente
                            for (int i = 0; i < nPunti; i++)
                                step = ricetta.goToPreviousStep();
                        }

                      //  if (ElemSelezionati != null)
                     //       ElemSelezionati.clear();    //se il LIST è vuoto allora indica che ho scorso il programma come steps altrimenti con selectNextEntity

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "getStepBefore catch Singolo", Toast.LENGTH_SHORT).show();
                    }
                }


                for (Element elementi : List_entita) {     //selezione
                    elementi.isSelected = false;       //deseleziono tutti gli element
                }
               // for (Element elementi : ElemSelezionati) {     //selezione
               //     elementi.isSelected = false;       //deseleziono gli element dell'entità
               // }




                float X_destinazione = 0.0f, Y_destinazione = 0.0f;
                if (step == null) {
                    MultiCmd_RichiestaPiedinoSu_C2.setValue(1.0d);
                    sl.WriteItem(MultiCmd_RichiestaPiedinoSu_C2);
                    X_destinazione = ricetta.pcX;
                    Y_destinazione = ricetta.pcY;
                }

                if (step != null) {
                    X_destinazione += step.p.x;                                  //quota X destinazione del primo step dell'element successivo
                    Y_destinazione += step.p.y;                                          //quota Y destinazione del primo step dell'element successivo
                } else
                    info_StepPiuMeno.MacStati_StepSingolo = 0;

                double X = Double.valueOf(X_destinazione);                              //invio quote al PLC
                double Y = Double.valueOf(Y_destinazione);


            //    Double LimiteXNegativo = (Double) MultiCmd_Vq_104_READ_FC_ind_X.getValue() / 1000;
            //    Double LimiteXPositivo = (Double) MultiCmd_Vq_105_READ_FC_ava_X.getValue() / 1000;
            //    Double LimiteYNegativo = (Double) MultiCmd_Vq_106_READ_FC_ind_Y.getValue() / 1000;
            //    Double LimiteYPositivo = (Double) MultiCmd_Vq_107_READ_FC_ava_Y.getValue() / 1000;

            //    if (X < LimiteXNegativo || X > LimiteXPositivo || Y < LimiteYNegativo || Y > LimiteYPositivo) {
             //       X = 0;      //se le coordinate sono fuori area per non bloccare il tutto lo faccio passare per 0
             //       Y = 0;      //se le coordinate sono fuori area per non bloccare il tutto lo faccio passare per 0
             //   }

                // se sono già sopra le quote da raggiungere allora esco senza muovermi e ricomincio
                if (X == (Double) MultiCmd_posizione_X_C2.getValue() && Y == (Double) MultiCmd_posizione_Y_C2.getValue()) {
                    info_StepPiuMeno.MacStati_StepSingolo = 0;
                    break;
                }

                MultiCmd_quota_destinazione_X_C2.setValue(X * 1000);
                MultiCmd_quota_destinazione_Y_C2.setValue(Y * 1000);

                MultiCmd_HmiMoveXY_C2.setValue(1.0d);

                MultiCmdItem[] Dati_out = new MultiCmdItem[]{MultiCmd_quota_destinazione_X_C2, MultiCmd_quota_destinazione_Y_C2,
                        MultiCmd_HmiMoveXY_C2};
                sl.WriteItems(Dati_out);

                info_StepPiuMeno.MacStati_StepSingolo = 25;
                break;
            case 25:
                if ((Double) MultiCmd_XY_fermi_C2.getValue() == 1.0d) { //aspetto che si muove
                    info_StepPiuMeno.MacStati_StepSingolo = 30;
                }
                break;
            case 30:
                if ((Double) MultiCmd_XY_fermi_C2.getValue() == 1.0d) { //aspetto che sono fermo

                    if (info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.VELOCE && info_StepPiuMeno.comando != Info_StepPiuMeno.Comando.STOP) {
                        info_StepPiuMeno.MacStati_StepSingolo = 10;
                    } else {
                        info_StepPiuMeno.MacStati_StepSingolo = 0;
                        info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.NULL;
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
     * Function for hide all the icons
     */
    private void SpegniTutteIcone() {

        ButtonPuntoPiu.setVisibility(View.GONE);
        ButtonPuntoMeno.setVisibility(View.GONE);
        Button_sposta.setVisibility(View.GONE);
        Button_arrow_up.setVisibility(View.GONE);
        Button_freccia_giu.setVisibility(View.GONE);
        Button_arrow_right.setVisibility(View.GONE);
        Button_arrow_left.setVisibility(View.GONE);
        Button_tasto_enter.setVisibility(View.GONE);
        Button_tasto_home.setVisibility(View.GONE);
        Button_esc.setVisibility(View.GONE);

    }
    /**
     * Function for display all the icons
     */
    private void Mostra_Tutte_Icone() {
        ButtonPuntoPiu.setVisibility(View.VISIBLE);
        ButtonPuntoMeno.setVisibility(View.VISIBLE);
        Button_sposta.setVisibility(View.VISIBLE);
        Button_arrow_up.setVisibility(View.VISIBLE);
        Button_freccia_giu.setVisibility(View.VISIBLE);
        Button_arrow_right.setVisibility(View.VISIBLE);
        Button_arrow_left.setVisibility(View.VISIBLE);
        Button_tasto_enter.setVisibility(View.VISIBLE);
        Button_tasto_home.setVisibility(View.VISIBLE);
        Button_esc.setVisibility(View.VISIBLE);
        Button_pattina_step_avanti.setVisibility(View.VISIBLE);

    }
    /**
     * Function for handle the arrows event
     *
     * @param Mci_write
     */
    public void GestiscoFreccia(Mci_write Mci_write) {
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
    /**
     * TODO
     */
    private void ShowQuoteRelative() {
        try {
            TextView TextView_Dist_Val = findViewById(R.id.textView_Dist_Val);
            if (info_modifica.QuoteRelativeAttive) {
                TextView_xRel.setVisibility(View.VISIBLE);
                TextView_YRel.setVisibility(View.VISIBLE);
                TextView_Dist_Val.setVisibility(View.VISIBLE);
                double XAttuale = (Double) MultiCmd_posizione_X_C2.getValue() / 1000d;
                double YAttuale = (Double) MultiCmd_posizione_Y_C2.getValue() / 1000d;
                double XPartenza = info_modifica.DeltaX_inizio;
                double YPartenza = info_modifica.DeltaY_inizio;

                double DeltaX = MathGeoTri.round5cent((float) (XAttuale - XPartenza));
                DeltaX = Math.floor(DeltaX * 100) / 100;
                double DeltaY = MathGeoTri.round5cent((float) (YAttuale - YPartenza));
                DeltaY = Math.floor(DeltaY * 100) / 100;
                double val = ((XAttuale - XPartenza) * (XAttuale - XPartenza) + (YAttuale - YPartenza) * (YAttuale - YPartenza));
                double distanza = MathGeoTri.round5cent((float) Math.sqrt(val));
                distanza = Math.floor(distanza * 100) / 100;

                TextView_xRel.setText("" + DeltaX);
                TextView_YRel.setText("" + DeltaY);
                TextView_Dist_Val.setText("" + distanza);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * TODO
     */
    private void Fai_Home(boolean Fai_killThread) {

        switch (step_Home) {
            case 0:
                MultiCmd_go_Home_c2.setValue(1.0d);
                sl.WriteItem(MultiCmd_go_Home_c2);
                step_Home = 10;
                break;
            case 10:
                sl.ReadItem(MultiCmd_go_Home_c2);
                if ((Double) MultiCmd_go_Home_c2.getValue() == 0.0d)
                    step_Home = 20;
                break;
            case 20:
                if(!Fai_killThread){
                    step_Home = 0;
                    info_modifica.comando = Info_modifica.Comando.HOME_DONE;
                }
                else{

                    step_Home = 30;
                }

                break;

            case 30:
                step_Home = 0;
                StopThread = true;

                // StopThread = true;
                 KillThread();

                finish();

            default:
                break;
        }
    }
    /**
     * TODO
     */
    private void Fai_Esci() {
        switch (step_Fai_Esci) {
            case 0:
                MultiCmd_go_Home_c2.setValue(1.0d);
                sl.WriteItem(MultiCmd_go_Home_c2);
                step_Fai_Esci = 10;
                break;
            case 10:
                sl.ReadItem(MultiCmd_go_Home_c2);
                if ((Double) MultiCmd_go_Home_c2.getValue() == 0.0d)
                    step_Fai_Esci = 20;
                break;
            case 20:
                step_Fai_Esci = 0;
                info_modifica.comando = Info_modifica.Comando.ESCI_DONE_AZZERAMENTO;
                break;
            default:
                break;
        }
    }
    /**
     * TODO
     *
     *
     */
    private void NacondoTastiDaPinzaAlta() {

        switch (step_NacondoTastiDaPinzaAlta){
            case 0:
                if((Double)MultiCmd_Vb4906_AppPinzaIntAltaC2.getValue() == 1.0d)
                    step_NacondoTastiDaPinzaAlta = 10;
                else
                    step_NacondoTastiDaPinzaAlta = 100;

                break;
            case 10:
                SpegniTutteIcone();
                step_NacondoTastiDaPinzaAlta = 20;

                break;
            case 20:
                if((Double)MultiCmd_Vb4906_AppPinzaIntAltaC2.getValue() == 0.0d)
                    step_NacondoTastiDaPinzaAlta = 0;

                break;

            case 100:
                Mostra_Tutte_Icone();
                step_NacondoTastiDaPinzaAlta = 120;
                break;
            case 120:
                if((Double)MultiCmd_Vb4906_AppPinzaIntAltaC2.getValue() == 1.0d)
                    step_NacondoTastiDaPinzaAlta = 0;

                break;

            default:
                break;


        }
    }
    /**
     * TODO
     *
     * @param activeStepIndex
     */
    private void ShowIndicePunto(int activeStepIndex) {
        if (activeStepIndex < 0)
            TextView_cnt_punti.setText("0");    //altrimenti mi scrive -1
        else
            TextView_cnt_punti.setText("" + ricetta.getActiveStepIndex());
    }
    /**
     * TODO
     */
    private void Scrivi_codice_HMI() {
        List<JamPointCode> codeStatus = ricetta.getActiveStepCodes();

        if (codeStatus.size() > 0) {

         //   Button_delete_code.setVisibility(View.VISIBLE);
            String stringa_codice = "Code: ";
            for (JamPointCode code : codeStatus) {
                String tipo_codice = code.tipoCodice.toString();

                switch (tipo_codice) {
                    case "OP1":
                        if (code.valori.size() == 1) {
                            if (code.valori.get(0).codeValueType == CodeValueType.OnOff) {
                                if(code.valori.get(0).currentValue.equals("VALUE0"))
                                    stringa_codice = stringa_codice + " OP1 OFF";
                                if(code.valori.get(0).currentValue.equals("VALUE1"))
                                    stringa_codice = stringa_codice + " OP1 ON";

                                break;
                            }
                        }
                        throw new UnsupportedOperationException();
                    case "OP2":
                        if (code.valori.size() == 1) {
                            if (code.valori.get(0).codeValueType == CodeValueType.OnOff) {
                                if(code.valori.get(0).currentValue.equals("VALUE0"))
                                    stringa_codice = stringa_codice + " OP2 OFF";
                                if(code.valori.get(0).currentValue.equals("VALUE1"))
                                    stringa_codice = stringa_codice + " OP2 ON";
                                break;
                            }
                        }
                        throw new UnsupportedOperationException();
                    case "OP3":
                        if (code.valori.size() == 1) {
                            if (code.valori.get(0).codeValueType == CodeValueType.OnOff) {
                                if(code.valori.get(0).currentValue.equals("VALUE0"))
                                    stringa_codice = stringa_codice + " OP3 OFF";
                                if(code.valori.get(0).currentValue.equals("VALUE1"))
                                    stringa_codice = stringa_codice + " OP3 ON";
                                break;
                            }
                        }
                        throw new UnsupportedOperationException();
                    case "SPLIT1":
                        if (code.valori.size() == 1) {
                            if (code.valori.get(0).codeValueType == CodeValueType.OnOff) {
                                if(code.valori.get(0).currentValue.equals("VALUE1"))
                                    stringa_codice = stringa_codice + " SPLIT 1 ON";

                                break;
                            }
                        }
                        throw new UnsupportedOperationException();
                    case "SPLIT2":
                        if (code.valori.size() == 1) {
                            if (code.valori.get(0).codeValueType == CodeValueType.OnOff) {
                                if(code.valori.get(0).currentValue.equals("VALUE1"))
                                    stringa_codice = stringa_codice + " SPLIT 2 ON";

                                break;
                            }
                        }
                        throw new UnsupportedOperationException();
                    case "SPEED_M8":
                        if (code.valori.size() == 1) {
                            if (code.valori.get(0).codeValueType == CodeValueType.Numeric) {
                                stringa_codice = stringa_codice + " SPEED " + code.valori.get(0).currentValue;
                                break;
                            }
                        }
                        throw new UnsupportedOperationException();
                    case "TENS_M8":
                        if (code.valori.size() == 1) {
                            if (code.valori.get(0).codeValueType == CodeValueType.Numeric) {
                                stringa_codice = stringa_codice + " TENSION " + code.valori.get(0).currentValue;
                                break;
                            }
                        }
                        throw new UnsupportedOperationException();
                    case "ANGOLO_ROT":
                        if (code.valori.size() == 2) {
                            if (code.valori.get(0).codeValueType == CodeValueType.Numeric && code.valori.get(1).codeValueType == CodeValueType.Numeric) {
                                stringa_codice = stringa_codice + "Angle: " + code.valori.get(0).currentValue + "; Stitches: " + code.valori.get(1).currentValue;
                                break;
                            }
                        }
                        throw new UnsupportedOperationException();
                    default:
                        throw new UnsupportedOperationException();
                }
            }
            TextView_Code.setText(stringa_codice);
        } else {
            TextView_Code.setText("Code:");

          //  Button_delete_code.setVisibility(View.GONE);
        }
    }
     /* Button for center the bitmap and reset the zoom
     *
             * @param view
     */
    public void btn_Center_on_click(View view) {
        myView.Center_Bitmap_Main(1.25F, 380, 5);
        myView.AggiornaCanvas(true);
    }

    /**
     * Button for zoom in the bitmap
     *
     * @param view
     */
    public void btn_ZoomPiu_on_click(View view) {
        myView.Zoom(0.1F);
        myView.AggiornaCanvas(true);
    }

    //#endregion ZoomBitMap

    /**
     * Button for zoom out the bitmap
     *
     * @param view
     */
    public void btn_ZoomMeno_on_click(View view) {
        myView.Zoom(-0.1F);
        myView.AggiornaCanvas(true);
    }

    class MyAndroidThread_Pattina implements Runnable {
        Activity activity;


        public MyAndroidThread_Pattina(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            while (true) {
                Thread_Running = true;

                try {
                    Thread.sleep((long) 10d);
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
                        MultiCmd_Vn3804_pagina_touch.setValue(1010.0d);
                        sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                    }

                    rc_error = false;
                    sl.Clear();


                    try{

                        sl.ReadItems(mci_array_read_all);


                        if (sl.getReturnCode() != 0 ) {
                            cnt_errori_connessione++;
                            //se non riceve bene i valori provo a chiudere e riaprire il Socket
                            sl.Close();
                            rc_error = true;
                        }

                    }catch (Exception err){
                        rc_error = true;
                    }

                    if (!rc_error) {
                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb4370_Motore_Pattina_Giu);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb4371_Motore_Pattina_Su);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb162PattinaPassoAvanti);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb161ZeroMotoreSuGiu);
                        Utility.GestiscoMci_Out_Toggle(sl,Mci_write_Vb152_Pattina_OnOff);
                        Utility.GestiscoMci_Out_Toggle(sl,Mci_write_Vb157_Pattina_PassoPasso);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb4901_start_cucitura_T2);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb4904_AppPedaleHmiC2);
                        Utility.ScrivoVbVnVq(sl, Mci_Vb_OutPiedino_su);
                        Utility.ScrivoVbVnVq(sl, Mci_write_Vb165HoPremutoPiuMeno);
                        Utility.GestiscoMci_Edge_Out(sl,Mci_write_Vb4902_Reset_Cuci_C2);

                       GestiscoFreccia(Mci_write_JogYMeno);
                        GestiscoFreccia(Mci_write_JogYPiu);
                        GestiscoFreccia(Mci_write_JogXPiu);
                        GestiscoFreccia(Mci_write_JogXMeno);

                        if (info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.SINGOLO || info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.VELOCE) {
                            StepSingolo(1);
                        }
                        if (info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.N_SALTO) {
                            StepSingolo(Info_StepPiuMeno.numeroRipetuto);
                        }
                        double X = (Double) MultiCmd_posizione_X_C2.getValue() / 1000d;
                        double Y = (Double) MultiCmd_posizione_Y_C2.getValue() / 1000d;
                        Coord_Pinza.set(X, Y, ricetta);

                        if (info_modifica.comando == Info_modifica.Comando.HOME) Fai_Home(false);
                        if (info_modifica.comando == Info_modifica.Comando.FAIHOME_AND_EXIT) Fai_Home(true);
                        if (info_modifica.comando == Info_modifica.Comando.ESCI) Fai_Esci();


                        AggiornaGuiDaThread();

                    }




                }else {
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
                    try{
                        TextView_cnt_errori_connessione.setText("Cnt errori connesione: " + cnt_errori_connessione);
                        if(!rc_error) {
                            Emergenza();
                            ControlloErrori();
                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb152_Pattina_OnOff, Button_pattina_on_off, "ic_pattina_onoff_press", "ic_pattina_onoff");
                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb157_Pattina_PassoPasso, Button_pattina_test, "ic_pattina_test_press", "ic_pattina_test");
                            Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb4904_AppPedaleHmiC2, Button_pedale_singolo, "pedale_singolo_press", "pedale_singolo");
                            ShowQuoteRelative();

                            if (Coord_Pinza.XCoord_precedente != Coord_Pinza.XCoordPosPinza || Coord_Pinza.YCoord_precedente != Coord_Pinza.YCoordPosPinza) {
                                myView.AggiornaCanvas(true);
                                Coord_Pinza.XCoord_precedente = Coord_Pinza.XCoordPosPinza;
                                Coord_Pinza.YCoord_precedente = Coord_Pinza.YCoordPosPinza;
                            }
                            Double Debug_mc_stati = (Double) Multicmd_Vn164_mc_stati_Pattina.getValue();
                            TextView_Debug_number.setText("Debug number: "+Debug_mc_stati);

                        }
                        NacondoTastiDaPinzaAlta();
                        Scrivi_codice_HMI();
                        if (info_modifica.comando == Info_modifica.Comando.ESCI_DONE_AZZERAMENTO) {
                            info_modifica.comando = Info_modifica.Comando.Null;
                            ricetta.clearActiveStep();  //imposta indice step a -1
                            ricetta.repair();  //ripara la ricetta nel caso ci siano degli errori di continuità o di coordinate
                            Run_Alert_dialog();
                        }
                        if (info_modifica.comando == Info_modifica.Comando.HOME_DONE) {
                            info_modifica.comando = Info_modifica.Comando.Null;
                            Mostra_Tutte_Icone();
                        }
                        ShowIndicePunto(ricetta.getActiveStepIndex());
                        TextView_tot_punti.setText("" + ricetta.getStepsCount());

                    } catch (Exception e) {

                    }
                }
            });
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
                            case 40:
                                TextView_errore.setText("");
                                break;

                            default:
                                break;
                        }
                        break;

                    case 40:
                        if (Mci_Vn2_allarmi_da_CN.valore_precedente == 0.0d || Mci_Vn2_allarmi_da_CN.valore_precedente == -1.0d) {
                            Mci_Vn2_allarmi_da_CN.valore_precedente = Double.valueOf(i);
                            Mci_Vn2_allarmi_da_CN.Fronte_positivo = true;
                            String[] tab_names = new String[]{};
                            String Stringa_allarme = "";

                            tab_names = getResources().getStringArray(R.array.allarmi_vn2);
                            Stringa_allarme = tab_names[i];

                            TextView_errore.setText(Stringa_allarme);
                            TextView_errore.setTextColor(Color.RED);
                        }


                        break;

                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Allarmi_Vn2 catch numero: " + i, Toast.LENGTH_SHORT).show();
            }
        }
    }


}
