import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ruta {
    String identificador;
    List<Vuelo> vuelos;

    Ruta(String identificador) {
        this.identificador = identificador;
        this.vuelos = new ArrayList<>();
    }

    void agregarVuelo(Vuelo vuelo) {
        vuelos.add(vuelo);
    }

    @Override
    public String toString() {
        StringBuilder descripcion = new StringBuilder("Ruta " + identificador + ": ");
        for (Vuelo vuelo : vuelos) {
            descripcion.append(vuelo.origen.codigo).append(" -> ").append(vuelo.destino.codigo)
                    .append(" (ID: ").append(vuelo.identificador).append(") | ");
        }
        return descripcion.toString();
    }
}
