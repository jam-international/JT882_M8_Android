package com.jam_int.jt882_m8;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Info_file {

    /**
     * TODO I don't know this well
     */

    /**
     * !!!! Le stringe dei livelli non devono contenere [Start... ]. esempio per entrare in    [StartEncodersParam]  devo chiamare  EncodersParam !!!!!!!!!!!!
     **/
    public static boolean Scrivi_campo(String path, String livello0, String livello1, String livello2, String livello3, String nome_variabile, String valore_da_scrivere, Context context) throws IOException {

        boolean done = false;
        if (livello1 == null) livello1 = livello2 = livello3 = livello0;
        if (livello2 == null) livello2 = livello3 = livello1;
        if (livello3 == null) livello3 = livello2;
        ArrayList<struct_Campo> Campi_scomposti = new ArrayList<>();
        Campi_scomposti = Scomponi_File(null, path);
        if (Campi_scomposti.size() > 0) {
            for (struct_Campo item : Campi_scomposti) {
                if (item.livello0.contains(Pulisci(livello0)) &&
                        item.livello1.contains(Pulisci(livello1)) &&
                        item.livello2.contains(Pulisci(livello2)) &&
                        item.livello3.contains(Pulisci(livello3)) /*&&
                       item.nome_variabile.equals(Pulisci(nome_variabile))*/
                ) {
                    if (item.nome_variabile.equals(Pulisci(nome_variabile))) {
                        item.valore = valore_da_scrivere;
                        ScrivoTuttiCampiSuFile(Campi_scomposti);
                        return true;
                    }
                }
            }
        }
        return done;
    }
    /**
     *
     * !!!! Le stringe dei livelli non devono contenere [Start... ]. esempio per entrare in    [StartEncodersParam]  devo chiamare  EncodersParam !!!!!!!!!!!!
     *
     **/
 /*   public static  boolean Scrivi_campo(String path, String livello0, String livello1, String livello2, String livello3, String nome_variabile, String valore_da_scrivere, Context context) throws IOException {

        boolean done = false;

        FileInputStream fstream = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String estensione = SubstringExtensions.After(path,".");
        String path_appoggio = SubstringExtensions.Before(path, ".") + "_tmp." + estensione;
        FileOutputStream fostream = new FileOutputStream(path_appoggio);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fostream));

        String line;

        try {
            while ((line = br.readLine()) != null)
            {
                writer.write(line + "\r\n");
                if (line.contains("[Start" + livello0 + "]"))
                {   //livello 0

                    if (livello1 != null && livello1 != "")
                    {
                        while ((line = br.readLine()) != null)
                        {
                            writer.write(line + "\r\n");
                            if (line.contains("[Start" + livello1 + "]"))
                            {   //livello 1.

                                if (livello2 != null && livello2 != "")
                                {
                                    while ((line = br.readLine()) != null)
                                    {
                                        writer.write(line + "\r\n");
                                        if (line.contains("[Start" + livello2 + "]"))
                                        {   //livello 2.
                                            if (livello3 != null && livello3 != "")
                                            {
                                                while ((line = br.readLine()) != null)
                                                {
                                                    writer.write(line + "\r\n");
                                                    if (line.contains("[Start" + livello3 + "]"))
                                                    {   //livello 3.
                                                        while ((line = br.readLine()) != null)
                                                        {
                                                            if (line.contains(nome_variabile) && !done)
                                                            {
                                                                line = SubstringExtensions.Before(line,"=") + "= " + valore_da_scrivere;
                                                                done = true;
                                                            }
                                                            writer.write(line + "\r\n");
                                                        }
                                                    }
                                                }
                                            }
                                            else
                                            { //non c'è livello3
                                                while ((line = br.readLine()) != null)
                                                {
                                                    if (line.contains(nome_variabile) && !done)
                                                    {
                                                        line = SubstringExtensions.Before(line, "=") + "= " + valore_da_scrivere;
                                                        done = true;
                                                    }
                                                    writer.write(line + "\r\n");
                                                }
                                            }
                                        }
                                    }
                                }
                                else
                                { //non c'è livello2
                                    while ((line = br.readLine()) != null)
                                    {
                                        if (line.contains(nome_variabile) && !done)
                                        {
                                            line = SubstringExtensions.Before(line, "=") + "= " + valore_da_scrivere;
                                            done = true;
                                        }
                                        writer.write(line + "\r\n");
                                    }
                                }
                            }
                        }
                    }
                    else
                    { //non c'è livello1
                        while ((line = br.readLine()) != null)
                        {
                            if (line.contains(nome_variabile) && !done)
                            {
                                line = SubstringExtensions.Before(line, "=") + "= " + valore_da_scrivere;
                                done = true;
                            }
                            writer.write(line + "\r\n");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();

                if (writer != null)
                    writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (done)
        {
            String file_bak = SubstringExtensions.Before(path, ".");
            try{
                File file = new File(file_bak + ".bak");
                file.delete(); //cancello eventuale file back
                Info_file.makecopy_file(path, file_bak + ".bak",null);  //sposto path originale nel bak

                File file_originale = new File(path);
                file_originale.delete(); //cancello file orginale

                Info_file.makecopy_file(path_appoggio, path,null);

                File file_appoggio = new File(path_appoggio);
                file_appoggio.delete(); //cancello file di appoggio
            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(context, "error write txt field files", Toast.LENGTH_SHORT).show();
            }
        }
        return  done;
    }
    */

    /**
     * !!!! Le stringe dei livelli non devono contenere [Start... ]. esempio per entrare in    [StartEncodersParam]  devo chiamare  EncodersParam !!!!!!!!!!!!
     **/
    public static String Leggi_campo(String path, String livello0, String livello1, String livello2, String livello3, String nome_variabile, Context context) throws IOException {

        String risultato = "";

        FileInputStream fstream = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                if (line.contains("[Start" + livello0 + "]")) {   //livello 0
                    if (livello1 != null && livello1 != "" && !livello1.contains("null")) {
                        while ((line = br.readLine()) != null) {
                            if (line.contains("[Start" + livello1 + "]")) {   //livello 1.
                                if (livello2 != null && livello2 != "" && !livello2.contains("null")) {
                                    while ((line = br.readLine()) != null) {
                                        if (line.contains("[Start" + livello2 + "]")) {   //livello 2.
                                            if (livello3 != null && livello3 != "" && !livello3.contains("null")) {
                                                while ((line = br.readLine()) != null) {
                                                    if (line.contains("[Start" + livello3 + "]")) {   //livello 3.
                                                        while ((line = br.readLine()) != null) {
                                                            if (line.contains(nome_variabile)) {
                                                                risultato = SubString.After(line, "=");
                                                                char first = risultato.charAt(0);
                                                                String tmp = risultato;
                                                                if (first == ' ')
                                                                    risultato = tmp.substring(1);
                                                                if (risultato.length() > 0) break;
                                                            }
                                                        }
                                                    }
                                                    if (risultato.length() > 0) break;
                                                }
                                            } else { //non c'è livello3
                                                while ((line = br.readLine()) != null) {
                                                    if (line.contains("[Start")) {
                                                        risultato = "errore";
                                                        break;
                                                    }  //trovato lo start del livello successivo, non va bene
                                                    if (line.contains(nome_variabile)) {
                                                        risultato = SubString.After(line, "=");
                                                        char first = risultato.charAt(0);
                                                        String tmp = risultato;
                                                        if (first == ' ')
                                                            risultato = tmp.substring(1);
                                                        if (risultato.length() > 0) break;
                                                    }
                                                }
                                            }
                                        }
                                        if (risultato.length() > 0) break;
                                    }
                                } else { //non c'è livello2
                                    while ((line = br.readLine()) != null) {
                                        if (line.contains("[Start")) {
                                            risultato = "errore";
                                            break;
                                        }  //trovato lo start del livello successivo, non va bene
                                        if (line.contains(nome_variabile)) {
                                            risultato = SubString.After(line, "=");
                                            char first = risultato.charAt(0);
                                            String tmp = risultato;
                                            if (first == ' ')
                                                risultato = tmp.substring(1);

                                            if (risultato.length() > 0) break;
                                        }
                                    }
                                }
                            }
                            if (risultato.length() > 0) break;
                        }
                    } else { //non c'è livello1
                        while ((line = br.readLine()) != null) {
                            if (line.contains("[Start")) {
                                risultato = "errore";
                                break;
                            }  //trovato lo start del livello successivo, non va bene
                            if (line.contains(nome_variabile)) {
                                risultato = SubString.After(line, "=");
                                char first = risultato.charAt(0);
                                String tmp = risultato;
                                if (first == ' ')
                                    risultato = tmp.substring(1);
                                if (risultato.length() > 0) break;
                            }
                        }
                    }
                }
                if (risultato.length() > 0) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "error reading txt field files", Toast.LENGTH_SHORT).show();
        }
        return risultato;
    }

    private static ArrayList<struct_Campo> Scomponi_File(ArrayList<struct_Campo> arrayCampi, String percorso) throws IOException {
        ArrayList<struct_Campo> arrayCampi_file = new ArrayList<>();
        boolean finito = false;
        String livello0 = "", livello1 = "", livello2 = "", livello3 = "", nome_variabile = "", valore = "", versione = "";
        String livello = "inizio";
        if (arrayCampi != null || percorso != null) {
            String path;
            if (arrayCampi != null)
                path = arrayCampi.get(0).path;
            else
                path = percorso;

            FileInputStream fstream = new FileInputStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String line = null;

            while ((line = br.readLine()) != null && !finito) {
                if (!line.equals("")) {
                    switch (livello) {
                        case "inizio":
                            if (line.contains("Start")) {
                                livello0 = line;
                                livello = "Versione";
                                struct_Campo Campo = new struct_Campo(path, "Livello0", Pulisci(line), "", "", "", "", "");
                                arrayCampi_file.add(Campo);
                            }
                            break;
                        case "Versione":
                            if (line.contains("Version")) {
                                versione = line;
                                livello = "Livello0Start";
                                struct_Campo Campo = new struct_Campo(path, "Versione", Pulisci(line), "", "", "", "", "");
                                arrayCampi_file.add(Campo);
                            }
                            break;
                        case "Livello0Start":
                            if (line.contains("Start")) {
                                livello = "Livello1Dentro";
                                livello1 = line;
                                struct_Campo Campo = new struct_Campo(path, "Livello1", Pulisci(line), "", "", "", "", "");
                                arrayCampi_file.add(Campo);
                            }
                            break;
                        case "Livello0Dentro":
                            if (line.contains("End")) {
                                livello = "Livello0Start";
                                struct_Campo Campo = new struct_Campo(path, "Livello0", Pulisci(line), "", "", "", "", "");
                                arrayCampi_file.add(Campo);
                            } else {
                                if (line.contains("Start")) {
                                    livello = "Livello1Dentro";
                                    livello2 = line;
                                    struct_Campo Campo = new struct_Campo(path, "Livello1", Pulisci(line), "", "", "", "", "");
                                    arrayCampi_file.add(Campo);
                                    break;
                                }
                                String nome_variabile_tmp = SubString.Before(line, "=");
                                nome_variabile = Pulisci(nome_variabile_tmp);
                                String valore_tmp = SubString.After(line, "=");
                                valore = Pulisci(valore_tmp);
                                struct_Campo Campo = new struct_Campo(path, Pulisci(livello0), Pulisci(livello0), Pulisci(livello0), Pulisci(livello0), Pulisci(nome_variabile), Pulisci(valore), Pulisci(versione));
                                arrayCampi_file.add(Campo);
                            }

                            break;
                        case "Livello1Dentro":
                            if (line.contains("End")) {
                                livello = "Livello0Start";
                                struct_Campo Campo = new struct_Campo(path, "Livello1", Pulisci(line), "", "", "", "", "");
                                arrayCampi_file.add(Campo);
                            } else {
                                if (line.contains("Start")) {
                                    livello = "Livello2Dentro";
                                    livello2 = line;
                                    struct_Campo Campo = new struct_Campo(path, "Livello2", Pulisci(line), "", "", "", "", "");
                                    arrayCampi_file.add(Campo);
                                    break;
                                }
                                String nome_variabile_tmp = SubString.Before(line, "=");
                                nome_variabile = Pulisci(nome_variabile_tmp);
                                String valore_tmp = SubString.After(line, "=");
                                valore = Pulisci(valore_tmp);
                                struct_Campo Campo = new struct_Campo(path, Pulisci(livello0), Pulisci(livello1), Pulisci(livello1), Pulisci(livello1), Pulisci(nome_variabile), Pulisci(valore), Pulisci(versione));
                                arrayCampi_file.add(Campo);
                            }
                            break;
                        case "Livello2Dentro":
                            if (line.contains("End")) {
                                livello = "Livello1Dentro";
                                struct_Campo Campo = new struct_Campo(path, "Livello2", Pulisci(line), "", "", "", "", "");
                                arrayCampi_file.add(Campo);
                            } else {
                                if (line.contains("Start")) {
                                    livello = "Livello3Dentro";
                                    livello3 = line;
                                    struct_Campo Campo = new struct_Campo(path, "Livello3", Pulisci(line), "", "", "", "", "");
                                    arrayCampi_file.add(Campo);
                                    break;
                                }
                                String nome_variabile_tmp = SubString.Before(line, "=");
                                nome_variabile = Pulisci(nome_variabile_tmp);
                                String valore_tmp = SubString.After(line, "=");
                                valore = Pulisci(valore_tmp);
                                struct_Campo Campo = new struct_Campo(path, Pulisci(livello0), Pulisci(livello1), Pulisci(livello2), Pulisci(livello2), Pulisci(nome_variabile), Pulisci(valore), Pulisci(versione));
                                arrayCampi_file.add(Campo);
                            }
                            break;
                        case "Livello3Dentro":
                            if (line.contains("End")) {
                                livello = "Livello2Dentro";
                                struct_Campo Campo = new struct_Campo(path, "Livello3", Pulisci(line), "", "", "", "", "");
                                arrayCampi_file.add(Campo);
                            } else {
                                String nome_variabile_tmp = SubString.Before(line, "=");
                                nome_variabile = Pulisci(nome_variabile_tmp);
                                String valore_tmp = SubString.After(line, "=");
                                valore = Pulisci(valore_tmp);
                                struct_Campo Campo = new struct_Campo(path, Pulisci(livello0), Pulisci(livello1), Pulisci(livello2), Pulisci(livello3), Pulisci(nome_variabile), Pulisci(valore), Pulisci(versione));
                                arrayCampi_file.add(Campo);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return arrayCampi_file;
    }

    private static String Pulisci(String line) {
        String ret1 = line.replaceAll("\t|\r|\n|", "");
        String ret = ret1.replaceAll("\\s", "");        //tolgo spazi

        return ret;
    }

    private static void ScrivoTuttiCampiSuFile(ArrayList<struct_Campo> arrayCampi) throws FileNotFoundException {
        String path = arrayCampi.get(0).path;

        FileOutputStream fostream = new FileOutputStream(path);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fostream));

        try {
            /* x debuggare
            for (int i = 0; i<arrayCampi.size(); i++){
                struct_Campo item = arrayCampi.get(i);
                writer.write(item.livello0+","+item.livello1+","+item.livello2+","+item.livello3+","+item.nome_variabile+","+item.valore+","+item.versione+"\r\n");
            }
            writer.write("\r\n");
            writer.write("\r\n");
            // fine x debuggare
             */
            for (struct_Campo item : arrayCampi) {
                if (item.livello0.contains("Versione")) {
                    writer.write("\t" + item.livello1 + "\r\n");
                } else {
                    if (!item.livello0.contains("[") && !item.livello0.contains("]")) {
                        if (item.livello0.contains("0")) {
                            writer.write(item.livello1 + "\r\n");
                        }
                        if (item.livello0.contains("1")) {
                            writer.write("\t" + item.livello1 + "\r\n");
                        }
                        if (item.livello0.contains("2")) {
                            writer.write("\t\t" + item.livello1 + "\r\n");
                        }
                        if (item.livello0.contains("3")) {
                            writer.write("\t\t\t" + item.livello1 + "\r\n");
                        }
                    } else {
                        //Non servono i pulisci altrimenti mi toglie gli spazi e non funziona
                        if (item.livello0.equals(item.livello1) && item.livello1.equals(item.livello2) && item.livello2.equals(item.livello3)) {
                            writer.write("\t" + item.nome_variabile + " = " + item.valore + "\r\n");
                        }
                        if (!item.livello0.equals(item.livello1) && item.livello1.equals(item.livello2) && item.livello2.equals(item.livello3)) {
                            writer.write("\t\t" + item.nome_variabile + " = " + item.valore + "\r\n");
                        }
                        if (!item.livello0.equals(item.livello1) && !item.livello1.equals(item.livello2) && item.livello2.equals(item.livello3)) {
                            writer.write("\t\t\t" + item.nome_variabile + " = " + item.valore + "\r\n");
                        }
                        if (!item.livello0.equals(item.livello1) && !item.livello1.equals(item.livello2) && !item.livello2.equals(item.livello3)) {
                            writer.write("\t\t\t\t" + item.nome_variabile + " = " + item.valore + "\r\n");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();
                if (writer != null)
                    writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // TODO is better to move this in another file
    public static class struct_Campo {
        public String path;
        public String livello0;
        public String livello1;
        public String livello2;
        public String livello3;
        public String nome_variabile;
        public String valore;
        public String versione;

        public struct_Campo(String Path, String Livello0, String Livello1, String Livello2, String Livello3, String Nome_variabile, String Valore, String Versione) {
            path = Path;
            livello0 = Livello0;
            livello1 = Livello1;
            livello2 = Livello2;
            livello3 = Livello3;
            nome_variabile = Nome_variabile;
            valore = Valore;
            versione = Versione;
        }
    }
}
