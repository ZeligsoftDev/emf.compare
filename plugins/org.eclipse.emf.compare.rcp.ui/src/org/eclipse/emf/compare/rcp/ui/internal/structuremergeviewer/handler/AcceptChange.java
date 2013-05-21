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
package org.eclipse.emf.compare.rcp.ui.internal.structuremergeviewer.handler;

import static org.eclipse.emf.compare.utils.EMFComparePredicates.fromSide;

import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceSource;

/**
 * Handler that manages a merge of a difference in case of one side of the comparison is in read-only mode.
 * 
 * @author <a href="mailto:axel.richard@obeo.fr">Axel Richard</a>
 * @since 3.0
 */
public class AcceptChange extends AcceptRejectChange {

	@Override
	protected boolean isCopyDiffCase(Diff diff, boolean leftToRight) {
		if (leftToRight) {
			return fromSide(DifferenceSource.LEFT).apply(diff);
		} else {
			return fromSide(DifferenceSource.RIGHT).apply(diff);
		}
	}

}