import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Principal {
    static boolean existenErrores=false;
    public static void main(String[] args)throws IOException{
        if(args.length>1){
            System.out.println("Uso correcto: interprete [archivo.txt]");
            // Convención defininida en el archivo "system.h" de UNIX
            System.exit(64);
        }else if(args.length==1){
            ejecutarArchivo(args[0]);
        }else{
            ejecutarPrompt(); 
        }
          
    }
    private static void ejecutarArchivo(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        ejecutar(new String(bytes, Charset.defaultCharset()));
        // Se indica que existe un error
        if(existenErrores) System.exit(65);
    }
    public static void ejecutarPrompt()throws IOException{
        InputStreamReader input= new InputStreamReader(System.in);
        BufferedReader reader= new BufferedReader(input);

        for(;;){
            System.out.print(">>>");
            String linea=reader.readLine();
            if(linea==null) break;
            ejecutar(linea);
            existenErrores=false;
        }
    }
    static void error(int linea, String mensaje){
        reportar(linea, "", mensaje);
    }
    private static void reportar(int linea, String posicion, String mensaje){
        System.err.println(
                "[linea " + linea + "] Error " + posicion + ": " + mensaje
        );
        existenErrores = true;
    }
    public static void ejecutar(String source){
       Scanner scanner= new Scanner(source);
       List<Token> tokens= scanner.scanTokens();
        Parser parser = new AST(tokens);
        List <Statement> tree=parser.parse();
        if(tree!=null){
            System.out.println("Analisis AST correcto");
        }else{
            System.out.println("Analisis AST incorrecto");
        }
         
    }
}