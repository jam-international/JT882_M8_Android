package com.jam_int.jt882_m8;

import com.jamint.ricette.Element;
import com.jamint.ricette.JamPointStep;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that contains all the commands for edit the Ricetta
 */
public class Info_modifica {
    /**
     * Set the default command
     */
    public Comando comando = Comando.Null;
    int id_punto_inizio_modifica;
    int id_punto_fine_modifica;
    int id_element_inizio_modifica;
    int id_element_fine_modifica;
    int id_punto_middle_modifica;
    float DeltaX_inizio;
    float DeltaY_inizio;
    float X_Middle;
    float Y_Middle;
    float X_Start;
    float Y_Start;
    float X_End;
    float Y_End;
    boolean QuoteRelativeAttive;
    List<Element> ElemSelezionati;
    float LP, AltezzaZigZag;
    JamPointStep StepAttivo;
    boolean puntoCarico;

    /**
     * Constructor for initialize the values
     */
    public Info_modifica() {
        id_punto_inizio_modifica = 0;
        id_punto_fine_modifica = 0;
        comando = Comando.Null;
        DeltaX_inizio = 0f;
        DeltaY_inizio = 0f;
        QuoteRelativeAttive = false;
        ElemSelezionati = new ArrayList<Element>();
        LP = 3.0f;
        X_Middle = 0f;
        Y_Middle = 0f;
        X_Start = 0f;
        Y_Start = 0f;
        X_End = 0f;
        Y_End = 0f;
        AltezzaZigZag = 3.0f;
        StepAttivo = null;
        id_element_inizio_modifica = 0;
        id_element_fine_modifica = 0;
        puntoCarico = false;
    }

    public enum Comando {
        Null, HOME, HOME_DONE, SPOSTA1, SPOSTA2, CANCELLA,SPOSTA2_STRETCH,STRETCH, ZIGZAG, ZIGZAG_1, ZIGZAG_2, M888, SPOSTA_ALL, RADDRIZZA_LINEA, RADDRIZZA_ARCO, RADDRIZZA_ARCO1,
        LINEA, LINEA1, STRETCH1, STRETCH2, ARCO3P_0, ARCO3P_1, ARCO3P_2, FEED, RADDRIZZA_ARCO_ENTITA, ESCI, ESCI_DONE_AZZERAMENTO,FAIHOME_AND_EXIT
    }
}
