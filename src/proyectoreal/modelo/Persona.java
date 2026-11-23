package proyectoreal.modelo;

/**
 * Clase padre (abstracta) de Cliente y Trabajador.
 * Guarda lo que ambos tienen en comun: un ID y un nombre.
 *
 * No se pueden crear objetos "Persona" directamente; solo de sus hijas.
 *
 * @author EliLe
 */
public abstract class Persona {

    private int id;
    private String nombre;

    public Persona(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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

    // ===================== SETTERS =====================
    public void setId(int id)
    {
        this.id = id;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    /**
     * Texto corto para mostrar a la persona en un JComboBox.
     * Cada clase hija lo escribe a su manera (polimorfismo).
     */
    public abstract String getDescripcion();

    @Override
    public String toString() {
        return getDescripcion();
    }
}
