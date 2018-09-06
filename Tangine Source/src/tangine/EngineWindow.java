package tangine;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultTreeModel;
import javax.imageio.ImageIO;
import java.awt.Font;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Window.Type;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class EngineWindow {
	static EngineWindow window;
	protected static JLabel picLabel;
	protected static JLabel currentFile;
	private JFrame frmTangine;
	private StoryMapTree storyMap;
	private int place;
	private String notes;

	/*
	 * Main method that launches a window and displays it.
	 * For future reference, EngineWindow and GameWindow are technically two separate programs.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					String lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
					UIManager.setLookAndFeel(lookAndFeel);
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					window = new EngineWindow();
					window.frmTangine.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//Constructors. Because it's JSwing, all it does is initialize.
	public EngineWindow() {
		initialize();
	}

	/*
	 * Creates, names, and sizes the JFrame and all components.
	 * Precondition: an EngineWindow has been created.
	 */
	private void initialize() {
		frmTangine = new JFrame();
		frmTangine.setTitle("Tangine");
		frmTangine.setBounds(100, 100, 1000, 555);
		frmTangine.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmTangine.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent we) { // Ask before exiting.
		        String ObjButtons[] = {"Yes","No"};
		        int PromptResult = JOptionPane.showOptionDialog(null,"Are you sure you want to exit Tangine?","Tangine",JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,ObjButtons,ObjButtons[1]);
		        if(PromptResult==JOptionPane.YES_OPTION) {
		            System.exit(0);
		        }
		    }
		});
		frmTangine.getContentPane().setLayout(null);

		// These are up here because every other part of the window needs them to exist in order to function properly.
		StoryMapTree storyMap = new StoryMapTree();
		JLabel imgPath = new JLabel("--No Image Inserted--");

		// This is the panel inside which the narration TextArea resides.
		JPanel narrationPanel = new JPanel();
		narrationPanel.setBounds(292, 34, 448, 381);
		frmTangine.getContentPane().add(narrationPanel);
		narrationPanel.setLayout(new GridLayout(0, 1, 0, 0));

		// Said text area.
		JTextArea narrationArea = new JTextArea();
		narrationArea.setFont(new Font("Dialog", Font.PLAIN, 14));
		narrationArea.setToolTipText("");
		narrationArea.setWrapStyleWord(true);
		narrationArea.setLineWrap(true);
		JScrollPane narrationScrollPane = new JScrollPane(narrationArea); //Put that in a scrollpane so people can write long stories.
		narrationArea.addFocusListener(new FocusAdapter() { //Give it a few listeners so that it can interact with the StoryMap.
			StoryNode focused;
			/*
			 * Find the focused story node to target it as the node to write to.
			 * @param e - The event of the narration area gaining focus required to run the code.
			 */
			@Override
			public void focusGained(FocusEvent e) {
				if (storyMap.getCurrentNode() != null)
					focused = storyMap.getCurrentNode();
			}
			/*
			 * Get the contents of the narration TextPanel. If the caption isn't null, and if there is a focused node,
			 * set that node's caption to the textArea contents and log the change.
			 * @param e - The event of the narration area losing focus required to run the code.
			 */
			public void focusLost(FocusEvent e) {
				String caption = narrationArea.getText();
				if (caption != null) {
					if (focused != null) {
						focused.setCaption(caption);
						System.out.println("storyNodeCaption should update...");
					}
				}
			}
		});
		narrationPanel.add(narrationScrollPane); // Finally, put it all together.


		JPanel mapPanel = new JPanel(); // Create the tree's panel. We created the tree earlier.
		mapPanel.setBounds(11, 34, 271, 382);
		frmTangine.getContentPane().add(mapPanel);
		mapPanel.setLayout(new GridLayout(0, 1, 0, 0));
		// Tree is initialized earlier because it and the narrationPanel are directly related.
		FocusAdapter treeFocusAdapter = new FocusAdapter() { // Create a focus listener.
			/*
			 * Log the fact that this method was called, then, if a node is highlighted,
			 * write the node's caption to the narrationArea and log the fact that it's done that.
			 * if there's not a node, let the log know.
			 * @param e - The event of the map panel gaining focus required to run the code.
			 */
			@Override
			public void focusGained(FocusEvent e) {
				System.out.println("focusGained has been called.");
					if (storyMap.getCurrentNode() != null) {
						narrationArea.setText(storyMap.getCurrentNode().getCaption());
						System.out.println("narrationArea should update...");
					}else {
						System.out.println("no currentNode.");
					}
				}
			};
		storyMap.tree.addFocusListener(treeFocusAdapter); // Add the focusListener to the tree.
		
		MouseAdapter treeMouseAdapter = new MouseAdapter(){ // Create a mouse listener.
			/*
			 * If a node is highlighted, write the node's caption to the narrationArea, update the current image, and log it.
			 * If the node has an image, let the user know in the UI. Otherwise, tell them the opposite. If there's no
			 * highlighted node, let the log know.
			 * @param e - The event of clicking the panel required to run the code.
			 */
			public void mouseClicked(MouseEvent e) {
				if (storyMap.getCurrentNode() != null) {
					narrationArea.setText(storyMap.getCurrentNode().getCaption());
					System.out.println("narrationArea should update...");
					EngineWindow.currentFile = storyMap.getCurrentNode().getImage();
					if (currentFile != null)
						imgPath.setText("Imaged Added.");
					else
						imgPath.setText("--No Image Inserted--");
				}else {
					System.out.println("no currentNode.");
				}
			}
		};
		storyMap.tree.addMouseListener(treeMouseAdapter);
		mapPanel.add(storyMap);
		
		JPanel notesPanel = new JPanel(); //The container for our notes area.
		notesPanel.setBounds(750, 33, 235, 382);
		frmTangine.getContentPane().add(notesPanel);
		notesPanel.setLayout(new GridLayout(0, 1, 0, 0));

		JTextArea notesArea = new JTextArea(); // Create said notes area.
		notesArea.setWrapStyleWord(true);
		notesArea.addFocusListener(new FocusAdapter() {
			/*
			 * If the user clicks away, update the notes String for when the user saves
			 * and let the log know what happened.
			 * @param e - The event of the notesArea losing focus required to run the code.
			 */
			@Override
			public void focusLost(FocusEvent e) {
				notes = notesArea.getText();
				System.out.println("Updated notes.");
			}
		});
		JScrollPane notesScrollPane = new JScrollPane(notesArea);
		notesArea.setLineWrap(true);
		notesPanel.add(notesScrollPane); // Add it to the panel.

		JButton btnNewBranch = new JButton("New Path"); 
		btnNewBranch.setFont(new Font("Dialog", Font.BOLD, 16));
		btnNewBranch.addActionListener(new ActionListener() {
			/*
			 * If the button is pressed, create a new node and log that event.
			 * @param e - The event of clicking the button required to run the code.
			 */
			public void actionPerformed(ActionEvent e) {
				storyMap.addObject("New Path " + place++, null, null);
				System.out.println("New path made.");
			}
		});
		btnNewBranch.setBounds(12, 428, 150, 59);
		frmTangine.getContentPane().add(btnNewBranch); // Add that to the frame.

		JButton btnDeletePath = new JButton("Delete Path");
		btnDeletePath.addActionListener(new ActionListener() {
			/*
			 * If the button is pressed, remove the current node and all children and log that event.
			 * @param e - The event of clicking the button required to run the code.
			 */
			public void actionPerformed(ActionEvent e) {
				storyMap.removeCurrentNode();
				System.out.println("Path removed."); 
			}
		});
		btnDeletePath.setBounds(12, 490, 150, 25);
		frmTangine.getContentPane().add(btnDeletePath); // Add to frame.

		imgPath.setFont(new Font("Dialog", Font.ITALIC, 12)); // Set the font of the imgPath that we created at the top of initialize().
