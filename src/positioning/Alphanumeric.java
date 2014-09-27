package positioning;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Defines the alphanumeric grid reference system.
 * 
 * @author sean
 */
public class Alphanumeric extends AbstractGridReference implements ActionListener
{
	/*
	 * Fields
	 */
	
	ArrayList<String> prefixes;
	
	private String prefix;
	private int alphanumericEasting;
	private int alphanumericNorthing;
	
	/*
	 * GUI edit controls
	 */
	
	JLabel pointPrefixLabel = new JLabel("Prefix");
	JLabel pointEastingLabel = new JLabel("Easting");
	JLabel pointNorthingLabel = new JLabel("Northing");
	JComboBox pointPrefixComboBox = new JComboBox();
	JTextField pointEastingTextField = new JTextField(5);
	JTextField pointNorthingTextField = new JTextField(5);
	
	public Alphanumeric(String prefix, int alphanumericEasting, int alphanumericNorthing)
	{
		super("Alphanumeric");
		
		this.prefix = prefix;
		this.alphanumericEasting = alphanumericEasting;
		this.alphanumericNorthing = alphanumericNorthing;
		
		// Set numeric coordinates
		setEasting(alphanumericEasting);
		setNorthing(alphanumericNorthing);
		
		// Load prefixes from file
		initialisePrefixes();
	}

