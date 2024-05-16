package com.jam_int.jt882_m8;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

public class FermatureActivity extends AppCompatActivity {
    Thread_LoopEmergenza thread_LoopEmergenza;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fermature);
        Log.d("JAM TAG", "FermatureActivity");

        thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
        thread_LoopEmergenza.thread_LoopEmergenza_Start(this);

        // Spinners for chose the number of repetition
        Spinner spRH1_start = findViewById(R.id.Spinner_NRepeatStart_H1);
        Spinner spRH1_end = findViewById(R.id.Spinner_NRepeatEnd_H1);
        Spinner spRH2_start = findViewById(R.id.Spinner_NRepeatStart_H2);
        Spinner spRH2_end = findViewById(R.id.Spinner_NRepeatEnd_H2);

        spRH1_start.setFocusable(false);

        // Possible repetition values
        String[] arraySpinner = new String[]{
                "1", "2", "3"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the possible values to every spinner
        spRH1_start.setAdapter(adapter);
        spRH1_end.setAdapter(adapter);
        spRH2_start.setAdapter(adapter);
        spRH2_end.setAdapter(adapter);

        // Set the default value
        spRH1_start.setSelection(1);
        spRH1_end.setSelection(0);
        spRH2_start.setSelection(1);
        spRH2_end.setSelection(0);

        EditText etnpFi1 = findViewById(R.id.EditText_NPointsStart_H1);
        EditText etnpFf1 = findViewById(R.id.EditText_NPointsEnd_H1);
        EditText etnpFi2 = findViewById(R.id.EditText_NPointsStart_H2);
        EditText etnpFf2 = findViewById(R.id.EditText_NPointsEnd_H2);

        // Set the default points number
        etnpFi1.setText(String.valueOf(Values.pFi1));
        etnpFf1.setText(String.valueOf(Values.pFf1));
        etnpFi2.setText(String.valueOf(Values.pFi2));
        etnpFf2.setText(String.valueOf(Values.pFf2));

        etnpFi1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FermatureActivity.this, etnpFi1, 50, 1, false, false, 5, false, "");
                }
                return false;
            }
        });

        etnpFf1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FermatureActivity.this, etnpFf1, 50, 1, false, false, 5, false, "");
                }
                return false;
            }
        });

        etnpFi2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FermatureActivity.this, etnpFi2, 50, 1, false, false, 5, false, "");
                }
                return false;
            }
        });

        etnpFf2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, FermatureActivity.this, etnpFf2, 50, 1, false, false, 5, false, "");
                }
                return false;
            }
        });

        //Controllo se e' 882 o 862, se e' 862 nascondo la parte della seconda testa
        if ((Values.type == 1 && Values.model == 0) ||
                (Values.type == 2 && Values.model == 0) ||
                (Values.type == 1 && Values.model == 1 && Values.model1 == 1) ||
                (Values.type == 2 && Values.model == 1 && Values.model1 == 1) ||
                (Values.type == 1 && Values.model == 1 && Values.model1 == 2) ||
                (Values.type == 2 && Values.model == 1 && Values.model1 == 2) ||
                (Values.type == 3 && Values.model == 1 && Values.model1 == 2) ||
                (Values.type == 4 && Values.model == 1 && Values.model1 == 2) ||
                (Values.type == 7 && Values.model == 1 && Values.model1 == 1) ||
                (Values.type == 8 && Values.model == 1 && Values.model1 == 1) ||
                (Values.type == 1 && Values.model == 2) ||
                (Values.type == 2 && Values.model == 2) ||
                (Values.type == 1 && Values.model == 3) ||
                (Values.type == 2 && Values.model == 3) ||
                (Values.type == 1 && Values.model == 4) ||
                (Values.type == 2 && Values.model == 4) ||
                (Values.type == 1 && Values.model == 5) ||
                (Values.type == 2 && Values.model == 5)) {
            LinearLayout ll2 = findViewById(R.id.LinearLayout_H2);
            ll2.setVisibility(View.GONE);
        }

        SetImages();

        ShowHideFermaturaValues();
    }

    /**
     * onPause
     */
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
            Log.d("JAM TAG", "FermatureActivity");

        }

    }
    /**
     * Function for init the images that the chose pocket type have
     */
    private void SetImages() {
        ImageView ImageView_StartH1_1 = findViewById(R.id.ImageView_StartH1_1);
        ImageView ImageView_StartH1_2 = findViewById(R.id.ImageView_StartH1_2);
        ImageView ImageView_EndH1_1 = findViewById(R.id.ImageView_EndH1_1);
        ImageView ImageView_EndH1_2 = findViewById(R.id.ImageView_EndH1_2);

        ImageView ImageView_StartH2_1 = findViewById(R.id.ImageView_StartH2_1);
        ImageView ImageView_StartH2_2 = findViewById(R.id.ImageView_StartH2_2);
        ImageView ImageView_EndH2_1 = findViewById(R.id.ImageView_EndH2_1);
        ImageView ImageView_EndH2_2 = findViewById(R.id.ImageView_EndH2_2);

        // Big if for choose the images to display
        if ((Values.model == 0 && Values.type == 1)) {
            ImageView_StartH1_1.setImageResource(R.drawable.f1_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f1_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f1_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f1_f2);
        } else if (Values.model == 0 && Values.type == 2) {
            ImageView_StartH1_1.setImageResource(R.drawable.f2_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f2_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f2_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f2_f2);
        } else if (Values.model == 0 && Values.type == 3) {
            ImageView_StartH1_1.setImageResource(R.drawable.f3_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f3_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f3_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f3_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f3_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f3_i2_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f3_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f3_f2_2);
        } else if (Values.model == 0 && Values.type == 4) {
            ImageView_StartH1_1.setImageResource(R.drawable.f4_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f4_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f4_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f4_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f4_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f4_i1_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f4_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f4_f1_2);
        } else if (Values.model == 0 && Values.type == 5) {
            ImageView_StartH1_1.setImageResource(R.drawable.f5_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f5_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f5_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f5_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f5_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f5_i2_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f5_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f5_f2_2);
        } else if (Values.model == 0 && Values.type == 6) {
            ImageView_StartH1_1.setImageResource(R.drawable.f6_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f6_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f6_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f6_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f6_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f6_i1_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f6_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f6_f1_2);
        } else if ((Values.type == 1 && Values.model == 1 && Values.model1 == 1)) {
            ImageView_StartH1_1.setImageResource(R.drawable.f1_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f1_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f1_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f1_f2);
        } else if (Values.model == 1 && Values.type == 2 && Values.model1 == 1) {
            ImageView_StartH1_1.setImageResource(R.drawable.f2_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f2_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f2_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f2_f2);
        } else if (Values.model == 1 && Values.type == 3 && Values.model1 == 1) {
            ImageView_StartH1_1.setImageResource(R.drawable.f3_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f3_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f3_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f3_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f3_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f3_i2_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f3_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f3_f2_2);
        } else if (Values.model == 1 && Values.type == 4 && Values.model1 == 1) {
            ImageView_StartH1_1.setImageResource(R.drawable.f4_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f4_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f4_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f4_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f4_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f4_i1_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f4_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f4_f1_2);
        } else if (Values.model == 1 && Values.type == 5 && Values.model1 == 1) {
            ImageView_StartH1_1.setImageResource(R.drawable.f5_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f5_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f5_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f5_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f5_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f5_i2_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f5_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f5_f2_2);
        } else if (Values.model == 1 && Values.type == 6 && Values.model1 == 1) {
            ImageView_StartH1_1.setImageResource(R.drawable.f6_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f6_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f6_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f6_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f6_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f6_i1_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f6_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f6_f1_2);
        } else if (Values.model == 1 && Values.type == 7 && Values.model1 == 1) {
            ImageView_StartH1_1.setImageResource(R.drawable.f6_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f6_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f6_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f6_f2);
        } else if (Values.model == 1 && Values.type == 8 && Values.model1 == 1) {
            ImageView_StartH1_1.setImageResource(R.drawable.f6_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f6_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f6_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f6_f2);
        } else if ((Values.type == 1 && Values.model == 1 && Values.model1 == 2)) {
            ImageView_StartH1_1.setImageResource(R.drawable.f1_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f1_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f1_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f1_f2);
        } else if ((Values.type == 2 && Values.model == 1 && Values.model1 == 2)) {
            ImageView_StartH1_1.setImageResource(R.drawable.f1_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f1_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f1_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f1_f2);
        } else if (Values.model == 1 && Values.type == 3 && Values.model1 == 2) {
            ImageView_StartH1_1.setImageResource(R.drawable.f2_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f2_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f2_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f2_f2);
        } else if (Values.model == 1 && Values.type == 4 && Values.model1 == 2) {
            ImageView_StartH1_1.setImageResource(R.drawable.f2_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f2_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f2_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f2_f2);
        } else if (Values.model == 1 && Values.type == 5 && Values.model1 == 2) {
            ImageView_StartH1_1.setImageResource(R.drawable.f3_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f3_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f3_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f3_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f3_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f3_i2_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f3_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f3_f2_2);
        } else if (Values.model == 1 && Values.type == 6 && Values.model1 == 2) {
            ImageView_StartH1_1.setImageResource(R.drawable.f3_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f3_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f3_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f3_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f3_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f3_i2_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f3_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f3_f2_2);
        } else if (Values.model == 1 && Values.type == 7 && Values.model1 == 2) {
            ImageView_StartH1_1.setImageResource(R.drawable.f4_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f4_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f4_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f4_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f4_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f4_i1_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f4_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f4_f1_2);
        } else if (Values.model == 1 && Values.type == 8 && Values.model1 == 2) {
            ImageView_StartH1_1.setImageResource(R.drawable.f4_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f4_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f4_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f4_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f4_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f4_i1_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f4_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f4_f1_2);
        } else if (Values.model == 1 && Values.type == 9 && Values.model1 == 2) {
            ImageView_StartH1_1.setImageResource(R.drawable.f5_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f5_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f5_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f5_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f5_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f5_i2_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f5_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f5_f2_2);
        } else if (Values.model == 1 && Values.type == 10 && Values.model1 == 2) {
            ImageView_StartH1_1.setImageResource(R.drawable.f5_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f5_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f5_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f5_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f5_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f5_i2_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f5_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f5_f2_2);
        } else if (Values.model == 1 && Values.type == 11 && Values.model1 == 2) {
            ImageView_StartH1_1.setImageResource(R.drawable.f6_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f6_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f6_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f6_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f6_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f6_i1_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f6_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f6_f1_2);
        } else if (Values.model == 1 && Values.type == 12 && Values.model1 == 2) {
            ImageView_StartH1_1.setImageResource(R.drawable.f6_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f6_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f6_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f6_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f6_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f6_i1_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f6_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f6_f1_2);
        } else if ((Values.model == 2 && Values.type == 1)) {
            ImageView_StartH1_1.setImageResource(R.drawable.f1_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f1_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f1_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f1_f2);
        } else if (Values.model == 2 && Values.type == 2) {
            ImageView_StartH1_1.setImageResource(R.drawable.f2_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f2_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f2_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f2_f2);
        } else if (Values.model == 2 && Values.type == 3) {
            ImageView_StartH1_1.setImageResource(R.drawable.f3_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f3_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f3_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f3_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f3_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f3_i2_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f3_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f3_f2_2);
        } else if (Values.model == 2 && Values.type == 4) {
            ImageView_StartH1_1.setImageResource(R.drawable.f4_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f4_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f4_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f4_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f4_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f4_i1_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f4_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f4_f1_2);
        } else if (Values.model == 2 && Values.type == 5) {
            ImageView_StartH1_1.setImageResource(R.drawable.f5_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f5_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f5_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f5_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f5_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f5_i2_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f5_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f5_f2_2);
        } else if (Values.model == 2 && Values.type == 6) {
            ImageView_StartH1_1.setImageResource(R.drawable.f6_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f6_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f6_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f6_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f6_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f6_i1_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f6_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f6_f1_2);
        } else if ((Values.model == 3 && Values.type == 1)) {
            ImageView_StartH1_1.setImageResource(R.drawable.f1_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f1_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f1_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f1_f2);
        } else if (Values.model == 3 && Values.type == 2) {
            ImageView_StartH1_1.setImageResource(R.drawable.f2_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f2_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f2_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f2_f2);
        } else if (Values.model == 3 && Values.type == 3) {
            ImageView_StartH1_1.setImageResource(R.drawable.f3_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f3_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f3_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f3_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f3_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f3_i2_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f3_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f3_f2_2);
        } else if (Values.model == 3 && Values.type == 4) {
            ImageView_StartH1_1.setImageResource(R.drawable.f4_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f4_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f4_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f4_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f4_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f4_i1_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f4_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f4_f1_2);
        } else if (Values.model == 3 && Values.type == 5) {
            ImageView_StartH1_1.setImageResource(R.drawable.f5_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f5_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f5_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f5_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f5_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f5_i2_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f5_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f5_f2_2);
        } else if (Values.model == 3 && Values.type == 6) {
            ImageView_StartH1_1.setImageResource(R.drawable.f6_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f6_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f6_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f6_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f6_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f6_i1_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f6_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f6_f1_2);
        } else if ((Values.model == 4 && Values.type == 1)) {
            ImageView_StartH1_1.setImageResource(R.drawable.f1_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f1_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f1_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f1_f2);
        } else if (Values.model == 4 && Values.type == 2) {
            ImageView_StartH1_1.setImageResource(R.drawable.f2_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f2_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f2_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f2_f2);
        } else if (Values.model == 4 && Values.type == 3) {
            ImageView_StartH1_1.setImageResource(R.drawable.f3_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f3_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f3_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f3_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f3_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f3_i2_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f3_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f3_f2_2);
        } else if (Values.model == 4 && Values.type == 4) {
            ImageView_StartH1_1.setImageResource(R.drawable.f4_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f4_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f4_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f4_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f4_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f4_i1_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f4_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f4_f1_2);
        } else if (Values.model == 4 && Values.type == 5) {
            ImageView_StartH1_1.setImageResource(R.drawable.f5_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f5_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f5_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f5_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f5_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f5_i2_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f5_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f5_f2_2);
        } else if (Values.model == 4 && Values.type == 6) {
            ImageView_StartH1_1.setImageResource(R.drawable.f6_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f6_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f6_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f6_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f6_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f6_i1_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f6_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f6_f1_2);
        } else if ((Values.model == 5 && Values.type == 1)) {
            ImageView_StartH1_1.setImageResource(R.drawable.f1_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f1_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f1_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f1_f2);
        } else if (Values.model == 5 && Values.type == 2) {
            ImageView_StartH1_1.setImageResource(R.drawable.f2_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f2_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f2_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f2_f2);
        } else if (Values.model == 5 && Values.type == 3) {
            ImageView_StartH1_1.setImageResource(R.drawable.f3_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f3_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f3_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f3_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f3_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f3_i2_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f3_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f3_f2_2);
        } else if (Values.model == 5 && Values.type == 4) {
            ImageView_StartH1_1.setImageResource(R.drawable.f4_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f4_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f4_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f4_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f4_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f4_i1_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f4_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f4_f1_2);
        } else if (Values.model == 5 && Values.type == 5) {
            ImageView_StartH1_1.setImageResource(R.drawable.f5_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f5_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f5_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f5_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f5_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f5_i2_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f5_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f5_f2_2);
        } else if (Values.model == 5 && Values.type == 6) {
            ImageView_StartH1_1.setImageResource(R.drawable.f6_i1);
            ImageView_StartH1_2.setImageResource(R.drawable.f6_i2);
            ImageView_EndH1_1.setImageResource(R.drawable.f6_f1);
            ImageView_EndH1_2.setImageResource(R.drawable.f6_f2);


            ImageView_StartH2_1.setImageResource(R.drawable.f6_i1_2);
            ImageView_StartH2_2.setImageResource(R.drawable.f6_i1_2);
            ImageView_EndH2_1.setImageResource(R.drawable.f6_f1_2);
            ImageView_EndH2_2.setImageResource(R.drawable.f6_f1_2);
        }
    }

    /**
     * Function for update the possible values of a fermatura
     */
    private void ShowHideFermaturaValues() {

        ViewFlipper vfStartH1 = findViewById(R.id.ViewFlipper_StartH1);
        ViewFlipper vfEndH1 = findViewById(R.id.ViewFlipper_EndH1);

        ViewFlipper vfStartH2 = findViewById(R.id.ViewFlipper_StartH2);
        ViewFlipper vfEndH2 = findViewById(R.id.ViewFlipper_EndH2);

        EditText etnpFi1 = findViewById(R.id.EditText_NPointsStart_H1);
        TextView txtvNPointsFi1 = findViewById(R.id.TextView_NPointsStart_H1);

        EditText etnpFf1 = findViewById(R.id.EditText_NPointsEnd_H1);
        TextView txtvNPointsFf1 = findViewById(R.id.TextView_NPointsEnd_H1);


        EditText etnpFi2 = findViewById(R.id.EditText_NPointsStart_H2);
        TextView txtvNPointsFi2 = findViewById(R.id.TextView_NPointsStart_H2);

        EditText etnpFf2 = findViewById(R.id.EditText_NPointsEnd_H2);
        TextView txtvNPointsFf2 = findViewById(R.id.TextView_NPointsEnd_H2);

        if ((Values.model == 0 && Values.type == 1)) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                default: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
            }
        } else if (Values.model == 0 && Values.type == 2) {
            switch (vfStartH1.getDisplayedChild()) {
                case 1: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 1: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 0 && Values.type == 3) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 0: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                case 0: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 0 && Values.type == 4) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 0 && Values.type == 5) {
            switch (vfStartH1.getDisplayedChild()) {
                case 1: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 1: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                case 1: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                case 1: {
                    etnpFf2.setVisibility(View.GONE);
                    txtvNPointsFf2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 0 && Values.type == 6) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 0: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                default: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if ((Values.type == 1 && Values.model == 1 && Values.model1 == 1)) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 1 && Values.type == 2 && Values.model1 == 1) {
            switch (vfStartH1.getDisplayedChild()) {
                case 1: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 1: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 1 && Values.type == 3 && Values.model1 == 1) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 0: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                case 0: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 1 && Values.type == 4 && Values.model1 == 1) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 1 && Values.type == 5 && Values.model1 == 1) {
            switch (vfStartH1.getDisplayedChild()) {
                case 1: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 1: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                case 1: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                case 1: {
                    etnpFf2.setVisibility(View.GONE);
                    txtvNPointsFf2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 1 && Values.type == 6 && Values.model1 == 1) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 0: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                default: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 1 && Values.type == 7 && Values.model1 == 1) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 0: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 1 && Values.type == 8 && Values.model1 == 1) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 0: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if ((Values.type == 1 && Values.model == 1 && Values.model1 == 2)) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if ((Values.type == 2 && Values.model == 1 && Values.model1 == 2)) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 1 && Values.type == 3 && Values.model1 == 2) {
            switch (vfStartH1.getDisplayedChild()) {
                case 1: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 1: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 1 && Values.type == 4 && Values.model1 == 2) {
            switch (vfStartH1.getDisplayedChild()) {
                case 1: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 1: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 1 && Values.type == 5 && Values.model1 == 2) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 0: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                case 0: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 1 && Values.type == 6 && Values.model1 == 2) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 0: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                case 0: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 1 && Values.type == 7 && Values.model1 == 2) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 1 && Values.type == 8 && Values.model1 == 2) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 1 && Values.type == 9 && Values.model1 == 2) {
            switch (vfStartH1.getDisplayedChild()) {
                case 1: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 1: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                case 1: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                case 1: {
                    etnpFf2.setVisibility(View.GONE);
                    txtvNPointsFf2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 1 && Values.type == 10 && Values.model1 == 2) {
            switch (vfStartH1.getDisplayedChild()) {
                case 1: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 1: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                case 1: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                case 1: {
                    etnpFf2.setVisibility(View.GONE);
                    txtvNPointsFf2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 1 && Values.type == 11 && Values.model1 == 2) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 0: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                default: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 1 && Values.type == 12 && Values.model1 == 2) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 0: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                default: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if ((Values.model == 2 && Values.type == 1)) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 2 && Values.type == 2) {
            switch (vfStartH1.getDisplayedChild()) {
                case 1: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 1: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 2 && Values.type == 3) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 0: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                case 0: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 2 && Values.type == 4) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 2 && Values.type == 5) {
            switch (vfStartH1.getDisplayedChild()) {
                case 1: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 1: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                case 1: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                case 1: {
                    etnpFf2.setVisibility(View.GONE);
                    txtvNPointsFf2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 2 && Values.type == 6) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 0: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                default: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if ((Values.model == 3 && Values.type == 1)) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 3 && Values.type == 2) {
            switch (vfStartH1.getDisplayedChild()) {
                case 1: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 1: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 3 && Values.type == 3) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 0: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                case 0: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 3 && Values.type == 4) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 3 && Values.type == 5) {
            switch (vfStartH1.getDisplayedChild()) {
                case 1: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 1: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                case 1: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                case 1: {
                    etnpFf2.setVisibility(View.GONE);
                    txtvNPointsFf2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 3 && Values.type == 6) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 0: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                default: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if ((Values.model == 4 && Values.type == 1)) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 4 && Values.type == 2) {
            switch (vfStartH1.getDisplayedChild()) {
                case 1: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 1: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 4 && Values.type == 3) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 0: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                case 0: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 4 && Values.type == 4) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 4 && Values.type == 5) {
            switch (vfStartH1.getDisplayedChild()) {
                case 1: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 1: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                case 1: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi2.setVisibility(View.VISIBLE);
                    txtvNPointsFi2.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                case 1: {
                    etnpFf2.setVisibility(View.GONE);
                    txtvNPointsFf2.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        } else if (Values.model == 4 && Values.type == 6) {
            switch (vfStartH1.getDisplayedChild()) {
                case 0: {
                    etnpFi1.setVisibility(View.GONE);
                    txtvNPointsFi1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFi1.setVisibility(View.VISIBLE);
                    txtvNPointsFi1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfEndH1.getDisplayedChild()) {
                case 0: {
                    etnpFf1.setVisibility(View.GONE);
                    txtvNPointsFf1.setVisibility(View.GONE);
                }
                break;
                default: {
                    etnpFf1.setVisibility(View.VISIBLE);
                    txtvNPointsFf1.setVisibility(View.VISIBLE);
                }
                break;
            }

            switch (vfStartH2.getDisplayedChild()) {
                default: {
                    etnpFi2.setVisibility(View.GONE);
                    txtvNPointsFi2.setVisibility(View.GONE);
                }
                break;
            }

            switch (vfEndH2.getDisplayedChild()) {
                default: {
                    etnpFf2.setVisibility(View.VISIBLE);
                    txtvNPointsFf2.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    }

    /**
     * Functions for go to the next or previous fermatura type
     */
    //#region NextBackImages
    public void BtnNext_StartH1(View view) {
        ViewFlipper vfStartH1 = findViewById(R.id.ViewFlipper_StartH1);
        vfStartH1.showNext();

        ShowHideFermaturaValues();
    }

    public void BtnBack_StartH1(View v) {
        ViewFlipper vfStartH1 = findViewById(R.id.ViewFlipper_StartH1);
        vfStartH1.showPrevious();

        ShowHideFermaturaValues();
    }

    public void BtnNext_EndH1(View view) {
        ViewFlipper vfEndH1 = findViewById(R.id.ViewFlipper_EndH1);
        vfEndH1.showNext();

        ShowHideFermaturaValues();
    }

    public void BtnBack_EndH1(View view) {
        ViewFlipper vfEndH1 = findViewById(R.id.ViewFlipper_EndH1);
        vfEndH1.showPrevious();

        ShowHideFermaturaValues();
    }

    public void BtnNext_StartH2(View view) {
        ViewFlipper vfStartH2 = findViewById(R.id.ViewFlipper_StartH2);
        vfStartH2.showNext();

        ShowHideFermaturaValues();
    }

    public void BtnBack_StartH2(View view) {
        ViewFlipper vfStartH2 = findViewById(R.id.ViewFlipper_StartH2);
        vfStartH2.showPrevious();

        ShowHideFermaturaValues();
    }

    public void BtnNext_EndH2(View view) {
        ViewFlipper vfEndH2 = findViewById(R.id.ViewFlipper_EndH2);
        vfEndH2.showNext();

        ShowHideFermaturaValues();
    }

    public void BtnBack_EndH2(View view) {
        ViewFlipper vfEndH2 = findViewById(R.id.ViewFlipper_EndH2);
        vfEndH2.showPrevious();

        ShowHideFermaturaValues();
    }
    //#endregion

    /**
     * Button for go back and save the chose settings
     *
     * @param view
     */
    public void BtnBackPage(View view) {

        //Save Fermature Types
        ViewFlipper vfStartH1 = findViewById(R.id.ViewFlipper_StartH1);
        ViewFlipper vfEndH1 = findViewById(R.id.ViewFlipper_EndH1);
        ViewFlipper vfStartH2 = findViewById(R.id.ViewFlipper_StartH2);
        ViewFlipper vfEndH2 = findViewById(R.id.ViewFlipper_EndH2);

        Values.Fi1 = vfStartH1.getDisplayedChild();
        Values.Ff1 = vfEndH1.getDisplayedChild();
        Values.Fi2 = vfStartH2.getDisplayedChild();
        Values.Ff2 = vfEndH2.getDisplayedChild();

        //Save Fermature Points number
        EditText etnpFi1 = findViewById(R.id.EditText_NPointsStart_H1);
        EditText etnpFf1 = findViewById(R.id.EditText_NPointsEnd_H1);
        EditText etnpFi2 = findViewById(R.id.EditText_NPointsStart_H2);
        EditText etnpFf2 = findViewById(R.id.EditText_NPointsEnd_H2);

        try {
            Values.pFi1 = Integer.parseInt(etnpFi1.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Values.pFf1 = Integer.parseInt(etnpFf1.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Values.pFi2 = Integer.parseInt(etnpFi2.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Values.pFf2 = Integer.parseInt(etnpFf2.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Save Fermature Repetition count
        Spinner spRH1_start = findViewById(R.id.Spinner_NRepeatStart_H1);
        Spinner spRH1_end = findViewById(R.id.Spinner_NRepeatEnd_H1);
        Spinner spRH2_start = findViewById(R.id.Spinner_NRepeatStart_H2);
        Spinner spRH2_end = findViewById(R.id.Spinner_NRepeatEnd_H2);

        Values.Fi1t = Integer.parseInt(spRH1_start.getSelectedItem().toString());
        Values.Ff1t = Integer.parseInt(spRH1_end.getSelectedItem().toString());
        Values.Fi2t = Integer.parseInt(spRH2_start.getSelectedItem().toString());
        Values.Ff2t = Integer.parseInt(spRH2_end.getSelectedItem().toString());

        finish();
    }

    /**
     * Button for set the default values
     *
     * @param view
     */
    public void BtnDefaultValues(View view) {

        // Reset EditTexts

        EditText etnpFi1 = findViewById(R.id.EditText_NPointsStart_H1);
        EditText etnpFf1 = findViewById(R.id.EditText_NPointsEnd_H1);
        EditText etnpFi2 = findViewById(R.id.EditText_NPointsStart_H2);
        EditText etnpFf2 = findViewById(R.id.EditText_NPointsEnd_H2);

        etnpFi1.setText(String.valueOf(3));
        etnpFf1.setText(String.valueOf(3));
        etnpFi2.setText(String.valueOf(3));
        etnpFf2.setText(String.valueOf(3));

        //Reset Spinners

        Spinner spRH1_start = findViewById(R.id.Spinner_NRepeatStart_H1);
        Spinner spRH1_end = findViewById(R.id.Spinner_NRepeatEnd_H1);
        Spinner spRH2_start = findViewById(R.id.Spinner_NRepeatStart_H2);
        Spinner spRH2_end = findViewById(R.id.Spinner_NRepeatEnd_H2);

        spRH1_start.setSelection(1);
        spRH1_end.setSelection(0);
        spRH2_start.setSelection(1);
        spRH2_end.setSelection(1);

        //Reset ViewFlippers

        ViewFlipper vfStartH1 = findViewById(R.id.ViewFlipper_StartH1);
        ViewFlipper vfEndH1 = findViewById(R.id.ViewFlipper_EndH1);
        ViewFlipper vfStartH2 = findViewById(R.id.ViewFlipper_StartH2);
        ViewFlipper vfEndH2 = findViewById(R.id.ViewFlipper_EndH2);

        vfStartH1.setDisplayedChild(0);
        vfEndH1.setDisplayedChild(0);
        vfStartH2.setDisplayedChild(0);
        vfEndH2.setDisplayedChild(0);
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
