package entidades;

public class EmpleadoDePlanta extends Empleado {
	private double valorDia;
	private String categoria; // "INICIAL", "TÉCNICO" o "EXPERTO"

	public EmpleadoDePlanta(String nombre, double valorDia, String categoria) {
		super(nombre);

		if (valorDia <= 0 || !categoriaEsValida(categoria))
			throw new IllegalArgumentException();

		this.valorDia = valorDia;
		this.categoria = categoria;
	}

	private boolean categoriaEsValida(String categoria) {
		return (categoria == "INICIAL" || categoria == "TÉCNICO" || categoria == "EXPERTO");
	}

	@Override
	public double calcularCosto(double cantidadDeDias) {
		double cantidadHoras = Math.ceil(cantidadDeDias) * 8;

		return valorDia * cantidadHoras;
	}
}
