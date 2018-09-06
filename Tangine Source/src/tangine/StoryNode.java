package tangine;
import javax.swing.JLabel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.io.Serializable;

public class StoryNode extends DefaultMutableTreeNode implements TreeNode, Serializable{
	private String nodeTitle; // The title of the node. Used in the editor tree and client buttons.
	private String nodeCaption; // Body of the node. Where the player puts narration.
	private JLabel nodeImage; // Optional image displayed alongside the caption in the client.
	private static final long serialVersionUID = 774L;

	
	//Constructors. Lovely constructors.
	public StoryNode() {
		nodeTitle = null;	/*This is the title of the node, that is, what it's called in the tree,
							  and what it's called in the header of the exported game.*/
		nodeCaption = null;	//This is the body of the node. Where the player puts the main txt.
		nodeImage = null;	//This is an optional image to display.
	}
	
	public StoryNode(
					String title,
					String caption,
					JLabel image) {
		nodeTitle = title;
		nodeCaption = caption;
		nodeImage = image;
	} 
	
	public String getTitle() {
		return nodeTitle;
	}
	
	public String getCaption() {
		return nodeCaption;
	}
	
	public JLabel getImage() {
		return nodeImage;
	}
	
	public void setTitle(String title) {
		nodeTitle = title;
	}
	
	public void setCaption(String caption) {
		nodeCaption = caption;
	}
	
	public void setImage(JLabel picLabel) {
		nodeImage = picLabel;
	}
	
	public String toString() {
		String back = "";
		back += this.getTitle();
		return back;
	}
}
