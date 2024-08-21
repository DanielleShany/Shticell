package engine.expression.impl.numeric;

import engine.expression.api.Expression;
import engine.sheet.cell.api.CellType;
import engine.sheet.cell.api.EffectiveValue;
import engine.sheet.cell.impl.EffectiveValueImpl;

public class TimesExpression extends NumericExpression {
    private final Expression left;
    private final Expression right;
    public TimesExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }
    @Override
    public EffectiveValue eval() {
        EffectiveValue leftValue = left.eval();
        EffectiveValue rightValue = right.eval();
        // do some checking... error handling...
        double result = leftValue.extractValueWithExpectation(Double.class) * rightValue.extractValueWithExpectation(Double.class);

        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

}
