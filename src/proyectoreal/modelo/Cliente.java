package proyectoreal.modelo;

/**
 * Cliente de la cartera de ENEL (hereda ID y nombre de Persona).
 *
 * @author EliLe
 */
public class Cliente extends Persona {

    private String telefono;
    private String direccion;
    private String tipoServicio;   // Comercial, Residencial o Industrial

    public Cliente(int id, String nombre, String telefono, String direccion, String tipoServicio) {
        super(id, nombre);
        this.telefono = telefono;
        this.direccion = direccion;
        this.tipoServicio = tipoServicio;
    }

    // ===================== GETTERS =====================
    public String getTelefono()
    {
        return telefono;
    }

    public String getDireccion()
    {
        return direccion;
    }

    public String getTipoServicio()
    {
        return tipoServicio;
    }

    // ===================== SETTERS =====================
    public void setTelefono(String telefono)
    {
        this.telefono = telefono;
    }

    public void setDireccion(String direccion)
    {
        this.direccion = direccion;
    }

    public void setTipoServicio(String tipoServicio)
    {
        this.tipoServicio = tipoServicio;
    }

    @Override
    public String getDescripcion() {
        return getId() + " - " + getNombre() + " (" + tipoServicio + ")";
    }
}
