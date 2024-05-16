package com.jam_int.jt882_m8;

import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import communication.ShoppingList;

/**
 * Created by Daniele Albani on 25/05/2018.
 */

public class Toggle_Button {

    /**
     * TODO i don't know well this class
     */
    public Toggle_Button() {
    }

    public static void CreaToggleButton(final Mci_write mci, final Button button, final String ic_press, final String ic_unpress, final Context applicationContext, final ShoppingList sl) {
        //al primo giro, imposto l'icona del button premuta oppure no a seconda della Vb letta, poi verrà cambiata dall'evento
        if (ic_press != null) {
            if ((Double) mci.mci.getValue() == 1.0d) {
                int image_Premuto = applicationContext.getResources().getIdentifier(ic_press, "drawable", applicationContext.getPackageName());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    button.setBackground(applicationContext.getResources().getDrawable((image_Premuto)));
                }
            } else {
                int image_Premuto = applicationContext.getResources().getIdentifier(ic_unpress, "drawable", applicationContext.getPackageName());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    button.setBackground(applicationContext.getResources().getDrawable((image_Premuto)));
                }
            }
        }

        button.setOnTouchListener(new View.OnTouchListener() {
            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mci.Fronte_positivo = true;
                }

                return false;
            }
        });
    }

    /**
     * Function for disable the image button
     *
     * @param button
     * @param image_filename
     * @param context
     */
    public static void Disabilita_Imagebutton(Button button, String image_filename, Context context) {
        button.setClickable(false);
        button.setEnabled(false);
        int image_Premuto = context.getResources().getIdentifier(image_filename, "drawable", context.getPackageName());
        button.setBackgroundResource(image_Premuto);
    }

    /**
     * Function for activate the image button
     *
     * @param button
     * @param image_filename
     * @param context
     */
    public static void Abilita_Imagebutton(Button button, String image_filename, Context context) {
        button.setClickable(true);
        button.setEnabled(true);
        button.setBackgroundResource(R.drawable.ic_login_on);
        int image_Premuto = context.getResources().getIdentifier(image_filename, "drawable", context.getPackageName());
        button.setBackgroundResource(image_Premuto);
    }
}
