package com.jam_int.jt882_m8;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

public class SelectType1Activity extends AppCompatActivity {
    Thread_LoopEmergenza thread_LoopEmergenza;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_type1);
        Log.d("JAM TAG", "SelectType1Activity");
        ViewFlipper vf = findViewById(R.id.ViewFlipper1);
        Values.type = vf.getDisplayedChild() + 1;

        thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
        thread_LoopEmergenza.thread_LoopEmergenza_Start(this);
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
    @Override
    protected void onResume() {
        super.onResume();
        if(!thread_LoopEmergenza.getThreadStatus()){
            thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
            thread_LoopEmergenza.thread_LoopEmergenza_Start(this);
            Log.d("JAM TAG", "SelectType1Activity");

        }
        // Set the default fermature repetition value
        Values.Fi1t = 2;
        Values.Ff1t = 1;
    }

    /**
     * Button for go to the next element of the view flipper
     *
     * @param v
     */
    public void BtnNext(View v) {
        ViewFlipper vf = findViewById(R.id.ViewFlipper1);
        vf.showNext();
        ViewFlipper vf1 = findViewById(R.id.ViewFlipper2);
        vf1.showNext();
        LinearLayout ll2 = findViewById(R.id.LinearLayout2);
        TextView txt22 = findViewById(R.id.textView22);
        TextView txt21 = findViewById(R.id.textView21);
        if (vf.getDisplayedChild() == 0 || vf.getDisplayedChild() == 1 || vf.getDisplayedChild() == 6 || vf.getDisplayedChild() == 7) {
            ll2.setVisibility(View.GONE);
            vf1.setVisibility(View.GONE);
            txt22.setVisibility(View.GONE);
            txt21.setVisibility(View.INVISIBLE);
        } else {
            ll2.setVisibility(View.VISIBLE);
            vf1.setVisibility(View.VISIBLE);
            txt22.setVisibility(View.VISIBLE);
            txt21.setVisibility(View.VISIBLE);
        }
        Values.type = vf.getDisplayedChild() + 1;
    }

    /**
     * Button for go to the previous element of the view flipper
     *
     * @param v
     */
    public void BtnBack(View v) {
        ViewFlipper vf = findViewById(R.id.ViewFlipper1);
        vf.showPrevious();
        ViewFlipper vf1 = findViewById(R.id.ViewFlipper2);
        vf1.showPrevious();
        LinearLayout ll2 = findViewById(R.id.LinearLayout2);
        TextView txt22 = findViewById(R.id.textView22);
        TextView txt21 = findViewById(R.id.textView21);
        if (vf.getDisplayedChild() == 0 || vf.getDisplayedChild() == 1 || vf.getDisplayedChild() == 6 || vf.getDisplayedChild() == 7) {
            ll2.setVisibility(View.GONE);
            vf1.setVisibility(View.GONE);
            txt22.setVisibility(View.GONE);
            txt21.setVisibility(View.INVISIBLE);
        } else {
            ll2.setVisibility(View.VISIBLE);
            vf1.setVisibility(View.VISIBLE);
            txt22.setVisibility(View.VISIBLE);
            txt21.setVisibility(View.VISIBLE);
        }
        Values.type = vf.getDisplayedChild() + 1;
    }

    /**
     * Button for go to the next Activity
     *
     * @param v
     */
    public void BtnNext1(View v) {
        Intent page3 = new Intent(this, MarginActivity.class);
        startActivity(page3);
    }

    /**
     * Button for go to the Skip activity
     *
     * @param v
     */
    public void BtnSkip(View v) {
        Intent page3 = new Intent(this, SkipActivity.class);
        startActivity(page3);
    }

    /**
     * Close the current activity and go back
     *
     * @param v
     */
    public void BtnBack1(View v) {
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