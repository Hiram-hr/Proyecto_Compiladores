
public class StmtLoop extends Statement {
    final Expression condition;
    final Statement body;

    StmtLoop(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public Object execute(TablaSimbolos tablaSimbolos) {

        // Resolvemos la condición
        Object condicion_solve = condition.solve(tablaSimbolos);
        
        if(!(condicion_solve instanceof Boolean))
            throw new RuntimeException("Error: Condición inválida");

        while((boolean) condition.solve(tablaSimbolos))
            body.execute(tablaSimbolos);

        return null;
    
    }
}
