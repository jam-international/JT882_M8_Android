package com.jam_int.jt882_m8;


import android.content.Context;
import android.graphics.PointF;
import android.widget.Toast;

import com.jamint.ricette.MathGeoTri;

import org.kabeja.dxf.DXFArc;
import org.kabeja.dxf.DXFEntity;
import org.kabeja.dxf.DXFLine;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Better to make this public and use it everywhere
 * <p>
 * TODO move in a new file
 */
enum TipiTasche {
    Tipo_5lati_archi, Tipo_5lati_dritti, Tipo_4lati_smussi, Tipo_quadrata, Tipo_4lati_raccordi, Tipo_7lati_archi
}

public class Dxf_import {
    /**
     * Read Dxf and get the points i need to draw the Ricetta
     *
     * @param entity  List of Dxf entities
     * @param context Context
     * @return A list of Strings that contain the lines of a pts file
     */
    public static ArrayList<String> getPtsValue(ArrayList<DXFEntity> entity, Context context) {
        ArrayList<String> ret = new ArrayList<String>();
        ArrayList<DXFEntity> entity_ordinate = ordinaEntity(entity, context);

        // Check if the Dxf entity number is more than 0 and less than 7 (the current max entity count)
        if (entity_ordinate.size() > 0 && entity_ordinate.size() <= 7) {
            try {
                // Get the tasca type
                TipiTasche tipo_Tasca = getTipoTasca(entity_ordinate, context);

                switch (tipo_Tasca) {
                    case Tipo_5lati_archi: {
                        // Tipo
                        ret.add("0");
                        // Numero punti
                        ret.add("9");
                    }
                    break;
                    case Tipo_5lati_dritti: {
                        // Tipo
                        ret.add("1");
                        // Numero punti
                        ret.add("9");
                    }
                    break;
                    case Tipo_4lati_smussi: {
                        // Tipo
                        ret.add("2");
                        // Numero punti
                        ret.add("8");
                    }
                    break;
                    case Tipo_quadrata: {
                        // Tipo
                        ret.add("3");
                        // Numero punti
                        ret.add("7");
                        //linea verticale dx
                    }
                    break;
                    case Tipo_4lati_raccordi: {
                        // Tipo
                        ret.add("4");
                        // Numero punti
                        ret.add("8");
                    }
                    break;
                    case Tipo_7lati_archi: {
                        // TODO
                    }
                    break;
                }

                ret.addAll(ConvertDxfEntitiesToStringPoints(entity_ordinate));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (ret.size() == 0)
            Toast.makeText(context, "No pts points were generated", Toast.LENGTH_SHORT).show();
        if (ret.size() < 9)
            Toast.makeText(context, "Insufficient number of pts points", Toast.LENGTH_SHORT).show();
        if (ret.size() > 13)
            Toast.makeText(context, "Too many pts points", Toast.LENGTH_SHORT).show();

        return ret;
    }

    /**
     * Function for convert Dxf entities to a list of points
     *
     * @param dxfEntities Dxf entities list
     * @return String points
     */
    private static ArrayList<String> ConvertDxfEntitiesToStringPoints(ArrayList<DXFEntity> dxfEntities) {
        ArrayList<String> ret = new ArrayList<String>();

        // Check the start entity type
        if (dxfEntities.get(0).getType().equals("LINE")) {
            DXFLine lineStart = (DXFLine) dxfEntities.get(0);
            ret.add(-(Math.round(lineStart.getStartPoint().getX() * 100.0f) / 100.0f) + "," + (-Math.round(lineStart.getStartPoint().getY() * 100.0f) / 100.0f));
        } else if (dxfEntities.get(0).getType().equals("ARC")) {
            DXFArc arcStart = (DXFArc) dxfEntities.get(0);
            ret.add(-(Math.round(arcStart.getStartPoint().getX() * 100.0f) / 100.0f) + "," + (-Math.round(arcStart.getStartPoint().getY() * 100.0f) / 100.0f));
        }

        // Loop for every entity
        for (int i = 0; i < dxfEntities.size(); i++) {
            // Check the entity type
            if (dxfEntities.get(i).getType().equals("LINE")) {
                // Convert the entity to Line
                DXFLine line = (DXFLine) dxfEntities.get(i);

                ret.add(-(Math.round(line.getEndPoint().getX() * 100.0f) / 100.0f) + "," + (-Math.round(line.getEndPoint().getY() * 100.0f) / 100.0f));
                ret.add(-(Math.round(line.getEndPoint().getX() * 100.0f) / 100.0f) + "," + (-Math.round(line.getEndPoint().getY() * 100.0f) / 100.0f));
            } else if (dxfEntities.get(i).getType().equals("ARC")) {
                // Convert the entity to Arc
                DXFArc arc = (DXFArc) dxfEntities.get(i);

                boolean direction = MathGeoTri.ArcDirection_StartEndCenter(new PointF((float) arc.getStartPoint().getX(), (float) arc.getStartPoint().getY()), new PointF((float) arc.getEndPoint().getX(), (float) arc.getEndPoint().getY()), new PointF((float) arc.getCenterPoint().getX(), (float) arc.getCenterPoint().getY()));
                PointF midPoint = MathGeoTri.CalculateArcThirdPoint(new PointF((float) arc.getStartPoint().getX(), (float) arc.getStartPoint().getY()), new PointF((float) arc.getEndPoint().getX(), (float) arc.getEndPoint().getY()), (float) arc.getRadius(), new PointF((float) arc.getCenterPoint().getX(), (float) arc.getCenterPoint().getY()), ! direction);
                ret.add(-(Math.round(midPoint.x * 100.0f) / 100.0f) + "," + (-Math.round(midPoint.y * 100.0f) / 100.0f));
                ret.add(-(Math.round(arc.getEndPoint().getX() * 100.0f) / 100.0f) + "," + (-Math.round(arc.getEndPoint().getY() * 100.0f) / 100.0f));
            }
        }

        return ret;
    }

    /**
     * Function for get the tasca type based on the line and arc count
     *
     * @param entity_ordinate List of Dxf entities
     * @param context         Context
     * @return
     */
    private static TipiTasche getTipoTasca(ArrayList<DXFEntity> entity_ordinate, Context context) {
        int cnt_LINEE = contaLinee(entity_ordinate);
        int cnt_ARCHI = contaArchi(entity_ordinate);
        if (entity_ordinate.size() == 4 && cnt_ARCHI >= 1) return TipiTasche.Tipo_5lati_archi;
        if (entity_ordinate.size() == 4 && cnt_LINEE == 4) return TipiTasche.Tipo_5lati_dritti;
        if (entity_ordinate.size() == 5 && cnt_LINEE == 5) return TipiTasche.Tipo_4lati_smussi;
        if (entity_ordinate.size() == 3) return TipiTasche.Tipo_quadrata;
        if (entity_ordinate.size() == 5 && cnt_ARCHI == 2) return TipiTasche.Tipo_4lati_raccordi;
        if (entity_ordinate.size() == 6) // TODO;
            Toast.makeText(context, "Pocket type not recognized", Toast.LENGTH_SHORT).show();

        return null;
    }

    /**
     * Function for count the number of arcs
     *
     * @param entity_ordinate
     * @return
     */
    private static int contaArchi(ArrayList<DXFEntity> entity_ordinate) {
        int cnt = 0;
        for (DXFEntity item : entity_ordinate) {
            String tipo_entity = item.getType();
            if (tipo_entity.equals("ARC")) cnt++;
        }
        return cnt;
    }

    /**
     * Function for count the number of lines
     *
     * @param entity_ordinate
     * @return
     */
    private static int contaLinee(ArrayList<DXFEntity> entity_ordinate) {
        int cnt = 0;
        for (DXFEntity item : entity_ordinate) {
            String tipo_entity = item.getType();
            if (tipo_entity.equals("LINE")) cnt++;
        }
        return cnt;
    }

    /**
     * Function for reorder the Dxf entities list
     *
     * @param entity
     * @param context
     * @return
     */
    private static ArrayList<DXFEntity> ordinaEntity(ArrayList<DXFEntity> entity, Context context) {
        ArrayList<DXFEntity> copy_entity;
        ArrayList<DXFEntity> ret = new ArrayList<DXFEntity>();
        PointF punto_di_ricerca = new PointF(0f, 0f);

        // La riga in alto orizzontale non serve per il pts e mi da fastidio per calcola il punto più vicino allo 0,0, la elimino
        copy_entity = elimina_riga_orizzontale(entity);

        for (int i = 0; i < copy_entity.size(); i++) {
            ArrayList<ArrayList<String>> id_più_vicino = getIdPiuVicino(copy_entity, punto_di_ricerca);

            if (id_più_vicino.size() > 0) {
                String Id_string = id_più_vicino.get(0).get(0);
                int Id = Integer.parseInt(Id_string);

                String tipo_entity = id_più_vicino.get(0).get(1);
                if (id_più_vicino.get(0).get(2).equals("p1") && tipo_entity.equals("LINE")) {
                    DXFLine line = (DXFLine) copy_entity.get(Id);
                    DXFLine newLine = new DXFLine();
                    newLine.setStartPoint(line.getStartPoint());
                    newLine.setEndPoint(line.getEndPoint());
                    ret.add(newLine);
                    punto_di_ricerca = new PointF((float) newLine.getEndPoint().getX(), (float) newLine.getEndPoint().getY());    //mi segno il punto opposto dell'entità per cercare la prossima entità concatenata
                }
                if (id_più_vicino.get(0).get(2).equals("p2") && tipo_entity.equals("LINE")) {
                    DXFLine line = (DXFLine) copy_entity.get(Id);
                    DXFLine newLine = new DXFLine();
                    newLine.setStartPoint(line.getEndPoint());
                    newLine.setEndPoint(line.getStartPoint());
                    ret.add(newLine);
                    punto_di_ricerca = new PointF((float) newLine.getEndPoint().getX(), (float) newLine.getEndPoint().getY());    //mi segno il punto opposto dell'entità per cercare la prossima entità concatenata

                }
                if (id_più_vicino.get(0).get(2).equals("p1") && tipo_entity.equals("ARC")) {
                    DXFArc arc = (DXFArc) copy_entity.get(Id);
                    DXFArc newArc = new DXFArc();
                    newArc.setCenterPoint(arc.getCenterPoint());
                    newArc.setRadius(arc.getRadius());
                    newArc.setStartAngle(arc.getStartAngle());
                    newArc.setEndAngle(arc.getEndAngle());
                    ret.add(newArc);
                    punto_di_ricerca = new PointF((float) newArc.getEndPoint().getX(), (float) newArc.getEndPoint().getY());    //mi segno il punto opposto dell'entità per cercare la prossima entità concatenata
                }
                if (id_più_vicino.get(0).get(2).equals("p3") && tipo_entity.equals("ARC")) {
                    DXFArc arc = (DXFArc) copy_entity.get(Id);
                    DXFArc newArc = new DXFArc();
                    newArc.setCenterPoint(arc.getCenterPoint());
                    newArc.setRadius(arc.getRadius());
                    newArc.setStartAngle(arc.getEndAngle());
                    newArc.setEndAngle(arc.getStartAngle());
                    ret.add(newArc);
                    punto_di_ricerca = new PointF((float) newArc.getStartPoint().getX(), (float) newArc.getStartPoint().getY());    //mi segno il punto opposto dell'entità per cercare la prossima entità concatenata
                }
                copy_entity.remove(Id);
                i = -1;
            }
        }

        if (ret.size() == 0)
            Toast.makeText(context, "No ordered entities found", Toast.LENGTH_SHORT).show();
        if (ret.size() > 5)
            Toast.makeText(context, "Incorrect number of ordered entities", Toast.LENGTH_SHORT).show();

        return ret;
    }

    /**
     * Function for get the near id of the given Dxf entity
     *
     * @param entity           The entity i'm currently on
     * @param punto_di_ricerca The point that will the start point of the next ent
     * @return
     */
    private static ArrayList<ArrayList<String>> getIdPiuVicino(ArrayList<DXFEntity> entity, PointF punto_di_ricerca) {
        ArrayList<ArrayList<String>> ret = new ArrayList<ArrayList<String>>();
        int Id_cnt = -1;
        double distanza_minima_trovata = 1000d;
        for (DXFEntity item : entity) {
            String tipo = item.getType();
            switch (tipo) {
                case "LINE": {
                    DXFLine line = (DXFLine) item;
                    float x1 = (float) line.getStartPoint().getX();
                    float y1 = (float) line.getStartPoint().getY();
                    float x2 = (float) line.getEndPoint().getX();
                    float y2 = (float) line.getEndPoint().getY();
                    double dist1 = MathGeoTri.Distance(punto_di_ricerca, new PointF(x1, y1));
                    double dist2 = MathGeoTri.Distance(punto_di_ricerca, new PointF(x2, y2));

                    Id_cnt++;
                    if (dist1 < distanza_minima_trovata) {
                        distanza_minima_trovata = dist1;
                        ret.clear();
                        ret.add(new ArrayList<String>(Arrays.asList(String.valueOf(Id_cnt), "LINE", "p1")));
                    }
                    if (dist2 < distanza_minima_trovata) {
                        distanza_minima_trovata = dist2;
                        ret.clear();
                        ret.add(new ArrayList<String>(Arrays.asList(String.valueOf(Id_cnt), "LINE", "p2")));
                    }
                }
                break;
                case "ARC": {
                    DXFArc arc = (DXFArc) item;
                    float x1 = (float) arc.getStartPoint().getX();
                    float y1 = (float) arc.getStartPoint().getY();
                    float x3 = (float) arc.getEndPoint().getX();
                    float y3 = (float) arc.getEndPoint().getY();
                    double dist1 = MathGeoTri.Distance(punto_di_ricerca, new PointF(x1, y1));
                    double dist3 = MathGeoTri.Distance(punto_di_ricerca, new PointF(x3, y3));
                    Id_cnt++;
                    if (dist1 < distanza_minima_trovata) {
                        distanza_minima_trovata = dist1;
                        ret.clear();
                        ret.add(new ArrayList<String>(Arrays.asList(String.valueOf(Id_cnt), "ARC", "p1")));
                    }
                    if (dist3 < distanza_minima_trovata) {
                        distanza_minima_trovata = dist3;
                        ret.clear();
                        ret.add(new ArrayList<String>(Arrays.asList(String.valueOf(Id_cnt), "ARC", "p3")));
                    }
                }
                break;
                default:
                    break;
            }
        }
        return ret;
    }

    /**
     * Function for remove the horizontal line
     * <p>
     * Elimino solo la linea orizzontale che si trova a max 5 dallo 0
     * <p>
     * Martino 20/09/2021
     *
     * @param entity List of Dxf Entities
     * @return
     */
    private static ArrayList<DXFEntity> elimina_riga_orizzontale(ArrayList<DXFEntity> entity) {
        int maxLineY = -5;
        ArrayList<DXFEntity> ret = new ArrayList<DXFEntity>();
        // Loop for every dxf entity
        for (DXFEntity item : entity) {
            // Check the entity type
            String tipo_entity = item.getType();
            switch (tipo_entity) {
                case "LINE": {
                    DXFLine line = (DXFLine) item;
                    float y1 = (float) line.getStartPoint().getY();
                    float y2 = (float) line.getEndPoint().getY();

                    if (Math.abs(y1-y2) >0.1f || y1 < maxLineY || y2 < maxLineY) {
                       ret.add(item);
                    }
                }
                break;
                case "ARC": {
                    DXFArc arc = (DXFArc) item;
                    float y1 = (float) arc.getStartPoint().getY();
                    float y3 = (float) arc.getEndPoint().getY();

                    if (MathGeoTri.round5cent(y1) != MathGeoTri.round5cent(y3) || y1 < maxLineY || y3 < maxLineY) {
                        ret.add(item);
                    }
                }
                break;
                default:
                    break;
            }
        }
        return ret;
    }
}
