package com.jam_int.jt882_m8;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class Statistiche extends Activity {
    /**
     * Indexes
     */
    static final int Produzione = 1;
    static final int SkipStitch = 2;
    /**
     * Lists of information
     * <p>
     * TODO i think is better with a Class
     */
    ArrayList<ArrayList<String>> Lista_gg = new ArrayList<ArrayList<String>>();
    ArrayList<ArrayList<String>> produzione_per_giorni = new ArrayList<ArrayList<String>>();
    ArrayList<String> sfilature_per_gg;
    /**
     * UI components
     */
    LineChart lineChart;
    /**
     * Current chart index
     */
    int TipoVisualizzazione = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistiche);

        // Setup the users list
        ArrayList<String> arraySpinner = new ArrayList<String>();
        Spinner UsersList = findViewById(R.id.UsersSpinner);
        arraySpinner.add(" ");
        arraySpinner.add("ADMIN");

        lineChart = findViewById(R.id.lineChart);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        UsersList.setAdapter(adapter);

        try {
            ArrayList<Users_file.struct_User> Users = Users_file.Leggi_file("storage/emulated/0/JamData/Users.txt");

            for (Users_file.struct_User User : Users) {
                arraySpinner.add(User.Username);
            }

            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            UsersList.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        lineChart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                lineChart.zoom(0, 0, 0, 0);
                return true;
            }
        });

        final File LogFile = new File(Environment.getExternalStorageDirectory() + "/JamData/MachineLog.txt");
        if (LogFile.exists()) {
            findViewById(R.id.button_clear_log).setEnabled(false);
            findViewById(R.id.radioButton_production).setEnabled(false);
            findViewById(R.id.radioButton_skip_stitch).setEnabled(false);
            findViewById(R.id.button30gg).setEnabled(false);
            findViewById(R.id.button7gg).setEnabled(false);
            findViewById(R.id.UsersSpinner).setEnabled(false);
            findViewById(R.id.button_exit).setEnabled(false);
            final Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        try {
                            Lista_gg = get_Lista_giorni(LogFile);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (Lista_gg.size() > 0) {
                            produzione_per_giorni = GetProduzionePerGiorni(Lista_gg);
                            sfilature_per_gg = GetSfilaturePerGiorno(Lista_gg);
                        }
                        Statistiche.this.runOnUiThread(new Runnable() {
                            public void run() {
                                findViewById(R.id.button_clear_log).setEnabled(true);
                                findViewById(R.id.radioButton_production).setEnabled(true);
                                findViewById(R.id.radioButton_skip_stitch).setEnabled(true);
                                findViewById(R.id.button30gg).setEnabled(true);
                                findViewById(R.id.button7gg).setEnabled(true);
                                findViewById(R.id.UsersSpinner).setEnabled(true);
                                findViewById(R.id.button_exit).setEnabled(true);

                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }

        Button ClearLog = findViewById(R.id.button_clear_log);
        ClearLog.setVisibility(View.VISIBLE);
    }

    /**
     * Function for fill the chart with the production information
     *
     * @param produzione_per_giorni
     * @param giorni
     */
    private void Grafico(ArrayList<ArrayList<String>> produzione_per_giorni, int giorni) {
        int cnt = 0;
        ArrayList<Entry> entries = new ArrayList<>();
        final String[] day;
        if (produzione_per_giorni.size() == 1) {
            day = new String[2];
            day[0] = "0";
            ArrayList<String> ultimogiorno = new ArrayList<>();
            ultimogiorno = produzione_per_giorni.get(produzione_per_giorni.size() - 1);
            entries.add(new Entry(0, 0.0f));
            for (String item : ultimogiorno) {
                String[] val = item.split("\\|");
                entries.add(new Entry(1, Float.parseFloat(val[1])));
                day[1] = val[0];
            }
        } else {
            day = new String[giorni + 1];
            try {
                Date now = new Date();
                String Date = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(now);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                Date myDate = dateFormat.parse(Date);

                for (int i = 0; i <= giorni; i++) {
                    ArrayList<String> giorno = new ArrayList<>();

                    Date newDate = new Date(myDate.getTime() - 86400000L * (giorni - i)); // 24 * 60 * 60 * 1000
                    day[cnt] = dateFormat.format(newDate);
                    boolean gg = false;

                    for (int p = 0; p < produzione_per_giorni.size(); p++) {
                        giorno = produzione_per_giorni.get(p);
                        String[] val = giorno.get(0).split("\\|");
                        if (val[0].equals(dateFormat.format(newDate))) {
                            for (String item : giorno) {
                                String[] val1 = item.split("\\|");
                                entries.add(new Entry(cnt, Float.parseFloat(val1[1])));
                                gg = true;
                            }
                        }
                    }
                    if (!gg) {
                        entries.add(new Entry(cnt, 0F));
                    }
                    cnt++;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Customized values");
        dataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        // Controlling X axis
        XAxis xAxis = lineChart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //Customizing x axis value

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return day[(int) value];
            }
        };
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);
        // Controlling right side of y axis
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);

        // Controlling left side of y axis
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        // Setting Data
        LineData data = new LineData(dataSet);
        lineChart.setData(data);
        lineChart.animateX(750);
        //refresh
        lineChart.invalidate();
    }

    /**
     * Function for fill the chart with the sfilature information
     *
     * @param sfilature_per_gg
     * @param giorni
     */
    private void Grafico_sfilature(ArrayList<String> sfilature_per_gg, int giorni) {
        int cnt = 0;
        ArrayList<Entry> entries = new ArrayList<>();
        final String[] day;
        if (sfilature_per_gg.size() == 1) {
            day = new String[2];
            day[0] = "0";
            String ultimogiorno = sfilature_per_gg.get(sfilature_per_gg.size() - 1);

            entries.add(new Entry(0, 0.0f));

            String[] val = ultimogiorno.split("\\|");
            entries.add(new Entry(1, Float.parseFloat(val[0])));
            day[1] = val[1];
        } else {
            day = new String[giorni + 1];
            try {
                Date now = new Date();
                String Date = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(now);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                Date myDate = dateFormat.parse(Date);

                for (int i = 0; i <= giorni; i++) {
                    String giorno = "";

                    Date newDate = new Date(myDate.getTime() - 86400000L * (giorni - i)); // 24 * 60 * 60 * 1000
                    day[cnt] = dateFormat.format(newDate);
                    boolean gg = false;

                    //String[] val = giorno.get(0).split("\\|");
                    for (int p = 0; p < sfilature_per_gg.size(); p++) {
                        giorno = sfilature_per_gg.get(p);
                        String[] val = giorno.split("\\|");
                        if (val[1].equals(dateFormat.format(newDate))) {
                            if (entries.size() > cnt) {
                                String[] val1 = giorno.split("\\|");
                                Entry tmp = entries.get(cnt);
                                tmp.setY(tmp.getY() + Float.parseFloat(val1[0]));
                                entries.set(cnt, tmp);
                                gg = true;
                            } else {
                                String[] val1 = giorno.split("\\|");
                                entries.add(new Entry(cnt, Float.parseFloat(val1[0])));
                                gg = true;
                            }
                        }
                    }
                    if (!gg) {
                        entries.add(new Entry(cnt, 0.0f));
                    }
                    cnt++;

                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Customized values");
        dataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        // Controlling X axis
        XAxis xAxis = lineChart.getXAxis();
        // Set the xAxis position to bottom. Default is top
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //Customizing x axis value

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return day[(int) value];
            }
        };

        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);
        // Controlling right side of y axis
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);

        // Controlling left side of y axis
        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        // Setting Data
        LineData data = new LineData(dataSet);
        lineChart.setData(data);
        lineChart.animateX(750);
        //refresh
        lineChart.invalidate();
    }

    /**
     * Function for get the sfilature for each day
     *
     * @param lista_gg
     * @return
     */
    private ArrayList<String> GetSfilaturePerGiorno(ArrayList<ArrayList<String>> lista_gg) {
        ArrayList<String> sfilature_per_giorni = new ArrayList<String>();
        ArrayList<String> sfilature_per_giorni_tot = new ArrayList<String>();
        ArrayList<ArrayList<String>> sfilature_temp = new ArrayList<ArrayList<String>>();
        String data_giorno = "";
        for (ArrayList<String> giorno : lista_gg) {
            String[] dataGiorno = giorno.get(0).split("\\|");
            data_giorno = dataGiorno[1];
            String Username = " ";
            ArrayList<String> sfilature_gg = new ArrayList<String>();
            for (String item : giorno) {

                if (item.startsWith("5|")) {
                    sfilature_gg.add(item + "|" + data_giorno);
                } else if (item.startsWith("1|")) {
                    String[] str = item.split("\\|");
                    Username = str[str.length - 1];
                    sfilature_gg.add(data_giorno + "|" + Username);
                }
            }
            sfilature_temp.add(sfilature_gg);
        }

        Spinner UsersSpinner = findViewById(R.id.UsersSpinner);
        //String[] str_primo_valore = sfilature_temp.get(0).get(0).split("\\|");
        int sfilature_gg = 0;
        for (ArrayList<String> giorno : sfilature_temp) {

            try {
                String[] str_gg = giorno.get(0).split("\\|");
                for (int i = 1; i < giorno.size(); i++) {
                    if (str_gg[1].equals(UsersSpinner.getSelectedItem().toString()) || UsersSpinner.getSelectedItem().toString().equals(" ")) {
                        sfilature_gg++;
                    }
                }
                String res = sfilature_gg + "|" + str_gg[0];
                sfilature_per_giorni.add(res);
                sfilature_gg = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String[] str_primo_valore = sfilature_per_giorni.get(0).split("\\|");
        int sfilature_gg_tot = 0;
        for (String giorno : sfilature_per_giorni) {
            try {
                String[] str_last_valore = giorno.split("\\|");
                if (str_last_valore[1].equals(str_primo_valore[1])) {
                    sfilature_gg_tot = sfilature_gg_tot + Integer.parseInt(str_last_valore[0]);
                    str_primo_valore = str_last_valore;
                } else {
                    sfilature_per_giorni_tot.add(sfilature_gg_tot + "|" + str_last_valore[1]);
                    sfilature_gg_tot = Integer.parseInt(str_last_valore[0]);
                    str_primo_valore = str_last_valore;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sfilature_per_giorni_tot.add(sfilature_gg_tot + "|" + str_primo_valore[1]);

        return sfilature_per_giorni_tot;
    }

    /**
     * Function for get the production for each day
     *
     * @param lista_gg
     * @return
     */
    private ArrayList<ArrayList<String>> GetProduzionePerGiorni(ArrayList<ArrayList<String>> lista_gg) {
        ArrayList<ArrayList<String>> produzione_per_giorni1 = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> produzione_temp = new ArrayList<ArrayList<String>>();
        String data_giorno = "";
        for (ArrayList<String> giorno : lista_gg) {
            String[] dataGiorno = giorno.get(0).split("\\|");
            data_giorno = dataGiorno[1];
            String Username = " ";
            ArrayList<String> produzione_gg = new ArrayList<String>();
            for (String item : giorno) {
                if (item.startsWith("4|")) {
                    produzione_gg.add(item + "|" + data_giorno + "|" + Username);
                } else if (item.startsWith("1|")) {
                    String[] str = item.split("\\|");
                    Username = str[str.length - 1];
                }
            }
            produzione_temp.add(produzione_gg);
        }

        Spinner UsersSpinner = findViewById(R.id.UsersSpinner);
        String[] str_primo_valore = produzione_temp.get(0).get(0).split("\\|");
        int produzione_gg = 0;
        //int startprod = Integer.parseInt(str_primo_valore[1]);
        for (ArrayList<String> giorno : produzione_temp) {
            try {
                String[] str_last_valore = giorno.get(giorno.size() - 1).split("\\|");
                if (str_primo_valore[3].equals(str_last_valore[3])) {
                    if (str_last_valore[4].equals(UsersSpinner.getSelectedItem().toString()) || UsersSpinner.getSelectedItem().toString().equals(" ")) {
                        produzione_gg += giorno.size() - 1;
                        str_primo_valore = str_last_valore;
                    }
                } else {
                    produzione_per_giorni1.add(new ArrayList<String>(Arrays.asList(str_primo_valore[3] + "|" + produzione_gg)));
                    produzione_gg = 0;
                    str_primo_valore = str_last_valore;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        produzione_per_giorni1.add(new ArrayList<String>(Arrays.asList(str_primo_valore[3] + "|" + produzione_gg)));
        return produzione_per_giorni1;
    }

    /**
     * Function for create the list of days from the log file
     *
     * @param FileLog
     * @return
     * @throws FileNotFoundException
     */
    private ArrayList<ArrayList<String>> get_Lista_giorni(File FileLog) throws FileNotFoundException {
        ArrayList<ArrayList<String>> Lista_gg1 = new ArrayList<ArrayList<String>>();
        BufferedReader reader = new BufferedReader(new FileReader(FileLog));
        try {
            String line;
            {
                ArrayList<String> gg = new ArrayList<String>();
                while ((line = reader.readLine()) != null) {
                    if (!line.equals("")) {
                        String caratteri_iniziali = line.substring(0, 2);

                        if (caratteri_iniziali.equals("1|"))                    //trovato prima data
                        {
                            gg.add(line);
                            String line1;
                            while ((line1 = reader.readLine()) != null) {
                                String caratteri_iniziali2 = line1.substring(0, 2);
                                if (caratteri_iniziali2.equals("1|")) {
                                    Lista_gg1.add(gg);
                                    gg = new ArrayList<String>();
                                    gg.add(line1);
                                } else
                                    gg.add(line1);
                            }
                            Lista_gg1.add(gg);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Lista_gg1;
    }

    /**
     * Function for select the chart to display and the days range
     *
     * @param days
     */
    private void SelectGrafico(int days) {
        switch (TipoVisualizzazione) {
            case 0:
                Toast.makeText(this, "Select variable to show on left", Toast.LENGTH_SHORT).show();
                break;
            case Produzione:
                produzione_per_giorni = GetProduzionePerGiorni(Lista_gg);
                Grafico(produzione_per_giorni, days);
                break;
            case SkipStitch:
                sfilature_per_gg = GetSfilaturePerGiorno(Lista_gg);
                Grafico_sfilature(sfilature_per_gg, days);
                break;
        }
    }

    /**
     * Button for exit from the page
     *
     * @param view
     */
    public void onClick_exit(View view) {
        finish();
    }

    /**
     * Button for delete the log file
     *
     * @param view
     * @throws FileNotFoundException
     */
    public void onClick_cancella_log(View view) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(Environment.getExternalStorageDirectory() + "/JamData/MachineLog.txt");
        writer.print("");
        writer.close();
    }

    /**
     * Radio button for see the last 7 days information
     *
     * @param view
     */
    public void onClick7days(View view) {

        findViewById(R.id.button_clear_log).setEnabled(false);
        findViewById(R.id.radioButton_production).setEnabled(false);
        findViewById(R.id.radioButton_skip_stitch).setEnabled(false);
        findViewById(R.id.button30gg).setEnabled(false);
        findViewById(R.id.button7gg).setEnabled(false);
        findViewById(R.id.UsersSpinner).setEnabled(false);
        findViewById(R.id.button_exit).setEnabled(false);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Statistiche.this.runOnUiThread(new Runnable() {
                        public void run() {
                            SelectGrafico(7);
                            findViewById(R.id.button_clear_log).setEnabled(true);
                            findViewById(R.id.radioButton_production).setEnabled(true);
                            findViewById(R.id.radioButton_skip_stitch).setEnabled(true);
                            findViewById(R.id.button30gg).setEnabled(true);
                            findViewById(R.id.button7gg).setEnabled(true);
                            findViewById(R.id.UsersSpinner).setEnabled(true);
                            findViewById(R.id.button_exit).setEnabled(true);

                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    /**
     * Radio button for see the last 30 days information
     *
     * @param view
     */
    public void onClick30days(View view) {
        findViewById(R.id.button_clear_log).setEnabled(false);
        findViewById(R.id.radioButton_production).setEnabled(false);
        findViewById(R.id.radioButton_skip_stitch).setEnabled(false);
        findViewById(R.id.button30gg).setEnabled(false);
        findViewById(R.id.button7gg).setEnabled(false);
        findViewById(R.id.UsersSpinner).setEnabled(false);
        findViewById(R.id.button_exit).setEnabled(false);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Statistiche.this.runOnUiThread(new Runnable() {
                        public void run() {
                            SelectGrafico(30);
                            findViewById(R.id.button_clear_log).setEnabled(true);
                            findViewById(R.id.radioButton_production).setEnabled(true);
                            findViewById(R.id.radioButton_skip_stitch).setEnabled(true);
                            findViewById(R.id.button30gg).setEnabled(true);
                            findViewById(R.id.button7gg).setEnabled(true);
                            findViewById(R.id.UsersSpinner).setEnabled(true);
                            findViewById(R.id.button_exit).setEnabled(true);

                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    /**
     * Button for set the production chart
     *
     * @param view
     */
    public void onClickProduction(View view) {
        TipoVisualizzazione = Produzione;
    }

    /**
     * Button for set the sfilature chart
     *
     * @param view
     */
    public void onClickSkipStitch(View view) {
        TipoVisualizzazione = SkipStitch;
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
