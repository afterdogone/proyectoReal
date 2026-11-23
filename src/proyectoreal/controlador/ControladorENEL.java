package proyectoreal.controlador;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import proyectoreal.interfaz.InterfazENEL;
import proyectoreal.modelo.Cliente;
import proyectoreal.modelo.DetalleVenta;
import proyectoreal.modelo.Nomina;
import proyectoreal.modelo.Servicio;
import proyectoreal.modelo.Trabajador;
import proyectoreal.modelo.Venta;

/**
 * Controlador del sistema: guarda TODA la informacion en memoria (ArrayList)
 * y tiene los metodos para agregar, buscar, editar y eliminar.
 *
 * Todo es static: hay una sola "base de datos" para todo el programa y se
 * usa directamente, por ejemplo: ControladorENEL.getClientes().
 * Los datos se pierden al cerrar el programa.
 *
 * @author EliLe
 */
public class ControladorENEL {

    // ===================== LISTAS EN MEMORIA =====================
    private static final ArrayList<Cliente> listaClientes = new ArrayList<>();
    private static final ArrayList<Trabajador> listaTrabajadores = new ArrayList<>();
    private static final ArrayList<Servicio> listaServicios = new ArrayList<>();
    private static final ArrayList<Venta> listaVentas = new ArrayList<>();
    private static final ArrayList<Nomina> listaNominas = new ArrayList<>();

    // Contadores para folios e IDs automaticos
    private static int siguienteIdServicio = 1;
    private static int siguienteFolioVenta = 1;
    private static int siguienteFolioNomina = 1;

    // El bloque static se ejecuta una sola vez, al usar la clase por primera vez
    static {
        precargarDatos();
    }

    // No se crean objetos de esta clase: todo se usa como ControladorENEL.metodo()
    private ControladorENEL() {
    }

    // =================================================================
    //                            CLIENTES
    // =================================================================
    public static ArrayList<Cliente> getClientes() {
        return listaClientes;
    }

    /** Busca un cliente por ID. Regresa null si no existe. */
    public static Cliente buscarCliente(int id) {
        for (Cliente objCliente : listaClientes) {
            if (objCliente.getId() == id) {
                return objCliente;
            }
        }
        return null;
    }

    /** Agrega un cliente. Regresa false si el ID ya esta ocupado. */
    public static boolean agregarCliente(Cliente objCliente) {
        if (buscarCliente(objCliente.getId()) != null) {
            return false;
        }
        listaClientes.add(objCliente);
        return true;
    }

    /** Reemplaza los datos del cliente que tiene el mismo ID. */
    public static boolean editarCliente(Cliente objClienteNuevo) {
        Cliente objCliente = buscarCliente(objClienteNuevo.getId());
        if (objCliente == null) {
            return false;
        }
        objCliente.setNombre(objClienteNuevo.getNombre());
        objCliente.setTelefono(objClienteNuevo.getTelefono());
        objCliente.setDireccion(objClienteNuevo.getDireccion());
        objCliente.setTipoServicio(objClienteNuevo.getTipoServicio());
        return true;
    }

    public static boolean eliminarCliente(int id) {
        Cliente objCliente = buscarCliente(id);
        if (objCliente == null) {
            return false;
        }
        listaClientes.remove(objCliente);
        return true;
    }

    // =================================================================
    //                           TRABAJADORES
    // =================================================================
    public static ArrayList<Trabajador> getTrabajadores() {
        return listaTrabajadores;
    }

    public static Trabajador buscarTrabajador(int id) {
        for (Trabajador objTrabajador : listaTrabajadores) {
            if (objTrabajador.getId() == id) {
                return objTrabajador;
            }
        }
        return null;
    }

    public static boolean agregarTrabajador(Trabajador objTrabajador) {
        if (buscarTrabajador(objTrabajador.getId()) != null) {
            return false;
        }
        listaTrabajadores.add(objTrabajador);
        return true;
    }

