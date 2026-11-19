package es.ies.cm.dam2.pmdm.eventogo;

import java.text.NumberFormat;
import java.util.Locale;

// Calcula el presupuesto estimado de un evento a partir de lo que se ha rellenado en el formulario.
// Todas las tarifas están aquí arriba, en un solo sitio
public class Presupuesto {

    // personal técnico
    public static final double JORNADA_TECNICO = 280.0;      // por técnico y por jornada
    public static final int HORAS_JORNADA = 8;               // horas que cubre la jornada
    public static final double PRECIO_HORA_EXTRA = 20.0;     //por cada hora a partir de las 8 h (por técnico)
    public static final double MONTAJE_DESMONTAJE = 150.0;   // fijos por montar y desmontar
    public static final int ASISTENTES_POR_TECNICO = 50;     // un técnico más por cada 50 asistentes
    // Si el evento NO incluye AV y el cliente solo quiere personal, el precio es una tarifa fija
    public static final double TARIFA_SOLO_PERSONAL = 1500.0;

    // Tarifa del AV según la magnitud, la dificultad y los asistentes
    public static final double AV_BASE = 500.0;              // mínimos de cualquier montaje de AV
    public static final double AV_POR_ASISTENTE = 5.0;       // por cada asistente
    public static final double AV_POR_SERVICIO = 100.0;      // por cada servicio  y estrella dificultad del evento

    // Técnicos necesarios, uno por cada 50 asistentes y como mínimo uno
    public static int tecnicos(Evento e) {
        int tecnicos = (int) Math.ceil(e.getAsistentes() / (double) ASISTENTES_POR_TECNICO);
        return Math.max(1, tecnicos);
    }

    // Horas del evento que pasan de la jornada de 8 h (0 si dura 8 o menos)
    public static int horasExtra(Evento e) {
        return Math.max(0, e.getDuracion() - HORAS_JORNADA);
    }

    // Coste de las jornadas del personal técnicos x jornada
    public static double jornadas(Evento e) {
        return tecnicos(e) * JORNADA_TECNICO;
    }

    // Coste de las horas extra: técnicos x horas extra x precio de la hora extra
    public static double extras(Evento e) {
        return tecnicos(e) * horasExtra(e) * PRECIO_HORA_EXTRA;
    }

    // Coste del AV, solo si el evento lo incluye. Sube con los asistentes y con la complejidad
    public static double av(Evento e) {
        if (!e.isIncluyeAV()) {
            return 0;
        }
        return AV_BASE + e.getAsistentes() * AV_POR_ASISTENTE + e.getServicios() * AV_POR_SERVICIO;
    }

    // Total: sin AV es la tarifa fija de solo personal; con AV, personal + horas extra + montaje + AV
    public static double total(Evento e) {
        if (!e.isIncluyeAV()) {
            return TARIFA_SOLO_PERSONAL;
        }
        return jornadas(e) + extras(e) + MONTAJE_DESMONTAJE + av(e);
    }

    // Da formato de dinero en español
    public static String euros(double importe) {
        return NumberFormat.getCurrencyInstance(new Locale("es", "ES")).format(importe);
    }
}
