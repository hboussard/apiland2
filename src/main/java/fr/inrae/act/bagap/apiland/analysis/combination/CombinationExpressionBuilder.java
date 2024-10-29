package fr.inrae.act.bagap.apiland.analysis.combination;

import java.util.HashSet;
import java.util.Set;

import org.antlr.v4.runtime.misc.NotNull;

import fr.inrae.act.bagap.apiland.analysis.combination.CombinationParser.BlocContext;
import fr.inrae.act.bagap.apiland.analysis.combination.expression.AndCombinationExpression;
import fr.inrae.act.bagap.apiland.analysis.combination.expression.CombinationExpression;
import fr.inrae.act.bagap.apiland.analysis.combination.expression.ConditionCombinationExpression;
import fr.inrae.act.bagap.apiland.analysis.combination.expression.EqualZeroCombinationExpression;
import fr.inrae.act.bagap.apiland.analysis.combination.expression.InfEqualZeroCombinationExpression;
import fr.inrae.act.bagap.apiland.analysis.combination.expression.InfZeroCombinationExpression;
import fr.inrae.act.bagap.apiland.analysis.combination.expression.NegativeCombinationExpression;
import fr.inrae.act.bagap.apiland.analysis.combination.expression.NotZeroCombinationExpression;
import fr.inrae.act.bagap.apiland.analysis.combination.expression.OrCombinationExpression;
import fr.inrae.act.bagap.apiland.analysis.combination.expression.SimpleCombinationExpression;
import fr.inrae.act.bagap.apiland.analysis.combination.expression.SupEqualZeroCombinationExpression;
import fr.inrae.act.bagap.apiland.analysis.combination.expression.SupZeroCombinationExpression;

public class CombinationExpressionBuilder extends CombinationBaseListener {

	private Set<String> localNames;
	
	private CombinationExpression expression;
	
	private String boolExpression, boolOp;
	
	public CombinationExpressionBuilder(){ 
		localNames = new HashSet<String>();
	}
	
	public CombinationExpression build(){
		return this.expression;
	}
	
	private void addCombinationExpression(CombinationExpression ce){
		if(this.expression == null){
			this.expression = ce;
		}else{
			this.expression.addCombinationExpression(ce);
		}
	}
	
	@Override 
	public void enterConditional(@NotNull CombinationParser.ConditionalContext ctx) {  
		//System.out.println("enterConditionnal "+ctx.getText());
		addCombinationExpression(new ConditionCombinationExpression());
	}
	
	@Override 
	public void enterName(@NotNull CombinationParser.NameContext ctx) { 
		//System.out.println("enterName "+ctx.getText());
		localNames.add(ctx.getText());
	}
	
	@Override 
	public void exitBloc(@NotNull BlocContext ctx) { 
		//System.out.println("exitBloc "+ctx.getText());
		addCombinationExpression(new SimpleCombinationExpression(ctx.getText(), localNames.toArray(new String[localNames.size()])));
		localNames.clear();
	}
	
	@Override 
	public void exitLeftoperation(@NotNull CombinationParser.LeftoperationContext ctx) { 
		//System.out.println("exitLeftoperation "+ctx.getText());
		boolExpression = ctx.getText();
	}
	
	@Override 
	public void enterBoolop(@NotNull CombinationParser.BoolopContext ctx) {
		//System.out.println("enterBoolop "+ctx.getText());
		boolOp = ctx.getText();	
	}
	
	@Override 
	public void exitRightoperation(@NotNull CombinationParser.RightoperationContext ctx) {
		//System.out.println("exitRightoperation "+ctx.getText());
		boolExpression = boolExpression+"-"+ctx.getText();
	}
	
	@Override 
	public void exitBoolterm(@NotNull CombinationParser.BooltermContext ctx) {
		//System.out.println("exitBoolterm "+ctx.getText());
		switch(boolOp){
		case ">" : addCombinationExpression(new SupZeroCombinationExpression(boolExpression, localNames.toArray(new String[localNames.size()]))); break;
		case "<" : addCombinationExpression(new InfZeroCombinationExpression(boolExpression, localNames.toArray(new String[localNames.size()]))); break;
		case ">=" : addCombinationExpression(new SupEqualZeroCombinationExpression(boolExpression, localNames.toArray(new String[localNames.size()]))); break;
		case "<=" : addCombinationExpression(new InfEqualZeroCombinationExpression(boolExpression, localNames.toArray(new String[localNames.size()]))); break;
		case "==" : addCombinationExpression(new EqualZeroCombinationExpression(boolExpression, localNames.toArray(new String[localNames.size()]))); break;
		case "!=" : addCombinationExpression(new NotZeroCombinationExpression(boolExpression, localNames.toArray(new String[localNames.size()]))); break;
		default : throw new IllegalArgumentException (boolOp);
		}
	}
	
	@Override 
	public void enterBooltermnegation(@NotNull CombinationParser.BooltermnegationContext ctx) { 
		//System.out.println("enterBooltermnegation "+ctx.getText());
		addCombinationExpression(new NegativeCombinationExpression());
	}
	
	@Override 
	public void enterAndgenericboolterm(@NotNull CombinationParser.AndgenericbooltermContext ctx) {  
		//System.out.println("enterAndgenericboolterm "+ctx.getText()+" "+ctx.genericboolterm().size());
		addCombinationExpression(new AndCombinationExpression(ctx.genericboolterm().size()));
	}
	
	@Override 
	public void enterOrgenericboolterm(@NotNull CombinationParser.OrgenericbooltermContext ctx) {  
		//System.out.println("enterOrgenericboolterm "+ctx.getText()+" "+ctx.genericboolterm().size());
		addCombinationExpression(new OrCombinationExpression(ctx.genericboolterm().size()));
	}
	
	
}
