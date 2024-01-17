//Literal: es el valor que se le asigna a las variables, por ejemplo: 2, hola mundo, etc.
public class ExprLiteral extends Expression {
    final Object value;

    ExprLiteral(Object value) {
        this.value = value;
    }

    @Override
    public Object solve(TablaSimbolos tablaSimbolos) {
        // Al ser un valor literal no hace falta resolverlo
        return value;
    }
}
