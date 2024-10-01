package com.jam_int.jt882_m8;

import static android.content.Intent.ACTION_MEDIA_MOUNTED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jamint.ricette.CenterPointRadius;
import com.jamint.ricette.Element;
import com.jamint.ricette.MathGeoTri;
import com.jamint.ricette.Ricetta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import communication.MultiCmdItem;
import communication.ShoppingList;
import me.jahnen.libaums.core.UsbMassStorageDevice;
import me.jahnen.libaums.core.fs.FileSystem;
import me.jahnen.libaums.core.fs.UsbFile;

public class TascaFrecceActivity extends Activity {

    final private static int RESULT_PAGE_EMG = 101;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    /**
     * Receiver for handle the punto carico result
     */
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle_txt = intent.getExtras();
            String valore = bundle_txt.getString("ret_valore");

            if (!valore.equals("")) {
                Values.Dpc = Float.parseFloat(valore);
            }
        }
    };
    Ricetta r = new Ricetta(Values.plcType);
    int punti = 0, mc_stati_step = 0;
    ArrayList<String> str = new ArrayList<String>();
    ArrayList<String> points_list = new ArrayList<String>();
    ArrayList<Element> List_entità = new ArrayList<>();
    Dynamic_view myView_Tasca;
    Thread t1;
    CoordPosPinza Coord_Pinza = new CoordPosPinza();
    int step_Fai_Esci = 0;
    FrameLayout frame_canvas;
    /**
     * ShoppingList for communicate with PLC
     */
    ShoppingList sl;
    /**
     * Thread
     */
    Boolean Thread_Running = false, StopThread = false, Debug_mode = false, first_cycle = true,Esegui_Azzeramento = false,Fai_finish = false;
    /**
     * PLC vars
     */
    MultiCmdItem[] mci_array_read_all;
    MultiCmdItem MultiCmd_tasto_verde, MultiCmd_CH1_in_emergenza, MultiCmd_Vb556_PCConnesso,MultiCmd_Vb72_HmiMoveXY,
            MultiCmd_Vb557_PCPiedino, MultiCmd_Vb52_PCHome, MultiCmd_Vn3804_pagina_touch,
            MultiCmd_VQ110, MultiCmd_VQ111_PosX, MultiCmd_VQ112_PosY, MultiCmd_Vb253_Modalita_Step, MultiCmd_Vb88_Sgancio_ago, MultiCmd_VQ7002_QuotaDestinazioneX, MultiCmd_VQ7022_QuotaDestinazioneY,
            Multicmd_Vb4807_PinzeAlteDopoPC,
            MultiCmd_JogXMeno, MultiCmd_JogXPiu, MultiCmd_jogXPiuYPiu, MultiCmd_jogXPiuYMeno, MultiCmd_jogXMenoYPiu, MultiCmd_jogXMenoYMeno, MultiCmd_JogYMeno, MultiCmd_JogYPiu;
    Mci_write Mci_write_Vb556_PCConnesso = new Mci_write(),
            Mci_write_Vb557_PCPiedino = new Mci_write(),
            Mci_write_Vb52_PCHome = new Mci_write(),
            Mci_write_Vb253_Modalita_Step = new Mci_write(),
            MciWrite_mci_vb88_sblocca_ago = new Mci_write(),
            Mci_write_JogXMeno = new Mci_write(),
            Mci_write_JogXPiu = new Mci_write(),
            Mci_write_JogYMeno = new Mci_write(),
            Mci_write_JogYPiu = new Mci_write(),
            Mci_write_jogXPiuYPiu = new Mci_write(),
            Mci_write_jogXPiuYMeno = new Mci_write(),
            Mci_write_jogXMenoYPiu = new Mci_write(),
            Mci_write_jogXMenoYMeno = new Mci_write(),
            Mci_write_VQ7002_QuotaDestinazioneX = new Mci_write(),
            Mci_write_VQ7022_QuotaDestinazioneY = new Mci_write(),
            Mci_write_MultiCmd_Vb72_HmiMoveXY = new Mci_write();

    /**
     * UI components
     */
    Button Button_arrow_up, Button_freccia_giu, Button_arrow_right, Button_arrow_left, Button_arrow_up_right, Button_arrow_down_right, Button_arrow_down_left,
            Button_arrow_up_left, Button_tasto_enter, Button_Sgancio_ago;
    TextView TextView_XAss, TextView_YAss, TextView_Info;
    TableLayout TableLayout_punti;
    Button Button_nextPage;
    boolean EditedPTS = false;
    Integer timeout_counter, Timeout_emergenza = 5;
    /**
     * USB
     */
    UsbFile root;
    UsbMassStorageDevice device_usb;
    FileSystem currentFs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasca_frecce);

        frame_canvas = findViewById(R.id.frameLayout);
        myView_Tasca = new Dynamic_view(TascaFrecceActivity.this, 380, 400, List_entità, 1F, Coord_Pinza, false, -110, 10, null, getResources().getDimension(R.dimen.modifica_programma_activity_framelayout_width), getResources().getDimension(R.dimen.modifica_programma_activity_framelayout_height));
        frame_canvas.addView(myView_Tasca);
        myView_Tasca.setBackgroundColor(Color.LTGRAY);

        myView_Tasca.frameDrawer.scale(-1F, 1F);
        myView_Tasca.frameDrawer.translate(-300, 30);        //qui la tasca è in alto a dx

        Button_arrow_up = findViewById(R.id.button_arrow_up);
        Button_freccia_giu = findViewById(R.id.button_freccia_giu);
        Button_arrow_right = findViewById(R.id.button_arrow_right);
        Button_arrow_left = findViewById(R.id.button_arrow_left);
        Button_arrow_up_right = findViewById(R.id.button_arrow_up_right);
        Button_arrow_down_right = findViewById(R.id.button_arrow_down_right);
        Button_arrow_down_left = findViewById(R.id.button_arrow_down_left);
        Button_arrow_up_left = findViewById(R.id.button_arrow_up_left);

        Button_Sgancio_ago = findViewById(R.id.button_sblocca_ago);
        Button_nextPage = findViewById(R.id.button_nextPage);
        Button_tasto_enter = findViewById(R.id.button_tasto_enter);

        TextView_XAss = findViewById(R.id.textView_XAss_value);
        TextView_YAss = findViewById(R.id.textView_YAss_value);
        TextView_Info = findViewById(R.id.textView_Info);

        Button_nextPage.setVisibility(View.GONE);

        init_TableRow();

        // Request permission for read and write files
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

        if (GetUSBConnectionStatus()) {
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

        IntentFilter filter_attached = new IntentFilter(ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(usbReceiver, filter_attached);
        IntentFilter filter_mounted = new IntentFilter(ACTION_MEDIA_MOUNTED);
        registerReceiver(usbReceiver, filter_mounted);
        IntentFilter filter_permission = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter_permission);
        IntentFilter filter_detached = new IntentFilter(ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbReceiver, filter_detached);
    }

    /**
     * Button for exit from this activity
     *
     * @param view
     */
    public void onclickExit(View view) {

        MultiCmd_Vb556_PCConnesso.setValue(0.0d);
        sl.WritePush(MultiCmd_Vb556_PCConnesso);
        sl.WriteItem(MultiCmd_Vb556_PCConnesso);

        /** Martino     Blocco l'ago quando esco da Tascafrecce */
        MciWrite_mci_vb88_sblocca_ago.mci.setValue(0.0d);
        sl.WriteItem(MciWrite_mci_vb88_sblocca_ago.mci);
        // Premuto no

        // Start the thread
        if (!Thread_Running) {
            StopThread = false;
            MyAndroidThread_Modifica myTask = new MyAndroidThread_Modifica(TascaFrecceActivity.this);
            t1 = new Thread(myTask, "Main myTask");
            t1.start();
        }
        step_Fai_Esci = 0;              //preparo per il ciclo azzeramento assi
        Esegui_Azzeramento = true;     //esegue azzeramento e poi KillThread();

    }

    /**
     * Button for save a point in the list of points
     *
     * @param v
     */
    public void BtnSavePoint(View v) {
        ViewFlipper vf1 = findViewById(R.id.ViewFlipper1);
        if (vf1.getDisplayedChild() == 0) {
            if (points_list.size() < 9) {
                points_list.add(TextView_XAss.getText().toString() + ", " + TextView_YAss.getText().toString());
                if (str.size() == 0) {
                    str.add("0");   //ipotizzo per creare il campo nell'array
                    str.add("9");   //ipotizzo per creare il campo nell'array
                }
                str.add(TextView_XAss.getText().toString() + ", " + TextView_YAss.getText().toString());
                punti++;
                ScriviCoordinateSuTable(punti, TextView_XAss.getText().toString(), TextView_YAss.getText().toString());
            }
            on_click_draw();
        } else if (vf1.getDisplayedChild() == 1) {
            if (points_list.size() < 4) {
                points_list.add(TextView_XAss.getText().toString() + ", " + TextView_YAss.getText().toString());
                if (str.size() == 0) {
                    str.add("0");   //ipotizzo per creare il campo nell'array
                    str.add("4");   //ipotizzo per creare il campo nell'array
                }
                str.add(TextView_XAss.getText().toString() + ", " + TextView_YAss.getText().toString());
                punti++;
                ScriviCoordinateSuTable(punti, TextView_XAss.getText().toString(), TextView_YAss.getText().toString());
            }
            on_click_draw();
        } else if (vf1.getDisplayedChild() == 2) {
            if (points_list.size() < 8) {
                points_list.add(TextView_XAss.getText().toString() + ", " + TextView_YAss.getText().toString());
                if (str.size() == 0) {
                    str.add("0");   //ipotizzo per creare il campo nell'array
                    str.add("8");   //ipotizzo per creare il campo nell'array
                }
                str.add(TextView_XAss.getText().toString() + ", " + TextView_YAss.getText().toString());
                punti++;
                ScriviCoordinateSuTable(punti, TextView_XAss.getText().toString(), TextView_YAss.getText().toString());
            }
            on_click_draw();
        } else if (vf1.getDisplayedChild() == 3) {
            if (points_list.size() < 6) {
                points_list.add(TextView_XAss.getText().toString() + ", " + TextView_YAss.getText().toString());
                if (str.size() == 0) {
                    str.add("0");   //ipotizzo per creare il campo nell'array
                    str.add("6");   //ipotizzo per creare il campo nell'array
                }
                str.add(TextView_XAss.getText().toString() + ", " + TextView_YAss.getText().toString());
                punti++;
                ScriviCoordinateSuTable(punti, TextView_XAss.getText().toString(), TextView_YAss.getText().toString());
            }
            on_click_draw();
        }
    }

    /**
     * Button for open the activity for import a DXF
     *
     * @param v
     */
    public void BtnImportDxf(View v) {
        Intent intent_par = new Intent(getApplicationContext(), PopUpImportDxf.class);
        startActivityForResult(intent_par, 2);
    }

    /**
     * Button for export all the pts files to a USB
     *
     * @param v
     */
    public void BtnExportPts(View v) {
        try {
            UsbManager m = (UsbManager) getApplicationContext().getSystemService(USB_SERVICE);
            HashMap<String, UsbDevice> devices = m.getDeviceList();
            Collection<UsbDevice> ite = devices.values();
            UsbDevice[] usbs = ite.toArray(new UsbDevice[]{});

            if (usbs.length != 0) {
                String path = Environment.getExternalStorageDirectory().toString() + "/JamData/punti";
                File directory = new File(path);
                File[] files = directory.listFiles();

                UsbFile folder = root;
                try {
                    folder.createDirectory("punti");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int count = 0;
                for (int i = 0; i < files.length; i++) {
                    Utility.copyFileToUsb(files[i], folder);
                    count++;
                }

                if (count == files.length) {
                    Toast.makeText(getApplicationContext(), "Pts files copied in the folder 'punti'", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.UsbXmlUnmounted, Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Function for let the user insert all the points and at the end display the draw
     */
    public void on_click_draw() {
        try {

            ViewFlipper vf1 = findViewById(R.id.ViewFlipper1);
            if (vf1.getDisplayedChild() == 0) {
                ArrayList<String> st = new ArrayList<String>();
                st = points_list;
                if (st.size() == 0) TextView_Info.setText(getString(R.string.MuoviP1));
                if (st.size() == 1) TextView_Info.setText(getString(R.string.MuoviP2));
                if (st.size() == 2) TextView_Info.setText(getString(R.string.MuoviP3));
                if (st.size() == 3) TextView_Info.setText(getString(R.string.MuoviP4));
                if (st.size() == 4) TextView_Info.setText(getString(R.string.MuoviP5));
                if (st.size() == 5) TextView_Info.setText(getString(R.string.MuoviP6));
                if (st.size() == 6) TextView_Info.setText(getString(R.string.MuoviP7));
                if (st.size() == 7) TextView_Info.setText(getString(R.string.MuoviP8));
                if (st.size() == 8) TextView_Info.setText(getString(R.string.MuoviP9));
                if (st.size() == 9) {
                    if (points_list.get(1).endsWith(points_list.get(2)) && points_list.get(7).endsWith(points_list.get(8)) && points_list.get(3).endsWith(points_list.get(4)) && points_list.get(5).endsWith(points_list.get(6))) {
                        str.set(0, "1");
                    } else if (!points_list.get(1).endsWith(points_list.get(2)) && !points_list.get(7).endsWith(points_list.get(8))) {
                        str.set(0, "0");
                    }
                    str.set(1, "" + points_list.size());
                    TextView_Info.setText(getString(R.string.DoneNext));
                }


                r = new Ricetta(Values.plcType);

                try {
                    String[] start = st.get(0).split(",");
                    r.setDrawPosition(new PointF(Float.parseFloat(start[0]), Float.parseFloat(start[1])));

                    Line(st.get(0), st.get(1), st.get(2));
                    Line(st.get(2), st.get(3), st.get(4));
                    Line(st.get(4), st.get(5), st.get(6));
                    Line(st.get(6), st.get(7), st.get(8));
                    Line(st.get(8), st.get(0), st.get(0));
                } catch (Exception er) {
                    er.printStackTrace();
                }

                ArrayList List_entità = (ArrayList<Element>) r.elements;
                myView_Tasca.Ricalcola_entità_canvas(List_entità);

                myView_Tasca.AggiornaCanvas(true);
            } else if (vf1.getDisplayedChild() == 1) {
                ArrayList<String> st = new ArrayList<String>();
                st = points_list;
                if (st.size() == 0) TextView_Info.setText(getString(R.string.MuoviP1));
                if (st.size() == 1) TextView_Info.setText(getString(R.string.MuoviP2));
                if (st.size() == 2) TextView_Info.setText(getString(R.string.MuoviP3));
                if (st.size() == 3) TextView_Info.setText(getString(R.string.MuoviP4));
                if (st.size() == 4) {
                    str.set(0, "3");
                    str.set(1, "" + points_list.size());
                    TextView_Info.setText(getString(R.string.DoneNext));
                }

                r = new Ricetta(Values.plcType);

                try {
                    String[] start = st.get(0).split(",");
                    r.setDrawPosition(new PointF(Float.parseFloat(start[0]), Float.parseFloat(start[1])));

                    Line(st.get(0), st.get(1), st.get(1));
                    Line(st.get(1), st.get(2), st.get(2));
                    Line(st.get(2), st.get(3), st.get(3));
                    Line(st.get(3), st.get(0), st.get(0));
                } catch (Exception er) {
                    er.printStackTrace();
                }

                ArrayList List_entità = (ArrayList<Element>) r.elements;
                myView_Tasca.Ricalcola_entità_canvas(List_entità);
                myView_Tasca.AggiornaCanvas(true);
            } else if (vf1.getDisplayedChild() == 2) {
                ArrayList<String> st = new ArrayList<String>();
                st = points_list;
                if (st.size() == 0) TextView_Info.setText(getString(R.string.MuoviP1));
                if (st.size() == 1) TextView_Info.setText(getString(R.string.MuoviP2));
                if (st.size() == 2) TextView_Info.setText(getString(R.string.MuoviP3));
                if (st.size() == 3) TextView_Info.setText(getString(R.string.MuoviP4));
                if (st.size() == 4) TextView_Info.setText(getString(R.string.MuoviP5));
                if (st.size() == 5) TextView_Info.setText(getString(R.string.MuoviP6));
                if (st.size() == 6) TextView_Info.setText(getString(R.string.MuoviP7));
                if (st.size() == 7) TextView_Info.setText(getString(R.string.MuoviP8));
                if (st.size() == 8) {
                    str.set(0, "4");
                    str.set(1, "" + points_list.size());
                    TextView_Info.setText(getString(R.string.DoneNext));
                }

                r = new Ricetta(Values.plcType);

                try {
                    String[] start = st.get(0).split(",");
                    r.setDrawPosition(new PointF(Float.parseFloat(start[0]), Float.parseFloat(start[1])));

                    Line(st.get(0), st.get(1), st.get(1));
                    Line(st.get(1), st.get(2), st.get(3));
                    Line(st.get(3), st.get(4), st.get(4));
                    Line(st.get(4), st.get(5), st.get(6));
                    Line(st.get(6), st.get(7), st.get(7));
                    Line(st.get(7), st.get(0), st.get(0));
                } catch (Exception er) {
                    er.printStackTrace();
                }

                ArrayList List_entità = (ArrayList<Element>) r.elements;
                myView_Tasca.Ricalcola_entità_canvas(List_entità);
                myView_Tasca.AggiornaCanvas(true);
            } else if (vf1.getDisplayedChild() == 3) {
                ArrayList<String> st = new ArrayList<String>() {
                };
                st = points_list;
                if (st.size() == 0) TextView_Info.setText(getString(R.string.MuoviP1));
                if (st.size() == 1) TextView_Info.setText(getString(R.string.MuoviP2));
                if (st.size() == 2) TextView_Info.setText(getString(R.string.MuoviP3));
                if (st.size() == 3) TextView_Info.setText(getString(R.string.MuoviP4));
                if (st.size() == 4) TextView_Info.setText(getString(R.string.MuoviP5));
                if (st.size() == 5) TextView_Info.setText(getString(R.string.MuoviP6));

                if (st.size() == 6) {
                    str.set(0, "2");
                    str.set(1, "" + points_list.size() + 2); //Aggiungo i due punti che non vengono inseriti dell'utente
                    TextView_Info.setText(getString(R.string.DoneNext));
                }


                r = new Ricetta(Values.plcType);

                try {
                    String[] start = st.get(0).split(",");
                    r.setDrawPosition(new PointF(Float.parseFloat(start[0]), Float.parseFloat(start[1])));

                    Line(st.get(0), st.get(1), st.get(1));
                    Line(st.get(1), st.get(2), st.get(2));
                    Line(st.get(2), st.get(3), st.get(3));
                    Line(st.get(3), st.get(4), st.get(4));
                    Line(st.get(4), st.get(5), st.get(5));
                    Line(st.get(5), st.get(0), st.get(0));
                } catch (Exception er) {
                    er.printStackTrace();
                }

                ArrayList List_entità = (ArrayList<Element>) r.elements;
                myView_Tasca.Ricalcola_entità_canvas(List_entità);
                myView_Tasca.AggiornaCanvas(true);
            } else if (vf1.getDisplayedChild() == 4) {
                ArrayList<String> st = new ArrayList<String>();
                st = points_list;
                if (st.size() == 0) TextView_Info.setText(getString(R.string.MuoviP1));
                if (st.size() == 1) TextView_Info.setText(getString(R.string.MuoviP2));
                if (st.size() == 2) TextView_Info.setText(getString(R.string.MuoviP3));
                if (st.size() == 3) TextView_Info.setText(getString(R.string.MuoviP4));
                if (st.size() == 4) TextView_Info.setText(getString(R.string.MuoviP5));
                if (st.size() == 5) TextView_Info.setText(getString(R.string.MuoviP6));
                if (st.size() == 6) TextView_Info.setText(getString(R.string.MuoviP7));
                if (st.size() == 7) TextView_Info.setText(getString(R.string.MuoviP8));
                if (st.size() == 8) TextView_Info.setText(getString(R.string.MuoviP9));
                if (st.size() == 9) TextView_Info.setText(getString(R.string.MuoviP10));
                if (st.size() == 10) TextView_Info.setText(getString(R.string.MuoviP11));
                if (st.size() == 11) TextView_Info.setText(getString(R.string.MuoviP12));
                if (st.size() == 12) TextView_Info.setText(getString(R.string.MuoviP13));
                if (st.size() == 13) {
                    str.set(0, "5");
                    str.set(1, "" + points_list.size());
                    TextView_Info.setText(getString(R.string.DoneNext));
                }

                r = new Ricetta(Values.plcType);

                try {
                    String[] start = st.get(0).split(",");
                    r.setDrawPosition(new PointF(Float.parseFloat(start[0]), Float.parseFloat(start[1])));

                    Line(st.get(0), st.get(1), st.get(2));
                    Line(st.get(2), st.get(3), st.get(4));
                    Line(st.get(4), st.get(5), st.get(6));
                    Line(st.get(6), st.get(7), st.get(8));
                    Line(st.get(8), st.get(9), st.get(10));
                    Line(st.get(10), st.get(11), st.get(12));
                    Line(st.get(12), st.get(0), st.get(0));
                } catch (Exception er) {
                    er.printStackTrace();
                }

                ArrayList List_entità = (ArrayList<Element>) r.elements;
                myView_Tasca.Ricalcola_entità_canvas(List_entità);

                myView_Tasca.AggiornaCanvas(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(TascaFrecceActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Button for edit a point position
     *
     * @param v
     */
    public void on_click_edit_point(View v) {
        EditedPTS = true;
        for (int i = 0; i < 13; i++) {
            try {
                View row = TableLayout_punti.getChildAt(i);
                Drawable background = row.getBackground();
                if (background instanceof ColorDrawable) {
                    int color = ((ColorDrawable) background).getColor();
                    if (color == Color.GRAY) {
                        final TableRow TableRow_punti = new TableRow(this);

                        TextView txtP = new TextView(this);
                        TextView txtX = new TextView(this);
                        TextView txtY = new TextView(this);
                        if (!points_list.get(i).equals("")) {
                            txtP.setTextColor(Color.RED);
                            txtP.setWidth(40);
                            txtP.setTextSize(18);
                            txtP.setText("P" + (i + 1) + ":  ");
                            TableRow_punti.addView(txtP);

                            TableRow_punti.setClickable(true);

                            txtX.setWidth(60);
                            txtX.setTextSize(18);
                            txtX.setText(" ");
                            TableRow_punti.addView(txtX);

                            txtY.setWidth(60);
                            txtY.setTextSize(18);
                            txtY.setText(" ");
                            TableRow_punti.addView(txtY);

                            final int finalI = i;
                            TableRow_punti.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    for (int i = 0; i < 13; i++) {
                                        try {
                                            View row = TableLayout_punti.getChildAt(i);
                                            row.setBackgroundColor(Color.TRANSPARENT);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    v.setBackgroundColor(Color.GRAY);
                                    SpostoPinzeAlPuntoPrecedente(finalI + 2);
                                }
                            });

                            String oldx = GetCampoTableRow(i + 1, 2);
                            String oldy = GetCampoTableRow(i + 1, 3);

                            TableLayout_punti.removeViewAt(i);
                            TableLayout_punti.addView(TableRow_punti, i);

                            SetCampoTableRow(i + 1, 1, TextView_XAss.getText().toString());
                            SetCampoTableRow(i + 1, 2, TextView_YAss.getText().toString());
                            points_list.set(i, TextView_XAss.getText().toString() + "," + TextView_YAss.getText().toString());

                            for (int i1 = 1; i1 < 14; i1++) {
                                String x = GetCampoTableRow(i1, 2);
                                String y = GetCampoTableRow(i1, 3);
                                if (oldx.equals(x) && oldy.equals(y) && i1 != i + 1) {
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                                    final int i2 = i1;
                                    alertDialog.setMessage("P" + i1 + " have the same value, change it?");
                                    alertDialog.setPositiveButton("Yes",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    SetCampoTableRow(i2, 1, TextView_XAss.getText().toString());
                                                    SetCampoTableRow(i2, 2, TextView_YAss.getText().toString());
                                                    points_list.set(i2 - 1, TextView_XAss.getText().toString() + "," + TextView_YAss.getText().toString());
                                                    on_click_draw();
                                                }
                                            });
                                    alertDialog.setNegativeButton("No",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    alertDialog.show();
                                }

                            }
                            break;
                        } else {
                            Toast.makeText(this, "Cannot edit this point", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                row.setBackgroundColor(Color.TRANSPARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        on_click_draw();
    }

    /**
     * Function for save a pts file
     *
     * @param v
     */
    public void on_click_save(View v) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(getResources().getString(R.string.Save));

        // Setting Dialog Message
        alertDialog.setMessage(getResources().getString(R.string.FileName_));
        final EditText input = new EditText(this);
        input.setFocusable(false);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(23)});
        input.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog_lettere.Lancia_KeyDialogo_lettere(TascaFrecceActivity.this, input, "");
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
        alertDialog.setPositiveButton(getResources().getString(R.string.Save),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!input.getText().toString().equals("")) {
                            str.clear();
                            dialog.cancel();
                            final File root = new File(Environment.getExternalStorageDirectory(), "JamData/punti");
                            final String name = input.getText().toString();
                            Values.File = name + ".pts";
                            File file = new File(root, name + ".pts");
                            if (file.exists() || file.exists()) {
                                final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(TascaFrecceActivity.this);

                                // Setting Dialog Title
                                alertDialog1.setTitle("overWrite");

                                // Setting Dialog Message
                                alertDialog1.setMessage("overWrite?");

                                // Setting Positive "Yes" Button
                                alertDialog1.setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                EditedPTS = false;
                                                try {
                                                    if (points_list.size() == 9) {
                                                        if (CheckDifferencePoint(points_list.get(1), points_list.get(2)) && CheckDifferencePoint(points_list.get(7), points_list.get(8)) && CheckDifferencePoint(points_list.get(3), points_list.get(4)) && CheckDifferencePoint(points_list.get(5), points_list.get(6))) {
                                                            str.add("1");
                                                        } else {
                                                            str.add("0");
                                                        }
                                                    } else if (points_list.size() == 4) {
                                                        str.add("3");
                                                    } else if (points_list.size() == 8) {
                                                        str.add("4");
                                                    } else if (points_list.size() == 6) {
                                                        str.add("2");
                                                    } else if (points_list.size() == 13) {
                                                        str.add("5");
                                                    }
                                                    Values.File = name + ".pts";
                                                    if (points_list.size() == 6) {
                                                        str.add("" + (points_list.size() + 2));
                                                    } else {
                                                        str.add("" + points_list.size());
                                                    }
                                                    for (int i = 0; i < points_list.size(); i++) {
                                                        if ((i == 2 && points_list.size() == 6) || (i == 4 && points_list.size() == 6)) {
                                                            str.add(points_list.get(i));
                                                        }
                                                        str.add(points_list.get(i));
                                                    }
                                                    try {
                                                        if (!root.exists()) {
                                                            root.mkdirs();
                                                        }
                                                        File gpxfile = new File(root, name + ".pts");
                                                        // append text
                                                        BufferedWriter out = new BufferedWriter(new FileWriter(gpxfile.toString(), false));
                                                        for (int i = 0; i < str.size(); i++) {
                                                            out.write(str.get(i) + "\n");
                                                        }
                                                        out.close();
                                                        Button_nextPage.setVisibility(View.VISIBLE);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                        Toast.makeText(TascaFrecceActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                    Toast.makeText(TascaFrecceActivity.this, "Saved", Toast.LENGTH_LONG).show();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(TascaFrecceActivity.this, "File not Saved", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                alertDialog1.setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                alertDialog1.show();

                            } else {
                                str.clear();
                                EditedPTS = false;
                                try {
                                    if (points_list.size() == 9) {
                                        if (CheckDifferencePoint(points_list.get(1), points_list.get(2)) && CheckDifferencePoint(points_list.get(7), points_list.get(8)) && CheckDifferencePoint(points_list.get(3), points_list.get(4)) && CheckDifferencePoint(points_list.get(5), points_list.get(6))) {
                                            str.add("1");
                                        } else {
                                            str.add("0");
                                        }
                                    } else if (points_list.size() == 4) {
                                        str.add("3");
                                    } else if (points_list.size() == 8) {
                                        str.add("4");
                                    } else if (points_list.size() == 6) {
                                        str.add("2");
                                    } else if (points_list.size() == 13) {
                                        str.add("5");
                                    }
                                    Values.File = name + ".pts";
                                    if (points_list.size() == 6) {
                                        str.add("" + (points_list.size() + 2));
                                    } else {
                                        str.add("" + points_list.size());
                                    }
                                    for (int i = 0; i < points_list.size(); i++) {
                                        if ((i == 2 && points_list.size() == 6) || (i == 4 && points_list.size() == 6)) {
                                            str.add(points_list.get(i));
                                        }
                                        str.add(points_list.get(i));
                                    }
                                    try {
                                        if (!root.exists()) {
                                            root.mkdirs();
                                        }
                                        File gpxfile = new File(root, name + ".pts");
                                        BufferedWriter out = new BufferedWriter(new FileWriter(gpxfile.toString(), false));
                                        for (int i = 0; i < str.size(); i++) {
                                            out.write(str.get(i) + "\n");
                                        }
                                        out.close();
                                        Button_nextPage.setVisibility(View.VISIBLE);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Toast.makeText(TascaFrecceActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(TascaFrecceActivity.this, "Saved", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(TascaFrecceActivity.this, "File not Saved", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Toast.makeText(TascaFrecceActivity.this, "Wrong File Name", Toast.LENGTH_LONG).show();
                        }
                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton(getResources().getString(R.string.Cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    /**
     * Function for load a pts file
     *
     * @param v
     */
    public void on_click_load(View v) {
        Intent intent_par = new Intent(getApplicationContext(), PopUpSelectFile.class);
        startActivityForResult(intent_par, 1);
        EditedPTS = false;
    }

    /**
     * Button for go to the next step of pocket creation
     *
     * @param v
     */
    public void on_click_next(View v) {
        if (!str.isEmpty()) {
            if (EditedPTS) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setMessage("File edited save it?");
                alertDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(TascaFrecceActivity.this);

                                // Setting Dialog Title
                                alertDialog.setTitle(getResources().getString(R.string.Save));

                                // Setting Dialog Message
                                alertDialog.setMessage("File Name:");
                                final EditText input = new EditText(TascaFrecceActivity.this);
                                input.setFocusable(false);
                                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(23)});
                                input.setOnTouchListener(new View.OnTouchListener() {

                                    //	@SuppressLint("ClickableViewAccessibility")
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                            KeyDialog_lettere.Lancia_KeyDialogo_lettere(TascaFrecceActivity.this, input, "");
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
                                alertDialog.setPositiveButton(getResources().getString(R.string.Save),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                str.clear();
                                                dialog.cancel();
                                                final File root = new File(Environment.getExternalStorageDirectory(), "JamData/punti");

                                                final String name = input.getText().toString();
                                                File file = new File(root, name + ".pts");
                                                if (file.exists()) {
                                                    final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(TascaFrecceActivity.this);

                                                    // Setting Dialog Title
                                                    alertDialog1.setTitle("overWrite");

                                                    // Setting Dialog Message
                                                    alertDialog1.setMessage("overWrite?");

                                                    // Setting Positive "Yes" Button
                                                    alertDialog1.setPositiveButton("Yes",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    try {
                                                                        if (points_list.size() == 9) {
                                                                            if (CheckDifferencePoint(points_list.get(1), points_list.get(2)) && CheckDifferencePoint(points_list.get(7), points_list.get(8)) && CheckDifferencePoint(points_list.get(3), points_list.get(4)) && CheckDifferencePoint(points_list.get(5), points_list.get(6))) {
                                                                                str.add("1");
                                                                            } else {
                                                                                str.add("0");
                                                                            }
                                                                        } else if (points_list.size() == 4) {
                                                                            str.add("3");
                                                                        } else if (points_list.size() == 8) {
                                                                            str.add("4");
                                                                        } else if (points_list.size() == 6) {
                                                                            str.add("2");
                                                                        } else if (points_list.size() == 13) {
                                                                            str.add("5");
                                                                        }
                                                                        Values.File = name + ".pts";
                                                                        str.add("" + points_list.size());
                                                                        for (int i = 0; i < points_list.size(); i++) {
                                                                            str.add(points_list.get(i));
                                                                        }
                                                                        try {
                                                                            if (!root.exists()) {
                                                                                root.mkdirs();
                                                                            }
                                                                            File gpxfile = new File(root, name + ".pts");
                                                                            // append text
                                                                            BufferedWriter out = new BufferedWriter(new FileWriter(gpxfile.toString(), false));
                                                                            for (int i = 0; i < str.size(); i++) {
                                                                                out.write(str.get(i) + "\n");
                                                                            }
                                                                            out.close();
                                                                            Button_nextPage.setVisibility(View.VISIBLE);
                                                                        } catch (IOException e) {
                                                                            e.printStackTrace();
                                                                            Toast.makeText(TascaFrecceActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                        Toast.makeText(TascaFrecceActivity.this, "Saved", Toast.LENGTH_LONG).show();

                                                                        CalcolaMisure();
                                                                    } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                        Toast.makeText(TascaFrecceActivity.this, "File not Saved", Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });

                                                    alertDialog1.setNegativeButton("No",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.cancel();
                                                                }
                                                            });
                                                    alertDialog1.show();

                                                } else {
                                                    try {
                                                        if (points_list.size() == 9) {
                                                            if (CheckDifferencePoint(points_list.get(1), points_list.get(2)) && CheckDifferencePoint(points_list.get(7), points_list.get(8)) && CheckDifferencePoint(points_list.get(3), points_list.get(4)) && CheckDifferencePoint(points_list.get(5), points_list.get(6))) {
                                                                str.add("1");
                                                            } else {
                                                                str.add("0");
                                                            }
                                                        } else if (points_list.size() == 4) {
                                                            str.add("3");
                                                        } else if (points_list.size() == 8) {
                                                            str.add("4");
                                                        } else if (points_list.size() == 6) {
                                                            str.add("2");
                                                        } else if (points_list.size() == 13) {
                                                            str.add("5");
                                                        }
                                                        Values.File = name + ".pts";
                                                        str.add("" + points_list.size());
                                                        for (int i = 0; i < points_list.size(); i++) {
                                                            str.add(points_list.get(i));
                                                        }
                                                        try {
                                                            if (!root.exists()) {
                                                                root.mkdirs();
                                                            }
                                                            File gpxfile = new File(root, name + ".pts");
                                                            // append text
                                                            BufferedWriter out = new BufferedWriter(new FileWriter(gpxfile.toString(), false));
                                                            for (int i = 0; i < str.size(); i++) {
                                                                out.write(str.get(i) + "\n");
                                                            }
                                                            out.close();
                                                            Button_nextPage.setVisibility(View.VISIBLE);
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                            Toast.makeText(TascaFrecceActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                        Toast.makeText(TascaFrecceActivity.this, "Saved", Toast.LENGTH_LONG).show();

                                                        CalcolaMisure();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Toast.makeText(TascaFrecceActivity.this, "File not Saved", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }
                                        });
                                // Setting Negative "NO" Button
                                alertDialog.setNegativeButton(getResources().getString(R.string.Cancel),
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });

                                // Showing Alert Message
                                alertDialog.show();
                            }
                        });
                alertDialog.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                CalcolaMisure();
                            }
                        });
                alertDialog.show();
            } else { //Se il file non è stato modificato
                CalcolaMisure();
            }
        }
    }

    /**
     * Button for show the next pocket type
     *
     * @param v
     */
    public void BtnNextModel(View v) {
        ViewFlipper vf1 = findViewById(R.id.ViewFlipper1);
        vf1.showNext();
        ImageCheck();
    }

    /**
     * Button for show the previous pocket type
     *
     * @param v
     */
    public void BtnBackModel(View v) {
        ViewFlipper vf1 = findViewById(R.id.ViewFlipper1);
        vf1.showPrevious();
        ImageCheck();
    }

    /**
     * Button for change the punto carico value
     *
     * @param v
     */
    public void BtnPuntoCarico(View v) {
        KeyDialog.Lancia_KeyDialogo(null, TascaFrecceActivity.this, null, 100, 0, true, false, 0d, false, "KeyDialog_ret");
    }

    @Override
    protected void onResume() {
        if (sl != null) {
            sl.Clear();
            if (!Thread_Running && !Debug_mode) {
                StopThread = false;
                MyAndroidThread_Modifica myTask = new MyAndroidThread_Modifica(TascaFrecceActivity.this);
                t1 = new Thread(myTask, "Main myTask");
                t1.start();
            }
        } else {
            sl = SocketHandler.getSocket();

            sl.Clear();
            StopThread = false;

            MultiCmd_Vb556_PCConnesso = sl.Add("Io", 1, MultiCmdItem.dtVB, 556, MultiCmdItem.dpNONE);
            MultiCmd_Vb253_Modalita_Step = sl.Add("Io", 1, MultiCmdItem.dtVB, 253, MultiCmdItem.dpNONE);
            MultiCmd_VQ110 = sl.Add("Io", 1, MultiCmdItem.dtVQ, 110, MultiCmdItem.dpNONE);
            MultiCmd_Vb72_HmiMoveXY = sl.Add("Io", 1, MultiCmdItem.dtVB, 72, MultiCmdItem.dpNONE);

            MultiCmd_jogXPiuYPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 54, MultiCmdItem.dpNONE);
            MultiCmd_jogXPiuYMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 55, MultiCmdItem.dpNONE);
            MultiCmd_jogXMenoYPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 56, MultiCmdItem.dpNONE);
            MultiCmd_jogXMenoYMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 57, MultiCmdItem.dpNONE);
            MultiCmd_JogXMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 62, MultiCmdItem.dpNONE);
            MultiCmd_JogXPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 63, MultiCmdItem.dpNONE);
            MultiCmd_JogYMeno = sl.Add("Io", 1, MultiCmdItem.dtVB, 64, MultiCmdItem.dpNONE);
            MultiCmd_JogYPiu = sl.Add("Io", 1, MultiCmdItem.dtVB, 65, MultiCmdItem.dpNONE);

            MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 21, MultiCmdItem.dpNONE);
            MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);
            MultiCmd_Vb88_Sgancio_ago = sl.Add("Io", 1, MultiCmdItem.dtVB, 1018, MultiCmdItem.dpNONE);
            MultiCmd_VQ111_PosX = sl.Add("Io", 1, MultiCmdItem.dtVQ, 51, MultiCmdItem.dpNONE);
            MultiCmd_VQ112_PosY = sl.Add("Io", 1, MultiCmdItem.dtVQ, 52, MultiCmdItem.dpNONE);
            MultiCmd_Vb557_PCPiedino = sl.Add("Io", 1, MultiCmdItem.dtVB, 65, MultiCmdItem.dpNONE);
            MultiCmd_VQ7002_QuotaDestinazioneX = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7002, MultiCmdItem.dpNONE);
            MultiCmd_VQ7022_QuotaDestinazioneY = sl.Add("Io", 1, MultiCmdItem.dtVQ, 7022, MultiCmdItem.dpNONE);
            MultiCmd_Vb52_PCHome = sl.Add("Io", 1, MultiCmdItem.dtVB, 52, MultiCmdItem.dpNONE);
            Multicmd_Vb4807_PinzeAlteDopoPC = sl.Add("Io", 1, MultiCmdItem.dtVB, 4807, MultiCmdItem.dpNONE);
            MultiCmd_Vn3804_pagina_touch = sl.Add("Io", 1, MultiCmdItem.dtVN, 3804, MultiCmdItem.dpNONE);



            mci_array_read_all = new MultiCmdItem[]{MultiCmd_VQ110, MultiCmd_VQ111_PosX, MultiCmd_VQ112_PosY, MultiCmd_tasto_verde,
                    MultiCmd_CH1_in_emergenza};

            Mci_write_Vb556_PCConnesso.mci = MultiCmd_Vb556_PCConnesso;
            Mci_write_Vb556_PCConnesso.write_flag = false;

            Mci_write_Vb557_PCPiedino.mci = MultiCmd_Vb557_PCPiedino;
            Mci_write_Vb557_PCPiedino.write_flag = false;

            Mci_write_Vb52_PCHome.mci = MultiCmd_Vb52_PCHome;
            Mci_write_Vb52_PCHome.write_flag = false;

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


            Mci_write_VQ7002_QuotaDestinazioneX.mci = MultiCmd_VQ7002_QuotaDestinazioneX;
            Mci_write_VQ7002_QuotaDestinazioneX.write_flag = false;

            Mci_write_VQ7022_QuotaDestinazioneY.mci = MultiCmd_VQ7022_QuotaDestinazioneY;
            Mci_write_VQ7022_QuotaDestinazioneY.write_flag = false;

            Mci_write_MultiCmd_Vb72_HmiMoveXY.mci = MultiCmd_Vb72_HmiMoveXY;
            Mci_write_MultiCmd_Vb72_HmiMoveXY.write_flag = false;





            Mci_write_Vb253_Modalita_Step.mci = MultiCmd_Vb253_Modalita_Step;
            Mci_write_Vb253_Modalita_Step.write_flag = false;

            MciWrite_mci_vb88_sblocca_ago.mci = MultiCmd_Vb88_Sgancio_ago;
            MciWrite_mci_vb88_sblocca_ago.write_flag = false;
            MciWrite_mci_vb88_sblocca_ago.valore_precedente = 0.0d;
            MciWrite_mci_vb88_sblocca_ago.valore = 0.0d;

            Mci_write_Vb556_PCConnesso.valore = 1.0d;
            Mci_write_Vb556_PCConnesso.write_flag = true;

            Mci_write_Vb253_Modalita_Step.valore = 0.0d;
            Mci_write_Vb253_Modalita_Step.write_flag = true;

            Mci_write_Vb52_PCHome.valore = 1.0d;
            Mci_write_Vb52_PCHome.write_flag = true;






            EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogYPiu, Button_arrow_up, "ic_up_press", "ic_up", getApplicationContext(), 100);
            EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogYMeno, Button_freccia_giu, "ic_down_press", "ic_down", getApplicationContext(), 100);
            EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogXPiu, Button_arrow_right, "ic_right_press", "ic_right", getApplicationContext(), 100);
            EdgeButton.CreaEdgeButton_Frecce(Mci_write_JogXMeno, Button_arrow_left, "ic_left_press", "ic_left", getApplicationContext(), 100);
            EdgeButton.CreaEdgeButton_Frecce(Mci_write_jogXMenoYMeno, Button_arrow_up_right, "freccia_su_dx_b", "freccia_su_dx_a", getApplicationContext(), 100);
            EdgeButton.CreaEdgeButton_Frecce(Mci_write_jogXMenoYPiu, Button_arrow_down_right, "freccia_giu_dx_b", "freccia_giu_dx_a", getApplicationContext(), 100);
            EdgeButton.CreaEdgeButton_Frecce(Mci_write_jogXPiuYPiu, Button_arrow_down_left, "freccia_giu_sx_b", "freccia_giu_sx_a", getApplicationContext(), 100);
            EdgeButton.CreaEdgeButton_Frecce(Mci_write_jogXPiuYMeno, Button_arrow_up_left, "freccia_su_sx_b", "freccia_su_sx_a", getApplicationContext(), 100);

            Toggle_Button.CreaToggleButton(MciWrite_mci_vb88_sblocca_ago, Button_Sgancio_ago, "ic_sblocca_ago_press", "ic_sblocca_ago", getApplicationContext(), sl);

            TextView_Info.setText(getString(R.string.MuoviP1));

            timeout_counter = Timeout_emergenza;

            // Start the thread
            if (!Thread_Running && !Debug_mode) {
                StopThread = false;
                MyAndroidThread_Modifica myTask = new MyAndroidThread_Modifica(TascaFrecceActivity.this);
                t1 = new Thread(myTask, "Main myTask");
                t1.start();
            }
        }

        // Register the receiver
        LocalBroadcastManager.getInstance(TascaFrecceActivity.this).registerReceiver(mMessageReceiver, new IntentFilter("KeyDialog_ret"));
        //registerReceiver(mMessageReceiver, new IntentFilter("KeyDialog_ret"));
        super.onResume();
    }

    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
    }

    @Override                   //your activity is no longer visible to the user
    public void onStop() {
        super.onStop();
        Log.d("JAM TAG", "TascaFrecce onStop");
        try {
            unregisterReceiver(usbReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        KillThread();
    }

    @Override                   //your activity is no longer visible to the user
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Function for init the table row that will contain the points of the pocket
     */
    private void init_TableRow() {
        TableLayout_punti = findViewById(R.id.tableLayout_punti);
        for (int i = 0; i < 13; i++) {
            final TableRow TableRow_punti = new TableRow(this);

            TextView txtP = new TextView(this);
            txtP.setTextColor(Color.RED);
            txtP.setWidth(30);
            txtP.setTextSize(18);
            txtP.setText("P" + (i + 1) + ":  ");
            TableRow_punti.addView(txtP);

            TableRow_punti.setClickable(true);

            TextView txtX = new TextView(this);
            txtX.setWidth(60);
            txtX.setTextSize(18);
            txtX.setText(" ");
            TableRow_punti.addView(txtX);

            TextView txtY = new TextView(this);
            txtY.setWidth(60);
            txtY.setTextSize(18);
            txtY.setText(" ");
            TableRow_punti.addView(txtY);

            final int finalI = i;
            TableRow_punti.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    for (int i = 0; i < 13; i++) {
                        try {
                            TextView_Info.setText(getString(R.string.ConfirmEditPoint));
                            View row = TableLayout_punti.getChildAt(i);
                            row.setBackgroundColor(Color.TRANSPARENT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    v.setBackgroundColor(Color.GRAY);
                    SpostoPinzeAlPuntoPrecedente(finalI + 2);
                }
            });
            TableLayout_punti.addView(TableRow_punti);
        }
    }

    /**
     * Function for set the X,Y coordinate of a point
     *
     * @param numeroPunto
     * @param QuotaX
     * @param QuotaY
     */
    private void ScriviCoordinateSuTable(int numeroPunto, String QuotaX, String QuotaY) {
        SetCampoTableRow(numeroPunto, 1, QuotaX);
        SetCampoTableRow(numeroPunto, 2, QuotaY);
    }

    /**
     * Function for move the needle to the previous point when a the point where it was don't exist anymore
     *
     * @param numero_punto_cancellato
     */
    private void SpostoPinzeAlPuntoPrecedente(int numero_punto_cancellato) {
        if (numero_punto_cancellato >= 2) {
            String X_srt = GetCampoTableRow(numero_punto_cancellato - 1, 2);
            String Y_str = GetCampoTableRow(numero_punto_cancellato - 1, 3);
            try {
                double X = Double.valueOf(X_srt);
                double Y = Double.valueOf(Y_str);
                Mci_write_VQ7002_QuotaDestinazioneX.valore = X*1000;
                Mci_write_VQ7002_QuotaDestinazioneX.write_flag = true;

                Mci_write_VQ7022_QuotaDestinazioneY.valore = Y*1000;
                Mci_write_VQ7022_QuotaDestinazioneY.write_flag = true;

                Mci_write_MultiCmd_Vb72_HmiMoveXY.valore = 1.0d;
                Mci_write_MultiCmd_Vb72_HmiMoveXY.write_flag = true;


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Function for check which image is displayed and edit the list of points
     */
    public void ImageCheck() {
        ViewFlipper vf1 = findViewById(R.id.ViewFlipper1);
        TableLayout_punti = findViewById(R.id.tableLayout_punti);
        TableLayout_punti.removeAllViews();

        int textWidth = 40;
        if (vf1.getDisplayedChild() == 0) {
            for (int i = 0; i < 9; i++) {
                final TableRow TableRow_punti = new TableRow(this);

                TextView txtP = new TextView(this);
                txtP.setTextColor(Color.RED);
                txtP.setWidth(textWidth);
                txtP.setTextSize(18);
                txtP.setText("P" + (i + 1) + ":  ");
                TableRow_punti.addView(txtP);

                TableRow_punti.setClickable(true);

                TextView txtX = new TextView(this);
                txtX.setWidth(60);
                txtX.setTextSize(18);
                txtX.setText(" ");
                TableRow_punti.addView(txtX);

                TextView txtY = new TextView(this);
                txtY.setWidth(60);
                txtY.setTextSize(18);
                txtY.setText(" ");
                TableRow_punti.addView(txtY);

                final int finalI = i;
                TableRow_punti.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        for (int i = 0; i < 9; i++) {
                            try {
                                TextView_Info.setText(getString(R.string.ConfirmEditPoint));
                                View row = TableLayout_punti.getChildAt(i);
                                row.setBackgroundColor(Color.TRANSPARENT);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        v.setBackgroundColor(Color.GRAY);
                        SpostoPinzeAlPuntoPrecedente(finalI + 2);
                    }
                });


                TableLayout_punti.addView(TableRow_punti);
            }
        } else if (vf1.getDisplayedChild() == 1) {
            for (int i = 0; i < 4; i++) {
                final TableRow TableRow_punti = new TableRow(this);

                TextView txtP = new TextView(this);
                txtP.setTextColor(Color.RED);
                txtP.setWidth(textWidth);
                txtP.setTextSize(18);
                txtP.setText("P" + (i + 1) + ":  ");
                TableRow_punti.addView(txtP);

                TableRow_punti.setClickable(true);

                TextView txtX = new TextView(this);
                txtX.setWidth(60);
                txtX.setTextSize(18);
                txtX.setText(" ");
                TableRow_punti.addView(txtX);

                TextView txtY = new TextView(this);
                txtY.setWidth(60);
                txtY.setTextSize(18);
                txtY.setText(" ");
                TableRow_punti.addView(txtY);

                final int finalI = i;
                TableRow_punti.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        for (int i = 0; i < 4; i++) {
                            try {
                                TextView_Info.setText(getString(R.string.ConfirmEditPoint));
                                View row = TableLayout_punti.getChildAt(i);
                                row.setBackgroundColor(Color.TRANSPARENT);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        v.setBackgroundColor(Color.GRAY);
                        SpostoPinzeAlPuntoPrecedente(finalI + 2);
                    }
                });


                TableLayout_punti.addView(TableRow_punti);
            }
        } else if (vf1.getDisplayedChild() == 2) {
            for (int i = 0; i < 8; i++) {
                final TableRow TableRow_punti = new TableRow(this);

                TextView txtP = new TextView(this);
                txtP.setTextColor(Color.RED);
                txtP.setWidth(textWidth);
                txtP.setTextSize(18);
                txtP.setText("P" + (i + 1) + ":  ");
                TableRow_punti.addView(txtP);

                TableRow_punti.setClickable(true);

                TextView txtX = new TextView(this);
                txtX.setWidth(60);
                txtX.setTextSize(18);
                txtX.setText(" ");
                TableRow_punti.addView(txtX);

                TextView txtY = new TextView(this);
                txtY.setWidth(60);
                txtY.setTextSize(18);
                txtY.setText(" ");
                TableRow_punti.addView(txtY);

                final int finalI = i;
                TableRow_punti.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        for (int i = 0; i < 5; i++) {
                            try {
                                TextView_Info.setText(getString(R.string.ConfirmEditPoint));
                                View row = TableLayout_punti.getChildAt(i);
                                row.setBackgroundColor(Color.TRANSPARENT);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        v.setBackgroundColor(Color.GRAY);
                        SpostoPinzeAlPuntoPrecedente(finalI + 2);
                    }
                });


                TableLayout_punti.addView(TableRow_punti);
            }
        } else if (vf1.getDisplayedChild() == 3) {
            for (int i = 0; i < 6; i++) {
                final TableRow TableRow_punti = new TableRow(this);

                TextView txtP = new TextView(this);
                txtP.setTextColor(Color.RED);
                txtP.setWidth(textWidth);
                txtP.setTextSize(18);
                txtP.setText("P" + (i + 1) + ":  ");
                TableRow_punti.addView(txtP);

                TableRow_punti.setClickable(true);

                TextView txtX = new TextView(this);
                txtX.setWidth(60);
                txtX.setTextSize(18);
                txtX.setText(" ");
                TableRow_punti.addView(txtX);

                TextView txtY = new TextView(this);
                txtY.setWidth(60);
                txtY.setTextSize(18);
                txtY.setText(" ");
                TableRow_punti.addView(txtY);

                final int finalI = i;
                TableRow_punti.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        for (int i = 0; i < 5; i++) {
                            try {
                                TextView_Info.setText(getString(R.string.ConfirmEditPoint));
                                View row = TableLayout_punti.getChildAt(i);
                                row.setBackgroundColor(Color.TRANSPARENT);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        v.setBackgroundColor(Color.GRAY);
                        SpostoPinzeAlPuntoPrecedente(finalI + 2);
                    }
                });

                TableLayout_punti.addView(TableRow_punti);
            }
        } else if (vf1.getDisplayedChild() == 4) {
            for (int i = 0; i < 13; i++) {
                final TableRow TableRow_punti = new TableRow(this);

                TextView txtP = new TextView(this);
                txtP.setTextColor(Color.RED);
                txtP.setWidth(textWidth);
                txtP.setTextSize(18);
                txtP.setText("P" + (i + 1) + ":  ");
                TableRow_punti.addView(txtP);

                TableRow_punti.setClickable(true);

                TextView txtX = new TextView(this);
                txtX.setWidth(60);
                txtX.setTextSize(18);
                txtX.setText(" ");
                TableRow_punti.addView(txtX);

                TextView txtY = new TextView(this);
                txtY.setWidth(60);
                txtY.setTextSize(18);
                txtY.setText(" ");
                TableRow_punti.addView(txtY);

                final int finalI = i;
                TableRow_punti.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        for (int i = 0; i < 13; i++) {
                            try {
                                TextView_Info.setText(getString(R.string.ConfirmEditPoint));
                                View row = TableLayout_punti.getChildAt(i);
                                row.setBackgroundColor(Color.TRANSPARENT);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        v.setBackgroundColor(Color.GRAY);
                        SpostoPinzeAlPuntoPrecedente(finalI + 2);
                    }
                });


                TableLayout_punti.addView(TableRow_punti);
            }
        }
    }

    /**
     * Function for transform the pts points to values
     */
    public void CalcolaMisure() {
        MultiCmd_Vb556_PCConnesso.setValue(0.0d);
        sl.WriteItem(Mci_write_Vb556_PCConnesso.mci);

        Mci_write_Vb52_PCHome.valore = 1.0d;        //chiamo azzeramento degli assi
        Mci_write_Vb52_PCHome.write_flag = true;


        if (str.get(0).equals("0")) {
            Values.model = 0;
        } else if (str.get(0).equals("1")) {
            Values.model = 1;
        } else if (str.get(0).equals("3")) {
            Values.model = 3;
        } else if (str.get(0).equals("2")) {
            Values.model = 2;
        } else if (str.get(0).equals("4")) {
            Values.model = 4;
        } else if (str.get(0).equals("5")) {
            Values.model = 5;
        }

        if (Values.model == 0 || Values.model == 1) {
            String[] v1;
            String[] v2;
            String[] v3;

            v1 = str.get(2).split(",");
            v2 = str.get(10).split(",");
            Values.L = Float.parseFloat(v2[0]) - Float.parseFloat(v1[0]);

            v1 = str.get(2).split(",");
            v2 = str.get(6).split(",");
            Values.Hf = Float.parseFloat(v2[1]) - Float.parseFloat(v1[1]);

            v1 = str.get(2).split(",");
            v2 = str.get(6).split(",");
            v3 = str.get(9).split(",");
            float metaL = (Float.parseFloat(v3[0]) - Float.parseFloat(v1[0])) / 2;
            float metaL_RispetoAZero = metaL + Float.parseFloat(v1[0]);
            Values.P = metaL_RispetoAZero - Float.parseFloat(v2[0]);

            v1 = str.get(8).split(",");
            v2 = str.get(2).split(",");
            Values.S2 = Float.parseFloat(v1[0]) - (Float.parseFloat(v2[0]) + Values.L / 2);

            v1 = str.get(4).split(",");
            v2 = str.get(2).split(",");
            Values.S1 = (Float.parseFloat(v2[0]) + Values.L / 2) - Float.parseFloat(v1[0]);

            v1 = str.get(8).split(",");
            v2 = str.get(10).split(",");
            Values.H2 = Float.parseFloat(v1[1]) - Float.parseFloat(v2[1]);

            v1 = str.get(2).split(",");
            v2 = str.get(4).split(",");
            Values.H1 = Float.parseFloat(v2[1]) - Float.parseFloat(v1[1]);

            v1 = str.get(8).split(",");
            v2 = str.get(10).split(",");
            v3 = str.get(9).split(",");

            if (CheckDifferenceValue(v2[0], v3[0]) && CheckDifferenceValue(v2[1], v3[1])) {
                Values.M2 = 0F;
            } else {
                float pmx = (Float.parseFloat(v1[0]) + (Float.parseFloat(v2[0]))) / 2;
                float pmy = (Float.parseFloat(v1[1]) + (Float.parseFloat(v2[1]))) / 2;

                Values.M2 = (float) Math.sqrt(Math.pow(pmy - Float.parseFloat(v3[1]), 2) + Math.pow(pmx - Float.parseFloat(v3[0]), 2));
            }

            v1 = str.get(2).split(",");
            v2 = str.get(4).split(",");
            v3 = str.get(3).split(",");

            if (CheckDifferenceValue(v2[0], v3[0]) && CheckDifferenceValue(v2[1], v3[1])) {
                Values.M1 = 0F;
            } else {
                float pmx = (Float.parseFloat(v1[0]) + (Float.parseFloat(v2[0]))) / 2;
                float pmy = (Float.parseFloat(v1[1]) + (Float.parseFloat(v2[1]))) / 2;

                Values.M1 = (float) Math.sqrt(Math.pow(pmy - Float.parseFloat(v3[1]), 2) + Math.pow(pmx - Float.parseFloat(v3[0]), 2));
            }

            v1 = str.get(4).split(",");
            v2 = str.get(6).split(",");
            v3 = str.get(5).split(",");

            if (CheckDifferenceValue(v2[0], v3[0]) && CheckDifferenceValue(v2[1], v3[1])) {
                Values.M4 = 0F;
            } else {
                float pmx = (Float.parseFloat(v1[0]) + (Float.parseFloat(v2[0]))) / 2;
                float pmy = (Float.parseFloat(v1[1]) + (Float.parseFloat(v2[1]))) / 2;

                Values.M4 = (float) Math.sqrt(Math.pow(pmy - Float.parseFloat(v3[1]), 2) + Math.pow(pmx - Float.parseFloat(v3[0]), 2));
            }
            v1 = str.get(6).split(",");
            v2 = str.get(8).split(",");
            v3 = str.get(7).split(",");

            if (CheckDifferenceValue(v2[0], v3[0]) && CheckDifferenceValue(v2[1], v3[1])) {
                Values.M3 = 0F;
            } else {
                float pmx = (Float.parseFloat(v1[0]) + (Float.parseFloat(v2[0]))) / 2;
                float pmy = (Float.parseFloat(v1[1]) + (Float.parseFloat(v2[1]))) / 2;

                Values.M3 = (float) Math.sqrt(Math.pow(pmy - Float.parseFloat(v3[1]), 2) + Math.pow(pmx - Float.parseFloat(v3[0]), 2));
            }

            if (Values.M1 == 0 && Values.M2 == 0 && Values.M3 == 0 && Values.M4 == 0) {
                Intent intent_par = new Intent(getApplicationContext(), SelectModelActivity.class);
                startActivity(intent_par);
            } else {
                Intent page = new Intent(TascaFrecceActivity.this, SelectTypeActivity.class);
                startActivity(page);
            }
        } else if (Values.model == 3) {
            String[] v1;
            String[] v2;
            String[] v3;

            v1 = str.get(2).split(",");
            v2 = str.get(5).split(",");
            Values.L = Float.parseFloat(v2[0]) - Float.parseFloat(v1[0]);

            v1 = str.get(2).split(",");
            v2 = str.get(3).split(",");
            Values.Hf = Float.parseFloat(v2[1]) - Float.parseFloat(v1[1]);


            Intent page = new Intent(TascaFrecceActivity.this, SelectTypeActivity4.class);
            startActivity(page);
        } else if (Values.model == 4) {
            String[] v1;
            String[] v2;
            String[] v3;

            v1 = str.get(2).split(",");
            v2 = str.get(9).split(",");
            Values.L = Float.parseFloat(v2[0]) - Float.parseFloat(v1[0]);

            v1 = str.get(2).split(",");
            v2 = str.get(5).split(",");
            Values.Hf = Float.parseFloat(v2[1]) - Float.parseFloat(v1[1]);

            v1 = str.get(3).split(",");
            v2 = str.get(5).split(",");
            Values.S1 = Float.parseFloat(v2[1]) - (Float.parseFloat(v1[1]));

            v1 = str.get(3).split(",");
            v2 = str.get(5).split(",");
            Values.S2 = (Float.parseFloat(v2[0]) - Float.parseFloat(v1[0]));

            v1 = str.get(3).split(",");
            v2 = str.get(5).split(",");
            v3 = str.get(4).split(",");

            if (CheckDifferenceValue(v2[0], v3[0]) && CheckDifferenceValue(v2[1], v3[1])) {
                Values.M1 = 0F;
            } else {
                CenterPointRadius cpr = MathGeoTri.CalculateArc_CenterRadius(new PointF(Float.parseFloat(v1[0]), Float.parseFloat(v1[1])), new PointF(Float.parseFloat(v2[0]), Float.parseFloat(v2[1])), new PointF(Float.parseFloat(v3[0]), Float.parseFloat(v3[1])));
                Values.M1 = cpr.radius;
            }

            v1 = str.get(6).split(",");
            v2 = str.get(8).split(",");
            v3 = str.get(7).split(",");

            if (CheckDifferenceValue(v2[0], v3[0]) && CheckDifferenceValue(v2[1], v3[1])) {
                Values.M2 = 0F;
            } else {
                CenterPointRadius cpr = MathGeoTri.CalculateArc_CenterRadius(new PointF(Float.parseFloat(v1[0]), Float.parseFloat(v1[1])), new PointF(Float.parseFloat(v2[0]), Float.parseFloat(v2[1])), new PointF(Float.parseFloat(v3[0]), Float.parseFloat(v3[1])));
                Values.M2 = cpr.radius;
            }

            Intent page = new Intent(TascaFrecceActivity.this, SelectTypeActivity5.class);
            startActivity(page);
        } else if (Values.model == 2) {
            String[] v1;
            String[] v2;
            String[] v3;

            Values.M1 = 0;
            Values.M2 = 0;

            v1 = str.get(2).split(",");
            v2 = str.get(9).split(",");
            Values.L = Float.parseFloat(v2[0]) - Float.parseFloat(v1[0]);

            v1 = str.get(2).split(",");
            v2 = str.get(5).split(",");
            Values.Hf = Float.parseFloat(v2[1]) - Float.parseFloat(v1[1]);

            v1 = str.get(3).split(",");
            v2 = str.get(5).split(",");
            Values.S1 = Float.parseFloat(v2[0]) - (Float.parseFloat(v1[0]));

            v1 = str.get(3).split(",");
            v2 = str.get(5).split(",");
            Values.H1 = Float.parseFloat(v2[1]) - (Float.parseFloat(v1[1]));

            v1 = str.get(6).split(",");
            v2 = str.get(8).split(",");
            Values.S2 = Float.parseFloat(v2[0]) - (Float.parseFloat(v1[0]));

            v1 = str.get(6).split(",");
            v2 = str.get(8).split(",");
            Values.H2 = Float.parseFloat(v2[1]) - (Float.parseFloat(v1[1]));

            Intent intent_par = new Intent(getApplicationContext(), SelectTypeActivity3.class);
            startActivity(intent_par);
        } else if (Values.model == 5) {
            String[] v1;
            String[] v2;
            String[] v3;

            v1 = str.get(2).split(",");
            v2 = str.get(14).split(",");
            Values.L = Float.parseFloat(v2[0]) - Float.parseFloat(v1[0]);

            v1 = str.get(2).split(",");
            v2 = str.get(8).split(",");
            Values.Hf = Float.parseFloat(v2[1]) - Float.parseFloat(v1[1]);

            float centerXPocket = Float.parseFloat(str.get(2).split(",")[0]) + Values.L / 2;

            v1 = str.get(8).split(",");
            Values.P = centerXPocket - Float.parseFloat(v1[0]);

            v1 = str.get(12).split(",");
            Values.S2 = Float.parseFloat(v1[0]) - centerXPocket;

            v1 = str.get(4).split(",");
            Values.S1 = centerXPocket - Float.parseFloat(v1[0]);

            v1 = str.get(10).split(",");
            Values.S4 = Float.parseFloat(v1[0]) - centerXPocket;

            v1 = str.get(6).split(",");
            Values.S3 = centerXPocket - Float.parseFloat(v1[0]);

            v1 = str.get(14).split(",");
            v2 = str.get(12).split(",");
            Values.H2 = Float.parseFloat(v1[1]) - Float.parseFloat(v2[1]);

            v1 = str.get(2).split(",");
            v2 = str.get(4).split(",");
            Values.H1 = Float.parseFloat(v2[1]) - Float.parseFloat(v1[1]);

            v1 = str.get(6).split(",");
            v2 = str.get(2).split(",");
            Values.H3 = Float.parseFloat(v1[1]) - Float.parseFloat(v2[1]);

            v1 = str.get(14).split(",");
            v2 = str.get(10).split(",");
            Values.H4 = Float.parseFloat(v2[1]) - Float.parseFloat(v1[1]);

            v1 = str.get(2).split(",");
            v2 = str.get(3).split(",");
            v3 = str.get(4).split(",");

            if (CheckDifferenceValue(v2[0], v3[0]) && CheckDifferenceValue(v2[1], v3[1])) {
                Values.M1 = 0F;
            } else {
                float pmx = (Float.parseFloat(v1[0]) + (Float.parseFloat(v2[0]))) / 2;
                float pmy = (Float.parseFloat(v1[1]) + (Float.parseFloat(v2[1]))) / 2;

                Values.M1 = (float) Math.sqrt(Math.pow(pmy - Float.parseFloat(v3[1]), 2) + Math.pow(pmx - Float.parseFloat(v3[0]), 2));
            }

            v1 = str.get(4).split(",");
            v2 = str.get(5).split(",");
            v3 = str.get(6).split(",");

            if (CheckDifferenceValue(v2[0], v3[0]) && CheckDifferenceValue(v2[1], v3[1])) {
                Values.M3 = 0F;
            } else {
                float pmx = (Float.parseFloat(v1[0]) + (Float.parseFloat(v2[0]))) / 2;
                float pmy = (Float.parseFloat(v1[1]) + (Float.parseFloat(v2[1]))) / 2;

                Values.M3 = (float) Math.sqrt(Math.pow(pmy - Float.parseFloat(v3[1]), 2) + Math.pow(pmx - Float.parseFloat(v3[0]), 2));
            }

            v1 = str.get(6).split(",");
            v2 = str.get(7).split(",");
            v3 = str.get(8).split(",");

            if (CheckDifferenceValue(v2[0], v3[0]) && CheckDifferenceValue(v2[1], v3[1])) {
                Values.M5 = 0F;
            } else {
                float pmx = (Float.parseFloat(v1[0]) + (Float.parseFloat(v2[0]))) / 2;
                float pmy = (Float.parseFloat(v1[1]) + (Float.parseFloat(v2[1]))) / 2;

                Values.M5 = (float) Math.sqrt(Math.pow(pmy - Float.parseFloat(v3[1]), 2) + Math.pow(pmx - Float.parseFloat(v3[0]), 2));
            }

            v1 = str.get(8).split(",");
            v2 = str.get(9).split(",");
            v3 = str.get(10).split(",");

            if (CheckDifferenceValue(v2[0], v3[0]) && CheckDifferenceValue(v2[1], v3[1])) {
                Values.M6 = 0F;
            } else {
                float pmx = (Float.parseFloat(v1[0]) + (Float.parseFloat(v2[0]))) / 2;
                float pmy = (Float.parseFloat(v1[1]) + (Float.parseFloat(v2[1]))) / 2;

                Values.M6 = (float) Math.sqrt(Math.pow(pmy - Float.parseFloat(v3[1]), 2) + Math.pow(pmx - Float.parseFloat(v3[0]), 2));
            }

            v1 = str.get(10).split(",");
            v2 = str.get(11).split(",");
            v3 = str.get(12).split(",");

            if (CheckDifferenceValue(v2[0], v3[0]) && CheckDifferenceValue(v2[1], v3[1])) {
                Values.M4 = 0F;
            } else {
                float pmx = (Float.parseFloat(v1[0]) + (Float.parseFloat(v2[0]))) / 2;
                float pmy = (Float.parseFloat(v1[1]) + (Float.parseFloat(v2[1]))) / 2;

                Values.M4 = (float) Math.sqrt(Math.pow(pmy - Float.parseFloat(v3[1]), 2) + Math.pow(pmx - Float.parseFloat(v3[0]), 2));
            }

            v1 = str.get(12).split(",");
            v2 = str.get(13).split(",");
            v3 = str.get(14).split(",");

            if (CheckDifferenceValue(v2[0], v3[0]) && CheckDifferenceValue(v2[1], v3[1])) {
                Values.M2 = 0F;
            } else {
                float pmx = (Float.parseFloat(v1[0]) + (Float.parseFloat(v2[0]))) / 2;
                float pmy = (Float.parseFloat(v1[1]) + (Float.parseFloat(v2[1]))) / 2;

                Values.M2 = (float) Math.sqrt(Math.pow(pmy - Float.parseFloat(v3[1]), 2) + Math.pow(pmx - Float.parseFloat(v3[0]), 2));
            }

            Intent page = new Intent(TascaFrecceActivity.this, SelectTypeActivity6.class);
            startActivity(page);
        }
    }

    /**
     * Function for draw a line on draw
     *
     * @param p1
     * @param p2
     * @param p3
     */
    public void Line(String p1, String p2, String p3) {
        try {
            String[] p2_str = p2.split(",");
            String[] p3_str = p3.split(",");
            PointF Point2 = new PointF(Float.parseFloat(p2_str[0]), Float.parseFloat(p2_str[1]));
            PointF Point3 = new PointF(Float.parseFloat(p3_str[0]), Float.parseFloat(p3_str[1]));

            if (MathGeoTri.Distance(Point2.x, Point2.y, Point3.x, Point3.y) < 0.1) {
                String[] start = p1.split(",");
                String[] end = p2.split(",");
                ViewFlipper vf1 = findViewById(R.id.ViewFlipper1);
                if (points_list.size() >= 3 && vf1.getDisplayedChild() == 0) {
                    r.setDrawPosition(new PointF(Float.parseFloat(start[0]), Float.parseFloat(start[1])));
                }
                r.drawLineTo(new PointF(Float.parseFloat(end[0]), Float.parseFloat(end[1])));
            } else {
                String[] start = p1.split(",");
                String[] middle = p2.split(",");
                String[] end = p3.split(",");

                PointF pStart = new PointF(Float.parseFloat(start[0]), Float.parseFloat(start[1]));
                PointF pMiddle = new PointF(Float.parseFloat(middle[0]), Float.parseFloat(middle[1]));
                PointF pEnd = new PointF(Float.parseFloat(end[0]), Float.parseFloat(end[1]));

                if (points_list.size() >= 3) {
                    r.setDrawPosition(pStart);
                }
                r.drawArcTo(pMiddle, pEnd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Function for set the text of a column in a row
     *
     * @param riga
     * @param colonna
     * @param testo
     */
    private void SetCampoTableRow(int riga, int colonna, String testo) {
        try {
            TableRow row = (TableRow) TableLayout_punti.getChildAt(riga - 1);
            TextView text_view = (TextView) row.getChildAt(colonna);
            text_view.setText(testo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Function for get the text of a column in a row
     *
     * @param riga
     * @param colonna
     * @return
     */
    private String GetCampoTableRow(int riga, int colonna) {
        String ret = null;
        try {
            TableRow row = (TableRow) TableLayout_punti.getChildAt(riga - 1);
            TextView text_view = (TextView) row.getChildAt(colonna - 1);
            ret = text_view.getText().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * Function for handle the emergency page event
     */
    private void Gestione_vb_emergenza() {
        if ((Double) MultiCmd_tasto_verde.getValue() == 0.0d || (Double) MultiCmd_CH1_in_emergenza.getValue() == 1.0d) {
            KillThread();
            Utility.ClearActivitiesTopToEmergencyPage(getApplicationContext());
        }
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
     * Function for arrow button
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
     * Function for load a pts file and display it in the draw
     *
     * @param file_path
     */
    private void Load_pts_file(String file_path) {
        try {
            str.clear();
            points_list.clear();
            punti = 0;
            File root = new File(Environment.getExternalStorageDirectory(), "JamData/punti");
            if (!root.exists()) {
                root.mkdirs();
            }
            File file = new File(root, file_path);

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null)
                str.add(line);

            br.close();

            ViewFlipper vf1 = findViewById(R.id.ViewFlipper1);
            if (str.size() == 11) {
                vf1.setDisplayedChild(0);
                ImageCheck();
                for (int i = 2; i < 11; i++) {
                    if (points_list.size() < 9) {
                        String str_coord = str.get(i);
                        points_list.add(str_coord);
                        punti++;

                        String[] Split_coord = str_coord.split(",");
                        if (Split_coord.length == 2) {
                            ScriviCoordinateSuTable(punti, Split_coord[0], Split_coord[1]);
                        }
                    }
                    if (points_list.size() == 9) {
                        on_click_draw();
                    }
                }
                Button_nextPage.setVisibility(View.VISIBLE);
                Toast.makeText(TascaFrecceActivity.this, "Loaded", Toast.LENGTH_LONG).show();
            } else if (str.size() == 6) {
                vf1.setDisplayedChild(1);
                ImageCheck();
                for (int i = 2; i < 6; i++) {
                    if (points_list.size() < 4) {
                        String str_coord = str.get(i);
                        points_list.add(str_coord);
                        punti++;

                        String[] Split_coord = str_coord.split(",");
                        if (Split_coord.length == 2) {
                            ScriviCoordinateSuTable(punti, Split_coord[0], Split_coord[1]);
                        }
                    }
                    if (points_list.size() == 4) {
                        on_click_draw();
                    }
                }
                Button_nextPage.setVisibility(View.VISIBLE);
                Toast.makeText(TascaFrecceActivity.this, "Loaded", Toast.LENGTH_LONG).show();
            } else if (str.size() == 10 && !str.get(4).equals(str.get(5)) && !str.get(7).equals(str.get(8))) {
                vf1.setDisplayedChild(2);
                ImageCheck();
                for (int i = 2; i < 10; i++) {
                    if (points_list.size() < 8) {
                        String str_coord = str.get(i);
                        points_list.add(str_coord);
                        punti++;

                        String[] Split_coord = str_coord.split(",");
                        if (Split_coord.length == 2) {
                            ScriviCoordinateSuTable(punti, Split_coord[0], Split_coord[1]);
                        }
                    }
                    if (points_list.size() == 8) {
                        on_click_draw();
                    }
                }
                Button_nextPage.setVisibility(View.VISIBLE);
                Toast.makeText(TascaFrecceActivity.this, "Loaded", Toast.LENGTH_LONG).show();
            } else if (str.size() == 10 && str.get(4).equals(str.get(5)) && str.get(7).equals(str.get(8))) {
                vf1.setDisplayedChild(3);
                ImageCheck();
                for (int i = 2; i < 10; i++) {
                    if (i == 5 || i == 8) {
                    } else {
                        if (points_list.size() < 6) {
                            String str_coord = str.get(i);
                            points_list.add(str_coord);
                            punti++;

                            String[] Split_coord = str_coord.split(",");
                            if (Split_coord.length == 2) {
                                ScriviCoordinateSuTable(punti, Split_coord[0], Split_coord[1]);
                            }

                        }
                        if (points_list.size() == 6) {
                            on_click_draw();
                        }
                    }
                }
                Button_nextPage.setVisibility(View.VISIBLE);
                Toast.makeText(TascaFrecceActivity.this, "Loaded", Toast.LENGTH_LONG).show();
            } else if (str.size() == 15) {
                vf1.setDisplayedChild(4);
                ImageCheck();
                for (int i = 2; i < 15; i++) {
                    if (points_list.size() < 13) {
                        String str_coord = str.get(i);
                        points_list.add(str_coord);
                        punti++;

                        String[] Split_coord = str_coord.split(",");
                        if (Split_coord.length == 2) {
                            ScriviCoordinateSuTable(punti, Split_coord[0], Split_coord[1]);
                        }
                    }
                    if (points_list.size() == 13) {
                        on_click_draw();
                    }
                }
                Button_nextPage.setVisibility(View.VISIBLE);
                Toast.makeText(TascaFrecceActivity.this, "Loaded", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(TascaFrecceActivity.this, "pts file error", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function for check the difference between 2 string values
     * <p>
     * TODO i can move this or remove it
     *
     * @param Point1
     * @param Point2
     * @return
     */
    public boolean CheckDifferenceValue(String Point1, String Point2) {
        Float pt1 = Float.parseFloat(Point1);

        Float pt2 = Float.parseFloat(Point2);

        return Math.abs(pt1 - pt2) < 0.1F;
    }

    /**
     * Function for check the distance between 2 string points 'x,y'
     * <p>
     * TODO i can move this or remove it
     *
     * @param Point1
     * @param Point2
     * @return
     */
    public boolean CheckDifferencePoint(String Point1, String Point2) {
        String[] str1 = Point1.split(",");
        PointF pt1 = new PointF(Float.parseFloat(str1[0]), Float.parseFloat(str1[1]));

        String[] str2 = Point2.split(",");
        PointF pt2 = new PointF(Float.parseFloat(str2[0]), Float.parseFloat(str2[1]));

        return Math.abs(pt1.x - pt2.x) < 0.1F && Math.abs(pt1.y - pt2.y) < 0.1F;
    }

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

            case 1:         //ritorno dalla pagina carica file pts
                if (Values.File != null) {
                    Load_pts_file(Values.File);
                }
                if (!Thread_Running && !Debug_mode) {
                    StopThread = false;
                    MyAndroidThread_Modifica myTask = new MyAndroidThread_Modifica(TascaFrecceActivity.this);
                    t1 = new Thread(myTask, "Main myTask");
                    t1.start();
                }
                break;
            case 2:     //ritorno dal DXF IN
                myView_Tasca.AggiornaCanvas(true);
                if (!returnedResult.equals("")) {
                    File file = new File(returnedResult);
                    PopUpSelectFile p = new PopUpSelectFile();
                    p.CheckPts(file);
                    Load_pts_file(file.getName());
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

                // Code below is to handle presses of Volume up or Volume down.
                // Without this, after pressing volume buttons, the navigation bar will
                // show up and won't hide
                final View decorView = getWindow().getDecorView();
                decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

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

    private void  KillThread() {

        LocalBroadcastManager.getInstance(TascaFrecceActivity.this).unregisterReceiver(mMessageReceiver);
        StopThread = true;

        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receiver for handle usb events
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
                                root = currentFs.getRootDirectory();

                                UsbFile[] files = root.listFiles();

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

    /**
     * Thread for communicate with PLC
     */
    class MyAndroidThread_Modifica implements Runnable {
        Activity activity;

        public MyAndroidThread_Modifica(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            Log.d("JAM TAG", "-- TascaFrecce start thread");
            while (true) {
                Thread_Running = true;
                Boolean rc_error;
                try {
                    Thread.sleep((long) 10d);
                    if (StopThread) {
                        MultiCmd_Vn3804_pagina_touch.setValue(0.0d);
                        sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                        Log.d("JAM TAG", "Thread TascaFrecce stopping");
                        Thread_Running = false;
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                }

                if (sl.IsConnected()) {
                    boolean error = false;

                   // if (first_cycle) {
                        MultiCmd_Vn3804_pagina_touch.setValue(1007.0d);
                        sl.WriteItem(MultiCmd_Vn3804_pagina_touch);
                        Multicmd_Vb4807_PinzeAlteDopoPC.setValue(0.0d);
                        sl.WriteItem(Multicmd_Vb4807_PinzeAlteDopoPC);
                    //    first_cycle = false;
                  //  }


                    sl.WriteQueued();
                    sl.ReadItems(mci_array_read_all);
                    rc_error = sl.getReturnCode() != 0;

                    if (rc_error == false) { //se ho avuto un errore di ricezione salto

                        switch (mc_stati_step) {
                            case 0:
                                if (Mci_write_Vb556_PCConnesso.write_flag == true) {
                                    Mci_write_Vb556_PCConnesso.mci.setValue(Mci_write_Vb556_PCConnesso.valore);
                                    sl.WriteItem(Mci_write_Vb556_PCConnesso.mci);
                                    if (sl.getReturnCode() == 0)
                                        Mci_write_Vb556_PCConnesso.write_flag = false;
                                }
                                mc_stati_step = 10;

                                try {
                                    Thread.sleep((long) 100d);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 10:
                                if (Mci_write_Vb52_PCHome.write_flag == true) {
                                    Mci_write_Vb52_PCHome.mci.setValue(Mci_write_Vb52_PCHome.valore);
                                    sl.WriteItem(Mci_write_Vb52_PCHome.mci);
                                    if (sl.getReturnCode() == 0)
                                        Mci_write_Vb52_PCHome.write_flag = false;
                                }
                                mc_stati_step = 20;
                                try {
                                    Thread.sleep((long) 100d);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "StartMainThread catch", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                if (Mci_write_Vb253_Modalita_Step.write_flag == true) {
                                    Mci_write_Vb253_Modalita_Step.mci.setValue(Mci_write_Vb253_Modalita_Step.valore);
                                    sl.WriteItem(Mci_write_Vb253_Modalita_Step.mci);

                                }

                                GestiscoFreccia(Mci_write_JogYMeno);
                                GestiscoFreccia(Mci_write_JogYPiu);
                                GestiscoFreccia(Mci_write_JogXPiu);
                                GestiscoFreccia(Mci_write_JogXMeno);
                                GestiscoFreccia(Mci_write_jogXPiuYMeno);
                                GestiscoFreccia(Mci_write_jogXPiuYPiu);
                                GestiscoFreccia(Mci_write_jogXMenoYPiu);
                                GestiscoFreccia(Mci_write_jogXMenoYMeno);
                                Utility.ScrivoVbVnVq(sl,Mci_write_Vb52_PCHome);

                                Utility.ScrivoVbVnVq(sl,Mci_write_VQ7002_QuotaDestinazioneX);
                                Utility.ScrivoVbVnVq(sl,Mci_write_VQ7022_QuotaDestinazioneY);
                                Utility.ScrivoVbVnVq(sl,Mci_write_MultiCmd_Vb72_HmiMoveXY);



                                Utility.GestiscoMci_Out_Toggle(sl, MciWrite_mci_vb88_sblocca_ago);

                                if (Mci_write_Vb557_PCPiedino.write_flag == true) {
                                    Mci_write_Vb557_PCPiedino.mci.setValue(Mci_write_Vb557_PCPiedino.valore);
                                    sl.WriteItem(Mci_write_Vb557_PCPiedino.mci);
                                    if (sl.getReturnCode() == 0)
                                        Mci_write_Vb557_PCPiedino.write_flag = false;
                                }

                                break;

                        }


                        sl.ReadItem(MultiCmd_Vb52_PCHome);

                        if (Esegui_Azzeramento){  Fai_Esci();}



                        //fine comando home

                        double X = (Double) MultiCmd_VQ111_PosX.getValue() / 1000d;
                        double Y = (Double) MultiCmd_VQ112_PosY.getValue() / 1000d;

                        Coord_Pinza.set(X, Y, null);


                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    TextView_XAss.setText("" + ((Double) MultiCmd_VQ111_PosX.getValue() / 1000d));
                                    TextView_YAss.setText("" + ((Double) MultiCmd_VQ112_PosY.getValue() / 1000d));
                                    myView_Tasca.AggiornaCanvas(true);

                                    Utility.GestioneVisualizzazioneToggleButton(getApplicationContext(), MciWrite_mci_vb88_sblocca_ago, Button_Sgancio_ago, "ic_sblocca_ago_press", "ic_sblocca_ago");

                                    Gestione_vb_emergenza();

                                    if(Fai_finish) {  //quando il ciclo azzeramento gestito da Fai_Esci ha finito chiama qui per cambiare pagina
                                        Fai_finish = false;
                                        finish();

                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                } else {
                    sl.Connect();
                }
            }
        }

        /**
         * TODO
         */
        private void Fai_Esci() {
            switch (step_Fai_Esci) {
                case 0:
                    Mci_write_Vb52_PCHome.valore = 1.0d;
                    Mci_write_Vb52_PCHome.write_flag = true;
                    step_Fai_Esci = 10;
                    break;
                case 10:
                    sl.ReadItem(MultiCmd_Vb52_PCHome);
                    if ((Double) MultiCmd_Vb52_PCHome.getValue() == 0.0d)
                        step_Fai_Esci = 20;
                    break;
                case 20:
                    Esegui_Azzeramento = false;
                    step_Fai_Esci = 30;
                    Fai_finish = true;

                    break;
                default:
                    break;
            }
        }
    }


}
