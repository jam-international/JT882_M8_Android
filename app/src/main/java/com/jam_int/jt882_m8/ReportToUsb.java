package com.jam_int.jt882_m8;

import static android.content.Intent.ACTION_MEDIA_MOUNTED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import me.jahnen.libaums.core.UsbMassStorageDevice;
import me.jahnen.libaums.core.fs.FileSystem;
import me.jahnen.libaums.core.fs.UsbFile;

public class ReportToUsb extends Activity {
    ImageButton ImageUsb, ImagePlay, ImageStop, Button_Exit;
    TextView TextView_barra_sotto;
    ListView listview;

    static UsbFile root;
    UsbMassStorageDevice device_usb;
    FileSystem currentFs;
    ProgressBar Progress_Bar;
    ArrayList<File> syslogFiles;
    ArrayAdapter<String> adapter_upgrade;
    Boolean Stop_DoinBackground = false;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_to_usb);

        ImageUsb = findViewById(R.id.imageUsb);
        ImagePlay = findViewById(R.id.imagePlay);
        Button_Exit = findViewById(R.id.imageButton_exit);
        Progress_Bar = findViewById(R.id.progressBar);
        Progress_Bar.setMax(100);
        Progress_Bar.getProgressDrawable().setColorFilter(
                Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
        TextView_barra_sotto = findViewById(R.id.textView_barra_sotto);
        TextView_barra_sotto.setText("Usb not insert");
        listview = findViewById(R.id.ListView_upgrade);

        ImagePlay.setVisibility(View.GONE);
        ImageStop = findViewById(R.id.imageStop);
        ImageStop.setVisibility(View.GONE);

        Intent intent = getIntent();
        String[] myStrings = intent.getStringArrayExtra("strings");

        if(myStrings.length > 0){

            PrintWriter writer = null;
            try {
                File rootFolder = android.os.Environment.getExternalStorageDirectory();
                File file_paht = new File(rootFolder.getAbsolutePath() + "/JamData/variabili_debug.txt");
                writer = new PrintWriter(file_paht, "UTF-8");

                for (String s: myStrings) {
                    writer.println(s);
                }

                writer.close();

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }


        }

        syslogFiles = new ArrayList<>();



        if (GetUSBConnectionStatus()) {
            UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(getApplicationContext());
            if (devices.length > 0) {
                device_usb = devices[0];

                PendingIntent permissionIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
                IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                registerReceiver(usbReceiver, filter);
                UsbManager manager = (UsbManager) getSystemService(getApplicationContext().USB_SERVICE);
                manager.requestPermission(device_usb.getUsbDevice(), permissionIntent);
            } else {
                TextView_barra_sotto.setText("Insert USB!");
            }
        } else {
            TextView_barra_sotto.setText("Insert USB!");
        }

        // Register usb events
        IntentFilter filter_attached = new IntentFilter(ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(usbReceiver, filter_attached);
        IntentFilter filter_mounted = new IntentFilter(ACTION_MEDIA_MOUNTED);
        registerReceiver(usbReceiver, filter_mounted);
        IntentFilter filter_permission = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter_permission);
        IntentFilter filter_detached = new IntentFilter(ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbReceiver, filter_detached);

    }

    private void GetSysLogFile() {

        try {
            root = currentFs.getRootDirectory();
            File rootFolder = android.os.Environment.getExternalStorageDirectory();
            File folder = new File(rootFolder.getAbsolutePath() + "/JamData");
            File[] listOfFiles = folder.listFiles();
            if(listOfFiles != null) {
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        System.out.println("File " + listOfFiles[i].getName());
                        String filename = listOfFiles[i].getName();
                        //syslog files
                        if (filename.contains("syslog")){
                            File file_syslog = new File(rootFolder.getAbsolutePath() + "/JamData/"+listOfFiles[i].getName());
                            syslogFiles.add(file_syslog);
                        }
                        //deb files
                        if (filename.contains(".deb")){
                            File file_syslog = new File(rootFolder.getAbsolutePath() + "/JamData/"+listOfFiles[i].getName());
                            syslogFiles.add(file_syslog);
                        }


                    } else if (listOfFiles[i].isDirectory()) {
                        System.out.println("Directory " + listOfFiles[i].getName());
                    }
                }
            }
            //info_Jam
            File file_info_Jam = new File(rootFolder.getAbsolutePath() + "/JamData/info_Jam.txt");
            if(file_info_Jam.isFile() && file_info_Jam.length() != 0 && file_info_Jam != null)
                syslogFiles.add(file_info_Jam);
            //MachineLog
            File file_MachineLog = new File(rootFolder.getAbsolutePath() + "/JamData/MachineLog.txt");
            if(file_MachineLog.isFile() && file_MachineLog.length() != 0 && file_MachineLog != null)
                syslogFiles.add(file_MachineLog);
            //File_XML_path_R XML
            File file_xml_path_R = new File(Values.File_XML_path_R);
            if(file_xml_path_R.isFile() && file_xml_path_R.length() != 0 && file_xml_path_R != null)
                syslogFiles.add(file_xml_path_R);
            //File_XML_path_R UDF
            try {
                String path_file_udf = Values.File_XML_path_R;
                path_file_udf = path_file_udf.replace(".xml",".udf");

                File file_xml_path_R_udf = new File(path_file_udf);
                if(file_xml_path_R_udf.isFile() && file_xml_path_R_udf.length() != 0 && file_xml_path_R_udf != null)
                    syslogFiles.add(file_xml_path_R_udf);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //variabili_debug.txt
            File file_syslog = new File(rootFolder.getAbsolutePath() + "/JamData/variabili_debug.txt");
            if(file_syslog.isFile() && file_syslog.length() != 0 && file_syslog != null)
                syslogFiles.add(file_syslog);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

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
        try {
            unregisterReceiver(usbReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Button for exit from the activity
     *
     * @param view
     */
    public void onclick_button_Exit(View view) {
        unregisterReceiver(usbReceiver);


        this.finish();
    }
    /**
     * Button for start the upgrade
     *
     * @param view
     */
    public void onclick_play(View view) {
        ImagePlay.setImageResource(R.drawable.ic_button_play_press);
        ImageStop.setVisibility(View.VISIBLE);



        if (syslogFiles.size() > 0 ) {

                TextView_barra_sotto.setText("Downloading "+syslogFiles.size()+" files..............Please wait");
                new ScriviFileIntoUSB(ReportToUsb.this).execute(syslogFiles);

        } else {
            Toast.makeText(getApplicationContext(), "there are no files to download", Toast.LENGTH_SHORT).show();
            ImageStop.setVisibility(View.GONE);
            ImagePlay.setImageResource(R.drawable.ic_button_play);
        }

    }
    /**
     * Button for stop the upgrade
     *
     * @param view
     */
    public void onclick_stop(View view) {
        Stop_DoinBackground = true;
        ImageStop.setVisibility(View.GONE);
        ImagePlay.setVisibility(View.GONE);

        Button_Exit.setVisibility(View.VISIBLE);
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
     * Receiver for the Usb
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
                try {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReportToUsb.this, android.R.layout.simple_list_item_1);
                    adapter.notifyDataSetChanged();

                    ImageUsb.setImageResource(R.drawable.ic_usb_x);
                    ImagePlay.setVisibility(View.GONE);



                } catch (Exception e) {
                    e.printStackTrace();
                }
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

                                ImageUsb.setImageResource(R.drawable.ic_usb_ok);
                                ImagePlay.setVisibility(View.VISIBLE);

                                GetSysLogFile();
                                if(syslogFiles.size()> 0) {
                                    ImagePlay.setVisibility(View.VISIBLE);
                                    TextView_barra_sotto.setText("Ready to send " + syslogFiles.size() + " files");
                                }

                            } catch (Exception e) {
                               // Toast.makeText(getApplicationContext(), getString(R.string.ErroreUSB_FAT32), Toast.LENGTH_SHORT).show();
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


    class ScriviFileIntoUSB extends AsyncTask<ArrayList<File>, ArrayList<String>, ArrayList<Integer>> {
        private final ProgressDialog dialog;
        ImageButton btn_Exit;


        public ScriviFileIntoUSB(Context context) {
            this.dialog = new ProgressDialog(context);
            this.dialog.setTitle("Titel");
            this.dialog.setMessage("Message");
            btn_Exit = findViewById(R.id.imageButton_exit);
        }


        @Override
        protected ArrayList<Integer> doInBackground(ArrayList<File>... params) {
            boolean r = false;
            ArrayList<String> val_progress = new ArrayList<>();
            Integer numero_file = 0;
            Integer numero_done = 0;
            Integer numero_errori = 0;
            Stop_DoinBackground = false;

            int cnt = 0;
            ArrayList<Integer> res = new ArrayList();
            Integer numero_file_totali = params[0].size();
            for (File file_item : params[0]) {
                if (Stop_DoinBackground) break;
                try {
                    Utility.copyFileToUsb(file_item, root);
                    numero_file++;
                    val_progress.add(file_item.getName()+  " copying ....");
                    publishProgress(val_progress);
                } catch (IOException e) {

                    numero_errori++;
                }


            }
            res.add(numero_file);
            res.add(numero_done);
            res.add(numero_errori);
            return res;
        }

        protected void onPostExecute(ArrayList<Integer> ret) {
            super.onPostExecute(ret);
            this.dialog.dismiss();

            Integer numero_file = 0;
            Integer numero_done = 0;
            Integer numero_errori = 0;
            if (ret.size() > 1) {
                numero_file = ret.get(0);
                numero_done = ret.get(1);
                numero_errori = ret.get(2);
            }

            btn_Exit.setVisibility(View.VISIBLE);
            ImagePlay.setVisibility(View.GONE);
            ImageStop.setVisibility(View.GONE);

            TextView_barra_sotto.setText("End: Total files: " + numero_file + ", Errors: " + numero_errori);

            if(device_usb != null)
                device_usb.close(); //chiudo la chiavetta USB

        }

        @Override
        protected void onProgressUpdate(ArrayList<String>... values) {
            ArrayList<String> val = values[0];
            ArrayList<String> myObject = new ArrayList<String>(val);    //facendo la copia evito crash se nel frattempo qualcuno cambia i dati

            adapter_upgrade = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    myObject);
            adapter_upgrade.notifyDataSetChanged();

            listview.setAdapter(adapter_upgrade);
            listview.setSelection(adapter_upgrade.getCount() - 1); //scrolla in alto
        }
    }

}
