package com.jam_int.jt882_m8;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.util.Pair;

import com.jamint.ricette.CenterPointRadius;
import com.jamint.ricette.CodeType;
import com.jamint.ricette.CodeValue;
import com.jamint.ricette.CodeValueType;
import com.jamint.ricette.Element;
import com.jamint.ricette.ElementArc;
import com.jamint.ricette.ElementLine;
import com.jamint.ricette.ElementZigZag;
import com.jamint.ricette.JamPointStep;
import com.jamint.ricette.MathGeoTri;
import com.jamint.ricette.Ricetta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

class ResultActivityMaths {

    /**
     * Var that indicate if the fermatura need to have 1 more point
     */
    static boolean oneMorePoint = false;

    /**
     * Function for chose the model to execute actions
     *
     * @param activity
     * @param model
     * @param model1
     * @param type
     * @param FileName
     * @param pocketValues
     * @return
     */
    public static Pair<Ricetta, Ricetta> ChoseModel(Activity activity, int model, int model1, int type, String FileName, HashMap<String, Float> pocketValues) {
        // Fix Math error if M is 0
        if (pocketValues.get("M") == 0) {
            pocketValues.put("M", 0.001f);
        }

        if (model == 0 || (model == 1 && model1 == 1)) {
            TreeMap<String, PointF> points = Model_0_Points(type, FileName, pocketValues);
            Pair<Ricetta, Ricetta> pairRicettaDraw = Model_0_Ricetta(type, points, pocketValues);
            return Model_0_Codes(type, pairRicettaDraw);
        } else if (model == 1 && model1 == 2) {
            TreeMap<String, PointF> points = Model_1_2_Points(type, FileName, pocketValues);
            Pair<Ricetta, Ricetta> pairRicettaDraw = Model_1_2_Ricetta(type, points, pocketValues);
            return Model_1_2_Codes(type, pairRicettaDraw);
        } else if (model == 3) {
            TreeMap<String, PointF> points = Model_3_Points(type, FileName, pocketValues);
            Pair<Ricetta, Ricetta> pairRicettaDraw = Model_3_Ricetta(type, points, pocketValues);
            return Model_3_Codes(type, pairRicettaDraw);
        } else if (model == 2 || model == 4) {
            TreeMap<String, PointF> points = Model_4_Points(type, FileName, pocketValues);
            Pair<Ricetta, Ricetta> pairRicettaDraw = Model_4_Ricetta(type, points, pocketValues);
            return Model_4_Codes(type, pairRicettaDraw);
        } else if (model == 5) {
            TreeMap<String, PointF> points = Model_5_Points(type, FileName, pocketValues);
            Pair<Ricetta, Ricetta> pairRicettaDraw = Model_5_Ricetta(type, points, pocketValues);
            return Model_5_Codes(type, pairRicettaDraw);
        } else {
            Toast.makeText(activity, "Pocket Model not found", Toast.LENGTH_LONG).show();
        }

        return null;
    }

    //Possibilita 2
     /*
            POTREI FARLA COSI' MA DOVREI CAMBIARE TUTTO, QUINDI PER ORA UNISCO QUELLA CON I LATI DRITTI E QUELLA CON I LATI CURVI, L' ALTRA LA LASCIO SEPARATA

            At least 1 line is an Arc (i can remove model_1_1 i think, i can remove model_1_2, i only need to set I != 0 for get it or i get the normal model_0)

                                        Model 0 Shell:

                P9  ---------------------------------------------------------   P1
                    (                                                       )
                     (                                                     )
                      (                                                   )
                       (                                                 )
                        (                                               )
                         (                                             )
                      P8  (                                           )  P2
                           (                                         )
                            (                                       )
                             (                                     )
                              (                                   )
                               (                                 )
                                (                               )
                             P7  -------------------------------  P3
                                       P6       P5       P4

                                         Model 0 Draw:

                    M + (1/2 H travetta)
                       |
                       |
                P9  ------   P10                                  P22  ------   P1
                    (     (                                           )     )
                     (     (                                         )     )
                      (     (  P11                             P21  )     )
                       (     (                                     )     )-/ --- M
                        (     (                                   )     )
                     P8  (     (  P12                      P20   )     )   P2
                          (     (                               /     )
                           (     (                             /     )
                            (     ( P13                  P19  /     )
                             (     (                         /     )
                              (     (  P14 P15 P16 P17  P18 /     )
                               (     (_____________________/     )
                                (                               )
                             P7  -------------------------------  P3
                                      P6        P5       P4


            P12 is at P10 - I, or is the 3 point of the Arc
            P13 exist only if P11 exist (if there is the Arc between P10, P11, P12)
            For now P13 and P19 are always empty because a type with 2 Arcs in the internal part doesn't exist
     */




    /*

                                        Model 0 Shell:

                P9  ---------------------------------------------------------   P1
                    (                                                       )
                     (                                                     )
                      (                                                   )
                       (                                                 )
                        (                                               )
                         (                                             )
                      P8  (                                           )  P2
                           (                                         )
                            (                                       )
                             (                                     )
                              (                                   )
                               (                                 )
                                (                               )
                             P7  -------------------------------  P3
                                       P6       P5       P4

                                         Model 0 Draw:

                    M + (1/2 H travetta)
                       |
                       |
                P9  ------   P10                                  P18  ------   P1
                    (     (                                           )     )
                     (     (                                         )     )
                      (     (                                       )     )
                       (     (                                     )     )-/ --- M
                        (     (                                   )     )
                     P8  (     (  P11                      P17   )     )   P2
                          (     (                               )     )
                           (     (                             )     )
                            (     (                           )     )
                             (     (                         )     )
                              (     (  P12 P13 P14 P15  P16 )     )
                               (     (_____________________)     )
                                (                               )
                             P7  -------------------------------  P3
                                      P6        P5       P4

     */

    /**
     * Function for calculate the draw points from the shell points
     *
     * @param type
     * @param fileName
     * @param pocketValues
     * @return
     */
    public static TreeMap<String, PointF> Model_0_Points(int type, String fileName, HashMap<String, Float> pocketValues) {

        TreeMap<String, PointF> pointsMap = new TreeMap();

        HashMap<String, PointF> pointsPTS = ReadPointsFromPTS(fileName);

        float topLeftMargin_Backtack = pocketValues.get("M") + pocketValues.get("B") / 2;
        float topRightMargin_Backtack = pocketValues.get("M") + pocketValues.get("G") / 2;
        if (type == 5) {
            topLeftMargin_Backtack = pocketValues.get("M");
            topRightMargin_Backtack = pocketValues.get("M");
        }

        if (!pointsPTS.get("P4").equals(pointsPTS.get("P5")) && !pointsPTS.get("P6").equals(pointsPTS.get("P7"))) {

            //2 Arcs

            CenterPointRadius Info_C1 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P3"), pointsPTS.get("P4"), pointsPTS.get("P5"));    //arco in basso a dx
            Float R1 = Info_C1.radius;
            PointF Centro1 = new PointF(Info_C1.center.x, Info_C1.center.y);

            CenterPointRadius Info_C2 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));    //arco in basso a sx
            Float R2 = Info_C2.radius;
            PointF Centro2 = new PointF(Info_C2.center.x, Info_C2.center.y);

            //trovo intersezione degli archi in basso ristretti di M
            ArrayList<PointF> Inters_P5_ristretto = MathGeoTri.FindArcArcIntersections(Centro1.x, Centro1.y, R1 - pocketValues.get("M"), Centro2.x, Centro2.y, R2 - pocketValues.get("M"));

