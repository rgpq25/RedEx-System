package Clases;

import java.util.Date;
import java.util.Objects;

public class Duracion {
    private Date inicio;
    private Date fin;

    public Duracion(Date inicio, Date fin) {
        this.inicio = inicio;
        this.fin = fin;
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
