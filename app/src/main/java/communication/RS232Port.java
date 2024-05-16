package communication;

import android.os.SystemClock;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android_serialport_api.SerialPort;


public class RS232Port extends SerialPort
{
    private Double TimeOut = 0d;
    private File Device = null; 
    private Integer Handle = 0;

    public synchronized Integer getHandle()
    {
        return Handle;
    }

    public synchronized void setHandle(Integer handle)
    {
        Handle = handle;
    }

    public synchronized Double getTimeOut()
    {
        return TimeOut;
    }

    public synchronized void setTimeOut(Double timeOut)
    {
        TimeOut = timeOut;
    }

    public synchronized File getDevice()
    {
        return Device;
    }

    public synchronized void setDevice(File device)
    {
        Device = device;
    }

    public RS232Port(File device, int baudrate, Double timeout)
            throws SecurityException, IOException
    {
        super(device, baudrate, 0);
        
        TimeOut = timeout;
        Device = device;
    }

    public void Write(String buf)
    {
        Write(buf.getBytes());
    }

    public void Write(byte[] buf)
    {
        try
        {
            this.getOutputStream().write(buf);
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public byte[] ReadBytes(int toread)
    {
        InputStream s = this.getInputStream();
        long t = SystemClock.elapsedRealtime();
        long lapse = 0;
        try
        {
            while(true)
            {
                lapse =  SystemClock.elapsedRealtime() - t;
                if (lapse < 0)
                    t = SystemClock.elapsedRealtime();

                int ava = s.available();

                if (ava >= toread)
                {
                    byte[] b = new byte[toread];
                    s.read(b);
                    return b;
                }
                
                if (lapse > TimeOut)
                {
                    byte[] b = new byte[s.available()];
                    s.read(b);
                    
                    return b;
                }
            }
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return new byte[]{};
    }

    public String Read(int toread)
    {
        return new String(ReadBytes(toread));
    }
    
    public int Available()
    {
        try
        {
            return this.getInputStream().available();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }
    
    public void Flush()
    {
        InputStream s = this.getInputStream();
        try
        {
            s.skip(s.available());
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void Close()
    {
        close();
    }
}
