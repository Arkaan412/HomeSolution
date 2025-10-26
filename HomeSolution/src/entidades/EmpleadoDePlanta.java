package entidades;

public class EmpleadoDePlanta extends Empleado {
	private double valorDia;
	private String categoria; // "INICIAL", "T�CNICO" o "EXPERTO"

	public EmpleadoDePlanta(String nombre, double valorDia, String categoria) {
		super(nombre);

		if (valorDia <= 0 || categoriaEsValida(categoria))
			throw new IllegalArgumentException();

		this.valorDia = valorDia;
		this.categoria = categoria;
	}

	private boolean categoriaEsValida(String categoria) {
		return (categoria == "INICIAL" || categoria == "T�CNICO" || categoria == "EXPERTO");
	}
}
