package com.jam_int.jt882_m8;

import static com.jam_int.jt882_m8.Values.Machine_model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.jamint.ricette.CodeValue;
import com.jamint.ricette.CodeValueType;
import com.jamint.ricette.Element;
import com.jamint.ricette.ElementArc;
import com.jamint.ricette.ElementArcZigZag;
import com.jamint.ricette.ElementFeed;
import com.jamint.ricette.ElementLine;
import com.jamint.ricette.ElementZigZag;
import com.jamint.ricette.JamPointCode;
import com.jamint.ricette.JamPointStep;
import com.jamint.ricette.MathGeoTri;
import com.jamint.ricette.Ricetta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import communication.MultiCmdItem;
import communication.ShoppingList;
import communication.SmartAlarm;

public class Modifica_programma extends Activity {
    /**
     * On Activity result indexes
     */
    final private static int PAGE_UDF_T1_DX = 200;
    final private static int PAGE_UDF_T1_SX = 201;
    final private static int PAGE_UDF_T2_DX = 209;
    final private static int PAGE_UDF_T2_SX = 210;
    final private static int RESULT_PAGE_EMG = 101;
    final private static int RESULT_PAGE_LOAD_EEP = 102;
    final private static int RESULT_PAGE_CODE = 103;
    final private static int POPUPFOLDER = 104;
    /**
     * Shopping list for communicate with the PLC
     */
    ShoppingList sl;
    /**
     * Thread
     */
    Thread t1;
    /**
     * Draw
     */
    Dynamic_view myView;
    FrameLayout frame_canvas;
    Info_StepPiuMeno info_StepPiuMeno = new Info_StepPiuMeno();
    boolean onClickTastoEntitaPiu = false, onClickTastoEntitaMeno = false;
    Info_modifica info_modifica = new Info_modifica();
    String str_allarmi = "";
    /**
     * Things displayed on the bitmap
     */
    Ricetta ricetta;
    /**
     * Receiver that handle the code edit??? (It's not handled by the OnActivityResult function???)
     * <p>
     * TODO
     */
    private final BroadcastReceiver Code_MessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle_code = intent.getExtras();
            List<JamPointCode> codeReturn = (List<JamPointCode>) bundle_code.getSerializable("valoreCodice");

