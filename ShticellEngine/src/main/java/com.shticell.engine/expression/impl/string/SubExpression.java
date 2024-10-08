package com.shticell.engine.expression.impl.string;

import com.shticell.engine.expression.api.Expression;
import com.shticell.engine.cell.impl.CellType;
import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.EffectiveValueImpl;

import java.util.ArrayList;
import java.util.List;

public class SubExpression extends StringExpression {
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

        try {
            String result = sourceString
                    .extractValueWithExpectation(String.class)
                    .substring(
                            (int) Math.floor(start.extractValueWithExpectation(Double.class) ), // Ensure start is an int
                            (int) Math.floor(end.extractValueWithExpectation(Double.class))   // Ensure end is an int
                    );

            return new EffectiveValueImpl(CellType.STRING, result);
        }
        catch (Exception e) {
            return new EffectiveValueImpl((CellType.UNKNOWN), "!UNDEFINED!" );
        }
    }

    @Override
    public CellType getFunctionResultType () {return CellType.STRING; }

    @Override
    public List<Expression> getExpressions(){
        List<Expression> expressions = new ArrayList<Expression>();
        expressions.add(source);
        expressions.add(startIndex);
        expressions.add(endIndex);
        return expressions;
    }



}
