

public class StmtIf extends Statement {
    final Expression condition;
    final Statement thenBranch;
    final Statement elseBranch;

    StmtIf(Expression condition, Statement thenBranch, Statement elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public Object execute(TablaSimbolos tablaSimbolos) {

        // Resolvemos la condición
        Object condicion_solve = condition.solve(tablaSimbolos);

        if(!(condicion_solve instanceof Boolean))
            throw new RuntimeException("Error: Condición inválida");

        if((boolean) condicion_solve)
            return thenBranch.execute(tablaSimbolos);

        else if(elseBranch != null)
            return elseBranch.execute(tablaSimbolos);

        else
            return null;
     }
}
