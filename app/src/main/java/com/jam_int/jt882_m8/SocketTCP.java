package com.jam_int.jt882_m8;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import communication.MultiCmdItem;
import communication.ShoppingList;


class SocketTCP {
    /**
     * Socket info for receive message
     */
    public static ServerSocket server;
    public static final int PORT = 60000;
    public boolean stopSL;

    Timer timer = new Timer();

    /**
     * Socket info that represent the connected machine
     */
    public static Socket clientSocket;

    /**
     * Thread for wait a new connection
     */
    public static Thread threadWaitNewConnection;
    /**
     * Thread for listen on incoming messages
     */
    public static Thread threadListenMessages;
    /**
     * Thread for send a message
     */
    public static Thread threadSendMessages;

    public static PrintWriter out;
    public static BufferedReader in;


    /**
     * Var for communicate with the PLC
     */
    ShoppingList sl;
    /**
     * Var that indicate the PLC communication Thread status
     */
    Boolean Thread_Running = false;
    static boolean    ThreadWaitNewConnection_can_run = true;
    Boolean StopThread = false;
    static Boolean PcConnesso = false;
    MultiCmdItem mci;
    /**
     * Thread for communicate with the PLC
     */
    Thread th;

    public static boolean Send = false;
    public static boolean Write;
    public static Double Value;
    public static String Type;
    public static int Var;
    Double ValueCheck = 9999d;
    public  static String cmd = "";

