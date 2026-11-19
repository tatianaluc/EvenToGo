package es.ies.cm.dam2.pmdm.eventogo;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

// Carga inicial de la bd Room, que se hace UNA sola vez (lo apunta un boolean en SharedPreferences):
//  - Si venimos de la versión anterior de la app (eventos guardados en SQLite), esos eventos se pasan a Room
//    conservando su id, así la wishlist sigue apuntando a los eventos correctos.
//  - Si es una instalación limpia, se cargan los 20 eventos de ejemplo (6 de usuarios y 14 de catálogo)
//    y dos reuniones, para que se pueda probar todo nada más instalar.
public class DatosIniciales {

    // SharedPreferences: archivo pequeño de clave -> valor que guarda Android. Aquí solo guarda si ya cargamos
    private static final String PREFERENCIAS = "eventogo";
    private static final String CLAVE_CARGADO = "datos_cargados";

    // Se llama al abrir la app. Todo el trabajo va al hilo aparte; al ser el mismo hilo que usan las
    // pantallas, lo que pidan después se ejecuta cuando la carga ya ha terminado
    public static void cargarSiHaceFalta(Context contexto) {
        Context app = contexto.getApplicationContext();
        AppDatabase.ejecutor.execute(() -> {
            SharedPreferences pref = app.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
            if (pref.getBoolean(CLAVE_CARGADO, false)) {
                return; // ya se hizo en otra ocasión
            }

            AppDatabase room = AppDatabase.getInstance(app);
            // Solo cargamos si Room está vacía, para no duplicar nunca nada
            if (room.eventoDao().contarTodos() == 0) {
                EventosDB sqlite = new EventosDB(app);
                ArrayList<Evento> antiguos = sqlite.leerEventosAntiguos();
                if (!antiguos.isEmpty()) {
                    // Migración: los eventos viejos de SQLite pasan a Room con su mismo id
                    room.eventoDao().insertarVarios(antiguos);
                    sqlite.borrarEventosAntiguos(); // y se quita la tabla vieja, ya no hace falta
                } else {
                    cargarEjemplos(room);
                }
            }
            // Apuntamos que ya está hecho, así no se repite
            pref.edit().putBoolean(CLAVE_CARGADO, true).apply();
        });
    }

    // Instalación limpia: 20 eventos (los 6 primeros con dueño) y dos reuniones
    private static void cargarEjemplos(AppDatabase room) {
        List<Evento> eventos = new ArrayList<>();
        // de admin
        eventos.add(conDueno(1, new Evento("Boda de Noemí y Pablo", "Boda", "Familia Ruiz", 250, "25/09/2027", "18:00", "02:00", 6, true, "Zona de Eventos Exterior")));
        // de admin
        eventos.add(conDueno(1, new Evento("Presentación disco Maluma", "Concierto", "WarnerBros Music", 180, "18/07/2027", "12:00", "00:00", 9, true, "Carpa")));
        // de admin
        eventos.add(conDueno(1, new Evento("Quest Global", "Corporativo", "NewLink", 200, "03/03/2027", "09:00", "15:00", 7, true, "Salón de Eventos Interior")));
        // de carlos
        eventos.add(conDueno(2, new Evento("Los 60 de Rosa", "Cumpleaños", "Rosa Pérez", 40, "21/11/2026", "20:00", "00:00", 3, false, "Local Particular")));
        // de carlos
        eventos.add(conDueno(2, new Evento("Boda Civil Ana y JuanMa", "Boda", "Pilar y Dani", 90, "05/09/2027", "12:00", "19:00", 5, true, "Zona de Eventos Exterior")));
        // de carlos
        eventos.add(conDueno(2, new Evento("Concierto benéfico Isabel Gemio", "Concierto", "Fundación Isabel Gemio", 250, "10/12/2026", "19:00", "00:00", 6, true, "Carpa")));
        eventos.add(new Evento("Babor new line", "Corporativo", "Viajes El Corte Inglés", 150, "14/01/2027", "10:00", "13:00", 5, true, "Sala de reuniones"));
        eventos.add(new Evento("50 cumpleaños Director del Hotel", "Cumpleaños", "NH Habana", 60, "30/01/2027", "21:00", "02:00", 4, false, "Salón de Eventos Interior"));
        eventos.add(new Evento("Boda Jack & Phillipe", "Boda", "Media Naranja", 50, "22/05/2027", "17:00", "21:00", 3, false, "Local Particular"));
        eventos.add(new Evento("Presentación Videoclip Aitana", "Concierto", "Cuarto Azul", 200, "27/02/2027", "16:00", "20:00", 5, true, "Salón de Eventos Interior"));
        eventos.add(new Evento("Cena de navidad empresa", "Corporativo", "Dormakaba", 120, "17/12/2026", "21:00", "01:00", 4, false, "Salón de Eventos Interior"));
        eventos.add(new Evento("Cumpelaños Memphis DePay", "Cumpleaños", "TOP Spain", 300, "09/01/2027", "20:00", "05:00", 10, true, "Carpa"));
        eventos.add(new Evento("Boda temática Drag", "Boda", "Rings to Sunrise", 140, "10/10/2027", "18:00", "03:00", 7, true, "Salón de Eventos Interior"));
        eventos.add(new Evento("Gira de verano Joss Stone", "Concierto", "Sony Music", 100, "25/07/2027", "18:00", "02:00", 8, true, "Zona de Eventos Exterior"));
        eventos.add(new Evento("Jornada de formación", "Corporativo", "Instituto Claudio Moyano", 80, "08/02/2027", "09:00", "17:00", 3, true, "Sala de reuniones"));
        eventos.add(new Evento("Cumpleaños sorpresa", "Cumpleaños", "Nuria Vega", 25, "14/11/2026", "20:00", "23:00", 2, false, "Local Particular"));
        eventos.add(new Evento("Aniversario 25 de bodas", "Otro", "Familia Cano", 70, "19/04/2027", "14:00", "19:00", 4, false, "Local Particular"));
        eventos.add(new Evento("Comunión Elena", "Otro", "Brilla tu Peque", 260, "05/03/2027", "13:00", "19:00", 8, true, "Salón de Eventos Interior"));
        eventos.add(new Evento("Inauguración de tienda", "Corporativo", "Moda Viva", 110, "12/03/2027", "18:00", "22:00", 5, true, "Local Particular"));
        eventos.add(new Evento("Concierto de Navidad", "Concierto", "Coro Municipal", 300, "20/12/2026", "20:00", "23:00", 6, true, "Salón de Eventos Interior"));

        // insertarVarios devuelve el id de cada evento, en el mismo orden en que los hemos metido
        List<Long> ids = room.eventoDao().insertarVarios(eventos);

        // Dos reuniones de ejemplo (la relación 1-N): una aceptada y otra cancelada
        List<ReunionEvento> reuniones = new ArrayList<>();
        reuniones.add(new ReunionEvento(ids.get(0).intValue(), "Familia Ruiz", "15/12/2026",
                "Oficina", ReunionEvento.ACEPTADA));
        reuniones.add(new ReunionEvento(ids.get(3).intValue(), "Rosa Pérez", "20/12/2026",
                "Online", ReunionEvento.CANCELADA));
        room.reunionDao().insertarVarias(reuniones);
    }

    // Devuelve el evento con su dueño puesto (1 = admin, 2 = carlos), para escribir los ejemplos más corto
    private static Evento conDueno(int usuarioId, Evento evento) {
        evento.setUsuarioId(usuarioId);
        return evento;
    }
}
