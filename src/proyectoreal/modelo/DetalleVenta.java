package proyectoreal.modelo;

/**
 * Un renglon del carrito / ticket: que se vendio, cuanto y a que precio.
 *
 * @author EliLe
 */
public class DetalleVenta {

    private Servicio objServicio;
    private double cantidad;         // piezas o metros cuadrados
    private double precioUnitario;   // se copia al momento de vender

    public DetalleVenta(Servicio objServicio, double cantidad) {
        this.objServicio = objServicio;
        this.cantidad = cantidad;
        this.precioUnitario = objServicio.getPrecio();
    }

    // ===================== GETTERS =====================
    public Servicio getServicio()
    {
        return objServicio;
    }

    public double getCantidad()
    {
        return cantidad;
    }

    public double getPrecioUnitario()
    {
        return precioUnitario;
    }

    // ===================== SETTERS =====================
    public void setServicio(Servicio objServicio)
    {
        this.objServicio = objServicio;
    }

    public void setCantidad(double cantidad)
    {
        this.cantidad = cantidad;
    }

    public void setPrecioUnitario(double precioUnitario)
    {
        this.precioUnitario = precioUnitario;
    }

    /** Cantidad por precio unitario (sin IVA). */
    public double getSubtotal() {
        return cantidad * precioUnitario;
    }
}
