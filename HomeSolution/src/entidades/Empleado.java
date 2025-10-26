package entidades;

public class Empleado {
	private static int siguienteLegajo = 1;

	private String nombre;
	private Integer legajo;
	private double valorHora;
	private boolean estaAsignado;

	public Empleado(String nombre) {
		if (nombre == null || nombre == "")
			throw new IllegalArgumentException();

		this.nombre = nombre;

		this.legajo = siguienteLegajo++;
		this.estaAsignado = false;
	}

	public boolean estaAsignado() {
		return estaAsignado;
	}

	public int obtenerLegajo() {
		return legajo;
	}
}
