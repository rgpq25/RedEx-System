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

import Clases.Vuelo;

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
            descripcion.append(vuelo.getPlan_vuelo().getCiudadOrigen().getId()).append(" -> ").append(vuelo.getPlan_vuelo().getCiudadDestino().getId())
                    .append(" (ID: ").append(vuelo.getId()).append(") | ");
        }
        return descripcion.toString();
    }
}