	public Alphanumeric(AbstractGridReference abstractCoordinateSystem)
	{
		this();
		
		// Load prefixes from file
		initialisePrefixes();
		
		setName(abstractCoordinateSystem.getName());
		
		// The letter prefix doesn't need to be known to work out the alphanumeric 
		// easting and northing so we can do that here
		alphanumericEasting = abstractCoordinateSystem.getEasting() % 100000;
		alphanumericNorthing = abstractCoordinateSystem.getNorthing() % 100000;

		// Extract the numeric prefixes from the all-numeric easting and northing
		// This exploits the fact that integer division rounds towards zero in Java
		int eastingPrefix = abstractCoordinateSystem.getEasting() / 100000;
		int northingPrefix = abstractCoordinateSystem.getNorthing() / 100000;

		String eastingPrefixString = Integer.toString(eastingPrefix);
		String northingPrefixString = Integer.toString(northingPrefix);

		try
		{
			/*
			 * Open the prefixes.dat file which stores each OS letter prefix
			 * along with the appropriate all-numeric prefixes
			 */
			BufferedReader prefixDatBuffer = new BufferedReader(new FileReader("prefixes.dat"));
			String prefixDatRead;

			/*
			 * Test each line of the file to see which letter prefix matches
			 * ours and then extract the appropriate all-numeric prefixes
			 */

			boolean foundPrefix = false;

			while(((prefixDatRead = prefixDatBuffer.readLine()) != null) & !(foundPrefix))
			{

				/*
				 * Read one row from the prefix.dat file, storing the letter
				 * prefix, easting prefix and northing prefix in an array
				 */
				String prefixArray[] = prefixDatRead.split("\t");
				String testLetterPrefix = prefixArray[0];
				String testEastingPrefix = prefixArray[1];
				String testNorthingPrefix = prefixArray[2];

				/*
				 * If the numeric prefixes match, generate the alphanumeric
				 * references using the data corresponding to these prefixes
				 */
				if((testEastingPrefix.equals(eastingPrefixString)) & (testNorthingPrefix.equals(northingPrefixString)))
				{
					foundPrefix = true;
					
					setPrefix(testLetterPrefix);
				}
			}
			
			if(!foundPrefix)
			{
				/*
				 * This would be the place to throw an exception for an
				 * invalid grid reference since, if the program ends up
				 * here, it has escaped the while loop without finding a
				 * match.
				 */
			}
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public Alphanumeric(Alphanumeric alphanumeric)
	{
		this(alphanumeric.getPrefix(), alphanumeric.getAlphanumericEasting(), alphanumeric.getAlphanumericNorthing());
		
		setName(alphanumeric.getName());
	}
	
	public Alphanumeric()
	{
		this("NN", 167, 712);
	}
	
	private void initialisePrefixes()
	{
		if(prefixes == null)
		{
			prefixes = new ArrayList<String>();
			
			try
			{
				BufferedReader prefixDatBuffer = new BufferedReader(new FileReader("prefixes.dat"));
				String prefixDatRead;
			
				while((prefixDatRead = prefixDatBuffer.readLine()) != null)
				{
					String prefix = prefixDatRead.split("\t")[0];
					
					prefixes.add(prefix);
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public JPanel getEditControls()
	{
		JPanel editPanel = new JPanel();
		
		pointPrefixComboBox.setModel(new DefaultComboBoxModel(prefixes.toArray()));
		pointPrefixComboBox.setSelectedItem(prefix);
		
		pointPrefixComboBox.setActionCommand("prefix selected");
		pointPrefixComboBox.addActionListener(this);
		
		// Set easting and northing
		pointEastingTextField.setText(Integer.toString(alphanumericEasting));
		pointNorthingTextField.setText(Integer.toString(alphanumericNorthing));
		
		editPanel.add(pointPrefixLabel);
		editPanel.add(pointPrefixComboBox);
		editPanel.add(pointEastingLabel);
		editPanel.add(pointEastingTextField);
		editPanel.add(pointNorthingLabel);
		editPanel.add(pointNorthingTextField);
		
		return editPanel;
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		String command = event.getActionCommand();
		
		if(command.equals("prefix selected"))
		{
			prefix = (String) pointPrefixComboBox.getSelectedItem();
		}
	}
	
	@Override
	public void setCoordinates()
	{
		setPrefix((String) pointPrefixComboBox.getSelectedItem());
		setAlphanumericEasting(Integer.parseInt(pointEastingTextField.getText()));
		setAlphanumericNorthing(Integer.parseInt(pointNorthingTextField.getText()));
	}

	@Override
	protected void setEasting(int alphanumericEasting)
	{
		try
		{
			/*
			 * Open the prefixes.dat file which stores each OS letter prefix
			 * along with the appropriate all-numeric prefixes
			 */
			BufferedReader PrefixDatBuffer = new BufferedReader(new FileReader("prefixes.dat"));
			String PrefixDatRead;

			/*
			 * Test each line of the file to see which letter prefix matches
			 * ours and then extract the appropriate all-numeric prefixes
			 */
			boolean foundPrefix = false;

			while(((PrefixDatRead = PrefixDatBuffer.readLine()) != null) & !(foundPrefix))
			{

				/*
				 * Read one row from the prefix.dat file, storing the letter
				 * prefix, easting prefix and northing prefix in an array
				 */
				String prefixArray[] = PrefixDatRead.split("\t");
				String testLetterPrefix = prefixArray[0];

				/*
				 * If the letter prefixes match, generate the all-numeric
				 * references using the data corresponding to this prefix
				 */
				if(testLetterPrefix.equals(prefix))
				{
					foundPrefix = true;

					String allNumericEastingPrefix = prefixArray[1];

					this.easting = alphanumericEasting + 100000 * Integer.parseInt(allNumericEastingPrefix);
				}
			}
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	protected void setNorthing(int alphanumericNorthing)
	{
		try
		{
			/*
			 * Open the prefixes.dat file which stores each OS letter prefix
			 * along with the appropriate all-numeric prefixes
			 */
			BufferedReader PrefixDatBuffer = new BufferedReader(new FileReader("prefixes.dat"));
			String PrefixDatRead;

			/*
			 * Test each line of the file to see which letter prefix matches
			 * ours and then extract the appropriate all-numeric prefixes
			 */
			boolean foundPrefix = false;

			while(((PrefixDatRead = PrefixDatBuffer.readLine()) != null) & !(foundPrefix))
			{

				/*
				 * Read one row from the prefix.dat file, storing the letter
				 * prefix, easting prefix and northing prefix in an array
				 */
				String prefixArray[] = PrefixDatRead.split("\t");
				String testLetterPrefix = prefixArray[0];

				/*
				 * If the letter prefixes match, generate the all-numeric
				 * references using the data corresponding to this prefix
				 */
				if(testLetterPrefix.equals(prefix))
				{
					foundPrefix = true;

					String allNumericNorthingPrefix = prefixArray[2];

					this.northing = alphanumericNorthing + 100000 * Integer.parseInt(allNumericNorthingPrefix);
				}
			}
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		initialisePrefixes();
	}

	public String getPrefix()
	{
		return prefix;
	}

	protected void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public int getAlphanumericEasting()
	{
		return alphanumericEasting;
	}

	protected void setAlphanumericEasting(int alphanumericEasting)
	{
		this.alphanumericEasting = alphanumericEasting;
		
		setEasting(alphanumericEasting);
	}

	public int getAlphanumericNorthing()
	{
		return alphanumericNorthing;
	}

	protected void setAlphanumericNorthing(int alphanumericNorthing)
	{
		this.alphanumericNorthing = alphanumericNorthing;
		
		setNorthing(alphanumericNorthing);
	}
}
