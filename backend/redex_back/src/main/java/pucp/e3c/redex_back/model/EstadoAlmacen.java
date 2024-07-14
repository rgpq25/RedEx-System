package pucp.e3c.redex_back.model;

import java.util.Date;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map.Entry;
import java.util.SortedMap;

@Component
public class EstadoAlmacen {

    private HashMap<String, TreeMap<Date, Integer>> uso_historico;
    private ArrayList<Aeropuerto> aeropuertos;

    private static final Logger LOGGER = LoggerFactory.getLogger(EstadoAlmacen.class);

    // Cambiar esto a deepClone
    public EstadoAlmacen(HashMap<String, TreeMap<Date, Integer>> uso_historico) {
        this.uso_historico = deepClone(uso_historico);
    }

    // Método para realizar una copia profunda de un HashMap<String, TreeMap<Date,
    // Integer>>
    private HashMap<String, TreeMap<Date, Integer>> deepClone(HashMap<String, TreeMap<Date, Integer>> original) {
        HashMap<String, TreeMap<Date, Integer>> clone = new HashMap<>();
        for (Map.Entry<String, TreeMap<Date, Integer>> entry : original.entrySet()) {
            String key = entry.getKey();
            TreeMap<Date, Integer> value = new TreeMap<>(entry.getValue());
            clone.put(key, value);
        }
        return clone;
    }

    public EstadoAlmacen() {
        uso_historico = new HashMap<String, TreeMap<Date, Integer>>();
        aeropuertos = null;
    }

    public EstadoAlmacen(ArrayList<Aeropuerto> _aeropuertos) {
        uso_historico = new HashMap<String, TreeMap<Date, Integer>>();
        this.aeropuertos = _aeropuertos;
    }

    public EstadoAlmacen(EstadoAlmacen estado) {
        uso_historico = estado.getUso_historico();
    }

    public HashMap<String, TreeMap<Date, Integer>> getUso_historico() {
        return uso_historico;
    }

    public ArrayList<Aeropuerto> getAeropuertos() {
        return aeropuertos;
    }

    public void setAeropuertos(ArrayList<Aeropuerto> aeropuertos) {
        this.aeropuertos = aeropuertos;
    }

