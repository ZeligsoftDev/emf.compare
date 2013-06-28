/*******************************************************************************
 * Copyright (c) 2013 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.compare.ide.ui.internal.structuremergeviewer.handler.propertytester;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.emf.compare.rcp.ui.internal.EMFCompareConstants;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * A property tester that check the way of merge.
 * 
 * @author <a href="mailto:axel.richard@obeo.fr">Axel Richard</a>
 * @since 3.0
 */
public class RightToLeftSidePropertyTester extends PropertyTester {

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.core.expressions.PropertyTester#test(java.lang.Object, java.lang.String,
	 *      java.lang.Object[], java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IEditorPart) {
			IEditorInput i = ((IEditorPart)receiver).getEditorInput();
			if (i instanceof CompareEditorInput) {
				CompareConfiguration configuration = ((CompareEditorInput)i).getCompareConfiguration();
				Boolean leftToRight = (Boolean)configuration.getProperty(EMFCompareConstants.MERGE_WAY);
				if (leftToRight != null && !leftToRight.booleanValue()) {
					return true;
				}
			}
		}
		return false;
	}

}