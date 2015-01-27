package net.sf.openrocket.gui.main;

import java.awt.Window;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import net.sf.openrocket.aerodynamics.WarningSet;
import net.sf.openrocket.document.OpenRocketDocument;
import net.sf.openrocket.document.OpenRocketDocumentFactory;
import net.sf.openrocket.file.RocketLoadException;
import net.sf.openrocket.gui.configdialog.ComponentConfigDialog;
import net.sf.openrocket.gui.dialogs.SwingWorkerDialog;
import net.sf.openrocket.gui.dialogs.WarningDialog;
import net.sf.openrocket.gui.util.OpenFileWorker;
import net.sf.openrocket.guice.BasicFrameFactory;
import net.sf.openrocket.l10n.Translator;
import net.sf.openrocket.util.BugException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Guice helper class to open new BasicFrame instances.
 */
public class BasicFrameHelper {
	private static final Logger log = LoggerFactory.getLogger(BasicFrameHelper.class);
	
	@Inject
	private BasicFrameFactory factory;
	
	@Inject
	private Translator trans;
	
	
	public BasicFrame newFrame() {
		log.info("New action initiated");
		
		OpenRocketDocument doc = OpenRocketDocumentFactory.createNewRocket();
		BasicFrame frame = factory.create(doc);
		frame.setReplaceable(true);
		frame.setVisible(true);
		return frame;
	}
	
	
	/**
	 * Open a file based on a URL.
	 * @param url		the file to open.
	 * @param parent	the parent window for dialogs.
	 * @return			<code>true</code> if opened successfully.
	 */
	public void open(URL url, Window parent) {
		String displayName = null;
		// First figure out the file name from the URL
		
		// Try using URI.getPath();
		try {
			URI uri = url.toURI();
			displayName = uri.getPath();
		} catch (URISyntaxException ignore) {
		}
		
		// Try URL-decoding the URL
		if (displayName == null) {
			try {
				displayName = URLDecoder.decode(url.toString(), "UTF-8");
			} catch (UnsupportedEncodingException ignore) {
			}
		}
		
		if (displayName == null) {
			displayName = "";
		}
		
		// Remove path from filename
		if (displayName.lastIndexOf('/') >= 0) {
			displayName = displayName.substring(displayName.lastIndexOf('/') + 1);
		}
		
		
		// Open the file
		log.info("Opening file from url=" + url + " filename=" + displayName);
		
		OpenFileWorker worker = new OpenFileWorker(url);
		open(worker, displayName, parent, true);
	}
	
	
	/**
	 * Open the specified file in a new design frame.  If an error occurs, an error
	 * dialog is shown and <code>false</code> is returned.
	 *
	 * @param file		the file to open.
	 * @param parent	the parent component for which a progress dialog is opened.
	 * @return			whether the file was successfully loaded and opened.
	 */
	public boolean open(File file, Window parent) {
		OpenFileWorker worker = new OpenFileWorker(file);
		return open(worker, file.getName(), parent, false);
	}
	
	
	/**
	 * Open the specified file using the provided worker.
	 *
	 * @param worker	the OpenFileWorker that loads the file.
	 * @param displayName	the file name to display in dialogs.
	 * @param file		the File to set the document to (may be null).
	 * @param parent
	 * @param openRocketConfigDialog if true, will open the configuration dialog of the rocket.  This is useful for examples.
	 * @return
	 */
	private boolean open(OpenFileWorker worker, String displayName, Window parent, boolean openRocketConfigDialog) {
		// Open the file in a Swing worker thread
		log.info("Starting OpenFileWorker");
		if (!SwingWorkerDialog.runWorker(parent, "Opening file", "Reading " + displayName + "...", worker)) {
			// User cancelled the operation
			log.info("User cancelled the OpenFileWorker");
			return false;
		}
		
		
		// Handle the document
		OpenRocketDocument doc = null;
		try {
			
			doc = worker.get();
			
		} catch (ExecutionException e) {
			
			Throwable cause = e.getCause();
			
			if (cause instanceof FileNotFoundException) {
				
				log.warn("File not found", cause);
				JOptionPane.showMessageDialog(parent,
						"File not found: " + displayName,
						"Error opening file", JOptionPane.ERROR_MESSAGE);
				return false;
				
			} else if (cause instanceof RocketLoadException) {
				
				log.warn("Error loading the file", cause);
				JOptionPane.showMessageDialog(parent,
						"Unable to open file '" + displayName + "': "
								+ cause.getMessage(),
						"Error opening file", JOptionPane.ERROR_MESSAGE);
				return false;
				
			} else {
				
				throw new BugException("Unknown error when opening file", e);
				
			}
			
		} catch (InterruptedException e) {
			throw new BugException("EDT was interrupted", e);
		}
		
		if (doc == null) {
			throw new BugException("Document loader returned null");
		}
		
		
		// Show warnings
		WarningSet warnings = worker.getRocketLoader().getWarnings();
		if (!warnings.isEmpty()) {
			log.info("Warnings while reading file: " + warnings);
			WarningDialog.showWarnings(parent,
					new Object[] {
							//// The following problems were encountered while opening
							trans.get("BasicFrame.WarningDialog.txt1") + " " + displayName + ".",
							//// Some design features may not have been loaded correctly.
							trans.get("BasicFrame.WarningDialog.txt2")
					},
					//// Warnings while opening file
					trans.get("BasicFrame.WarningDialog.title"), warnings);
		}
		
		// Open the frame
		log.debug("Opening new frame with the document");
		BasicFrame frame = factory.create(doc);
		frame.setVisible(true);
		
		if (parent != null && parent instanceof BasicFrame) {
			((BasicFrame) parent).closeIfReplaceable();
		}
		if (openRocketConfigDialog) {
			ComponentConfigDialog.showDialog(frame, doc, doc.getRocket());
		}
		
		return true;
	}
	
}
