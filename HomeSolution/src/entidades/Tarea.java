package entidades;

public class Tarea {
	private String titulo;
	private String descripcion;

	private Empleado empleado;
	private boolean estaTerminada;

	private double diasEstimados;
	private double diasDeRetraso;
	private double diasDeTrabajoReales;

	private static final int cantidadHorasDiaCompleto = 8;
	private static final int cantidadHorasMedioDia = 4;
	private static final int adicionalEmpleadoSinRetrasos = 2;

	public Tarea(String titulo, String descripcion, double diasEstimados) {
		if (titulo == null || titulo == "")
			throw new IllegalArgumentException();
		if (descripcion == null || descripcion == "")
			throw new IllegalArgumentException();
		if (diasEstimados <= 0)
			throw new IllegalArgumentException();

		this.titulo = titulo;
		this.descripcion = descripcion;
		this.diasEstimados = diasEstimados;
	}

	public String obtenerTitulo() {
		return titulo;
	}

	public void asignarEmpleado(Empleado empleado) {
		this.empleado = empleado;
	}

	public Empleado obtenerEmpleado() {
		return empleado;
	}
}
