// if(!verdadero), !x, i++, -
public class ExprUnary extends Expression{
    final Token operator;
    final Expression right;

    public ExprUnary(Token operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    @Override
    public Object solve(TablaSimbolos tablaSimbolos){
        Object rigth_solve = right.solve(tablaSimbolos);

        switch (operator.tipo){
            case BANG:

                if(rigth_solve instanceof Boolean)
                    return !(boolean) rigth_solve;
                else
                    throw new RuntimeException("El operador '!' solo se usa en expresiones booleanas");
                
            case MINUS:
            
                if(rigth_solve instanceof Integer)
                    return -(int)rigth_solve;
                
                else if(rigth_solve instanceof Double)
                    return -(double) rigth_solve;

                else
                    throw new RuntimeException("El operador menos solo se usa en números");
            
            default:
                throw new RuntimeException("Operador no reconocido");
        }
    }
}

