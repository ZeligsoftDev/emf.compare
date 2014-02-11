/*******************************************************************************
 * Copyright (c) 2014 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.emf.compare.rcp.internal.match;

import static com.google.common.collect.Iterables.filter;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.IMatchEngine.Factory;
import org.eclipse.emf.compare.rcp.EMFCompareRCPPlugin;
import org.eclipse.emf.compare.rcp.internal.engine.IItemDescriptor;
import org.eclipse.emf.compare.rcp.internal.engine.IItemRegistry;
import org.eclipse.emf.compare.rcp.internal.engine.impl.AbstractItemDescriptor;
import org.eclipse.emf.compare.rcp.internal.engine.impl.FactoryDescriptor;
import org.eclipse.emf.compare.rcp.internal.engine.impl.ItemUtil;
import org.eclipse.emf.compare.rcp.internal.preferences.EMFComparePreferences;
import org.eclipse.emf.compare.scope.IComparisonScope;

/**
 * MatchEnginefactoryRegistry that wrap an IItemRegistry<IMatchEngine.Factory>.
 * 
 * @author <a href="mailto:arthur.daussy@obeo.fr">Arthur Daussy</a>
 */
public class MatchEnginefactoryRegistryWrapper implements IMatchEngine.Factory.Registry {

	/** EMPTY_STRING. */
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	/** Instance of the registry that need to be wrapped. */
	private IItemRegistry<IMatchEngine.Factory> registry;

	/**
	 * Constructor.
	 * 
	 * @param registy
	 *            {@link MatchEnginefactoryRegistryWrapper#registry}
	 */
	public MatchEnginefactoryRegistryWrapper(IItemRegistry<Factory> registy) {
		super();
		this.registry = registy;
	}

	/**
	 * Transform the IItemRegistry<IMatchEngine.Factory> to collection to be more use full.
	 * 
	 * @return Collection<IMatchEngine.Factory>
	 */
	private Collection<IMatchEngine.Factory> getFactories() {
		Function<IItemDescriptor<Factory>, Factory> toFactoryFunction = AbstractItemDescriptor
				.getItemFunction();
		return Collections2.transform(registry.getItemDescriptors(), toFactoryFunction);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.compare.match.IMatchEngine.Factory.Registry#getHighestRankingMatchEngine(java.lang.Object)
	 */
	public IMatchEngine.Factory getHighestRankingMatchEngineFactory(IComparisonScope scope) {
		IMatchEngine.Factory result = null;

		Iterator<IMatchEngine.Factory> matchEngineFactories = Iterators.filter(getMatchEngineFactories(scope)
				.iterator(), new DisableEngineFilter(getDisabledEngines()));
		while (matchEngineFactories.hasNext()) {
			IMatchEngine.Factory engineFactory = matchEngineFactories.next();
			if (result == null || engineFactory.getRanking() > result.getRanking()) {
				result = engineFactory;
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.compare.match.IMatchEngine.Factory.Registry#getMatchEngines(org.eclipse.emf.compare.scope.IComparisonScope)
	 */
	public List<IMatchEngine.Factory> getMatchEngineFactories(IComparisonScope scope) {
		Iterable<IMatchEngine.Factory> matchEngineFactories = filter(getFactories(), Predicates.and(
				isMatchEngineFactoryActivable(scope), new DisableEngineFilter(getDisabledEngines())));
		return Lists.newArrayList(matchEngineFactories);
	}

	/**
	 * Returns a predicate that represents the activation condition based on the scope.
	 * 
	 * @param scope
	 *            The scope on which the group provider will be applied.
	 * @return A predicate that represents the activation condition based on the scope.
	 */
	private static Predicate<IMatchEngine.Factory> isMatchEngineFactoryActivable(final IComparisonScope scope) {
		return new Predicate<IMatchEngine.Factory>() {
			/**
			 * {@inheritDoc}
			 * 
			 * @see com.google.common.base.Predicate#apply(java.lang.Object)
			 */
			public boolean apply(IMatchEngine.Factory factory) {
				return factory.isMatchEngineFactoryFor(scope);
			}
		};
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.compare.match.IMatchEngine.Factory.Registry#add(org.eclipse.emf.compare.match.IMatchEngine)
	 */
	public IMatchEngine.Factory add(IMatchEngine.Factory filter) {
		Preconditions.checkNotNull(filter);
		IItemDescriptor<Factory> factory = registry.add(new FactoryDescriptor<IMatchEngine.Factory>(
				EMPTY_STRING, EMPTY_STRING, filter.getRanking(), filter.getMatchEngine().getClass()
						.getCanonicalName(), filter));
		if (factory != null) {
			return factory.getItem();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.compare.match.IMatchEngine.Factory.Registry#remove(java.lang.String)
	 */
	public IMatchEngine.Factory remove(String className) {
		IItemDescriptor<Factory> remove = registry.remove(className);
		if (remove != null) {
			return remove.getItem();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.emf.compare.match.IMatchEngine.Factory.Registry#clear()
	 */
	public void clear() {
		registry.clear();
	}

	/**
	 * Get the {@link IMatchEngine.Factory} that have been disabled.
	 * 
	 * @return Collection of {@link IMatchEngine.Factory} that have been disabled
	 */
	private Collection<IMatchEngine.Factory> getDisabledEngines() {
		Collection<IItemDescriptor<Factory>> result = ItemUtil.getItemsDescriptor(registry,
				EMFComparePreferences.MATCH_ENGINE_DISABLE_ENGINES, EMFCompareRCPPlugin.getDefault()
						.getEMFComparePreferences());
		if (result == null) {
			result = Collections.emptyList();
		}
		Function<IItemDescriptor<Factory>, Factory> toFactoryFunction = AbstractItemDescriptor
				.getItemFunction();
		return Collections2.transform(result, toFactoryFunction);
	}

	/**
	 * Predicate that filter out disabled predicates.
	 * 
	 * @author <a href="mailto:arthur.daussy@obeo.fr">Arthur Daussy</a>
	 */
	private class DisableEngineFilter implements Predicate<IMatchEngine.Factory> {

		/** All disable factories. */
		private Collection<IMatchEngine.Factory> disabled;

		/**
		 * Constructor.
		 * 
		 * @param disabled
		 *            {@link DisableEngineFilter#disabled}
		 */
		public DisableEngineFilter(Collection<Factory> disabled) {
			super();
			this.disabled = disabled;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean apply(Factory input) {
			return !disabled.contains(input);
		}
	}
}
