
import java.util.List;

public class StmtBlock extends Statement{
    final List<Statement> statements;

    StmtBlock(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public Object execute(TablaSimbolos tablaSimbolos) {

        TablaSimbolos tabla_nueva = new TablaSimbolos(tablaSimbolos);

        // Ejecutamos todas las declaraciones de la lista
        for(Statement statement: statements){

            if(statement != null){

                if(statement instanceof StmtReturn)
                    return statement.execute(tabla_nueva);

                statement.execute(tabla_nueva);
            }
        }

        return null;
    }
}
