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


public class SkipActivity extends AppCompatActivity {
    Thread_LoopEmergenza thread_LoopEmergenza;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skip);
        Log.d("JAM TAG", "SkipActivity");

        thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
        thread_LoopEmergenza.thread_LoopEmergenza_Start(this);

        ImageView img = findViewById(R.id.imageView1);

        final EditText et_M = findViewById(R.id.editText_M);
        et_M.setVisibility(View.VISIBLE);
        et_M.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, SkipActivity.this, et_M, 12, -12, true, true, 2, false, "");
                }
                return false;
            }
        });
        TextView tv_M = findViewById(R.id.textView_M);
        tv_M.setVisibility(View.VISIBLE);

        if (Values.M != -1000) {
            et_M.setText(String.valueOf(Values.M));
        }

        final EditText et_A = findViewById(R.id.editText_A);
        et_A.setVisibility(View.VISIBLE);
        et_A.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, SkipActivity.this, et_A, 30, 0.1, true, false, 15, false, "");
                }
                return false;
            }
        });
        TextView tv_A = findViewById(R.id.textView_A);
        tv_A.setVisibility(View.VISIBLE);

        if (Values.A != -1000) {
            et_A.setText(String.valueOf(Values.A));
        }

        final EditText et_B = findViewById(R.id.editText_B);
        et_B.setVisibility(View.VISIBLE);
        et_B.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, SkipActivity.this, et_B, 10, 0.1, true, false, 3, false, "");
                }
                return false;
            }
        });
        TextView tv_B = findViewById(R.id.textView_B);
        tv_B.setVisibility(View.VISIBLE);

        if (Values.B != -1000) {
            et_B.setText(String.valueOf(Values.B));
        }

        final EditText et_C = findViewById(R.id.editText_C);
        et_C.setVisibility(View.VISIBLE);
        et_C.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, SkipActivity.this, et_C, 5, 0.1, true, false, 1, false, "");
                }
                return false;
            }
        });
        TextView tv_C = findViewById(R.id.textView_C);
        tv_C.setVisibility(View.VISIBLE);

        if (Values.C != -1000) {
            et_C.setText(String.valueOf(Values.C));
        }

        final EditText et_F = findViewById(R.id.editText_F);
        et_F.setVisibility(View.VISIBLE);
        et_F.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, SkipActivity.this, et_F, 30, 0.1, true, false, 15, false, "");
                }
                return false;
            }
        });
        TextView tv_F = findViewById(R.id.textView_F);
        tv_F.setVisibility(View.VISIBLE);

        if (Values.F != -1000) {
            et_F.setText(String.valueOf(Values.F));
        }

        final EditText et_G = findViewById(R.id.editText_G);
        et_G.setVisibility(View.VISIBLE);
        et_G.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, SkipActivity.this, et_G, 10, 0.1, true, false, 3, false, "");
                }
                return false;
            }
        });
        TextView tv_G = findViewById(R.id.textView_G);
        tv_G.setVisibility(View.VISIBLE);

        if (Values.G != -1000) {
            et_G.setText(String.valueOf(Values.G));
        }

        final EditText et_H = findViewById(R.id.editText_H);
        et_H.setVisibility(View.VISIBLE);
        et_H.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, SkipActivity.this, et_H, 5, 0.1, true, false, 1, false, "");
                }
                return false;
            }
        });
        TextView tv_H = findViewById(R.id.textView_H);
        tv_H.setVisibility(View.VISIBLE);

        if (Values.H != -1000) {
            et_H.setText(String.valueOf(Values.H));
        }

        final EditText et_E = findViewById(R.id.editText_E);
        et_E.setVisibility(View.VISIBLE);
        et_E.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, SkipActivity.this, et_E, 15, 0.1, true, false, 6.4, false, "");
                }
                return false;
            }
        });
        TextView tv_E = findViewById(R.id.textView_E);
        tv_E.setVisibility(View.VISIBLE);

        if (Values.E != -1000) {
            et_E.setText(String.valueOf(Values.E));
        }

        final EditText et_D = findViewById(R.id.editText_D);
        et_D.setVisibility(View.VISIBLE);
        et_D.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, SkipActivity.this, et_D, 15, 0.1, true, false, 6.4, false, "");
                }
                return false;
            }
        });
        TextView tv_D = findViewById(R.id.textView_D);
        tv_D.setVisibility(View.VISIBLE);

        if (Values.D != -1000) {
            et_D.setText(String.valueOf(Values.D));
        }

        final EditText et_I = findViewById(R.id.editText_I);
        et_I.setVisibility(View.VISIBLE);
        et_I.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, SkipActivity.this, et_I, 200, 0.1, true, false, 30, false, "");
                }
                return false;
            }
        });
        TextView tv_I = findViewById(R.id.textView_I);
        tv_I.setVisibility(View.VISIBLE);

        if (Values.I != -1000) {
            et_I.setText(String.valueOf(Values.I));
        } else {
            if (Values.model == 4 || Values.model == 2 || Values.model == 3) {
                et_I.setText("6.4");
            } else {
                et_I.setText("30.0");
            }
        }

        final EditText et_Lm = findViewById(R.id.editText_Lm);
        et_Lm.setVisibility(View.VISIBLE);
        et_Lm.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, SkipActivity.this, et_Lm, 50, 0.1, true, false, 10, false, "");
                }
                return false;
            }
        });
        TextView tv_Lm = findViewById(R.id.textView_Lm);
        tv_Lm.setVisibility(View.VISIBLE);

        if (Values.Lm != -1000) {
            et_Lm.setText(String.valueOf(Values.Lm));
        }

        final EditText et_N = findViewById(R.id.editText_N);
        et_N.setVisibility(View.VISIBLE);
        et_N.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, SkipActivity.this, et_N, 50, 0.1, true, false, 10, false, "");
                }
                return false;
            }
        });
        TextView tv_N = findViewById(R.id.textView_N);
        tv_N.setVisibility(View.VISIBLE);

        if (Values.N != -1000) {
            et_N.setText(String.valueOf(Values.N));
        }

        final EditText et_O = findViewById(R.id.editText_O);
        et_O.setVisibility(View.VISIBLE);
        et_O.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, SkipActivity.this, et_O, 50, 0.1, true, false, 10, false, "");
                }
                return false;
            }
        });
        TextView tv_O = findViewById(R.id.textView_O);
        tv_O.setVisibility(View.VISIBLE);

        if (Values.O != -1000) {
            et_O.setText(String.valueOf(Values.O));
        }

        final EditText et_SL = findViewById(R.id.editText_SL);
        et_SL.setVisibility(View.VISIBLE);
        et_SL.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog.Lancia_KeyDialogo(null, SkipActivity.this, et_SL, 12, 0.1, true, false, 3.3, false, "");
                }
                return false;
            }
        });
        TextView tv23 = findViewById(R.id.textView_SL);
        tv23.setVisibility(View.VISIBLE);

        if (Values.LP != -1000) {
            et_SL.setText(String.valueOf(Values.LP));
        }

        if (Values.type == 1 && Values.model == 0) {

            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.ar_1_s);
        } else if (Values.type == 2 && Values.model == 0) {

            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);

            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);

            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);

            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.ar_1_s);
        } else if (Values.type == 3 && Values.model == 0) {

            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.ar_1_s);
        } else if (Values.type == 4 && Values.model == 0) {

            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.ar_1_s);
        } else if (Values.type == 5 && Values.model == 0) {

            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);

            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);

            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);

            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.ar_1_s);
        } else if (Values.type == 6 && Values.model == 0) {

            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.ar_1_s);
        } else if (Values.type == 1 && Values.model == 1 && Values.model1 == 1) {

            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.dr_1_s);
        } else if (Values.type == 2 && Values.model == 1 && Values.model1 == 1) {

            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);

            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);

            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);

            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.dr_1_s);  // TODO Da cambiare
        } else if (Values.type == 3 && Values.model == 1 && Values.model1 == 1) {

            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.dr_1_s);
        } else if (Values.type == 4 && Values.model == 1 && Values.model1 == 1) {

            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.dr_1_s);
        } else if (Values.type == 5 && Values.model == 1 && Values.model1 == 1) {

            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);

            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);

            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);

            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.dr_1_s);
        } else if (Values.type == 6 && Values.model == 1 && Values.model1 == 1) {

            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.dr_1_s);
        } else if (Values.type == 7 && Values.model == 1 && Values.model1 == 1) {

            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);

            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);

            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);

            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.dr_1_s);
        } else if (Values.type == 8 && Values.model == 1 && Values.model1 == 1) {

            et_A.setVisibility(View.GONE);
            tv_A.setVisibility(View.GONE);

            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);

            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);

            et_F.setVisibility(View.GONE);
            tv_F.setVisibility(View.GONE);

            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);

            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            et_E.setVisibility(View.GONE);
            tv_E.setVisibility(View.GONE);

            et_D.setVisibility(View.GONE);
            tv_D.setVisibility(View.GONE);

            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.dr_8_s);
        } else if (Values.type == 1 && Values.model == 1 && Values.model1 == 2) {
            img.setImageResource(R.drawable.drar_1_s);
        } else if (Values.type == 2 && Values.model == 1 && Values.model1 == 2) {

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.drdr_1_s); //Da cambiare
        } else if (Values.type == 3 && Values.model == 1 && Values.model1 == 2) {
            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);

            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);

            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);

            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            img.setImageResource(R.drawable.drar_1_s);
        } else if (Values.type == 4 && Values.model == 1 && Values.model1 == 2) {
            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);

            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);

            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);

            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.drdr_1_s); //Da cambiare
        } else if (Values.type == 5 && Values.model == 1 && Values.model1 == 2) {
            img.setImageResource(R.drawable.drar_1_s);
        } else if (Values.type == 6 && Values.model == 1 && Values.model1 == 2) {

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.drdr_1_s);
        } else if (Values.type == 7 && Values.model == 1 && Values.model1 == 2) {
            img.setImageResource(R.drawable.drar_1_s);
        } else if (Values.type == 8 && Values.model == 1 && Values.model1 == 2) {

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.drdr_1_s);
        } else if (Values.type == 9 && Values.model == 1 && Values.model1 == 2) {

            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);

            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);

            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);

            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            img.setImageResource(R.drawable.drar_1_s); //TODO Da cambiare
        } else if (Values.type == 10 && Values.model == 1 && Values.model1 == 2) {

            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);

            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);

            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);

            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.drdr_1_s); //TODO Da cambiare
        } else if (Values.type == 11 && Values.model == 1 && Values.model1 == 2) {
            img.setImageResource(R.drawable.drar_1_s);
        } else if (Values.type == 12 && Values.model == 1 && Values.model1 == 2) {

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            img.setImageResource(R.drawable.drdr_1_s);
        } else if (Values.model == 2 && Values.type == 1) {
            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.qs_1_s);
        } else if (Values.model == 2 && Values.type == 2) {
            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);

            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);

            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);

            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.qs_1_s); //TODO Da cambiare
        } else if (Values.model == 2 && Values.type == 3) {
            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.qs_1_s);
        } else if (Values.model == 2 && Values.type == 4) {
            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.qs_1_s);
        } else if (Values.model == 2 && Values.type == 5) {
            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);

            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);

            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);

            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.qs_1_s); //TODO Da cambiare
        } else if (Values.model == 2 && Values.type == 6) {
            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.qs_1_s);
        } else if (Values.model == 3 && Values.type == 1) {
            et_E.setVisibility(View.GONE);
            tv_E.setVisibility(View.GONE);

            et_D.setVisibility(View.GONE);
            tv_D.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.q_1_s);
        } else if (Values.model == 3 && Values.type == 2) {
            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);

            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);

            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);

            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            et_E.setVisibility(View.GONE);
            tv_E.setVisibility(View.GONE);

            et_D.setVisibility(View.GONE);
            tv_D.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.q_1_s); //TODO Da cambiare
        } else if (Values.model == 3 && Values.type == 3) {
            et_E.setVisibility(View.GONE);
            tv_E.setVisibility(View.GONE);

            et_D.setVisibility(View.GONE);
            tv_D.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.q_1_s);
        } else if (Values.model == 3 && Values.type == 4) {
            et_E.setVisibility(View.GONE);
            tv_E.setVisibility(View.GONE);

            et_D.setVisibility(View.GONE);
            tv_D.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.q_1_s);
        } else if (Values.model == 3 && Values.type == 5) {

            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);

            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);

            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);

            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            et_E.setVisibility(View.GONE);
            tv_E.setVisibility(View.GONE);

            et_D.setVisibility(View.GONE);
            tv_D.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.q_1_s); //TODO Da cambiare
        } else if (Values.model == 3 && Values.type == 6) {
            et_E.setVisibility(View.GONE);
            tv_E.setVisibility(View.GONE);

            et_D.setVisibility(View.GONE);
            tv_D.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.q_1_s);
        } else if (Values.model == 4 && Values.type == 1) {
            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            if (Values.M1 > 1) {
                et_D.setVisibility(View.GONE);
                tv_D.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_3_s);
            }
            if (Values.M2 > 1) {
                et_E.setVisibility(View.GONE);
                tv_E.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_2_s);
            }
            if (Values.M1 > 1 && Values.M2 > 1) {
                et_E.setVisibility(View.GONE);
                tv_E.setVisibility(View.GONE);
                et_D.setVisibility(View.GONE);
                tv_D.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_1_s);
            }
        } else if (Values.model == 4 && Values.type == 2) {
            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);

            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);

            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);

            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            if (Values.M1 > 1) {
                et_D.setVisibility(View.GONE);
                tv_D.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_3_s);
            }
            if (Values.M2 > 1) {
                et_E.setVisibility(View.GONE);
                tv_E.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_2_s);
            }
            if (Values.M1 > 1 && Values.M2 > 1) {
                et_E.setVisibility(View.GONE);
                tv_E.setVisibility(View.GONE);
                et_D.setVisibility(View.GONE);
                tv_D.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_1_s);
            }
        } else if (Values.model == 4 && Values.type == 3) {
            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            if (Values.M1 > 1) {
                et_D.setVisibility(View.GONE);
                tv_D.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_3_s);
            }
            if (Values.M2 > 1) {
                et_E.setVisibility(View.GONE);
                tv_E.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_2_s);
            }
            if (Values.M1 > 1 && Values.M2 > 1) {
                et_E.setVisibility(View.GONE);
                tv_E.setVisibility(View.GONE);
                et_D.setVisibility(View.GONE);
                tv_D.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_1_s);
            }
        } else if (Values.model == 4 && Values.type == 4) {
            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            if (Values.M1 > 1) {
                et_D.setVisibility(View.GONE);
                tv_D.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_3_s);
            }
            if (Values.M2 > 1) {
                et_E.setVisibility(View.GONE);
                tv_E.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_2_s);
            }
            if (Values.M1 > 1 && Values.M2 > 1) {
                et_E.setVisibility(View.GONE);
                tv_E.setVisibility(View.GONE);
                et_D.setVisibility(View.GONE);
                tv_D.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_1_s);
            }
        } else if (Values.model == 4 && Values.type == 5) {

            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);

            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);

            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);

            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);

            if (Values.M1 > 1) {
                et_D.setVisibility(View.GONE);
                tv_D.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_3_s);
            }
            if (Values.M2 > 1) {
                et_E.setVisibility(View.GONE);
                tv_E.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_2_s);
            }
            if (Values.M1 > 1 && Values.M2 > 1) {
                et_E.setVisibility(View.GONE);
                tv_E.setVisibility(View.GONE);
                et_D.setVisibility(View.GONE);
                tv_D.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_1_s);
            }
        } else if (Values.model == 4 && Values.type == 6) {
            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            if (Values.M1 > 1) {
                et_D.setVisibility(View.GONE);
                tv_D.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_3_s);
            }
            if (Values.M2 > 1) {
                et_E.setVisibility(View.GONE);
                tv_E.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_2_s);
            }
            if (Values.M1 > 1 && Values.M2 > 1) {
                et_E.setVisibility(View.GONE);
                tv_E.setVisibility(View.GONE);
                et_D.setVisibility(View.GONE);
                tv_D.setVisibility(View.GONE);
                img.setImageResource(R.drawable.qa_1_s);
            }
        } else if (Values.model == 5 && Values.type == 1) {
            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);

            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.ar3_1_s);
        } else if (Values.model == 5 && Values.type == 2) {
            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);
            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);
            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);
            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);
            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.ar3_2_s);
        } else if (Values.model == 5 && Values.type == 3) {
            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);
            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.ar3_1_s);
        } else if (Values.model == 5 && Values.type == 4) {
            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);
            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.ar3_1_s);
        } else if (Values.model == 5 && Values.type == 5) {
            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);
            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_B.setVisibility(View.GONE);
            tv_B.setVisibility(View.GONE);
            et_C.setVisibility(View.GONE);
            tv_C.setVisibility(View.GONE);
            et_G.setVisibility(View.GONE);
            tv_G.setVisibility(View.GONE);
            et_H.setVisibility(View.GONE);
            tv_H.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.ar3_2_s);
        } else if (Values.model == 5 && Values.type == 6) {
            et_I.setVisibility(View.GONE);
            tv_I.setVisibility(View.GONE);
            et_Lm.setVisibility(View.GONE);
            tv_Lm.setVisibility(View.GONE);

            et_N.setVisibility(View.GONE);
            tv_N.setVisibility(View.GONE);

            et_O.setVisibility(View.GONE);
            tv_O.setVisibility(View.GONE);
            img.setImageResource(R.drawable.ar3_1_s);
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
            Log.d("JAM TAG", "SkipActivity");

        }

    }
    /**
     * Button for go to the next activity and save all the values
     *
     * @param v
     */
    public void BtnNext(View v) {
        EditText et_M = findViewById(R.id.editText_M);
        Values.M = Float.parseFloat(et_M.getText().toString());

        EditText et_A = findViewById(R.id.editText_A);
        Values.A = Float.parseFloat(et_A.getText().toString());

        EditText et_B = findViewById(R.id.editText_B);
        Values.B = Float.parseFloat(et_B.getText().toString());

        EditText et_C = findViewById(R.id.editText_C);
        Values.C = Float.parseFloat(et_C.getText().toString());

        EditText et_F = findViewById(R.id.editText_F);
        Values.F = Float.parseFloat(et_F.getText().toString());

        EditText et_G = findViewById(R.id.editText_G);
        Values.G = Float.parseFloat(et_G.getText().toString());

        EditText et_H = findViewById(R.id.editText_H);
        Values.H = Float.parseFloat(et_H.getText().toString());

        EditText et_E = findViewById(R.id.editText_E);
        Values.E = Float.parseFloat(et_E.getText().toString());

        EditText et_D = findViewById(R.id.editText_D);
        Values.D = Float.parseFloat(et_D.getText().toString());

        EditText et_I = findViewById(R.id.editText_I);
        Values.I = Float.parseFloat(et_I.getText().toString());

        EditText et_Lm = findViewById(R.id.editText_Lm);
        Values.Lm = Float.parseFloat(et_Lm.getText().toString());

        EditText et_N = findViewById(R.id.editText_N);
        Values.N = Float.parseFloat(et_N.getText().toString());

        EditText et_O = findViewById(R.id.editText_O);
        Values.O = Float.parseFloat(et_O.getText().toString());

        EditText et_SL = findViewById(R.id.editText_SL);
        Values.LP = Float.parseFloat(et_SL.getText().toString());

        Intent Resultpage = new Intent(this, ResultActivity.class);
        startActivity(Resultpage);
    }

    /**
     * Button for go to the previous activity
     *
     * @param v
     */
    public void BtnBack(View v) {
        finish();
    }

    /**
     * Button for go to the backtack activity
     *
     * @param v
     */
    public void BtnFermature(View v) {
        EditText et_SL = findViewById(R.id.editText_SL);
        Values.LP = Float.parseFloat(et_SL.getText().toString());
        Intent Fermaturepage = new Intent(this, FermatureActivity.class);
        startActivity(Fermaturepage);
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
