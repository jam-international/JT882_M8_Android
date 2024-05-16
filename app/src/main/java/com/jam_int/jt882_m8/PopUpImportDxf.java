package com.jam_int.jt882_m8;

import static android.content.Intent.ACTION_MEDIA_MOUNTED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.commons.io.FilenameUtils;
import org.kabeja.dxf.DXFConstants;
import org.kabeja.dxf.DXFDocument;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFLayer;
import org.kabeja.parser.DXFParser;
import org.kabeja.parser.Parser;
import org.kabeja.parser.ParserBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.jahnen.libaums.core.UsbMassStorageDevice;
import me.jahnen.libaums.core.fs.FileSystem;
import me.jahnen.libaums.core.fs.UsbFile;

public class PopUpImportDxf extends AppCompatActivity {

    /**
     * USB permission
     */
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    static UsbFile root;
    /**
     * List of available files
     */
    ListView LvList;
    /**
     * Extension string for display only the files with this extension
     */
    String estensione_list = "dxf";
    String Folder_corrente = "";
    /**
     * Selected file to import
     */
    File FileSelezionato;
    /**
     * Indicate if the selected file is a folder or not
     */
    boolean Folder = false;
    /**
     * USB
     */
    UsbMassStorageDevice device_usb;
    FileSystem currentFs;
    Intent databack = new Intent();
    String databack_text = "";  //serve per indicare alla Mainactivity quali file XML vanno ricaricati

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_import_dxf);

        // Init the usb list
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

        // Init the receiver for handle the usb events
        IntentFilter filter_attached = new IntentFilter(ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(usbReceiver, filter_attached);
        IntentFilter filter_mounted = new IntentFilter(ACTION_MEDIA_MOUNTED);
        registerReceiver(usbReceiver, filter_mounted);
        IntentFilter filter_permission = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter_permission);
        IntentFilter filter_detached = new IntentFilter(ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbReceiver, filter_detached);

        LvList = findViewById(R.id.LvList);
        Inizializzo_eventi();
    }

    /**
     * Function for init the ListView event
     */
    private void Inizializzo_eventi() {
        //Evento alla pressione di una riga del List di DX
        LvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                final String entryName = (String) adapterView.getItemAtPosition(pos);    //leggo il nome del file o folder premuto

                if (entryName.endsWith("/")) {  //è un file o folder?
                    //folder
                    if (Folder) {
                        Folder_corrente = Folder_corrente + "/" + SubString.Before(entryName, "/");
                        String[] str = Folder_corrente.split("/");
                        UsbFile[] files = new UsbFile[0];
                        try {
                            files = root.listFiles();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        UsbFile[] files1 = new UsbFile[0];
                        UsbFile root1;
                        root1 = root;
                        for (int i = 1; i < str.length; i++) {
                            for (UsbFile file : files) {
                                if (str[i].equals(file.getName())) {
                                    try {
                                        files = root1.search(file.getName()).listFiles();
                                        root1 = file;
                                        break;
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        ShowList_USB(LvList, files1, estensione_list);
                        Folder = false;
                    } else {
                        String file_selezionato = Folder_corrente + "/" + entryName;
                        String file_precedente_selezionato = "";
                        if (FileSelezionato != null)
                            file_precedente_selezionato = FileSelezionato.getPath();
                        if (file_selezionato.equals(file_precedente_selezionato)) {  //stesso file di prima, lo deselezioni
                            TolgoSelezioneVisiva(adapterView);

                        } else {
                            //selezionato nuovo file
                            FileSelezionato = new File(Folder_corrente + "/" + entryName);
                            SelezionaGrigio(adapterView, view);
                        }
                        Folder = true;
                    }

                } else {   //file
                    String file_selezionato = Folder_corrente + "/" + entryName;
                    String file_precedente_selezionato = "";
                    if (FileSelezionato != null)
                        file_precedente_selezionato = FileSelezionato.getPath();
                    if (file_selezionato.equals(file_precedente_selezionato)) {  //stesso file di prima, lo deselezioni
                        TolgoSelezioneVisiva(adapterView);

                    } else {
                        //selezionato nuovo file
                        FileSelezionato = new File(Folder_corrente + "/" + entryName);
                        SelezionaGrigio(adapterView, view);
                    }
                }
            }
        });
    }

    /**
     * Button for exit
     *
     * @param view
     */
    public void onClick_Button_exit(View view) {
        Values.File = null;
        finish();
    }

    /**
     * Button for confirm the import
     *
     * @param v
     */
    public void On_click_btn_confirm(View v) {
        try {
            UsbFile fileInput = null;
            try {
                String[] folder_split = Folder_corrente.split("/");

                UsbFile folder = root;
                for (String str : folder_split) {
                    if (!str.equals("")) {
                        folder = folder.search(str);
                    }
                }
                fileInput = folder.search(FileSelezionato.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            File fileOutput = new File("/storage/emulated/0/JamData/dxfImported.dxf");
            Utility.copyFileToHMI(fileInput, fileOutput, currentFs);
            if (fileOutput.exists()) {
                Parser parser = ParserBuilder.createDefaultParser();
                parser.parse(fileOutput.getPath(), DXFParser.DEFAULT_ENCODING);
                DXFDocument doc = parser.getDocument();
                DXFLayer layer = doc.getDXFLayer("0");
                List<DXFEntity> entityLines = layer.getDXFEntities(DXFConstants.ENTITY_TYPE_LINE);
                List<DXFEntity> entityArcs = layer.getDXFEntities(DXFConstants.ENTITY_TYPE_ARC);
                List<DXFEntity> list = entityLines;
                if (entityArcs != null) {
                    list.addAll(entityArcs);
                }
                ArrayList<DXFEntity> entity = new ArrayList<DXFEntity>(list);

                if (entity.size() > 0 || entity.size() <= 6) {
                    ArrayList<String> valori_pts = Dxf_import.getPtsValue(entity, getApplicationContext());
                    if (valori_pts.size() > 0) {
                        File gpxfile = new File("/storage/emulated/0/JamData/punti/" + FileSelezionato.getName().replace(FilenameUtils.getExtension(FileSelezionato.getName()), "pts"));
                        //append text
                        BufferedWriter out = new BufferedWriter(new FileWriter(gpxfile.toString(), false));
                        for (int i = 0; i < valori_pts.size(); i++) {
                            out.write(valori_pts.get(i) + "\n");
                        }
                        out.close();
                        databack_text = gpxfile.getAbsolutePath();
                        //---set the data to pass back---
                        databack.setData(Uri.parse(databack_text));
                        setResult(RESULT_OK, databack);

                        Values.File = gpxfile.getName();

                        finish();
                        Toast.makeText(this, "DXF imported", Toast.LENGTH_LONG).show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Button for go up with the folder
     *
     * @param v
     */
    public void On_click_button_back(View v) {
        try {
            if (!Folder_corrente.equals("/")) //non faccio scendere sotto la root
            {
                String Folder_back = SubString.BeforeLast(Folder_corrente, "/");
                Folder_corrente = Folder_back;
                String[] str = Folder_corrente.split("/");
                UsbFile[] files = new UsbFile[0];
                try {
                    files = root.listFiles();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UsbFile[] files1 = new UsbFile[0];
                if (str.length > 1) {
                    for (UsbFile file : files) {
                        if (str[str.length - 1].equals(file.getName())) {
                            try {
                                files1 = root.search(Folder_corrente).listFiles();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    files1 = root.search("/").listFiles();
                }

                ShowList_USB(LvList, files1, estensione_list);
                TolgoSelezioneVisiva(LvList);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        try {
            unregisterReceiver(usbReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show list of Dxf files on the USB
     *
     * @param lvList
     * @param files
     * @param filtro_estensione
     */
    private void ShowList_USB(ListView lvList, UsbFile[] files, String filtro_estensione) {
        ArrayAdapter<String> adapter;
        ArrayList<String> folders = new ArrayList<String>();
        ArrayList<String> listItems = new ArrayList<String>();
        ArrayList<String> files_list = new ArrayList<String>();

        for (UsbFile file : files) {
            // Check if is a file or a folder
            if (file.isDirectory()) {
                folders.add(file.getName());
            } else {
                String name = file.getName();
                String estensione = "";
                try {
                    estensione = name.substring(name.lastIndexOf(".") + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (estensione.equalsIgnoreCase(filtro_estensione))
                    files_list.add(file.getName());
            }
        }

        Collections.sort(folders, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        Collections.sort(files_list, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        listItems.clear();

        // Add a slash at the end of every folder name
        for (int i = 0; i < folders.size(); i++) {
            listItems.add(folders.get(i) + "/");
        }

        for (int i = 0; i < files_list.size(); i++) {
            listItems.add(files_list.get(i));
        }

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        adapter.notifyDataSetChanged();

        lvList.setAdapter(adapter);
    }

    /**
     * Function for remove selection from all rows
     *
     * @param adapterView
     */
    private void TolgoSelezioneVisiva(AdapterView<?> adapterView) {
        for (int j = 0; j < adapterView.getChildCount(); j++)
            adapterView.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * Function for deselect a row
     *
     * @param adapterView
     * @param view
     */
    private void SelezionaGrigio(AdapterView<?> adapterView, View view) {
        for (int j = 0; j < adapterView.getChildCount(); j++)
            adapterView.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
        view.setBackgroundColor(Color.LTGRAY);
    }

    /**
     * Function for check if at least 1 device is connected
     *
     * @return
     */
    private boolean GetUSBConnectionStatus() {
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(getApplicationContext());
        return devices.length > 0;
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
     * Receiver that handle the usb events
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
                // I don't need to do nothing on this event
            }

            if (ACTION_USB_DEVICE_DETACHED.equals(action)) {
                try {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(PopUpImportDxf.this, android.R.layout.simple_list_item_1);
                    ArrayList<String> listItems = new ArrayList<String>();
                    adapter.notifyDataSetChanged();
                    LvList.setAdapter(adapter);
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
                                root = currentFs.getRootDirectory();

                                UsbFile[] files = root.listFiles();

                                ShowList_USB(LvList, files, estensione_list);

                                Log.d("ListRow", "Row ListSx: " + LvList.getAdapter().getCount());
                                Log.d("ListRow", "Row ListDx: " + LvList.getAdapter().getCount());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), getString(R.string.ErroreUSB_FAT32), Toast.LENGTH_SHORT).show();
                                Log.d("TAG", "usb init error " + e);
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


}