    public EstadoAlmacen(ArrayList<Paquete> paquetes, ArrayList<PlanRutaNT> plan,
            HashMap<Integer, Vuelo> vuelos, HashMap<Integer, Integer> capacidades, ArrayList<Aeropuerto> _aeropuertos) {
        // LOGGER.info("EstadoAlmacen Constructor");
        this.aeropuertos = _aeropuertos;
        // Mapa para almacenar la historia de capacidad de cada aeropuerto
        this.uso_historico = new HashMap<String, TreeMap<Date, Integer>>();
        ArrayList<String> aeropuertos = _aeropuertos.stream()
                .map(aeropuerto -> aeropuerto.getUbicacion().getId())
                .collect(Collectors.toCollection(ArrayList::new));
        // Inicializar el TreeMap para cada aeropuerto
        for (String aeropuerto : aeropuertos) {
            this.uso_historico.put(aeropuerto, new TreeMap<>());
        }
        for (Integer IdVuelo : capacidades.keySet()) {
            // Encuentra el aeropuerto de salida y llegada basado en el vuelo
            String aeropuertoSalida = vuelos.get(IdVuelo).getPlanVuelo().getCiudadOrigen().getId();
            String aeropuertoLlegada = vuelos.get(IdVuelo).getPlanVuelo().getCiudadDestino().getId();
            /*
             * if(aeropuertoSalida.equals("OAKB")){
             * LOGGER.info("EstadoAlmacen Vuelo " + vuelos.get(IdVuelo).getId() +
             * " Aeropuerto de salida: " + aeropuertoSalida + " cambio capacidad: -" +
             * capacidades.get(IdVuelo) + " - fecha: " +
             * vuelos.get(IdVuelo).getFechaSalida());
             * }
             * if(aeropuertoLlegada.equals("OAKB")){
             * LOGGER.info("EstadoAlmacen Vuelo " + vuelos.get(IdVuelo).getId() +
             * "Aeropuerto de llegada: " + aeropuertoLlegada + " cambio capacidad: +" +
             * capacidades.get(IdVuelo) + " - fecha: " +
             * vuelos.get(IdVuelo).getFechaLlegada());
             * }
             */
            // Registrar salida de paquete
            registrarCapacidad(aeropuertoSalida, vuelos.get(IdVuelo).getFechaSalida(), -capacidades.get(IdVuelo));

            registrarCapacidad(aeropuertoLlegada, vuelos.get(IdVuelo).getFechaLlegada(), capacidades.get(IdVuelo));

        }
        for (int i = 0; i < paquetes.size(); i++) {
            String aeropuertoSalida = paquetes.get(i).getEnvio().getUbicacionOrigen().getId();
            String aeropuertoLlegada = paquetes.get(i).getEnvio().getUbicacionDestino().getId();
            /*
             * if(aeropuertoSalida.equals("OAKB")){
             * LOGGER.info("EstadoAlmacen Paquete "+ paquetes.get(i).getId() +" Envio "+
             * paquetes.get(i).getEnvio().getId() +" Aeropuerto de salida: " +
             * aeropuertoSalida + " cambio +1 - fecha: " +
             * paquetes.get(i).getEnvio().getFechaRecepcion());
             * }
             */

            registrarCapacidad(aeropuertoSalida, removeTime(paquetes.get(i).getEnvio().getFechaRecepcion()), 1);

            if (plan.get(i) == null || plan.get(i).getVuelos().size() == 0) {
                continue;
            }

            Date newDate = removeTime(plan.get(i).getFin());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(newDate);
            calendar.add(Calendar.MINUTE, 1);
            newDate = calendar.getTime();
            registrarCapacidad(aeropuertoLlegada, newDate, -1);
            /*
             * if(aeropuertoLlegada.equals("OAKB")){
             * LOGGER.info("EstadoAlmacen Paquete "+ paquetes.get(i).getId() +" Envio "+
             * paquetes.get(i).getEnvio().getId() +" Aeropuerto de llegada: " +
             * aeropuertoLlegada + " cambio -1 - fecha: " + plan.get(i).getFin());
             * }
             */
        }
        for (String aeropuerto : aeropuertos) {
            TreeMap<Date, Integer> capacidad = this.uso_historico.get(aeropuerto);
            int sumaAcumulada = 0;
            for (Map.Entry<Date, Integer> entrada : capacidad.entrySet()) {
                sumaAcumulada += entrada.getValue();
                // Actualizar el TreeMap con el valor acumulado
                capacidad.put(entrada.getKey(), sumaAcumulada);
                if (sumaAcumulada < 0) {
                    // throw new IllegalArgumentException("sumaAcumulada cannot be negative");
                    // System.out.println("Suma acumulada cannot be negative");
                }
            }

        }
    }

