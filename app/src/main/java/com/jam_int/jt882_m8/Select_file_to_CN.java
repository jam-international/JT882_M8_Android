package com.jam_int.jt882_m8;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jamint.ricette.Element;
import com.jamint.ricette.Ricetta;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import communication.MultiCmdItem;
import communication.Protocol;
import communication.ShoppingList;


public class Select_file_to_CN extends Activity {

    /**
     * UI components
     */
    ImageButton IButton_Back, IButton_Confirm, IButton_Exit, IButton_LoadFromUsb;
    // List of available files
    ListView LvListFile;
    ProgressBar Progress_Bar;

    /**
     * Extension of the files to display
     */
    String estensione_file = "XML";
    /**
     * Folder where the files are located
     */
    String Folder_corrente = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ricette";

    /**
     * Selected file
     */
    File FileSelezionato,FileSelezionato_da_quote_r1,FileSelezionato_da_quote_r2;

    // TODO i don't know if this is useless or not
    Intent databack_load_udf = new Intent();

    /**
     * ShoppingList for communicate with PLC
     */
    ShoppingList sl;
    Protocol.OnProgressListener pl;
    int ProgressBar_value = 0;
    boolean Thread_Running = false, StopThread = false;
    Thread thread_SelectFile;
    MultiCmdItem MultiCmd_tasto_verde, MultiCmd_CH1_in_emergenza;
    MultiCmdItem[] mci_array_read_all;

