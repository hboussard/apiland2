package fr.inrae.act.bagap.apiland.analysis.distance;

import fr.inrae.act.bagap.apiland.analysis.combination.expression.CombinationExpression;

public class CombinationExpressionDistanceFunction implements DistanceFunction {

	private CombinationExpression expression;
	
	public CombinationExpressionDistanceFunction(CombinationExpression expression, double distanceMax) {
		this.expression = expression;
		this.expression.setValue("dmax", distanceMax);
	}

	@Override
	public double interprete(double distance) {
		expression.setValue("distance", distance);
		return expression.evaluate();
	}

}
