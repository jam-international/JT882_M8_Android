package com.jam_int.jt882_m8;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

/**
 * Created by Daniele Albani on 24/05/2018.
 */

public class EdgeButton {

    @SuppressLint("ClickableViewAccessibility")
    public static void CreaEdgeButton(final Mci_write multiCmd, final Button button, final String ic_button_press, final String ic_button, final Context applicationContext) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Check the event type
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        if (ic_button_press != null) {
                            int image_Premuto = applicationContext.getResources().getIdentifier(ic_button_press, "drawable", applicationContext.getPackageName());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                button.setBackground(applicationContext.getResources().getDrawable((image_Premuto)));
                            }
                        }
                        multiCmd.Fronte_positivo = true;
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if (ic_button != null) {
                            int image_Non_Premuto = applicationContext.getResources().getIdentifier(ic_button, "drawable", applicationContext.getPackageName());
                            button.setBackground(applicationContext.getResources().getDrawable((image_Non_Premuto)));
                        }
                        multiCmd.Fronte_negativo = true;
                        return true;
                    default:
                }
                return false;
            }
        });
    }

    public static void CreaEdgeButton_Frecce(final Mci_write multiCmd, final Button button, final String ic_button_press, final String ic_button, final Context applicationContext, final long delay) {
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Check the event type
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        if (ic_button_press != null) {
                            int image_Premuto = applicationContext.getResources().getIdentifier(ic_button_press, "drawable", applicationContext.getPackageName());
                            button.setBackground(applicationContext.getResources().getDrawable((image_Premuto)));
                        }

                        multiCmd.valore = 1.0d;
                        multiCmd.write_flag = true;

                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        // RELEASED
                        if (ic_button_press != null) {
                            int image_Non_Premuto = applicationContext.getResources().getIdentifier(ic_button, "drawable", applicationContext.getPackageName());
                            button.setBackground(applicationContext.getResources().getDrawable((image_Non_Premuto)));
                        }

                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        multiCmd.valore = 0.0d;
                        multiCmd.write_flag = true;

                        return true;
                    default:
                }
                return false;
            }
        });
    }

    /**
     * Function for display the current VB status
     *
     * @param Mci
     * @param button
     * @param stato_off
     * @param stato_on
     * @param applicationContext
     */
    public static void Visualizza_stato_VB(final Mci_write Mci, final Button button, final String stato_off, final String stato_on, final Context applicationContext) {
        if ((Double) Mci.mci.getValue() == 1.0d) {
            int image_Premuto = applicationContext.getResources().getIdentifier(stato_on, "drawable", applicationContext.getPackageName());
            button.setBackground(applicationContext.getResources().getDrawable((image_Premuto)));
            Mci.valore_precedente = 1.0d;
        } else {
            int image_Premuto = applicationContext.getResources().getIdentifier(stato_off, "drawable", applicationContext.getPackageName());
            button.setBackground(applicationContext.getResources().getDrawable((image_Premuto)));
            Mci.valore_precedente = 0.0d;
        }
    }
}
