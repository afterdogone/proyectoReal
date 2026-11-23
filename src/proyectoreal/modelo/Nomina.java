package proyectoreal.modelo;

import proyectoreal.interfaz.InterfazENEL;

/**
 * Un pago de nomina ya realizado a un trabajador.
 *
 * Total neto = (dias x sueldo por dia)
 *            + (horas extra x pago por hora x 2)
 *            - descuentos
 *
 * @author EliLe
 */
public class Nomina {

    private int folio;
    private String fecha;
    private String nombreTrabajador;   // se copia por si luego eliminan al trabajador
    private double sueldoDia;
    private int diasTrabajados;
    private double horasExtra;
    private double descuentos;

    public Nomina(Trabajador objTrabajador, int diasTrabajados, double horasExtra, double descuentos) {
        this.nombreTrabajador = objTrabajador.getNombre();
        this.sueldoDia = objTrabajador.getSueldoDia();
        this.diasTrabajados = diasTrabajados;
        this.horasExtra = horasExtra;
        this.descuentos = descuentos;
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

    public String getNombreTrabajador()
    {
        return nombreTrabajador;
    }

    public double getSueldoDia()
    {
        return sueldoDia;
    }

    public int getDiasTrabajados()
    {
        return diasTrabajados;
    }

    public double getHorasExtra()
    {
        return horasExtra;
    }

    public double getDescuentos()
    {
        return descuentos;
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

    public void setNombreTrabajador(String nombreTrabajador)
    {
        this.nombreTrabajador = nombreTrabajador;
    }

    public void setSueldoDia(double sueldoDia)
    {
        this.sueldoDia = sueldoDia;
    }

    public void setDiasTrabajados(int diasTrabajados)
    {
        this.diasTrabajados = diasTrabajados;
    }

    public void setHorasExtra(double horasExtra)
    {
        this.horasExtra = horasExtra;
    }

    public void setDescuentos(double descuentos)
    {
        this.descuentos = descuentos;
    }

    // ===================== CALCULOS =====================
    public double getSueldoBase() {
        return diasTrabajados * sueldoDia;
    }

    public double getPagoHorasExtra() {
        double pagoPorHora = sueldoDia / InterfazENEL.HORAS_JORNADA;
        return horasExtra * pagoPorHora * InterfazENEL.FACTOR_HORA_EXTRA;
    }

    public double getTotalNeto() {
        return getSueldoBase() + getPagoHorasExtra() - descuentos;
    }
}
