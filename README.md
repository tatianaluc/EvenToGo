# EvenToGo

Aplicación Android (Java) para pedir presupuesto del servicio audiovisual de un evento: registras tu evento, eliges los servicios, la app calcula el presupuesto estimado y puedes pedir una reunión con el project manager.

Proyecto de la 1.ª evaluación de **PMDM** · 2.º DAM · curso 2026-27 · Autora: Tatiana De Luca

Repositorio: https://github.com/tatianaluc/EvenToGo (rama `main2`)

## Cómo ejecutarla

1. Clona el repositorio y ábrelo con Android Studio (carpeta `EvenToGo`).
2. Deja que Gradle sincronice (Java 11, `compileSdk` 37, `minSdk` 29).
3. Ejecuta en un emulador o móvil con Android 10 (API 29) o superior.

## Credenciales de prueba

| Usuario | Clave | Qué trae |
|---|---|---|
| `admin` | `admin` | 3 eventos propios |
| `carlos` | `carlos` | 3 eventos propios |

Desde el login también se pueden crear cuentas nuevas. La primera vez que se abre la app (instalación limpia) se cargan **20 eventos** (3 de admin, 3 de carlos y 14 de catálogo sin dueño) y **2 reuniones** de ejemplo. Solo se cargan si la base de datos está vacía, así que se puede añadir, editar y borrar sin problema.

## Qué incluye

| Criterio | Dónde está                                                                                                                                                    |
|---|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Componentes básicos y avanzados | TextView, Button, EditText, CheckBox, ImageView; RadioButton (`Reunion`, `BuscarEvento`), Switch de favorito y RatingBar de complejidad (`FichaEvento`)       |
| Flujo completo | 14 pantallas conectadas: inicio → login → Mis eventos → ficha → alta/edición → vuelta. Todas tienen vuelta (ver `docs/esquema-navegacion.md`)                 |
| Layouts y giro | ConstraintLayout, LinearLayout, TableLayout (`AcercaDe`) y ScrollView para que nada se solape al girar; se guarda el estado con `onSaveInstanceState`         |
| RecyclerView y selección | Mis eventos con CardView (`EventosRecyclerAdapter`); clic corto abre la ficha, clic largo abre menú contextual (Editar / Eliminar); Spinners con datos reales |
| Menús y panel lateral | Navigation Drawer con 10 entradas, menú de opciones y menú contextual en las tarjetas                                                                         |
| DialogFragment y selectores | `ConfirmarDialog`, `CuentaDialog`, `SelectorFechaDialog` y `SelectorHoraDialog`                                                                               |
| Autenticación y lista personal | Login contra la tabla `usuarios`; lista personal (wishlist) distinta para cada usuario; el id del usuario viaja por `Intent`                                  |
| Internacionalización | Todos los textos en `strings.xml` y traducción completa en `values-en/`                                                                                       |
| Bases de datos | SQLite (`usuarios`, `wishlist`) y Room (`eventos`, `reuniones`), CRUD en ambas, consultas fuera del hilo principal y carga inicial                            |
| Documentación | Este README, `docs/`, esquemas de navegación y bd y los manuales en PDF                                                                                       |

## Estructura del repositorio

```
EvenToGo/
├── app/                      código de la aplicación
├── docs/
│   ├── capturas/             capturas de pantalla de la app
│   ├── esquema-bd.md         esquema de la base de datos
│   ├── esquema-navegacion.md esquema de navegación entre pantallas
└── README.md
```

## Hitos

Resumen del trabajo del trimestre, partes del historial de commits:

- **21/09:** primer commit y proyecto base.
- **04/10 – 08/10:** prácticas 1b, 2 y 3.
- **09/10 – 17/10:** prácticas 4 y 5.
- **18/10:** práctica 6.
- **01/11:** prácticas 7, 8, 9 y 11.
- **08/11 – 10/11:** práctica 12 y base de datos SQLite (`EventosDB`).
- **16/11 – 19/11:** login, buscar evento, práctica 13 (Room), revisión del código, interfaz y versión final.
