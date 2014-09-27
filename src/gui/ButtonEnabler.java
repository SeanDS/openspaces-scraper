package gui;

/**
 * Interface to define an object which is capable of disabling GUI buttons during e.g. a 
 * threaded event
 * 
 * @author sean
 */
public interface ButtonEnabler
{
	public void enableButtons();
	
	public void disableButtons();
}
