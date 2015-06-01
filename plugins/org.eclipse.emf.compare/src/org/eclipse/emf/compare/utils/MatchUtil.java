/*******************************************************************************
 * Copyright (c) 2012, 2015 Obeo and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *     Michael Borkowski - bug 467576
 *******************************************************************************/
package org.eclipse.emf.compare.utils;

import static org.eclipse.emf.compare.utils.ReferenceUtil.getAsList;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.DifferenceKind;
import org.eclipse.emf.compare.DifferenceSource;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.compare.util.CompareSwitch;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * This utility class holds methods that will be used by the diff and merge processes.
 * 
 * @author <a href="mailto:cedric.notot@obeo.fr">Cedric Notot</a>
 */
public final class MatchUtil {
	/**
	 * Utility classes don't need a default constructor.
	 */
	private MatchUtil() {
		// Hides default constructor
	}

	/**
	 * Get the object which is the origin value from the given matching <code>object</code>.
	 * 
	 * @param comparison
	 *            The comparison.
	 * @param object
	 *            The given object.
	 * @return The origin value.
	 */
	public static EObject getOriginObject(Comparison comparison, EObject object) {
		EObject result = null;
		Match match = comparison.getMatch(object);
		if (match != null) {
			if (comparison.isThreeWay()) {
				result = match.getOrigin();
			} else {
				if (object == match.getLeft()) {
					result = match.getRight();
				} else {
					result = match.getLeft();
				}
			}
		}
		return result;
	}

	/**
	 * From a given mono-valued reference change, get the origin value.
	 * 
	 * @param comparison
	 *            The comparison.
	 * @param difference
	 *            The given reference change.
	 * @return The origin value.
	 */
	public static EObject getOriginValue(Comparison comparison, ReferenceChange difference) {
		final EReference reference = difference.getReference();
		if (!reference.isContainment() && !reference.isMany()
				&& difference.getKind().equals(DifferenceKind.CHANGE)) {
			EObject originContainer = getOriginContainer(comparison, difference);
			if (originContainer != null) {
				Object originValue = ReferenceUtil.safeEGet(originContainer, reference);
				if (originValue instanceof EObject) {
					return (EObject)originValue;
				}
			}
		}
		return null;
	}

	/**
	 * Get the business model object containing the given <code>difference</code> in the origin side.
	 * 
	 * @param comparison
	 *            The comparison.
	 * @param difference
	 *            The difference.
	 * @return The object.
	 */
	public static EObject getOriginContainer(Comparison comparison, Diff difference) {
		final EObject diffContainer;
		if (comparison.isThreeWay()) {
			diffContainer = difference.getMatch().getOrigin();
		} else {
			if (getContainer(comparison, difference) == difference.getMatch().getLeft()) {
				diffContainer = difference.getMatch().getRight();
			} else {
				diffContainer = difference.getMatch().getLeft();
			}
		}
		return diffContainer;
	}

	/**
	 * Get the business model object containing the given <code>difference</code>.
	 * 
	 * @param comparison
	 *            The comparison.
	 * @param difference
	 *            The difference.
	 * @return The object.
	 */
	public static EObject getContainer(Comparison comparison, Diff difference) {
		EObject result = null;
		Match match = difference.getMatch();
		final DifferenceSource source = difference.getSource();
		final DifferenceKind kind = difference.getKind();
		switch (kind) {
			case DELETE:
				if (comparison.isThreeWay()) {
					result = match.getOrigin();
				} else {
					result = match.getRight();
				}
				break;
			case ADD:
				// fall through
			case MOVE:
				if (source == DifferenceSource.LEFT) {
					result = match.getLeft();
				} else {
					result = match.getRight();
				}
				break;
			case CHANGE:
				final Object value = getValue(difference);
				final EStructuralFeature feature = getStructuralFeature(difference);
				if (value == null || feature == null) {
					// TODO ?
					throw new IllegalArgumentException();
				}
				if (source == DifferenceSource.LEFT) {
					if (featureContains(match.getLeft(), feature, value)) {
						result = match.getLeft();
					} else if (comparison.isThreeWay()) {
						result = match.getOrigin();
					} else {
						result = match.getRight();
					}
				} else {
					if (featureContains(match.getRight(), feature, value)) {
						result = match.getRight();
					} else if (comparison.isThreeWay()) {
						result = match.getOrigin();
					} else {
						// Cannot happen ... for now
						result = match.getLeft();
					}
				}
				break;
			default:
				// no other case for now.
		}
		return result;
	}

	/**
	 * Determines whether the given feature of the given {@link EObject} contains the provided value, while
	 * correctly handling proxies (in other words, in case of proxies, the proxy URI is compared instead of
	 * the objects, which would otherwise lead to false negatives).
	 * 
	 * @param eObject
	 *            The object of which a feature is to be checked
	 * @param feature
	 *            The feature of which containment is to be checked
	 * @param value
	 *            The value which is to be verified in the feature
	 * @return <code>true</code> if the feature contains the given value
	 */
	// public for testing
	public static boolean featureContains(EObject eObject, EStructuralFeature feature, Object value) {
		URI proxyUri = null;
		if (value instanceof EObject) {
			EObject eObjectValue = (EObject)value;
			proxyUri = EcoreUtil.getURI(eObjectValue);
		}

		boolean contains = false;

		for (Object element : getAsList(eObject, feature)) {
			if (element == value) {
				contains = true;
				break;
			}
			if (element != null && element.equals(value)) {
				contains = true;
				break;
			}
			if (proxyUri != null && element instanceof EObject) {
				EObject eObjectElement = (EObject)element;
				if (eObjectElement.eIsProxy() && EcoreUtil.getURI(eObjectElement).equals(proxyUri)) {
					contains = true;
					break;
				}
			}
		}

		return contains;
	}

	/**
	 * Get the value of any difference.
	 * 
	 * @param input
	 *            The difference.
	 * @return the value of the difference.
	 */
	public static Object getValue(Diff input) {
		final CompareSwitch<Object> customSwitch = new CompareSwitch<Object>() {
			@Override
			public Object caseAttributeChange(AttributeChange object) {
				return object.getValue();
			}

			@Override
			public Object caseReferenceChange(ReferenceChange object) {
				return object.getValue();
			}

		};
		return customSwitch.doSwitch(input);
	}

	/**
	 * Get the structural feature of any difference.
	 * 
	 * @param input
	 *            The difference.
	 * @return the structural feature.
	 */
	public static EStructuralFeature getStructuralFeature(Diff input) {
		final CompareSwitch<EStructuralFeature> customSwitch = new CompareSwitch<EStructuralFeature>() {
			@Override
			public EStructuralFeature caseAttributeChange(AttributeChange object) {
				return object.getAttribute();
			}

			@Override
			public EStructuralFeature caseReferenceChange(ReferenceChange object) {
				return object.getReference();
			}

		};
		return customSwitch.doSwitch(input);
	}

}
