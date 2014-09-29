package gui;

/**
 * Interface to define an object which is capable of interacting with the GUI during scraping.
 * 
 * @author sean
 */
public interface ScrapeGUIInteractor
{
	public void enableButtons();
	
	public void disableButtons();
	
	public void messageUnableToConnect();
}
