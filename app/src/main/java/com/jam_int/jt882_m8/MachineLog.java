package com.jam_int.jt882_m8;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * This is a class that write the log of the machine status
 */
public class MachineLog {

    /**
     * Using this class is a lot better than having a function inside MainActivity
     */

    static ArrayList<String> MachineLog = new ArrayList();
    static ArrayList<String> MachineLog_prec = new ArrayList<String>(Arrays.asList("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""));

    /*************************************************************************************************
     var.1 data
     var.2 operatore
     var.3 vb30 in cucitura
     var.4 Vq3591 contatore produzione
     var.5 Vn2 allarmi HMI
     var.6 Vn4 warning HMI
     var 7: vq1110 velocità cucitura
     var 8:  TextView_nomeprog_R_val
     var 9: TextView_nomeprog_L_val
     *************************************************************************************************/

    /**
     * Function for write the log
     */
    public static void write(String numero_operatore, Double vb30, Double vq3591, Double vn2, Double vn4, Double vq1110, String prog_DX, String prog_SX) {
        boolean scrivi = false;
        try {
            Date now = new Date();

            String Date = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(now);
            String Time = new SimpleDateFormat("HHmmss", Locale.ENGLISH).format(now);

            //var 1: Data
            if (!MachineLog_prec.get(0).equals(Date)) {
                MachineLog_prec.set(0, Date);
                MachineLog.add("1|" + Date);
                scrivi = true;
            }

            //var 2: operatore
            if (!MachineLog_prec.get(1).equals(numero_operatore)) {
                MachineLog_prec.set(1, numero_operatore);
                MachineLog.add("2|" + numero_operatore + "|" + Time);
                scrivi = true;
            }

            //var 3: Vb30 in cucitura
            //Double vb30_d = (Double) MultiCmd_Vb30_C1_InCucitura.getValue();
            Integer vb30_int = vb30.intValue();

            if (!MachineLog_prec.get(2).equals(String.valueOf(vb30_int))) {
                MachineLog_prec.set(2, String.valueOf(vb30_int));
                MachineLog.add("3|" + vb30_int + "|" + Time);
                scrivi = true;
            }

            //var 4: Vq3591 contatore produzione
            //Double vq3591_d = (Double) Multicmd_Vq3591_CNT_CicliAutomaticoUser.getValue();
            Integer vq3591_int = vq3591.intValue();
            if (!MachineLog_prec.get(3).equals(String.valueOf(vq3591_int))) {
                MachineLog_prec.set(3, String.valueOf(vq3591_int));
                MachineLog.add("4|" + vq3591_int + "|" + Time);
                scrivi = true;
            }

            //var 5: Vn2 allarmi HMI
            //Double vn2_d = (Double) MultiCmd_Vn2_allarmi_da_CN.getValue();
            Integer vn2_int = vn2.intValue();

            if (!MachineLog_prec.get(4).equals(String.valueOf(vn2_int))) {
                MachineLog_prec.set(4, String.valueOf(vn2_int));
                if (vn2_int != 0) {
                    MachineLog.add("5|" + vn2_int + "|" + Time);
                    scrivi = true;
                }
            }

            //var 6: Vn4 warning HMI
            //Double vn4d = (Double) MultiCmd_Vn4_Warning.getValue();
            Integer vn4i = vn4.intValue();
            if (!MachineLog_prec.get(5).equals(String.valueOf(vn4i))) {
                MachineLog_prec.set(5, String.valueOf(vn4i));
                if (vn4i != 0) {
                    MachineLog.add("6|" + vn4i + "|" + Time);
                    scrivi = true;
                }
            }

            //var 7: vq1110 velocità cucitura
            //Double vq1110d = (Double) MultiCmd_Vq1110_Speed.getValue() / 1000;
            Integer vq1110i = vq1110.intValue();
            if (!MachineLog_prec.get(6).equals(String.valueOf(vq1110i))) {
                MachineLog_prec.set(6, String.valueOf(vq1110i));
                MachineLog.add("7|" + vq1110i + "|" + Time);
                scrivi = true;
            }

            //var 8: nome programma dx
            //String prog_DX = TextView_nomeprog_R_val.getText().toString();
            if (!MachineLog_prec.get(7).equals(prog_DX)) {
                MachineLog_prec.set(7, prog_DX);
                MachineLog.add("8|" + prog_DX + "|" + Time);
                scrivi = true;
            }

            //var 9: nomr programma sx
            //String prog_SX = TextView_nomeprog_L_val.getText().toString();
            if (!MachineLog_prec.get(8).equals(prog_SX)) {
                MachineLog_prec.set(8, prog_SX);
                MachineLog.add("9|" + prog_SX + "|" + Time);
                scrivi = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("" + e);
        }

        if (MachineLog.size() > 0 && scrivi) {
            try {
                // Open given file in append mode.
                BufferedWriter out = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/JamData/MachineLog.txt", true));
                for (String riga : MachineLog) {
                    out.write(riga + "\n");
                }
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MachineLog.clear();
        }
    }


}
