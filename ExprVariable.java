

public class ExprVariable extends Expression {
    final Token name;

    ExprVariable(Token name) {
        this.name = name;
    }
//Verifica si fue declarada la variable apoyándose de la tabla de simbolos
    @Override
    public Object solve(TablaSimbolos tablaSimbolos) {

        if(tablaSimbolos.existeIdentificador(name.lexema))
            return tablaSimbolos.obtener(name.lexema);

        else
            throw new RuntimeException("Error: Variable no definida "+ name.lexema);
    }
}