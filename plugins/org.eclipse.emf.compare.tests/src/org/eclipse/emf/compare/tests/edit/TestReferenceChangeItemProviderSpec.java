/*******************************************************************************
 * Copyright (c) 2012 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.compare.tests.edit;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.get;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.ReferenceChange;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.Test;

/**
 * @author <a href="mailto:mikael.barbero@obeo.fr">Mikael Barbero</a>
 */
public class TestReferenceChangeItemProviderSpec extends AbstractTestCompareItemProviderAdapter {

	@Test
	public void testGetChildren_AudioVisualItem() throws IOException {
		Match ePackageMatch = TestMatchItemProviderSpec.getEcoreA1_EPackageMatch();

		Collection<?> ePackage_MatchChildren = adaptAsITreItemContentProvider(ePackageMatch).getChildren(
				ePackageMatch);
		Match audioVisualItem_Match = getMatchWithFeatureValue(ePackage_MatchChildren, "name",
				"AudioVisualItem");
		Collection<?> audioVisualItem_MatchChildren = adaptAsITreItemContentProvider(audioVisualItem_Match)
				.getChildren(audioVisualItem_Match);

		ReferenceChange titleReferenceChange = getReferenceChangeWithFeatureValue(
				audioVisualItem_MatchChildren, "name", "title");

		Collection<?> titleReferenceChange_Children = adaptAsITreItemContentProvider(titleReferenceChange)
				.getChildren(titleReferenceChange);

		assertEquals(1, titleReferenceChange_Children.size());
		Object child = get(titleReferenceChange_Children, 0);
		assertTrue(child instanceof ReferenceChange);
		assertEquals(EcorePackage.Literals.ETYPED_ELEMENT__ETYPE, ((ReferenceChange)child).getReference());

		ReferenceChange titledItemReferenceChange = getReferenceChangeWithFeatureValue(
				audioVisualItem_MatchChildren, "name", "TitledItem");
		Collection<?> titledItemReferenceChange_Children = adaptAsITreItemContentProvider(
				titledItemReferenceChange).getChildren(titledItemReferenceChange);
		assertEquals(0, titledItemReferenceChange_Children.size());
	}

	@Test
	public void testGetChildren_Book() throws IOException {
		Match ePackageMatch = TestMatchItemProviderSpec.getEcoreA1_EPackageMatch();

		Collection<?> ePackage_MatchChildren = adaptAsITreItemContentProvider(ePackageMatch).getChildren(
				ePackageMatch);
		Match book_Match = getMatchWithFeatureValue(ePackage_MatchChildren, "name", "Book");
		Collection<?> book_MatchChildren = adaptAsITreItemContentProvider(book_Match).getChildren(book_Match);

		ReferenceChange subtitleReferenceChange = getReferenceChangeWithFeatureValue(book_MatchChildren,
				"name", "subtitle");

		Collection<?> subtitleReferenceChange_Children = adaptAsITreItemContentProvider(
				subtitleReferenceChange).getChildren(subtitleReferenceChange);

		assertEquals(1, subtitleReferenceChange_Children.size());
		Notifier child = (Notifier)get(subtitleReferenceChange_Children, 0);
		assertTrue(child instanceof ReferenceChange);
		assertEquals(EcorePackage.Literals.ETYPED_ELEMENT__ETYPE, ((ReferenceChange)child).getReference());
		assertTrue(adaptAsITreItemContentProvider(child).getChildren(child).isEmpty());

		ReferenceChange titleReferenceChange = getReferenceChangeWithFeatureValue(book_MatchChildren, "name",
				"title");
		Collection<?> titleReferenceChange_Children = adaptAsITreItemContentProvider(titleReferenceChange)
				.getChildren(titleReferenceChange);
		assertEquals(1, titleReferenceChange_Children.size());
		child = (Notifier)get(titleReferenceChange_Children, 0);
		assertTrue(child instanceof ReferenceChange);
		assertEquals(EcorePackage.Literals.ETYPED_ELEMENT__ETYPE, ((ReferenceChange)child).getReference());
		assertTrue(adaptAsITreItemContentProvider(child).getChildren(child).isEmpty());

		ReferenceChange titledItemReferenceChange = getReferenceChangeWithFeatureValue(book_MatchChildren,
				"name", "TitledItem");
		Collection<?> titledItemReferenceChange_Children = adaptAsITreItemContentProvider(
				titledItemReferenceChange).getChildren(titledItemReferenceChange);
		assertEquals(0, titledItemReferenceChange_Children.size());
	}

