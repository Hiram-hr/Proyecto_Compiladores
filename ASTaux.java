import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ASTaux implements Parser {
    private final List<Token> tokens;
    private int i = 0;
    private Token preanalisis;
    private final List<TipoToken> PRIMARY_EXP = Arrays.asList(TipoToken.BANG, TipoToken.MINUS, TipoToken.TRUE, TipoToken.FALSE, TipoToken.NULL, TipoToken.NUMBER, TipoToken.STRING, TipoToken.IDENTIFIER, TipoToken.LEFT_PAREN);
    public ASTaux(List<Token> tokens) {
        this.tokens = tokens;
        preanalisis = this.tokens.get(i);
    }

     @Override
    public List<Statement> parse() {
        List<Statement> statements = PROGRAM();
        return statements;
    }
    // PROGRAM
    public List<Statement> PROGRAM(){
        List<Statement> program = new ArrayList<>();
        if(preanalisis.tipo != TipoToken.EOF){
            List<Statement> resultado = DECLARATION(program);
            return resultado;
        }

        return null;
    }

    // DECLARACIONES
    
    // DECLARATION
    private List<Statement> DECLARATION(List<Statement> program){

        Statement statement;

        if(preanalisis.tipo == TipoToken.FUN){

            statement = FUN_DECL();
            program.add(statement);
            return DECLARATION(program);
        }

        else if(preanalisis.tipo == TipoToken.VAR){

            statement = VAR_DECL();
            program.add(statement);
            return DECLARATION(program);

        }

        else if(PRIMARY_EXP.contains(preanalisis.tipo) || preanalisis.tipo == TipoToken.FOR || preanalisis.tipo == TipoToken.IF || preanalisis.tipo == TipoToken.PRINT || preanalisis.tipo == TipoToken.RETURN || preanalisis.tipo == TipoToken.WHILE || preanalisis.tipo == TipoToken.LEFT_BRACE){

            statement = STATEMENT();
            program.add(statement);
            return DECLARATION(program);
        }

        return program;
    }

    // FUN_DECL
    private Statement FUN_DECL(){

        match(TipoToken.FUN);
        return FUNCTION();
        
    }

    // VAR_DECL
    private Statement VAR_DECL(){

        match(TipoToken.VAR);
        match(TipoToken.IDENTIFIER);

        Token identificador = previous();
        Expression expression = VAR_INIT();
        match(TipoToken.SEMICOLON);

        return new StmtVar(identificador, expression);
    }

    private Expression VAR_INIT(){

        if(preanalisis.tipo == TipoToken.EQUAL){

            match(TipoToken.EQUAL);
            return EXPRESSION();
        }

        return null;
    }

    // SENTENCIAS

    // STATEMENT
    private Statement STATEMENT(){

        if(PRIMARY_EXP.contains(preanalisis.tipo))    
            return EXPR_STMT();
        

        else if(preanalisis.tipo == TipoToken.FOR)
            return FOR_STMT();

        else if(preanalisis.tipo == TipoToken.IF)
            return IF_STMT();

        else if(preanalisis.tipo == TipoToken.PRINT)
            return PRINT_STMT();

        else if(preanalisis.tipo == TipoToken.RETURN)
            return RETURN_STMT();

        else if(preanalisis.tipo == TipoToken.WHILE){

            return WHILE_STMT();
        }

        else if(preanalisis.tipo == TipoToken.LEFT_BRACE){

            return BLOCK();
        }
        
        return null;
    }


    // EXPR_STMT
    private Statement EXPR_STMT(){

        Expression expression = EXPRESSION();
        match(TipoToken.SEMICOLON);

        return new StmtExpression(expression);
    }

    // FOR_STMT
    private Statement FOR_STMT(){

         // Esta parte utiliza azucar sintáctico

        match(TipoToken.FOR);
        match(TipoToken.LEFT_PAREN);

        Statement inicio = FOR_STMT_1();
        Expression condicion = FOR_STMT_2();
        Expression incremento = FOR_STMT_3();

        match(TipoToken.RIGHT_PAREN);

        Statement cuerpo = STATEMENT();

        // Analizamos incremento
        if(incremento != null)
            cuerpo = new StmtBlock(Arrays.asList(cuerpo, new StmtExpression(incremento)));

        
        
        // Analizamos condición
        if(condicion == null)
            condicion = new ExprLiteral(true); 
        

        cuerpo  = new StmtLoop(condicion, cuerpo);

        // Analizamos inicializador
        if(inicio != null)
            cuerpo = new StmtBlock(Arrays.asList(inicio, cuerpo));

        
    
        return cuerpo;

    }

    // FOR_STMT_1
    private Statement FOR_STMT_1(){

        if(preanalisis.tipo == TipoToken.VAR)
            return VAR_DECL();
            
        else if(PRIMARY_EXP.contains(preanalisis.tipo))
            return EXPR_STMT();

        match(TipoToken.SEMICOLON);
        return null;
    }

    // FOR_STMT_2
    private Expression FOR_STMT_2(){

        if(PRIMARY_EXP.contains(preanalisis.tipo)){

            Expression expression = EXPRESSION();
            match(TipoToken.SEMICOLON);
            return expression;
        }

        match(TipoToken.SEMICOLON);
        return null;
    }

    // FOR_STMT_3
    private Expression FOR_STMT_3(){

        if(PRIMARY_EXP.contains(preanalisis.tipo))
            return EXPRESSION();


        return null;
    }

    // IF_STMT
    private Statement IF_STMT(){

        match(TipoToken.IF);
        match(TipoToken.LEFT_PAREN);
        Expression condicion = EXPRESSION();
        match(TipoToken.RIGHT_PAREN);

        Statement then_exp = STATEMENT();
        Statement else_exp = ELSE_STMT();

        return new StmtIf(condicion, then_exp, else_exp);
    }

    // ELSE_STMT
    private Statement ELSE_STMT(){

        if(preanalisis.tipo == TipoToken.ELSE){

            match(TipoToken.ELSE);
            return STATEMENT();
        }

        return null;
    }

    // PRINT_STMT
    private Statement PRINT_STMT(){

        match(TipoToken.PRINT);
        Expression print = EXPRESSION();
        match(TipoToken.SEMICOLON);
        return new StmtPrint(print);
    }

    // RETURN_STMT
    private Statement RETURN_STMT(){

        match(TipoToken.RETURN);
        Expression return_exp = RETURN_EXP_OPC();
        match(TipoToken.SEMICOLON);
        return new StmtReturn(return_exp);

    }

    // RETURN_EXP_OPC
    private Expression RETURN_EXP_OPC(){

        if(PRIMARY_EXP.contains(preanalisis.tipo))
            return EXPRESSION();

        return null;
    }

    // WHILE_STMT
    private Statement WHILE_STMT(){

        match(TipoToken.WHILE);
        match(TipoToken.LEFT_PAREN);

        Expression condicion = EXPRESSION();
        match(TipoToken.RIGHT_PAREN);

        Statement cuerpo = STATEMENT();

        return new StmtLoop(condicion, cuerpo);
    }

    // BLOCK
    private Statement BLOCK(){

        match(TipoToken.LEFT_BRACE);

        List<Statement> statements = new ArrayList<>();

        statements = DECLARATION(statements);
        match(TipoToken.RIGHT_BRACE);

        return new StmtBlock(statements);
    }


    // EXPRESIONES

    // EXPRESSION
    private Expression EXPRESSION(){
        return ASSIGNMENT();
    }

    // ASSIGNMENT
    private Expression ASSIGNMENT(){

        Expression expression = LOGIC_OR();
        expression = ASSIGNMENT_OPC(expression);

        return expression;
    }

    // ASSIGNMENT_OPC
    private Expression ASSIGNMENT_OPC(Expression expression){
        
        Expression expression_aux;

        if(preanalisis.tipo == TipoToken.EQUAL){

            if(expression instanceof ExprVariable){

                match(TipoToken.EQUAL);
                expression_aux = EXPRESSION();

                return new ExprAssign(((ExprVariable) expression).name, expression_aux);
            
            }else{

                System.out.println("Error no se encontró identificador\n");
            }

        }

        return expression;
    }

    // LOGIC_OR
    private Expression LOGIC_OR(){

        Expression expression = LOGIC_AND();
        expression = LOGIC_OR_2(expression);

        return expression;
    }

    // LOGIC_OR_2
    private Expression LOGIC_OR_2(Expression expression){

        if(preanalisis.tipo == TipoToken.OR){

            match(TipoToken.OR);
            Token operador = previous();
            Expression expression_aux = LOGIC_AND();
            Expression expression_final = new ExprLogical(expression, operador, expression_aux);

            return LOGIC_OR_2(expression_final);
        }

        return expression;
    }

    // LOGIC_AND
    private Expression LOGIC_AND(){

        Expression expression = EQUALITY();
        expression = LOGIC_AND_2(expression);

        return expression;
    }

    // LOGIC_AND_2
    private Expression LOGIC_AND_2(Expression expression){

        if(preanalisis.tipo == TipoToken.AND){

            match(TipoToken.AND);
            Token operador = previous();
            Expression expression_aux = EQUALITY();
            Expression expression_final = new ExprLogical(expression, operador, expression_aux);

            return LOGIC_AND_2(expression_final);
        }

        return expression;
    }

    // EQUALITY
    private Expression EQUALITY(){

        Expression expression = COMPARISON();
        expression = EQUALITY_2(expression);

        return expression;
    }

    // EQUALITY_2
    private Expression EQUALITY_2(Expression expression){

        Token operador;
        Expression expression_aux;
        Expression expression_final;

        if(preanalisis.tipo == TipoToken.BANG_EQUAL){

            match(TipoToken.BANG_EQUAL);
            operador = previous();
            expression_aux = COMPARISON();
            expression_final = new ExprBinary(expression, operador, expression_aux);

            return EQUALITY_2(expression_final);
            
        }

        else if(preanalisis.tipo == TipoToken.EQUAL_EQUAL){

            match(TipoToken.EQUAL_EQUAL);
            operador = previous();
            expression_aux = COMPARISON();
            expression_final = new ExprBinary(expression, operador, expression_aux);

            return EQUALITY_2(expression_final);
            
        }

        return expression;
    }

    // COMPARISON
    private Expression COMPARISON(){

        Expression expression = TERM();
        expression = COMPARISON_2(expression);

        return expression;
    }

    // COMPARISON_2
    private Expression COMPARISON_2(Expression expression){

        
        Token operador;
        Expression expression_aux;
        Expression expression_final;

        if(preanalisis.tipo == TipoToken.GREATER){

            match(TipoToken.GREATER);
            operador = previous();
            expression_aux = TERM();
            expression_final = new ExprBinary(expression, operador, expression_aux);

            return COMPARISON_2(expression_final);
            
        }

        else if(preanalisis.tipo == TipoToken.GREATER_EQUAL){

            match(TipoToken.GREATER_EQUAL);
            operador = previous();
            expression_aux = TERM();
            expression_final = new ExprBinary(expression, operador, expression_aux);

            return COMPARISON_2(expression_final);
            
        }

         else if(preanalisis.tipo == TipoToken.LESS){

            match(TipoToken.LESS);
            operador = previous();
            expression_aux = TERM();
            expression_final = new ExprBinary(expression, operador, expression_aux);

            return COMPARISON_2(expression_final);
            
        }

         else if(preanalisis.tipo == TipoToken.LESS_EQUAL){

            match(TipoToken.LESS_EQUAL);
            operador = previous();
            expression_aux = TERM();
            expression_final = new ExprBinary(expression, operador, expression_aux);

            return COMPARISON_2(expression_final);
            
        }


        return expression;
    }


    // TERM
    private Expression TERM(){

        Expression expression = FACTOR();
        expression = TERM_2(expression);

        return expression;
    }

    // TERM_2
    private Expression TERM_2(Expression expression){

        Token operador;
        Expression expression_aux;
        Expression expression_final;

        if(preanalisis.tipo == TipoToken.MINUS){

            match(TipoToken.MINUS);
            operador = previous();
            expression_aux = FACTOR();
            expression_final = new ExprBinary(expression, operador, expression_aux);

            return TERM_2(expression_final);
            
        }

        else if(preanalisis.tipo == TipoToken.PLUS){

            match(TipoToken.PLUS);
            operador = previous();
            expression_aux = FACTOR();
            expression_final = new ExprBinary(expression, operador, expression_aux);

            return TERM_2(expression_final);
            
        }

        return expression;

    }


    // FACTOR
    private Expression FACTOR(){

        Expression expression = UNARY();
        expression = FACTOR_2(expression);

        return expression;
    }

    // FACTOR_2
    private Expression FACTOR_2(Expression expression){

        Token operador;
        Expression expression_aux;
        Expression expression_final;

        if(preanalisis.tipo == TipoToken.STAR){

            match(TipoToken.STAR);
            operador = previous();
            expression_aux = UNARY();
            expression_final = new ExprBinary(expression, operador, expression_aux);

            return FACTOR_2(expression_final);
            
        }

        else if(preanalisis.tipo == TipoToken.SLASH){

            match(TipoToken.SLASH);
            operador = previous();
            expression_aux = UNARY();
            expression_final = new ExprBinary(expression, operador, expression_aux);

            return FACTOR_2(expression_final);
            
        }

        return expression;

        
    }

    // UNARY
    private Expression UNARY(){

        Token operador;
        Expression expression_aux;

        if(preanalisis.tipo == TipoToken.BANG){

            match(TipoToken.BANG);
            operador = previous();
            expression_aux = UNARY();

            return new ExprUnary(operador, expression_aux);
            
        }

        else if(preanalisis.tipo == TipoToken.MINUS){

            match(TipoToken.MINUS);
            operador = previous();
            expression_aux = UNARY();

            return new ExprUnary(operador, expression_aux);
            
        }

        else{

            return CALL();
        }
    }

    // CALL
    private Expression CALL(){

        Expression expression = PRIMARY();
        expression = CALL_2(expression);

        return expression;
    }

    // CALL_2
    private Expression CALL_2(Expression expression){

        if(preanalisis.tipo == TipoToken.LEFT_PAREN){

            match(TipoToken.LEFT_PAREN);
            List<Expression> argumentos = ARGUMENTS_OPC();
            match(TipoToken.RIGHT_PAREN);

            return CALL_2(new ExprCallFunction(expression, argumentos));
        }

        return expression;
    }

    // PRIMARY
    private Expression PRIMARY(){

        if(preanalisis.tipo == TipoToken.TRUE){

            match(TipoToken.TRUE);
            return new ExprLiteral(true);
        }

        else if(preanalisis.tipo == TipoToken.FALSE){

            match(TipoToken.FALSE);
            return new ExprLiteral(false);
        }

        else if(preanalisis.tipo == TipoToken.NULL){

            match(TipoToken.FALSE);
            return new ExprLiteral(null);
        }

        else if(preanalisis.tipo == TipoToken.NUMBER){

            match(TipoToken.NUMBER);
            Token numero = previous();
            return new ExprLiteral(numero.literal);
        }

        else if(preanalisis.tipo == TipoToken.STRING){

            match(TipoToken.STRING);
            Token string = previous();
            return new ExprLiteral(string.literal);
        }

        else if(preanalisis.tipo == TipoToken.IDENTIFIER){

            match(TipoToken.IDENTIFIER);
            Token identificador = previous();
            return new ExprVariable(identificador);
        }

        else if(preanalisis.tipo == TipoToken.LEFT_PAREN){

            match(TipoToken.LEFT_PAREN);
            Expression expression = EXPRESSION();

            match(TipoToken.RIGHT_PAREN);
            return new ExprGrouping(expression);
        }

        return null;
    }

    // OTRAS

    // FUNCTION
    private Statement FUNCTION(){

        match(TipoToken.IDENTIFIER);
        Token identificador = previous();
        match(TipoToken.LEFT_PAREN);
        List<Token> parametros = PARAMETERS_OPC();
        match(TipoToken.RIGHT_PAREN);
        Statement cuerpo = BLOCK();

        return new StmtFunction(identificador, parametros, (StmtBlock) cuerpo);
    }

    // PARAMETERS_OPC
    private List<Token> PARAMETERS_OPC(){

        List<Token> parametros = new ArrayList<>();

        if(preanalisis.tipo == TipoToken.IDENTIFIER){

            parametros = PARAMETERS(parametros);
            return parametros;
        }

        return null;
    }

    // PARAMETERS
    private List<Token> PARAMETERS(List<Token> parametros){

        match(TipoToken.IDENTIFIER);
        Token identificador = previous();
        parametros.add(identificador);

        parametros = PARAMETERS_2(parametros);

        return parametros;
    }

    // PARAMETERS_2
    private List<Token> PARAMETERS_2(List<Token> parametros){

        if(preanalisis.tipo == TipoToken.COMMA){

            match(TipoToken.COMMA);
            match(TipoToken.IDENTIFIER);
            Token identificador = previous();
            parametros.add(identificador);

            return PARAMETERS_2(parametros);
        }

        return parametros;
    }

    // ARGUMENTS_OPC
    private List<Expression> ARGUMENTS_OPC(){

        List<Expression> argumentos = new ArrayList<>();

        if(PRIMARY_EXP.contains(preanalisis.tipo)){

            Expression expression = EXPRESSION();
            argumentos.add(expression);

            ARGUMENTS(argumentos);

            return argumentos;
        }

        return null;
    }

    // ARGUMENTS
    private List<Expression> ARGUMENTS(List<Expression> argumentos){

        if(preanalisis.tipo == TipoToken.COMMA){

            match(TipoToken.COMMA);
            Expression expression = EXPRESSION();
            argumentos.add(expression);
            ARGUMENTS(argumentos);
        }

        return null; //
    }


    private void match(TipoToken tt) {
        if(preanalisis.tipo ==  tt){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            String message = "Error: " + "Se esperaba " + preanalisis.tipo + " pero se encontró " + tt ;
            System.out.println(message);
        }
    }
    private Token previous() {
        return this.tokens.get(i - 1);
    }


   
}
