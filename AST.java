import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AST implements Parser{
    private int i = 0;
    private Token preanalisis;
    private final List<Token> tokens;
    public AST(List<Token> tokens){
        this.tokens=tokens;
        preanalisis=this.tokens.get(i);
    }
    private List<Statement> PROGRAM(){
        List<Statement> statements = new ArrayList<>();
        DECLARATION(statements);
        return statements;
    }
    private void DECLARATION(List<Statement> statements){
        Statement resultado=null;
        while (true) {
            if(preanalisis.tipo==TipoToken.FUN){
                resultado=FUN_DECL();
                statements.add(resultado);
            }else if(preanalisis.tipo==TipoToken.VAR){
                resultado=VAR_DECL();
                statements.add(resultado);
            }else if(preanalisis.tipo==TipoToken.IF||preanalisis.tipo==TipoToken.FOR||
            preanalisis.tipo==TipoToken.PRINT||preanalisis.tipo==TipoToken.RETURN||
            preanalisis.tipo==TipoToken.WHILE||preanalisis.tipo==TipoToken.LEFT_BRACE
            ||preanalisis.tipo==TipoToken.TRUE||preanalisis.tipo==TipoToken.FALSE||
            preanalisis.tipo==TipoToken.NULL||preanalisis.tipo==TipoToken.NUMBER||preanalisis.tipo==TipoToken.STRING||
            preanalisis.tipo==TipoToken.IDENTIFIER||preanalisis.tipo==TipoToken.LEFT_BRACE){
                resultado=STATEMENT();
                statements.add(resultado);
            }else{
                break;
            }
        }
    }

    private Statement FUN_DECL(){
            match(TipoToken.FUN);
            return FUNCTION();
    }

    private Statement VAR_DECL(){
            match(TipoToken.VAR);
            match(TipoToken.IDENTIFIER);
            Token t=previous();
            Expression x=VAR_INIT();
            match(TipoToken.SEMICOLON);
            return new StmtVar(t, x);
    }

    private Statement STATEMENT(){
        switch (preanalisis.tipo){
            case  BANG, MINUS, TRUE, FALSE, NULL, NUMBER, STRING, IDENTIFIER, LEFT_PAREN:
                return EXPR_STMT();
            case IF:
                return IF_STMT();
            case FOR:
                return FOR_STMT();
            case PRINT:
                return PRINT_STMT();
            case RETURN:
                return RETURN_STMT();
            case WHILE:
                return WHILE_STMT();
            case LEFT_BRACE:
                return BLOCK();
            default:
                throw new Error("Error de sintaxis: Se esperaba STATEMENT" + " pero se encontró " + preanalisis.tipo);
        }
    }

    private Statement EXPR_STMT(){
        Expression expr=EXPRESSION();
        match(TipoToken.SEMICOLON);
        return new StmtExpression(expr);
    }

        private Statement FOR_STMT(){
                match(TipoToken.FOR);
                match(TipoToken.LEFT_PAREN);
                Statement x= FOR_STMT_1();
                Expression y=FOR_STMT_2();
                Expression z=FOR_STMT_3();
                match(TipoToken.RIGHT_PAREN);
                Statement body= STATEMENT();
                if(z!=null){
                    body=new StmtBlock(Arrays.asList(body,new StmtExpression(z)));
                }
                if(y==null){
                    y=new ExprLiteral(true);
                }
                body=new StmtLoop(y, body);
                if(x!=null){
                    body=new StmtBlock(Arrays.asList(x,body));
                }
                return body;
        }

    private Statement FOR_STMT_1(){
        if(preanalisis.tipo==TipoToken.VAR){
            return VAR_DECL();
        }else if(preanalisis.tipo!=TipoToken.SEMICOLON){
            return EXPR_STMT();
        }else{
            match(TipoToken.SEMICOLON);
            return null;
        }
    }

    private Expression FOR_STMT_2(){
        if(preanalisis.tipo!=TipoToken.SEMICOLON){
            Expression expr=EXPRESSION();
            match(TipoToken.SEMICOLON);
            return expr;
        }else{
            match(TipoToken.SEMICOLON);
            return null;
        }
    }

    private Expression FOR_STMT_3(){
        if(preanalisis.tipo!=TipoToken.RIGHT_PAREN){
            return EXPRESSION();
        }else{
            return null;
        }
    }

    private Statement IF_STMT(){
            match(TipoToken.IF);
            match(TipoToken.LEFT_PAREN);
            Expression x=EXPRESSION();
            match(TipoToken.RIGHT_PAREN);
            Statement b=STATEMENT();
            Statement a=ELSE_STATEMENT();
            return new StmtIf(x, b, a);
    }

    private Statement ELSE_STATEMENT(){
        if(preanalisis.tipo==TipoToken.ELSE){
            match(TipoToken.ELSE);
            return STATEMENT();
        }
        return null;
    }

    private Statement PRINT_STMT(){
            match(TipoToken.PRINT);
            Expression value=EXPRESSION();
            match(TipoToken.SEMICOLON);
            return new StmtPrint(value);
    }

    private Statement RETURN_STMT(){
            match(TipoToken.RETURN);
            Expression value=RETURN_EXP_OPC();
            match(TipoToken.SEMICOLON);
            return new StmtReturn(value);
    }

    private Expression RETURN_EXP_OPC(){
        if(preanalisis.tipo!=TipoToken.SEMICOLON){
            return EXPRESSION();
        }
        return null;
    }

    private Statement WHILE_STMT(){
           match(TipoToken.WHILE); 
           match(TipoToken.LEFT_PAREN); 
           Expression condition=EXPRESSION();
           match(TipoToken.RIGHT_PAREN); 
           Statement body=STATEMENT();
           return new StmtLoop(condition, body);
    }

    private Statement FUNCTION(){
            match(TipoToken.IDENTIFIER);
            Token t=previous();
            match(TipoToken.LEFT_PAREN);
            List<Token>parametros= PARAMETERS_OPC();
            match(TipoToken.RIGHT_PAREN);
            StmtBlock body= BLOCK();
            return new StmtFunction(t, parametros, body);
    }

    private List<Token> PARAMETERS_OPC(){
        if(preanalisis.tipo!=TipoToken.RIGHT_PAREN){
            return PARAMETERS();
        }
        return Collections.emptyList();//caso vacio
    }

    private List<Token> PARAMETERS(){
        List<Token> parametros=new ArrayList<>();
        if(preanalisis.tipo==TipoToken.IDENTIFIER){
            match(TipoToken.IDENTIFIER);
            parametros.add(previous());
            PARAMETERS_2(parametros);
            return parametros;
        }else{
            throw new Error("\nError de sintaxis: Se esperaba IDENTIFICADOR" + " pero se encontró " + preanalisis.tipo);
        }
    }

    private List<Token> PARAMETERS_2(List<Token> lista){
        if(preanalisis.tipo==TipoToken.COMMA){
            match(TipoToken.COMMA);
            match(TipoToken.IDENTIFIER);
            lista.add(previous());
            PARAMETERS_2(lista);
        }
        return lista;
    }

    private StmtBlock BLOCK(){
        match(TipoToken.LEFT_BRACE);
        List<Statement> statements=new ArrayList<>();
        DECLARATION(statements);
        match(TipoToken.RIGHT_BRACE);
        return new StmtBlock(statements);
    }

    private Expression VAR_INIT(){
        if(preanalisis.tipo==TipoToken.EQUAL){
              match(TipoToken.EQUAL);
              return EXPRESSION();
        }
        return null;
    }

    private Expression EXPRESSION(){
        Expression expr= ASSIGNMENT();
        return expr;
    }

    private Expression ASSIGNMENT(){
            Expression expr=LOGIC_OR();
            expr=ASSIGNMENT_OPC(expr);
            return expr;
    }

    private Expression LOGIC_OR(){
                Expression expr= LOGIC_AND();
                expr= LOGIC_OR_2(expr);
                return expr;
    }

    private Expression LOGIC_OR_2(Expression expr){
        if(preanalisis.tipo==TipoToken.OR){
            match(TipoToken.OR);
            Expression right =LOGIC_AND();
            Token exprr=previous();
            expr=new ExprBinary(expr, exprr, right);
            expr=LOGIC_OR_2(expr);
            return expr;
        }
        return expr;
    }

    private Expression LOGIC_AND(){
                Expression expr= EQUALITY();
                expr =LOGIC_AND_2(expr);
                return expr;
    }

    private Expression LOGIC_AND_2(Expression expr){
        if(preanalisis.tipo==TipoToken.AND){
            match(TipoToken.AND);
            Expression right= EQUALITY();
            expr=new ExprBinary(expr, new Token(TipoToken.AND, "and"), right);
            expr=LOGIC_AND_2(expr);
            return expr;
        }
        return expr;
    }

    private Expression EQUALITY(){
                Expression expr= COMPARISON();
                expr =EQUALITY_2(expr);
                return expr;
    }
    private Expression EQUALITY_2(Expression expr){
        while(preanalisis.tipo==TipoToken.BANG_EQUAL||preanalisis.tipo==TipoToken.EQUAL_EQUAL){
            Token operador=preanalisis;
            match(preanalisis.tipo);
            Expression right = COMPARISON();
            expr= new ExprBinary(expr, operador, right);
        }
        return expr;
    }
    private Expression COMPARISON(){
            Expression expr=TERM();
            expr= COMPARISON_2(expr);
            return expr;
    }
    private Expression COMPARISON_2(Expression expr){
            while(preanalisis.tipo==TipoToken.GREATER||
            preanalisis.tipo==TipoToken.GREATER_EQUAL||
            preanalisis.tipo==TipoToken.LESS||
            preanalisis.tipo==TipoToken.LESS_EQUAL){
                Token operador= preanalisis;
                match(preanalisis.tipo);
                Expression right=TERM();
                expr= new ExprBinary(expr, operador, right);
            }
            return expr;
        }
        private Expression TERM(){
            Expression expr=FACTOR();
            expr=TERM_2(expr);
            return expr;
        }

    private Expression TERM_2(Expression expr){
        while(preanalisis.tipo==TipoToken.MINUS||preanalisis.tipo==TipoToken.PLUS){
            Token operador=preanalisis;
            match(preanalisis.tipo);
            Expression right=FACTOR();
            expr=new ExprBinary(expr, operador, right);
        }
        return expr;
    }

    private Expression FACTOR(){
        Expression expr = UNARY();
        expr = FACTOR_2(expr);
        return expr;
    }

    private Expression FACTOR_2(Expression expr){
        switch (preanalisis.tipo){
            case SLASH:
                match(TipoToken.SLASH);
                Token operador = previous();
                Expression expr2 = UNARY();
                ExprBinary expb = new ExprBinary(expr, operador, expr2);
                return FACTOR_2(expb);
            case STAR:
                match(TipoToken.STAR);
                operador = previous();
                expr2 = UNARY();
                expb = new ExprBinary(expr, operador, expr2);
                return FACTOR_2(expb);
        }
        return expr;
    }

    private Expression UNARY(){
        switch (preanalisis.tipo){
            case BANG:
                match(TipoToken.BANG);
                Token operador = previous();
                Expression expr = UNARY();
                return new ExprUnary(operador, expr);
            case MINUS:
                match(TipoToken.MINUS);
                operador = previous();
                expr =UNARY();
                return new ExprUnary(operador, expr);
            default:
                return CALL();
        }
    }

    private Expression CALL(){
        Expression expr = PRIMARY();
        expr = CALL_2(expr);
        return expr;
    }
//rev
    private Expression CALL_2(Expression expr){
        switch (preanalisis.tipo){
            case LEFT_PAREN:
                match(TipoToken.LEFT_PAREN);
                List<Expression> lstArguments = ARGUMENTS_OPC();
                match(TipoToken.RIGHT_PAREN);
                ExprCallFunction ecf = new ExprCallFunction(expr, lstArguments);
                return CALL_2(ecf);
        }
        return expr;
    }

    private List<Expression> ARGUMENTS_OPC(){
        if(preanalisis.tipo!=TipoToken.RIGHT_PAREN){
            return ARGUMENTS();
        }
        return Collections.emptyList();//caso-> E
    }

    private List<Expression> ARGUMENTS(){
        List<Expression> argumentos = new ArrayList<>();
        while (preanalisis.tipo != TipoToken.RIGHT_PAREN) {
            argumentos.add(EXPRESSION());
            if (preanalisis.tipo == TipoToken.COMMA) {
                match(TipoToken.COMMA);
            }
        }
        return argumentos;
    }
    private void FUNCTIONS(){
        if(preanalisis.tipo == TipoToken.FUN){
            FUN_DECL();
            FUNCTIONS();
        }
    }
    private Expression PRIMARY(){
        switch (preanalisis.tipo){
            case TRUE:
                match(TipoToken.TRUE);
                return new ExprLiteral(true);
            case FALSE:
                match(TipoToken.FALSE);
                return new ExprLiteral(false);
            case NULL:
                match(TipoToken.NULL);
                return new ExprLiteral(null);
            case NUMBER:
                match(TipoToken.NUMBER);
                Token numero = previous();
                return new ExprLiteral(numero.literal);
            case STRING:
                match(TipoToken.STRING);
                Token cadena = previous();
                return new ExprLiteral(cadena.literal);
            case IDENTIFIER:
                match(TipoToken.IDENTIFIER);
                Token id = previous();
                return new ExprVariable(id);
            case LEFT_PAREN:
                match(TipoToken.LEFT_PAREN);
                Expression expr = EXPRESSION();
                // Tiene que ser cachado aquello que retorna
                match(TipoToken.RIGHT_PAREN);
                return new ExprGrouping(expr);
            default: 
                throw new Error("Error de sintaxis: Se esperaba STATEMENT" + " pero se encontró " + preanalisis.tipo);
        }
    }

    private Expression ASSIGNMENT_OPC(Expression exp){
        if(preanalisis.tipo==TipoToken.EQUAL){
            match(TipoToken.EQUAL);
            Token t =previous();
            Expression tt=EXPRESSION();
            exp= new ExprAssign(t, tt);
        }
        return exp;
    }

    private void match(TipoToken tt){
        if(preanalisis.tipo ==  tt){
            i++;
            preanalisis = tokens.get(i);
        }
        else {
            throw new Error("Error de sintaxis: Se esperaba " + tt + " pero se encontró " + preanalisis.tipo);
        }
    }

    private Token previous() {
        return this.tokens.get(i - 1);
    }

    @Override
    public List<Statement> parse(){
       return PROGRAM();
    }
}