    public void agregarAEstadoActual(ArrayList<Paquete> paquetes, ArrayList<PlanRutaNT> plan,
            HashMap<Integer, Vuelo> vuelos, HashMap<Integer, Integer> capacidades, ArrayList<Aeropuerto> _aeropuertos) {
        // LOGGER.info("EstadoAlmacen Constructor");
        this.aeropuertos = _aeropuertos;
        // Mapa para almacenar la historia de capacidad de cada aeropuerto
        ArrayList<String> aeropuertos = _aeropuertos.stream()
                .map(aeropuerto -> aeropuerto.getUbicacion().getId())
                .collect(Collectors.toCollection(ArrayList::new));
        // Inicializar el TreeMap para cada aeropuerto
        for (String aeropuerto : aeropuertos) {
            this.uso_historico.put(aeropuerto, new TreeMap<>());
        }
        for (Integer IdVuelo : capacidades.keySet()) {
            // Encuentra el aeropuerto de salida y llegada basado en el vuelo
            String aeropuertoSalida = vuelos.get(IdVuelo).getPlanVuelo().getCiudadOrigen().getId();
            String aeropuertoLlegada = vuelos.get(IdVuelo).getPlanVuelo().getCiudadDestino().getId();
            /*
             * if(aeropuertoSalida.equals("OAKB")){
             * LOGGER.info("EstadoAlmacen Vuelo " + vuelos.get(IdVuelo).getId() +
             * " Aeropuerto de salida: " + aeropuertoSalida + " cambio capacidad: -" +
             * capacidades.get(IdVuelo) + " - fecha: " +
             * vuelos.get(IdVuelo).getFechaSalida());
             * }
             * if(aeropuertoLlegada.equals("OAKB")){
             * LOGGER.info("EstadoAlmacen Vuelo " + vuelos.get(IdVuelo).getId() +
             * "Aeropuerto de llegada: " + aeropuertoLlegada + " cambio capacidad: +" +
             * capacidades.get(IdVuelo) + " - fecha: " +
             * vuelos.get(IdVuelo).getFechaLlegada());
             * }
             */
            // Registrar salida de paquete
            registrarCapacidad(aeropuertoSalida, vuelos.get(IdVuelo).getFechaSalida(), -capacidades.get(IdVuelo));

            registrarCapacidad(aeropuertoLlegada, vuelos.get(IdVuelo).getFechaLlegada(), capacidades.get(IdVuelo));

        }
        for (int i = 0; i < paquetes.size(); i++) {
            String aeropuertoSalida = paquetes.get(i).getEnvio().getUbicacionOrigen().getId();
            String aeropuertoLlegada = paquetes.get(i).getEnvio().getUbicacionDestino().getId();
            /*
             * if(aeropuertoSalida.equals("OAKB")){
             * LOGGER.info("EstadoAlmacen Paquete "+ paquetes.get(i).getId() +" Envio "+
             * paquetes.get(i).getEnvio().getId() +" Aeropuerto de salida: " +
             * aeropuertoSalida + " cambio +1 - fecha: " +
             * paquetes.get(i).getEnvio().getFechaRecepcion());
             * }
             */

            registrarCapacidad(aeropuertoSalida, removeTime(paquetes.get(i).getEnvio().getFechaRecepcion()), 1);

            if (plan.get(i) == null || plan.get(i).getVuelos().size() == 0) {
                continue;
            }

            Date newDate = removeTime(plan.get(i).getFin());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(newDate);
            calendar.add(Calendar.MINUTE, 1);
            newDate = calendar.getTime();
            registrarCapacidad(aeropuertoLlegada, newDate, -1);
            /*
             * if(aeropuertoLlegada.equals("OAKB")){
             * LOGGER.info("EstadoAlmacen Paquete "+ paquetes.get(i).getId() +" Envio "+
             * paquetes.get(i).getEnvio().getId() +" Aeropuerto de llegada: " +
             * aeropuertoLlegada + " cambio -1 - fecha: " + plan.get(i).getFin());
             * }
             */
        }
        for (String aeropuerto : aeropuertos) {
            TreeMap<Date, Integer> capacidad = this.uso_historico.get(aeropuerto);
            int sumaAcumulada = 0;
            for (Map.Entry<Date, Integer> entrada : capacidad.entrySet()) {
                sumaAcumulada += entrada.getValue();
                // Actualizar el TreeMap con el valor acumulado
                capacidad.put(entrada.getKey(), sumaAcumulada);
                if (sumaAcumulada < 0) {
                    // throw new IllegalArgumentException("sumaAcumulada cannot be negative");
                    // System.out.println("Suma acumulada cannot be negative");
                }
            }

        }
    }

    public void sumaCalculada() {
        ArrayList<String> aeropuertos = this.aeropuertos.stream()
                .map(aeropuerto -> aeropuerto.getUbicacion().getId())
                .collect(Collectors.toCollection(ArrayList::new));
        for (String aeropuerto : aeropuertos) {
            TreeMap<Date, Integer> capacidad = this.uso_historico.get(aeropuerto);
            int sumaAcumulada = 0;
            for (Map.Entry<Date, Integer> entrada : capacidad.entrySet()) {
                sumaAcumulada += entrada.getValue();
                // Actualizar el TreeMap con el valor acumulado
                capacidad.put(entrada.getKey(), sumaAcumulada);
                if (sumaAcumulada < 0) {
                    // throw new IllegalArgumentException("sumaAcumulada cannot be negative");
                    // System.out.println("Suma acumulada cannot be negative");
                }
            }

        }
    }

    private Date removeTime(Date date) {
        // Obtener una instancia de Calendar y establecer la fecha dada
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Ajustar las horas, minutos, segundos y milisegundos a cero
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Obtener la nueva fecha sin tiempo
        return calendar.getTime();
    }

