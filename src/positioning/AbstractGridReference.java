package positioning;

import javax.swing.JPanel;

/**
 * Grid reference abstract class. This defines the fields and methods to be used by any
 * child classes which implement a grid reference system.
 * 
 * @author sean
 */
public abstract class AbstractGridReference
{
	/*
	 * Fields
	 */
	
	// User-friendly name
	//
	// This particular name is never actually displayed, as only non-abstract children
	// of this class are instantiated.
	String name = "Abstract Grid Reference";
	
	// All-numeric system variables
	protected int easting;
	protected int northing;
	
	/**
	 * 
	 * @param easting
	 * @param northing
	 */
	public AbstractGridReference(String name, int easting, int northing)
	{
		this.name = name;
		this.easting = easting;
		this.northing = northing;
	}
	
	public AbstractGridReference(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public abstract JPanel getEditControls();
	
	public abstract void setCoordinates();
	
	public String toString()
	{
		return name;
	}

	public int getEasting()
	{
		return easting;
	}
	
	// This is protected as we want all eastings set via the edit panel implemented in child classes only
	protected abstract void setEasting(int easting);

	public int getNorthing()
	{
		return northing;
	}
	
	// This is protected as we want all northings set via the edit panel implemented in child classes only
	protected abstract void setNorthing(int northing);
}
