package Clases;

import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.Calendar;

public class EstadoAlmacen {

    private HashMap<Aeropuerto, HashMap<Date, Integer>> uso_historico;

    public EstadoAlmacen(HashMap<Aeropuerto, HashMap<Date, Integer>> uso_historico) {
        this.uso_historico = uso_historico;
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

    public HashMap<Aeropuerto, Integer> verificar_capacidad_hasta(Date fecha) {
        HashMap<Aeropuerto, Integer> capacidad_hasta = new HashMap<Aeropuerto, Integer>();

        for (Aeropuerto aeropuerto : uso_historico.keySet()) {
            int capacidad = 0;
            for (Date fecha_historica : uso_historico.get(aeropuerto).keySet()) {
                if (fecha_historica.before(fecha)) {
                    capacidad = uso_historico.get(aeropuerto).get(fecha_historica);
                    break;
                }
            }
            capacidad_hasta.put(aeropuerto, capacidad);
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

    public long calcular_minutos_entre(Date ini, Date fin) {
        long inicio = ini.getTime();
        long final_ = fin.getTime();
        return ((final_ - inicio) / (1000 * 60));
    }

    public HashMap<Aeropuerto, Integer> consultarPorcentajeUsoPromedioPorHora() {
        HashMap<Aeropuerto, Integer> porcentajes_Uso_Por_hora = new HashMap<>();

        for (Aeropuerto aeropuerto : uso_historico.keySet()) {
            ArrayList<Date> fechasList = new ArrayList<>(uso_historico.get(aeropuerto).keySet());
            Collections.sort(fechasList);

            long totalMinutos = calcular_minutos_entre(fechasList.get(0), fechasList.get(fechasList.size() - 1));

            double totalUso = 0;

            for (int i = 0; i < fechasList.size() - 1; i++) {
                Date fechaActual = fechasList.get(i);
                Date fechaSiguiente = fechasList.get(i + 1);

                long lapso = calcular_minutos_entre(fechaActual, fechaSiguiente);
                int usoActual = uso_historico.get(aeropuerto).get(fechaActual);

                totalUso += usoActual * lapso;
            }

            double promedioHoras = totalUso / totalMinutos * 60;
            int porcentaje = (int) (promedioHoras / aeropuerto.getCapacidad_maxima() * 100);
            porcentajes_Uso_Por_hora.put(aeropuerto, porcentaje);
        }

        return porcentajes_Uso_Por_hora;
    }

}