    // Métodos auxiliares para encontrar aeropuertos y registrar capacidades
    private Aeropuerto encontrarAeropuertoPorUbicacion(String ubicacion) {
        for (Aeropuerto aeropuerto : this.aeropuertos) {
            if (aeropuerto.getUbicacion().getId().equals(ubicacion)) {
                return aeropuerto;
            }
        }
        return null; // Si no se encuentra ningún aeropuerto con la ubicación dada
    }

    public void registrarCapacidad(String aeropuerto, Date fecha, int cambio) {
        if (aeropuerto == null) {
            throw new IllegalArgumentException("Aeropuerto no puede ser null");
        }
        TreeMap<Date, Integer> capacidades = this.uso_historico.get(aeropuerto);
        if (capacidades == null) {
            capacidades = new TreeMap<>();
            this.uso_historico.put(aeropuerto, capacidades);
        }
        // System.out.println(
        // "Registrando capacidad: Aeropuerto=" + aeropuerto.getId() + ", Fecha=" +
        // fecha + ", Cambio=" + cambio);
        Integer capacidadActual = capacidades.getOrDefault(fecha, 0);
        capacidades.put(fecha, capacidadActual + cambio);
    }

    public void registrarCapacidadOperacionesDiaDia(String aeropuerto, Date fecha, int cambio) {
        if (aeropuerto == null) {
            throw new IllegalArgumentException("Aeropuerto no puede ser null");
        }
        TreeMap<Date, Integer> capacidades = this.uso_historico.get(aeropuerto);
        if (capacidades == null) {
            capacidades = new TreeMap<>();
            this.uso_historico.put(aeropuerto, capacidades);
        }
        Entry<Date, Integer> entry = capacidades.floorEntry(fecha);
        int capacidadActual = (entry != null) ? entry.getValue() : 0;
        capacidades.put(fecha, capacidadActual + cambio);

        SortedMap<Date, Integer> fechasPosteriores = capacidades.tailMap(fecha, false);
        for (Map.Entry<Date, Integer> e : fechasPosteriores.entrySet()) {
            e.setValue(e.getValue() + cambio);
        }
    }

    public void consulta_historica() {
        for (String aeropuerto : uso_historico.keySet()) {
            System.out.println("Ubicacion: " + aeropuerto);
            ArrayList<Date> fechasOrdenadas = new ArrayList<>(uso_historico.get(aeropuerto).keySet());
            Collections.sort(fechasOrdenadas);
            for (Date fecha : fechasOrdenadas) {
                System.out.println("Fecha: " + fecha + " | Uso: " + uso_historico.get(aeropuerto).get(fecha));
            }
        }
    }

