import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {

    private static final Map<String, TipoToken> palabrasReservadas;
    private static final Map<String, TipoToken> unCaracter;

    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("and",    TipoToken.AND);
        palabrasReservadas.put("else",   TipoToken.ELSE);
        palabrasReservadas.put("false",  TipoToken.FALSE);
        palabrasReservadas.put("for",    TipoToken.FOR);
        palabrasReservadas.put("fun",    TipoToken.FUN);
        palabrasReservadas.put("if",     TipoToken.IF);
        palabrasReservadas.put("null",   TipoToken.NULL);
        palabrasReservadas.put("or",     TipoToken.OR);
        palabrasReservadas.put("print",  TipoToken.PRINT);
        palabrasReservadas.put("return", TipoToken.RETURN);
        palabrasReservadas.put("true",   TipoToken.TRUE);
        palabrasReservadas.put("var",    TipoToken.VAR);
        palabrasReservadas.put("while",  TipoToken.WHILE);

        // Creamos otro hashmap para simbolos de un solo caracter
        unCaracter = new HashMap<>();
        unCaracter.put("(", TipoToken.LEFT_PAREN);
        unCaracter.put(")", TipoToken.RIGHT_PAREN);
        unCaracter.put("{", TipoToken.LEFT_BRACE);
        unCaracter.put("}", TipoToken.RIGHT_BRACE);
        unCaracter.put(".", TipoToken.DOT);
        unCaracter.put(",", TipoToken.COMMA);
        unCaracter.put(";", TipoToken.SEMICOLON);
        unCaracter.put("*", TipoToken.STAR);
        unCaracter.put("+", TipoToken.PLUS);
        unCaracter.put("-", TipoToken.MINUS);
    }

    



    private final String source;

    private final List<Token> tokens = new ArrayList<>();
    
    public Scanner(String source){
        this.source = source + " ";
    }

     // Símbolos un solo caracter 
        List<String> simbolos = Arrays.asList(
                "(", ")", "{", "}", ".", ",", ";", "*", "+","-");

    public List<Token> scan() throws Exception {


        String lexema = "";
        int estado = 0, lineas = 1;
        char c;

        // Consideramos estado 0 como inicial y estado -1 como final

        for(int i=0; i<source.length(); i++){
    
            c = source.charAt(i);
        
            switch(estado){

                // Consderamos el case 0 como estado inicial

                case 0:

                    if(Character.isLetter(c)){

                        estado = 1;
                        lexema += c;
                    }

                    else if(Character.isDigit(c)){

                        estado = 2;
                        lexema += c;
                    }

                    else if(c == '"'){
                    
                        estado = 8;
                        lexema += c;
                    }

                    else if(c == '/'){

                        estado = 10;
                        lexema += c;
                    }

                    else if(simbolos.contains(c+"")){

                        estado = 14;
                        lexema += c;
                    }

                    else if(c == '<'){

                        estado = 15;
                        lexema += c;
                    }

                    else if(c == '>'){

                        estado = 16;
                        lexema += c;
                    }

                    else if(c == '!'){

                        estado = 17;
                        lexema += c;
                    }

                    else if(c == '='){

                        estado = 18;
                        lexema += c;
                    }

                break;

                case 1:

                    if(Character.isLetter(c) || Character.isDigit(c)){
                        
                        estado = 1;
                        lexema += c;
                    
                    }else{

                        // Creamos el token para identificadores o palabras clave
                        TipoToken t_token = palabrasReservadas.get(lexema);

                        if(t_token == null){

                            Token new_token = new Token(TipoToken.IDENTIFIER, lexema);
                            tokens.add(new_token);

                        }else{

                            Token new_token = new Token(t_token, lexema);
                            tokens.add(new_token);


                        }

                        estado = 0;
                        lexema = "";
                        i--;
                    }

                break;

                case 2:

                    if(Character.isDigit(c)){
                        
                        estado = 2;
                        lexema += c; 
                    }

                    else if(c == '.'){

                        estado = 3;
                        lexema += c;
                    }

                    else if(c == 'E'){

                        estado = 5;
                        lexema += c;
                    }

                    else{

                        // Creamos token para número entero
                        Token new_token = new Token(TipoToken.NUMBER, lexema, Integer.valueOf(lexema));
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                        i--;

                    }

                break;

                case 3:

                    if(Character.isDigit(c)){

                        estado = 4;
                        lexema += c;
                    
                    }else{
                        
                        estado = -1;
                        lexema += c;
                    }

                break;

                case 4:

                    if(Character.isDigit(c)){

                        estado = 4;
                        lexema += c;
                    }

                    else if(c == 'E'){

                        estado = 5;
                        lexema += c;
                    }

                    else{

                        // Creamos token para número con decimal
                        Token new_token = new Token(TipoToken.NUMBER, lexema, Double.valueOf(lexema));
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                        i--;

                    }


                break;


                case 5:
                    
                if(c == '+' || c == '-'){

                    estado = 6;
                    lexema += c;
                
                }

                else if(Character.isDigit(c)){

                    estado = 7;
                    lexema += c;
                }

                else{

                    estado = -1;
                    lexema += c;

                }

                break;

                case 6:

                    if(Character.isDigit(c)){

                        estado = 7;
                        lexema += c;
                    
                    }

                    else{

                        estado = -1;
                        lexema += c;
                    }

                break;

                case 7:

                    if(Character.isDigit(c)){

                        estado = 7;
                        lexema += c;
                    }

                    else{

                        // Creamos token para número con exponente
                        Token new_token = new Token(TipoToken.NUMBER, lexema, Double.valueOf(lexema));
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                        i--;
                    }

                break;

                case 8:

                    if(c == '"'){

                        // Creamos el token cadena
                        lexema += c;
                        Token new_token = new Token(TipoToken.STRING, lexema, lexema.replaceAll("\"",""));
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";  
                    }


                    else if(c != '\n' && c != '\r'){

                        estado = 9;
                        lexema += c;
                    
                    }

                    

                    else{

                        estado = -1;
                        lexema += c;
                    }

                break;

                case 9:

                
                    if(c == '"'){

                        // Creamos el token cadena
                        lexema += c;
                        Token new_token = new Token(TipoToken.STRING, lexema, lexema.replaceAll("\"",""));
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                    }

                    else if(c != '\n' && c != '\r'){

                        estado = 9;
                        lexema += c;
                    }


                    else{

                        estado = -1;
                        lexema += c;

                    }

                break;

                case 10:

                    if(c == '*'){

                        estado = 12;
                        lexema += c;
                    }

                    else if(c == '/'){

                        estado = 11;
                        lexema += c;
                    }

                    else{

                        // Creamos token para simbolo división
                        Token new_token = new Token(TipoToken.SLASH, lexema);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                        i--;


                    }

                break;

                case 11:

                    if(c != '\n' && c != '\r'){

                        estado = 11;
                        lexema += c;
                    }

                    else{

                        // No creamos token para comentario
                        estado = 0;
                        lexema = "";
                        
                    }

                break;

                case 12:

                    if(c == '*'){

                        estado = 13;
                        lexema += c;
                    
                    }else{

                        estado = 12;
                        lexema += c;
                    }

                break;

                case 13:

                    if(c == '\n'){

                        estado = 12;
                        lexema += c;
                    }

                    else if(c == '/'){

                        // no creamos token para comentario
                        lexema += c;
                        
                        estado = 0;
                        lexema = "";
                    }

                    else{

                        estado = 13;
                        lexema += c;
                    }

                break;

                case 14:
                    
                     // Creamos el token para identificadores o palabras clave
                    TipoToken t_token = unCaracter.get(lexema);
                    Token new_Token = new Token(t_token, lexema);

                    tokens.add(new_Token);

                    estado = 0;
                    lexema = "";
                    i--;

                break;

                case 15:

                    if(c == '='){

                        // Creamos token para "<="
                        lexema += c;
                        Token new_token = new Token(TipoToken.LESS_EQUAL, lexema);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                    
                    }else{

                        // Creamos token para "<"
                        Token new_token = new Token(TipoToken.LESS, lexema);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                        i--;


                    }

                break;

                case 16:

                    
                    if(c == '='){

                        // Creamos token para ">="
                        lexema += c;
                        Token new_token = new Token(TipoToken.GREATER_EQUAL, lexema);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                    
                    }else{

                        // Creamos token para ">"
                        Token new_token = new Token(TipoToken.GREATER, lexema);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                        i--;


                    }
                break;

                case 17:

                    
                    if(c == '='){

                        // Creamos token para "!="
                        lexema += c;
                        Token new_token = new Token(TipoToken.BANG_EQUAL, lexema);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                    
                    }else{

                        // Creamos token para "!"
                        Token new_token = new Token(TipoToken.BANG, lexema);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                        i--;
                    }

                break;

                case 18:

                    
                    if(c == '='){

                        // Creamos token para "=="
                        lexema += c;
                        Token new_token = new Token(TipoToken.EQUAL_EQUAL, lexema);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                    
                    }else{

                        // Creamos token para "="
                        Token new_token = new Token(TipoToken.EQUAL, lexema);
                        tokens.add(new_token);

                        estado = 0;
                        lexema = "";
                        i--;
                    }

                    
                break;

                // Consideramos default como el estado muerto, el cual marcará si existe algún error
                default:

                        Principal.error(lineas, lexema);
                        estado = 0;
                        lexema = "";

                break;

            }   


            // Verificamos si existe un salto de línea para incrementar las líneas del código
             if(c == '\n')
                lineas++;
        
        
        }

        // Añadimos EOF
        tokens.add(new Token(TipoToken.EOF,lexema));

        // Verifcamos si quedó en un estado de aceptación o no
            if(estado != 0){

                Principal.error(lineas, lexema);
                lexema = "";
                estado = 0;
            }


        return tokens;
    }

}
