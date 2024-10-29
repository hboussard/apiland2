package fr.inrae.act.bagap.apiland.analysis.combination.expression;

import java.util.Set;
import java.util.TreeSet;

import fr.inrae.act.bagap.apiland.raster.Raster;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class SimpleCombinationExpression implements CombinationExpression {
	
	private String exp;
	
	private Set<String> names;
	
	private Expression expression;
	
	private boolean hasnodata;
	
	public SimpleCombinationExpression(String exp, String... names){
		this.exp = exp;
		this.names = new TreeSet<String>();
		for(String name : names){
			this.names.add(name);
		}
		hasnodata = false;
		expression = new ExpressionBuilder(exp).variables(names).build();
	}
	
	public String toString(){
		/*
		StringBuilder sb = new StringBuilder();
		for(String n : names){
			sb.append(n+" ");
		}
		return sb.toString();
		*/
		return exp;
	}
	
	protected Expression getExpression(){
		return expression;
	}

	@Override
	public void setValue(String name, double value){
		if(names.contains(name)){
			if(value == Raster.getNoDataValue()){
				hasnodata = true;
			}
			expression.setVariable(name, value);
		}
	}
	
	@Override
	public double evaluate() {
		try{
			if(!hasnodata){
				return expression.evaluate();
			}else{
				hasnodata = false;
				return Raster.getNoDataValue();
			}
			
		}catch(ArithmeticException e){
			return Raster.getNoDataValue();
		}
	}

	@Override
	public boolean addCombinationExpression(CombinationExpression ce) {
		return false;
	}

	@Override
	public void init() {
		hasnodata = false;
	}
	
}
