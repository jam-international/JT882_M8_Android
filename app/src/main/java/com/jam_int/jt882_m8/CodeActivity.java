package com.jam_int.jt882_m8;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class CodeActivity extends AppCompatActivity {
    Thread_LoopEmergenza thread_LoopEmergenza;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code2);
        Log.d("JAM TAG", "CodeActivity");

        thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
        thread_LoopEmergenza.thread_LoopEmergenza_Start(this);



        // Hide the top part (some model don't require it)
        LinearLayout ll1 = findViewById(R.id.LinearLayout1);
        ll1.setVisibility(View.GONE);
        ImageView imgV1 = findViewById(R.id.imageView);
        imgV1.setVisibility(View.GONE);

        // Init the EditText value
        EditText edt1 = findViewById(R.id.editText1);
        EditText edt2 = findViewById(R.id.editText2);
        EditText edt4 = findViewById(R.id.editText4);
        EditText edt3 = findViewById(R.id.editText3);
        EditText edt7 = findViewById(R.id.editText7);
        if (Values.OP2On != -2) {
            edt1.setText("" + Values.OP2On);
        }
        if (Values.OP1On != 0) {
            edt2.setText("" + Values.OP1On);
        }
        if (Values.OP2Off != 2) {
            edt4.setText("" + Values.OP2Off);
        }

        if (Values.Speed1 != -4) {
            edt3.setText("" + Values.Speed1);
        }

        if (Values.OP3 != -4) {
            edt7.setText("" + Values.OP3);
        }

        // Chose the images to display by the model
        if (Values.model == 0) {
            if (Values.type == 1 || Values.type == 2) {
                ll1.setVisibility(View.VISIBLE);
                imgV1.setVisibility(View.VISIBLE);
            }
        } else if (Values.model == 1 && Values.model1 == 1) {
            if (Values.type == 1 || Values.type == 2 || Values.type == 7 || Values.type == 8) {
                ll1.setVisibility(View.VISIBLE);
                imgV1.setVisibility(View.VISIBLE);
            }
        } else if (Values.model == 1 && Values.model1 == 2) {
            if (Values.type == 1 || Values.type == 2 || Values.type == 3 || Values.type == 4) {
                ll1.setVisibility(View.VISIBLE);
                imgV1.setVisibility(View.VISIBLE);
            }
        } else if (Values.model == 2) {
            if (Values.type == 1 || Values.type == 2) {
            }
        } else if (Values.model == 3) {
            if (Values.type == 1 || Values.type == 2) {
                ll1.setVisibility(View.VISIBLE);
                imgV1.setVisibility(View.VISIBLE);
            }
        } else if (Values.model == 4) {
            if (Values.type == 1 || Values.type == 2) {
                ll1.setVisibility(View.VISIBLE);
                imgV1.setVisibility(View.VISIBLE);
            }
        }

        // Setup the EditText OP2ON
        final EditText et1 = findViewById(R.id.editText1);
        et1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, CodeActivity.this, et1, 50, -50, true, true, -4, false, "");
                }
                return false;
            }
        });

        // Setup the EditText OP1ON
        EditText et_OP1ON = findViewById(R.id.editText2);
        et_OP1ON.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, CodeActivity.this, et_OP1ON, 50, -50, true, true, -1, false, "");
                }
                return false;
            }
        });

        // Setup the EditText OP2OFF
        EditText et_OP2OFF = findViewById(R.id.editText4);
        et_OP2OFF.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, CodeActivity.this, et_OP2OFF, 50, -50, true, true, -3, false, "");
                }
                return false;
            }
        });

        // Setup the EditText SPEED
        EditText et_SPEED = findViewById(R.id.editText3);
        et_SPEED.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, CodeActivity.this, et_SPEED, 50, -50, true, true, 2, false, "");
                }
                return false;
            }
        });

        // Setup the EditText OP3
        EditText et_OP3 = findViewById(R.id.editText7);
        et_OP3.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, CodeActivity.this, et_OP3, 50, -50, true, true, 7, false, "");
                }
                return false;
            }
        });
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
            Log.d("JAM TAG", "CodeActivity");

        }

    }
    /**
     * Button back for save values and go back to the draw result
     *
     * @param v
     */
    public void BtnBack(View v) {
        EditText et1 = findViewById(R.id.editText1);
        Values.OP2On = Integer.parseInt(et1.getText().toString());

        EditText et2 = findViewById(R.id.editText2);
        Values.OP1On = Integer.parseInt(et2.getText().toString());

        EditText et4 = findViewById(R.id.editText4);
        Values.OP2Off = Integer.parseInt(et4.getText().toString());

        EditText et3 = findViewById(R.id.editText3);
        Values.Speed1 = -Integer.parseInt(et3.getText().toString());

        EditText et7 = findViewById(R.id.editText7);
        Values.OP3 = -Integer.parseInt(et7.getText().toString());

        finish();
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
                decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
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
