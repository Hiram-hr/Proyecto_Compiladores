

public class ExprLogical extends Expression{
    final Expression left;
    final Token operator;
    final Expression right;

    ExprLogical(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Object solve(TablaSimbolos tablaSimbolos) {

        // Resolvemos las expresiones
        Object left_solve = left.solve(tablaSimbolos);
        Object right_solve = right.solve(tablaSimbolos);

        switch (operator.tipo) {
            case AND:
                return (boolean)left_solve && (boolean)right_solve;

            case OR:
                return (boolean)left_solve || (boolean)right_solve;
        
            default:
                throw new RuntimeException("Error: Operador no reconocido");
        }


    }
}

