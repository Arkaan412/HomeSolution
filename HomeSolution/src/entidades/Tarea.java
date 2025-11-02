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
			throw new IllegalArgumentException("El título no puede estar vacío.");
//		if (descripcion == null || descripcion == "")
		if (descripcion == null)
			throw new IllegalArgumentException("La descripción no puede estar vacía.");
		if (diasEstimados <= 0)
			throw new IllegalArgumentException("La cantidad de días debe ser mayor a 0.");

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

		empleado.registrarRetraso();
	}

	public Empleado finalizar() {
		if (estaFinalizada())
			throw new RuntimeException("La tarea ya está finalizada.");
		if (empleado == null)
			throw new RuntimeException("La tarea no tiene ningún empleado asignado. No es posible finalizarla.");

		estaFinalizada = true;

		diasDeTrabajoReales = diasEstimados + diasDeRetraso;

		liberarEmpleado();

		return empleado;
	}

	private void liberarEmpleado() {
		empleado.liberar();
	}

	public boolean estaFinalizada() {
		return estaFinalizada;
	}

	public Empleado reasignarEmpleado(Empleado nuevoEmpleado) {
		if (this.empleado == null)
			throw new RuntimeException("La tarea indicada no tenía un empleado asignado.");

		Empleado empleadoAnterior = this.empleado;
		this.empleado.liberar();

		asignarEmpleado(nuevoEmpleado);

		return empleadoAnterior;
	}

	public double obtenerCosto() {
		if (empleado == null)
			return 0.0;
//		if (empleado == null)
//			throw new IllegalArgumentException("No se puede calcular el costo de la tarea '" + titulo
//					+ "' debido a que no tiene un empleado asignado.");

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