    /**
     * Constructor that start the threads
     */
    public SocketTCP() {

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandlerTCP)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandlerTCP());

        }
        // Start the Server
        try {
            InetAddress ip = getLocalAddress();
            if(ip != null){
            //    server = new ServerSocket(PORT);
                server = new ServerSocket(PORT,1000, ip);

                InitConnectionTimer();

                sl = SocketHandler.getSocket();
                sl.Clear();
                sl.Clear();

                StartMainThread();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class CustomExceptionHandlerTCP implements Thread.UncaughtExceptionHandler {
        private final Thread.UncaughtExceptionHandler defaultUEH_TCP;



        /**
         * If any of the parameters is null, the respective functionality
         * will not be used
         */
        public CustomExceptionHandlerTCP() {

            this.defaultUEH_TCP = Thread.getDefaultUncaughtExceptionHandler();
        }

        public void uncaughtException(Thread t, Throwable e) {

            ThreadWaitNewConnection_can_run = false;
            defaultUEH_TCP.uncaughtException(t, e);
        }


    }
    /**
     * restituisce IP impostato del device
     */
    public static InetAddress getLocalAddress(){
        try {
            Enumeration<NetworkInterface> b = NetworkInterface.getNetworkInterfaces();
            while( b.hasMoreElements()){
                for ( InterfaceAddress f : b.nextElement().getInterfaceAddresses())
                    if ( f.getAddress().isSiteLocalAddress())
                        return f.getAddress();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void SetPcConnesso() {
        SocketTCP.PcConnesso =true;
    }

    /**
     * Function that start a timer for check which action execute
     */
    private void InitConnectionTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (clientSocket == null) {
                    // This is for the first init
                    threadWaitNewConnection = new Thread(new ThreadWaitNewConnection());
                    threadWaitNewConnection.start();
                } else if (clientSocket != null && clientSocket.isClosed()) {
                    PcConnesso = false;
                    // This is after a disconnection
                    if (threadListenMessages != null) {
                        threadListenMessages = null;
                    }

                    if (threadSendMessages != null) {
                        threadSendMessages = null;
                    }

                    threadWaitNewConnection = new Thread(new ThreadWaitNewConnection());
                    threadWaitNewConnection.start();
                }
            }
        }, 0, 1000);
    }

    /**
     * Function that start the Thread that communicate with the PLC
     */
    private void StartMainThread() {
        final Double[] cntTcp = {0.0d};
        th = new Thread() {
            @Override
            public void run() {
                while (true) {
                    super.run();
                    Thread_Running = true;

                    try {
                        Thread.sleep((long) 10d);
                        if (StopThread || stopSL) {
                            Thread_Running = false;
                            return;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (sl.IsConnected()) {
/*
                        cntTcp[0] = cntTcp[0] + 1.0d;
                        mci = sl.Add("Io", 1, MultiCmdItem.dtVN, 1, MultiCmdItem.dpNONE);
                        mci.setValue((Double) cntTcp[0]);
                        sl.WriteItem(mci);
                        try {
                            Thread.sleep((long) 10d);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        sl.ReadItem(mci);
                        if(clientSocket != null && clientSocket.isConnected())
                        new Thread(new ThreadSendMessage("Value " + cntTcp[0].toString())).start();
*/

                        if (Send) {
                            ValueCheck = 9999d;
                            if (Type.equals("VN")) {
                                mci = sl.Add("Io", 1, MultiCmdItem.dtVN, Var, MultiCmdItem.dpNONE);
                            } else if (Type.equals("VQ")) {
                                mci = sl.Add("Io", 1, MultiCmdItem.dtVQ, Var, MultiCmdItem.dpNONE);
                            } else if (Type.equals("VB")) {
                                mci = sl.Add("Io", 1, MultiCmdItem.dtVB, Var, MultiCmdItem.dpNONE);
                                Log.d("TCP", "Send VB:"+Var);
                            }

                            if (Write) {
                                mci.setValue(Value);
                                sl.WriteItem(mci);
                            }

                            sl.ReadItem(mci);

                            final Double Output = (Double) mci.getValue();

                            ValueCheck = Output;

                            if (ValueCheck != 9999d) {
                                if (Write) {
                                    if (Value.compareTo(ValueCheck) == 0) {
                                        new Thread(new ThreadSendMessage("Value wrote")).start();
                                    } else {
                                        new Thread(new ThreadSendMessage("Error writing")).start();
                                    }
                                } else {
                                    if(cmd.equals("cmdGetProduction")){
                                        cmd ="";
                                        new Thread(new ThreadSendMessage("cmdGetProduction#" + Output.toString())).start();
                                    }else
                                        new Thread(new ThreadSendMessage("Value " + Output.toString())).start();
                                }
                            }
                            Send = false;
                        }
                    } else {
                        sl.Connect();
                    }
                }
            }
        };
        th.start();
    }

    public boolean getPcConnection() {
        return  PcConnesso;
    }
}

/**
 * Thread for connection, when connected start the ThreadListenMessage that wait for new messages
 */
class ThreadWaitNewConnection implements Runnable {

    public ThreadWaitNewConnection() {
    }

    @Override
    public void run() {
        if(SocketTCP.ThreadWaitNewConnection_can_run) {
            try {
                InetAddress ip = SocketTCP.getLocalAddress();
                Log.d("TCP", "Server Started at " + ip.toString() + " : " + SocketTCP.PORT);

                SocketTCP.server.setSoTimeout(200);
                SocketTCP.clientSocket = SocketTCP.server.accept();

                SocketTCP.out = new PrintWriter(SocketTCP.clientSocket.getOutputStream(), true);
                SocketTCP.in = new BufferedReader(new InputStreamReader(SocketTCP.clientSocket.getInputStream()));

                SocketTCP.threadListenMessages = new Thread(new ThreadListenMessage());
                SocketTCP.threadListenMessages.start();

            } catch (SocketTimeoutException e) {
                // Do nothing, just retry
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * ThreadListenMessage listen for incoming messages
 */
class ThreadListenMessage implements Runnable {

    public ThreadListenMessage() {
    }

    @Override
    public void run() {
        while (!SocketTCP.clientSocket.isClosed()) {
            SocketTCP.SetPcConnesso();

            try {
                final String message = SocketTCP.in.readLine();
                if (message != null) {
                    Log.d("TCP", message);

                    ExecuteCmd(message);
                } else {
                    Log.d("TCP", "Closed");

                    SocketTCP.clientSocket.close();

                    SocketTCP.threadListenMessages.interrupt();
                }
            } catch (IOException e) {
                try {
                    SocketTCP.clientSocket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                e.printStackTrace();
            }
        }
    }



    /**
     * Function for execute a command (Read/Write vars)
     *
     * @param cmd
     */
    private void ExecuteCmd(String cmd) {
        if (cmd.startsWith("RVB")) {


            String var = cmd.replace("RVB", "");
            SocketTCP.Write = false;
            SocketTCP.Type = "VB";
            try {
                SocketTCP.Var = Integer.parseInt(var);
                SocketTCP.Send = true;
            } catch (Exception e) {
                CommandFormatException();
            }
        } else if (cmd.startsWith("RVQ")) {
            String var = cmd.replace("RVQ", "");
            SocketTCP.Write = false;
            SocketTCP.Type = "VQ";
            try {
                SocketTCP.Var = Integer.parseInt(var);
                SocketTCP.Send = true;
            } catch (Exception e) {
                CommandFormatException();
            }
        } else if (cmd.startsWith("RVN")) {
            String var = cmd.replace("RVN", "");
            SocketTCP.Write = false;
            SocketTCP.Type = "VN";
            try {
                SocketTCP.Var = Integer.parseInt(var);
                SocketTCP.Send = true;
            } catch (Exception e) {
                CommandFormatException();
            }
        } else if (cmd.startsWith("WVB")) {
            String cmd1 = cmd.replace("WVB", "");
            if (cmd1.contains("_")) {
                String var = SubString.Before(cmd1, "_");
                String value = SubString.After(cmd1, "_");
                SocketTCP.Write = true;
                SocketTCP.Value = Double.parseDouble(value);
                SocketTCP.Type = "VB";
                SocketTCP.Var = Integer.parseInt(var);

                SocketTCP.Send = true;
            } else {
                CommandFormatException();
            }
        } else if (cmd.startsWith("WVQ")) {
            String cmd1 = cmd.replace("WVQ", "");
            if (cmd1.contains("_")) {
                String var = SubString.Before(cmd1, "_");
                String value = SubString.After(cmd1, "_");
                SocketTCP.Write = true;
                SocketTCP.Value = Double.parseDouble(value);
                SocketTCP.Type = "VQ";
                SocketTCP.Var = Integer.parseInt(var);

                SocketTCP.Send = true;
            } else {
                CommandFormatException();
            }
        } else if (cmd.startsWith("WVN")) {
            String cmd1 = cmd.replace("WVN", "");
            if (cmd1.contains("_")) {
                String var = SubString.Before(cmd1, "_");
                String value = SubString.After(cmd1, "_");
                SocketTCP.Write = true;
                SocketTCP.Value = Double.parseDouble(value);
                SocketTCP.Type = "VN";
                SocketTCP.Var = Integer.parseInt(var);

                SocketTCP.Send = true;
            } else {
                CommandFormatException();
            }
        }else if (cmd.startsWith("getFW")) {
            String fw = Emergency_page.getFirmware();
            new Thread(new ThreadSendMessage(fw)).start();

        }else if (cmd.startsWith("getPLCver")) {
            String getPLCver = Emergency_page.getPLCver();
            new Thread(new ThreadSendMessage(getPLCver)).start();

        }else if (cmd.startsWith("Machine_model")) {
            if(Values.Machine_model != null) {
                String Machine_model = Values.Machine_model;
                new Thread(new ThreadSendMessage(Machine_model)).start();
            }

        }
        else if (cmd.startsWith("File_XML_path_R")) {
            if(Values.File_XML_path_R != null) {
                String File_XML_path_R = Values.File_XML_path_R;
                new Thread(new ThreadSendMessage(File_XML_path_R)).start();
            }
        }
        else if (cmd.startsWith("File_XML_path_L")) {
            if(Values.File_XML_path_L !=null){
                String File_XML_path_L = Values.File_XML_path_L;
                new Thread(new ThreadSendMessage(File_XML_path_L)).start();
            }


        }else if(cmd.startsWith("cmdCommessa")){
            /// SendCmd("cmdCommessa#nome_commessa#numero_dima#prog_dx#prog_sx#qta_da_cucire");
            String st = cmd;
            String[] dati_commessa = st.split("#");
            if(dati_commessa.length > 0){
                File dir = new File("/storage/emulated/0/JamData/Commesse");
                if (!dir.exists()) dir.mkdirs();
                File file_commessa = new File("/storage/emulated/0/JamData/Commesse/"+dati_commessa[1]+".txt");
                if(!file_commessa.exists()){
                    try {
                      //  file_commessa.createNewFile();
                        FileWriter writer = new FileWriter(file_commessa);
                        for(int i =1; i<dati_commessa.length; i++) {
                            writer.append(dati_commessa[i]+"\n");
                        }
                        writer.flush();
                        writer.close();
                        new Thread(new ThreadSendMessage("#Msg: Commessa creata")).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else
                    new Thread(new ThreadSendMessage("#Er:1 La commessa gia' esiste")).start();

            }

         }else if(cmd.startsWith("cmdGetCommesse")){

            File folder = new File("/storage/emulated/0/JamData/Commesse");
            File[] listOfFiles = folder.listFiles();
            String pacchetto_a_inviare="cmdGetCommesse#";
            if(listOfFiles.length > 0){
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        try {
                            String pathFile =  "/storage/emulated/0/JamData/Commesse/"+ file.getName();
                            BufferedReader  reader = new BufferedReader(new FileReader(pathFile));
                            String line = reader.readLine();
                            pacchetto_a_inviare = pacchetto_a_inviare + line+"#";
                            while (line != null) {
                                //System.out.println(line);
                                // read next line
                                line = reader.readLine();
                                pacchetto_a_inviare = pacchetto_a_inviare + line+"#";
                            }

                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(pacchetto_a_inviare.length() > 0)
                {
                    new Thread(new ThreadSendMessage(pacchetto_a_inviare)).start();

                }


            }
            else {  //nessuna commessa
                new Thread(new ThreadSendMessage("nessuna")).start();



            }


        }else if(cmd.startsWith("cmdCancellaCommesse")){

            String st = cmd;
            String[] dati_commessa = st.split("#");
            if(dati_commessa.length > 1)
            {
                File file = new File( "/storage/emulated/0/JamData/Commesse/"+dati_commessa[1]+".txt");
                if (file.exists()){
                    file.delete();
                    new Thread(new ThreadSendMessage("#Msg: Commessa cancellata")).start();
                }

            }

        }else if (cmd.startsWith("cmdGetProduction")) {

            SocketTCP.Write = false;
            SocketTCP.Type = "VQ";
            SocketTCP.Var = 3591;
            SocketTCP.Send = true;
            SocketTCP.cmd = "cmdGetProduction";

        }
        else if (cmd.startsWith("cmdResetProduction")) {

            SocketTCP.Write = true;
            SocketTCP.Value = (Double)0.0d;
            SocketTCP.Type = "VQ";
            SocketTCP.Var = 3591;
            SocketTCP.Send = true;

        }




        else {
            UnknownCommand();
        }
    }

    /**
     * Function for send a message to the remote machine that the command is unknown
     */
    private void UnknownCommand() {
        new Thread(new ThreadSendMessage("Unknown command")).start();
    }

    /**
     * Function for send a message to the remote machine that the command is not well formatted
     */
    private void CommandFormatException() {
        new Thread(new ThreadSendMessage("Unknown format")).start();
    }
}

/**
 * ThreadSendMessage is used for send a message
 */
class ThreadSendMessage implements Runnable {
    private String message;

    public ThreadSendMessage(String message) {
        this.message = message;
    }

    @Override
    public void run() {
        SocketTCP.out.write(message);
        SocketTCP.out.flush();

      //  Log.d("TCP", message);
    }
}
