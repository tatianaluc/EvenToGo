package es.ies.cm.dam2.pmdm.eventogo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

// Clase que crea y gestiona la base de datos SQLite de la app. Extiende SQLiteOpenHelper, que se encarga de
// crear la bd la primera vez que se abre y de actualizarla cuando subimos la versión.
// Desde la práctica 13 aquí solo quedan los USUARIOS y la WISHLIST. Los eventos y las reuniones
// ahora están en Room (AppDatabase), por eso la wishlist guarda solo el id del evento, sin clave ajena
public class EventosDB extends SQLiteOpenHelper {

    // Nombres de las tablas, públicos para usarlos desde las activities sin escribirlos a mano
    // TABLA_EVENTOS ya no se crea: es la tabla de la versión 1, que solo se lee una vez para pasar sus eventos a Room
    public static final String TABLA_EVENTOS = "eventos";
    public static final String TABLA_USUARIOS = "usuarios";
    public static final String TABLA_WISHLIST = "wishlist";
    // Límites de asistentes en un solo sitio, así el formulario y los ejemplos usan siempre los mismos
    public static final int MIN_ASISTENTES = 5;
    public static final int MAX_ASISTENTES = 300;
    private static final String NOMBRE_BD = "eventogo.db";
    // Versión 1: usuarios, wishlist (con clave ajena a eventos) y eventos.
    // Versión 2 (práctica 13): los eventos pasan a Room y la wishlist pierde esa clave ajena
    private static final int VERSION_BD = 2;

    public EventosDB(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    // SQLite no comprueba las claves foráneas por defecto, hay que activarlo cada vez que se abre la bd
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Se ejecuta una vez, cuando la bd no existe todavía (instalación limpia)
    // aquí creamos las 2 tablas y metemos los usuarios
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Tabla de usuarios para el login.
        // El nombre es UNIQUE para que no haya dos usuarios iguales
        db.execSQL("CREATE TABLE " + TABLA_USUARIOS + " (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "nombre TEXT NOT NULL UNIQUE, " + "clave TEXT NOT NULL)");

        // Tabla wishlist: la lista personal de cada usuario. Guarda el id de usuario y el id del evento,
        // pero como los eventos están en otra bd (Room), no puede haber clave ajena hacia ellos.
        // UNIQUE evita guardar dos veces el mismo evento, y la clave foránea en cascada hace que
        // si se borra un usuario, desaparezcan también sus filas de aquí
        crearTablaWishlist(db, TABLA_WISHLIST);

        // Los dos usuarios que pide la práctica (admin tiene el id 1 y carlos el 2)
        db.execSQL("INSERT INTO " + TABLA_USUARIOS + " (nombre, clave) VALUES ('admin', 'admin')");
        db.execSQL("INSERT INTO " + TABLA_USUARIOS + " (nombre, clave) VALUES ('carlos', 'carlos')");
    }

