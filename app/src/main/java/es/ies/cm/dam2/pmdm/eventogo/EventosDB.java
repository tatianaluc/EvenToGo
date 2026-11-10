package es.ies.cm.dam2.pmdm.eventogo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

// Clase que crea y gestiona la base de datos SQLite de la app. Extiende SQLiteOpenHelper, que se encarga de
// crear la bd la primera vez que se abre y de actualizarla cuando subimos la versión
public class EventosDB extends SQLiteOpenHelper {

    // Nombres de las tablas, públicos para usarlos desde las activities sin escribirlos a mano
    public static final String TABLA_EVENTOS = "eventos";
    public static final String TABLA_USUARIOS = "usuarios";
    public static final String TABLA_WISHLIST = "wishlist";
    // Límites de asistentes en un solo sitio, así el formulario, los ejemplos y la bd usan siempre los mismos
    public static final int MIN_ASISTENTES = 5;
    public static final int MAX_ASISTENTES = 300;
    private static final String NOMBRE_BD = "eventogo.db";
    // La bd nace en la versión 1 porque no existe ninguna anterior. Si en el futuro cambiamos la estructura
    // subiremos este número
    private static final int VERSION_BD = 1;

    public EventosDB(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    // SQLite no comprueba las claves foráneas por defecto, hay que activarlo cada vez que se abre la bd
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Se ejecuta una vez, cuando la bd no existe todavía
    // aquí creamos las 3 tablas y metemos los usuarios
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Tabla de eventos.
        // El CHECK hace que la propia bd rechace entre
        // asistentes fuera de rango o servicios fuera de 1-10
        db.execSQL("CREATE TABLE " + TABLA_EVENTOS + " (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "nombre TEXT NOT NULL, " + "tipo TEXT NOT NULL, " + "cliente TEXT NOT NULL, " + "asistentes INTEGER NOT NULL CHECK(asistentes BETWEEN " + MIN_ASISTENTES + " AND " + MAX_ASISTENTES + "), " + "fecha TEXT NOT NULL, " + "hora_inicio TEXT NOT NULL, "     // formato HH:mm
                + "hora_fin TEXT NOT NULL, "        // formato HH:mm
                + "servicios INTEGER NOT NULL CHECK(servicios BETWEEN 1 AND 10), " + "incluye_av INTEGER NOT NULL DEFAULT 0, "   // 0 = no, 1 = si
                + "lugar TEXT NOT NULL)");

        // Tabla de usuarios para el login.
        // El nombre es UNIQUE para que no haya dos usuarios iguales
        db.execSQL("CREATE TABLE " + TABLA_USUARIOS + " (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "nombre TEXT NOT NULL UNIQUE, " + "clave TEXT NOT NULL)");

        // Tabla wishlist Es la relación entre usuarios y eventos.
        // UNIQUE evita guardar dos veces el mismo evento, y las claves foráneas en cascada hacen que
        // si se borra un usuario o un evento, desaparezcan también sus filas de aquí
        db.execSQL("CREATE TABLE " + TABLA_WISHLIST + " (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "usuario_id INTEGER NOT NULL, " + "evento_id INTEGER NOT NULL, " + "UNIQUE(usuario_id, evento_id), " + "FOREIGN KEY(usuario_id) REFERENCES " + TABLA_USUARIOS + "(id) ON DELETE CASCADE, " + "FOREIGN KEY(evento_id) REFERENCES " + TABLA_EVENTOS + "(id) ON DELETE CASCADE)");

        // Los dos usuarios que pide la práctica
        db.execSQL("INSERT INTO " + TABLA_USUARIOS + " (nombre, clave) VALUES ('admin', 'admin')");
        db.execSQL("INSERT INTO " + TABLA_USUARIOS + " (nombre, clave) VALUES ('carlos', 'carlos')");
        // y los 20 eventos de ejemplo
        cargarEjemplos(db);
    }

