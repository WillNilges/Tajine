package tangine;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;



public class GameWindow {

	private JFrame frame;
	static GameWindow window;
	File saveFile;
	StoryMapTree storyMap;
	DefaultTreeModel storyModel;
	StoryNode currentNode;
	JScrollPane scrollPane;
	JButton[] buttonArray;

	/**
	 * Launches the application. Opens a JFileChooser to load a .adfs file, then
	 * launches the interpereter based on the file.
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
				FileDialog fileDialog = new FileDialog(new JFrame(), "Choose a .adfs file to get started", FileDialog.LOAD);
				fileDialog.setVisible(true);
				String filename = fileDialog.getDirectory() + fileDialog.getFile();
				File saveFile = new File(filename);
				
				try {
					window = new GameWindow(saveFile);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GameWindow(File fileIn) {
		saveFile = fileIn;
		StoryNode root;
		DefaultTreeModel inModel;
		JTree inTree;
		try {
			InputStream file = new FileInputStream(fileIn);
			BufferedInputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream(buffer);
			root = (StoryNode)input.readObject();
			inModel = (DefaultTreeModel)input.readObject();
			storyModel = inModel;
			inTree = (JTree)input.readObject();
			input.close();
			storyMap = new StoryMapTree(inModel, inTree);
			storyMap.reloadModel();
			currentNode = (StoryNode) ((StoryNode) storyModel.getRoot()).getChildAt(0);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		initialize(currentNode);
	}
	public GameWindow(File fileIn, StoryNode nodeIn) {
		saveFile = fileIn;
		currentNode = nodeIn;
		initialize(currentNode);
	}

	/**
	 * Initialize the contents of the frame. Creates the frame, the image panel, the caption
	 * text area, and a bunch of buttons based on what it reads in from the StoryNode panel.
	 * @param node - StoryNode to create the JPanel with.
	 */
	void initialize(StoryNode node) {		
		frame = new JFrame();
		frame.setBounds(100, 100, 1000, 720);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		String fileName = saveFile.getName();
		frame.setTitle(saveFile.getName().substring(0, fileName.length()-5) + " - " + currentNode.getTitle());

		JPanel imagePanel = new JPanel();
		imagePanel.setBounds(12, 20, 600, 600);
		if (node.getImage() != null) {
			JLabel myPicture;
			if (node.getImage() != null) {
				myPicture = node.getImage();
				imagePanel.add(myPicture);
			}
		}
		frame.getContentPane().add(imagePanel);

		JPanel captionPanel = new JPanel();
		captionPanel.setBounds(616, 20, 372, 600);
		captionPanel.setLayout(new BorderLayout(0, 0));

		JTextArea captionTextArea = new JTextArea();
		captionTextArea.setFont(new Font("Dialog", Font.PLAIN, 14));
		captionTextArea.setBackground(new Color(255, 255, 255));
		captionTextArea.setWrapStyleWord(true);
		captionTextArea.setLineWrap(true);
		captionTextArea.setEditable(false);
		captionPanel.add(captionTextArea);
		captionTextArea.setText(node.getCaption());
		captionTextArea.setCaretPosition(0);
		frame.getContentPane().add(captionPanel);
		
		JScrollPane captionScrollPane = new JScrollPane(captionTextArea);
		captionPanel.add(captionScrollPane);


		JPanel optionsPanel = new JPanel();
		JPanel optionsScrollPanel = new JPanel();
		optionsPanel.setBounds(12, 630, 976, 55);
		System.out.println("Attempting... There might be " + node.getChildCount() + " things.");
		if (node.getChildCount() > 0) {
			for (int i = 0; i < node.getChildCount(); i++) {
				StoryNode child = (StoryNode)node.getChildAt(i);
				JButton button = new JButton(((StoryNode) node.getChildAt(i)).getTitle());
				button.addActionListener(new ActionListener() {
					/*
					 * Sets the buttons to recursively call a GameWindow constructor based
					 * on a child of the currentNode. Logs the event.
					 * @param e - The event of clicking the button required to run the code.
					 */
					public void actionPerformed(ActionEvent e) {
						currentNode = child;
						window.frame.setVisible(false);
						window = new GameWindow(saveFile, child);
						window.frame.setVisible(true);
						frame.repaint();
						System.out.println("GameWindow should be re-initialized.");
					}
				});
				optionsScrollPanel.add(button);
				//			scrollPane.repaint();
				System.out.println("Added button to panel.");
				//			return scrollPane;//This is where recursion might come in. I'm not sure if I can declare and add buttons like this. Several levels of recursion might fix that.
			}
		}else {
			JLabel end = new JLabel("The end.");
			optionsPanel.add(end);
		}
		System.out.println("Done.");
		optionsPanel.setLayout(new GridLayout(1, 0, 0, 0));
		frame.getContentPane().add(optionsPanel);
		
		JScrollPane optionsScrollPane = new JScrollPane(optionsScrollPanel);
		optionsPanel.add(optionsScrollPane);

		JToolBar toolBar = new JToolBar();
		toolBar.setBounds(0, 0, 182, 19);
		frame.getContentPane().add(toolBar);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			/*
			 * Opens an InputStream and reconstructs the opened save file, adding a currentNode object at the end
			 * to save progress.
			 * @param e - The event of clicking the button required to run the code.
			 * @exception e1 - an IOException thrown from having an invalid/missing save file.
			 * @exception e1 - a ClassNotFoundException thrown from missing any data that needs to be written.
			 */
			public void actionPerformed(ActionEvent e) {
				try {
					InputStream file = new FileInputStream(saveFile);
					BufferedInputStream buffer = new BufferedInputStream(file);
					ObjectInput input = new ObjectInputStream(buffer);
					
					StoryNode root = (StoryNode) input.readObject();
					DefaultTreeModel inModel = (DefaultTreeModel)input.readObject();
					JTree inTree = (JTree)input.readObject();
					String notes = (String)input.readObject();
					input.close();
					
					String path = saveFile.getCanonicalPath();
					File theFile;
					theFile = new File(path);
					OutputStream fileOut = new FileOutputStream(theFile);
					OutputStream saveBuffer = new BufferedOutputStream(fileOut);
					ObjectOutput output = new ObjectOutputStream(saveBuffer);
					output.writeObject(root);
					output.writeObject(inModel);
					output.writeObject(inTree);
					output.writeObject(notes);
					output.writeObject(currentNode);//You can have this at the very end of .adfs files.
					output.close();
					System.out.println("Game Saved.");
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
		toolBar.add(btnSave);

		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			/*
			 * Creates an InputStream based on the current saveFile and reads in all data to get to
			 * the saved node, then constructs a new GameWindow with that node as input.
			 * @param e - The event of clicking the button required to run the code.
			 * @exception e1 - FileNotFoundException thrown by not having a file to read from.
			 * @exception e1 - IOException thrown by missing objects. Will attempt to read them,
			 * but fail because they're literally not in the file.
			 * @exception e1 - A ClassNotFOundException thrown by not being able to interpret the
			 * loaded data properly.
			 */
			public void actionPerformed(ActionEvent e) {
				InputStream file;
				try {
					file = new FileInputStream(saveFile);
					BufferedInputStream buffer = new BufferedInputStream(file);
					ObjectInput input = new ObjectInputStream(buffer);
					
					StoryNode root = (StoryNode) input.readObject();
					DefaultTreeModel inModel = (DefaultTreeModel)input.readObject();
					JTree inTree = (JTree)input.readObject();
					String notes = (String)input.readObject();
					StoryNode tempNode = (StoryNode)input.readObject();
					if (tempNode != null) {
						currentNode = tempNode;
						window.frame.setVisible(false);
						window = new GameWindow(saveFile, currentNode);
						window.frame.setVisible(true);
						frame.repaint();
					}
					input.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		toolBar.add(btnLoad);
		
		JButton btnRestart = new JButton("Restart");
		btnRestart.addActionListener(new ActionListener() {
			/*
			 * Restarts the text adventure by creating an InputStream re-making the GameWindow with the
			 * root node's first child (which is always the start of an adventure. Asks before
			 * doing anything to avoid destorying progress.
			 * @param e - The event of clicking the button required to run the code.
			 * @exception e1 - FileNotFoundException thrown by not having the saveFile param set.
			 * @exception e1 - IOException thrown by missing the rootNode.
			 * @exception e1 - A ClassNotFOundException thrown by not being able to interpret the
			 * loaded data properly.
			 */
			public void actionPerformed(ActionEvent e) {
				String ObjButtons[] = {"Yes","No"};
		        int PromptResult = JOptionPane.showOptionDialog(null,
		        												"Do you want to restart?",
		        												"Tangine",
		        												JOptionPane.DEFAULT_OPTION,
		        												JOptionPane.WARNING_MESSAGE,
		        												null,
		        												ObjButtons,
		        												ObjButtons[1]);
		        if(PromptResult == JOptionPane.YES_OPTION) {
					window.frame.setVisible(false);
					window = new GameWindow(saveFile);
					window.frame.setVisible(true);
					frame.repaint();
		        }
			}
		});
		toolBar.add(btnRestart);
	}
}