    String operazione = "", Chiamante = "T1_R";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_file);

        // Setup ShoppingList
        sl = SocketHandler.getSocket();
        sl.Clear("Io");
        if (!sl.IsConnected()) {
            sl.Connect();
        }

        databack_load_udf.setData(Uri.parse("NO"));
        setResult(RESULT_OK, databack_load_udf);   //nel caso esco senza caricare...

        IButton_Confirm = findViewById(R.id.imageButton_confirm);
        IButton_Exit = findViewById(R.id.imageButton_exit);
        IButton_Back = findViewById(R.id.imageButton_back);
        IButton_LoadFromUsb = findViewById(R.id.imageButton_LoadFromUsb);

        LvListFile = findViewById(R.id.LvList_file);

        Progress_Bar = findViewById(R.id.progressBarSave);
        Progress_Bar.setMax(100);
        Progress_Bar.getProgressDrawable().setColorFilter(Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);

        IButton_Confirm.setEnabled(false);



        Inizializzo_eventi();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            operazione = extras.getString("operazione");
            Chiamante = extras.getString("Chiamante");
            if (operazione.equals("Loading....")) {
                FileSelezionato = null;
            } else {
                String FilePath_Da_Tasca_Quote_r1 = extras.getString("FilePath_Da_Tasca_Quote_r1");
                String FilePath_Da_Tasca_Quote_r2 = extras.getString("FilePath_Da_Tasca_Quote_r2");
                String filepath = extras.getString("File_path");
                IButton_Confirm.setVisibility(View.GONE);
                IButton_Back.setVisibility(View.GONE);
                IButton_Exit.setVisibility(View.GONE);
                IButton_LoadFromUsb.setVisibility(View.GONE);
                if(filepath != null && !filepath.equals(""))
                    FileSelezionato = new File(filepath);
                if(FilePath_Da_Tasca_Quote_r1 != null && !FilePath_Da_Tasca_Quote_r1.equals(""))
                    FileSelezionato_da_quote_r1 = new File(FilePath_Da_Tasca_Quote_r1);
                if(FilePath_Da_Tasca_Quote_r2 != null && !FilePath_Da_Tasca_Quote_r2.equals(""))
                    FileSelezionato_da_quote_r2 = new File(FilePath_Da_Tasca_Quote_r2);

                InviaFileSelezionato();
            }
        }

        try {
            if (operazione.equals("Loading....")) {
                ShowList(LvListFile, Folder_corrente, estensione_file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "USB memory error", Toast.LENGTH_SHORT).show();
        }

        MultiCmd_tasto_verde = sl.Add("Io", 1, MultiCmdItem.dtDI, 21, MultiCmdItem.dpNONE);
        MultiCmd_CH1_in_emergenza = sl.Add("Io", 1, MultiCmdItem.dtVB, 7909, MultiCmdItem.dpNONE);

        mci_array_read_all = new MultiCmdItem[]{ MultiCmd_CH1_in_emergenza,  MultiCmd_tasto_verde};

        // Start the thread
        if (!Thread_Running) {
            SelectFileThread_Main myTask_SelectFile = new SelectFileThread_Main(this);
            thread_SelectFile = new Thread(myTask_SelectFile, "SelecetFile myTask");
            thread_SelectFile.start();
            Log.d("JAM TAG", "Start SelectFile Thread from OnCreate");
        }
     }
    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
        if (Thread_Running) {
            try {
                Log.d("JAM TAG", "End SelectFile Thread from on Pause");
                KillThread();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override                   //your activity is no longer visible to the user
    public void onStop() {
        super.onStop();
    }

    @Override                   //your activity is no longer visible to the user
    public void onDestroy() {
        super.onDestroy();
    }
    /**
     * Init the ListView event and progress bar event
     */
    private void Inizializzo_eventi() {
        //Evento alla pressione di una riga del List di DX
        LvListFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {

                final String entryName = (String) adapterView.getItemAtPosition(pos);    //leggo il nome del file o folder premuto

                if (entryName.endsWith("/")) {  //è un file o folder?
                    //folder
                    Folder_corrente = Folder_corrente + "/" + SubString.Before(entryName, "/");
                    ShowList(LvListFile, Folder_corrente, estensione_file);
                } else {   //file
                    String file_selezionato = Folder_corrente + "/" + entryName;
                    String file_precedente_selezionato = "";
                    if (FileSelezionato != null)
                        file_precedente_selezionato = FileSelezionato.getPath();
                    if (file_selezionato.equals(file_precedente_selezionato)) {  //stesso file di prima, lo deselezioni
                        TolgoSelezioneVisiva();
                        IButton_Confirm.setEnabled(false);
                        FileSelezionato = null;
                    } else {
                        //selezionato nuovo file
                        FileSelezionato = new File(Folder_corrente + "/" + entryName);
                        SelezionaGrigio(pos);
                        IButton_Confirm.setEnabled(true);
                    }
                }
            }
        });

        pl = new Protocol.OnProgressListener() {
            @Override
            public void onProgressUpdate(int Completion) {
                // Display Progress value (Completion of 100)
                ProgressBar_value = Completion;
                Progress_Bar.setProgress(ProgressBar_value);
            }
        };
    }

    ArrayList<Boolean> listItemsSelected = new ArrayList<Boolean>();
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    /**
     * Function for display the list of files
     *
     * @param lvList
     * @param path_folder
     * @param filtro_estensione
     */
    private void ShowList(ListView lvList, String path_folder, String filtro_estensione) {

        ArrayList<String> folders = new ArrayList<String>();

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
            listItemsSelected.add(false);
        }

        for (int i = 0; i < files.size(); i++) {
            listItems.add(files.get(i));
            listItemsSelected.add(false);
        }

        adapter = new ListFileAdapter(this, listItems, listItemsSelected);
        adapter.notifyDataSetChanged();
        lvList.setAdapter(adapter);

    }

    /**
     * Button for exit from this activity
     *
     * @param view
     */
    public void Onclick_button_Exit(View view) {
        Esci();
    }

    /**
     * Button for confirm and load a Ricetta
     *
     * @param view
     */
    public void onclick_confirm(View view) {
        if (FileSelezionato != null) {
            databack_load_udf.setData(Uri.parse("CARICATO"));
            setResult(RESULT_OK, databack_load_udf);   //indico al prossimo activityResult che ho caricato un udf quindi va riletto
            DisabilitaListView();
            IButton_Back.setVisibility(View.GONE);
            IButton_LoadFromUsb.setVisibility(View.GONE);
            IButton_Exit.setVisibility(View.GONE);
            IButton_Confirm.setVisibility(View.GONE);
            InviaFileSelezionato();
        }
    }

    /**
     * Button for go up on the folders
     *
     * @param v
     */
    public void onclick_back(View v) {
        if (!SubString.After(Folder_corrente, "/").equals("ricette")) //non faccio scendere sotto ricette
        {
            String Folder_back = SubString.BeforeLast(Folder_corrente, "/");
            Folder_corrente = Folder_back;
            ShowList(LvListFile, Folder_corrente, estensione_file);
            TolgoSelezioneVisiva();
        }
    }

    /**
     * Button for load a file from the USB
     * <p>
     * TODO
     *
     * @param view
     */
    public void onclick_load_from_usb(View view) {
        //da fare       Intent page = new Intent(getApplicationContext(), Usb_Files_Activity.class);
        //da fare       startActivity(page);
       // KillThread();
        Intent page = new Intent(getApplicationContext(), Usb_Files_Activity.class);
        startActivityForResult(page, 1);
    }

    /**
     * Function for send a Ricetta to the CN
     */
    private void InviaFileSelezionato() {
        Ricetta ricetta = new Ricetta(Values.plcType);
        try {
            // Open ricetta from file
            if(FileSelezionato != null){
                    ricetta.open(FileSelezionato);
                if (ricetta.elements.size() != 0) {
                    // Create udf FIle
                    String path_xml = FileSelezionato.getPath();
                    String path_udf = path_xml.replace(".xml", ".udf");
                    File fileUdf = new File(path_udf);

                    // Create all the Steps
                    if (ricetta.getPoints(false).size() == 0) {
                        for (Element elem : ricetta.elements)
                            elem.createSteps();

                        ricetta.save(FileSelezionato);
                    }
                    //Da resultActivity non ritorna nessun chiamante quindi è null
                    if (Chiamante == null) {
                        Chiamante = "T1_L";
                    }
                    if (Chiamante.equals("T1_L") || Chiamante.equals("T2_L"))
                        ricetta.numeroRicetta = 2;
                    else ricetta.numeroRicetta = 1;

                    // Export Udf
                    ricetta.exportToUdf(fileUdf);



                    // Send loaded file
                    if (!sl.IsConnected()) {
                        sl.Connect();
                    }
                    if (sl.IsConnected()) {
                        new Scrivi_file_dentro_CN(Select_file_to_CN.this).execute(fileUdf.getAbsolutePath(), fileUdf.getName());

                    } else {
                        Toast.makeText(this,
                                "CN not connected",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "XML is Empty", Toast.LENGTH_SHORT).show();
                    Esci();
                }
            }
            if(FileSelezionato_da_quote_r1 != null){
                ricetta.open(FileSelezionato_da_quote_r1);
                if (ricetta.elements.size() != 0) {
                    // Create udf FIle
                    String path_xml = FileSelezionato_da_quote_r1.getPath();
                    String path_udf = path_xml.replace(".xml", ".udf");
                    File fileUdf = new File(path_udf);

                    // Create all the Steps
                    if (ricetta.getPoints(false).size() == 0) {
                        for (Element elem : ricetta.elements)
                            elem.createSteps();

                        ricetta.save(FileSelezionato_da_quote_r1);
                    }
                    //Da resultActivity non ritorna nessun chiamante quindi è null
                    if (Chiamante == null) {
                        Chiamante = "T1_L";
                    }
                    if (Chiamante.equals("T1_L") || Chiamante.equals("T2_L"))
                        ricetta.numeroRicetta = 2;
                    else ricetta.numeroRicetta = 1;

                    // Export Udf
                    ricetta.exportToUdf(fileUdf);



                    // Send loaded file
                    if (!sl.IsConnected()) {
                        sl.Connect();
                    }
                    if (sl.IsConnected()) {
                        new Scrivi_file_dentro_CN(Select_file_to_CN.this).execute(fileUdf.getAbsolutePath(), fileUdf.getName());

                    } else {
                        Toast.makeText(this,
                                "CN not connected",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "XML is Empty", Toast.LENGTH_SHORT).show();
                    Esci();
                }



            }

            if(FileSelezionato_da_quote_r2 != null)
            {
                ricetta.open(FileSelezionato_da_quote_r2);
                if (ricetta.elements.size() != 0) {
                    // Create udf FIle
                    String path_xml = FileSelezionato_da_quote_r2.getPath();
                    String path_udf = path_xml.replace(".xml", ".udf");
                    File fileUdf = new File(path_udf);

                    // Create all the Steps
                    if (ricetta.getPoints(false).size() == 0) {
                        for (Element elem : ricetta.elements)
                            elem.createSteps();

                        ricetta.save(FileSelezionato_da_quote_r2);
                    }
                    //Da resultActivity non ritorna nessun chiamante quindi è null
                    if (Chiamante == null) {
                        Chiamante = "T1_L";
                    }
                    if (Chiamante.equals("T1_L") || Chiamante.equals("T2_L"))
                        ricetta.numeroRicetta = 2;
                    else ricetta.numeroRicetta = 1;

                    // Export Udf
                    ricetta.exportToUdf(fileUdf);



                    // Send loaded file
                    if (!sl.IsConnected()) {
                        sl.Connect();
                    }
                    if (sl.IsConnected()) {
                        new Scrivi_file_dentro_CN(Select_file_to_CN.this).execute(fileUdf.getAbsolutePath(), fileUdf.getName());

                    } else {
                        Toast.makeText(this,
                                "CN not connected",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "XML is Empty", Toast.LENGTH_SHORT).show();
                    Esci();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            IButton_Exit.setVisibility(View.VISIBLE);
            Toast.makeText(this, "error opening xml file ", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Function for get the row by the position (index)
     *
     * @param position
     * @param listView
     * @return
     */
    public View getViewByPosition(int position, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (position < firstListItemPosition || position > lastListItemPosition) {
            return listView.getAdapter().getView(position, listView.getChildAt(position), listView);
        } else {
            final int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    /**
     * Remove the selection from a row
     *
     *
     */
    private void TolgoSelezioneVisiva() {
        for (int j = 0; j < listItemsSelected.size(); j++) {
            listItemsSelected.set(j, false);
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * Make a row selected
     *
     */
    private void SelezionaGrigio(int posSelectedItem) {
        for (int j = 0; j < listItemsSelected.size(); j++) {
            listItemsSelected.set(j, false);
        }

        listItemsSelected.set(posSelectedItem, true);

        adapter.notifyDataSetChanged();
    }

    /**
     * Function for disable the listview
     * <p>
     * This is for avoid to load 2 Ricettas
     */
    private void DisabilitaListView() {
        if (LvListFile != null) {
            for (int i = 0; i < LvListFile.getCount(); i++) {
                View v = getViewByPosition(i, LvListFile);
                v.setEnabled(false);
            }
        }
    }

    /**
     * Function for activate the listview
     */
    private void AbilitaListView() {
        if (LvListFile != null) {
            for (int i = 0; i < LvListFile.getCount(); i++) {
                View v = getViewByPosition(i, LvListFile);
                v.setEnabled(true);
            }
        }
    }

    /**
     * Function for exit from this activity
     */
    private void Esci() {
        this.finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            ShowList(LvListFile, Folder_corrente, estensione_file);
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
     * Function for handle the emergency button event
     *
     * @param activity
     */
    private void Emergenza(Activity activity) {
        if ((Double) MultiCmd_tasto_verde.getValue() == 0.0d || (Double) MultiCmd_CH1_in_emergenza.getValue() == 1.0d) {
            KillThread();
            Utility.ClearActivitiesTopToEmergencyPage(activity);
        }
    }
    private void KillThread() {

        StopThread = true;

        try {
            if (!Thread_Running)
                thread_SelectFile.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("JAM TAG", "End SelectFile Thread");
    }

    class Scrivi_file_dentro_CN extends AsyncTask<String, Integer, Boolean> {
        private final ProgressDialog dialog;

        public Scrivi_file_dentro_CN(Context context) {
            this.dialog = new ProgressDialog(context);
            this.dialog.setTitle("Title");
            this.dialog.setMessage("Message");
        }

        @Override
        protected Boolean doInBackground(String... params) {

            boolean ret = false, ret1 = false;

            if (FileSelezionato != null ) {
                String Path_fonte = params[0];
                String Path_destinazione = "C:\\cnc\\userdata\\" + params[1];
                ret = sl.FileUpload(Path_destinazione, Path_fonte, null);
                return ret;
            } else {
                if (FileSelezionato_da_quote_r1 != null && FileSelezionato_da_quote_r2 == null) {
                    String Path_fonte = params[0];
                    String Path_destinazione = "C:\\cnc\\userdata\\" + params[1];
                    ret = sl.FileUpload(Path_destinazione, Path_fonte, null);
                    return ret;
                } else {
                    if (FileSelezionato_da_quote_r1 == null && FileSelezionato_da_quote_r2 != null) {

                        String Path_fonte = params[0];
                        String Path_destinazione = "C:\\cnc\\userdata\\" + params[1];
                        ret = sl.FileUpload(Path_destinazione, Path_fonte, null);
                        return ret;
                    }else {
                        if (FileSelezionato_da_quote_r1 != null && FileSelezionato_da_quote_r2 != null) {

                            String Path_fonte = params[0];
                            String Path_fonte_T2 = Path_fonte.replace("_1", "_2");
                            String Path_destinazione = "C:\\cnc\\userdata\\" + params[1];
                            String Path_destinazione_T2 = Path_destinazione.replace("_1", "_2");
                            ret = sl.FileUpload(Path_destinazione, Path_fonte, null);
                            ret1 = sl.FileUpload(Path_destinazione_T2, Path_fonte_T2, null);
                            return ret & ret1;
                        }
                    }
                }

            }
            return ret;
        }


        @Override
        protected void onPostExecute(Boolean r) {
            super.onPostExecute(r);
            this.dialog.dismiss();

            if (r) {
                if (FileSelezionato != null) {
                    switch (Chiamante) {
                        case "T1_R":
                            Values.File_XML_path_R = FileSelezionato.getPath();
                            break;
                        case "T1_L":
                            Values.File_XML_path_L = FileSelezionato.getPath();
                            break;
                        case "T2_R":
                            Values.File_XML_path_T2_R = FileSelezionato.getPath();
                            break;
                        case "T2_L":
                            Values.File_XML_path_T2_L = FileSelezionato.getPath();
                            break;
                        default:
                            break;
                    }
                }
                /*
                case "PAGE_CREA_TASCA":

                            if(Values.Chiamante.equals("CREATO_TASCA_DA_QUOTE_1") || Values.Chiamante.equals("CREATO_TASCA_DA_QUOTE_12")) {
                                try {
                                    Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.txt", "InfoJAM", "LastProgram", null, null, "LastProgram_R", Values.File_XML_path_R, getApplicationContext());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if(Values.Chiamante.equals("CREATO_TASCA_DA_QUOTE_2") || Values.Chiamante.equals("CREATO_TASCA_DA_QUOTE_12")) {
                                try {
                                    Info_file.Scrivi_campo("storage/emulated/0/JamData/info_Jam.txt", "InfoJAM", "LastProgram", null, null, "LastProgram_R_T2", Values.File_XML_path_T2_R, getApplicationContext());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }



                            Values.Chiamante = "CREATO_TASCA_DA_QUOTE";

                 */

                 finish();
            } else {
                Toast.makeText(getApplicationContext(), "Send error", Toast.LENGTH_SHORT).show();

                AbilitaListView();
                IButton_Confirm.setVisibility(View.VISIBLE);
                IButton_Back.setVisibility(View.VISIBLE);
                IButton_Exit.setVisibility(View.VISIBLE);
                IButton_LoadFromUsb.setVisibility(View.VISIBLE);
            }
        }
    }
    class SelectFileThread_Main implements Runnable {
        Activity activity;
        boolean rc_error;

        public SelectFileThread_Main(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            while (true) {
                Thread_Running = true;
                try {
                    Thread.sleep((long) 100d);
                    if (StopThread) {
                        Thread_Running = false;

                        return;
                    }

                    sl.Clear();

                    // ------------------------ RX -------------------------------
                    sl.ReadItems(mci_array_read_all);
                    if (sl.getReturnCode() != 0) {
                        rc_error = true;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "StartSelectFileThread catch", Toast.LENGTH_SHORT).show();
                }

                if (sl.IsConnected()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!rc_error)  Emergenza(activity);
                        }
                    });
                } else
                    sl.Connect();
            }
        }

    }
}
