package com.jam_int.jt882_m8;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.jamint.ricette.CenterPointRadius;
import com.jamint.ricette.MathGeoTri;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class FDrawActivity extends AppCompatActivity {

    /**
     * Bitmap where the draw is displayed
     */
    public Bitmap bmp;

    /**
     * List that contains the values on LastValues file
     */
    ArrayList<String> fileStr = new ArrayList<String>();

    /**
     * Path of the file that contains the last used values
     */
    String pathLastValuesFile = Environment.getExternalStorageDirectory() + "/JamData/LastValues.txt";

    /**
     * Bool that indicate if i need to update the canvas or go to next activity
     */
    boolean Draw;
    Thread_LoopEmergenza thread_LoopEmergenza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fdraw);
        Log.d("JAM TAG", "FDrawActivity");
        Draw = false;

        // Read the last values from a file
        String linea = "";
        try {
            /*ar_1_sch*/
            File lastvalues = new File(Environment.getExternalStorageDirectory() + "/JamData/LastValues.txt");
            if (!lastvalues.exists()) {
                lastvalues.createNewFile();
                CompilaLastValueFile(lastvalues,"0","0","0","0","0","128","128","160","63","63","0",
                        "145","0","0","0","0","0","0");
            }
            /*ar_2_sch*/
            File lastvalues1 = new File(Environment.getExternalStorageDirectory() + "/JamData/LastValues1.txt");
            if (!lastvalues1.exists()) {
                lastvalues1.createNewFile();
                CompilaLastValueFile(lastvalues1,"1","0","0","0","0","128","128","160","63","63","0",
                        "145","0","0","0","0","0","0");
            }
            /*q_1_sch*/
            File lastvalues2 = new File(Environment.getExternalStorageDirectory() + "/JamData/LastValues2.txt");
            if (!lastvalues2.exists()) {
                lastvalues2.createNewFile();
                CompilaLastValueFile(lastvalues2,"2","0","0","0","0","128","128","160","63","63","0",
                        "145","0","0","0","0","0","0");
            }
            /*qa_1_sch*/
            File lastvalues3 = new File(Environment.getExternalStorageDirectory() + "/JamData/LastValues3.txt");
            if (!lastvalues3.exists()) {
                lastvalues3.createNewFile();
                CompilaLastValueFile(lastvalues3,"3","20","20","0","0","128","128","160","63","63","0",
                        "145","0","0","0","0","0","0");
            }
            /*qa_2_sch*/
            File lastvalues4 = new File(Environment.getExternalStorageDirectory() + "/JamData/LastValues4.txt");
            if (!lastvalues4.exists()) {
                lastvalues4.createNewFile();
                CompilaLastValueFile(lastvalues4,"4","20","20","0","0","128","128","160","63","63","0",
                        "145","0","0","0","0","0","0");
            }
            /*qs_1_sch*/
            File lastvalues5 = new File(Environment.getExternalStorageDirectory() + "/JamData/LastValues5.txt");
            if (!lastvalues5.exists()) {
                lastvalues5.createNewFile();
                CompilaLastValueFile(lastvalues5,"5","0","0","0","0","20","20","160","20","20","0",
                        "145","0","0","0","0","0","0");
            }
            /*ar3_1_sch*/
            File lastvalues6 = new File(Environment.getExternalStorageDirectory() + "/JamData/LastValues6.txt");
            if (!lastvalues6.exists()) {
                lastvalues6.createNewFile();
                CompilaLastValueFile(lastvalues6,"6","0","0","0","0","100","100","160","60","60","0",
                        "145","120","120","0","0","50","50");
            }

            /*leggo i valori precedenti di ar_1_sch*/
            BufferedReader br = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory() + "/JamData/LastValues.txt"));
            while ((linea = br.readLine()) != null) {
                fileStr.add(linea);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /** Setup all the events on the EditTexts for get if the text as been changed and the KeyDialog as input method **/
        final EditText eT_M1 = findViewById(R.id.editText_M1);
        final EditText eT_M2 = findViewById(R.id.editText_M2);
        final EditText eT_H1 = findViewById(R.id.editText_H1);
        final EditText eT_H2 = findViewById(R.id.editText_H2);
        final EditText eT_H3 = findViewById(R.id.editText_H3);
        final EditText eT_H4 = findViewById(R.id.editText_H4);
        final EditText eT_L = findViewById(R.id.editText_L);
        final EditText eT_S2 = findViewById(R.id.editText_S2);
        final EditText eT_S1 = findViewById(R.id.editText_S1);
        final EditText eT_S3 = findViewById(R.id.editText_S3);
        final EditText eT_S4 = findViewById(R.id.editText_S4);
        final EditText eT_P = findViewById(R.id.editText_P);
        final EditText eT_H = findViewById(R.id.editText_H);
        final EditText eT_M3 = findViewById(R.id.editText_M3);
        final EditText eT_M4 = findViewById(R.id.editText_M4);
        final EditText eT_M5 = findViewById(R.id.editText_M5);
        final EditText eT_M6 = findViewById(R.id.editText_M6);

        eT_M1.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Draw = false;
            }
        });
        eT_M1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FDrawActivity.this, eT_M1, 50, 0, true, false, 0, false, "");
                }
                return false;
            }
        });

        eT_M2.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Draw = false;
            }
        });
        eT_M2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FDrawActivity.this, eT_M2, 50, 0, true, false, 0, false, "");
                }
                return false;
            }
        });

        eT_H2.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Draw = false;
            }
        });
        eT_H2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FDrawActivity.this, eT_H2, 400, 0, true, false, 128, false, "");
                }
                return false;
            }
        });

        eT_H1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Draw = false;
            }
        });
        eT_H1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FDrawActivity.this, eT_H1, 400, 0, true, false, 128, false, "");
                }
                return false;
            }
        });

        eT_H3.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Draw = false;
            }
        });
        eT_H3.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FDrawActivity.this, eT_H3, 400, 0, true, false, 128, false, "");
                }
                return false;
            }
        });

        eT_H4.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Draw = false;
            }
        });
        eT_H4.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FDrawActivity.this, eT_H4, 400, 0, true, false, 128, false, "");
                }
                return false;
            }
        });

        eT_L.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Draw = false;
            }
        });
        eT_L.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FDrawActivity.this, eT_L, 400, 0, true, false, 160, false, "");
                }
                return false;
            }
        });

        eT_S2.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Draw = false;
            }
        });
        eT_S2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FDrawActivity.this, eT_S2, 200, 0, true, false, 63, false, "");
                }
                return false;
            }
        });

        eT_S1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Draw = false;
            }
        });
        eT_S1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FDrawActivity.this, eT_S1, 200, 0, true, false, 63, false, "");
                }
                return false;
            }
        });

        eT_S3.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Draw = false;
            }
        });
        eT_S3.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FDrawActivity.this, eT_S3, 200, 0, true, false, 63, false, "");
                }
                return false;
            }
        });

        eT_S4.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Draw = false;
            }
        });
        eT_S4.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FDrawActivity.this, eT_S4, 200, 0, true, false, 63, false, "");
                }
                return false;
            }
        });

        eT_P.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Draw = false;
            }
        });
        eT_P.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FDrawActivity.this, eT_P, 50, -50, true, true, 0, false, "");
                }
                return false;
            }
        });

        eT_H.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Draw = false;
            }
        });
        eT_H.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FDrawActivity.this, eT_H, 400, 0, true, false, 155, false, "");
                }
                return false;
            }
        });

        eT_M3.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Draw = false;
            }
        });
        eT_M3.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FDrawActivity.this, eT_M3, 50, 0, true, false, 0, false, "");
                }
                return false;
            }
        });

        eT_M4.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Draw = false;
            }
        });
        eT_M4.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FDrawActivity.this, eT_M4, 50, 0, true, false, 0, false, "");
                }
                return false;
            }
        });

        eT_M5.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Draw = false;
            }
        });
        eT_M5.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FDrawActivity.this, eT_M5, 50, 0, true, false, 0, false, "");
                }
                return false;
            }
        });

        eT_M6.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Draw = false;
            }
        });
        eT_M6.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FDrawActivity.this, eT_M6, 50, 0, true, false, 0, false, "");
                }
                return false;
            }
        });

        // Check the image for chose the values to display
        CheckLoadValues();
  //      LoadLastValues();
        ImageCheck();


        thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
        thread_LoopEmergenza.thread_LoopEmergenza_Start(this);
    }

    /**
     * @param file
     * @param tipo
     * @param M1
     * @param M2
     * @param M3
     * @param M4
     * @param H2
     * @param H1
     * @param L
     * @param S2
     * @param S1
     * @param P
     * @param Hf
     * @param H3
     * @param H4
     * @param M5
     * @param M6
     * @param S3
     * @param S4
     */
    private void CompilaLastValueFile(File file, String tipo, String M1, String M2, String M3, String M4, String H2, String H1,
                                      String L, String S2, String S1, String P, String Hf, String H3, String H4, String M5, String M6, String S3, String S4) {

        try {
            File root = new File(Environment.getExternalStorageDirectory(), "JamData");
            if (!root.exists()) {
                root.mkdirs();
            }

            BufferedWriter out = new BufferedWriter(new FileWriter(file.toString()));

            out.write(tipo + "\n");
            out.write(M1 + "\n");
            out.write(M2 + "\n");
            out.write(M3 + "\n");
            out.write(M4 + "\n");
            out.write(H2 + "\n");
            out.write(H1 + "\n");
            out.write(L + "\n");
            out.write(S2 + "\n");
            out.write(S1 + "\n");
            out.write(P + "\n");
            out.write(Hf + "\n");
            out.write(H3 + "\n");
            out.write(H4 + "\n");
            out.write(M5 + "\n");
            out.write(M6 + "\n");
            out.write(S3 + "\n");
            out.write(S4 + "\n");

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
        try {
            thread_LoopEmergenza.KillThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        if(!thread_LoopEmergenza.getThreadStatus()){
            thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
            thread_LoopEmergenza.thread_LoopEmergenza_Start(this);
            Log.d("JAM TAG", "FDrawActivity");

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
     * Button for go to the next step.
     * If the draw is not initialized it will draw the pocket with the chose values
     * Otherwise it will go to the next page
     *
     * @param v
     */
    public void BtnNext(View v) {
        // If for check if i need to draw the pocket or if i can go to the next page
          if (Draw == false) {
            BtnDraw();
            Draw = BtnDraw();


        } else {
            // List of pts points
            ArrayList<String> points_list = new ArrayList<String>();

            EditText eT_H2 = findViewById(R.id.editText_H2);
            EditText eT_H1 = findViewById(R.id.editText_H1);
            EditText eT_H3 = findViewById(R.id.editText_H3);
            EditText eT_H4 = findViewById(R.id.editText_H4);
            EditText eT_L = findViewById(R.id.editText_L);
            EditText eT_S2 = findViewById(R.id.editText_S2);
            EditText eT_S1 = findViewById(R.id.editText_S1);
            EditText eT_S3 = findViewById(R.id.editText_S3);
            EditText eT_S4 = findViewById(R.id.editText_S4);
            EditText eT_P = findViewById(R.id.editText_P);
            EditText eT_H = findViewById(R.id.editText_H);
            EditText eT_M1 = findViewById(R.id.editText_M1);
            EditText eT_M2 = findViewById(R.id.editText_M2);
            EditText eT_M3 = findViewById(R.id.editText_M3);
            EditText eT_M4 = findViewById(R.id.editText_M4);
            EditText eT_M5 = findViewById(R.id.editText_M5);
            EditText eT_M6 = findViewById(R.id.editText_M6);

            String image = GetImageName();

            //region SendValue
            float startx = 163f;
            float starty = -1.2F;
            if (image.equals("ar_1_sch")) {

                Values.M1 = Float.parseFloat(eT_M1.getText().toString());
                Values.M2 = Float.parseFloat(eT_M2.getText().toString());
                Values.M3 = Float.parseFloat(eT_M3.getText().toString());
                Values.M4 = Float.parseFloat(eT_M4.getText().toString());
                Values.H2 = Float.parseFloat(eT_H2.getText().toString());
                Values.H1 = Float.parseFloat(eT_H1.getText().toString());
                Values.L = Float.parseFloat(eT_L.getText().toString());
                Values.S2 = Float.parseFloat(eT_S2.getText().toString());
                Values.S1 = Float.parseFloat(eT_S1.getText().toString());
                Values.P = Float.parseFloat(eT_P.getText().toString());
                Values.Hf = Float.parseFloat(eT_H.getText().toString());

                points_list.clear();

                float x = startx - Values.L / 2;
                float y = starty;
                points_list.add("" + x + "," + y);

                float Sagitta = Values.M1;

                if (Sagitta <= 1) {
                    x = startx - Values.S1;
                    y = starty + Values.H1;
                    points_list.add("" + x + "," + y);
                } else {
                    float Lunghezza_corda = MathGeoTri.Distance(startx - Values.L / 2, starty, startx - Values.S1, starty + Values.H1);
                    float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

                    PointF Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx - Values.L / 2, starty), new PointF(startx - Values.S1, starty + Values.H1), raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

                    PointF p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx - Values.L / 2, starty), new PointF(startx - Values.S1, starty + Values.H1), raggio, Centro_arco, true);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);
                }

                x = startx - Values.S1;
                y = starty + Values.H1;
                points_list.add("" + x + "," + y);

                Sagitta = Values.M3;

                if (Sagitta <= 1) {
                    x = startx - Values.P;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);
                } else {
                    float Lunghezza_corda = MathGeoTri.Distance(startx - Values.S1, starty + Values.H1, startx - Values.P, starty + Values.Hf);
                    float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

                    PointF Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx - Values.S1, starty + Values.H1), new PointF(startx - Values.P, starty + Values.Hf), raggio, true);

                    PointF p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx - Values.S1, starty + Values.H1), new PointF(startx - Values.P, starty + Values.Hf), raggio, Centro_arco, true);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);
                }

                x = startx - Values.P;
                y = starty + Values.Hf;
                points_list.add("" + x + "," + y);

                Sagitta = Values.M4;

                if (Sagitta <= 1) {
                    x = startx + Values.S2;
                    y = starty + Values.H2;
                    points_list.add("" + x + "," + y);
                } else {
                    float Lunghezza_corda = MathGeoTri.Distance(startx - Values.P, starty + Values.Hf, startx + Values.S2, starty + Values.H2);
                    float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

                    PointF Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx - Values.P, starty + Values.Hf), new PointF(startx + Values.S2, starty + Values.H2), raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

                    PointF p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx - Values.P, starty + Values.Hf), new PointF(startx + Values.S2, starty + Values.H2), raggio, Centro_arco, true);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);
                }

                x = startx + Values.S2;
                y = starty + Values.H2;
                points_list.add("" + x + "," + y);

                Sagitta = Values.M2;

                if (Sagitta <= 1) {
                    x = startx + Values.L / 2;
                    y = starty;
                    points_list.add("" + x + "," + y);
                } else {
                    float Lunghezza_corda = MathGeoTri.Distance(startx + Values.S2, starty + Values.H2, startx + Values.L / 2, starty);
                    float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

                    PointF Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx + Values.S2, starty + Values.H2), new PointF(startx + Values.L / 2, starty), raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

                    PointF p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx + Values.S2, starty + Values.H2), new PointF(startx + Values.L / 2, starty), raggio, Centro_arco, true);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);
                }

                x = startx + Values.L / 2;
                y = starty;
                points_list.add("" + x + "," + y);

            } else if (image.equals("ar_2_sch")) {

                float Lb = (bmp.getWidth() - Float.parseFloat(eT_L.getText().toString())) / 2;
                float Hb = (bmp.getHeight() - Float.parseFloat(eT_H.getText().toString())) / 2;

                PointF p3;
                ArrayList<PointF> Intersezioni = MathGeoTri.FindArcArcIntersections(new PointF(bmp.getWidth() / 2 + Float.parseFloat(eT_P.getText().toString()), Hb + Float.parseFloat(eT_H.getText().toString())), Float.parseFloat(eT_S1.getText().toString()), new PointF(Float.parseFloat(eT_L.getText().toString()) + Lb, Hb), Float.parseFloat(eT_H1.getText().toString()));  //trovo le due intersezioni del cerchietto con l'arco
                if (MathGeoTri.ArcDirection_StartMidEnd(new PointF(Float.parseFloat(eT_L.getText().toString()) + Lb, Hb), Intersezioni.get(0), new PointF(bmp.getWidth() / 2 + Float.parseFloat(eT_P.getText().toString()), Hb + Float.parseFloat(eT_H.getText().toString())))) {
                    p3 = Intersezioni.get(0);
                } else {
                    p3 = Intersezioni.get(1);
                }
                Values.H1 = p3.y - Hb;
                Values.S1 = p3.x - (bmp.getWidth() / 2 + Float.parseFloat(eT_P.getText().toString()));

                Intersezioni = MathGeoTri.FindArcArcIntersections(new PointF(bmp.getWidth() / 2 + Float.parseFloat(eT_P.getText().toString()), Hb + Float.parseFloat(eT_H.getText().toString())), Float.parseFloat(eT_S2.getText().toString()), new PointF(Lb, Hb), Float.parseFloat(eT_H2.getText().toString()));  //trovo le due intersezioni del cerchietto con l'arco
                if (MathGeoTri.ArcDirection_StartMidEnd(new PointF(Lb, Hb), Intersezioni.get(0), new PointF(bmp.getWidth() / 2 + Float.parseFloat(eT_P.getText().toString()), Hb + Float.parseFloat(eT_H.getText().toString())))) {
                    p3 = Intersezioni.get(1);
                } else {
                    p3 = Intersezioni.get(0);
                }
                Values.H2 = p3.y - Hb;
                Values.S2 = (bmp.getWidth() / 2 + Float.parseFloat(eT_P.getText().toString())) - p3.x;

                Values.M1 = Float.parseFloat(eT_M1.getText().toString());
                Values.M2 = Float.parseFloat(eT_M2.getText().toString());
                Values.M3 = Float.parseFloat(eT_M3.getText().toString());
                Values.M4 = Float.parseFloat(eT_M4.getText().toString());

                Values.L = Float.parseFloat(eT_L.getText().toString());
                Values.P = Float.parseFloat(eT_P.getText().toString());
                Values.Hf = Float.parseFloat(eT_H.getText().toString());

                points_list.clear();
                float x = startx - Values.L / 2;
                float y = starty;
                points_list.add("" + x + "," + y);

                float Sagitta = Values.M1;

                if (Sagitta <= 1) {
                    x = startx - Values.S1;
                    y = starty + Values.H1;
                    points_list.add("" + x + "," + y);
                } else {
                    float Lunghezza_corda = MathGeoTri.Distance(startx - Values.L / 2, starty, startx - Values.S1, starty + Values.H1);
                    float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

                    PointF Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx - Values.L / 2, starty), new PointF(startx - Values.S1, starty + Values.H1), raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

                    PointF p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx - Values.L / 2, starty), new PointF(startx - Values.S1, starty + Values.H1), raggio, Centro_arco, false);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);
                }

                /*x = startx - Values.S1;
                y = starty + Values.H1;
                points_list.add("" + x + "," + y);*/

                x = startx - Values.S1;
                y = starty + Values.H1;
                points_list.add("" + x + "," + y);

                /*x = startx - Values.P;
                y = starty + Values.Hf;
                points_list.add("" + x + "," + y);*/

                Sagitta = Values.M3;

                if (Sagitta <= 1) {
                    x = startx - Values.P;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);
                } else {
                    float Lunghezza_corda = MathGeoTri.Distance(startx - Values.S1, starty + Values.H1, startx - Values.P, starty + Values.Hf);
                    float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

                    PointF Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx - Values.S1, starty + Values.H1), new PointF(startx - Values.P, starty + Values.Hf), raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

                    PointF p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx - Values.S1, starty + Values.H1), new PointF(startx - Values.P, starty + Values.Hf), raggio, Centro_arco, false);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);
                }

                x = startx - Values.P;
                y = starty + Values.Hf;
                points_list.add("" + x + "," + y);

                /*x = startx + Values.S2;
                y = starty + Values.H2;
                points_list.add("" + x + "," + y);*/

                Sagitta = Values.M4;

                if (Sagitta <= 1) {
                    x = startx + Values.S2;
                    y = starty + Values.H2;
                    points_list.add("" + x + "," + y);
                } else {
                    float Lunghezza_corda = MathGeoTri.Distance(startx - Values.P, starty + Values.Hf, startx + Values.S2, starty + Values.H2);
                    float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

                    PointF Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx - Values.P, starty + Values.Hf), new PointF(startx + Values.S2, starty + Values.H2), raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

                    PointF p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx - Values.P, starty + Values.Hf), new PointF(startx + Values.S2, starty + Values.H2), raggio, Centro_arco, false);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);
                }

                x = startx + Values.S2;
                y = starty + Values.H2;
                points_list.add("" + x + "," + y);

               /* x = startx + Values.L/2;
                y = starty;
                points_list.add("" + x + "," + y);*/

                Sagitta = Values.M2;

                if (Sagitta <= 1) {
                    x = startx + Values.L / 2;
                    y = starty;
                    points_list.add("" + x + "," + y);
                } else {
                    float Lunghezza_corda = MathGeoTri.Distance(startx + Values.S2, starty + Values.H2, startx + Values.L / 2, starty);
                    float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

                    PointF Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx + Values.S2, starty + Values.H2), new PointF(startx + Values.L / 2, starty), raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

                    PointF p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx + Values.S2, starty + Values.H2), new PointF(startx + Values.L / 2, starty), raggio, Centro_arco, false);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);
                }

                x = startx + Values.L / 2;
                y = starty;
                points_list.add("" + x + "," + y);
            } else if (image.equals("q_1_sch")) {

                Values.L = Float.parseFloat(eT_L.getText().toString());
                Values.Hf = Float.parseFloat(eT_H.getText().toString());

                points_list.clear();
                float x = startx - Values.L / 2;
                float y = starty;
                points_list.add("" + x + "," + y);

                x = startx - Values.L / 2;
                y = starty + Values.Hf;
                points_list.add("" + x + "," + y);

                x = startx + Values.L / 2;
                y = starty + Values.Hf;
                points_list.add("" + x + "," + y);

                x = startx + Values.L / 2;
                y = starty;
                points_list.add("" + x + "," + y);
            } else if (image.equals("qa_1_sch")) {

                //float Lb = (bmp.getWidth() - Float.parseFloat(eT_L.getText().toString())) / 2;
                //float Hb = (bmp.getHeight() - Float.parseFloat(eT_H.getText().toString())) / 2;

                if (Float.parseFloat(eT_M1.getText().toString()) < 1 && Float.parseFloat(eT_M2.getText().toString()) < 1) {
                    Values.L = Float.parseFloat(eT_L.getText().toString());
                    Values.Hf = Float.parseFloat(eT_H.getText().toString());

                    points_list.clear();
                    float x = startx - Values.L / 2;
                    float y = starty;
                    points_list.add("" + x + "," + y);

                    x = startx - Values.L / 2;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);

                    x = startx + Values.L / 2;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);

                    x = startx + Values.L / 2;
                    y = starty;
                    points_list.add("" + x + "," + y);
                } else {
                    Values.M1 = Float.parseFloat(eT_M1.getText().toString());
                    Values.M2 = Float.parseFloat(eT_M2.getText().toString());
                    Values.M3 = -1000;
                    Values.M4 = -1000;
                    Values.L = Float.parseFloat(eT_L.getText().toString());
                    Values.P = -1000;
                    Values.Hf = Float.parseFloat(eT_H.getText().toString());
                    Values.S1 = -1000;
                    Values.S2 = -1000;
                    Values.H1 = -1000;
                    Values.H2 = -1000;

                /*if(Values.M1 <=1 && Values.M2 <= 1) {

                    points_list.clear();
                    float x = startx - Values.L / 2;
                    float y = starty;
                    points_list.add("" + x + "," + y);

                    x = startx - Values.L / 2;
                    y = starty + Values.Hf - Values.H1;
                    points_list.add("" + x + "," + y);

                    x = startx - Values.L / 2 + Values.S1;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);

                    x = startx - Values.L / 2 + Values.S1;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);

                    //Inizio parte sinistra

                    x = startx + Values.L / 2 - Values.S2;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);

                    x = startx + Values.L / 2;
                    y = starty + Values.Hf - Values.H2;
                    points_list.add("" + x + "," + y);

                    /*x = startx + Values.L / 2 - Values.S2;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);*/

                /*x = startx + Values.L / 2;
                y = starty + Values.Hf - Values.H2;
                points_list.add("" + x + "," + y);*/

                    /*x = startx + Values.L / 2;
                    y = starty + Values.Hf - Values.S1;
                    points_list.add("" + x + "," + y);*/

                    /*x = startx + Values.L / 2;
                    y = starty;
                    points_list.add("" + x + "," + y);*/

                    /*x = startx + Values.L / 2;
                    y = starty;
                    points_list.add("" + x + "," + y);
                }
                else
                {*/
                    points_list.clear();
                    float x = startx - Values.L / 2;
                    float y = starty;
                    points_list.add("" + x + "," + y);

                    x = startx - Values.L / 2;
                    y = starty + Values.Hf - Values.M1;
                    points_list.add("" + x + "," + y);

                    float raggio = Values.M1;

                    //float Lunghezza_corda = (float) distance(startx - Values.L / 2, starty + Values.Hf - Values.H1, startx - Values.L / 2 + Values.S1, starty + Values.Hf);
                    //float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

                    PointF Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx - Values.L / 2, starty + Values.Hf - Values.M1), new PointF(startx - Values.L / 2 + Values.M1, starty + Values.Hf), raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

                    PointF p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx - Values.L / 2, starty + Values.Hf - Values.M1), new PointF(startx - Values.L / 2 + Values.M1, starty + Values.Hf), raggio, Centro_arco, true);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);

                    x = startx - Values.L / 2 + Values.M1;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);

                    //Inizio parte sinistra

                    x = startx + Values.L / 2 - Values.M2;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);

                    raggio = Values.M2;

                    Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx + Values.L / 2, starty + Values.Hf - Values.M2), new PointF(startx + Values.L / 2 - Values.M2, starty + Values.Hf), raggio, false);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

                    p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx + Values.L / 2, starty + Values.Hf - Values.M2), new PointF(startx + Values.L / 2 - Values.M2, starty + Values.Hf), raggio, Centro_arco, false);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);

                    /*x = startx + Values.L / 2 - Values.S2;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);*/

                    x = startx + Values.L / 2;
                    y = starty + Values.Hf - Values.M2;
                    points_list.add("" + x + "," + y);

                    /*x = startx + Values.L / 2;
                    y = starty + Values.Hf - Values.S1;
                    points_list.add("" + x + "," + y);*/

                    /*x = startx + Values.L / 2;
                    y = starty;
                    points_list.add("" + x + "," + y);*/

                    x = startx + Values.L / 2;
                    y = starty;
                    points_list.add("" + x + "," + y);
                }
                // }
            } else if (image.equals("qa_2_sch")) {

                if (Float.parseFloat(eT_M1.getText().toString()) < 1 && Float.parseFloat(eT_M2.getText().toString()) < 1) {
                    Values.L = Float.parseFloat(eT_L.getText().toString());
                    Values.Hf = Float.parseFloat(eT_H.getText().toString());

                    points_list.clear();
                    float x = startx - Values.L / 2;
                    float y = starty;
                    points_list.add("" + x + "," + y);

                    x = startx - Values.L / 2;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);

                    x = startx + Values.L / 2;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);

                    x = startx + Values.L / 2;
                    y = starty;
                    points_list.add("" + x + "," + y);
                } else {
                    Values.M1 = Float.parseFloat(eT_M1.getText().toString());
                    Values.M2 = Float.parseFloat(eT_M2.getText().toString());
                    Values.M3 = -1000;
                    Values.M4 = -1000;
                    Values.L = Float.parseFloat(eT_L.getText().toString());
                    Values.P = -1000;
                    Values.Hf = Float.parseFloat(eT_H.getText().toString());
                    Values.S1 = Float.parseFloat(eT_S1.getText().toString());
                    Values.S2 = -1000;
                    Values.H1 = Float.parseFloat(eT_H1.getText().toString());
                    Values.H2 = Float.parseFloat(eT_H2.getText().toString());

                /*if(Values.M1 <=1 && Values.M2 <= 1) {

                    points_list.clear();
                    float x = startx - Values.L / 2;
                    float y = starty;
                    points_list.add("" + x + "," + y);

                    x = startx - Values.L / 2;
                    y = starty + Values.Hf - Values.H1;
                    points_list.add("" + x + "," + y);

                    x = startx - Values.L / 2 + Values.S1;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);

                    x = startx - Values.L / 2 + Values.S1;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);

                    //Inizio parte sinistra

                    x = startx + Values.L / 2 - Values.S2;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);

                    x = startx + Values.L / 2;
                    y = starty + Values.Hf - Values.H2;
                    points_list.add("" + x + "," + y);

                    /*x = startx + Values.L / 2 - Values.S2;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);*/

                /*x = startx + Values.L / 2;
                y = starty + Values.Hf - Values.H2;
                points_list.add("" + x + "," + y);*/

                    /*x = startx + Values.L / 2;
                    y = starty + Values.Hf - Values.S1;
                    points_list.add("" + x + "," + y);*/

                    /*x = startx + Values.L / 2;
                    y = starty;
                    points_list.add("" + x + "," + y);*/

                    /*x = startx + Values.L / 2;
                    y = starty;
                    points_list.add("" + x + "," + y);
                }
                else
                {*/
                    points_list.clear();
                    float x = startx - Values.L / 2;
                    float y = starty;
                    points_list.add("" + x + "," + y);

                    x = startx - Values.L / 2;
                    y = starty + Values.H1;
                    points_list.add("" + x + "," + y);

                    float raggio = Values.M1;

                    //float Lunghezza_corda = (float) distance(startx - Values.L / 2, starty + Values.Hf - Values.H1, startx - Values.L / 2 + Values.S1, starty + Values.Hf);
                    //float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

                    PointF Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx - Values.S1 / 2, starty + Values.Hf), new PointF(startx - Values.L / 2, starty + Values.H1), raggio, false);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

                    PointF p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx - Values.S1 / 2, starty + Values.Hf), new PointF(startx - Values.L / 2, starty + Values.H1), raggio, Centro_arco, false);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);

                    x = startx - Values.S1 / 2;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);

                    //Inizio parte sinistra

                    x = startx + Values.S1 / 2;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);

                    raggio = Values.M2;

                    Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx + Values.S1 / 2, starty + Values.Hf), new PointF(startx + Values.L / 2, starty + Values.H2), raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

                    p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx + Values.S1 / 2, starty + Values.Hf), new PointF(startx + Values.L / 2, starty + Values.H2), raggio, Centro_arco, true);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);

                    /*x = startx + Values.L / 2 - Values.S2;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);*/

                    x = startx + Values.L / 2;
                    y = starty + Values.H2;
                    points_list.add("" + x + "," + y);

                    /*x = startx + Values.L / 2;
                    y = starty + Values.Hf - Values.S1;
                    points_list.add("" + x + "," + y);*/

                    /*x = startx + Values.L / 2;
                    y = starty;
                    points_list.add("" + x + "," + y);*/

                    x = startx + Values.L / 2;
                    y = starty;
                    points_list.add("" + x + "," + y);
                }
                // }
            } else if (image.equals("qs_1_sch")) {

                //float Lb = (bmp.getWidth() - Float.parseFloat(eT_L.getText().toString())) / 2;
                //float Hb = (bmp.getHeight() - Float.parseFloat(eT_H.getText().toString())) / 2;

                Values.M1 = -1000;
                Values.M2 = -1000;
                Values.M3 = -1000;
                Values.M4 = -1000;
                Values.L = Float.parseFloat(eT_L.getText().toString());
                Values.P = -1000;
                Values.Hf = Float.parseFloat(eT_H.getText().toString());
                Values.S1 = Float.parseFloat(eT_S1.getText().toString());
                Values.S2 = Float.parseFloat(eT_S2.getText().toString());
                Values.H1 = Float.parseFloat(eT_H1.getText().toString());
                Values.H2 = Float.parseFloat(eT_H2.getText().toString());

                /*if(Values.M1 <=1 && Values.M2 <= 1) {

                    points_list.clear();
                    float x = startx - Values.L / 2;
                    float y = starty;
                    points_list.add("" + x + "," + y);

                    x = startx - Values.L / 2;
                    y = starty + Values.Hf - Values.H1;
                    points_list.add("" + x + "," + y);

                    x = startx - Values.L / 2 + Values.S1;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);

                    x = startx - Values.L / 2 + Values.S1;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);

                    //Inizio parte sinistra

                    x = startx + Values.L / 2 - Values.S2;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);

                    x = startx + Values.L / 2;
                    y = starty + Values.Hf - Values.H2;
                    points_list.add("" + x + "," + y);

                    /*x = startx + Values.L / 2 - Values.S2;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);*/

                /*x = startx + Values.L / 2;
                y = starty + Values.Hf - Values.H2;
                points_list.add("" + x + "," + y);*/

                    /*x = startx + Values.L / 2;
                    y = starty + Values.Hf - Values.S1;
                    points_list.add("" + x + "," + y);*/

                    /*x = startx + Values.L / 2;
                    y = starty;
                    points_list.add("" + x + "," + y);*/

                    /*x = startx + Values.L / 2;
                    y = starty;
                    points_list.add("" + x + "," + y);
                }
                else
                {*/
                points_list.clear();

                float x = startx - Values.L / 2;
                float y = starty;
                points_list.add("" + x + "," + y);

                x = startx - Values.L / 2;
                y = starty + Values.Hf - Values.H1;
                points_list.add("" + x + "," + y);

                x = startx - Values.L / 2 + Values.S1;
                y = starty + Values.Hf;
                points_list.add("" + x + "," + y);

                x = startx - Values.L / 2 + Values.S1;
                y = starty + Values.Hf;
                points_list.add("" + x + "," + y);

                //Inizio parte sinistra

                x = startx + Values.L / 2 - Values.S2;
                y = starty + Values.Hf;
                points_list.add("" + x + "," + y);

                x = startx + Values.L / 2;
                y = starty + Values.Hf - Values.H2;
                points_list.add("" + x + "," + y);

                x = startx + Values.L / 2;
                y = starty + Values.Hf - Values.H2;
                points_list.add("" + x + "," + y);

                x = startx + Values.L / 2;
                y = starty;
                points_list.add("" + x + "," + y);
            } else if (image.equals("ar3_1_sch")) {

                Values.M1 = Float.parseFloat(eT_M1.getText().toString());
                Values.M2 = Float.parseFloat(eT_M2.getText().toString());
                Values.M3 = Float.parseFloat(eT_M3.getText().toString());
                Values.M4 = Float.parseFloat(eT_M4.getText().toString());
                Values.M5 = Float.parseFloat(eT_M5.getText().toString());
                Values.M6 = Float.parseFloat(eT_M6.getText().toString());
                Values.H2 = Float.parseFloat(eT_H2.getText().toString());
                Values.H1 = Float.parseFloat(eT_H1.getText().toString());
                Values.H3 = Float.parseFloat(eT_H3.getText().toString());
                Values.H4 = Float.parseFloat(eT_H4.getText().toString());
                Values.L = Float.parseFloat(eT_L.getText().toString());
                Values.S2 = Float.parseFloat(eT_S2.getText().toString());
                Values.S1 = Float.parseFloat(eT_S1.getText().toString());
                Values.S3 = Float.parseFloat(eT_S3.getText().toString());
                Values.S4 = Float.parseFloat(eT_S4.getText().toString());
                Values.P = Float.parseFloat(eT_P.getText().toString());
                Values.Hf = Float.parseFloat(eT_H.getText().toString());

                points_list.clear();

                float x = startx - Values.L / 2;
                float y = starty;
                points_list.add("" + x + "," + y);

                float Sagitta = Values.M1;

                if (Sagitta <= 1) {
                    x = startx - Values.S1;
                    y = starty + Values.H1;
                    points_list.add("" + x + "," + y);
                } else {
                    float Lunghezza_corda = MathGeoTri.Distance(startx - Values.L / 2, starty, startx - Values.S1, starty + Values.H1);
                    float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

                    PointF Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx - Values.L / 2, starty), new PointF(startx - Values.S1, starty + Values.H1), raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

                    PointF p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx - Values.L / 2, starty), new PointF(startx - Values.S1, starty + Values.H1), raggio, Centro_arco, true);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);
                }

                x = startx - Values.S1;
                y = starty + Values.H1;
                points_list.add("" + x + "," + y);

                Sagitta = Values.M3;

                if (Sagitta <= 1) {
                    x = startx - Values.S3;
                    y = starty + Values.H3;
                    points_list.add("" + x + "," + y);
                } else {
                    float Lunghezza_corda = MathGeoTri.Distance(startx - Values.S1, starty + Values.H1, startx - Values.S3, starty + Values.H3);
                    float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

                    PointF Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx - Values.S1, starty + Values.H1), new PointF(startx - Values.S3, starty + Values.H3), raggio, true);

                    PointF p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx - Values.S1, starty + Values.H1), new PointF(startx - Values.S3, starty + Values.H3), raggio, Centro_arco, true);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);
                }

                x = startx - Values.S3;
                y = starty + Values.H3;
                points_list.add("" + x + "," + y);

                Sagitta = Values.M5;

                if (Sagitta <= 1) {
                    x = startx - Values.P;
                    y = starty + Values.Hf;
                    points_list.add("" + x + "," + y);
                } else {
                    float Lunghezza_corda = MathGeoTri.Distance(startx - Values.S3, starty + Values.H3, startx - Values.P, starty + Values.Hf);
                    float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

                    PointF Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx - Values.S3, starty + Values.H3), new PointF(startx - Values.P, starty + Values.Hf), raggio, true);

                    PointF p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx - Values.S3, starty + Values.H3), new PointF(startx - Values.P, starty + Values.Hf), raggio, Centro_arco, true);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);
                }

                x = startx - Values.P;
                y = starty + Values.Hf;
                points_list.add("" + x + "," + y);

                Sagitta = Values.M6;

                if (Sagitta <= 1) {
                    x = startx + Values.S4;
                    y = starty + Values.H4;
                    points_list.add("" + x + "," + y);
                } else {
                    float Lunghezza_corda = MathGeoTri.Distance(startx - Values.P, starty + Values.Hf, startx + Values.S4, starty + Values.H4);
                    float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

                    PointF Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx - Values.P, starty + Values.Hf), new PointF(startx + Values.S4, starty + Values.H4), raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

                    PointF p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx - Values.P, starty + Values.Hf), new PointF(startx + Values.S4, starty + Values.H4), raggio, Centro_arco, true);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);
                }

                x = startx + Values.S4;
                y = starty + Values.H4;
                points_list.add("" + x + "," + y);

                Sagitta = Values.M4;

                if (Sagitta <= 1) {
                    x = startx + Values.S2;
                    y = starty + Values.H2;
                    points_list.add("" + x + "," + y);
                } else {
                    float Lunghezza_corda = MathGeoTri.Distance(startx + Values.S4, starty + Values.H4, startx + Values.S2, starty + Values.H2);
                    float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

                    PointF Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx + Values.S4, starty + Values.H4), new PointF(startx + Values.S2, starty + Values.H2), raggio, true);

                    PointF p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx + Values.S4, starty + Values.H4), new PointF(startx + Values.S2, starty + Values.H2), raggio, Centro_arco, true);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);
                }

                x = startx + Values.S2;
                y = starty + Values.H2;
                points_list.add("" + x + "," + y);

                Sagitta = Values.M2;

                if (Sagitta <= 1) {
                    x = startx + Values.L / 2;
                    y = starty;
                    points_list.add("" + x + "," + y);
                } else {
                    float Lunghezza_corda = MathGeoTri.Distance(startx + Values.S2, starty + Values.H2, startx + Values.L / 2, starty);
                    float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

                    PointF Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(startx + Values.S2, starty + Values.H2), new PointF(startx + Values.L / 2, starty), raggio, true);

                    PointF p = MathGeoTri.CalculateArcThirdPoint(new PointF(startx + Values.S2, starty + Values.H2), new PointF(startx + Values.L / 2, starty), raggio, Centro_arco, true);

                    x = p.x;
                    y = p.y;
                    points_list.add("" + x + "," + y);
                }

                x = startx + Values.L / 2;
                y = starty;
                points_list.add("" + x + "," + y);
            }
            //endregion

            // List of string that contains the values to write on pts file
            ArrayList<String> str = new ArrayList<String>();

            if (image.equals("ar_1_sch") || image.equals("ar_2_sch")) {
                if (Values.M1 == 0 && Values.M2 == 0 && Values.M3 == 0 && Values.M4 == 0) {
                    Values.model = 1;
                } else {
                    Values.model = 0;
                }
            } else if (image.equals("q_1_sch")) {
                Values.model = 3;
            } else if (image.equals("qa_1_sch") || image.equals("qa_2_sch")) {
                if (Values.M1 < 1 && Values.M2 < 1) {
                    Values.model = 3;
                } else {
                    Values.model = 4;
                }
            } else if (image.equals("qs_1_sch")) {
                Values.model = 2;
            } else if (image.equals("ar3_1_sch")) {
                Values.model = 5;
            }

            if (Values.model == 0) {
                str.add("0");
            } else if (Values.model == 1) {
                str.add("1");
            } else if (Values.model == 2) {
                str.add("2");
            } else if (Values.model == 3) {
                str.add("3");
            } else if (Values.model == 4) {
                str.add("4");
            } else if (Values.model == 5) {
                str.add("5");
            }

            str.add("" + points_list.size());
            for (int i = 0; i < points_list.size(); i++) {
                str.add(points_list.get(i));
            }

            try {
                File root = new File(Environment.getExternalStorageDirectory(), "JamData/punti");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File gpxfile = new File(root, "punti.pts");
                // append text
                BufferedWriter out = new BufferedWriter(new FileWriter(gpxfile.toString(), false));
                for (int i = 0; i < str.size(); i++) {
                    out.write(str.get(i) + "\n");
                }
                out.close();
                Values.File = "punti.pts";
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }

            ViewFlipper vf1 = findViewById(R.id.ViewFlipper1);
            Values.imgScheletro = vf1.getDisplayedChild();

            if (image.equals("ar_1_sch") || image.equals("ar_2_sch")) {
                Values.I = 30f;
                if (Values.M1 == 0 && Values.M2 == 0 && Values.M3 == 0 && Values.M4 == 0) {
                    Intent page2 = new Intent(this, SelectModelActivity.class);
                    startActivity(page2);
                } else {
                    Intent page2 = new Intent(this, SelectTypeActivity.class);
                    startActivity(page2);
                }
            } else if (image.equals("q_1_sch")) {
                Values.I = 6.4f;
                Intent page2 = new Intent(this, SelectTypeActivity4.class);
                startActivity(page2);
            } else if (image.equals("qa_1_sch") || image.equals("qa_2_sch")) {
                Values.I = 6.4f;
                if (Values.M1 < 1 && Values.M2 < 1) {
                    Intent page2 = new Intent(this, SelectTypeActivity4.class);
                    startActivity(page2);
                } else {
                    Intent page2 = new Intent(this, SelectTypeActivity5.class);
                    startActivity(page2);
                }
            } else if (image.equals("qs_1_sch")) {
                Values.I = 6.4f;
                Intent page2 = new Intent(this, SelectTypeActivity3.class);
                startActivity(page2);
            } else if (image.equals("ar3_1_sch")) {
                Values.I = 30f;
                Intent page2 = new Intent(this, SelectTypeActivity6.class);
                startActivity(page2);
            }


              SaveLastValuesFile();
        }
    }


    /**
     * SaveLastValuesFile.
     *
     */
    public void SaveLastValuesFile() {
        // Salva i valori in un file per il bottone Last Value
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "JamData");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = null;
            int imagetype = (int)Values.imgScheletro;
            switch (imagetype){
                case 0:
                    gpxfile = new File(root, "LastValues.txt");
                    break;

                case 1:
                    gpxfile = new File(root, "LastValues1.txt");
                    break;
                case 2:
                    gpxfile = new File(root, "LastValues2.txt");
                    break;
                case 3:
                    gpxfile = new File(root, "LastValues3.txt");
                    break;
                case 4:
                    gpxfile = new File(root, "LastValues4.txt");
                    break;
                case 5:
                    gpxfile = new File(root, "LastValues5.txt");
                    break;
                case 6:
                    gpxfile = new File(root, "LastValues6.txt");
                    break;


                default:
                    break;



            }


            BufferedWriter out = new BufferedWriter(new FileWriter(gpxfile.toString()));

            out.write(Values.imgScheletro + "\n");
            out.write(Values.M1 + "\n");
            out.write(Values.M2 + "\n");
            out.write(Values.M3 + "\n");
            out.write(Values.M4 + "\n");
            out.write(Values.H2 + "\n");
            out.write(Values.H1 + "\n");
            out.write(Values.L + "\n");
            out.write(Values.S2 + "\n");
            out.write(Values.S1 + "\n");
            out.write(Values.P + "\n");
            out.write(Values.Hf + "\n");
            out.write(Values.H3 + "\n");
            out.write(Values.H4 + "\n");
            out.write(Values.M5 + "\n");
            out.write(Values.M6 + "\n");
            out.write(Values.S3 + "\n");
            out.write(Values.S4 + "\n");

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Function for "translate" the current chose image index to a string
     *
     * @return
     */
    private String GetImageName() {
        ViewFlipper vf1 = findViewById(R.id.ViewFlipper1);
        if (vf1.getDisplayedChild() == 0) {
            return "ar_1_sch";
        } else if (vf1.getDisplayedChild() == 1) {
            return "ar_2_sch";
        } else if (vf1.getDisplayedChild() == 2) {
            return "q_1_sch";
        } else if (vf1.getDisplayedChild() == 3) {
            return "qa_1_sch";
        } else if (vf1.getDisplayedChild() == 4) {
            return "qa_2_sch";
        } else if (vf1.getDisplayedChild() == 5) {
            return "qs_1_sch";
        } else if (vf1.getDisplayedChild() == 6) {
            return "ar3_1_sch";
        }
        return null;
    }

    /**
     * Function for draw the pocket on the canvas
     *
     * @return
     */
    public boolean BtnDraw() {
        String image = GetImageName();

        // Scale of the draw
        float s;

        // Check the displayed image
        if (image.equals("ar_1_sch")) {
            EditText eT_M1 = findViewById(R.id.editText_M1);
            EditText eT_M2 = findViewById(R.id.editText_M2);
            EditText eT_H2 = findViewById(R.id.editText_H2);
            EditText eT_H1 = findViewById(R.id.editText_H1);
            EditText eT_L = findViewById(R.id.editText_L);
            EditText eT_S2 = findViewById(R.id.editText_S2);
            EditText eT_S1 = findViewById(R.id.editText_S1);
            EditText eT_P = findViewById(R.id.editText_P);
            EditText eT_H = findViewById(R.id.editText_H);
            EditText eT_M3 = findViewById(R.id.editText_M3);
            EditText eT_M4 = findViewById(R.id.editText_M4);

            float L = Float.parseFloat(eT_L.getText().toString());
            float H = Float.parseFloat(eT_H.getText().toString());
            float H1 = Float.parseFloat(eT_H1.getText().toString());
            float H2 = Float.parseFloat(eT_H2.getText().toString());
            float S1 = Float.parseFloat(eT_S1.getText().toString());
            float S2 = Float.parseFloat(eT_S2.getText().toString());
            float P = Float.parseFloat(eT_P.getText().toString());
            float M1 = Float.parseFloat(eT_M1.getText().toString());
            float M2 = Float.parseFloat(eT_M2.getText().toString());
            float M3 = Float.parseFloat(eT_M3.getText().toString());
            float M4 = Float.parseFloat(eT_M4.getText().toString());

            //if(M1 == 0 || M2 == 0 || H2 == 0 || H1 == 0 || L == 0 || S2 == 0 || S1 == 0 || P == 0 || H == 0 || M3 == 0 || M4 == 0)
            if (eT_M1.getText().toString().isEmpty() ||
                    eT_M2.getText().toString().isEmpty() ||
                    eT_H2.getText().toString().isEmpty() ||
                    eT_H1.getText().toString().isEmpty() ||
                    eT_L.getText().toString().isEmpty() ||
                    eT_S2.getText().toString().isEmpty() ||
                    eT_S1.getText().toString().isEmpty() ||
                    eT_P.getText().toString().isEmpty() ||
                    eT_H.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please insert all the values", Toast.LENGTH_LONG).show();
                return false;
            }
            bmp = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
            float Lb = (bmp.getWidth() - Float.parseFloat(eT_L.getText().toString())) / 2;
            float Hb = (bmp.getHeight() - Float.parseFloat(eT_H.getText().toString())) / 2;
            float M2b = (bmp.getWidth() / 2 - Float.parseFloat(eT_M2.getText().toString()));
            ImageView img = findViewById(R.id.imageView7);
            final Canvas canvas = new Canvas(bmp);
            //Setto il colore dello sfondo
            canvas.drawColor(Color.GRAY);
            //region Paint
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(3);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStyle(Paint.Style.STROKE);

            Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint1.setColor(Color.GREEN);
            paint1.setStrokeWidth(10);
            paint1.setAntiAlias(true);
            paint1.setStrokeCap(Paint.Cap.ROUND);
            paint1.setStyle(Paint.Style.STROKE);
            //endregion
            img.setImageBitmap(bmp);

            DrawImage_ar_1_sch(canvas, paint, paint1, Lb, Hb, L, H, H1, H2, S1, S2, P, M1, M2, M3, M4);

            float pm1x = (Lb + (bmp.getWidth() / 2 - S2)) / 2;
            float pm1y = (Hb + (H2 + Hb)) / 2;
            CenterPointRadius CentroRaggio = MathGeoTri.CalculateArc_CenterRadius(new PointF(Lb, Hb), new PointF(pm1x - M2, pm1y), new PointF(bmp.getWidth() / 2 - S2, H2 + Hb));
            RectF oval = new RectF(CentroRaggio.center.x - CentroRaggio.radius, CentroRaggio.center.y - CentroRaggio.radius, CentroRaggio.center.x + CentroRaggio.radius, CentroRaggio.center.y + CentroRaggio.radius);
            float endangle = (float) (Math.atan2(Hb - CentroRaggio.center.y, Lb - CentroRaggio.center.x) * 180 / (float) Math.PI);
            float startAngle = (float) Math.atan2(H2 + Hb - CentroRaggio.center.y, bmp.getHeight() / 2 - S2 - CentroRaggio.center.x) * 180 / (float) Math.PI;
            if (endangle < 0) {
                endangle = endangle + 360;
            }

            //region MostLeftPoint
            PointF point1 = new PointF();
            try {
                int k = 0;
                if (M1 <= 1) {
                    float l = (float) Math.sqrt((float) Math.pow(Math.abs((L + Lb) - (bmp.getWidth() / 2 + S1)), 2) + (float) Math.pow(Math.abs(Hb - H1 + Hb), 2)); //lunghezza linea
                    float rest = l % 5;
                    float NLP = 5;
                    if (rest != 0) {
                        float lrest = (l - rest) / 5;
                        NLP = l / (lrest + 1);
                    }
                    for (int il = 0; il * NLP <= l; il++) {
                        ArrayList<PointF> p = MathGeoTri.CircleStraightLineIntersection(new PointF(L + Lb, Hb), new PointF(bmp.getWidth() / 2 + S1, H1 + Hb), new PointF(L + Lb, Hb), NLP * il);
                        if (k == 0) {
                            point1 = new PointF(p.get(0).x, p.get(0).y);
                            k = 1;
                        } else {
                            if (p.get(0).x > point1.x) {
                                point1 = new PointF(p.get(0).x, p.get(0).y);
                            }
                        }
                    }
                } else {
                    for (double i = 0; i < 360; i = i + 0.2) {
                        double angle = (2 * Math.PI / 360) * i;
                        if (startAngle < 0) {
                            startAngle = startAngle + 360;
                        }
                        if (MathGeoTri.ArcDirection_StartMidEnd(new PointF(L + Lb, Hb), new PointF(bmp.getWidth() / 2 + Float.parseFloat(eT_M1.getText().toString()), Hb + Float.parseFloat(eT_H.getText().toString()) / 2), new PointF(bmp.getWidth() / 2 + Float.parseFloat(eT_S1.getText().toString()), Float.parseFloat(eT_H1.getText().toString()) + Hb))) {
                            if (i < endangle || i > startAngle) {
                                if (k == 0) {
                                    point1.x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                    point1.y = CentroRaggio.center.y + CentroRaggio.radius * (float) Math.sin(angle);
                                    k = 1;
                                } else {
                                    float x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                    if (x > point1.x) {
                                        point1.x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                        point1.y = CentroRaggio.center.y + CentroRaggio.radius * (float) Math.sin(angle);
                                    }
                                }
                            }
                        } else {
                            if (i > endangle && i < startAngle) {
                                if (k == 0) {
                                    point1.x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                    point1.y = CentroRaggio.center.y + CentroRaggio.radius * (float) Math.sin(angle);
                                    k = 1;
                                } else {
                                    //canvas.drawPoint((float) CentroRaggio.get(0) + (float) CentroRaggio.get(2) * (float) Math.cos(angle), (float) CentroRaggio.get(1) + (float) CentroRaggio.get(2) * (float) Math.sin(angle),paint1);
                                    float x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                    if (x > point1.x) {
                                        point1.x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                        point1.y = CentroRaggio.center.y + CentroRaggio.radius * (float) Math.sin(angle);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
            //endregion

            //region MostRightPoint
            PointF point = new PointF();
            try {
                int k = 0;
                if (M2 <= 1) {
                    float l = (float) Math.sqrt((float) Math.pow(Math.abs((bmp.getWidth() / 2 - S2) - Lb), 2) + (float) Math.pow(Math.abs((Float.parseFloat(eT_H2.getText().toString()) + Hb) - Hb), 2)); //lunghezza linea
                    float rest = l % 5;
                    float NLP = 5;
                    if (rest != 0) {
                        float lrest = (l - rest) / 5;
                        NLP = l / (lrest + 1);
                    }
                    for (int il = 0; il * NLP <= l; il++) {
                        ArrayList<PointF> p = MathGeoTri.CircleStraightLineIntersection(new PointF(Lb, Hb), new PointF((bmp.getWidth() / 2 - S2), (Float.parseFloat(eT_H2.getText().toString()) + Hb)), new PointF(Lb, Hb), NLP * il);
                        if (k == 0) {
                            point = new PointF(p.get(0).x, p.get(0).y);
                            k = 1;
                        } else {
                            if (p.get(0).x < point.x) {
                                point = new PointF(p.get(0).x, p.get(0).y);
                            }
                        }
                    }
                } else {
                    for (double i = 0; i < 360; i = i + 0.2) {
                        double angle = (2 * Math.PI / 360) * i;
                        if (MathGeoTri.ArcDirection_StartMidEnd(new PointF(Lb, Hb), new PointF(M2b, Hb + H / 2), new PointF(bmp.getWidth() / 2 - Float.parseFloat(eT_S2.getText().toString()), Float.parseFloat(eT_H2.getText().toString()) + Hb))) {
                            if (i > endangle || i < startAngle) {
                                if (k == 0) {
                                    point.x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                    point.y = CentroRaggio.center.y + CentroRaggio.radius * (float) Math.sin(angle);
                                    k = 1;
                                } else {
                                    float x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                    if (x < point.x) {
                                        point.x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                        point.y = CentroRaggio.center.y + CentroRaggio.radius * (float) Math.sin(angle);
                                    }
                                }
                            }
                        } else {
                            if (i > startAngle && i < endangle) {
                                if (k == 0) {
                                    point.x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                    point.y = CentroRaggio.center.y + CentroRaggio.radius * (float) Math.sin(angle);
                                    k = 1;
                                } else {
                                    float x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                    if (x < point.x) {
                                        point.x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                        point.y = CentroRaggio.center.y + CentroRaggio.radius * (float) Math.sin(angle);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
            //endregion

            //region MostTopPoint
            PointF top = new PointF();
            top.x = Lb;
            top.y = Hb;
            //endregion

            //region MostBotPoint
            PointF bot = new PointF();
            bot.x = Lb;
            //Confronto H1 e H2
            float max = Math.max(Float.parseFloat(eT_H2.getText().toString()) + Hb, Float.parseFloat(eT_H1.getText().toString()) + Hb);
            //Confronto il risultato prima con H
            bot.y = Math.max(max, Float.parseFloat(eT_H.getText().toString()) + Hb);
            //endregion

            //Trovo le dimensioni della figura
            double h = Math.abs(bot.y - top.y);
            double w = Math.abs(point1.x - point.x);
            s = (float) Math.max(h, w);
            //la scalo in modo che sia ingrandita/rimpicciolita
            canvas.scale(300 / s, 300 / s, 200, 200);
            paint.setStrokeWidth(s / 300 * 2);
            canvas.drawColor(Color.GRAY);

            DrawImage_ar_1_sch(canvas, paint, paint1, Lb, Hb, L, H, H1, H2, S1, S2, P, M1, M2, M3, M4);
        } else if (image.equals("ar_2_sch")) {
            //region TakeValue
            EditText eT_M1 = findViewById(R.id.editText_M1);
            EditText eT_M2 = findViewById(R.id.editText_M2);
            EditText eT_H2 = findViewById(R.id.editText_H2);
            EditText eT_H1 = findViewById(R.id.editText_H1);
            EditText eT_L = findViewById(R.id.editText_L);
            EditText eT_S2 = findViewById(R.id.editText_S2);
            EditText eT_S1 = findViewById(R.id.editText_S1);
            EditText eT_P = findViewById(R.id.editText_P);
            EditText eT_H = findViewById(R.id.editText_H);
            EditText eT_M3 = findViewById(R.id.editText_M3);
            EditText eT_M4 = findViewById(R.id.editText_M4);

            float L = Float.parseFloat(eT_L.getText().toString());
            float H = Float.parseFloat(eT_H.getText().toString());
            float H1 = Float.parseFloat(eT_H1.getText().toString());
            float H2 = Float.parseFloat(eT_H2.getText().toString());
            float S1 = Float.parseFloat(eT_S1.getText().toString());
            float S2 = Float.parseFloat(eT_S2.getText().toString());
            float P = Float.parseFloat(eT_P.getText().toString());
            float M1 = Float.parseFloat(eT_M1.getText().toString());
            float M2 = Float.parseFloat(eT_M2.getText().toString());
            float M3 = Float.parseFloat(eT_M3.getText().toString());
            float M4 = Float.parseFloat(eT_M4.getText().toString());
            //endregion

            if (eT_M1.getText().toString().isEmpty() ||
                    eT_M2.getText().toString().isEmpty() ||
                    eT_H2.getText().toString().isEmpty() ||
                    eT_H1.getText().toString().isEmpty() ||
                    eT_L.getText().toString().isEmpty() ||
                    eT_S2.getText().toString().isEmpty() ||
                    eT_S1.getText().toString().isEmpty() ||
                    eT_P.getText().toString().isEmpty() ||
                    eT_H.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please insert all the values", Toast.LENGTH_LONG).show();
                return false;
            }
            bmp = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
            float Lb = (bmp.getWidth() - Float.parseFloat(eT_L.getText().toString())) / 2;
            float Hb = (bmp.getHeight() - Float.parseFloat(eT_H.getText().toString())) / 2;
            float M2b = (bmp.getWidth() / 2 - Float.parseFloat(eT_M2.getText().toString()));
            ImageView img = findViewById(R.id.imageView7);
            final Canvas canvas = new Canvas(bmp);
            //Setto il colore dello sfondo
            canvas.drawColor(Color.GRAY);
            //region Paint
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(3);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStyle(Paint.Style.STROKE);

            Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint1.setColor(Color.GREEN);
            paint1.setStrokeWidth(10);
            paint1.setAntiAlias(true);
            paint1.setStrokeCap(Paint.Cap.ROUND);
            paint1.setStyle(Paint.Style.STROKE);
            //endregion
            img.setImageBitmap(bmp);

            DrawImage_ar_2_sch(canvas, paint, paint1, Lb, Hb, L, H, H1, H2, S1, S2, P, M1, M2, M3, M4);

            float pm1x = (Lb + (bmp.getWidth() / 2 - S2)) / 2;
            float pm1y = (Hb + (H2 + Hb)) / 2;
            CenterPointRadius CentroRaggio = MathGeoTri.CalculateArc_CenterRadius(new PointF(Lb, Hb), new PointF(pm1x - M2, pm1y), new PointF(bmp.getWidth() / 2 - S2, H2 + Hb));
            RectF oval = new RectF(CentroRaggio.center.x - CentroRaggio.radius, CentroRaggio.center.y - CentroRaggio.radius, CentroRaggio.center.x + CentroRaggio.radius, CentroRaggio.center.y + CentroRaggio.radius);
            float endangle = (float) (Math.atan2(Hb - CentroRaggio.center.y, Lb - CentroRaggio.center.x) * 180 / (float) Math.PI);
            float startAngle = (float) Math.atan2(H2 + Hb - CentroRaggio.center.y, bmp.getHeight() / 2 - S2 - CentroRaggio.center.x) * 180 / (float) Math.PI;
            if (endangle < 0) {
                endangle = endangle + 360;
            }

            if (M2 <= 1) {
                canvas.drawLine(Lb, Hb, (bmp.getWidth() / 2 - S2), (H2 + Hb), paint);
            } else {
                if (MathGeoTri.ArcDirection_StartMidEnd(new PointF(Lb, Hb), new PointF(pm1x - M2, pm1y), new PointF(200 - S2, H2 + Hb))) {
                    canvas.drawArc(oval, endangle, 360 - endangle + startAngle, false, paint);
                } else {
                    canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
                }
            }

            //region MostLeftPoint
            PointF point1 = new PointF();
            try {
                int k = 0;
                if (M1 <= 1) {
                    float l = (float) Math.sqrt((float) Math.pow(Math.abs(((Float.parseFloat(eT_L.getText().toString()) + Lb) - (bmp.getWidth() / 2 + S1))), 2) + (float) Math.pow(Math.abs(Hb - H1 + Hb), 2)); //lunghezza linea
                    float rest = l % 5;
                    float NLP = 5;
                    if (rest != 0) {
                        float lrest = (l - rest) / 5;
                        NLP = l / (lrest + 1);
                    }
                    for (int il = 0; il * NLP <= l; il++) {
                        ArrayList<PointF> p = MathGeoTri.CircleStraightLineIntersection(new PointF(Float.parseFloat(eT_L.getText().toString()) + Lb, Hb), new PointF(bmp.getWidth() / 2 + S1, H1 + Hb), new PointF(Float.parseFloat(eT_L.getText().toString()) + Lb, Hb), NLP * il);
                        if (k == 0) {
                            point1 = new PointF(p.get(0).x, p.get(0).y);
                            k = 1;
                        } else {
                            if (p.get(0).x > point1.x) {
                                point1 = new PointF(p.get(0).x, p.get(0).y);
                            }
                        }
                    }
                } else {
                    for (double i = 0; i < 360; i = i + 0.2) {
                        double angle = (2 * Math.PI / 360) * i;
                        if (startAngle < 0) {
                            startAngle = startAngle + 360;
                        }
                        if (MathGeoTri.ArcDirection_StartMidEnd(new PointF(Float.parseFloat(eT_L.getText().toString()) + Lb, Hb), new PointF(bmp.getWidth() / 2 + Float.parseFloat(eT_M1.getText().toString()), Hb + Float.parseFloat(eT_H.getText().toString()) / 2), new PointF(bmp.getWidth() / 2 + S1, H1 + Hb))) {
                            if (i < endangle || i > startAngle) {
                                if (k == 0) {
                                    point1.x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                    point1.y = CentroRaggio.center.y + CentroRaggio.radius * (float) Math.sin(angle);
                                    k = 1;
                                } else {
                                    float x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                    if (x > point1.x) {
                                        point1.x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                        point1.y = CentroRaggio.center.y + CentroRaggio.radius * (float) Math.sin(angle);
                                    }
                                }
                            }
                        } else {
                            if (i > endangle && i < startAngle) {
                                if (k == 0) {
                                    point1.x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                    point1.y = CentroRaggio.center.y + CentroRaggio.radius * (float) Math.sin(angle);
                                    k = 1;
                                } else {
                                    //canvas.drawPoint((float) CentroRaggio.get(0) + (float) CentroRaggio.get(2) * (float) Math.cos(angle), (float) CentroRaggio.get(1) + (float) CentroRaggio.get(2) * (float) Math.sin(angle),paint1);
                                    float x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                    if (x > point1.x) {
                                        point1.x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                        point1.y = CentroRaggio.center.y + CentroRaggio.radius * (float) Math.sin(angle);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
            //endregion

            //region MostRightPoint
            PointF point = new PointF();
            try {
                int k = 0;
                if (M2 <= 1) {
                    float l = (float) Math.sqrt((float) Math.pow(Math.abs((bmp.getWidth() / 2 - S2) - Lb), 2) + (float) Math.pow(Math.abs((H2 + Hb) - Hb), 2)); //lunghezza linea
                    float rest = l % 5;
                    float NLP = 5;
                    if (rest != 0) {
                        float lrest = (l - rest) / 5;
                        NLP = l / (lrest + 1);
                    }
                    for (int il = 0; il * NLP <= l; il++) {
                        ArrayList<PointF> p = MathGeoTri.CircleStraightLineIntersection(new PointF(Lb, Hb), new PointF((bmp.getWidth() / 2 - S2), (H2 + Hb)), new PointF(Lb, Hb), NLP * il);
                        if (k == 0) {
                            point = new PointF(p.get(0).x, p.get(0).y);
                            k = 1;
                        } else {
                            if (p.get(0).x < point.x) {
                                point = new PointF(p.get(0).x, p.get(0).y);
                            }
                        }
                    }
                } else {
                    for (double i = 0; i < 360; i = i + 0.2) {
                        double angle = (2 * Math.PI / 360) * i;
                        if (MathGeoTri.ArcDirection_StartMidEnd(new PointF(Lb, Hb), new PointF(M2b, Hb + Float.parseFloat(eT_H.getText().toString()) / 2), new PointF(bmp.getWidth() / 2 - S2, H2 + Hb))) {
                            if (i > endangle || i < startAngle) {
                                if (k == 0) {
                                    point.x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                    point.y = CentroRaggio.center.y + CentroRaggio.radius * (float) Math.sin(angle);
                                    k = 1;
                                } else {
                                    float x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                    if (x < point.x) {
                                        point.x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                        point.y = CentroRaggio.center.y + CentroRaggio.radius * (float) Math.sin(angle);
                                    }
                                }
                            }
                        } else {
                            if (i > startAngle && i < endangle) {
                                if (k == 0) {
                                    point.x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                    point.y = CentroRaggio.center.y + CentroRaggio.radius * (float) Math.sin(angle);
                                    k = 1;
                                } else {
                                    float x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                    if (x < point.x) {
                                        point.x = CentroRaggio.center.x + CentroRaggio.radius * (float) Math.cos(angle);
                                        point.y = CentroRaggio.center.y + CentroRaggio.radius * (float) Math.sin(angle);
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
            //endregion

            //region MostTopPoint
            PointF top = new PointF();
            top.x = Lb;
            top.y = Hb;
            //endregion

            //region MostBotPoint
            PointF bot = new PointF();
            bot.x = Lb;
            //Confronto H1 e H2
            float max = Math.max(H2 + Hb, H1 + Hb);
            //Confronto il risultato prima con H
            bot.y = Math.max(max, H + Hb);
            //endregion

            //Trovo le dimensioni della figura
            double h = Math.abs(bot.y - top.y);
            double w = Math.abs(point1.x - point.x);
            s = (float) Math.max(h, w);
            //la scalo in modo che sia ingrandita/rimpicciolita
            canvas.scale(300 / s, 300 / s, 200, 200);
            paint.setStrokeWidth(s / 300 * 2);
            canvas.drawColor(Color.GRAY);

            //region ReDraw
            DrawImage_ar_2_sch(canvas, paint, paint1, Lb, Hb, L, H, H1, H2, S1, S2, P, M1, M2, M3, M4);
            //endregion
        } else if (image.equals("q_1_sch")) {
            //region TakeValue
            EditText eT_L = findViewById(R.id.editText_L);
            EditText eT_H = findViewById(R.id.editText_H);

            float L = Float.parseFloat(eT_L.getText().toString());
            float H = Float.parseFloat(eT_H.getText().toString());
            //endregion

            bmp = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
            float Lb = (bmp.getWidth() - L) / 2;
            float Hb = (bmp.getHeight() - H) / 2;
            android.widget.ImageView img = findViewById(R.id.imageView7);
            final Canvas canvas = new Canvas(bmp);
            //Setto il colore dello sfondo
            canvas.drawColor(Color.GRAY);
            //region Paint
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(3);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStyle(Paint.Style.STROKE);

            Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint1.setColor(Color.GREEN);
            paint1.setStrokeWidth(10);
            paint1.setAntiAlias(true);
            paint1.setStrokeCap(Paint.Cap.ROUND);
            paint1.setStyle(Paint.Style.STROKE);
            //endregion
            img.setImageBitmap(bmp);

            DrawImage_q_1_sch(canvas, paint, paint1, Lb, Hb, L, H);

            //region MostLeftPoint
            PointF point1 = new PointF(Lb, Hb);
            //endregion

            //region MostRightPoint
            PointF point = new PointF(Lb + Float.parseFloat(eT_L.getText().toString()), Hb);
            //endregion

            //region MostTopPoint
            PointF top = new PointF();
            top.x = Lb;
            top.y = Hb;
            //endregion

            //region MostBotPoint
            PointF bot = new PointF();
            bot.x = Lb;
            //Confronto H1 e H2
            float max = Math.max(Float.parseFloat(eT_H.getText().toString()) + Hb, Float.parseFloat(eT_H.getText().toString()) + Hb);
            //Confronto il risultato prima con H
            bot.y = Math.max(max, Float.parseFloat(eT_H.getText().toString()) + Hb);
            //endregion

            //Trovo le dimensioni della figura
            double h = Math.abs(bot.y - top.y);
            double w = Math.abs(point1.x - point.x);
            s = (float) Math.max(h, w);
            //la scalo in modo che sia ingrandita/rimpicciolita
            canvas.scale(300 / s, 300 / s, 200, 200);
            paint.setStrokeWidth(s / 300 * 2);
            canvas.drawColor(Color.GRAY);

            DrawImage_q_1_sch(canvas, paint, paint1, Lb, Hb, L, H);
        } else if (image.equals("qa_1_sch")) {
            //region TakeValue
            EditText eT_L = findViewById(R.id.editText_L);
            //EditText eT_S2 = (EditText) findViewById(R.id.editText_S2);
            //EditText eT_S1 = (EditText) findViewById(R.id.editText_S1);
            //EditText eT_H1 = (EditText) findViewById(R.id.editText_H1);
            //EditText eT_H2 = (EditText) findViewById(R.id.editText_H2);
            EditText eT_H = findViewById(R.id.editText_H);
            EditText eT_M1 = findViewById(R.id.editText_M1);
            EditText eT_M2 = findViewById(R.id.editText_M2);

            float L = Float.parseFloat(eT_L.getText().toString());
            float H = Float.parseFloat(eT_H.getText().toString());
            float M1 = Float.parseFloat(eT_M1.getText().toString());
            float M2 = Float.parseFloat(eT_M2.getText().toString());
            //endregion

            bmp = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
            float Lb = (bmp.getWidth() - L) / 2;
            float Hb = (bmp.getHeight() - H) / 2;
            android.widget.ImageView img = findViewById(R.id.imageView7);
            final Canvas canvas = new Canvas(bmp);
            //Setto il colore dello sfondo
            canvas.drawColor(Color.GRAY);
            //region Paint
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(3);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStyle(Paint.Style.STROKE);

            Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint1.setColor(Color.GREEN);
            paint1.setStrokeWidth(10);
            paint1.setAntiAlias(true);
            paint1.setStrokeCap(Paint.Cap.ROUND);
            paint1.setStyle(Paint.Style.STROKE);
            //endregion
            img.setImageBitmap(bmp);

            DrawImage_qa_1_sch(canvas, paint, paint1, Lb, Hb, L, H, M1, M2);

            //region MostLeftPoint
            PointF point1 = new PointF(Lb, Hb);
            //endregion

            //region MostRightPoint
            PointF point = new PointF(Lb + L, Hb);
            //endregion

            //region MostTopPoint
            PointF top = new PointF();
            top.x = Lb;
            top.y = Hb;
            //endregion

            //region MostBotPoint
            PointF bot = new PointF();
            bot.x = Lb;
            //Confronto H1 e H2
            //Confronto il risultato prima con H
            bot.y = H + Hb;
            //endregion

            //Trovo le dimensioni della figura
            double h = Math.abs(bot.y - top.y);
            double w = Math.abs(point1.x - point.x);
            s = (float) Math.max(h, w);
            //la scalo in modo che sia ingrandita/rimpicciolita
            canvas.scale(300 / s, 300 / s, 200, 200);
            paint.setStrokeWidth(s / 300 * 2);
            canvas.drawColor(Color.GRAY);

            DrawImage_qa_1_sch(canvas, paint, paint1, Lb, Hb, L, H, M1, M2);

        } else if (image.equals("qa_2_sch")) {
            //region TakeValue
            EditText eT_L = findViewById(R.id.editText_L);
            //EditText eT_S2 = (EditText) findViewById(R.id.editText_S2);
            EditText eT_S1 = findViewById(R.id.editText_S1);
            EditText eT_H1 = findViewById(R.id.editText_H1);
            EditText eT_H2 = findViewById(R.id.editText_H2);
            EditText eT_H = findViewById(R.id.editText_H);
            EditText eT_M1 = findViewById(R.id.editText_M1);
            EditText eT_M2 = findViewById(R.id.editText_M2);

            float L = Float.parseFloat(eT_L.getText().toString());
            float S1 = Float.parseFloat(eT_S1.getText().toString());
            float H1 = Float.parseFloat(eT_H1.getText().toString());
            float H2 = Float.parseFloat(eT_H2.getText().toString());
            float H = Float.parseFloat(eT_H.getText().toString());
            float M1 = Float.parseFloat(eT_M1.getText().toString());
            float M2 = Float.parseFloat(eT_M2.getText().toString());

            if (S1 + M2 + M1 < L) {
                //Misure errate archi
                Toast.makeText(this, "S1 + M2 + M1 must less L", Toast.LENGTH_LONG).show();
                return false;
            }

            //endregion
            bmp = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
            float Lb = (bmp.getWidth() - L) / 2;
            float Hb = (bmp.getHeight() - H) / 2;
            android.widget.ImageView img = findViewById(R.id.imageView7);
            final Canvas canvas = new Canvas(bmp);
            //Setto il colore dello sfondo
            canvas.drawColor(Color.GRAY);
            //region Paint
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(3);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStyle(Paint.Style.STROKE);

            Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint1.setColor(Color.GREEN);
            paint1.setStrokeWidth(10);
            paint1.setAntiAlias(true);
            paint1.setStrokeCap(Paint.Cap.ROUND);
            paint1.setStyle(Paint.Style.STROKE);
            //endregion
            img.setImageBitmap(bmp);

            DrawImage_qa_2_sch(canvas, paint, paint1, Lb, Hb, L, S1, H1, H2, H, M1, M2);

            //region MostLeftPoint
            PointF point1 = new PointF(Lb, Hb);
            //endregion

            //region MostRightPoint
            PointF point = new PointF(Lb + L, Hb);
            //endregion

            //region MostTopPoint
            PointF top = new PointF();
            top.x = Lb;
            top.y = Hb;
            //endregion

            //region MostBotPoint
            PointF bot = new PointF();
            bot.x = Lb;
            //Confronto H1 e H2
            bot.y = H + Hb;
            //endregion

            //Trovo le dimensioni della figura
            double h = Math.abs(bot.y - top.y);
            double w = Math.abs(point1.x - point.x);
            s = (float) Math.max(h, w);
            //la scalo in modo che sia ingrandita/rimpicciolita
            canvas.scale(300 / s, 300 / s, 200, 200);
            paint.setStrokeWidth(s / 300 * 2);
            canvas.drawColor(Color.GRAY);

            DrawImage_qa_2_sch(canvas, paint, paint1, Lb, Hb, L, S1, H1, H2, H, M1, M2);

        } else if (image.equals("qs_1_sch")) {
            //region TakeValue
            EditText eT_L = findViewById(R.id.editText_L);
            EditText eT_S2 = findViewById(R.id.editText_S2);
            EditText eT_S1 = findViewById(R.id.editText_S1);
            EditText eT_H1 = findViewById(R.id.editText_H1);
            EditText eT_H2 = findViewById(R.id.editText_H2);
            EditText eT_H = findViewById(R.id.editText_H);
            //EditText eT_M1 = (EditText) findViewById(R.id.editText_M1);
            //EditText eT_M2 = (EditText) findViewById(R.id.editText_M2);

            float L = Float.parseFloat(eT_L.getText().toString());
            float H1 = Float.parseFloat(eT_H1.getText().toString());
            float H2 = Float.parseFloat(eT_H2.getText().toString());
            float H = Float.parseFloat(eT_H.getText().toString());
            float S1 = Float.parseFloat(eT_S1.getText().toString());
            float S2 = Float.parseFloat(eT_S2.getText().toString());
            //endregion

            bmp = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
            float Lb = (bmp.getWidth() - Float.parseFloat(eT_L.getText().toString())) / 2;
            float Hb = (bmp.getHeight() - Float.parseFloat(eT_H.getText().toString())) / 2;
            android.widget.ImageView img = findViewById(R.id.imageView7);
            final Canvas canvas = new Canvas(bmp);
            //Setto il colore dello sfondo
            canvas.drawColor(Color.GRAY);
            //region Paint
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(3);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStyle(Paint.Style.STROKE);

            Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint1.setColor(Color.GREEN);
            paint1.setStrokeWidth(10);
            paint1.setAntiAlias(true);
            paint1.setStrokeCap(Paint.Cap.ROUND);
            paint1.setStyle(Paint.Style.STROKE);
            //endregion
            img.setImageBitmap(bmp);

            DrawImage_qs_1_sch(canvas, paint, paint1, Lb, Hb, L, H, H1, H2, S1, S2);

            //region MostLeftPoint
            PointF point1 = new PointF(Lb, Hb);
            //endregion

            //region MostRightPoint
            PointF point = new PointF(Lb + Float.parseFloat(eT_L.getText().toString()), Hb);
            //endregion

            //region MostTopPoint
            PointF top = new PointF();
            top.x = Lb;
            top.y = Hb;
            //endregion

            //region MostBotPoint
            PointF bot = new PointF();
            bot.x = Lb;
            //Confronto H1 e H2
            float max = Math.max(Float.parseFloat(eT_H.getText().toString()) + Hb, Float.parseFloat(eT_H.getText().toString()) + Hb);
            //Confronto il risultato prima con H
            bot.y = Math.max(max, Float.parseFloat(eT_H.getText().toString()) + Hb);
            //endregion

            //Trovo le dimensioni della figura
            double h = Math.abs(bot.y - top.y);
            double w = Math.abs(point1.x - point.x);
            s = (float) Math.max(h, w);
            //la scalo in modo che sia ingrandita/rimpicciolita
            canvas.scale(300 / s, 300 / s, 200, 200);
            paint.setStrokeWidth(s / 300 * 2);
            canvas.drawColor(Color.GRAY);

            DrawImage_qs_1_sch(canvas, paint, paint1, Lb, Hb, L, H, H1, H2, S1, S2);
        } else if (image.equals("ar3_1_sch")) {
            //region TakeValue
            EditText eT_L = findViewById(R.id.editText_L);
            EditText eT_P = findViewById(R.id.editText_P);
            EditText eT_S2 = findViewById(R.id.editText_S2);
            EditText eT_S1 = findViewById(R.id.editText_S1);
            EditText eT_S3 = findViewById(R.id.editText_S3);
            EditText eT_S4 = findViewById(R.id.editText_S4);
            EditText eT_H1 = findViewById(R.id.editText_H1);
            EditText eT_H2 = findViewById(R.id.editText_H2);
            EditText eT_H3 = findViewById(R.id.editText_H3);
            EditText eT_H4 = findViewById(R.id.editText_H4);
            EditText eT_H = findViewById(R.id.editText_H);
            EditText eT_M1 = findViewById(R.id.editText_M1);
            EditText eT_M2 = findViewById(R.id.editText_M2);
            EditText eT_M3 = findViewById(R.id.editText_M3);
            EditText eT_M4 = findViewById(R.id.editText_M4);
            EditText eT_M5 = findViewById(R.id.editText_M5);
            EditText eT_M6 = findViewById(R.id.editText_M6);

            float L = Float.parseFloat(eT_L.getText().toString());
            float P = Float.parseFloat(eT_P.getText().toString());
            float H1 = Float.parseFloat(eT_H1.getText().toString());
            float H2 = Float.parseFloat(eT_H2.getText().toString());
            float H3 = Float.parseFloat(eT_H3.getText().toString());
            float H4 = Float.parseFloat(eT_H4.getText().toString());
            float H = Float.parseFloat(eT_H.getText().toString());
            float S1 = Float.parseFloat(eT_S1.getText().toString());
            float S2 = Float.parseFloat(eT_S2.getText().toString());
            float S3 = Float.parseFloat(eT_S3.getText().toString());
            float S4 = Float.parseFloat(eT_S4.getText().toString());
            float M1 = Float.parseFloat(eT_M1.getText().toString());
            float M2 = Float.parseFloat(eT_M2.getText().toString());
            float M3 = Float.parseFloat(eT_M3.getText().toString());
            float M4 = Float.parseFloat(eT_M4.getText().toString());
            float M5 = Float.parseFloat(eT_M5.getText().toString());
            float M6 = Float.parseFloat(eT_M6.getText().toString());
            //endregion

            bmp = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
            float Lb = (bmp.getWidth() - Float.parseFloat(eT_L.getText().toString())) / 2;
            float Hb = (bmp.getHeight() - Float.parseFloat(eT_H.getText().toString())) / 2;
            android.widget.ImageView img = findViewById(R.id.imageView7);
            final Canvas canvas = new Canvas(bmp);
            //Setto il colore dello sfondo
            canvas.drawColor(Color.GRAY);
            //region Paint
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStrokeWidth(3);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStyle(Paint.Style.STROKE);

            Paint paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint1.setColor(Color.GREEN);
            paint1.setStrokeWidth(10);
            paint1.setAntiAlias(true);
            paint1.setStrokeCap(Paint.Cap.ROUND);
            paint1.setStyle(Paint.Style.STROKE);
            //endregion
            img.setImageBitmap(bmp);

            DrawImage_ar3_1_sch(canvas, paint, paint1, Lb, Hb, L, H, P, H1, H2, H3, H4, S1, S2, S3, S4, M1, M2, M3, M4, M5, M6);

            //region MostLeftPoint
            PointF point1 = new PointF(Lb, Hb);
            //endregion

            //region MostRightPoint
            PointF point = new PointF(Lb + Float.parseFloat(eT_L.getText().toString()), Hb);
            //endregion

            //region MostTopPoint
            PointF top = new PointF();
            top.x = Lb;
            top.y = Hb;
            //endregion

            //region MostBotPoint
            PointF bot = new PointF();
            bot.x = Lb;
            //Confronto H1 e H2
            float max = Math.max(Float.parseFloat(eT_H.getText().toString()) + Hb, Float.parseFloat(eT_H.getText().toString()) + Hb);
            //Confronto il risultato prima con H
            bot.y = Math.max(max, Float.parseFloat(eT_H.getText().toString()) + Hb);
            //endregion

            //Trovo le dimensioni della figura
            double h = Math.abs(bot.y - top.y);
            double w = Math.abs(point1.x - point.x);
            s = (float) Math.max(h, w);
            //la scalo in modo che sia ingrandita/rimpicciolita
            canvas.scale(300 / s, 300 / s, 200, 200);
            paint.setStrokeWidth(s / 300 * 2);
            canvas.drawColor(Color.GRAY);

            DrawImage_ar3_1_sch(canvas, paint, paint1, Lb, Hb, L, H, P, H1, H2, H3, H4, S1, S2, S3, S4, M1, M2, M3, M4, M5, M6);
        } else {
            return false;
        }

        return true;
    }

    /**
     * Function for draw the qs pocket
     *
     * @param canvas
     * @param paint
     * @param paint1
     * @param Lb
     * @param Hb
     * @param L
     * @param H
     * @param H1
     * @param H2
     * @param S1
     * @param S2
     */
    private void DrawImage_qs_1_sch(Canvas canvas, Paint paint, Paint paint1, float Lb, float Hb, float L, float H, float H1, float H2, float S1, float S2) {
        //LDX
        canvas.drawLine(Lb, Hb, L + Lb, Hb, paint);
        //LSX
        canvas.drawLine(Lb, Hb, Lb, Hb + (H - H2), paint);
        //LBOTTOM
        canvas.drawLine(Lb + L, Hb, Lb + L, Hb + (H - H1), paint);
        //LTOP
        canvas.drawLine(Lb + S2, Hb + H, Lb + (L - S1), Hb + H, paint);

        PointF P1 = new PointF(bmp.getWidth() / 2 + L / 2, H + Hb - H1);
        PointF P3 = new PointF(bmp.getWidth() / 2 + L / 2 - S1, H + Hb);

        canvas.drawLine(P1.x, P1.y, P3.x, P3.y, paint);

        P1 = new PointF(bmp.getWidth() / 2 - L / 2, H + Hb - H2);
        P3 = new PointF(bmp.getWidth() / 2 - L / 2 + S2, H + Hb);

        canvas.drawLine(P1.x, P1.y, P3.x, P3.y, paint);
    }

    /**
     * Function for draw the ar3 pocket
     *
     * @param canvas
     * @param paint
     * @param paint1
     * @param Lb
     * @param Hb
     * @param L
     * @param H
     * @param P
     * @param H1
     * @param H2
     * @param H3
     * @param H4
     * @param S1
     * @param S2
     * @param S3
     * @param S4
     * @param M1
     * @param M2
     * @param M3
     * @param M4
     * @param M5
     * @param M6
     */
    private void DrawImage_ar3_1_sch(Canvas canvas, Paint paint, Paint paint1, float Lb, float Hb, float L, float H, float P, float H1, float H2, float H3, float H4, float S1, float S2, float S3, float S4, float M1, float M2, float M3, float M4, float M5, float M6) {

        float CenterX = bmp.getWidth() / 2;

        //L
        canvas.drawLine(Lb, Hb, L + Lb, Hb, paint);

        PointF P1 = new PointF(CenterX + L / 2, Hb);
        PointF P3 = new PointF(CenterX + S1, H1 + Hb);
        PointF P5 = new PointF(CenterX + S3, H3 + Hb);
        PointF P7 = new PointF(CenterX + P, H + Hb);
        PointF P9 = new PointF(CenterX - S4, H4 + Hb);
        PointF P11 = new PointF(CenterX - S2, H2 + Hb);
        PointF P13 = new PointF(CenterX - L / 2, Hb);

        //M1
        if (M1 <= 1) {
            canvas.drawLine(P1.x, P1.y, P3.x, P3.y, paint);
        } else {
            float Lunghezza_corda = MathGeoTri.Distance(P1.x, P1.y, P3.x, P3.y);
            float raggio = ((M1 * M1) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * M1);

            PointF Centro_arco = MathGeoTri.CalculateCenterPoint(P1, P3, raggio, false);

            RectF oval = new RectF(Centro_arco.x - raggio, Centro_arco.y - raggio, Centro_arco.x + raggio, Centro_arco.y + raggio);
            float startAngle = (float) Math.atan2(P1.y - Centro_arco.y, P1.x - Centro_arco.x) * 180 / (float) Math.PI;
            float endangle = (float) Math.atan2(P3.y - Centro_arco.y, P3.x - Centro_arco.x) * 180 / (float) Math.PI;
            canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
        }

        //M2
        if (M2 <= 1) {
            canvas.drawLine(P11.x, P11.y, P13.x, P13.y, paint);
        } else {
            float Lunghezza_corda = MathGeoTri.Distance(P11.x, P11.y, P13.x, P13.y);
            float raggio = ((M2 * M2) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * M2);

            PointF Centro_arco = MathGeoTri.CalculateCenterPoint(P11, P13, raggio, false);

            RectF oval = new RectF(Centro_arco.x - raggio, Centro_arco.y - raggio, Centro_arco.x + raggio, Centro_arco.y + raggio);
            float startAngle = (float) Math.atan2(P11.y - Centro_arco.y, P11.x - Centro_arco.x) * 180 / (float) Math.PI;
            float endangle = (float) Math.atan2(P13.y - Centro_arco.y, P13.x - Centro_arco.x) * 180 / (float) Math.PI;
            canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
        }

        //M3
        if (M3 <= 1) {
            canvas.drawLine(P3.x, P3.y, P5.x, P5.y, paint);
        } else {
            float Lunghezza_corda = MathGeoTri.Distance(P3.x, P3.y, P5.x, P5.y);
            float raggio = ((M3 * M3) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * M3);

            PointF Centro_arco = MathGeoTri.CalculateCenterPoint(P3, P5, raggio, false);

            RectF oval = new RectF(Centro_arco.x - raggio, Centro_arco.y - raggio, Centro_arco.x + raggio, Centro_arco.y + raggio);
            float startAngle = (float) Math.atan2(P3.y - Centro_arco.y, P3.x - Centro_arco.x) * 180 / (float) Math.PI;
            float endangle = (float) Math.atan2(P5.y - Centro_arco.y, P5.x - Centro_arco.x) * 180 / (float) Math.PI;
            canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
        }

        //M4
        if (M4 <= 1) {
            canvas.drawLine(P9.x, P9.y, P11.x, P11.y, paint);
        } else {
            float Lunghezza_corda = MathGeoTri.Distance(P9.x, P9.y, P11.x, P11.y);
            float raggio = ((M4 * M4) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * M4);

            PointF Centro_arco = MathGeoTri.CalculateCenterPoint(P9, P11, raggio, false);

            PointF Pthird = MathGeoTri.CalculateArcThirdPoint(P9, P11, raggio, Centro_arco, false);

            RectF oval = new RectF(Centro_arco.x - raggio, Centro_arco.y - raggio, Centro_arco.x + raggio, Centro_arco.y + raggio);
            float startAngle = (float) Math.atan2(P9.y - Centro_arco.y, P9.x - Centro_arco.x) * 180 / (float) Math.PI;
            float endangle = (float) Math.atan2(P11.y - Centro_arco.y, P11.x - Centro_arco.x) * 180 / (float) Math.PI;

            if (endangle < 0) {
                endangle = endangle + 360;
            }

            if (MathGeoTri.ArcDirection_StartMidEnd(P9, Pthird, P11)) {
                canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
            } else {
                canvas.drawArc(oval, endangle, 360 - endangle + startAngle, false, paint);
            }
        }

        //M5
        if (M5 <= 1) {
            canvas.drawLine(P5.x, P5.y, P7.x, P7.y, paint);
        } else {
            float Lunghezza_corda = MathGeoTri.Distance(P5.x, P5.y, P7.x, P7.y);
            float raggio = ((M5 * M5) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * M5);

            PointF Centro_arco = MathGeoTri.CalculateCenterPoint(P5, P7, raggio, false);

            RectF oval = new RectF(Centro_arco.x - raggio, Centro_arco.y - raggio, Centro_arco.x + raggio, Centro_arco.y + raggio);
            float startAngle = (float) Math.atan2(P5.y - Centro_arco.y, P5.x - Centro_arco.x) * 180 / (float) Math.PI;
            float endangle = (float) Math.atan2(P7.y - Centro_arco.y, P7.x - Centro_arco.x) * 180 / (float) Math.PI;
            canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
        }

        //M6
        if (M6 <= 1) {
            canvas.drawLine(P7.x, P7.y, P9.x, P9.y, paint);
        } else {
            float Lunghezza_corda = MathGeoTri.Distance(P7.x, P7.y, P9.x, P9.y);
            float raggio = ((M6 * M6) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * M6);

            PointF Centro_arco = MathGeoTri.CalculateCenterPoint(P7, P9, raggio, false);

            RectF oval = new RectF(Centro_arco.x - raggio, Centro_arco.y - raggio, Centro_arco.x + raggio, Centro_arco.y + raggio);
            float startAngle = (float) Math.atan2(P7.y - Centro_arco.y, P7.x - Centro_arco.x) * 180 / (float) Math.PI;
            float endangle = (float) Math.atan2(P9.y - Centro_arco.y, P9.x - Centro_arco.x) * 180 / (float) Math.PI;
            canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
        }
    }

    /**
     * Function for draw the qa pocket
     *
     * @param canvas
     * @param paint
     * @param paint1
     * @param Lb
     * @param Hb
     * @param L
     * @param H
     * @param M1
     * @param M2
     */
    private void DrawImage_qa_1_sch(Canvas canvas, Paint paint, Paint paint1, float Lb, float Hb, float L, float H, float M1, float M2) {
        //LDX
        canvas.drawLine(Lb, Hb, L + Lb, Hb, paint);
        //LSX
        canvas.drawLine(Lb, Hb, Lb, Hb + (H - M2), paint);
        //LBOTTOM
        canvas.drawLine(Lb + L, Hb, Lb + L, Hb + H - M1, paint);
        //LTOP
        canvas.drawLine(Lb + M1, Hb + H, Lb + (L - M2), Hb + H, paint);

        PointF P1 = new PointF(bmp.getWidth() / 2 + L / 2, H + Hb - M1);
        PointF P3 = new PointF(bmp.getWidth() / 2 + L / 2 - M1, H + Hb);

        float raggio = M1;

        PointF Centro_arco = MathGeoTri.CalculateCenterPoint(P1, P3, raggio, false);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

        RectF oval = new RectF(Centro_arco.x - raggio, Centro_arco.y - raggio, Centro_arco.x + raggio, Centro_arco.y + raggio);
        float startAngle = (float) Math.atan2(P1.y - Centro_arco.y, P1.x - Centro_arco.x) * 180 / (float) Math.PI;
        float endangle = (float) Math.atan2(P3.y - Centro_arco.y, P3.x - Centro_arco.x) * 180 / (float) Math.PI;
        if (endangle < 0) {
            endangle = endangle + 360;
        }

        float pm1x = (P1.x + P3.x) / 2;
        float pm1y = (P1.y + P3.y) / 2;
        if (endangle < 0) {
            endangle = endangle + 360;
        }
        if (startAngle < 0) {
            startAngle = startAngle + 360;
        }

        ArrayList<PointF> points = MathGeoTri.CircleStraightLineIntersection(Centro_arco, new PointF(pm1x, pm1y), new PointF(pm1x, pm1y), raggio);

        float Dist1 = MathGeoTri.Distance(Centro_arco.x, Centro_arco.y, points.get(0).x, points.get(0).y);
        float Dist2 = MathGeoTri.Distance(Centro_arco.x, Centro_arco.y, points.get(1).x, points.get(1).y);

        PointF P2;

        if (Dist1 > Dist2) {
            P2 = points.get(0);
        } else {
            P2 = points.get(1);
        }


        if (!MathGeoTri.ArcDirection_StartMidEnd(P1, P2, P3)) {
            canvas.drawArc(oval, endangle, 360 - endangle + startAngle, false, paint);
        } else {
            canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
        }

        P1 = new PointF(bmp.getWidth() / 2 - L / 2, H + Hb - M2);
        P3 = new PointF(bmp.getWidth() / 2 - L / 2 + M2, H + Hb);

        raggio = M2;

        Centro_arco = MathGeoTri.CalculateCenterPoint(P1, P3, raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

        oval = new RectF(Centro_arco.x - raggio, Centro_arco.y - raggio, Centro_arco.x + raggio, Centro_arco.y + raggio);
        startAngle = (float) Math.atan2(P1.y - Centro_arco.y, P1.x - Centro_arco.x) * 180 / (float) Math.PI;
        endangle = (float) Math.atan2(P3.y - Centro_arco.y, P3.x - Centro_arco.x) * 180 / (float) Math.PI;
        if (endangle < 0) {
            endangle = endangle + 360;
        }

        pm1x = (P1.x + P3.x) / 2;
        pm1y = (P1.y + P3.y) / 2;
        if (endangle < 0) {
            endangle = endangle + 360;
        }
        if (startAngle < 0) {
            startAngle = startAngle + 360;
        }

        points = MathGeoTri.CircleStraightLineIntersection(Centro_arco, new PointF(pm1x, pm1y), new PointF(pm1x, pm1y), raggio);

        Dist1 = MathGeoTri.Distance(Centro_arco.x, Centro_arco.y, points.get(0).x, points.get(0).y);
        Dist2 = MathGeoTri.Distance(Centro_arco.x, Centro_arco.y, points.get(1).x, points.get(1).y);


        if (Dist1 > Dist2) {
            P2 = points.get(0);
        } else {
            P2 = points.get(1);
        }
        if (MathGeoTri.ArcDirection_StartMidEnd(P1, P2, P3)) {
            canvas.drawArc(oval, endangle, 360 - endangle + startAngle, false, paint);
        } else {
            canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
        }
    }

    /**
     * Function for draw the q pocket
     *
     * @param canvas
     * @param paint
     * @param paint1
     * @param Lb
     * @param Hb
     * @param L
     * @param H
     */
    private void DrawImage_q_1_sch(Canvas canvas, Paint paint, Paint paint1, float Lb, float Hb, float L, float H) {
        //L
        canvas.drawLine(Lb, Hb, L + Lb, Hb, paint);
        //H
        canvas.drawLine(Lb, Hb, Lb, H + Hb, paint);
        canvas.drawLine(Lb + L, Hb, Lb + L, Hb + H, paint);
        canvas.drawLine(Lb, Hb + H, Lb + L, Hb + H, paint);

    }

    /**
     * Function for draw the ar2 pocket
     *
     * @param canvas
     * @param paint
     * @param paint1
     * @param Lb
     * @param Hb
     * @param L
     * @param H
     * @param H1
     * @param H2
     * @param S1
     * @param S2
     * @param P
     * @param M1
     * @param M2
     * @param M3
     * @param M4
     */
    private void DrawImage_ar_2_sch(Canvas canvas, Paint paint, Paint paint1, float Lb, float Hb, float L, float H, float H1, float H2, float S1, float S2, float P, float M1, float M2, float M3, float M4) {
        //L
        canvas.drawLine(Lb, Hb, L + Lb, Hb, paint);

        PointF p3;
        ArrayList<PointF> Intersezioni = MathGeoTri.FindArcArcIntersections(new PointF(bmp.getWidth() / 2 + P, Hb + H), S1, new PointF(L + Lb, Hb), H1);  //trovo le due intersezioni del cerchietto con l'arco
        if (MathGeoTri.ArcDirection_StartMidEnd(new PointF(L + Lb, Hb), Intersezioni.get(0), new PointF(bmp.getWidth() / 2 + P, Hb + H))) {
            p3 = Intersezioni.get(0);
        } else {
            p3 = Intersezioni.get(1);
        }
        float H1_2 = p3.y - Hb; //eT_S1
        float S1_2 = p3.x - (bmp.getWidth() / 2 + P); //eT_H1

        Intersezioni = MathGeoTri.FindArcArcIntersections(new PointF(bmp.getWidth() / 2 + P, Hb + H), S2, new PointF(Lb, Hb), H2);  //trovo le due intersezioni del cerchietto con l'arco
        if (MathGeoTri.ArcDirection_StartMidEnd(new PointF(Lb, Hb), Intersezioni.get(0), new PointF(bmp.getWidth() / 2 + P, Hb + H))) {
            p3 = Intersezioni.get(1);
        } else {
            p3 = Intersezioni.get(0);
        }
        float H2_2 = p3.y - Hb; //eT_H2
        float S2_2 = (bmp.getWidth() / 2 + P) - p3.x; //eT_S2

        //S1
        PointF P1 = new PointF(bmp.getWidth() / 2 + P, H + Hb);
        PointF P3 = new PointF(bmp.getWidth() / 2 + S1_2, H1_2 + Hb);

        float Sagitta = M3;

        if (Sagitta <= 1) {
            //S1
            canvas.drawLine(bmp.getWidth() / 2 + P, H + Hb, bmp.getWidth() / 2 + S1_2, H1_2 + Hb, paint);
        } else {
            float Lunghezza_corda = MathGeoTri.Distance(P1.x, P1.y, P3.x, P3.y);
            float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

            PointF Centro_arco = MathGeoTri.CalculateCenterPoint(P1, P3, raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

            RectF oval = new RectF(Centro_arco.x - raggio, Centro_arco.y - raggio, Centro_arco.x + raggio, Centro_arco.y + raggio);
            float startAngle = (float) Math.atan2(P1.y - Centro_arco.y, P1.x - Centro_arco.x) * 180 / (float) Math.PI;
            float endangle = (float) Math.atan2(P3.y - Centro_arco.y, P3.x - Centro_arco.x) * 180 / (float) Math.PI;
            canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
        }


        //S2
        P1 = new PointF(bmp.getWidth() / 2 - S2_2, H2_2 + Hb);
        P3 = new PointF(bmp.getWidth() / 2 + P, H + Hb);

        Sagitta = M4;

        if (Sagitta <= 1) {
            //S2
            canvas.drawLine(bmp.getWidth() / 2 - S2_2, H2_2 + Hb, bmp.getWidth() / 2 + P, H + Hb, paint);
        } else {
            float Lunghezza_corda = MathGeoTri.Distance(P1.x, P1.y, P3.x, P3.y);
            float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

            PointF Centro_arco = MathGeoTri.CalculateCenterPoint(P1, P3, raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

            RectF oval = new RectF(Centro_arco.x - raggio, Centro_arco.y - raggio, Centro_arco.x + raggio, Centro_arco.y + raggio);
            float startAngle = (float) Math.atan2(P1.y - Centro_arco.y, P1.x - Centro_arco.x) * 180 / (float) Math.PI;
            float endangle = (float) Math.atan2(P3.y - Centro_arco.y, P3.x - Centro_arco.x) * 180 / (float) Math.PI;
            canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
        }


        //region ReDrawArcoDx
        float pmx = ((L + Lb) + (bmp.getWidth() / 2 + S1_2)) / 2;
        float pmy = (Hb + (H1_2 + Hb)) / 2;
        CenterPointRadius CentroRaggio = MathGeoTri.CalculateArc_CenterRadius(new PointF(L + Lb, Hb), new PointF(pmx + M1, pmy), new PointF(bmp.getWidth() / 2 + S1_2, H1_2 + Hb));
        RectF oval = new RectF(CentroRaggio.center.x - CentroRaggio.radius, CentroRaggio.center.y - CentroRaggio.radius, CentroRaggio.center.x + CentroRaggio.radius, CentroRaggio.center.y + CentroRaggio.radius);
        float startAngle = (float) Math.atan2(Hb - CentroRaggio.center.y, L + Lb - CentroRaggio.center.x) * 180 / (float) Math.PI;
        float endangle = (float) Math.atan2(H1_2 + Hb - CentroRaggio.center.y, bmp.getHeight() / 2 + S1_2 - CentroRaggio.center.x) * 180 / (float) Math.PI;
        if (endangle < 0) {
            endangle = endangle + 360;
        }

        if (M1 <= 1) {
            canvas.drawLine(L + Lb, Hb, bmp.getWidth() / 2 + S1_2, H1_2 + Hb, paint);
        } else {
            if (MathGeoTri.ArcDirection_StartMidEnd(new PointF(L + Lb, Hb), new PointF(pmx + M1, pmy), new PointF(bmp.getWidth() / 2 + S1_2, H1_2 + Hb))) {
                canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
            } else {
                canvas.drawArc(oval, endangle, 360 - endangle + startAngle, false, paint);
            }
        }
        //endregion

        //region ReDrawArcoSx
        float pm1x = (Lb + (bmp.getWidth() / 2 - S2_2)) / 2;
        float pm1y = (Hb + (H2_2 + Hb)) / 2;
        CentroRaggio = MathGeoTri.CalculateArc_CenterRadius(new PointF(Lb, Hb), new PointF(pm1x - M2, pm1y), new PointF(bmp.getWidth() / 2 - S2_2, H2_2 + Hb));
        oval = new RectF(CentroRaggio.center.x - CentroRaggio.radius, CentroRaggio.center.y - CentroRaggio.radius, CentroRaggio.center.x + CentroRaggio.radius, CentroRaggio.center.y + CentroRaggio.radius);
        endangle = (float) (Math.atan2(Hb - CentroRaggio.center.y, Lb - CentroRaggio.center.x) * 180 / (float) Math.PI);
        startAngle = (float) Math.atan2(H2_2 + Hb - CentroRaggio.center.y, bmp.getHeight() / 2 - S2_2 - CentroRaggio.center.x) * 180 / (float) Math.PI;
        if (endangle < 0) {
            endangle = endangle + 360;
        }

        if (M2 <= 1) {
            canvas.drawLine(Lb, Hb, (bmp.getWidth() / 2 - S2_2), (H2_2 + Hb), paint);
        } else {
            if (MathGeoTri.ArcDirection_StartMidEnd(new PointF(Lb, Hb), new PointF(pm1x - M2, pm1y), new PointF(200 - S2_2, H2_2 + Hb))) {
                canvas.drawArc(oval, endangle, 360 - endangle + startAngle, false, paint);
            } else {
                canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
            }
        }
        //endregion
    }

    /**
     * Function for draw the ar1 pocket
     *
     * @param canvas
     * @param paint
     * @param paint1
     * @param Lb
     * @param Hb
     * @param L
     * @param H
     * @param H1
     * @param H2
     * @param S1
     * @param S2
     * @param P
     * @param M1
     * @param M2
     * @param M3
     * @param M4
     */
    private void DrawImage_ar_1_sch(Canvas canvas, Paint paint, Paint paint1, float Lb, float Hb, float L, float H, float H1, float H2, float S1, float S2, float P, float M1, float M2, float M3, float M4) {
        //L
        canvas.drawLine(Lb, Hb, L + Lb, Hb, paint);

        //S1
        PointF P1 = new PointF(bmp.getWidth() / 2 + P, H + Hb);
        PointF P3 = new PointF(bmp.getWidth() / 2 + S1, H1 + Hb);

        float Sagitta = M3;

        if (Sagitta <= 1) {
            //S1
            canvas.drawLine(bmp.getWidth() / 2 + P, H + Hb, bmp.getWidth() / 2 + S1, H1 + Hb, paint);
        } else {
            float Lunghezza_corda = MathGeoTri.Distance(P1.x, P1.y, P3.x, P3.y);
            float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

            PointF Centro_arco = MathGeoTri.CalculateCenterPoint(P1, P3, raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

            RectF oval = new RectF(Centro_arco.x - raggio, Centro_arco.y - raggio, Centro_arco.x + raggio, Centro_arco.y + raggio);
            float startAngle = (float) Math.atan2(P1.y - Centro_arco.y, P1.x - Centro_arco.x) * 180 / (float) Math.PI;
            float endangle = (float) Math.atan2(P3.y - Centro_arco.y, P3.x - Centro_arco.x) * 180 / (float) Math.PI;
            canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
        }


        //S2
        P1 = new PointF(bmp.getWidth() / 2 - S2, H2 + Hb);
        P3 = new PointF(bmp.getWidth() / 2 + P, H + Hb);

        Sagitta = M4;

        if (Sagitta <= 1) {
            //S2
            canvas.drawLine(bmp.getWidth() / 2 - S2, H2 + Hb, bmp.getWidth() / 2 + P, H + Hb, paint);
        } else {
            float Lunghezza_corda = MathGeoTri.Distance(P1.x, P1.y, P3.x, P3.y);
            float raggio = ((Sagitta * Sagitta) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * Sagitta);

            PointF Centro_arco = MathGeoTri.CalculateCenterPoint(P1, P3, raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

            RectF oval = new RectF(Centro_arco.x - raggio, Centro_arco.y - raggio, Centro_arco.x + raggio, Centro_arco.y + raggio);
            float startAngle = (float) Math.atan2(P1.y - Centro_arco.y, P1.x - Centro_arco.x) * 180 / (float) Math.PI;
            float endangle = (float) Math.atan2(P3.y - Centro_arco.y, P3.x - Centro_arco.x) * 180 / (float) Math.PI;
            canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
        }


        //region ReDrawArcoDx
        float pmx = ((L + Lb) + (bmp.getWidth() / 2 + S1)) / 2;
        float pmy = (Hb + H1 + Hb) / 2;
        CenterPointRadius CentroRaggio = MathGeoTri.CalculateArc_CenterRadius(new PointF(L + Lb, Hb), new PointF(pmx + M1, pmy), new PointF(bmp.getWidth() / 2 + S1, H1 + Hb));
        RectF oval = new RectF(CentroRaggio.center.x - CentroRaggio.radius, CentroRaggio.center.y - CentroRaggio.radius, CentroRaggio.center.x + CentroRaggio.radius, CentroRaggio.center.y + CentroRaggio.radius);
        float startAngle = (float) Math.atan2(Hb - CentroRaggio.center.y, L + Lb - CentroRaggio.center.x) * 180 / (float) Math.PI;
        float endangle = (float) Math.atan2(H1 + Hb - CentroRaggio.center.y, bmp.getHeight() / 2 + S1 - CentroRaggio.center.x) * 180 / (float) Math.PI;
        if (endangle < 0) {
            endangle = endangle + 360;
        }

        if (M1 <= 1) {
            canvas.drawLine(L + Lb, Hb, bmp.getWidth() / 2 + S1, H1 + Hb, paint);
        } else {
            if (MathGeoTri.ArcDirection_StartMidEnd(new PointF(L + Lb, Hb), new PointF(pmx + M1, pmy), new PointF(bmp.getWidth() / 2 + S1, H1 + Hb))) {
                canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
            } else {
                canvas.drawArc(oval, endangle, 360 - endangle + startAngle, false, paint);
            }
        }
        //endregion

        //region ReDrawArcoSx
        float pm1x = (Lb + (bmp.getWidth() / 2 - S2)) / 2;
        float pm1y = (Hb + (H2 + Hb)) / 2;
        CentroRaggio = MathGeoTri.CalculateArc_CenterRadius(new PointF(Lb, Hb), new PointF(pm1x - M2, pm1y), new PointF(bmp.getWidth() / 2 - S2, H2 + Hb));
        oval = new RectF(CentroRaggio.center.x - CentroRaggio.radius, CentroRaggio.center.y - CentroRaggio.radius, CentroRaggio.center.x + CentroRaggio.radius, CentroRaggio.center.y + CentroRaggio.radius);
        endangle = (float) (Math.atan2(Hb - CentroRaggio.center.y, Lb - CentroRaggio.center.x) * 180 / (float) Math.PI);
        startAngle = (float) Math.atan2(H2 + Hb - CentroRaggio.center.y, bmp.getHeight() / 2 - S2 - CentroRaggio.center.x) * 180 / (float) Math.PI;
        if (endangle < 0) {
            endangle = endangle + 360;
        }

        if (M2 <= 1) {
            canvas.drawLine(Lb, Hb, (bmp.getWidth() / 2 - S2), (H2 + Hb), paint);
        } else {
            if (MathGeoTri.ArcDirection_StartMidEnd(new PointF(Lb, Hb), new PointF(pm1x - M2, pm1y), new PointF(200 - S2, H2 + Hb))) {
                canvas.drawArc(oval, endangle, 360 - endangle + startAngle, false, paint);
            } else {
                canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
            }
        }
        //endregion
    }

    /**
     * Function for draw the qa pocket
     *
     * @param canvas
     * @param paint
     * @param paint1
     * @param Lb
     * @param Hb
     * @param L
     * @param S1
     * @param H1
     * @param H2
     * @param H
     * @param M1
     * @param M2
     */
    private void DrawImage_qa_2_sch(Canvas canvas, Paint paint, Paint paint1, float Lb, float Hb, float L, float S1, float H1, float H2, float H, float M1, float M2) {
        //LDX
        canvas.drawLine(Lb, Hb, L + Lb, Hb, paint);
        //LSX
        canvas.drawLine(Lb, Hb, Lb, Hb + H1, paint);
        //LBOTTOM
        canvas.drawLine(Lb + L, Hb, Lb + L, Hb + H2, paint);
        //LTOP
        canvas.drawLine(Lb + L / 2 - S1 / 2, Hb + H, Lb + L / 2 + S1 / 2, Hb + H, paint);

        PointF P1 = new PointF(bmp.getWidth() / 2 + L / 2, Hb + H1);
        PointF P3 = new PointF(bmp.getWidth() / 2 + S1 / 2, Hb + H);

        float raggio = M1;

        PointF Centro_arco = MathGeoTri.CalculateCenterPoint(P1, P3, raggio, false);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

        RectF oval = new RectF(Centro_arco.x - raggio, Centro_arco.y - raggio, Centro_arco.x + raggio, Centro_arco.y + raggio);
        float startAngle = (float) Math.atan2(P1.y - Centro_arco.y, P1.x - Centro_arco.x) * 180 / (float) Math.PI;
        float endangle = (float) Math.atan2(P3.y - Centro_arco.y, P3.x - Centro_arco.x) * 180 / (float) Math.PI;
        if (endangle < 0) {
            endangle = endangle + 360;
        }

        float pm1x = (P1.x + P3.x) / 2;
        float pm1y = (P1.y + P3.y) / 2;
        if (endangle < 0) {
            endangle = endangle + 360;
        }
        if (startAngle < 0) {
            startAngle = startAngle + 360;
        }

        ArrayList<PointF> points = MathGeoTri.CircleStraightLineIntersection(Centro_arco, new PointF(pm1x, pm1y), new PointF(pm1x, pm1y), raggio);

        float Dist1 = MathGeoTri.Distance(Centro_arco.x, Centro_arco.y, points.get(0).x, points.get(0).y);
        float Dist2 = MathGeoTri.Distance(Centro_arco.x, Centro_arco.y, points.get(1).x, points.get(1).y);

        PointF P2;

        if (Dist1 > Dist2) {
            P2 = points.get(0);
        } else {
            P2 = points.get(1);
        }


        if (!MathGeoTri.ArcDirection_StartMidEnd(P1, P2, P3)) {
            canvas.drawArc(oval, endangle, 360 - endangle + startAngle, false, paint);
        } else {
            canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
        }

        P1 = new PointF(bmp.getWidth() / 2 - L / 2, Hb + H2);
        P3 = new PointF(bmp.getWidth() / 2 - S1 / 2, Hb + H);

        raggio = M2;

        Centro_arco = MathGeoTri.CalculateCenterPoint(P1, P3, raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

        oval = new RectF(Centro_arco.x - raggio, Centro_arco.y - raggio, Centro_arco.x + raggio, Centro_arco.y + raggio);
        startAngle = (float) Math.atan2(P1.y - Centro_arco.y, P1.x - Centro_arco.x) * 180 / (float) Math.PI;
        endangle = (float) Math.atan2(P3.y - Centro_arco.y, P3.x - Centro_arco.x) * 180 / (float) Math.PI;
        if (endangle < 0) {
            endangle = endangle + 360;
        }

        pm1x = (P1.x + P3.x) / 2;
        pm1y = (P1.y + P3.y) / 2;
        if (endangle < 0) {
            endangle = endangle + 360;
        }
        if (startAngle < 0) {
            startAngle = startAngle + 360;
        }

        points = MathGeoTri.CircleStraightLineIntersection(Centro_arco, new PointF(pm1x, pm1y), new PointF(pm1x, pm1y), raggio);

        Dist1 = MathGeoTri.Distance(Centro_arco.x, Centro_arco.y, points.get(0).x, points.get(0).y);
        Dist2 = MathGeoTri.Distance(Centro_arco.x, Centro_arco.y, points.get(1).x, points.get(1).y);


        if (Dist1 > Dist2) {
            P2 = points.get(0);
        } else {
            P2 = points.get(1);
        }
        if (MathGeoTri.ArcDirection_StartMidEnd(P1, P2, P3)) {
            canvas.drawArc(oval, endangle, 360 - endangle + startAngle, false, paint);
        } else {
            canvas.drawArc(oval, startAngle, endangle - startAngle, false, paint);
        }
    }

    /**
     * Function for go to the next pocket type image
     *
     * @param v
     */
    public void BtnNextModel(View v) {
        Draw = false;
        ViewFlipper vf1 = findViewById(R.id.ViewFlipper1);
        vf1.showNext();
        CheckLoadValues();
        ImageCheck();

    }

    /**
     * Function for go to the previous pocket type image
     *
     * @param v
     */
    public void BtnBackModel(View v) {
        Draw = false;
        ViewFlipper vf1 = findViewById(R.id.ViewFlipper1);
        vf1.showPrevious();
        CheckLoadValues();
        ImageCheck();

    }

    /**
     * Function for check if the selected image is equal to the last drew image, if yes load the saved values
     */
    private void CheckLoadValues() {
        String image = GetImageName();
        String linea = "";
        String pathFile="";
        try {
            if (!fileStr.isEmpty()) {
                int imgScheletro = Integer.parseInt(fileStr.get(0));
                if (image.equals("ar_1_sch")) {
                    pathFile = Environment.getExternalStorageDirectory() + "/JamData/LastValues.txt";
                } else if (image.equals("ar_2_sch")) {
                    pathFile = Environment.getExternalStorageDirectory() + "/JamData/LastValues1.txt";
                } else if (image.equals("q_1_sch")) {
                    pathFile = Environment.getExternalStorageDirectory() + "/JamData/LastValues2.txt";
                } else if (image.equals("qa_1_sch")) {
                    pathFile = Environment.getExternalStorageDirectory() + "/JamData/LastValues3.txt";
                } else if (image.equals("qa_2_sch")) {
                    pathFile = Environment.getExternalStorageDirectory() + "/JamData/LastValues4.txt";
                } else if (image.equals("qs_1_sch")) {
                    pathFile = Environment.getExternalStorageDirectory() + "/JamData/LastValues5.txt";
                } else if (image.equals("ar3_1_sch")) {
                    pathFile = Environment.getExternalStorageDirectory() + "/JamData/LastValues6.txt";
                }
            }/*
            if (!fileStr.isEmpty()) {
                int imgScheletro = Integer.parseInt(fileStr.get(0));
                if (image.equals("ar_1_sch") && imgScheletro == 0) {
                    pathFile = Environment.getExternalStorageDirectory() + "/JamData/LastValues.txt";
                } else if (image.equals("ar_2_sch") && imgScheletro == 1) {
                    pathFile = Environment.getExternalStorageDirectory() + "/JamData/LastValues1.txt";
                } else if (image.equals("q_1_sch") && imgScheletro == 2) {
                    pathFile = Environment.getExternalStorageDirectory() + "/JamData/LastValues2.txt";
                } else if (image.equals("qa_1_sch") && imgScheletro == 3) {
                    pathFile = Environment.getExternalStorageDirectory() + "/JamData/LastValues3.txt";
                } else if (image.equals("qa_2_sch") && imgScheletro == 4) {
                    pathFile = Environment.getExternalStorageDirectory() + "/JamData/LastValues4.txt";
                } else if (image.equals("qs_1_sch") && imgScheletro == 5) {
                    pathFile = Environment.getExternalStorageDirectory() + "/JamData/LastValues5.txt";
                } else if (image.equals("ar3_1_sch") && imgScheletro == 6) {
                    pathFile = Environment.getExternalStorageDirectory() + "/JamData/LastValues6.txt";
                }
            }*/

            if(pathFile != null && !pathFile.equals("")){
                try{
                   // File lastvalues = new File(pathFile);
                   // lastvalues.createNewFile();     //faccio un nuovo file per cancellare i dati vecchi
                    fileStr = new ArrayList<String>();   //svuoto Array
                    BufferedReader br = new BufferedReader(new FileReader(pathFile));
                    while ((linea = br.readLine()) != null) {
                        fileStr.add(linea);
                    }
                    br.close();
                    LoadLastValues();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            // Exception could happen if i add new values and i try to read the from the file the first time without them been initialized
            e.printStackTrace();
        }
    }

    /**
     * Load the saved pocket values
     * <p>
     * NOTE: Those need to have the order of the values in the file
     */
    public void  LoadLastValues() {
        EditText eT_L = findViewById(R.id.editText_L);
        EditText eT_H = findViewById(R.id.editText_H);
        EditText eT_P = findViewById(R.id.editText_P);
        EditText eT_S1 = findViewById(R.id.editText_S1);
        EditText eT_S2 = findViewById(R.id.editText_S2);
        EditText eT_S3 = findViewById(R.id.editText_S3);
        EditText eT_S4 = findViewById(R.id.editText_S4);
        EditText eT_H1 = findViewById(R.id.editText_H1);
        EditText eT_H2 = findViewById(R.id.editText_H2);
        EditText eT_H3 = findViewById(R.id.editText_H3);
        EditText eT_H4 = findViewById(R.id.editText_H4);
        EditText eT_M1 = findViewById(R.id.editText_M1);
        EditText eT_M2 = findViewById(R.id.editText_M2);
        EditText eT_M3 = findViewById(R.id.editText_M3);
        EditText eT_M4 = findViewById(R.id.editText_M4);
        EditText eT_M5 = findViewById(R.id.editText_M5);
        EditText eT_M6 = findViewById(R.id.editText_M6);

        String image = GetImageName();
        if (image.equals("ar_1_sch")) {
            eT_L.setText(fileStr.get(7));
            eT_H.setText(fileStr.get(11));
            eT_P.setText(fileStr.get(10));
            eT_S1.setText(fileStr.get(9));
            eT_S2.setText(fileStr.get(8));
            eT_H1.setText(fileStr.get(6));
            eT_H2.setText(fileStr.get(5));
            eT_M1.setText(fileStr.get(1));
            eT_M2.setText(fileStr.get(2));
            eT_M3.setText(fileStr.get(3));
            eT_M4.setText(fileStr.get(4));
        } else if (image.equals("ar_2_sch")) {
            eT_L.setText(fileStr.get(7));
            eT_H.setText(fileStr.get(11));
            eT_P.setText(fileStr.get(10));
            eT_M1.setText(fileStr.get(1));
            eT_M2.setText(fileStr.get(2));
            eT_M3.setText(fileStr.get(3));
            eT_M4.setText(fileStr.get(4));

            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.HALF_UP);

            double S1 = Math.sqrt(Math.pow(Float.parseFloat(fileStr.get(9)), 2) + Math.pow(Float.parseFloat(fileStr.get(11)) - Float.parseFloat(fileStr.get(6)), 2));
            eT_S1.setText(df.format(S1));

            double S2 = Math.sqrt(Math.pow(Float.parseFloat(fileStr.get(8)), 2) + Math.pow(Float.parseFloat(fileStr.get(11)) - Float.parseFloat(fileStr.get(5)), 2));
            eT_S2.setText(df.format(S2));

            double H1 = Math.sqrt(Math.pow(Float.parseFloat(fileStr.get(6)), 2) + Math.pow(Float.parseFloat(fileStr.get(7)) / 2 - Float.parseFloat(fileStr.get(9)), 2));
            eT_H1.setText(df.format(H1));

            double H2 = Math.sqrt(Math.pow(Float.parseFloat(fileStr.get(5)), 2) + Math.pow(Float.parseFloat(fileStr.get(7)) / 2 - Float.parseFloat(fileStr.get(8)), 2));
            eT_H2.setText(df.format(H2));

        } else if (image.equals("q_1_sch")) {
            eT_L.setText(fileStr.get(7));
            eT_H.setText(fileStr.get(11));
        } else if (image.equals("qa_1_sch")) {
            eT_L.setText(fileStr.get(7));
            eT_H.setText(fileStr.get(11));
            eT_M1.setText(fileStr.get(1));
            eT_M2.setText(fileStr.get(2));
        } else if (image.equals("qa_2_sch")) {
            eT_L.setText(fileStr.get(7));
            eT_H.setText(fileStr.get(11));
            eT_M1.setText(fileStr.get(1));
            eT_M2.setText(fileStr.get(2));
        } else if (image.equals("qs_1_sch")) {
            eT_L.setText(fileStr.get(7));
            eT_H.setText(fileStr.get(11));
            eT_S1.setText(fileStr.get(9));
            eT_S2.setText(fileStr.get(8));
            eT_H1.setText(fileStr.get(6));
            eT_H2.setText(fileStr.get(5));
        } else if (image.equals("ar3_1_sch")) {
            eT_L.setText(fileStr.get(7));
            eT_H.setText(fileStr.get(11));
            eT_P.setText(fileStr.get(10));
            eT_S1.setText(fileStr.get(9));
            eT_S2.setText(fileStr.get(8));
            eT_H1.setText(fileStr.get(6));
            eT_H2.setText(fileStr.get(5));
            eT_M1.setText(fileStr.get(1));
            eT_M2.setText(fileStr.get(2));
            eT_M3.setText(fileStr.get(3));
            eT_M4.setText(fileStr.get(4));
            eT_H3.setText(fileStr.get(12));
            eT_H4.setText(fileStr.get(13));
            eT_M5.setText(fileStr.get(14));
            eT_M6.setText(fileStr.get(15));
            eT_S3.setText(fileStr.get(16));
            eT_S4.setText(fileStr.get(17));
        }
    }

    /**
     * Function for check which values display
     */
    public void ImageCheck() {
        TextView t_L = findViewById(R.id.textView_L);
        EditText eT_L = findViewById(R.id.editText_L);
        t_L.setVisibility(View.GONE);
        eT_L.setVisibility(View.GONE);

        TextView t_H = findViewById(R.id.textView_H);
        EditText eT_H = findViewById(R.id.editText_H);
        t_H.setVisibility(View.GONE);
        eT_H.setVisibility(View.GONE);

        TextView t_P = findViewById(R.id.textView_P);
        EditText eT_P = findViewById(R.id.editText_P);
        t_P.setVisibility(View.GONE);
        eT_P.setVisibility(View.GONE);

        TextView t_S1 = findViewById(R.id.textView_S1);
        EditText eT_S1 = findViewById(R.id.editText_S1);
        t_S1.setVisibility(View.GONE);
        eT_S1.setVisibility(View.GONE);

        TextView t_S2 = findViewById(R.id.textView_S2);
        EditText eT_S2 = findViewById(R.id.editText_S2);
        t_S2.setVisibility(View.GONE);
        eT_S2.setVisibility(View.GONE);

        TextView t_S3 = findViewById(R.id.textView_S3);
        EditText eT_S3 = findViewById(R.id.editText_S3);
        t_S3.setVisibility(View.GONE);
        eT_S3.setVisibility(View.GONE);

        TextView t_S4 = findViewById(R.id.textView_S4);
        EditText eT_S4 = findViewById(R.id.editText_S4);
        t_S4.setVisibility(View.GONE);
        eT_S4.setVisibility(View.GONE);

        TextView t_H1 = findViewById(R.id.textView_H1);
        EditText eT_H1 = findViewById(R.id.editText_H1);
        t_H1.setVisibility(View.GONE);
        eT_H1.setVisibility(View.GONE);

        TextView t_H2 = findViewById(R.id.textView_H2);
        EditText eT_H2 = findViewById(R.id.editText_H2);
        t_H2.setVisibility(View.GONE);
        eT_H2.setVisibility(View.GONE);

        TextView t_H3 = findViewById(R.id.textView_H3);
        EditText eT_H3 = findViewById(R.id.editText_H3);
        t_H3.setVisibility(View.GONE);
        eT_H3.setVisibility(View.GONE);

        TextView t_H4 = findViewById(R.id.textView_H4);
        EditText eT_H4 = findViewById(R.id.editText_H4);
        t_H4.setVisibility(View.GONE);
        eT_H4.setVisibility(View.GONE);

        TextView t_M1 = findViewById(R.id.textView_M1);
        EditText eT_M1 = findViewById(R.id.editText_M1);
        t_M1.setVisibility(View.GONE);
        eT_M1.setVisibility(View.GONE);

        TextView t_M2 = findViewById(R.id.textView_M2);
        EditText eT_M2 = findViewById(R.id.editText_M2);
        t_M2.setVisibility(View.GONE);
        eT_M2.setVisibility(View.GONE);

        TextView t_M3 = findViewById(R.id.textView_M3);
        EditText eT_M3 = findViewById(R.id.editText_M3);
        t_M3.setVisibility(View.GONE);
        eT_M3.setVisibility(View.GONE);

        TextView t_M4 = findViewById(R.id.textView_M4);
        EditText eT_M4 = findViewById(R.id.editText_M4);
        t_M4.setVisibility(View.GONE);
        eT_M4.setVisibility(View.GONE);

        TextView t_M5 = findViewById(R.id.textView_M5);
        EditText eT_M5 = findViewById(R.id.editText_M5);
        t_M5.setVisibility(View.GONE);
        eT_M5.setVisibility(View.GONE);

        TextView t_M6 = findViewById(R.id.textView_M6);
        EditText eT_M6 = findViewById(R.id.editText_M6);
        t_M6.setVisibility(View.GONE);
        eT_M6.setVisibility(View.GONE);

        String image = GetImageName();
        if (image.equals("ar_1_sch")) {
            t_L.setVisibility(View.VISIBLE);
            eT_L.setVisibility(View.VISIBLE);
          //  eT_L.setText("160");

            t_H.setVisibility(View.VISIBLE);
            eT_H.setVisibility(View.VISIBLE);
          //  eT_H.setText("145");

            t_P.setVisibility(View.VISIBLE);
            eT_P.setVisibility(View.VISIBLE);
         //   eT_P.setText("0");

            t_S1.setVisibility(View.VISIBLE);
            eT_S1.setVisibility(View.VISIBLE);
          //  eT_S1.setText("63");

            t_S2.setVisibility(View.VISIBLE);
            eT_S2.setVisibility(View.VISIBLE);
          //  eT_S2.setText("63");

            t_H1.setVisibility(View.VISIBLE);
            eT_H1.setVisibility(View.VISIBLE);
         //   eT_H1.setText("128");

            t_H2.setVisibility(View.VISIBLE);
            eT_H2.setVisibility(View.VISIBLE);
          //  eT_H2.setText("128");

            t_M1.setVisibility(View.VISIBLE);
            eT_M1.setVisibility(View.VISIBLE);
          //  eT_M1.setText("0");

            t_M2.setVisibility(View.VISIBLE);
            eT_M2.setVisibility(View.VISIBLE);
          //  eT_M2.setText("0");

            t_M3.setVisibility(View.VISIBLE);
            eT_M3.setVisibility(View.VISIBLE);
         //   eT_M3.setText("0");

            t_M4.setVisibility(View.VISIBLE);
            eT_M4.setVisibility(View.VISIBLE);
        //    eT_M4.setText("0");

        } else if (image.equals("ar_2_sch")) {
            t_L.setVisibility(View.VISIBLE);
            eT_L.setVisibility(View.VISIBLE);
          //  eT_L.setText("160");

            t_H.setVisibility(View.VISIBLE);
            eT_H.setVisibility(View.VISIBLE);
         //   eT_H.setText("145");

            t_P.setVisibility(View.VISIBLE);
            eT_P.setVisibility(View.VISIBLE);
         //   eT_P.setText("0");

            t_S1.setVisibility(View.VISIBLE);
            eT_S1.setVisibility(View.VISIBLE);
          //  eT_S1.setText("63");

            t_S2.setVisibility(View.VISIBLE);
            eT_S2.setVisibility(View.VISIBLE);
         //   eT_S2.setText("63");

            t_H1.setVisibility(View.VISIBLE);
            eT_H1.setVisibility(View.VISIBLE);
         //   eT_H1.setText("128");

            t_H2.setVisibility(View.VISIBLE);
            eT_H2.setVisibility(View.VISIBLE);
         //   eT_H2.setText("128");

            t_M1.setVisibility(View.VISIBLE);
            eT_M1.setVisibility(View.VISIBLE);
         //   eT_M1.setText("0");

            t_M2.setVisibility(View.VISIBLE);
            eT_M2.setVisibility(View.VISIBLE);
         //   eT_M2.setText("0");

            t_M3.setVisibility(View.VISIBLE);
            eT_M3.setVisibility(View.VISIBLE);
         //   eT_M3.setText("0");

            t_M4.setVisibility(View.VISIBLE);
            eT_M4.setVisibility(View.VISIBLE);
          //  eT_M4.setText("0");
        } else if (image.equals("q_1_sch")) {
            t_L.setVisibility(View.VISIBLE);
            eT_L.setVisibility(View.VISIBLE);
           // eT_L.setText("160");

            t_H.setVisibility(View.VISIBLE);
            eT_H.setVisibility(View.VISIBLE);
           // eT_H.setText("145");
        } else if (image.equals("qa_1_sch")) {
            t_L.setVisibility(View.VISIBLE);
            eT_L.setVisibility(View.VISIBLE);
          //  eT_L.setText("160");

            t_H.setVisibility(View.VISIBLE);
            eT_H.setVisibility(View.VISIBLE);
          //  eT_H.setText("145");

            t_M1.setVisibility(View.VISIBLE);
            eT_M1.setVisibility(View.VISIBLE);
          //  eT_M1.setText("20");

            t_M2.setVisibility(View.VISIBLE);
            eT_M2.setVisibility(View.VISIBLE);
          //  eT_M2.setText("20");
        } else if (image.equals("qa_2_sch")) {
            t_L.setVisibility(View.VISIBLE);
            eT_L.setVisibility(View.VISIBLE);
         //  eT_L.setText("160");

            t_H.setVisibility(View.VISIBLE);
            eT_H.setVisibility(View.VISIBLE);
          //  eT_H.setText("145");

            t_S1.setVisibility(View.VISIBLE);
            eT_S1.setVisibility(View.VISIBLE);

            t_H1.setVisibility(View.VISIBLE);
            eT_H1.setVisibility(View.VISIBLE);

            t_H2.setVisibility(View.VISIBLE);
            eT_H2.setVisibility(View.VISIBLE);

            t_M1.setVisibility(View.VISIBLE);
            eT_M1.setVisibility(View.VISIBLE);
          //  eT_M1.setText("20");

            t_M2.setVisibility(View.VISIBLE);
            eT_M2.setVisibility(View.VISIBLE);
         //   eT_M2.setText("20");
        } else if (image.equals("qs_1_sch")) {
            t_L.setVisibility(View.VISIBLE);
            eT_L.setVisibility(View.VISIBLE);
          //  eT_L.setText("160");

            t_H.setVisibility(View.VISIBLE);
            eT_H.setVisibility(View.VISIBLE);
          //  eT_H.setText("145");

            t_S1.setVisibility(View.VISIBLE);
            eT_S1.setVisibility(View.VISIBLE);
         //   eT_S1.setText("20");

            t_S2.setVisibility(View.VISIBLE);
            eT_S2.setVisibility(View.VISIBLE);
          //  eT_S2.setText("20");

            t_H1.setVisibility(View.VISIBLE);
            eT_H1.setVisibility(View.VISIBLE);
          //  eT_H1.setText("20");

            t_H2.setVisibility(View.VISIBLE);
            eT_H2.setVisibility(View.VISIBLE);
          //  eT_H2.setText("20");
        } else if (image.equals("ar3_1_sch")) {
            t_L.setVisibility(View.VISIBLE);
            eT_L.setVisibility(View.VISIBLE);
          //  eT_L.setText("160");

            t_H.setVisibility(View.VISIBLE);
            eT_H.setVisibility(View.VISIBLE);
         //   eT_H.setText("145");

            t_S1.setVisibility(View.VISIBLE);
            eT_S1.setVisibility(View.VISIBLE);
         //   eT_S1.setText("60");

            t_S2.setVisibility(View.VISIBLE);
            eT_S2.setVisibility(View.VISIBLE);
          //  eT_S2.setText("60");

            t_S3.setVisibility(View.VISIBLE);
            eT_S3.setVisibility(View.VISIBLE);
         //   eT_S3.setText("50");

            t_S4.setVisibility(View.VISIBLE);
            eT_S4.setVisibility(View.VISIBLE);
         //   eT_S4.setText("50");

            t_H1.setVisibility(View.VISIBLE);
            eT_H1.setVisibility(View.VISIBLE);
         //   eT_H1.setText("100");

            t_H2.setVisibility(View.VISIBLE);
            eT_H2.setVisibility(View.VISIBLE);
         //   eT_H2.setText("100");

            t_H3.setVisibility(View.VISIBLE);
            eT_H3.setVisibility(View.VISIBLE);
         //   eT_H3.setText("120");

            t_H4.setVisibility(View.VISIBLE);
            eT_H4.setVisibility(View.VISIBLE);
          //  eT_H4.setText("120");

            t_M1.setVisibility(View.VISIBLE);
            eT_M1.setVisibility(View.VISIBLE);
         //   eT_M1.setText("0");

            t_M2.setVisibility(View.VISIBLE);
            eT_M2.setVisibility(View.VISIBLE);
         //   eT_M2.setText("0");

            t_M3.setVisibility(View.VISIBLE);
            eT_M3.setVisibility(View.VISIBLE);
          //  eT_M3.setText("0");

            t_M4.setVisibility(View.VISIBLE);
            eT_M4.setVisibility(View.VISIBLE);
          //  eT_M4.setText("0");

            t_M5.setVisibility(View.VISIBLE);
            eT_M5.setVisibility(View.VISIBLE);
          //  eT_M5.setText("0");

            t_M6.setVisibility(View.VISIBLE);
            eT_M6.setVisibility(View.VISIBLE);
         //   eT_M6.setText("0");
        }
    }

    /**
     * Button for exit from the draw pocket part and go back to tools
     *
     * @param v
     */
    public void BtnBack(View v) {
        finish();
        Values.Chiamante = "FDraw";
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
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_IMMERSIVE;

            // This work only for android 4.4+
            int currentApiVersion;
            currentApiVersion = android.os.Build.VERSION.SDK_INT;
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
