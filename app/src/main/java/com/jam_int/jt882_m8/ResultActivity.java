package com.jam_int.jt882_m8;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.jamint.ricette.Element;
import com.jamint.ricette.Ricetta;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import me.jahnen.libaums.core.UsbMassStorageDevice;
import me.jahnen.libaums.core.fs.FileSystem;
import me.jahnen.libaums.core.fs.UsbFile;

public class ResultActivity extends AppCompatActivity {

    /**
     * Vars for activity result
     */
    final private static int RESULT_PAGE_LOAD_EEP = 102;
    final private static int RESULT_PAGE_CODE = 103;
    final private static int POPUPFOLDER = 104;
    /**
     * List of values of the pocket
     */
    HashMap pocketValues = new LinkedHashMap();
    /**
     * Ricetta vars that will contain the 2 Ricetta values
     */
    Ricetta r = new Ricetta(Values.plcType);
    Ricetta r1 = new Ricetta(Values.plcType);

    /**
     * The 2 dynamic view that contain the Ricetta draw
     */
    Dynamic_view rViewDraw;
    Dynamic_view r1ViewDraw;
    FrameLayout frame_canvas_r;
    FrameLayout frame_canvas_r1;

    /**
     * The Save folder for a Ricetta
     */
    String currentSaveFolder = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ricette";

    /**
     * USB
     */
    UsbFile rootusb;
    UsbMassStorageDevice device_usb;
    FileSystem currentFs;

    /**
     * Thread_LoopEmergenza
     */
    Thread_LoopEmergenza thread_LoopEmergenza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Log.d("JAM TAG", "ResultActivity");

        thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
        thread_LoopEmergenza.thread_LoopEmergenza_Start(this);

        // Invert Vars because the draw is mirrored
        try {
            pocketValues.put("M1", Values.M2);
            pocketValues.put("M2", Values.M1);
            pocketValues.put("M3", Values.M4);
            pocketValues.put("M4", Values.M3);
            pocketValues.put("M5", Values.M6);
            pocketValues.put("M6", Values.M5);

            pocketValues.put("H1", Values.H2);
            pocketValues.put("H2", Values.H1);
            pocketValues.put("H3", Values.H4);
            pocketValues.put("H4", Values.H3);

            pocketValues.put("L", Values.L);

            pocketValues.put("S1", Values.S2);
            pocketValues.put("S2", Values.S1);
            pocketValues.put("S3", Values.S4);
            pocketValues.put("S4", Values.S3);

            pocketValues.put("P", -Values.P);

            pocketValues.put("Hf", Values.Hf);

            pocketValues.put("M", Values.M);

            pocketValues.put("A", Values.F);
            pocketValues.put("B", Values.G);
            pocketValues.put("C", Values.H);

            pocketValues.put("F", Values.A);
            pocketValues.put("G", Values.B);
            pocketValues.put("H", Values.C);

            pocketValues.put("E", Values.D);

            pocketValues.put("D", Values.E);

            pocketValues.put("LP", Values.LP);

            pocketValues.put("I", Values.I);
            pocketValues.put("Lm", Values.Lm);
            pocketValues.put("N", Values.N);
            pocketValues.put("O", Values.O);

            frame_canvas_r = findViewById(R.id.FrameLayout_r);
            frame_canvas_r1 = findViewById(R.id.FrameLayout_r1);
        } catch (Exception e) {
            e.printStackTrace();
            ShowErrorReport(e);
        }