    // Se ejecutaría al subir de version. Mientras esté en la versión 1, no hay nada que migrar
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAntigua, int versionNueva) {

    }

    // Los 20 eventos de ejemplo, ahora guardados en la bd
    private void cargarEjemplos(SQLiteDatabase db) {
        // Los 20 eventos de ejemplo
        insertarEvento(db, new Evento("Boda de Noemí y Pablo", "Boda", "Familia Ruiz", 250, "25/09/2027", "18:00", "02:00", 6, true, "Zona de Eventos Exterior"));
        insertarEvento(db, new Evento("Presentación disco Maluma", "Concierto", "WarnerBros Music", 180, "18/07/2027", "12:00", "00:00", 9, true, "Carpa"));
        insertarEvento(db, new Evento("Quest Global", "Corporativo", "NewLink", 200, "03/03/2027", "09:00", "15:00", 7, true, "Salón de Eventos Interior"));
        insertarEvento(db, new Evento("Los 60 de Rosa", "Cumpleaños", "Rosa Pérez", 40, "21/11/2026", "20:00", "00:00", 3, false, "Local Particular"));
        insertarEvento(db, new Evento("Boda Civil Ana y JuanMa", "Boda", "Pilar y Dani", 90, "05/09/2027", "12:00", "19:00", 5, true, "Zona de Eventos Exterior"));
        insertarEvento(db, new Evento("Concierto benéfico Isabel Gemio", "Concierto", "Fundación Isabel Gemio", 250, "10/12/2026", "19:00", "00:00", 6, true, "Carpa"));
        insertarEvento(db, new Evento("Babor new line", "Corporativo", "Viajes El Corte Inglés", 150, "14/01/2027", "10:00", "13:00", 5, true, "Sala de reuniones"));
        insertarEvento(db, new Evento("50 cumpleaños Director del Hotel", "Cumpleaños", "NH Habana", 60, "30/01/2027", "21:00", "02:00", 4, false, "Salón de Eventos Interior"));
        insertarEvento(db, new Evento("Boda Jack & Phillipe", "Boda", "Media Naranja", 50, "22/05/2027", "17:00", "21:00", 3, false, "Local Particular"));
        insertarEvento(db, new Evento("Presentación Videoclip Aitana", "Concierto", "Cuarto Azul", 200, "27/02/2027", "16:00", "20:00", 5, true, "Salón de Eventos Interior"));
        insertarEvento(db, new Evento("Cena de navidad empresa", "Corporativo", "Dormakaba", 120, "17/12/2026", "21:00", "01:00", 4, false, "Salón de Eventos Interior"));
        insertarEvento(db, new Evento("Cumpelaños Memphis DePay", "Cumpleaños", "TOP Spain", 300, "09/01/2027", "20:00", "05:00", 10, true, "Carpa"));
        insertarEvento(db, new Evento("Boda temática Drag", "Boda", "Rings to Sunrise", 140, "10/10/2027", "18:00", "03:00", 7, true, "Salón de Eventos Interior"));
        insertarEvento(db, new Evento("Gira de verano Joss Stone", "Concierto", "Sony Music", 100, "25/07/2027", "18:00", "02:00", 8, true, "Zona de Eventos Exterior"));
        insertarEvento(db, new Evento("Jornada de formación", "Corporativo", "Instituto Claudio Moyano", 80, "08/02/2027", "09:00", "17:00", 3, true, "Sala de reuniones"));
        insertarEvento(db, new Evento("Cumpleaños sorpresa", "Cumpleaños", "Nuria Vega", 25, "14/11/2026", "20:00", "23:00", 2, false, "Local Particular"));
        insertarEvento(db, new Evento("Aniversario 25 de bodas", "Otro", "Familia Cano", 70, "19/04/2027", "14:00", "19:00", 4, false, "Local Particular"));
        insertarEvento(db, new Evento("Comunión Elena", "Otro", "Brilla tu Peque", 260, "05/03/2027", "13:00", "19:00", 8, true, "Salón de Eventos Interior"));
        insertarEvento(db, new Evento("Inauguración de tienda", "Corporativo", "Moda Viva", 110, "12/03/2027", "18:00", "22:00", 5, true, "Local Particular"));
        insertarEvento(db, new Evento("Concierto de Navidad", "Concierto", "Coro Municipal", 300, "20/12/2026", "20:00", "23:00", 6, true, "Salón de Eventos Interior"));
    }

    // Devuelve cuántos eventos hay guardados. Se usará para la pantalla de los 9 botones y el
    // contador del Main
    public int contarEventos() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLA_EVENTOS, null);
        int total = 0;
        if (c.moveToFirst()) {
            total = c.getInt(0);   // el contador viene en la columna 0 de la única fila
        }
        c.close();
        return total;
    }

    // Pasa un Evento a ContentValues columna -> valor, lo usan el insert y el update
    private ContentValues valoresDe(Evento e) {
        ContentValues valores = new ContentValues();
        valores.put("nombre", e.getNombre());
        valores.put("tipo", e.getTipo());
        valores.put("cliente", e.getCliente());
        valores.put("asistentes", e.getAsistentes());
        valores.put("fecha", e.getFecha());
        valores.put("hora_inicio", e.getHoraInicio());
        valores.put("hora_fin", e.getHoraFin());
        valores.put("servicios", e.getServicios());
        valores.put("incluye_av", e.isIncluyeAV() ? 1 : 0);
        valores.put("lugar", e.getLugar());
        return valores;
    }

    // Guarda un evento y devuelve el id que le ha puesto la bd, -1 si ha fallado
    public long insertarEvento(Evento e) {
        return insertarEvento(getWritableDatabase(), e);
    }

    // Misma operación pero recibiendo la bd. Hace falta para el onCreate, donde no se puede pedir
    // datos porque la bd todavía se está creando
    private long insertarEvento(SQLiteDatabase db, Evento e) {
        return db.insert(TABLA_EVENTOS, null, valoresDe(e));
    }

    // Actualiza la fila del evento que tenga ese id. Devuelve cuántas filas ha cambiado
    public int actualizarEvento(Evento e) {
        return getWritableDatabase().update(TABLA_EVENTOS, valoresDe(e), "id = ?", new String[]{String.valueOf(e.getId())});
    }

    // Borra el evento con ese id. Si tuviera filas en la wishlist, en cascada las borra también
    public int borrarEvento(int id) {
        return getWritableDatabase().delete(TABLA_EVENTOS, "id = ?", new String[]{String.valueOf(id)});
    }

    // Lee todos los eventos de la bd.
    // Si porNombre es true salen ordenados alfabéticamente sin distinguir mayúsculas (COLLATE NOCASE),
    // y si no, por id, que es el orden en el que se guardaron
    public ArrayList<Evento> obtenerEventos(boolean porNombre) {
        ArrayList<Evento> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String orden = porNombre ? "nombre COLLATE NOCASE" : "id";
        Cursor c = db.query(TABLA_EVENTOS, null, null, null, null, null, orden);
        while (c.moveToNext()) { // cada vuelta es una fila de la tabla
            Evento e = new Evento(c.getString(c.getColumnIndexOrThrow("nombre")), c.getString(c.getColumnIndexOrThrow("tipo")), c.getString(c.getColumnIndexOrThrow("cliente")), c.getInt(c.getColumnIndexOrThrow("asistentes")), c.getString(c.getColumnIndexOrThrow("fecha")), c.getString(c.getColumnIndexOrThrow("hora_inicio")), c.getString(c.getColumnIndexOrThrow("hora_fin")), c.getInt(c.getColumnIndexOrThrow("servicios")), c.getInt(c.getColumnIndexOrThrow("incluye_av")) == 1, // true
                    c.getString(c.getColumnIndexOrThrow("lugar")));
            e.setId(c.getInt(c.getColumnIndexOrThrow("id"))); // el id no va en el constructor
            lista.add(e);
        }
        c.close();
        return lista;
    }
}
