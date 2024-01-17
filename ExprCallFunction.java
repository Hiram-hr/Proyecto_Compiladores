

import java.util.ArrayList;
import java.util.List;

public class ExprCallFunction extends Expression{
    final Expression callee;
    // final Token paren;
    final List<Expression> arguments;

    ExprCallFunction(Expression callee, /*Token paren,*/ List<Expression> arguments) {
        this.callee = callee;
        // this.paren = paren;
        this.arguments = arguments;
    }

    @Override
    public Object solve(TablaSimbolos tablaSimbolos) {

        TablaSimbolos tabla_nueva = new TablaSimbolos(tablaSimbolos); 

        // Verificamos que no sea una expresión de tipo variable
        if(!(callee instanceof ExprVariable))
            throw new RuntimeException("Error al invocar función");

        Object funcion_temp = callee.solve(tablaSimbolos);
        if(!(funcion_temp instanceof StmtFunction))
            throw new RuntimeException("Error: Identificador inválido para invocar función");

       
        // Una vez verificado que sea una función, obtenemos los argumentos de la función y los resolvemmos
        List<Object> argumentos = new ArrayList<>();

        for(Expression argumento: arguments)
            argumentos.add(argumento.solve(tabla_nueva));

        // Verificamos si se pudieron resolver todos los argumentos
        if(argumentos.size() != ((StmtFunction) funcion_temp).params.size())
            throw new RuntimeException("Error: Error en argumentos al invocar "+ ((ExprVariable) callee).name.lexema);

        int i = 0;
        for(Token token: ((StmtFunction) funcion_temp).params){

            tabla_nueva.asignar(token.lexema, argumentos.get(i)); 
            i++;
        }

        return ((StmtFunction) funcion_temp).body.execute(tabla_nueva);
    }
}
