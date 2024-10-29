package fr.inrae.act.bagap.apiland.analysis.combination.expression;

public interface CombinationExpression {

	void init();
	
	void setValue(String name, double value);
	
	double evaluate();
	
	boolean addCombinationExpression(CombinationExpression ce);
	
}
