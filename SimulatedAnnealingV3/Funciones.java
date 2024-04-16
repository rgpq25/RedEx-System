import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Clases.Aeropuerto;
import Clases.Paquete;
import Clases.PlanVuelo;
import Clases.Vuelo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

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

    public void ordenarPaquetes(Paquete []paquetes){
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

    public void ordenarVuelos(Vuelo []vuelos){
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
}