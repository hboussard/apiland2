package fr.inrae.act.bagap.apiland.analysis.combination;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import fr.inrae.act.bagap.apiland.analysis.combination.CombinationParser.EvaluateContext;
import fr.inrae.act.bagap.apiland.analysis.distance.CombinationExpressionDistanceFunction;
import fr.inrae.act.bagap.apiland.analysis.tab.CombinationExpressionPixel2PixelTabCalculation;

public class CombinationExpressionFactory {

	private static ConcreteCombinationExpressionListener parse(String combination) throws RecognitionException {
		ConcreteCombinationExpressionListener listener = null;
		
		CombinationParser parser = new CombinationParser(new CommonTokenStream(new CombinationLexer(new ANTLRInputStream(combination)))); 
		EvaluateContext context = parser.evaluate();
			
		listener = new ConcreteCombinationExpressionListener();
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(listener, context);
		
		return listener;
	}
	
	public static CombinationExpressionPixel2PixelTabCalculation createPixel2PixelTabCalculation(float[] outAscii, String combination, int noDataValue, String[] names, float[]... inAscii){
		ConcreteCombinationExpressionListener listener = parse(combination);
		
		return new CombinationExpressionPixel2PixelTabCalculation(outAscii, listener.getCombinationExpression(), noDataValue, names, inAscii);
	}
	
	public static CombinationExpressionDistanceFunction createDistanceFunction(String combination, double distanceMax){
		ConcreteCombinationExpressionListener listener = parse(combination);
		
		return new CombinationExpressionDistanceFunction(listener.getCombinationExpression(), distanceMax);
		//return listener.getDistanceFunction(distanceMax);
	}
	
}
