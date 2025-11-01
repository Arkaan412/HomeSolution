package entidades;

public class Tarea {
	private String titulo;
	private String descripcion;

	private Empleado empleado;
	private boolean estaFinalizada;

	private double diasEstimados;
	private double diasDeRetraso;
	private double diasDeTrabajoReales;

	private static final int cantidadHorasDiaCompleto = 8;
	private static final int cantidadHorasMedioDia = 4;
	private static final double adicionalEmpleadoSinRetrasos = 1.02;

	public Tarea(String titulo, String descripcion, double diasEstimados) {
		if (titulo == null || titulo == "")
			throw new IllegalArgumentException("El t�tulo no puede estar vac�o.");
//		if (descripcion == null || descripcion == "")
		if (descripcion == null)
			throw new IllegalArgumentException("La descripci�n no puede estar vac�a.");
		if (diasEstimados <= 0)
			throw new IllegalArgumentException("La cantidad de d�as debe ser mayor a 0.");

		this.titulo = titulo;
		this.descripcion = descripcion;
		this.diasEstimados = diasEstimados;
	}

	public String obtenerTitulo() {
		return titulo;
	}

	public void asignarEmpleado(Empleado empleado) {
		empleado.asignar();

		this.empleado = empleado;
	}

	public Empleado obtenerEmpleado() {
		return empleado;
	}

	public void registrarRetraso(double cantidadDias) {
		diasDeRetraso += cantidadDias;
	}

	public void finalizar() {
		if (estaFinalizada())
			throw new RuntimeException("La tarea ya est� finalizada.");

		estaFinalizada = true;

		diasDeTrabajoReales = diasEstimados + diasDeRetraso;

		liberarEmpleado();
	}

	private void liberarEmpleado() {
		empleado.liberar();
	}

	public boolean estaFinalizada() {
		return estaFinalizada;
	}

	public Empleado reasignarEmpleado(Empleado nuevoEmpleado) {
		if (this.empleado == null)
			throw new RuntimeException("La tarea indicada no ten�a un empleado asignado.");

		Empleado empleadoAnterior = this.empleado;
		this.empleado.liberar();

		asignarEmpleado(nuevoEmpleado);

		return empleadoAnterior;
	}

	public double obtenerCosto() {
		if (empleado == null)
			throw new IllegalArgumentException("No se puede calcular el costo de la tarea '" + titulo
					+ "' debido a que no tiene un empleado asignado.");

		double costoEmpleado = empleado.calcularCosto(diasDeTrabajoReales);

		boolean esEmpleadoDePlanta = empleado instanceof EmpleadoDePlanta;

		if (!esEmpleadoDePlanta)
			return costoEmpleado;

		if (!huboRetrasos())
			return costoEmpleado * adicionalEmpleadoSinRetrasos;

		return costoEmpleado;
	}

	public boolean huboRetrasos() {
		return diasDeRetraso > 0;
	}

	@Override
	public String toString() {
		return titulo;
	}
}
