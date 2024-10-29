package fr.inrae.act.bagap.apiland.analysis.combination.expression;

public class OrCombinationExpression extends ComplexLogicalCombinationExpression {	
	
	public OrCombinationExpression(int size) {
		super(size);
	}
	
	@Override
	public boolean evaluation() {
		for(BooleanCombinationExpression lce : getBooleanCombinationExpressions()){
			if(lce.evaluation()){
				return true;
			}
		}
		return false;
	}

}