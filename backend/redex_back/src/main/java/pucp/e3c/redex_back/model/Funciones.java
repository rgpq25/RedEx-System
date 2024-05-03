package pucp.e3c.redex_back.model;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class Funciones {
    public static String getFormattedDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
}