package entidades;

public class EmpleadoDePlanta extends Empleado {
	private double valorDia;
	private String categoria; // "INICIAL", "T�CNICO" o "EXPERTO"

	public EmpleadoDePlanta(String nombre, double valorDia, String categoria) {
		super(nombre);

		if (valorDia <= 0)
			throw new IllegalArgumentException("El valor por d�a no puede ser menor o igual a 0.");
		if (!categoriaEsValida(categoria))
			throw new IllegalArgumentException("La categor�a indicada no es v�lida");

		this.valorDia = valorDia;
		this.categoria = categoria;
	}

	private boolean categoriaEsValida(String categoria) {
		return categoria.equalsIgnoreCase("INICIAL") || categoria.equalsIgnoreCase("T�CNICO")
				|| categoria.equalsIgnoreCase("EXPERTO");
	}

	@Override
	public double calcularCosto(double cantidadDeDias) {
		double cantidadHoras = Math.ceil(cantidadDeDias) * 8;

		return valorDia * cantidadHoras;
	}
}
