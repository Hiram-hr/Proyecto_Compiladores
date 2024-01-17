
public class StmtPrint extends Statement {
    final Expression expression;

    StmtPrint(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Object execute(TablaSimbolos tablaSimbolos) {

        // Resolvemos la expresión
        Object expresion = expression.solve(tablaSimbolos);

        System.out.println(expresion.toString());
        return null;
    }
}
