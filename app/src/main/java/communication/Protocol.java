package communication;

import android.content.Context;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;

public class Protocol {

    // ** Martino 19/10/2022 **
    // TODO Dopo 2 min le socket vengono chiuse automaticamente se non viene eseguita nessuna azione (CAPRE!!).
    //  Per questo la comunicazione salta e l'app va in errore.
    //  <p>
    //  Per risolvere basta mettere che ogni 30sec-1min viene mandato un messaggio (un ACK message)
    //  Per evitare di mettere un thread per la comunicazione in ogni pagina (in alcune pagine non ho bisogno di comunicare con il PLC) posso
    //  creare qui un piccolo thread che mantiene la connessione attiva

    // TODO i TODO vengono usati per indicare cose da fare (infatti c'è una parte apposita di Android Studio per vederli tutti)
    //  quindi vanno tolti quelli inutili generati automaticamente (CAPRE PT.2!!)

    // TODO i Exception.printStackTrace() servono a vedere gli errori che normalmente non ci dovrebbero essere, in caso ci siano devono essere gestiti.
    //  Metteteli (CAPRE PT.3)

    // TODO Mettetelo qualche commento usando le funzioni automatiche (/** prima di una variabile o funzione) o manualmente. In modo da capirci qualcosa e nel caso si debba richiamare
    //  il codice da un altra class si vedrà visualizzato il commento e gli input (CAPRE PT.4)

    public static final int rcOK = 0;
    public static final int rcTimeOut = -1;
    public static final int rcBadCheckSum = -2;
    public static final int rcProtocolError = -3;
    public static final int rcPortNotOpen = -4;
    public static final int rcMultiCMDDataError = -5;
    public static final int rcUDPPacketError = -6;
    public static final int rcWrongDataLen = -7;
    public static final int rcBusy = -8;
    public static final int rcTCPPacketError = -9;
    public static final int rcKappaError = -1000;
    public static final int rcUnknownCommand = -1001;
    public static final int rcException = -1002;

    private String Key = null;
    private LinkedHashMap<String, VFKItem> VFK;

    public List<MultiCmdItem> ToWrite = new ArrayList<MultiCmdItem>();

    private String IP;
    private int Port;
    private long PollTime = 10;

    private Integer ReturnCode = rcOK;
    private Integer ReturnPar = 0;
    private String ExMessage = "";
    private Double ReConnectDelay = 0d;


    private Double ReadTimeOut;
    private Double ConnectTimeOut;
    private Socket socket = null;
    private RS232Port rs232 = null;
    private Integer BaudRate = 38400;
    private Boolean Numbering = false;
    private final float CNProtocolVersion = 0;
    private Short PackNbr = 0;
    private CommHub Owner;

    private DataInputStream inbuf;
    private DataOutputStream outbuf;
    private final boolean FlgUDP = false;
    long MainTimeLatch;

    public Short getPackNbr() {
        return PackNbr;
    }

    public synchronized Boolean getNumbering() {
        return Numbering;
    }

    public synchronized void setNumbering(Boolean numbering) {
        Numbering = numbering;
    }

    public synchronized Integer getBaudRate() {
        return BaudRate;
    }

    public synchronized void setBaudRate(Integer baudRate) {
        BaudRate = baudRate;
    }

    public synchronized long getMainTimeLatch() {
        return MainTimeLatch;
    }

    public synchronized void setMainTimeLatch(long mainTimeLatch) {
        MainTimeLatch = mainTimeLatch;
    }

    public synchronized String getIP() {
        return IP;
    }

    public synchronized void setIP(String iP) {
        IP = iP;
    }

    public synchronized Double getReConnectDelay() {
        return ReConnectDelay;
    }

    public synchronized void setReConnectDelay(Double reConnectDelay) {
        ReConnectDelay = reConnectDelay;
    }

    public synchronized LinkedHashMap<String, VFKItem> getVFK() {
        return VFK;
    }

    public synchronized void setVFK(LinkedHashMap<String, VFKItem> vFK) {
        VFK = vFK;
    }

    public synchronized String getKey() {
        return Key;
    }

    public synchronized void setKey(String key) {
        Key = key;
    }

    public synchronized int getPort() {
        return Port;
    }

    public synchronized void setPort(int port) {
        Port = port;
    }

    public synchronized String getExMessage() {
        return ExMessage;
    }

    public synchronized void setExMessage(String exMessage) {
        ExMessage = exMessage;
    }

    public synchronized Integer getReturnCode() {
        return ReturnCode;
    }

    public synchronized void setReturnCode(Integer returnCode) {
        ReturnCode = returnCode;
    }

    public synchronized Integer getReturnPar() {
        return ReturnPar;
    }

