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
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.jamint.ricette.Ricetta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import me.jahnen.libaums.core.UsbMassStorageDevice;
import me.jahnen.libaums.core.fs.FileSystem;
import me.jahnen.libaums.core.fs.UsbFile;

public class Usb_Files_Activity extends Activity {

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    static UsbFile root;
    /**
     * Elements that contains the list of files
     */
    ListView LvListDX;
    ListView LvListSX;
    /**
     * Buttons for execute actions on files
     */
    Button Button_dxsx;
    Button Button_sxdx;
    Button Button_back_sx;
    Button Button_back_dx;
    Button Button_delete_dx;
    Button Button_delete_sx;
    Button Button_sxdx_all;
    Button Button_dxsx_all;
    ImageButton IButton_exit;
    /**
     * List that indicate the allowed extension of the USB files
     */
    ArrayList<String> estensione_list_sx = new ArrayList<String>();
    /**
     * Var tha indicate the extension of the files to read from the HD files
     */
    String estensione_list_dx = "xml";
    /**
     * Var that indicate the start path of the Ricette files (Default path)
     */
    String Folder_corrente_dx = "/storage/emulated/0/ricette";
    /**
     * Var that indicate the start path of the USB (Default path)
     */
    String Folder_corrente_sx = "";
    /**
     * Bool for indicate if the selected file is a folder or not
     */
    boolean FolderSx = false, FolderDx = false;
    /**
     * Var for save the selected file of every list
     */
    File FileSelezionato_dx, FileSelezionato_sx;
    /**
     * USB vars
     */
    UsbMassStorageDevice device_usb;
    FileSystem currentFs;
    /**
     * Var that will contain the Usb files
     */
    ArrayAdapter<String> adapterUSB;
    ArrayList<String> listItemsUSB = new ArrayList<String>();
    ArrayList<Boolean> listItemsSelectedUSB = new ArrayList<Boolean>();

