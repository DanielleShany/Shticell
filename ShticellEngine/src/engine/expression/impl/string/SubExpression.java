package engine.expression.impl.string;

import engine.expression.api.Expression;
import engine.sheet.api.CellType;
import engine.sheet.api.EffectiveValue;
import engine.sheet.impl.EffectiveValueImpl;

public class SubExpression implements Expression {
    private Expression source;
    private Expression startIndex;
    private Expression endIndex;
    public SubExpression(Expression source, Expression startIndex, Expression endIndex) {
        this.source = source;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }
    public EffectiveValue eval() {
        EffectiveValue sourceString = source.eval();
        EffectiveValue start = startIndex.eval();
        EffectiveValue end = endIndex.eval();
        // do some checking... error handling...
        String result = sourceString
                .extractValueWithExpectation(String.class)
                .substring(
                        (int) Math.floor(start.extractValueWithExpectation(Double.class)), // Ensure start is an int
                        (int) Math.floor(end.extractValueWithExpectation(Double.class))   // Ensure end is an int
                );

        return new EffectiveValueImpl(CellType.STRING, result);
    }

}