package com.jam_int.jt882_m8;

import com.jamint.ricette.Ricetta;

public class CoordPosPinza {
    double XCoordPosPinza, YCoordPosPinza, XCoord_precedente, YCoord_precedente;

    public CoordPosPinza() {
    }

    public void set(double X, double Y, Ricetta r) {
       // if (r != null) {
       //     XCoordPosPinza = X ;
       //     YCoordPosPinza = Y ;
       // } else {
            XCoordPosPinza = X;
            YCoordPosPinza = Y;
       // }
    }
}
