
public class StmtExpression extends Statement {
    final Expression expression;

    StmtExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Object execute(TablaSimbolos tablaSimbolos) {

        return expression.solve(tablaSimbolos);
    }
}
