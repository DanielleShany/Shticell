package engine.expression.impl.numeric;

import engine.expression.api.Expression;
import engine.sheet.cell.impl.CellType;
import engine.sheet.cell.api.EffectiveValue;
import engine.sheet.cell.impl.EffectiveValueImpl;

public class AbsExpression extends NumericExpression {
    private final Expression expression;
    public AbsExpression(Expression expression) {
        this.expression = expression;
    }
    public EffectiveValue eval() {
        EffectiveValue value = expression.eval();
        // do some checking... error handling...
        double result = Math.abs(value.extractValueWithExpectation(Double.class));

        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

}
