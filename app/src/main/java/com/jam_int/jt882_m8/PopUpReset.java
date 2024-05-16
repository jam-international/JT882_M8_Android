package com.jam_int.jt882_m8;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import communication.MultiCmdItem;
import communication.ShoppingList;

public class PopUpReset extends AppCompatActivity {

    /**
     * ShoppigList for communicate with PLC
     */
    ShoppingList sl;

    /**
     * PLC vars
     */
    MultiCmdItem[] mci_array_factory;

    /**
     * Thread
     */
    Thread thread_factory;
    boolean Thread_Running = false, StopThread = false, Letto_VBVNVQ_done = false, Scritto_VBVNVQ_Done = false;
    boolean readVBVNVQ = false, writeVBVNVQ = false;

    boolean Edit = false;
    String[] dati_variabili = new String[15002];
    File root;
    TextView TextView_cnt;
    int mc_readVBVNVQ = 0, cicli = 0, mc_writeVBVNVQ = 0;
    StringBuffer sb;
    String[] lines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_reset);
        TextView_cnt = findViewById(R.id.textView_cnt);
        TextView_cnt.setText("");
    }

    /**
     * Button for save the current configuration (remove the selected files)
     *
     * @param v
     */
    public void BtnConferma(View v) {
        try {
            //Info_Jam.txt
            CheckBox checkBox = findViewById(R.id.checkBox);
            if (checkBox.isChecked()) {
                File JamData = new File(Environment.getExternalStorageDirectory() + "/JamData");
                File info_Jam = new File(JamData, "info_Jam.txt");
                info_Jam.delete();
                Edit = true;
            }

            //MachineLog.txt
            CheckBox checkBox1 = findViewById(R.id.checkBox1);
            if (checkBox1.isChecked()) {
                File JamData = new File(Environment.getExternalStorageDirectory() + "/JamData");
                File MachineLog = new File(JamData, "MachineLog.txt");
                MachineLog.delete();
                Edit = true;
            }

            //Password.txt
            CheckBox checkBox2 = findViewById(R.id.checkBox2);
            if (checkBox2.isChecked()) {
                File JamData = new File(Environment.getExternalStorageDirectory() + "/JamData");
                File Password = new File(JamData, "Password.txt");
                Password.delete();
                Edit = true;

                Toast.makeText(getApplicationContext(), "Default Password is 67872", Toast.LENGTH_LONG).show();
            }

            //Users.txt
            CheckBox checkBox3 = findViewById(R.id.checkBox3);
            if (checkBox3.isChecked()) {
                File JamData = new File(Environment.getExternalStorageDirectory() + "/JamData");
                File Users = new File(JamData, "Users.txt");
                Users.delete();
                Edit = true;
            }

            //Save parametri fabbrica
            CheckBox checkBox4 = findViewById(R.id.checkBox4);
            if (checkBox4.isChecked()) {

                sl = SocketHandler.getSocket();
                sl.Clear();

                File root = new File(Environment.getExternalStorageDirectory(), "JamData");
                if (!root.exists()) {
                    root.mkdirs();
                }

                TextView_cnt.setText("Running");

                readVBVNVQ = true;
                StopThread = false;


                // Start the thread
                if (!Thread_Running) {
                    MyAndroidThread_Factory myTask_factory = new MyAndroidThread_Factory(PopUpReset.this);
                    thread_factory = new Thread(myTask_factory, "Factory myTask");
                    thread_factory.start();
                }
            }

            //Save parametri fabbrica
            CheckBox checkBox5 = findViewById(R.id.checkBox5);
            if (checkBox5.isChecked()) {

                sl = SocketHandler.getSocket();
                sl.Clear();

                File root = new File(Environment.getExternalStorageDirectory(), "JamData");
                if (!root.exists()) {
                    root.mkdirs();
                }

                TextView_cnt.setText("Running");


                writeVBVNVQ = true;
                StopThread = false;

                // Start the thread
                if (!Thread_Running) {
                    MyAndroidThread_Factory myTask_factory = new MyAndroidThread_Factory(PopUpReset.this);
                    thread_factory = new Thread(myTask_factory, "Factory myTask");
                    thread_factory.start();
                }
            }

            if (Edit) {
                Intent mStartActivity = new Intent(PopUpReset.this, Emergency_page.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(PopUpReset.this, mPendingIntentId, mStartActivity,
                        PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) PopUpReset.this.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
            } else {
                if (!checkBox4.isChecked() && !checkBox5.isChecked())
                    finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Button for exit
     *
     * @param v
     */
    public void on_click_exit(View v) {
        KillThread();
        finish();
    }

    /**
     * Checkbox Click
     * <p>
     * TODO Why this?
     *
     * @param view
     */
    public void checkbox4_onclick(View view) {
 //       CheckBox checkbox4 = findViewById(R.id.checkBox4);
//        checkbox4.setChecked(false);
    }

    /**
     * Checkbox Click
     * <p>
     * TODO Why this?
     *
     * @param view
     */
    public void checkbox5_onclick(View view) {
 //       CheckBox checkbox5 = findViewById(R.id.checkBox5);
 //       checkbox5.setChecked(false);
    }

    /**
     * Function for write a PLC var
     *
     * @throws IOException
     */
    private void Scrivi_VBVNVQ() throws IOException {

        switch (mc_writeVBVNVQ) {
            case 0: //
                //Creating an InputStream object
                File root = new File(Environment.getExternalStorageDirectory(), "JamData");
                InputStream inputStream = new FileInputStream(root + "/factory_VBVNVQ.pts");
                //creating an InputStreamReader object
                InputStreamReader isReader = new InputStreamReader(inputStream);
                //Creating a BufferedReader object
                BufferedReader reader = new BufferedReader(isReader);
                sb = new StringBuffer();
                String str;
                while ((str = reader.readLine()) != null) {
                    sb.append(str + "\n");
                }
                lines = sb.toString().split("\\n");
                cicli = 0;
                mc_writeVBVNVQ = 1;

                MultiCmdItem mci1 = sl.Add("Io", 1, MultiCmdItem.dtVB, 0, MultiCmdItem.dpNONE);
                mci1.setValue(1.0d);
                sl.WriteItem(mci1);
                break;
            case 1:
                mci_array_factory = new MultiCmdItem[100];

                for (int i = 0; i < 100; i++) {
                    try {
                        String line = lines[i + cicli * 100];
                        String[] separated = line.split("=");
                        int type = Integer.valueOf(separated[0]);
                        int index = Integer.valueOf(separated[1]);
                        Double valore = Double.valueOf(separated[2]);
                        MultiCmdItem mci = sl.Add("Io", 1, type, index, MultiCmdItem.dpNONE);
                        mci.setValue(valore);
                        mci_array_factory[i] = mci;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mc_writeVBVNVQ = 2;
                break;
            case 2:
                sl.WriteItems(mci_array_factory);
                cicli++;
                mc_writeVBVNVQ = 3;
                break;
            case 3:
                if (cicli < 150) {
                    mc_writeVBVNVQ = 1;
                } else {
                    mc_writeVBVNVQ = 4;
                }
                break;
            case 4:
                writeVBVNVQ = false;  //fine
                Scritto_VBVNVQ_Done = true;
                mc_writeVBVNVQ = 0;
                break;
            default:
                break;
        }
    }

    /**
     * Function for read a PLC var
     */
    private void Leggi_VBVNVQ() {
        switch (mc_readVBVNVQ) {
            case 0: //leggo VB
                mci_array_factory = new MultiCmdItem[100];
                for (int i = 0; i < 100; i++) {
                    MultiCmdItem mci = sl.Add("Io", 1, MultiCmdItem.dtVB, i + (cicli * 100), MultiCmdItem.dpNONE);
                    mci_array_factory[i] = mci;
                }
                mc_readVBVNVQ = 1;
                break;
            case 1:
                sl.ReadItems(mci_array_factory);
                if (sl.getReturnCode() == 0) {
                    for (int i = 0; i < 100; i++) {
                        MultiCmdItem mci = mci_array_factory[i];
                        dati_variabili[i + (cicli * 100)] = mci.getType() + "=" + mci.getIndex() + "=" + mci.getValue().toString();
                    }
                }
                cicli++;
                mc_readVBVNVQ = 2;
                break;
            case 2:
                if (cicli < 50) {
                    mc_readVBVNVQ = 0;
                } else
                    mc_readVBVNVQ = 3;
                break;
            case 3:
                cicli = 0;
                mc_readVBVNVQ = 5;
                break;
            case 5:
                mci_array_factory = new MultiCmdItem[100];

                for (int i = 0; i < 100; i++) {

                    MultiCmdItem mci = sl.Add("Io", 1, MultiCmdItem.dtVN, i + (cicli * 100), MultiCmdItem.dpNONE);
                    mci_array_factory[i] = mci;
                }
                mc_readVBVNVQ = 6;
            case 6:
                sl.ReadItems(mci_array_factory);
                if (sl.getReturnCode() == 0) {
                    for (int i = 0; i < 100; i++) {
                        MultiCmdItem mci = mci_array_factory[i];
                        dati_variabili[i + (cicli * 100) + 5000] = mci.getType() + "=" + mci.getIndex() + "=" + mci.getValue().toString();
                    }
                }
                cicli++;
                mc_readVBVNVQ = 7;
                break;
            case 7:
                if (cicli < 50) {
                    mc_readVBVNVQ = 5;
                } else
                    mc_readVBVNVQ = 8;
                break;
            case 8:
                cicli = 0;
                mc_readVBVNVQ = 10;
                break;
            case 10:
                mci_array_factory = new MultiCmdItem[100];
                for (int i = 0; i < 100; i++) {
                    MultiCmdItem mci = sl.Add("Io", 1, MultiCmdItem.dtVQ, i + (cicli * 100), MultiCmdItem.dpNONE);
                    mci_array_factory[i] = mci;
                }
                mc_readVBVNVQ = 11;
            case 11:
                sl.ReadItems(mci_array_factory);
                if (sl.getReturnCode() == 0) {
                    for (int i = 0; i < 100; i++) {
                        MultiCmdItem mci = mci_array_factory[i];
                        dati_variabili[i + (cicli * 100) + 10000] = mci.getType() + "=" + mci.getIndex() + "=" + mci.getValue().toString();
                    }
                }
                cicli++;
                mc_readVBVNVQ = 12;
                break;
            case 12:
                if (cicli < 50) {
                    mc_readVBVNVQ = 10;
                } else
                    mc_readVBVNVQ = 13;
                break;
            case 13:
                mc_readVBVNVQ = 0;
                readVBVNVQ = false;
                Letto_VBVNVQ_done = true;
                break;
            default:
                break;
        }
    }

    private void KillThread() {
        if (Thread_Running) {
            StopThread = true;

            try {
                if (!Thread_Running)
                    thread_factory.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("JAM TAG", "Stop Factory reset Thread");
        }
    }

    class MyAndroidThread_Factory implements Runnable {
        Activity activity;

        public MyAndroidThread_Factory(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            while (true) {
                Thread_Running = true;

                try {
                    Thread.sleep((long) 20d);
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
                    if (readVBVNVQ) {
                        Leggi_VBVNVQ();
                    }

                    if (writeVBVNVQ) {
                        try {
                            Scrivi_VBVNVQ();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (Letto_VBVNVQ_done) {
                                File root = new File(Environment.getExternalStorageDirectory(), "JamData");
                                File gpxfile = new File(root, "factory_VBVNVQ.pts");
                                // append text
                                BufferedWriter out = null;

                                try {
                                    out = new BufferedWriter(new FileWriter(gpxfile.toString(), false));
                                    for (String s : dati_variabili) {
                                        out.write(s + "\n");
                                    }

                                    out.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                TextView_cnt.setText("Done");
                                StopThread = true;
                                Letto_VBVNVQ_done = false;
                                CheckBox checkbox4 = findViewById(R.id.checkBox4);
                                checkbox4.setChecked(false);

                            } else {
                                String testo = (String) TextView_cnt.getText();
                                if (!testo.equals("Done"))
                                    TextView_cnt.setText("Running " + cicli);
                            }

                            if (writeVBVNVQ) {
                                TextView_cnt.setText("Running " + cicli);
                            }
                            if (Scritto_VBVNVQ_Done) {
                                TextView_cnt.setText("Done");
                                StopThread = true;
                                Scritto_VBVNVQ_Done = false;
                                CheckBox checkbox5 = findViewById(R.id.checkBox5);
                                checkbox5.setChecked(false);
                            }
                        }
                    });
                } else
                    sl.Connect();
            }
        }
    }
}