    public void consulta_historicaTxt(String filename) {
        try {
            // Crear un objeto PrintWriter para escribir en el archivo
            PrintWriter writer = new PrintWriter(new File(filename));

            // Recorrer el mapa uso_historico
            for (String aeropuerto : uso_historico.keySet()) {
                writer.println("Aeropuerto: " + aeropuerto);
                ArrayList<Date> fechasOrdenadas = new ArrayList<>(uso_historico.get(aeropuerto).keySet());
                Collections.sort(fechasOrdenadas);
                for (Date fecha : fechasOrdenadas) {
                    writer.println("Fecha: " + Funciones.getFormattedDate(fecha) + " | Uso: "
                            + uso_historico.get(aeropuerto).get(fecha));
                }
            }

            // Cerrar el PrintWriter
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Integer> verificar_capacidad_en(Date fecha) {
        HashMap<String, Integer> capacidad_hasta = new HashMap<String, Integer>();
        System.out.println(fecha);
        for (String aeropuerto : uso_historico.keySet()) {
            TreeMap<Date, Integer> registros = uso_historico.get(aeropuerto);
            Date fecha_cercana = null;
            for (Date fecha_historica : registros.keySet()) {
                if (fecha_historica.before(fecha) || fecha_historica.equals(fecha)) {
                    if (fecha_cercana == null || fecha_historica.after(fecha_cercana)) {
                        fecha_cercana = fecha_historica;
                    }
                }
            }
            if (fecha_cercana != null) {
                capacidad_hasta.put(aeropuerto, registros.get(fecha_cercana));
            } else {
                capacidad_hasta.put(aeropuerto, 0);
            }
        }

        return capacidad_hasta;
    }

    public HashMap<String, Integer> verificar_capacidad() {
        HashMap<String, Integer> capacidad_actual = new HashMap<String, Integer>();

        for (String aeropuerto : uso_historico.keySet()) {
            ArrayList<Date> fechas = new ArrayList<Date>(uso_historico.get(aeropuerto).keySet());
            Collections.sort(fechas, new Comparator<Date>() {
                public int compare(Date a, Date b) {
                    return a.compareTo(b);
                }
            });

            capacidad_actual.put(aeropuerto, uso_historico.get(aeropuerto).get(fechas.get(fechas.size() - 1)));
        }

        return capacidad_actual;

    }

    public boolean verificar_capacidad_maxima() {

        for (String aeropuerto : uso_historico.keySet()) {
            for (Date fecha : uso_historico.get(aeropuerto).keySet()) {
                if (uso_historico.get(aeropuerto).get(fecha) > encontrarAeropuertoPorUbicacion(aeropuerto)
                        .getCapacidadMaxima()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean verificar_capacidad_maxima_hasta(Date limite) {

        for (String aeropuerto : uso_historico.keySet()) {
            for (Date fecha : uso_historico.get(aeropuerto).keySet()) {
                if (fecha.after(limite)) {
                    continue;
                }
                if (uso_historico.get(aeropuerto).get(fecha) > encontrarAeropuertoPorUbicacion(aeropuerto)
                        .getCapacidadMaxima()) {
                    return false;
                }
            }
        }
        return true;
    }

    private long calcular_minutos_entre(Date ini, Date fin) {
        long inicio = ini.getTime();
        long final_ = fin.getTime();
        return ((final_ - inicio) / (1000 * 60));
    }

    public HashMap<Aeropuerto, Double> consultarPorcentajeUsoPromedioPorHora() {
        HashMap<Aeropuerto, Double> porcentajes_Uso_Por_hora = new HashMap<>();

        for (String aeropuerto : uso_historico.keySet()) {
            ArrayList<Date> fechasList = new ArrayList<>(uso_historico.get(aeropuerto).keySet());
            Collections.sort(fechasList);
            Aeropuerto aeropuerto_onb = encontrarAeropuertoPorUbicacion(aeropuerto);

            if (!fechasList.isEmpty()) { // Verificar que la lista no esté vacía
                long totalMinutos = calcular_minutos_entre(fechasList.get(0), fechasList.get(fechasList.size() - 1));
                double totalUso = 0;

                for (int i = 0; i < fechasList.size() - 2; i++) {
                    Date fechaActual = fechasList.get(i);
                    Date fechaSiguiente = fechasList.get(i + 1);
                    long lapso = calcular_minutos_entre(fechaActual, fechaSiguiente);
                    int usoActual = uso_historico.get(aeropuerto).get(fechaActual);

                    totalUso += usoActual * lapso;
                }
                double promedioHoras = (totalUso / totalMinutos) / 60;
                Double porcentaje = (Double) (promedioHoras / aeropuerto_onb.getCapacidadMaxima());
                porcentajes_Uso_Por_hora.put(aeropuerto_onb, porcentaje);
            } else {
                // Manejar el caso donde no hay datos históricos para el aeropuerto
                porcentajes_Uso_Por_hora.put(aeropuerto_onb, 0.0); // Por ejemplo, asignar 0 como porcentaje
            }
        }

        return porcentajes_Uso_Por_hora;
    }

    public double calcularCostoTotalAlmacenamiento() {
        if (!verificar_capacidad_maxima()) {
            return 10000; // Penalización por exceder la capacidad máxima
        }
        HashMap<Aeropuerto, Double> porcentajesUso = consultarPorcentajeUsoPromedioPorHora();
        double costoTotal = 0.0;

        for (Map.Entry<Aeropuerto, Double> entrada : porcentajesUso.entrySet()) {
            Double porcentajeCapacidadMaxima = entrada.getValue();
            if (porcentajeCapacidadMaxima != null && !porcentajeCapacidadMaxima.isNaN()) {
                costoTotal += porcentajeCapacidadMaxima;
            }
        }
        return costoTotal;
    }

    @Override
    public String toString() {
        return "EstadoAlmacen{" + "uso_historico=" + uso_historico + '}';
    }
}
