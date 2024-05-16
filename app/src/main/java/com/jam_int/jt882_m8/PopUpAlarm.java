package com.jam_int.jt882_m8;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PopUpAlarm extends AppCompatActivity {
    TextView TextView_allarmi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_alarm);
        Intent intent = getIntent();
        String stringAlarm = intent.getExtras().getString("stringAlarm");


        TextView_allarmi = (TextView)findViewById(R.id.textView_allarmi);
        TextView_allarmi.setText(stringAlarm);
    }
    /**
     * Button for exit
     *
     * @param view
     */
    public void onClick_Button_exit(View view) {
        finish();
    }
}
