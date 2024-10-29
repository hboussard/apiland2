package fr.inrae.act.bagap.apiland.analysis.combination.expression;

public class AndCombinationExpression extends ComplexLogicalCombinationExpression {	

	public AndCombinationExpression(int size) {
		super(size);
	}
	
	@Override
	public boolean evaluation() {
		for(BooleanCombinationExpression lce : getBooleanCombinationExpressions()){
			//System.out.println(lce);
			if(!lce.evaluation()){
				return false;
			}
		}
		return true;
	}


	
}
