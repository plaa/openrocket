package net.sf.openrocket.gui.main;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Implements a menu for the example Open Rocket design files.
 */
public final class ExampleDesignFileMenu extends JMenu {
	
	@Inject
	private BasicFrameHelper basicFrameHelper;
	
	/**
	 * The window to which an open design file action will be parented to (typically an instance of BasicFrame).
	 */
	private final Window parent;
	
	/**
	 * Constructor.
	 *
	 * @param s         the I18N menu string
	 * @param theParent the window to which an open design file action will be parented to (typically an instance of
	 *                  BasicFrame).
	 */
	@Inject
	public ExampleDesignFileMenu(@Assisted String s, @Assisted Window theParent) {
		super(s);
		
		parent = theParent;
		updateMenu();
	}
	
	/**
	 * Create menu items.
	 */
	private void updateMenu() {
		removeAll();
		ExampleDesignFile[] examples = ExampleDesignFile.getExampleDesigns();
		for (ExampleDesignFile file : examples) {
			Action action = createAction(file);
			action.putValue(Action.NAME, file.toString());
			JMenuItem menuItem = new JMenuItem(action);
			add(menuItem);
		}
	}
	
	/**
	 * When a user clicks on one of the recently used design files, open it.
	 *
	 * @param file the design file name (absolute path)
	 *
	 * @return the action to open a design file
	 */
	private Action createAction(final ExampleDesignFile example) {
		Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				basicFrameHelper.open(example.getURL(), parent);
			}
		};
		
		action.putValue(Action.ACTION_COMMAND_KEY, example.toString());
		return action;
	}
}