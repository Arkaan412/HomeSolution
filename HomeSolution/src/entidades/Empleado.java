package entidades;

public abstract class Empleado {
	private static int siguienteLegajo = 1;

	private String nombre;
	private Integer legajo;
	private boolean estaAsignado;
	private int cantidadDeRetrasos;

	protected Empleado(String nombre) {
		if (nombre == null || nombre.isEmpty())
			throw new IllegalArgumentException("El nombre no puede ser nulo ni estar vacío.");

		this.nombre = nombre;

		this.legajo = siguienteLegajo++;
		this.estaAsignado = false;
	}

	protected boolean estaAsignado() {
		return estaAsignado;
	}

	public int obtenerLegajo() {
		return legajo;
	}

	protected void registrarRetraso() {
		cantidadDeRetrasos++;
	}

	public int obtenerCantidadDeRetrasos() {
		return cantidadDeRetrasos;
	}

	protected void liberar() {
		estaAsignado = false;
	}

	protected void asignar() {
		estaAsignado = true;
	}

	public String obtenerNombre() {
		return nombre;
	}

	protected abstract double calcularCosto(double cantidadDeDias);

	@Override
	public String toString() {
//		String infoEmpleado = nombre + " | " + "Legajo: " + legajo;
		String infoEmpleado = legajo.toString();

		return infoEmpleado;
	}
}
