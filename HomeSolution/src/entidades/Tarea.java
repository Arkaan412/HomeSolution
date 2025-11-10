package entidades;

import java.util.Objects;

public class Tarea {
	private String titulo;
	private String descripcion;

	private Empleado empleado;
	private boolean estaFinalizada;

	private double diasEstimados;
	private double diasDeRetraso;
	public double diasDeTrabajoReales;

	protected Tarea(String titulo, String descripcion, double diasEstimados) {
		if (titulo == null || titulo == "")
			throw new IllegalArgumentException("El título no puede estar vacío.");
//		if (descripcion == null || descripcion == "")
		if (descripcion == null)
			throw new IllegalArgumentException("La descripción no puede estar vacía.");
		if (diasEstimados <= 0)
			throw new IllegalArgumentException("La cantidad de días debe ser mayor a 0.");
		if (diasEstimados % 0.5 != 0)
			throw new IllegalArgumentException("La cantidad de días debe ser múltiplo de 0.5");

		this.titulo = titulo;
		this.descripcion = descripcion;
		this.diasEstimados = Math.ceil(diasEstimados); // Si trabajó medio día, se lo cuenta como día completo.

		this.diasDeTrabajoReales = this.diasEstimados;
	}

	public String obtenerTitulo() {
		return titulo;
	}

	protected void asignarEmpleado(Empleado empleado) {
		empleado.asignar();

		this.empleado = empleado;
	}

	public Empleado obtenerEmpleado() {
		return empleado;
	}

	protected void registrarRetraso(double cantidadDias) {
		diasDeRetraso += cantidadDias;

		diasDeTrabajoReales = diasEstimados + diasDeRetraso;

		empleado.registrarRetraso();
	}

	protected Empleado finalizar() {
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

	protected boolean estaFinalizada() {
		return estaFinalizada;
	}

	protected Empleado reasignarEmpleado(Empleado nuevoEmpleado) {
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

		double costoEmpleado = empleado.calcularCosto(diasDeTrabajoReales);

		return costoEmpleado;
	}

	public boolean huboRetrasos() {
		return diasDeRetraso > 0;
	}

	@Override
	public String toString() {
		return titulo;
	}

	@Override
	public int hashCode() {
		return Objects.hash(descripcion, titulo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Tarea)) {
			return false;
		}
		Tarea other = (Tarea) obj;
		return Objects.equals(descripcion, other.descripcion) && Objects.equals(titulo, other.titulo);
	}
}
