import java.util.HashMap;
import java.util.Map;

public class TablaSimbolos {


    // Definimos las variables
    private final Map<String, Object> values;
    private final TablaSimbolos padre;


    // Creamos los constructores para crear las tablas de símbolos

    public TablaSimbolos(){
        values = new HashMap<>();
        padre = null;
    }

    public TablaSimbolos (TablaSimbolos padre){
        this.padre = padre;
        values = new HashMap<>();
    }

    // Función que se encarga de verificar si ya existe un identificador en la tabla
    public boolean existeIdentificador(String identificador){

        // Verificamos si ya se encontró el identificador
        boolean encontrado = values.containsKey(identificador);

        // Buscamos en las tablas padre en caso de que existan
        if(encontrado == false && padre != null)
            encontrado = padre.existeIdentificador(identificador);

        return encontrado;
    }

    // Función que se encarga de obtener el valor de un identificador
    public Object obtener(String identificador) {

        if (values.containsKey(identificador)) 
            return values.get(identificador);

        // Verificamos si existen las tablas padre
        else if(padre != null)
            return padre.obtener(identificador);
        
        throw new RuntimeException("Variable no definida '" + identificador);
    }

    // Función que se encarga de asignar a un identificador un valor e ingresarlo a la tabla de valores
    public void asignar(String identificador, Object valor){
        
        // Verificamos si existen tablas padre y además si contiene el identificador que buscamos
        if(padre != null && padre.existeIdentificador(identificador))
            padre.asignar(identificador, valor);

        else
            values.put(identificador, valor);

    }
    
}