            if (codeReturn.size() > 0) {

                boolean add = true;
                for (JamPointCode code : codeReturn) {  //elenco codice restituiti dalla pagina code


                    List<JamPointCode> codeEsistenti = ricetta.getActiveStepCodes();    //elenco dei codici che già ci sono sul punto
                    for (JamPointCode code_esistente: codeEsistenti) {
                        if(code.tipoCodice.equals(code_esistente.tipoCodice)){      //se il tipo di codice esisteva già.... gli cambio il valore con quello nuovo
                            code_esistente.valori.get(0).currentValue = code.valori.get(0).currentValue;
                            add = false;
                        }
                    }



                    if(add) {   //se il tipo di codice non esisteva già lo aggiungo.
                        for (CodeValue codeVal : code.valori) {
                            ricetta.addActiveStepCode(code.tipoCodice, codeVal);
                        }
                    }
                    Aggiorna_canvas();
                }
            }
        }
    };
    ArrayList<Element> List_entita = new ArrayList<>();
    List<Element> ElemSelezionati = new ArrayList<Element>();
    /**
     * Receiver for handle the LP and height edit
     */
    private final BroadcastReceiver mMessageReceiver_KeyDialog = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle_txt = intent.getExtras();

            String valore = bundle_txt.getString("ret_valore");
            String ids = bundle_txt.getString("txtview_id");

            int id = Integer.valueOf(ids);

            if (id == TextView_valore_A.getId()) {
                TextView txtview = findViewById(id);
                txtview.setText(valore);
                try {
                    float nuovoPasso = Float.parseFloat(valore);
                    cambia_lunghezzaPunto_elemento(nuovoPasso);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (id == TextView_valore_B.getId()) {
                TextView txtview = findViewById(id);
                txtview.setText(valore);
                try {
                    float nuovaAltezza = Float.parseFloat(valore);
                    cambia_altezza_travetta(nuovaAltezza);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    Handler UpdateHandler = new Handler();
    CoordPosPinza Coord_Pinza = new CoordPosPinza();
    int step_Home = 0, Chiamante;
    int step_Fai_Esci = 0;
    /**
     * Folder choose for save the file
     */
    String Folder;
    /**
     * File xml loaded
     */
    String File_Xml_path;
    /**
     * UI components
     */
    Button Button_arrow_up, Button_freccia_giu, Button_arrow_right, Button_arrow_left, Button_arrow_up_right, Button_arrow_down_right, Button_arrow_down_left, Button_arrow_up_left, ButtonPuntoPiu, ButtonPuntoMeno,
            Button_tasto_home, Button_sposta, Button_feed, Button_codici, Button_888M, Button_linea, Button_arco3p, Button_spline, Button_travetta, Button_cancella, Button_tasto0, Button_tasto1, Button_tasto2,
            Button_tasto3, Button_tasto4, Button_tasto5, Button_tasto6, Button_tasto7, Button_tasto8, Button_tasto9, Button_tasto_punto, Button_tasto_del,
            Button_entita_piu, Button_entita_meno, Button_piu, Button_meno, Button_tasto_enter, Button_stretch_edge, Button_esc, Button_move_all,
            Button_raddrizza_arco, Button_raddrizza_linea, Button_Sgancio_ago, Button_exit, Button_traform_toLine, Button_traform_toArc, Button_traform_toZigZag, Button_traform_toFeed, Button_explode,
            Button_debug, Button_undo, Button_redo, Button_new, Button_delete_code,Button_JOG_SlowFast,Button_sposta_stretch;
    TextView TextView_Prog_name, TextView_XAss, TextView_YAss, TextView_xRel, TextView_YRel, TextView_cnt_punti, TextView_tot_punti, TextView_valore_A, TextView_valore_B, TextView_info, TextView_Code;
    ImageView ImageView;
    Boolean Thread_Running = false, StopThread = false, first_cycle = true, Allarme_mostrato = false;
    /**
     * PLC vars
     */
    MultiCmdItem MultiCmd_XY_fermi, MultiCmd_quota_destinazione_X, MultiCmd_quota_destinazione_Y, MultiCmd_Vb_OutPiedino_su, MultiCmd_Vb_OutPiedino_giu, MultiCmd_Status_Piedino,
            MultiCmd_Start_movimento_X, MultiCmd_Start_movimento_Y, MultiCmd_posizione_X, MultiCmd_posizione_Y, MultiCmd_Vn3804_pagina_touch,
            MultiCmd_go_Home, MultiCmd_JogYMeno, MultiCmd_JogYPiu, MultiCmd_start_Jog_incrementaleXPiu, MultiCmd_start_Jog_incrementaleXMeno,
            MultiCmd_start_Jog_incrementaleYPiu, MultiCmd_start_Jog_incrementaleYMeno, MultiCmd_incrementaleX, MultiCmd_incrementaleY, MultiCmd_tasto_verde,
            MultiCmd_CH1_in_emergenza, MultiCmd_JogXMeno, MultiCmd_JogXPiu, MultiCmd_jogXPiuYPiu, MultiCmd_jogXPiuYMeno, MultiCmd_jogXMenoYPiu, MultiCmd_jogXMenoYMeno,
            MultiCmd_Sblocca_Ago, MultiCmd_Quota_Assoluta_rotazione,  Multicmd_Vb4807_PinzeAlteDopoPC,Multicmd_Vb4907_PinzeAlteDopoPC_C2,
            MultiCmd_Vq_104_READ_FC_ind_X, MultiCmd_Vq_105_READ_FC_ava_X, MultiCmd_Vq_106_READ_FC_ind_Y, MultiCmd_Vq_107_READ_FC_ava_Y, MultiCmd_HmiMoveXY, MultiCmd_RichiestaPiedinoSu,
            MultiCmd_Vb4014_JogSlowFast;
    Mci_write Mci_write_quota_destinazione_X = new Mci_write(),
            Mci_write_quota_destinazione_Y = new Mci_write(),
            Mci_write_JogXMeno = new Mci_write(),
            Mci_write_JogXPiu = new Mci_write(),
            Mci_write_JogYMeno = new Mci_write(),
            Mci_write_JogYPiu = new Mci_write(),
            Mci_write_jogXPiuYPiu = new Mci_write(),
            Mci_write_jogXPiuYMeno = new Mci_write(),
            Mci_write_jogXMenoYPiu = new Mci_write(),
            Mci_write_jogXMenoYMeno = new Mci_write(),
            Mci_Vb_OutPiedino_su = new Mci_write(),

            Mci_write_Vb4014_JogSlowFast = new Mci_write(),
            Mci_Sblocca_Ago = new Mci_write();
          //  Mci_write_jog_Rotaz_dx = new Mci_write(),
         //   Mci_write_jog_Rotaz_sx = new Mci_write(),
         //   Mci_Vn3081_override_rotaz = new Mci_write();
    MultiCmdItem[] mci_array_read_all;
    /**
     * Default values for element height and LP
     */
    float old_A = 3.0f, old_B = 0.5f;
    /**
     * Lists of buttons
     */
    ArrayList<Button> Lista_pulsanti_comandi = new ArrayList<>();
    ArrayList<Button> Lista_pulsanti_comandi_numeri = new ArrayList<>();
    ArrayList<Button> Lista_pulsanti_comandi_frecce = new ArrayList<>();
    ArrayList<Button> Lista_pulsanti_PiuMeno = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_programma);

        // Setup the ShoppingList
        sl = SocketHandler.getSocket();
        sl.Clear();

        frame_canvas = findViewById(R.id.frameLayout);

        ImageView = findViewById(R.id.imageView_tasca);

        TextView_Prog_name = findViewById(R.id.textView_Prog_name);

        TextView_XAss = findViewById(R.id.textView_XAss);
        TextView_YAss = findViewById(R.id.textView_YAss);
        TextView_xRel = findViewById(R.id.textView_xRel);
        TextView_YRel = findViewById(R.id.textView_YRel);

        TextView_info = findViewById(R.id.textView_info);
        TextView_Code = findViewById(R.id.textView_Code);
        TextView_cnt_punti = findViewById(R.id.textView_cnt_punti);
        TextView_tot_punti = findViewById(R.id.textView_tot_punti);

        TextView_valore_A = findViewById(R.id.textView_valore_A);
        TextView_valore_B = findViewById(R.id.textView_valore_B);

        // TODO duplicated
        ButtonPuntoPiu = findViewById(R.id.button_piu);
        ButtonPuntoMeno = findViewById(R.id.button_meno);
        Button_piu = findViewById(R.id.button_piu);
        Button_meno = findViewById(R.id.button_meno);

        Button_arrow_up = findViewById(R.id.button_arrow_up);
        Button_freccia_giu = findViewById(R.id.button_freccia_giu);
        Button_arrow_right = findViewById(R.id.button_arrow_right_caric);
        Button_arrow_left = findViewById(R.id.button_arrow_left);
        Button_arrow_up_right = findViewById(R.id.button_arrow_up_right);
        Button_arrow_down_right = findViewById(R.id.button_arrow_down_right);
        Button_arrow_down_left = findViewById(R.id.button_arrow_down_left);
        Button_arrow_up_left = findViewById(R.id.button_arrow_up_left);

        Button_exit = findViewById(R.id.button_exit);

        Button_entita_piu = findViewById(R.id.button_entita_piu);
        Button_entita_meno = findViewById(R.id.button_entita_meno);

        Button_feed = findViewById(R.id.button_feed);
        Button_linea = findViewById(R.id.button_linea);
        Button_spline = findViewById(R.id.button_spline);
        Button_travetta = findViewById(R.id.button_travetta);
        Button_arco3p = findViewById(R.id.button_arco3p);

        Button_tasto0 = findViewById(R.id.button_tasto0);
        Button_tasto1 = findViewById(R.id.button_tasto1);
        Button_tasto2 = findViewById(R.id.button_tasto2);
        Button_tasto3 = findViewById(R.id.button_tasto3);
        Button_tasto4 = findViewById(R.id.button_tasto4);
        Button_tasto5 = findViewById(R.id.button_tasto5);
        Button_tasto6 = findViewById(R.id.button_tasto6);
        Button_tasto7 = findViewById(R.id.button_tasto7);
        Button_tasto8 = findViewById(R.id.button_tasto8);
        Button_tasto9 = findViewById(R.id.button_tasto9);

        Button_raddrizza_arco = findViewById(R.id.button_raddrizza_arco);
        Button_raddrizza_linea = findViewById(R.id.button_raddrizza_linea);

        Button_traform_toLine = findViewById(R.id.button_traform_toLine);
        Button_traform_toArc = findViewById(R.id.button_traform_toArc);
        Button_traform_toZigZag = findViewById(R.id.button_traform_toZigZag);
        Button_traform_toFeed = findViewById(R.id.button_traform_toFeed);
        Button_explode = findViewById(R.id.button_explode);

        Button_debug = findViewById(R.id.button_debug);
        Button_esc = findViewById(R.id.button_esc);
        Button_undo = findViewById(R.id.button_undo);
        Button_redo = findViewById(R.id.button_redo);
        Button_new = findViewById(R.id.button_new);

        Button_tasto_punto = findViewById(R.id.button_tasto_punto);
        Button_tasto_del = findViewById(R.id.button_tasto_del);
        Button_move_all = findViewById(R.id.button_move_all);
        Button_tasto_enter = findViewById(R.id.button_tasto_enter);
        Button_stretch_edge = findViewById(R.id.button_stretch_edge);
        Button_Sgancio_ago = findViewById(R.id.button_sblocca_ago);
        Button_tasto_home = findViewById(R.id.button_tasto_home);
        Button_sposta = findViewById(R.id.button_sposta);
        Button_codici = findViewById(R.id.button_codici);
        Button_delete_code = findViewById(R.id.button_delete_code);
        Button_888M = findViewById(R.id.button_888M);
        Button_cancella = findViewById(R.id.button_cancella);

        Button_JOG_SlowFast = findViewById(R.id.button_JOG_SlowFast);
        Button_sposta_stretch = findViewById(R.id.button_sposta_stretch);





        Crea_Liste_pulsanti();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Chiamante = extras.getInt("Chiamante");
        }

        try {
            switch (Chiamante) {
                case PAGE_UDF_T1_DX:
                    File_Xml_path = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram_R", getApplicationContext());
                    break;
                case PAGE_UDF_T1_SX:
                    File_Xml_path = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram_L", getApplicationContext());
                    break;
                case PAGE_UDF_T2_DX:
                    File_Xml_path = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram_R_T2", getApplicationContext());
                    break;
                case PAGE_UDF_T2_SX:
                    File_Xml_path = Info_file.Leggi_campo("storage/emulated/0/JamData/info_Jam.txt", "LastProgram", "null", null, null, "LastProgram_L_T2", getApplicationContext());
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //prendo la cartella dove è contenuto il file
        File f = new File(File_Xml_path);
        Folder = f.getParent();




        // Write the program name
        String filename = SubString.After(File_Xml_path, "/");
        TextView_Prog_name.setText(SubString.Before(filename, "."));



        switch (Chiamante) {
            case PAGE_UDF_T1_DX:
            case   PAGE_UDF_T1_SX:
                MultiCmd_XY_fermi = sl.Add("Io", 1, MultiCmdItem.dtVB, 4058, MultiCmdItem.dpNONE);
                MultiCmd_Vb_OutPiedino_su = sl.Add("Io", 1, MultiCmdItem.dtVB, 1003, MultiCmdItem.dpNONE);
                MultiCmd_Vb_OutPiedino_giu = sl.Add("Io", 1, MultiCmdItem.dtVB, 1004, MultiCmdItem.dpNONE);
                MultiCmd_quota_destinazione_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7002, MultiCmdItem.dpNONE);
                MultiCmd_quota_destinazione_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7022, MultiCmdItem.dpNONE);
                MultiCmd_Status_Piedino = sl.Add("Io", 1, MultiCmdItem.dtDO, 17, MultiCmdItem.dpNONE);
                MultiCmd_Start_movimento_X = sl.Add("Io", 1, MultiCmdItem.dtVB, 7005, MultiCmdItem.dpNONE);
                MultiCmd_Start_movimento_Y = sl.Add("Io", 1, MultiCmdItem.dtVB, 7025, MultiCmdItem.dpNONE);
                MultiCmd_posizione_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 51, MultiCmdItem.dpNONE);
                MultiCmd_posizione_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 52, MultiCmdItem.dpNONE);
                MultiCmd_go_Home = sl.Add("Io", 1, MultiCmdItem.dtVB, 52, MultiCmdItem.dpNONE);
                MultiCmd_jogXPiuYPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 54, MultiCmdItem.dpNONE);
                MultiCmd_jogXPiuYMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 55, MultiCmdItem.dpNONE);
                MultiCmd_jogXMenoYPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 56, MultiCmdItem.dpNONE);
                MultiCmd_jogXMenoYMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 57, MultiCmdItem.dpNONE);
                MultiCmd_JogXMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 62, MultiCmdItem.dpNONE);
                MultiCmd_JogXPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 63, MultiCmdItem.dpNONE);
                MultiCmd_JogYMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 64, MultiCmdItem.dpNONE);
                MultiCmd_JogYPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 65, MultiCmdItem.dpNONE);
                MultiCmd_start_Jog_incrementaleXPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7003, MultiCmdItem.dpNONE);
                MultiCmd_start_Jog_incrementaleXMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7004, MultiCmdItem.dpNONE);
                MultiCmd_start_Jog_incrementaleYPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7023, MultiCmdItem.dpNONE);
                MultiCmd_start_Jog_incrementaleYMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7024, MultiCmdItem.dpNONE);
                MultiCmd_incrementaleX = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7001, MultiCmdItem.dpNONE);
                MultiCmd_incrementaleY = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7021, MultiCmdItem.dpNONE);
                MultiCmd_Sblocca_Ago = sl.Add("Io", 1, MultiCmdItem.dtVB, 1018, MultiCmdItem.dpNONE);
                MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);
                MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 21, MultiCmdItem.dpNONE);
                MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
                Multicmd_Vb4807_PinzeAlteDopoPC = sl.Add("Io", 1, MultiCmdItem.dtVB, 4807, MultiCmdItem.dpNONE);
                Multicmd_Vb4907_PinzeAlteDopoPC_C2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4907, MultiCmdItem.dpNONE);
                MultiCmd_Vq_104_READ_FC_ind_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 104, MultiCmdItem.dpNONE);
                MultiCmd_Vq_105_READ_FC_ava_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 105, MultiCmdItem.dpNONE);
                MultiCmd_Vq_106_READ_FC_ind_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 106, MultiCmdItem.dpNONE);
                MultiCmd_Vq_107_READ_FC_ava_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 107, MultiCmdItem.dpNONE);
                MultiCmd_HmiMoveXY = sl.Add("Io", 1, MultiCmdItem.dtVB, 72, MultiCmdItem.dpNONE);
                MultiCmd_RichiestaPiedinoSu = sl.Add("Io", 1, MultiCmdItem.dtVB, 74, MultiCmdItem.dpNONE);
                MultiCmd_Vb4014_JogSlowFast = sl.Add("Io", 1, MultiCmdItem.dtVB, 4014, MultiCmdItem.dpNONE);
                break;
            case PAGE_UDF_T2_DX:
            case PAGE_UDF_T2_SX:
                MultiCmd_XY_fermi = sl.Add("Io", 1, MultiCmdItem.dtVB, 4059, MultiCmdItem.dpNONE);
                MultiCmd_Vb_OutPiedino_su = sl.Add("Io", 1, MultiCmdItem.dtVB, 2003, MultiCmdItem.dpNONE);
                MultiCmd_Vb_OutPiedino_giu = sl.Add("Io", 1, MultiCmdItem.dtVB, 2004, MultiCmdItem.dpNONE);
                MultiCmd_quota_destinazione_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7102, MultiCmdItem.dpNONE);
                MultiCmd_quota_destinazione_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7122, MultiCmdItem.dpNONE);

                MultiCmd_Start_movimento_X = sl.Add("Io", 1, MultiCmdItem.dtVB, 7105, MultiCmdItem.dpNONE);
                MultiCmd_Start_movimento_Y = sl.Add("Io", 1, MultiCmdItem.dtVB, 7125, MultiCmdItem.dpNONE);
                MultiCmd_posizione_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 56, MultiCmdItem.dpNONE);
                MultiCmd_posizione_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 57, MultiCmdItem.dpNONE);
                MultiCmd_go_Home  = sl.Add("Io", 1, MultiCmdItem.dtVB, 53, MultiCmdItem.dpNONE);
                MultiCmd_jogXPiuYPiu  = sl.Add("Io", 1, MultiCmdItem.dtVB, 58, MultiCmdItem.dpNONE);
                MultiCmd_jogXPiuYMeno  = sl.Add("Io", 1, MultiCmdItem.dtVB, 59, MultiCmdItem.dpNONE);
                MultiCmd_jogXMenoYPiu  = sl.Add("Io", 1, MultiCmdItem.dtVB, 60, MultiCmdItem.dpNONE);
                MultiCmd_jogXMenoYMeno  = sl.Add("Io", 1, MultiCmdItem.dtVB, 61, MultiCmdItem.dpNONE);
                MultiCmd_JogXMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 66, MultiCmdItem.dpNONE);
                MultiCmd_JogXPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 67, MultiCmdItem.dpNONE);
                MultiCmd_JogYMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 68, MultiCmdItem.dpNONE);
                MultiCmd_JogYPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 69, MultiCmdItem.dpNONE);
                MultiCmd_start_Jog_incrementaleXPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7103, MultiCmdItem.dpNONE);
                MultiCmd_start_Jog_incrementaleXMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7104, MultiCmdItem.dpNONE);
                MultiCmd_start_Jog_incrementaleYPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 7123, MultiCmdItem.dpNONE);
                MultiCmd_start_Jog_incrementaleYMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 7124, MultiCmdItem.dpNONE);
                MultiCmd_incrementaleX = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7101, MultiCmdItem.dpNONE);
                MultiCmd_incrementaleY = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7121, MultiCmdItem.dpNONE);
                MultiCmd_Sblocca_Ago = sl.Add("Io", 1, MultiCmdItem.dtVB, 2018, MultiCmdItem.dpNONE);
                MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);
                MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 21, MultiCmdItem.dpNONE);
                MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
                Multicmd_Vb4807_PinzeAlteDopoPC = sl.Add("Io", 1, MultiCmdItem.dtVB, 4807, MultiCmdItem.dpNONE);
                Multicmd_Vb4907_PinzeAlteDopoPC_C2 = sl.Add("Io", 1, MultiCmdItem.dtVB, 4907, MultiCmdItem.dpNONE);
                MultiCmd_Vq_104_READ_FC_ind_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 104, MultiCmdItem.dpNONE);
                MultiCmd_Vq_105_READ_FC_ava_X = sl.Add("Io", 1, MultiCmdItem.dtVQ, 105, MultiCmdItem.dpNONE);
                MultiCmd_Vq_106_READ_FC_ind_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 106, MultiCmdItem.dpNONE);
                MultiCmd_Vq_107_READ_FC_ava_Y = sl.Add("Io", 1, MultiCmdItem.dtVQ, 107, MultiCmdItem.dpNONE);
                MultiCmd_HmiMoveXY = sl.Add("Io", 1, MultiCmdItem.dtVB, 73, MultiCmdItem.dpNONE);
                MultiCmd_RichiestaPiedinoSu = sl.Add("Io", 1, MultiCmdItem.dtVB, 75, MultiCmdItem.dpNONE);
                MultiCmd_Vb4014_JogSlowFast = sl.Add("Io", 1, MultiCmdItem.dtVB, 4014, MultiCmdItem.dpNONE);
                switch (Machine_model) {
                    case "JT882M":
                        MultiCmd_Status_Piedino = sl.Add("Io", 1, MultiCmdItem.dtDO,34, MultiCmdItem.dpNONE);
                        break;
                    case "JT882MA": //Argentina con 4 schede IO Belli
                    case "JT882MB": //macchina inclinata con moduli IO Sipro
                        MultiCmd_Status_Piedino = sl.Add("Io", 1, MultiCmdItem.dtDO,49, MultiCmdItem.dpNONE);
                        break;

                    default:
                        break;
                }

                break;

            default:
                break;

        }


        try {
            Load_XML(File_Xml_path);

            ricetta.activeStepIndex = 0;
            Coord_Pinza.XCoordPosPinza = ricetta.pcX;
            Coord_Pinza.YCoordPosPinza = ricetta.pcY;
            TextView_XAss.setText("" + (Double)Coord_Pinza.XCoordPosPinza);
            TextView_YAss.setText("" + (Double)Coord_Pinza.YCoordPosPinza);


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Errore XML in ingresso", Toast.LENGTH_SHORT).show();
        }

        Mci_write_quota_destinazione_X.mci = MultiCmd_quota_destinazione_X;
        Mci_write_quota_destinazione_X.write_flag = false;

        Mci_write_quota_destinazione_Y.mci = MultiCmd_quota_destinazione_Y;
        Mci_write_quota_destinazione_Y.write_flag = false;

        Mci_write_JogXMeno.mci = MultiCmd_JogXMeno;
        Mci_write_JogXMeno.write_flag = false;

        Mci_write_JogXPiu.mci = MultiCmd_JogXPiu;
        Mci_write_JogXPiu.write_flag = false;

        Mci_write_JogYMeno.mci = MultiCmd_JogYMeno;
        Mci_write_JogYMeno.write_flag = false;

        Mci_write_JogYPiu.mci = MultiCmd_JogYPiu;
        Mci_write_JogYPiu.write_flag = false;

        Mci_write_jogXPiuYPiu.mci = MultiCmd_jogXPiuYPiu;
        Mci_write_jogXPiuYPiu.write_flag = false;

        Mci_write_jogXPiuYMeno.mci = MultiCmd_jogXPiuYMeno;
        Mci_write_jogXPiuYMeno.write_flag = false;

        Mci_write_jogXMenoYPiu.mci = MultiCmd_jogXMenoYPiu;
        Mci_write_jogXMenoYPiu.write_flag = false;

        Mci_write_jogXMenoYMeno.mci = MultiCmd_jogXMenoYMeno;
        Mci_write_jogXMenoYMeno.write_flag = false;

        Mci_Vb_OutPiedino_su.mci = MultiCmd_Vb_OutPiedino_su;
        Mci_Vb_OutPiedino_su.write_flag = false;

        Mci_Sblocca_Ago.mci = MultiCmd_Sblocca_Ago;
        Mci_Sblocca_Ago.write_flag = false;

        Mci_write_Vb4014_JogSlowFast.mci = MultiCmd_Vb4014_JogSlowFast;
        Mci_write_Vb4014_JogSlowFast.write_flag = false;

       // Mci_write_jog_Rotaz_sx.mci = MultiCmd_jog_Rotaz_sx;
      //  Mci_write_jog_Rotaz_sx.write_flag = false;

      ////  Mci_write_jog_Rotaz_dx.mci = MultiCmd_jog_Rotaz_dx;
      //  Mci_write_jog_Rotaz_dx.write_flag = false;

      //  Mci_Vn3081_override_rotaz.mci = MultiCmd_Vn3081_override_rotaz;
      //  Mci_Vn3081_override_rotaz.write_flag = false;

        mci_array_read_all = new MultiCmdItem[]{MultiCmd_XY_fermi, MultiCmd_Status_Piedino, MultiCmd_posizione_X, MultiCmd_posizione_Y, MultiCmd_tasto_verde, MultiCmd_CH1_in_emergenza,
                };

        Init_Eventi();

        TextView_tot_punti.setText(String.valueOf(ricetta.getStepsCount()));

        TextView_xRel.setVisibility(View.GONE);
        TextView_YRel.setVisibility(View.GONE);

        // Init the edge buttons
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogYPiu, Button_arrow_up, "ic_up_press", "ic_up", getApplicationContext(), 100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogYMeno, Button_freccia_giu, "ic_down_press", "ic_down", getApplicationContext(), 100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogXPiu, Button_arrow_right, "ic_right_press", "ic_right", getApplicationContext(), 100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogXMeno, Button_arrow_left, "ic_left_press", "ic_left", getApplicationContext(), 100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_jogXMenoYMeno, Button_arrow_up_right, "freccia_su_dx_b", "freccia_su_dx_a", getApplicationContext(), 100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_jogXMenoYPiu, Button_arrow_down_right, "freccia_giu_dx_b", "freccia_giu_dx_a", getApplicationContext(), 100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_jogXPiuYPiu, Button_arrow_down_left, "freccia_giu_sx_b", "freccia_giu_sx_a", getApplicationContext(), 100);
        EdgeButton.CreaEdgeButton_Frecce(Mci_write_jogXPiuYMeno, Button_arrow_up_left, "freccia_su_sx_b", "freccia_su_sx_a", getApplicationContext(), 100);

        Toggle_Button.CreaToggleButton(Mci_Sblocca_Ago, Button_Sgancio_ago, "ic_sblocca_ago_press", "ic_sblocca_ago", getApplicationContext(), sl);
        Toggle_Button.CreaToggleButton(Mci_write_Vb4014_JogSlowFast, Button_JOG_SlowFast, "ic_slow", "ic_fast", getApplicationContext(), sl);

        //faccio partire il broadcast per ricevere eventuale codice premuto (qui parte sempre ma solo una volta)
        LocalBroadcastManager.getInstance(this).registerReceiver(Code_MessageReceiver, new IntentFilter("CodeDialog_exit"));
        //faccio partire il broadcast per ricevere eventuale ritorno del cambio della lunghezza punto (qui parte sempe ma solo una volta)
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_KeyDialog, new IntentFilter("ret_valore"));

        if (!Thread_Running) {
            StopThread = false;
            MyAndroidThread_Modifica myTask = new MyAndroidThread_Modifica(Modifica_programma.this);
            t1 = new Thread(myTask, "Main myTask");
            t1.setName("Thread_modifica_programma");
            t1.start();
            Log.d("JAM TAG", "Modifica programma Thread from OnCreate");
        }
    }

    /**
     * Init events of Buttons step +/- and textview A and B
     */
    private void Init_Eventi() {
        ButtonPuntoPiu.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (info_StepPiuMeno.tipo_spostamento != Info_StepPiuMeno.Tipo_spostamento.N_SALTO) {   //se ripremo + durante lo spostamento di n punti allora lo fermo
                        if (info_StepPiuMeno.MacStati_StepSingolo == 0) {
                            info_StepPiuMeno.MacStati_StepVeloce = 0; //pulisco

                            if ((Double) MultiCmd_XY_fermi.getValue() == 1.0d) {



                                if (Utility.isNumeric((String) TextView_info.getText())) {
                                    try {
                                        Info_StepPiuMeno.numeroRipetuto = Integer.parseInt((String) TextView_info.getText());
                                        if (Info_StepPiuMeno.numeroRipetuto > 0) {
                                            info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.N_SALTO;
                                            info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.GO;
                                            info_StepPiuMeno.direzione = Info_StepPiuMeno.Direzione.AVANTI;

                                            info_StepPiuMeno.MacStati_StepSingolo = 10;

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {


                                    info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.SINGOLO;
                                    info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.GO;
                                    info_StepPiuMeno.direzione = Info_StepPiuMeno.Direzione.AVANTI;

                                    info_StepPiuMeno.MacStati_StepSingolo = 10;
                                }
                                info_modifica.QuoteRelativeAttive = true;

                                info_modifica.DeltaX_inizio = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
                                info_modifica.DeltaY_inizio = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;
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
                    if (info_StepPiuMeno.tipo_spostamento != Info_StepPiuMeno.Tipo_spostamento.N_SALTO) {   //se ripremo + durante lo spostamento di n punti allora lo fermo
                        if (info_StepPiuMeno.MacStati_StepSingolo == 0) {
                            info_StepPiuMeno.MacStati_StepVeloce = 0; //pulisco

                            if ((Double) MultiCmd_XY_fermi.getValue() == 1.0d) {



                                if (Utility.isNumeric((String) TextView_info.getText())) {
                                    try {
                                        Info_StepPiuMeno.numeroRipetuto = Integer.parseInt((String) TextView_info.getText());
                                        if (Info_StepPiuMeno.numeroRipetuto > 0) {
                                            info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.N_SALTO;
                                            info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.GO;
                                            info_StepPiuMeno.direzione = Info_StepPiuMeno.Direzione.DIETRO;

                                            info_StepPiuMeno.MacStati_StepSingolo = 10;

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {

                                    info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.SINGOLO;
                                    info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.GO;
                                    info_StepPiuMeno.direzione = Info_StepPiuMeno.Direzione.DIETRO;

                                    info_StepPiuMeno.MacStati_StepSingolo = 10;
                                }
                                info_modifica.QuoteRelativeAttive = true;

                                info_modifica.DeltaX_inizio = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
                                info_modifica.DeltaY_inizio = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;

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

        TextView_valore_A.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog KeyD_A = new KeyDialog();
                    KeyDialog.Lancia_KeyDialogo(null, Modifica_programma.this, TextView_valore_A, 5, 0.1, true, false, 0d, false, "ret_valore", false,"");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                }
                return false;
            }
        });
        TextView_valore_B.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog KeyD_B = new KeyDialog();
                    KeyDialog.Lancia_KeyDialogo(null, Modifica_programma.this, TextView_valore_B, 5, 0.1, true, false, 0d, false, "ret_valore", false,"");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                }
                return false;
            }
        });
    }

    /**
     * Function for fill the list of buttons
     */
    private void Crea_Liste_pulsanti() {
        //comandi
        Lista_pulsanti_comandi.add(Button_tasto_home);
        Lista_pulsanti_comandi.add(Button_sposta);
        Lista_pulsanti_comandi.add(Button_feed);
        Lista_pulsanti_comandi.add(Button_sposta_stretch);
        Lista_pulsanti_comandi.add(Button_codici);
        Lista_pulsanti_comandi.add(Button_888M);
        Lista_pulsanti_comandi.add(Button_linea);
        Lista_pulsanti_comandi.add(Button_arco3p);
        Lista_pulsanti_comandi.add(Button_spline);
        Lista_pulsanti_comandi.add(Button_travetta);
        Lista_pulsanti_comandi.add(Button_cancella);
        Lista_pulsanti_comandi.add(Button_stretch_edge);
        Lista_pulsanti_comandi.add((Button_move_all));
        Lista_pulsanti_comandi.add((Button_raddrizza_arco));
        Lista_pulsanti_comandi.add((Button_raddrizza_linea));
        Lista_pulsanti_comandi.add((Button_Sgancio_ago));
        Lista_pulsanti_comandi.add((Button_delete_code));

        //frecce
        Lista_pulsanti_comandi_frecce.add(Button_arrow_up);
        Lista_pulsanti_comandi_frecce.add(Button_freccia_giu);
        Lista_pulsanti_comandi_frecce.add(Button_arrow_right);
        Lista_pulsanti_comandi_frecce.add(Button_arrow_left);
        Lista_pulsanti_comandi_frecce.add(Button_arrow_up_right);
        Lista_pulsanti_comandi_frecce.add(Button_arrow_down_right);
        Lista_pulsanti_comandi_frecce.add(Button_arrow_down_left);
        Lista_pulsanti_comandi_frecce.add(Button_arrow_up_left);
        Lista_pulsanti_comandi_frecce.add(Button_JOG_SlowFast);

        //numeri
        Lista_pulsanti_comandi_numeri.add(Button_tasto0);
        Lista_pulsanti_comandi_numeri.add(Button_tasto1);
        Lista_pulsanti_comandi_numeri.add(Button_tasto2);
        Lista_pulsanti_comandi_numeri.add(Button_tasto3);
        Lista_pulsanti_comandi_numeri.add(Button_tasto4);
        Lista_pulsanti_comandi_numeri.add(Button_tasto5);
        Lista_pulsanti_comandi_numeri.add(Button_tasto6);
        Lista_pulsanti_comandi_numeri.add(Button_tasto7);
        Lista_pulsanti_comandi_numeri.add(Button_tasto8);
        Lista_pulsanti_comandi_numeri.add(Button_tasto9);
        Lista_pulsanti_comandi_numeri.add(Button_tasto_punto);
        Lista_pulsanti_comandi_numeri.add(Button_tasto_del);

        // + -
        Lista_pulsanti_PiuMeno.add(Button_entita_piu);
        Lista_pulsanti_PiuMeno.add(Button_entita_meno);
        Lista_pulsanti_PiuMeno.add(Button_piu);
        Lista_pulsanti_PiuMeno.add(Button_meno);
    }

    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
    }

    @Override                   //your activity is no longer visible to the user
    public void onStop() {
        super.onStop();
    }

    //#region ButtonsEvents

    //#region ZoomBitMap

    @Override                   //your activity is no longer visible to the user
    public void onDestroy() {
        super.onDestroy();

        KillThread();
    }

    /**
     * Button for center the bitmap and reset the zoom
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

    /**
     * Button for reset the needle position
     *
     * @param view
     */
    public void on_click_move_to_home(View view) {
        ricetta.clearActiveStep();  //imposta indice step a -1
        info_StepPiuMeno.MacStati_StepSingolo = 0; //pulisco
        SpegniTutteIcone();
     //   Mci_Vn3081_override_rotaz.valore = 1000d;
    //    Mci_Vn3081_override_rotaz.write_flag = true;
        info_modifica.comando = Info_modifica.Comando.HOME;
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
     * Button for clear the bitmap and create a new Ricetta
     *
     * @param view
     */
    public void onclick_New(View view) {
        ricetta = new Ricetta(Values.plcType);
        ricetta.pcX = 0.1f;
        ricetta.pcY = 0.1f;
        ricetta.clearActiveStep();  //imposta indice step a -1
        ricetta.setDrawPosition(new PointF(0.1f, 0f));

        Aggiorna_canvas();
    }

    /**
     * Button for exit from this activity
     *
     * @param view
     */
    public void onclickExit(final View view) {
        SpegniTutteIcone();
        Button_exit.setVisibility(View.GONE);
        if (Values.Debug_mode) info_modifica.comando = Info_modifica.Comando.ESCI_DONE_AZZERAMENTO;
        else
            info_modifica.comando = Info_modifica.Comando.ESCI;
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
        UpdateButtonsVisibility(ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL);
        TextView_info.setText("");
        Mci_Vb_OutPiedino_su.valore = 1.0d;
        Mci_Vb_OutPiedino_su.write_flag = true;
    }

    /**
     * Button for go to the next entity
     *
     * @param view
     */
    public void on_Click_Tasto_Entita_piu(View view) {
        onClickTastoEntitaPiu = true;
    }

    /**
     * Button for go to the previous entity
     *
     * @param view
     */
    public void on_Click_Tasto_Entita_meno(View view) {
        onClickTastoEntitaMeno = true;
    }

    //#region NumericPad

    //#region ButtonNumbers

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
                    case ARCO3P_0:  //da finire

                        info_modifica.X_Middle = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
                        info_modifica.Y_Middle = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;

                        TextView_info.setText(getString(R.string.Arco3P_p3));  //Muovere usando le frecce poi premi Enter
                        info_modifica.QuoteRelativeAttive = true;
                        info_modifica.comando = Info_modifica.Comando.ARCO3P_1;

                        break;
                    case ARCO3P_1:  //da finire
                        info_modifica.X_End = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
                        info_modifica.Y_End = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;
                        TextView_info.setText(getString(R.string.DigitaLP) + " (" + old_A + ")");  //Digitare lunghezza punto
                        info_modifica.comando = Info_modifica.Comando.ARCO3P_2;
                        break;
                    case ARCO3P_2:

                        float LP_arco = old_A;
                        if (Utility.isNumeric((String) TextView_info.getText())) {
                            try {
                                LP_arco = Float.parseFloat((String) TextView_info.getText());
                                old_A = LP_arco;
                            } catch (Exception e) {
                                e.printStackTrace();
                                TextView_info.setText(getString(R.string.Errore));
                            }
                        } else {
                            LP_arco = old_A;
                        }

                        //daniele 18/06/20
                        //se ho raggiunto il punto finale dell'arco con il tasto + porto in dietro lo step attivo altrimenti no.
                        int idx_attivo = ricetta.getActiveStepIndex();
                        if (idx_attivo > info_modifica.id_punto_inizio_modifica) {       //se ho raggiunto il punto finale dell'arco con il tasto +
                            for (int i = idx_attivo; i > info_modifica.id_punto_inizio_modifica + 1; i--) {
                                ricetta.goToPreviousStep();
                            }
                        }

                        idx_attivo = ricetta.getActiveStepIndex();
                        if (idx_attivo >= info_modifica.id_punto_inizio_modifica) {
                            info_modifica.comando = Info_modifica.Comando.Null;
                            Element el = ricetta.drawArcTo(new PointF(info_modifica.X_Middle, info_modifica.Y_Middle), new PointF(info_modifica.X_End, info_modifica.Y_End), LP_arco);
                            el.passo = LP_arco;
                            TextView_info.setText(getString(R.string.Fatto));
                        } else {
                            TextView_info.setText(getString(R.string.Errore));
                        }

                        UpdateButtonsVisibility(ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL);
                        info_modifica.QuoteRelativeAttive = false;
                        Aggiorna_canvas();

                        Mci_Vb_OutPiedino_su.valore = 1.0d;
                        Mci_Vb_OutPiedino_su.write_flag = true;
                        break;
                    case FEED:
                        try {
                            double Xfinale = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                            double Yfinale = (Double) MultiCmd_posizione_Y.getValue() / 1000d;

                            JamPointStep StepAttuale = ricetta.getActiveStep();
                            if (StepAttuale == null || ricetta.elements.size() == 0) {   //probabilemnte è il primo feed di un programma vutot
                                float x_f = MathGeoTri.round5cent(info_modifica.DeltaX_inizio);
                                float y_f = MathGeoTri.round5cent(info_modifica.DeltaY_inizio);
                                ricetta.setDrawPosition(new PointF(x_f, y_f));
                                ricetta.pcX = x_f;
                                ricetta.pcY = y_f;
                            } else
                                ricetta.setDrawPosition(new PointF(StepAttuale.p.x, StepAttuale.p.y));

                            ricetta.drawFeedTo(new PointF(((float) Xfinale), ((float) Yfinale)));

                            info_modifica.comando = Info_modifica.Comando.Null;
                            UpdateButtonsVisibility(ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL);
                            info_modifica.QuoteRelativeAttive = false;
                            Aggiorna_canvas();
                            TextView_info.setText(getString(R.string.Fatto));  //
                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            TextView_info.setText(getString(R.string.Errore));
                        }

                        break;
                    case CANCELLA:
                        risultato = false;
                        try {
                            if (ElemSelezionati.size() == 0) {   //Steps
                                if (ricetta.getActiveStepIndex() == info_modifica.id_punto_inizio_modifica) {   //un punto
                                    risultato = ricetta.deleteActiveStep();
                                    List<JamPointCode> ListCodici = new ArrayList<>();
                                    ListCodici = ricetta.checkInvalidCodes(true);
                                    if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                    ricetta.clearInvalidCodes();
                                    info_modifica.comando = Info_modifica.Comando.Null;
                                } else   //più punti
                                {
                                    ricetta.selectionStepEnd();
                                    risultato = ricetta.deleteSelectedSteps();
                                    List<JamPointCode> ListCodici = new ArrayList<>();
                                    ListCodici = ricetta.checkInvalidCodes(true);
                                    if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                    ricetta.clearInvalidCodes();
                                    info_modifica.comando = Info_modifica.Comando.Null;
                                }

                                ricetta.selectionStepClear();
                                info_modifica.comando = Info_modifica.Comando.Null;
                            }

                            if (risultato) {
                                TextView_info.setText(getString(R.string.Fatto));
                                Aggiorna_canvas();
                            } else {
                                TextView_info.setText(getString(R.string.Errore));
                                info_modifica = new Info_modifica();
                            }

                            UpdateButtonsVisibility(ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL);

                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            TextView_info.setText(getString(R.string.Errore));
                        }
                        break;
                    case LINEA:
                        float Xfinale = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
                        float Yfinale = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;

                        TextView_info.setText(getString(R.string.DigitaLP) + " (" + old_A + ")");  //Digitare lunghezza punto

                        if (Math.abs(Xfinale - info_modifica.DeltaX_inizio) > 0 || Math.abs(Yfinale - info_modifica.DeltaY_inizio) > 0) {
                            info_modifica.comando = Info_modifica.Comando.LINEA1;
                            info_modifica.DeltaX_inizio = Xfinale;
                            info_modifica.DeltaY_inizio = Yfinale;
                        } else {
                            //non ho mosso le frecce allora nnullo il comando
                            info_modifica.comando = Info_modifica.Comando.Null;
                            UpdateButtonsVisibility(ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL);
                            info_modifica = new Info_modifica();
                            TextView_info.setText("");
                        }
                        break;
                    case LINEA1:
                        try {
                            if (Utility.isNumeric((String) TextView_info.getText())) {
                                try {
                                    float LP = Float.parseFloat((String) TextView_info.getText());
                                    info_modifica.LP = LP;
                                    old_A = LP;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    TextView_info.setText(getString(R.string.Errore));
                                }
                            } else {
                                info_modifica.LP = old_A;
                            }

                            info_modifica.QuoteRelativeAttive = false;

                            float X = info_modifica.DeltaX_inizio;
                            float Y = info_modifica.DeltaY_inizio;

                            JamPointStep StepAttuale = ricetta.getActiveStep();
                            if (StepAttuale == null || ricetta.elements.size() == 0) {   //probabilemnte è la prima linea di un programma vuoto
                                ricetta.setDrawPosition(new PointF(info_modifica.X_Start,info_modifica.Y_Start));
                                ricetta.pcX = info_modifica.X_Start;
                                ricetta.pcY = info_modifica.Y_Start;
                            } else
                            ricetta.setDrawPosition(new PointF(StepAttuale.p.x, StepAttuale.p.y));


                            if (info_modifica.StepAttivo != StepAttuale && info_modifica.StepAttivo != null)     //caso in cui ho avanzato con + e non con le frecce
                            {
                                TextView_info.setText(getString(R.string.DigitaLP) + " (" + old_A + ")");  //Digitare lunghezza punto
                                ricetta.selectionStepEnd();
                                ricetta.joinSelectedStepsByLine();
                            } else {
                                Element el_linea = ricetta.drawLineTo(new PointF(X, Y), info_modifica.LP);
                                el_linea.passo = info_modifica.LP;
                                el_linea.createSteps();
                            }
                            info_modifica.comando = Info_modifica.Comando.Null;
                            Aggiorna_canvas();
                            UpdateButtonsVisibility(ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL);
                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;

                            TextView_info.setText(getString(R.string.Fatto));
                        } catch (Exception e) {
                            e.printStackTrace();
                            TextView_info.setText(getString(R.string.Errore));
                        }
                        break;
                    case SPOSTA1:
                        int step_index = ricetta.activeStepIndex;
                        ricetta.selectionStepEnd();
                        if (step_index == -1) {
                            if (CheckPuntisovrapposti(Coord_Pinza.XCoordPosPinza, Coord_Pinza.YCoordPosPinza, ricetta.pcX, ricetta.pcY)) {
                                info_modifica.puntoCarico = true;
                            }
                        }

                        info_modifica.DeltaX_inizio = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
                        info_modifica.DeltaY_inizio = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;
                        info_modifica.comando = Info_modifica.Comando.SPOSTA2;
                        info_modifica.id_punto_fine_modifica = ricetta.getActiveStepIndex();
                        TextView_info.setText(getString(R.string.StringMove2));  //Muovere usando le frecce poi premi Enter
                        info_modifica.QuoteRelativeAttive = true;
                        break;
                    case SPOSTA2:
                        int ret_move=-999;
                        try {
                            Xfinale = (float)Coord_Pinza.XCoordPosPinza;
                            Yfinale = (float)Coord_Pinza.YCoordPosPinza;
                            info_modifica.QuoteRelativeAttive = false;
                            if (Xfinale != info_modifica.DeltaX_inizio || Yfinale != info_modifica.DeltaY_inizio) {
                                double DeltaX = Xfinale - info_modifica.DeltaX_inizio;
                                double DeltaY = Yfinale - info_modifica.DeltaY_inizio;

                                if (ElemSelezionati.size() == 0) {   //Steps
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
                                        ListCodici = ricetta.checkInvalidCodes(true);
                                        if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                        ricetta.clearInvalidCodes();

                                    }
                                }

                                else {
                                    info_modifica.id_element_fine_modifica = ricetta.getSelectedEntityFirstElementIndex();
                                    JamPointStep step = ricetta.getActiveStep();
                                    PointF p = new PointF(step.p.x, step.p.y);      //quota dell'ultimo punto attivo
                                    double DeltaX_entity = Xfinale - p.x;
                                    double DeltaY__entity = Yfinale - p.y;

                                    ret_move = ricetta.moveSelectedEntityNoStretch((float) DeltaX_entity, (float) DeltaY__entity,info_modifica.id_element_inizio_modifica,info_modifica.id_element_fine_modifica);
                                    if(ret_move == -12 ) TextView_info.setText("Move Error: Not Possible move first element");
                                    else
                                        TextView_info.setText("Move Done (type:" +ret_move+")");
                                    List<JamPointCode> ListCodici = new ArrayList<>();
                                    ListCodici = ricetta.checkInvalidCodes(true);
                                    if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                    ricetta.clearInvalidCodes();

                                    info_modifica.comando = Info_modifica.Comando.Null;
                                }

                                ricetta.repair();  //ripara la ricetta nel caso ci siano degli errori di continuità o di coordinate

                                Aggiorna_canvas();

                                UpdateButtonsVisibility(ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL);


                            }
                              Mci_Vb_OutPiedino_su.valore = 1.0d;
                               Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            TextView_info.setText(getString(R.string.Errore)+":"+ret_move);
                        }
                        break;
                    case SPOSTA2_STRETCH:
                        ret_move = -999;
                        try {
                            Xfinale = (float)Coord_Pinza.XCoordPosPinza;
                            Yfinale = (float)Coord_Pinza.YCoordPosPinza;
                            info_modifica.QuoteRelativeAttive = false;
                            if (Xfinale != info_modifica.DeltaX_inizio || Yfinale != info_modifica.DeltaY_inizio) {
                                double DeltaX = Xfinale - info_modifica.DeltaX_inizio;
                                double DeltaY = Yfinale - info_modifica.DeltaY_inizio;

                                if (ElemSelezionati.size() == 0) {   //Steps
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
                                        ListCodici = ricetta.checkInvalidCodes(true);
                                        if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                        ricetta.clearInvalidCodes();

                                    }
                                }

                                else {
                                    info_modifica.id_element_fine_modifica = ricetta.getSelectedEntityFirstElementIndex();
                                    JamPointStep step = ricetta.getActiveStep();
                                    PointF p = new PointF(step.p.x, step.p.y);      //quota dell'ultimo punto attivo
                                    double DeltaX_entity = Xfinale - p.x;
                                    double DeltaY__entity = Yfinale - p.y;

                                    ret_move = ricetta.moveSelectedEntity((float) DeltaX_entity, (float) DeltaY__entity,info_modifica.id_element_inizio_modifica,info_modifica.id_element_fine_modifica);

                                    if(ret_move == -11) TextView_info.setText("Move Error: Not Possible move first element");
                                    else
                                        TextView_info.setText("Move Done (type:" +ret_move+")");


                                    List<JamPointCode> ListCodici = new ArrayList<>();
                                    ListCodici = ricetta.checkInvalidCodes(true);
                                    if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                    ricetta.clearInvalidCodes();

                                    info_modifica.comando = Info_modifica.Comando.Null;
                                }
                                ricetta.repair();  //ripara la ricetta nel caso ci siano degli errori di continuità o di coordinate

                                Aggiorna_canvas();

                                UpdateButtonsVisibility(ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL);
                                TextView_info.setText("Move code:"+ret_move);

                            }
                              Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            TextView_info.setText(getString(R.string.Errore)+":"+ret_move);
                        }
                        break;
                    case STRETCH:
                        ricetta.selectionStepEnd();
                        TextView_info.setText(getString(R.string.StretchStep));  //Muovere con +- fino allo step di stretch
                        info_modifica.comando = Info_modifica.Comando.STRETCH1;
                        break;
                    case STRETCH1:
                        info_modifica.DeltaX_inizio = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
                        info_modifica.DeltaY_inizio = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;
                        TextView_info.setText(getString(R.string.StretchPointFrecce));  //Muovere con frecce per stirare il punto
                        info_modifica.comando = Info_modifica.Comando.STRETCH2;
                        break;
                    case STRETCH2:
                        try {
                            Xfinale = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
                            Yfinale = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;

                            double DeltaX = Xfinale - info_modifica.DeltaX_inizio;
                            double DeltaY = Yfinale - info_modifica.DeltaY_inizio;

                            boolean ret = ricetta.stretchActiveStep((float) DeltaX, (float) DeltaY);
                            info_modifica.comando = Info_modifica.Comando.Null;
                            Aggiorna_canvas();

                            UpdateButtonsVisibility(ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL);
                            if (ret) {
                                List<JamPointCode> ListCodici = new ArrayList<>();
                                ListCodici = ricetta.checkInvalidCodes(true);
                                if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                ricetta.clearInvalidCodes();
                                TextView_info.setText(getString(R.string.Fatto));
                            } else
                                TextView_info.setText(getString(R.string.Errore));

                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            TextView_info.setText(getString(R.string.Errore));
                        }
                        break;
                    case ZIGZAG:

                        info_modifica.X_End = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
                        info_modifica.Y_End = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;

                        info_modifica.QuoteRelativeAttive = false;
                        TextView_info.setText(getString(R.string.ZigZag1) + " (" + old_A + ")");
                        info_modifica.comando = Info_modifica.Comando.ZIGZAG_1;
                        break;
                    case ZIGZAG_1:
                        if (Utility.isNumeric((String) TextView_info.getText())) {
                            try {
                                info_modifica.AltezzaZigZag = Float.parseFloat((String) TextView_info.getText());
                                old_A = info_modifica.AltezzaZigZag;
                                TextView_info.setText(getString(R.string.ZigZag2) + " (" + old_B + ")");
                                info_modifica.comando = Info_modifica.Comando.ZIGZAG_2;
                            } catch (Exception e) {
                                e.printStackTrace();
                                TextView_info.setText(getString(R.string.Errore));
                            }
                        } else {
                            info_modifica.AltezzaZigZag = old_A;
                            TextView_info.setText(getString(R.string.ZigZag2) + " (" + old_B + ")");
                            info_modifica.comando = Info_modifica.Comando.ZIGZAG_2;
                        }
                        break;
                    case ZIGZAG_2:
                        try {
                            float Passo_ZigZag = old_B;
                            if (Utility.isNumeric((String) TextView_info.getText())) {
                                try {
                                    Passo_ZigZag = Float.parseFloat((String) TextView_info.getText());
                                    old_B = Passo_ZigZag;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    TextView_info.setText(getString(R.string.Errore));
                                }
                            } else {
                                Passo_ZigZag = old_B;
                            }

                            JamPointStep StepAttuale = ricetta.getActiveStep();


                            if (StepAttuale == null || ricetta.elements.size() == 0) {   //probabilmente è la prima linea di un programma vuoto
                                ricetta.pcX = info_modifica.X_Start;
                                ricetta.pcY = info_modifica.Y_Start;
                                ricetta.setDrawPosition(new PointF(info_modifica.X_Start,info_modifica.Y_Start));
                                ElementZigZag el_z = ricetta.drawZigZagTo(new PointF(((float) info_modifica.X_End - ricetta.pcX), ((float) info_modifica.Y_End) - ricetta.pcY), info_modifica.AltezzaZigZag, Passo_ZigZag);
                                el_z.passo = Passo_ZigZag;
                                el_z.altezza = info_modifica.AltezzaZigZag;
                                el_z.createSteps();
                            }else{

                                if (info_modifica.StepAttivo != StepAttuale && info_modifica.StepAttivo != null)     //caso in cui sono avanzato con + e non con le frecce
                                {
                                    // modifica programmatore
                                    ricetta.activeStepIndex = ricetta.getStepIndex(info_modifica.StepAttivo);  //assegno allo StepIndex il punto dove ho iniziato a premere +

                                    ElementZigZag el_z = ricetta.drawZigZagTo(new PointF(StepAttuale.p.x, StepAttuale.p.y), info_modifica.AltezzaZigZag, Passo_ZigZag);
                                    el_z.passo = Passo_ZigZag;
                                    el_z.altezza = info_modifica.AltezzaZigZag;
                                    el_z.createSteps();
                                    // fine modifica programmatore
                                } else {  //normale
                                    ricetta.setDrawPosition(new PointF(StepAttuale.p.x, StepAttuale.p.y));

                                    // modifica programmatore       ElementZigZag el_z = ricetta.drawZigZagTo(new PointF(((float) info_modifica.X_End - ricetta.pcX), ((float) info_modifica.Y_End) - ricetta.pcY), info_modifica.AltezzaZigZag, Passo_ZigZag);
                                    ElementZigZag el_z = ricetta.drawZigZagTo(new PointF(((float) info_modifica.X_End ), ((float) info_modifica.Y_End)), info_modifica.AltezzaZigZag, Passo_ZigZag);

                                    el_z.passo = Passo_ZigZag;
                                    el_z.altezza = info_modifica.AltezzaZigZag;
                                    el_z.createSteps();
                                }

                            }




                            info_modifica.comando = Info_modifica.Comando.Null;
                            UpdateButtonsVisibility(ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL);
                            TextView_info.setText(getString(R.string.Fatto));
                            Aggiorna_canvas();

                            //    Mci_Vb_OutPiedino_su.valore = 1.0d;
                            //   Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            TextView_info.setText(getString(R.string.Errore));
                        }
                        break;
                    case M888:
                        try {
                            if (Utility.isNumeric((String) TextView_info.getText())) {
                                try {
                                    float LP = Float.parseFloat((String) TextView_info.getText());
                                    ricetta.modify(LP);

                                    List<JamPointCode> ListCodici = new ArrayList<>();
                                    ListCodici = ricetta.checkInvalidCodes(true);
                                    if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                    ricetta.clearInvalidCodes();

                                    info_modifica.comando = Info_modifica.Comando.Null;
                                    UpdateButtonsVisibility(ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL);
                                    TextView_info.setText(getString(R.string.Fatto));
                                    Aggiorna_canvas();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    TextView_info.setText(getString(R.string.Errore));
                                }
                            }
                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            TextView_info.setText(getString(R.string.Errore));
                        }
                        break;
                    case SPOSTA_ALL:
                        try {
                            if(ricetta.getActiveStepIndex() == -1) {  //controllo se sono sul punto di carico
                                if (ricetta.elements.size() > 0) {
                                    float PCX = ricetta.pcX;
                                    float PCY = ricetta.pcY;

                                    Xfinale = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
                                    Yfinale = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;
                                    info_modifica.QuoteRelativeAttive = true;
                                    if (Xfinale != info_modifica.DeltaX_inizio || Yfinale != info_modifica.DeltaY_inizio) {
                                        double DeltaX = Xfinale - info_modifica.DeltaX_inizio;
                                        double DeltaY = Yfinale - info_modifica.DeltaY_inizio;

                                        ricetta.move((float) DeltaX, (float) DeltaY);
                                        ricetta.pcX = PCX;  //rimetto il pc come era prima
                                        ricetta.pcY = PCY;  //rimetto il pc come era prima
                                        ricetta.elements.get(0).pStart = new PointF(PCX, PCY); // FirstEntityPoint;
                                        Aggiorna_canvas();
                                        info_modifica.comando = Info_modifica.Comando.Null;
                                        UpdateButtonsVisibility(ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL);
                                        TextView_info.setText(getString(R.string.Fatto));  //
                                    }
                                }
                            }else
                            {//non sono sul punto di carico allora sposto tutto quello che c'è dopo
                                Xfinale = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
                                Yfinale = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;
                                double DeltaX = Xfinale - info_modifica.DeltaX_inizio;
                                double DeltaY = Yfinale - info_modifica.DeltaY_inizio;

                                int id_step_start = ricetta.getActiveStepIndex();
                                int id_elem_start = ricetta.elements.indexOf(ricetta.getActiveStep().element);
                                // int  id_end =  ricetta.elements.indexOf(ricetta.elements.size()-1) ;
                                int  id_end = ricetta.elements.size()-1 ;


                                ricetta.moveElements(id_elem_start,id_end,(float) DeltaX, (float) DeltaY);

                                List<JamPointCode> ListCodici = new ArrayList<>();
                                ListCodici = ricetta.checkInvalidCodes(true);
                                if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                ricetta.clearInvalidCodes();
                                Aggiorna_canvas();
                                info_modifica.comando = Info_modifica.Comando.Null;
                                UpdateButtonsVisibility(ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL);
                                TextView_info.setText(getString(R.string.Fatto));  //

                            }

                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            TextView_info.setText(getString(R.string.Errore));
                        }
                        break;
                    case RADDRIZZA_LINEA:
                        try {
                            if (info_modifica.id_punto_inizio_modifica != ricetta.getActiveStepIndex()) {
                                ricetta.selectionStepEnd();
                                boolean result = ricetta.joinSelectedStepsByLine();
                                ricetta.selectionStepClear();
                                info_modifica.comando = Info_modifica.Comando.Null;
                                Aggiorna_canvas();
                                UpdateButtonsVisibility(ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL);
                                if (result) {
                                    List<JamPointCode> ListCodici = new ArrayList<>();
                                    ListCodici = ricetta.checkInvalidCodes(true);
                                    if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                                    ricetta.clearInvalidCodes();

                                    TextView_info.setText(getString(R.string.Fatto));
                                } else
                                    TextView_info.setText(getString(R.string.Errore));
                            }

                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            TextView_info.setText(getString(R.string.Errore));
                        }
                        break;
                    case RADDRIZZA_ARCO_ENTITA:

                        //entity
                        info_modifica.ElemSelezionati.clear();  //pulisco
                        float XMiddle = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
                        float YMiddle = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;

                        ricetta.transformSelectedEntityToArc(new PointF(XMiddle, YMiddle));

                        List<JamPointCode> ListCodici_Ent = new ArrayList<>();
                        ListCodici_Ent = ricetta.checkInvalidCodes(true);
                        if (ListCodici_Ent.size() > 0) ShowCodeToast(ListCodici_Ent);
                        ricetta.clearInvalidCodes();

                        info_modifica.comando = Info_modifica.Comando.Null;
                        TextView_info.setText(getString(R.string.Fatto));
                        UpdateButtonsVisibility(ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL);
                        Aggiorna_canvas();
                        break;
                    case RADDRIZZA_ARCO:
                        if (info_modifica.ElemSelezionati.size() == 0) {
                            info_modifica.id_punto_middle_modifica = ricetta.getActiveStepIndex();
                            info_modifica.comando = Info_modifica.Comando.RADDRIZZA_ARCO1;
                            TextView_info.setText(getString(R.string.EndPointStep));  //Avanza con + e - allo step
                        } else {
                            //ho selezionato una entità e non posso applicarci questo comando
                        }
                        break;
                    case RADDRIZZA_ARCO1:
                        try {
                            info_modifica.id_punto_fine_modifica = ricetta.getActiveStepIndex();
                            ricetta.joinSelectedStepsByArc(info_modifica.id_punto_inizio_modifica, info_modifica.id_punto_middle_modifica, info_modifica.id_punto_fine_modifica);
                            TextView_info.setText(getString(R.string.Fatto));
                            Aggiorna_canvas();
                            UpdateButtonsVisibility(ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL, ButtonsVisibilityStatus.NULL);
                            info_modifica.comando = Info_modifica.Comando.Null;
                            List<JamPointCode> ListCodici = new ArrayList<>();
                            ListCodici = ricetta.checkInvalidCodes(true);
                            if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                            ricetta.clearInvalidCodes();
                            Mci_Vb_OutPiedino_su.valore = 1.0d;
                            Mci_Vb_OutPiedino_su.write_flag = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            TextView_info.setText(getString(R.string.Errore));
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

    /**
     * Button 0 event
     *
     * @param view
     */
    public void on_click_0(View view) {
        if (Utility.isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText() + "0");
        else
            TextView_info.setText("0");
    }

    /**
     * Button 1 event
     *
     * @param view
     */
    public void on_click_1(View view) {

        if (Utility.isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText() + "1");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("1");
        }
    }

    /**
     * Button 2 event
     *
     * @param view
     */
    public void on_click_2(View view) {

        if (Utility.isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText() + "2");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("2");
        }
    }

    /**
     * Button 3 event
     *
     * @param view
     */
    public void on_click_3(View view) {
        if (Utility.isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText() + "3");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("3");
        }
    }

    /**
     * Button 4 event
     *
     * @param view
     */
    public void on_click_4(View view) {
        if (Utility.isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText() + "4");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("4");
        }
    }

    /**
     * Button 5 event
     *
     * @param view
     */
    public void on_click_5(View view) {

        if (Utility.isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText() + "5");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("5");
        }
    }

    /**
     * Button 6 event
     *
     * @param view
     */
    public void on_click_6(View view) {
        if (Utility.isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText() + "6");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("6");
        }
    }

    /**
     * Button 7 event
     *
     * @param view
     */
    public void on_click_7(View view) {
        if (Utility.isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText() + "7");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("7");
        }
    }

    /**
     * Button 8 event
     *
     * @param view
     */
    public void on_click_8(View view) {
        if (Utility.isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText() + "8");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("8");
        }
    }

    //#endregion ButtonNumbers

    /**
     * Button 9 event
     *
     * @param view
     */
    public void on_click_9(View view) {
        if (Utility.isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText() + "9");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("9");
        }
    }

    /**
     * Button . event
     *
     * @param view
     */
    public void on_click_punto(View view) {
        if (Utility.isNumeric((String) TextView_info.getText()))
            TextView_info.setText(TextView_info.getText() + ".");
        else {
            info_StepPiuMeno.last_testo_textView_info = (String) TextView_info.getText();
            TextView_info.setText("0.");
        }
    }

    //#endregion NumericPad

    //#region EditElement

    /**
     * Button delete last wrote value
     *
     * @param view
     */
    public void on_click_del(View view) {
        TextView_info.setText("");
    }

    /**
     * Button for transform an Element into an Arc
     *
     * @param view
     */
    public void on_click_traform_toArc(View view) {
        // Right now it will work only with ElementArcZigZag and ElementArc (useless, but in line is the same)
        if (ElemSelezionati.size() > 0 && (ricetta.getSelectedEntity() instanceof ElementArcZigZag || ricetta.getSelectedEntity() instanceof ElementArc)) {
            try {
                if (ricetta.getSelectedEntity() instanceof ElementArcZigZag) {
                    ricetta.transformSelectedEntityToArc(((ElementArcZigZag) ricetta.getSelectedEntity()).pMiddle);
                } else if (ricetta.getSelectedEntity() instanceof ElementArc) {
                    ricetta.transformSelectedEntityToArc(((ElementArc) ricetta.getSelectedEntity()).pMiddle);
                }

                List<JamPointCode> ListCodici = new ArrayList<>();
                ListCodici = ricetta.checkInvalidCodes(true);
                if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                ricetta.clearInvalidCodes();

                ElemSelezionati = ricetta.selectPreviousEntity();
                ElemSelezionati = ricetta.selectNextEntity();

                Aggiorna_canvas();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Button for transform an Element into a Line
     *
     * @param view
     */
    public void on_click_traform_toLine(View view) {
        if (ElemSelezionati.size() > 0) {   //entity
            try {
                ricetta.transformSelectedEntityToLine();

                List<JamPointCode> ListCodici = new ArrayList<>();
                ListCodici = ricetta.checkInvalidCodes(true);
                if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                ricetta.clearInvalidCodes();


                ElemSelezionati = ricetta.selectPreviousEntity();   //per aggiornare
                ElemSelezionati = ricetta.selectNextEntity();   //per aggiornare

                Aggiorna_canvas();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Button for transform an Element into a ZigZag
     *
     * @param view
     */
    public void on_click_traform_toZigZag(View view) {
        if (ElemSelezionati.size() > 0) {   //entity
            try {
                ricetta.transformSelectedEntityToZigZag();

                List<JamPointCode> ListCodici = new ArrayList<>();
                ListCodici = ricetta.checkInvalidCodes(true);
                if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                ricetta.clearInvalidCodes();

                ElemSelezionati = ricetta.selectPreviousEntity();   //per aggiornare
                ElemSelezionati = ricetta.selectNextEntity();   //per aggiornare
                Aggiorna_canvas();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Button for transform an Element into a Feed
     *
     * @param view
     */
    public void on_click_traform_toFeed(View view) {
        if (ElemSelezionati.size() > 0) {   //entity
            ricetta.transformSelectedEntityToFeed();

            List<JamPointCode> ListCodici = new ArrayList<>();
            ListCodici = ricetta.checkInvalidCodes(false);
            if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
            ricetta.clearInvalidCodes();

            ElemSelezionati = ricetta.selectPreviousEntity();   //per aggiornare
            ElemSelezionati = ricetta.selectNextEntity();   //per aggiornare
            Aggiorna_canvas();
        }
    }

    //#endregion EditElement

    /**
     * Button for explode an Element
     *
     * @param view
     */
    public void on_click_explode(View view) {
        if (ElemSelezionati.size() > 0) {   //entity
            ricetta.explodeSelectedEntity();
            ElemSelezionati.clear();
            Aggiorna_canvas();
        }
    }

    /**
     * Button for move elements or steps
     *
     * @param view
     */
    public void on_click_sposta(View view) {
        //move steps
        if (ElemSelezionati.size() == 0) {
            ricetta.selectionStepStart();

            Set_Altri_button_comandi_invisibili(Button_sposta);
            info_modifica = new Info_modifica();
            info_modifica.QuoteRelativeAttive = true;
            info_modifica.id_punto_inizio_modifica = ricetta.getActiveStepIndex();
            info_modifica.comando = Info_modifica.Comando.SPOSTA1;
            info_modifica.id_punto_inizio_modifica = ricetta.getActiveStepIndex();
            info_modifica.DeltaX_inizio = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
            info_modifica.DeltaY_inizio = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;

            TextView_info.setText(getString(R.string.StringMove1));  //Premi tasti + - fino ultimo punto da spostare
        } else {

            Set_Altri_button_comandi_invisibili(Button_sposta);
            info_modifica = new Info_modifica();
            info_modifica.QuoteRelativeAttive = true;
            info_modifica.comando = Info_modifica.Comando.SPOSTA2;
            info_modifica.id_element_inizio_modifica = ricetta.getSelectedEntityFirstElementIndex();
            info_modifica.DeltaX_inizio = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
            info_modifica.DeltaY_inizio = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;
            TextView_info.setText(getString(R.string.StringMove3));  //Muovere usando le frecce poi premi Enter
        }

        Set_Altri_button_comandi_invisibili(Button_sposta);
    }
    public void on_click_sposta_stretch(View view) {

        //move steps
        if (ElemSelezionati.size() == 0) {
            ricetta.selectionStepStart();

            Set_Altri_button_comandi_invisibili(Button_sposta_stretch);
            info_modifica = new Info_modifica();
            info_modifica.QuoteRelativeAttive = true;
            info_modifica.id_punto_inizio_modifica = ricetta.getActiveStepIndex();
            info_modifica.comando = Info_modifica.Comando.SPOSTA1;
            info_modifica.id_punto_inizio_modifica = ricetta.getActiveStepIndex();
            info_modifica.DeltaX_inizio = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
            info_modifica.DeltaY_inizio = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;
            float Xfinale = (float) Coord_Pinza.XCoordPosPinza;
            float Yfinale = (float) Coord_Pinza.YCoordPosPinza;
            info_modifica.DeltaX_inizio = Xfinale;
            info_modifica.DeltaY_inizio = Yfinale;
            TextView_info.setText(getString(R.string.StringMove1));  //Premi tasti + - fino ultimo punto da spostare
        } else {

            Set_Altri_button_comandi_invisibili(Button_sposta_stretch);
            info_modifica = new Info_modifica();
            info_modifica.QuoteRelativeAttive = true;
            info_modifica.comando = Info_modifica.Comando.SPOSTA2_STRETCH;
            info_modifica.id_element_inizio_modifica = ricetta.getSelectedEntityFirstElementIndex();
            info_modifica.DeltaX_inizio = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
            info_modifica.DeltaY_inizio = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;
            float Xfinale = (float) Coord_Pinza.XCoordPosPinza;
            float Yfinale = (float) Coord_Pinza.YCoordPosPinza;
            info_modifica.DeltaX_inizio = Xfinale;
            info_modifica.DeltaY_inizio = Yfinale;
            TextView_info.setText(getString(R.string.StringMove3));  //Muovere usando le frecce poi premi Enter
        }

        Set_Altri_button_comandi_invisibili(Button_sposta_stretch);
    }
    /**
     * Button for open the page for add/remove codes
     *
     * @param view
     */
    public void on_click_codici(View view) {
        int punto_attuale = ricetta.getActiveStepIndex();
        List<JamPointCode> codeStatus = ricetta.getActiveStepCodes();

        if (punto_attuale == -1) {    //mi serve per passare l'angolo di rotazione della partenza
            codeStatus = ricetta.codes;
        }
        Code_page.Lancia_Code_Page(this, codeStatus);
    }

    /**
     * TODO
     *
     * @param view
     */
    public void on_click_raddrizza_arco(View view) {
        Set_Altri_button_comandi_invisibili(Button_raddrizza_arco);
        info_modifica = new Info_modifica();
        ricetta.selectionStepStart();
        info_modifica.id_punto_inizio_modifica = ricetta.getActiveStepIndex();
        info_modifica.comando = Info_modifica.Comando.RADDRIZZA_ARCO;
        info_modifica.ElemSelezionati = ElemSelezionati;
        if (ElemSelezionati.size() > 0) {    //sto lavorando come entità
            TextView_info.setText(getString(R.string.MiddlePointStep));  //Premi frecce fino punto di mezzo dell'arco
            info_modifica.comando = Info_modifica.Comando.RADDRIZZA_ARCO_ENTITA;
        } else {   //sto lavorando come punti
            TextView_info.setText(getString(R.string.MiddlePointStep));  //Avanza con + e - allo step
            info_modifica.id_punto_inizio_modifica = ricetta.getActiveStepIndex();
        }
    }

    //#region DrawNewElement

    /**
     * TODO
     *
     * @param view
     */
    public void on_click_raddrizza_linea(View view) {
        if (ElemSelezionati.size() > 0) {   //entity
            ricetta.transformSelectedEntityToLine(); // changeSelectedEntityToLine();
            Aggiorna_canvas();
        } else {
            //steps
            Set_Altri_button_comandi_invisibili(Button_raddrizza_linea);
            info_modifica = new Info_modifica();
            ricetta.selectionStepStart();
            info_modifica.comando = Info_modifica.Comando.RADDRIZZA_LINEA;
            info_modifica.id_punto_inizio_modifica = ricetta.getActiveStepIndex();
            TextView_info.setText(getString(R.string.StringRaddrizzaLinea1));  //Premi tasti + - fino ultimo punto da allineare
        }
    }

    /**
     * Button for draw a new feed
     *
     * @param view
     */
    public void on_click_feed(View view) {
        Set_Altri_button_comandi_invisibili(Button_feed);
        info_modifica = new Info_modifica();
        info_modifica.comando = Info_modifica.Comando.FEED;
        info_modifica.DeltaX_inizio = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
        info_modifica.DeltaY_inizio = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;
        TextView_info.setText(getString(R.string.Feed));  //Muovere usando le frecce poi premi Enter
        info_modifica.QuoteRelativeAttive = true;
    }

    /**
     * Button for draw a new line
     *
     * @param view
     */
    public void on_click_linea(View view) {
        Set_Altri_button_comandi_invisibili(Button_linea);
        info_modifica = new Info_modifica();
        info_modifica.comando = Info_modifica.Comando.LINEA;
        info_modifica.DeltaX_inizio = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
        info_modifica.DeltaY_inizio = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;
        info_modifica.X_Start = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
        info_modifica.Y_Start =  ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;
        info_modifica.QuoteRelativeAttive = true;
        TextView_info.setText(getString(R.string.StringMove4));  //premi +- o Muovere usando le frecce poi premi Enter

        info_modifica.StepAttivo = ricetta.getActiveStep();
        ricetta.selectionStepStart();
    }

    /**
     * Button for draw a new spline
     * <p>
     * TODO need to be implemented
     *
     * @param view
     */
    public void on_click_spline(View view) {
    }

    /**
     * Button for draw a new arc from 3 points
     *
     * @param view
     */
    public void on_click_arco3p(View view) {
        Set_Altri_button_comandi_invisibili(Button_arco3p);
        info_modifica = new Info_modifica();
        info_modifica.comando = Info_modifica.Comando.ARCO3P_0;
        JamPointStep StepAttuale = ricetta.getActiveStep();
        // modifica programmatore
        if (StepAttuale == null || ricetta.elements.size() == 0) {   //probabilemnte è il primo di un programma vuoto
            ricetta.setDrawPosition(new PointF(info_modifica.X_Start,info_modifica.Y_Start));
            ricetta.pcX = info_modifica.X_Start;
            ricetta.pcY = info_modifica.Y_Start;
        } else
            ricetta.setDrawPosition(new PointF(StepAttuale.p.x, StepAttuale.p.y));
        info_modifica.id_punto_inizio_modifica = ricetta.getActiveStepIndex();   //daniele 18/06/20
        TextView_info.setText(getString(R.string.Arco3P_p2));  //Muovere usando le frecce poi premi Enter
    }

    //#endregion DrawNewElement

    /**
     * Button for draw a new backtack
     *
     * @param view
     */
    public void on_click_travetta(View view) {
        Set_Altri_button_comandi_invisibili(Button_travetta);
        info_modifica = new Info_modifica();
        info_modifica.comando = Info_modifica.Comando.ZIGZAG;
        info_modifica.DeltaX_inizio = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
        info_modifica.DeltaY_inizio = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;
        info_modifica.QuoteRelativeAttive = true;
        TextView_info.setText(getString(R.string.ZigZag));

        info_modifica.StepAttivo = ricetta.getActiveStep();
        ricetta.selectionStepStart();
    }

    /**
     * TODO
     *
     * @param view
     */
    public void on_click_debug(View view) {
        ricetta.repair();
    }

    /**
     * Button for delete steps or entities
     *
     * @param view
     */
    public void on_click_cancella(View view) {
        if (ElemSelezionati.size() == 0) {  //steps
            ricetta.selectionStepStart();
            Set_Altri_button_comandi_invisibili(Button_cancella);
            info_modifica = new Info_modifica();
            info_modifica.comando = Info_modifica.Comando.CANCELLA;
            info_modifica.id_punto_inizio_modifica = ricetta.getActiveStepIndex();
            TextView_info.setText(getString(R.string.StringCancella));  //Premi tasti + - fino ultimo punto da cancellare
        } else {    //entity
            ricetta.deleteSelectedEntity();     //cancello entity
            List<JamPointCode> ListCodici = new ArrayList<>();
            ListCodici = ricetta.checkInvalidCodes(false);
            if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
            ricetta.clearInvalidCodes();

            info_StepPiuMeno.MacStati_StepSingolo = 10;
            info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.TO_STEP_ATTIVO;

            TextView_info.setText(getString(R.string.Fatto));
            Aggiorna_canvas();
            info_modifica.comando = Info_modifica.Comando.Null;
        }
    }

    /**
     * TODO
     *
     * @param view
     */
    public void on_click_stretchIntersection(View view) {
        Set_Altri_button_comandi_invisibili(Button_stretch_edge);
        info_modifica.comando = Info_modifica.Comando.STRETCH;
        //  info_modifica.id_punto_inizio_modifica = ricetta.getActiveStepIndex();
        ricetta.selectionStepStart();
        TextView_info.setText(getString(R.string.EndStretchStep));  //Muovere con + fino allo step finale dello stretch
    }

    /**
     * TODO
     *
     * @param view
     */
    public void on_click_888M(View view) {
        info_modifica.comando = Info_modifica.Comando.M888;

        TextView_info.setText(getString(R.string.DigitaNuovaLP));  //Digitare nuova lunghezza punto
        Set_Altri_button_comandi_invisibili(Button_888M);
    }

    /**
     * Button for move the entire Ricetta
     *
     * @param view
     */
    public void on_click_move_all(View view) {
        Set_Altri_button_comandi_invisibili(Button_move_all);
        info_modifica = new Info_modifica();
        info_modifica.comando = Info_modifica.Comando.SPOSTA_ALL;
        info_modifica.DeltaX_inizio = ((Double) MultiCmd_posizione_X.getValue()).floatValue() / 1000f;
        info_modifica.DeltaY_inizio = ((Double) MultiCmd_posizione_Y.getValue()).floatValue() / 1000f;
        TextView_info.setText(getString(R.string.StringMove2));  //Muovere usando le frecce poi premi Enter
        info_modifica.QuoteRelativeAttive = true;
    }

    //#endregion ButtonsEvents

    //#region HideShowButtons

    /**
     * Button for delete all the codes on the active step
     *
     * @param view
     */
    public void on_click_cancella_codice(View view) {
        try {
            boolean result = ricetta.clearActiveStepCodes();
            Aggiorna_canvas();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Function for display all the icons
     */
    private void Mostra_Tutte_Icone() {
        UpdateButtonsVisibility(ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.VISIBLE, ButtonsVisibilityStatus.VISIBLE);
        Button_tasto_enter.setVisibility(View.VISIBLE);
        info_modifica.comando = Info_modifica.Comando.Null;
        Button_exit.setVisibility(View.VISIBLE);
        Button_undo.setVisibility(View.VISIBLE);
        Button_redo.setVisibility(View.VISIBLE);
        Button_new.setVisibility(View.VISIBLE);
        Button_esc.setVisibility(View.VISIBLE);
    }

    /**
     * Function for hide all the icons
     */
    private void SpegniTutteIcone() {
        UpdateButtonsVisibility(ButtonsVisibilityStatus.HIDDEN, ButtonsVisibilityStatus.HIDDEN, ButtonsVisibilityStatus.HIDDEN, ButtonsVisibilityStatus.HIDDEN);

        ImageView.setVisibility(View.GONE);
        Button_traform_toLine.setVisibility(View.GONE);
        Button_traform_toArc.setVisibility(View.GONE);
        Button_traform_toZigZag.setVisibility(View.GONE);
        Button_traform_toFeed.setVisibility(View.GONE);
        Button_explode.setVisibility(View.GONE);
        Button_tasto_enter.setVisibility(View.GONE);
        Button_undo.setVisibility(View.GONE);
        Button_redo.setVisibility(View.GONE);
        Button_new.setVisibility(View.GONE);
        Button_esc.setVisibility(View.GONE);
    }

    /**
     * Function for Hide/Show Buttons
     * <p>
     * The status allowed are:
     * VISIBLE = Set the buttons of the chose list visible
     * HIDDEN = Set the buttons of the chose list invisible
     * NULL = Don't edit the chose list buttons status
     * <p>
     * I need this because i can't do it with a simple boolean because i don't have the null status
     *
     * @param buttonsCommands
     * @param buttonsNumbers
     * @param buttonsArrows
     * @param buttonsNextPrevious
     */
    private void UpdateButtonsVisibility(ButtonsVisibilityStatus buttonsCommands, ButtonsVisibilityStatus buttonsNumbers, ButtonsVisibilityStatus buttonsArrows, ButtonsVisibilityStatus buttonsNextPrevious) {
        // Loop for hide/show the command buttons
        if (buttonsCommands != ButtonsVisibilityStatus.NULL) {
            for (Button item : Lista_pulsanti_comandi) {
                if (buttonsCommands == ButtonsVisibilityStatus.VISIBLE) {
                    item.setVisibility(View.VISIBLE);
                } else if (buttonsCommands == ButtonsVisibilityStatus.HIDDEN) {
                    item.setVisibility(View.GONE);
                }
            }
        }

        // Loop for hide/show the number buttons
        if (buttonsNumbers != ButtonsVisibilityStatus.NULL) {
            for (Button item : Lista_pulsanti_comandi_numeri) {
                if (buttonsNumbers == ButtonsVisibilityStatus.VISIBLE) {
                    item.setVisibility(View.VISIBLE);
                } else if (buttonsNumbers == ButtonsVisibilityStatus.HIDDEN) {
                    item.setVisibility(View.GONE);
                }
            }
        }

        // Loop for hide/show the arrow buttons
        if (buttonsArrows != ButtonsVisibilityStatus.NULL) {
            for (Button item : Lista_pulsanti_comandi_frecce) {
                if (buttonsArrows == ButtonsVisibilityStatus.VISIBLE) {
                    item.setVisibility(View.VISIBLE);
                } else if (buttonsArrows == ButtonsVisibilityStatus.HIDDEN) {
                    item.setVisibility(View.GONE);
                }
            }
        }

        // Loop for hide/show the NextPrevious buttons
        if (buttonsNextPrevious != ButtonsVisibilityStatus.NULL) {
            for (Button item : Lista_pulsanti_PiuMeno) {
                if (buttonsNextPrevious == ButtonsVisibilityStatus.VISIBLE) {
                    item.setVisibility(View.VISIBLE);
                } else if (buttonsNextPrevious == ButtonsVisibilityStatus.HIDDEN) {
                    item.setVisibility(View.GONE);
                }
            }
        }

        // Those are all the buttons not implemented
        Button_spline.setVisibility(View.GONE);
        Button_debug.setVisibility(View.GONE);
    }

    /**
     * Function for hide all the buttons TODO TRANNE the chose button
     *
     * @param button_attivo
     */
    private void Set_Altri_button_comandi_invisibili(Button button_attivo) {
        for (Button item : Lista_pulsanti_comandi) {
            if (item != button_attivo) {
                item.setVisibility(View.GONE);
            }
        }
    }

    //#endregion HideShowButtons

    //#region Utilities

    /**
     * Function for handle the button entita piu event
     */
    private void GestioneEntitaPiu() {

        if (onClickTastoEntitaPiu) {
            onClickTastoEntitaPiu = false;
            MultiCmd_RichiestaPiedinoSu.setValue(1.0d);
            sl.WriteItem(MultiCmd_RichiestaPiedinoSu);
            float X_destinazione = 0.0f, Y_destinazione = 0.0f;

            if ((Double) MultiCmd_XY_fermi.getValue() == 1.0d) {
                if (List_entita.size() > 0) {

                    if (ricetta.getActiveStepIndex() == -1)    //nel caso entrando ero a -1 faccio un agonoNextStep per andare al primo punto.
                    {
                        ricetta.goToNextStep();
                    } else if (info_modifica.comando == Info_modifica.Comando.SPOSTA2) {   //se avevo questi comandi e ho ripremuto E+ continuo a inserire le entità in modo da farle tutte gialle
                        List<Element> el = ricetta.selectNextEntity();
                        for (Element element : el) {
                            ElemSelezionati.add(element);
                        }
                    } else {
                        for (Element elementi : ElemSelezionati) {     //selezione
                            elementi.isSelected = false;       //seleziono gli element dell'entità
                        }
                        ElemSelezionati = ricetta.selectNextEntity();   //seleziono solo una entità, solo una sarà gialla
                    }
                    X_destinazione = ricetta.getSelectedEntityStartPoint().x;  // + ricetta.pcX;                                      //quota X destinazione del primo step dell'element successivo
                    Y_destinazione = ricetta.getSelectedEntityStartPoint().y;  // + ricetta.pcY;

                    try {
                        double X = Double.valueOf(X_destinazione);
                        double Y = Double.valueOf(Y_destinazione);

                        Double LimiteXNegativo = (Double) MultiCmd_Vq_104_READ_FC_ind_X.getValue() / 1000;
                        Double LimiteXPositivo = (Double) MultiCmd_Vq_105_READ_FC_ava_X.getValue() / 1000;
                        Double LimiteYNegativo = (Double) MultiCmd_Vq_106_READ_FC_ind_Y.getValue() / 1000;
                        Double LimiteYPositivo = (Double) MultiCmd_Vq_107_READ_FC_ava_Y.getValue() / 1000;

                        if (X < LimiteXNegativo || X > LimiteXPositivo || Y < LimiteYNegativo || Y > LimiteYPositivo) {
                            X = 0;      //se le coordinate sono fuori area per non bloccare il tutto lo faccio passare per 0
                            Y = 0;      //se le coordinate sono fuori area per non bloccare il tutto lo faccio passare per 0
                        }

                        info_modifica.QuoteRelativeAttive = true;
                        //invio quote al PLC

                        MultiCmd_quota_destinazione_X.setValue(X * 1000);
                        MultiCmd_quota_destinazione_Y.setValue(Y * 1000);

                        MultiCmd_HmiMoveXY.setValue(1.0d);
                        MultiCmdItem[] Dati_out = new MultiCmdItem[]{MultiCmd_quota_destinazione_X, MultiCmd_quota_destinazione_Y,
                                MultiCmd_HmiMoveXY};
                        sl.WriteItems(Dati_out);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    for (Element elementi : List_entita) {     //selezione
                        elementi.isSelected = false;       //deseleziono tutti gli element
                    }
                    for (Element elementi : ElemSelezionati) {     //selezione
                        elementi.isSelected = true;       //seleziono gli element dell'entità
                    }
                }


            }
        }

    }

    /**
     * Function for handle the button entita meno event
     */
    private void GestioneEntitaMeno() {
        if (onClickTastoEntitaMeno) {
            onClickTastoEntitaMeno = false;
            MultiCmd_RichiestaPiedinoSu.setValue(1.0d);
            sl.WriteItem(MultiCmd_RichiestaPiedinoSu);
            float X_destinazione = 0.0f, Y_destinazione = 0.0f;

            if ((Double) MultiCmd_XY_fermi.getValue() == 1.0d) {

                if (List_entita.size() > 0) {
                    if (ricetta.getActiveStepIndex() == -1)    //nel caso entrando ero a -1 faccio un goToPreviousStep per posizionarmi all'ultimo punto
                    {
                        ricetta.goToPreviousStep();
                        ricetta.selectNextEntity();
                    }
                    ElemSelezionati = ricetta.selectPreviousEntity();

                    try {
                        X_destinazione = ricetta.getSelectedEntityStartPoint().x; //+ ricetta.pcX;                                      //quota X destinazione del primo step dell'element successivo
                        Y_destinazione = ricetta.getSelectedEntityStartPoint().y; // + ricetta.pcY;                                      //quota Y destinazione del primo step dell'element successivo

                        double X = Double.valueOf(X_destinazione);                              //invio quote al PLC
                        double Y = Double.valueOf(Y_destinazione);

                        MultiCmd_quota_destinazione_X.setValue(X * 1000);
                        MultiCmd_quota_destinazione_Y.setValue(Y * 1000);

                        MultiCmd_HmiMoveXY.setValue(1.0d);

                        MultiCmdItem[] Dati_out = new MultiCmdItem[]{MultiCmd_quota_destinazione_X, MultiCmd_quota_destinazione_Y,
                                MultiCmd_HmiMoveXY};
                        sl.WriteItems(Dati_out);

                        for (Element elementi : List_entita) {     //selezione
                            elementi.isSelected = false;       //deseleziono tutti gli element
                        }
                        for (Element elementi : ElemSelezionati) {     //selezione
                            elementi.isSelected = true;       //seleziono gli element dell'entità
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * Refresh the canvas
     */
    private void Aggiorna_canvas() {
        myView.Ricalcola_entità_canvas(ricetta.elements);
        myView.AggiornaCanvas(true);
    }

    /**
     * Load an Xml
     *
     * @param percorso_file
     */
    private void Load_XML(String percorso_file) {
        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/ricette");
            dir.mkdirs();
            File file = new File(percorso_file);
            int i = file.getName().lastIndexOf('.');
            String name = file.getName().substring(0, i);
            File file1 = new File(file.getParent() + "/" + name + ".xml");

            ricetta = new Ricetta(Values.plcType);
            try {
                ricetta.open(file1);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "error opening xml file ", Toast.LENGTH_SHORT).show();
            }
            List_entita = (ArrayList<Element>) ricetta.elements;

            myView = new Dynamic_view(this, 433, 350, List_entita, 1.45F, Coord_Pinza, false, -510, 15, null, getResources().getDimension(R.dimen.modifica_programma_activity_framelayout_width), getResources().getDimension(R.dimen.modifica_programma_activity_framelayout_height));

            frame_canvas.addView(myView);
            myView.Ricalcola_entità_canvas(List_entita);
            myView.Center_Bitmap_Main(1.25F, 380, 5);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Unable to draw pocket canvas", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean CheckPuntisovrapposti(double xCoordPosPinza, double yCoordPosPinza, float pcX, float pcY) {
        return xCoordPosPinza - pcX > -0.06 &&
                xCoordPosPinza - pcX < 0.06 &&
                yCoordPosPinza - pcY > -0.06 &&
                yCoordPosPinza - pcY < 0.06;
    }

    /**
     * Function for handle the arrows event
     *
     * @param Mci_write
     */
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

    /**
     * Function for display a toast that tells the removed codes
     *
     * @param listCodici
     */
    private void ShowCodeToast(List<JamPointCode> listCodici) {

        String testo = getString(R.string.Code_error) + ": ";

        for (JamPointCode code : listCodici) {
            testo = testo + "\n" + code.tipoCodice;
        }
        Toast.makeText(getApplication(), testo, Toast.LENGTH_LONG).show();
    }

    /**
     * Function for change the height of a ZigZag
     *
     * @param nuova_altezza
     */
    private void cambia_altezza_travetta(float nuova_altezza) {
        try {
            if (ElemSelezionati.size() > 0) {
                if (ElemSelezionati.get(0) instanceof ElementZigZag) {
                    ricetta.modifySelectedEntityZigZag(nuova_altezza);
                }
                Aggiorna_canvas();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("numberStr is not a number");
        }
    }

    /**
     * Function for change the LP of an Element
     *
     * @param nuovoPasso
     */
    private void cambia_lunghezzaPunto_elemento(float nuovoPasso) {
        try {
            if (ElemSelezionati.size() > 0) {
                if ((ElemSelezionati.get(0) instanceof ElementLine) || ElemSelezionati.get(0) instanceof ElementArc || ElemSelezionati.get(0) instanceof ElementArcZigZag || ElemSelezionati.get(0) instanceof ElementZigZag) {
                    ricetta.modifySelectedEntity(nuovoPasso);
                }
                List<JamPointCode> ListCodici = new ArrayList<>();
                ListCodici = ricetta.checkInvalidCodes(true);
                if (ListCodici.size() > 0) ShowCodeToast(ListCodici);
                ricetta.clearInvalidCodes();

                Aggiorna_canvas();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("numberStr is not a number");
        }
    }

    /**
     * TODO
     */
    private void Scrivi_codice_HMI() {
        List<JamPointCode> codeStatus = ricetta.getActiveStepCodes();

        if (codeStatus.size() > 0) {

            Button_delete_code.setVisibility(View.VISIBLE);
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

            Button_delete_code.setVisibility(View.GONE);
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
                double XAttuale = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                double YAttuale = (Double) MultiCmd_posizione_Y.getValue() / 1000d;
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
    private void Show_info_entità() {
        //info
        if (ElemSelezionati.size() > 0) {
            if (ElemSelezionati.size() > 1)
                Button_explode.setVisibility(View.VISIBLE);
            else
                Button_explode.setVisibility(View.GONE);
            if (ElemSelezionati.get(0) instanceof ElementFeed) {
                ImageView.setVisibility(View.GONE);
                TextView_valore_A.setVisibility(View.GONE);
                TextView_valore_B.setVisibility(View.GONE);
                Button_traform_toLine.setVisibility(View.VISIBLE);
                Button_traform_toArc.setVisibility(View.GONE);
                Button_traform_toZigZag.setVisibility(View.VISIBLE);
                Button_traform_toFeed.setVisibility(View.VISIBLE);
                //Button_explode.setVisibility(View.GONE);
            } else {
                ImageView.setVisibility(View.VISIBLE);
                Button_traform_toArc.setVisibility(View.GONE);
                Button_traform_toLine.setVisibility(View.VISIBLE);
                Button_traform_toZigZag.setVisibility(View.VISIBLE);
                Button_traform_toFeed.setVisibility(View.VISIBLE);
              //  Button_explode.setVisibility(View.VISIBLE);
                if (ElemSelezionati.get(0) instanceof ElementLine || ElemSelezionati.get(0) instanceof ElementArc) {
                    ImageView.setBackground(getResources().getDrawable(R.drawable.info_cucitura));
                    TextView_valore_A.setVisibility(View.VISIBLE);
                    TextView_valore_B.setVisibility(View.GONE);
                    TextView_valore_A.setText("" + ElemSelezionati.get(0).passo);

                    if (ElemSelezionati.get(0) instanceof ElementArc) {
                        Button_traform_toArc.setVisibility(View.VISIBLE);
                    }
                }
                if (ElemSelezionati.get(0) instanceof ElementZigZag) {
                    ImageView.setBackground(getResources().getDrawable(R.drawable.info_travetta));
                    TextView_valore_B.setVisibility(View.VISIBLE);
                    TextView_valore_A.setVisibility(View.VISIBLE);
                    try{
                        TextView_valore_B.setText("" + ((ElementZigZag) ElemSelezionati.get(0)).altezza);
                        TextView_valore_A.setText("" + ElemSelezionati.get(0).passo);

                    }
                    catch (Exception e) {
                           e.printStackTrace();
                    }
                }
                if (ElemSelezionati.get(0) instanceof ElementArcZigZag) {
                    ImageView.setBackground(getResources().getDrawable(R.drawable.info_travetta));
                    TextView_valore_B.setVisibility(View.VISIBLE);
                    TextView_valore_A.setVisibility(View.VISIBLE);
                    TextView_valore_B.setText("" + ((ElementArcZigZag) ElemSelezionati.get(0)).altezza);
                    TextView_valore_A.setText("" + ElemSelezionati.get(0).passo);

                    Button_traform_toArc.setVisibility(View.VISIBLE);
                }
            }
        } else {
            ImageView.setVisibility(View.GONE);
            TextView_valore_A.setVisibility(View.GONE);
            TextView_valore_B.setVisibility(View.GONE);
            Button_traform_toLine.setVisibility(View.GONE);
            Button_traform_toArc.setVisibility(View.GONE);
            Button_traform_toZigZag.setVisibility(View.GONE);
            Button_traform_toFeed.setVisibility(View.GONE);
            Button_explode.setVisibility(View.GONE);
        }
    }

    /**
     * TODO
     *
     * @param stringa_allarmi
     */
    private void MostraAllarmiCn(String stringa_allarmi) {
        if (!Allarme_mostrato && stringa_allarmi != null && stringa_allarmi != "") {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
            alertDialog.setCancelable(false);
            alertDialog.setTitle("Allarm");
            alertDialog.setMessage(stringa_allarmi);
            alertDialog.show();
        }
        Allarme_mostrato = true;
        str_allarmi = ""; //per non rientrare
    }

    /**
     * TODO
     */
    private void Emergenza() {
        if ((Double) MultiCmd_tasto_verde.getValue() == 0.0d || (Double) MultiCmd_CH1_in_emergenza.getValue() == 1.0d) {
            KillThread();
            Utility.ClearActivitiesTopToEmergencyPage(getApplicationContext());
        }
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
        if (Chiamante == PAGE_UDF_T1_DX)
            intent_par.putExtra("Chiamante", "T1_R");
        if (Chiamante == PAGE_UDF_T1_SX)
            intent_par.putExtra("Chiamante", "T1_L");
        if (Chiamante == PAGE_UDF_T2_DX)
            intent_par.putExtra("Chiamante", "T2_R");
        if (Chiamante == PAGE_UDF_T2_SX)
            intent_par.putExtra("Chiamante", "T2_L");


        startActivityForResult(intent_par, RESULT_PAGE_LOAD_EEP);
    }

    /**
     * TODO
     */
    private void Fai_Esci() {
        switch (step_Fai_Esci) {
            case 0:
                MultiCmd_go_Home.setValue(1.0d);
                sl.WriteItem(MultiCmd_go_Home);
                step_Fai_Esci = 10;
                break;
            case 10:
                sl.ReadItem(MultiCmd_go_Home);
                if ((Double) MultiCmd_go_Home.getValue() == 0.0d)
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
     */
    private void Fai_Home(boolean Fai_killThread) {

        switch (step_Home) {
            case 0:
                MultiCmd_go_Home.setValue(1.0d);
                sl.WriteItem(MultiCmd_go_Home);
                step_Home = 10;
                break;
            case 10:
                sl.ReadItem(MultiCmd_go_Home);
                if ((Double) MultiCmd_go_Home.getValue() == 0.0d)
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

            default:
                break;
        }
    }

    /**
     * TODO
     *
     * @param activeStep
     */
    private void Moveto(JamPointStep activeStep) {
        switch (info_StepPiuMeno.MacStati_StepSingolo) {
            case 10:
                MultiCmd_Vb_OutPiedino_su.setValue(1.0d);
                sl.WriteItem(MultiCmd_Vb_OutPiedino_su);

                float X_destinazione = 0.0f, Y_destinazione = 0.0f;

                if (activeStep != null) {
                    X_destinazione += activeStep.p.x;
                    Y_destinazione += activeStep.p.y;
                } else
                    info_StepPiuMeno.MacStati_StepSingolo = 0;

                double X = Double.valueOf(X_destinazione);                              //invio quote al PLC
                double Y = Double.valueOf(Y_destinazione);

                Double LimiteXNegativo = (Double) MultiCmd_Vq_104_READ_FC_ind_X.getValue() / 1000;
                Double LimiteXPositivo = (Double) MultiCmd_Vq_105_READ_FC_ava_X.getValue() / 1000;
                Double LimiteYNegativo = (Double) MultiCmd_Vq_106_READ_FC_ind_Y.getValue() / 1000;
                Double LimiteYPositivo = (Double) MultiCmd_Vq_107_READ_FC_ava_Y.getValue() / 1000;

                if (X < LimiteXNegativo || X > LimiteXPositivo || Y < LimiteYNegativo || Y > LimiteYPositivo) {
                    X = 0;      //se le coordinate sono fuori area per non bloccare il tutto lo faccio passare per 0
                    Y = 0;      //se le coordinate sono fuori area per non bloccare il tutto lo faccio passare per 0
                }

                // se sono già sopra le quote da raggiungere allora esco senza muovermi e ricomincio
                if (X == (Double) MultiCmd_posizione_X.getValue() && Y == (Double) MultiCmd_posizione_Y.getValue()) {
                    info_StepPiuMeno.MacStati_StepSingolo = 0;
                    break;
                }

                MultiCmd_quota_destinazione_X.setValue(X * 1000);
                MultiCmd_quota_destinazione_Y.setValue(Y * 1000);

                MultiCmd_HmiMoveXY.setValue(1.0d);
                MultiCmd_Start_movimento_Y.setValue(1.0d);

                MultiCmdItem[] Dati_out = new MultiCmdItem[]{MultiCmd_quota_destinazione_X, MultiCmd_quota_destinazione_Y,
                        MultiCmd_HmiMoveXY};
                sl.WriteItems(Dati_out);

                info_StepPiuMeno.MacStati_StepSingolo = 25;

                break;
            case 25:
                if ((Double) MultiCmd_XY_fermi.getValue() == 1.0d) { //aspetto che si muove
                    info_StepPiuMeno.MacStati_StepSingolo = 0;
                    info_StepPiuMeno.comando = Info_StepPiuMeno.Comando.NULL;
                }
                break;
            default:
                break;
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

                if ((Double) MultiCmd_XY_fermi.getValue() == 1.0d) { //se X e Y sono fermi
                    info_StepPiuMeno.MacStati_StepSingolo = 20;
                }//mofifica Mantini dalla Cina
                int idx = ricetta.getActiveStepIndex();
                if (ricetta.getActiveStepIndex() == -1 || ricetta.getActiveStepIndex() == 0 || info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.N_SALTO) {     //se sono sul punto di carico
                    //alzo piedino nel caso faccio un psostamento dal punto di carico oppure faccio un aslto di n punti
                    MultiCmd_RichiestaPiedinoSu.setValue(1.0d);
                    sl.WriteItem(MultiCmd_RichiestaPiedinoSu);
                }
                ElemSelezionati.clear();
                break;
            case 20:
                JamPointStep step = new JamPointStep();
                if (info_StepPiuMeno.direzione == Info_StepPiuMeno.Direzione.AVANTI) {
                    try {
                        //alzo il piedino se è basso e se trovo un feed
                        if ((ricetta.isNextElementFeed()) && (double) MultiCmd_Status_Piedino.getValue() == 0.0d) {
                            MultiCmd_RichiestaPiedinoSu.setValue(1.0d);
                            sl.WriteItem(MultiCmd_RichiestaPiedinoSu);
                        }

                        for (int i = 0; i < nPunti; i++)
                            step = ricetta.goToNextStep();

                        if (ElemSelezionati != null)
                            ElemSelezionati.clear();    //se il LIST è vuoto allora indica che ho scorso il programma come steps altrimenti con selectNextEntity

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "getStepAfter catch Singolo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        //alzo il piedino se è basso e se trovo un feed
                        if ((ricetta.isPreviousElementFeed()) && (double) MultiCmd_Status_Piedino.getValue() == 0.0d) {
                            MultiCmd_RichiestaPiedinoSu.setValue(1.0d);
                            sl.WriteItem(MultiCmd_RichiestaPiedinoSu);
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

                        if (ElemSelezionati != null)
                            ElemSelezionati.clear();    //se il LIST è vuoto allora indica che ho scorso il programma come steps altrimenti con selectNextEntity

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "getStepBefore catch Singolo", Toast.LENGTH_SHORT).show();
                    }
                }


                for (Element elementi : List_entita) {     //selezione
                    elementi.isSelected = false;       //deseleziono tutti gli element
                }
                for (Element elementi : ElemSelezionati) {     //selezione
                    elementi.isSelected = false;       //deseleziono gli element dell'entità
                }




                float X_destinazione = 0.0f, Y_destinazione = 0.0f;
                if (step == null) {
                    MultiCmd_RichiestaPiedinoSu.setValue(1.0d);
                    sl.WriteItem(MultiCmd_RichiestaPiedinoSu);
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


                Double LimiteXNegativo = (Double) MultiCmd_Vq_104_READ_FC_ind_X.getValue() / 1000;
                Double LimiteXPositivo = (Double) MultiCmd_Vq_105_READ_FC_ava_X.getValue() / 1000;
                Double LimiteYNegativo = (Double) MultiCmd_Vq_106_READ_FC_ind_Y.getValue() / 1000;
                Double LimiteYPositivo = (Double) MultiCmd_Vq_107_READ_FC_ava_Y.getValue() / 1000;

                if (X < LimiteXNegativo || X > LimiteXPositivo || Y < LimiteYNegativo || Y > LimiteYPositivo) {
                    X = 0;      //se le coordinate sono fuori area per non bloccare il tutto lo faccio passare per 0
                    Y = 0;      //se le coordinate sono fuori area per non bloccare il tutto lo faccio passare per 0
                }

                // se sono già sopra le quote da raggiungere allora esco senza muovermi e ricomincio
                if (X == (Double) MultiCmd_posizione_X.getValue() && Y == (Double) MultiCmd_posizione_Y.getValue()) {
                    info_StepPiuMeno.MacStati_StepSingolo = 0;
                    break;
                }

                MultiCmd_quota_destinazione_X.setValue(X * 1000);
                MultiCmd_quota_destinazione_Y.setValue(Y * 1000);

                MultiCmd_HmiMoveXY.setValue(1.0d);

                MultiCmdItem[] Dati_out = new MultiCmdItem[]{MultiCmd_quota_destinazione_X, MultiCmd_quota_destinazione_Y,
                        MultiCmd_HmiMoveXY};
                sl.WriteItems(Dati_out);

                info_StepPiuMeno.MacStati_StepSingolo = 25;
                break;
            case 25:
                if ((Double) MultiCmd_XY_fermi.getValue() == 1.0d) { //aspetto che si muove
                    info_StepPiuMeno.MacStati_StepSingolo = 30;
                }
                break;
            case 30:
                if ((Double) MultiCmd_XY_fermi.getValue() == 1.0d) { //aspetto che sono fermo

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

        File file = new File(File_Xml_path);

        int i = file.getName().lastIndexOf('.');
        String name = file.getName().substring(0, i);
        input.setText(name);
        input.setFocusable(false);
        input.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog_lettere.Lancia_KeyDialogo_lettere(Modifica_programma.this, input, "");
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
                            if (Folder == null) {
                                dir = new File(root.getAbsolutePath() + "/ricette");
                            } else {
                                dir = new File(Folder);
                            }
                            dir.mkdirs();
                            File file = new File(dir, input.getText().toString() + ".xml");
                            File file1 = new File(dir, input.getText().toString() + ".usr");
                            if (file.exists() || file1.exists()) {
                                final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(Modifica_programma.this);

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

    //#endregion Utilities

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_PAGE_LOAD_EEP:
                Intent databack = new Intent();
                switch (Chiamante) {
                    case PAGE_UDF_T1_DX:
                        databack.setData(Uri.parse("CARICATO_T1_DX"));  break;
                    case PAGE_UDF_T1_SX:
                        databack.setData(Uri.parse("CARICATO_T1_SX"));  break;
                    case PAGE_UDF_T2_DX:
                        databack.setData(Uri.parse("CARICATO_T2_DX"));  break;
                    case PAGE_UDF_T2_SX:
                        databack.setData(Uri.parse("CARICATO_T2_SX"));  break;
                    default:
                        break;
                }

                setResult(RESULT_OK, databack);
                KillThread();
                finish();
                break;
            case POPUPFOLDER:
                if (resultCode == 0) {
                    Mostra_Tutte_Icone();
                    if (!Thread_Running) {
                        StopThread = false;
                        MyAndroidThread_Modifica myTask = new MyAndroidThread_Modifica(Modifica_programma.this);
                        Thread t1 = new Thread(myTask, "Main myTask");
                        t1.start();
                        Log.d("JAM TAG", "Start Modifica_programma Thread");
                    }
                } else {
                    Folder = data.getExtras().getString("FolderPath");
                    try {
                        View v = new View(this);
                        onclickExit(v);
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

    private void KillThread() {
        if(myView != null) {
            myView.release();    //rilascio la memoria delle bitmap
            myView = null;
        }
        StopThread = true;
        try {

            //Register or UnRegister your broadcast receiver here
            LocalBroadcastManager.getInstance(this).unregisterReceiver(Code_MessageReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver_KeyDialog);

        } catch(IllegalArgumentException e) {

            e.printStackTrace();
        }

        try {

            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("JAM TAG", "Stop Modifica_programma Thread");

    }

    /**
     * The enum that contains the allowed buttons status
     */
    enum ButtonsVisibilityStatus {VISIBLE, HIDDEN, NULL}

    /**
     * Thread for communicate with the PLC
     */
    class MyAndroidThread_Modifica implements Runnable {
        Activity activity;


        public MyAndroidThread_Modifica(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            while (true) {
                Thread_Running = true;
                Boolean rc_error;
                try {
                    Thread.sleep((long) 10d);
                    if (StopThread) {
                        MultiCmd_Vn3804_pagina_touch.setValue(0.0d);
                        sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                        Thread_Running = false;
                        if (info_modifica.comando == Info_modifica.Comando.FAIHOME_AND_EXIT)
                        {
                            Intent databack = new Intent();
                            switch (Chiamante) {
                                case PAGE_UDF_T1_DX:
                                    databack.setData(Uri.parse("CARICATO_T1_DX"));  break;
                                case PAGE_UDF_T1_SX:
                                    databack.setData(Uri.parse("CARICATO_T1_SX"));  break;
                                case PAGE_UDF_T2_DX:
                                    databack.setData(Uri.parse("CARICATO_T2_DX"));  break;
                                case PAGE_UDF_T2_SX:
                                    databack.setData(Uri.parse("CARICATO_T2_SX"));  break;
                                default:
                                    break;
                            }

                            setResult(RESULT_OK, databack);
                         //   KillThread();
                            finish();



                        }


                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                }

                if (sl.IsConnected()) {

                    if (first_cycle) {

                        first_cycle = false;
            //            Mci_Vn3081_override_rotaz.valore = 10d;
             //           Mci_Vn3081_override_rotaz.write_flag = true;

                        Multicmd_Vb4807_PinzeAlteDopoPC.setValue(0.0d);
                        sl.WriteItem(Multicmd_Vb4807_PinzeAlteDopoPC);
                        Multicmd_Vb4907_PinzeAlteDopoPC_C2.setValue(0.0d);
                        sl.WriteItem(Multicmd_Vb4907_PinzeAlteDopoPC_C2);
                        sl.ReadItem(MultiCmd_Vq_104_READ_FC_ind_X);
                        sl.ReadItem(MultiCmd_Vq_105_READ_FC_ava_X);
                        sl.ReadItem(MultiCmd_Vq_106_READ_FC_ind_Y);
                        sl.ReadItem(MultiCmd_Vq_107_READ_FC_ava_Y);

                    }
                    try{
                        MultiCmd_Vn3804_pagina_touch.setValue(1003.0d);
                        sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                        sl.WriteQueued();
                        sl.ReadItems(mci_array_read_all);
                        rc_error = sl.getReturnCode() != 0;

                        if (sl.getReturnCode() != 0) {
                            //se non riceve bene i valori provo a chiudere e riaprire il Socket
                            sl.Close();
                            Thread.sleep((long) 300d);
                            sl.Connect();
                            Thread.sleep((long) 300d);
                            //
                            rc_error = true;
                        }
                    }catch (Exception err){
                        rc_error = true;
                    }

                    if (rc_error == false) { //se ho avuto un errore di ricezione salto

                        if (info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.SINGOLO || info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.VELOCE) {
                            StepSingolo(1);
                        }
                        if (info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.N_SALTO) {
                            StepSingolo(Info_StepPiuMeno.numeroRipetuto);
                        }
                        if (info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.TO_STEP_ATTIVO) {
                            Moveto(ricetta.getActiveStep());
                        }

                        if (info_modifica.comando == Info_modifica.Comando.HOME) Fai_Home(false);
                        if (info_modifica.comando == Info_modifica.Comando.FAIHOME_AND_EXIT) Fai_Home(true);
                        if (info_modifica.comando == Info_modifica.Comando.ESCI) Fai_Esci();

                        GestiscoFreccia(Mci_write_JogYMeno);
                        GestiscoFreccia(Mci_write_JogYPiu);
                        GestiscoFreccia(Mci_write_JogXPiu);
                        GestiscoFreccia(Mci_write_JogXMeno);
                        GestiscoFreccia(Mci_write_jogXPiuYMeno);
                        GestiscoFreccia(Mci_write_jogXPiuYPiu);
                        GestiscoFreccia(Mci_write_jogXMenoYPiu);
                        GestiscoFreccia(Mci_write_jogXMenoYMeno);
                     //   GestiscoFreccia(Mci_write_jog_Rotaz_sx);
                    //    GestiscoFreccia(Mci_write_jog_Rotaz_dx);

                        Utility.ScrivoVbVnVq(sl, Mci_Vb_OutPiedino_su);
                //        Utility.ScrivoVbVnVq(sl, Mci_Vn3081_override_rotaz);

                        Utility.GestiscoMci_Out_Toggle(sl, Mci_Sblocca_Ago);
                        Utility.GestiscoMci_Out_Toggle(sl,Mci_write_Vb4014_JogSlowFast);

                        GestioneEntitaPiu();
                        GestioneEntitaMeno();
                        double X = (Double) MultiCmd_posizione_X.getValue() / 1000d;
                        double Y = (Double) MultiCmd_posizione_Y.getValue() / 1000d;
                        Coord_Pinza.set(X, Y, ricetta);
                        //Lettura delle emergenze attive

                        MultiCmdItem mci = new MultiCmdItem(1, MultiCmdItem.dtAL, 9, MultiCmdItem.dpAL_M32, sl);
                        sl.ReadItem(mci);
                        int[] emebuf = (int[]) mci.getValue();

                        Map<String, SmartAlarm> eme = new LinkedHashMap<String, SmartAlarm>();

                        Integer idx2 = 0;

                        if (!Allarme_mostrato) {
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
                                for (SmartAlarm al : eme.values()) {
                                    MultiCmdItem descmci = new MultiCmdItem(1, MultiCmdItem.dtAL, al.getIndex(), MultiCmdItem.dpAL_M32_Description, sl);
                                    sl.ReadItem(descmci);
                                    String d = (String) descmci.getValue();
                                    str_allarmi = str_allarmi + d + "\n";
                                    if (d.equals(""))
                                        d = al.getParameters()[0].toString() + ": " + al.getParameters()[1].toString() + ", " + al.getParameters()[2].toString();

                                    al.setDescription(d);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
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
                      Emergenza();



                    if (info_StepPiuMeno.tipo_spostamento == Info_StepPiuMeno.Tipo_spostamento.N_SALTO && info_StepPiuMeno.comando == Info_StepPiuMeno.Comando.NULL) {
                        //finito salto di n punti
                        info_StepPiuMeno.tipo_spostamento = Info_StepPiuMeno.Tipo_spostamento.NULL;
                        TextView_info.setText(info_StepPiuMeno.last_testo_textView_info);  //
                        info_StepPiuMeno.last_testo_textView_info = "Info:";
                    }

                    if (info_modifica.comando == Info_modifica.Comando.HOME_DONE) {
                        info_modifica.comando = Info_modifica.Comando.Null;
            //            Mci_Vn3081_override_rotaz.valore = 10d;
             //           Mci_Vn3081_override_rotaz.write_flag = true;
                        Mostra_Tutte_Icone();
                    }

                    if (info_modifica.comando == Info_modifica.Comando.ESCI_DONE_AZZERAMENTO) {
                        info_modifica.comando = Info_modifica.Comando.Null;
                        ricetta.clearActiveStep();  //imposta indice step a -1
                        ricetta.repair();  //ripara la ricetta nel caso ci siano degli errori di continuità o di coordinate
                        Run_Alert_dialog();
                    }

                    Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_Sblocca_Ago, Button_Sgancio_ago, "ic_sblocca_ago_press", "ic_sblocca_ago");
                    Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), Mci_write_Vb4014_JogSlowFast, Button_JOG_SlowFast, "ic_slow", "ic_fast");



                    Scrivi_codice_HMI();
                    TextView_XAss.setText("" + ((Double) MultiCmd_posizione_X.getValue() / 1000d));
                    TextView_YAss.setText("" + ((Double) MultiCmd_posizione_Y.getValue() / 1000d));
                    ShowQuoteRelative();
                    TextView_tot_punti.setText("" + ricetta.getStepsCount());
                    ShowIndicePunto(ricetta.getActiveStepIndex());
                    if (Coord_Pinza.XCoord_precedente != Coord_Pinza.XCoordPosPinza || Coord_Pinza.YCoord_precedente != Coord_Pinza.YCoordPosPinza) {
                        myView.AggiornaCanvas(true);
                        Coord_Pinza.XCoord_precedente = Coord_Pinza.XCoordPosPinza;
                        Coord_Pinza.YCoord_precedente = Coord_Pinza.YCoordPosPinza;
                    }
                    Show_info_entità();
                    MostraAllarmiCn(str_allarmi);
                    } catch (Exception e) {

                    }
                }
            });
        }
    }
}
