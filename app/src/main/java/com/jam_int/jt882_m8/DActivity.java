package com.jam_int.jt882_m8;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class DActivity extends AppCompatActivity {
    Thread_LoopEmergenza thread_LoopEmergenza;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d);
        Log.d("JAM TAG", "DActivity");

        thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
        thread_LoopEmergenza.thread_LoopEmergenza_Start(this);
        /** Setup D */

        // Setup the EditText D
        final EditText et_D = findViewById(R.id.editText19);
        et_D.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, DActivity.this, et_D, 15, 0.1, true, false, 6.4, false, "");
                }
                return false;
            }
        });
        // Assign E value
        et_D.setText(String.valueOf(Values.E));
        // Check if D had a value, if yes override the current value
        if (Values.D != -1000) {
            et_D.setText(String.valueOf(Values.D));
        }

        // Big if for choose the image and values to display
        if (Values.type == 1 && Values.model == 0) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.ar_1_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.ar_1_zo_d);
        } else if (Values.type == 2 && Values.model == 0) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.ar_2_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.ar_2_zo_d);
        } else if (Values.type == 3 && Values.model == 0) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.ar_3_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.ar_3_zo_d);
        } else if (Values.type == 4 && Values.model == 0) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.ar_4_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.ar_4_zo_d);
        } else if (Values.type == 5 && Values.model == 0) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.ar_5_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.ar_5_zo_d);
        } else if (Values.type == 6 && Values.model == 0) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.ar_6_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.ar_6_zo_d);
        } else if (Values.type == 1 && Values.model == 1 && Values.model1 == 1) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.dr_1_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.dr_1_zo_d);
        } else if (Values.type == 2 && Values.model == 1 && Values.model1 == 1) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.dr_2_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.dr_2_zo_d);
        } else if (Values.type == 3 && Values.model == 1 && Values.model1 == 1) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.dr_3_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.dr_3_zo_d);
        } else if (Values.type == 4 && Values.model == 1 && Values.model1 == 1) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.dr_4_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.dr_4_zo_d);
        } else if (Values.type == 5 && Values.model == 1 && Values.model1 == 1) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.dr_5_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.dr_5_zo_d);
        } else if (Values.type == 6 && Values.model == 1 && Values.model1 == 1) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.dr_6_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.dr_6_zo_d);
        } else if (Values.type == 7 && Values.model == 1 && Values.model1 == 1) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.dr_7_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.dr_7_zo_d);
        } else if (Values.type == 1 && Values.model == 1 && Values.model1 == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drar_1_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drar_1_zo_d);
        } else if (Values.type == 2 && Values.model == 1 && Values.model1 == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drdr_1_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drdr_1_zo_d);
        } else if (Values.type == 3 && Values.model == 1 && Values.model1 == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drar_2_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drar_2_zo_d);
        } else if (Values.type == 4 && Values.model == 1 && Values.model1 == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drdr_2_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drdr_2_zo_d);
        } else if (Values.type == 5 && Values.model == 1 && Values.model1 == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drar_3_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drar_3_zo_d);
        } else if (Values.type == 6 && Values.model == 1 && Values.model1 == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drdr_3_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drdr_3_zo_d);
        } else if (Values.type == 7 && Values.model == 1 && Values.model1 == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drar_4_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drar_4_zo_d);
        } else if (Values.type == 8 && Values.model == 1 && Values.model1 == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drdr_4_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drdr_4_zo_d);
        } else if (Values.type == 9 && Values.model == 1 && Values.model1 == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drar_5_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drar_5_zo_d);
        } else if (Values.type == 10 && Values.model == 1 && Values.model1 == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drdr_5_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drdr_5_zo_d);
        } else if (Values.type == 11 && Values.model == 1 && Values.model1 == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drar_6_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drar_6_zo_d);
        } else if (Values.type == 12 && Values.model == 1 && Values.model1 == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drdr_6_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drdr_6_zo_d);
        } else if (Values.type == 1 && Values.model == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_1_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qs_1_zo_d);
        } else if (Values.type == 2 && Values.model == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_2_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qs_2_zo_d);
        } else if (Values.type == 3 && Values.model == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_3_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qs_3_zo_d);
        } else if (Values.type == 4 && Values.model == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_4_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qs_4_zo_d);
        } else if (Values.type == 5 && Values.model == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_5_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qs_5_zo_d);
        } else if (Values.type == 6 && Values.model == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_6_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qs_6_zo_d);
        } else if (Values.type == 1 && Values.model == 4) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_1_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qa_1_zo_d);
        } else if (Values.type == 2 && Values.model == 4) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_2_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qa_2_zo_d);
        } else if (Values.type == 3 && Values.model == 4) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_3_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qa_3_zo_d);
        } else if (Values.type == 4 && Values.model == 4) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_4_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qa_4_zo_d);
        } else if (Values.type == 5 && Values.model == 4) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_5_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qa_5_zo_d);
        } else if (Values.type == 6 && Values.model == 4) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_6_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qa_6_zo_d);
        } else if (Values.type == 1 && Values.model == 5) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.ar3_1_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.ar3_1_zo_d);
        } else if (Values.type == 2 && Values.model == 5) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.ar3_2_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.ar3_2_zo_d);
        } else if (Values.type == 3 && Values.model == 5) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.ar3_3_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.ar3_3_zo_d);
        } else if (Values.type == 4 && Values.model == 5) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.ar3_4_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.ar3_4_zo_d);
        } else if (Values.type == 5 && Values.model == 5) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.ar3_5_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.ar3_5_zo_d);
        } else if (Values.type == 6 && Values.model == 5) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.ar3_6_zi_d);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.ar3_6_zo_d);
        } else {
            throw new UnsupportedOperationException("Pocket type not initialized");
        }
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
            Log.d("JAM TAG", "DActivity");

        }

    }
    /**
     * Button for go to the next Activity
     *
     * @param v
     */
    public void BtnNext(View v) {
        EditText eT_D = findViewById(R.id.editText19);
        if (Values.model == 0 || (Values.model == 1 && Values.model1 == 1) || Values.model == 3 || Values.model == 5) {
            Values.D = Float.parseFloat(eT_D.getText().toString());
            Intent page4 = new Intent(this, LPActivity.class);
            startActivity(page4);
        } else if (Values.model == 1 && Values.model1 == 2) {
            Values.D = Float.parseFloat(eT_D.getText().toString());
            Intent page4 = new Intent(this, RILActivity.class);
            startActivity(page4);
        } else if (Values.model == 4 || Values.model == 2) {
            Values.D = Float.parseFloat(eT_D.getText().toString());
            Intent page4 = new Intent(this, RILActivity.class);
            startActivity(page4);
        }
    }

    /**
     * Close the current activity and go back
     *
     * @param v
     */
    public void BtnBack(View v) {
        finish();
    }

    /**
     * Return to the tool page
     *
     * @param v
     */
    public void BtnExit(View v) {
        Intent intent = new Intent(this, Tool_page.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
