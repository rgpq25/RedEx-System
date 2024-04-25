package Clases;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Duracion {
    private Date inicio;
    private Date fin;

    public Duracion(Date inicio, Date fin) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        try {
            Date firstHourDate = outputFormat.parse(outputFormat.format(inicio));
            this.inicio = firstHourDate;
            

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(fin);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            Date nextHourDate = calendar.getTime();
            this.fin = nextHourDate;

            Funciones.printFormattedDate(firstHourDate);
            Funciones.printFormattedDate(nextHourDate);
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("ERROR en Duracion objeto");
        }   
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Duracion duracion = (Duracion) o;
        return Objects.equals(inicio, duracion.inicio) &&
                Objects.equals(fin, duracion.fin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inicio, fin);
    }

    public long getDuracionEnHoras() {
        long duracionMillis = fin.getTime() - inicio.getTime();
        return duracionMillis / (1000 * 60 * 60);
    }

    public long getDuracionEnMinutos() {
        long duracionMillis = fin.getTime() - inicio.getTime();
        return duracionMillis / (1000 * 60);
    }

    public long getDuracionEnDias() {
        long duracionMillis = fin.getTime() - inicio.getTime();
        return duracionMillis / (1000 * 60 * 60 * 24);
    }
}
