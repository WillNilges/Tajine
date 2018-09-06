//Based on: https://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html#DynamicTreeDemo


/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package tangine;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.Serializable;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class StoryMapTree extends JPanel implements Serializable{
	protected StoryNode startNode; // The root of the StoryMap.
	protected DefaultTreeModel model; // The StoryMap's model, for GUI and saving.
	protected JTree tree = new JTree(); // The StoryMap's JTree, for GUI.
	JScrollPane scrollPane; // A ScrollPane that the StoryMap is placed in before being place in the JFrame.
	private Toolkit toolkit = Toolkit.getDefaultToolkit(); // Toolkit for giving audio feedback in the GUI.
	static final long serialVersionUID = 774L; // Serialization UID, an identifier.

	public StoryMapTree() {
		super(new GridLayout(1,0));
		startNode = new StoryNode("Start", "Write something!", null);
		model = new DefaultTreeModel(startNode);
		model.addTreeModelListener(new MyTreeModelListener());
		tree = new JTree(model);
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setRootVisible(false);
		scrollPane = new JScrollPane(tree);
		add(scrollPane);
	}
	
	public StoryMapTree(DefaultTreeModel newModel, JTree newTree) {
		super(new GridLayout(1,0));
		startNode = new StoryNode("Start", "Write something!", null);
		model = newModel;
		model.addTreeModelListener(new MyTreeModelListener());
		tree = newTree;
		tree.setEditable(true);
		tree.getSelectionModel().setSelectionMode
		(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		scrollPane = new JScrollPane(tree);
		add(scrollPane);
	}
	//Remove all nodes except the start node.
	public void clear() {
		startNode.removeAllChildren();
		reloadModel();
	}

	/*
	 * Remove the selected node. Check if anything's selected. If something is, then delete it.
	 * A beep means that either there was no selection, or the root was selected.
	 */
	public void removeCurrentNode() {
		TreePath currentSelection = tree.getSelectionPath();
		if (currentSelection != null) {
			StoryNode currentNode = (StoryNode)(currentSelection.getLastPathComponent());
			StoryNode parent = (StoryNode)(currentNode.getParent());
			if (parent != null) {
				try {
					model.removeNodeFromParent(currentNode);
				}catch(ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
				reloadModel();
				System.out.println("A path should have been removed.");
				return;
			}
		} 

		toolkit.beep();
	}

	/*
	 * Changes the selected node's caption. Check if anything's selected. If something is, then change it.
	 * A beep means that either there was no selection, or the root was selected.
	 * @param caption - A string to write to the current node's caption.
	 */
	public void setCurrentNodeCaption(String caption) {
		TreePath currentSelection = tree.getSelectionPath();
		if (currentSelection != null) {
			StoryNode currentNode = (StoryNode) (currentSelection.getLastPathComponent());
			MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
			if (parent != null) {
				currentNode.setCaption(caption);
				System.out.println("Caption = " + currentNode.getCaption());
				return;
			}
		} 
		toolkit.beep();
	}
	
	/*
	 * Changes the selected node's caption. Check if anything's selected. If something is, then change it.
	 * A beep means that either there was no selection, or the root was selected.
	 * @param picLabel - A JLabel image to write to the current node's caption.
	 */
	public void setCurrentNodeImage(JLabel picLabel) {
		TreePath currentSelection = tree.getSelectionPath();
		if (currentSelection != null) {
			StoryNode currentNode = (StoryNode) (currentSelection.getLastPathComponent());
			MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
			if (parent != null) {
				currentNode.setImage(picLabel);
				System.out.println("image added. ");
				return;
			}
		}
		toolkit.beep();
	}

	/*
	 * Checks to see if a node is selected. If one is, then return that node.
	 * @return currentNode - The currently selected StoryNode.
	 */
	public StoryNode getCurrentNode() {
		TreePath currentSelection = tree.getSelectionPath();
		if (currentSelection != null) {
			StoryNode currentNode = (StoryNode) (currentSelection.getLastPathComponent());
			StoryNode parent = (StoryNode)(currentNode.getParent());
			return currentNode;
		}
		else {
			System.out.println("No selection path.");
			return null;
		}

	}

	/* 
	 * This version of addObject is for ease-of-use more than anything. addObject requires more information
	 * that can be added programatically.
	 * Add branch to the currently selected node. Check that a parent path actually exists, then
	 * create the other fields required for addObject to run. Call the real addObject class.
	 * @param title - The title of the new StoryNode
	 * @param caption - The caption of the new StoryNode
	 * @param image - the image for the new StoryNode
	 * @return the newly created StoryNode.
	 */
	public StoryNode addObject(String title, String caption, JLabel image) {
		StoryNode parentNode = null;
		TreePath parentPath = tree.getSelectionPath();

		if (parentPath == null) {
			parentNode = startNode;
		} else {
			parentNode = (StoryNode) parentPath.getLastPathComponent();
		}
		reloadModel();
		return addObject(parentNode, title, caption, image, true);
	}

	/* 
	 * This version of addObject is for ease-of-use more than anything. addObject requires more information
	 * that can be added programatically. This version allows the programmer to specify the parent node.
	 * Would be useful if I decide to keep developing this software. Add branch to the currently selected node.
	 * Call the real addObject class, this time with a known parent.
	 * @param parent - The StoryNode above this new one.
	 * @param title - The title of the new StoryNode.
	 * @param caption - The caption of the new StoryNode.
	 * @param image - the image for the new StoryNode.
	 * @return the newly created StoryNode.
	 */
	public StoryNode addObject(StoryNode parent, String title, String caption, JLabel image) {
		reloadModel();
		return addObject(parent, title, caption, image, false);
	}

	/* 
	 * This is the true version of addObject.
	 * Add branch to the currently selected node. Declares the parent the root node if there isn't one specified,
	 * then calls insertNodeInto on the TreeModel using the variables passed in. Also scrolls the JTree view to 
	 * the created node and logs the event.
	 * @param parent - The StoryNode above this new one.
	 * @param title - The title of the new StoryNode.
	 * @param caption - The caption of the new StoryNode.
	 * @param image - the image for the new StoryNode.
	 * @param isVisible - Weather or not the new node is visible in the editor.
	 * @return the newly created StoryNode.
	 */
	public StoryNode addObject(StoryNode parent, String title, String caption, JLabel image, boolean isVisible) {
		StoryNode childNode = new StoryNode(title, caption, image);

		if (parent == null) {
			parent = startNode;
		}

		//You gotta invoke this on the TreeModel, and _NOT_ DefaultMutableTreeNode
		model.insertNodeInto(childNode,
				parent, 
				parent.getChildCount());

		//Move the view to the fresh node.
		if (isVisible) {
			tree.scrollPathToVisible(new TreePath(childNode.getPath()));
		}
		reloadModel();
		System.out.println("A new path should have been made");
		return childNode;
	}
	
	public String toString() {
		return this.getName();
	}
	
	public StoryNode getRootNode() {
		return startNode;
	}
	
	public DefaultTreeModel getModel() {
		return model;
	}
	
	public JTree getJTree() {
		return tree;
	}
	
	public void setRootNode(StoryNode in) {
		startNode = in;
		System.out.println("Set startNode");
	}
	
	/*
	 * Sets the StoryMapTree's TreeModel, thereby restructuring it.
	 * @param in - The DefaultTreeModel to be used.
	 * @param root - The root StoryNode of the treeModel.
	 */
	public void setModel(DefaultTreeModel in, StoryNode root) {
		in.setRoot(root);
		TreeModelListener[] l = in.getTreeModelListeners();
		model = in;
		model.removeTreeModelListener(l[0]);
		model.addTreeModelListener(new MyTreeModelListener());
		reloadModel();
		System.out.println("Set TreeModel");
	}
	
	/*
	 * Sets the StoryMapTree's JTree. Used to communicate a change in the DefaultTreeModel
	 * to the JPanel.
	 * @param in - The JTree to set the panel to.
	 */
	public void setJTree(JTree in) {
		tree.removeSelectionPath(tree.getSelectionPath());
		in.setModel(model);
		tree = in;
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		remove(scrollPane);
		System.out.println("Set JTree");
		scrollPane = new JScrollPane(tree);
		add(scrollPane);
	}
	
	/*
	 * Reloads the DefaultTreeModel and all the JFrame components of the StoryMapTree 
	 */
	public void reloadModel() {
		model.reload();
		tree.repaint();
		scrollPane.repaint();
		for (int i = 0; i < tree.getRowCount(); i++) {
		    tree.expandRow(i);
		}
		System.out.println("Refreshed.");
	}
	
	
	class MyTreeModelListener implements TreeModelListener {
		/*
		 * If the event lists children, then the changed
		 * node is the child of the node we've already
		 * gotten.  Otherwise, the changed node and the
		 * specified node are the same. You cannot edit the
		 * rootNode.
		 * @param event - Something changing in the tree
		 */
		public void treeNodesChanged(TreeModelEvent event) {
			StoryNode node;
			node = (StoryNode) event.getTreePath().getLastPathComponent();
			try {
				int index = event.getChildIndices()[0];
				node = (StoryNode)(node.getChildAt(index));
				node.setTitle(node.getUserObject().toString());
			}catch(Exception e) {
				e.printStackTrace();
			}

			System.out.println("The user has finished editing the node.");
			System.out.println("New value: " + node.getUserObject());
		}
		public void treeNodesInserted(TreeModelEvent e) {
		}
		public void treeNodesRemoved(TreeModelEvent e) {
		}
		public void treeStructureChanged(TreeModelEvent e) {
		}
	}
}
