package net.sf.openrocket.guice;

import java.awt.Window;

import net.sf.openrocket.gui.main.ExampleDesignFileMenu;

/**
 * Guice assisted injection factory interface.
 */
public interface ExampleDesignFileMenuFactory {
	
	public ExampleDesignFileMenu create(String name, Window parent);
	
}
