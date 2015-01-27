package net.sf.openrocket.guice;

import java.awt.Window;

import net.sf.openrocket.document.OpenRocketDocument;
import net.sf.openrocket.gui.dialogs.optimization.GeneralOptimizationDialog;

/**
 * Guice assisted injection factory interface.
 */
public interface GeneralOptimizationDialogFactory {
	
	public GeneralOptimizationDialog create(OpenRocketDocument document, Window parent);
	
}
