package Clases;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RutasAlmacenadas {
    private TreeMap<Integer, PlanRuta> rutasPorId = new TreeMap<>();
    private HashMap<String, PlanRuta> rutasPorCodigo = new HashMap<>();

    public void agregarRuta(Integer id, PlanRuta ruta) {
        rutasPorId.put(id, ruta);
        rutasPorCodigo.put(ruta.getCodigo(), ruta);
    }

    public PlanRuta obtenerRutaPorId(Integer id) {
        return rutasPorId.get(id);
    }

    public PlanRuta obtenerRutaPorCodigo(String codigo) {
        return rutasPorCodigo.get(codigo);
    }

    public void eliminarRuta(Integer id) {
        if (rutasPorId.containsKey(id)) {
            PlanRuta ruta = rutasPorId.remove(id);
            rutasPorCodigo.remove(ruta.getCodigo());
        }
    }

    public boolean obtenerClaveXClavesDespues(Integer claveInicial, AtomicInteger x) {
        Integer claveActual = claveInicial;
        int pasosDeseados = x.get();
        for (int i = 0; i < pasosDeseados; i++) {
            Integer siguiente = this.rutasPorId.higherKey(claveActual);
            if (siguiente == null) {
                x.set(claveActual); // Guarda la última clave válida alcanzada
                return false; // No se pudo avanzar x pasos completos
            }
            claveActual = siguiente;
        }
        x.set(claveActual); // Guarda la nueva posición alcanzada
        return true; // Se pudo avanzar x pasos completos
    }

    public Integer obtenerPrimeraClave() {
        if (this.rutasPorId.isEmpty()) {
            return null; // Retorna null si el TreeMap está vacío
        }
        return this.rutasPorId.firstKey(); // Retorna la primera clave del TreeMap
    }

    public Integer obtenerUltimaClave() {
        if (this.rutasPorId.isEmpty()) {
            return null; // Retorna null si el TreeMap está vacío
        }
        return this.rutasPorId.lastKey(); // Retorna la última clave del TreeMap
    }

    public boolean obtenerClaveXClavesAtras(Integer claveInicial, AtomicInteger x) {
        Integer claveActual = claveInicial;
        int pasosDeseados = x.get();
        for (int i = 0; i < pasosDeseados; i++) {
            Integer anterior = this.rutasPorId.lowerKey(claveActual);
            if (anterior == null) {
                x.set(claveActual); // Guarda la última clave válida alcanzada
                return false; // No se pudo retroceder x pasos completos
            }
            claveActual = anterior;
        }
        x.set(claveActual); // Guarda la nueva posición alcanzada
        return true; // Se pudo retroceder x pasos completos
    }
    /*
     * public void actualizarCodigoRuta(Integer id, String nuevoCodigo) {
     * if (rutasPorId.containsKey(id)) {
     * PlanRuta ruta = rutasPorId.get(id);
     * rutasPorCodigo.remove(ruta.getCodigo()); // Eliminar la entrada antigua
     * ruta.setCodigo(nuevoCodigo);
     * rutasPorCodigo.put(nuevoCodigo, ruta); // Añadir la nueva entrada
     * }
     * }
     */
}
