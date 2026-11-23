package proyectoreal.modelo;

import java.util.ArrayList;
import proyectoreal.interfaz.InterfazENEL;

/**
 * Una venta del punto de venta. Mientras no se cobra, funciona como el
 * "carrito": se le van agregando renglones (DetalleVenta).
 *
 * @author EliLe
 */
public class Venta {

    private int folio;
    private String fecha;
    private String nombreCliente;
    private String metodoPago;
    private double montoRecibido;
    private double cambio;
    private ArrayList<DetalleVenta> detalles = new ArrayList<>();

    public Venta() {
        this.nombreCliente = InterfazENEL.PUBLICO_GENERAL;
    }

    // ===================== GETTERS =====================
    public int getFolio()
    {
        return folio;
    }

    public String getFecha()
    {
        return fecha;
    }

    public String getNombreCliente()
    {
        return nombreCliente;
    }

    public String getMetodoPago()
    {
        return metodoPago;
    }

    public double getMontoRecibido()
    {
        return montoRecibido;
    }

    public double getCambio()
    {
        return cambio;
    }

    public ArrayList<DetalleVenta> getDetalles()
    {
        return detalles;
    }

    // ===================== SETTERS =====================
    public void setFolio(int folio)
    {
        this.folio = folio;
    }

    public void setFecha(String fecha)
    {
        this.fecha = fecha;
    }

    public void setNombreCliente(String nombreCliente)
    {
        this.nombreCliente = nombreCliente;
    }

    public void setMetodoPago(String metodoPago)
    {
        this.metodoPago = metodoPago;
    }

    public void setMontoRecibido(double montoRecibido)
    {
        this.montoRecibido = montoRecibido;
    }

    public void setCambio(double cambio)
    {
        this.cambio = cambio;
    }

    // ===================== CARRITO =====================

    /**
     * Agrega un renglon. Si el mismo concepto ya estaba en el carrito,
     * solo suma la cantidad para no repetir renglones.
     */
    public void agregarDetalle(DetalleVenta objDetalle) {
        for (DetalleVenta objExistente : detalles) {
            if (objExistente.getServicio() == objDetalle.getServicio()) {
                objExistente.setCantidad(objExistente.getCantidad() + objDetalle.getCantidad());
                return;
            }
        }
        detalles.add(objDetalle);
    }

    /** Quita el renglon que esta en la posicion indicada. */
    public void quitarDetalle(int indice) {
        if (indice >= 0 && indice < detalles.size()) {
            detalles.remove(indice);
        }
    }

    /** Cuanto de un concepto ya esta en el carrito (sirve para revisar stock). */
    public double cantidadDe(Servicio objServicio) {
        for (DetalleVenta objDetalle : detalles) {
            if (objDetalle.getServicio() == objServicio) {
                return objDetalle.getCantidad();
            }
        }
        return 0;
    }

    public boolean estaVacia() {
        return detalles.isEmpty();
    }

    // ===================== TOTALES =====================

    /** Redondea a 2 decimales (centavos). */
    private double redondear(double valor) {
        return Math.round(valor * 100) / 100.0;
    }

    public double getSubtotal() {
        double suma = 0;
        for (DetalleVenta objDetalle : detalles) {
            suma += objDetalle.getSubtotal();
        }
        return redondear(suma);
    }

    public double getIva() {
        return redondear(getSubtotal() * InterfazENEL.IVA);
    }

    public double getTotal() {
        return redondear(getSubtotal() + getIva());
    }

    // ===================== TICKET =====================

    /** Arma el texto del ticket para mostrarlo en un JTextArea. */
    public String generarTicket() {
        StringBuilder sb = new StringBuilder();
        sb.append(InterfazENEL.NOMBRE_EMPRESA).append("\n");
        sb.append("Folio: ").append(folio).append("\n");
        sb.append("Fecha: ").append(fecha).append("\n");
        sb.append("Cliente: ").append(nombreCliente).append("\n");
        sb.append("----------------------------------------\n");
        for (DetalleVenta objDetalle : detalles) {
            sb.append(objDetalle.getServicio().getNombre()).append("\n");
            sb.append(String.format("   %.2f x $%,.2f = $%,.2f%n",
                    objDetalle.getCantidad(), objDetalle.getPrecioUnitario(), objDetalle.getSubtotal()));
        }
        sb.append("----------------------------------------\n");
        sb.append(String.format("Subtotal:  $%,.2f%n", getSubtotal()));
        sb.append(String.format("IVA 16%%:   $%,.2f%n", getIva()));
        sb.append(String.format("TOTAL:     $%,.2f%n", getTotal()));
        sb.append("Pago con:  ").append(metodoPago).append("\n");
        sb.append(String.format("Recibido:  $%,.2f%n", montoRecibido));
        sb.append(String.format("Cambio:    $%,.2f%n", cambio));
        sb.append("\n¡Gracias por su preferencia!");
        return sb.toString();
    }
}
