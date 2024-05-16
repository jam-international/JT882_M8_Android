package com.jam_int.jt882_m8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Users_file {
    /********************************************************************************************************************************************
     *
     * !!!! Le stringe dei livelli non devono contenere [Start... ]. esempio per entrare in    [StartEncodersParam]  devo chiamare  EncodersParam !!!!!!!!!!!!
     *
     ********************************************************************************************************************************************/
    public static boolean Scrivi_campo(String path, String livello0, String livello1, String livello2, String livello3, String nome_variabile, String valore_da_scrivere) throws IOException {

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
                        item.livello3.contains(Pulisci(livello3))
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

    public static void AggiungiCampo(String path, String livello0, String livello1, String livello2, String livello3) throws IOException {

        boolean done = false;
        boolean write = true;
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
                        item.livello3.contains(Pulisci(livello3))
                    //item.nome_variabile.equals(Pulisci(nome_variabile))
                ) {
                    write = false;
                }
            }
            if (write) {
                struct_Campo item = new struct_Campo(path, "Livello1", "[Start" + livello1 + "]", "", "", "", "", "");
                Campi_scomposti.add(item);
                item = new struct_Campo(path, "[Start" + livello0 + "]", "[Start" + livello1 + "]", "[Start" + livello2 + "]", "[Start" + livello3 + "]", "Password", "", "");
                Campi_scomposti.add(item);
                item = new struct_Campo(path, "[Start" + livello0 + "]", "[Start" + livello1 + "]", "[Start" + livello2 + "]", "[Start" + livello3 + "]", "Level", "", "");
                Campi_scomposti.add(item);
                item = new struct_Campo(path, "Livello1", "[End" + livello1 + "]", "", "", "", "", "");
                Campi_scomposti.add(item);

                ScrivoTuttiCampiSuFile(Campi_scomposti);
            }
        }
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
                                livello = "Livello0Start";
                                struct_Campo Campo = new struct_Campo(path, "Livello0", Pulisci(line), "", "", "", "", "");
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
                                String nome_variabile_tmp = SubstringExtensions.Before(line, "=");
                                nome_variabile = Pulisci(nome_variabile_tmp);
                                String valore_tmp = SubstringExtensions.After(line, "=");
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
                                String nome_variabile_tmp = SubstringExtensions.Before(line, "=");
                                nome_variabile = Pulisci(nome_variabile_tmp);
                                String valore_tmp = SubstringExtensions.After(line, "=");
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
                                String nome_variabile_tmp = SubstringExtensions.Before(line, "=");
                                nome_variabile = Pulisci(nome_variabile_tmp);
                                String valore_tmp = SubstringExtensions.After(line, "=");
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

                                String nome_variabile_tmp = SubstringExtensions.Before(line, "=");
                                nome_variabile = Pulisci(nome_variabile_tmp);
                                String valore_tmp = SubstringExtensions.After(line, "=");
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
            //ultima riga
            //  String End_tmp = (Pulisci( arrayCampi_file.get(0).livello1.toString()));
            //   String End = End_tmp.replace("Start","End");
            //  struct_Campo Campo = new struct_Campo(path,"Livello0",End,"","","","","") ;
            //  arrayCampi_file.add(Campo);


        }


        return arrayCampi_file;
    }

    private static String Pulisci(String line) {
        String ret1 = line.replaceAll("\t|\r|\n|", "");
        String ret = ret1.replaceAll("\\s", "");        //tolgo spazi
        //ret = ret + "\r\n";

        return ret;
    }

    private static void ScrivoTuttiCampiSuFile(ArrayList<struct_Campo> arrayCampi) throws FileNotFoundException {
        String path = arrayCampi.get(0).path;

        // path = path+"1";
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

    /********************************************************************************************************************************************
     *
     * !!!! Le stringe dei livelli non devono contenere [Start... ]. esempio per entrare in    [StartEncodersParam]  devo chiamare  EncodersParam !!!!!!!!!!!!
     *
     *********************************************************************************************************************************************/
    public static ArrayList<struct_User> Leggi_file(String path) throws FileNotFoundException {

        ArrayList<struct_User> Users = new ArrayList<struct_User>();

        FileInputStream fstream = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        //  BufferedReader reader = new BufferedReader(new FileReader(path));
        // StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try {
            while ((line = br.readLine()) != null) {

                if (line.contains("[StartUsersList]")) {   //livello 0

                    while ((line = br.readLine()) != null) {
                        struct_User User = new struct_User("", "", "");
                        if (line.contains("[Start")) {   //livello 1.

                            User.Username = SubstringExtensions.After(line, "[Start");
                            User.Username = SubstringExtensions.Before(User.Username, "]");

                            while ((line = br.readLine()) != null) {
                                if (line.contains("[Start")) {   //livello 2.


                                    /*while ((line = br.readLine()) != null) {

                                        if (line.contains("[Start")) {   //livello 3.
                                            while ((line = br.readLine()) != null) {
                                                User.Username = SubstringExtensions.After(line, "[Start");
                                            }

                                        }
                                    }*/
                                } else {//non c'è livello3
                                    if (line.contains("Password")) {
                                        User.Password = SubstringExtensions.After(line, "=");
                                    } else if (line.contains("Livello")) {
                                        User.Level = SubstringExtensions.After(line, "=");
                                    }
                                    while ((line = br.readLine()) != null) {
                                        if (line.contains("Password")) {
                                            User.Password = SubstringExtensions.After(line, "=");
                                        } else if (line.contains("Level")) {
                                            User.Level = SubstringExtensions.After(line, "=");
                                        } else if (line.contains("[End" + User.Username + "]")) {
                                            break;
                                        }
                                    }
                                    Users.add(User);
                                    break;
                                }
                            }
                        } /*else { //non c'è livello2
                            if (line.contains("Password")) {
                                User.Password = SubstringExtensions.After(line, "=");
                            } else if (line.contains("Level")) {
                                User.Level = SubstringExtensions.After(line, "=");
                            }
                            while ((line = br.readLine()) != null) {
                                if (line.contains("Password")) {
                                    User.Password = SubstringExtensions.After(line, "=");
                                } else if (line.contains("Level")) {
                                    User.Level = SubstringExtensions.After(line, "=");
                                }else if (line.contains("[End"+User.Username+"]"))
                                {
                                    break;
                                }
                            }
                        }*/
                    }
                } /*else { //non c'è livello1
                    if (line.contains("Password")) {
                        User.Password = SubstringExtensions.After(line, "=");
                    } else if (line.contains("Level")) {
                        User.Level = SubstringExtensions.After(line, "=");
                    }
                    while ((line = br.readLine()) != null) {
                        if (line.contains("Password")) {
                            User.Password = SubstringExtensions.After(line, "=");
                        } else if (line.contains("Level")) {
                            User.Level = SubstringExtensions.After(line, "=");
                        }else if (line.contains("[End"+User.Username+"]"))
                        {
                            break;
                        }
                    }
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Users;
    }

    public static void Cancella_User(String path, String livello0, String livello1, String livello2, String livello3) throws FileNotFoundException {

        ArrayList<struct_User> Users = new ArrayList<struct_User>();

        FileInputStream fstream = new FileInputStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        //  BufferedReader reader = new BufferedReader(new FileReader(path));
        // StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try {
            while ((line = br.readLine()) != null) {

                if (line.contains("[StartUsersList]")) {   //livello 0

                    while ((line = br.readLine()) != null) {
                        struct_User User = new struct_User("", "", "");
                        if (line.contains("[Start")) {   //livello 1.

                            User.Username = SubstringExtensions.After(line, "[Start");
                            User.Username = SubstringExtensions.Before(User.Username, "]");

                            if (!User.Username.equals(livello1)) {
                                while ((line = br.readLine()) != null) {
                                    if (line.contains("[Start")) {   //livello 2.


                                    /*while ((line = br.readLine()) != null) {

                                        if (line.contains("[Start")) {   //livello 3.
                                            while ((line = br.readLine()) != null) {
                                                User.Username = SubstringExtensions.After(line, "[Start");
                                            }

                                        }
                                    }*/
                                    } else {//non c'è livello3
                                        if (line.contains("Password")) {
                                            User.Password = SubstringExtensions.After(line, "=");
                                        } else if (line.contains("Livello")) {
                                            User.Level = SubstringExtensions.After(line, "=");
                                        }
                                        while ((line = br.readLine()) != null) {
                                            if (line.contains("Password")) {
                                                User.Password = SubstringExtensions.After(line, "=");
                                            } else if (line.contains("Level")) {
                                                User.Level = SubstringExtensions.After(line, "=");
                                            } else if (line.contains("[End" + User.Username + "]")) {
                                                break;
                                            }
                                        }
                                        Users.add(User);
                                        break;
                                    }
                                }
                            }
                        }/*else { //non c'è livello2
                            if (line.contains("Password")) {
                                User.Password = SubstringExtensions.After(line, "=");
                            } else if (line.contains("Level")) {
                                User.Level = SubstringExtensions.After(line, "=");
                            }
                            while ((line = br.readLine()) != null) {
                                if (line.contains("Password")) {
                                    User.Password = SubstringExtensions.After(line, "=");
                                } else if (line.contains("Level")) {
                                    User.Level = SubstringExtensions.After(line, "=");
                                }else if (line.contains("[End"+User.Username+"]"))
                                {
                                    break;
                                }
                            }
                        }*/
                    }
                } /*else { //non c'è livello1
                    if (line.contains("Password")) {
                        User.Password = SubstringExtensions.After(line, "=");
                    } else if (line.contains("Level")) {
                        User.Level = SubstringExtensions.After(line, "=");
                    }
                    while ((line = br.readLine()) != null) {
                        if (line.contains("Password")) {
                            User.Password = SubstringExtensions.After(line, "=");
                        } else if (line.contains("Level")) {
                            User.Level = SubstringExtensions.After(line, "=");
                        }else if (line.contains("[End"+User.Username+"]"))
                        {
                            break;
                        }
                    }
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(, "error reading txt field files", Toast.LENGTH_SHORT).show();
        }

        ScrivoTuttiCampiSuFile(ConvertUsersToCampo(path, Users, livello0, livello1, livello2, livello3));
    }

    public static ArrayList<struct_Campo> ConvertUsersToCampo(String path, ArrayList<struct_User> Users, String livello0, String livello1, String livello2, String livello3) {
        ArrayList<struct_Campo> arrayCampi_file = new ArrayList<>();
        try {
            boolean finito = false;
            //String livello0 = "", livello1 = "", livello2 = "", livello3 = "", nome_variabile = "", valore = "", versione = "";
        /*if (arrayCampi != null || percorso != null) {
            String path;
            if (arrayCampi != null)
                path = arrayCampi.get(0).path;
            else
                path = percorso;*/


            String line = null;

            struct_Campo Campo = new struct_Campo(path, "Livello0", "[Start" + Pulisci(livello0) + "]", "", "", "", "", "");
            arrayCampi_file.add(Campo);

            for (struct_User user : Users) {
                struct_Campo item = new struct_Campo(path, "Livello1", "[Start" + user.Username + "]", "", "", "", "", "");
                arrayCampi_file.add(item);
                item = new struct_Campo(path, "[Start" + livello0 + "]", "[Start" + user.Username + "]", "[Start" + user.Username + "]", "[Start" + user.Username + "]", "Password", user.Password, "");
                arrayCampi_file.add(item);
                item = new struct_Campo(path, "[Start" + livello0 + "]", "[Start" + user.Username + "]", "[Start" + user.Username + "]", "[Start" + user.Username + "]", "Level", user.Level, "");
                arrayCampi_file.add(item);
                item = new struct_Campo(path, "Livello1", "[End" + user.Username + "]", "", "", "", "", "");
                arrayCampi_file.add(item);
            }
            //ultima riga
            //  String End_tmp = (Pulisci( arrayCampi_file.get(0).livello1.toString()));
            //   String End = End_tmp.replace("Start","End");
            //  struct_Campo Campo = new struct_Campo(path,"Livello0",End,"","","","","") ;
            //  arrayCampi_file.add(Campo);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayCampi_file;
    }

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

    static class SubstringExtensions {
        /// <summary>
        /// Get string value between [first] a and [last] b.
        /// </summary>
        public static String Between(String value, String a, String b) {
            int posA = value.indexOf(a);
            int posB = value.lastIndexOf(b);
            if (posA == -1) {
                return "";
            }
            if (posB == -1) {
                return "";
            }
            int adjustedPosA = posA + a.length();
            if (adjustedPosA >= posB) {
                return "";
            }
            return value.substring(adjustedPosA, posB - adjustedPosA);
        }

        /// <summary>
        /// Get string value after [first] a.
        /// </summary>
        public static String Before(String value, String a) {
            int posA = value.indexOf(a);
            if (posA == -1) {
                return "";
            }
            return value.substring(0, posA);
        }

        /// <summary>
        /// Get string value after [last] a.
        /// </summary>
        public static String After(String value, String a) {
            int posA = value.lastIndexOf(a);
            if (posA == -1) {
                return "";
            }
            int adjustedPosA = posA + a.length();
            if (adjustedPosA >= value.length()) {
                return "";
            }
            return value.substring(adjustedPosA);
        }
    }

    public static class struct_User {
        public String Username;
        public String Password;
        public String Level;

        public struct_User(String Username, String Password, String Level) {
            Username = Username;
            Password = Password;
            Level = Level;

        }


    }
}
