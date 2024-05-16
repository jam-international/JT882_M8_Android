package com.jam_int.jt882_m8;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RILActivity extends AppCompatActivity {
    Thread_LoopEmergenza thread_LoopEmergenza;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ri);
        Log.d("JAM TAG", "RILActivity");

        thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
        thread_LoopEmergenza.thread_LoopEmergenza_Start(this);


        /** Setup I */

        // Setup the EditText I
        final EditText et_I = findViewById(R.id.editText_I);
        et_I.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, RILActivity.this, et_I, 200, 0.1, true, false, 30, false, "");
                }
                return false;
            }
        });

        // Check if I had a value, if yes override the current value
        if (Values.I != -1000) {
            et_I.setText(String.valueOf(Values.I));
        } else {
            if (Values.model == 4 || Values.model == 2 || Values.model == 3) {
                et_I.setText("6.4");
            } else {
                et_I.setText("30.0");
            }
        }

        /** Setup Lm */

        // Setup the EditText Lm
        final EditText et_Lm = findViewById(R.id.editText_Lm);
        et_Lm.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, RILActivity.this, et_Lm, 50, 0.1, true, false, 10, false, "");
                }
                return false;
            }
        });

        // Setup the TextView Lm
        TextView tv_Lm = findViewById(R.id.textView_Lm);
        tv_Lm.setVisibility(View.VISIBLE);

        // Check if Lm had a value, if yes override the current value
        if (Values.Lm != -1000) {
            et_Lm.setText(String.valueOf(Values.Lm));
        }

        /** Setup N */

        // Setup the EditText N
        final EditText et_N = findViewById(R.id.editText_N);
        et_N.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, RILActivity.this, et_N, 50, 0.1, true, false, 10, false, "");
                }
                return false;
            }
        });

        // Setup the TextView N
        TextView tv_N = findViewById(R.id.textView_N);
        tv_N.setVisibility(View.VISIBLE);

        // Check if N had a value, if yes override the current value
        if (Values.N != -1000) {
            et_N.setText(String.valueOf(Values.N));
        }

        /** Setup O */

        // Setup the EditText O
        final EditText et_O = findViewById(R.id.editText_O);
        et_O.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, RILActivity.this, et_O, 50, 0.1, true, false, 10, false, "");
                }
                return false;
            }
        });

        // Setup the TextView O
        TextView tv_O = findViewById(R.id.textView_O);
        tv_O.setVisibility(View.VISIBLE);

        // Check if O had a value, if yes override the current value
        if (Values.O != -1000) {
            et_O.setText(String.valueOf(Values.O));
        }

        // Big if for choose the image and values to display
        if (Values.model == 1 && Values.model1 == 2 && Values.type == 1) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drar_1_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drar_1_zo_ril);
        } else if (Values.model == 1 && Values.model1 == 2 && Values.type == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drdr_1_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drdr_1_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.model == 1 && Values.model1 == 2 && Values.type == 3) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drar_2_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drar_2_zo_ril);
        } else if (Values.model == 1 && Values.model1 == 2 && Values.type == 4) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drdr_2_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drdr_2_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.model == 1 && Values.model1 == 2 && Values.type == 5) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drar_3_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drar_3_zo_ril);
        } else if (Values.model == 1 && Values.model1 == 2 && Values.type == 6) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drdr_3_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drdr_3_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.model == 1 && Values.model1 == 2 && Values.type == 7) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drar_4_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drar_4_zo_ril);
        } else if (Values.model == 1 && Values.model1 == 2 && Values.type == 8) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drdr_4_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drdr_4_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.model == 1 && Values.model1 == 2 && Values.type == 9) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drar_5_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drar_5_zo_ril);
        } else if (Values.model == 1 && Values.model1 == 2 && Values.type == 10) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drdr_5_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drdr_5_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.model == 1 && Values.model1 == 2 && Values.type == 11) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drar_6_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drar_6_zo_ril);
        } else if (Values.model == 1 && Values.model1 == 2 && Values.type == 12) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.drdr_6_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.drdr_6_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 1 && Values.model == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_1_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qs_1_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 2 && Values.model == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_2_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qs_2_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 3 && Values.model == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_3_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qs_3_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 4 && Values.model == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_4_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qs_4_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 5 && Values.model == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_5_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qs_5_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 6 && Values.model == 2) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qs_6_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qs_6_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 1 && Values.model == 3) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.q_1_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.q_1_zo_ril);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);
            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 2 && Values.model == 3) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.q_2_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.q_2_zo_ril);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);
            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 3 && Values.model == 3) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.q_3_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.q_3_zo_ril);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);
            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 4 && Values.model == 3) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.q_4_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.q_4_zo_ril);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);
            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 5 && Values.model == 3) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.q_5_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.q_5_zo_ril);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);
            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 6 && Values.model == 3) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.q_6_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.q_6_zo_ril);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);
            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 1 && Values.model == 4) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qa_1_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qa_1_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 2 && Values.model == 4) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qa_2_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qa_2_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 3 && Values.model == 4) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qa_3_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qa_3_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 4 && Values.model == 4) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qa_4_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qa_4_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 5 && Values.model == 4) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qa_5_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qa_5_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        } else if (Values.type == 6 && Values.model == 4) {
            ImageView zoom = findViewById(R.id.imageView);
            zoom.setImageResource(R.drawable.qa_6_zi_ril);
            ImageView img = findViewById(R.id.imageView1);
            img.setImageResource(R.drawable.qa_6_zo_ril);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);
            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
        }

        // This 2 model type don't have this value
        if (Values.model == 2 || Values.model == 4) {
            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);
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
            Log.d("JAM TAG", "RILActivity");

        }

    }
    /**
     * Button for go to the next Activity
     *
     * @param v
     */
    public void BtnNext(View v) {
        EditText eT_I = findViewById(R.id.editText_I);
        EditText eT_Lm = findViewById(R.id.editText_Lm);
        EditText eT_N = findViewById(R.id.editText_N);
        EditText eT_O = findViewById(R.id.editText_O);

        Values.I = Float.parseFloat(eT_I.getText().toString());
        Values.Lm = Float.parseFloat(eT_Lm.getText().toString());
        Values.N = Float.parseFloat(eT_N.getText().toString());
        Values.O = Float.parseFloat(eT_O.getText().toString());

        if ((Values.model == 1 && Values.model1 == 2 && Values.type == 1) ||
                (Values.model == 1 && Values.model1 == 2 && Values.type == 2) ||
                (Values.model == 1 && Values.model1 == 2 && Values.type == 3) ||
                (Values.model == 1 && Values.model1 == 2 && Values.type == 4) ||
                (Values.model == 1 && Values.model1 == 2 && Values.type == 5) ||
                (Values.model == 1 && Values.model1 == 2 && Values.type == 6) ||
                (Values.model == 1 && Values.model1 == 2 && Values.type == 7) ||
                (Values.model == 1 && Values.model1 == 2 && Values.type == 8) ||
                (Values.model == 1 && Values.model1 == 2 && Values.type == 9) ||
                (Values.model == 1 && Values.model1 == 2 && Values.type == 10) ||
                (Values.model == 1 && Values.model1 == 2 && Values.type == 11) ||
                (Values.model == 1 && Values.model1 == 2 && Values.type == 12)) {

            Intent page4 = new Intent(this, LPActivity.class);
            startActivity(page4);
        } else if (Values.model == 2 || Values.model == 4 || Values.model == 3) {
            Intent page4 = new Intent(this, LPActivity.class);
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
     * Button for exit and return to the Tool page
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