    // Se ejecuta al subir de versión: de la 1 a la 2 la wishlist tiene que dejar de tener la clave
    // ajena hacia eventos (SQLite no puede quitar una clave ajena, así que se crea una tabla nueva y se copian los datos)
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAntigua, int versionNueva) {
        if (versionAntigua < 2) {
            crearTablaWishlist(db, "wishlist_nueva");
            db.execSQL("INSERT INTO wishlist_nueva (usuario_id, evento_id) SELECT usuario_id, evento_id FROM " + TABLA_WISHLIST);
            db.execSQL("DROP TABLE " + TABLA_WISHLIST);
            db.execSQL("ALTER TABLE wishlist_nueva RENAME TO " + TABLA_WISHLIST);
            // La tabla eventos de la versión 1 se queda de momento: DatosIniciales pasará sus filas a Room y luego la borra
        }
    }

    // Crea la tabla de la wishlist con el nombre que le digamos (se usa al crearla y al migrar)
    private void crearTablaWishlist(SQLiteDatabase db, String nombreTabla) {
        db.execSQL("CREATE TABLE " + nombreTabla + " (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "usuario_id INTEGER NOT NULL, " + "evento_id INTEGER NOT NULL, "
                + "UNIQUE(usuario_id, evento_id), "
                + "FOREIGN KEY(usuario_id) REFERENCES " + TABLA_USUARIOS + "(id) ON DELETE CASCADE)");
    }

    // ---------- MIGRACIÓN de la versión 1 (eventos en SQLite) a Room ----------

    // Lee los eventos que quedaran en la tabla antigua. Si no existe (instalación limpia) devuelve la lista vacía
    public ArrayList<Evento> leerEventosAntiguos() {
        ArrayList<Evento> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        // sqlite_master es la tabla donde SQLite apunta todas las tablas que existen
        Cursor existe = db.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table' AND name = ?",
                new String[]{TABLA_EVENTOS});
        boolean hayTabla = existe.moveToFirst();
        existe.close();
        if (!hayTabla) {
            return lista;
        }
        Cursor c = db.query(TABLA_EVENTOS, null, null, null, null, null, "id");
        while (c.moveToNext()) { // cada vuelta es una fila de la tabla
            lista.add(eventoDe(c));
        }
        c.close();
        return lista;
    }

    // Borra la tabla antigua de eventos, una vez que sus filas ya están en Room
    public void borrarEventosAntiguos() {
        getWritableDatabase().execSQL("DROP TABLE IF EXISTS " + TABLA_EVENTOS);
    }

    // Convierte la fila en la que está el cursor en un Evento (solo lo usa la migración)
    private Evento eventoDe(Cursor c) {
        Evento e = new Evento(
                c.getString(c.getColumnIndexOrThrow("nombre")),
                c.getString(c.getColumnIndexOrThrow("tipo")),
                c.getString(c.getColumnIndexOrThrow("cliente")),
                c.getInt(c.getColumnIndexOrThrow("asistentes")),
                c.getString(c.getColumnIndexOrThrow("fecha")),
                c.getString(c.getColumnIndexOrThrow("hora_inicio")),
                c.getString(c.getColumnIndexOrThrow("hora_fin")),
                c.getInt(c.getColumnIndexOrThrow("servicios")),
                c.getInt(c.getColumnIndexOrThrow("incluye_av")) == 1, // 1 = true
                c.getString(c.getColumnIndexOrThrow("lugar")));
        e.setId(c.getInt(c.getColumnIndexOrThrow("id"))); // se conserva el id para que la wishlist siga valiendo
        int colDueno = c.getColumnIndexOrThrow("usuario_id");
        // en la tabla antigua el usuario_id vacío (NULL) era un evento de catálogo
        e.setUsuarioId(c.isNull(colDueno) ? Evento.SIN_DUENO : c.getInt(colDueno));
        return e;
    }

    // ---------- USUARIOS ----------

    // Comprueba si existe un usuario con ese nombre y clave. Devuelve su id, o -1 si no existe
    public int comprobarLogin(String nombre, String clave) {
        SQLiteDatabase db = getReadableDatabase();
        // los ? se rellenan por orden con el array, el primero con el nombre y el segundo con la clave
        Cursor c = db.query(TABLA_USUARIOS, new String[]{"id"},
                "nombre = ? AND clave = ?", new String[]{nombre, clave},
                null, null, null);
        int idUsuario = -1;
        if (c.moveToFirst()) { // si hay una fila, el usuario existe
            idUsuario = c.getInt(0);
        }
        c.close();
        return idUsuario;
    }

    // ---------- WISHLIST ----------

    // Añade un evento a la lista del usuario. Devuelve false si ya estaba
    public boolean anadirAWishlist(int usuarioId, int eventoId) {
        ContentValues valores = new ContentValues();
        valores.put("usuario_id", usuarioId);
        valores.put("evento_id", eventoId);
        // UNIQUE(usuario_id, evento_id): si ya existe, insert no duplica y devuelve -1
        return getWritableDatabase().insert(TABLA_WISHLIST, null, valores) != -1;
    }

    // Quita un evento de la lista del usuario
    public void quitarDeWishlist(int usuarioId, int eventoId) {
        getWritableDatabase().delete(TABLA_WISHLIST, "usuario_id = ? AND evento_id = ?",
                new String[]{String.valueOf(usuarioId), String.valueOf(eventoId)});
    }

    // Ids de los eventos que el usuario tiene en su lista (solo ids, el evento se busca luego en Room)
    public ArrayList<Integer> obtenerIdsWishlist(int usuarioId) {
        ArrayList<Integer> ids = new ArrayList<>();
        Cursor c = getReadableDatabase().query(TABLA_WISHLIST, new String[]{"evento_id"},
                "usuario_id = ?", new String[]{String.valueOf(usuarioId)}, null, null, "id");
        while (c.moveToNext()) {
            ids.add(c.getInt(0)); // columna 0 = evento_id, la única que hemos pedido
        }
        c.close();
        return ids;
    }
}
