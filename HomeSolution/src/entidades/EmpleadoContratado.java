package entidades;

public class EmpleadoContratado extends Empleado {
	private double valorHora;

	public EmpleadoContratado(String nombre, double valorHora) {
		super(nombre);

		if (valorHora <= 0)
			throw new IllegalArgumentException();

		this.valorHora = valorHora;
	}
}
