package com.jam_int.jt882_m8;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by Daniele Albani on 23/05/2018.
 */

public class KeyDialog_lettere extends Activity {

    /**
     * TODO i don't know well this activity
     * <p>
     * Why this have a static method and a onCreate???
     */

    public static void Lancia_KeyDialogo_lettere(final Activity activityctivity, final TextView editText, String text_originale) {
        Lancia_KeyDialogo_lettere(activityctivity, editText, text_originale, false);
    }

    public static void Lancia_KeyDialogo_lettere(final Activity activityctivity, final TextView editText, String text_originale, boolean password) {

        final Dialog dialog = new Dialog(activityctivity);

        // Include dialog.xml file
        dialog.setContentView(R.layout.keydialog_lettere);
        // Set dialog title
        dialog.setTitle(text_originale);


        // set values for custom dialog components - text, image and button
        final TextView text = dialog.findViewById(R.id.textDialog);
        if (password) {
            text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        text.setText(text_originale);


        showImmersiveDialog(dialog, activityctivity);
        dialog.show();
        dialog.getWindow().setLayout(800, 600);


        Button Pulsante1 = dialog.findViewById(R.id.button_1);
        Pulsante1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "1");
            }
        });
        Button Pulsante2 = dialog.findViewById(R.id.button_2);
        Pulsante2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "2");
            }
        });
        Button Pulsante3 = dialog.findViewById(R.id.button_3);
        Pulsante3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "3");
            }
        });
        Button Pulsante4 = dialog.findViewById(R.id.button_4);
        Pulsante4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "4");
            }
        });
        Button Pulsante5 = dialog.findViewById(R.id.button_5);
        Pulsante5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "5");
            }
        });
        Button Pulsante6 = dialog.findViewById(R.id.button_6);
        Pulsante6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "6");
            }
        });
        Button Pulsante7 = dialog.findViewById(R.id.button_7);
        Pulsante7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "7");
            }
        });
        Button Pulsante8 = dialog.findViewById(R.id.button_8);
        Pulsante8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "8");
            }
        });
        Button Pulsante9 = dialog.findViewById(R.id.button_9);
        Pulsante9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "9");
            }
        });
        Button Pulsante0 = dialog.findViewById(R.id.button_0);
        Pulsante0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "0");
            }
        });

        Button PulsanteQ = dialog.findViewById(R.id.button_Q);
        PulsanteQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "Q");
            }
        });
        Button PulsanteW = dialog.findViewById(R.id.button_W);
        PulsanteW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "W");
            }
        });
        Button PulsanteE = dialog.findViewById(R.id.button_E);
        PulsanteE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "E");
            }
        });
        Button PulsanteR = dialog.findViewById(R.id.button_R);
        PulsanteR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "R");
            }
        });
        Button PulsanteT = dialog.findViewById(R.id.button_T);
        PulsanteT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "T");
            }
        });
        Button PulsanteY = dialog.findViewById(R.id.button_Y);
        PulsanteY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "Y");
            }
        });
        Button PulsanteU = dialog.findViewById(R.id.button_U);
        PulsanteU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "U");
            }
        });
        Button PulsanteI = dialog.findViewById(R.id.button_I);
        PulsanteI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "I");
            }
        });
        Button PulsanteO = dialog.findViewById(R.id.button_O);
        PulsanteO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "O");
            }
        });

        Button PulsanteP = dialog.findViewById(R.id.button_P);
        PulsanteP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "P");
            }
        });
        Button PulsanteA = dialog.findViewById(R.id.button_A);
        PulsanteA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "A");
            }
        });

        Button PulsanteS = dialog.findViewById(R.id.button_S);
        PulsanteS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "S");
            }
        });
        Button
                PulsanteD = dialog.findViewById(R.id.button_D);
        PulsanteD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "D");
            }
        });

        Button PulsanteF = dialog.findViewById(R.id.button_F);
        PulsanteF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "F");
            }
        });

        Button PulsanteG = dialog.findViewById(R.id.button_G);
        PulsanteG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "G");
            }
        });

        Button PulsanteH = dialog.findViewById(R.id.button_H);
        PulsanteH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "H");
            }
        });

        Button PulsanteJ = dialog.findViewById(R.id.button_J);
        PulsanteJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "J");
            }
        });

        Button PulsanteK = dialog.findViewById(R.id.button_K);
        PulsanteK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "K");
            }
        });

        Button PulsanteL = dialog.findViewById(R.id.button_L);
        PulsanteL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "L");
            }
        });

        Button PulsanteZ = dialog.findViewById(R.id.button_Z);
        PulsanteZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "Z");
            }
        });

        Button PulsanteX = dialog.findViewById(R.id.button_X);
        PulsanteX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "X");
            }
        });

        Button PulsanteC = dialog.findViewById(R.id.button_C);
        PulsanteC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "C");
            }
        });

        Button PulsanteV = dialog.findViewById(R.id.button_V);
        PulsanteV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "V");
            }
        });

        Button PulsanteB = dialog.findViewById(R.id.button_B);
        PulsanteB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "B");
            }
        });

        Button PulsanteN = dialog.findViewById(R.id.button_N);
        PulsanteN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "N");
            }
        });

        Button PulsanteM = dialog.findViewById(R.id.button_M);
        PulsanteM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "M");
            }
        });

        Button Pulsante_trattino_centro = dialog.findViewById(R.id.button_trattino_centro);
        Pulsante_trattino_centro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "-");
            }
        });

        Button Pulsante_trattino_basso = dialog.findViewById(R.id.button_trattino_basso);
        Pulsante_trattino_basso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + "_");
            }
        });

        Button Pulsante_spazio = dialog.findViewById(R.id.button_spazio);
        Pulsante_spazio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text.getText().length() < 23)
                    text.setText(text.getText() + " ");
            }
        });

        Button Pulsante_cancella_last = dialog.findViewById(R.id.button_cancella_last);
        Pulsante_cancella_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String testo = text.getText().toString();
                    if (testo != null && testo.length() > 0) {
                        testo = testo.substring(0, testo.length() - 1);
                        text.setText(testo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        Button declineButton = dialog.findViewById(R.id.declineButton);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.dismiss();
            }
        });
        Button CancelButton = dialog.findViewById(R.id.CancellaButton);
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText("");
            }
        });
        Button ConfermaButton = dialog.findViewById(R.id.ConfirmButton);
        ConfermaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String testo = text.getText().toString();
                testo = testo.substring(0, Math.min(testo.length(), 23));
                editText.setText(testo);

                dialog.dismiss();

            }
        });


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
        setContentView(R.layout.keydialog_lettere);
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
