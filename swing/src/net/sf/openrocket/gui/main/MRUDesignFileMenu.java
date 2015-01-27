package net.sf.openrocket.gui.main;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Implements a menu for the Most-Recently-Used Open Rocket design files.
 */
public final class MRUDesignFileMenu extends JMenu {
	
	@Inject
	private BasicFrameHelper basicFrameHelper;
	
	/**
	 * The window to which an open design file action will be parented to (typically an instance of BasicFrame).
	 */
	private Window parent;
	
	/**
	 * Constructor.
	 *
	 * @param s         the I18N menu string
	 * @param theParent the window to which an open design file action will be parented to (typically an instance of
	 *                  BasicFrame).
	 */
	@Inject
	public MRUDesignFileMenu(@Assisted String s, @Assisted Window theParent) {
		super(s);
		
		parent = theParent;
		MRUDesignFile opts = MRUDesignFile.getInstance();
		opts.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (!evt.getPropertyName().equals(MRUDesignFile.MRU_FILE_LIST_PROPERTY)) {
					return;
				}
				updateMenu();
			}
		});
		
		updateMenu();
	}
	
	/**
	 * Create menu items.
	 */
	private void updateMenu() {
		removeAll();
		List<String> list = MRUDesignFile.getInstance().getMRUFileList();
		for (String name : list) {
			Action action = createAction(name);
			action.putValue(Action.NAME, name);
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
	private Action createAction(String file) {
		Action action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String command = e.getActionCommand();
				if (basicFrameHelper.open(new File(command), parent)) {
					MRUDesignFile.getInstance().addFile(command);
				}
				else {
					MRUDesignFile.getInstance().removeFile(command);
				}
			}
		};
		
		action.putValue(Action.ACTION_COMMAND_KEY, file);
		return action;
	}
	
}