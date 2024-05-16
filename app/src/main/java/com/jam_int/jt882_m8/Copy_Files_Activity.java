package com.jam_int.jt882_m8;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.jamint.ricette.Ricetta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class Copy_Files_Activity extends Activity {

    /**
     * List of on activity result indexes
     */
    final private static int POPUPFOLDER = 104;
    /**
     * List of file row
     */
    ListView LvList;
    /**
     * Button that execute actions on files
     */
    ImageButton Button_copy;
    ImageButton Button_delete;
    ImageButton Button_rename;
    ImageButton Button_move;
    ImageButton IButton_exit;
    Boolean Folder = false;
    /**
     * Extension of files to read
     */
    String estensione_list = "xml";
    /**
     * Folder where the files are located
     */
    String Folder_corrente = "storage/emulated/0/ricette";

    /**
     * Selected file
     */
    File FileSelezionato;

    String Folder2;
    String FileName;

    /**
     * Variables that will contain the files info
     * <p>
     * TODO i can make a class for them
     */
    ArrayAdapter<String> adapter;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayList<Boolean> listItemsSelected = new ArrayList<Boolean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_files);

        // Get the button elements and save them
        Button_delete = findViewById(R.id.btn_delete);
        Button_copy = findViewById(R.id.btn_copy);
        Button_rename = findViewById(R.id.btn_rename);
        Button_move = findViewById(R.id.btn_move);
        IButton_exit = findViewById(R.id.imageButton_exit);
        Button_delete.setEnabled(false);
        Button_copy.setEnabled(false);
        Button_rename.setEnabled(false);
        Button_move.setEnabled(false);
        Button_delete.setImageResource(R.drawable.gomma_disable);
        Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
        Button_rename.setImageResource(R.drawable.ic_rename_disable);
        Button_move.setImageResource(R.drawable.move_file_disable);

        // Get the list element
        LvList = findViewById(R.id.LvList);
        Inizializzo_eventi();
        // Display the list
        ShowList(LvList, Folder_corrente, estensione_list);
    }

    /**
     * Function for display the list of files and folders
     *
     * @param lvList
     * @param path_folder
     * @param filtro_estensione
     */
    private void ShowList(ListView lvList, String path_folder, String filtro_estensione) {
        ArrayList<String> folders = new ArrayList<String>();
        ArrayList<String> files = new ArrayList<String>();
        // Get the file list from a path
        File[] allEntries = new File(path_folder).listFiles();

        // Loop for read all files and folders
        for (int i = 0; i < allEntries.length; i++) {
            // Check if the selected element is a file or a folder
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

        listItems.clear();

        // Init every folder of the list as not selected
        for (int i = 0; i < folders.size(); i++) {
            listItems.add(folders.get(i) + "/");
            listItemsSelected.add(false);
        }

        // Init every file of the list as not selected
        for (int i = 0; i < files.size(); i++) {
            listItems.add(files.get(i));
            listItemsSelected.add(false);
        }

        adapter = new ListFileAdapter(Copy_Files_Activity.this, listItems, listItemsSelected);
        adapter.notifyDataSetChanged();

        lvList.setAdapter(adapter);
    }

    /**
     * Events to override
     */
    private void Inizializzo_eventi() {

        // Override list click event
        LvList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                // Read the name of the file/folder selected
                final String entryName = (String) adapterView.getItemAtPosition(pos);

                // Check if is a file or a folder
                if (entryName.endsWith("/")) {
                    // Folder
                    if (Folder && FileSelezionato.getPath().equals(Folder_corrente + "/" + SubString.Before(entryName, "/"))) {
                        Folder_corrente = Folder_corrente + "/" + SubString.Before(entryName, "/");
                        ShowList(LvList, Folder_corrente, estensione_list);
                        TolgoSelezioneVisiva();
                        Button_copy.setEnabled(false);
                        Button_delete.setEnabled(false);
                        Button_rename.setEnabled(false);
                        Button_move.setEnabled(false);
                        Button_delete.setImageResource(R.drawable.gomma_disable);
                        Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                        Button_rename.setImageResource(R.drawable.ic_rename_disable);
                        Button_move.setImageResource(R.drawable.move_file_disable);
                        Folder = false;
                    } else {
                        String file_selezionato = Folder_corrente + "/" + entryName;
                        String file_precedente_selezionato = "";
                        if (FileSelezionato != null)
                            file_precedente_selezionato = FileSelezionato.getPath();
                        if (file_selezionato.equals(file_precedente_selezionato)) {  //stesso file di prima, lo deselezioni
                            TolgoSelezioneVisiva();
                            Button_copy.setEnabled(false);
                            Button_delete.setEnabled(false);
                            Button_rename.setEnabled(false);
                            Button_move.setEnabled(false);
                            Button_delete.setImageResource(R.drawable.gomma_disable);
                            Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                            Button_rename.setImageResource(R.drawable.ic_rename_disable);
                            Button_move.setImageResource(R.drawable.move_file_disable);
                            FileSelezionato = null;
                        } else {
                            //selezionato nuovo file
                            FileSelezionato = new File(Folder_corrente + "/" + entryName);
                            SelezionaGrigio(pos);
                            Button_copy.setEnabled(true);
                            Button_delete.setEnabled(true);
                            Button_rename.setEnabled(true);
                            Button_move.setEnabled(true);
                            Button_delete.setImageResource(R.drawable.gomma);
                            Button_copy.setImageResource(R.drawable.ic_copy_file);
                            Button_rename.setImageResource(R.drawable.ic_rename);
                            Button_move.setImageResource(R.drawable.move_file);
                        }
                        Folder = true;
                    }
                } else {
                    // File
                    String file_selezionato = Folder_corrente + "/" + entryName;
                    String file_precedente_selezionato = "";
                    if (FileSelezionato != null)
                        file_precedente_selezionato = FileSelezionato.getPath();
                    if (file_selezionato.equals(file_precedente_selezionato)) {  //stesso file di prima, lo deselezioni
                        TolgoSelezioneVisiva();
                        Button_copy.setEnabled(false);
                        Button_delete.setEnabled(false);
                        Button_rename.setEnabled(false);
                        Button_move.setEnabled(false);
                        Button_delete.setImageResource(R.drawable.gomma_disable);
                        Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                        Button_rename.setImageResource(R.drawable.ic_rename_disable);
                        Button_move.setImageResource(R.drawable.move_file_disable);
                        FileSelezionato = null;
                    } else {
                        //selezionato nuovo file
                        FileSelezionato = new File(Folder_corrente + "/" + entryName);
                        SelezionaGrigio(pos);
                        Button_copy.setEnabled(true);
                        String str1 = FileSelezionato.getAbsolutePath();
                        String str2 = Values.File_XML_path_R;
                        if (!str1.equals(str2)) { //controllo se ho cancellato il file carico nel CN
                            Button_delete.setEnabled(true);
                            Button_delete.setImageResource(R.drawable.gomma);
                        } else {
                            Button_delete.setEnabled(false);
                            Button_delete.setImageResource(R.drawable.gomma_disable);
                        }
                        Button_rename.setEnabled(true);
                        Button_move.setEnabled(true);

                        Button_copy.setImageResource(R.drawable.ic_copy_file);
                        Button_rename.setImageResource(R.drawable.ic_rename);
                        Button_move.setImageResource(R.drawable.move_file);
                    }

                    Folder = false;
                }
            }
        });
    }

    //#region ButtonsClick

    /**
     * Button copy click
     *
     * @param view
     */
    public void On_click_btn_copy(View view) {
        if (!FileSelezionato.isDirectory()) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            // Setting Dialog Title
            alertDialog.setTitle(getResources().getString(R.string.Save));

            alertDialog.setMessage(getResources().getString(R.string.FileName_));

            final EditText input = new EditText(this);
            input.setFocusable(false);
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(23)});
            input.setText(FileSelezionato.getName().replace(".xml", ""));
            input.setOnTouchListener(new View.OnTouchListener() {

                //	@SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        KeyDialog_lettere.Lancia_KeyDialogo_lettere(Copy_Files_Activity.this, input, "");
                    }
                    return false;
                }
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);

            alertDialog.setPositiveButton(getResources().getString(R.string.Save),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (FileSelezionato != null) {

                                if (!input.getText().toString().matches("")) {
                                    boolean esiste = ControlloSeEsiste(LvList, input.getText().toString() + ".xml");
                                    if (esiste) {
                                        // chiamo il messaggio Yes/No per sovrascrivere
                                        AlertDialog.Builder builder = new AlertDialog.Builder(Copy_Files_Activity.this);

                                        builder.setTitle("File already exists");
                                        builder.setMessage("do you want overwrite existing file?");

                                        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog, int which) {
                                                // Premuto Yes
                                                File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ricette");

                                                String Path_HMI = dir.getPath() + "/" + input.getText().toString() + ".xml";
                                                File DestinationLocation = new File(Path_HMI);

                                                try {
                                                    Utility.copyFile(FileSelezionato, DestinationLocation);
                                                    Values.File_XML_path_R = DestinationLocation.getPath();

                                                    Ricetta r = new Ricetta(Values.plcType);
                                                    try {
                                                        r.open(FileSelezionato);
                                                        TolgoSelezioneVisiva();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Toast.makeText(Copy_Files_Activity.this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                                                    }
                                                    r.exportToUsr(new File(DestinationLocation.getPath().replace(input.getText().toString() + ".xml", input.getText().toString() + ".usr")));
                                                    ShowList(LvList, Folder_corrente, estensione_list);
                                                    Button_copy.setEnabled(false);
                                                    Button_delete.setEnabled(false);
                                                    Button_rename.setEnabled(false);
                                                    Button_move.setEnabled(false);
                                                    Button_delete.setImageResource(R.drawable.gomma_disable);
                                                    Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                                                    Button_rename.setImageResource(R.drawable.ic_rename_disable);
                                                    Button_move.setImageResource(R.drawable.move_file_disable);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getApplicationContext(), "Copy file error", Toast.LENGTH_SHORT).show();
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
                                    } else {
                                        File dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ricette");
                                        String Path_HMI = dir.getPath() + "/" + input.getText().toString() + ".xml";
                                        File DestinationLocation = new File(Path_HMI);

                                        try {
                                            Utility.copyFile(FileSelezionato, DestinationLocation);
                                            Values.File_XML_path_R = DestinationLocation.getPath();

                                            Ricetta r = new Ricetta(Values.plcType);
                                            try {
                                                r.open(FileSelezionato);
                                                TolgoSelezioneVisiva();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Toast.makeText(Copy_Files_Activity.this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                                            }
                                            r.exportToUsr(new File(DestinationLocation.getPath().replace(input.getText().toString() + ".xml", input.getText().toString() + ".usr")));
                                            ShowList(LvList, Folder_corrente, estensione_list);
                                            Button_copy.setEnabled(false);
                                            Button_delete.setEnabled(false);
                                            Button_rename.setEnabled(false);
                                            Button_move.setEnabled(false);
                                            Button_delete.setImageResource(R.drawable.gomma_disable);
                                            Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                                            Button_rename.setImageResource(R.drawable.ic_rename_disable);
                                            Button_move.setImageResource(R.drawable.move_file_disable);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "Copy file error", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    TolgoSelezioneVisiva();
                                    Button_copy.setEnabled(false);
                                    Button_delete.setEnabled(false);
                                    Button_rename.setEnabled(false);
                                    Button_move.setEnabled(false);
                                    Button_delete.setImageResource(R.drawable.gomma_disable);
                                    Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                                    Button_rename.setImageResource(R.drawable.ic_rename_disable);
                                    Button_move.setImageResource(R.drawable.move_file_disable);
                                } else {
                                    Toast.makeText(Copy_Files_Activity.this, "File name not valid", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
            alertDialog.setNegativeButton(getResources().getString(R.string.Cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.setNeutralButton(getResources().getString(R.string.Folder),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            FileName = input.getText().toString();
                            Intent intent_par = new Intent(getApplicationContext(), PopUpSelectFolder.class);
                            startActivityForResult(intent_par, POPUPFOLDER);
                        }
                    });
            alertDialog.show();
        } else {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            // Setting Dialog Title
            alertDialog.setTitle(getResources().getString(R.string.Save));

            // Setting Dialog Message
            try {
                //Tolgo la parte di storage/emulated/0/ricette
                String newFolder = "";
                String[] str = Folder2.split("/");
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

            final EditText input = new EditText(this);
            input.setFocusable(false);
            input.setText(FileSelezionato.getName());
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(23)});
            input.setOnTouchListener(new View.OnTouchListener() {

                //	@SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        KeyDialog_lettere.Lancia_KeyDialogo_lettere(Copy_Files_Activity.this, input, "");
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
                            dialog.cancel();
                            try {
                                File root = android.os.Environment.getExternalStorageDirectory();
                                File dir1 = new File(root.getAbsolutePath() + "/ricette");
                                File dir;
                                if (Folder2 == null) {
                                    dir = new File(root.getAbsolutePath() + "/ricette");
                                } else {
                                    dir = new File(Folder2);
                                }
                                File dest = new File(dir, input.getText().toString());
                                File file = new File(dir1, FileSelezionato.getName());
                                dir.mkdirs();
                                Utility.copyFile(file, dest);
                                ShowList(LvList, Folder_corrente, estensione_list);
                            } catch (Exception e) {
                                e.printStackTrace();
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

            alertDialog.setNeutralButton(getResources().getString(R.string.Folder),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent_par = new Intent(getApplicationContext(), PopUpSelectFolder.class);
                            startActivityForResult(intent_par, POPUPFOLDER);
                        }
                    });

            AlertDialog d = alertDialog.show();
        }
        Folder = false;
    }

    /**
     * Button for delete a file or folder
     *
     * @param view
     */
    public void On_click_btn_delete(View view) {
        if ((FileSelezionato != null)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Confirm delete");
            builder.setMessage("Are you sure to delete file?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Premuto Yes
                    Utility.deleteRecursive(FileSelezionato);
                    if (!FileSelezionato.isDirectory()) {
                        File file = new File(FileSelezionato.getPath().replace(".xml", ".usr"));
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    dialog.dismiss();
                    ShowList(LvList, Folder_corrente, estensione_list);
                    TolgoSelezioneVisiva();
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

            // Update buttons status
            Button_delete.setEnabled(false);
            Button_copy.setEnabled(false);
            Button_rename.setEnabled(false);
            Button_move.setEnabled(false);
            Button_delete.setImageResource(R.drawable.gomma_disable);
            Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
            Button_rename.setImageResource(R.drawable.ic_rename_disable);
            Button_move.setImageResource(R.drawable.move_file_disable);
            ShowList(LvList, Folder_corrente, estensione_list);
            Folder = false;
        }
    }

    /**
     * Function for rename a file
     *
     * @param view
     */
    public void On_click_btn_rename(View view) {
        if ((FileSelezionato != null)) {
            if (!FileSelezionato.isDirectory()) {
                final Ricetta r = new Ricetta(Values.plcType);

                try {
                    r.open(FileSelezionato);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Copy_Files_Activity.this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                }

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.Save));

                // Setting Dialog Message
                alertDialog.setMessage(getResources().getString(R.string.FileName_));
                final EditText input = new EditText(this);
                input.setFocusable(false);
                int i = FileSelezionato.getName().lastIndexOf('.');
                String name = FileSelezionato.getName().substring(0, i);
                input.setText(name);
                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(23)});
                input.setOnTouchListener(new View.OnTouchListener() {

                    //	@SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            KeyDialog_lettere.Lancia_KeyDialogo_lettere(Copy_Files_Activity.this, input, "");
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
                                dialog.cancel();
                                File root = android.os.Environment.getExternalStorageDirectory();
                                File dir = new File(root.getAbsolutePath() + "/ricette");
                                dir.mkdirs();
                                File file = new File(dir, input.getText().toString() + ".xml");
                                File file1 = new File(dir, input.getText().toString() + ".usr");
                                if (file.exists() || file1.exists()) {
                                    final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(Copy_Files_Activity.this);

                                    // Setting Dialog Title
                                    alertDialog1.setTitle("overWrite");

                                    // Setting Dialog Message
                                    alertDialog1.setMessage("overWrite?");

                                    // Setting Positive "Yes" Button
                                    alertDialog1.setPositiveButton("Yes",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    File root = android.os.Environment.getExternalStorageDirectory();
                                                    File dir = new File(root.getAbsolutePath() + "/ricette");
                                                    dir.mkdirs();
                                                    File file = new File(dir, input.getText().toString() + ".xml");
                                                    File file1 = new File(dir, input.getText().toString() + ".usr");
                                                    try {
                                                        r.save(file);
                                                        try {
                                                            r.exportToUsr(file1);
                                                            Button_copy.setEnabled(false);
                                                            Button_delete.setEnabled(false);
                                                            Button_rename.setEnabled(false);
                                                            Button_move.setEnabled(false);
                                                            Button_delete.setImageResource(R.drawable.gomma_disable);
                                                            Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                                                            Button_rename.setImageResource(R.drawable.ic_rename_disable);
                                                            Button_move.setImageResource(R.drawable.move_file_disable);
                                                            Utility.InviaFileA_Cn(getApplicationContext(), file);

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            Toast.makeText(getApplicationContext(), "error Usr export ", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                        Toast.makeText(getApplicationContext(), "error saving xml file ", Toast.LENGTH_SHORT).show();
                                                    }

                                                    FileSelezionato.delete();
                                                    file = new File(FileSelezionato.getPath().replace(".xml", ".usr"));
                                                    if (file.exists()) {
                                                        file.delete();
                                                    }
                                                    ShowList(LvList, Folder_corrente, estensione_list);
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
                                        r.save(file);
                                        try {
                                            r.exportToUsr(file1);
                                            Button_copy.setEnabled(false);
                                            Button_delete.setEnabled(false);
                                            Button_rename.setEnabled(false);
                                            Button_move.setEnabled(false);
                                            Button_delete.setImageResource(R.drawable.gomma_disable);
                                            Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                                            Button_rename.setImageResource(R.drawable.ic_rename_disable);
                                            Button_move.setImageResource(R.drawable.move_file_disable);
                                            Utility.InviaFileA_Cn(getApplicationContext(), file);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "error Usr export ", Toast.LENGTH_SHORT).show();
                                        }

                                        FileSelezionato.delete();
                                        file = new File(FileSelezionato.getPath().replace(".xml", ".usr"));
                                        if (file.exists()) {
                                            file.delete();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(), "error saving xml file ", Toast.LENGTH_SHORT).show();
                                    }
                                    ShowList(LvList, Folder_corrente, estensione_list);
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

                AlertDialog d = alertDialog.show();
            } else {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

                // Setting Dialog Title
                alertDialog.setTitle(getResources().getString(R.string.Save));

                // Setting Dialog Message
                alertDialog.setMessage("Folder Name:");
                final EditText input = new EditText(this);
                input.setFocusable(false);
                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(23)});
                input.setText(FileSelezionato.getName());
                input.setOnTouchListener(new View.OnTouchListener() {

                    //	@SuppressLint("ClickableViewAccessibility")
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            KeyDialog_lettere.Lancia_KeyDialogo_lettere(Copy_Files_Activity.this, input, "");
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
                                dialog.cancel();
                                try {
                                    File root = android.os.Environment.getExternalStorageDirectory();
                                    File dir = new File(root.getAbsolutePath() + "/ricette");
                                    File dest = new File(dir, input.getText().toString());
                                    File file = new File(dir, FileSelezionato.getName());
                                    dir.mkdirs();
                                    Utility.copyFile(file, dest);
                                    Utility.deleteRecursive(FileSelezionato);
                                    ShowList(LvList, Folder_corrente, estensione_list);
                                } catch (Exception e) {
                                    e.printStackTrace();
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

                AlertDialog d = alertDialog.show();
            }
            Folder = false;
        }
    }

    /**
     * Button for go up in folder
     *
     * @param view
     */
    public void On_click_button_back(View view) {
        if (!SubString.After(Folder_corrente, "/").equals("ricette")) //non faccio scendere sotto ricette
        {
            String Folder_back = SubString.BeforeLast(Folder_corrente, "/");
            Folder_corrente = Folder_back;
            ShowList(LvList, Folder_corrente, estensione_list);
            Button_delete.setEnabled(false);
            Button_rename.setEnabled(false);
            Button_copy.setEnabled(false);
            Button_move.setEnabled(false);
            Button_delete.setImageResource(R.drawable.gomma_disable);
            Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
            Button_rename.setImageResource(R.drawable.ic_rename_disable);
            Button_move.setImageResource(R.drawable.move_file_disable);

            TolgoSelezioneVisiva();
            Folder = false;
        }
    }

    /**
     * Button for move a file to a different path
     *
     * @param view
     */
    public void On_click_button_move(View view) {
        //Parte il PopUp per selezionare la cartella
        Intent intent_par = new Intent(getApplicationContext(), PopUpSelectFolder.class);
        startActivityForResult(intent_par, 1);
        //Parte la fase di copia/cancella
    }

    /**
     * Button for create a new folder
     *
     * @param view
     */
    public void On_click_btn_createfolder(View view) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Create Folder");

        // Setting Dialog Message
        alertDialog.setMessage("Folder Name:");
        final EditText input = new EditText(this);
        input.setFocusable(false);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(23)});
        input.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog_lettere.Lancia_KeyDialogo_lettere(Copy_Files_Activity.this, input, "");
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
        alertDialog.setPositiveButton("Create",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        try {
                            File Folder = new File(Folder_corrente, input.getText().toString());
                            Folder.mkdir();
                            ShowList(LvList, Folder_corrente, estensione_list);
                        } catch (Exception e) {
                            e.printStackTrace();
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
        AlertDialog d = alertDialog.show();
    }

    /**
     * Button for open info pdf
     *
     * @param view
     */
    public void on_click_Button_Info(View view) {
        /*
        Intent intent_par = new Intent(getApplicationContext(), PopUpPdf.class);
        intent_par.putExtra("FileName", "pagina_files.pdf");
        startActivity(intent_par);
        */
    }

    /**
     * Button for exit
     *
     * @param view
     */
    public void onClick_Button_exit(View view) {
        finish();
    }
    //#endregion ButtonsClick

    /**
     * Function for remove selection from all rows
     */
    private void TolgoSelezioneVisiva() {
        for (int j = 0; j < listItemsSelected.size(); j++) {
            listItemsSelected.set(j, false);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Function for deselect a row
     *
     * @param posSelectedItem
     */
    private void SelezionaGrigio(int posSelectedItem) {
        for (int j = 0; j < listItemsSelected.size(); j++) {
            listItemsSelected.set(j, false);
        }

        listItemsSelected.set(posSelectedItem, true);

        adapter.notifyDataSetChanged();
    }

    /**
     * Function for check if a name exist in the list
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode != 0) {
                String Folder = data.getExtras().getString("FolderPath");
                if (!Folder.equals(FileSelezionato.getPath())) {
                    File f = new File(Folder + "/" + FileSelezionato.getName());
                    try {
                        Utility.copyFile(FileSelezionato, f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Utility.deleteRecursive(FileSelezionato);
                    ShowList(LvList, Folder_corrente, estensione_list);
                }
                if (!Folder.equals(FileSelezionato.getPath())) {
                    File fnew = new File(Folder + "/" + FileSelezionato.getName().replace(".xml", ".usr"));
                    File fold = new File(FileSelezionato.getPath().replace(FileSelezionato.getName(), FileSelezionato.getName().replace(".xml", ".usr")));
                    try {
                        Utility.copyFile(fold, fnew);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Utility.deleteRecursive(fold);
                    ShowList(LvList, Folder_corrente, estensione_list);
                }
                File f = new File(Folder + "/" + FileSelezionato.getName());
                Utility.InviaFileA_Cn(this, f);
            }
        } else if (requestCode == POPUPFOLDER) {
            if (resultCode != 0) {
                Folder2 = data.getExtras().getString("FolderPath");
                try {
                    if (FileSelezionato != null) {

                        if (!FileName.matches("")) {
                            boolean esiste = ControlloSeEsiste(LvList, FileName + ".xml");
                            if (esiste) {
                                // chiamo il messaggio Yes/No per sovrascrivere
                                AlertDialog.Builder builder = new AlertDialog.Builder(Copy_Files_Activity.this);

                                builder.setTitle("File already exists");
                                builder.setMessage("do you want overwrite existing file?");

                                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        // Premuto Yes
                                        File dir;
                                        if (Folder2 == null) {
                                            dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ricette");
                                        } else {
                                            dir = new File(Folder2);
                                        }

                                        String Path_HMI = dir.getPath() + "/" + FileName + ".xml";
                                        File DestinationLocation = new File(Path_HMI);

                                        try {
                                            Utility.copyFile(FileSelezionato, DestinationLocation);
                                            Values.File_XML_path_R = DestinationLocation.getPath();

                                            Ricetta r = new Ricetta(Values.plcType);
                                            try {
                                                r.open(FileSelezionato);
                                                TolgoSelezioneVisiva();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                Toast.makeText(Copy_Files_Activity.this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                                            }
                                            r.exportToUsr(new File(DestinationLocation.getPath().replace(FileName + ".xml", FileName + ".usr")));
                                            ShowList(LvList, Folder_corrente, estensione_list);
                                            Button_copy.setEnabled(false);
                                            Button_delete.setEnabled(false);
                                            Button_rename.setEnabled(false);
                                            Button_move.setEnabled(false);
                                            Button_delete.setImageResource(R.drawable.gomma_disable);
                                            Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                                            Button_rename.setImageResource(R.drawable.ic_rename_disable);
                                            Button_move.setImageResource(R.drawable.move_file_disable);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getApplicationContext(), "Copy file error", Toast.LENGTH_SHORT).show();
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
                            } else {
                                File dir;
                                if (Folder2 == null) {
                                    dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ricette");
                                } else {
                                    dir = new File(Folder2);
                                }
                                String Path_HMI = dir.getPath() + "/" + FileName + ".xml";
                                File DestinationLocation = new File(Path_HMI);

                                try {
                                    Utility.copyFile(FileSelezionato, DestinationLocation);
                                    Values.File_XML_path_R = DestinationLocation.getPath();

                                    Ricetta r = new Ricetta(Values.plcType);
                                    try {
                                        r.open(FileSelezionato);
                                        TolgoSelezioneVisiva();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(Copy_Files_Activity.this, "error opening xml file ", Toast.LENGTH_SHORT).show();
                                    }
                                    r.exportToUsr(new File(DestinationLocation.getPath().replace(FileName + ".xml", FileName + ".usr")));
                                    ShowList(LvList, Folder_corrente, estensione_list);
                                    Button_copy.setEnabled(false);
                                    Button_delete.setEnabled(false);
                                    Button_rename.setEnabled(false);
                                    Button_move.setEnabled(false);
                                    Button_delete.setImageResource(R.drawable.gomma_disable);
                                    Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                                    Button_rename.setImageResource(R.drawable.ic_rename_disable);
                                    Button_move.setImageResource(R.drawable.move_file_disable);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), "Copy file error", Toast.LENGTH_SHORT).show();
                                }
                            }
                            TolgoSelezioneVisiva();
                            Button_copy.setEnabled(false);
                            Button_delete.setEnabled(false);
                            Button_rename.setEnabled(false);
                            Button_move.setEnabled(false);
                            Button_delete.setImageResource(R.drawable.gomma_disable);
                            Button_copy.setImageResource(R.drawable.ic_copy_file_disable);
                            Button_rename.setImageResource(R.drawable.ic_rename_disable);
                            Button_move.setImageResource(R.drawable.move_file_disable);
                        }
                    } else {
                        Toast.makeText(this, "File name not valid", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
}

