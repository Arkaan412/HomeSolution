package entidades;

public class EmpleadoDePlanta extends Empleado {
	private double valorDia;
	private String categoria; // "INICIAL", "TÉCNICO" o "EXPERTO"

	protected EmpleadoDePlanta(String nombre, double valorDia, String categoria) {
		super(nombre);

		if (valorDia <= 0)
			throw new IllegalArgumentException("El valor por día no puede ser menor o igual a 0.");
		if (!categoriaEsValida(categoria))
			throw new IllegalArgumentException("La categoría indicada no es válida");

		this.valorDia = valorDia;
		this.categoria = categoria;
	}

	private boolean categoriaEsValida(String categoria) {
		return categoria.equalsIgnoreCase("INICIAL") || categoria.equalsIgnoreCase("TÉCNICO")
				|| categoria.equalsIgnoreCase("TECNICO") || categoria.equalsIgnoreCase("EXPERTO");
	}

	@Override
	protected double calcularCosto(double cantidadDeDias) {
		double costo = Math.ceil(cantidadDeDias); // Si trabajó medio día, se lo cuenta como día completo.

		return valorDia * costo;
	}
}
