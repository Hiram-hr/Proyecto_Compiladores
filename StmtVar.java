
public class StmtVar extends Statement {
    final Token name;
    final Expression initializer;

    StmtVar(Token name, Expression initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    @Override
    public Object execute(TablaSimbolos tablaSimbolos) {

        // Verificamos si tenemos que inicializar la variable
        if(initializer != null)
            tablaSimbolos.asignar(name.lexema, initializer.solve(tablaSimbolos));

        else
            tablaSimbolos.asignar(name.lexema, null); //ES NECESARIO ASIGNARLE NULL PARA EVITAR VALORES BASURA


        return null;

    }
}
