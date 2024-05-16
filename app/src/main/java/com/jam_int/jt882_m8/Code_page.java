package com.jam_int.jt882_m8;

import static com.jamint.ricette.MathGeoTri.isNumeric;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jamint.ricette.CodeType;
import com.jamint.ricette.CodeValue;
import com.jamint.ricette.CodeValueType;
import com.jamint.ricette.JamPointCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Code_page extends Activity {

    /**
     * Function for initialize the codes values
     *
     * TODO (I don't know why this has been created as static)
     *
     * @param activity_modifica_programma
     * @param codeStatus
     */
    public static void Lancia_Code_Page(final Activity activity_modifica_programma, List<JamPointCode> codeStatus) {
        codeStatus = codeStatus;

        final List<JamPointCode> newCodeStatus = new ArrayList<JamPointCode>();
        final ArrayList<Integer> Lista_codici = new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0));
        final Dialog dialog = new Dialog(activity_modifica_programma);

        dialog.setContentView(R.layout.activity_code);
        showImmersiveDialog(dialog, activity_modifica_programma);
        dialog.show();
        dialog.getWindow().setLayout(500, 600);

        show_codeStatus(codeStatus, dialog, activity_modifica_programma);

        Button Button_exit = dialog.findViewById(R.id.button_exit);
        Button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCodeStatus.clear();
                for (int i = 0; i < Lista_codici.size(); i++) {
                    JamPointCode codeop = null;
                    switch (i) {
                        case 0:
                            codeop = new JamPointCode(CodeType.OP1);
                            break;
                        case 1:
                            codeop = new JamPointCode(CodeType.OP2);
                            break;
                        case 2:
                            codeop = new JamPointCode(CodeType.OP3);
                            break;
                        case 3:
                            codeop = new JamPointCode(CodeType.SPLIT1);
                            break;
                        case 4:
                            codeop = new JamPointCode(CodeType.SPLIT2);
                            break;
                    }
                    // TODO Check when codeop is null
                    // TODO Change String to enum or var that contain the string
                    if (Lista_codici.get(i) == 1) {
                        codeop.valori.add(new CodeValue(CodeValueType.OnOff, "VALUE1"));
                        newCodeStatus.add(codeop);
                    } else if (Lista_codici.get(i) == 2) {
                        codeop.valori.add(new CodeValue(CodeValueType.OnOff, "VALUE0"));
                        newCodeStatus.add(codeop);
                    }
                }

                TextView TextView_slow_valore = dialog.findViewById(R.id.textView_slow_valore);
                String slow_valore = TextView_slow_valore.getText().toString();
                if (isNumeric(slow_valore)) {
                    JamPointCode codeop = new JamPointCode(CodeType.SPEED_M8);
                    codeop.valori.add(new CodeValue(CodeValueType.Numeric, slow_valore));
                    newCodeStatus.add(codeop);
                }

                TextView TextView_tension_valore = dialog.findViewById(R.id.textView_tension_valore);
                String tensione_valore = TextView_tension_valore.getText().toString();
                if (isNumeric(tensione_valore)) {
                    JamPointCode codeop = new JamPointCode(CodeType.TENS_M8);
                    codeop.valori.add(new CodeValue(CodeValueType.Numeric, tensione_valore));
                    newCodeStatus.add(codeop);
                }

                if (newCodeStatus.size() > 0) {
                    Intent intent_code = new Intent("CodeDialog_exit");
                    Bundle bundle_code = new Bundle();
                    bundle_code.putSerializable("valoreCodice", (Serializable) newCodeStatus);
                    intent_code.putExtras(bundle_code);

                    LocalBroadcastManager.getInstance(activity_modifica_programma).sendBroadcast(intent_code);   //lancio il BoradCast per proseguire
                }

                dialog.dismiss();
            }
        });

        Button Button_op1 = dialog.findViewById(R.id.button_op1);
        Button_op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button Button_op1 = dialog.findViewById(R.id.button_op1);
                // Check the current displayed image for chose the next image
                if ((int) Button_op1.getTag() == R.drawable.code_op1) {
                    Lista_codici.set(0, 1);
                    Button_op1.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op1_on));
                    Button_op1.setTag(R.drawable.code_op1_on);
                } else if ((int) Button_op1.getTag() == R.drawable.code_op1_on) {
                    Lista_codici.set(0, 2);
                    Button_op1.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op1_off));
                    Button_op1.setTag(R.drawable.code_op1_off);
                } else if ((int) Button_op1.getTag() == R.drawable.code_op1_off) {
                    Lista_codici.set(0, 0);
                    Button_op1.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op1));
                    Button_op1.setTag(R.drawable.code_op1);
                }
            }

        });
        Button Button_op2 = dialog.findViewById(R.id.button_op2);
        Button_op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button Button_op2 = dialog.findViewById(R.id.button_op2);
                // Check the current displayed image for chose the next image
                if ((int) Button_op2.getTag() == R.drawable.code_op2) {
                    Lista_codici.set(1, 1);
                    Button_op2.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op2_on));
                    Button_op2.setTag(R.drawable.code_op2_on);
                } else if ((int) Button_op2.getTag() == R.drawable.code_op2_on) {
                    Lista_codici.set(1, 2);
                    Button_op2.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op2_off));
                    Button_op2.setTag(R.drawable.code_op2_off);
                } else if ((int) Button_op2.getTag() == R.drawable.code_op2_off) {
                    Lista_codici.set(1, 0);
                    Button_op2.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op2));
                    Button_op2.setTag(R.drawable.code_op2);
                }
            }
        });
        Button Button_op3 = dialog.findViewById(R.id.button_op3);
        Button_op3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button Button_op3 = dialog.findViewById(R.id.button_op3);
                // Check the current displayed image for chose the next image
                if ((int) Button_op3.getTag() == R.drawable.code_op3) {
                    Lista_codici.set(2, 1);
                    Button_op3.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op3_on));
                    Button_op3.setTag(R.drawable.code_op3_on);
                } else if ((int) Button_op3.getTag() == R.drawable.code_op3_on) {
                    Lista_codici.set(2, 2);
                    Button_op3.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op3_off));
                    Button_op3.setTag(R.drawable.code_op3_off);
                } else if ((int) Button_op3.getTag() == R.drawable.code_op3_off) {
                    Lista_codici.set(2, 0);
                    Button_op3.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_op3));
                    Button_op3.setTag(R.drawable.code_op3);
                }
            }
        });
        Button Button_Split1 = dialog.findViewById(R.id.button_Split1);
        Button_Split1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button Button_Split1 = dialog.findViewById(R.id.button_Split1);
                // Check the current displayed image for chose the next image
                if ((int) Button_Split1.getTag() == R.drawable.code_split1) {
                    Lista_codici.set(3, 1);
                    Button_Split1.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_split1_on));
                    Button_Split1.setTag(R.drawable.code_split1_on);
                } else if ((int) Button_Split1.getTag() == R.drawable.code_split1) {
                    Lista_codici.set(3, 0);
                    Button_Split1.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_split1));
                    Button_Split1.setTag(R.drawable.code_split1);
                }
            }

        });
        Button Button_Split2 = dialog.findViewById(R.id.button_Split2);
        Button_Split2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button Button_Split2 = dialog.findViewById(R.id.button_Split2);
                // Check the current displayed image for chose the next image
                if ((int) Button_Split2.getTag() == R.drawable.code_split2) {
                    Lista_codici.set(4, 1);
                    Button_Split2.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_split2_on));
                    Button_Split2.setTag(R.drawable.code_split2_on);
                } else if ((int) Button_Split2.getTag() == R.drawable.code_split2) {
                    Lista_codici.set(4, 0);
                    Button_Split2.setBackground(activity_modifica_programma.getResources().getDrawable(R.drawable.code_split2));
                    Button_Split2.setTag(R.drawable.code_split2);
                }
            }

        });
        // Textview that display the slow value
        final TextView TextView_slow_valore = dialog.findViewById(R.id.textView_slow_valore);
        TextView_slow_valore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = (String) TextView_slow_valore.getText();
                TextView_slow_valore.setText("");   //non faccio scrivere niente come valore iniziale del KeyDialog
                KeyDialog.Lancia_KeyDialogo(null, activity_modifica_programma, TextView_slow_valore, 3000, 0, false, false, 1000, false, "", false,val);
            }
        });

        // Textview that display the tension value
        final TextView TextView_tension_valore = dialog.findViewById(R.id.textView_tension_valore);
        TextView_tension_valore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String val = (String) TextView_tension_valore.getText();

                TextView_tension_valore.setText("");   //non faccio scrivere niente come valore iniziale del KeyDialog
                KeyDialog.Lancia_KeyDialogo(null, activity_modifica_programma, TextView_tension_valore, 100, 0, false, false, 15, false, "", false,val);
            }
        });
    }

    /**
     * Show the current code status on every code button by updating the status
     * @param codeStatus
     * @param dialog
     * @param base_context
     */
    private static void show_codeStatus(List<JamPointCode> codeStatus, Dialog dialog, Context base_context) {
        Button Button_op1 = dialog.findViewById(R.id.button_op1);
        Button Button_op2 = dialog.findViewById(R.id.button_op2);
        Button Button_op3 = dialog.findViewById(R.id.button_op3);
        Button Button_Split1 = dialog.findViewById(R.id.button_Split1);
        Button Button_Split2 = dialog.findViewById(R.id.button_Split2);

        Button_op1.setTag(R.drawable.code_op1);
        Button_op2.setTag(R.drawable.code_op2);
        Button_op3.setTag(R.drawable.code_op3);
        Button_Split1.setTag(R.drawable.code_split1);
        Button_Split2.setTag(R.drawable.code_split2);

        for (JamPointCode item : codeStatus) {
            String codice = item.tipoCodice.toString();
            switch (codice) {
                case "OP1":
                    if (item.valori.size() == 1) {
                        if (item.valori.get(0).codeValueType == CodeValueType.OnOff) {
                            if (item.valori.get(0).currentValue.equals("VALUE1")) {
                                Button_op1.setTag(R.drawable.code_op1_on);
                                Button_op1.setBackground(base_context.getResources().getDrawable(R.drawable.code_op1_on));
                                break;
                            } else if (item.valori.get(0).currentValue.equals("VALUE0")) {
                                Button_op1.setTag(R.drawable.code_op1_off);
                                Button_op1.setBackground(base_context.getResources().getDrawable(R.drawable.code_op1_off));
                                break;
                            } else {
                                Button_op1.setTag(R.drawable.code_op1);
                                break;
                            }
                        }
                    }
                    throw new UnsupportedOperationException();

                case "OP2":
                    if (item.valori.size() == 1) {
                        if (item.valori.get(0).codeValueType == CodeValueType.OnOff) {
                            if (item.valori.get(0).currentValue.equals("VALUE1")) {
                                Button_op2.setTag(R.drawable.code_op2_on);
                                Button_op2.setBackground(base_context.getResources().getDrawable(R.drawable.code_op2_on));
                                break;
                            } else if (item.valori.get(0).currentValue.equals("VALUE0")) {
                                Button_op2.setTag(R.drawable.code_op2_off);
                                Button_op2.setBackground(base_context.getResources().getDrawable(R.drawable.code_op2_off));
                                break;
                            } else {
                                Button_op2.setTag(R.drawable.code_op2);
                                break;
                            }
                        }
                    }
                    throw new UnsupportedOperationException();
                case "OP3":
                    if (item.valori.size() == 1) {
                        if (item.valori.get(0).codeValueType == CodeValueType.OnOff) {
                            if (item.valori.get(0).currentValue.equals("VALUE1")) {
                                Button_op3.setTag(R.drawable.code_op3_on);
                                Button_op3.setBackground(base_context.getResources().getDrawable(R.drawable.code_op3_on));
                                break;
                            } else if (item.valori.get(0).currentValue.equals("VALUE0")) {
                                Button_op3.setTag(R.drawable.code_op3_off);
                                Button_op3.setBackground(base_context.getResources().getDrawable(R.drawable.code_op3_off));
                                break;
                            } else {
                                Button_op3.setTag(R.drawable.code_op3);
                                break;
                            }
                        }
                    }
                    throw new UnsupportedOperationException();

                case "SPEED_M8":
                    TextView TextView_slow_valore = dialog.findViewById(R.id.textView_slow_valore);
                    String speed = item.valori.get(0).currentValue;
                    TextView_slow_valore.setText(speed);

                    break;

                case "TENS_M8":

                    TextView TextView_tension_valore = dialog.findViewById(R.id.textView_tension_valore);
                    String tens = item.valori.get(0).currentValue;
                    TextView_tension_valore.setText(tens);

                    break;
                case "SPLIT1":
                    if (item.valori.size() == 1) {
                        if (item.valori.get(0).codeValueType == CodeValueType.OnOff) {
                            if (item.valori.get(0).currentValue.equals("VALUE1")) {
                                Button_Split1.setTag(R.drawable.code_split1_on);
                                Button_Split1.setBackground(base_context.getResources().getDrawable(R.drawable.code_split1_on));
                                break;
                            } else {
                                Button_Split1.setTag(R.drawable.code_split1);
                                break;
                            }
                        }
                    }
                    throw new UnsupportedOperationException();
                case "SPLIT2":
                    if (item.valori.size() == 1) {
                        if (item.valori.get(0).codeValueType == CodeValueType.OnOff) {
                            if (item.valori.get(0).currentValue.equals("VALUE1")) {
                                Button_Split2.setTag(R.drawable.code_split2_on);
                                Button_Split2.setBackground(base_context.getResources().getDrawable(R.drawable.code_split2_on));
                                break;
                            } else {
                                Button_Split2.setTag(R.drawable.code_split2);
                                break;
                            }
                        }
                    }
                    throw new UnsupportedOperationException();
                default:
                    break;
            }
        }
    }

    //Per non visualizzare la Navigation bar del Dialog, inizialmente la setto non-focusable e poi con l'evento show la rimetto focusable (in questo modo non appare).
    public static void showImmersiveDialog(final Dialog mDialog, final Activity mActivity) {
        //Set the dialog to not focusable
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        mDialog.getWindow().getDecorView().setSystemUiVisibility(setSystemUiVisibility());

        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                //Clear the not focusable flag from the window
                mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                //Update the WindowManager with the new attributes
                WindowManager wm = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
                wm.updateViewLayout(mDialog.getWindow().getDecorView(), mDialog.getWindow().getAttributes());
            }
        });


        mDialog.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    mDialog.getWindow().getDecorView().setSystemUiVisibility(setSystemUiVisibility());
                }
            }
        });
    }

    public static int setSystemUiVisibility() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code);
    }
    @Override
    public void onResume() {
        super.onResume();
     //   TextView TextView_slow_valore = findViewById(R.id.textView_slow_valore);
      //  TextView_slow_valore.setText("press to set (0 = normal)");

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
