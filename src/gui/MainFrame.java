package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import positioning.AbstractGridReference;
import positioning.Alphanumeric;
import positioning.Numeric;
import scraper.ScrapeWorker;

/**
 * Scraper GUI
 * 
 * @author sean
 */
public class MainFrame extends JFrame implements ActionListener, ItemListener, ButtonEnabler
{
	private static final long serialVersionUID = 1L;
	
	/*
	 * GUI edit controls
	 */
	
	// Layout manager
	GroupLayout layout = new GroupLayout(getContentPane());
	ParallelGroup horizontalGroup = layout.createParallelGroup();
	SequentialGroup verticalGroup = layout.createSequentialGroup();

	// API key
	JLabel keyLabel = new JLabel("API key");
	JTextField keyTextField = new JTextField(20);

	// Grid reference combo box
	ArrayList<AbstractGridReference> gridReferences = new ArrayList<AbstractGridReference>();
	AbstractGridReference selectedGridReference;
	JLabel gridReferenceComboBoxLabel = new JLabel("Coordinate System");
	JComboBox gridReferenceComboBox = new JComboBox();
	
	// Grid reference edit panel
	JPanel gridReferenceEditPanel = new JPanel();
	
	// Edit controls for number of tiles about specified point
	JLabel tileNLabel = new JLabel("North");
	JLabel tileSLabel = new JLabel("South");
	JLabel tileELabel = new JLabel("East");
	JLabel tileWLabel = new JLabel("West");
	JTextField tileNTextField = new JTextField(2);
	JTextField tileSTextField = new JTextField(2);
	JTextField tileETextField = new JTextField(2);
	JTextField tileWTextField = new JTextField(2);

	// Layers
	JLabel layersLabel = new JLabel("Layers");
	String layersComboBoxOptions[] = {"1000", "500", "200", "100", "50", "25", "10", "5", "2", "1"};
	JComboBox layersComboBox = new JComboBox(layersComboBoxOptions);
	
	// Tile details edit panel
	JPanel tileDetailsEditPanel = new JPanel();

	// Save location
	JTextField saveTextField = new JTextField(20);
	JFileChooser saveFolderChooser = new JFileChooser();
	JButton saveFolderBrowseButton = new JButton("Browse...");
	JCheckBox saveCheckBox = new JCheckBox("Save to", true);

	// Archive directory
	JLabel archiveLabel = new JLabel("Archive directory");
	JTextField archiveTextField = new JTextField(15);
	JFileChooser archiveFolderChooser = new JFileChooser();
	JButton archiveFolderBrowseButton = new JButton("Browse...");

	// Create and stop buttons
	JButton createButton = new JButton("Create");
	JButton stopButton = new JButton("Stop");
	JButton aboutButton = new JButton("About");

	/*
	 * Other
	 */
	
	// The object which gets images as an independent thread. This is defined here so that
	// the stop button can access it (in order to halt its operation) while it is in action.
	ScrapeWorker scrapeWorker;
	
	/**
	 * Builds GUI into a JFrame object
	 * 
	 * @return JFrame object
	 */
	public MainFrame()
	{
		// Set program to exit when closed
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Set frame size
		// This is unnecessary as pack() is called later
		//setSize(new Dimension(600, 400));
		
		// Set title
		this.setTitle("OpenSpaces Scraper");
		
		/*
		 * Set up layout manager
		 */
		
		layout.setHorizontalGroup(horizontalGroup);
		layout.setVerticalGroup(verticalGroup);
		
		layout.setAutoCreateGaps(true);
		
		setLayout(layout);

		/*
		 * API key edit controls
		 */

		//keyTextField.setText("6694613F8B469C97E0405F0AF160360A");

		horizontalGroup.addGroup(
			layout.createSequentialGroup()
				.addComponent(keyLabel)
				.addComponent(keyTextField)
			);
				
		verticalGroup.addGroup(
			layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(keyLabel)
				.addComponent(keyTextField)
			);
		
		/*
		 * Coordinate system edit controls
		 */
		
		initialiseGridReferences();
		
		gridReferenceComboBox.addActionListener(this);
		gridReferenceComboBox.setActionCommand("grid reference system selected");
		
		horizontalGroup.addGroup(
				layout.createSequentialGroup()
					.addComponent(gridReferenceComboBoxLabel)
					.addComponent(gridReferenceComboBox)
				);
					
		verticalGroup.addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(gridReferenceComboBoxLabel)
					.addComponent(gridReferenceComboBox)
				);
		
