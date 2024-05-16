package com.jam_int.jt882_m8;

/**
 * TODO ???
 */
public class Info_StepPiuMeno {
    static int numeroRipetuto = 0;
    public Tipo_spostamento tipo_spostamento = Tipo_spostamento.NULL;
    public Comando comando = Comando.NULL;
    public Direzione direzione = Direzione.NULL;
    int MacStati_StepVeloce = 0;
    int MacStati_StepSingolo = 0;
    String last_testo_textView_info = "Info";

    public enum Tipo_spostamento {NULL, SINGOLO, VELOCE, SINGOLO_RIPETUTO, N_SALTO, TO_STEP_ATTIVO}

    public enum Comando {NULL, GO, STOP}

    public enum Direzione {NULL, AVANTI, DIETRO}
}