	@Test
	public void testGetChildren_Borrowable() throws IOException {
		Match ePackageMatch = TestMatchItemProviderSpec.getEcoreA1_EPackageMatch();

		Collection<?> ePackage_MatchChildren = adaptAsITreItemContentProvider(ePackageMatch).getChildren(
				ePackageMatch);
		Match borrowableCategory_Match = getMatchWithFeatureValue(ePackage_MatchChildren, "name",
				"Borrowable");
		Collection<?> borrowable_MatchChildren = adaptAsITreItemContentProvider(borrowableCategory_Match)
				.getChildren(borrowableCategory_Match);

		assertEquals(1, borrowable_MatchChildren.size());
	}

	@Test
	public void testGetChildren_BookCategory() throws IOException {
		Match ePackageMatch = TestMatchItemProviderSpec.getEcoreA1_EPackageMatch();

		Collection<?> ePackage_MatchChildren = adaptAsITreItemContentProvider(ePackageMatch).getChildren(
				ePackageMatch);
		Match bookCategory_Match = getMatchWithFeatureValue(ePackage_MatchChildren, "name", "BookCategory");
		Collection<?> bookCategory_MatchChildren = adaptAsITreItemContentProvider(bookCategory_Match)
				.getChildren(bookCategory_Match);

		ReferenceChange dictionaryReferenceChange = getReferenceChangeWithFeatureValue(
				bookCategory_MatchChildren, "name", "Dictionary");
		ReferenceChange encyclopediaReferenceChange = getReferenceChangeWithFeatureValue(
				bookCategory_MatchChildren, "name", "Encyclopedia");
		ReferenceChange mangaReferenceChange = getReferenceChangeWithFeatureValue(bookCategory_MatchChildren,
				"name", "Manga");
		ReferenceChange manhwaReferenceChange = getReferenceChangeWithFeatureValue(
				bookCategory_MatchChildren, "name", "Manhwa");

		assertTrue(adaptAsITreItemContentProvider(dictionaryReferenceChange).getChildren(
				dictionaryReferenceChange).isEmpty());
		assertTrue(adaptAsITreItemContentProvider(encyclopediaReferenceChange).getChildren(
				encyclopediaReferenceChange).isEmpty());
		assertTrue(adaptAsITreItemContentProvider(mangaReferenceChange).getChildren(mangaReferenceChange)
				.isEmpty());
		assertTrue(adaptAsITreItemContentProvider(manhwaReferenceChange).getChildren(manhwaReferenceChange)
				.isEmpty());
	}