        Draw();
    }

    /**
     * onPause
     */
    public void onPause() {     // system calls this method as the first indication that the user is leaving your activity
        super.onPause();
        try {
            thread_LoopEmergenza.KillThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        if(!thread_LoopEmergenza.getThreadStatus()){
            thread_LoopEmergenza = new Thread_LoopEmergenza();              //thread comunicazione con M8 per controllare l'Emergenza (senza il sockect dopo un pò si chiude)
            thread_LoopEmergenza.thread_LoopEmergenza_Start(this);
            Log.d("JAM TAG", "ResultActivity");

        }

    }
    /**
     * Function for draw the 2 Ricetta
     */
    public void Draw() {
        try {
            // Reset Ricette
            r = new Ricetta(Values.plcType);
            r1 = new Ricetta(Values.plcType);

            // Read points from pts
            HashMap<String, PointF> pointsPTS =  ResultActivityMaths.ReadPointsFromPTS(Values.File);

            // Convert the point to a list for reorder
            ArrayList<Map.Entry<String, PointF>> list = new ArrayList<Map.Entry<String, PointF>>();
            list.addAll(pointsPTS.entrySet());

            // Reorder the points by number
            Collections.sort(list, new Comparator<Map.Entry<String, PointF>>() {
                public int compare(Map.Entry<String, PointF> o1, Map.Entry<String, PointF> o2) {
                    Integer pos1 = Integer.parseInt(o1.getKey().toString().replace("P", ""));
                    Integer pos2 = Integer.parseInt(o2.getKey().toString().replace("P", ""));
                    return pos1.compareTo(pos2);
                }
            });

            // Add the reordered points to a list
            ArrayList<PointF> Lista_punti_pts = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Lista_punti_pts.add(list.get(i).getValue());
            }

            // Do all the math, codes, ecc... for the 2 Ricetta
            Pair<Ricetta, Ricetta> ricette = ResultActivityMaths.ChoseModel(this, Values.model, Values.model1, Values.type, Values.File, pocketValues);
            // Copy the 2 Ricetta result in local vars
            r = ricette.first;
            r1 = ricette.second;

            CoordPosPinza Coord_Pinza = new CoordPosPinza();

            // Draw the first Ricetta
            ArrayList List_entita = (ArrayList<Element>) r.elements;
            rViewDraw = new Dynamic_view(this, 500, 400, List_entita, 1.15F, Coord_Pinza, false, -400, 10, Lista_punti_pts, getResources().getDimension(R.dimen.modifica_programma_activity_framelayout_width), getResources().getDimension(R.dimen.modifica_programma_activity_framelayout_height));
            frame_canvas_r.addView(rViewDraw);
            rViewDraw.setBackgroundColor(Color.LTGRAY);
            rViewDraw.Disegna_entità(List_entita);
            rViewDraw.Center_Bitmap();

            // Draw the second Ricetta
            ArrayList List_entita_1 = (ArrayList<Element>) r1.elements;
            r1ViewDraw = new Dynamic_view(this, 500, 400, List_entita_1, 1.15F, Coord_Pinza, false, -400, 10, Lista_punti_pts, getResources().getDimension(R.dimen.modifica_programma_activity_framelayout_width), getResources().getDimension(R.dimen.modifica_programma_activity_framelayout_height));
            frame_canvas_r1.addView(r1ViewDraw);
            r1ViewDraw.setBackgroundColor(Color.LTGRAY);
            r1ViewDraw.Disegna_entità(List_entita_1);
            r1ViewDraw.Center_Bitmap();

            // If r1 is empty, hide the r1 frame layout
            if (ricette.second.elements.isEmpty()) {
                frame_canvas_r1.setVisibility(View.GONE);
                findViewById(R.id.Button_H2ZoomAll).setVisibility(View.GONE);
                findViewById(R.id.Button_H2ZoomIn).setVisibility(View.GONE);
                findViewById(R.id.Button_H2ZoomOut).setVisibility(View.GONE);
            }

            if (Values.Dpc != 0) {
                if (r.elements.size() != 0)
                    r.move(0, -Values.Dpc);
                r.pcY += Values.Dpc;
                if (r1.elements.size() != 0)
                    r1.move(0, -Values.Dpc);
                r1.pcY += Values.Dpc;
                Values.Dpc = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ShowErrorReport(e);
        }
    }

    /**
     * Close the current activity and go back
     *
     * @param view
     */
    public void BtnBackPage(View view) {
        finish();
    }

    /**
     * Return to the tool page
     *
     * @param view
     */
    public void BtnExitPage(View view) {
        Intent intent = new Intent(this, Tool_page.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Button for save the 2 Ricetta
     *
     * @param view
     */
    public void BtnSave(View view) {
        // Start a Dialog for choose the Ricetta name
        AlertDialogSave(currentSaveFolder);
    }

    /**
     * Button for open Code Activity
     *
     * @param view
     */
    public void BtnCodesPage(View view) {
        Intent intent = new Intent(this, CodeActivity.class);
        startActivityForResult(intent, RESULT_PAGE_CODE);
    }

    /**
     * Button for center the zoom for first draw
     *
     * @param view
     */
    public void Btn_ZoomCenter_H1(View view) {
        rViewDraw.Center_Bitmap_Main(1.3F, 380, 20);
        rViewDraw.AggiornaCanvas(true);
    }

    /**
     * Button for zoom in for the first draw
     *
     * @param view
     */
    public void Btn_ZoomIn_H1(View view) {
        rViewDraw.Zoom(0.1F);
        rViewDraw.AggiornaCanvas(true);
    }

    /**
     * Button for zoom out for the first draw
     *
     * @param view
     */
    public void Btn_ZoomOut_H1(View view) {
        if (rViewDraw.zoom_canvas > 0.2) {
            rViewDraw.Zoom(-0.1F);
            rViewDraw.AggiornaCanvas(true);
        }
    }

    /**
     * Button for center the zoom for second draw
     *
     * @param view
     */
    public void Btn_ZoomCenter_H2(View view) {
        r1ViewDraw.Center_Bitmap_Main(1.3F, 380, 20);
        r1ViewDraw.AggiornaCanvas(true);
    }

    /**
     * Button for zoom in for the second draw
     *
     * @param view
     */
    public void Btn_ZoomIn_H2(View view) {
        r1ViewDraw.Zoom(0.1F);
        r1ViewDraw.AggiornaCanvas(true);
    }

    /**
     * Button for zoom out for the first draw
     *
     * @param view
     */
    public void Btn_ZoomOut_H2(View view) {
        if (r1ViewDraw.zoom_canvas > 0.2) {
            r1ViewDraw.Zoom(-0.1F);
            r1ViewDraw.AggiornaCanvas(true);
        }
    }

    /**
     * Save the last values on a file for reload them when reopen the FDraw activity
     */
    public void SaveLastValuesFile() {
        // Salva i valori in un file per il bottone Last Value
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "JamData");
            if (!root.exists()) {
                root.mkdirs();
            }

            File gpxfile = new File(root, "LastValues.txt");

            BufferedWriter out = new BufferedWriter(new FileWriter(gpxfile.toString()));

            out.write(Values.imgScheletro + "\n");
            out.write(Values.M1 + "\n");
            out.write(Values.M2 + "\n");
            out.write(Values.M3 + "\n");
            out.write(Values.M4 + "\n");
            out.write(Values.H2 + "\n");
            out.write(Values.H1 + "\n");
            out.write(Values.L + "\n");
            out.write(Values.S2 + "\n");
            out.write(Values.S1 + "\n");
            out.write(Values.P + "\n");
            out.write(Values.Hf + "\n");
            out.write(Values.H3 + "\n");
            out.write(Values.H4 + "\n");
            out.write(Values.M5 + "\n");
            out.write(Values.M6 + "\n");
            out.write(Values.S3 + "\n");
            out.write(Values.S4 + "\n");

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function for display a Dialog for insert the Ricetta name
     *
     * @param FolderPath
     */
    private void AlertDialogSave(String FolderPath) {
        SaveLastValuesFile();

        // Creating alert Dialog with one Button
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(getResources().getString(R.string.Save));
        alertDialog.setMessage(FolderPath);

        EditText input = new EditText(this);
        input.setFocusable(false);
        input.setOnTouchListener(new View.OnTouchListener() {

            //	@SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    KeyDialog_lettere.Lancia_KeyDialogo_lettere(ResultActivity.this, input, "");
                }
                return false;
            }
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton(getResources().getString(R.string.Save),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String FileName = input.getText().toString();

                        // Check if FileName is not empty
                        if (!FileName.matches("")) {
                            // Check if r1 is empty
                            if (r1.elements.isEmpty()) {
                                // Save Only r
                                if (new File(currentSaveFolder, FileName + ".xml").exists() || new File(currentSaveFolder, FileName + ".usr").exists()) {
                                    AlertDialogOverrideR(currentSaveFolder, FileName);
                                } else {
                                    SaveRicetta(currentSaveFolder, FileName, r);
                                    Values.File_XML_path_R = currentSaveFolder + "/" + FileName + ".xml";
                                    Values.Chiamante = "CREATO_TASCA_DA_QUOTE_1";
                                    CaricaPaginaSendToCn( RESULT_PAGE_LOAD_EEP,currentSaveFolder+"/"+FileName + ".xml","","");
                                }
                            } else {
                                // Save r and r1
                                AlertDialogRadioButtonChooseSaves(currentSaveFolder, FileName);
                            }
                        } else {
                            AlertDialogSave(currentSaveFolder);
                        }
                    }
                });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton(getResources().getString(R.string.Cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.setNeutralButton(getResources().getString(R.string.Folder),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent_par = new Intent(getApplicationContext(), PopUpSelectFolder.class);
                        startActivityForResult(intent_par, POPUPFOLDER);
                    }
                });

        alertDialog.show();
    }

    /**
     * FUnction for display a Dialog for override Ricetta1 and Ricetta2
     *
     * @param Folder
     * @param FileName
     * @param rb12Checked
     * @param rb1Checked
     * @param rb12UsbChecked
     * @param rb2Checked
     * @param rb2UsbChecked
     */
    private void AlertDialogOverrideR1R2(String Folder, String FileName, boolean rb12Checked, boolean rb1Checked, boolean rb12UsbChecked, boolean rb2Checked, boolean rb2UsbChecked) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResultActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("overWrite");

        // Setting Dialog Message
        alertDialog.setMessage("overWrite?");

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SavesRicettaRB(Folder, FileName, rb12Checked, rb1Checked, rb12UsbChecked, rb2Checked, rb2UsbChecked);
                        LoadRicettaR1R2(Folder, FileName + "_1.xml");
                    }
                });

        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    /**
     * Function for display a radio button list for choose which Ricetta save
     *
     * @param Folder
     * @param FileName
     */
    private void AlertDialogRadioButtonChooseSaves(String Folder, String FileName) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResultActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle(getResources().getString(R.string.Save));

        final RadioGroup rg = new RadioGroup(ResultActivity.this);

        final RadioButton rb12 = new RadioButton(ResultActivity.this);
        final RadioButton rb1 = new RadioButton(ResultActivity.this);
        final RadioButton rb2 = new RadioButton(ResultActivity.this);

        rb12.setText("1-2");
        rg.addView(rb12);
        rb1.setText("1");
        rg.addView(rb1);
        rb2.setText("2");
        rg.addView(rb2);

        final RadioButton rb12u = new RadioButton(ResultActivity.this);
        final RadioButton rb2u = new RadioButton(ResultActivity.this);
        if (UsbConnected()) {
            rb12u.setText("1-2 usb");
            rg.addView(rb12u);
            rb2u.setText("2 usb");
            rg.addView(rb2u);
        }

        rg.check(rb12.getId());

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        rg.setLayoutParams(lp);
        alertDialog.setView(rg);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton(getResources().getString(R.string.Save),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Check if at least 1 file already exist
                        if (new File(Folder, FileName + "_1.xml").exists() || new File(Folder, FileName + "_1.usr").exists() || new File(Folder, FileName + "_2.xml").exists() || new File(Folder, FileName + "_2.usr").exists()) {
                            //AlertDialog override existing files
                            AlertDialogOverrideR1R2(Folder, FileName, rb12.isChecked(), rb1.isChecked(), rb12u.isChecked(), rb2.isChecked(), rb2u.isChecked());
                        } else {
                            SavesRicettaRB(Folder, FileName, rb12.isChecked(), rb1.isChecked(), rb12u.isChecked(), rb2.isChecked(), rb2u.isChecked());
                            LoadRicettaR1R2(Folder, FileName + "_1.xml");
                        }
                    }
                });

        AlertDialog d = alertDialog.show();
        d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        d.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    /**
     * Function for send Ricetta to CN
     *
     * @param Folder
     * @param fileName
     */
    private void LoadRicettaR1R2(String Folder, String fileName) {

        if(Values.Chiamante.equals("CREATO_TASCA_DA_QUOTE_1"))
            CaricaPaginaSendToCn(RESULT_PAGE_LOAD_EEP, "",Folder+"/"+fileName,"");
        if(Values.Chiamante.equals("CREATO_TASCA_DA_QUOTE_2")){
            String fileName_r2 = fileName.replace("_1","_2");
            CaricaPaginaSendToCn(RESULT_PAGE_LOAD_EEP, "","", Folder+"/"+fileName_r2);}
        if(Values.Chiamante.equals("CREATO_TASCA_DA_QUOTE_12")) {
            String fileName_r2 = fileName.replace("_1","_2");
            CaricaPaginaSendToCn(RESULT_PAGE_LOAD_EEP, "", Folder + "/" + fileName, Folder+"/"+fileName_r2);
        }

    }

    /**
     * Function for save the Ricetta based on the radio button selected
     *
     * @param Folder
     * @param FileName
     * @param rb12Checked
     * @param rb1Checked
     * @param rb12UsbChecked
     * @param rb2Checked
     * @param rb2UsbChecked
     */
    private void SavesRicettaRB(String Folder, String FileName, boolean rb12Checked, boolean rb1Checked, boolean rb12UsbChecked, boolean rb2Checked, boolean rb2UsbChecked) {

        if (rb1Checked) {
            SaveRicetta(Folder, FileName + "_1", r);
            Values.Chiamante = "CREATO_TASCA_DA_QUOTE_1";
            Values.File_XML_path_R = Folder+"/"+FileName + "_1.xml";
        }
        if (rb2Checked) {
            SaveRicetta(Folder, FileName + "_2", r1);
            Values.Chiamante = "CREATO_TASCA_DA_QUOTE_2";
            Values.File_XML_path_T2_R = Folder+"/"+FileName + "_2.xml";
        }

        if (rb12Checked) {
            SaveRicetta(Folder, FileName + "_1", r);
            SaveRicetta(Folder, FileName + "_2", r1);
            Values.Chiamante = "CREATO_TASCA_DA_QUOTE_12";
            Values.File_XML_path_R = Folder+"/"+FileName + "_1.xml";
            Values.File_XML_path_T2_R = Folder+"/"+FileName + "_2.xml";
        }
    }

    /**
     * Function for save a RIcetta
     *
     * @param Folder
     * @param FileName
     * @param r
     */
    private void SaveRicetta(String Folder, String FileName, Ricetta r) {
        File dir;
        if (Folder == null) {
            dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ricette");
        } else {
            dir = new File(Folder);
        }

        File fileXml = new File(dir, FileName + ".xml");
        File fileUsr = new File(dir, FileName + ".usr");

        dir.mkdirs();
        try {
            SetXmlIniValue(r);  //carico nei valori iniziali del xml i valori memorizzati nelle variabili Values
            r.save(fileXml);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "error saving xml file ", Toast.LENGTH_SHORT).show();
        }

        try {
            r.exportToUsr(fileUsr);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "error Usr export ", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Function for save a Ricetta on a USB
     *
     * @param Folder
     * @param FileName
     * @param r
     */
    private void SaveRicettaUsb(String Folder, String FileName, Ricetta r) {
        File dir;
        if (Folder == null) {
            dir = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ricette");
        } else {
            dir = new File(Folder);
        }

        File fileXml = new File(dir, FileName + ".xml");

        dir.mkdirs();
        try {
            SetXmlIniValue(r);  //carico nei valori iniziali del xml i valori memorizzati nelle variabili Values
            r.save(fileXml);
            Utility.copyFileToUsb(fileXml, rootusb);
            fileXml.delete();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "error saving xml file ", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     * @param r
     */
    private void SetXmlIniValue(Ricetta r) {
        if(Values.UdfPuntiVelIni_T1 <0d || Values.UdfPuntiVelIni_T1 >0.010d) Values.UdfPuntiVelIni_T1 = 0.003d;
        r.UdfPuntiVelIni = Values.UdfPuntiVelIni_T1;

        if(Values.UdfVelIniRPM_T1 <100 || Values.UdfVelIniRPM_T1 >1000) Values.UdfVelIniRPM_T1 = 300;
        r.UdfVelIniRPM = Values.UdfVelIniRPM_T1;

        if(Values.UdfPuntiVelRall_T1 <0d || Values.UdfPuntiVelRall_T1 >0.010d) Values.UdfPuntiVelRall_T1 = 0.003d;
        r.UdfPuntiVelRall = Values.UdfPuntiVelRall_T1;

        if(Values.UdfVelRallRPM_T1 <100 || Values.UdfVelRallRPM_T1 >1000) Values.UdfVelRallRPM_T1 = 300;
        r.UdfVelRallRPM = Values.UdfVelRallRPM_T1;

        if(Values.Udf_FeedG0_T1 <10 || Values.Udf_FeedG0_T1 >100000) Values.Udf_FeedG0_T1 = 80000;
        r.Udf_FeedG0 = Values.Udf_FeedG0_T1;

        if(Values.Udf_ValTensione_T1 <1 || Values.Udf_ValTensione_T1 >100) Values.Udf_ValTensione_T1 = 10;
        r.Udf_ValTensioneT1 = Values.Udf_ValTensione_T1;

        r.Udf_20 = 0;

        if(Values.Udf_ValElettrocalamitaSopra_T1 <1 || Values.Udf_ValElettrocalamitaSopra_T1 >100) Values.Udf_ValElettrocalamitaSopra_T1 = 50;
        r.Udf_ValElettrocalamitaSopra = Values.Udf_ValElettrocalamitaSopra_T1;

        if(Values.Udf_ValElettrocalamitaSotto_T1 <1 || Values.Udf_ValElettrocalamitaSotto_T1 >100) Values.Udf_ValElettrocalamitaSotto_T1 = 50;
        r.Udf_ValElettrocalamitaSotto = Values.Udf_ValElettrocalamitaSotto_T1;

        if(Values.Udf_VelocitaCaricLavoro_T1 <1 || Values.Udf_VelocitaCaricLavoro_T1 >10000) Values.Udf_ValElettrocalamitaSotto_T1 = 80000;
        r.Udf_VelocitaCaricLavoro = Values.Udf_VelocitaCaricLavoro_T1;

        r.Udf_24 = 0;
        r.Udf_25 = 0;
        r.Udf_26 = 0;
        r.Udf_27 = 0;
        r.Udf_28 = 0;
        r.Udf_29 = 0;
        r.Udf_30 = 0;

        if(Values.Udf_SequenzaPiegatore_chiusura_T1 <1 || Values.Udf_SequenzaPiegatore_chiusura_T1 >9999) Values.Udf_SequenzaPiegatore_chiusura_T1 = 1230;
        r.Udf_SequenzaPiegatore_chiusura = Values.Udf_SequenzaPiegatore_chiusura_T1;

        if(Values.Udf_SequenzaPiegatore_apetura_T1 <1 || Values.Udf_SequenzaPiegatore_apetura_T1 >9999) Values.Udf_SequenzaPiegatore_apetura_T1 = 321;
        r.Udf_SequenzaPiegatore_apetura = Values.Udf_SequenzaPiegatore_apetura_T1;
    }

    /**
     * Function for display a Dialog for choose if override
     *
     * @param Folder
     * @param FileName
     */
    private void AlertDialogOverrideR(String Folder, String FileName) {
        final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(ResultActivity.this);

        // Setting Dialog Title
        alertDialog1.setTitle("overwrite");

        // Setting Dialog Message
        alertDialog1.setMessage("overwrite?");

        // Setting Positive "Yes" Button
        alertDialog1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SaveRicetta(Folder, FileName, r);
                Values.File_XML_path_R = Folder + "/" + FileName + ".xml";
                Values.Chiamante = "CREATO_TASCA_DA_QUOTE_1";
                CaricaPaginaSendToCn( RESULT_PAGE_LOAD_EEP,Folder+"/"+FileName + ".xml","","");

            }
        });

        alertDialog1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog1.show();
    }

    /**
     * Function for send a Ricetta to CN
     *
     * @param RESULT_PAGE_LOAD_EEP
     */
    public void CaricaPaginaSendToCn( int RESULT_PAGE_LOAD_EEP,String FilePath,String FilePath_Da_Tasca_Quote_r1,String FilePath_Da_Tasca_Quote_r2) {
        File dir;

        Intent intent_par = new Intent(this, Select_file_to_CN.class);

        intent_par.putExtra("operazione", "Saving....");
        intent_par.putExtra("Chiamante","PAGE_CREA_TASCA");

        intent_par.putExtra("File_path", FilePath);
        intent_par.putExtra("FilePath_Da_Tasca_Quote_r1", FilePath_Da_Tasca_Quote_r1);
        intent_par.putExtra("FilePath_Da_Tasca_Quote_r2", FilePath_Da_Tasca_Quote_r2);


        startActivityForResult(intent_par, RESULT_PAGE_LOAD_EEP);
    }

    /**
     * Function for check if USB is connected
     *
     * @return
     */
    public boolean UsbConnected() {
        UsbMassStorageDevice[] devices = UsbMassStorageDevice.getMassStorageDevices(this);
        if (devices.length > 0) {
            try {
                device_usb = devices[0];
                device_usb.init();

                // Only uses the first partition on the device
                currentFs = device_usb.getPartitions().get(0).getFileSystem();
                Log.d("TAG", "Capacity: " + currentFs.getCapacity());
                Log.d("TAG", "Occupied Space: " + currentFs.getOccupiedSpace());
                Log.d("TAG", "Free Space: " + currentFs.getFreeSpace());
                Log.d("TAG", "Chunk size: " + currentFs.getChunkSize());
                rootusb = currentFs.getRootDirectory();

                return true;
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.ErroreUSB_FAT32), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Function for display a message that contains all the vars and the error
     *
     * @param e
     */
    private void ShowErrorReport(Exception e) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        // Setting Dialog Title
        alertDialog.setTitle("Error Report");

        StringBuilder sb = new StringBuilder();
        sb.append("M1 =" + Values.M2 + " M2 =" + Values.M1 + " M3 =" + Values.M4 + " M4 =" + Values.M3 + " M5 =" + Values.M6 + " M6 =" + Values.M5 + " H1 =" + Values.H2 + " H2 =" + Values.H1 + " H3 =" + Values.H4 + " H4 =" + Values.H3 + " L =" + Values.L);
        sb.append("\n");
        sb.append("S1 =" + Values.S2 + " S2 =" + Values.S1 + " S3 =" + Values.S4 + " S4 =" + Values.S3 + " P =" + Values.P + " Hf =" + Values.Hf);
        sb.append("\n");
        sb.append("M =" + Values.M + " A =" + Values.F + " B =" + Values.G + " C =" + Values.H + " F =" + Values.A + " G =" + Values.B + " H =" + Values.C);
        sb.append("\n");
        sb.append("E =" + Values.D + " D =" + Values.E + " LP =" + Values.LP + " I =" + Values.I + " Lm =" + Values.Lm);
        sb.append("\n");
        sb.append("model =" + Values.model + " model1 =" + Values.model1 + " type =" + Values.type);
        sb.append("\n");
        sb.append("Error = " + e.toString());

        for (StackTraceElement element : e.getStackTrace()) {
            sb.append("\n");
            sb.append("StackTrace = " + element);
        }


        String msg = sb.toString();

        new AlertDialog.Builder(this)
                .setTitle("Debug Data:")
                .setCancelable(false)
                .setMessage(msg)
                .setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(ResultActivity.this, Tool_page.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_PAGE_LOAD_EEP:

                Intent intent = new Intent(this, Tool_page.class);
                intent.putExtra("ACTIVITY_TASCA_QUOTE", "CARICATO_T1_DX_DA_QUOTE");
                intent.setData(Uri.parse("CARICATO_T1_DX_DA_QUOTE"));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                setResult(RESULT_OK, intent);
                startActivity(intent);

            break;
            case POPUPFOLDER: {
                // Get selected folder for save the file
                currentSaveFolder = data.getExtras().getString("FolderPath");
                AlertDialogSave(currentSaveFolder);
            }
            break;
            case RESULT_PAGE_CODE: {
                // Redraw
                Draw();
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            // This work only for android 4.4+
            int currentApiVersion;
            currentApiVersion = Build.VERSION.SDK_INT;
            if (currentApiVersion >= Build.VERSION_CODES.KITKAT) {

                getWindow().getDecorView().setSystemUiVisibility(flags);

                // Code below is to handle presses of Volume up or Volume down.
                // Without this, after pressing volume buttons, the navigation bar will
                // show up and won't hide
                final View decorView = getWindow().getDecorView();
                decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            decorView.setSystemUiVisibility(flags);
                        }
                    }
                });
            }
        }
    }
}