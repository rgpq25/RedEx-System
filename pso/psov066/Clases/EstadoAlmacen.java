package Clases;

import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class EstadoAlmacen {

    private HashMap<Aeropuerto, TreeMap<Date, Integer>> uso_historico;

    public EstadoAlmacen(HashMap<Aeropuerto, TreeMap<Date, Integer>> uso_historico) {
        this.uso_historico = uso_historico;
    }

    public EstadoAlmacen() {
        uso_historico = new HashMap<Aeropuerto, TreeMap<Date, Integer>>();
    }

    public HashMap<Aeropuerto, TreeMap<Date, Integer>> getUso_historico() {
        return uso_historico;
    }

    public EstadoAlmacen(ArrayList<Paquete> paquetes, ArrayList<PlanRuta> plan,
            HashMap<Integer, Vuelo> vuelos, HashMap<Integer, Integer> capacidades, ArrayList<Aeropuerto> aeropuertos) {
        // Mapa para almacenar la historia de capacidad de cada aeropuerto
        this.uso_historico = new HashMap<Aeropuerto, TreeMap<Date, Integer>>();

        // Inicializar el TreeMap para cada aeropuerto
        for (Aeropuerto aeropuerto : aeropuertos) {
            this.uso_historico.put(aeropuerto, new TreeMap<>());
        }
        for (Integer IdVuelo : capacidades.keySet()) {
            // Encuentra el aeropuerto de salida y llegada basado en el vuelo
            Aeropuerto aeropuertoSalida = encontrarAeropuertoPorUbicacion(
                    vuelos.get(IdVuelo).getPlan_vuelo().getCiudadOrigen(), aeropuertos);
            Aeropuerto aeropuertoLlegada = encontrarAeropuertoPorUbicacion(
                    vuelos.get(IdVuelo).getPlan_vuelo().getCiudadDestino(), aeropuertos);

            // Registrar salida de paquete
            registrarCapacidad(aeropuertoSalida, vuelos.get(IdVuelo).getFecha_salida(), -capacidades.get(IdVuelo));

            registrarCapacidad(aeropuertoLlegada, vuelos.get(IdVuelo).getFecha_llegada(), capacidades.get(IdVuelo));
        }
        for (int i = 0; i < paquetes.size(); i++) {

            Aeropuerto aeropuertoSalida = encontrarAeropuertoPorUbicacion(
                    paquetes.get(i).getCiudadOrigen(), aeropuertos);
            Aeropuerto aeropuertoLlegada = encontrarAeropuertoPorUbicacion(
                    paquetes.get(i).getCiudadDestino(), aeropuertos);

            registrarCapacidad(aeropuertoSalida, removeTime(paquetes.get(i).getFecha_recepcion()), 1);
            registrarCapacidad(aeropuertoLlegada, removeTime(plan.get(i).getFin()), -1);
        }
        for (Aeropuerto aeropuerto : aeropuertos) {
            TreeMap<Date, Integer> capacidad = this.uso_historico.get(aeropuerto);
            int sumaAcumulada = 0;
            for (Map.Entry<Date, Integer> entrada : capacidad.entrySet()) {
                sumaAcumulada += entrada.getValue();
                // Actualizar el TreeMap con el valor acumulado
                capacidad.put(entrada.getKey(), sumaAcumulada);
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
    private static Aeropuerto encontrarAeropuertoPorUbicacion(Ubicacion ubicacion, ArrayList<Aeropuerto> aeropuertos) {
        for (Aeropuerto aeropuerto : aeropuertos) {
            if (aeropuerto.getUbicacion().equals(ubicacion)) {
                return aeropuerto;
            }
        }
        return null; // Si no se encuentra ningún aeropuerto con la ubicación dada
    }

    private void registrarCapacidad(Aeropuerto aeropuerto, Date fecha, int cambio) {
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

    public void consulta_historica() {
        for (Aeropuerto aeropuerto : uso_historico.keySet()) {
            System.out.println("Aeropuerto: " + aeropuerto.getId());
            ArrayList<Date> fechasOrdenadas = new ArrayList<>(uso_historico.get(aeropuerto).keySet());
            Collections.sort(fechasOrdenadas);
            for (Date fecha : fechasOrdenadas) {
                System.out.println("Fecha: " + fecha + " | Uso: " + uso_historico.get(aeropuerto).get(fecha));
            }
        }
    }

    public HashMap<Aeropuerto, Integer> verificar_capacidad_en(Date fecha) {
        HashMap<Aeropuerto, Integer> capacidad_hasta = new HashMap<Aeropuerto, Integer>();
        System.out.println(fecha);
        for (Aeropuerto aeropuerto : uso_historico.keySet()) {
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

    public HashMap<Aeropuerto, Integer> verificar_capacidad() {
        HashMap<Aeropuerto, Integer> capacidad_actual = new HashMap<Aeropuerto, Integer>();

        for (Aeropuerto aeropuerto : uso_historico.keySet()) {
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

        for (Aeropuerto aeropuerto : uso_historico.keySet()) {
            for (Date fecha : uso_historico.get(aeropuerto).keySet()) {
                if (uso_historico.get(aeropuerto).get(fecha) > aeropuerto.getCapacidad_maxima()) {
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

        for (Aeropuerto aeropuerto : uso_historico.keySet()) {
            ArrayList<Date> fechasList = new ArrayList<>(uso_historico.get(aeropuerto).keySet());
            Collections.sort(fechasList);

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
                Double porcentaje = (Double) (promedioHoras / aeropuerto.getCapacidad_maxima());
                porcentajes_Uso_Por_hora.put(aeropuerto, porcentaje);
            } else {
                // Manejar el caso donde no hay datos históricos para el aeropuerto
                porcentajes_Uso_Por_hora.put(aeropuerto, 0.0); // Por ejemplo, asignar 0 como porcentaje
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
            Aeropuerto aeropuerto = entrada.getKey();
            Double porcentajeCapacidadMaxima = entrada.getValue();
            // Costo inversamente proporcional al uso si es menor que la capacidad máxima
            costoTotal += porcentajeCapacidadMaxima;
        }

        return costoTotal;
    }

}