	@Test
	public void testGetChildren_Magazine1() throws IOException {
		Match ePackageMatch = TestMatchItemProviderSpec.getEcoreA1_EPackageMatch();

		Collection<?> ePackage_MatchChildren = adaptAsITreItemContentProvider(ePackageMatch).getChildren(
				ePackageMatch);

		Collection<?> magazineChildren = null;
		for (ReferenceChange referenceChange : filter(ePackage_MatchChildren, ReferenceChange.class)) {
			EClass eClass = (EClass)referenceChange.getValue();
			if ("Magazine".equals(eClass.getName())
					&& "CirculatingItem".equals(eClass.getESuperTypes().get(0).getName())) {
				magazineChildren = adaptAsITreItemContentProvider(referenceChange).getChildren(
						referenceChange);
				assertEquals(3, magazineChildren.size());
				break;
			}
		}
		ReferenceChange magazineSuperTypeChange = getReferenceChangeWithFeatureValue(magazineChildren,
				"name", "CirculatingItem");
		assertTrue(adaptAsITreItemContentProvider(magazineSuperTypeChange).getChildren(
				magazineSuperTypeChange).isEmpty());

		ReferenceChange magazineSFChange1 = getReferenceChangeWithFeatureValue(magazineChildren, "name",
				"pages");
		assertEquals(1, adaptAsITreItemContentProvider(magazineSFChange1).getChildren(magazineSFChange1)
				.size());

		ReferenceChange magazineSFChange2 = getReferenceChangeWithFeatureValue(magazineChildren, "name",
				"title");
		assertEquals(1, adaptAsITreItemContentProvider(magazineSFChange2).getChildren(magazineSFChange2)
				.size());
	}

	@Test
	public void testGetChildren_Magazine2() throws IOException {
		Match ePackageMatch = TestMatchItemProviderSpec.getEcoreA1_EPackageMatch();

		Collection<?> ePackage_MatchChildren = adaptAsITreItemContentProvider(ePackageMatch).getChildren(
				ePackageMatch);

		Collection<?> magazineChildren = null;
		for (ReferenceChange referenceChange : filter(ePackage_MatchChildren, ReferenceChange.class)) {
			EClass eClass = (EClass)referenceChange.getValue();
			if ("Magazine".equals(eClass.getName())
					&& "Periodical".equals(eClass.getESuperTypes().get(0).getName())) {
				magazineChildren = adaptAsITreItemContentProvider(referenceChange).getChildren(
						referenceChange);
				assertEquals(1, magazineChildren.size());
				break;
			}
		}
		ReferenceChange magazineSuperTypeChange = getReferenceChangeWithFeatureValue(magazineChildren,
				"name", "Periodical");
		assertTrue(adaptAsITreItemContentProvider(magazineSuperTypeChange).getChildren(
				magazineSuperTypeChange).isEmpty());
	}

	@Test
	public void testGetChildren_Periodical() throws IOException {
		Match ePackageMatch = TestMatchItemProviderSpec.getEcoreA1_EPackageMatch();

		Collection<?> ePackage_MatchChildren = adaptAsITreItemContentProvider(ePackageMatch).getChildren(
				ePackageMatch);
		ReferenceChange periodical_ReferenceChange = getReferenceChangeWithFeatureValue(
				ePackage_MatchChildren, "name", "Periodical");
		Collection<?> periodical_ReferenceChangeChildren = adaptAsITreItemContentProvider(
				periodical_ReferenceChange).getChildren(periodical_ReferenceChange);

		assertEquals(3, periodical_ReferenceChangeChildren.size());

		ReferenceChange issuesPerYearChange = getReferenceChangeWithFeatureValue(
				periodical_ReferenceChangeChildren, "name", "issuesPerYear");

		ReferenceChange itemChange = getReferenceChangeWithFeatureValue(periodical_ReferenceChangeChildren,
				"name", "Item");
		ReferenceChange titledItemChange = getReferenceChangeWithFeatureValue(
				periodical_ReferenceChangeChildren, "name", "TitledItem");

		Collection<?> issuesPerYearChildren = adaptAsITreItemContentProvider(issuesPerYearChange)
				.getChildren(issuesPerYearChange);
		assertEquals(1, issuesPerYearChildren.size());
		ReferenceChange issuePerYearChild = (ReferenceChange)issuesPerYearChildren.iterator().next();
		assertEquals(EcorePackage.Literals.ETYPED_ELEMENT__ETYPE, issuePerYearChild.getReference());

		assertTrue(adaptAsITreItemContentProvider(itemChange).getChildren(itemChange).isEmpty());
		assertTrue(adaptAsITreItemContentProvider(titledItemChange).getChildren(titledItemChange).isEmpty());
	}

