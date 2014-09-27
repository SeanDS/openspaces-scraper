package positioning;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Defines the numeric grid reference system.
 * 
 * @author sean
 */
public class Numeric extends AbstractGridReference
{
	JPanel editPanel = new JPanel();
	
	JLabel eastingLabel = new JLabel("Easting");
	JLabel northingLabel = new JLabel("Northing");
	
	JTextField eastingTextField = new JTextField(7);
	JTextField northingTextField = new JTextField(7);
	
	JPanel eastingPanel = new JPanel();
	JPanel northingPanel = new JPanel();
	
	public Numeric(String name, int easting, int northing)
	{
		super(name, easting, northing);
	}
	
	public Numeric(int easting, int northing)
	{
		this("Numeric", easting, northing);
	}
	
	public Numeric()
	{
		this(216000, 771000);
	}
	
	@Override
	public JPanel getEditControls()
	{
		eastingTextField.setText(Integer.toString(easting));
		northingTextField.setText(Integer.toString(northing));
		
		eastingPanel.add(eastingLabel);
		eastingPanel.add(eastingTextField);
		
		northingPanel.add(northingLabel);
		northingPanel.add(northingTextField);
		
		editPanel.add(eastingPanel);
		editPanel.add(northingPanel);
		
		return editPanel;
	}

	@Override
	public void setCoordinates()
	{
		setEasting(Integer.parseInt(eastingTextField.getText()));
		setNorthing(Integer.parseInt(northingTextField.getText()));
	}

	@Override
	protected void setEasting(int easting)
	{
		this.easting = easting;
	}

	@Override
	protected void setNorthing(int northing)
	{
		this.northing = northing;
	}
}