            //per scegliere l'intersezione corretta cerco quella più vicina al punto P5
            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P5_ristretto.get(0).x, Inters_P5_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P5_ristretto.get(1).x, Inters_P5_ristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P5", Inters_P5_ristretto.get(0));
            } else {
                pointsMap.put("P5", Inters_P5_ristretto.get(1));
            }
        } else if (pointsPTS.get("P4").equals(pointsPTS.get("P5")) && !pointsPTS.get("P6").equals(pointsPTS.get("P7"))) {

            //1 Arc

            List<Float> points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P5"), -pocketValues.get("M"));

            float Inter400 = (points.get(2) + (400F) * points.get(1)) / points.get(0);
            float Inter0 = (points.get(2) + 0F * points.get(1)) / points.get(0);

            CenterPointRadius Info_C2 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));    //arco in basso a sx
            Float R2 = Info_C2.radius;
            PointF Centro2 = new PointF(Info_C2.center.x, Info_C2.center.y);

            //trovo intersezione degli archi in basso ristretti di M
            ArrayList<PointF> Inters_P5_ristretto = MathGeoTri.CircleStraightLineIntersection(new PointF((400F), Inter400), new PointF(0F, Inter0), Centro2, R2 - pocketValues.get("M"));

            //per scegliere l'intersezione corretta cerco quella più vicina al punto P5
            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P5_ristretto.get(0).x, Inters_P5_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P5_ristretto.get(1).x, Inters_P5_ristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P5", Inters_P5_ristretto.get(0));
            } else {
                pointsMap.put("P5", Inters_P5_ristretto.get(1));
            }
        } else if (!pointsPTS.get("P4").equals(pointsPTS.get("P5")) && pointsPTS.get("P6").equals(pointsPTS.get("P7"))) {

            //1 Arc

            CenterPointRadius Info_C1 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P3"), pointsPTS.get("P4"), pointsPTS.get("P5"));    //arco in basso a dx
            Float R1 = Info_C1.radius;
            PointF Centro1 = new PointF(Info_C1.center.x, Info_C1.center.y);

            List<Float> points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), -pocketValues.get("M"));

            float Inter400 = (points.get(2) + (400F) * points.get(1)) / points.get(0);
            float Inter0 = (points.get(2) + 0F * points.get(1)) / points.get(0);
            //trovo intersezione degli archi in basso ristretti di M
            ArrayList<PointF> Inters_P5_ristretto = MathGeoTri.CircleStraightLineIntersection(new PointF((400F), Inter400), new PointF(0F, Inter0), Centro1, R1 - pocketValues.get("M"));

            //per scegliere l'intersezione corretta cerco quella più vicina al punto P5
            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P5_ristretto.get(0).x, Inters_P5_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P5_ristretto.get(1).x, Inters_P5_ristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P5", Inters_P5_ristretto.get(0));
            } else {
                pointsMap.put("P5", Inters_P5_ristretto.get(1));
            }
        } else if (pointsPTS.get("P4").equals(pointsPTS.get("P5")) && pointsPTS.get("P6").equals(pointsPTS.get("P7"))) {

            //2 Lines

            List<Float> points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P5"), pocketValues.get("M"));
            List<Float> points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), -pocketValues.get("M"));

            PointF inter = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));
            pointsMap.put("P5", inter);
        }

        //region Backtacks-Sx
        float startx = pointsPTS.get("P1").x + pocketValues.get("M");
        float starty = pointsPTS.get("P1").y + topRightMargin_Backtack;
        float endx = pointsPTS.get("P1").x + pocketValues.get("M") + pocketValues.get("F");
        float endy = pointsPTS.get("P1").y + topRightMargin_Backtack;
        pointsMap.put("P1", new PointF(startx, starty));
        pointsMap.put("P18", new PointF(endx, endy));
        //endregion

        //region Backtacks-Dx
        startx = pointsPTS.get("P9").x - pocketValues.get("M");
        starty = pointsPTS.get("P9").y + topLeftMargin_Backtack;
        endx = pointsPTS.get("P9").x - pocketValues.get("M") - pocketValues.get("A");
        endy = pointsPTS.get("P9").y + topLeftMargin_Backtack;
        pointsMap.put("P9", new PointF(startx, starty));
        pointsMap.put("P10", new PointF(endx, endy));
        //endregion

        List<Float> points;
        List<Float> points1;

        float Sagitta = pocketValues.get("M1");

        if (Sagitta <= 1) {
            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), -pocketValues.get("M"));
            points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P9"), -pocketValues.get("M"));

            PointF inter = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

            //H1Dx
            pointsMap.put("P7", inter);
        } else {
            if (!pointsPTS.get("P6").equals(pointsPTS.get("P7"))) {
                CenterPointRadius Info_Csx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P7"), pointsPTS.get("P8"), pointsPTS.get("P9"));    //arco lato sx
                Float R3 = Info_Csx.radius;
                PointF Centro3 = new PointF(Info_Csx.center.x, Info_Csx.center.y);

                CenterPointRadius Info_C2 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));    //arco in basso a sx
                Float R2 = Info_C2.radius;
                PointF Centro2 = new PointF(Info_C2.center.x, Info_C2.center.y);

                //trovo intersezione degli archi in basso ristretti di M
                ArrayList<PointF> Inters_P7_ristretto = MathGeoTri.FindArcArcIntersections(Centro3.x, Centro3.y, R3 - pocketValues.get("M"), Centro2.x, Centro2.y, R2 - pocketValues.get("M"));

                //per scegliere l'intersezione corretta cerco quella più vicina al punto P5
                float Dist1 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P7_ristretto.get(0).x, Inters_P7_ristretto.get(0).y);
                float Dist2 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P7_ristretto.get(1).x, Inters_P7_ristretto.get(1).y);

                if (Dist1 < Dist2) {
                    pointsMap.put("P7", Inters_P7_ristretto.get(0));
                } else {
                    pointsMap.put("P7", Inters_P7_ristretto.get(1));
                }

                pointsMap.put("P8", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P7"), pointsMap.get("P9"), R3 - pocketValues.get("M"), Centro3, Sagitta));
            } else {
                CenterPointRadius Info_Csx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P7"), pointsPTS.get("P8"), pointsPTS.get("P9"));    //arco lato sx
                Float R3 = Info_Csx.radius;
                PointF Centro3 = new PointF(Info_Csx.center.x, Info_Csx.center.y);

                points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), -pocketValues.get("M"));

                // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
                float Inter400 = ((-points.get(1)) * 400F - points.get(2)) / points.get(0);
                float Inter0 = ((-points.get(1)) * 0F - points.get(2)) / points.get(0);

                //trovo intersezione degli archi in basso ristretti di M
                ArrayList<PointF> Inters_P7_ristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro3, R3 - pocketValues.get("M"));

                //per scegliere l'intersezione corretta cerco quella più vicina al punto P5
                float Dist1 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P7_ristretto.get(0).x, Inters_P7_ristretto.get(0).y);
                float Dist2 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P7_ristretto.get(1).x, Inters_P7_ristretto.get(1).y);

                if (Dist1 < Dist2) {
                    pointsMap.put("P7", Inters_P7_ristretto.get(0));
                } else {
                    pointsMap.put("P7", Inters_P7_ristretto.get(1));
                }

                pointsMap.put("P8", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P7"), pointsMap.get("P9"), R3 - pocketValues.get("M"), Centro3, Sagitta));
            }
        }

        Sagitta = pocketValues.get("M2");

        if (Sagitta <= 1) {

            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P5"), pocketValues.get("M"));

            points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P1"), pointsPTS.get("P3"), pocketValues.get("M"));

            PointF inter1 = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

            pointsMap.put("P3", inter1);
        } else {

            if (!pointsPTS.get("P4").equals(pointsPTS.get("P5"))) {
                //troviamo l'arco sotto a dx ristretto
                CenterPointRadius Info_C4 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P1"), pointsPTS.get("P2"), pointsPTS.get("P3"));    //arco dx
                Float R4 = Info_C4.radius;
                PointF Centro4 = new PointF(Info_C4.center.x, Info_C4.center.y);

                CenterPointRadius Info_C1 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P3"), pointsPTS.get("P4"), pointsPTS.get("P5"));    //arco in basso a dx
                Float R1 = Info_C1.radius;
                PointF Centro1 = new PointF(Info_C1.center.x, Info_C1.center.y);

                //trovo intersezione degli archi in basso ristretti di M
                ArrayList<PointF> Inters_P3_ristretto = MathGeoTri.FindArcArcIntersections(Centro1.x, Centro1.y, R1 - pocketValues.get("M"), Centro4.x, Centro4.y, R4 - pocketValues.get("M"));

                //per scegliere l'intersezione corretta cerco quella più vicina al punto P5
                float Dist1 = MathGeoTri.Distance(pointsPTS.get("P3").x, pointsPTS.get("P3").y, Inters_P3_ristretto.get(0).x, Inters_P3_ristretto.get(0).y);
                float Dist2 = MathGeoTri.Distance(pointsPTS.get("P3").x, pointsPTS.get("P3").y, Inters_P3_ristretto.get(1).x, Inters_P3_ristretto.get(1).y);

                if (Dist1 < Dist2) {
                    pointsMap.put("P3", Inters_P3_ristretto.get(0));
                } else {
                    pointsMap.put("P3", Inters_P3_ristretto.get(1));
                }

                pointsMap.put("P2", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P1"), pointsMap.get("P3"), R4 - pocketValues.get("M"), Centro4, Sagitta));
            } else {
                CenterPointRadius Info_C4 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P1"), pointsPTS.get("P2"), pointsPTS.get("P3"));    //arco dx
                Float R4 = Info_C4.radius;
                PointF Centro4 = new PointF(Info_C4.center.x, Info_C4.center.y);

                points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P5"), pocketValues.get("M"));

                // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
                float Inter400 = ((-points.get(1)) * 400F - points.get(2)) / points.get(0);
                float Inter0 = ((-points.get(1)) * 0F - points.get(2)) / points.get(0);

                //trovo intersezione degli archi in basso ristretti di M
                ArrayList<PointF> Inters_P3_ristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro4, R4 - pocketValues.get("M"));

                //per scegliere l'intersezione corretta cerco quella più vicina al punto P5
                float Dist1 = MathGeoTri.Distance(pointsPTS.get("P3").x, pointsPTS.get("P3").y, Inters_P3_ristretto.get(0).x, Inters_P3_ristretto.get(0).y);
                float Dist2 = MathGeoTri.Distance(pointsPTS.get("P3").x, pointsPTS.get("P3").y, Inters_P3_ristretto.get(1).x, Inters_P3_ristretto.get(1).y);


                if (Dist1 < Dist2) {
                    pointsMap.put("P3", Inters_P3_ristretto.get(0));
                } else {
                    pointsMap.put("P3", Inters_P3_ristretto.get(1));
                }

                pointsMap.put("P2", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P1"), pointsMap.get("P3"), R4 - pocketValues.get("M"), Centro4, Sagitta));
            }
        }

        Sagitta = pocketValues.get("M3");

        if (Sagitta <= 1) {

        } else {
            //troviamo l'arco sotto a sx ristretto

            CenterPointRadius Info_arco_sotto_sx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));    //arco dx     P1Im, P2Im, P3Im
            Float R_arco_sotto_sx = Info_arco_sotto_sx.radius;
            PointF Centro_sotto_sx = new PointF(Info_arco_sotto_sx.center.x, Info_arco_sotto_sx.center.y);

            //trovo P6 ristretto
            pointsMap.put("P6", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsPTS.get("P5"), pointsPTS.get("P7"), R_arco_sotto_sx - pocketValues.get("M"), Centro_sotto_sx, Sagitta)); //P1Ristretto, P5Ristretto, R4, Centro4, Sagitta
        }

        //region ArcoSx-Sx

        Sagitta = pocketValues.get("M4");

        if (Sagitta <= 1) {
        } else {
            //troviamo l'arco sotto a dx ristretto

            CenterPointRadius Info_arco_sotto_dx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P3"), pointsPTS.get("P4"), pointsPTS.get("P5"));    //arco dx basso
            Float R_arco_sotto_dx = Info_arco_sotto_dx.radius;
            PointF Centro_sotto_dx = new PointF(Info_arco_sotto_dx.center.x, Info_arco_sotto_dx.center.y);

            //trovo P4 ristretto
            pointsMap.put("P4", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P3"), pointsMap.get("P5"), R_arco_sotto_dx - pocketValues.get("M"), Centro_sotto_dx, Sagitta));
        }
        //endregion


        if (!pointsPTS.get("P4").equals(pointsPTS.get("P5")) && !pointsPTS.get("P6").equals(pointsPTS.get("P7"))) {
            CenterPointRadius Info_C1_R = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P3"), pointsPTS.get("P4"), pointsPTS.get("P5"));    //arco in alto a dx
            Float R1_R = Info_C1_R.radius;
            PointF Centro1_R = new PointF(Info_C1_R.center.x, Info_C1_R.center.y);

            CenterPointRadius Info_C2_R = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));    //arco in alto a sx
            Float R2_R = Info_C2_R.radius;
            PointF Centro2_R = new PointF(Info_C2_R.center.x, Info_C2_R.center.y);

            //trovo intersezione degli archi in basso ristretti di M
            ArrayList<PointF> Inters_P5_ristrettoristretto = MathGeoTri.FindArcArcIntersections(Centro1_R.x, Centro1_R.y, R1_R - pocketValues.get("M") - pocketValues.get("D"), Centro2_R.x, Centro2_R.y, R2_R - pocketValues.get("M") - pocketValues.get("E"));

            //per scegliere l'intersezione corretta cerco quella più vicina al punto P5
            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P5_ristrettoristretto.get(0).x, Inters_P5_ristrettoristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P5_ristrettoristretto.get(1).x, Inters_P5_ristrettoristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P14", Inters_P5_ristrettoristretto.get(0));
            } else {
                pointsMap.put("P14", Inters_P5_ristrettoristretto.get(1));
            }
        } else if (pointsPTS.get("P4").equals(pointsPTS.get("P5")) && !pointsPTS.get("P6").equals(pointsPTS.get("P7"))) {

            CenterPointRadius Info_C2_R = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));    //arco in alto a sx
            Float R2_R = Info_C2_R.radius;
            PointF Centro2_R = new PointF(Info_C2_R.center.x, Info_C2_R.center.y);

            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P5"), -pocketValues.get("M") - pocketValues.get("D"));

            float Inter400 = (points.get(2) + (400F) * points.get(1)) / points.get(0);
            float Inter0 = (points.get(2) + 0F * points.get(1)) / points.get(0);

            //trovo intersezione degli archi in basso ristretti di M
            ArrayList<PointF> Inters_P5_ristrettoristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro2_R, R2_R - pocketValues.get("M") - pocketValues.get("E"));

            //per scegliere l'intersezione corretta cerco quella più vicina al punto P5
            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P5_ristrettoristretto.get(0).x, Inters_P5_ristrettoristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P5_ristrettoristretto.get(1).x, Inters_P5_ristrettoristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P14", Inters_P5_ristrettoristretto.get(0));
            } else {
                pointsMap.put("P14", Inters_P5_ristrettoristretto.get(1));
            }
        } else if (!pointsPTS.get("P4").equals(pointsPTS.get("P5")) && pointsPTS.get("P6").equals(pointsPTS.get("P7"))) {
            CenterPointRadius Info_C1_R = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P3"), pointsPTS.get("P4"), pointsPTS.get("P5"));    //arco in alto a dx
            Float R1_R = Info_C1_R.radius;
            PointF Centro1_R = new PointF(Info_C1_R.center.x, Info_C1_R.center.y);

            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), pocketValues.get("M") + pocketValues.get("E"));

            // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
            float Inter400 = ((-points.get(1)) * 400F - points.get(2)) / points.get(0);
            float Inter0 = ((-points.get(1)) * 0F - points.get(2)) / points.get(0);

            //trovo intersezione degli archi in basso ristretti di M
            ArrayList<PointF> Inters_P5_ristrettoristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro1_R, R1_R - pocketValues.get("M") - pocketValues.get("D"));

            //per scegliere l'intersezione corretta cerco quella più vicina al punto P5
            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P5_ristrettoristretto.get(0).x, Inters_P5_ristrettoristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P5_ristrettoristretto.get(1).x, Inters_P5_ristrettoristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P14", Inters_P5_ristrettoristretto.get(0));
            } else {
                pointsMap.put("P14", Inters_P5_ristrettoristretto.get(1));
            }
        } else if (pointsPTS.get("P4").equals(pointsPTS.get("P5")) && pointsPTS.get("P6").equals(pointsPTS.get("P7"))) {

            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P5"), pocketValues.get("M") + pocketValues.get("D"));

            points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), -pocketValues.get("M") - pocketValues.get("E"));

            PointF inter = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));
            pointsMap.put("P14", inter);
        }

        //S2Top
        Sagitta = pocketValues.get("M3");

        if (Sagitta <= 1) {

            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), -pocketValues.get("M") - pocketValues.get("D"));

            points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P9"), -pocketValues.get("M") - pocketValues.get("D"));

            PointF inter = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

            pointsMap.put("P12", inter);

        } else {
            if (!pointsPTS.get("P8").equals(pointsPTS.get("P9"))) {
                CenterPointRadius Info_Csx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P7"), pointsPTS.get("P8"), pointsPTS.get("P9"));    //arco lato sx
                Float R3 = Info_Csx.radius;
                PointF Centro3 = new PointF(Info_Csx.center.x, Info_Csx.center.y);

                CenterPointRadius Info_C2 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));    //arco in basso a sx
                Float R2 = Info_C2.radius;
                PointF Centro2 = new PointF(Info_C2.center.x, Info_C2.center.y);

                //trovo intersezione degli archi in alto ristretti di M e E
                ArrayList<PointF> Inters_P7_ristrettoristretto = MathGeoTri.FindArcArcIntersections(Centro3.x, Centro3.y, R3 - pocketValues.get("M") - pocketValues.get("E"), Centro2.x, Centro2.y, R2 - pocketValues.get("M") - pocketValues.get("E"));

                //per scegliere l'intersezione corretta cerco quella più vicina al punto P7
                float Dist1 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P7_ristrettoristretto.get(0).x, Inters_P7_ristrettoristretto.get(0).y);
                float Dist2 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P7_ristrettoristretto.get(1).x, Inters_P7_ristrettoristretto.get(1).y);

                if (Dist1 < Dist2) {
                    pointsMap.put("P12", Inters_P7_ristrettoristretto.get(0));
                } else {
                    pointsMap.put("P12", Inters_P7_ristrettoristretto.get(1));
                }

                CenterPointRadius Info_Casx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));    //arco lato sx
                Float R3_asx = Info_Casx.radius;
                PointF Centro3_asx = new PointF(Info_Casx.center.x, Info_Casx.center.y);

                pointsMap.put("P13", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P14"), pointsMap.get("P12"), R3_asx - pocketValues.get("E") - pocketValues.get("M"), Centro3_asx, Sagitta));
            } else {
                CenterPointRadius Info_Casx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));    //arco lato sx
                Float R3_asx = Info_Casx.radius;
                PointF Centro3_asx = new PointF(Info_Casx.center.x, Info_Casx.center.y);

                points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P9"), pocketValues.get("M") + pocketValues.get("E"));

                // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
                float Inter400 = ((-points.get(1)) * 400F - points.get(2)) / points.get(0);
                float Inter0 = ((-points.get(1)) * 0F - points.get(2)) / points.get(0);

                //trovo intersezione degli archi in alto ristretti di M e E
                ArrayList<PointF> Inters_P7_ristrettoristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro3_asx, R3_asx - pocketValues.get("M") - pocketValues.get("E"));

                //per scegliere l'intersezione corretta cerco quella più vicina al punto P7
                float Dist1 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P7_ristrettoristretto.get(0).x, Inters_P7_ristrettoristretto.get(0).y);
                float Dist2 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P7_ristrettoristretto.get(1).x, Inters_P7_ristrettoristretto.get(1).y);

                if (Dist1 < Dist2) {
                    pointsMap.put("P12", Inters_P7_ristrettoristretto.get(0));
                } else {
                    pointsMap.put("P12", Inters_P7_ristrettoristretto.get(1));
                }

                pointsMap.put("P13", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P14"), pointsMap.get("P12"), R3_asx - pocketValues.get("E") - pocketValues.get("M"), Centro3_asx, Sagitta));
            }
        }

        //S1Top

        Sagitta = pocketValues.get("M4");

        if (Sagitta <= 1) {
            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P5"), pocketValues.get("M") + pocketValues.get("E"));

            points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P1"), pointsPTS.get("P3"), pocketValues.get("M") + pocketValues.get("E"));

            PointF inter1 = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

            pointsMap.put("P16", inter1);
        } else {
            if (!pointsPTS.get("P2").equals(pointsPTS.get("P3"))) {
                //troviamo l'arco sotto a dx ristretto

                CenterPointRadius Info_C4 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P1"), pointsPTS.get("P2"), pointsPTS.get("P3"));    //arco dx
                Float R4 = Info_C4.radius;
                PointF Centro4 = new PointF(Info_C4.center.x, Info_C4.center.y);

                CenterPointRadius Info_C1 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P3"), pointsPTS.get("P4"), pointsPTS.get("P5"));    //arco in basso a dx
                Float R1 = Info_C1.radius;
                PointF Centro1 = new PointF(Info_C1.center.x, Info_C1.center.y);

                //trovo intersezione degli archi in basso ristretti di M
                ArrayList<PointF> Inters_P3_ristrettoristretto = MathGeoTri.FindArcArcIntersections(Centro1.x, Centro1.y, R1 - pocketValues.get("M") - pocketValues.get("D"), Centro4.x, Centro4.y, R4 - pocketValues.get("M") - pocketValues.get("D"));

                //per scegliere l'intersezione corretta cerco quella più vicina al punto P5
                float Dist1 = MathGeoTri.Distance(pointsPTS.get("P3").x, pointsPTS.get("P3").y, Inters_P3_ristrettoristretto.get(0).x, Inters_P3_ristrettoristretto.get(0).y);
                float Dist2 = MathGeoTri.Distance(pointsPTS.get("P3").x, pointsPTS.get("P3").y, Inters_P3_ristrettoristretto.get(1).x, Inters_P3_ristrettoristretto.get(1).y);

                if (Dist1 < Dist2) {
                    pointsMap.put("P16", Inters_P3_ristrettoristretto.get(0));
                } else {
                    pointsMap.put("P16", Inters_P3_ristrettoristretto.get(1));
                }

                //troviamo l'arco sotto a dx ristretto

                CenterPointRadius Info_arco_sotto_dx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P3"), pointsPTS.get("P4"), pointsPTS.get("P5"));    //arco dx basso
                Float R_arco_sotto_dx = Info_arco_sotto_dx.radius;
                PointF Centro_sotto_dx = new PointF(Info_arco_sotto_dx.center.x, Info_arco_sotto_dx.center.y);

                //trovo P4 ristretto
                pointsMap.put("P15", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P16"), pointsMap.get("P14"), R_arco_sotto_dx - pocketValues.get("D") - pocketValues.get("M"), Centro_sotto_dx, Sagitta));
            } else {
                //trovo intersezione degli archi in alto ristretti di M e E
                CenterPointRadius Info_C4 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P3"), pointsPTS.get("P4"), pointsPTS.get("P5"));    //arco dx
                Float R4 = Info_C4.radius;
                PointF Centro4 = new PointF(Info_C4.center.x, Info_C4.center.y);

                points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P1"), pointsPTS.get("P3"), pocketValues.get("M") + pocketValues.get("D"));

                // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
                float Inter400 = ((-points.get(1)) * 400F - points.get(2)) / points.get(0);
                float Inter0 = ((-points.get(1)) * 0F - points.get(2)) / points.get(0);

                //trovo intersezione degli archi in basso ristretti di M
                ArrayList<PointF> Inters_P3_ristrettoristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro4, R4 - pocketValues.get("M") - pocketValues.get("D"));

                //per scegliere l'intersezione corretta cerco quella più vicina al punto P5
                float Dist1 = MathGeoTri.Distance(pointsPTS.get("P3").x, pointsPTS.get("P3").y, Inters_P3_ristrettoristretto.get(0).x, Inters_P3_ristrettoristretto.get(0).y);
                float Dist2 = MathGeoTri.Distance(pointsPTS.get("P3").x, pointsPTS.get("P3").y, Inters_P3_ristrettoristretto.get(1).x, Inters_P3_ristrettoristretto.get(1).y);


                if (Dist1 < Dist2) {
                    pointsMap.put("P16", Inters_P3_ristrettoristretto.get(0));
                } else {
                    pointsMap.put("P16", Inters_P3_ristrettoristretto.get(1));
                }

                //troviamo l'arco sotto a dx ristretto

                CenterPointRadius Info_arco_sotto_dx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P3"), pointsPTS.get("P4"), pointsPTS.get("P5"));    //arco dx basso
                Float R_arco_sotto_dx = Info_arco_sotto_dx.radius;
                PointF Centro_sotto_dx = new PointF(Info_arco_sotto_dx.center.x, Info_arco_sotto_dx.center.y);

                //trovo P4 ristretto
                pointsMap.put("P15", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P16"), pointsMap.get("P14"), R_arco_sotto_dx - pocketValues.get("D") - pocketValues.get("M"), Centro_sotto_dx, Sagitta));
            }
        }

        //region ArcoSx-Dx

        Sagitta = pocketValues.get("M2");

        if (Sagitta <= 1) {
            pointsMap.put("P18", new PointF(pointsPTS.get("P1").x + pocketValues.get("M") + pocketValues.get("F"), pointsPTS.get("P1").y + topRightMargin_Backtack));
        } else {

            if (!pointsPTS.get("P4").equals(pointsPTS.get("P5"))) {
                //troviamo l'arco sotto a dx ristretto

                CenterPointRadius Info_C4 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P1"), pointsPTS.get("P2"), pointsPTS.get("P3"));    //arco dx
                Float R4 = Info_C4.radius;
                PointF Centro4 = new PointF(Info_C4.center.x, Info_C4.center.y);

                //ho trovato P3ristretto ora ceco P1ristretto, faccio una linea orizzontale di P1 e la interseco con arco dx ristretto
                ArrayList<PointF> P1ristrettoristretto_list = MathGeoTri.CircleStraightLineIntersection(new PointF(pointsPTS.get("P9").x, pointsPTS.get("P9").y + topLeftMargin_Backtack), new PointF(pointsPTS.get("P1").x, pointsPTS.get("P1").y + topLeftMargin_Backtack), Centro4, R4 - pocketValues.get("M") - pocketValues.get("A"));

                //per scegliere l'intersezione corretta cerco quella più vicina al punto P9
                float Dist1 = MathGeoTri.Distance(pointsPTS.get("P1").x, pointsPTS.get("P1").y, P1ristrettoristretto_list.get(0).x, P1ristrettoristretto_list.get(0).y);
                float Dist2 = MathGeoTri.Distance(pointsPTS.get("P1").x, pointsPTS.get("P1").y, P1ristrettoristretto_list.get(1).x, P1ristrettoristretto_list.get(1).y);


                if (Dist1 < Dist2) {
                    pointsMap.put("P18", P1ristrettoristretto_list.get(0));
                } else {
                    pointsMap.put("P18", P1ristrettoristretto_list.get(1));
                }


                pointsMap.put("P17", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P18"), pointsMap.get("P16"), R4 - (pocketValues.get("A") + pocketValues.get("D")) / 2, Centro4, Sagitta));
            } else {
                CenterPointRadius Info_C4 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P1"), pointsPTS.get("P2"), pointsPTS.get("P3"));    //arco dx
                Float R4 = Info_C4.radius;
                PointF Centro4 = new PointF(Info_C4.center.x, Info_C4.center.y);

                //ho trovato P3ristretto ora cerco P1ristretto, faccio una linea orizzontale di P1 e la interseco con arco dx ristretto
                ArrayList<PointF> P1ristrettoristretto_list = MathGeoTri.CircleStraightLineIntersection(new PointF(pointsPTS.get("P9").x, pointsPTS.get("P9").y + topLeftMargin_Backtack), new PointF(pointsPTS.get("P1").x, pointsPTS.get("P1").y + topLeftMargin_Backtack), Centro4, R4 - pocketValues.get("M") - pocketValues.get("A"));

                //per scegliere l'intersezione corretta cerco quella più vicina al punto P9
                float Dist1 = MathGeoTri.Distance(pointsPTS.get("P1").x, pointsPTS.get("P1").y, P1ristrettoristretto_list.get(0).x, P1ristrettoristretto_list.get(0).y);
                float Dist2 = MathGeoTri.Distance(pointsPTS.get("P1").x, pointsPTS.get("P1").y, P1ristrettoristretto_list.get(1).x, P1ristrettoristretto_list.get(1).y);


                if (Dist1 < Dist2) {
                    pointsMap.put("P18", P1ristrettoristretto_list.get(0));
                } else {
                    pointsMap.put("P18", P1ristrettoristretto_list.get(1));
                }

                pointsMap.put("P17", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P18"), pointsMap.get("P16"), R4 - (pocketValues.get("A") + pocketValues.get("D")) / 2 - pocketValues.get("M"), Centro4, Sagitta));
            }
        }
        //endregion

        //region ArcoDx-Sx

        Sagitta = pocketValues.get("M1");

        if (Sagitta <= 1) {
            pointsMap.put("P10", new PointF(pointsPTS.get("P9").x - pocketValues.get("M") - pocketValues.get("A"), pointsPTS.get("P9").y + topLeftMargin_Backtack));
        } else {
            if (!pointsPTS.get("P6").equals(pointsPTS.get("P7"))) {
                CenterPointRadius Info_Csx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P7"), pointsPTS.get("P8"), pointsPTS.get("P9"));    //arco lato sx
                Float R3 = Info_Csx.radius;
                PointF Centro3 = new PointF(Info_Csx.center.x, Info_Csx.center.y);

                //ho trovato P7ristretto ora ceco P9ristretto, faccio una linea orizzontale di P9 e la interseco con arco sx ristretto
                ArrayList<PointF> P9ristrettoristretto_list = MathGeoTri.CircleStraightLineIntersection(new PointF(pointsPTS.get("P9").x, pointsPTS.get("P9").y + topRightMargin_Backtack), new PointF(pointsPTS.get("P1").x, pointsPTS.get("P1").y + topRightMargin_Backtack), Centro3, R3 - pocketValues.get("M") - pocketValues.get("F"));

                //per scegliere l'intersezione corretta cerco quella più vicina al punto P9
                float Dist1 = MathGeoTri.Distance(pointsPTS.get("P9").x, pointsPTS.get("P9").y, P9ristrettoristretto_list.get(0).x, P9ristrettoristretto_list.get(0).y);
                float Dist2 = MathGeoTri.Distance(pointsPTS.get("P9").x, pointsPTS.get("P9").y, P9ristrettoristretto_list.get(1).x, P9ristrettoristretto_list.get(1).y);

                if (Dist1 < Dist2) {
                    pointsMap.put("P10", P9ristrettoristretto_list.get(0));
                } else {
                    pointsMap.put("P10", P9ristrettoristretto_list.get(1));
                }

                pointsMap.put("P11", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P12"), pointsMap.get("P10"), R3 - (pocketValues.get("F") + pocketValues.get("E")) / 2 - pocketValues.get("M"), Centro3, Sagitta));
            } else {
                CenterPointRadius Info_Csx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P7"), pointsPTS.get("P8"), pointsPTS.get("P9"));    //arco lato sx
                Float R3 = Info_Csx.radius;
                PointF Centro3 = new PointF(Info_Csx.center.x, Info_Csx.center.y);

                //ho trovato P7ristretto ora ceco P9ristretto, faccio una linea orizzontale di P9 e la interseco con arco sx ristretto
                ArrayList<PointF> P9ristrettoristretto_list = MathGeoTri.CircleStraightLineIntersection(new PointF(pointsPTS.get("P9").x, pointsPTS.get("P9").y + topRightMargin_Backtack), new PointF(pointsPTS.get("P1").x, pointsPTS.get("P1").y + topRightMargin_Backtack), Centro3, R3 - pocketValues.get("M") - pocketValues.get("F"));

                //per scegliere l'intersezione corretta cerco quella più vicina al punto P9
                float Dist1 = MathGeoTri.Distance(pointsPTS.get("P9").x, pointsPTS.get("P9").y, P9ristrettoristretto_list.get(0).x, P9ristrettoristretto_list.get(0).y);
                float Dist2 = MathGeoTri.Distance(pointsPTS.get("P9").x, pointsPTS.get("P9").y, P9ristrettoristretto_list.get(1).x, P9ristrettoristretto_list.get(1).y);

                if (Dist1 < Dist2) {
                    pointsMap.put("P10", P9ristrettoristretto_list.get(0));
                } else {
                    pointsMap.put("P10", P9ristrettoristretto_list.get(1));
                }

                pointsMap.put("P11", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P12"), pointsMap.get("P10"), R3 - (pocketValues.get("F") + pocketValues.get("E")) / 2 - pocketValues.get("M"), Centro3, Sagitta));
            }
        }
        //endregion

        return pointsMap;
    }

    /**
     * Function for calculate the 2 Ricetta from the given points
     *
     * @param type
     * @param points
     * @param pocketValues
     * @return
     */
    public static Pair<Ricetta, Ricetta> Model_0_Ricetta(int type, TreeMap<String, PointF> points, HashMap<String, Float> pocketValues) {
        Ricetta ricettaResult = new Ricetta(Values.plcType);
        Ricetta ricetta1Result = new Ricetta(Values.plcType);
        //Default Start Draw Position
        ricettaResult.setDrawPosition(new PointF(0.1f, 0.1f));
        ricetta1Result.setDrawPosition(new PointF(0.1f, 0.1f));

        if (type == 1) {

            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    }
                }
                break;
            }

            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P2"), points.get("P3"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P4"), points.get("P5"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P8"), points.get("P9"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));

            ricettaResult.drawZigZagTo(points.get("P10"), pocketValues.get("G"), pocketValues.get("H"));

            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P11"), points.get("P12"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P13"), points.get("P14"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P14"), pocketValues.get("LP"));
            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P15"), points.get("P16"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P16"), pocketValues.get("LP"));
            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P17"), points.get("P18"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P18"), pocketValues.get("LP"));

            ricettaResult.drawZigZagTo(points.get("P1"), pocketValues.get("B"), pocketValues.get("C"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalZigZagFermatura(ricettaResult, pocketValues.get("C"), pocketValues.get("B"), Values.pFf1, Values.Ff1t, points.get("P1"), true, oneMorePoint, false);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, line);
                    }
                }
                break;
            }
        } else if (type == 2) {

            switch (Values.Fi1) {
                case 0: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    }
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P2"), points.get("P3"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P4"), points.get("P5"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P8"), points.get("P9"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));

            ricettaResult.drawLineTo(points.get("P10"), pocketValues.get("LP"));

            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P11"), points.get("P12"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P13"), points.get("P14"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P14"), pocketValues.get("LP"));
            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P15"), points.get("P16"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P16"), pocketValues.get("LP"));
            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P17"), points.get("P18"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P18"), pocketValues.get("LP"));

            ricettaResult.drawLineTo(points.get("P1"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, line);
                    }
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.F, points.get("P1"), true, oneMorePoint, false);
                }
                break;
            }
        } else if (type == 3) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    }
                }
                break;
            }

            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P2"), points.get("P3"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P4"), points.get("P5"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P8"), points.get("P9"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.F, points.get("P9"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P9");
                        arc.pMiddle = points.get("P8");
                        arc.pEnd = points.get("P7");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P9"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P9");
                        line.pEnd = points.get("P7");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P9"), oneMorePoint, false, line);
                    }
                }
                break;
            }

            switch (Values.Fi2) {
                case 0: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P1"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P1"), oneMorePoint, true, line);
                    }
                }
                break;
            }

            ricetta1Result.drawZigZagTo(points.get("P18"), pocketValues.get("G"), pocketValues.get("H"));
            if (pocketValues.get("M2") > 1)
                ricetta1Result.drawArcTo(points.get("P17"), points.get("P16"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P16"), pocketValues.get("LP"));
            if (pocketValues.get("M4") > 1)
                ricetta1Result.drawArcTo(points.get("P15"), points.get("P14"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P14"), pocketValues.get("LP"));
            if (pocketValues.get("M3") > 1)
                ricetta1Result.drawArcTo(points.get("P13"), points.get("P12"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            if (pocketValues.get("M1") > 1)
                ricetta1Result.drawArcTo(points.get("P11"), points.get("P10"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            ricetta1Result.drawZigZagTo(points.get("P9"), pocketValues.get("B"), pocketValues.get("C"));

            switch (Values.Ff2) {
                case 0: {
                    ricetta1Result = HorizontalZigZagFermatura(ricetta1Result, pocketValues.get("C"), pocketValues.get("B"), Values.pFf2, Values.Ff2t, points.get("P9"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P9");
                        arc.pMiddle = points.get("P8");
                        arc.pEnd = points.get("P7");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P9"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P9");
                        line.pEnd = points.get("P7");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P9"), oneMorePoint, false, line);
                    }
                }
                break;
            }
        } else if (type == 4) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P18"), false, oneMorePoint, true);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P18");
                        arc.pMiddle = points.get("P17");
                        arc.pEnd = points.get("P16");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P18"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P18");
                        line.pEnd = points.get("P16");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P18"), oneMorePoint, true, line);
                    }
                }
                break;
            }

            ricettaResult.drawZigZagTo(points.get("P1"), pocketValues.get("G"), pocketValues.get("H"));
            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P2"), points.get("P3"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P4"), points.get("P5"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P8"), points.get("P9"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            ricettaResult.drawZigZagTo(points.get("P10"), pocketValues.get("B"), pocketValues.get("C"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalZigZagFermatura(ricettaResult, pocketValues.get("C"), pocketValues.get("B"), Values.pFf1, Values.Ff1t, points.get("P10"), true, oneMorePoint, false);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P10");
                        arc.pMiddle = points.get("P11");
                        arc.pEnd = points.get("P12");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P10"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P10");
                        line.pEnd = points.get("P12");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P10"), oneMorePoint, false, line);
                    }
                }
                break;
            }

            switch (Values.Fi2) {
                case 0:
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P18");
                        arc.pMiddle = points.get("P17");
                        arc.pEnd = points.get("P16");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P18"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P18");
                        line.pEnd = points.get("P16");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P18"), oneMorePoint, true, line);
                    }
                }
                break;
            }

            if (pocketValues.get("M2") > 1)
                ricetta1Result.drawArcTo(points.get("P17"), points.get("P16"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P16"), pocketValues.get("LP"));
            if (pocketValues.get("M4") > 1)
                ricetta1Result.drawArcTo(points.get("P15"), points.get("P14"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P14"), pocketValues.get("LP"));
            if (pocketValues.get("M3") > 1)
                ricetta1Result.drawArcTo(points.get("P13"), points.get("P12"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            if (pocketValues.get("M1") > 1)
                ricetta1Result.drawArcTo(points.get("P11"), points.get("P10"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P10"), pocketValues.get("LP"));

            switch (Values.Ff2) {
                case 0:
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P10");
                        arc.pMiddle = points.get("P11");
                        arc.pEnd = points.get("P12");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P10"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P10");
                        line.pEnd = points.get("P12");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P10"), oneMorePoint, false, line);
                    }
                }
                break;
            }
        } else if (type == 5) {
            switch (Values.Fi1) {
                case 0: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    }
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P2"), points.get("P3"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P4"), points.get("P5"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P8"), points.get("P9"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    if (pocketValues.get("M1") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P9");
                        arc.pMiddle = points.get("P8");
                        arc.pEnd = points.get("P7");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P9"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P9");
                        line.pEnd = points.get("P7");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P9"), oneMorePoint, false, line);
                    }
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.F, points.get("P9"), false, oneMorePoint, false);
                }
                break;
            }

            switch (Values.Fi2) {
                case 0: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P18");
                        arc.pMiddle = points.get("P17");
                        arc.pEnd = points.get("P16");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P18"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P18");
                        line.pEnd = points.get("P16");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P18"), oneMorePoint, true, line);
                    }
                }
                break;
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P18"), false, oneMorePoint, true);
                }
                break;
            }

            if (pocketValues.get("M2") > 1)
                ricetta1Result.drawArcTo(points.get("P17"), points.get("P16"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P16"), pocketValues.get("LP"));
            if (pocketValues.get("M4") > 1)
                ricetta1Result.drawArcTo(points.get("P15"), points.get("P14"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P14"), pocketValues.get("LP"));
            if (pocketValues.get("M3") > 1)
                ricetta1Result.drawArcTo(points.get("P13"), points.get("P12"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            if (pocketValues.get("M1") > 1)
                ricetta1Result.drawArcTo(points.get("P11"), points.get("P10"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P10"), pocketValues.get("LP"));

            switch (Values.Ff2) {
                case 0: {
                    if (pocketValues.get("M1") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P10");
                        arc.pMiddle = points.get("P11");
                        arc.pEnd = points.get("P12");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P10"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P10");
                        line.pEnd = points.get("P12");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P10"), oneMorePoint, false, line);
                    }
                }
                break;
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Ff2t, Values.F, points.get("P10"), true, oneMorePoint, false);
                }
                break;
            }
        } else if (type == 6) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    }
                }
                break;
            }

            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P2"), points.get("P3"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P4"), points.get("P5"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P8"), points.get("P9"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));

            ricettaResult.drawFeedTo(points.get("P10"));

            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P11"), points.get("P12"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P13"), points.get("P14"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P14"), pocketValues.get("LP"));
            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P15"), points.get("P16"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P16"), pocketValues.get("LP"));
            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P17"), points.get("P18"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P18"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.A, points.get("P18"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P18");
                        arc.pMiddle = points.get("P17");
                        arc.pEnd = points.get("P16");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P18"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P18");
                        line.pEnd = points.get("P16");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P18"), oneMorePoint, false, line);
                    }
                }
                break;
            }

            switch (Values.Fi2) {
                case 0:
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            ricetta1Result.drawZigZagTo(points.get("P18"), pocketValues.get("G"), pocketValues.get("H"));
            ricetta1Result.drawFeedTo(points.get("P10"));
            ricetta1Result.drawZigZagTo(points.get("P9"), pocketValues.get("B"), pocketValues.get("C"));

            switch (Values.Ff2) {
                case 0:
                case 1: {
                    ricetta1Result = HorizontalZigZagFermatura(ricetta1Result, pocketValues.get("C"), pocketValues.get("B"), Values.pFf2, Values.Ff2t, points.get("P9"), false, oneMorePoint, false);
                }
                break;
            }
        } else if (type == 7) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    }
                }
                break;
            }

            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P2"), points.get("P3"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P4"), points.get("P5"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P8"), points.get("P9"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));

            ricettaResult.drawFeedTo(points.get("P10"));

            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P11"), points.get("P12"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P13"), points.get("P14"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P14"), pocketValues.get("LP"));
            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P15"), points.get("P16"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P16"), pocketValues.get("LP"));
            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P17"), points.get("P18"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P18"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P18"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P18");
                        arc.pMiddle = points.get("P17");
                        arc.pEnd = points.get("P16");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P18"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P18");
                        line.pEnd = points.get("P16");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P18"), oneMorePoint, false, line);
                    }
                }
                break;
            }
        } else if (type == 8) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    }
                }
                break;
            }

            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P2"), points.get("P3"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P4"), points.get("P5"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P8"), points.get("P9"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.F, points.get("P9"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P9");
                        arc.pMiddle = points.get("P8");
                        arc.pEnd = points.get("P7");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P9"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P9");
                        line.pEnd = points.get("P7");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P9"), oneMorePoint, false, line);
                    }
                }
                break;
            }

            ricettaResult.drawFeedTo(new PointF(points.get("P9").x, points.get("P5").y));
            ricettaResult.drawFeedTo(new PointF(points.get("P1").x, points.get("P5").y));
            ricettaResult.drawFeedTo(new PointF(points.get("P1").x, points.get("P1").y));
        }

        return new Pair<>(ricettaResult, ricetta1Result);
    }

    /**
     * Function for add codes on the 2 Ricetta
     *
     * @param type
     * @param ricette
     * @return
     */
    public static Pair<Ricetta, Ricetta> Model_0_Codes(int type, Pair<Ricetta, Ricetta> ricette) {
        if (type == 1 || type == 2) {

            /**************************
             * OP2 and OP1 codes
             ***************************/

            // Set OP2 ON the line before the backtack
            try {
                if (Values.OP2On >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP2On == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            // Set OP1 ON on backtack start
            try {
                if (Values.OP1On >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP1On == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            // Set OP2 OFF on backtack
            try {
                if (Values.OP2Off >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP2Off == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************
             * Speed1 and OP3 codes
             ***************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            /*****************************************************/
            // Set Speed1 OFF after start backtak (fast pocket)
            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.Numeric, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(11 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(11 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.Numeric, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(11 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(11 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.Numeric, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(11 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(11 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(11 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(11 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            /*****************************************************/
            // Set Speed1 ON before second backtack (slow backtack)
            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            /*****************************************************/
            // Set Speed1 OFF after second backtack (fast pocket)
            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            /*****************************************************/
            // Set Speed1 ON at first backtack, end part (slow backtack)
            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(11 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(10 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(10 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(10 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(10 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(11 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(10 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(10 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(10 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(10 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        } else if (type == 3) {

            /***************************************
             * Speed1 and OP3 codes on first pocket
             ****************************************/

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF at first line after first backtack
            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON at line before second backtack
            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /***************************************
             * Speed1 and OP3 codes on second pocket
             ****************************************/

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 OFF at line after first backtack
            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.second.elements.get(3 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.second.elements.get(3 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            /*****************************************************/
            // Set SPEED1 ON at line before second backtack
            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 > 0) {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


        } else if (type == 4) {

            /***************************************
             * Speed1 and OP3 codes on first pocket
             ****************************************/

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 OFF at line after first backtack
            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(3 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(3 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 ON at line after second backtack
            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            /***************************************
             * Speed1 and OP3 codes on second pocket
             ****************************************/

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 OFF at line after backtack

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 ON at line before second backtack

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.get(Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.get(Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        } else if (type == 5) {

            /***************************************
             * Speed1 and OP3 codes on first pocket
             ****************************************/

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 OFF at line after first backtack

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 ON at line before second backtack

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            /***************************************
             * Speed1 and OP3 codes on second pocket
             ****************************************/

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 OFF at line after first backtack

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(-Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(-Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 ON at line before second backtack

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.get(Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.get(Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (type == 6) {

            /**************************
             * OP2 and OP1 codes
             ***************************/

            try {
                if (Values.OP2On >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP2On == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            // TODO Questo codice va sul feed quindi va a finire a fine feed
            try {
                if (Values.OP1On >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP1On == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            // TODO Questo codice va sul feed quindi non viene messo
            try {
                if (Values.OP2Off >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP2Off == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /***************************************
             * Speed1 and OP3 codes on first pocket
             ****************************************/

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 OFF at line after first backtack

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 ON at line before second backtack

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED OFF at line after second backtack

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 ON at line before first backtack

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(11 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(10 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(10 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(10 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(10 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(11 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(10 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(10 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(10 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(10 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /***************************************
             * Speed1 and OP3 codes on first pocket
             ****************************************/

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        return ricette;
    }




    /*

    // A lot of points here are useless because there will be no arcs

                                        Model 1_2 Shell:

                P9  ---------------------------------------------------------   P1
                    \                                                       /
                     \                                                     /
                      \                                                   /
                       \                                                 /
                        \                                               /
                         \                                             /
                      P8  \                                           /  P2
                           \                                         /
                            \                                       /
                             \                                     /
                              \                                   /
                               \                                 /
                                \                               /
                             P7  -------------------------------  P3
                                       P6       P5       P4

                                         Model 1_2 Draw:

                    M + (1/2 H travetta)
                       |
                       |
                P5  ------   P6                                   P14  ------   P1
                    \     (                                           )     /
                     \     (                                         )     /
                      \     (  P7                              P13  )     /
                       \     (                                     )     /-/ --- M
                        \     (                                   )     /
                         \     (                                 )     /
                          \     \  P8                      P12  /     /
                           \     \                             /     /
                            \     \                           /     /
                             \     \                         /     /
                              \     \  P9      P10      P11 /     /
                               \     \_____________________/     /
                                \                               /
                             P4  -------------------------------  P2
                                                P3

     */

    /**
     * Function for calculate the draw points from the shell points
     *
     * @param type
     * @param fileName
     * @param pocketValues
     * @return
     */
    public static TreeMap<String, PointF> Model_1_2_Points(int type, String fileName, HashMap<String, Float> pocketValues) {
        TreeMap<String, PointF> pointsMap = new TreeMap();

        HashMap<String, PointF> pointsPTS = ReadPointsFromPTS(fileName);

        float topLeftMargin_Backtack = pocketValues.get("M") + pocketValues.get("B") / 2;
        float topRightMargin_Backtack = pocketValues.get("M") + pocketValues.get("G") / 2;
        if (type == 9 || type == 10) {
            topLeftMargin_Backtack = pocketValues.get("M");
            topRightMargin_Backtack = pocketValues.get("M");
        }


        //region Backtacks-Sx
        float startx = pointsPTS.get("P1").x + pocketValues.get("M");
        float starty = pointsPTS.get("P1").y + topRightMargin_Backtack;
        float endx = pointsPTS.get("P1").x + pocketValues.get("M") + pocketValues.get("F");
        float endy = pointsPTS.get("P1").y + topRightMargin_Backtack;
        pointsMap.put("P1", new PointF(startx, starty));
        pointsMap.put("P14", new PointF(endx, endy));
        //endregion

        //region Backtacks-Dx
        startx = pointsPTS.get("P9").x - pocketValues.get("M");
        starty = pointsPTS.get("P9").y + topLeftMargin_Backtack;
        endx = pointsPTS.get("P9").x - pocketValues.get("M") - pocketValues.get("A");
        endy = pointsPTS.get("P9").y + topLeftMargin_Backtack;
        pointsMap.put("P5", new PointF(startx, starty));
        pointsMap.put("P6", new PointF(endx, endy));
        //endregion

        //region interH1DxS2Bot
        List<Float> points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), -pocketValues.get("M"));

        List<Float> points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P9"), pointsPTS.get("P7"), -pocketValues.get("M"));

        PointF H1S1 = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

        //H1Dx
        pointsMap.put("P4", new PointF(H1S1.x, Math.abs(H1S1.y)));
        //endregion

        //region interH2SxS1Bot
        points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P5"), pocketValues.get("M"));

        points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P1"), pointsPTS.get("P3"), pocketValues.get("M"));

        PointF H2S2 = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

        //H2Sx
        pointsMap.put("P2", H2S2);
        //endregion

        //region interS1BotS2Bot
        points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P3"), pocketValues.get("M"));

        points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P5"), -pocketValues.get("M"));

        PointF S2S1 = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

        pointsMap.put("P3", S2S1);
        //endregion

        //region interS2TopH1Sx
        points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), -pocketValues.get("M") - pocketValues.get("D"));

        points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P9"), pointsPTS.get("P7"), -pocketValues.get("M") - pocketValues.get("D"));

        H1S1 = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));
        //endregion

        //region interH1SxArcDx
        // See model_3 bottom line for info
        if (pointsPTS.get("P9").y < pointsPTS.get("P1").y) {
            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P9"), pointsPTS.get("P1"), pocketValues.get("I"));
        } else {
            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P9"), pointsPTS.get("P1"), -pocketValues.get("I"));
        }
        points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P9"), pointsPTS.get("P7"), -pocketValues.get("M"));

        PointF H1Top = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

        pointsMap.put("P8", new PointF(H1Top.x - pocketValues.get("Lm"), H1Top.y));
        pointsMap.put("P9", H1S1);
        //endregion

        //region interS1TopH2Dx
        points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P5"), pocketValues.get("M") + pocketValues.get("E"));

        points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P1"), pointsPTS.get("P3"), pocketValues.get("M") + pocketValues.get("E"));

        H2S2 = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));
        //endregion

        //region interH2DxArcSx
        // See model_3 bottom line for info
        if (pointsPTS.get("P9").y < pointsPTS.get("P1").y) {
            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P9"), pointsPTS.get("P1"), pocketValues.get("I"));
        } else {
            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P9"), pointsPTS.get("P1"), -pocketValues.get("I"));
        }

        points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P1"), pocketValues.get("M"));

        PointF H2Top = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

        pointsMap.put("P12", new PointF(H2Top.x + pocketValues.get("Lm"), H2Top.y));
        pointsMap.put("P11", H2S2);
        //endregion

        //region interS2S1Top
        points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P5"), pocketValues.get("M") + pocketValues.get("E"));

        points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), -pocketValues.get("M") - pocketValues.get("D"));

        PointF S2S1Top = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

        pointsMap.put("P10", S2S1Top);

        {
            float Lunghezza_corda = MathGeoTri.Distance(pointsMap.get("P6"), pointsMap.get("P8"));
            float raggio = ((pocketValues.get("O") * pocketValues.get("O")) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * pocketValues.get("O"));

            PointF Centro_arco = MathGeoTri.CalculateCenterPoint(pointsMap.get("P6"), pointsMap.get("P8"), raggio, false);

            pointsMap.put("P7", MathGeoTri.CalculateArcThirdPoint(pointsMap.get("P6"), pointsMap.get("P8"), raggio, Centro_arco, false));
        }

        {
            float Lunghezza_corda = MathGeoTri.Distance(pointsMap.get("P14"), pointsMap.get("P12"));
            float raggio = ((pocketValues.get("N") * pocketValues.get("N")) + (Lunghezza_corda / 2 * Lunghezza_corda / 2)) / (2 * pocketValues.get("N"));

            PointF Centro_arco = MathGeoTri.CalculateCenterPoint(pointsMap.get("P14"), pointsMap.get("P12"), raggio, true);

            pointsMap.put("P13", MathGeoTri.CalculateArcThirdPoint(pointsMap.get("P14"), pointsMap.get("P12"), raggio, Centro_arco, true));
        }
        //endregion

        return pointsMap;
    }

    /**
     * Function for calculate the 2 Ricetta from the given points
     *
     * @param type
     * @param points
     * @param pocketValues
     * @return
     */
    public static Pair<Ricetta, Ricetta> Model_1_2_Ricetta(int type, TreeMap<String, PointF> points, HashMap<String, Float> pocketValues) {
        Ricetta ricettaResult = new Ricetta(Values.plcType);
        Ricetta ricetta1Result = new Ricetta(Values.plcType);
        //Default Start Draw Position
        ricettaResult.setDrawPosition(new PointF(0.1f, 0.1f));
        ricetta1Result.setDrawPosition(new PointF(0.1f, 0.1f));

        if (type == 1) {

            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            ricettaResult.drawZigZagTo(points.get("P6"), pocketValues.get("B"), pocketValues.get("C"));
            ricettaResult.drawArcTo(points.get("P7"), points.get("P8"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P11"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            ricettaResult.drawArcTo(points.get("P13"), points.get("P14"), pocketValues.get("LP"));
            ricettaResult.drawZigZagTo(points.get("P1"), pocketValues.get("G"), pocketValues.get("H"));

            switch (Values.Ff1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, line);
                    //}
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.A, points.get("P1"), true, oneMorePoint, false);
                }
                break;
            }
        } else if (type == 2) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            ricettaResult.drawZigZagTo(points.get("P6"), pocketValues.get("B"), pocketValues.get("C"));
            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P11"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P14"), pocketValues.get("LP"));
            ricettaResult.drawZigZagTo(points.get("P1"), pocketValues.get("G"), pocketValues.get("H"));

            switch (Values.Ff1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, line);
                    //}
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.A, points.get("P1"), true, oneMorePoint, false);
                }
                break;
            }
        } else if (type == 3) {
            switch (Values.Fi1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P6"), pocketValues.get("LP"));
            ricettaResult.drawArcTo(points.get("P7"), points.get("P8"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P11"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            ricettaResult.drawArcTo(points.get("P13"), points.get("P14"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P1"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, line);
                    // }
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.A, points.get("P1"), true, oneMorePoint, false);
                }
                break;
            }
        } else if (type == 4) {
            switch (Values.Fi1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P6"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P11"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P14"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P1"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, line);
                    //}
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.A, points.get("P1"), true, oneMorePoint, false);
                }
                break;
            }
        } else if (type == 5) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.F, points.get("P5"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P5"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P5");
                    line.pEnd = points.get("P4");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P5"), oneMorePoint, false, line);
                    //}
                }
                break;
            }

            switch (Values.Fi2) {
                case 0: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P14");
                        arc.pMiddle = points.get("P13");
                        arc.pEnd = points.get("P12");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
            }


            ricetta1Result.drawZigZagTo(points.get("P14"), pocketValues.get("G"), pocketValues.get("H"));
            ricetta1Result.drawArcTo(points.get("P13"), points.get("P12"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P11"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricetta1Result.drawArcTo(points.get("P7"), points.get("P6"), pocketValues.get("LP"));
            ricetta1Result.drawZigZagTo(points.get("P5"), pocketValues.get("B"), pocketValues.get("C"));

            switch (Values.Ff2) {
                case 0: {
                    ricetta1Result = HorizontalZigZagFermatura(ricetta1Result, pocketValues.get("C"), pocketValues.get("B"), Values.pFf2, Values.Ff2t, points.get("P5"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P5"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P5");
                    line.pEnd = points.get("P4");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P5"), oneMorePoint, false, line);
                    //}
                }
                break;
            }
        } else if (type == 6) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.F, points.get("P5"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P5"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P5");
                    line.pEnd = points.get("P4");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P5"), oneMorePoint, false, line);
                    //}
                }
                break;
            }

            switch (Values.Fi2) {
                case 0: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricetta1Result.drawZigZagTo(points.get("P14"), pocketValues.get("G"), pocketValues.get("H"));
            ricetta1Result.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P11"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P6"), pocketValues.get("LP"));
            ricetta1Result.drawZigZagTo(points.get("P5"), pocketValues.get("B"), pocketValues.get("C"));

            switch (Values.Ff2) {
                case 0: {
                    ricetta1Result = HorizontalZigZagFermatura(ricetta1Result, pocketValues.get("C"), pocketValues.get("B"), Values.pFf2, Values.Ff2t, points.get("P5"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P5"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P5");
                    line.pEnd = points.get("P4");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P5"), oneMorePoint, false, line);
                    //}
                }
                break;
            }
        } else if (type == 7) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P14"), false, oneMorePoint, true);
                }
                break;
                case 1: {
                    //if (pocketValues.get("M2") > 1) {
                    ElementArc arc = new ElementArc();
                    arc.pStart = points.get("P14");
                    arc.pMiddle = points.get("P13");
                    arc.pEnd = points.get("P12");
                    arc.passo = pocketValues.get("LP");
                    arc.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P14"), oneMorePoint, true, arc);
                    /*}
                    else
                    {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P14");
                        line.pEnd = points.get("P13");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P14"), oneMorePoint, true, line);
                    }*/
                }
                break;
            }

            ricettaResult.drawZigZagTo(points.get("P1"), pocketValues.get("G"), pocketValues.get("H"));
            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            ricettaResult.drawZigZagTo(points.get("P6"), pocketValues.get("B"), pocketValues.get("C"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalZigZagFermatura(ricettaResult, pocketValues.get("C"), pocketValues.get("B"), Values.pFf1, Values.Ff1t, points.get("P6"), true, oneMorePoint, false);
                }
                break;
                case 1: {
                    //if (pocketValues.get("M2") > 1) {
                    ElementArc arc = new ElementArc();
                    arc.pStart = points.get("P6");
                    arc.pMiddle = points.get("P7");
                    arc.pEnd = points.get("P8");
                    arc.passo = pocketValues.get("LP");
                    arc.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P6"), oneMorePoint, false, arc);
                    /*}
                    else
                    {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P6");
                        line.pEnd = points.get("P7");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P6"), oneMorePoint, false, line);
                    }*/
                }
                break;
            }

            switch (Values.Fi2) {
                case 0:
                case 1: {
                    //if (pocketValues.get("M2") > 1) {
                    ElementArc arc = new ElementArc();
                    arc.pStart = points.get("P14");
                    arc.pMiddle = points.get("P13");
                    arc.pEnd = points.get("P12");
                    arc.passo = pocketValues.get("LP");
                    arc.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P14"), oneMorePoint, true, arc);
                    /*}
                    else
                    {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P14");
                        line.pEnd = points.get("P");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P14"), oneMorePoint, true, line);
                    }*/
                }
                break;
            }

            ricetta1Result.drawArcTo(points.get("P13"), points.get("P12"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P11"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricetta1Result.drawArcTo(points.get("P7"), points.get("P6"), pocketValues.get("LP"));

            switch (Values.Ff2) {
                case 0:
                case 1: {
                    //if (pocketValues.get("M2") > 1) {
                    ElementArc arc = new ElementArc();
                    arc.pStart = points.get("P6");
                    arc.pMiddle = points.get("P7");
                    arc.pEnd = points.get("P8");
                    arc.passo = pocketValues.get("LP");
                    arc.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P6"), oneMorePoint, false, arc);
                    /*}
                    else
                    {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P6"), oneMorePoint, false, line);
                    }*/
                }
                break;
            }
        } else if (type == 8) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P14"), false, oneMorePoint, true);
                }
                break;
                case 1: {
                    //if (pocketValues.get("M2") > 1) {
                    ElementArc arc = new ElementArc();
                    arc.pStart = points.get("P14");
                    arc.pMiddle = points.get("P13");
                    arc.pEnd = points.get("P12");
                    arc.passo = pocketValues.get("LP");
                    arc.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P14"), oneMorePoint, true, arc);
                    /*}
                    else
                    {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P14"), oneMorePoint, true, line);
                    }*/
                }
                break;
            }

            ricettaResult.drawZigZagTo(points.get("P1"), pocketValues.get("G"), pocketValues.get("H"));
            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            ricettaResult.drawZigZagTo(points.get("P6"), pocketValues.get("B"), pocketValues.get("C"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalZigZagFermatura(ricettaResult, pocketValues.get("C"), pocketValues.get("B"), Values.pFf1, Values.Ff1t, points.get("P6"), true, oneMorePoint, false);
                }
                break;
                case 1: {
                    //if (pocketValues.get("M2") > 1) {
                    ElementArc arc = new ElementArc();
                    arc.pStart = points.get("P6");
                    arc.pMiddle = points.get("P7");
                    arc.pEnd = points.get("P8");
                    arc.passo = pocketValues.get("LP");
                    arc.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P6"), oneMorePoint, false, arc);
                    /*}
                    else
                    {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P6"), oneMorePoint, false, line);
                    }*/
                }
                break;
            }

            switch (Values.Fi2) {
                case 0:
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P14"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P14");
                    line.pEnd = points.get("P12");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P14"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricetta1Result.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P11"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P6"), pocketValues.get("LP"));

            switch (Values.Ff2) {
                case 0:
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P6"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P6");
                    line.pEnd = points.get("P8");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P6"), oneMorePoint, false, line);
                    //}
                }
                break;
            }
        } else if (type == 9) {
            switch (Values.Fi1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P5"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P5");
                    line.pEnd = points.get("P4");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P5"), oneMorePoint, false, line);
                    //}
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.F, points.get("P5"), false, oneMorePoint, false);
                }
                break;
            }

            switch (Values.Fi2) {
                case 0: {
                    //if (pocketValues.get("M2") > 1) {
                    ElementArc arc = new ElementArc();
                    arc.pStart = points.get("P14");
                    arc.pMiddle = points.get("P13");
                    arc.pEnd = points.get("P12");
                    arc.passo = pocketValues.get("LP");
                    arc.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P14"), oneMorePoint, true, arc);
                    /*}
                    else
                    {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P14"), oneMorePoint, true, line);
                    }*/
                }
                break;
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P14"), false, oneMorePoint, true);
                }
                break;
            }

            ricetta1Result.drawArcTo(points.get("P13"), points.get("P12"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P11"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricetta1Result.drawArcTo(points.get("P7"), points.get("P6"), pocketValues.get("LP"));

            switch (Values.Ff2) {
                case 0: {
                    //if (pocketValues.get("M2") > 1) {
                    ElementArc arc = new ElementArc();
                    arc.pStart = points.get("P6");
                    arc.pMiddle = points.get("P7");
                    arc.pEnd = points.get("P8");
                    arc.passo = pocketValues.get("LP");
                    arc.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P6"), oneMorePoint, false, arc);
                    /*}
                    else
                    {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P6"), oneMorePoint, false, line);
                    }*/
                }
                break;
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Ff2t, Values.F, points.get("P6"), true, oneMorePoint, false);
                }
                break;
            }
        } else if (type == 10) {
            switch (Values.Fi1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P5"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P5");
                    line.pEnd = points.get("P4");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P5"), oneMorePoint, false, line);
                    //}
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.F, points.get("P5"), false, oneMorePoint, false);
                }
                break;
            }

            switch (Values.Fi2) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P14"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P14");
                    line.pEnd = points.get("P12");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P14"), oneMorePoint, true, line);
                    //}
                }
                break;
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P14"), false, oneMorePoint, true);
                }
                break;
            }

            ricetta1Result.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P11"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P6"), pocketValues.get("LP"));

            switch (Values.Ff2) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P6"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P6");
                    line.pEnd = points.get("P8");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P6"), oneMorePoint, false, line);
                    //}
                }
                break;
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Ff2t, Values.F, points.get("P6"), true, oneMorePoint, false);
                }
                break;
            }
        } else if (type == 11) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            ricettaResult.drawFeedTo(points.get("P6"));
            ricettaResult.drawArcTo(points.get("P7"), points.get("P8"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P11"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            ricettaResult.drawArcTo(points.get("P13"), points.get("P14"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.A, points.get("P14"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    //if (pocketValues.get("M2") > 1) {
                    ElementArc arc = new ElementArc();
                    arc.pStart = points.get("P14");
                    arc.pMiddle = points.get("P13");
                    arc.pEnd = points.get("P12");
                    arc.passo = pocketValues.get("LP");
                    arc.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P14"), oneMorePoint, false, arc);
                    /*}
                    else
                    {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P14"), oneMorePoint, false, line);
                    }*/
                }
                break;
            }

            switch (Values.Fi2) {
                case 0:
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            ricetta1Result.drawZigZagTo(points.get("P14"), pocketValues.get("B"), pocketValues.get("C"));
            ricetta1Result.drawFeedTo(points.get("P6"));
            ricetta1Result.drawZigZagTo(points.get("P5"), pocketValues.get("G"), pocketValues.get("H"));

            switch (Values.Ff2) {
                case 0:
                case 1: {
                    ricetta1Result = HorizontalZigZagFermatura(ricetta1Result, pocketValues.get("C"), pocketValues.get("B"), Values.pFf2, Values.Ff2t, points.get("P5"), false, oneMorePoint, false);
                }
                break;
            }
        } else if (type == 12) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            ricettaResult.drawFeedTo(points.get("P6"));
            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P11"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P14"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.A, points.get("P14"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P14"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P14");
                    line.pEnd = points.get("P12");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P14"), oneMorePoint, false, line);
                    //}
                }
                break;
            }

            switch (Values.Fi2) {
                case 0:
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            ricetta1Result.drawZigZagTo(points.get("P14"), pocketValues.get("B"), pocketValues.get("C"));
            ricetta1Result.drawFeedTo(points.get("P6"));
            ricetta1Result.drawZigZagTo(points.get("P5"), pocketValues.get("G"), pocketValues.get("H"));

            switch (Values.Ff2) {
                case 0:
                case 1: {
                    ricetta1Result = HorizontalZigZagFermatura(ricetta1Result, pocketValues.get("C"), pocketValues.get("B"), Values.pFf2, Values.Ff2t, points.get("P5"), false, oneMorePoint, false);
                }
                break;
            }
        }

        return new Pair<>(ricettaResult, ricetta1Result);
    }

    /**
     * Function for add codes on the 2 Ricetta
     *
     * @param type
     * @param ricette
     * @return
     */
    public static Pair<Ricetta, Ricetta> Model_1_2_Codes(int type, Pair<Ricetta, Ricetta> ricette) {
        if (type == 1 || type == 2 || type == 3 || type == 4) {

            /**************************
             * OP2 and OP1 codes
             ***************************/

            // Set OP2 ON the line before the backtack
            try {
                if (Values.OP2On >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP2On == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            // Set OP1 ON on backtack start
            try {
                if (Values.OP1On >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP1On == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            // Set OP2 OFF on backtack
            try {
                if (Values.OP2Off >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP2Off == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************
             * Speed1 and OP3 codes
             ***************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtak (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after second backtak (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before first backtak (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        } else if (Values.type == 5 || Values.type == 6) {

            /**************************************
             * Speed1 and OP3 codes for first pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtak (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtak (fast pocket)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************************
             * Speed1 and OP3 codes for second pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.second.elements.get(3 + (Values.Fi2t - 1)).steps.get(-Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.size() - 2 - Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.second.elements.get(3 + (Values.Fi2t - 1)).steps.get(-Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before first backtak (fast pocket)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.second.elements.get(9 + (Values.Fi2t - 1)).steps.get(Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(8 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(8 + (Values.Fi2t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.second.elements.get(9 + (Values.Fi2t - 1)).steps.get(Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(8 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(8 + (Values.Fi2t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (Values.type == 7 || Values.type == 8) {

            /**************************************
             * Speed1 and OP3 codes for first pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            /*****************************************************/
            // Set Speed1 OFF after first backtak (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(3 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(3 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (fast pocket)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************************
             * Speed1 and OP3 codes for second pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(-Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(-Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (Values.type == 9 || Values.type == 10) {

            /**************************************
             * Speed1 and OP3 codes for first pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************************
             * Speed1 and OP3 codes for second pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (slow backtack)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(-Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(-Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi2t - 1)).steps.get(Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi2t - 1)).steps.get(Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (Values.type == 11 || Values.type == 12) {

            /**************************
             * OP2 and OP1 codes
             ***************************/

            try {
                if (Values.OP2On >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP2On == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            // TODO Codice su feed, va a finire in fondo al feed
            try {
                if (Values.OP1On >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP1On == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            // TODO Codice su feed
            try {
                if (Values.OP2Off >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP2Off == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************************
             * Speed1 and OP3 codes for first pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed


            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (slow backtack)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF before second backtack (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after second backtack (slow backtack)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before first backtack (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            /**************************************
             * Speed1 and OP3 codes for second pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        return ricette;
    }




    /*

                                 Model 3 Shell:

       P4   |                                                |  P1
            |                                                |
            |                                                |
            |                                                |
            |                                                |
            |                                                |
            |                                                |
            |                                                |
            |                                                |
            |                                                |
            |                                                |
            |                                                |
            |                                                |
            |                                                |
            |                                                |
            |                                                |
            |                                                |
            |                                                |
            |                                                |
      P3    |________________________________________________|  P2




                                Model 3 Draw:


          M + (1/2 H travetta)
                |
                |
        P4   ______   P5                          P8   ______   P1
            |      |                                  |      |
            |      |                                  |      |
            |      |                                  |      |
            |      |                                  |      |
            |      |                                  |      |
            |      |                                  |      |
            |      |                                  |      |
            |      |                                  |      |-| --- M
            |      |                                  |      |
            |      |                                  |      |
            |      |                                  |      |
            |      |                                  |      |
            |      |                                  |      |
            |      |                                  |      |
            |      |                                  |      |
            |      |                                  |      |
            |      |  P6                          P7  |      |
            |      |__________________________________|      |
            |                                                |
      P3    |________________________________________________|  P2


 */

    /**
     * Function for calculate the draw points from the shell points
     *
     * @param type
     * @param fileName
     * @param pocketValues
     * @return
     */
    public static TreeMap<String, PointF> Model_3_Points(int type, String fileName, HashMap<String, Float> pocketValues) {

        TreeMap<String, PointF> pointsMap = new TreeMap();

        HashMap<String, PointF> pointsPTS = ReadPointsFromPTS(fileName);

        float topLeftMargin_Backtack = pocketValues.get("M") + pocketValues.get("B") / 2;
        float topRightMargin_Backtack = pocketValues.get("M") + pocketValues.get("G") / 2;
        if (type == 5) {
            topLeftMargin_Backtack = pocketValues.get("M");
            topRightMargin_Backtack = pocketValues.get("M");
        }


        //region Backtacks-Sx
        float startx = pointsPTS.get("P1").x + pocketValues.get("M");
        float starty = pointsPTS.get("P1").y + topRightMargin_Backtack;
        float endx = pointsPTS.get("P1").x + pocketValues.get("M") + pocketValues.get("F");
        float endy = pointsPTS.get("P1").y + topRightMargin_Backtack;
        pointsMap.put("P1", new PointF(startx, starty));
        pointsMap.put("P8", new PointF(endx, endy));
        //endregion

        //region Backtacks-Dx
        startx = pointsPTS.get("P4").x - pocketValues.get("M");
        starty = pointsPTS.get("P4").y + topLeftMargin_Backtack;
        endx = pointsPTS.get("P4").x - pocketValues.get("M") - pocketValues.get("A");
        endy = pointsPTS.get("P4").y + topLeftMargin_Backtack;
        pointsMap.put("P4", new PointF(startx, starty));
        pointsMap.put("P5", new PointF(endx, endy));
        //endregion

        List<Float> xycP1P2_M = MathGeoTri.CalculateParallelLine(pointsPTS.get("P1"), pointsPTS.get("P2"), pocketValues.get("M"));
        List<Float> xycP2P3__M;
        // If P2 > P3 i have the parallel line in a side, otherwise i have the line in the other side
        if (pointsPTS.get("P2").y > pointsPTS.get("P3").y) {
            xycP2P3__M = MathGeoTri.CalculateParallelLine(pointsPTS.get("P2"), pointsPTS.get("P3"), -pocketValues.get("M"));
        } else {
            xycP2P3__M = MathGeoTri.CalculateParallelLine(pointsPTS.get("P2"), pointsPTS.get("P3"), pocketValues.get("M"));
        }
        PointF interArcoDxDxSBot = MathGeoTri.CalculateStraightLinesIntersection(xycP1P2_M.get(0), xycP1P2_M.get(1), xycP1P2_M.get(2), xycP2P3__M.get(0), xycP2P3__M.get(1), xycP2P3__M.get(2));

        pointsMap.put("P2", interArcoDxDxSBot);


        List<Float> xycP4P3__M = MathGeoTri.CalculateParallelLine(pointsPTS.get("P4"), pointsPTS.get("P3"), -pocketValues.get("M"));
        PointF interArcoSxSxSBot = MathGeoTri.CalculateStraightLinesIntersection(xycP4P3__M.get(0), xycP4P3__M.get(1), xycP4P3__M.get(2), xycP2P3__M.get(0), xycP2P3__M.get(1), xycP2P3__M.get(2));

        pointsMap.put("P3", interArcoSxSxSBot);


        List<Float> xycP4P3__M__A = MathGeoTri.CalculateParallelLine(pointsPTS.get("P4"), pointsPTS.get("P3"), -pocketValues.get("M") - pocketValues.get("A"));
        List<Float> xycP2P3__M__I;
        // If P2 > P3 i have the parallel line in a side, otherwise i have the line in the other side
        if (pointsPTS.get("P2").y > pointsPTS.get("P3").y) {
            xycP2P3__M__I = MathGeoTri.CalculateParallelLine(pointsPTS.get("P2"), pointsPTS.get("P3"), -pocketValues.get("M") - pocketValues.get("I"));
        } else {
            xycP2P3__M__I = MathGeoTri.CalculateParallelLine(pointsPTS.get("P2"), pointsPTS.get("P3"), pocketValues.get("M") + pocketValues.get("I"));
        }
        PointF interArcoSxDxSTop = MathGeoTri.CalculateStraightLinesIntersection(xycP4P3__M__A.get(0), xycP4P3__M__A.get(1), xycP4P3__M__A.get(2), xycP2P3__M__I.get(0), xycP2P3__M__I.get(1), xycP2P3__M__I.get(2));

        pointsMap.put("P6", interArcoSxDxSTop);


        List<Float> xycP3P2__M__I;
        // If P2 > P3 i have the parallel line in a side, otherwise i have the line in the other side
        if (pointsPTS.get("P2").y > pointsPTS.get("P3").y) {
            xycP3P2__M__I = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P2"), -pocketValues.get("M") - pocketValues.get("I"));
        } else {
            xycP3P2__M__I = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P2"), pocketValues.get("M") + pocketValues.get("I"));
        }
        List<Float> xycP1P2_M_F = MathGeoTri.CalculateParallelLine(pointsPTS.get("P1"), pointsPTS.get("P2"), pocketValues.get("M") + pocketValues.get("F"));
        PointF interArcoDxSxSTop = MathGeoTri.CalculateStraightLinesIntersection(xycP3P2__M__I.get(0), xycP3P2__M__I.get(1), xycP3P2__M__I.get(2), xycP1P2_M_F.get(0), xycP1P2_M_F.get(1), xycP1P2_M_F.get(2));

        pointsMap.put("P7", interArcoDxSxSTop);

        return pointsMap;
    }

    /**
     * Function for calculate the 2 Ricetta from the given points
     *
     * @param type
     * @param points
     * @param pocketValues
     * @return
     */
    public static Pair<Ricetta, Ricetta> Model_3_Ricetta(int type, TreeMap<String, PointF> points, HashMap<String, Float> pocketValues) {
        Ricetta ricettaResult = new Ricetta(Values.plcType);
        Ricetta ricetta1Result = new Ricetta(Values.plcType);
        //Default Start Draw Position
        ricettaResult.setDrawPosition(new PointF(0.1f, 0.1f));
        ricetta1Result.setDrawPosition(new PointF(0.1f, 0.1f));

        if (type == 1) {

            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawZigZagTo(points.get("P5"), pocketValues.get("B"), pocketValues.get("C"));
            ricettaResult.drawLineTo(points.get("P6"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricettaResult.drawZigZagTo(points.get("P1"), pocketValues.get("G"), pocketValues.get("H"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalZigZagFermatura(ricettaResult, pocketValues.get("C"), pocketValues.get("B"), Values.pFf1, Values.Ff1t, points.get("P1"), true, oneMorePoint, false);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, line);
                    //}
                }
                break;
            }
        } else if (type == 2) {

            switch (Values.Fi1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P6"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P1"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, line);
                    //}
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.A, points.get("P1"), true, oneMorePoint, false);
                }
                break;
            }
        } else if (type == 3) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.F, points.get("P4"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P4"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P4");
                    line.pEnd = points.get("P3");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P4"), oneMorePoint, false, line);
                    //}
                }
                break;
            }

            switch (Values.Fi2) {
                case 0: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricetta1Result.drawZigZagTo(points.get("P8"), pocketValues.get("G"), pocketValues.get("H"));
            ricetta1Result.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P6"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            ricetta1Result.drawZigZagTo(points.get("P4"), pocketValues.get("G"), pocketValues.get("H"));

            switch (Values.Ff2) {
                case 0: {
                    ricetta1Result = HorizontalZigZagFermatura(ricetta1Result, pocketValues.get("C"), pocketValues.get("B"), Values.pFf2, Values.Ff2t, points.get("P4"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P4"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P4");
                    line.pEnd = points.get("P3");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P4"), oneMorePoint, false, line);
                    //}
                }
                break;
            }
        } else if (type == 4) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P8"), false, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P8"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P8"), oneMorePoint, true, line);
                    //}
                }
                break;
            }


            ricettaResult.drawZigZagTo(points.get("P1"), pocketValues.get("G"), pocketValues.get("H"));
            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawZigZagTo(points.get("P5"), pocketValues.get("B"), pocketValues.get("C"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalZigZagFermatura(ricettaResult, pocketValues.get("C"), pocketValues.get("B"), Values.pFf1, Values.Ff1t, points.get("P5"), true, oneMorePoint, false);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P5"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P5");
                    line.pEnd = points.get("P6");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P5"), oneMorePoint, false, line);
                    //}
                }
                break;
            }

            switch (Values.Fi2) {
                case 0:
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P8"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P8");
                    line.pEnd = points.get("P7");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P8"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricetta1Result.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P6"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P5"), pocketValues.get("LP"));

            switch (Values.Ff2) {
                case 0:
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P5"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P5");
                    line.pEnd = points.get("P6");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P5"), oneMorePoint, false, line);
                    //}
                }
                break;
            }
        } else if (type == 5) {
            switch (Values.Fi1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P4"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P4");
                    line.pEnd = points.get("P3");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P4"), oneMorePoint, false, line);
                    //}
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.F, points.get("P4"), false, oneMorePoint, false);
                }
                break;
            }

            switch (Values.Fi2) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P8"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P8");
                    line.pEnd = points.get("P7");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P8"), oneMorePoint, true, line);
                    //}
                }
                break;
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P8"), false, oneMorePoint, true);
                }
                break;
            }

            ricetta1Result.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P6"), pocketValues.get("LP"));
            ricetta1Result.drawLineTo(points.get("P5"), pocketValues.get("LP"));

            switch (Values.Ff2) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P5"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P5");
                    line.pEnd = points.get("P6");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P5"), oneMorePoint, false, line);
                    //}
                }
                break;
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Ff2t, Values.F, points.get("P5"), true, oneMorePoint, false);
                }
                break;
            }
        } else if (type == 6) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawFeedTo(points.get("P5"));
            ricettaResult.drawLineTo(points.get("P6"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.A, points.get("P8"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P8"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P8");
                    line.pEnd = points.get("P7");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P8"), oneMorePoint, false, line);
                    //}
                }
                break;
            }

            switch (Values.Fi2) {
                case 0:
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            ricetta1Result.drawZigZagTo(points.get("P8"), pocketValues.get("LP"));
            ricetta1Result.drawFeedTo(points.get("P5"));
            ricetta1Result.drawZigZagTo(points.get("P4"), pocketValues.get("LP"));

            switch (Values.Ff2) {
                case 0:
                case 1: {
                    ricetta1Result = HorizontalZigZagFermatura(ricetta1Result, pocketValues.get("C"), pocketValues.get("B"), Values.pFf2, Values.Ff2t, points.get("P4"), false, oneMorePoint, false);
                }
                break;
            }
        }

        //TODO NOT FOUND
        else if (type == 7) {
            ricettaResult.drawFeedTo(points.get("P1"));

            //TODO
            /*if (Values.Fi1 == 0) {
                ArrayList<Float> res = CheckFValues(Values.dpFi1, Values.pFi1, Values.A);
                Values.dpFi1 = res.get(0);
                Values.pFi1 = Math.round(res.get(1));
                if (Values.Fi1t == 1) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 2) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(BacktascksDx.get(0));
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 3) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                }
            } else if (Values.Fi1 == 1) {
                if (Values.Fi1t == 1) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 2) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(BacktascksDx.get(0));
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 3) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                }
            }*/

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawFeedTo(points.get("P3"));
            ricettaResult.drawFeedTo(points.get("P2"));
            ricettaResult.drawFeedTo(points.get("P1"));

            //TODO
            /*if (Values.Ff1 == 0) {
                ArrayList<Float> res = CheckFValues(Values.dpFf1, Values.pFf1, Values.F);
                Values.dpFf1 = res.get(0);
                Values.pFf1 = Math.round(res.get(1));
                if (Values.Ff1t == 1) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                } else if (Values.Ff1t == 2) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                } else if (Values.Ff1t == 3) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                }
            } else if (Values.Ff1 == 1) {
                if (Values.Ff1t == 1) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                } else if (Values.Ff1t == 2) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                } else if (Values.Ff1t == 3) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                }
            }*/
        } else if (type == 8) {

            //TODO
            //PointF asdf = MatGeoTri.CalcolaIntersezioneDueRette(new PointF(0,Hb + Values.E), new PointF(400,Hb + Values.E), ArcoDxSx.get(0), ArcoDxSx.get(1));
            //r.drawFeedTo(new PointF(asdf.x, -asdf.y));
            //TODO
            /*if (Values.Fi1 == 0) {
                ArrayList<Float> res = CheckFValues(Values.dpFi1, Values.pFi1, Values.A);
                Values.dpFi1 = res.get(0);
                Values.pFi1 = Math.round(res.get(1));
                if (Values.Fi1t == 1) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 2) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(BacktascksDx.get(0));
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 3) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                }
            } else if (Values.Fi1 == 1) {
                if (Values.Fi1t == 1) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 2) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(BacktascksDx.get(0));
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 3) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                }
            }*/

            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P1"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));

            //TODO
            //asdf = MatGeoTri.CalcolaIntersezioneDueRette(new PointF(0,Hb + Values.E), new PointF(400,Hb + Values.E), ArcoSxDx.get(0), ArcoSxDx.get(1));
            //r.drawLineTo(new PointF(asdf.x, -asdf.y), LP);

            ricettaResult.drawFeedTo(points.get("P5"));
            ricettaResult.drawFeedTo(points.get("P4"));
            ricettaResult.drawFeedTo(points.get("P3"));
            ricettaResult.drawFeedTo(points.get("P2"));
            ricettaResult.drawFeedTo(points.get("P1"));

            //TODO
            /*if (Values.Ff1 == 0) {
                ArrayList<Float> res = CheckFValues(Values.dpFf1, Values.pFf1, Values.F);
                Values.dpFf1 = res.get(0);
                Values.pFf1 = Math.round(res.get(1));
                if (Values.Ff1t == 1) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                } else if (Values.Ff1t == 2) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                } else if (Values.Ff1t == 3) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                }
            } else if (Values.Ff1 == 1) {
                if (Values.Ff1t == 1) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                } else if (Values.Ff1t == 2) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                } else if (Values.Ff1t == 3) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                }
            }*/
        } else if (type == 9) {

            ricettaResult.drawFeedTo(points.get("P1"));
            ricettaResult.drawLineTo(points.get("P8"));
            //TODO
            /*if (Values.Fi1 == 0) {
                ArrayList<Float> res = CheckFValues(Values.dpFi1, Values.pFi1, Values.A);
                Values.dpFi1 = res.get(0);
                Values.pFi1 = Math.round(res.get(1));
                if (Values.Fi1t == 1) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 2) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(BacktascksDx.get(0));
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 3) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                }
            } else if (Values.Fi1 == 1) {
                if (Values.Fi1t == 1) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 2) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(BacktascksDx.get(0));
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 3) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                }
            }*/
            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            ricettaResult.drawFeedTo(points.get("P4"));
            ricettaResult.drawFeedTo(points.get("P3"));
            ricettaResult.drawFeedTo(points.get("P2"));
            ricettaResult.drawFeedTo(points.get("P1"));

            //TODO
            /*if (Values.Ff1 == 0) {
                ArrayList<Float> res = CheckFValues(Values.dpFf1, Values.pFf1, Values.F);
                Values.dpFf1 = res.get(0);
                Values.pFf1 = Math.round(res.get(1));
                if (Values.Ff1t == 1) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                } else if (Values.Ff1t == 2) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                } else if (Values.Ff1t == 3) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                }
            } else if (Values.Ff1 == 1) {
                if (Values.Ff1t == 1) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                } else if (Values.Ff1t == 2) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                } else if (Values.Ff1t == 3) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                }
            }*/
        } else if (type == 10) {

            //TODO
            //PointF asdf = MatGeoTri.CalcolaIntersezioneDueRette(new PointF(0,Hb + Values.E), new PointF(400,Hb + Values.E), ArcoDxDx.get(0), ArcoDxDx.get(1));
            //r.drawFeedTo(new PointF(asdf.x, -asdf.y));

            ricettaResult.drawLineTo(points.get("P1"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            //TODO
            /*if (Values.Fi1 == 0) {
                ArrayList<Float> res = CheckFValues(Values.dpFi1, Values.pFi1, Values.A);
                Values.dpFi1 = res.get(0);
                Values.pFi1 = Math.round(res.get(1));
                if (Values.Fi1t == 1) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 2) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(BacktascksDx.get(0));
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 3) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                }
            } else if (Values.Fi1 == 1) {
                if (Values.Fi1t == 1) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 2) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(BacktascksDx.get(0));
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 3) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                }
            }*/
            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));

            //TODO
            //asdf = MatGeoTri.CalcolaIntersezioneDueRette(new PointF(0,Hb + Values.E), new PointF(400,Hb + Values.E), ArcoSxSx.get(0), ArcoSxSx.get(1));
            //r.drawLineTo(new PointF(asdf.x, -asdf.y), LP);

            ricettaResult.drawFeedTo(points.get("P5"));
            ricettaResult.drawFeedTo(points.get("P4"));
            ricettaResult.drawFeedTo(points.get("P3"));
            ricettaResult.drawFeedTo(points.get("P2"));
            ricettaResult.drawFeedTo(points.get("P1"));

            //TODO
            /*if (Values.Ff1 == 0) {
                ArrayList<Float> res = CheckFValues(Values.dpFf1, Values.pFf1, Values.F);
                Values.dpFf1 = res.get(0);
                Values.pFf1 = Math.round(res.get(1));
                if (Values.Ff1t == 1) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                } else if (Values.Ff1t == 2) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                } else if (Values.Ff1t == 3) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                }
            } else if (Values.Ff1 == 1) {
                if (Values.Ff1t == 1) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                } else if (Values.Ff1t == 2) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                } else if (Values.Ff1t == 3) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                }
            }*/
        }

        return new Pair<>(ricettaResult, ricetta1Result);
    }

    /**
     * Function for add codes on the 2 Ricetta
     *
     * @param type
     * @param ricette
     * @return
     */
    public static Pair<Ricetta, Ricetta> Model_3_Codes(int type, Pair<Ricetta, Ricetta> ricette) {

        if (type == 1 || type == 2) {

            /**************************************
             * OP2 and OP1 codes for first pocket
             ***************************************/

            try {
                if (Values.OP2On >= 0) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP2On == -1) {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            try {
                if (Values.OP1On >= 0) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP1On == -1) {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP2Off >= 0) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP2Off == -1) {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************************
             * Speed1 and OP3 codes for first pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (fast pocket)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after second backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before first backtack (fast pocket)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (Values.type == 3) {

            /**************************************
             * Speed1 and OP3 codes for first pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (fast pocket)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************************
             * Speed1 and OP3 codes for second pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.second.elements.get(3 + (Values.Fi2t - 1)).steps.get(-Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.second.elements.get(3 + (Values.Fi2t - 1)).steps.get(-Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 < 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (fast pocket)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.get(Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.get(Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (Values.type == 4) {

            /**************************************
             * Speed1 and OP3 codes for first pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(3 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(3 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************************
             * Speed1 and OP3 codes for second pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(-Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(-Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.get(Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(4 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(4 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(4 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(4 + (Values.Fi2t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.get(Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(4 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(4 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(4 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(4 + (Values.Fi2t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        } else if (Values.type == 5) {

            /**************************************
             * Speed1 and OP3 codes for first pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************************
             * Speed1 and OP3 codes for second pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(-Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(-Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.get(Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(4 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(4 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(4 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(4 + (Values.Fi2t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.second.elements.get(5 + (Values.Fi2t - 1)).steps.get(Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(4 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(4 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(4 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(4 + (Values.Fi2t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (Values.type == 6) {

            /**************************************
             * OP2 and OP1 codes for first pocket
             ***************************************/

            try {
                if (Values.OP2On >= 0) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP2On == -1) {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            // TODO Questo codice è su un feed e quindi va a finire al primo punto del feed
            try {
                if (Values.OP1On >= 0) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP1On == -1) {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            // TODO Questo codice è su un feed e quindi non viene messo
            try {
                if (Values.OP2Off >= 0) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP2Off == -1) {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************************
             * Speed1 and OP3 codes for first pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try { 
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(4 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after second backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(5 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before first backtack (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************************
             * Speed1 and OP3 codes for second pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        return ricette;
    }




    /*

                                     Model 4 Shell:

           P8   |                                                |  P1
                |                                                |
                |                                                |
                |                                                |
                |                                                |
                |                                                |
                |                                                |
                |                                                |
                |                                                |
                |                                                |
                |                                                |
                |                                                |
                |                                                |
                |                                                |
                |                                                |
                |                                                |
            P7  (                                                )   P2
                 (                                              )
              P6  (                                            )   P3
                   (__________________________________________)
                P5                                               P4



                                    Model 4 Draw:


              M + (1/2 H travetta)
                    |
                    |
            P8   ______   P9                          P16  ______  P1
                |      |                                  |      |
                |      |                                  |      |
                |      |                                  |      |
                |      |                                  |      |
                |      |                                  |      |
                |      |                                  |      |
                |      |                                  |      |
                |      |                                  |      |-| --- M
                |      |                                  |      |
                |      |                                  |      |
                |      |                                  |      |
                |      |                                  |      |
                |      |                                  |      |
                |      |                                  |      |
                |      |                                  |      |
                |      |  P10                        P15  |      |
           P7   (      (   P11  P12            P13  P14   )      )  P2
                 (      (________________________________)      )
            P6    (                                            )   P3
              P5   (__________________________________________)   P4





     */

    /**
     * Function for calculate the draw points from the shell points
     *
     * @param type
     * @param fileName
     * @param pocketValues
     * @return
     */
    public static TreeMap<String, PointF> Model_4_Points(int type, String fileName, HashMap<String, Float> pocketValues) {

        TreeMap<String, PointF> pointsMap = new TreeMap();

        HashMap<String, PointF> pointsPTS = ReadPointsFromPTS(fileName);

        float topLeftMargin_Backtack = pocketValues.get("M") + pocketValues.get("B") / 2;
        float topRightMargin_Backtack = pocketValues.get("M") + pocketValues.get("G") / 2;
        if (type == 5) {
            topLeftMargin_Backtack = pocketValues.get("M");
            topRightMargin_Backtack = pocketValues.get("M");
        }

        //region Backtacks-Sx
        float startx = pointsPTS.get("P1").x + pocketValues.get("M");
        float starty = pointsPTS.get("P1").y + topRightMargin_Backtack;
        float endx = pointsPTS.get("P1").x + pocketValues.get("M") + pocketValues.get("F");
        float endy = pointsPTS.get("P1").y + topRightMargin_Backtack;
        pointsMap.put("P1", new PointF(startx, starty));
        pointsMap.put("P16", new PointF(endx, endy));
        //endregion

        //region Backtacks-Dx
        startx = pointsPTS.get("P8").x - pocketValues.get("M");
        starty = pointsPTS.get("P8").y + topLeftMargin_Backtack;
        endx = pointsPTS.get("P8").x - pocketValues.get("M") - pocketValues.get("A");
        endy = pointsPTS.get("P8").y + topLeftMargin_Backtack;
        pointsMap.put("P8", new PointF(startx, starty));
        pointsMap.put("P9", new PointF(endx, endy));
        //endregion

        //region interH1DxS2Bot
        //parallela lato dx
        List<Float> points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P1"), pointsPTS.get("P2"), pocketValues.get("M"));
        //parallela latodx inclinato
        List<Float> points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P2"), pointsPTS.get("P4"), pocketValues.get("M"));

        PointF interArcoDxDxArcoDxDxBot = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

        //H1Dx
        pointsMap.put("P2", interArcoDxDxArcoDxDxBot);
        //endregion

        float raggio = pocketValues.get("M2");

        points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P2"), pointsPTS.get("P4"), pocketValues.get("M"));

        if(raggio <1)
            points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P4"), pointsPTS.get("P5"),  pocketValues.get("M"));
        else
            points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P4"), pointsPTS.get("P5"),  pocketValues.get("M"));

        PointF interArcoDxDxBotSBot = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));
    //sono in Pakistan, se arrivo qui dalla pagina dei punti oppure dalla pagina delle dimensioni le linee orizzontali in basso vengono diverse, non ho tempo di capire ma con questo if ho risolto
        if(interArcoDxDxBotSBot.y > pointsPTS.get("P4").y)
        {
            if(raggio <1)
                points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P4"), pointsPTS.get("P5"), - pocketValues.get("M"));
            else
                points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P4"), pointsPTS.get("P5"),  - pocketValues.get("M"));

             interArcoDxDxBotSBot = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));



        }


        PointF Centro_arco = MathGeoTri.CalculateCenterPoint(interArcoDxDxArcoDxDxBot, interArcoDxDxBotSBot, raggio - pocketValues.get("M"), true);

        PointF Pmedio = MathGeoTri.CalculateArcThirdPoint(interArcoDxDxArcoDxDxBot, interArcoDxDxBotSBot, raggio - pocketValues.get("M"), Centro_arco, true);

        //H2Sx
        pointsMap.put("P3", Pmedio);
        pointsMap.put("P4", interArcoDxDxBotSBot);
        if(raggio <1)
            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P4"), pointsPTS.get("P5"),  pocketValues.get("M"));
        else
            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P4"), pointsPTS.get("P5"),  pocketValues.get("M"));

        points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P5"), -pocketValues.get("M"));

        PointF inerSBotArcoSxSxBot = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));
//sono in Pakistan, se arrivo qui dalla pagina dei punti oppure dalla pagina delle dimensioni le linee orizzontali in basso vengono diverse, non ho tempo di capire ma con questo if ho risolto
        if(inerSBotArcoSxSxBot.y > pointsPTS.get("P5").y){

            if(raggio <1)
                points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P4"), pointsPTS.get("P5"),  -pocketValues.get("M"));
            else
                points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P4"), pointsPTS.get("P5"),  -pocketValues.get("M"));

            points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P5"), -pocketValues.get("M"));

             inerSBotArcoSxSxBot = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

        }

        points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P5"), -pocketValues.get("M"));

        points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P8"), pointsPTS.get("P7"), -pocketValues.get("M"));

        PointF interArcoSxSxBotArcoSxSx = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

        raggio = pocketValues.get("M1");

        Centro_arco = MathGeoTri.CalculateCenterPoint(inerSBotArcoSxSxBot, interArcoSxSxBotArcoSxSx, raggio - pocketValues.get("M"), true);

        Pmedio = MathGeoTri.CalculateArcThirdPoint(inerSBotArcoSxSxBot, interArcoSxSxBotArcoSxSx, raggio - pocketValues.get("M"), Centro_arco, true);

        //H2Sx
        pointsMap.put("P5", inerSBotArcoSxSxBot);
        pointsMap.put("P6", Pmedio);
        pointsMap.put("P7", interArcoSxSxBotArcoSxSx);

        //H2Sx
        pointsMap.put("P7", interArcoSxSxBotArcoSxSx);
        pointsMap.put("P8", new PointF(pointsPTS.get("P8").x - pocketValues.get("M"), pointsPTS.get("P8").y + topLeftMargin_Backtack));

        PointF interArcoSxDxArcoSxDxTop = new PointF(0f, 0f);
        if (pocketValues.get("M1") > 1) {
            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P8"), pointsPTS.get("P7"), -pocketValues.get("M") - pocketValues.get("A"));

            points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P5"), -pocketValues.get("M") - pocketValues.get("I"));

            interArcoSxDxArcoSxDxTop = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

            //H2Sx
            pointsMap.put("P10", interArcoSxDxArcoSxDxTop);
        } else {
            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P8"), -pocketValues.get("M") - pocketValues.get("D"));

            points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P5"), -pocketValues.get("M") - pocketValues.get("D"));

            interArcoSxDxArcoSxDxTop = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

            //H2Sx
            pointsMap.put("P10", interArcoSxDxArcoSxDxTop);
        }


        PointF interArcoSxDxBotSTop = new PointF(0F, 0F);
        if (pocketValues.get("M1") > 1) {
            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P5"), -pocketValues.get("M") - pocketValues.get("A"));

            points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P4"), pocketValues.get("M") + pocketValues.get("I"));

            //Per ora lascio come distanza centrale la stessa di E ma poi dovrò aggiungere un altra lettera

            interArcoSxDxBotSTop = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

            raggio = pocketValues.get("M1");
            raggio = raggio - pocketValues.get("I") - pocketValues.get("M");

            Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(interArcoSxDxArcoSxDxTop.x, interArcoSxDxArcoSxDxTop.y), new PointF(interArcoSxDxBotSTop.x, interArcoSxDxBotSTop.y), raggio , false);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

            Pmedio = MathGeoTri.CalculateArcThirdPoint(interArcoSxDxArcoSxDxTop, interArcoSxDxBotSTop, raggio, Centro_arco, false);

            //H2Sx
            //pointsMap.put("P10", interArcoSxDxArcoSxDxTop);
            pointsMap.put("P11", Pmedio);
            pointsMap.put("P12", interArcoSxDxBotSTop);
        } else {
            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P5"), -pocketValues.get("M") - pocketValues.get("D"));

            points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P4"), pocketValues.get("M") + pocketValues.get("I"));

            //Per ora lascio come distanza centrale la stessa di E ma poi dovrò aggiungere un altra lettera

            interArcoSxDxBotSTop = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));
//sono in Pakistan, se arrivo qui dalla pagina dei punti oppure dalla pagina delle dimensioni le linee orizzontali in basso vengono diverse, non ho tempo di capire ma con questo if ho risolto
          if(interArcoSxDxBotSTop.y > pointsPTS.get("P5").y){

              points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P5"), -pocketValues.get("M") - pocketValues.get("D"));

              points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P4"), -pocketValues.get("M") - pocketValues.get("I"));

              //Per ora lascio come distanza centrale la stessa di E ma poi dovrò aggiungere un altra lettera

              interArcoSxDxBotSTop = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

          }

            raggio = pocketValues.get("M1");

            Centro_arco = MathGeoTri.CalculateCenterPoint(interArcoSxDxArcoSxDxTop, interArcoSxDxBotSTop, raggio - pocketValues.get("M"), false);

            Pmedio = MathGeoTri.CalculateArcThirdPoint(interArcoSxDxArcoSxDxTop, interArcoSxDxBotSTop, raggio, Centro_arco, false);

            //H2Sx
            //pointsMap.put("P10", interArcoSxDxArcoSxDxTop);
            pointsMap.put("P11", Pmedio);
            pointsMap.put("P12", interArcoSxDxBotSTop);
        }


        PointF interSBotArcoDxSxTop = new PointF(0F, 0F);
        if (pocketValues.get("M2") > 1) {
            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P1"), pointsPTS.get("P2"), pocketValues.get("M") + pocketValues.get("F"));

            points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P2"), pointsPTS.get("P4"), pocketValues.get("M") + pocketValues.get("I"));

            interSBotArcoDxSxTop = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

            //H2Sx
            pointsMap.put("P15", interSBotArcoDxSxTop);
        } else {
            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P1"), pointsPTS.get("P2"), pocketValues.get("M") + pocketValues.get("E"));

            points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P2"), pointsPTS.get("P4"), pocketValues.get("M") + pocketValues.get("E"));

            interSBotArcoDxSxTop = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

            //H2Sx
            pointsMap.put("P15", interSBotArcoDxSxTop);
        }

        PointF interArcoDxSxTopArcoDxSx = new PointF(0f, 0f);
        if (pocketValues.get("M2") > 1) {
            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P2"), pointsPTS.get("P4"), pocketValues.get("M") + pocketValues.get("F"));

            points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P4"), pocketValues.get("M") + pocketValues.get("I"));

            //Per ora lascio come distanza centrale la stessa di E ma poi dovrò aggiungere un altra lettera

            interArcoDxSxTopArcoDxSx = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));

            raggio = pocketValues.get("M2");
            raggio = raggio - pocketValues.get("I") - pocketValues.get("M");

            Centro_arco = MathGeoTri.CalculateCenterPoint(new PointF(interSBotArcoDxSxTop.x, interSBotArcoDxSxTop.y), new PointF(interArcoDxSxTopArcoDxSx.x, interArcoDxSxTopArcoDxSx.y), raggio, true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

            Pmedio = MathGeoTri.CalculateArcThirdPoint(interSBotArcoDxSxTop, interArcoDxSxTopArcoDxSx, raggio, Centro_arco, true);

            //H2Sx
            //pointsMap.put("P15", interSBotArcoDxSxTop);
            pointsMap.put("P14", Pmedio);
            pointsMap.put("P13", interArcoDxSxTopArcoDxSx);
        } else {
            points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P2"), pointsPTS.get("P4"), pocketValues.get("M") + pocketValues.get("E"));
            points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P4"), pocketValues.get("M") + pocketValues.get("I"));

            //Per ora lascio come distanza centrale la stessa di E ma poi dovrò aggiungere un altra lettera

            interArcoDxSxTopArcoDxSx = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));
//sono in Pakistan, se arrivo qui dalla pagina dei punti oppure dalla pagina delle dimensioni le linee orizzontali in basso vengono diverse, non ho tempo di capire ma con questo if ho risolto
         if(interArcoDxSxTopArcoDxSx.y > pointsPTS.get("P4").y){
                points = MathGeoTri.CalculateParallelLine(pointsPTS.get("P2"), pointsPTS.get("P4"), pocketValues.get("M") + pocketValues.get("E"));
                points1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P4"), -pocketValues.get("M") - pocketValues.get("I"));

                //Per ora lascio come distanza centrale la stessa di E ma poi dovrò aggiungere un altra lettera

                interArcoDxSxTopArcoDxSx = MathGeoTri.CalculateStraightLinesIntersection(points.get(0), points.get(1), points.get(2), points1.get(0), points1.get(1), points1.get(2));


            }



            raggio = pocketValues.get("M2");

            Centro_arco = MathGeoTri.CalculateCenterPoint(interSBotArcoDxSxTop, interArcoDxSxTopArcoDxSx, raggio - pocketValues.get("M"), true);  //centro arco P1 P3 considerando M1 (non ancora ristretto)

            Pmedio = MathGeoTri.CalculateArcThirdPoint(interSBotArcoDxSxTop, interArcoDxSxTopArcoDxSx, raggio, Centro_arco, true);

            //H2Sx
            //pointsMap.put("P15", interSBotArcoDxSxTop);
            pointsMap.put("P14", Pmedio);
            pointsMap.put("P13", interArcoDxSxTopArcoDxSx);
        }
        //endregion

        return pointsMap;
    }

    /**
     * Function for calculate the 2 Ricetta from the given points
     *
     * @param type
     * @param points
     * @param pocketValues
     * @return
     */
    public static Pair<Ricetta, Ricetta> Model_4_Ricetta(int type, TreeMap<String, PointF> points, HashMap<String, Float> pocketValues) {
        Ricetta ricettaResult = new Ricetta(Values.plcType);
        Ricetta ricetta1Result = new Ricetta(Values.plcType);
        //Default Start Draw Position
        ricettaResult.setDrawPosition(new PointF(0.1f, 0.1f));
        ricetta1Result.setDrawPosition(new PointF(0.1f, 0.1f));

        if (type == 1) {

            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            if (pocketValues.get("M2") <= 1) {
                ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P3"), points.get("P4"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M1") <= 1) {
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricettaResult.drawZigZagTo(points.get("P9"), pocketValues.get("B"), pocketValues.get("C"));
            ricettaResult.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            if (pocketValues.get("M1") <= 1) {
                ricettaResult.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P11"), points.get("P12"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P13"), pocketValues.get("LP"));
            if (pocketValues.get("M2") <= 1) {
                ricettaResult.drawLineTo(points.get("P15"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P14"), points.get("P15"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P16"), pocketValues.get("LP"));
            ricettaResult.drawZigZagTo(points.get("P1"), pocketValues.get("G"), pocketValues.get("H"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalZigZagFermatura(ricettaResult, pocketValues.get("C"), pocketValues.get("B"), Values.pFf1, Values.Ff1t, points.get("P1"), true, oneMorePoint, false);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, line);
                    //}
                }
                break;
            }
        } else if (type == 2) {
            switch (Values.Fi1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            if (pocketValues.get("M2") <= 1) {
                ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P3"), points.get("P4"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M1") <= 1) {
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            if (pocketValues.get("M1") <= 1) {
                ricettaResult.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P11"), points.get("P12"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P13"), pocketValues.get("LP"));
            if (pocketValues.get("M2") <= 1) {
                ricettaResult.drawLineTo(points.get("P15"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P14"), points.get("P15"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P16"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P1"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, line);
                    //}
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.A, points.get("P1"), true, oneMorePoint, false);
                }
                break;
            }
        } else if (type == 3) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            if (pocketValues.get("M2") <= 1) {
                ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P3"), points.get("P4"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M1") <= 1) {
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.F, points.get("P8"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P8"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P8");
                    line.pEnd = points.get("P7");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P8"), oneMorePoint, false, line);
                    //}
                }
                break;
            }

            switch (Values.Fi2) {
                case 0: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricetta1Result.drawZigZagTo(points.get("P16"), pocketValues.get("G"), pocketValues.get("H"));
            ricetta1Result.drawLineTo(points.get("P15"), pocketValues.get("LP"));
            if (pocketValues.get("M2") <= 1) {
                ricetta1Result.drawLineTo(points.get("P13"), pocketValues.get("LP"));
            } else {
                ricetta1Result.drawArcTo(points.get("P14"), points.get("P13"), pocketValues.get("LP"));
            }
            ricetta1Result.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            if (pocketValues.get("M1") <= 1) {
                ricetta1Result.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            } else {
                ricetta1Result.drawArcTo(points.get("P11"), points.get("P10"), pocketValues.get("LP"));
            }
            ricetta1Result.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            ricetta1Result.drawZigZagTo(points.get("P8"), pocketValues.get("B"), pocketValues.get("C"));

            switch (Values.Ff2) {
                case 0: {
                    ricetta1Result = HorizontalZigZagFermatura(ricetta1Result, pocketValues.get("C"), pocketValues.get("B"), Values.pFf2, Values.Ff2t, points.get("P8"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P8"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P8");
                    line.pEnd = points.get("P7");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P8"), oneMorePoint, false, line);
                    //}
                }
                break;
            }
        } else if (type == 4) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P16"), false, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P16"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P16");
                    line.pEnd = points.get("P15");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P16"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricettaResult.drawZigZagTo(points.get("P1"), pocketValues.get("G"), pocketValues.get("H"));
            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            if (pocketValues.get("M2") <= 1) {
                ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P3"), points.get("P4"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M1") <= 1) {
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricettaResult.drawZigZagTo(points.get("P9"), pocketValues.get("B"), pocketValues.get("C"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalZigZagFermatura(ricettaResult, pocketValues.get("C"), pocketValues.get("B"), Values.pFf1, Values.Ff1t, points.get("P9"), true, oneMorePoint, false);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P9"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P9");
                    line.pEnd = points.get("P10");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P9"), oneMorePoint, false, line);
                    //}
                }
                break;
            }

            switch (Values.Fi2) {
                case 0:
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P9"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P16");
                    line.pEnd = points.get("P15");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P16"), oneMorePoint, false, line);
                    //}
                }
                break;
            }

            ricetta1Result.drawLineTo(points.get("P15"), pocketValues.get("LP"));
            if (pocketValues.get("M2") <= 1) {
                ricetta1Result.drawLineTo(points.get("P13"), pocketValues.get("LP"));
            } else {
                ricetta1Result.drawArcTo(points.get("P14"), points.get("P13"), pocketValues.get("LP"));
            }
            ricetta1Result.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            if (pocketValues.get("M1") <= 1) {
                ricetta1Result.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            } else {
                ricetta1Result.drawArcTo(points.get("P11"), points.get("P10"), pocketValues.get("LP"));
            }
            ricetta1Result.drawLineTo(points.get("P9"), pocketValues.get("LP"));

            switch (Values.Ff2) {
                case 0:
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P9"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P9");
                    line.pEnd = points.get("P10");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P9"), oneMorePoint, false, line);
                    //}
                }
                break;
            }
        } else if (type == 5) {
            switch (Values.Fi1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            if (pocketValues.get("M2") <= 1) {
                ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P3"), points.get("P4"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M1") <= 1) {
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P8"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P8");
                    line.pEnd = points.get("P7");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P8"), oneMorePoint, false, line);
                    //}
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.F, points.get("P8"), false, oneMorePoint, false);
                }
                break;
            }

            switch (Values.Fi2) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P16"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P16");
                    line.pEnd = points.get("P15");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P16"), oneMorePoint, true, line);
                    //}
                }
                break;
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P16"), false, oneMorePoint, true);
                }
                break;
            }

            ricetta1Result.drawLineTo(points.get("P15"), pocketValues.get("LP"));
            if (pocketValues.get("M2") <= 1) {
                ricetta1Result.drawLineTo(points.get("P13"), pocketValues.get("LP"));
            } else {
                ricetta1Result.drawArcTo(points.get("P14"), points.get("P13"), pocketValues.get("LP"));
            }
            ricetta1Result.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            if (pocketValues.get("M1") <= 1) {
                ricetta1Result.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            } else {
                ricetta1Result.drawArcTo(points.get("P11"), points.get("P10"), pocketValues.get("LP"));
            }
            ricetta1Result.drawLineTo(points.get("P9"), pocketValues.get("LP"));

            switch (Values.Ff2) {
                case 0: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P9"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P9");
                    line.pEnd = points.get("P10");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P9"), oneMorePoint, false, line);
                    //}
                }
                break;
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Ff2t, Values.F, points.get("P9"), true, oneMorePoint, false);
                }
                break;
            }
        } else if (type == 6) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P1");
                    line.pEnd = points.get("P2");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    //}
                }
                break;
            }

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            if (pocketValues.get("M2") <= 1) {
                ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P3"), points.get("P4"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M1") <= 1) {
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricettaResult.drawFeedTo(points.get("P9"));
            ricettaResult.drawLineTo(points.get("P10"), pocketValues.get("LP"));
            if (pocketValues.get("M1") <= 1) {
                ricettaResult.drawLineTo(points.get("P12"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P11"), points.get("P12"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P13"), pocketValues.get("LP"));
            if (pocketValues.get("M2") <= 1) {
                ricettaResult.drawLineTo(points.get("P15"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P14"), points.get("P15"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P16"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.A, points.get("P16"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    /*if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P16"), oneMorePoint, false, arc);
                    }
                    else
                    {*/
                    ElementLine line = new ElementLine();
                    line.pStart = points.get("P16");
                    line.pEnd = points.get("P15");
                    line.passo = pocketValues.get("LP");
                    line.createSteps();

                    ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P16"), oneMorePoint, false, line);
                    //}
                }
                break;
            }

            switch (Values.Fi2) {
                case 0:
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            ricetta1Result.drawZigZagTo(points.get("P16"), pocketValues.get("LP"));
            ricetta1Result.drawFeedTo(points.get("P9"));
            ricetta1Result.drawZigZagTo(points.get("P8"), pocketValues.get("B"), pocketValues.get("C"));

            switch (Values.Ff2) {
                case 0:
                case 1: {
                    ricetta1Result = HorizontalZigZagFermatura(ricetta1Result, pocketValues.get("C"), pocketValues.get("B"), Values.pFf2, Values.Ff2t, points.get("P8"), false, oneMorePoint, false);
                }
                break;
            }
        }


        //TODO NOT FOUND
        else if (type == 7) {
            ricettaResult.drawFeedTo(points.get("P1"));

            //TODO
            /*if (Values.Fi1 == 0) {
                ArrayList<Float> res = CheckFValues(Values.dpFi1, Values.pFi1, Values.A);
                Values.dpFi1 = res.get(0);
                Values.pFi1 = Math.round(res.get(1));
                if (Values.Fi1t == 1) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 2) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(BacktascksDx.get(0));
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 3) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                }
            } else if (Values.Fi1 == 1) {
                if (Values.Fi1t == 1) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 2) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(BacktascksDx.get(0));
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 3) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                }
            }*/

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            if (pocketValues.get("M2") <= 1) {
                ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P3"), points.get("P4"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M1") <= 1) {
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            //TODO
            /*ricettaResult.drawFeedTo(new PointF(ArcoSxSxBot.get(ArcoSxSxBot.size() - 1).x, ArcoSxSxBot.get(ArcoSxSxBot.size() - 1).y + Values.M2 - Values.M));
            ricettaResult.drawFeedTo(new PointF(ArcoDxDxBot.get(ArcoDxDxBot.size() - 1).x - Values.M1 + Values.M, ArcoDxDxBot.get(ArcoDxDxBot.size() - 1).y));*/
            ricettaResult.drawFeedTo(points.get("P1"));

            //TODO
            /*if (Values.Ff1 == 0) {
                ArrayList<Float> res = CheckFValues(Values.dpFf1, Values.pFf1, Values.F);
                Values.dpFf1 = res.get(0);
                Values.pFf1 = Math.round(res.get(1));
                if (Values.Ff1t == 1) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                } else if (Values.Ff1t == 2) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                } else if (Values.Ff1t == 3) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                }
            } else if (Values.Ff1 == 1) {
                if (Values.Ff1t == 1) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                } else if (Values.Ff1t == 2) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                } else if (Values.Ff1t == 3) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                }
            }*/
        } else if (type == 8) {

            //TODO
            //PointF asdf = MatGeoTri.CalcolaIntersezioneDueRette(new PointF(0,Hb + Values.E), new PointF(400,Hb + Values.E), ArcoDxSx.get(0), ArcoDxSx.get(1));
            //ricettaResult.drawFeedTo(new PointF(asdf.x, -asdf.y));

            ricettaResult.drawLineTo(points.get("P16"), pocketValues.get("LP"));

            ricettaResult.drawLineTo(points.get("P1"), pocketValues.get("LP"));

            /*if (Values.Fi1 == 0) {
                ArrayList<Float> res = CheckFValues(Values.dpFi1, Values.pFi1, Values.A);
                Values.dpFi1 = res.get(0);
                Values.pFi1 = Math.round(res.get(1));
                if (Values.Fi1t == 1) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 2) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(BacktascksDx.get(0));
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 3) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                }
            } else if (Values.Fi1 == 1) {
                if (Values.Fi1t == 1) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 2) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(BacktascksDx.get(0));
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 3) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                }
            }*/
            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            if (pocketValues.get("M2") <= 1) {
                ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P3"), points.get("P4"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M1") <= 1) {
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            //TODO
            //asdf = MatGeoTri.CalcolaIntersezioneDueRette(new PointF(0,Hb + Values.E), new PointF(400,Hb + Values.E), ArcoSxDx.get(0), ArcoSxDx.get(1));
            //r.drawLineTo(new PointF(asdf.x, -asdf.y), LP);
            ricettaResult.drawFeedTo(points.get("P9"));
            ricettaResult.drawFeedTo(points.get("P8"));
            //ricettaResult.drawFeedTo(new PointF(ArcoSxSxBot.get(ArcoSxSxBot.size() - 1).x, ArcoSxSxBot.get(ArcoSxSxBot.size() - 1).y + Values.M2 - Values.M));
            //ricettaResult.drawFeedTo(new PointF(ArcoDxDxBot.get(ArcoDxDxBot.size() - 1).x - Values.M1 + Values.M, ArcoDxDxBot.get(ArcoDxDxBot.size() - 1).y));
            ricettaResult.drawFeedTo(points.get("P1"));

            //TODO
            /*if (Values.Ff1 == 0) {
                ArrayList<Float> res = CheckFValues(Values.dpFf1, Values.pFf1, Values.F);
                Values.dpFf1 = res.get(0);
                Values.pFf1 = Math.round(res.get(1));
                if (Values.Ff1t == 1) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                } else if (Values.Ff1t == 2) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                } else if (Values.Ff1t == 3) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                }
            } else if (Values.Ff1 == 1) {
                if (Values.Ff1t == 1) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                } else if (Values.Ff1t == 2) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                } else if (Values.Ff1t == 3) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                }
            }*/
        } else if (type == 9) {
            ricettaResult.drawFeedTo(points.get("P1"));

            ricettaResult.drawLineTo(points.get("P16"), pocketValues.get("LP"));

            //TODO
            /*if (Values.Fi1 == 0) {
                ArrayList<Float> res = CheckFValues(Values.dpFi1, Values.pFi1, Values.A);
                Values.dpFi1 = res.get(0);
                Values.pFi1 = Math.round(res.get(1));
                if (Values.Fi1t == 1) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 2) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(BacktascksDx.get(0));
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 3) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                }
            } else if (Values.Fi1 == 1) {
                if (Values.Fi1t == 1) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 2) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(BacktascksDx.get(0));
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 3) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                }
            }*/
            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            if (pocketValues.get("M2") <= 1) {
                ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P3"), points.get("P4"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M1") <= 1) {
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            ricettaResult.drawFeedTo(points.get("P8"));
            //TODO
            //ricettaResult.drawFeedTo(new PointF(ArcoSxSxBot.get(ArcoSxSxBot.size() - 1).x, ArcoSxSxBot.get(ArcoSxSxBot.size() - 1).y + Values.M2 - Values.M));
            //ricettaResult.drawFeedTo(new PointF(ArcoDxDxBot.get(ArcoDxDxBot.size() - 1).x - Values.M1 + Values.M, ArcoDxDxBot.get(ArcoDxDxBot.size() - 1).y));
            ricettaResult.drawFeedTo(points.get("P1"));

            /*if (Values.Ff1 == 0) {
                ArrayList<Float> res = CheckFValues(Values.dpFf1, Values.pFf1, Values.F);
                Values.dpFf1 = res.get(0);
                Values.pFf1 = Math.round(res.get(1));
                if (Values.Ff1t == 1) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                } else if (Values.Ff1t == 2) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                } else if (Values.Ff1t == 3) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                }
            } else if (Values.Ff1 == 1) {
                if (Values.Ff1t == 1) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                } else if (Values.Ff1t == 2) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                } else if (Values.Ff1t == 3) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                }
            }*/
        } else if (type == 10) {
            //TODO
            //PointF asdf = MatGeoTri.CalcolaIntersezioneDueRette(new PointF(0,Hb + Values.E), new PointF(400,Hb + Values.E), ArcoDxDx.get(0), ArcoDxDx.get(1));
            //ricettaResult.drawFeedTo(new PointF(asdf.x, -asdf.y));

            ricettaResult.drawLineTo(points.get("P1"));

            ricettaResult.drawLineTo(points.get("P16"));

            //TODO
            /*if (Values.Fi1 == 0) {
                ArrayList<Float> res = CheckFValues(Values.dpFi1, Values.pFi1, Values.A);
                Values.dpFi1 = res.get(0);
                Values.pFi1 = Math.round(res.get(1));
                if (Values.Fi1t == 1) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 2) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(BacktascksDx.get(0));
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 3) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1) + Values.dpFi1, BacktascksDx.get(0).y));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x + (Values.dpFi1 * Values.pFi1), BacktascksDx.get(0).y));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                }
            } else if (Values.Fi1 == 1) {
                if (Values.Fi1t == 1) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 2) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(BacktascksDx.get(0));
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                } else if (Values.Fi1t == 3) {
                    if (PuntoinPiu) {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1) + Values.dpFi1));
                    } else {
                        FStart.add(new PointF(BacktascksDx.get(0).x, BacktascksDx.get(0).y + (Values.dpFi1 * Values.pFi1)));
                    }
                    r.drawFeedTo(FStart.get(0));
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                    r.drawLineTo(FStart.get(0), Values.dpFi1);
                    r.drawLineTo(BacktascksDx.get(0), Values.dpFi1);
                }
            }*/

            ricettaResult.drawLineTo(points.get("P2"), pocketValues.get("LP"));
            if (pocketValues.get("M2") <= 1) {
                ricettaResult.drawLineTo(points.get("P4"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P3"), points.get("P4"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));
            if (pocketValues.get("M1") <= 1) {
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));
            } else {
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            }
            ricettaResult.drawLineTo(points.get("P8"), pocketValues.get("LP"));
            ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));
            //TODO
            //asdf = MatGeoTri.CalcolaIntersezioneDueRette(new PointF(0,Hb + Values.E), new PointF(400,Hb + Values.E), ArcoSxSx.get(0), ArcoSxSx.get(1));
            //r.drawLineTo(new PointF(asdf.x, -asdf.y), LP);
            ricettaResult.drawFeedTo(points.get("P9"));
            ricettaResult.drawFeedTo(points.get("P8"));
            //TODO
            //ricettaResult.drawFeedTo(new PointF(ArcoSxSxBot.get(ArcoSxSxBot.size() - 1).x, ArcoSxSxBot.get(ArcoSxSxBot.size() - 1).y + Values.M2 - Values.M));
            //ricettaResult.drawFeedTo(new PointF(ArcoDxDxBot.get(ArcoDxDxBot.size() - 1).x - Values.M1 + Values.M, ArcoDxDxBot.get(ArcoDxDxBot.size() - 1).y));
            ricettaResult.drawFeedTo(points.get("P1"));

            //TODO
            /*if (Values.Ff1 == 0) {
                ArrayList<Float> res = CheckFValues(Values.dpFf1, Values.pFf1, Values.F);
                Values.dpFf1 = res.get(0);
                Values.pFf1 = Math.round(res.get(1));
                if (Values.Ff1t == 1) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                } else if (Values.Ff1t == 2) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                } else if (Values.Ff1t == 3) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1) + Values.dpFf1), BacktascksSx.get(0).y));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x - ((Values.dpFf1 * Values.pFf1)), BacktascksSx.get(0).y));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                }
            } else if (Values.Ff1 == 1) {
                if (Values.Ff1t == 1) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                } else if (Values.Ff1t == 2) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                } else if (Values.Ff1t == 3) {
                    if (PuntoinPiu) {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1) + Values.dpFf1));
                    } else {
                        FEnd.add(new PointF(BacktascksSx.get(0).x, BacktascksSx.get(0).y + (Values.dpFf1 * Values.pFf1)));
                    }
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                    r.drawLineTo(ArcoSxSx.get(ArcoSxSx.size() - 1), Values.dpFf1);
                    r.drawLineTo(FEnd.get(0), Values.dpFf1);
                }
            }*/
        }

        return new Pair<>(ricettaResult, ricetta1Result);
    }

    /**
     * Function for add codes on the 2 Ricetta
     *
     * @param type
     * @param ricette
     * @return
     */
    public static Pair<Ricetta, Ricetta> Model_4_Codes(int type, Pair<Ricetta, Ricetta> ricette) {
        if (type == 1 || type == 2) {

            /**************************************
             * OP2 and OP1 codes for first pocket
             ***************************************/

            try {
                if (Values.OP2On >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP2On == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP1On >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP1On == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP2Off >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP2Off == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************************
             * Speed1 and OP3 codes for first pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after second backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before first backtack (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (Values.type == 3) {


            /**************************************
             * Speed1 and OP3 codes for first pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************************
             * Speed1 and OP3 codes for second pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (slow backtack)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.second.elements.get(3 + (Values.Fi2t - 1)).steps.get(-Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.second.elements.get(3 + (Values.Fi2t - 1)).steps.get(-Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after second backtack (fast pocket)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi2t - 1)).steps.get(Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi2t - 1)).steps.get(Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (Values.type == 4) {

            /**************************************
             * Speed1 and OP3 codes for first pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(3 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(3 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            /**************************************
             * Speed1 and OP3 codes for second pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(-Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(-Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF before second backtack (fast pocket)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (Values.type == 5) {

            /**************************************
             * Speed1 and OP3 codes for first pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (slow backtack)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi2t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi2t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi2t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi2t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi2t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi2t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi2t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi2t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************************
             * Speed1 and OP3 codes for second pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(-Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(-Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before second backtack (fast pocket)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(6 + (Values.Fi2t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (Values.type == 6) {

            /**************************************
             * OP2 and OP1 codes for first pocket
             ***************************************/

            try {
                if (Values.OP2On >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP2On == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            // TODO Questo codice è su un feed e quindi va a finire al primo punto del feed
            try {
                if (Values.OP1On >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP1On == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            // TODO Questo codice è su un feed e quindi non viene messo
            try {
                if (Values.OP2Off >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP2Off == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************************
             * Speed1 and OP3 codes for first pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after first backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF before second backtack (fast pocket)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(6 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF after second backtack (fast pocket)

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON before first backtack (fast pocket)

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(13 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(12 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************************
             * OP2 and OP1 codes for first pocket
             ***************************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        return ricette;
    }




    /*

                                        Model 5 Shell:

                P13 ---------------------------------------------------------- P1
                    (                                                        )
                     (                                                      )
                      (                                                    )
                   P12 (                                                  ) P2
                        (                                                )
                         (                                              )
                     P11  (                                            ) P3
                            (                                        )
                           P10 (                                   ) P4
                                (                                )
                                P9 (                          ) P5
                                      (                    )
                                       P8 (            ) P6
                                              ------
                                                P7

                                         Model 5 Shell:
                    M + (1/2 H travetta)
                       |
                       |
                P13 ------ P14                                      P26 ------ P1
                    (     (                                            )     )
                     (     (                                          )     )
                     (     (                                          )     )-/ --- M
                   P12 (     ( P15                              P25 )     ) P2
                        (     (                                    )     )
                        (     (                                    )     )
                     P11 (     ( P16                          P24 )     ) P3
                            (     (                            )     )
                           P10 (     ( P17              P23 )     ) P4
                                 (     ( P18          P22 )     )
                                P9 (     (               )     ) P5
                                      (    (P19 P20 P21)     )
                                      P8 (      ----      ) P6
                                            (         )
                                               ------
                                                 P7

     */

    /**
     * Function for calculate the draw points from the shell points
     *
     * @param type
     * @param fileName
     * @param pocketValues
     * @return
     */
    public static TreeMap<String, PointF> Model_5_Points(int type, String fileName, HashMap<String, Float> pocketValues) {

        TreeMap<String, PointF> pointsMap = new TreeMap();

        HashMap<String, PointF> pointsPTS = ReadPointsFromPTS(fileName);

        float topLeftMargin_Backtack = pocketValues.get("M") + pocketValues.get("B") / 2;
        float topRightMargin_Backtack = pocketValues.get("M") + pocketValues.get("G") / 2;
        if (type == 5) {
            topLeftMargin_Backtack = pocketValues.get("M");
            topRightMargin_Backtack = pocketValues.get("M");
        }

        List<Float> equaz;
        List<Float> equaz1;

        if (!pointsPTS.get("P6").equals(pointsPTS.get("P7")) && !pointsPTS.get("P8").equals(pointsPTS.get("P9"))) {

            //2 Arcs

            CenterPointRadius Info_C1 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));
            Float R1 = Info_C1.radius;
            PointF Centro1 = new PointF(Info_C1.center.x, Info_C1.center.y);

            CenterPointRadius Info_C2 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P7"), pointsPTS.get("P8"), pointsPTS.get("P9"));
            Float R2 = Info_C2.radius;
            PointF Centro2 = new PointF(Info_C2.center.x, Info_C2.center.y);

            ArrayList<PointF> Inters_P5_ristretto = MathGeoTri.FindArcArcIntersections(Centro1.x, Centro1.y, R1 - pocketValues.get("M"), Centro2.x, Centro2.y, R2 - pocketValues.get("M"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P5_ristretto.get(0).x, Inters_P5_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P5_ristretto.get(1).x, Inters_P5_ristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P7", Inters_P5_ristretto.get(0));
            } else {
                pointsMap.put("P7", Inters_P5_ristretto.get(1));
            }
        } else if (pointsPTS.get("P6").equals(pointsPTS.get("P7")) && !pointsPTS.get("P8").equals(pointsPTS.get("P9"))) {

            //1 Arc

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), pocketValues.get("M"));

            // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
            float Inter400 = ((-equaz.get(1)) * 400F - equaz.get(2)) / equaz.get(0);
            float Inter0 = ((-equaz.get(1)) * 0F - equaz.get(2)) / equaz.get(0);

            CenterPointRadius Info_C2 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P7"), pointsPTS.get("P8"), pointsPTS.get("P9"));
            Float R2 = Info_C2.radius;
            PointF Centro2 = new PointF(Info_C2.center.x, Info_C2.center.y);

            ArrayList<PointF> Inters_P5_ristretto = MathGeoTri.CircleStraightLineIntersection(new PointF((400F), Inter400), new PointF(0F, Inter0), Centro2, R2 - pocketValues.get("M"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P5_ristretto.get(0).x, Inters_P5_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P5_ristretto.get(1).x, Inters_P5_ristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P7", Inters_P5_ristretto.get(0));
            } else {
                pointsMap.put("P7", Inters_P5_ristretto.get(1));
            }
        } else if (!pointsPTS.get("P6").equals(pointsPTS.get("P7")) && pointsPTS.get("P8").equals(pointsPTS.get("P9"))) {

            //1 Arc

            CenterPointRadius Info_C1 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));
            Float R1 = Info_C1.radius;
            PointF Centro1 = new PointF(Info_C1.center.x, Info_C1.center.y);

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P9"), -pocketValues.get("M"));

            // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
            float Inter400 = ((-equaz.get(1)) * 400F - equaz.get(2)) / equaz.get(0);
            float Inter0 = ((-equaz.get(1)) * 0F - equaz.get(2)) / equaz.get(0);

            ArrayList<PointF> Inters_P5_ristretto = MathGeoTri.CircleStraightLineIntersection(new PointF((400F), Inter400), new PointF(0F, Inter0), Centro1, R1 - pocketValues.get("M"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P5_ristretto.get(0).x, Inters_P5_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P5_ristretto.get(1).x, Inters_P5_ristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P7", Inters_P5_ristretto.get(0));
            } else {
                pointsMap.put("P7", Inters_P5_ristretto.get(1));
            }
        } else if (pointsPTS.get("P6").equals(pointsPTS.get("P7")) && pointsPTS.get("P8").equals(pointsPTS.get("P9"))) {

            /** TESTED */

            //2 Lines

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), pocketValues.get("M"));
            equaz1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P9"), -pocketValues.get("M"));

            PointF inter = MathGeoTri.CalculateStraightLinesIntersection(equaz.get(0), equaz.get(1), equaz.get(2), equaz1.get(0), equaz1.get(1), equaz1.get(2));
            pointsMap.put("P7", inter);
        }

        //region Backtacks-Sx
        float startx = pointsPTS.get("P1").x + pocketValues.get("M");
        float starty = pointsPTS.get("P1").y + topRightMargin_Backtack;
        float endx = pointsPTS.get("P1").x + pocketValues.get("M") + pocketValues.get("F");
        float endy = pointsPTS.get("P1").y + topRightMargin_Backtack;
        pointsMap.put("P1", new PointF(startx, starty));
        pointsMap.put("P26", new PointF(endx, endy));
        //endregion

        //region Backtacks-Dx
        startx = pointsPTS.get("P13").x - pocketValues.get("M");
        starty = pointsPTS.get("P13").y + topLeftMargin_Backtack;
        endx = pointsPTS.get("P13").x - pocketValues.get("M") - pocketValues.get("A");
        endy = pointsPTS.get("P13").y + topLeftMargin_Backtack;
        pointsMap.put("P13", new PointF(startx, starty));
        pointsMap.put("P14", new PointF(endx, endy));
        //endregion

        float Sagitta = pocketValues.get("M1");

        if (Sagitta <= 1) {

            /** TESTED */

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P9"), pointsPTS.get("P11"), -pocketValues.get("M"));
            equaz1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P11"), pointsPTS.get("P13"), -pocketValues.get("M"));
            PointF inter = MathGeoTri.CalculateStraightLinesIntersection(equaz.get(0), equaz.get(1), equaz.get(2), equaz1.get(0), equaz1.get(1), equaz1.get(2));
            pointsMap.put("P11", inter);

        } else if (!pointsPTS.get("P12").equals(pointsPTS.get("P13")) && !pointsPTS.get("P10").equals(pointsPTS.get("P11"))) {

            CenterPointRadius Info_Csx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P11"), pointsPTS.get("P12"), pointsPTS.get("P13"));
            Float R3 = Info_Csx.radius;
            PointF Centro3 = new PointF(Info_Csx.center.x, Info_Csx.center.y);

            CenterPointRadius Info_C2 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P9"), pointsPTS.get("P10"), pointsPTS.get("P11"));
            Float R2 = Info_C2.radius;
            PointF Centro2 = new PointF(Info_C2.center.x, Info_C2.center.y);

            ArrayList<PointF> Inters_P7_ristretto = MathGeoTri.FindArcArcIntersections(Centro3.x, Centro3.y, R3 - pocketValues.get("M"), Centro2.x, Centro2.y, R2 - pocketValues.get("M"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P11").x, pointsPTS.get("P11").y, Inters_P7_ristretto.get(0).x, Inters_P7_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P11").x, pointsPTS.get("P11").y, Inters_P7_ristretto.get(1).x, Inters_P7_ristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P11", Inters_P7_ristretto.get(0));
            } else {
                pointsMap.put("P11", Inters_P7_ristretto.get(1));
            }

            pointsMap.put("P12", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P11"), pointsMap.get("P13"), R3 - pocketValues.get("M"), Centro3, Sagitta));
        } else if (!pointsPTS.get("P12").equals(pointsPTS.get("P13")) && pointsPTS.get("P10").equals(pointsPTS.get("P11"))) {

            /** TESTED */

            CenterPointRadius Info_Csx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P11"), pointsPTS.get("P12"), pointsPTS.get("P13"));
            Float R3 = Info_Csx.radius;
            PointF Centro3 = new PointF(Info_Csx.center.x, Info_Csx.center.y);

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P11"), pointsPTS.get("P9"), -pocketValues.get("M"));

            // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
            float Inter400 = ((-equaz.get(1)) * 400F - equaz.get(2)) / equaz.get(0);
            float Inter0 = ((-equaz.get(1)) * 0F - equaz.get(2)) / equaz.get(0);

            ArrayList<PointF> Inters_P11_ristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro3, R3 - pocketValues.get("M"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P11").x, pointsPTS.get("P11").y, Inters_P11_ristretto.get(0).x, Inters_P11_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P11").x, pointsPTS.get("P11").y, Inters_P11_ristretto.get(1).x, Inters_P11_ristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P11", Inters_P11_ristretto.get(0));
            } else {
                pointsMap.put("P11", Inters_P11_ristretto.get(1));
            }

            pointsMap.put("P12", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P11"), pointsMap.get("P13"), R3 - pocketValues.get("M"), Centro3, Sagitta));
        }

        Sagitta = pocketValues.get("M2");

        if (Sagitta <= 1) {

            /** TESTED */

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P5"), pocketValues.get("M"));
            equaz1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P1"), pointsPTS.get("P3"), pocketValues.get("M"));
            PointF inter1 = MathGeoTri.CalculateStraightLinesIntersection(equaz.get(0), equaz.get(1), equaz.get(2), equaz1.get(0), equaz1.get(1), equaz1.get(2));
            pointsMap.put("P3", inter1);

        } else if (!pointsPTS.get("P2").equals(pointsPTS.get("P3")) && !pointsPTS.get("P4").equals(pointsPTS.get("P5"))) {
            CenterPointRadius Info_C4 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P1"), pointsPTS.get("P2"), pointsPTS.get("P3"));
            Float R4 = Info_C4.radius;
            PointF Centro4 = new PointF(Info_C4.center.x, Info_C4.center.y);

            CenterPointRadius Info_C1 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P3"), pointsPTS.get("P4"), pointsPTS.get("P5"));
            Float R1 = Info_C1.radius;
            PointF Centro1 = new PointF(Info_C1.center.x, Info_C1.center.y);

            ArrayList<PointF> Inters_P3_ristretto = MathGeoTri.FindArcArcIntersections(Centro1.x, Centro1.y, R1 - pocketValues.get("M"), Centro4.x, Centro4.y, R4 - pocketValues.get("M"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P3").x, pointsPTS.get("P3").y, Inters_P3_ristretto.get(0).x, Inters_P3_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P3").x, pointsPTS.get("P3").y, Inters_P3_ristretto.get(1).x, Inters_P3_ristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P3", Inters_P3_ristretto.get(0));
            } else {
                pointsMap.put("P3", Inters_P3_ristretto.get(1));
            }

            pointsMap.put("P2", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P1"), pointsMap.get("P3"), R4 - pocketValues.get("M"), Centro4, Sagitta));
        } else if (!pointsPTS.get("P2").equals(pointsPTS.get("P3")) && pointsPTS.get("P4").equals(pointsPTS.get("P5"))) {

            /** TESTED */

            CenterPointRadius Info_C4 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P1"), pointsPTS.get("P2"), pointsPTS.get("P3"));
            Float R4 = Info_C4.radius;
            PointF Centro4 = new PointF(Info_C4.center.x, Info_C4.center.y);

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P5"), pocketValues.get("M"));

            // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
            float Inter400 = ((-equaz.get(1)) * 400F - equaz.get(2)) / equaz.get(0);
            float Inter0 = ((-equaz.get(1)) * 0F - equaz.get(2)) / equaz.get(0);

            ArrayList<PointF> Inters_P3_ristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro4, R4 - pocketValues.get("M"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P3").x, pointsPTS.get("P3").y, Inters_P3_ristretto.get(0).x, Inters_P3_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P3").x, pointsPTS.get("P3").y, Inters_P3_ristretto.get(1).x, Inters_P3_ristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P3", Inters_P3_ristretto.get(0));
            } else {
                pointsMap.put("P3", Inters_P3_ristretto.get(1));
            }

            pointsMap.put("P2", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P1"), pointsMap.get("P3"), R4 - pocketValues.get("M"), Centro4, Sagitta));
        }

        Sagitta = pocketValues.get("M3");

        if (Sagitta <= 1) {

            /** TESTED */

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P9"), -pocketValues.get("M"));
            equaz1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P9"), pointsPTS.get("P11"), -pocketValues.get("M"));
            PointF inter = MathGeoTri.CalculateStraightLinesIntersection(equaz.get(0), equaz.get(1), equaz.get(2), equaz1.get(0), equaz1.get(1), equaz1.get(2));
            pointsMap.put("P9", inter);

        } else if (!pointsPTS.get("P10").equals(pointsPTS.get("P11")) && !pointsPTS.get("P8").equals(pointsPTS.get("P9"))) {

            CenterPointRadius Info_Csx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P9"), pointsPTS.get("P10"), pointsPTS.get("P11"));
            Float R3 = Info_Csx.radius;
            PointF Centro3 = new PointF(Info_Csx.center.x, Info_Csx.center.y);

            CenterPointRadius Info_C2 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P7"), pointsPTS.get("P8"), pointsPTS.get("P9"));
            Float R2 = Info_C2.radius;
            PointF Centro2 = new PointF(Info_C2.center.x, Info_C2.center.y);

            ArrayList<PointF> Inters_P7_ristretto = MathGeoTri.FindArcArcIntersections(Centro3.x, Centro3.y, R3 - pocketValues.get("M"), Centro2.x, Centro2.y, R2 - pocketValues.get("M"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P9").x, pointsPTS.get("P9").y, Inters_P7_ristretto.get(0).x, Inters_P7_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P9").x, pointsPTS.get("P9").y, Inters_P7_ristretto.get(1).x, Inters_P7_ristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P9", Inters_P7_ristretto.get(0));
            } else {
                pointsMap.put("P9", Inters_P7_ristretto.get(1));
            }

            pointsMap.put("P10", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P9"), pointsMap.get("P11"), R3 - pocketValues.get("M"), Centro3, Sagitta));
        } else if (!pointsPTS.get("P10").equals(pointsPTS.get("P11")) && pointsPTS.get("P8").equals(pointsPTS.get("P9"))) {

            /** TESTED */

            CenterPointRadius Info_Csx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P9"), pointsPTS.get("P10"), pointsPTS.get("P11"));
            Float R3 = Info_Csx.radius;
            PointF Centro3 = new PointF(Info_Csx.center.x, Info_Csx.center.y);

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P9"), -pocketValues.get("M"));

            // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
            float Inter400 = ((-equaz.get(1)) * 400F - equaz.get(2)) / equaz.get(0);
            float Inter0 = ((-equaz.get(1)) * 0F - equaz.get(2)) / equaz.get(0);

            ArrayList<PointF> Inters_P7_ristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro3, R3 - pocketValues.get("M"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P9").x, pointsPTS.get("P9").y, Inters_P7_ristretto.get(0).x, Inters_P7_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P9").x, pointsPTS.get("P9").y, Inters_P7_ristretto.get(1).x, Inters_P7_ristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P9", Inters_P7_ristretto.get(0));
            } else {
                pointsMap.put("P9", Inters_P7_ristretto.get(1));
            }

            pointsMap.put("P10", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P9"), pointsMap.get("P11"), R3 - pocketValues.get("M"), Centro3, Sagitta));
        }

        Sagitta = pocketValues.get("M4");

        if (Sagitta <= 1) {

            /** TESTED */

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P5"), pocketValues.get("M"));
            equaz1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), pocketValues.get("M"));
            PointF inter1 = MathGeoTri.CalculateStraightLinesIntersection(equaz.get(0), equaz.get(1), equaz.get(2), equaz1.get(0), equaz1.get(1), equaz1.get(2));
            pointsMap.put("P5", inter1);

        } else if (!pointsPTS.get("P4").equals(pointsPTS.get("P5")) && !pointsPTS.get("P6").equals(pointsPTS.get("P7"))) {

            CenterPointRadius Info_C4 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P3"), pointsPTS.get("P4"), pointsPTS.get("P5"));
            Float R4 = Info_C4.radius;
            PointF Centro4 = new PointF(Info_C4.center.x, Info_C4.center.y);

            CenterPointRadius Info_C1 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));
            Float R1 = Info_C1.radius;
            PointF Centro1 = new PointF(Info_C1.center.x, Info_C1.center.y);

            ArrayList<PointF> Inters_P3_ristretto = MathGeoTri.FindArcArcIntersections(Centro1.x, Centro1.y, R1 - pocketValues.get("M"), Centro4.x, Centro4.y, R4 - pocketValues.get("M"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P3_ristretto.get(0).x, Inters_P3_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P3_ristretto.get(1).x, Inters_P3_ristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P5", Inters_P3_ristretto.get(0));
            } else {
                pointsMap.put("P5", Inters_P3_ristretto.get(1));
            }

            pointsMap.put("P4", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P3"), pointsMap.get("P5"), R4 - pocketValues.get("M"), Centro4, Sagitta));
        } else if (!pointsPTS.get("P4").equals(pointsPTS.get("P5")) && pointsPTS.get("P6").equals(pointsPTS.get("P7"))) {

            /** TESTED */

            CenterPointRadius Info_C4 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P3"), pointsPTS.get("P4"), pointsPTS.get("P5"));
            Float R4 = Info_C4.radius;
            PointF Centro4 = new PointF(Info_C4.center.x, Info_C4.center.y);

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), pocketValues.get("M"));

            // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
            float Inter400 = ((-equaz.get(1)) * 400F - equaz.get(2)) / equaz.get(0);
            float Inter0 = ((-equaz.get(1)) * 0F - equaz.get(2)) / equaz.get(0);

            ArrayList<PointF> Inters_P3_ristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro4, R4 - pocketValues.get("M"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P3_ristretto.get(0).x, Inters_P3_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P3_ristretto.get(1).x, Inters_P3_ristretto.get(1).y);


            if (Dist1 < Dist2) {
                pointsMap.put("P5", Inters_P3_ristretto.get(0));
            } else {
                pointsMap.put("P5", Inters_P3_ristretto.get(1));
            }

            pointsMap.put("P4", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P3"), pointsMap.get("P5"), R4 - pocketValues.get("M"), Centro4, Sagitta));
        }

        Sagitta = pocketValues.get("M5");

        if (Sagitta <= 1) {
            // I already have all
        } else if (!pointsPTS.get("P8").equals(pointsPTS.get("P9")) && !pointsPTS.get("P10").equals(pointsPTS.get("P11"))) {

            CenterPointRadius Info_Csx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P7"), pointsPTS.get("P8"), pointsPTS.get("P9"));
            Float R3 = Info_Csx.radius;
            PointF Centro3 = new PointF(Info_Csx.center.x, Info_Csx.center.y);

            /*CenterPointRadius Info_C2 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));
            Float R2 = Info_C2.radius;
            PointF Centro2 = new PointF(Info_C2.center.x, Info_C2.center.y);*/

            // I already have P9

            /*ArrayList<PointF> Inters_P7_ristretto = MathGeoTri.FindArcArcIntersections(Centro3.x, Centro3.y, R3 - pocketValues.get("M"), Centro2.x, Centro2.y, R2 - pocketValues.get("M"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P9").x, pointsPTS.get("P9").y, Inters_P7_ristretto.get(0).x, Inters_P7_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P9").x, pointsPTS.get("P9").y, Inters_P7_ristretto.get(1).x, Inters_P7_ristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P9", Inters_P7_ristretto.get(0));
            } else {
                pointsMap.put("P9", Inters_P7_ristretto.get(1));
            }*/

            pointsMap.put("P8", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P7"), pointsMap.get("P9"), R3 - pocketValues.get("M"), Centro3, Sagitta));
        } else if (!pointsPTS.get("P8").equals(pointsPTS.get("P9")) && pointsPTS.get("P10").equals(pointsPTS.get("P11"))) {

            /** TESTED */

            CenterPointRadius Info_Csx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P7"), pointsPTS.get("P8"), pointsPTS.get("P9"));
            Float R3 = Info_Csx.radius;
            PointF Centro3 = new PointF(Info_Csx.center.x, Info_Csx.center.y);

            // I already have P9

            /*equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P9"), pointsPTS.get("P11"), pocketValues.get("M"));

            // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
            float Inter400 = ((-equaz.get(1)) * 400F - equaz.get(2)) / equaz.get(0);
            float Inter0 = ((-equaz.get(1)) * 0F - equaz.get(2)) / equaz.get(0);

            ArrayList<PointF> Inters_P7_ristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro3, R3 - pocketValues.get("M"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P9").x, pointsPTS.get("P9").y, Inters_P7_ristretto.get(0).x, Inters_P7_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P9").x, pointsPTS.get("P9").y, Inters_P7_ristretto.get(1).x, Inters_P7_ristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P9", Inters_P7_ristretto.get(0));
            } else {
                pointsMap.put("P9", Inters_P7_ristretto.get(1));
            }*/

            pointsMap.put("P8", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P7"), pointsMap.get("P9"), R3 - pocketValues.get("M"), Centro3, Sagitta));
        }

        Sagitta = pocketValues.get("M6");

        if (Sagitta <= 1) {
            // I already have all
        } else if (!pointsPTS.get("P6").equals(pointsPTS.get("P7")) && !pointsPTS.get("P4").equals(pointsPTS.get("P5"))) {

            CenterPointRadius Info_C4 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));
            Float R4 = Info_C4.radius;
            PointF Centro4 = new PointF(Info_C4.center.x, Info_C4.center.y);

            // I already have P5

            /*CenterPointRadius Info_C1 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));
            Float R1 = Info_C1.radius;
            PointF Centro1 = new PointF(Info_C1.center.x, Info_C1.center.y);

            ArrayList<PointF> Inters_P3_ristretto = MathGeoTri.FindArcArcIntersections(Centro1.x, Centro1.y, R1 - pocketValues.get("M"), Centro4.x, Centro4.y, R4 - pocketValues.get("M"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P3_ristretto.get(0).x, Inters_P3_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P3_ristretto.get(1).x, Inters_P3_ristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P5", Inters_P3_ristretto.get(0));
            } else {
                pointsMap.put("P5", Inters_P3_ristretto.get(1));
            }*/

            pointsMap.put("P6", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P5"), pointsMap.get("P7"), R4 - pocketValues.get("M"), Centro4, Sagitta));
        } else if (!pointsPTS.get("P6").equals(pointsPTS.get("P7")) && pointsPTS.get("P4").equals(pointsPTS.get("P5"))) {

            /** TESTED */

            CenterPointRadius Info_C4 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));
            Float R4 = Info_C4.radius;
            PointF Centro4 = new PointF(Info_C4.center.x, Info_C4.center.y);

            // I already have P5

            /*equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), pocketValues.get("M"));

            // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
            float Inter400 = ((-equaz.get(1)) * 400F - equaz.get(2)) / equaz.get(0);
            float Inter0 = ((-equaz.get(1)) * 0F - equaz.get(2)) / equaz.get(0);

            ArrayList<PointF> Inters_P3_ristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro4, R4 - pocketValues.get("M"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P3_ristretto.get(0).x, Inters_P3_ristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P3_ristretto.get(1).x, Inters_P3_ristretto.get(1).y);


            if (Dist1 < Dist2) {
                pointsMap.put("P5", Inters_P3_ristretto.get(0));
            } else {
                pointsMap.put("P5", Inters_P3_ristretto.get(1));
            }*/

            pointsMap.put("P6", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P5"), pointsMap.get("P7"), R4 - pocketValues.get("M"), Centro4, Sagitta));
        }

        /*************************************/
        /** START INTERNAL PART */
        /*************************************/

        if (!pointsPTS.get("P6").equals(pointsPTS.get("P7")) && !pointsPTS.get("P8").equals(pointsPTS.get("P9"))) {
            CenterPointRadius Info_C1_R = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));
            Float R1_R = Info_C1_R.radius;
            PointF Centro1_R = new PointF(Info_C1_R.center.x, Info_C1_R.center.y);

            CenterPointRadius Info_C2_R = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P7"), pointsPTS.get("P8"), pointsPTS.get("P9"));
            Float R2_R = Info_C2_R.radius;
            PointF Centro2_R = new PointF(Info_C2_R.center.x, Info_C2_R.center.y);

            ArrayList<PointF> Inters_P5_ristrettoristretto = MathGeoTri.FindArcArcIntersections(Centro1_R.x, Centro1_R.y, R1_R - pocketValues.get("M") - pocketValues.get("D"), Centro2_R.x, Centro2_R.y, R2_R - pocketValues.get("M") - pocketValues.get("E"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P5_ristrettoristretto.get(0).x, Inters_P5_ristrettoristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P5_ristrettoristretto.get(1).x, Inters_P5_ristrettoristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P20", Inters_P5_ristrettoristretto.get(0));
            } else {
                pointsMap.put("P20", Inters_P5_ristrettoristretto.get(1));
            }
        } else if (pointsPTS.get("P6").equals(pointsPTS.get("P7")) && !pointsPTS.get("P8").equals(pointsPTS.get("P9"))) {

            CenterPointRadius Info_C2_R = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P7"), pointsPTS.get("P8"), pointsPTS.get("P9"));
            Float R2_R = Info_C2_R.radius;
            PointF Centro2_R = new PointF(Info_C2_R.center.x, Info_C2_R.center.y);

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), pocketValues.get("M") + pocketValues.get("D"));

            // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
            float Inter400 = ((-equaz.get(1)) * 400F - equaz.get(2)) / equaz.get(0);
            float Inter0 = ((-equaz.get(1)) * 0F - equaz.get(2)) / equaz.get(0);

            ArrayList<PointF> Inters_P5_ristrettoristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro2_R, R2_R - pocketValues.get("M") - pocketValues.get("E"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P5_ristrettoristretto.get(0).x, Inters_P5_ristrettoristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P5_ristrettoristretto.get(1).x, Inters_P5_ristrettoristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P20", Inters_P5_ristrettoristretto.get(0));
            } else {
                pointsMap.put("P20", Inters_P5_ristrettoristretto.get(1));
            }
        } else if (!pointsPTS.get("P6").equals(pointsPTS.get("P7")) && pointsPTS.get("P8").equals(pointsPTS.get("P9"))) {

            CenterPointRadius Info_C1_R = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));
            Float R1_R = Info_C1_R.radius;
            PointF Centro1_R = new PointF(Info_C1_R.center.x, Info_C1_R.center.y);

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P9"), -pocketValues.get("M") - pocketValues.get("E"));

            // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
            float Inter400 = ((-equaz.get(1)) * 400F - equaz.get(2)) / equaz.get(0);
            float Inter0 = ((-equaz.get(1)) * 0F - equaz.get(2)) / equaz.get(0);

            ArrayList<PointF> Inters_P5_ristrettoristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro1_R, R1_R - pocketValues.get("M") - pocketValues.get("D"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P5_ristrettoristretto.get(0).x, Inters_P5_ristrettoristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P7").x, pointsPTS.get("P7").y, Inters_P5_ristrettoristretto.get(1).x, Inters_P5_ristrettoristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P20", Inters_P5_ristrettoristretto.get(0));
            } else {
                pointsMap.put("P20", Inters_P5_ristrettoristretto.get(1));
            }
        } else if (pointsPTS.get("P6").equals(pointsPTS.get("P7")) && pointsPTS.get("P8").equals(pointsPTS.get("P9"))) {

            /** TESTED */

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), pocketValues.get("M") + pocketValues.get("D"));
            equaz1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P9"), -pocketValues.get("M") - pocketValues.get("E"));
            PointF inter = MathGeoTri.CalculateStraightLinesIntersection(equaz.get(0), equaz.get(1), equaz.get(2), equaz1.get(0), equaz1.get(1), equaz1.get(2));
            pointsMap.put("P20", inter);
        }

        Sagitta = pocketValues.get("M5");

        if (Sagitta <= 1) {

            /** TESTED */

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P7"), pointsPTS.get("P9"), -pocketValues.get("M") - pocketValues.get("D"));
            equaz1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P9"), pointsPTS.get("P11"), -pocketValues.get("M") - pocketValues.get("D"));
            PointF inter = MathGeoTri.CalculateStraightLinesIntersection(equaz.get(0), equaz.get(1), equaz.get(2), equaz1.get(0), equaz1.get(1), equaz1.get(2));
            pointsMap.put("P18", inter);

        } else if (!pointsPTS.get("P8").equals(pointsPTS.get("P9")) && !pointsPTS.get("P10").equals(pointsPTS.get("P11"))) {

            CenterPointRadius Info_Csx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P7"), pointsPTS.get("P8"), pointsPTS.get("P9"));
            Float R3 = Info_Csx.radius;
            PointF Centro3 = new PointF(Info_Csx.center.x, Info_Csx.center.y);

            CenterPointRadius Info_C2 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P9"), pointsPTS.get("P10"), pointsPTS.get("P11"));
            Float R2 = Info_C2.radius;
            PointF Centro2 = new PointF(Info_C2.center.x, Info_C2.center.y);

            ArrayList<PointF> Inters_P7_ristrettoristretto = MathGeoTri.FindArcArcIntersections(Centro3.x, Centro3.y, R3 - pocketValues.get("M") - pocketValues.get("E"), Centro2.x, Centro2.y, R2 - pocketValues.get("M") - pocketValues.get("E"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P9").x, pointsPTS.get("P9").y, Inters_P7_ristrettoristretto.get(0).x, Inters_P7_ristrettoristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P9").x, pointsPTS.get("P9").y, Inters_P7_ristrettoristretto.get(1).x, Inters_P7_ristrettoristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P18", Inters_P7_ristrettoristretto.get(0));
            } else {
                pointsMap.put("P18", Inters_P7_ristrettoristretto.get(1));
            }

            pointsMap.put("P19", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P18"), pointsMap.get("P20"), R3 - pocketValues.get("E") - pocketValues.get("M"), Centro3, Sagitta));
        } else if (!pointsPTS.get("P8").equals(pointsPTS.get("P9")) && pointsPTS.get("P10").equals(pointsPTS.get("P11"))) {
            CenterPointRadius Info_Casx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P7"), pointsPTS.get("P8"), pointsPTS.get("P9"));
            Float R3_asx = Info_Casx.radius;
            PointF Centro3_asx = new PointF(Info_Casx.center.x, Info_Casx.center.y);

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P9"), pointsPTS.get("P11"), -pocketValues.get("M") - pocketValues.get("E"));

            // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
            float Inter400 = ((-equaz.get(1)) * 400F - equaz.get(2)) / equaz.get(0);
            float Inter0 = ((-equaz.get(1)) * 0F - equaz.get(2)) / equaz.get(0);

            ArrayList<PointF> Inters_P7_ristrettoristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro3_asx, R3_asx - pocketValues.get("M") - pocketValues.get("E"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P9").x, pointsPTS.get("P9").y, Inters_P7_ristrettoristretto.get(0).x, Inters_P7_ristrettoristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P9").x, pointsPTS.get("P9").y, Inters_P7_ristrettoristretto.get(1).x, Inters_P7_ristrettoristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P18", Inters_P7_ristrettoristretto.get(0));
            } else {
                pointsMap.put("P18", Inters_P7_ristrettoristretto.get(1));
            }

            pointsMap.put("P19", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P18"), pointsMap.get("P20"), R3_asx - pocketValues.get("E") - pocketValues.get("M"), Centro3_asx, Sagitta));
        }

        Sagitta = pocketValues.get("M3");

        if (Sagitta <= 1) {

            /** TESTED */

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P9"), pointsPTS.get("P11"), -pocketValues.get("M") - pocketValues.get("D"));
            equaz1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P11"), pointsPTS.get("P13"), -pocketValues.get("M") - pocketValues.get("D"));
            PointF inter = MathGeoTri.CalculateStraightLinesIntersection(equaz.get(0), equaz.get(1), equaz.get(2), equaz1.get(0), equaz1.get(1), equaz1.get(2));
            pointsMap.put("P16", inter);

        } else if (!pointsPTS.get("P10").equals(pointsPTS.get("P11")) && !pointsPTS.get("P12").equals(pointsPTS.get("P13"))) {

            CenterPointRadius Info_Csx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P9"), pointsPTS.get("P10"), pointsPTS.get("P11"));
            Float R3 = Info_Csx.radius;
            PointF Centro3 = new PointF(Info_Csx.center.x, Info_Csx.center.y);

            CenterPointRadius Info_C2 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P11"), pointsPTS.get("P12"), pointsPTS.get("P13"));
            Float R2 = Info_C2.radius;
            PointF Centro2 = new PointF(Info_C2.center.x, Info_C2.center.y);

            ArrayList<PointF> Inters_P7_ristrettoristretto = MathGeoTri.FindArcArcIntersections(Centro3.x, Centro3.y, R3 - pocketValues.get("M") - pocketValues.get("E"), Centro2.x, Centro2.y, R2 - pocketValues.get("M") - pocketValues.get("E"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P11").x, pointsPTS.get("P11").y, Inters_P7_ristrettoristretto.get(0).x, Inters_P7_ristrettoristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P11").x, pointsPTS.get("P11").y, Inters_P7_ristrettoristretto.get(1).x, Inters_P7_ristrettoristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P16", Inters_P7_ristrettoristretto.get(0));
            } else {
                pointsMap.put("P16", Inters_P7_ristrettoristretto.get(1));
            }

            pointsMap.put("P17", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P16"), pointsMap.get("P18"), R3 - pocketValues.get("E") - pocketValues.get("M"), Centro3, Sagitta));
        } else if (!pointsPTS.get("P10").equals(pointsPTS.get("P11")) && pointsPTS.get("P12").equals(pointsPTS.get("P13"))) {

            /** TESTED */

            CenterPointRadius Info_Casx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P9"), pointsPTS.get("P10"), pointsPTS.get("P11"));
            Float R3_asx = Info_Casx.radius;
            PointF Centro3_asx = new PointF(Info_Casx.center.x, Info_Casx.center.y);

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P11"), pointsPTS.get("P13"), -pocketValues.get("M") - pocketValues.get("E"));

            // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
            float Inter400 = ((-equaz.get(1)) * 400F - equaz.get(2)) / equaz.get(0);
            float Inter0 = ((-equaz.get(1)) * 0F - equaz.get(2)) / equaz.get(0);

            ArrayList<PointF> Inters_P7_ristrettoristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro3_asx, R3_asx - pocketValues.get("M") - pocketValues.get("E"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P11").x, pointsPTS.get("P11").y, Inters_P7_ristrettoristretto.get(0).x, Inters_P7_ristrettoristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P11").x, pointsPTS.get("P11").y, Inters_P7_ristrettoristretto.get(1).x, Inters_P7_ristrettoristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P16", Inters_P7_ristrettoristretto.get(0));
            } else {
                pointsMap.put("P16", Inters_P7_ristrettoristretto.get(1));
            }

            pointsMap.put("P17", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P16"), pointsMap.get("P18"), R3_asx - pocketValues.get("E") - pocketValues.get("M"), Centro3_asx, Sagitta));
        }

        Sagitta = pocketValues.get("M6");

        if (Sagitta <= 1) {

            /** TESTED */

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P5"), pocketValues.get("M") + pocketValues.get("E"));
            equaz1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P5"), pointsPTS.get("P7"), pocketValues.get("M") + pocketValues.get("E"));
            PointF inter1 = MathGeoTri.CalculateStraightLinesIntersection(equaz.get(0), equaz.get(1), equaz.get(2), equaz1.get(0), equaz1.get(1), equaz1.get(2));
            pointsMap.put("P22", inter1);
        } else if (!pointsPTS.get("P6").equals(pointsPTS.get("P7")) && !pointsPTS.get("P4").equals(pointsPTS.get("P5"))) {

            CenterPointRadius Info_C4 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));
            Float R4 = Info_C4.radius;
            PointF Centro4 = new PointF(Info_C4.center.x, Info_C4.center.y);

            CenterPointRadius Info_C1 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P3"), pointsPTS.get("P4"), pointsPTS.get("P5"));
            Float R1 = Info_C1.radius;
            PointF Centro1 = new PointF(Info_C1.center.x, Info_C1.center.y);

            ArrayList<PointF> Inters_P3_ristrettoristretto = MathGeoTri.FindArcArcIntersections(Centro1.x, Centro1.y, R1 - pocketValues.get("M") - pocketValues.get("D"), Centro4.x, Centro4.y, R4 - pocketValues.get("M") - pocketValues.get("D"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P3_ristrettoristretto.get(0).x, Inters_P3_ristrettoristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P3_ristrettoristretto.get(1).x, Inters_P3_ristrettoristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P22", Inters_P3_ristrettoristretto.get(0));
            } else {
                pointsMap.put("P22", Inters_P3_ristrettoristretto.get(1));
            }

            pointsMap.put("P21", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P20"), pointsMap.get("P22"), R4 - pocketValues.get("D") - pocketValues.get("M"), Centro4, Sagitta));
        } else if (!pointsPTS.get("P6").equals(pointsPTS.get("P7")) && pointsPTS.get("P4").equals(pointsPTS.get("P5"))) {

            CenterPointRadius Info_C4 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P5"), pointsPTS.get("P6"), pointsPTS.get("P7"));
            Float R4 = Info_C4.radius;
            PointF Centro4 = new PointF(Info_C4.center.x, Info_C4.center.y);

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P5"), pocketValues.get("M") + pocketValues.get("D"));

            // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
            float Inter400 = ((-equaz.get(1)) * 400F - equaz.get(2)) / equaz.get(0);
            float Inter0 = ((-equaz.get(1)) * 0F - equaz.get(2)) / equaz.get(0);

            ArrayList<PointF> Inters_P3_ristrettoristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro4, R4 - pocketValues.get("M") - pocketValues.get("D"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P3_ristrettoristretto.get(0).x, Inters_P3_ristrettoristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P5").x, pointsPTS.get("P5").y, Inters_P3_ristrettoristretto.get(1).x, Inters_P3_ristrettoristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P22", Inters_P3_ristrettoristretto.get(0));
            } else {
                pointsMap.put("P22", Inters_P3_ristrettoristretto.get(1));
            }

            pointsMap.put("P21", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P20"), pointsMap.get("P22"), R4 - pocketValues.get("D") - pocketValues.get("M"), Centro4, Sagitta));
        }

        Sagitta = pocketValues.get("M4");

        if (Sagitta <= 1) {

            /** TESTED */

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P3"), pointsPTS.get("P5"), pocketValues.get("M") + pocketValues.get("E"));
            equaz1 = MathGeoTri.CalculateParallelLine(pointsPTS.get("P1"), pointsPTS.get("P3"), pocketValues.get("M") + pocketValues.get("E"));
            PointF inter1 = MathGeoTri.CalculateStraightLinesIntersection(equaz.get(0), equaz.get(1), equaz.get(2), equaz1.get(0), equaz1.get(1), equaz1.get(2));
            pointsMap.put("P24", inter1);

        } else if (!pointsPTS.get("P4").equals(pointsPTS.get("P5")) && !pointsPTS.get("P2").equals(pointsPTS.get("P3"))) {

            CenterPointRadius Info_C4 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P3"), pointsPTS.get("P4"), pointsPTS.get("P5"));
            Float R4 = Info_C4.radius;
            PointF Centro4 = new PointF(Info_C4.center.x, Info_C4.center.y);

            CenterPointRadius Info_C1 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P1"), pointsPTS.get("P2"), pointsPTS.get("P3"));
            Float R1 = Info_C1.radius;
            PointF Centro1 = new PointF(Info_C1.center.x, Info_C1.center.y);

            ArrayList<PointF> Inters_P3_ristrettoristretto = MathGeoTri.FindArcArcIntersections(Centro1.x, Centro1.y, R1 - pocketValues.get("M") - pocketValues.get("D"), Centro4.x, Centro4.y, R4 - pocketValues.get("M") - pocketValues.get("D"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P3").x, pointsPTS.get("P3").y, Inters_P3_ristrettoristretto.get(0).x, Inters_P3_ristrettoristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P3").x, pointsPTS.get("P3").y, Inters_P3_ristrettoristretto.get(1).x, Inters_P3_ristrettoristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P24", Inters_P3_ristrettoristretto.get(0));
            } else {
                pointsMap.put("P24", Inters_P3_ristrettoristretto.get(1));
            }

            pointsMap.put("P23", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P22"), pointsMap.get("P24"), R4 - pocketValues.get("D") - pocketValues.get("M"), Centro4, Sagitta));
        } else if (!pointsPTS.get("P4").equals(pointsPTS.get("P5")) && pointsPTS.get("P2").equals(pointsPTS.get("P3"))) {

            /** TESTED */

            CenterPointRadius Info_C4 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P3"), pointsPTS.get("P4"), pointsPTS.get("P5"));
            Float R4 = Info_C4.radius;
            PointF Centro4 = new PointF(Info_C4.center.x, Info_C4.center.y);

            equaz = MathGeoTri.CalculateParallelLine(pointsPTS.get("P1"), pointsPTS.get("P3"), pocketValues.get("M") + pocketValues.get("D"));

            // ax + by + c = 0 --> by = -ax - c --> y = (-ax - c)/b
            float Inter400 = ((-equaz.get(1)) * 400F - equaz.get(2)) / equaz.get(0);
            float Inter0 = ((-equaz.get(1)) * 0F - equaz.get(2)) / equaz.get(0);

            ArrayList<PointF> Inters_P3_ristrettoristretto = MathGeoTri.CircleStraightLineIntersection(new PointF(400F, Inter400), new PointF(0F, Inter0), Centro4, R4 - pocketValues.get("M") - pocketValues.get("D"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P3").x, pointsPTS.get("P3").y, Inters_P3_ristrettoristretto.get(0).x, Inters_P3_ristrettoristretto.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P3").x, pointsPTS.get("P3").y, Inters_P3_ristrettoristretto.get(1).x, Inters_P3_ristrettoristretto.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P24", Inters_P3_ristrettoristretto.get(0));
            } else {
                pointsMap.put("P24", Inters_P3_ristrettoristretto.get(1));
            }

            pointsMap.put("P23", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P22"), pointsMap.get("P24"), R4 - pocketValues.get("D") - pocketValues.get("M"), Centro4, Sagitta));
        }

        Sagitta = pocketValues.get("M2");

        if (Sagitta <= 1) {
            // I already have P26 (from the backtack part) and the P24 points, so i don't need nothing more
        } else {

            /** TESTED */

            CenterPointRadius Info_C4 = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P1"), pointsPTS.get("P2"), pointsPTS.get("P3"));
            Float R4 = Info_C4.radius;
            PointF Centro4 = new PointF(Info_C4.center.x, Info_C4.center.y);

            // I already have P26

            /*ArrayList<PointF> P1ristrettoristretto_list = MathGeoTri.CircleStraightLineIntersection(new PointF(pointsPTS.get("P13").x, pointsPTS.get("P13").y + topLeftMargin_Backtack), new PointF(pointsPTS.get("P1").x, pointsPTS.get("P1").y + topLeftMargin_Backtack), Centro4, R4 - pocketValues.get("M") - pocketValues.get("A"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P13").x, pointsPTS.get("P13").y, P1ristrettoristretto_list.get(0).x, P1ristrettoristretto_list.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P13").x, pointsPTS.get("P13").y, P1ristrettoristretto_list.get(1).x, P1ristrettoristretto_list.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P26", P1ristrettoristretto_list.get(0));
            } else {
                pointsMap.put("P26", P1ristrettoristretto_list.get(1));
            }*/

            pointsMap.put("P25", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P26"), pointsMap.get("P24"), R4 - (pocketValues.get("A") + pocketValues.get("D")) / 2 - pocketValues.get("M"), Centro4, Sagitta));
        }

        Sagitta = pocketValues.get("M1");

        if (Sagitta <= 1) {
            // I already have P13 (from the backtack part) and the P15 points, so i don't need nothing more
        } else {

            /** TESTED */

            CenterPointRadius Info_Csx = MathGeoTri.CalculateArc_CenterRadius(pointsPTS.get("P11"), pointsPTS.get("P12"), pointsPTS.get("P13"));
            Float R3 = Info_Csx.radius;
            PointF Centro3 = new PointF(Info_Csx.center.x, Info_Csx.center.y);

            // I already have P14

            /*ArrayList<PointF> P13ristrettoristretto_list = MathGeoTri.CircleStraightLineIntersection(new PointF(pointsPTS.get("P13").x, pointsPTS.get("P13").y + topRightMargin_Backtack), new PointF(pointsPTS.get("P1").x, pointsPTS.get("P1").y + topRightMargin_Backtack), Centro3, R3 - pocketValues.get("M") - pocketValues.get("F"));

            float Dist1 = MathGeoTri.Distance(pointsPTS.get("P13").x, pointsPTS.get("P13").y, P13ristrettoristretto_list.get(0).x, P13ristrettoristretto_list.get(0).y);
            float Dist2 = MathGeoTri.Distance(pointsPTS.get("P13").x, pointsPTS.get("P13").y, P13ristrettoristretto_list.get(1).x, P13ristrettoristretto_list.get(1).y);

            if (Dist1 < Dist2) {
                pointsMap.put("P14", P13ristrettoristretto_list.get(0));
            } else {
                pointsMap.put("P14", P13ristrettoristretto_list.get(1));
            }*/

            pointsMap.put("P15", MathGeoTri.CalculateArcThirdPoint_Sagitta(pointsMap.get("P16"), pointsMap.get("P14"), R3 - (pocketValues.get("F") + pocketValues.get("E")) / 2 - pocketValues.get("M"), Centro3, Sagitta));
        }

        return pointsMap;
    }

    /**
     * Function for calculate the 2 Ricetta from the given points
     *
     * @param type
     * @param points
     * @param pocketValues
     * @return
     */
    public static Pair<Ricetta, Ricetta> Model_5_Ricetta(int type, TreeMap<String, PointF> points, HashMap<String, Float> pocketValues) {
        Ricetta ricettaResult = new Ricetta(Values.plcType);
        Ricetta ricetta1Result = new Ricetta(Values.plcType);
        //Default Start Draw Position
        ricettaResult.setDrawPosition(new PointF(0.1f, 0.1f));
        ricetta1Result.setDrawPosition(new PointF(0.1f, 0.1f));

        if (type == 1) {

            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    }
                }
                break;
            }

            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P2"), points.get("P3"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));

            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P4"), points.get("P5"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));

            if (pocketValues.get("M6") > 1)
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));

            if (pocketValues.get("M5") > 1)
                ricettaResult.drawArcTo(points.get("P8"), points.get("P9"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));

            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P10"), points.get("P11"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P11"), pocketValues.get("LP"));

            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P12"), points.get("P13"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P13"), pocketValues.get("LP"));

            ricettaResult.drawZigZagTo(points.get("P14"), pocketValues.get("G"), pocketValues.get("H"));

            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P15"), points.get("P16"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P16"), pocketValues.get("LP"));

            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P17"), points.get("P18"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P18"), pocketValues.get("LP"));

            if (pocketValues.get("M5") > 1)
                ricettaResult.drawArcTo(points.get("P19"), points.get("P20"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P20"), pocketValues.get("LP"));

            if (pocketValues.get("M6") > 1)
                ricettaResult.drawArcTo(points.get("P21"), points.get("P22"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P22"), pocketValues.get("LP"));

            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P23"), points.get("P24"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P24"), pocketValues.get("LP"));

            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P25"), points.get("P26"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P26"), pocketValues.get("LP"));

            ricettaResult.drawZigZagTo(points.get("P1"), pocketValues.get("B"), pocketValues.get("C"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalZigZagFermatura(ricettaResult, pocketValues.get("C"), pocketValues.get("B"), Values.pFf1, Values.Ff1t, points.get("P1"), true, oneMorePoint, false);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, line);
                    }
                }
                break;
            }
        } else if (type == 2) {

            switch (Values.Fi1) {
                case 0: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    }
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P2"), points.get("P3"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));

            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P4"), points.get("P5"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));

            if (pocketValues.get("M6") > 1)
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));

            if (pocketValues.get("M5") > 1)
                ricettaResult.drawArcTo(points.get("P8"), points.get("P9"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));

            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P10"), points.get("P11"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P11"), pocketValues.get("LP"));

            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P12"), points.get("P13"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P13"), pocketValues.get("LP"));

            ricettaResult.drawLineTo(points.get("P14"), pocketValues.get("LP"));

            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P15"), points.get("P16"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P16"), pocketValues.get("LP"));

            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P17"), points.get("P18"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P18"), pocketValues.get("LP"));

            if (pocketValues.get("M5") > 1)
                ricettaResult.drawArcTo(points.get("P19"), points.get("P20"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P20"), pocketValues.get("LP"));

            if (pocketValues.get("M6") > 1)
                ricettaResult.drawArcTo(points.get("P21"), points.get("P22"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P22"), pocketValues.get("LP"));

            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P23"), points.get("P24"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P24"), pocketValues.get("LP"));

            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P25"), points.get("P26"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P26"), pocketValues.get("LP"));

            ricettaResult.drawLineTo(points.get("P1"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P1"), oneMorePoint, false, line);
                    }
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.F, points.get("P1"), true, oneMorePoint, false);
                }
                break;
            }
        } else if (type == 3) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    }
                }
                break;
            }

            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P2"), points.get("P3"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));

            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P4"), points.get("P5"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));

            if (pocketValues.get("M6") > 1)
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));

            if (pocketValues.get("M5") > 1)
                ricettaResult.drawArcTo(points.get("P8"), points.get("P9"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));

            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P10"), points.get("P11"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P11"), pocketValues.get("LP"));

            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P12"), points.get("P13"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P13"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.F, points.get("P13"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P13");
                        arc.pMiddle = points.get("P12");
                        arc.pEnd = points.get("P11");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P13"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P13");
                        line.pEnd = points.get("P11");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P13"), oneMorePoint, false, line);
                    }
                }
                break;
            }

            switch (Values.Fi2) {
                case 0: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P1"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P1"), oneMorePoint, true, line);
                    }
                }
                break;
            }

            ricetta1Result.drawZigZagTo(points.get("P26"), pocketValues.get("B"), pocketValues.get("C"));

            if (pocketValues.get("M2") > 1)
                ricetta1Result.drawArcTo(points.get("P25"), points.get("P24"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P24"), pocketValues.get("LP"));

            if (pocketValues.get("M4") > 1)
                ricetta1Result.drawArcTo(points.get("P23"), points.get("P22"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P22"), pocketValues.get("LP"));

            if (pocketValues.get("M6") > 1)
                ricetta1Result.drawArcTo(points.get("P21"), points.get("P20"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P20"), pocketValues.get("LP"));

            if (pocketValues.get("M5") > 1)
                ricetta1Result.drawArcTo(points.get("P19"), points.get("P18"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P18"), pocketValues.get("LP"));

            if (pocketValues.get("M3") > 1)
                ricetta1Result.drawArcTo(points.get("P17"), points.get("P16"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P16"), pocketValues.get("LP"));

            if (pocketValues.get("M1") > 1)
                ricetta1Result.drawArcTo(points.get("P15"), points.get("P14"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P14"), pocketValues.get("LP"));

            ricetta1Result.drawZigZagTo(points.get("P13"), pocketValues.get("G"), pocketValues.get("H"));

            switch (Values.Ff2) {
                case 0: {
                    ricetta1Result = HorizontalZigZagFermatura(ricetta1Result, pocketValues.get("C"), pocketValues.get("B"), Values.pFf2, Values.Ff2t, points.get("P13"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P13");
                        arc.pMiddle = points.get("P12");
                        arc.pEnd = points.get("P11");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P13"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P13");
                        line.pEnd = points.get("P11");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P13"), oneMorePoint, false, line);
                    }
                }
                break;
            }
        } else if (type == 4) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P26"), false, oneMorePoint, true);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P26");
                        arc.pMiddle = points.get("P25");
                        arc.pEnd = points.get("P24");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P26"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P26");
                        line.pEnd = points.get("P24");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P26"), oneMorePoint, true, line);
                    }
                }
                break;
            }

            ricettaResult.drawZigZagTo(points.get("P1"), pocketValues.get("G"), pocketValues.get("H"));

            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P2"), points.get("P3"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));

            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P4"), points.get("P5"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));

            if (pocketValues.get("M6") > 1)
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));

            if (pocketValues.get("M5") > 1)
                ricettaResult.drawArcTo(points.get("P8"), points.get("P9"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));

            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P10"), points.get("P11"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P11"), pocketValues.get("LP"));

            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P12"), points.get("P13"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P13"), pocketValues.get("LP"));

            ricettaResult.drawZigZagTo(points.get("P14"), pocketValues.get("B"), pocketValues.get("C"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalZigZagFermatura(ricettaResult, pocketValues.get("C"), pocketValues.get("B"), Values.pFf1, Values.Ff1t, points.get("P14"), true, oneMorePoint, false);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P14");
                        arc.pMiddle = points.get("P15");
                        arc.pEnd = points.get("P16");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P14"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P14");
                        line.pEnd = points.get("P16");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P14"), oneMorePoint, false, line);
                    }
                }
                break;
            }

            switch (Values.Fi2) {
                case 0:
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P26");
                        arc.pMiddle = points.get("P25");
                        arc.pEnd = points.get("P24");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P26"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P26");
                        line.pEnd = points.get("P24");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P26"), oneMorePoint, true, line);
                    }
                }
                break;
            }

            if (pocketValues.get("M2") > 1)
                ricetta1Result.drawArcTo(points.get("P25"), points.get("P24"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P24"), pocketValues.get("LP"));

            if (pocketValues.get("M4") > 1)
                ricetta1Result.drawArcTo(points.get("P23"), points.get("P22"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P22"), pocketValues.get("LP"));

            if (pocketValues.get("M6") > 1)
                ricetta1Result.drawArcTo(points.get("P21"), points.get("P20"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P20"), pocketValues.get("LP"));

            if (pocketValues.get("M5") > 1)
                ricetta1Result.drawArcTo(points.get("P19"), points.get("P18"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P18"), pocketValues.get("LP"));

            if (pocketValues.get("M3") > 1)
                ricetta1Result.drawArcTo(points.get("P17"), points.get("P16"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P16"), pocketValues.get("LP"));

            if (pocketValues.get("M1") > 1)
                ricetta1Result.drawArcTo(points.get("P15"), points.get("P14"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P14"), pocketValues.get("LP"));

            switch (Values.Ff2) {
                case 0:
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P14");
                        arc.pMiddle = points.get("P15");
                        arc.pEnd = points.get("P16");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P14"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P14");
                        line.pEnd = points.get("P16");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P14"), oneMorePoint, false, line);
                    }
                }
                break;
            }
        } else if (type == 5) {
            switch (Values.Fi1) {
                case 0: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    }
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P2"), points.get("P3"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));

            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P4"), points.get("P5"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));

            if (pocketValues.get("M6") > 1)
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));

            if (pocketValues.get("M5") > 1)
                ricettaResult.drawArcTo(points.get("P8"), points.get("P9"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));

            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P10"), points.get("P11"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P11"), pocketValues.get("LP"));

            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P12"), points.get("P13"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P13"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P13");
                        arc.pMiddle = points.get("P12");
                        arc.pEnd = points.get("P11");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P13"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P13");
                        line.pEnd = points.get("P11");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P13"), oneMorePoint, false, line);
                    }
                }
                break;
                case 1: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.F, points.get("P13"), false, oneMorePoint, false);
                }
                break;
            }

            switch (Values.Fi2) {
                case 0: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P26");
                        arc.pMiddle = points.get("P25");
                        arc.pEnd = points.get("P24");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P26"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P26");
                        line.pEnd = points.get("P24");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFi2, Values.Fi2t, points.get("P26"), oneMorePoint, true, line);
                    }
                }
                break;
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P26"), false, oneMorePoint, true);
                }
                break;
            }

            if (pocketValues.get("M2") > 1)
                ricetta1Result.drawArcTo(points.get("P25"), points.get("P24"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P24"), pocketValues.get("LP"));

            if (pocketValues.get("M4") > 1)
                ricetta1Result.drawArcTo(points.get("P23"), points.get("P22"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P22"), pocketValues.get("LP"));

            if (pocketValues.get("M6") > 1)
                ricetta1Result.drawArcTo(points.get("P21"), points.get("P20"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P20"), pocketValues.get("LP"));

            if (pocketValues.get("M5") > 1)
                ricetta1Result.drawArcTo(points.get("P19"), points.get("P18"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P18"), pocketValues.get("LP"));

            if (pocketValues.get("M3") > 1)
                ricetta1Result.drawArcTo(points.get("P17"), points.get("P16"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P16"), pocketValues.get("LP"));

            if (pocketValues.get("M1") > 1)
                ricetta1Result.drawArcTo(points.get("P15"), points.get("P14"), pocketValues.get("LP"));
            else
                ricetta1Result.drawLineTo(points.get("P14"), pocketValues.get("LP"));

            switch (Values.Ff2) {
                case 0: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P14");
                        arc.pMiddle = points.get("P15");
                        arc.pEnd = points.get("P16");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P14"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P14");
                        line.pEnd = points.get("P16");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricetta1Result = VerticalLineFermatura(ricetta1Result, pocketValues.get("LP"), Values.pFf2, Values.Ff2t, points.get("P14"), oneMorePoint, false, line);
                    }
                }
                break;
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Ff2t, Values.F, points.get("P14"), true, oneMorePoint, false);
                }
                break;
            }
        } else if (type == 6) {
            switch (Values.Fi1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Fi1t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P1");
                        arc.pMiddle = points.get("P2");
                        arc.pEnd = points.get("P3");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P1");
                        line.pEnd = points.get("P3");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFi1, Values.Fi1t, points.get("P1"), oneMorePoint, true, line);
                    }
                }
                break;
            }

            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P2"), points.get("P3"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P3"), pocketValues.get("LP"));

            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P4"), points.get("P5"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P5"), pocketValues.get("LP"));

            if (pocketValues.get("M6") > 1)
                ricettaResult.drawArcTo(points.get("P6"), points.get("P7"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P7"), pocketValues.get("LP"));

            if (pocketValues.get("M5") > 1)
                ricettaResult.drawArcTo(points.get("P8"), points.get("P9"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P9"), pocketValues.get("LP"));

            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P10"), points.get("P11"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P11"), pocketValues.get("LP"));

            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P12"), points.get("P13"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P13"), pocketValues.get("LP"));

            ricettaResult.drawFeedTo(points.get("P14"));

            if (pocketValues.get("M1") > 1)
                ricettaResult.drawArcTo(points.get("P15"), points.get("P16"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P16"), pocketValues.get("LP"));

            if (pocketValues.get("M3") > 1)
                ricettaResult.drawArcTo(points.get("P17"), points.get("P18"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P18"), pocketValues.get("LP"));

            if (pocketValues.get("M5") > 1)
                ricettaResult.drawArcTo(points.get("P19"), points.get("P20"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P20"), pocketValues.get("LP"));

            if (pocketValues.get("M6") > 1)
                ricettaResult.drawArcTo(points.get("P21"), points.get("P22"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P22"), pocketValues.get("LP"));

            if (pocketValues.get("M4") > 1)
                ricettaResult.drawArcTo(points.get("P23"), points.get("P24"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P24"), pocketValues.get("LP"));

            if (pocketValues.get("M2") > 1)
                ricettaResult.drawArcTo(points.get("P25"), points.get("P26"), pocketValues.get("LP"));
            else
                ricettaResult.drawLineTo(points.get("P26"), pocketValues.get("LP"));

            switch (Values.Ff1) {
                case 0: {
                    ricettaResult = HorizontalLineFermatura(ricettaResult, Values.Ff1t, Values.A, points.get("P26"), false, oneMorePoint, false);
                }
                break;
                case 1: {
                    if (pocketValues.get("M2") > 1) {
                        ElementArc arc = new ElementArc();
                        arc.pStart = points.get("P26");
                        arc.pMiddle = points.get("P25");
                        arc.pEnd = points.get("P24");
                        arc.passo = pocketValues.get("LP");
                        arc.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P26"), oneMorePoint, false, arc);
                    } else {
                        ElementLine line = new ElementLine();
                        line.pStart = points.get("P26");
                        line.pEnd = points.get("P24");
                        line.passo = pocketValues.get("LP");
                        line.createSteps();

                        ricettaResult = VerticalLineFermatura(ricettaResult, pocketValues.get("LP"), Values.pFf1, Values.Ff1t, points.get("P26"), oneMorePoint, false, line);
                    }
                }
                break;
            }

            switch (Values.Fi2) {
                case 0:
                case 1: {
                    ricetta1Result = HorizontalLineFermatura(ricetta1Result, Values.Fi2t, Values.A, points.get("P1"), true, oneMorePoint, true);
                }
                break;
            }

            ricetta1Result.drawZigZagTo(points.get("P26"), pocketValues.get("G"), pocketValues.get("H"));
            ricetta1Result.drawFeedTo(points.get("P14"));
            ricetta1Result.drawZigZagTo(points.get("P13"), pocketValues.get("B"), pocketValues.get("C"));

            switch (Values.Ff2) {
                case 0:
                case 1: {
                    ricetta1Result = HorizontalZigZagFermatura(ricetta1Result, pocketValues.get("C"), pocketValues.get("B"), Values.pFf2, Values.Ff2t, points.get("P13"), false, oneMorePoint, false);
                }
                break;
            }
        }

        return new Pair<>(ricettaResult, ricetta1Result);
    }

    /**
     * Function for add codes on the 2 Ricetta
     *
     * @param type
     * @param ricette
     * @return
     */
    public static Pair<Ricetta, Ricetta> Model_5_Codes(int type, Pair<Ricetta, Ricetta> ricette) {
        if (type == 1 || type == 2) {

            /**************************
             * OP2 and OP1 codes
             ***************************/

            // Set OP2 ON the line before the backtack
            try {
                if (Values.OP2On >= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP2On == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            // Set OP1 ON on backtack start
            try {
                if (Values.OP1On >= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP1On == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            // Set OP2 OFF on backtack
            try {
                if (Values.OP2Off >= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP2Off == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /**************************
             * Speed1 and OP3 codes
             ***************************/

            // Set Speed1 ON at start (slow backtack)

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            /*****************************************************/
            // Set Speed1 OFF after start backtak (fast pocket)
            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.Numeric, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(15 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(15 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.Numeric, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(15 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(15 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.Numeric, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(15 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(15 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(15 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(15 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            /*****************************************************/
            // Set Speed1 ON before second backtack (slow backtack)
            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            /*****************************************************/
            // Set Speed1 OFF after second backtack (fast pocket)
            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            /*****************************************************/
            // Set Speed1 ON at first backtack, end part (slow backtack)
            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(15 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(14 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(14 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(14 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(14 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(15 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(14 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(14 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(14 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(14 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        } else if (type == 3) {

            /***************************************
             * Speed1 and OP3 codes on first pocket
             ****************************************/

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 OFF at first line after first backtack
            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set Speed1 ON at line before second backtack
            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /***************************************
             * Speed1 and OP3 codes on second pocket
             ****************************************/

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 OFF at line after first backtack
            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.second.elements.get(3 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.second.elements.get(3 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            /*****************************************************/
            // Set SPEED1 ON at line before second backtack
            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.second.elements.get(9 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 > 0) {
                    JamPointStep step = ricette.second.elements.get(9 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


        } else if (type == 4) {

            /***************************************
             * Speed1 and OP3 codes on first pocket
             ****************************************/

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 OFF at line after first backtack
            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(3 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(3 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 ON at line after second backtack
            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            /***************************************
             * Speed1 and OP3 codes on second pocket
             ****************************************/

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 OFF at line after backtack

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 ON at line before second backtack

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi2t - 1)).steps.get(Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi2t - 1)).steps.get(Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        } else if (type == 5) {

            /***************************************
             * Speed1 and OP3 codes on first pocket
             ****************************************/

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 OFF at line after first backtack

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 ON at line before second backtack

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }


            /***************************************
             * Speed1 and OP3 codes on second pocket
             ****************************************/

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 OFF at line after first backtack

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(-Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.second.elements.get(2 + (Values.Fi2t - 1)).steps.get(-Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(1 + (Values.Fi2t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 ON at line before second backtack

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi2t - 1)).steps.get(Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.second.elements.get(8 + (Values.Fi2t - 1)).steps.get(Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.size() - 1 - 2);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.get(ricette.second.elements.get(7 + (Values.Fi2t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else if (type == 6) {

            /**************************
             * OP2 and OP1 codes
             ***************************/

            try {
                if (Values.OP2On >= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP2On == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2On);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            // TODO Questo codice va sul feed quindi va a finire a fine feed
            try {
                if (Values.OP1On >= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP1On == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP1On);
                    ricette.first.addStepCode(step, CodeType.OP1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            // TODO Questo codice va sul feed quindi non viene messo
            try {
                if (Values.OP2Off >= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP2Off == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP2Off);
                    ricette.first.addStepCode(step, CodeType.OP2, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /***************************************
             * Speed1 and OP3 codes on first pocket
             ****************************************/

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.first.elements.get(1).steps.get(1);
                ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 OFF at line after first backtack

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(2 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(1 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 ON at line before second backtack

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(7 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED OFF at line after second backtack

            try {
                if (Values.Speed1 <= 0) {
                    JamPointStep step = ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.get(-Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 - Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED0, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 <= 0) {
                    JamPointStep step = ricette.first.elements.get(9 + (Values.Fi1t - 1)).steps.get(-Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                } else {
                    JamPointStep step = ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(8 + (Values.Fi1t - 1)).steps.size() - 1 - Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE0"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /*****************************************************/
            // Set SPEED1 ON at line before first backtack

            try {
                if (Values.Speed1 >= 0) {
                    JamPointStep step = ricette.first.elements.get(15 + (Values.Fi1t - 1)).steps.get(Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.Speed1 == -1) {
                    JamPointStep step = ricette.first.elements.get(14 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(14 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(14 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(14 + (Values.Fi1t - 1)).steps.size() - 1 + Values.Speed1);
                    ricette.first.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                if (Values.OP3 >= 0) {
                    JamPointStep step = ricette.first.elements.get(15 + (Values.Fi1t - 1)).steps.get(Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else if (Values.OP3 == -1) {
                    JamPointStep step = ricette.first.elements.get(14 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(14 + (Values.Fi1t - 1)).steps.size() - 1 - 2);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                } else {
                    JamPointStep step = ricette.first.elements.get(14 + (Values.Fi1t - 1)).steps.get(ricette.first.elements.get(14 + (Values.Fi1t - 1)).steps.size() - 1 + Values.OP3);
                    ricette.first.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            /***************************************
             * Speed1 and OP3 codes on first pocket
             ****************************************/

            // TODO Fix this, i think is in the feed so is skipped

            // Set SPEED1 ON after the first feed
            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.SPEED1, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            try {
                JamPointStep step = ricette.second.elements.get(1).steps.get(1);
                ricette.second.addStepCode(step, CodeType.OP3, new CodeValue(CodeValueType.OnOff, "VALUE1"));
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        return ricette;
    }


    /**
     * Function for add a Horizontal ZigZag fermatura to the Ricetta
     * <p>
     * I need to pass the endpoint for a better result (i need to calculate the new lines using the endpoint direction, now is only horizontal)
     *
     * @param ricetta
     * @param LP
     * @param heightPoints
     * @param nPoints
     * @param rep
     * @param resultPoint
     * @param directionPos
     * @param oneMorePoint
     * @param start
     * @return
     */
    private static Ricetta HorizontalZigZagFermatura(Ricetta ricetta, float LP, float heightPoints, int nPoints, int rep, PointF resultPoint, boolean directionPos, boolean oneMorePoint, boolean start) {

        //Length of 1 Fermature Repetition
        float oneRepFermatureLength = LP / 2 * nPoints + (oneMorePoint ? LP / 2 : 0);

        boolean currentDirectionPos = directionPos;
        PointF startPoint = resultPoint;
        //Calculate Start point
      /*  DANIELE for (int i = 0; i < rep; i++) {
            if (currentDirectionPos) {
                startPoint = new PointF(startPoint.x + oneRepFermatureLength, startPoint.y);
                currentDirectionPos = false;
            } else {
                startPoint = new PointF(startPoint.x - oneRepFermatureLength, startPoint.y);
                currentDirectionPos = true;
            }
        }
*/
        //If is a Start Fermatura i draw the feed
        if (start) {
            ricetta.drawFeedTo(startPoint);
        }

        for (int i = 0; i < rep; i++) {
            if (currentDirectionPos) {
                startPoint = new PointF(startPoint.x + oneRepFermatureLength, startPoint.y);
                if (ricetta.elements.get(ricetta.elements.size() - 1) instanceof ElementZigZag) {
                    ricetta.drawZigZagTo(startPoint, heightPoints, LP, (ElementZigZag) ricetta.elements.get(ricetta.elements.size() - 1));
                } else {
                    ricetta.drawZigZagTo(startPoint, heightPoints, LP);
                }
                currentDirectionPos = false;
            } else {
                startPoint = new PointF(startPoint.x - oneRepFermatureLength, startPoint.y);
                if (ricetta.elements.get(ricetta.elements.size() - 1) instanceof ElementZigZag) {
                    ricetta.drawZigZagTo(startPoint, heightPoints, LP, (ElementZigZag) ricetta.elements.get(ricetta.elements.size() - 1));
                } else {
                    ricetta.drawZigZagTo(startPoint, heightPoints, LP);
                }
                currentDirectionPos = true;
            }
        }

        return ricetta;
    }

    /**
     * Old Vertial line Fermatura (NOT FOLLOW THE DRAW)
     * @param ricetta
     * @param LP
     * @param nPoints
     * @param rep
     * @param resultPoint
     * @param oneMorePoint
     * @param start
     * @param elem
     * @return
     */
    /*private static Ricetta VerticalLineFermatura(Ricetta ricetta, float LP, int nPoints, int rep, PointF resultPoint, boolean directionPos, boolean oneMorePoint, boolean start) {
        //Length of 1 Fermature Repetition
        float oneRepFermatureLength = LP * nPoints + (oneMorePoint ? LP : 0);

        boolean currentDirectionPos = directionPos;
        PointF startPoint = resultPoint;
        // Calculate Start point
        // I use this as a rewind, i start from the end and go back to find the start point
        for (int i = 0; i < rep; i++) {
            if (currentDirectionPos) {
                startPoint = new PointF(startPoint.x, startPoint.y + oneRepFermatureLength);
                currentDirectionPos = false;
            } else {
                startPoint = new PointF(startPoint.x, startPoint.y - oneRepFermatureLength);
                currentDirectionPos = true;
            }
        }

        // If is a Start Fermatura i draw the feed
        if (start) {
            ricetta.drawFeedTo(startPoint);
        } else {
            if (currentDirectionPos) {
                startPoint = new PointF(startPoint.x, startPoint.y + oneRepFermatureLength);
                currentDirectionPos = false;
            } else {
                startPoint = new PointF(startPoint.x, startPoint.y - oneRepFermatureLength);
                currentDirectionPos = true;
            }
        }

        // Now i draw every Fermatura, this will result that the end of Fermatura is the resultPoint
        for (int i = 0; i < rep; i++) {
            if (currentDirectionPos) {
                startPoint = new PointF(startPoint.x, startPoint.y + oneRepFermatureLength);
                ricetta.drawLineTo(startPoint, LP);
                currentDirectionPos = false;
            } else {
                startPoint = new PointF(startPoint.x, startPoint.y - oneRepFermatureLength);
                ricetta.drawLineTo(startPoint, LP);
                currentDirectionPos = true;
            }
        }

        return ricetta;
    }*/

    /**
     * Function for add a Vertical Fermatura to the Ricetta
     * <p>
     * The computed Fermature will follow the draw
     *
     * @param ricetta
     * @param LP
     * @param nPoints
     * @param rep
     * @param resultPoint
     * @param oneMorePoint
     * @param start
     * @param elem
     * @return
     */
    private static Ricetta VerticalLineFermatura(Ricetta ricetta, float LP, int nPoints, int rep, PointF resultPoint, boolean oneMorePoint, boolean start, Element elem) {

        PointF point1 = resultPoint;
        PointF point2;
        // I get the point inside the given element that need to be the other side of the Fermatura
        float Dist1 = MathGeoTri.Distance(elem.steps.get(elem.steps.size() - (nPoints + (oneMorePoint == true ? 1 : 0))).p, point1);
        float Dist2 = MathGeoTri.Distance(elem.steps.get(nPoints + (oneMorePoint == true ? 1 : 0)).p, point1);
        if (Dist1 > Dist2) {
            point2 = elem.steps.get(nPoints + (oneMorePoint == true ? 1 : 0)).p;
        } else {
            point2 = elem.steps.get(elem.steps.size() - (nPoints + (oneMorePoint == true ? 1 : 0))).p;
        }


        // I DON' T NEED THIS, I CAN DO IT SIMPLER
        // Calculate Start point
        // I use this as a rewind, i start from the end and go back to find the start point
        /*for (int i = 0; i < rep; i++) {

            // I go backward so i need to swap the startPoint
            if(startPoint.equals(point1))
            {
                startPoint = point2;
            }else{
                startPoint = point1;
            }
        }*/

        //boolean currentDirectionPos = directionPos;
        PointF startPoint = rep % 2 == 0 ? point1 : point2;


        // If is a Start Fermatura i draw the feed
        if (start) {
            ricetta.drawFeedTo(startPoint);
            for (int i = 0; i < rep; i++) {

                if(i==0 ){
                    if (rep==1 || rep ==3) startPoint = point1;
                    else  startPoint = point2;
                }
                if(i==1 ){
                    if (rep==2) startPoint = point1;
                    else  startPoint = point2;
                }
                if(i==2 ){
                    startPoint = point1;

                }




                ricetta.drawLineTo(startPoint, LP);
            }
        }else {
            for (int i = 0; i < rep; i++) {
                //DANIELE 12/05/2023, prima con una sola fermatura finale verticale
                if(i==0 || i==2) startPoint = point2;
                if(i==1) startPoint = point1;
                ricetta.drawLineTo(startPoint, LP);
            }

        }




        return ricetta;
    }

    /**
     * Old Horizontal line Fermatura
     * @param ricetta
     * @param rep
     * @param fermaturaLength
     * @param resultPoint
     * @param directionPos
     * @param oneMorePoint
     * @param start
     * @return
     */
    /*private static Ricetta HorizontalLineFermatura(Ricetta ricetta, float LP, int nPoints, int rep, PointF resultPoint, boolean directionPos, boolean oneMorePoint, boolean start) {

        //Length of 1 Fermature Repetition
        float oneRepFermatureLength = LP * nPoints + (oneMorePoint ? LP : 0);

        boolean currentDirectionPos = directionPos;
        PointF startPoint = resultPoint;
        //Calculate Start point
        for (int i = 0; i < rep; i++) {
            if (currentDirectionPos) {
                startPoint = new PointF(startPoint.x + oneRepFermatureLength, startPoint.y);
                currentDirectionPos = false;
            } else {
                startPoint = new PointF(startPoint.x - oneRepFermatureLength, startPoint.y);
                currentDirectionPos = true;
            }
        }

        //If is a Start Fermatura i draw the feed
        if (start) {
            ricetta.drawFeedTo(startPoint);
        } else {
            if (currentDirectionPos) {
                startPoint = new PointF(startPoint.x + oneRepFermatureLength, startPoint.y);
                currentDirectionPos = false;
            } else {
                startPoint = new PointF(startPoint.x - oneRepFermatureLength, startPoint.y);
                currentDirectionPos = true;
            }
        }

        for (int i = 0; i < rep; i++) {
            if (currentDirectionPos) {
                startPoint = new PointF(startPoint.x + oneRepFermatureLength, startPoint.y);
                ricetta.drawLineTo(startPoint, LP);
                currentDirectionPos = false;
            } else {
                startPoint = new PointF(startPoint.x - oneRepFermatureLength, startPoint.y);
                ricetta.drawLineTo(startPoint, LP);
                currentDirectionPos = true;
            }
        }

        return ricetta;
    }*/

    /**
     * Function for add to Ricetta a Horizontal line Fermatura
     * <p>
     * This use the new method:
     * <p>
     * **********************************************************************************
     * Calcolo fermature:
     * **********************************************************************************
     * Per calcolare le fermature uso di default 3.
     * <p>
     * Faccio 6.4 (Lunghezza travetta) / 3 = 2 (numero di punti) 6.4 / 2 = 3.2 (nuova lunghezza punto). In questo caso il risultato è accettabile perchè è minore di 3.5 quindi il numero di punti è 6.4/3.2 = 2
     * <p>
     * Se ad esempio è 7.1 / 3 = 2 (numero di punti) 7.1 / 2 = 3.55. In questo caso il risultato non è accettabile perchè maggiore di 3.5 e e quindi rifaccio il calcolo
     * Rifaccio il calcolo aggiungendo 1 punto quindi faccio 7.1 / 3 = 2.38. In questo caso il risulato va bene perchè è minore di 3.5 quindi faccio 3 punti da 2.38
     * <p>
     * Se ad esempio è 3.4 / 3 = 1 (numero di punti) 3.4 / 1 = 3.4. Accettabile
     * <p>
     * Se ad esempio è 3.6 / 3 = 1 (numero di punti) 3.6 / 1 = 3.6. Non accettabile, ricalcolo
     * 3.6 / 2 = 1.8 Accettabile
     *
     * @param ricetta
     * @param rep
     * @param fermaturaLength
     * @param resultPoint
     * @param directionPos
     * @param oneMorePoint
     * @param start
     * @return
     */
    private static Ricetta HorizontalLineFermatura(Ricetta ricetta, int rep, float fermaturaLength, PointF resultPoint, boolean directionPos, boolean oneMorePoint, boolean start) {

        int defaultPoints = 3;

        int pointsCount = Math.round(fermaturaLength / defaultPoints);

        //Length of 1 Fermature Repetition
        float oneRepFermatureLP = fermaturaLength / pointsCount;

        if (oneRepFermatureLP > 3.5f) {
            oneRepFermatureLP = fermaturaLength / (pointsCount + 1);
        }

        boolean currentDirectionPos = directionPos;
        PointF startPointCalc = resultPoint;
        //Calculate Start point
        for (int i = 0; i < rep; i++) {
            if (currentDirectionPos) {
                startPointCalc = new PointF(startPointCalc.x + fermaturaLength, startPointCalc.y);
                currentDirectionPos = false;
            } else {
                startPointCalc = new PointF(startPointCalc.x - fermaturaLength, startPointCalc.y);
                currentDirectionPos = true;
            }
        }

        //If is a Start Fermatura i draw the feed
        if (start) {
            ricetta.drawFeedTo(startPointCalc);
        } else {
            if (currentDirectionPos) {
                startPointCalc = new PointF(startPointCalc.x + fermaturaLength, startPointCalc.y);
                currentDirectionPos = false;
            } else {
                startPointCalc = new PointF(startPointCalc.x - fermaturaLength, startPointCalc.y);
                currentDirectionPos = true;
            }
        }

        for (int i = 0; i < rep; i++) {
            if (currentDirectionPos) {
                startPointCalc = new PointF(startPointCalc.x + fermaturaLength, startPointCalc.y);
                ricetta.drawLineTo(startPointCalc, oneRepFermatureLP);
                currentDirectionPos = false;
            } else {
                startPointCalc = new PointF(startPointCalc.x - fermaturaLength, startPointCalc.y);
                ricetta.drawLineTo(startPointCalc, oneRepFermatureLP);
                currentDirectionPos = true;
            }
        }

        return ricetta;
    }

    /**
     * Function for get points from a PTS file
     *
     * @param fileName
     * @return
     */
    public static HashMap<String, PointF> ReadPointsFromPTS(String fileName) {
        try {
            HashMap<String, PointF> points = new HashMap<String, PointF>();

            //Read Points From file

            File root = new File(Environment.getExternalStorageDirectory(), "JamData/punti");
            if (!root.exists()) {
                root.mkdirs();
            }
            File file = new File(root, fileName);

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            int countRow = 0;
            int countPoint = 1;
            while ((line = br.readLine()) != null) {
                if (countRow == 0) {
                    //Model
                } else if (countRow == 1) {
                    //Type
                } else {
                    String[] str = line.split(",");
                    PointF point = new PointF(Float.parseFloat(str[0]), Float.parseFloat(str[1]));
                    points.put("P" + countPoint, point);
                    countPoint++;
                }
                countRow++;
            }

            br.close();

            return points;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}