    public static boolean eliminarTrabajador(int id) {
        Trabajador objTrabajador = buscarTrabajador(id);
        if (objTrabajador == null) {
            return false;
        }
        listaTrabajadores.remove(objTrabajador);
        return true;
    }

    // =================================================================
    //                 CATALOGO (SERVICIOS E INSUMOS)
    // =================================================================
    public static ArrayList<Servicio> getServicios() {
        return listaServicios;
    }

    public static Servicio buscarServicio(int id) {
        for (Servicio objServicio : listaServicios) {
            if (objServicio.getId() == id) {
                return objServicio;
            }
        }
        return null;
    }

    /** Da de alta un servicio o insumo con ID automatico. */
    public static Servicio agregarServicio(String nombre, String tipo, double precio, int stock) {
        Servicio objServicio = new Servicio(siguienteIdServicio, nombre, tipo, precio, stock);
        siguienteIdServicio++;
        listaServicios.add(objServicio);
        return objServicio;
    }

    public static boolean editarServicio(int id, String nombre, String tipo, double precio, int stock) {
        Servicio objServicio = buscarServicio(id);
        if (objServicio == null) {
            return false;
        }
        objServicio.setNombre(nombre);
        objServicio.setTipo(tipo);
        objServicio.setPrecio(precio);
        objServicio.setStock(stock);
        return true;
    }

    public static boolean eliminarServicio(int id) {
        Servicio objServicio = buscarServicio(id);
        if (objServicio == null) {
            return false;
        }
        listaServicios.remove(objServicio);
        return true;
    }

    // =================================================================
    //                              VENTAS
    // =================================================================
    public static ArrayList<Venta> getVentas() {
        return listaVentas;
    }

    /**
     * Revisa que haya inventario para todos los insumos del carrito.
     * Regresa el nombre del insumo que no alcanza, o null si todo esta bien.
     */
    public static String validarStock(Venta objVenta) {
        for (DetalleVenta objDetalle : objVenta.getDetalles()) {
            Servicio objServicio = objDetalle.getServicio();
            if (objServicio.esInsumo() && objDetalle.getCantidad() > objServicio.getStock()) {
                return objServicio.getNombre();
            }
        }
        return null;
    }

    /**
     * Cobra la venta: le pone folio y fecha, calcula el cambio,
     * descuenta el stock de los insumos y la guarda en la lista.
     */
    public static void registrarVenta(Venta objVenta, String metodoPago, double montoRecibido) {
        objVenta.setFolio(siguienteFolioVenta);
        siguienteFolioVenta++;
        objVenta.setFecha(fechaActual());
        objVenta.setMetodoPago(metodoPago);
        objVenta.setMontoRecibido(montoRecibido);
        objVenta.setCambio(montoRecibido - objVenta.getTotal());

        for (DetalleVenta objDetalle : objVenta.getDetalles()) {
            objDetalle.getServicio().descontarStock((int) objDetalle.getCantidad());
        }
        listaVentas.add(objVenta);
    }

    // =================================================================
    //                              NOMINA
    // =================================================================
    public static ArrayList<Nomina> getNominas() {
        return listaNominas;
    }

    /** Guarda un pago ya calculado en el historial, con folio y fecha. */
    public static void registrarNomina(Nomina objNomina) {
        objNomina.setFolio(siguienteFolioNomina);
        siguienteFolioNomina++;
        objNomina.setFecha(fechaActual());
        listaNominas.add(objNomina);
    }

    // =================================================================
    //                            UTILIDADES
    // =================================================================

    /** Da formato de dinero: 1234.5 -> "$1,234.50" */
    public static String dinero(double cantidad) {
        return String.format("$%,.2f", cantidad);
    }

