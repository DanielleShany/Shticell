package com.shticell.engine.expression.impl.numeric;

import com.shticell.engine.expression.api.Expression;
import com.shticell.engine.cell.impl.CellType;
import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.EffectiveValueImpl;

import java.util.ArrayList;
import java.util.List;

public class DivideExpression extends NumericExpression {
    private final Expression left;
    private final Expression right;
    private boolean divisible;
    public DivideExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
        divisible = true;
    }

    @Override
    public EffectiveValue eval() {
        EffectiveValue leftValue = left.eval();
        EffectiveValue rightValue = right.eval();

        try {
            double divider = rightValue.extractValueWithExpectation(Double.class);
            if (divider == 0)
                return new EffectiveValueImpl(CellType.UNKNOWN, "NaN");

            double result = leftValue.extractValueWithExpectation(Double.class) / rightValue.extractValueWithExpectation(Double.class);
            return new EffectiveValueImpl(CellType.NUMERIC, result);
        }
        catch (Exception e)
        {
            divisible = false;
            return new EffectiveValueImpl(CellType.UNKNOWN, "NaN");
        }

    }

    @Override
    public CellType getFunctionResultType() {return divisible? CellType.NUMERIC: CellType.STRING; }

    public List<Expression> getExpressions() {
        List<Expression> expressions = new ArrayList<Expression>();
        expressions.add(left);
        expressions.add(right);
        return expressions;
    }


}
