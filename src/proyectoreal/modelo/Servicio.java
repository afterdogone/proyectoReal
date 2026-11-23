package proyectoreal.modelo;

import proyectoreal.interfaz.InterfazENEL;

/**
 * Elemento del catalogo: puede ser un SERVICIO de limpieza (no lleva stock)
 * o un INSUMO / producto que si se descuenta del inventario al venderse.
 *
 * @author EliLe
 */
public class Servicio {

    private int id;
    private String nombre;
    private String tipo;      // InterfazENEL.TIPO_SERVICIO o InterfazENEL.TIPO_INSUMO
    private double precio;    // precio base por unidad o por metro cuadrado
    private int stock;        // solo se usa si es insumo

    public Servicio(int id, String nombre, String tipo, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.precio = precio;
        this.stock = stock;
    }

    // ===================== GETTERS =====================
    public int getId()
    {
        return id;
    }

    public String getNombre()
    {
        return nombre;
    }

    public String getTipo()
    {
        return tipo;
    }

    public double getPrecio()
    {
        return precio;
    }

    public int getStock()
    {
        return stock;
    }

    // ===================== SETTERS =====================
    public void setId(int id)
    {
        this.id = id;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }

    public void setPrecio(double precio)
    {
        this.precio = precio;
    }

    public void setStock(int stock)
    {
        this.stock = stock;
    }

    /** true si es un insumo (maneja inventario). */
    public boolean esInsumo() {
        return InterfazENEL.TIPO_INSUMO.equals(tipo);
    }

    /** Resta piezas del inventario (solo si es insumo). */
    public void descontarStock(int cantidad) {
        if (esInsumo()) {
            stock = stock - cantidad;
        }
    }

    /** Texto para la columna "Stock" de la tabla del catalogo. */
    public String getStockTexto() {
        return esInsumo() ? String.valueOf(stock) : "N/A";
    }

    @Override
    public String toString() {
        return nombre + " (" + tipo + ")";
    }
}
