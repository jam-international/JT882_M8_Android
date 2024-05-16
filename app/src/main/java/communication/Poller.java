package communication;

import java.util.List;

public class Poller extends Thread 
{


	public boolean running;
	
	public double QuoteAx1;
	public double QuoteAx2;
	public List<Integer> Alarm;
	
	private final ShoppingList Shopper;

	public void setRunning(boolean running) 
	{
		this.running = running;
	}

	public Poller(ShoppingList shopper) 
	{
		super();
		
		Shopper = shopper;
		
	}

	@Override
	public void run() 
	{
		while (running)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) { 
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


				if (!Shopper.IsConnected())
				{
					Shopper.ClearCued();
					Shopper.Connect();
				}
				else
				{
					Shopper.WriteQueued();

					Shopper.Read();	
				}
			

		}
		running = false;
	}
}