//		imgPath.setBackground(Color.LIGHT_GRAY);
		imgPath.setBounds(310, 459, 446, 15);
		frmTangine.getContentPane().add(imgPath);
		
		
		JButton btnInsertImage = new JButton("Add Image");
		btnInsertImage.setToolTipText("Add an image to be displayed along with your narration."); 
		btnInsertImage.addActionListener(new ActionListener() {
			/*
			 * Show a file chooser so that the user can easily pick his/her file. Then, write the image to the node and
			 * update imgPath to let the user know that something happened. Don't crash by catching any IO exceptions.
			 * @param e - The event of clicking the button required to run the code.
			 */
			public void actionPerformed(ActionEvent e) {
				FileDialog fileDialog = new FileDialog(frmTangine, "Add Image", FileDialog.LOAD);
				fileDialog.setVisible(true);
				String filename = fileDialog.getDirectory() + fileDialog.getFile();
				File chosenFile = new File(filename);
				try {
					String chosenFilePath = chosenFile.getCanonicalPath();
					BufferedImage imageIn;
					imageIn = ImageIO.read(chosenFile);
					EngineWindow.picLabel = new JLabel(new ImageIcon(imageIn.getScaledInstance(600, 600, Image.SCALE_FAST)));
					storyMap.setCurrentNodeImage(EngineWindow.picLabel);
					currentFile = EngineWindow.picLabel;
			        imgPath.setText(chosenFilePath);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		JButton btnViewImage = new JButton("View Image");
		btnViewImage.setToolTipText("Show your uploaded image.");
		btnViewImage.addActionListener(new ActionListener() {
			/*
			 * If the current node has an image, display it.
			 */
			public void actionPerformed(ActionEvent e) {
				if (currentFile != null) {
					EngineWindow.picLabel = currentFile;
			        JOptionPane.showMessageDialog(null, picLabel);
		        }
			}
		});
		btnViewImage.setBounds(494, 427, 117, 25);
		frmTangine.getContentPane().add(btnViewImage);
		btnInsertImage.setBounds(365, 427, 117, 25);
		frmTangine.getContentPane().add(btnInsertImage);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setBounds(0, 0, 149, 19);
		frmTangine.getContentPane().add(toolBar);
		
		JButton btnNewButton = new JButton("New");
		btnNewButton.addActionListener(new ActionListener() {
			/*
			 * Ask the user if he/she really wants to create a new story.
			 * If he/she says yes, then remake the window and destroy all changes.
			 * @param e - The event of clicking the button required to run the code.
			 */
			public void actionPerformed(ActionEvent e) {
				String ObjButtons[] = {"Yes","No"};
		        int PromptResult = JOptionPane.showOptionDialog(null,"Do you want to create a new story?\nAll unsaved work will be lost.","Tangine",JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,ObjButtons,ObjButtons[1]);
		        if(PromptResult==JOptionPane.YES_OPTION) {
					window.frmTangine.setVisible(false);
					window = new EngineWindow();
					window.frmTangine.setVisible(true);
		        }
			}
		});
		toolBar.add(btnNewButton);
		

		JButton btnSave = new JButton("Save");
		toolBar.add(btnSave);
		btnSave.addActionListener(new ActionListener() {
			/*
			 * Open a file chooser dialogue and then save.
			 * See save Javadoc for information on how saving works.
			 * @param e - The event of clicking the button required to run the code.
			 */
			public void actionPerformed(ActionEvent e) {
				FileDialog fileDialog = new FileDialog(frmTangine, "Save Project", FileDialog.SAVE);
				fileDialog.setVisible(true);
				String filename = fileDialog.getDirectory() + fileDialog.getFile();
				File chosenFile = new File(filename);
				save(chosenFile, storyMap);
			}
		});
		btnSave.setFont(new Font("Dialog", Font.BOLD, 12));

		JButton btnLoad = new JButton("Open");
		toolBar.add(btnLoad);
		btnLoad.addActionListener(new ActionListener() {
			/*
			 * Open a file chooser to select a file to load into the editor. Opens a FileStream and
			 * reads and casts objects to variables and rebuilds everything. It then re-saves everything
			 * to refresh the file in the file system. Catches a couple of exceptions thrown if the user
			 * selects an invalid file.
			 * @param e - The event of clicking the button required to run the code.
			 */
			public void actionPerformed(ActionEvent e) {
				FileDialog fileDialog = new FileDialog(frmTangine, "Open Project", FileDialog.LOAD);
				fileDialog.setVisible(true);
				String filename = fileDialog.getDirectory() + fileDialog.getFile();
				File chosenFile = new File(filename);
				StoryNode root;
				DefaultTreeModel in;
				JTree inTree;
				try {
					InputStream file = new FileInputStream(chosenFile);
					BufferedInputStream buffer = new BufferedInputStream(file);
					ObjectInput input = new ObjectInputStream(buffer);
					mapPanel.setVisible(false);
					storyMap.clear();
					root = (StoryNode)input.readObject();
					in = (DefaultTreeModel)input.readObject();
					inTree = (JTree)input.readObject();
					notes = (String)input.readObject();
					notesArea.setText(notes);
					input.close();
					storyMap.setRootNode(root);
					storyMap.setModel(in, root);
					storyMap.setJTree(inTree);
					storyMap.tree.addFocusListener(treeFocusAdapter);
					storyMap.tree.addMouseListener(treeMouseAdapter);
					storyMap.reloadModel();
					mapPanel.setVisible(true);
					save(chosenFile, storyMap);
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});

		
		JLabel label = new JLabel();
		label.setToolTipText("Your entire story will appear here, allowing you to move about it freely.");
		label.setText("Story map");
		label.setBounds(11, 17, 72, 15);
		frmTangine.getContentPane().add(label);
		
		JLabel label_1 = new JLabel();
		label_1.setToolTipText("Write your caption here. This is what will appear to the player and tell them what's going on.");
		label_1.setText("Narration");
		label_1.setBounds(292, 17, 68, 15);
		frmTangine.getContentPane().add(label_1);
				
		JLabel label_2 = new JLabel();
		label_2.setToolTipText("Jot down any notes about your story here.");
		label_2.setText("Notes");
		label_2.setBounds(750, 18, 42, 15);
		frmTangine.getContentPane().add(label_2);
						
		JLabel lblImage = new JLabel("Image");
		lblImage.setBounds(308, 432, 70, 15);
		frmTangine.getContentPane().add(lblImage);
	}
	/*
	 * Writes the StoryMap's root node, model, and JTree to a .adfs file at the specified location.
	 * Catches an IOException thrown from a non-existent file.
	 * Precondition: An editor window must be initialized.
	 * @param chosenFile - The file to write to
	 * @param storyMap - The story map to write the file to.
	 * @exception - IOException
	 */
	private void save(File chosenFile, StoryMapTree storyMap) {
		try {
			String path = chosenFile.getCanonicalPath();
			File theFile;
			if (chosenFile.exists()) {
				theFile = new File(path);
			}else {
				theFile = new File(path + ".adfs");
			}
			OutputStream saveFile = new FileOutputStream(theFile);
			OutputStream saveBuffer = new BufferedOutputStream(saveFile);
			ObjectOutput output = new ObjectOutputStream(saveBuffer);
			output.writeObject(storyMap.getRootNode());
			output.writeObject(storyMap.getModel());
			output.writeObject(storyMap.getJTree());
			output.writeObject(notes);
			output.close();
			System.out.println("Project Saved.");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
