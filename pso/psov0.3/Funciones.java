import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import java.util.stream.Collectors;

import Clases.Ubicacion;
import Clases.Vuelo;
import Clases.Aeropuerto;
import Clases.PlanVuelo;



public class Funciones {
    
    public Funciones() {
    }

    public ArrayList<Vuelo> generarVuelos(ArrayList<Aeropuerto> aeropuertos, ArrayList<PlanVuelo> planesVuelo,
            int maxDias) {
        ArrayList<Vuelo> vuelos = new ArrayList<>();

        // Mueve la instancia de Calendar aquí para inicializarla una vez por día.
        Calendar cal = Calendar.getInstance();

        for (int day = 0; day < maxDias; day++) {
            // Utiliza la fecha actual como base y añade la cantidad de días correspondiente
            // a la iteración.
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_MONTH, day);

            for (PlanVuelo plan : planesVuelo) {
                // Configura la hora de partida según el plan.
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(plan.getHora_ciudad_origen().split(":")[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(plan.getHora_ciudad_origen().split(":")[1]));
                Date fechaPartida = cal.getTime();

                // Clona 'cal' para configurar la hora de llegada.
                Calendar calLlegada = (Calendar) cal.clone();
                calLlegada.set(Calendar.HOUR_OF_DAY, Integer.parseInt(plan.getHora_ciudad_destino().split(":")[0]));
                calLlegada.set(Calendar.MINUTE, Integer.parseInt(plan.getHora_ciudad_destino().split(":")[1]));
                Date fechaLlegada = calLlegada.getTime();

                // Comprobar si la fecha de llegada es anterior a la de partida y ajustarla si
                // es necesario.
                if (!fechaLlegada.after(fechaPartida)) {
                    calLlegada.add(Calendar.DAY_OF_MONTH, 1); // Añadir un día a la fecha de llegada.
                    fechaLlegada = calLlegada.getTime();
                }

                // Agrega el vuelo a la lista.
                vuelos.add(new Vuelo(plan, fechaPartida, fechaLlegada));
            }
        }

        return vuelos;
    }


    public ArrayList<PlanVuelo> generarPlanesDeVuelo(ArrayList<Aeropuerto> aeropuertos, int repeticionesPorConexion) {
        Random random = new Random();
        ArrayList<PlanVuelo> planes = new ArrayList<>();

        int idPlan = 0;
        // Generar planes para cada par de aeropuertos
        for (Aeropuerto origen : aeropuertos) {
            for (Aeropuerto destino : aeropuertos) {
                if (!origen.equals(destino)) {
                    for (int i = 0; i < repeticionesPorConexion; i++) {
                        int horaSalidaHora = random.nextInt(24);
                        int horaSalidaMinuto = random.nextInt(60);
                        String horaSalida = String.format("%02d:%02d", horaSalidaHora, horaSalidaMinuto);

                        // Calcular duración del vuelo basada en una lógica ficticia o real (puede ser
                        // basada en distancia u otros factores)
                        int duracionHoras = 1 + random.nextInt(8); // Duración de vuelo de 1 a 8 horas
                        int duracionMinutos = random.nextInt(60);

                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.HOUR_OF_DAY, horaSalidaHora);
                        cal.set(Calendar.MINUTE, horaSalidaMinuto);
                        cal.add(Calendar.HOUR_OF_DAY, duracionHoras);
                        cal.add(Calendar.MINUTE, duracionMinutos);

                        String horaLlegada = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE));

                        int capacidad = 100 + random.nextInt(401); // Capacidades entre 100 y 500

                        PlanVuelo plan = new PlanVuelo(idPlan++, origen.getUbicacion(), destino.getUbicacion(),
                                horaSalida, horaLlegada, capacidad);
                        planes.add(plan);
                    }
                }
            }
        }

        return planes;
    }

    public ArrayList<Ubicacion> generarUbicaciones(int cantidad) {
        // Lista expandida de ubicaciones posibles con zonas horarias ajustadas
        Ubicacion[] posiblesUbicaciones = {
                new Ubicacion("SKBO", "América del Sur", "Colombia", "Bogotá", "bogo", "-5"),
                new Ubicacion("SEQM", "América del Sur", "Ecuador", "Quito", "quit", "-5"),
                new Ubicacion("SVMI", "América del Sur", "Venezuela", "Caracas", "cara", "-4"),
                new Ubicacion("SBBR", "América del Sur", "Brasil", "Brasilia", "bras", "-3"),
                new Ubicacion("SPIM", "América del Sur", "Perú", "Lima", "lima", "-5"),
                new Ubicacion("SLLP", "América del Sur", "Bolivia", "La Paz", "lapa", "-4"),
                new Ubicacion("SCEL", "América del Sur", "Chile", "Santiago de Chile", "sant", "-3"),
                new Ubicacion("SABE", "América del Sur", "Argentina", "Buenos Aires", "buen", "-3"),
                new Ubicacion("SGAS", "América del Sur", "Paraguay", "Asunción", "asun", "-4"),
                new Ubicacion("SUAA", "América del Sur", "Uruguay", "Montevideo", "mont", "-3"),
                new Ubicacion("LATI", "Europa", "Albania", "Tirana", "tira", "+2"),
                new Ubicacion("EDDI", "Europa", "Alemania", "Berlín", "berl", "+2"),
                new Ubicacion("LOWW", "Europa", "Austria", "Viena", "vien", "+2"),
                new Ubicacion("EBCI", "Europa", "Bélgica", "Bruselas", "brus", "+2"),
                new Ubicacion("UMMS", "Europa", "Bielorrusia", "Minsk", "mins", "+3"),
                new Ubicacion("LBSF", "Europa", "Bulgaria", "Sofia", "sofi", "+3"),
                new Ubicacion("LKPR", "Europa", "Checa", "Praga", "prag", "+2"),
                new Ubicacion("LDZA", "Europa", "Croacia", "Zagreb", "zagr", "+2"),
                new Ubicacion("EKCH", "Europa", "Dinamarca", "Copenhague", "cope", "+2")
        };

        ArrayList<Ubicacion> ubicaciones = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < cantidad; i++) {
            int index = random.nextInt(posiblesUbicaciones.length);
            ubicaciones.add(posiblesUbicaciones[index]);
        }
        return ubicaciones;
    }

    public ArrayList<Aeropuerto> generarAeropuertos(ArrayList<Ubicacion> ubicaciones, int capacidadMaxima) {
        ArrayList<Aeropuerto> aeropuertos = new ArrayList<>();
        for (Ubicacion ubicacion : ubicaciones) {
            aeropuertos.add(new Aeropuerto(ubicacion, 0, capacidadMaxima));
        }
        return aeropuertos;
    }

}
