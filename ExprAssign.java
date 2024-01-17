

public class ExprAssign extends Expression{
    final Token name;
    final Expression value;

    ExprAssign(Token name, Expression value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Object solve(TablaSimbolos tablaSimbolos) {

        if(tablaSimbolos.existeIdentificador(name.lexema)){

            // Asignamos a la tabla el valor
            tablaSimbolos.asignar(name.lexema, value.solve(tablaSimbolos));
            return value.solve(tablaSimbolos);
        
        }else{

            throw new RuntimeException("Variable no definida " + name.lexema+"");
        }
    }
}
