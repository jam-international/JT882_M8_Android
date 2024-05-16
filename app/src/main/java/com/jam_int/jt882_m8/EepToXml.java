package com.jam_int.jt882_m8;

import android.graphics.PointF;
import android.os.Environment;

import com.jamint.ricette.CodeType;
import com.jamint.ricette.CodeValue;
import com.jamint.ricette.CodeValueType;
import com.jamint.ricette.Element;
import com.jamint.ricette.JamPointStep;
import com.jamint.ricette.Ricetta;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import me.jahnen.libaums.core.fs.UsbFile;
import me.jahnen.libaums.core.fs.UsbFileInputStream;

public class EepToXml {

    /**
     * Function for convert a Eep file to Usr
     *
     * @param file_eep
     * @return
     * @throws IOException
     */
    public static File ConvertEepToUsr(File file_eep) throws IOException {
        File root1 = Environment.getExternalStorageDirectory();
        File Usr_out = new File(root1.getAbsolutePath() + "/ricette/eepTousr.txt");

        byte[] Listato = new byte[(int) file_eep.length()];
        DataInputStream dis = null;

        try {
            dis = new DataInputStream(new FileInputStream(file_eep));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            dis.readFully(Listato);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //inizio a leggere i byte ed a comporre il listato di stringhe

        int numero_punti_programma = 0;
        ArrayList<String> Listato_Usr = new ArrayList<>();
        try {
            //[Name]
            Listato_Usr.add("[Name]");
            String nome = "";
            for (int i = 0x0A; i < 0x21; i++) {

                char c = (char) (Listato[i]);
                if (c != '\0')
                    nome = nome + c;

            }
            Listato_Usr.add(nome);
            Listato_Usr.add(""); //linea vuota

            //[Numero]
            Listato_Usr.add("[Numero]");
            double n = Listato[0x0] * 256 + Listato[0x1];
            DecimalFormat df = new DecimalFormat("#");
            String nst = df.format(n);
            Listato_Usr.add(nst);
            Listato_Usr.add(""); //linea vuota

            //[Header VA Len]
            Listato_Usr.add("[Header VA Len]");
            Listato_Usr.add(df.format(Listato[0x2f]));
            Listato_Usr.add(""); //linea vuota

            //[Data VA Len]
            Listato_Usr.add("[Data VA Len]");
            Listato_Usr.add(df.format(Listato[0x29]));
            Listato_Usr.add(""); //linea vuota

            //[Base Matrix]
            Listato_Usr.add("[Base Matrix]");
            n = Listato[0x27] * 256 + Listato[0x28];
            if (n > 0x7FFF) {
                n = -((0xFFFF - n) + 1);
            }
            nst = df.format(n);
            Listato_Usr.add(nst);
            Listato_Usr.add(""); //linea vuota

            //[Header Matrix]
            Listato_Usr.add("[Header Matrix]");
            n = Listato[0x2d] * 256 + Listato[0x2e];
            if (n > 0x7FFF) {
                n = -((0xFFFF - n) + 1);
            }
            nst = df.format(n);
            Listato_Usr.add(nst);
            Listato_Usr.add(""); //linea vuota

            //[Header]
            df = new DecimalFormat("#.#");
            Listato_Usr.add("[Header]");
            for (int i = 0; i < 10; i++) {
                int a = (Listato[0x32 + i * 4]) & 0xFF;
                double b = a * 16777216;

                int c = Listato[0x33 + i * 4] & 0xFF;
                int c1 = c & 0xFF;
                double d = c * 65536;

                int e = Listato[0x34 + i * 4] & 0xFF;
                double f = e * 256;

                int g = Listato[0x35 + i * 4] & 0xFF;
                double ln = b + d + f + g;

                if (ln > 0x7FFFFFFF) {
                    ln = -((0xFFFFFFFF - ln) + 1) / 1000;
                } else {
                    ln = ln / 1000;
                }
                nst = String.valueOf(ln);
                Listato_Usr.add("VQ:" + nst);
            }
            Listato_Usr.add("[Header End]");
            Listato_Usr.add(""); //linea vuota

            //numero punti programma
            numero_punti_programma = Listato[0x08] * 256 + Listato[0x09];


            //[Step]
            int address = 0x5a;
            for (int i = 0; i <= numero_punti_programma; i++) {
                if (address < Listato.length) {


                    Listato_Usr.add("[Step]");
                    n = (Listato[address] & 0xFF) * 256 + (Listato[address + 1] & 0xFF);
                    if (n > 0x7FFF) {
                        n = -((0xFFFF - n) + 1);
                    }
                    nst = String.valueOf(n);
                    Listato_Usr.add("VN:" + nst);

                    n = (Listato[address + 2] & 0xFF) * 16777216 + (Listato[address + 3] & 0xFF) * 65536 + (Listato[address + 4] & 0xFF) * 256 + (Listato[address + 5] & 0xFF);
                    if (n > 0x7FFFFFFF) {
                        n = -((0xFFFFFFFF - n) + 1);
                        n = n / 1000;
                    } else {
                        n = n / 1000;

                    }
                    nst = String.valueOf(n);
                    Listato_Usr.add("VQ:" + nst);

                    n = (Listato[address + 6] & 0xFF) * 16777216 + (Listato[address + 7] & 0xFF) * 65536 + (Listato[address + 8] & 0xFF) * 256 + (Listato[address + 9] & 0xFF);
                    if (n > 0x7FFFFFFF) {
                        n = -((0xFFFFFFFF - n) + 1) / 1000;
                    } else {
                        n = n / 1000;
                    }
                    nst = String.valueOf(n);
                    Listato_Usr.add("VQ:" + nst);

                    n = (Listato[address + 10] & 0xFF) * 16777216 + (Listato[address + 11] & 0xFF) * 65536 + (Listato[address + 12] & 0xFF) * 256 + (Listato[address + 13] & 0xFF);
                    if (n > 0x7FFFFFFF) {
                        n = -((0xFFFFFFFF - n) + 1) / 1000;
                    } else {
                        n = n / 1000;
                    }
                    nst = String.valueOf(n);
                    Listato_Usr.add("VQ:" + nst);

                    n = (Listato[address + 14] & 0xFF) * 16777216 + (Listato[address + 15] & 0xFF) * 65536 + (Listato[address + 16] & 0xFF) * 256 + (Listato[address + 17] & 0xFF);
                    if (n > 0x7FFFFFFF) {
                        n = -((0xFFFFFFFF - n) + 1) / 1000;
                    } else {
                        n = n / 1000;
                    }
                    nst = String.valueOf(n);
                    Listato_Usr.add("VQ:" + nst);

                    Listato_Usr.add("[End Step]");
                    Listato_Usr.add(""); //linea vuota

                    address = address + 18;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FileWriter writer = new FileWriter(Usr_out.getAbsolutePath());
        for (String str : Listato_Usr) {
            writer.write(str + System.lineSeparator());
        }
        writer.close();


        return Usr_out;
    }

    /**
     * Function for convert a Eep file on usb to a Usr file
     *
     * @param file_eep
     * @return
     * @throws IOException
     */
    public static File ConvertEepToUsr_UsbFile(UsbFile file_eep) throws IOException {
        File root1 = Environment.getExternalStorageDirectory();
        File Usr_out = new File(root1.getAbsolutePath() + "/ricette/eepTousr.txt");


        byte[] Listato = new byte[(int) file_eep.getLength()];
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new UsbFileInputStream(file_eep));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            dis.readFully(Listato);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //inizio a leggere i byte ed a comporre il listato di stringhe

        int numero_punti_programma = 0;
        ArrayList<String> Listato_Usr = new ArrayList<>();
        try {
            //[Name]
            Listato_Usr.add("[Name]");
            String nome = "";
            for (int i = 0x0A; i < 0x21; i++) {

                char c = (char) (Listato[i]);
                if (c != '\0')
                    nome = nome + c;

            }
            Listato_Usr.add(nome);
            Listato_Usr.add(""); //linea vuota

            //[Numero]
            Listato_Usr.add("[Numero]");
            double n = Listato[0x0] * 256 + Listato[0x1];
            DecimalFormat df = new DecimalFormat("#");
            String nst = df.format(n);
            Listato_Usr.add("1");       //salvo sempre sul numero 1
            Listato_Usr.add(""); //linea vuota

            //[Header VA Len]
            Listato_Usr.add("[Header VA Len]");
            Listato_Usr.add(df.format(Listato[0x2f]));
            Listato_Usr.add(""); //linea vuota

            //[Data VA Len]
            Listato_Usr.add("[Data VA Len]");
            Listato_Usr.add(df.format(Listato[0x29]));
            Listato_Usr.add(""); //linea vuota

            //[Base Matrix]
            Listato_Usr.add("[Base Matrix]");
            n = Listato[0x27] * 256 + Listato[0x28];
            if (n > 0x7FFF) {
                n = -((0xFFFF - n) + 1);
            }
            nst = df.format(n);
            Listato_Usr.add(nst);
            Listato_Usr.add(""); //linea vuota

            //[Header Matrix]
            Listato_Usr.add("[Header Matrix]");
            n = Listato[0x2d] * 256 + Listato[0x2e];
            if (n > 0x7FFF) {
                n = -((0xFFFF - n) + 1);
            }
            nst = df.format(n);
            Listato_Usr.add(nst);
            Listato_Usr.add(""); //linea vuota

            //[Header]
            df = new DecimalFormat("#.#");
            Listato_Usr.add("[Header]");
            for (int i = 0; i < 10; i++) {
                int a = (Listato[0x32 + i * 4]) & 0xFF;
                double b = a * 16777216;

                int c = Listato[0x33 + i * 4] & 0xFF;
                int c1 = c & 0xFF;
                double d = c * 65536;

                int e = Listato[0x34 + i * 4] & 0xFF;
                double f = e * 256;

                int g = Listato[0x35 + i * 4] & 0xFF;
                double ln = b + d + f + g;

                if (ln > 0x7FFFFFFF) {
                    ln = -((0xFFFFFFFF - ln) + 1) / 1000;
                } else {
                    ln = ln / 1000;
                }
                nst = String.valueOf(ln);
                Listato_Usr.add("VQ:" + nst);
            }
            Listato_Usr.add("[Header End]");
            Listato_Usr.add(""); //linea vuota

            //numero punti programma
            numero_punti_programma = ((Listato[0x08]) & 0xFF) + (256 + ((Listato[0x09]) & 0xFF));

            //[Step]
            int address = 0x5a;
            for (int i = 0; i <= numero_punti_programma; i++) {
                if (address < Listato.length) {
                    Listato_Usr.add("[Step]");
                    n = (Listato[address] & 0xFF) * 256 + (Listato[address + 1] & 0xFF);
                    if (n > 0x7FFF) {
                        n = -((0xFFFF - n) + 1);
                    }
                    nst = String.valueOf(n);
                    Listato_Usr.add("VN:" + nst);

                    n = (Listato[address + 2] & 0xFF) * 16777216 + (Listato[address + 3] & 0xFF) * 65536 + (Listato[address + 4] & 0xFF) * 256 + (Listato[address + 5] & 0xFF);
                    if (n > 0x7FFFFFFF) {
                        n = -((0xFFFFFFFF - n) + 1);
                        n = n / 1000;
                    } else {
                        n = n / 1000;
                    }
                    nst = String.valueOf(n);
                    Listato_Usr.add("VQ:" + nst);

                    n = (Listato[address + 6] & 0xFF) * 16777216 + (Listato[address + 7] & 0xFF) * 65536 + (Listato[address + 8] & 0xFF) * 256 + (Listato[address + 9] & 0xFF);
                    if (n > 0x7FFFFFFF) {
                        n = -((0xFFFFFFFF - n) + 1) / 1000;
                    } else {
                        n = n / 1000;
                    }
                    nst = String.valueOf(n);
                    Listato_Usr.add("VQ:" + nst);

                    n = (Listato[address + 10] & 0xFF) * 16777216 + (Listato[address + 11] & 0xFF) * 65536 + (Listato[address + 12] & 0xFF) * 256 + (Listato[address + 13] & 0xFF);
                    if (n > 0x7FFFFFFF) {
                        n = -((0xFFFFFFFF - n) + 1) / 1000;
                    } else {
                        n = n / 1000;
                    }
                    nst = String.valueOf(n);
                    Listato_Usr.add("VQ:" + nst);

                    n = (Listato[address + 14] & 0xFF) * 16777216 + (Listato[address + 15] & 0xFF) * 65536 + (Listato[address + 16] & 0xFF) * 256 + (Listato[address + 17] & 0xFF);
                    if (n > 0x7FFFFFFF) {
                        n = -((0xFFFFFFFF - n) + 1) / 1000;
                    } else {
                        n = n / 1000;
                    }
                    nst = String.valueOf(n);
                    Listato_Usr.add("VQ:" + nst);

                    Listato_Usr.add("[End Step]");
                    Listato_Usr.add(""); //linea vuota

                    address = address + 18;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FileWriter writer = new FileWriter(Usr_out.getAbsolutePath());
        for (String str : Listato_Usr) {
            writer.write(str + System.lineSeparator());
        }
        writer.close();

        return Usr_out;
    }

    /**
     * Function for get points form a usr file
     *
     * @param UsrFromEep
     * @return
     */
    public static ArrayList<ArrayList<String>> getPointsFromUsr(File UsrFromEep) {
        ArrayList<ArrayList<String>> Lista_points = new ArrayList<>();
        ArrayList<String> punto = new ArrayList<>();
        try {
            BufferedReader b = new BufferedReader(new FileReader(UsrFromEep));

            String readLine = "";

            while ((readLine = b.readLine()) != null) {
                switch (readLine) {
                    /*case "[Name]":
                        break;
                    case "[Numero]":
                            break;
                    case "[Header VA Len]":
                        break;
                    case "[Data VA Len]":
                        break;
                    case "[Base Matrix]":
                        break;
                    case "[Header Matrix]":
                        break;*/
                    case "[Header]":
                        //PCX
                        readLine = b.readLine();
                        String linea_pcx = readLine.replace("VQ:", "");
                        //PCY
                        readLine = b.readLine();
                        String linea_pcy = readLine.replace("VQ:", "");

                        punto.add("PC");
                        punto.add(linea_pcx);
                        punto.add(linea_pcy);
                        Lista_points.add(punto);
                        break;
                    case "[Step]":
                        punto = new ArrayList<>();
                        readLine = b.readLine();

                        String linea_codice = readLine.replace("VN:", "");
                        if (linea_codice.contains("."))
                            linea_codice = linea_codice.substring(0, linea_codice.indexOf("."));
                        //X
                        readLine = b.readLine();

                        String linea_x = readLine.replace("VQ:", "");
                        readLine = b.readLine();

                        String linea_y = readLine.replace("VQ:", "");

                        punto.add(linea_codice);
                        punto.add(linea_x);
                        punto.add(linea_y);
                        Lista_points.add(punto);
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Lista_points;
    }

    /**
     * Function for create a Ricetta from a list of points
     *
     * @param points
     * @return
     */
    public static Ricetta CreaRicetta(ArrayList<ArrayList<String>> points) {
        Ricetta r = new Ricetta(Values.plcType);
        if (points.size() > 0) {
            ArrayList<String> PC_array = points.get(0);
            try {
                float PCX = Float.parseFloat(PC_array.get(1));
                float PCY = Float.parseFloat(PC_array.get(2));

                r.setDrawPosition(new PointF(PCX, PCY));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                for (int i = 1; i < points.size(); i++) {
                    ArrayList<String> Punto_array = points.get(i);
                    switch (Punto_array.get(0)) {
                        case "0":
                            float X = Float.parseFloat(Punto_array.get(1));
                            float Y = Float.parseFloat(Punto_array.get(2));
                            r.drawFeedTo(new PointF(X, Y));
                            break;
                        case "6":
                            X = Float.parseFloat(Punto_array.get(1));
                            Y = Float.parseFloat(Punto_array.get(2));
                            r.drawLineTo(new PointF(X, Y), 12);
                            break;
                        case "103":
                            //prendo lo step dell'ultimo elemento inserito visto che ogni punto eep è un elemento
                            int numero_elementi = r.elements.size();
                            Element el = r.elements.get(numero_elementi - 1);
                            int num_step = el.steps.size();
                            JamPointStep step = r.elements.get(numero_elementi - 1).steps.get(num_step - 1);

                            String codice = Punto_array.get(1);
                            if (codice.contains("."))
                                codice = codice.substring(0, codice.indexOf("."));
                            String valore = Punto_array.get(2);

                            switch (codice) {
                                case "771":
                                    if (valore.contains("1")) {
                                        r.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                                    } else {
                                        r.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                                    }
                                    break;
                                case "772":
                                    if (valore.contains("1")) {
                                        r.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                                    } else {
                                        r.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                                    }
                                    break;
                                case "773":
                                    if (valore.contains("1")) {
                                        r.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                                    } else {
                                        r.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                                    }
                                    break;
                                case "667":
                                    if (valore.contains("1")) {
                                        r.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                                    } else {
                                        r.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                                    }
                                    break;
                                case "668":
                                    if (valore.contains("1")) {
                                        r.addStepCode(step, CodeType.SPEED2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                                    } else {
                                        r.addStepCode(step, CodeType.SPEED2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                                    }
                                    break;
                                case "669":
                                    if (valore.contains("1")) {
                                        r.addStepCode(step, CodeType.SPEED3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                                    } else {
                                        r.addStepCode(step, CodeType.SPEED3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                                    }
                                    break;
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return r;
    }
}
