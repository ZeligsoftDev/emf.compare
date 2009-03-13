/*
 * generated by Xtext
 */
package org.eclipse.emf.compare.epatch.dsl.parser.packrat.consumers;

import org.eclipse.emf.ecore.EClassifier;

import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.Alternatives;
import org.eclipse.xtext.RuleCall;

import org.eclipse.xtext.parser.packrat.consumers.IElementConsumer;
import org.eclipse.xtext.parser.packrat.consumers.INonTerminalConsumer;
import org.eclipse.xtext.parser.packrat.consumers.INonTerminalConsumerConfiguration;
import org.eclipse.xtext.parser.packrat.consumers.ITerminalConsumer;
import org.eclipse.xtext.parser.packrat.consumers.NonTerminalConsumer;

import org.eclipse.emf.compare.epatch.dsl.services.EpatchGrammarAccess.NamedObjectElements;

public final class EpatchNamedObjectConsumer extends NonTerminalConsumer {

	private NamedObjectElements rule;

	private INonTerminalConsumer createdObjectConsumer;

	private INonTerminalConsumer objectRefConsumer;

	private IElementConsumer alternatives$1$Consumer;

	private IElementConsumer ruleCall$2$Consumer;

	private IElementConsumer ruleCall$3$Consumer;

	protected class Alternatives$1$Consumer extends AlternativesConsumer {

		protected Alternatives$1$Consumer(final Alternatives alternatives) {
			super(alternatives);
		}

		@Override
		protected void doGetConsumers(ConsumerAcceptor acceptor) {
			acceptor.accept(ruleCall$2$Consumer);
			acceptor.accept(ruleCall$3$Consumer);
		}
	}

	protected class RuleCall$2$Consumer extends ElementConsumer<RuleCall> {

		protected RuleCall$2$Consumer(final RuleCall ruleCall) {
			super(ruleCall);
		}

		@Override
		protected int doConsume(boolean optional) throws Exception {
			return consumeNonTerminal(objectRefConsumer, null, false, false, false, getElement(), optional);
		}
	}

	protected class RuleCall$3$Consumer extends ElementConsumer<RuleCall> {

		protected RuleCall$3$Consumer(final RuleCall ruleCall) {
			super(ruleCall);
		}

		@Override
		protected int doConsume(boolean optional) throws Exception {
			return consumeNonTerminal(createdObjectConsumer, null, false, false, false, getElement(),
					optional);
		}
	}

	public EpatchNamedObjectConsumer(INonTerminalConsumerConfiguration configuration,
			ITerminalConsumer[] hiddenTokens) {
		super(configuration, hiddenTokens);
	}

	@Override
	protected int doConsume() throws Exception {
		return alternatives$1$Consumer.consume();
	}

	public NamedObjectElements getRule() {
		return rule;
	}

	public void setRule(NamedObjectElements rule) {
		this.rule = rule;

		alternatives$1$Consumer = new Alternatives$1$Consumer(rule.getAlternatives());
		ruleCall$2$Consumer = new RuleCall$2$Consumer(rule.getObjectRefParserRuleCall_0());
		ruleCall$3$Consumer = new RuleCall$3$Consumer(rule.getCreatedObjectParserRuleCall_1());
	}

	@Override
	protected AbstractRule getGrammarElement() {
		return getRule().getRule();
	}

	@Override
	protected EClassifier getDefaultType() {
		return getGrammarElement().getType().getClassifier();
	}

	public void setCreatedObjectConsumer(INonTerminalConsumer createdObjectConsumer) {
		this.createdObjectConsumer = createdObjectConsumer;
	}

	public void setObjectRefConsumer(INonTerminalConsumer objectRefConsumer) {
		this.objectRefConsumer = objectRefConsumer;
	}

}