		// Set border of edit panel
		gridReferenceEditPanel.setBorder(BorderFactory.createTitledBorder("Grid Reference"));
		
		horizontalGroup.addGroup(
				layout.createSequentialGroup()
					.addComponent(gridReferenceEditPanel)
				);
					
		verticalGroup.addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(gridReferenceEditPanel)
				);

		/*
		 * Tile and layer edit controls
		 */
		
		tileDetailsEditPanel.setBorder(BorderFactory.createTitledBorder("Tiles and Layers"));
		
		horizontalGroup.addGroup(
				layout.createSequentialGroup()
					.addComponent(tileDetailsEditPanel)
				);
					
		verticalGroup.addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(tileDetailsEditPanel)
				);

		// Tile and layer edit controls layout
		GroupLayout tileDetailsLayout = new GroupLayout(tileDetailsEditPanel);
		ParallelGroup tileDetailsHorizontalGroup = tileDetailsLayout.createParallelGroup();
		SequentialGroup tileDetailsVerticalGroup = tileDetailsLayout.createSequentialGroup();
		
		tileDetailsLayout.setHorizontalGroup(tileDetailsHorizontalGroup);
		tileDetailsLayout.setVerticalGroup(tileDetailsVerticalGroup);
		
		tileDetailsLayout.setAutoCreateGaps(true);
		
		tileDetailsEditPanel.setLayout(tileDetailsLayout);
		
		tileNTextField.setText("2");
		tileSTextField.setText("2");
		tileETextField.setText("2");
		tileWTextField.setText("2");
		
		tileDetailsHorizontalGroup.addGroup(
				tileDetailsLayout.createParallelGroup()
					.addGroup(
						tileDetailsLayout.createSequentialGroup()
						.addComponent(tileNLabel)
						.addComponent(tileNTextField)
						.addComponent(tileSLabel)
						.addComponent(tileSTextField)
						.addComponent(tileELabel)
						.addComponent(tileETextField)
						.addComponent(tileWLabel)
						.addComponent(tileWTextField)
						)
					.addGroup(
						tileDetailsLayout.createSequentialGroup()
						.addComponent(layersLabel)
						.addComponent(layersComboBox)
						)
				);
					
		tileDetailsVerticalGroup.addGroup(
				tileDetailsLayout.createSequentialGroup()
					.addGroup(
						tileDetailsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(tileNLabel)
						.addComponent(tileNTextField)
						.addComponent(tileSLabel)
						.addComponent(tileSTextField)
						.addComponent(tileELabel)
						.addComponent(tileETextField)
						.addComponent(tileWLabel)
						.addComponent(tileWTextField)
						)
					.addGroup(
						tileDetailsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(layersLabel)
						.addComponent(layersComboBox)
					)
				);

		layersComboBox.setSelectedIndex(7);

		/*
		 * Archive directory controls
		 */

		archiveTextField.setText(System.getProperty("user.dir") + File.separator + "Archive (1 to 50000)");

		archiveFolderBrowseButton.addActionListener(this);
		
		horizontalGroup.addGroup(
				layout.createSequentialGroup()
					.addComponent(archiveLabel)
					.addComponent(archiveTextField)
					.addComponent(archiveFolderBrowseButton)
				);
					
		verticalGroup.addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(archiveLabel)
					.addComponent(archiveTextField)
					.addComponent(archiveFolderBrowseButton)
				);
		
		/*
		 * Save edit controls
		 */

		saveTextField.setText(System.getProperty("user.dir"));

		saveFolderBrowseButton.addActionListener(this);
		saveCheckBox.addItemListener(this);
		
		// Check box tooltip
		saveCheckBox.setToolTipText("Produce and save a stitched image of the selected tiles");
		
		horizontalGroup.addGroup(
				layout.createSequentialGroup()
					.addComponent(saveCheckBox)
					.addComponent(saveTextField)
					.addComponent(saveFolderBrowseButton)
				);
					
		verticalGroup.addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(saveCheckBox)
					.addComponent(saveTextField)
					.addComponent(saveFolderBrowseButton)
				);

		/*
		 * Button
		 */

		createButton.addActionListener(this);
		stopButton.addActionListener(this);
		aboutButton.addActionListener(this);
		
		stopButton.setEnabled(false);

		horizontalGroup.addGroup(
				layout.createSequentialGroup()
					.addComponent(createButton)
					.addComponent(stopButton)
					.addComponent(aboutButton)
				);
					
		verticalGroup.addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(createButton)
					.addComponent(stopButton)
					.addComponent(aboutButton)
				);
		
		// Pack
		pack();
	}
	
	/**
	 * Creates the grid reference combo box and edit panel
	 */
	private void initialiseGridReferences()
	{
		// Add the grid reference systems
		gridReferences.add(new Numeric());
		gridReferences.add(new Alphanumeric());
		
		// Set selected grid reference system to be first in the list
		selectedGridReference = gridReferences.get(0);
		
		// Set the combo box model
		gridReferenceComboBox.setModel(new DefaultComboBoxModel(gridReferences.toArray()));
		
		// Set selected item
		gridReferenceComboBox.setSelectedItem(selectedGridReference);
		
		// Create the edit panel
		gridReferenceEditPanel.add(selectedGridReference.getEditControls());
	}

	/**
	 * Listens for click events
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		String command = e.getActionCommand();

		if(e.getSource() == createButton)
		{
			// Scrape and stitch images
			getImages();
		}
		else if(e.getSource() == stopButton)
		{			
			// Stop scraping
			scrapeWorker.setStopped(true);
			
			// Disable button
			stopButton.setEnabled(false);
			
			// NOTE: Stop button is re-enabled elsewhere
		}
		else if(e.getSource() == aboutButton)
		{			
			// Show about box
			JOptionPane.showMessageDialog(this, 
					"<html><p>&#169; Sean Leavey and Ewan Gunn</p></html>\n" +
					"https://github.com/SeanDS/\n" +
					"\n" +
					"Please use your own API key. Use your own key, and don't overuse the access provided freely by Ordnance Survey.\n" +
					"\n" +
					"Licenced under the GPLv2 licence.",
					"About",
					JOptionPane.INFORMATION_MESSAGE
			);
			
		}
		else if(command.equals("grid reference system selected"))
		{
			selectedGridReference = (AbstractGridReference) gridReferenceComboBox.getSelectedItem();
			
			// Clear edit panel
			gridReferenceEditPanel.removeAll();
			
			// Add new edit panel
			gridReferenceEditPanel.add(selectedGridReference.getEditControls());
			
			// Repaint
			validate();
			repaint();
		}
		else if(e.getSource() == saveFolderBrowseButton)
		{
			// Set file chooser to show only directories
			saveFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			// Set the chooser to the current save folder setting
			saveFolderChooser.setCurrentDirectory(new File(saveTextField.getText()));
			
			int returnValue = saveFolderChooser.showDialog(this, "Select");
			
			if(returnValue == JFileChooser.APPROVE_OPTION)
			{
				// Update text field
				saveTextField.setText(saveFolderChooser.getSelectedFile().getAbsolutePath());
			}
		}
		else if(e.getSource() == archiveFolderBrowseButton)
		{
			// Set file chooser to show only directories
			archiveFolderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			// Set the chooser to the current archive folder setting
			archiveFolderChooser.setCurrentDirectory(new File(archiveTextField.getText()));
			
			int returnValue = archiveFolderChooser.showDialog(this, "Select");
			
			if(returnValue == JFileChooser.APPROVE_OPTION)
			{
				// Update text field
				archiveTextField.setText(archiveFolderChooser.getSelectedFile().getAbsolutePath());
			}
		}
	}

	private void getImages()
	{
		// Get API key
		String key = keyTextField.getText();
		
		// Set coordinates from edit panel
		selectedGridReference.setCoordinates();
		
		File saveFolder = new File(saveTextField.getText());
		File archiveFolder = new File(archiveTextField.getText());
		
		// Check if save folder exists, and if not, prompt the user
		if(!saveFolder.exists())
		{
			String[] options = {"Yes", "No"};
			
			int option = JOptionPane.showOptionDialog(this, "Before continuing, the folder:\n\"" + saveFolder.getAbsolutePath() + "\"\nmust be created. Do you wish to continue?", "Save folder does not exist", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			
			if(option == JOptionPane.YES_OPTION)
			{
				saveFolder.mkdir();
			}
			else
			{
				return;
			}
		}
		
		// Check if archive folder exists, and if not, prompt the user
		if(!archiveFolder.exists())
		{
			String[] options = {"Yes", "No"};
			
			int option = JOptionPane.showOptionDialog(this, "Before continuing, the folder:\n\"" + archiveFolder.getAbsolutePath() + "\"\nmust be created. Do you wish to continue?", "Archive folder does not exist", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			
			if(option == JOptionPane.YES_OPTION)
			{
				archiveFolder.mkdir();
			}
			else
			{
				return;
			}
		}
		
		int numberOfTilesN = Integer.parseInt(tileNTextField.getText());
		int numberOfTilesS = Integer.parseInt(tileSTextField.getText());
		int numberOfTilesE = Integer.parseInt(tileETextField.getText());
		int numberOfTilesW = Integer.parseInt(tileWTextField.getText());

		int layers = Integer.parseInt(layersComboBoxOptions[layersComboBox.getSelectedIndex()]);
		int width;
		int height;

		if((layers == 1) || (layers == 2))
		{
			width = 250;
			height = width;
		}
		else
		{
			width = 200;
			height = width;
		}
		
		System.out.println("Selected grid reference: " + selectedGridReference.getEasting() + ", " + selectedGridReference.getNorthing());
		
		// This skullduggery truncates the grid references to snap the tiles to the cell edges.
		// Although the OS website does this automatically, it is necessary for the archival
		// feature.
		Numeric cornerReference = new Numeric(
				((selectedGridReference.getEasting() - numberOfTilesW * 1000) / 1000) * 1000,
				((selectedGridReference.getNorthing() - numberOfTilesS * 1000) / 1000) * 1000);

		// Calculate the number of tiles required
		int numberOfTilesX = numberOfTilesE + numberOfTilesW + 1;
		int numberOfTilesY = numberOfTilesN + numberOfTilesS + 1;

		/*
		// Output the user's input grid reference
		System.out.println("Grid reference details: ");

		System.out.println("\tPrefix: " + inputReference.getAlphanumericPrefix());
		System.out.println("\tAlphanumeric Easting: " + inputReference.getAlphanumericEasting());
		System.out.println("\tAlphanumeric Northing: " + inputReference.getAlphanumericNorthing());
		System.out.println("\tEasting: " + inputReference.getEasting());
		System.out.println("\tNorthing: " + inputReference.getNorthing());
		System.out.println();
		*/

		Alphanumeric alphanumeric = new Alphanumeric(selectedGridReference);
		
		File imageFile = null;
		
		// If file is to be saved, set up a file
		if(saveCheckBox.isSelected())
		{
			imageFile = new File(saveFolder.getAbsoluteFile() + File.separator + alphanumeric.getPrefix() + " " + alphanumeric.getAlphanumericEasting() + " "
				+ alphanumeric.getAlphanumericNorthing() + " " + "(" + numberOfTilesX + " x " + numberOfTilesY + ", layer " + layers + ").png");
		}

		// Create ScrapeWorker thread object
		scrapeWorker = new ScrapeWorker(key, imageFile, archiveFolder, cornerReference, numberOfTilesX,
				numberOfTilesY, layers, width, height, this);
		
		// Define thread
		Thread thread = new Thread(scrapeWorker);

		// Start thread
		thread.start();
	}
	
	@Override
	public void enableButtons()
	{
		createButton.setEnabled(true);
		stopButton.setEnabled(false);
	}

	@Override
	public void disableButtons()
	{
		createButton.setEnabled(false);
		stopButton.setEnabled(true);
	}

	@Override
	public void itemStateChanged(ItemEvent event)
	{
		if(event.getSource() == saveCheckBox)
		{
			// Enable/disable text field and browse button based on check box status
			saveTextField.setEnabled(saveCheckBox.isSelected());
			saveFolderBrowseButton.setEnabled(saveCheckBox.isSelected());
		}
	}
}