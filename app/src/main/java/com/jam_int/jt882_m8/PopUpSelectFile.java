package com.jam_int.jt882_m8;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class PopUpSelectFile extends AppCompatActivity {

    /**
     * Timer for update the list
     */
    private final Handler handler = new Handler();
    /**
     * UI components
     */
    ListView LvList;
    ImageButton IButton_exit;
    /**
     * Extension of the files to display
     */
    String estensione_list = "pts";
    /**
     * Default folder of the files
     */
    String Folder_corrente = "storage/emulated/0/JamData/punti";
    /**
     * Selected file
     */
    File FileSelezionato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_selectfile);

        if (!new File(Folder_corrente).exists()) {
            new File(Folder_corrente).mkdir();
        }

        IButton_exit = findViewById(R.id.imageButton_exit);
        LvList = findViewById(R.id.LvList);

        Inizializzo_eventi();

        /**
         * Timer for update the list
         */
        handler.postDelayed(runnable, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (TestUsb(Folder_corrente, Folder_corrente)) {
                ShowList(LvList, Folder_corrente, estensione_list);
                handler.removeCallbacks(runnable);
                return;
            }
            handler.postDelayed(runnable, 1000);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Function for init the ListView event
     */
    private void Inizializzo_eventi() {
        //Evento alla pressione di una riga del List di DX
        LvList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                final String entryName = (String) adapterView.getItemAtPosition(pos);    //leggo il nome del file o folder premuto

                // Check if is a file or a folder
                if (entryName.endsWith("/")) {  //è un file o folder?
                    Folder_corrente = Folder_corrente + "/" + SubString.Before(entryName, "/");
                    ShowList(LvList, Folder_corrente, estensione_list);

                } else {
                    String file_selezionato = Folder_corrente + "/" + entryName;
                    String file_precedente_selezionato = "";
                    if (FileSelezionato != null)
                        file_precedente_selezionato = FileSelezionato.getPath();
                    if (file_selezionato.equals(file_precedente_selezionato)) {
                        //stesso file di prima, lo deselezioni
                        TolgoSelezioneVisiva(adapterView);
                        FileSelezionato = null;
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
     * Function for display the list of pts files
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
     * Button for exit
     *
     * @param view
     */
    public void onClick_Button_exit(View view) throws IOException {
        Values.File = null;
        finish();
    }

    /**
     * Button for confirm the selection
     *
     * @param v
     */
    public void On_click_btn_confirm(View v) {
        try {
            if (FileSelezionato.getName() != null) {
                CheckPts(FileSelezionato);
                Values.File = FileSelezionato.getName();
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Button for delete the selected pts file
     *
     * @param v
     */
    public void On_click_btn_delete(View v) {
        try {
            if (FileSelezionato.getName() != null) {
                FileSelezionato.delete();
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
        view.setBackgroundColor(Color.LTGRAY);
    }

    /**
     * Function for check if the selected pts file is valid
     *
     * @param fileSelezionato
     */
    public void CheckPts(File fileSelezionato) {
        try {
            File file = new File(fileSelezionato.getPath());

            BufferedReader b = new BufferedReader(new FileReader(file));

            String[] readLine = new String[Integer.parseInt("" + file.length())];
            String line;

            int i = 0;
            while ((line = b.readLine()) != null) {
                readLine[i] = line;
                i++;
            }
            b.close();

            if (readLine[0].equals("3") && readLine[1].equals("7")) {
                if (readLine[3].equals(readLine[4]) && readLine[5].equals(readLine[6]) && readLine[7].equals(readLine[8])) {
                    BufferedWriter w = new BufferedWriter(new FileWriter(file));
                    w.write(readLine[0] + "\n");
                    w.write("4" + "\n");
                    w.write(readLine[2] + "\n");
                    w.write(readLine[3] + "\n");
                    w.write(readLine[5] + "\n");
                    w.write(readLine[7] + "\n");
                    w.close();
                }
            } else if (readLine[0].equals("4") && readLine[1].equals("11")) {
                if (readLine[3].equals(readLine[4]) && readLine[7].equals(readLine[8]) && readLine[11].equals(readLine[12])) {
                    BufferedWriter w = new BufferedWriter(new FileWriter(file));
                    w.write(readLine[0] + "\n");
                    w.write("5" + "\n");
                    w.write(readLine[2] + "\n");
                    w.write(readLine[3] + "\n");
                    w.write(readLine[5] + "\n");
                    w.write(readLine[6] + "\n");
                    w.write(readLine[12] + "\n");
                    w.close();
                }
            }else if(readLine[0].equals("2") && readLine[1].equals("8")) {
                if (readLine[3].equals(readLine[4]) && readLine[5].equals(readLine[6]) && readLine[7].equals(readLine[8])
                        && readLine[9].equals(readLine[10]) && readLine[11].equals(readLine[12])) {
                    BufferedWriter w = new BufferedWriter(new FileWriter(file));
                    w.write(readLine[0] + "\n");
                    w.write(readLine[1] + "\n");
                    w.write(readLine[2] + "\n");
                    w.write(readLine[4] + "\n");
                    w.write(readLine[5] + "\n");
                    w.write(readLine[6] + "\n");
                    w.write(readLine[7] + "\n");
                    w.write(readLine[9] + "\n");
                    w.write(readLine[10] + "\n");
                    w.write(readLine[11] + "\n");
                    w.close();
                }

            }else if(readLine[0].equals("4") && readLine[1].equals("8")) {
                if (readLine[3].equals(readLine[4]) && readLine[7].equals(readLine[8])
                        &&  readLine[11].equals(readLine[12])) {
                    BufferedWriter w = new BufferedWriter(new FileWriter(file));
                    w.write(readLine[0] + "\n");
                    w.write(readLine[1] + "\n");
                    w.write(readLine[2] + "\n");
                    w.write(readLine[3] + "\n");
                    w.write(readLine[5] + "\n");
                    w.write(readLine[6] + "\n");
                    w.write(readLine[7] + "\n");
                    w.write(readLine[9] + "\n");
                    w.write(readLine[10] + "\n");
                    w.write(readLine[11] + "\n");
                    w.close();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Function for test to copy a file to usb???
     *
     * @param path_folder
     * @param path_folder1
     * @return
     */
    private boolean TestUsb(String path_folder, String path_folder1) {
        String Path_HMI = path_folder + "/" + "test.txt";
        File destFile = new File(Path_HMI);
        String Path_HMI1 = path_folder1 + "/" + "test.txt";
        File sourceFile = new File(Path_HMI1);

        try {
            sourceFile.createNewFile();
            destFile.createNewFile();

            FileChannel source = null;
            FileChannel destination = null;
            try {
                source = new FileInputStream(sourceFile).getChannel();
                destination = new FileOutputStream(destFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null)
                    source.close();
                if (destination != null)
                    destination.close();
            }
            destFile.delete();
            sourceFile.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), getString(R.string.UsbXmlWait), Toast.LENGTH_SHORT).show();
            return false;
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

