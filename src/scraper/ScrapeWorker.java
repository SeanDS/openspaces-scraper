package scraper;

import gui.ButtonEnabler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import positioning.AbstractGridReference;
import positioning.Numeric;

public class ScrapeWorker implements Runnable
{
	private File imageFile;

	private AbstractGridReference targetReference;

	private String key;
	private int xTiles;
	private int yTiles;
	private int layers;
	private int width;
	private int height;
	private File archiveDirectory;
	private BufferedImage image = null;
	private ButtonEnabler callback;

	// This defines whether the worker is stopped or not, to allow the GUI stop button to stop
	// the operation mid way through
	private boolean isStopped = false;
	
	public ScrapeWorker(String key, File imageFile, File archiveDirectory, AbstractGridReference targetReference, int xTiles,
			int yTiles, int layers, int width, int height, ButtonEnabler callback)
	{
		this.key = key;
		this.imageFile = imageFile;
		this.targetReference = targetReference;
		this.archiveDirectory = archiveDirectory;
		this.xTiles = xTiles;
		this.yTiles = yTiles;
		this.layers = layers;
		this.width = width;
		this.height = height;
		this.callback = callback; // this is the object to perform a callback on when scraping is finished
	}

	@Override
	public void run()
	{
		callback.disableButtons();
		
		int scrapeCount = 0;
		int readCount = 0;
		
		// Check that we are still to proceed
		if(!isStopped)
		{
			int imageSizeX = width * xTiles;
			int imageSizeY = height * yTiles;
	
			// imageFile will be null if the user wishes not to make a stitched image
			if(imageFile != null)
			{
				try
				{
					// TODO: Check for memory availibility here, or else a fatal error occurs
					
					// Print some memory facts
					Runtime runtime = Runtime.getRuntime();
				    System.out.println("Maximum memory available: " + runtime.maxMemory() / 1048576
				    		+ "MB\nImage will require roughly "
				    		+ imageSizeX * imageSizeY * 6 / 1048576 // this is 3 bytes per pixel (RGB) * 2 (BufferedImage seems to use enough space for two images)
				    		+ "MB to produce.");
					
					// Create a blank image on which to later build the map
					image = new BufferedImage(imageSizeX, imageSizeY, BufferedImage.TYPE_INT_RGB);
				}
				catch(OutOfMemoryError e)
				{
					System.err.println("There was not enough memory available to this program to create the map you specified.\nPlease choose a smaller set of tiles.");

					// Program halts execution here regardless due to the error
				}
			}
	
			// Create image to hold individual tiles temporarily before they're
			// added to the main image
			BufferedImage tile = null;
	
			int totalNumberOfImages = xTiles * yTiles;
			
			// Loop through tiles adding them to main image
			// n1 = x-dir index
			// n2 = y-dir index
			for(int n1 = 0; n1 < xTiles; n1++)
			{				
				for(int n2 = 0; n2 < yTiles; n2++)
				{
					if(!isStopped)
					{
						// Set the target grid reference for this tile
						Numeric tileTargetReference = new Numeric(targetReference.getEasting() + n1 * layers
								* width, targetReference.getNorthing() + n2 * layers * height);
		
						int currentImageNumber = n1 * yTiles + n2 + 1;
		
						// Check if the desired tile is already in the archives
						// If not, fetch it and save it
						// If so, read from the archived file
						File tileFile = new File(archiveDirectory.getAbsoluteFile() + File.separator + layers + File.separator + tileTargetReference.getEasting() + " "
								+ tileTargetReference.getNorthing() + ".png");
		
						boolean archiveAvailable = tileFile.exists();
		
						if(!archiveAvailable)
						{
							// Scrape the image
							System.out.println("Scraping and archiving image " + currentImageNumber + " of "
									+ totalNumberOfImages + "...");
							tile = scrapeImage(tileTargetReference, key);
							
							File layerFolder = new File(archiveDirectory.getAbsoluteFile() + File.separator + layers);
							
							// Check if layer folder exists, and if not, create it
							if(!layerFolder.exists())
							{
								// Create layer folder
								layerFolder.mkdir();
							}
							
							// Save the image to the archives
							try
							{
								ImageIO.write(tile, "png", tileFile);
								scrapeCount++;
								//Thread.sleep(1000);
							}
							catch(IOException e)
							{
								e.printStackTrace();
							} /*catch (InterruptedException e) {
								e.printStackTrace();
								}*/
						}
						// Else retrieve the tile from the archives, as long as we actually want to make an image
						// (imageFile will be null if the user wishes not to make a stitched image)
						else if(imageFile != null)
						{
							try
							{
								// Retrieve the tile from the archives
								System.out.println("Reading image " + currentImageNumber + " of " + totalNumberOfImages
										+ " from the archive...");
								tile = ImageIO.read(tileFile);
								readCount++;
							}
							catch(IOException e)
							{
								e.printStackTrace();
							}
						}
						
						System.out.println("	Done");
		
						// Check if image file is null, i.e. whether user wishes to save a stitched image
						if(imageFile != null)
						{
							// Add the tile to the overall image
							image.createGraphics().drawImage(tile, n1 * width, imageSizeY - (n2 + 1) * height, null);
						}
					}
				}
			}
			
			if(!isStopped)
			{
				// Check if image file is null, i.e. whether user wishes to save a stitched image
				if(imageFile != null)
				{
					System.out.println();
			
					try
					{
						System.out.println("Saving...");
			
						// Save the image to disk
						ImageIO.write(image, "png", imageFile);
			
						System.out.println("Image saved successfully to \"" + imageFile.getAbsolutePath() + "\".");
					}
					catch(IOException e)
					{
						System.out.println("Cannot save image file to disk");
						e.printStackTrace();
					}
				}
			}
		}
		
		if(isStopped)
		{
			System.out.println("Operation stopped.");
		}
		
		System.out.println(scrapeCount + " new tiles were archived.");
		
		callback.enableButtons();
	}

