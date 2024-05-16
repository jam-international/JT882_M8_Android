package com.jam_int.jt882_m8;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class PopUpSelectFolder extends AppCompatActivity {

    /**
     * UI components
     */
    ListView LvList;
    ImageButton IButton_exit;

    /**
     * Bool that indicate if the selected file is a folder or not
     */
    Boolean Folder = false;

    /**
     * Extension of the files to display
     */
    String estensione_list = "";
    /**
     * Default folder
     */
    String Folder_corrente = "storage/emulated/0/ricette";

    /**
     * Selected file
     */
    File FileSelezionato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_selectfolder);

        // Create the ricette folder
        // TODO this is useless because i create it at the start of the app
        if (!new File(Folder_corrente).exists()) {
            new File(Folder_corrente).mkdir();
        }

        IButton_exit = findViewById(R.id.imageButton_exit);
        LvList = findViewById(R.id.LvList);

        Inizializzo_eventi();

        ShowList(LvList, Folder_corrente, estensione_list);
    }

    /**
     * Function for initialize the events
     */
    private void Inizializzo_eventi() {
        // Override the list item click event
        LvList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                final String entryName = (String) adapterView.getItemAtPosition(pos);    //leggo il nome del file o folder premuto

                if (Folder && FileSelezionato.getPath().equals(Folder_corrente + "/" + SubString.Before(entryName, "/"))) {
                    Folder_corrente = Folder_corrente + "/" + SubString.Before(entryName, "/");
                    ShowList(LvList, Folder_corrente, estensione_list);
                    Folder = false;
                } else {
                    FileSelezionato = new File(Folder_corrente + "/" + SubString.Before(entryName, "/"));
                    SelezionaGrigio(adapterView, view);
                    Folder = true;
                }
            }
        });
    }

    /**
     * Function for display the list of folders
     *
     * @param lvList
     * @param path_folder
     * @param filtro_estensione
     */
    private void ShowList(ListView lvList, String path_folder, String filtro_estensione) {
        ArrayAdapter<String> adapter;
        ArrayList<String> folders = new ArrayList<String>();
        ArrayList<String> listItems = new ArrayList<String>();
        ArrayList<String> files = new ArrayList<String>();
        File[] allEntries = new File(path_folder).listFiles();

        // Loop for read file from the folder
        for (int i = 0; i < allEntries.length; i++) {
            // Check if is a file or folder
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

        for (int i = 0; i < folders.size(); i++) {
            listItems.add(folders.get(i) + "/");
        }

        for (int i = 0; i < files.size(); i++) {
            listItems.add(files.get(i));
        }

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        adapter.notifyDataSetChanged();

        lvList.setAdapter(adapter);
    }

    /**
     * Button for go to the up folder
     *
     * @param view
     */
    public void On_click_button_back(View view) {
        // Non faccio scendere sotto ricette
        if (!SubString.After(Folder_corrente, "/").equals("ricette")) //non faccio scendere sotto ricette
        {
            String Folder_back = SubString.BeforeLast(Folder_corrente, "/");
            Folder_corrente = Folder_back;
            ShowList(LvList, Folder_corrente, estensione_list);
            TolgoSelezioneVisiva(LvList);
            Folder = false;
        }
    }

    /**
     * Button for exit
     *
     * @param view
     */
    public void onClick_Button_exit(View view) {
        setResult(0);
        finish();
    }

    /**
     * Button for confirm the selection
     *
     * @param v
     */
    public void On_click_btn_confirm(View v) {
        try {
            if (FileSelezionato != null) {
                Intent intent = getIntent();
                intent.putExtra("FolderPath", FileSelezionato.getPath());
                setResult(1, intent);
                finish();
            } else {
                Intent intent = getIntent();
                intent.putExtra("FolderPath", Folder_corrente);
                setResult(1, intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Button for delete the selection
     *
     * @param v
     */
    public void On_click_btn_delete(View v) {
        try {
            if (FileSelezionato.getName() != null) {
                boolean deleted = FileSelezionato.delete();
                ShowList(LvList, Folder_corrente, estensione_list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        // change the background color of the selected element
        view.setBackgroundColor(Color.RED);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

