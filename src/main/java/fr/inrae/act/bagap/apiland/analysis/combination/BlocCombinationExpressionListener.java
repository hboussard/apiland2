package fr.inrae.act.bagap.apiland.analysis.combination;

import org.antlr.v4.runtime.misc.NotNull;

import fr.inrae.act.bagap.apiland.analysis.combination.CombinationParser.BlocContext;
import fr.inrae.act.bagap.apiland.analysis.combination.expression.SimpleCombinationExpression;

public class BlocCombinationExpressionListener extends CombinationExpressionListener {
	
	public BlocCombinationExpressionListener(){
		super();
	}
	
	@Override 
	public void enterBloc(@NotNull BlocContext ctx) {
		System.out.println("enterBloc "+ctx.getText());
	}
	
	@Override 
	public void exitBloc(@NotNull BlocContext ctx) { 
		System.out.println("exitBloc "+ctx.getText());
		expression = new SimpleCombinationExpression(ctx.getText(), localNames.toArray(new String[localNames.size()]));
	}	
	
}
