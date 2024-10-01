package com.jam_int.jt882_m8;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.PointF;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.jamint.ricette.Ricetta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import communication.ShoppingList;
import me.jahnen.libaums.core.fs.FileSystem;
import me.jahnen.libaums.core.fs.UsbFile;
import me.jahnen.libaums.core.fs.UsbFileOutputStream;
import me.jahnen.libaums.core.fs.UsbFileStreamFactory;

public class Utility {
    /**
     * Function for get the file name with a lowercase extension
     *
     * @param destinationLocation
     * @return
     */
    public static String Downcase_ExtensionFile(File destinationLocation) {

        String filename = destinationLocation.getAbsolutePath();

        String filename_less_estension = SubString.BeforeLast(filename, ".");
        String estensione = SubString.After(filename, ".");
        String ext_downcase = estensione.toLowerCase();

        File destFile = new File(filename_less_estension + "." + ext_downcase);

        if (destinationLocation.renameTo(destFile)) {
            System.out.println("File renamed successfully");
        } else {
            System.out.println("Failed to rename file");
        }
        return filename_less_estension + "." + ext_downcase;
    }

    /**
     * Function for count the running threads
     *
     * @return
     */
    public static int ContaThread() {
        ThreadGroup currentGroup1 = Thread.currentThread().getThreadGroup();
        int noThreads1 = currentGroup1.activeCount();
        Thread[] lstThreads1 = new Thread[noThreads1];
        currentGroup1.enumerate(lstThreads1);
        return lstThreads1.length;
    }

    /**
     * Function for create a folder
     *
     * @param path
     * @return
     */
    public static boolean Crea_cartella(String path) {
        File folder = new File(path);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }

