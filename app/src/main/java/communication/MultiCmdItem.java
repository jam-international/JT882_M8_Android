package communication;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class MultiCmdItem {

    public static final int dtNONE = 0;
    public static final int dtVB = 1; 
    public static final int dtVN = 2;
    public static final int dtVQ = 3;
    public static final int dtDI = 4;
    public static final int dtDO = 5;
    public static final int dtAO = 7;
    public static final int dtAI = 6;
    public static final int dtEN = 8;
    public static final int dtAX = 9;
    public static final int dtVA = 10;
    public static final int dtOR = 11;
    public static final int dtFO = 12;
    public static final int dtAL = 13;
    public static final int dtOA = 14;
    public static final int dtGP = 15;
    public static final int dtDB = 16;
    public static final int dtFC = 22;
    public static final int dtVD = 25;
    public static final int dtVC = 27;

    public static final int dpNONE = 0;
    public static final int dpEN_HW = 0;
    public static final int dpEN_SW = 1;
    public static final int dpEN_CODE = 2;
    public static final int dpEN_POSITION = 3;
    public static final int dpEN_FREERUNCOUNTER = 4;
    public static final int dpEN_IRQCONTROL = 10;
    public static final int dpEN_IRQCOUNTER = 11;
    public static final int dpAO_DACFORMAT = 0;
    public static final int dpAO_VOLTFORMAT = 1;
    public static final int dpAO_PERCENTFORMAT = 3;
    public static final int dpAO_POSITIVEGAIN = 8;
    public static final int dpAO_NEGATIVEGAIN = 9;
    public static final int dpAO_OFFSET = 10;
    public static final int dpAO_CODE = 64;
    public static final int dpAI_DACFORMAT = 0;
    public static final int dpAI_VOLTFORMAT = 1;
    public static final int dpAI_PERCENTFORMAT = 3;
    public static final int dpAI_CODE = 64;
    public static final int dpAX_REAL_OWN = 0;
    public static final int dpAX_TH_ABS_OWN = 1;
    public static final int dpAX_REAL_ABS_STD = 2;
    public static final int dpAX_TH_ABS_STD = 3;
    public static final int dpAX_OBJ_OWN = 4;
    public static final int dpAX_OBJ_ABS_OWN = 5;
    public static final int dpAX_REAL_ABS_OWN = 6;
    public static final int dpAX_QUOTE_CARTESIANE = 7;
    public static final int dpAX_OBJ_MANUAL = 8;
    public static final int dpAX_M32_Quota_Reale_Assoluta = 16;
    public static final int dpAX_M32_Quota_Reale_Origini = 17;
    public static final int dpAX_M32_Quota_Teorica_Assoluta = 18;
    public static final int dpAX_M32_Quota_Teorica_Origini = 19;
    public static final int dpAX_M32_Quota_Obiettivo_Assoluta = 20;
    public static final int dpAX_M32_Quota_Obiettivo_Origini = 21;
    public static final int dpAX_M32_Distanza_Reale = 22;
    public static final int dpAX_M32_Distanza_Teorica = 23;
    public static final int dpAX_M32_Errore_Millimetri = 24;
    public static final int dpAX_M32_Errore_Impulsi = 25;
    public static final int dpAX_M32_Velocita_Teorica_Modulo = 26;
    public static final int dpAX_M32_Velocita_Teorica = 27;
    public static final int dpAX_M32_Quota_FreeRun = 28;
    public static final int dpAX_M32_SetPointOnDrive = 31;
    public static final int dpAX_M32_Fase = 32;
    public static final int dpAX_M32_Quota_Reale_Assoluta_Impulsi = 30;
    public static final int dpAX_M32_NumSetpoint = 33;
    public static final int dpAX_M32_AnOut = 35;
    public static final int dpAX_M32_Velocita_Reale_Millimetri_Min = 36;
    public static final int dpAX_M32_Velocita_Reale_Impulsi_Sec = 37;
    public static final int dpAX_M32_Quota_Cartesiana_Reale_Assoluta = 50;
    public static final int dpAX_M32_Quota_Cartesiana_Teorica_Assoluta = 52;
    public static final int dpAX_M32_Quota_Reale_Assoluta_Sync = 80;
    public static final int dpAX_M32_Quota_Reale_Origini_Sync = 81;
    public static final int dpAX_M32_Quota_Teorica_Assoluta_Sync = 82;
    public static final int dpAX_M32_Quota_Teorica_Origini_Sync = 83;
    public static final int dpAX_M32_TickCounter_Sync = 112;
    public static final int dpAL_REPORT = 0;
    public static final int dpAL_EMERGENCY = 1;
    public static final int dpAL_MAIN = 2;
    public static final int dpAL_PLC = 3;
    public static final int dpAL_M32 = 4;
    public static final int dpAL_M32_Description = 5;
    public static final int dpDB_MAIN1 = 0;
    public static final int dpDB_MAIN2 = 1;
    public static final int dpDB_MAIN3 = 2;
    public static final int dpDB_MAIN4 = 3;
    public static final int dpDB_MAIN5 = 4;
    public static final int dpDB_MAIN6 = 5;
    public static final int dpDB_MAIN7 = 6;
    public static final int dpDB_MAIN8 = 7;
    public static final int dpDB_FORK01 = 8;
    public static final int dpDB_FORK02 = 9;
    public static final int dpDB_FORK03 = 10;
    public static final int dpDB_FORK04 = 11;
    public static final int dpDB_FORK05 = 12;
    public static final int dpDB_FORK06 = 13;
    public static final int dpDB_FORK07 = 14;
    public static final int dpDB_FORK08 = 15;
    public static final int dpDB_FORK09 = 16;
    public static final int dpDB_FORK10 = 17;
    public static final int dpDB_FORK11 = 18;
    public static final int dpDB_FORK12 = 19;
    public static final int dpDB_FORK13 = 20;
    public static final int dpDB_FORK14 = 21;
    public static final int dpDB_FORK15 = 22;
    public static final int dpDB_FORK16 = 23;
    public static final int dpDB_FORK17 = 24;
    public static final int dpDB_FORK18 = 25;
    public static final int dpDB_FORK19 = 26;
    public static final int dpDB_FORK20 = 27;
    public static final int dpDB_FORK21 = 28;
    public static final int dpDB_FORK22 = 29;
    public static final int dpDB_FORK23 = 30;
    public static final int dpDB_FORK24 = 31;
    public static final int dpVC_MAIN1 = 0;
    public static final int dpVC_MAIN2 = 1;
    public static final int dpVC_MAIN3 = 2;
    public static final int dpVC_MAIN4 = 3;
    public static final int dpVC_MAIN5 = 4;
    public static final int dpVC_MAIN6 = 5;
    public static final int dpVC_MAIN7 = 6;
    public static final int dpVC_MAIN8 = 7;







    private String Key;
    private String Prefix;
    private int CN;
    private int Index;
    private int Type;
    private int Param;
    private int Multi;
    private Object Value; 
    private Protocol Owner;



    public List<String> Users = new ArrayList<String>(); 


    public synchronized Protocol getOwner() {
        return Owner;
    }

    public synchronized void setOwner(Protocol owner) {
        Owner = owner;
    }

    public MultiCmdItem (MultiCmdItem Src)
    {
        Init(Src.getCN(), Src.getType(), Src.getIndex(), Src.getParam(), Src.getOwner());
    }

    public MultiCmdItem (int cn, int type, int index, int param, Protocol owner)
    {
        Init(cn, type, index, param, owner);
    }

    private void Init(int cn, int type, int index, int param, Protocol owner)
    {
        CN = cn;
        Index = index;
        Type = type;
        Param = param;
        Multi = 0;
        Owner = owner;


        switch (Type)
        {
        case dtVB:
        case dtDI:
        case dtDO:
        {
            Value = 0d; // new Byte((byte)0);
            break;
        }

        case dtVN:
        case dtAI:
        case dtAO:
        {
            Value = 0d; //new Short((short) 0);
            break;
        }


        case dtEN:
        {
            switch(Param)
            {
            case dpEN_HW:
            case dpEN_CODE:
            case dpEN_IRQCONTROL:
                Value = 0d; //new Short((short) 0);
                break;

            case dpEN_SW:
            case dpEN_POSITION:
            case dpEN_FREERUNCOUNTER:
            case dpEN_IRQCOUNTER:
                Value = 0d; // Integer.valueOf((int) 0);
                break;

            default:
                break;
            }
        }


        case dtVQ:
        case dtAX:
        case dtOR:
        case dtFO:
        {
            Value = 0d; //Integer.valueOf((int)0);
            break;
        }

        case dtVA:
        {
            Value = "";
        }

        case dtGP:
        {
            if (Index >= 0 && Index <= 1023)
                Value = 0d;

            if (Index >= 3072 && Index <= 4095)
                Value = "";

            break;
        }

        case dtDB:
        {
            VFKItem vi = Owner.getVFK().get(Integer.toString(Index));

            if (vi.getDataType().equals("String"))
            {
                Value = "";
            }   
            else if(vi.getDataType().equals("Int16"))
            {
                Value = 0d;
            }
            else if(vi.getDataType().equals("Int32"))
            {
                Value = 0d;
            }

            break;
        }

        default:
            break;
        }

        getKey();
    }

    public void Reset()
    {
        Init(this.getCN(), this.getType(), this.getIndex(), this.getParam(), this.getOwner());
    }
    
    public synchronized Object getValue() {

        switch (Type)
        {
        case dtVQ:
        case dtAX:
        case dtOR:
            return Value;   

        case dtVN:
            return Value;   

        case dtVB:
            return Value;   

        default:
            return Value;
        }

    }

    public synchronized String getKey() {
        if (Key == null)
            Key = String.format("CN%02dT%03dP%03dI%05d", CN, Type, Param, Index);

        return Key;
    }

    public synchronized String getPrefix() {
        if (Prefix == null)
            Prefix = String.format("CN%02dT%03dP%03d", CN, Type, Param);

        return Prefix;
    }

    public int getCN() {
        return CN;
    }

    public boolean isFix3()
    {
        return (Type == dtVQ ||
                Type == dtAX ||
                Type == dtOR ||
                Type == dtFO 
                );
    }

    public void setCN(int cn) {
        CN = cn;
        Key = null;
        Prefix = null;
    }

    public synchronized int getIndex() {
        return Index;
    }

    public synchronized void setIndex(int index) {
        Index = index;
        Key = null;
        Prefix = null;
    }

    public synchronized Integer getType() {
        return Type;
    }

    public synchronized void setType(Integer type) {
        Type = type;
        Key = null;
        Prefix = null;
    }

    public synchronized Integer getParam() {
        return Param;
    }

    public synchronized void setParam(Integer param) {
        Param = param;
        Key = null;
        Prefix = null;
    }

    public synchronized void setValue(Object value) {



        switch (Type)
        {
        case dtVQ:
        case dtAX:
        case dtOR:
            Value = value;
            break;

        case MultiCmdItem.dtVN:
            Value = value;   
            break;

        case MultiCmdItem.dtVB:
            Value = value;   
            break;

        default:
            Value = value;
            break;
        }


    }

    public synchronized int getMulti() {
        return Multi;
    }

    public synchronized void setMulti(int multi) {
        Multi = multi;
    }

    public byte[] GetWriteBlock()
    {
        byte[] rt = GetRequestTern();
        byte[] b = new byte[]{};

        switch (Type)
        {
        case dtVB:
        case dtDO:
        case dtDI:
        {
            if ((Double)Value == 0)
                b = new byte[]{0};
            else
                b = new byte[]{1};
            break;
        }

        case dtNONE:
        {
            Short s = ((Double)0d).shortValue();
            byte[] Val = ByteBuffer.allocate(4).putShort(s).array();
            b = new byte[]{Val[0], Val[1]};
            break;
        }

        case dtVN:
        case dtAI:
        case dtAO:
        {
            Short s = ((Double)Value).shortValue();
            byte[] Val = ByteBuffer.allocate(4).putShort(s).array();
            b = new byte[]{Val[0], Val[1]};
            break;
        }

        case dtVQ:
        case dtAX:
        case dtOR:
        {
            Integer i = ((Double)Value).intValue();
            byte[] Val = ByteBuffer.allocate(4).putInt(i).array();
            b = new byte[]{Val[0], Val[1], Val[2], Val[3]};
            break;
        }

        case dtVA:
        {
            b = ((String)Value).getBytes(StandardCharsets.UTF_8);
            b = Protocol.ByteArrayCat(b, new byte[]{0});

            break;
        }
        case dtGP:
            if (Index >= 0 && Index <= 1023)
            {
                Integer i = ((Double)Value).intValue();
                byte[] Val = ByteBuffer.allocate(4).putInt(i).array();
                b = new byte[]{Val[0], Val[1], Val[2], Val[3]};
                break;
            }

            if (Index >= 3072 && Index <= 4095)
            {
                b = ((String)Value).getBytes(StandardCharsets.UTF_8);
                b = Protocol.ByteArrayCat(b, new byte[]{0});

                break;
            }



            break;
            
        case dtDB:
        {
            VFKItem vi = Owner.getVFK().get(Integer.toString(Index));

            if (vi.getDataType().equals("String"))
            {
                b = ((String)Value).getBytes(StandardCharsets.UTF_8);
                b = Protocol.ByteArrayCat(b, new byte[]{0});

            }
            else if(vi.getDataType().equals("Int16"))
            {
                Short s = ((Double)Value).shortValue();
                byte[] Val = ByteBuffer.allocate(4).putShort(s).array();
                b = new byte[]{Val[0], Val[1]};
                break;
            }
            else if(vi.getDataType().equals("Int32"))
            {
                Integer i = ((Double)Value).intValue();
                byte[] Val = ByteBuffer.allocate(4).putInt(i).array();
                b = new byte[]{Val[0], Val[1], Val[2], Val[3]};
                break;
            }

            break;
        }

        default:
            break;
        }

        return Protocol.ByteArrayCat(rt, b); 
    }

    public byte[] GetRequestTern()
    {
        byte[] b = new byte[]{};

        switch (Type)
        {
        case dtVB:
        case dtDI:
        case dtDO:
        {
            byte[] Idx = ByteBuffer.allocate(4).putInt(Index).array();
            b = new byte[]{(byte)((byte)Type | (byte)(Multi << 5)), Idx[2], Idx[3]};

            break;
        }


        case dtAX:
        case dtAL:
        case dtAO:
        case dtAI:
        case dtOR:
        case dtOA:
        {
            byte[] Idx = ByteBuffer.allocate(4).putInt(Index).array();
            b = new byte[]{(byte)((byte)Type | (byte)(Multi << 5)), (byte)Param , Idx[3]};
            break;
        }

        case dtDB:
        {
            byte[] Idx = ByteBuffer.allocate(4).putInt(Index).array();
            b = new byte[]{(byte)((byte)Type | (byte)(Multi << 5)), 
                           (byte) ((byte)(Param << 2) | (Idx[2] & (byte)3)), 
                           Idx[3]};
            break;
        }

        case dtEN:
        {
            byte[] Idx = ByteBuffer.allocate(4).putInt(Index).array();
            b = new byte[]{(byte)((byte)Type | (byte)(Multi << 5)), (byte) ((byte)((byte)Param & (byte)0x3F) | (Idx[2]<<6)) , Idx[3]};
            break;
        }

        case dtNONE:
        {
            byte[] Idx = ByteBuffer.allocate(4).putInt(0).array();
            b = new byte[]{(byte)((byte)2 | (byte)(0 << 5)), Idx[2], Idx[3]};
            break;
        }
        
        default:
        {
            byte[] Idx = ByteBuffer.allocate(4).putInt(Index).array();
            b = new byte[]{(byte)((byte)Type | (byte)(Multi << 5)), Idx[2], Idx[3]};
            break;
        }
        }


        return b; 
    }

    public int ParseValue(byte[] buf, int StartIdx)
    {
        int sz = 0;
        switch (Type)
        {
        case dtVB:
        case dtDI:
        case dtDO:
            try {
                Value = ((Byte)buf[StartIdx]).doubleValue();
                sz = 1;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Value = 0d;
            }
            break;

        case dtNONE:
        case dtVN:
        case dtAI:
        case dtAO:
            try {
                Value = ((Short)(ByteBuffer.wrap(buf, StartIdx, 2).getShort())).doubleValue();
                sz = 2;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Value = 0d;
            }
            break;

        case dtVQ:
        case dtAX:
        case dtOR:
            try {
                Value = ((Integer)ByteBuffer.wrap(buf, StartIdx, 4).getInt()).doubleValue();
                sz = 4;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Value = 0d;
            }
            break;

        case dtEN:
            switch(Param)
            {
            case dpEN_HW:
            case dpEN_CODE:
            case dpEN_IRQCONTROL:
                try {
                    Value = ((Short)(ByteBuffer.wrap(buf, StartIdx, 2).getShort())).doubleValue();
                    sz = 2;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Value = 0d;
                }
                break;
                
            case dpEN_SW:
            case dpEN_IRQCOUNTER:
            case dpEN_FREERUNCOUNTER:
                try {
                    Value = ((Integer)ByteBuffer.wrap(buf, StartIdx, 4).getInt()).doubleValue();
                    sz = 4;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Value = 0d;
                }
                break;
            }
            break;
            
        case dtVA:
            try {
                int cnt = 0;
                while(buf[StartIdx + cnt] != 0)
                {

                    cnt += 1;
                }
                cnt += 1;

                String s = new String (buf, StartIdx, cnt - 1);

                Value = s;
                sz = cnt;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Value = "";
            }

            break;


        case dtAL:
            switch (Param)
            {
            case dpAL_REPORT:
            case dpAL_EMERGENCY:
            case dpAL_MAIN:
                try {
                    Value = ByteBuffer.wrap(buf, StartIdx, 2).getShort();
                    sz = 2;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Value = (short)0;
                }
                break;

            case dpAL_PLC:
                switch (Index)
                {
                case 0:
                case 1:
                    try {
                        Value = ByteBuffer.wrap(buf, StartIdx, 2).getShort();
                        sz = 2;
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Value = (short)0;
                    }
                    break;

                case 2:
                    try {
                        int ptr = StartIdx;
                        int tt = 0;
                        int Tern = 0;
                        int AlarmWord = 0;
                        int Alarm = 0;
                        int TotTern = buf[ptr];
                        ArrayList<Integer> ac = new ArrayList<Integer>();
                        int bit = 0;


                        ptr ++;

                        for(tt = 0; tt < TotTern; tt++)
                        {
                            Tern = buf[ptr];
                            ptr ++;

                            AlarmWord = ByteBuffer.wrap(buf, ptr, 2).getShort();

                            for(bit = 0; bit < 16; bit++)
                            {
                                if ((AlarmWord & 1) != 0)
                                {
                                    Alarm = Tern * 16 + bit + 1;
                                    ac.add(Alarm);
                                }
                                AlarmWord = AlarmWord >> 1;
                            }

                            ptr += 2;
                        }

                        int[] al = new int[ac.size()];
                        tt = 0;
                        for (int a: ac)
                        {
                            al[tt] = a;
                            tt++;
                        }

                        Value = al;
                        sz = ptr - StartIdx;

                    } 
                    catch (Exception e) 
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Value = new int[]{0};
                    }
                    
                    break;
                }
                break;
                
            case dpAL_M32_Description:
                try {
                    int cnt = 0;
                    while(buf[StartIdx + cnt] != 0)
                    {

                        cnt += 1;
                    }
                    cnt += 1;

                    String s = new String (buf, StartIdx, cnt - 1);

                    Value = s;
                    sz = cnt;

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Value = "";
                }
                break;

            case dpAL_M32:
                switch (Index)
                {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    break;

                case 7:
                case 8:
                    Value = ByteBuffer.wrap(buf, StartIdx, 4).getInt();
                    sz = 4;
                    break;

                case 9:
                case 10:
                case 11:
                case 12:
                case 13:

                    try {
                        int i = 0;
                        int Tot = 0;
                        int ptr = StartIdx;

                        Tot = ByteBuffer.wrap(buf, StartIdx, 4).getInt();
                        ptr += 4;



                        switch (Index)
                        {
                        case 10: 
                            break;

                        case 11: 
                            break;

                        case 12: 
                            break;

                        case 9:
                        case 13:  
                            //Codice Di Rerrore + Par1 + Par2 + DateTime
                            int[] AlBuf = new int[Tot * 4 + 1];
                            AlBuf[0] = Tot;

                            for (i = 1; i <= Tot * 4; i++)
                            {
                                AlBuf[i] = ByteBuffer.wrap(buf, ptr, 4).getInt();
                                ptr += 4;
                            }
                            Value = AlBuf;
                            sz = ptr - StartIdx;
                            break;
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Value = new int[]{0};
                    }

                    break;
                }
            }
            break;

        case dtGP:
            if (Index >= 0 && Index <= 1023)
            {
                Value = ((Integer)ByteBuffer.wrap(buf, StartIdx, 4).getInt()).doubleValue();
                sz = 4;
            }

            if (Index >= 3072 && Index <= 4095)
            {
                try {
                    int cnt = 0;
                    while(buf[StartIdx + cnt] != 0)
                    {

                        cnt += 1;
                    }
                    cnt += 1;

                    String s = new String (buf, StartIdx, cnt - 1);

                    Value = s;
                    sz = cnt;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Value = "";
                }
            }



            break;

        case dtDB:
            VFKItem vi = Owner.getVFK().get(Integer.toString(Index));

            if (vi.getDataType().equals("String"))
            {
                try {
                    int cnt = 0;
                    while(buf[StartIdx + cnt] != 0)
                    {

                        cnt += 1;
                    }
                    cnt += 1;

                    String s = new String (buf, StartIdx, cnt - 1);

                    Value = s;
                    sz = cnt;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Value = "";
                }
            }	
            else if(vi.getDataType().equals("Int16"))
            {
                try {
                    Value = ((Short)(ByteBuffer.wrap(buf, StartIdx, 2).getShort())).doubleValue();
                    sz = 2;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Value = 0d;
                }
            }
            else if(vi.getDataType().equals("Int32"))
            {
                try {
                    Value = ((Integer)ByteBuffer.wrap(buf, StartIdx, 4).getInt()).doubleValue();
                    sz = 4;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Value = 0d;
                }
            }

            break;
        default:
            sz = 0;
            break;
        }

        return sz;
    }

}
