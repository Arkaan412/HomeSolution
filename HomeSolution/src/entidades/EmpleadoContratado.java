package entidades;

public class EmpleadoContratado extends Empleado {
	private double valorHora;

	protected EmpleadoContratado(String nombre, double valorHora) {
		super(nombre);

		if (valorHora <= 0)
			throw new IllegalArgumentException("El valor por hora no puede ser menor o igual a 0.");

		this.valorHora = valorHora;
	}

	@Override
	protected double calcularCosto(double cantidadDeDias) {
		double costo = cantidadDeDias * 8;

		return costo * valorHora;
	}
}
