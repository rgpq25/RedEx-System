import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import Clases.Aeropuerto;
import Clases.Paquete;
import Clases.PlanVuelo;
import Clases.RegistroAlmacenamiento;
import Clases.Ubicacion;
import Clases.Vuelo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class Funciones {

    public Funciones() {
    }

    public Aeropuerto[] leerAeropuertos(String inputPath) {
        List<Aeropuerto> aeropuertos_list = new ArrayList<Aeropuerto>();
        Aeropuerto[] aeropuertos = null;
        try {
            File file = new File(inputPath + "/aeropuertos.csv");
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(",");

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                Aeropuerto aeropuerto = new Aeropuerto();
                aeropuerto.setId(parts[0].trim());
                aeropuerto.setCapacidad_maxima(Integer.parseInt(parts[1].trim()));
                aeropuerto.setCapacidad_utilizada(Integer.parseInt(parts[2].trim()));
                aeropuertos_list.add(aeropuerto);
            }

            scanner.close();
            aeropuertos = aeropuertos_list.toArray(new Aeropuerto[aeropuertos_list.size()]);

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
        return aeropuertos;
    }

    public Paquete[] leerPaquetes(String inputPath) {
        List<Paquete> paquetes_list = new ArrayList<Paquete>();
        Paquete[] paquetes = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            File file = new File(inputPath + "/paquetes.csv");
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(",");

            int id = 1;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                Paquete paquete = new Paquete();
                paquete.setId(id);
                paquete.setFecha_recepcion(dateFormat.parse(parts[0].trim()));
                paquete.setId_ciudad_almacen(parts[1].trim());
                paquete.setId_ciudad_origen(parts[1].trim());
                paquete.setId_ciudad_destino(parts[2].trim());
                paquete.setFecha_maxima_entrega(dateFormat.parse(parts[3].trim()));
                paquetes_list.add(paquete);

                id++;
            }

            scanner.close();
            paquetes = paquetes_list.toArray(new Paquete[paquetes_list.size()]);

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return paquetes;
    }

    public Vuelo[] leerVuelos(String inputPath) {
        List<Vuelo> vuelos_list = new ArrayList<Vuelo>();
        Vuelo[] vuelos = null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            File file = new File(inputPath + "/vuelos.csv");
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(",");

            int id = 1;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                Vuelo vuelo = new Vuelo();
                vuelo.setPlan_vuelo(new PlanVuelo());
                vuelo.setId(id);
                vuelo.getPlan_vuelo().setId_ubicacion_origen(parts[0].trim());
                vuelo.getPlan_vuelo().setId_ubicacion_destino(parts[1].trim());
                vuelo.setFecha_salida(dateFormat.parse(parts[2].trim()));
                vuelo.setFecha_llegada(dateFormat.parse(parts[3].trim()));
                vuelo.getPlan_vuelo().setCapacidad_maxima(Integer.parseInt(parts[4].trim()));

                vuelos_list.add(vuelo);
                id++;
            }
            scanner.close();
            vuelos = vuelos_list.toArray(new Vuelo[vuelos_list.size()]);

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return vuelos;
    }

    public void ordenarPaquetes(Paquete[] paquetes) {
        for (int i = 0; i < paquetes.length; i++) {
            for (int j = 0; j < paquetes.length - 1; j++) {
                if (paquetes[j].getFecha_recepcion().compareTo(paquetes[j + 1].getFecha_recepcion()) > 0) {
                    Paquete temp = paquetes[j];
                    paquetes[j] = paquetes[j + 1];
                    paquetes[j + 1] = temp;
                }
            }
        }

    }

    public void ordenarVuelos(Vuelo[] vuelos) {
        for (int i = 0; i < vuelos.length; i++) {
            for (int j = 0; j < vuelos.length - 1; j++) {
                if (vuelos[j].getFecha_salida().compareTo(vuelos[j + 1].getFecha_salida()) > 0) {
                    Vuelo temp = vuelos[j];
                    vuelos[j] = vuelos[j + 1];
                    vuelos[j + 1] = temp;
                }
            }
        }
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
            aeropuertos.add(new Aeropuerto(ubicacion.getID(), ubicacion, 0, capacidadMaxima));
        }
        return aeropuertos;
    }

    public List<Paquete> generarPaquetes(int n, List<Aeropuerto> aeropuertos, int maximo_dias) {
        List<Paquete> paquetes = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            Collections.shuffle(aeropuertos);
            Aeropuerto origen = aeropuertos.get(0);
            Aeropuerto destino = aeropuertos.get(1);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, rand.nextInt(maximo_dias + 1));
            Date fechaRecepcion = (Date) cal.getTime();
            paquetes.add(new Paquete(origen.getId(), origen.getId(), destino.getId(), fechaRecepcion));
        }
        return paquetes;
    }

    public static PlanVuelo[] generarPlanesDeVuelo(Aeropuerto[] aeropuertos, int repeticionesPorConexion) {
        Random random = new Random();
        List<PlanVuelo> planes = new ArrayList<>();

        int idPlan = 0;
        // Generar planes para cada par de aeropuertos
        for (Aeropuerto origen : aeropuertos) {
            for (Aeropuerto destino : aeropuertos) {
                if (!origen.equals(destino)) {
                    for (int i = 0; i < repeticionesPorConexion; i++) {
                        String horaSalida = String.format("%02d:%02d", random.nextInt(24), random.nextInt(60));
                        String horaLlegada = String.format("%02d:%02d", random.nextInt(24), random.nextInt(60));
                        int capacidad = 100 + random.nextInt(401); // Capacidades entre 100 y 500

                        PlanVuelo plan = new PlanVuelo(idPlan++, origen.getId(), destino.getId(), horaSalida,
                                horaLlegada, capacidad);
                        planes.add(plan);
                    }
                }
            }
        }

        return planes.toArray(new PlanVuelo[0]);
    }

    public static Vuelo[] generarVuelos(Aeropuerto[] aeropuertos, PlanVuelo[] planesVuelo, int maxDias) {
        ArrayList<Vuelo> vuelos = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        for (int day = 0; day < maxDias; day++) {
            for (PlanVuelo plan : planesVuelo) {
                cal.setTime(new Date(System.currentTimeMillis()));
                cal.add(Calendar.DAY_OF_MONTH, day);
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(plan.getHora_ciudad_origen().split(":")[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(plan.getHora_ciudad_origen().split(":")[1]));
                Date fechaPartida = (Date) cal.getTime();

                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(plan.getHora_ciudad_destino().split(":")[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(plan.getHora_ciudad_destino().split(":")[1]));
                Date fechaLlegada = (Date) cal.getTime();

                vuelos.add(new Vuelo(plan, fechaPartida, fechaLlegada));
            }
        }

        return vuelos.toArray(new Vuelo[0]);
    }

    public void asignarVuelosAPaquetes(List<Paquete> paquetes, List<Vuelo> vuelos) {
        for (Paquete paquete : paquetes) {
            String currentLocation = paquete.getId_ciudad_origen();
            Set<String> visitados = new HashSet<>();
            visitados.add(currentLocation);

            while (!currentLocation.equals(paquete.getId_ciudad_destino())) {
                final String finalCurrentLocation = currentLocation;
                List<Vuelo> vuelosPosibles = vuelos.stream()
                        .filter(v -> v.getPlan_vuelo().getId_ubicacion_origen().equals(finalCurrentLocation)
                                && !visitados.contains(v.getPlan_vuelo().getId_ubicacion_destino()))
                        .collect(Collectors.toList());

                if (vuelosPosibles.isEmpty()) {
                    System.out.println("No hay más vuelos disponibles desde " + currentLocation
                            + " que cumplan con los requisitos.");
                    break;
                }

                Vuelo posibleVuelo = vuelosPosibles.get(new Random().nextInt(vuelosPosibles.size()));
                paquete.agregar_vuelo(posibleVuelo);
                currentLocation = posibleVuelo.getPlan_vuelo().getId_ubicacion_destino();
                visitados.add(currentLocation);

                if (currentLocation.equals(paquete.getId_ciudad_destino())) {
                    break;
                }
            }
        }
    }

    public ArrayList<RegistroAlmacenamiento> crearRegistrosAlmacenamiento(ArrayList<Paquete> paquetes) {
        ArrayList<RegistroAlmacenamiento> almacenamiento = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (Paquete paquete : paquetes) {
            Date currentTime = paquete.getFecha_recepcion();
            for (Vuelo vuelo : paquete.getListaVuelos()) {
                if (currentTime.before(vuelo.getFecha_salida())) {
                    almacenamiento.add(new RegistroAlmacenamiento(paquete.getId(),
                            currentTime,
                            vuelo.getFecha_salida(),
                            vuelo.getPlan_vuelo().getId_ubicacion_origen()));
                }
                currentTime = vuelo.getFecha_llegada();
            }
            // Final storage for 1 minute after the last flight
            calendar.setTime(currentTime);
            calendar.add(Calendar.MINUTE, 1); // Añadir un minuto a la última hora de llegada
            Date finalTime = calendar.getTime(); // Obtener la fecha actualizada

            almacenamiento.add(new RegistroAlmacenamiento(paquete.getId(),
                    currentTime,
                    finalTime,
                    paquete.getId_ciudad_destino()));
        }
        return almacenamiento;
    }

}