    /** Fecha y hora actual como texto. */
    public static String fechaActual() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
    }

    // =================================================================
    //                 DATOS DE PRUEBA (se cargan al iniciar)
    // =================================================================
    private static void precargarDatos() {
        // ---- Clientes ----
        agregarCliente(new Cliente(1, "Corporativo Torres del Valle", "4421234567",
                "Av. Constituyentes 120, Querétaro", InterfazENEL.TIPOS_CLIENTE[0]));
        agregarCliente(new Cliente(2, "Familia Hernández", "4429876543",
                "Calle Pino 45, Col. Jardines", InterfazENEL.TIPOS_CLIENTE[1]));
        agregarCliente(new Cliente(3, "Plásticos del Bajío S.A.", "4425551234",
                "Parque Industrial Norte, Nave 7", InterfazENEL.TIPOS_CLIENTE[2]));
        agregarCliente(new Cliente(4, "Clínica Dental Sonrisas", "4423334455",
                "Blvd. Bernardo Quintana 300", InterfazENEL.TIPOS_CLIENTE[0]));

        // ---- Trabajadores ----
        agregarTrabajador(new Trabajador(101, "Juan Pérez López", InterfazENEL.PUESTOS[0], 350));
        agregarTrabajador(new Trabajador(102, "María García Ruiz", InterfazENEL.PUESTOS[0], 350));
        agregarTrabajador(new Trabajador(103, "Carlos Ramírez Soto", InterfazENEL.PUESTOS[1], 520));
        agregarTrabajador(new Trabajador(104, "Ana Martínez Cruz", InterfazENEL.PUESTOS[0], 380));

        // ---- Catalogo: servicios (precio por m2) e insumos (precio por pieza) ----
        Servicio objOficina = agregarServicio("Limpieza de oficina (m2)", InterfazENEL.TIPO_SERVICIO, 18, 0);
        Servicio objSala = agregarServicio("Lavado de sala (pieza)", InterfazENEL.TIPO_SERVICIO, 450, 0);
        Servicio objSanitizacion = agregarServicio("Sanitización (m2)", InterfazENEL.TIPO_SERVICIO, 12, 0);
        agregarServicio("Limpieza profunda de cocina", InterfazENEL.TIPO_SERVICIO, 900, 0);
        Servicio objKit = agregarServicio("Kit de insumos básicos", InterfazENEL.TIPO_INSUMO, 250, 20);
        Servicio objDesinfectante = agregarServicio("Desinfectante 1 L", InterfazENEL.TIPO_INSUMO, 65, 40);
        agregarServicio("Paquete de franelas (6 pzs)", InterfazENEL.TIPO_INSUMO, 90, 25);

        // ---- Ventas de ejemplo ----
        Venta objVenta1 = new Venta();
        objVenta1.setNombreCliente(listaClientes.get(0).getNombre());
        objVenta1.agregarDetalle(new DetalleVenta(objOficina, 120));
        objVenta1.agregarDetalle(new DetalleVenta(objDesinfectante, 2));
        registrarVenta(objVenta1, InterfazENEL.METODOS_PAGO[1], objVenta1.getTotal());

        Venta objVenta2 = new Venta();
        objVenta2.setNombreCliente(listaClientes.get(1).getNombre());
        objVenta2.agregarDetalle(new DetalleVenta(objSala, 1));
        registrarVenta(objVenta2, InterfazENEL.METODO_EFECTIVO, 600);

        Venta objVenta3 = new Venta();
        objVenta3.agregarDetalle(new DetalleVenta(objKit, 1));
        objVenta3.agregarDetalle(new DetalleVenta(objSanitizacion, 50));
        registrarVenta(objVenta3, InterfazENEL.METODOS_PAGO[2], objVenta3.getTotal());

        // ---- Pagos de nomina de ejemplo ----
        registrarNomina(new Nomina(listaTrabajadores.get(0), 6, 4, 150));
        registrarNomina(new Nomina(listaTrabajadores.get(1), 5, 0, 0));
        registrarNomina(new Nomina(listaTrabajadores.get(2), 6, 2, 300));
    }
}
