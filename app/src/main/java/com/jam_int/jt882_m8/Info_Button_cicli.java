package com.jam_int.jt882_m8;

import communication.MultiCmdItem;

public class Info_Button_cicli {

    public Button_name button_name = Button_name.NULL;
    public TipoButton tipoButton = TipoButton.NULL;
    int step_mc_stati = 0;
    MultiCmdItem multicmd_button;
    MultiCmdItem multicmd_stato_green_red;
    boolean Run = false;
    boolean errore = false;
    public enum Button_name {NULL, PIEGATORE, CARICATORE, SCARICATORE, TEST_PIEGATORE, CAMBIO_ALL1}
    public enum TipoButton {NULL, EDGE, TOOGLE}
}
