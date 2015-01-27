package net.sf.openrocket.guice;

import java.awt.Window;

import net.sf.openrocket.gui.main.MRUDesignFileMenu;

/**
 * Guice assisted injection factory interface.
 */
public interface MRUDesignFileMenuFactory {
	
	public MRUDesignFileMenu create(String name, Window parent);
	
}
