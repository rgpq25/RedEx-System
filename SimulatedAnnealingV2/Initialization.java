import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Clases.Paquete;
import Clases.PlanRuta;
import Clases.Vuelo;

public class Initialization {

    public PlanRuta encontrarRuta(String origenPaquete, String destinoPaquete, LocalDate fechaAparicionPaquete, LocalDate fechaMaximaLlegadaPaquete, List<Vuelo> vuelosDisponibles){
            // Lista para almacenar las rutas encontradas
            List<PlanRuta> rutasEncontradas = new ArrayList<>();
    
            // Recorrer vuelos disponibles
            for (Vuelo vueloActual : vuelosDisponibles) {
                // Verificar si el vuelo sale en la fecha de aparición del paquete o después
                if (vueloActual.getFecha_salida().isAfter(fechaAparicionPaquete) || vueloActual.getFecha_salida().equals(fechaAparicionPaquete)) {
                    // Verificar si el vuelo llega antes o en la fecha máxima de llegada del paquete
                    if (vueloActual.getFecha_llegada().isBefore(fechaMaximaLlegadaPaquete) || vueloActual.getFecha_llegada().equals(fechaMaximaLlegadaPaquete)) {
                        // Crear una nueva ruta con el vuelo actual
                        List<Vuelo> rutaActual = new ArrayList<>();
                        rutaActual.add(vueloActual);
                        PlanRuta rutaPaqueteActual = new PlanRuta(rutaActual, vueloActual.fechaLlegada);
    
                        // Agregar la ruta a la lista de rutas encontradas
                        rutasEncontradas.add(rutaPaqueteActual);
    
                        // Verificar si el destino del vuelo actual es el destino del paquete
                        if (vueloActual.getPlan_vuelo().getId_ubicacion_destino().equals(destinoPaquete)) {
                            // Se encontró una ruta que llega al destino directamente, devolverla
                            return rutaPaqueteActual;
                        } else {
                            // Buscar rutas recursivamente desde el destino del vuelo actual
                            PlanRuta rutaRecursiva = encontrarRuta(vueloActual.getPlan_vuelo().getId_ubicacion_destino(), destinoPaquete, vueloActual.getFecha_llegada(), fechaMaximaLlegadaPaquete, vuelosDisponibles);
    
                            // Si se encuentra una ruta recursiva, combinarla con la ruta actual y agregarla a la lista
                            if (rutaRecursiva != null) {
                                rutaActual.addAll(rutaRecursiva.getVuelos());
                                rutasEncontradas.add(rutaPaqueteActual);
                            }
                        }
                    }
                }
            }
    
            // Si no se encontraron rutas, devolver null
            if (rutasEncontradas.isEmpty()) {
                return null;
            }
    
            // Devolver la ruta con la fecha de llegada estimada más temprana
            PlanRuta rutaMejor = rutasEncontradas.get(0);
            for (PlanRuta ruta : rutasEncontradas) {
                if (ruta.fechaLlegadaEstimada.isBefore(rutaMejor.fechaLlegadaEstimada)) {
                    rutaMejor = ruta;
                }
            }
            //return rutaMejor;
    }
}