        return success;
    }

    /**
     * Function for create and empty Ricetta
     *
     * @return
     */
    public static File CreaProgCucituraVuoto(Context context) {
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/JamData");

        dir.mkdirs();
        File file = new File(dir, "file_empty.xml");
        File file1 = new File(dir, "file_empty.usr");

        if (!file.exists() && !file1.exists()) {
            Ricetta r = new Ricetta(Values.plcType);
            r.setDrawPosition(new PointF(0.1f, 0f));
            r.drawFeedTo(new PointF(10f, 10f));
            r.drawFeedTo(new PointF(0.1f, 0f));
            try {
                r.save(file);
                try {
                    r.exportToUsr(file1);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "error Usr export ", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "error saving xml file ", Toast.LENGTH_SHORT).show();
            }
        }

        return file;
    }

    /**
     * Function for change toggle button status
     *
     * @param mci_write
     * @param button
     * @param ic_press
     * @param ic_unpress
     */
    public static void GestioneVisualizzazioneToggleButton(Context context, Mci_write mci_write, Button button, String ic_press, String ic_unpress) {
        if (Double.compare(mci_write.valore_precedente, (Double) mci_write.mci.getValue()) != 0) {
            if ((Double) mci_write.mci.getValue() == 1.0d) {
                int image_Premuto = context.getResources().getIdentifier(ic_press, "drawable", context.getPackageName());
                button.setBackground(context.getResources().getDrawable((image_Premuto)));
            } else {
                int image_Premuto = context.getResources().getIdentifier(ic_unpress, "drawable", context.getPackageName());
                button.setBackground(context.getResources().getDrawable((image_Premuto)));
            }

            mci_write.valore_precedente = (Double) mci_write.mci.getValue();
        }
    }

    /**
     * Function for change Edge button status
     *
     * @param mci_write
     */
    public static void GestiscoMci_Edge_Out(ShoppingList sl, Mci_write mci_write) {
        switch (mci_write.mc_stati) {
            case 0:
                if (mci_write.Fronte_positivo == true) {
                    mci_write.mci.setValue(1.0d);
                    sl.WriteItem(mci_write.mci);
                    mci_write.mc_stati = 10;
                    mci_write.Fronte_positivo = false;
                }
                break;
            case 10:
                if (mci_write.Fronte_negativo == true) {
                    mci_write.mci.setValue(0.0d);
                    sl.WriteItem(mci_write.mci);
                    mci_write.mc_stati = 0;
                    mci_write.Fronte_negativo = false;
                }
                break;
            default:
                break;
        }
    }

    /**
     * Function for change toggle button status
     *
     * @param mci_write
     */
    public static void GestiscoMci_Out_Toggle(ShoppingList sl, Mci_write mci_write) {
        switch (mci_write.mc_stati) {
            case 0:
                if (mci_write.Fronte_positivo == true) {
                    if ((Double) mci_write.mci.getValue() == 0.0d) {
                        mci_write.mci.setValue(1.0d);
                    } else {
                        mci_write.mci.setValue(0.0d);
                    }

                    sl.WriteItem(mci_write.mci);
                    mci_write.mc_stati = 10;
                    mci_write.Fronte_positivo = false;
                }
                break;
            case 10:
                mci_write.mc_stati = 0;
                break;
            default:
                break;
        }
    }

    /**
     * Fuction for send to the PLC a Mci var value
     * <p>
     * NOTE:
     * In java i can do this because if i change the value of sl even after the end of the function the sl value is changed
     *
     * @param variabile
     */
    public static void ScrivoVbVnVq(ShoppingList sl, Mci_write variabile) {
        if (variabile.write_flag == true) {
            variabile.write_flag = false;
            if (variabile.tipoVariabile == Mci_write.TipoVariabile.VQ)
                variabile.mci.setValue(variabile.valore * 1000);
            else
                variabile.mci.setValue(variabile.valore);
            sl.WriteItem(variabile.mci);
        }
    }

    /**
     * Function for copy a file from HD to USB
     *
     * @param sourceFile
     * @param folderUsb
     * @return
     * @throws IOException
     */
    public static Boolean copyFileToUsb(File sourceFile, UsbFile folderUsb) throws IOException {
        if (sourceFile.isDirectory()) { //e un file o folder?
            UsbFile f = folderUsb.search(sourceFile.getName());
            if (f != null) f.delete();

            UsbFile folder1 = folderUsb.createDirectory(sourceFile.getName());

            File[] allEntries = new File(sourceFile.getPath()).listFiles();
            if (allEntries != null) {
                for (File files : allEntries) {
                    UsbFile file = folder1.createFile(files.getName());

                    OutputStream os = new UsbFileOutputStream(file);

                    os.write(getByte(files.getPath()));
                    os.close();
                }
            }
        } else {
            UsbFile f = folderUsb.search(sourceFile.getName());
            if (f != null) f.delete();

            UsbFile file = folderUsb.createFile(sourceFile.getName());

            OutputStream os = new UsbFileOutputStream(file);

            os.write(getByte(sourceFile.getPath()));
            os.close();
        }
        return true;
    }

    /**
     * Function for copy a file from USB to HMI
     *
     * @param sourceFile
     * @param HMIPath
     * @param currentFs
     * @return
     * @throws IOException
     */
    public static Boolean copyFileToHMI(UsbFile sourceFile, File HMIPath, FileSystem currentFs)
            throws IOException {
        if (HMIPath != null) {
            InputStream is = UsbFileStreamFactory.createBufferedInputStream(sourceFile, currentFs);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;

            //byte[] data = new byte[16384];
            byte[] data = new byte[4080];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            FileOutputStream outputStream = new FileOutputStream(HMIPath);
            outputStream.write(buffer.toByteArray());
            outputStream.close();
            return true;
        }

        return false;
    }

    /**
     * Function for copy a file from USB to HMI
     *
     * @param sourceFile
     * @param HMIPath
     * @param currentFs
     * @return
     * @throws IOException
     */
    public static Boolean copyFileToHMI_Upgrade(UsbFile sourceFile, File HMIPath, FileSystem currentFs)
            throws IOException {
        try {
            if (HMIPath != null) HMIPath.delete();

            HMIPath.createNewFile();

            InputStream is = UsbFileStreamFactory.createBufferedInputStream(sourceFile, currentFs);

            ByteArrayOutputStream output = new ByteArrayOutputStream();



            byte[] bytes = new byte[4080];
           // byte[] bytes = new byte[1024];
            long count = 0;
            int n = 0;
            while (-1 != (n = is.read(bytes))) {
                output.write(bytes, 0, n);
                count += n;
            }

            FileOutputStream outputStream = new FileOutputStream(HMIPath);
            outputStream.write(output.toByteArray());
            outputStream.close();
        } catch (OutOfMemoryError | IOException ome) {
           // Toast.makeText(getApplicationContext(), "There is a problem with the USB, connect to the pc and check for errors", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    /**
     * TODO To move to Utility
     *
     * @param path
     * @param outPath
     * @return
     */
    public static boolean copyFileFromAssetsRecursively(Context context, String path, String outPath) {
        boolean ret = false;
        try {
            AssetManager assetManager = context.getAssets();
            String[] assets = assetManager.list(path);

            // Check if there is files inside the path
            if (assets.length == 0) {
                Utility.copyFileFromAssets(context, path, outPath);
            } else {
                String lastcar = outPath.substring(outPath.length() - 1);
                String fullPath;
                if (lastcar.equals("/"))
                    fullPath = outPath + path;
                else
                    fullPath = outPath + "/" + path;

                File dir = new File(fullPath);
                if (!dir.exists()) {
                    if (!dir.mkdir())
                        Toast.makeText(context, "No create external directory: " + dir, Toast.LENGTH_SHORT).show();
                }

                for (String asset : assets) {
                    copyFileFromAssetsRecursively(context, path + "/" + asset, outPath);
                }
            }
            ret = true;
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.e(TAG, "I/O Exception", ex);
        }
        return ret;
    }

    /**
     * Function for copy a file from assets
     *
     * @param filename
     * @param outPath
     */
    public static void copyFileFromAssets(Context context, String filename, String outPath) {
        AssetManager assetManager = context.getAssets();

        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(filename);
            String newFileName = outPath + "/" + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Function for send a Ricetta to CN
     *
     * @param fileSelezionato
     */
    public static void InviaFileA_Cn(Context context, File fileSelezionato) {
        Intent intent = new Intent(context, Select_file_to_CN.class);
        intent.putExtra("File_path", fileSelezionato.getPath());
        intent.putExtra("operazione", "Saving....");
        context.startActivity(intent);
    }

    /**
     * Function for reduce the size of the log file
     *
     * @param File_in
     * @throws IOException
     */
    public static void DimezzaFileLog(File File_in) throws IOException {
        File tmp = new File(Environment.getExternalStorageDirectory() + "/JamData/tmp.txt");

        BufferedReader br = new BufferedReader(new FileReader(File_in.getAbsolutePath()));
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));

        br.readLine();

        long lines = 0;
        while (br.readLine() != null) lines++;
        br.close();

        br = new BufferedReader(new FileReader(File_in.getAbsolutePath()));

        long metà_righe = lines / 2;
        long righe_cnt = 0;

        for (long i = 0; i < lines; i++) {
            righe_cnt++;
            String linea = br.readLine();
            if (righe_cnt > metà_righe)
                bw.write(String.format("%s%n", linea));
        }

        br.close();
        bw.close();

        File oldFile = new File(File_in.getAbsolutePath());
        if (oldFile.delete())
            tmp.renameTo(oldFile);
    }

    /**
     * Function for copy files recursively
     *
     * @param sourceFile
     * @param destFile
     * @return
     * @throws IOException
     */
    public static Boolean copyFile(File sourceFile, File destFile) throws IOException {
        if (destFile.exists()) {
            destFile.delete();
        }

        if (!destFile.exists()) {

            if (sourceFile.isDirectory()) {
                destFile.mkdir();
                File f = new File(destFile.getPath());
                if (f != null) f.delete();

                f.mkdir();

                File[] allEntries = new File(sourceFile.getPath()).listFiles();

                for (File file : allEntries) {
                    if (file.isDirectory()) {
                        Copy(file, f);
                    } else {
                        FileChannel source = null;
                        FileChannel destination = null;
                        try {
                            source = new FileInputStream(file).getChannel();
                            File destFile1 = new File(f, file.getName());
                            destination = new FileOutputStream(destFile1).getChannel();
                            destination.transferFrom(source, 0, source.size());
                        } finally {
                            if (source != null)
                                source.close();
                            if (destination != null)
                                destination.close();
                        }
                    }
                }
            } else {
                destFile.createNewFile();
                FileChannel source = null;
                FileChannel destination = null;
                try {
                    source = new FileInputStream(sourceFile).getChannel();
                    destination = new FileOutputStream(destFile).getChannel();
                    destination.transferFrom(source, 0, source.size());
                } finally {
                    if (source != null)
                        source.close();
                    if (destination != null)
                        destination.close();
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Function for copy file
     *
     * @param sourceFile
     * @param destFile
     * @throws IOException
     */
    private static void Copy(File sourceFile, File destFile) throws IOException {
        File f = new File(destFile.getPath() + "/" + sourceFile.getName());
        if (f != null) f.delete();

        f.mkdir();

        File[] allEntries = new File(sourceFile.getPath()).listFiles();

        for (File file : allEntries) {
            if (file.isDirectory()) {
                Copy(file, f);
            } else {
                FileChannel source = null;
                FileChannel destination = null;
                try {
                    source = new FileInputStream(file).getChannel();
                    File destFile1 = new File(f, file.getName());
                    destination = new FileOutputStream(destFile1).getChannel();
                    destination.transferFrom(source, 0, source.size());
                } finally {
                    if (source != null)
                        source.close();
                    if (destination != null)
                        destination.close();
                }
            }
        }
    }

    /**
     * Function for get bytes from string
     *
     * @param path
     * @return
     */
    private static byte[] getByte(String path) {
        byte[] getBytes = {};
        try {
            File file = new File(path);
            getBytes = new byte[(int) file.length()];
            InputStream is = new FileInputStream(file);
            is.read(getBytes);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getBytes;
    }

    /**
     * Function for delete files recursive
     *
     * @param fileOrDirectory
     */
    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Function for open the Emergency page and clear the activities list
     *
     * @param context
     */
    public static void ClearActivitiesTopToEmergencyPage(Context context) {
        Intent intent = new Intent(context, Emergency_page.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static Boolean CheckPresenzaProgramma_xml(String fileXmlPathR) {
        File file = new File(fileXmlPathR);
        if(file.exists())
            return true;
        else return
            false;
    }

    /**
     * Function for create an empty Ricetta
     *
     * @param activity
     * @return
     */
    private File CreaProgCucituraVuoto(Activity activity) {
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/JamData");

        dir.mkdirs();
        File file = new File(dir, "file_empty.xml");
        File file1 = new File(dir, "file_empty.usr");

        if (!file.exists() && !file1.exists()) {
            Ricetta r = new Ricetta(Values.plcType);
            r.setDrawPosition(new PointF(0.1f, 0.1f));
            r.drawFeedTo(new PointF(10f, 10f));
            r.drawFeedTo(new PointF(0.1f, 0.1f));
            try {
                r.save(file);
                try {
                    r.exportToUsr(file1);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "error Usr export ", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, "error saving xml file ", Toast.LENGTH_SHORT).show();
            }
        }
        return file;
    }
    public static String[] logHeap() {
        Double allocated = new Double(android.os.Debug.getNativeHeapAllocatedSize())/new Double((1048576));
        Double available = new Double(android.os.Debug.getNativeHeapSize())/1048576.0;
        Double free = new Double(Debug.getNativeHeapFreeSize())/1048576.0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        String[] ret = new String[2];
        ret[0] = "debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free)";
        ret[1] = "debug.memory: allocated: " + df.format(new Double(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(new Double(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(new Double(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)";


        return ret;


        //  Log.d("tag", "debug. =================================");
        //   Log.d("tag", "debug.heap native: allocated " + df.format(allocated) + "MB of " + df.format(available) + "MB (" + df.format(free) + "MB free)");
        //  Log.d("tag", "debug.memory: allocated: " + df.format(new Double(Runtime.getRuntime().totalMemory()/1048576)) + "MB of " + df.format(new Double(Runtime.getRuntime().maxMemory()/1048576))+ "MB (" + df.format(new Double(Runtime.getRuntime().freeMemory()/1048576)) +"MB free)");
    }
    /**
     * Class for handle crash and save them on a file
     */
    public static class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

        private final Thread.UncaughtExceptionHandler defaultUEH;

        private final String localPath;

        private final String url;

        /**
         * If any of the parameters is null, the respective functionality
         * will not be used
         */
        public CustomExceptionHandler(String localPath, String url) {
            this.localPath = localPath;
            this.url = url;
            this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        }

        public void uncaughtException(Thread t, Throwable e) {
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            e.printStackTrace(printWriter);
            String stacktrace = result.toString();
            printWriter.close();

            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
            String timestamp = dateFormat.format(date);
            String timestamp1 = timestamp.replace(":", "-");

            String filename = timestamp1 + ".deb";

            if (localPath != null) {
                writeToFile(stacktrace, filename);
            }

            defaultUEH.uncaughtException(t, e);
        }

        private void writeToFile(String stacktrace, String filename) {
            try {
                BufferedWriter bos = new BufferedWriter(new FileWriter(localPath + "/" + filename));
                bos.write(stacktrace);
                bos.flush();
                bos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

