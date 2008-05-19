/*
 * $Id$
 *
 * IcyBee - http://www.nuclearbunny.org/icybee/
 * A client for the Internet CB Network - http://www.icb.net/
 *
 * Copyright © 2000-2003 David C. Gibbons, dcg@nuclearbunny.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.nuclearbunny.icybee.ui;

import org.nuclearbunny.util.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;

public class URLGrabber extends JFrame implements URLListener {
    private Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);

	private JTree urlTree;
	private HashMap hostMap;
	
	public URLGrabber() {
		setTitle("URL Grabber");
		
		hostMap = new HashMap();
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Received URLs");
        urlTree = new JTree(root);
        urlTree.setRootVisible(true);
        urlTree.setEditable(false);
		urlTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		urlTree.setShowsRootHandles(true);        
		urlTree.setBorder(emptyBorder);
		
		urlTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selRow = urlTree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = urlTree.getPathForLocation(e.getX(), e.getY());
				if (e.getClickCount() == 2) {
					DefaultMutableTreeNode selNode = (DefaultMutableTreeNode)selPath.getLastPathComponent();
					if (selNode.getUserObject() instanceof URL) {
						URL url = (URL)selNode.getUserObject();

			            Cursor c = getCursor();
			            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			
			            if (!BrowserControl.displayURL(url)) {
			                JOptionPane.showMessageDialog(URLGrabber.this, url.toExternalForm(), "Unable to display URL",
			                        JOptionPane.ERROR_MESSAGE);
			            }
			
			            setCursor(c);
					}
				}
			}
		});
        
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        JScrollPane scrollView = new JScrollPane(urlTree);
        c.add(scrollView, BorderLayout.CENTER);
	}
	
	public void urlReceived(final URLEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				URL url = e.getURL();
				String urlText = url.toString();
				String host = url.getHost();
				
				DefaultTreeModel treeModel = (DefaultTreeModel) urlTree.getModel();
				DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)treeModel.getRoot();
				
				DefaultMutableTreeNode hostNode = (DefaultMutableTreeNode)hostMap.get(host);
				if (hostNode == null) {
					hostNode = new DefaultMutableTreeNode(host);
					hostMap.put(host, hostNode);
					treeModel.insertNodeInto(hostNode, rootNode, 0);
				}
		
				boolean urlAlreadyFound = false;
				Enumeration children = hostNode.children();
				while (children.hasMoreElements()) {
					DefaultMutableTreeNode child = (DefaultMutableTreeNode)children.nextElement();
					if (child.toString().equalsIgnoreCase(urlText)) {
						urlAlreadyFound = true;
						break;
					}
				}
				
				if (!urlAlreadyFound) {
					DefaultMutableTreeNode urlNode = new DefaultMutableTreeNode(url);
					treeModel.insertNodeInto(urlNode, hostNode, 0);
					urlTree.scrollPathToVisible(new TreePath(urlNode.getPath()));
				}
			}
		});
	}
}
