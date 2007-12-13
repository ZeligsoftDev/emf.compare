/*******************************************************************************
 * Copyright (c) 2006, 2007 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.compare.tests.unit.match;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.eclipse.emf.compare.tests.unit.match.statistic.differencesservice.DifferencesServiceTestSuite;
import org.eclipse.emf.compare.tests.unit.match.statistic.similarity.TestNameSimilarity;
import org.eclipse.emf.compare.tests.unit.match.statistic.similarity.structuresimilarity.StructureSimilarityTestSuite;

/**
 * Tests for the match plugin.
 * 
 * @author Laurent Goubet <a href="mailto:laurent.goubet@obeo.fr">laurent.goubet@obeo.fr</a>
 */
public class MatchTestSuite extends TestCase {
	/** Minimal -Xmx setting to run comparison tests. Set to 500m. */
	private static final long MIN_XMX_SETTING = 500000000;
	
	/**
	 * Launches the test with the given arguments.
	 * 
	 * @param args
	 *            Arguments of the testCase.
	 */
	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	/**
	 * Creates the {@link junit.framework.TestSuite TestSuite} for all the test.
	 * 
	 * @return The testsuite containing all the tests
	 */
	public static Test suite() {
		final TestSuite suite = new TestSuite("Tests for the match plugin."); //$NON-NLS-1$
		suite.addTestSuite(TestEnginesPriority.class);
		suite.addTestSuite(TestNameSimilarity.class);
		suite.addTest(StructureSimilarityTestSuite.suite());
		// These tests are too long/costly to be run with too low memory
		if (Runtime.getRuntime().maxMemory() > MIN_XMX_SETTING)
			suite.addTest(DifferencesServiceTestSuite.suite());
		return suite;
	}
}
