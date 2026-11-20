# Esquema de navegación

Pantallas (activities) de la app y cómo se llega de una a otra. Todas las pantallas tienen vuelta: el botón **Atrás** de la propia pantalla o el botón atrás del móvil, y las que devuelven un dato lo hacen con `ActivityResult`.

## Diagrama

```mermaid
flowchart TD
    Main["MainActivity<br/>Inicio"]
    Login["Login"]
    Col["Coleccion<br/>Mis eventos (RecyclerView + Drawer)"]
    Nuevo["NuevoEvento<br/>Alta y edición"]
    Ficha["FichaEvento<br/>Ficha y presupuesto"]
    Reu["Reunion<br/>Crear reunión"]
    Conf["ConfirmarReunion"]
    Inv["InvitacionCorreo"]
    Lista["ListaReuniones"]
    BuscarEv["BuscarEvento<br/>Catálogo"]
    Wish["Wishlist<br/>Mi lista"]
    BuscarServ["Buscar<br/>Servicios"]
    Comp["Compartir<br/>Compartir evento"]
    Acerca["AcercaDe"]
    Web(["Navegador web"])

    Main -->|"Mis eventos"| Login
    Main -->|"Nuestros eventos"| BuscarEv
    Main -->|"Descubre la web"| Web
    Main -->|"Acerca de"| Acerca

    Login -->|"usuario y clave correctos"| Col

    Col -->|"Añadir / Editar"| Nuevo
    Nuevo -->|"Guardar (vuelve con el evento)"| Col
    Col -->|"clic en una tarjeta"| Ficha
    Col -->|"menú lateral"| BuscarEv
    Col -->|"menú lateral"| Wish
    Col -->|"menú lateral"| BuscarServ
    Col -->|"menú lateral"| Comp
    Col -->|"menú lateral"| Acerca
    Col -->|"Cerrar sesión"| Main

    BuscarEv -->|"clic en un resultado"| Ficha
    Nuevo -->|"si no lo abrió Mis eventos"| Ficha

    Ficha -->|"Siguiente"| Reu
    Ficha -->|"Reuniones"| Lista
    Reu -->|"Confirmar"| Conf
    Conf -->|"Aceptar / Cancelar (vuelve con el estado)"| Reu
    Reu -->|"Invitar a la reunión"| Inv
```

## Diálogos

| Diálogo | Dónde se usa |
|---|---|
| `ConfirmarDialog` | Confirmar el borrado de un evento (`Coleccion`) o de una reunión (`ListaReuniones`) |
| `CuentaDialog` | Crear cuenta (`Login`), cambiar clave y eliminar la cuenta (menú lateral de `Coleccion`) |
| `SelectorFechaDialog` | Elegir fecha en `NuevoEvento` y `Reunion` |
| `SelectorHoraDialog` | Elegir hora de inicio y de fin en `NuevoEvento` |

## Menús

- **Menú lateral (Drawer), en `Coleccion`:** Mis eventos, Añadir evento, Catálogo de eventos, Mi lista, Buscar servicios, Compartir evento, Acerca de, Cambiar clave, Eliminar mi cuenta y Cerrar sesión.
- **Menú de opciones, en `Coleccion`:** Añadir evento, Ordenar por nombre, Buscar evento, Mi lista y Acerca de.
- **Menú contextual, en cada tarjeta (pulsación larga):** Editar y Eliminar.