    public synchronized void setReturnPar(Integer returnPar) {
        ReturnPar = returnPar;
    }


    public synchronized Double getReadTimeOut() {
        return ReadTimeOut;
    }

    public synchronized void setReadTimeOut(Double readTimeOut) {
        ReadTimeOut = readTimeOut;
    }


    public interface OnProgressListener {
        void onProgressUpdate(int Completion);
    }


    public Protocol(String the_ip, int port, Double readTimeOut, Double connectTimeOut) {
        ReadTimeOut = readTimeOut;
        IP = the_ip;
        Port = port;
        ConnectTimeOut = connectTimeOut;
    }

    public Protocol(int rs232port, Double readTimeOut) {
        IP = null;
        ReadTimeOut = readTimeOut;
        Port = rs232port;
    }

    public synchronized long getPollTime() {
        return PollTime;
    }

    public synchronized void setPollTime(long pollTime) {
        PollTime = pollTime;
    }

    private void SendCommand(byte[] CmdBuf) {
        //Flush
        try {
            while (inbuf.available() > 0)
                inbuf.read();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            outbuf.write(CmdBuf);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            outbuf.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //    protected void SendAsyncCommand(byte[] CmdBuf)
    //    {
    //        try
    //        {
    //            asyncbytes = new byte[0];
    //
    //            CmdBuf = AddLenAndChecksum(CmdBuf);
    //            outbufasync.write(CmdBuf);
    //        } catch (IOException e)
    //        {
    //            // TODO Auto-generated catch block
    //            e.printStackTrace();
    //        }
    //
    //        try
    //        {
    //            outbufasync.flush();
    //        } catch (IOException e)
    //        {
    //            // TODO Auto-generated catch block
    //            e.printStackTrace();
    //        }
    //    }

    private byte[] ReceiveAnswer(Double alternateTO) {
        int tout = (int) (ReadTimeOut * 1000);

        if (alternateTO != null)
            tout = (int) (alternateTO * 1000);

        int tot = 0;
        int ChunkLen = 10;


        byte[] lbuf = new byte[2];


        int InLen = 0;
        try {
            /* Antonio */

            if (IP == null) {
                rs232.setTimeOut(((Integer) tout).doubleValue());
                lbuf = rs232.ReadBytes(2);
                if (lbuf.length < 2) {
                    Close();
                    setReturnCode(rcTimeOut);
                    return new byte[]{};
                }

                InLen = ByteBuffer.wrap(lbuf, 0, 2).getShort();
            }

            /* fine Antonio */

            /* Daniele */
/*
            if (IP == null)
            {
                rs232.setTimeOut(((Integer)tout).doubleValue());
                lbuf[0] = 0;                                        //inizio modifica Daniele
                byte[] lbuf_dan = rs232.ReadBytes(1);

                if (lbuf.length < 1)
                {
                    Close();
                    return new byte[]{};
                }
                lbuf[1] = lbuf_dan[0];                              //fine modifica Daniele
                InLen = ByteBuffer.wrap(lbuf, 0, 2).getShort();
            }

            /* Fine Daniele */

            else {
                socket.setSoTimeout(tout);
                tot = inbuf.read(lbuf, 0, 2);
                InLen = ByteBuffer.wrap(lbuf, 0, 2).getShort();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Close();
            return new byte[]{};
        }


        byte[] buf = new byte[InLen + 3];

        InLen = InLen + 1;

        //Punta al terzo byte del buffer ritornato, lasciando i
        //primi due liberi per la lunghezza
        int ptr = 2;

        if (IP == null) {
            rs232.setTimeOut(((Integer) tout).doubleValue());
            byte[] b = rs232.ReadBytes(InLen);
            if (b.length < InLen) {
                Close();
                setReturnCode(rcTimeOut);
                return new byte[]{};
            }
            System.arraycopy(b, 0, buf, ptr, InLen);
        } else {
            byte[] chunk = new byte[ChunkLen];

            while (InLen > 0) {
                try {
                    socket.setSoTimeout(tout);
                    if (InLen > ChunkLen) {
                        tot = inbuf.read(chunk, 0, ChunkLen);
                        InLen -= ChunkLen;
                    } else {
                        tot = inbuf.read(chunk, 0, InLen);
                        InLen = 0;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Close();
                    return new byte[]{};
                }

                //Aggunge il chunk letto al buffer ritornato
                System.arraycopy(chunk, 0, buf, ptr, tot);
                ptr += tot;
            }
        }

        Integer rc = rcOK;
        Integer rp = 0;
        setExMessage("");

        //Copia la lunghezza nei primi 2 byte del buffer ritornato
        System.arraycopy(lbuf, 0, buf, 0, 2);


        //Toglie e controlla i PackageNumbering
        if (Numbering) {
            final ByteBuffer sb = ByteBuffer.allocate(2);
            sb.put(buf[3]);
            sb.put(buf[4]);
            sb.position(0);
            short pknum = sb.getShort();

            if (pknum == PackNbr) {
                System.arraycopy(buf, 5, buf, 2, buf.length - 5);
            } else {
                Close();
                rc = rcTCPPacketError;
                rp = 0;

                setReturnCode(rc);
                setReturnPar(rp);

                return buf;
            }
        }


        switch (buf[2]) {
            case 'o': {
                rc = rcBusy;
                break;
            }
            case 'E':
                rc = rcProtocolError;
                rp = 0;
                if (buf.length >= 2) {
                    switch (buf[3]) {
                        case 'X':
                            rp = buf[4] * 0x100 + buf[5];
                            break;
                        default:

                            rp = (int) buf[1];
                            break;
                    }
                }
                break;

            case 'K':
                if (buf.length == 1)
                    rc = rcKappaError;

            case 'S':
                if (buf.length >= 4)
                    if (buf[3] == 0)
                        rc = rcUnknownCommand;
        }

        setReturnCode(rc);
        setReturnPar(rp);

        return buf;
    }


    //    protected byte[] ReceiveAsyncAnswer()
    //    {
    //        int tot = 0;
    //        int InLen = 0;
    //        try
    //        {
    //            asyncsock.setSoTimeout(10);
    //            tot = inbufasync.available();
    //            byte[] tmp = new byte[tot];
    //            inbufasync.read(tmp, 0, tot);
    //            asyncbytes = ByteArrayCat(asyncbytes, tmp);
    //        } catch (IOException e)
    //        {
    //            return new byte[]{};
    //        }
    //
    //        if (asyncbytes.length >= 2)
    //            InLen = ByteBuffer.wrap(asyncbytes, 0, 2).getShort();
    //        else
    //            return new byte []{};
    //        
    //        if ((asyncbytes.length - 3) < InLen)
    //            return new byte[]{};
    //        else
    //            return asyncbytes;
    //        
    //    }


    byte[] Play(byte[] CmdBuf) {
        return Play(CmdBuf, null);
    }

    synchronized byte[] Play(byte[] CmdBuf, Double alternateTO) {

        byte[] answ = {};
        try {
//            if (!IsConnected())
//                Connect();

            if (PackNbr == Short.MAX_VALUE)
                PackNbr = 0;
            else
                PackNbr++;

            answ = new byte[]{};

            CmdBuf = AddLenAndChecksum(CmdBuf);
            SendCommand(CmdBuf);
            answ = ReceiveAnswer(alternateTO);


            long sleepfor = PollTime - (System.currentTimeMillis() - MainTimeLatch);
            if (sleepfor < 1) {
                sleepfor = 1;
            }

            try {
                Thread.sleep(sleepfor);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                setReturnCode(rcException);
                e.printStackTrace();
            }

            MainTimeLatch = System.currentTimeMillis();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            setReturnCode(rcException);
            e.printStackTrace();
        }

        return answ;
    }

    private byte[] AddLenAndChecksum(byte[] buf) {
        byte[] bufc;
        if (Numbering) {
            bufc = new byte[buf.length + 4];
            //Copia tutti i byte a partire dal primo dopo i 2 della lunghezza
            //li copia nel nuovo array a partire dal primo dopo 
            //lunghezza (2 byte) e package numbering (3 byte)
            System.arraycopy(buf, 2, bufc, 5, buf.length - 2);

            ByteBuffer b = ByteBuffer.allocate(2);
            b.putShort(PackNbr);
            byte[] result = b.array();

            bufc[2] = (byte) 0xF4;
            bufc[3] = result[0];
            bufc[4] = result[1];
        } else {
            bufc = new byte[buf.length + 1];
            System.arraycopy(buf, 0, bufc, 0, buf.length);
        }


        int chk;
        chk = 0;
        for (byte b : bufc) {
            chk += b;
        }

        bufc[bufc.length - 1] = (byte) chk;

        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(bufc.length - 3);
        byte[] result = b.array();


        bufc[0] = result[2];
        bufc[1] = result[3];


        return bufc;
    }

    public boolean IsConnected() {
        boolean connected = true;

        if (IP == null) {
            if (rs232 == null)
                connected = false;
        } else {
            if (socket == null)
                connected = false;
            else {
                if (!socket.isConnected()) {
                    connected = false;
                }
            }
        }
        return connected;
    }

    public boolean Connect() {
        try {
            if (IP == null) {
                String file;
                switch (Port) {
                    case 1:
                        file = "/dev/ttymxc4";
                        if (android.os.Build.MODEL.equals("SBCX-MX6DQ"))
                            file = "/dev/ttymxc4";
                        if (android.os.Build.MODEL.equals("SIPRO-MX6Q"))
                            file = "/dev/ttymxc2";
                        break;
                    default:
                        file = "/dev/ttymxc4";
                }
                rs232 = new RS232Port(new File(file), getBaudRate(), ReadTimeOut);

                inbuf = new DataInputStream(new DataInputStream(rs232.getInputStream()));
                outbuf = new DataOutputStream(new DataOutputStream(rs232.getOutputStream()));

                Numbering = false;
            } else {
                InetAddress serveradd = InetAddress.getByName(IP);
                socket = new Socket();
                socket.connect(new InetSocketAddress(serveradd, Port), ConnectTimeOut.intValue());

                inbuf = new DataInputStream(new DataInputStream(socket.getInputStream()));
                outbuf = new DataOutputStream(new DataOutputStream(socket.getOutputStream()));
            }
            /*  Daniele : fatto così non funziona, fa crescare l'applicazione
            // TODO ** Martino ** Start a small communication thread that send messages for keep the connection alive
            new Thread() {
                @Override
                public void run() {
                    super.run();

                    while (socket.isConnected()) {
                        try {
                            outbuf.write(new byte[]{0x00F});
                            Log.d("SOCKET_INFO", "The connection is still alive");
                            Thread.sleep(30 * 1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
*/
            return true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void Close() {
        try {
            if (IP == null) {
                rs232.close();
                rs232 = null;
            } else {
                //socket.shutdownInput();
                //socket.shutdownOutput();

                inbuf.close();
                outbuf.close();
                if (socket != null)
                    socket.close();
                socket = null;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void WriteVB(int Index, boolean Value) {

        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(Index);
        byte[] result = b.array();


        byte v;
        if (Value) {
            v = 1;
        } else {
            v = 0;
        }

        byte[] buf = new byte[]{0, 0, 0x34, (byte) 27, 1, result[2], result[3], v};


        Play(buf);
    }

    public void WriteVN(int Index, int Value) {


        byte[] Idx = ByteBuffer.allocate(4).putInt(Index).array();
        byte[] Val = ByteBuffer.allocate(4).putInt(Value).array();


        byte[] buf = new byte[]{0, 0, 0x34, (byte) 27, 2, Idx[2], Idx[3], Val[2], Val[3]};


        Play(buf);
    }

    public int ReadVN(int Index) {

        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(Index);
        byte[] result = b.array();


        byte[] buf = new byte[]{0, 0, 0x34, (byte) 155, 2, result[2], result[3]};

        byte[] a;

        a = Play(buf);

        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.put(a[3]);
        bb.put(a[4]);

        int i;
        i = bb.getShort(0);

        return i;
    }

    public int ReadAX(int Index, int Par) {

        byte[] buf = new byte[]{0, 0, 0x34, (byte) 155, 9, (byte) Par, (byte) Index};

        byte[] a;

        a = Play(buf);

        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.put(a[3]);
        bb.put(a[4]);
        bb.put(a[5]);
        bb.put(a[6]);

        int i;
        i = bb.getInt(0);

        return i;
    }

    public MSysFileInfo FileDir() {
        return FileDir(null, (byte) 0);
    }

    public MSysFileInfo FileDir(String PN, byte Attribute) {
        //Attribute 0x20 per file
        //Attribute 0x10 per cartelle


        byte[] b = new byte[]{};

        if ((PN == null) || (PN.equals("")))
            b = new byte[]{0, 0, 'X', 'n'};
        else {
            b = new byte[]{0, 0, 'X', 'f', 0, 0, 0, Attribute};
            b = ByteArrayCat(b, PN.getBytes());
            b = ByteArrayCat(b, new byte[]{0});
        }

        b = Play(b);

        short slen = 0;
        if (b.length > 6)
            slen = ByteBuffer.wrap(b, 4, 2).getShort();


        if (slen == 0)
            return null;
        else {
            MSysFileInfo fi = new MSysFileInfo();

            short fnlen = ByteBuffer.wrap(b, 22, 2).getShort();

            fi.FAttributes = b[9];

            Short d = ByteBuffer.wrap(b, 14, 2).getShort();
            Short t = ByteBuffer.wrap(b, 16, 2).getShort();

            fi.FDate = new GregorianCalendar(
                    1980 + ((d & 0xfe00) >> 9),
                    (d & 0x01e0) >> 5,
                    d & 0x001f,
                    (t & 0x0f800) >> 11,
                    (t & 0x07e0) >> 5,
                    (t & 0x001f) << 1
            );

            fi.FSize = ByteBuffer.wrap(b, 18, 4).getInt();
            String s = new String(b, 26, fnlen);
            fi.FName = s;

            return fi;
        }
    }

    public void ReadItem(MultiCmdItem mci) {
        if (this.IsConnected()) {
            byte[] b = new byte[]{0, 0, 0x34, (byte) 155};
            b = ByteArrayCat(b, mci.GetRequestTern());

            b = Play(b);

            mci.ParseValue(b, 3);
        } else
            mci.Reset();
    }

    public void WriteItem(MultiCmdItem mci) {
        byte[] b = new byte[]{0, 0, 0x34, (byte) 27};
        b = ByteArrayCat(b, mci.GetWriteBlock());

        b = Play(b);
    }


    public void WriteItems(MultiCmdItem[] mcis) {

        byte[] b = new byte[]{0, 0, 0x34, (byte) 27};

        for (MultiCmdItem mci : mcis) {
            b = ByteArrayCat(b, mci.GetWriteBlock());

            if (b.length > 256) {
                //         Log.i("TAG", "b.length:" + b.length );
                b = Play(b);
                b = new byte[]{0, 0, 0x34, (byte) 27};
            }
        }

        if (b.length > 4) {
            b = Play(b);
        }
    }

    public void ReadItems(MultiCmdItem[] mcis) {
        byte[] b = new byte[]{0, 0, 0x34, (byte) 155};

        for (MultiCmdItem mci : mcis)
            b = ByteArrayCat(b, mci.GetRequestTern());

        b = Play(b);

        int idx = 3;
        for (MultiCmdItem mci : mcis)
            idx += mci.ParseValue(b, idx);
    }


    public static byte[] ByteArrayCat(byte[] buf, byte[] ToCat) {
        /*
		byte[] newb = new byte[buf.length + ToCat.length];

		System.arraycopy(buf, 0, newb, 0, buf.length);
		System.arraycopy(ToCat, 0, newb, buf.length, ToCat.length);

		return  newb;
         */

        return ByteArrayCat(buf, ToCat, 0, ToCat.length);
    }

    public static byte[] ByteArrayCat(byte[] buf, byte[] ToCat, int ToCatFirst, int TotToCat) {
        byte[] newb = new byte[buf.length + TotToCat];

        System.arraycopy(buf, 0, newb, 0, buf.length);
        System.arraycopy(ToCat, ToCatFirst, newb, buf.length, TotToCat);

        return newb;
    }

    public Byte FileOpen(String PN, String Mode) {
        //Mode = "r" - Read
        //Mode = "w" - Write

        byte[] b = new byte[]{};


        b = new byte[]{0, 0, 'X', 'o'};
        b = ByteArrayCat(b, PN.getBytes());
        b = ByteArrayCat(b, new byte[]{0});
        b = ByteArrayCat(b, Mode.getBytes());
        b = ByteArrayCat(b, new byte[]{0});

        b = Play(b, 20d);

        if (b.length > 4)
            return b[4];
        else
            return 0;
    }

    public void FileWrite(byte Handle, byte[] buf, Integer offset, Short totBytes) {
        byte[] b = new byte[]{};

        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(totBytes);

        b = new byte[]{0, 0, 'X', 'w', Handle};
        b = ByteArrayCat(b, buffer.array());
        b = ByteArrayCat(b, buf, offset, totBytes);

        b = Play(b, 20d);
    }

    public byte[] FileRead(byte Handle, Short totBytes) {
        short tot = 0;
        byte[] b = new byte[]{};

        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort(totBytes);

        b = new byte[]{0, 0, 'X', 'r', Handle};
        b = ByteArrayCat(b, buffer.array());

        b = Play(b, 20d);

        tot = ByteBuffer.wrap(b, 5, 2).getShort();

        byte[] rb = new byte[tot];

        System.arraycopy(b, 7, rb, 0, tot);


        return rb;
    }

    public void FileClose(byte Handle) {
        byte[] b = new byte[]{};

        b = new byte[]{0, 0, 'X', 'c', Handle};

        b = Play(b, 20d);
    }

    public boolean FileUpload(String CNPathAndName, String PCPathAndName, OnProgressListener pl) {
        boolean Done = true;
        int chunkLen = 512;
        byte[] buf = new byte[chunkLen];

        Long tot = 0l;
        Long cnt = 0l;

        byte FH = FileOpen(CNPathAndName, "w");
        if (ReturnCode == rcOK) {
            try {
                File f = new File(PCPathAndName);
                tot = f.length();

                InputStream is = new FileInputStream(PCPathAndName);

                short read = 0;

                read = (short) is.read(buf, 0, chunkLen);
                while (read != -1) {
                    cnt += read;
                    if (pl != null)
                        pl.onProgressUpdate((int) (cnt * 100 / tot));

                    FileWrite(FH, buf, 0, read);

                    if (ReturnCode != rcOK) {
                        FileClose(FH);
                        is.close();
                        return false;
                    }


                    read = (short) is.read(buf, 0, chunkLen);
                }

                is.close();
                FileClose(FH);
                if (ReturnCode != rcOK)
                    return false;


            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (FH != 0)
                    FileClose(FH);
            }

        } else
            Done = false;

        return Done;
    }

    public boolean FileDownload(String CNPathAndName, String PCPathAndName, OnProgressListener pl) {
        boolean Done = true;

        short chunkLen = 512;

        byte[] buf = new byte[chunkLen];

        Integer byCnt = 0;

        MSysFileInfo fi = FileDir(CNPathAndName, (byte) 0x20);


        byte FH = FileOpen(CNPathAndName, "r");


        if (ReturnCode == rcOK) {
            try {
                OutputStream os = new FileOutputStream(PCPathAndName);


                buf = FileRead(FH, chunkLen);


                while (buf.length != 0) {
                    new String(buf, 0, buf.length);

                    os.write(buf, 0, buf.length);

                    byCnt += buf.length;

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (pl != null)
                        pl.onProgressUpdate(byCnt * 100 / fi.FSize);

                    if (ReturnCode != rcOK) {
                        FileClose(FH);
                        os.close();
                        return false;
                    }


                    buf = FileRead(FH, chunkLen);
                }

                os.close();
                FileClose(FH);
                FH = 0;
                if (ReturnCode != rcOK)
                    return false;


            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (FH != 0)
                    FileClose(FH);
            }
        } else
            Done = false;

        return Done;
    }

    public boolean FileDelete(String PN) {
        //Mode = "r" - Read
        //Mode = "w" - Write

        byte[] b = new byte[]{};


        b = new byte[]{0, 0, 'X', 'u'};
        b = ByteArrayCat(b, PN.getBytes());
        b = ByteArrayCat(b, new byte[]{0});

        b = Play(b);

        return ReturnCode == rcOK;
    }

    public boolean Send(byte[] Buf) {
        boolean rc = true;
        try {
            outbuf.write(Buf);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            rc = false;
        }

        try {
            outbuf.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            rc = false;
        }

        return rc;
    }

    public byte[] Read(int tot) {
        int tout = (int) (ReadTimeOut * 1000);

        byte[] lbuf = new byte[tot];

        try {
            if (IP == null) {
                rs232.setTimeOut(((Integer) tout).doubleValue());
                lbuf = rs232.ReadBytes(tot);

                if (lbuf.length == tot)
                    return lbuf;
                else
                    return new byte[]{};
            } else {
                socket.setSoTimeout(tout);

                int rd = inbuf.read(lbuf, 0, tot);

                if (rd == tot)
                    return lbuf;
                else
                    return new byte[]{};
            }

        } catch (IOException e) {
            e.printStackTrace();
            Close();
            return new byte[]{};
        }
    }


    public void Flush() {
        try {
            byte[] buf = new byte[inbuf.available()];

            inbuf.read(buf, 0, buf.length);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public int Available() {
        try {
            if (IP == null) {
                if (rs232 != null)
                    return inbuf.available();
                else
                    return 0;
            } else {
                if (socket != null && socket.isConnected())
                    return inbuf.available();
                else
                    return 0;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    //	@SuppressWarnings("null")
    //	public void LoadVFK()
    //	{
    //		VFKItem v = null;
    //		
    //		try {
    //			XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
    //			XmlPullParser myParser = xmlFactoryObject.newPullParser();
    //
    //
    //			VFK = new LinkedHashMap<String, VFKItem>();
    //
    //			InputStream fIn = context.getResources().openRawResource(sipro.droidplayer.R.raw.vfk);
    //
    //			//FileInputStream fIn = new FileInputStream(myFile);
    //
    //			myParser.setInput(fIn, null);
    //
    //			String txt = null;
    //
    //			int event = myParser.getEventType();
    //			while (event != XmlPullParser.END_DOCUMENT) 
    //			{
    //				String name = myParser.getName();
    //				switch (event){
    //				case XmlPullParser.START_TAG:
    //					break;
    //
    //				case XmlPullParser.TEXT:
    //					txt = myParser.getText();
    //					break;
    //
    //				case XmlPullParser.END_TAG:
    //					if(name.equals("Index"))
    //					{
    //						v = new VFKItem();
    //						v.setIndex(Integer.parseInt(txt));
    //					}
    //					else if(name.equals("Description"))
    //					{
    //						v.setDescription(txt);
    //					}
    //					else if(name.equals("CanRead"))
    //					{
    //						v.setReadable(txt.equals("true"));
    //					}
    //					else if(name.equals("CanWrite"))
    //					{
    //						v.setWritable(txt.equals("true"));
    //					}
    //					else if(name.equals("DataType"))
    //					{
    //						txt = txt.split(",")[0];
    //						txt = txt.split("\\.")[1];
    //						
    //						v.setDataType(txt);
    //						VFK.put(v.getIndex().toString(), v);
    //					}
    //
    //
    //					break;
    //				}		 
    //
    //				event = myParser.next(); 					
    //			}
    //
    //			fIn.close();
    //
    //		} catch (FileNotFoundException e) {
    //			// TODO Auto-generated catch block
    //			e.printStackTrace();
    //		} catch (XmlPullParserException e) {
    //			// TODO Auto-generated catch block
    //			e.printStackTrace();
    //		} catch (IOException e) {
    //			// TODO Auto-generated catch block
    //			e.printStackTrace();
    //		}
    //	}

    public void M32ConsoleCmd(String cmd) {
        byte[] b = new byte[]{};
        b = new byte[]{0, 0, '3', 10};
        b = ByteArrayCat(b, cmd.getBytes());
        b = ByteArrayCat(b, new byte[]{0});

        b = Play(b);
    }

    public void FolderCreate(String PN) {

        byte[] b = new byte[]{};


        b = new byte[]{0, 0, 'X', 'm'};
        b = ByteArrayCat(b, PN.getBytes());
        b = ByteArrayCat(b, new byte[]{0});

        b = Play(b);
    }

    public Float ReadProtocolVersion() {
        //ReadVN(0);


        Float v = 0f;

        byte[] b = new byte[]{};
        b = new byte[]{0, 0, (byte) 0xEA, (byte) 1, (byte) 0xF5};

        b = Play(b);

        if (ReturnCode == rcOK) {
            if (b.length >= 4)
                v = (float) b[2] + ((float) b[3]) / 100;
        }

        return v;
    }

    public void CheckNumbering() {
        //Controllo per compatibilit� Numbering
        //Disabilita il numbering se versione protocollo < v3.00
        boolean n = getNumbering();
        setNumbering(false);
        float v = ReadProtocolVersion();
        setNumbering(n);
        if (v < 3f)
            setNumbering(false);
    }


    public boolean Delete_EEp(byte numero_prog) {
        try {

            byte[] CmBuf = new byte[]{0x00, 0x04, 0x34, 0x13, 0x00, numero_prog};
            CmBuf = AddChecksum(CmBuf);
            SendCommand(CmBuf);
            byte[] answ = {};
            answ = new byte[]{};
            answ = ReceiveAnswer(null);
            return answ[1] == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private byte[] AddChecksum(byte[] cmBuf) {
        byte[] bufc = new byte[cmBuf.length + 1];
        System.arraycopy(cmBuf, 0, bufc, 0, cmBuf.length);


        int chk;
        chk = 0;

        for (int i = 2; i < bufc.length - 1; i++) {
            chk += bufc[i];
        }


        bufc[bufc.length - 1] = (byte) chk;

        return bufc;
    }

    public byte[] ReadEep(byte num_prog) {
/*
      //  "06EA0034CF0001EE"
      //  4  |'4' |209 | ProgNum |Chks|
        byte[] CmBuf = new byte[]{0x00, 0x04,(byte)0x34,(byte) 0xCF,0x00,0x01};
        CmBuf = AddChecksum(CmBuf);
        SendCommand(CmBuf);
        byte[] answ = {};
        answ = new byte[]{};
        answ = ReadAll(256);





        while(true)
        {
            CmBuf = new byte[]{0x00, 0x02,(byte)0x34,(byte) 0x8F};
            CmBuf = AddChecksum(CmBuf);
            SendCommand(CmBuf);
            byte[] answ1 = {};
            answ1 = new byte[]{};
            answ1 = ReadAll(256);

        //    byte[] bufc = new byte[cmBuf.length + 1];
        //    System.arraycopy(cmBuf, 0, bufc, 0, cmBuf.length);
            byte[] joinedArray = Arrays.copyOf(answ, answ.length + answ1.length);
            System.arraycopy(answ1, 0, joinedArray, answ.length, answ1.length);


            answ = new byte[joinedArray.length];
            System.arraycopy(joinedArray,0,answ,0,joinedArray.length);

            if(answ1[0] != -113)
                break;

        }


   */

        return null;


    }

    public boolean Busy() {
        byte[] CmBuf = new byte[]{0, 1, 50, 50};
        SendCommand(CmBuf);
        byte[] answ = {};
        answ = new byte[]{};
        answ = ReceiveAnswer(null);// ReadAll(256);
        return answ[2] != 1;        //0 NOT READY   1 READY     2 BUSY

    }

    private byte[] ReadAll(int n_byte) {

        byte[] lbuf = new byte[n_byte];
        rs232.setTimeOut(((Integer) 500).doubleValue());
/*
        lbuf = rs232.ReadBytes(2);
        if (lbuf.length < 2)
        {
            Close();
            return new byte[]{};
        }
        */

        lbuf[0] = 0;                                        //inizio modifica Daniele
        byte[] lbuf_dan = rs232.ReadBytes(1);

        if (lbuf.length < 1) {
            Close();
            return new byte[]{};
        }
        lbuf[1] = lbuf_dan[0];                              //fine modifica Daniele

        int InLen = ByteBuffer.wrap(lbuf, 0, 2).getShort();

        byte[] buf = new byte[InLen + 3];

        InLen = InLen + 1;

        //Punta al terzo byte del buffer ritornato, lasciando i
        //primi due liberi per la lunghezza
        int ptr = 2;


        rs232.setTimeOut(((Integer) 500).doubleValue());
        byte[] b = rs232.ReadBytes(InLen);
        if (b.length < InLen) {
            Close();
            return new byte[]{};
        }
        System.arraycopy(b, 0, buf, ptr, InLen);


        return b;
    }

    public byte[] ListaEepSuCn() {

        byte[] CmBuf = new byte[]{0, 2, 0x34, 0x52, (byte) 0x86};
        SendCommand(CmBuf);
        byte[] answ = {};
        answ = new byte[]{};
        answ = ReadAll(256);
        return answ;

    }

    public boolean WriteEepToCn(ArrayList listatobyte, OnProgressListener pl) {
        boolean ret = false;
        int NumeriPacchetti128 = listatobyte.size() / 128;
        byte[] Pacchetto128 = new byte[128];
        byte[] CmBuf = new byte[132];
        int byte_inviati = 0;
        int tentativi = 0;

        //    Delete_EEp((byte)1);
        //    Delete_EEp((byte)2);
        while (Busy() != false) ;

        for (int i = 0; i < NumeriPacchetti128; i++) {

            for (int r = 0; r < 128; r++) {
                Pacchetto128[r] = (byte) listatobyte.get(r + (i * 128));
            }


            CmBuf[0] = (byte) 0x82;
            CmBuf[1] = (byte) 0x34;
            if (i == 0) {
                CmBuf[2] = (byte) 0x4F;
            } else {
                CmBuf[2] = (byte) 0x0F;
            }
            System.arraycopy(Pacchetto128, 0, CmBuf, 3, 128);

            byte Checksum = 0;
            for (int t = 1; t < CmBuf.length - 1; t++) {
                Checksum += CmBuf[t];
            }


            CmBuf[CmBuf.length - 1] = Checksum;


            while (Busy() != false) ;
            SendCommand(CmBuf);
            byte_inviati = byte_inviati + CmBuf.length;


            byte[] answ = {};
            answ = new byte[]{};
            answ = ReadAll(256);

            if (answ[0] == (byte) 0x4f && answ[1] == (byte) 0x4f) {
                tentativi = 0;
                if (pl != null)
                    pl.onProgressUpdate(byte_inviati * 100 / listatobyte.size());

            } else {   //errore trasmissione
                tentativi++;
                i = 0;    //riprovo
                if (tentativi > 3)
                    break;
            }


        }
        //spedisco gli ultimi byte dopo aver mandato n 128
        int ByteRimanenti = listatobyte.size() - (NumeriPacchetti128 * 128);
        byte[] PacchettoRimanente = new byte[ByteRimanenti];
        for (int r = 0; r < ByteRimanenti; r++) {
            int t = NumeriPacchetti128 * 128 + r;
            PacchettoRimanente[r] = (byte) listatobyte.get(t);
        }


        CmBuf = new byte[ByteRimanenti + 4];
        CmBuf[0] = (byte) ((byte) ByteRimanenti + 2);
        CmBuf[1] = (byte) 0x34;
        CmBuf[2] = (byte) 0x2F;


        System.arraycopy(PacchettoRimanente, 0, CmBuf, 3, PacchettoRimanente.length);
        byte Checksum = 0;

        Checksum = 0;
        for (int t = 1; t < CmBuf.length - 1; t++) {
            Checksum += CmBuf[t];
        }

        CmBuf[CmBuf.length - 1] = Checksum;

        SendCommand(CmBuf);
        byte[] answ = {};
        answ = new byte[]{};
        answ = ReadAll(256);

        if (answ[0] == (byte) 0x6f && answ[1] == (byte) 0x6f) {
            if (pl != null)
                pl.onProgressUpdate(byte_inviati * 100 / listatobyte.size());
            ret = true;
        } else {   //errore trasmissione

        }


        return ret;
    }
}

