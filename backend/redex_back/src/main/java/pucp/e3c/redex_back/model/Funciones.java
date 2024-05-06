package pucp.e3c.redex_back.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.TimeZone;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Funciones {
    
    public static String getFormattedDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static int stringGmt2Int(String gmt) {
        return Integer.parseInt(gmt.replace("GMT", ""));
    }

    public static void printLineInLog(String line) {
        try {
            FileWriter fw = new FileWriter("finalExpNumResults.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(line + "\n");
            bw.close();
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    public static void printRutasTXT(ArrayList<Paquete> paquetes, ArrayList<PlanRutaNT> rutas, String filename) {
        File csvFile = new File(filename);
        PrintWriter out;

        try {
            out = new PrintWriter(csvFile);
            out.println("Solucion resultado impresa en fecha " + Funciones.getFormattedDate(new Date()));
            for (int i = 0; i < paquetes.size(); i++) {
                String origen_paquete = paquetes.get(i).getEnvio().getUbicacionOrigen().getId();
                String destino_paquete = paquetes.get(i).getEnvio().getUbicacionDestino().getId();
                Date fecha_recepcion = paquetes.get(i).getEnvio().getFechaRecepcion();
                Date fecha_maxima = paquetes.get(i).getEnvio().getFechaLimiteEntrega();
                out.println(
                        "Paquete " + (i + 1) + " - " + origen_paquete + "-" + destino_paquete + "  |  "
                                + Funciones.getFormattedDate(fecha_recepcion) + " => "
                                + Funciones.getFormattedDate(fecha_maxima));
                for (int j = 0; j < rutas.get(i).length(); j++) {
                    String id_origen = rutas.get(i).getVuelos().get(j).getPlanVuelo().getCiudadOrigen().getId();
                    String id_destino = rutas.get(i).getVuelos().get(j).getPlanVuelo().getCiudadDestino().getId();
                    Date fecha_salida = rutas.get(i).getVuelos().get(j).getFechaSalida();
                    Date fecha_llegada = rutas.get(i).getVuelos().get(j).getFechaLlegada();

                    out.println(
                            "         " + id_origen + "-" + id_destino + " " + Funciones.getFormattedDate(fecha_salida)
                                    + " - " + Funciones.getFormattedDate(fecha_llegada));
                }
                out.println();
            }

            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    
    public static HashMap<String, Ubicacion> getUbicacionMap() {
        int maxAirports = 40;
        HashMap<String, Ubicacion> ubicacionMap = new HashMap<String, Ubicacion>();
        Ubicacion ubicacion = new Ubicacion();
        if (maxAirports >= 1){
            ubicacion = new Ubicacion();
            ubicacion.fillData("SKBO", "America del Sur", "Colombia", "Bogota", "bogo", "GMT-5");
            ubicacionMap.put("SKBO", ubicacion);
        }
        if (maxAirports >= 2){
            ubicacion = new Ubicacion();
            ubicacion.fillData("SEQM", "America del Sur", "Ecuador", "Quito", "quit", "GMT-5");
            ubicacionMap.put("SEQM", ubicacion);
        }
        if (maxAirports >= 3){
            ubicacion = new Ubicacion();
            ubicacion.fillData("SVMI", "America del Sur", "Venezuela", "Caracas", "cara", "GMT-4");
            ubicacionMap.put("SVMI", ubicacion);
        }
        if (maxAirports >= 4){
            ubicacion = new Ubicacion();
            ubicacion.fillData("SBBR", "America del Sur", "Brasil", "Brasilia", "bras", "GMT-3");
            ubicacionMap.put("SBBR", ubicacion);
        }
        if (maxAirports >= 5){
            ubicacion = new Ubicacion();
            ubicacion.fillData("SPIM", "America del Sur", "Perú", "Lima", "lima", "GMT-5");
            ubicacionMap.put("SPIM", ubicacion);
        }
        if (maxAirports >= 6){
            ubicacion = new Ubicacion();
            ubicacion.fillData("SLLP", "America del Sur", "Bolivia", "La Paz", "lapa", "GMT-4");
            ubicacionMap.put("SLLP", ubicacion);
        }
        if (maxAirports >= 7){
            ubicacion = new Ubicacion();
            ubicacion.fillData("SCEL", "America del Sur", "Chile", "Santiago de Chile", "sant", "GMT-3");
            ubicacionMap.put("SCEL", ubicacion);
        }
        if (maxAirports >= 8){
            ubicacion = new Ubicacion();
            ubicacion.fillData("SABE", "America del Sur", "Argentina", "Buenos Aires", "buen", "GMT-3");
            ubicacionMap.put("SABE", ubicacion);
        }
        if (maxAirports >= 9){
            ubicacion = new Ubicacion();
            ubicacion.fillData("SGAS", "America del Sur", "Paraguay", "Asunción", "asun", "GMT-4");
            ubicacionMap.put("SGAS", ubicacion);
        }
        if (maxAirports >= 10){
            ubicacion = new Ubicacion();
            ubicacion.fillData("SUAA", "America del Sur", "Uruguay", "Montevideo", "mont", "GMT-3");
            ubicacionMap.put("SUAA", ubicacion);
        }
        if (maxAirports >= 11){
            ubicacion = new Ubicacion();
            ubicacion.fillData("LATI", "Europa", "Albania", "Tirana", "tira", "GMT+2");
            ubicacionMap.put("LATI", ubicacion);
        }
        if (maxAirports >= 12){
            ubicacion = new Ubicacion();
            ubicacion.fillData("EDDI", "Europa", "Alemania", "Berlin", "berl", "GMT+2");
            ubicacionMap.put("EDDI", ubicacion);
        }
        if (maxAirports >= 13){
            ubicacion = new Ubicacion();
            ubicacion.fillData("LOWW", "Europa", "Austria", "Viena", "vien", "GMT+2");
            ubicacionMap.put("LOWW", ubicacion);
        }
        if (maxAirports >= 14){
            ubicacion = new Ubicacion();
            ubicacion.fillData("EBCI", "Europa", "Belgica", "Bruselas", "brus", "GMT+2");
            ubicacionMap.put("EBCI", ubicacion);
        }
        if (maxAirports >= 15){
            ubicacion = new Ubicacion();
            ubicacion.fillData("UMMS", "Europa", "Bielorrusia", "Minsk", "mins", "GMT+3");
            ubicacionMap.put("UMMS", ubicacion);
        }
        if (maxAirports >= 16){
            ubicacion = new Ubicacion();
            ubicacion.fillData("LBSF", "Europa", "Bulgaria", "Sofia", "sofi", "GMT+3");
            ubicacionMap.put("LBSF", ubicacion);
        }
        if (maxAirports >= 17){
            ubicacion = new Ubicacion();
            ubicacion.fillData("LKPR", "Europa", "Checa", "Praga", "prag", "GMT+2");
            ubicacionMap.put("LKPR", ubicacion);
        }
        if (maxAirports >= 18){
            ubicacion = new Ubicacion();
            ubicacion.fillData("LDZA", "Europa", "Croacia", "Zagreb", "zagr", "GMT+2");
            ubicacionMap.put("LDZA", ubicacion);
        }
        if (maxAirports >= 19){
            ubicacion = new Ubicacion();
            ubicacion.fillData("EKCH", "Europa", "Dinamarca", "Copenhague", "cope", "GMT+2");
            ubicacionMap.put("EKCH", ubicacion);
        }
        if (maxAirports >= 20){
            ubicacion = new Ubicacion();
            ubicacion.fillData("EHAM", "Europa", "Holanda", "Amsterdam", "amst", "GMT+2");
            ubicacionMap.put("EHAM", ubicacion);
        }
        if (maxAirports >= 21){
            ubicacion = new Ubicacion();
            ubicacion.fillData("VIDP", "Asia", "India", "Delhi", "delh", "GMT+5");
            ubicacionMap.put("VIDP", ubicacion);
        }
        if (maxAirports >= 22){
            ubicacion = new Ubicacion();
            ubicacion.fillData("RKSI", "Asia", "Corea del Sur", "Seul", "seul", "GMT+9");
            ubicacionMap.put("RKSI", ubicacion);
        }
        if (maxAirports >= 23){
            ubicacion = new Ubicacion();
            ubicacion.fillData("VTBS", "Asia", "Tailandia", "Bangkok", "bang", "GMT+7");
            ubicacionMap.put("VTBS", ubicacion);
        }
        if (maxAirports >= 24){
            ubicacion = new Ubicacion();
            ubicacion.fillData("OMDB", "Asia", "Emiratos A.U", "Dubai", "emir", "GMT+4");
            ubicacionMap.put("OMDB", ubicacion);
        }
        if (maxAirports >= 25){
            ubicacion = new Ubicacion();
            ubicacion.fillData("ZBAA", "Asia", "China", "Beijing", "beij", "GMT+8");
            ubicacionMap.put("ZBAA", ubicacion);
        }
        if (maxAirports >= 26){
            ubicacion = new Ubicacion();
            ubicacion.fillData("RJTT", "Asia", "Japon", "Tokyo", "toky", "GMT+9");
            ubicacionMap.put("RJTT", ubicacion);
        }
        if (maxAirports >= 27){
            ubicacion = new Ubicacion();
            ubicacion.fillData("WMKK", "Asia", "Malasia", "Kuala Lumpur", "kual", "GMT+8");
            ubicacionMap.put("WMKK", ubicacion);
        }
        if (maxAirports >= 28){
            ubicacion = new Ubicacion();
            ubicacion.fillData("WSSS", "Asia", "Singapur", "Singapore", "sing", "GMT+8");
            ubicacionMap.put("WSSS", ubicacion);
        }
        if (maxAirports >= 29){
            ubicacion = new Ubicacion();
            ubicacion.fillData("WIII", "Asia", "Indonesia", "Jakarta", "jaka", "GMT+7");
            ubicacionMap.put("WIII", ubicacion);
        }
        if (maxAirports >= 30){
            ubicacion = new Ubicacion();
            ubicacion.fillData("RPLL", "Asia", "Filipinas", "Manila", "mani", "GMT+8");
            ubicacionMap.put("RPLL", ubicacion);
        }

        return ubicacionMap;
    }
    

    public static ArrayList<Aeropuerto> leerAeropuertos(String inputPath, HashMap<String, Ubicacion> ubicacionMap) {
        ArrayList<Aeropuerto> aeropuertos_list = new ArrayList<Aeropuerto>();

        try {
            File file = new File(inputPath + "/aeropuertos.csv");
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(",");

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");

                Aeropuerto aeropuerto = new Aeropuerto();

                aeropuerto.setUbicacion(ubicacionMap.get(parts[0].trim()));
                aeropuerto.setCapacidadMaxima(Integer.parseInt(parts[1].trim()));
                aeropuertos_list.add(aeropuerto);
            }

            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
        return aeropuertos_list;
    }

    public static ArrayList<Paquete> generarPaquetes(int n, List<Aeropuerto> aeropuertos, Date fechaInicio,
            Date fechaFin) {

        ArrayList<Paquete> paquetes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Collections.shuffle(aeropuertos);
            Aeropuerto origen = aeropuertos.get(0);
            Aeropuerto destino = aeropuertos.get(1);
            Date fechaRecepcion = generateRandomDateTime(fechaInicio, fechaFin);
            Paquete paquete = new Paquete();
            paquete.fillData(origen, origen.getUbicacion(), destino.getUbicacion(), fechaRecepcion);
            paquetes.add(paquete);

        }

        return paquetes;

    }

    public static Date generateRandomDateTime(Date startDate, Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        // Obtener la diferencia de tiempo en milisegundos
        long startMillis = calendar.getTimeInMillis();
        long endMillis = (endDate != null) ? endDate.getTime() : System.currentTimeMillis();

        // Asegurarse de que la fecha de inicio no es posterior a la fecha de fin
        if (endDate != null && startMillis > endMillis) {
            throw new IllegalArgumentException("La fecha de inicio debe ser antes de la fecha de fin.");
        }

        // Generar un tiempo aleatorio en este rango
        Random random = new Random();
        long randomMillis = startMillis + (long) (random.nextDouble() * (endMillis - startMillis));

        // Configurar el tiempo aleatorio generado
        calendar.setTimeInMillis(randomMillis);
        return calendar.getTime();

    }

    public static Date parseDateString(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<PlanVuelo> leerPlanesVuelo(HashMap<String, Ubicacion> ubicacionMap, String inputPath) {
        // Con esta funcion leeremos los planes_vuelo.v3.txt de /rawData
        ArrayList<PlanVuelo> vuelos_list = new ArrayList<PlanVuelo>();

        try {
            File file = new File(inputPath + "/planes_vuelo.txt");
            Scanner scanner = new Scanner(file);
            // delimiter must be EOL
            scanner.useDelimiter("\n");

            int id = 1;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("-");

                PlanVuelo planVuelo = new PlanVuelo();
                planVuelo.setId(id);
                planVuelo.setCiudadOrigen(ubicacionMap.get(parts[0].trim()));
                planVuelo.setCiudadDestino(ubicacionMap.get(parts[1].trim()));
                planVuelo.setCapacidadMaxima(Integer.parseInt(parts[4].trim()));
                planVuelo.setHoraCiudadOrigen(parts[2].trim());
                planVuelo.setHoraCiudadDestino(parts[3].trim());

                vuelos_list.add(planVuelo);
                id++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
        }
        return vuelos_list;
    }

    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    public static Date convertTimeZone(Date date, String fromTimeZone, String toTimeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(fromTimeZone));
        long timeInMillis = date.getTime();

        int fromOffset = TimeZone.getTimeZone(fromTimeZone).getOffset(timeInMillis);
        int toOffset = TimeZone.getTimeZone(toTimeZone).getOffset(timeInMillis);

        int offsetDifference = toOffset - fromOffset;

        return new Date(timeInMillis + offsetDifference);
    }

}