package proyectoreal.interfaz;

/**
 * Constantes del sistema: textos fijos, titulos, listas de opciones,
 * encabezados de las tablas y mensajes que se muestran al usuario.
 *
 * Al ser una interfaz, todos sus campos son automaticamente
 * public static final (constantes).
 *
 * @author EliLe
 */
public interface InterfazENEL {

    // ===================== DATOS GENERALES =====================
    String NOMBRE_EMPRESA = "ENEL Servicios de Limpieza";
    String TITULO_VENTANA = "ENEL Servicios de Limpieza - Punto de Venta y Gestión";

    double IVA = 0.16;                 // 16 %
    int HORAS_JORNADA = 8;             // horas de un turno normal
    double FACTOR_HORA_EXTRA = 2.0;    // las horas extra se pagan dobles

    // ===================== PESTAÑAS =====================
    String TAB_VENTA = "Punto de Venta";
    String TAB_CLIENTES = "Clientes";
    String TAB_NOMINA = "Trabajadores y Nómina";
    String TAB_CATALOGO = "Servicios e Inventario";

    // ===================== OPCIONES DE LOS COMBOS =====================
    String PUBLICO_GENERAL = "Público General";

    String[] TIPOS_CLIENTE = {"Comercial", "Residencial", "Industrial"};

    String[] PUESTOS = {"Operador", "Supervisor"};

    String METODO_EFECTIVO = "Efectivo";
    String[] METODOS_PAGO = {METODO_EFECTIVO, "Transferencia", "Tarjeta"};

    String TIPO_SERVICIO = "Servicio";
    String TIPO_INSUMO = "Insumo";
    String[] TIPOS_CATALOGO = {TIPO_SERVICIO, TIPO_INSUMO};

    // ===================== ENCABEZADOS DE LAS TABLAS =====================
    String[] ENCABEZADO_CARRITO = {"Concepto", "Cantidad", "Precio Unitario", "Subtotal"};

    String[] ENCABEZADO_VENTAS = {"Folio", "Fecha", "Cliente", "Método de pago", "Total"};

    String[] ENCABEZADO_CLIENTES = {"ID", "Nombre / Empresa", "Teléfono", "Dirección", "Tipo de servicio"};

    String[] ENCABEZADO_TRABAJADORES = {"ID", "Nombre", "Puesto", "Sueldo por día"};

    String[] ENCABEZADO_NOMINA = {"Folio", "Fecha", "Trabajador", "Días", "Horas extra",
        "Sueldo base", "Pago extra", "Descuentos", "Total neto"};

    String[] ENCABEZADO_CATALOGO = {"ID", "Tipo", "Nombre", "Precio", "Stock"};

    // ===================== TITULOS DE LOS MENSAJES =====================
    String TITULO_ERROR = "Error";
    String TITULO_AVISO = "Aviso";
    String TITULO_CONFIRMAR = "Confirmar";

    // ===================== MENSAJES =====================
    String MSG_CAMPOS_VACIOS = "Llena todos los campos obligatorios.";
    String MSG_NUMERO_INVALIDO = "Escribe un número válido (mayor o igual a 0) en: ";
    String MSG_TELEFONO_INVALIDO = "El teléfono debe tener exactamente 10 dígitos.";
    String MSG_ID_REPETIDO = "Ya existe un registro con ese ID.";
    String MSG_NO_ENCONTRADO = "No se encontró ningún registro con ese ID.";
    String MSG_SELECCIONA_FILA = "Selecciona primero un registro de la tabla.";
    String MSG_SELECCIONA_OPCION = "Selecciona una opción de la lista.";
    String MSG_CONFIRMAR_ELIMINAR = "¿Seguro que deseas eliminar este registro?";

    String MSG_GUARDADO = "Registro guardado correctamente.";
    String MSG_ACTUALIZADO = "Registro actualizado correctamente.";
    String MSG_ELIMINADO = "Registro eliminado correctamente.";

    String MSG_CANTIDAD_INVALIDA = "La cantidad debe ser mayor que 0.";
    String MSG_PIEZA_ENTERA = "Los insumos se venden por pieza (cantidad entera).";
    String MSG_STOCK_INSUFICIENTE = "No hay stock suficiente de: ";
    String MSG_CARRITO_VACIO = "El carrito está vacío.";
    String MSG_PAGO_INSUFICIENTE = "El monto recibido no cubre el total de la venta.";
    String MSG_VENTA_REGISTRADA = "Venta registrada. Revisa el ticket.";

    String MSG_DIAS_INVALIDOS = "Los días trabajados deben estar entre 0 y 7.";
    String MSG_NETO_NEGATIVO = "Los descuentos no pueden ser mayores que el pago.";
    String MSG_NOMINA_REGISTRADA = "Pago registrado. Total neto: ";
}
