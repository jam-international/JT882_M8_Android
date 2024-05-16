package com.jam_int.jt882_m8;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Tcp4_0 extends Activity {
    TableLayout tableLayout;
    int cnt_row = 0,cnt_commessa_selezionata=0;
    ArrayList<ArrayList<String>> ListaCommesse = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp4_0);

        tableLayout = findViewById(R.id.tableLayout1);

        tableLayout.removeAllViews();   //pulisco da eventuali righe

        Inizializza_TableRow();
        String Commessa_selezionata ="";
        try {
            Commessa_selezionata = Info_file.Leggi_campo("storage/emulated/0/JamData/Tcp.txt", "InfoJAM", "Tcp", null, null, "TcpNomeCommessa", getApplicationContext());
        } catch (IOException e) {

        }
        Show_Commesse(Commessa_selezionata);



    }

    private void Show_Commesse(String commessa_selezionata) {
        File folder = new File("/storage/emulated/0/JamData/Commesse");
        File[] listOfFiles = folder.listFiles();
        String pacchetto="";
        cnt_row = 0;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                try {
                    String pathFile =  "/storage/emulated/0/JamData/Commesse/"+ file.getName();
                    BufferedReader reader = new BufferedReader(new FileReader(pathFile));
                    String line = reader.readLine();
                    pacchetto =  line;
                    while (line != null) {
                        //System.out.println(line);
                        // read next line
                        line = reader.readLine();
                        pacchetto = pacchetto+"#"+line;
                    }

                    reader.close();
                    String[] dati = pacchetto.split("#");
                    if(dati.length >=6){
                        ArrayList<String> Commessa = new ArrayList<>();
                        Commessa.add(dati[0]);
                        Commessa.add(dati[1]);
                        Commessa.add(dati[2]);
                        Commessa.add(dati[3]);
                        Commessa.add(dati[4]);
                        Commessa.add(dati[5]);
                        ListaCommesse.add(Commessa);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(ListaCommesse.size()>0)
            ScriviRigaCommessa(ListaCommesse,commessa_selezionata);
    }

    private void ScriviRigaCommessa(ArrayList<ArrayList<String>> listaCommesse, String commessa_selezionata) {

        for (ArrayList Commessa: listaCommesse) {
            String st_Commessa =(String)Commessa.get(0);
            String st_Dima = (String)Commessa.get(1);
            String st_Prog_dx =(String)Commessa.get(2);
            String st_Prog_sx = (String)Commessa.get(3);
            String st_Qta = (String)Commessa.get(4);
            String st_Evaso = (String)Commessa.get(5); // FissaLunghezza((String)Commessa.get(5),10);

            TableRow tbrow = new TableRow(this);
            TableRow.LayoutParams tlparams = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT);

            //
            TextView tv0 = new TextView(this);
            tv0.setLayoutParams(tlparams);
            tv0.setText(st_Commessa);
            if(st_Commessa.equals(commessa_selezionata))
                tv0.setTextColor(Color.BLUE);
            else
                tv0.setTextColor(Color.BLACK);
            tv0.setTextSize(15);
            tv0.setGravity(Gravity.LEFT);
            tv0.setPadding(0, 15, 0, 15);        //cambiando il padding del testo all'interno del TextView riesco a aumentare lo spazione tra le righe
            tbrow.addView(tv0);


            TextView tv1 = new TextView(this);
            tv1.setLayoutParams(tlparams);
            tv1.setText(st_Dima);
            if(st_Commessa.equals(commessa_selezionata))
                tv1.setTextColor(Color.BLUE);
            else
                tv1.setTextColor(Color.BLACK);
            tv1.setTextSize(15);
            tv1.setGravity(Gravity.LEFT);
            tv1.setPadding(0, 15, 0, 15);        //cambiando il padding del testo all'interno del TextView riesco a aumentare lo spazione tra le righe
            tbrow.addView(tv1);

            TextView tv2 = new TextView(this);
            tv2.setLayoutParams(tlparams);
            tv2.setText(st_Prog_dx);
            if(st_Commessa.equals(commessa_selezionata))
                tv2.setTextColor(Color.BLUE);
            else
                tv2.setTextColor(Color.BLACK);
            tv2.setTextSize(15);
            tv2.setGravity(Gravity.LEFT);
            tv2.setPadding(0, 15, 0, 15);        //cambiando il padding del testo all'interno del TextView riesco a aumentare lo spazione tra le righe
            tbrow.addView(tv2);

            TextView tv3 = new TextView(this);
            tv3.setLayoutParams(tlparams);
            tv3.setText(st_Prog_sx);
            if(st_Commessa.equals(commessa_selezionata))
                tv3.setTextColor(Color.BLUE);
            else
                tv3.setTextColor(Color.BLACK);
            tv3.setTextSize(15);
            tv3.setGravity(Gravity.LEFT);
            tv3.setPadding(0, 15, 0, 15);        //cambiando il padding del testo all'interno del TextView riesco a aumentare lo spazione tra le righe
            tbrow.addView(tv3);

            TextView tv4 = new TextView(this);
            tv4.setLayoutParams(tlparams);
            tv4.setText(st_Qta);
            if(st_Commessa.equals(commessa_selezionata))
                tv4.setTextColor(Color.BLUE);
            else
                tv4.setTextColor(Color.BLACK);
            tv4.setTextSize(15);
            tv4.setGravity(Gravity.LEFT);
            tv4.setPadding(0, 15, 0, 15);        //cambiando il padding del testo all'interno del TextView riesco a aumentare lo spazione tra le righe
            tbrow.addView(tv4);

            TextView tv5 = new TextView(this);
            tv5.setLayoutParams(tlparams);
            tv5.setText(st_Evaso);
            if(st_Commessa.equals(commessa_selezionata))
                tv5.setTextColor(Color.BLUE);
            else
                tv5.setTextColor(Color.BLACK);
            tv5.setTextSize(15);
            tv5.setGravity(Gravity.LEFT);
            tv5.setPadding(0, 15, 0, 15);        //cambiando il padding del testo all'interno del TextView riesco a aumentare lo spazione tra le righe
            tbrow.addView(tv5);

            tbrow.setClickable(true);
            tbrow.setId(cnt_row++);


            tbrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int t = v.getId();
                    cnt_commessa_selezionata = t;
                    ArrayList<String> Commessa = new ArrayList<>();
                    Commessa = listaCommesse.get(t);
                    tableLayout.removeAllViews();   //pulisco da eventuali righe
                    Inizializza_TableRow();
                    cnt_row = 0;
                    ScriviRigaCommessa(listaCommesse, (String)Commessa.get(0));

                }
            });


            tableLayout.addView(tbrow);
        }
    }

    private void Inizializza_TableRow() {
        TableRow tbrow = new TableRow(this);

        TableRow.LayoutParams tlparams = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT);

        //
        TextView tv0 = new TextView(this);
        tv0.setLayoutParams(tlparams);
        tv0.setText(getString(R.string.Commessa));
        tv0.setTextColor(Color.RED);
        tv0.setTextSize(15);
        tv0.setGravity(Gravity.LEFT);
        tv0.setWidth(160);
        tbrow.addView(tv0);

        //
        TextView tv1 = new TextView(this);
        tv1.setLayoutParams(tlparams);
        tv1.setText(getString(R.string.NumeroDima));
        tv1.setTextColor(Color.RED);
        tv1.setTextSize(15);
        tv1.setGravity(Gravity.LEFT);
        tv1.setWidth(75);
        tbrow.addView(tv1);

        //
        TextView tv2 = new TextView(this);
        tv2.setLayoutParams(tlparams);
        tv2.setText(getString(R.string.ProgDxT1));
        tv2.setTextColor(Color.RED);
        tv2.setTextSize(15);
        tv2.setGravity(Gravity.LEFT);
        tv2.setWidth(160);
        tbrow.addView(tv2);

        //
        TextView tv3 = new TextView(this);
        tv3.setLayoutParams(tlparams);
        tv3.setText(getString(R.string.ProgSxT1));
        tv3.setTextColor(Color.RED);
        tv3.setTextSize(15);
        tv3.setGravity(Gravity.LEFT);
        tv3.setWidth(160);
        tbrow.addView(tv3);

        //
        TextView tv4 = new TextView(this);
        tv4.setLayoutParams(tlparams);
        tv4.setText(getString(R.string.QuantitàCommessa));
        tv4.setTextColor(Color.RED);
        tv4.setTextSize(15);
        tv4.setGravity(Gravity.LEFT);
        tv4.setWidth(75);
        tbrow.addView(tv4);

        //
        TextView tv5 = new TextView(this);
        tv5.setLayoutParams(tlparams);
        tv5.setText(getString(R.string.Evaso));
        tv5.setTextColor(Color.RED);
        tv5.setTextSize(15);
        tv5.setGravity(Gravity.LEFT);
        tv5.setWidth(75);
        tbrow.addView(tv5);



        tableLayout.addView(tbrow);


    }

    /**
     * Button for exit from this activity
     *
     * @param view
     */
    public void onclick_button_Exit(View view) {
        finish();
    }
    /**
     * Button confirm
     *
     * @param view
     */
    public void onclick_button_confirm(View view) throws IOException {
        ArrayList<String> Commessa = new ArrayList<>();
        Commessa = ListaCommesse.get(cnt_commessa_selezionata);
        String nomeCommessa = Commessa.get(0);
        Info_file.Scrivi_campo("storage/emulated/0/JamData/Tcp.txt", "InfoJAM", "Tcp", null, null, "TcpNomeCommessa", nomeCommessa, getApplicationContext());

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
}
