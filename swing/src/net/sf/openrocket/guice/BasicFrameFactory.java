package net.sf.openrocket.guice;

import net.sf.openrocket.document.OpenRocketDocument;
import net.sf.openrocket.gui.main.BasicFrame;


/**
 * Guice assisted injection factory interface.
 * <p>
 * You should use the BasicFrameHelper class instead of this class
 * to open new frames.
 */
public interface BasicFrameFactory {
	
	public BasicFrame create(OpenRocketDocument document);
	
}
