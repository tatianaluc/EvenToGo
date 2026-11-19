package es.ies.cm.dam2.pmdm.eventogo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

// Clase que crea y gestiona la base de datos SQLite de la app. Extiende SQLiteOpenHelper, que se encarga de
// crear la bd la primera vez que se abre y de actualizarla cuando subimos la versión.
public class EventosDB extends SQLiteOpenHelper {

    // Nombres de las tablas, públicos para usarlos desde las activities sin escribirlos a mano
    // TABLA_EVENTOS es la de la versión 1, que solo se lee una vez para pasar sus eventos a Room
    public static final String TABLA_EVENTOS = "eventos";
    public static final String TABLA_USUARIOS = "usuarios";
    public static final String TABLA_WISHLIST = "wishlist";
    // Límites de asistentes en un solo sitio, así el formulario y los ejemplos usan siempre los mismos
    public static final int MIN_ASISTENTES = 5;
    public static final int MAX_ASISTENTES = 300;
    private static final String NOMBRE_BD = "eventogo.db";
    // Versión 1: usuarios, wishlist y eventos.
    // Versión 2: los eventos pasan a Room y la wishlist pierde la fk
    private static final int VERSION_BD = 2;

    public EventosDB(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    // SQLite no comprueba las fk por defecto, hay que activarlo cada vez que se abre la bd
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Se ejecuta una vez, cuando la bd no existe todavía
    // aquí creamos las 2 tablas y metemos los usuarios
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Tabla de usuarios para el login.
        // El nombre es UNIQUE para que no haya dos usuarios iguales
        db.execSQL("CREATE TABLE " + TABLA_USUARIOS + " (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "nombre TEXT NOT NULL UNIQUE, " + "clave TEXT NOT NULL)");

        // Tabla wishlist: la lista personal de cada usuario. Guarda el id de usuario y el id del evento,
        // pero como los eventos están en Room, no puede haber clave ajena hacia ellos.
        // UNIQUE evita guardar dos veces el mismo evento, y la clave foránea en cascada hace que
        // si se borra un usuario, desaparezcan también sus filas de aquí
        crearTablaWishlist(db, TABLA_WISHLIST);

        // Los dos usuarios por defecto creados
        db.execSQL("INSERT INTO " + TABLA_USUARIOS + " (nombre, clave) VALUES ('admin', 'admin')");
        db.execSQL("INSERT INTO " + TABLA_USUARIOS + " (nombre, clave) VALUES ('carlos', 'carlos')");
    }

    // Se ejecuta al subir de versión.
    // SQLite no puede quitar una clave ajena, así que se crea una tabla nueva y se copian los datos
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAntigua, int versionNueva) {
        if (versionAntigua < 2) {
            crearTablaWishlist(db, "wishlist_nueva");
            db.execSQL("INSERT INTO wishlist_nueva (usuario_id, evento_id) SELECT usuario_id, evento_id FROM " + TABLA_WISHLIST);
            db.execSQL("DROP TABLE " + TABLA_WISHLIST);
            db.execSQL("ALTER TABLE wishlist_nueva RENAME TO " + TABLA_WISHLIST);
        }
    }

    // Crea la tabla de la wishlist con el nombre que le digamos, se usa al crearla y al migrar
    private void crearTablaWishlist(SQLiteDatabase db, String nombreTabla) {
        db.execSQL("CREATE TABLE " + nombreTabla + " (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "usuario_id INTEGER NOT NULL, " + "evento_id INTEGER NOT NULL, "
                + "UNIQUE(usuario_id, evento_id), "
                + "FOREIGN KEY(usuario_id) REFERENCES " + TABLA_USUARIOS + "(id) ON DELETE CASCADE)");
    }

    // Migrar de la versión 1 a Room ----------

    // Lee los eventos restantes en la tabla antigua. Si no existe devuelve la lista vacía
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
        // en la tabla antigua el usuario_id vacío era un evento de catálogo
        e.setUsuarioId(c.isNull(colDueno) ? Evento.SIN_DUENO : c.getInt(colDueno));
        return e;
    }

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

    // Crea un usuario nuevo. Devuelve false si ya existe uno con ese nombre
    public boolean registrarUsuario(String nombre, String clave) {
        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("clave", clave);
        // insert devuelve -1 si no puede insertar, por ejemplo porque el nombre ya existe
        return getWritableDatabase().insert(TABLA_USUARIOS, null, valores) != -1;
    }

    // Cambia la clave de un usuario, solo si la clave actual es correcta.
    // Devuelve false si no ha cambiado nada
    public boolean cambiarClave(int usuarioId, String claveActual, String claveNueva) {
        ContentValues valores = new ContentValues();
        valores.put("clave", claveNueva);
        // update devuelve cuántas filas ha cambiado, 1 si el id y la clave actual coinciden, 0 si no
        int filas = getWritableDatabase().update(TABLA_USUARIOS, valores, "id = ? AND clave = ?",
                new String[]{String.valueOf(usuarioId), claveActual});
        return filas > 0;
    }

    // Borra un usuario por su id. La clave foránea en cascada borra a la vez su wishlist
    public void borrarUsuario(int usuarioId) {
        getWritableDatabase().delete(TABLA_USUARIOS, "id = ?", new String[]{String.valueOf(usuarioId)});
    }

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
