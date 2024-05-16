package com.jam_int.jt882_m8;

import communication.MultiCmdItem;

public class Mci_write {
    public Comando comando = Comando.NULL;
    public TipoVariabile tipoVariabile = TipoVariabile.NULL;
    MultiCmdItem mci;
    boolean write_flag = false;
    Double valore = 0.0d;
    boolean SingoloImpulso = false;
    boolean PressioneLunga = false;
    Integer mc_stati = 0;
    boolean Fronte_positivo = false;
    boolean Fronte_negativo = false;
    Double valore_precedente = 0.0d;
    String mci_stringa = "";
    boolean Aggiorna_grafica = false;
    String path_file = "";

    public void Mci_write(Double Valore, Double Valore_precedente) {
        this.valore = Valore;
        this.valore_precedente = Valore_precedente;
        this.write_flag = false;
        this.tipoVariabile = Mci_write.TipoVariabile.NULL;
    }

    enum Comando {NULL, set_ON, set_OFF}

    enum TipoVariabile {NULL, VB, VN, VQ}
}
