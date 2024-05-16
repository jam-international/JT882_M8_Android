package communication;

import android.content.Context;


import com.jam_int.jt882_m8.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;

public class VFKBook
{
    private static LinkedHashMap<String, VFKItem> VFK;

    public static synchronized LinkedHashMap<String, VFKItem> getVFK()
    {
        return VFK;
    }

    public static synchronized void setVFK(LinkedHashMap<String, VFKItem> vFK)
    {
        VFK = vFK;
    }

    public static void Load(Context context)
    {
        VFKItem v = null;
        
        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myParser = xmlFactoryObject.newPullParser();


            VFK = new LinkedHashMap<String, VFKItem>();

            InputStream fIn = context.getResources().openRawResource(R.raw.vfk);

            //FileInputStream fIn = new FileInputStream(myFile);

            myParser.setInput(fIn, null);

            String txt = null;

            int event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) 
            {
                String name = myParser.getName();
                switch (event){
                case XmlPullParser.START_TAG:
                    break;

                case XmlPullParser.TEXT:
                    txt = myParser.getText();
                    break;

                case XmlPullParser.END_TAG:
                    if(name.equals("Index"))
                    {
                        v = new VFKItem();
                        v.setIndex(Integer.parseInt(txt));
                    }
                    else if(name.equals("Description"))
                    {
                        v.setDescription(txt);
                    }
                    else if(name.equals("CanRead"))
                    {
                        v.setReadable(txt.equals("true"));
                    }
                    else if(name.equals("CanWrite"))
                    {
                        v.setWritable(txt.equals("true"));
                    }
                    else if(name.equals("DataType"))
                    {
                        txt = txt.split(",")[0];
                        txt = txt.split("\\.")[1];
                        
                        v.setDataType(txt);
                        VFK.put(v.getIndex().toString(), v);
                    }


                    break;
                }        

                event = myParser.next();                    
            }

            fIn.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
