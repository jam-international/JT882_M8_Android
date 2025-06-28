package com.jam_int.jt882_m8;

import com.jamint.ricette.PLCType;

public class Values {

    /**
     * Global variables
     */

    // TODO make a list of global variables like:
    //  the ricette path,
    //  some default values like the [163, -1.2] point that is the center of the pocket,
    //  finally add the debug value well
    //  ecc...
    //  NOTE: make them all final for let them be readonly

    /**
     * Debug mode
     * <p>
     * This is used for remove the connection with PLC
     * <p>
     * TODO not implemented well. For implement it well i need to add a switch when the code communicate with PLC
     */
    public static final boolean Debug_mode = false;

    /**
     * Set the plc type for this project
     */
    public static final PLCType plcType = PLCType.M8;

    /**********************************************************************************************************************/

    /**
     * List of vars that i use for store values
     */

    /**
     * Pocket type
     */
    public static int type;
    public static int model;
    public static int model1;

    /**
     * Shell values
     */
    public static float M1 = -1000;
    public static float M2 = -1000;
    public static float M3 = -1000;
    public static float M4 = -1000;
    public static float M5 = -1000;
    public static float M6 = -1000;
    public static float H1 = -1000;
    public static float H2 = -1000;
    public static float H3 = -1000;
    public static float H4 = -1000;
    public static float L = -1000;
    public static float S1 = -1000;
    public static float S2 = -1000;
    public static float S3 = -1000;
    public static float S4 = -1000;
    public static float P = -1000;
    public static float Hf = -1000;

    /**
     * Draw values
     */
    public static float M = -1000;
    public static float A = -1000;
    public static float B = -1000;
    public static float C = -1000;
    public static float F = -1000;
    public static float G = -1000;
    public static float H = -1000;
    public static float E = -1000;
    public static float D = -1000;
    public static float LP = -1000;
    public static float I = -1000;
    public static float Lm = -1000;
    public static float N = -1000;
    public static float O = -1000;
    public static float Dpc = 0;

    /**
     * Fermatura Type
     */
    public static int Fi1 = 0;
    public static int Ff1 = 0;
    public static int Fi2 = 0;
    public static int Ff2 = 0;

    /**
     * Fermatura Points number
     */
    public static int pFi1 = 3;
    public static int pFf1 = 3;
    public static int pFi2 = 3;
    public static int pFf2 = 3;

    /**
     * Fermatura repetition
     */
    public static int Fi1t = 2;
    public static int Ff1t = 1;
    public static int Fi2t = 2;
    public static int Ff2t = 2;

    /**
     * Codes values
     */
    public static int OP2On = -2;
    public static int OP1On = 0;
    public static int OP2Off = 2;
    public static int Speed1 = -4;
    public static int OP3 = -4;

    /**
     * Loaded Xml files
     */
    public static String File_XML_path_R;
    public static String File_XML_path_L;

    public static String File_XML_path_T2_R;
    public static String File_XML_path_T2_L;

    /**
     * Machine model
     */
    public static String Machine_model;
    public static String Tcp_enable_status,TcpButton, TcpNomeCommessa;

    /**
     * Pts file selected
     */
    public static String File;

    public static int imgScheletro;
    public static String Chiamante;
    /**
     *
     */
    public static double UdfPuntiVelIni_T1 = 0.003d;
    public static int UdfVelIniRPM_T1 = 300;
    public static double UdfPuntiVelRall_T1 = 0.003d;
    public static int UdfVelRallRPM_T1 = 300;
    public static int Udf_FeedG0_T1 = 80000;
    public static int Udf_ValTensione_T1 = 10;
    public static int Udf_20_T1 = 0;
    public static int Udf_ValElettrocalamitaSopra_T1 = 50;
    public static int Udf_ValElettrocalamitaSotto_T1 = 50;
    public static int Udf_VelocitaCaricLavoro_T1 = 80000;
    public static int Udf_24_T1 = 0;
    public static int Udf_25_T1 = 0;
    public static int Udf_26_T1 = 0;
    public static int Udf_27_T1 = 0;
    public static int Udf_28_T1 = 0;
    public static int Udf_29_T1 = 0;
    public static int Udf_30_T1 = 0;
    public static int Udf_SequenzaPiegatore_chiusura_T1 = 1230;
    public static int Udf_SequenzaPiegatore_apetura_T1 = 321;

    public static double UdfPuntiVelIni_T2 = 0.003d;
    public static int UdfVelIniRPM_T2 = 300;
    public static double UdfPuntiVelRall_T2 = 0.003d;
    public static int UdfVelRallRPM_T2 = 300;
    public static int Udf_FeedG0_T2 = 80000;
    public static int Udf_ValTensione_T2 = 10;
    public static int Udf_20_T2 = 0;
    public static int Udf_ValElettrocalamitaSopra_T2 = 50;
    public static int Udf_ValElettrocalamitaSotto_T2 = 50;
    public static int Udf_VelocitaCaricLavoro_T2 = 80000;
    public static int Udf_24_T2 = 0;
    public static int Udf_25_T2 = 0;
    public static int Udf_26_T2 = 0;
    public static int Udf_27_T2 = 0;
    public static int Udf_28_T2 = 0;
    public static int Udf_29_T2 = 0;
    public static int Udf_30_T2 = 0;
    public static int Udf_SequenzaPiegatore_chiusura_T2 = 1230;
    public static int Udf_SequenzaPiegatore_apetura_T2 = 321;
    static String ver_firmware,PLCver,HMI_softver ="3.4";

}