	@Test
	public void testGetChildren_Person() throws IOException {
		Match ePackageMatch = TestMatchItemProviderSpec.getEcoreA1_EPackageMatch();

		Collection<?> ePackage_MatchChildren = adaptAsITreItemContentProvider(ePackageMatch).getChildren(
				ePackageMatch);
		Match person_Match = getMatchWithFeatureValue(ePackage_MatchChildren, "name", "Person");
		Collection<?> person_MatchChildren = adaptAsITreItemContentProvider(person_Match).getChildren(
				person_Match);

		assertEquals(3, person_MatchChildren.size());

		ReferenceChange issuesPerYearChange = getReferenceChangeWithFeatureValue(person_MatchChildren,
				"name", "firstName");
		Collection<?> firstNameChildren = adaptAsITreItemContentProvider(issuesPerYearChange).getChildren(
				issuesPerYearChange);
		assertEquals(1, firstNameChildren.size());
		ReferenceChange firstNameChild = (ReferenceChange)firstNameChildren.iterator().next();
		assertEquals(EcorePackage.Literals.ETYPED_ELEMENT__ETYPE, firstNameChild.getReference());

		ReferenceChange fullNameChange = getReferenceChangeWithFeatureValue(person_MatchChildren, "name",
				"fullName");
		Collection<?> fullNameChildren = adaptAsITreItemContentProvider(fullNameChange).getChildren(
				fullNameChange);
		assertEquals(1, fullNameChildren.size());
		ReferenceChange fullNameChild = (ReferenceChange)fullNameChildren.iterator().next();
		assertEquals(EcorePackage.Literals.ETYPED_ELEMENT__ETYPE, fullNameChild.getReference());

		ReferenceChange lastNameChange = getReferenceChangeWithFeatureValue(person_MatchChildren, "name",
				"lastName");
		Collection<?> lastNameChildren = adaptAsITreItemContentProvider(lastNameChange).getChildren(
				lastNameChange);
		assertEquals(2, lastNameChildren.size());
		Iterator<?> lastNameiterator = lastNameChildren.iterator();
		ReferenceChange lastName1stChild = (ReferenceChange)lastNameiterator.next();
		AttributeChange lastName2ndChild = (AttributeChange)lastNameiterator.next();
		assertEquals(EcorePackage.Literals.ETYPED_ELEMENT__ETYPE, lastName1stChild.getReference());
		assertEquals(EcorePackage.Literals.ENAMED_ELEMENT__NAME, lastName2ndChild.getAttribute());
	}

	@Test
	public void testGetChildren_TitledItem() throws IOException {
		Match ePackageMatch = TestMatchItemProviderSpec.getEcoreA1_EPackageMatch();

		Collection<?> ePackage_MatchChildren = adaptAsITreItemContentProvider(ePackageMatch).getChildren(
				ePackageMatch);

		ReferenceChange titledItem_ReferenceChange = getReferenceChangeWithFeatureValue(
				ePackage_MatchChildren, "name", "TitledItem");
		Collection<?> titledItem_ReferenceChangeChildren = adaptAsITreItemContentProvider(
				titledItem_ReferenceChange).getChildren(titledItem_ReferenceChange);
		assertEquals(1, titledItem_ReferenceChangeChildren.size());

		ReferenceChange title_Change = (ReferenceChange)titledItem_ReferenceChangeChildren.iterator().next();
		Collection<?> title_ChangeChildren = adaptAsITreItemContentProvider(title_Change).getChildren(
				title_Change);
		assertEquals(1, title_ChangeChildren.size());

		ReferenceChange eType_Change = (ReferenceChange)title_ChangeChildren.iterator().next();
		assertTrue(adaptAsITreItemContentProvider(eType_Change).getChildren(eType_Change).isEmpty());
	}
}