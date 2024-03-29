

public class ExprGrouping extends Expression {
    final Expression expression;

    ExprGrouping(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Object solve(TablaSimbolos tablaSimbolos) {
        return expression.solve(tablaSimbolos);
    }
}
