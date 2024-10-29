package fr.inrae.act.bagap.apiland.analysis.tab;

import fr.inrae.act.bagap.apiland.analysis.combination.expression.CombinationExpression;

public class CombinationExpressionPixel2PixelTabCalculation extends Pixel2PixelTabCalculation {

	private String[] names;
	
	private CombinationExpression expression;
	
	private int noDataValue;
	
	public CombinationExpressionPixel2PixelTabCalculation(float[] outTab, CombinationExpression expression, int noDataValue, String[] names, float[]... inTab) {
		super(outTab, inTab);
		this.names = names;
		this.expression = expression;
		this.noDataValue = noDataValue;
	}

	@Override
	protected float doTreat(float[] v) {
		
		expression.init();
		
		double value;
		boolean hasNodata = false;
		for(int i=0; i<names.length; i++){
			value = v[i];
			if(value == noDataValue){
				hasNodata = true;
			}
			expression.setValue(names[i], value);
		}
		if(!hasNodata){
			return (float) expression.evaluate();
		}else{
			return noDataValue;
		}
	}

}
