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

import Clases.Paquete;

public class EspacioBusquedaPSO {
    int[] espacio;
    List<Paquete> paquetes;
    List<Ruta> rutas;

    EspacioBusquedaPSO(List<Paquete> paquetes, List<Ruta> rutas) {
        this.espacio = new int[paquetes.size()];
        this.paquetes = paquetes;
        this.rutas = rutas;
    }

    @Override
    public String toString() {
        return paquetes.size() + " dimensiones desde 0 a " + rutas.size() + ". Espacio de " +
                Math.pow(rutas.size(), paquetes.size()) + " posiciones";
    }
}
