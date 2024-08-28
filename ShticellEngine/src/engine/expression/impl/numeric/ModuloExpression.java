package engine.expression.impl.numeric;

import engine.expression.api.Expression;
import engine.cell.impl.CellType;
import engine.cell.api.EffectiveValue;
import engine.cell.impl.EffectiveValueImpl;

import java.util.ArrayList;
import java.util.List;

public class ModuloExpression extends NumericExpression {
    private final Expression left;
    private final Expression right;

    public ModuloExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    @Override
    public EffectiveValue eval() {
        EffectiveValue leftValue = left.eval();
        EffectiveValue rightValue = right.eval();

        try {
            double result = leftValue.extractValueWithExpectation(Integer.class) % rightValue.extractValueWithExpectation(Integer.class);
            return new EffectiveValueImpl(CellType.NUMERIC, result);
        }
        catch (Exception e) {
            return new EffectiveValueImpl(CellType.UNKNOWN, "NaN");
        }

    }
    @Override
    public List<Expression> getExpressions() {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(left);
        expressions.add(right);
        return expressions;
    }
}
