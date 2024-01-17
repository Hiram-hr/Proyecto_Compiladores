

public class ExprBinary extends Expression{
    final Expression left;
    final Token operator;
    final Expression right;

    public ExprBinary(Expression left, Token operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Object solve(TablaSimbolos tablaSimbolos) {

        // Resolvemos cada expresion
        Object left_solve = left.solve(tablaSimbolos);
        Object right_solve = right.solve(tablaSimbolos);


        switch (operator.tipo) {

            case PLUS:

            // Sumar números
                    return Double.parseDouble(left_solve.toString()) + Double.parseDouble(right_solve.toString());


            case MINUS:
            // Restar números
                    return Double.parseDouble(left_solve.toString()) - Double.parseDouble(right_solve.toString());

            case SLASH:
            // Dividimos números
                    return Double.parseDouble(left_solve.toString()) / Double.parseDouble(right_solve.toString());

            case STAR:
            // multiplicamos números
                    return Double.parseDouble(left_solve.toString()) * Double.parseDouble(right_solve.toString());

            // Lógicas

            case LESS:
            // menor
                    return Double.parseDouble(left_solve.toString()) < Double.parseDouble(right_solve.toString());

            case GREATER:
            // mayor
                    return Double.parseDouble(left_solve.toString()) > Double.parseDouble(right_solve.toString());

            case LESS_EQUAL:
            // menor igual
                    return Double.parseDouble(left_solve.toString()) <= Double.parseDouble(right_solve.toString());

            case GREATER_EQUAL:
            // mayor igual
                    return Double.parseDouble(left_solve.toString()) >= Double.parseDouble(right_solve.toString());

            case EQUAL_EQUAL:
            // igual igual
                    return Double.parseDouble(left_solve.toString()) == Double.parseDouble(right_solve.toString());

            case BANG_EQUAL:
            // diferente
                    return Double.parseDouble(left_solve.toString()) != Double.parseDouble(right_solve.toString());

            default:
                throw new RuntimeException("Error de operador: no reconocido");
        }
        
    }

}
