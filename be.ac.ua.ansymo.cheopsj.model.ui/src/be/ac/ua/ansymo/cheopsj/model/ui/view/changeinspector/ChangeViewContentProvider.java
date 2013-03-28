/*******************************************************************************
 * Copyright (c) 2011 Quinten David Soetens
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Quinten David Soetens - initial API and implementation
 ******************************************************************************/
package be.ac.ua.ansymo.cheopsj.model.ui.view.changeinspector;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerEvent;
import be.ac.ua.ansymo.cheopsj.model.ModelManagerListener;



public class ChangeViewContentProvider implements IStructuredContentProvider,
	ModelManagerListener {
	private TableViewer viewer;
	private ChangeView view;
	private ModelManager manager;

	public ChangeViewContentProvider(ChangeView changeView) {
		view = changeView;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;
		if (manager != null)
			manager.removeModelManagerListener(this);
		manager = (ModelManager) newInput;
		if (manager != null)
			manager.addModelManagerListener(this);
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		return manager.getChanges().toArray();
	}

	@Override
	public void changesAdded(final ModelManagerEvent event) {
		// If this is the UI thread, then make the change.
		if (Display.getCurrent() != null) {
			updateViewer(event);
			return;
		}

		// otherwise, redirect to execute on the UI thread.
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				updateViewer(event);
			}
		});
	}

	private void updateViewer(ModelManagerEvent event) {
		// Use the setRedraw method to reduce flicker
		// when adding or removing multiple items in a table.
		viewer.getTable().setRedraw(false);
		try {
			viewer.add(event.getNewChanges());
			viewer.refresh();
			view.updateSummaryBreakdown();
		} finally {
			viewer.getTable().setRedraw(true);
		}
	}

	public String getSummary() {
		return manager.getSummary();
	}

}
