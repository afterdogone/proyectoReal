package proyectoreal.modelo;

import proyectoreal.interfaz.InterfazENEL;

/**
 * Trabajador de ENEL (hereda ID y nombre de Persona).
 *
 * @author EliLe
 */
public class Trabajador extends Persona {

    private String puesto;       // Operador o Supervisor
    private double sueldoDia;    // pago por dia / turno

    public Trabajador(int id, String nombre, String puesto, double sueldoDia) {
        super(id, nombre);
        this.puesto = puesto;
        this.sueldoDia = sueldoDia;
    }

    // ===================== GETTERS =====================
    public String getPuesto()
    {
        return puesto;
    }

    public double getSueldoDia()
    {
        return sueldoDia;
    }

    // ===================== SETTERS =====================
    public void setPuesto(String puesto)
    {
        this.puesto = puesto;
    }

    public void setSueldoDia(double sueldoDia)
    {
        this.sueldoDia = sueldoDia;
    }

    /** Lo que gana por hora normal: sueldo del dia entre las horas de la jornada. */
    public double getPagoPorHora() {
        return sueldoDia / InterfazENEL.HORAS_JORNADA;
    }

    @Override
    public String getDescripcion() {
        return getId() + " - " + getNombre() + " (" + puesto + ")";
    }
}
