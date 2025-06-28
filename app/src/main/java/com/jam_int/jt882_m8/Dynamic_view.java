package com.jam_int.jt882_m8;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.jamint.ricette.CenterPointRadius;
import com.jamint.ricette.Element;
import com.jamint.ricette.ElementArc;
import com.jamint.ricette.ElementArcZigZag;
import com.jamint.ricette.ElementFeed;
import com.jamint.ricette.ElementLine;
import com.jamint.ricette.ElementZigZag;
import com.jamint.ricette.JamPointCode;
import com.jamint.ricette.JamPointStep;
import com.jamint.ricette.MathGeoTri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dynamic_view extends View {
    /*************************************************************************************
     *************************************************************************************
     // Classe DynamicView
     *************************************************************************************
     *************************************************************************************/

    // TODO Guardando dal profiler e facendo un dump all'app appena inizializzata e uno dopo 10 pulsanti emergenza sembra che la memoria
    //  utilizzata dalla bitmap del disegno non viene rilasciata. Per confermare la mia teoria ho fatto anche dei test entrando 10 volte nella pagina modifica_programma
    //  ed anche li la memoria dedicata a byte[] (che contiene le bitmap) aumenta (dai 3M iniziali su Shallow Size passa a 45M).
    //  Se invece entro 20 volte su una pagine senza alcun disegno la memoria rimane la stessa (questo prova la teoria che è la bitmap).

    /***************************************************************************/

    // Per risolvere il problema bisogna rilasciare la memoria del canvas e della bitmap ogni volta che si esce dalla pagina.
    // (Per risolvere per bene il problema bisognerebbe controllare anche le string, float e char perchè come si nota nel test senza disegno aumentano dopo i restart)

    // Ho creato una funzione release() che libera canvas e Bitmap, per confermare il tutto ho impostato la view a null e con questo metodo ho recuperato quasi tutta la
    // memoria dopo aver forzato il garbage collector (da 12M a 6M, inizialmente erano 4M).

    // Un altra cosa da fare sarebbe risolvere i 2 leaks che non so bene cosa rappresentano.
    // https://medium.com/@umairkhalid786/the-easiest-way-to-detect-and-fix-memory-leaks-using-android-studio-profiler-tool-and-weakreference-cd7c212908a1
    // RISOLTI

    // Link free Bitmap Memory:
    // https://stackoverflow.com/questions/30758730/canvas-drawbitmap-and-freeing-up-memory/30759036#30759036
    // https://www.raywenderlich.com/4557771-android-memory-profiler-getting-started

    /***************************************************************************/

    // Senza rieseguire i test ho salvato i file in questo modo:
    // - Start (Appena accesa)
    // - After_10_restart (dopo 10 bottoni emergenza)
    // - After_10_modifica_programma (dopo 10 volte su modifica programma)
    // - After_20_copy_files (dopo 20 volte su copy files) (usato per vedere la differenza tra pagina con Bitmap e senza)

    // - Start_Without_draw (ho tolto il disegno dalla Main page per avere la conferma fosse quello)
    // - After_10_restart_WIthout_draw (dopo 10 restart senza il disegno)

    // Link per capire la differenza tra Shallow size e Retained size:
    // https://www.yourkit.com/docs/java/help/sizes.jsp
    // https://stackoverflow.com/questions/12707572/android-what-is-the-differences-between-shallow-and-retained-heap#:~:text=Shallow%20heap%20is%20the%20memory,real%20consumption%20of%20the%20VM.

    /***************************************************************************/

    // Informazioni varie sul profiler:

    // Alcune voci del profiler vengono interpretate in modo errato. Cioè che alcune operazioni che esegue l'app vengono registrate dal profiler come cumulative
    // mentre android in realtà rilascia la memoria. Questo porta voci tipo "FinalizerReference" a dimensioni altissime ache se in realtà non è così. Le colonne
    // da tenere sott'occhio sono quindi la colonna "Shallow Size", la colonna "Allocations" ed i leaks. Inoltre bisognerebbe capire il perchè la memoria usata
    // piano piano aumenta.

    // Infromazioni generali che ho trovato sulle varie voci del profiler:

    // FinalizerReference
    // Voce che è semplicemente una sequenza di riferimenti (infatti sono tutti da 36 byte)
    // https://stackoverflow.com/questions/52135012/i-want-to-know-why-finalizerreference-consume-so-much-memory-in-my-application

    // byte[]
    // Non lo so di preciso ma contiene le immagini (infatti analizzando si nota che rappresenta un Bitmap)

    //AppCompatButton
    // Oggetti grafici delle varie pagine

    private final Matrix mMatrix = new Matrix();
    /**
     * Bitmap part
     */
    Bitmap frame;
    Canvas frameDrawer;
    Paint paint;
    /**
     * Size of the bitmap
     */
    int width, height;
    /**
     * List of entities
     */
    List<Element> List_entità = new ArrayList<>();
    /**
     * List of points
     * <p>
     * TODO i don't know if this is useful, i already have the points inside the elements
     */
    ArrayList<PointF> List_punti = new ArrayList<>();
    /**
     * List of pts points
     */
    ArrayList<PointF> List_punti_pts = new ArrayList<>();

    /**
     * List of codes
     * <p>
     * TODO i don't know if this is useful, i already have the codes inside the elements
     */
    ArrayList<JamPointCode> List_code = new ArrayList<>();

    float zoom_canvas = 1F;
    float offsetX = 0;
    float offsetY = 0;
    float x_down = 0F;
    float y_down = 0F;
    float x_delta = 0F;
    float y_delta = 0F;
    boolean AutoZoomCenter = true;
    int offsetCentraggioIniziale_X, offsetCentraggioIniziale_Y;

    Wrapper Info_disegno;

    CoordPosPinza CoordPosPinza;

    /**
     * Var used for zoom at center
     */
    float centerX = 0;
    float centerY = 0;
    private final View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x_down = x;
                    y_down = y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    x_delta = x - x_down;
                    x_down = x;

                    y_delta = y - y_down;
                    y_down = y;

                    if (x_delta > 1 || x_delta < -1) {
                        setOffsetX_Y(x_delta, y_delta);
                    }
                    AggiornaCanvas(true);
                    break;
                case MotionEvent.ACTION_UP:
                    AggiornaCanvas(true);
                    break;
            }

            return true;
        }
    };
    float defaultcenterX = 0;

    /*public Dynamic_view(Context context, int width, int height, ArrayList<Element> List_entità, float zoom_canvas, ArrayList<PointF> List_punti, MainActivity.CoordPosPinza CoordPinza, boolean AutoZoomCenter) {
        super(context);

        this.width = width;
        this.height = height;

        frame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        frameDrawer = new Canvas(frame);
        this.List_entità = List_entità;
        this.zoom_canvas = zoom_canvas;
        this.CoordPosPinza = CoordPinza;
        this.AutoZoomCenter = AutoZoomCenter;

        paint = new Paint();

        setOnTouchListener(handleTouch);
    }*/
    float defaultcenterY = 0;

    /**
     * Function for launch the dynamic view
     *
     * @param context
     * @param width
     * @param height
     * @param elements
     * @param zoom_canvas
     * @param coord_pinza
     * @param AutoZoomCenter
     * @param OffsetCentraggioIniziale_X
     * @param OffsetCentraggioIniziale_Y
     * @param List_punti_pts
     * @param canvas_width
     * @param canvas_height
     */
    public Dynamic_view(Context context, int width, int height, ArrayList elements, float zoom_canvas, CoordPosPinza coord_pinza, boolean AutoZoomCenter, int OffsetCentraggioIniziale_X, int OffsetCentraggioIniziale_Y, ArrayList<PointF> List_punti_pts, float canvas_width, float canvas_height) {
        super(context);

        // Save the width and height
        this.width = width;
        this.height = height;

        // Init the bitmap
        frame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // Init the canvas
        frameDrawer = new Canvas(frame);

        List_punti = new ArrayList<>();
        this.List_entità = elements;
        this.zoom_canvas = zoom_canvas;
        this.CoordPosPinza = coord_pinza;
        this.AutoZoomCenter = AutoZoomCenter;
        this.offsetCentraggioIniziale_X = OffsetCentraggioIniziale_X;
        this.offsetCentraggioIniziale_Y = OffsetCentraggioIniziale_Y;
        this.List_punti_pts = List_punti_pts;

        // Calculate the center for zoom
        centerX = (canvas_width / 2) / zoom_canvas;
        centerY = (canvas_height / 2) / zoom_canvas;
        defaultcenterX = (canvas_width / 2) / zoom_canvas;
        defaultcenterY = (canvas_height / 2) / zoom_canvas;

        paint = new Paint();

        setOnTouchListener(handleTouch);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Matrix matrix = mMatrix;
        matrix.reset();

        // Draw points
        Disegna_punti(List_punti);
        // Draw entities
        Disegna_entità(List_entità);

        if (List_punti_pts != null && List_punti_pts.size() > 0)
            // Draw pts points
            Disegna_pts(List_punti_pts);
        try {
            paint.setColor(Color.BLUE);
            if (frameDrawer != null) {
                frameDrawer.drawCircle((float) CoordPosPinza.XCoordPosPinza, (float) CoordPosPinza.YCoordPosPinza, 2F, paint);

                frameDrawer.drawLine(-20, -5, 300, -5, paint);           //riquadro area macchina
                frameDrawer.drawLine(300, -5, 300, 262, paint);         //riquadro area macchina
                frameDrawer.drawLine(300, 262, -20, 262, paint);         //riquadro area macchina
                frameDrawer.drawLine(-20, 262, -20, -5, paint);           //riquadro area macchina
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        canvas.drawBitmap(frame, matrix, null);
    }

    /**
     * Function for draw pts points and connect
     *
     * @param list_punti_pts
     */
    private void Disegna_pts(ArrayList<PointF> list_punti_pts) {
        paint.setColor(Color.BLACK);
        for (int i = 0; i < list_punti_pts.size() - 1; i++) {
            PointF p1 = list_punti_pts.get(i);
            PointF p2 = list_punti_pts.get(i + 1);

            frameDrawer.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
        }
    }

    public void setOffsetX_Y(Float newValueX, Float newValueY) {
        if (List_entità.size() > 0) {
            float ValX = (float) (newValueX / (Math.sqrt(zoom_canvas) * 2));
            float ValY = (float) (newValueY / (Math.sqrt(zoom_canvas) * 2));

            offsetX = offsetX + ValX;
            offsetY = offsetY + ValY;
            frameDrawer.translate(-ValX, +ValY);

            centerX += ValX;
            centerY += -ValY;
        }
    }

    public void Center_Bitmap_Main(float zomm, int X_traslate, int Y_traslate) {
        frame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        frameDrawer = new Canvas(frame);
        paint = new Paint();
        frameDrawer.scale(-1F, 1F);
        frameDrawer.translate(-X_traslate, Y_traslate);
        frameDrawer.scale(zomm, zomm, 0, 0);

        centerX = defaultcenterX;
        centerY = defaultcenterY;
    }

    /**
     * Function for center the bitmap
     */
    public void Center_Bitmap() {
        if (List_entità.size() > 0) {
            float ScaleFactor = 1f;
            frame = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            frameDrawer = new Canvas(frame);
            paint = new Paint();
            frameDrawer.scale(-1F, 1F);
            if (AutoZoomCenter) {
                float minXScaleFactor = (433) / Info_disegno.DeltaX;  //-50 fa fare un zoom all leggermente pi piccolo del canvas
                float minYScaleFactor = (350) / Info_disegno.DeltaY;
                ScaleFactor = Math.min(minXScaleFactor, minYScaleFactor);
                frameDrawer.translate(-400, 10);        //qui la tasca è in alto a dx
            } else {
                ScaleFactor = zoom_canvas; //1f;
                frameDrawer.translate(offsetCentraggioIniziale_X, offsetCentraggioIniziale_Y);        //qui la tasca è in alto a dx
            }
            zoom_canvas = ScaleFactor - 0.1F; //riduco per lasciare margini
            frameDrawer.scale(zoom_canvas, zoom_canvas, 0, 0);
            setOffsetX_Y(x_delta, y_delta);
        }
    }

    /**
     * Function for zoom
     *
     * @param valoreZoom
     */
    public void Zoom(float valoreZoom) {
        if (List_entità.size() > 0) {
            //zoom
            zoom_canvas = zoom_canvas + valoreZoom;
            //frameDrawer.scale(1f + valoreZoom, 1f + valoreZoom, Info_disegno.XCentro - offsetX, Info_disegno.YCentro + offsetY);
            frameDrawer.scale(1f + valoreZoom, 1f + valoreZoom, centerX, centerY);
        }
    }

    /**
     * Function for erase a bitmap
     * <p>
     * TODO This is never used but i think that the bitmap remain alive (like seen in the profiler) so i need a way to free it for let the GC work
     */
    public void EraseBitmap() {
        frame.eraseColor(Color.TRANSPARENT);
        invalidate();
    }

    /**
     * Function for release the bitmap and free the memory
     */
    public void release() {
        // Release the Bitmap Memory
        this.frame.recycle();
        // Release the canvas
       //// this.frameDrawer = null;
    }

    /**
     * Function for update the canvas
     *
     * @param cancella
     */
    public void
    AggiornaCanvas(boolean cancella) {
        if (cancella)
            frame.eraseColor(Color.TRANSPARENT);
        this.invalidate();
    }

    /**
     * Function for draw the entities of a Ricetta
     *
     * @param list_entità
     */
    public void Disegna_entità(List<Element> list_entità) {
        if (list_entità.size() > 0) {
            List<Float> X_max_minList = new ArrayList<Float>(); //creo una list per poi trovare il valore min e max sull'asse X
            List<Float> Y_max_minList = new ArrayList<Float>(); //creo una list per poi trovare il valore min e max sull'asse Y

            // Loopfor every entity
            for (int i = 0; i < list_entità.size(); i++) {
                try {
                    Element entita;

                    entita = list_entità.get(i);

                    X_max_minList.add(entita.pStart.x);
                    Y_max_minList.add(entita.pStart.y);
                    X_max_minList.add(entita.pEnd.x);
                    Y_max_minList.add(entita.pEnd.y);

                    paint.setStrokeCap(Paint.Cap.ROUND);
                    paint.setStyle(Paint.Style.STROKE);

                    // Check the entity type
                    if (entita instanceof ElementLine || entita instanceof ElementZigZag) {
                        if (entita.isSelected) {
                            paint.setColor(Color.YELLOW);
                        } else
                            paint.setColor(Color.RED);
                        if(frameDrawer != null)
                         frameDrawer.drawLine(entita.pStart.x, entita.pStart.y, entita.pEnd.x, entita.pEnd.y, paint);
                    } else if (entita instanceof ElementFeed) {

                        paint.setColor(Color.GREEN);
                        if(frameDrawer != null)
                            frameDrawer.drawLine(entita.pStart.x, entita.pStart.y, entita.pEnd.x, entita.pEnd.y, paint);
                    } else if (entita instanceof ElementArc) {
                        if (entita.isSelected) {
                            paint.setColor(Color.YELLOW);
                        } else {
                            paint.setColor(Color.RED);
                        }
                        RectF rect = new RectF();
                        rect.left = ((ElementArc) entita).left;
                        rect.top = ((ElementArc) entita).top;
                        rect.right = ((ElementArc) entita).right;
                        rect.bottom = ((ElementArc) entita).bottom;
                        float sweep = 0;
                        if (((ElementArc) entita).startAngle > ((ElementArc) entita).endAngle)
                            sweep = Math.abs(((ElementArc) entita).startAngle - ((ElementArc) entita).endAngle); //caso end = 155 start = 186
                        else
                            sweep = (360 - ((ElementArc) entita).endAngle) + ((ElementArc) entita).startAngle;  //nel caso limite dove end =340 start = 19

                        CenterPointRadius cpr = MathGeoTri.CalculateArc_CenterRadius(entita.pStart, ((ElementArc) entita).pMiddle, entita.pEnd);
                        if (MathGeoTri.Sagitta(entita.pStart, entita.pEnd, cpr.radius, sweep) < 0.1) {  //se la "freccia" dell'arco è piccola disegno una linea
                            paint.setColor(Color.YELLOW);
                            frameDrawer.drawLine(entita.pStart.x, entita.pStart.y, entita.pEnd.x, entita.pEnd.y, paint);
                        } else {
                            frameDrawer.drawArc(rect, ((ElementArc) entita).endAngle, sweep, false, paint);
                        }
                    } else if (entita instanceof ElementArcZigZag) {
                        if (entita.isSelected) {
                            paint.setColor(Color.YELLOW);
                        } else {
                            paint.setColor(Color.RED);
                        }
                        RectF rect = new RectF();
                        rect.left = ((ElementArcZigZag) entita).left;
                        rect.top = ((ElementArcZigZag) entita).top;
                        rect.right = ((ElementArcZigZag) entita).right;
                        rect.bottom = ((ElementArcZigZag) entita).bottom;
                        float sweep = 0;
                        if (((ElementArcZigZag) entita).startAngle > ((ElementArcZigZag) entita).endAngle)
                            sweep = Math.abs(((ElementArcZigZag) entita).startAngle - ((ElementArcZigZag) entita).endAngle); //caso end = 155 start = 186
                        else
                            sweep = (360 - ((ElementArcZigZag) entita).endAngle) + ((ElementArcZigZag) entita).startAngle;  //nel caso limite dove end =340 start = 19

                        CenterPointRadius cpr = MathGeoTri.CalculateArc_CenterRadius(entita.pStart, ((ElementArcZigZag) entita).pMiddle, entita.pEnd);
                        if (MathGeoTri.Sagitta(entita.pStart, entita.pEnd, cpr.radius, sweep) < 0.1) {  //se la "freccia" dell'arco è piccola disegno una linea
                            paint.setColor(Color.YELLOW);
                            frameDrawer.drawLine(entita.pStart.x, entita.pStart.y, entita.pEnd.x, entita.pEnd.y, paint);
                        } else {
                            frameDrawer.drawArc(rect, ((ElementArcZigZag) entita).endAngle, sweep, false, paint);
                        }
                    } else {
                        throw new UnsupportedOperationException("Element type not recognized");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Float Xmin = Collections.min(X_max_minList);
            Float Xmax = Collections.max(X_max_minList);
            Float Ymin = Collections.min(Y_max_minList);
            Float Ymax = Collections.max(Y_max_minList);

            Float DeltaX, DeltaY;
            if (Xmax > Xmin) {
                DeltaX = Xmax - Xmin;
            } else {
                DeltaX = Xmin - Xmax;
            }
            if (Ymax > Ymin) {
                DeltaY = Ymax - Ymin;
            } else {
                DeltaY = Ymin - Ymax;
            }
            Info_disegno = new Wrapper(Xmin, Xmax, Ymin, Ymax, (Xmin + Xmax) / 2, (Ymin + Ymax) / 2, DeltaX, DeltaY);
        } else {
            float Xmin = 0;
            float Xmax = 250;
            float Ymin = 0;
            float Ymax = 250;
            float DeltaX = Xmax;
            float DeltaY = Ymax;
            Info_disegno = new Wrapper(Xmin, Xmax, Ymin, Ymax, (Xmin + Xmax) / 2, (Ymin + Ymax) / 2, DeltaX, DeltaY);
        }
    }

    /**
     * Draw steps of a Ricetta in the bitmap
     *
     * @param list_punti
     */
    public void Disegna_punti(ArrayList<PointF> list_punti) {
        if (list_punti.size() == 0) {
            Ricalcola_entità_canvas(List_entità);
        }

        if (list_punti.size() > 0) {
            for (int i = 0; i < list_punti.size(); i++) {
                try {
                    PointF punto = list_punti.get(i);

                    paint.setColor(Color.BLUE);
                    float radius = 1F;
                    if(frameDrawer !=null)
                        frameDrawer.drawCircle(punto.x, punto.y, radius, paint);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // TODO Fix too much codes
        if (List_code.size() > 0 && frameDrawer != null) {
            for (int i = 0; i < List_code.size(); i++) {
                try {
                    //x Murat
                    JamPointCode code = List_code.get(i);

                    String codice = code.tipoCodice.toString();
                    switch (codice) {
                        case "OP1":
                            paint.setColor(Color.YELLOW);
                            paint.setStyle(Paint.Style.FILL);
                            frameDrawer.drawCircle(code.getStep().p.x, code.getStep().p.y, 2F, paint);
                            break;
                        case "OP2":
                            paint.setColor(Color.BLUE);
                            paint.setStyle(Paint.Style.FILL);
                            frameDrawer.drawCircle(code.getStep().p.x, code.getStep().p.y, 2F, paint);
                            break;
                        case "OP3":
                            paint.setColor(Color.RED);
                            paint.setStyle(Paint.Style.FILL);
                            frameDrawer.drawCircle(code.getStep().p.x, code.getStep().p.y, 2F, paint);
                            break;
                        case "SPEED_M8":
                            paint.setColor(Color.GREEN);
                            paint.setStyle(Paint.Style.FILL);
                            frameDrawer.drawRect(code.getStep().p.x, code.getStep().p.y,code.getStep().p.x+2, code.getStep().p.y+2,paint);
                            break;
                        case "TENS_M8":
                            paint.setColor(Color.MAGENTA);
                            paint.setStyle(Paint.Style.FILL);
                            frameDrawer.drawRect(code.getStep().p.x, code.getStep().p.y,code.getStep().p.x+2, code.getStep().p.y+2,paint);
                            break;
                        case "SPLIT1":
                            paint.setColor(Color.YELLOW);
                            paint.setStyle(Paint.Style.FILL);
                            frameDrawer.drawRect(code.getStep().p.x-2, code.getStep().p.y-2,code.getStep().p.x+2, code.getStep().p.y+2,paint);
                            break;
                        case "SPLIT2":
                            paint.setColor(Color.BLACK);
                            paint.setStyle(Paint.Style.FILL);
                            frameDrawer.drawRect(code.getStep().p.x-2, code.getStep().p.y-2,code.getStep().p.x+2, code.getStep().p.y+2,paint);
                            break;


                    }


/*  rimettere se non funziona sopra x Murat
                    paint.setColor(Color.MAGENTA);
                    paint.setStyle(Paint.Style.FILL);
                    frameDrawer.drawCircle(code.getStep().p.x, code.getStep().p.y, 2F, paint);
*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Function for update the canvas from the elements list
     *
     * @param elements
     */
    public void Ricalcola_entità_canvas(List<Element> elements) {
        paint.setColor(Color.LTGRAY);
        frameDrawer.drawRect(0, 0, frame.getWidth(), frame.getHeight(), paint);

        List_entità = elements;
        List_punti.clear();
        List_code = new ArrayList<>();
        for (Element item : elements) {
            for (JamPointStep step : item.steps) {
                try {
                    List_punti.add(new PointF(step.p.x, step.p.y));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (JamPointCode code : item.ricetta.codes) {
                List_code.add(code);
            }
        }
    }
}

// TODO Not good to have this here because it can cause leaks
class Wrapper {
    public float Xmin, Xmax, Ymin, Ymax, XCentro, YCentro, DeltaX, DeltaY;

    public Wrapper(float Xmin, float Xmax, float Ymin, float Ymax, float XCentro, float YCentro, float DeltaX, float DeltaY) {
        this.Xmin = Xmin;
        this.Xmax = Xmax;
        this.Ymin = Ymin;
        this.Ymax = Ymax;
        this.XCentro = XCentro;
        this.YCentro = YCentro;
        this.DeltaX = DeltaX;
        this.DeltaY = DeltaY;
    }
}