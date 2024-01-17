
public class StmtReturn extends Statement {
    final Expression value;

    StmtReturn(Expression value) {
        this.value = value;
    }

    @Override
    public Object execute(TablaSimbolos tablaSimbolos) {
        return value.solve(tablaSimbolos);
     }
}
