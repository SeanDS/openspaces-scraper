package program;

import gui.MainFrame;

/**
 * Program to assemble OS maps from individual map tiles retrieved from the
 * Ordnance Survey website
 * 
 * @author Sean
 */
public class Scraper
{
	/**
	 * Main program
	 * 
	 * @param args
	 *            Command line arguments (none supported)
	 */
	public static void main(String[] args)
	{
		try
		{
			// Create a new thread to run the GUI stuff in (in order to be
			// thread-safe)
			javax.swing.SwingUtilities.invokeAndWait(new Runnable()
			{
				@Override
				public void run()
				{
					// Start program
					MainFrame getImages = new MainFrame();

					System.out.println("Starting GUI...");

					try
					{
						getImages.setVisible(true);
						
						System.out.println("GUI loaded successfully.\n");
					}
					catch(Exception e)
					{
						System.out.println("GUI not loaded successfully.\n");
						
						e.printStackTrace();
					}
				}
			});
		}
		catch(Exception e)
		{
			System.out.println("Failed to start program in independent thread.\n");
			
			e.printStackTrace();
		}
	}
}