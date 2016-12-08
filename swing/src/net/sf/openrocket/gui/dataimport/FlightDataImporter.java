package net.sf.openrocket.gui.dataimport;

import java.awt.Window;

import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.plugin.Plugin;

@Plugin
public interface FlightDataImporter {
	
	/**
	 * Return the user-visible name of this import plugin.
	 * This is shown to the user to choose an import source.
	 */
	public String getName();
	
	/**
	 * Perform the import.  This may involve opening new dialogs to prompt
	 * the user for more input.
	 * 
	 * TODO: What return type?  Should OpenRocketDocument be as parameter?
	 */
	public Simulation doImport(Window parent);
	
}