    /**
     * Var that will contain the HMI files
     */
    ArrayAdapter<String> adapterHMI;
    ArrayList<String> listItemsHMI = new ArrayList<String>();
    ArrayList<Boolean> listItemsSelectedHMI = new ArrayList<Boolean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_files);

        // Save the list elements
        LvListDX = findViewById(R.id.LvList_dx);
        LvListSX = findViewById(R.id.LvList_sx);

        // Save the buttons elements
        Button_dxsx = findViewById(R.id.Btn_dxsx);
        Button_sxdx = findViewById(R.id.Btn_sxdx);
        Button_dxsx_all = findViewById(R.id.Btn_dxsx_all);
        Button_sxdx_all = findViewById(R.id.Btn_sxdx_all);
        Button_back_sx = findViewById(R.id.button_back_sx);
        Button_back_dx = findViewById(R.id.button_back_dx);
        Button_sxdx = findViewById(R.id.Btn_sxdx);
        Button_delete_dx = findViewById(R.id.btn_delete_dx);
        Button_delete_sx = findViewById(R.id.btn_delete_sx);

        IButton_exit = findViewById(R.id.imageButton_exit);

        estensione_list_sx.add("xml");
        estensione_list_sx.add("eep");

        //controllo se la usb è già inserita, se si partirà il broadcast ACTION_USB_PERMISSION che scriverà i file sullo schermo,
        //se invece verrà inserita successivamente, partirà il broadcast ACTION_USB_DEVICE_ATTACHED il quale controllerà il permesso e
        //poi farà partire il broadcast ACTION_USB_PERMISSION che scriverà i file sullo schermo.

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

        // Register Usb events
        IntentFilter filter_attached = new IntentFilter(ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(usbReceiver, filter_attached);
        IntentFilter filter_mounted = new IntentFilter(ACTION_MEDIA_MOUNTED);
        registerReceiver(usbReceiver, filter_mounted);
        IntentFilter filter_permission = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(usbReceiver, filter_permission);
        IntentFilter filter_detached = new IntentFilter(ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbReceiver, filter_detached);

        Button_dxsx.setEnabled(false);
        Button_sxdx.setEnabled(false);
        Button_delete_dx.setEnabled(false);
        Button_delete_sx.setEnabled(false);
        Button_dxsx_all.setEnabled(false);
        Button_sxdx_all.setEnabled(false);

        // Override some events
        Inizializzo_eventi();

        // Show the list of files
        ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
    }

    /**
     * Function for override events of list when item clocked
     */
    private void Inizializzo_eventi() {

        //Evento alla pressione di una riga del List di DX
        LvListDX.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                try {
                    final String entryName = (String) adapterView.getItemAtPosition(pos);    //leggo il nome del file o folder premuto

                    if (entryName.endsWith("/")) {  //è un file o folder?
                        //folder

                        //Se FolderDx è false è stata selezionata una cartella ma non ci si è entrati
                        //Se FolderDx è true è stata selezionata una cartella e ci si è entrati
                        if (FolderDx && FileSelezionato_dx.getPath().equals(Folder_corrente_dx + "/" + SubString.Before(entryName, "/"))) {
                            Folder_corrente_dx = Folder_corrente_dx + "/" + SubString.Before(entryName, "/");
                            ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
                            TolgoSelezioneVisivaHMI();
                            FolderDx = false;
                            Button_dxsx.setEnabled(false);
                            Button_delete_dx.setEnabled(false);
                            FileSelezionato_dx = null;
                        } else {
                            String file_selezionato = Folder_corrente_dx + "/" + entryName;
                            String file_precedente_selezionato = "";
                            if (FileSelezionato_dx != null)
                                file_precedente_selezionato = FileSelezionato_dx.getPath();
                            if (file_selezionato.equals(file_precedente_selezionato)) {  //stesso file di prima, lo deselezioni
                                TolgoSelezioneVisivaHMI();
                                Button_dxsx.setEnabled(false);
                                Button_delete_dx.setEnabled(false);
                                FileSelezionato_dx = null;

                            } else {
                                //selezionato nuovo file
                                FileSelezionato_dx = new File(Folder_corrente_dx + "/" + entryName);
                                SelezionaGrigioHMI(pos);
                                try {
                                    if (root.listFiles().length != 0) {
                                        Button_dxsx.setEnabled(true);
                                        Button_delete_dx.setEnabled(true);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            FolderDx = true;
                        }

                    } else {   //file
                        String file_selezionato = Folder_corrente_dx + "/" + entryName;
                        String file_precedente_selezionato = "";
                        if (FileSelezionato_dx != null)
                            file_precedente_selezionato = FileSelezionato_dx.getPath();
                        if (file_selezionato.equals(file_precedente_selezionato)) {  //stesso file di prima, lo deselezioni
                            TolgoSelezioneVisivaHMI();
                            Button_dxsx.setEnabled(false);
                            Button_delete_dx.setEnabled(false);
                            FileSelezionato_dx = null;
                        } else {
                            //selezionato nuovo file
                            FileSelezionato_dx = new File(Folder_corrente_dx + "/" + entryName);
                            Button_delete_dx.setEnabled(true);
                            SelezionaGrigioHMI(pos);
                            try {
                                if (root != null) {
                                    if (root.listFiles().length != 0) {
                                        Button_dxsx.setEnabled(true);

                                        String str1 = FileSelezionato_dx.getAbsolutePath();
                                        String str2 = Values.File_XML_path_R;
                                        //controllo se ho cancellato il file carico nel CN
                                        Button_delete_dx.setEnabled(!str1.equals(str2));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        });

        LvListSX.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                try {
                    final String entryName = (String) adapterView.getItemAtPosition(pos);    //leggo il nome del file o folder premuto

                    if (entryName.endsWith("/")) {  //è un file o folder?
                        //folder
                        if (FolderSx && FileSelezionato_sx.getPath().equals(Folder_corrente_sx + "/" + SubString.Before(entryName, "/"))) {
                            Folder_corrente_sx = Folder_corrente_sx + "/" + SubString.Before(entryName, "/");
                            String[] str = Folder_corrente_sx.split("/");
                            UsbFile[] files = new UsbFile[0];
                            UsbFile root1;
                            root1 = root;
                            try {
                                files = root1.listFiles();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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

                            ShowList_USB(LvListSX, files, estensione_list_sx);
                            TolgoSelezioneVisivaUSB();
                            FolderSx = false;
                            Button_sxdx.setEnabled(false);
                            Button_delete_sx.setEnabled(false);
                            FileSelezionato_sx = null;
                        } else {
                            String file_selezionato = Folder_corrente_sx + "/" + entryName;
                            String file_precedente_selezionato = "";
                            if (FileSelezionato_sx != null)
                                file_precedente_selezionato = FileSelezionato_sx.getPath();
                            if (file_selezionato.equals(file_precedente_selezionato)) {  //stesso file di prima, lo deselezioni
                                TolgoSelezioneVisivaUSB();
                                Button_sxdx.setEnabled(false);
                                Button_delete_sx.setEnabled(false);
                                FileSelezionato_sx = null;
                            } else {
                                //selezionato nuovo file
                                FileSelezionato_sx = new File(Folder_corrente_sx + "/" + entryName);
                                SelezionaGrigioUSB(pos);
                                Button_sxdx.setEnabled(true);
                                Button_delete_sx.setEnabled(true);
                            }
                            FolderSx = true;
                        }

                    } else {   //file
                        String file_selezionato = Folder_corrente_sx + "/" + entryName;
                        String file_precedente_selezionato = "";
                        if (FileSelezionato_sx != null)
                            file_precedente_selezionato = FileSelezionato_sx.getPath();
                        if (file_selezionato.equals(file_precedente_selezionato)) {  //stesso file di prima, lo deselezioni
                            TolgoSelezioneVisivaUSB();
                            Button_sxdx.setEnabled(false);
                            Button_delete_sx.setEnabled(false);
                            FileSelezionato_sx = null;
                        } else {
                            //selezionato nuovo file
                            FileSelezionato_sx = new File(Folder_corrente_sx + "/" + entryName);
                            SelezionaGrigioUSB(pos);
                            Button_sxdx.setEnabled(true);
                            Button_delete_sx.setEnabled(true);
                        }
                    }
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        });
    }

    /**
     * Button for go to the previous folder on HD
     *
     * @param view
     */
    public void On_click_button_back_dx(View view) {
        if (!SubString.After(Folder_corrente_dx, "/").equals("ricette")) //non faccio scendere sotto ricette
        {
            String Folder_back = SubString.BeforeLast(Folder_corrente_dx, "/");
            Folder_corrente_dx = Folder_back;
            ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
            Button_dxsx.setEnabled(false);
            Button_delete_dx.setEnabled(false);

            TolgoSelezioneVisivaHMI();
        }
    }

    /**
     * Button for go to the previous folder on USB
     *
     * @param view
     */
    public void On_click_button_back_sx(View view) {
        if (!Folder_corrente_sx.equals("")) //non faccio scendere sotto la root
        {
            String Folder_back = SubString.BeforeLast(Folder_corrente_sx, "/");
            Folder_corrente_sx = Folder_back;
            String[] str = Folder_corrente_sx.split("/");
            UsbFile[] files = new UsbFile[0];

            UsbFile root1;
            root1 = root;
            try {
                files = root1.listFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

            ShowList_USB(LvListSX, files, estensione_list_sx);
            Button_sxdx.setEnabled(false);
            Button_delete_sx.setEnabled(false);

            TolgoSelezioneVisivaUSB();
        }
    }

    /**
     * Button for copy file from USB to HD
     *
     * @param view
     * @throws IOException
     */
    public void On_click_Btn_sxdx(View view) throws IOException {
        if (FileSelezionato_sx != null) {
            if (!FileSelezionato_sx.getName().contains(".")) {
                String name = FileSelezionato_sx.getName();

                String[] folder_split = FileSelezionato_sx.getAbsolutePath().split("/");

                UsbFile folder = root;
                for (String str : folder_split) {
                    if (!str.equals("")) {
                        folder = folder.search(str);
                    }
                }

                Utility.copyFileToHMI(folder, new File(Folder_corrente_dx + "/" + folder.getName()), currentFs);
                ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
            } else {
                String extension = FileSelezionato_sx.getPath().substring(FileSelezionato_sx.getPath().lastIndexOf("."));
                if (extension.equalsIgnoreCase(".xml")) {

                    String name = FileSelezionato_sx.getName();
                    String Path_HMI = Folder_corrente_dx + "/" + name;
                    final File[] DestinationLocation = {new File(Path_HMI)};
                    UsbFile file;

                    String[] folder_split = Folder_corrente_sx.split("/");

                    UsbFile folder = root;
                    for (String str : folder_split) {
                        if (!str.equals("")) {
                            folder = folder.search(str);
                        }
                    }

                    file = folder.search(name);

                    boolean esiste = ControlloSeEsiste(LvListDX, FileSelezionato_sx.getName());
                    if (esiste) {
                        // chiamo il messaggio Yes/No per sovrascrivere
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);

                        builder.setTitle("File already exists");
                        builder.setMessage("do you want overwrite existing file?");

                        final UsbFile finalFile = file;
                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                // Premuto Yes
                                try {
                                    Utility.copyFileToHMI(finalFile, new File(Folder_corrente_dx + "/" + finalFile.getName()), currentFs);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Ricetta r = new Ricetta(Values.plcType);
                                try {
                                    r.open(DestinationLocation[0]);
                                    r.exportToUsr(new File(DestinationLocation[0].getPath().replace(FileSelezionato_sx.getName(), FileSelezionato_sx.getName().replace(".xml", ".usr"))));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(Usb_Files_Activity.this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                                }

                                ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);

                                Toast.makeText(getApplicationContext(), "File " + FileSelezionato_sx.getName() + " Copied", Toast.LENGTH_LONG).show();
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

                    } else {
                        try {
                            Utility.copyFileToHMI(file, new File(Folder_corrente_dx + "/" + file.getName()), currentFs);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Ricetta r = new Ricetta(Values.plcType);
                        try {
                            r.open(DestinationLocation[0]);
                            r.exportToUsr(new File(DestinationLocation[0].getPath().replace(FileSelezionato_sx.getName(), FileSelezionato_sx.getName().replace(".xml", ".usr"))));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(Usb_Files_Activity.this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                        }

                        ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);

                        Toast.makeText(getApplicationContext(), "File " + FileSelezionato_sx.getName() + " Copied", Toast.LENGTH_LONG).show();
                    }
                    TolgoSelezioneVisivaUSB();
                    Button_sxdx.setEnabled(false);
                    Button_delete_sx.setEnabled(false);
                } else if (extension.equalsIgnoreCase(".eep")) {
                    // Import a eep file and convert it to xml file
                    try {
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                        alertDialog.setMessage("Convert eep file to xml file?");

                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                AlertDialog.Builder alertdialogname = new AlertDialog.Builder(Usb_Files_Activity.this);

                                alertdialogname.setTitle("File Name");

                                final EditText input = new EditText(getApplicationContext());
                                input.setFocusable(false);
                                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(23)});
                                input.setOnTouchListener(new View.OnTouchListener() {

                                    //	@SuppressLint("ClickableViewAccessibility")
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                            KeyDialog_lettere.Lancia_KeyDialogo_lettere(Usb_Files_Activity.this, input, "");
                                        }

                                        return false;
                                    }
                                });
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                input.setLayoutParams(lp);
                                alertdialogname.setView(input);

                                alertdialogname.setPositiveButton("Convert", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                        try {
                                            UsbFile file;

                                            String name = FileSelezionato_sx.getName();
                                            String Path_HMI = Folder_corrente_dx + "/" + name;
                                            final File[] DestinationLocation = {new File(Path_HMI)};

                                            String[] folder_split = Folder_corrente_sx.split("/");

                                            UsbFile folder = root;
                                            for (String str : folder_split) {
                                                if (!str.equals("")) {
                                                    folder = folder.search(str);
                                                }
                                            }

                                            file = folder.search(name);

                                            File UsrFromEep = EepToXml.ConvertEepToUsr_UsbFile(file);
                                            ArrayList<ArrayList<String>> points = EepToXml.getPointsFromUsr(UsrFromEep);
                                            Ricetta r = new Ricetta(Values.plcType);
                                            r = EepToXml.CreaRicetta(points);

                                            if (r.getStepsCount() > 0) {
                                                File root = Environment.getExternalStorageDirectory();
                                                File dir;

                                                dir = new File(root.getAbsolutePath() + "/ricette");

                                                dir.mkdirs();
                                                File file_xml = new File(dir, input.getText() + ".xml");
                                                File file_usr = new File(dir, input.getText() + ".usr");
                                                try {
                                                    r.save(file_xml);
                                                    try {
                                                        r.exportToUsr(file_usr);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Toast.makeText(getApplicationContext(), "error Usr export ", Toast.LENGTH_SHORT).show();
                                                    }

                                                    ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getApplicationContext(), "error saving xml file ", Toast.LENGTH_SHORT).show();
                                                }
                                                TolgoSelezioneVisivaUSB();
                                                Button_sxdx.setEnabled(false);
                                                Button_delete_sx.setEnabled(false);

                                                Toast.makeText(getApplicationContext(), "File convetred", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "error converting eep file ", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                alertdialogname.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                                alertdialogname.show();
                            }
                        });

                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        alertDialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    /**
     * Button for copy all the files from USB to HD
     *
     * @param view
     */
    public void On_click_Btn_sxdx_all(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Owerwrite All");
        builder.setMessage("All file will be OverWrite, Ok?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> folders = new ArrayList<String>();
                ArrayList<String> listItems = new ArrayList<String>();
                ArrayList<String> files_list = new ArrayList<String>();
                try {
                    String[] folder_split = Folder_corrente_sx.split("/");

                    UsbFile folder = root;
                    for (String str : folder_split) {
                        if (!str.equals("")) {
                            folder = folder.search(str);
                        }
                    }

                    UsbFile[] files = folder.listFiles();

                    for (UsbFile file : files) {

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

                            if (estensione.equalsIgnoreCase(estensione_list_sx.get(0)))
                                files_list.add(file.getName());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

                for (int i = 0; i < files_list.size(); i++) {
                    listItems.add(Folder_corrente_sx + "/" + files_list.get(i));
                }

                for (int i = 0; i < folders.size(); i++) {
                    try {
                        if (CheckXmlinFolder(folders.get(i))) {
                            String name = folders.get(i);
                            UsbFile file;

                            String[] folder_split = Folder_corrente_sx.split("/");

                            UsbFile folder = root;
                            for (String str : folder_split) {
                                if (!str.equals("")) {
                                    folder = folder.search(str);
                                }
                            }

                            file = folder.search(name);

                            Utility.copyFileToHMI(file, new File(Folder_corrente_dx + "/" + file.getName()), currentFs);
                            ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                for (String item : listItems) {
                    try {
                        FileSelezionato_sx = new File(item);
                        String name = FileSelezionato_sx.getName();
                        String Path_HMI = Folder_corrente_dx + "/" + name;
                        File DestinationLocation = new File(Path_HMI);

                        UsbFile file;

                        String[] folder_split = Folder_corrente_sx.split("/");

                        UsbFile folder = root;
                        for (String str : folder_split) {
                            if (!str.equals("")) {
                                folder = folder.search(str);
                            }
                        }

                        file = folder.search(name);

                        Utility.copyFileToHMI(file, new File(Folder_corrente_dx + "/" + file.getName()), currentFs);

                        Ricetta r = new Ricetta(Values.plcType);
                        try {
                            r.open(DestinationLocation);
                            r.exportToUsr(new File(DestinationLocation.getPath().replace(FileSelezionato_sx.getName(), FileSelezionato_sx.getName().replace(".xml", ".usr"))));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(Usb_Files_Activity.this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);

                TolgoSelezioneVisivaUSB();
                Button_sxdx.setEnabled(false);
                Button_delete_sx.setEnabled(false);

                Toast.makeText(Usb_Files_Activity.this, "All file copied", Toast.LENGTH_LONG).show();
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
     * Button for copy file from HD to USB
     *
     * @param view
     */
    public void On_click_Btn_dxsx(View view) {

        if (FileSelezionato_dx != null) {
            boolean esiste = ControlloSeEsiste(LvListSX, FileSelezionato_dx.getName());
            if (esiste) {
                // chiamo il messaggio Yes/No per sovrascrivere
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("File already exists");
                builder.setMessage("do you want overwrite existing file?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String[] folder_split = Folder_corrente_sx.split("/");

                            UsbFile folder = root;
                            for (String str : folder_split) {
                                if (!str.equals("")) {
                                    folder = folder.search(str);
                                }
                            }

                            Utility.copyFileToUsb(FileSelezionato_dx, folder);

                            UsbFile folder_loadfiles = root;
                            for (String str : folder_split) {
                                if (!str.equals("")) {
                                    folder_loadfiles = folder_loadfiles.search(str);
                                }
                            }

                            UsbFile[] files = folder_loadfiles.listFiles();

                            ShowList_USB(LvListSX, files, estensione_list_sx);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Copy file error", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getApplicationContext(), "File " + FileSelezionato_dx.getName() + " Copied", Toast.LENGTH_LONG).show();
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

            } else {

                try {
                    String[] folder_split = Folder_corrente_sx.split("/");

                    UsbFile folder = root;
                    for (String str : folder_split) {
                        if (!str.equals("")) {
                            folder = folder.search(str);
                        }
                    }

                    Utility.copyFileToUsb(FileSelezionato_dx, folder);

                    UsbFile folder_loadfiles = root;
                    for (String str : folder_split) {
                        if (!str.equals("")) {
                            folder_loadfiles = folder_loadfiles.search(str);
                        }
                    }

                    UsbFile[] files = folder_loadfiles.listFiles();
                    ShowList_USB(LvListSX, files, estensione_list_sx);
                    ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Copy file error", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getApplicationContext(), "File " + FileSelezionato_dx.getName() + " Copied", Toast.LENGTH_LONG).show();
            }

            TolgoSelezioneVisivaHMI();
            Button_dxsx.setEnabled(false);
            Button_delete_dx.setEnabled(false);
        }
    }

    /**
     * Button for copy all files on HD to USB
     *
     * @param view
     */
    public void On_click_Btn_dxsx_all(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Owerwrite All");
        builder.setMessage("All file will be OverWrite, Ok?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Premuto Yes
                ArrayList<String> folders = new ArrayList<String>();
                ArrayList<String> listItems = new ArrayList<String>();
                ArrayList<String> files = new ArrayList<String>();
                File[] allEntries = new File(Folder_corrente_dx).listFiles();

                for (int i = 0; i < allEntries.length; i++) {
                    if (allEntries[i].isDirectory()) {
                        folders.add(allEntries[i].getName());
                    } else if (allEntries[i].isFile()) {
                        String name = allEntries[i].getName();
                        String estensione = "";
                        try {
                            estensione = name.substring(name.lastIndexOf(".") + 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (estensione.equalsIgnoreCase(estensione_list_dx))
                            files.add(allEntries[i].getName());
                    }
                }

                Collections.sort(folders, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });

                Collections.sort(files, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });

                listItems.clear();

                for (int i = 0; i < files.size(); i++) {
                    listItems.add(Folder_corrente_dx + "/" + files.get(i));
                }

                for (int i = 0; i < folders.size(); i++) {
                    listItems.add(Folder_corrente_dx + "/" + folders.get(i));
                }

                for (String item : listItems) {
                    FileSelezionato_dx = new File(item);
                    if (FileSelezionato_dx != null) {
                        String name = FileSelezionato_dx.getName();

                        try {
                            String[] folder_split = Folder_corrente_sx.split("/");

                            UsbFile folder = root;
                            for (String str : folder_split) {
                                if (!str.equals("")) {
                                    folder = folder.search(str);
                                }
                            }

                            Utility.copyFileToUsb(FileSelezionato_dx, folder);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Copy file: " + name + " error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                try {
                    String[] folder_split = Folder_corrente_sx.split("/");

                    UsbFile folder_loadfiles = root;
                    for (String str : folder_split) {
                        if (!str.equals("")) {
                            folder_loadfiles = folder_loadfiles.search(str);
                        }
                    }

                    UsbFile[] files1 = folder_loadfiles.listFiles();

                    ShowList_USB(LvListSX, files1, estensione_list_sx);
                    ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                TolgoSelezioneVisivaHMI();
                Button_dxsx.setEnabled(false);
                Button_delete_dx.setEnabled(false);

                Toast.makeText(Usb_Files_Activity.this, "All file copied", Toast.LENGTH_LONG).show();
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
     * Button for delete a file on usb
     *
     * @param view
     */
    public void On_click_Btn_delete_sx(View view) {

        try {
            if ((FileSelezionato_sx != null)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Confirm delete");
                builder.setMessage("Are you sure to delete file?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Premuto Yes
                        try {
                            UsbFile file;
                            String[] folder_split = Folder_corrente_sx.split("/");

                            UsbFile folder = root;
                            for (String str : folder_split) {
                                if (!str.equals("")) {
                                    folder = folder.search(str);
                                }
                            }
                            file = folder.search(FileSelezionato_sx.getName());
                            file.delete();
                            ShowList_USB(LvListSX, folder.listFiles(), estensione_list_sx);
                            TolgoSelezioneVisivaUSB();
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

                Button_delete_sx.setEnabled(false);
                Button_sxdx.setEnabled(false);
            }
        } catch (OutOfMemoryError ome) {
            ome.printStackTrace();
            Toast.makeText(getApplicationContext(), "There is a problem with the USB, connect to the pc and check for errors", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Button for delete a file or folder from HMI
     *
     * @param view
     */
    public void On_click_Btn_delete(View view) {
        int itemscount = LvListDX.getAdapter().getCount();

        if (FileSelezionato_dx != null && itemscount > 1) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm delete");
            builder.setMessage("Are you sure to delete file?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Premuto Yes
                    Utility.deleteRecursive(FileSelezionato_dx);
                    dialog.dismiss();
                    ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);
                    TolgoSelezioneVisivaHMI();
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

            Button_delete_dx.setEnabled(false);
            Button_dxsx.setEnabled(false);
        } else {
            Toast.makeText(getApplicationContext(), "It is not allowed to delete all programs from the memory", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Button for exit
     *
     * @param view
     */
    public void onClick_Button_exit(View view) {
        finish();
    }

    /**
     * Function for check if a USB is connected
     *
     * @return
     */
    private boolean GetUSBConnectionStatus() {
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(getApplicationContext());
        return devices.length > 0;
    }

    /**
     * Function for display the files on the USB
     *
     * @param lvList
     * @param files
     * @param filtro_estensione
     */
    private void ShowList_USB(ListView lvList, UsbFile[] files, ArrayList<String> filtro_estensione) {
        ArrayList<String> folders = new ArrayList<String>();
        ArrayList<String> files_list = new ArrayList<String>();

        for (UsbFile file : files) {
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

                for (String estenzione : filtro_estensione) {
                    if (estensione.equalsIgnoreCase(estenzione)) {
                        if (estensione.equals("XML")) {
                            try {
                                file.setName(file.getName().replace(".XML", ".xml"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        files_list.add(file.getName());
                    }
                }
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

        listItemsUSB.clear();

        for (int i = 0; i < folders.size(); i++) {
            listItemsUSB.add(folders.get(i) + "/");
            listItemsSelectedUSB.add(false);
        }

        for (int i = 0; i < files_list.size(); i++) {
            listItemsUSB.add(files_list.get(i));
            listItemsSelectedUSB.add(false);
        }

        adapterUSB = new ListFileAdapter(Usb_Files_Activity.this, listItemsUSB, listItemsSelectedUSB);

        adapterUSB.notifyDataSetChanged();

        lvList.setAdapter(adapterUSB);
    }

    /**
     * Function for display files on HD
     *
     * @param lvList
     * @param path_folder
     * @param filtro_estensione
     */
    private void ShowList(ListView lvList, String path_folder, String filtro_estensione) {
        ArrayList<String> folders = new ArrayList<String>();
        ArrayList<String> files = new ArrayList<String>();
        File[] allEntries = new File(path_folder).listFiles();
        if (allEntries != null) {

            for (int i = 0; i < allEntries.length; i++) {
                if (allEntries[i].isDirectory()) {
                    folders.add(allEntries[i].getName());
                } else if (allEntries[i].isFile()) {
                    String name = allEntries[i].getName();
                    String estensione = "";
                    try {
                        estensione = name.substring(name.lastIndexOf(".") + 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (estensione.equalsIgnoreCase(filtro_estensione))
                        files.add(allEntries[i].getName());
                }
            }

            Collections.sort(folders, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });

            Collections.sort(files, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });

            listItemsHMI.clear();

            for (int i = 0; i < folders.size(); i++) {
                listItemsHMI.add(folders.get(i) + "/");
                listItemsSelectedHMI.add(false);
            }

            for (int i = 0; i < files.size(); i++) {
                listItemsHMI.add(files.get(i));
                listItemsSelectedHMI.add(false);
            }

            adapterHMI = new ListFileAdapter(Usb_Files_Activity.this, listItemsHMI, listItemsSelectedHMI);

            adapterHMI.notifyDataSetChanged();

            lvList.setAdapter(adapterHMI);
        } else {
            File folder = new File(path_folder);
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
    }

    /**
     * Function for check if a file name exist in the list
     *
     * @param lvList
     * @param filename
     * @return
     */
    private boolean ControlloSeEsiste(ListView lvList, String filename) {
        Adapter lista = lvList.getAdapter();
        for (int i = 0; i < lista.getCount(); i++) {
            String item = (String) lista.getItem(i);
            if (item.equals(filename)) return true;
        }
        return false;
    }

    /**
     * Function for remove the selection from all the HD files
     */
    private void TolgoSelezioneVisivaHMI() {
        for (int j = 0; j < listItemsSelectedHMI.size(); j++)
            listItemsSelectedHMI.set(j, false);

        adapterHMI.notifyDataSetChanged();
    }

    /**
     * Function for deselect a HD file
     *
     * @param pos
     */
    private void SelezionaGrigioHMI(int pos) {
        for (int j = 0; j < listItemsSelectedHMI.size(); j++)
            listItemsSelectedHMI.set(j, false);

        // change the background color of the selected element
        listItemsSelectedHMI.set(pos, true);

        adapterHMI.notifyDataSetChanged();
    }

    /**
     * Function for remove the selection from all the USB files
     */
    private void TolgoSelezioneVisivaUSB() {
        for (int j = 0; j < listItemsSelectedUSB.size(); j++)
            listItemsSelectedUSB.set(j, false);

        adapterUSB.notifyDataSetChanged();
    }

    /**
     * Function for deselect a USB file
     *
     * @param pos
     */
    private void SelezionaGrigioUSB(int pos) {
        for (int j = 0; j < listItemsSelectedUSB.size(); j++)
            listItemsSelectedUSB.set(j, false);

        // change the background color of the selected element
        listItemsSelectedUSB.set(pos, true);

        adapterUSB.notifyDataSetChanged();
    }

    /**
     * Function for check if a folder contains XML files
     *
     * @param folder
     * @return
     * @throws IOException
     */
    private boolean CheckXmlinFolder(String folder) throws IOException {
        String[] folder_split = folder.split("/");

        UsbFile folder1 = root;
        for (String str : folder_split) {
            if (!str.equals("")) {
                folder1 = folder1.search(str);
            }
        }

        UsbFile[] files = folder1.listFiles();

        for (UsbFile file : files) {
            if (file.isDirectory()) {
                if (CheckXmlinFolder(folder1 + "/" + file.getName())) {
                    return true;
                }
            } else {
                String name = file.getName();
                String estensione = "";
                try {
                    estensione = name.substring(name.lastIndexOf(".") + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (estensione.equalsIgnoreCase("xml"))
                    return true;
            }
        }
        return false;
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
     * BroadcastReceiver that handle USB staus
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

                    Button_dxsx_all.setEnabled(true);
                    Button_sxdx_all.setEnabled(true);
                }
            }

            if (ACTION_MEDIA_MOUNTED.equals(action)) {
            }

            if (ACTION_USB_DEVICE_DETACHED.equals(action)) {
                try {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Usb_Files_Activity.this, android.R.layout.simple_list_item_1);
                    adapter.notifyDataSetChanged();
                    LvListSX.setAdapter(adapter);

                    Button_dxsx_all.setEnabled(false);
                    Button_sxdx_all.setEnabled(false);
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

                                ShowList_USB(LvListSX, files, estensione_list_sx);
                                ShowList(LvListDX, Folder_corrente_dx, estensione_list_dx);

                                Log.d("ListRow", "Row ListSx: " + LvListSX.getAdapter().getCount());
                                Log.d("ListRow", "Row ListDx: " + LvListDX.getAdapter().getCount());


                                Button_dxsx_all.setEnabled(true);
                                Button_sxdx_all.setEnabled(true);

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


}

