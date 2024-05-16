package com.jam_int.jt882_m8;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class PopUpSettings extends AppCompatActivity {

    /**
     * Broadcast receiver for receive the event of password changed
     */
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();

                String val = intent.getStringExtra("ret_valore");

                if (!val.equals("")) {

                    try {
                        File password = new File(Environment.getExternalStorageDirectory() + "/JamData/Password.txt");

                        BufferedReader br = new BufferedReader(new FileReader(password.getAbsolutePath()));
                        String linea1 = br.readLine();  //password livello 1 attuale
                        String linea2 = br.readLine();  //password livello 2 attuale

                        BufferedWriter bw = new BufferedWriter(new FileWriter(password));

                        bw.write(String.format("%s%n", val));   //nuova password livello 1
                        bw.write(String.format("%s%n", linea2)); //stessa password levello 2

                        bw.close();
                        Toast.makeText(getApplicationContext(), "Password saved", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Password file error", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error: Password not saved", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_settings);

        try {
            ImageButton btnTcp = findViewById(R.id.btn_TCP);
            if(Values.Tcp_enable_status.equals("true")){
                btnTcp.setImageResource(R.drawable.ic_tcp_on);
            } else {
                btnTcp.setImageResource(R.drawable.ic_tcp_off);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Button for exit
     *
     * @param view
     */
    public void onClick_Button_exit(View view) {
        finish();
    }

    /**
     * Button for open reset activity
     *
     * @param view
     */
    public void On_click_btn_reset(View view) {
        Intent settings = new Intent(getApplicationContext(), PopUpReset.class);
        startActivityForResult(settings, 1);
    }

    /**
     * Button for change password
     *
     * @param view
     */
    public void On_click_btn_change_password(View view) {
        KeyDialog.Lancia_KeyDialogo(null, PopUpSettings.this, null, 99999d, 0d, false, false, 0d, true, "KeyDialog_parameter_ret", false,"");
    }

    /**
     * Button for enable/disable login
     *
     * @param view
     */
    public void On_click_btn_disable_login(View view) {
    }



    public void On_click_btn_socketTcp(View view) {
        try {
            ImageButton btnlogin = findViewById(R.id.btn_TCP);
            if(Values.Tcp_enable_status.equals("false")){
                btnlogin.setImageResource(R.drawable.ic_tcp_on);
                Info_file.Scrivi_campo("storage/emulated/0/JamData/Tcp.txt", "InfoJAM", "Tcp", null, null, "TcpEnable", "true", getApplicationContext());
                Values.Tcp_enable_status = "true";
            }else {
                btnlogin.setImageResource(R.drawable.ic_tcp_off);
                Info_file.Scrivi_campo("storage/emulated/0/JamData/Tcp.txt", "InfoJAM", "Tcp", null, null, "TcpEnable", "false", getApplicationContext());
                Values.Tcp_enable_status = "false";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Button for exit
     *
     * @param v
     */
    public void on_click_exit(View v) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("KeyDialog_parameter_ret"));
    }

    @Override
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
    }

    @Override                   //your activity is no longer visible to the user
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override                   //your activity is no longer visible to the user
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