	private BufferedImage scrapeImage(AbstractGridReference target, String key)
	{
		BufferedImage scrapedImage = null;
		
		URL url;
		URLConnection connection;
		
		try
		{
			// Construct URL
			url = new URL(
					"http://openspace.ordnancesurvey.co.uk/osmapapi/ts?FORMAT=image%2Fpng&KEY="
							+ key
							+ "&URL=http%3A%2F%2Fosopenspacewiki.ordnancesurvey.co.uk%2Fwiki%2Findex.php%3Ftitle%3DBasic_Map&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&EXCEPTIONS=application%2Fvnd.ogc.se_inimage&LAYERS="
							+ layers
							+ "&SRS=EPSG%3A27700&BBOX="
							+ target.getEasting()
							+ ","
							+ target.getNorthing()
							+ ","
							+ target.getEasting()
							+ layers * width
							+ ","
							+ target.getNorthing()
							+ layers * height
							+ "&WIDTH=" + width + "&HEIGHT=" + height);
			
			connection = url.openConnection();
			
			/*
			 * Uncomment the following if in the future the OS website requires a cookie
			 * to be sent with the HTTP request for the image. This could be a method
			 * implemented by Ordnance Survey to prevent harvesting of their images.
			 */
			// connection.setRequestProperty("Cookie", "BIGipServerprod_as101202_http_pool=2069932224.24862.0000");
			
			// This referer request is not strictly required, but it attempts to avoid
			// suspicion from Ordnance Survey as to the nature of the HTTP requests
			// this program makes.
			connection.setRequestProperty("Referer", "http://osopenspacewiki.ordnancesurvey.co.uk/wiki/index.php?title=Basic_Map");
			
			// Get image
			scrapedImage = ImageIO.read(connection.getInputStream());
		}
		catch(IOException e)
		{
			// Dump details to the console
			System.err.println("A connection to the Ordnance Survey website was unable to be made.");
			e.printStackTrace();
			System.err.println("Exiting program");
			System.exit(0);
		}
		
		return scrapedImage;
	}

	public BufferedImage getImage()
	{
		return image;
	}
	
	public void setStopped(boolean stopped)
	{
		this.isStopped = stopped;
	}
}