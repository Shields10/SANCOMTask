package com.ppwallet.rules;

public interface RulesFactory {
	public Rules createRule(String ruleName) throws Exception;
	}
