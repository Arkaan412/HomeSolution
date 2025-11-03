package entidades;

public class Cliente {
	String nombre;
	String mail;
	String telefono;

	public Cliente(String nombre, String mail, String telefono) {
		this.nombre = nombre;
		this.mail = mail;
		this.telefono = telefono;
	}

	@Override
	public String toString() {
		StringBuilder infoCliente = new StringBuilder();

		infoCliente.append("Nombre: " + nombre);
		infoCliente.append(" | ");
		infoCliente.append("Mail: " + mail);
		infoCliente.append(" | ");
		infoCliente.append("Teléfono: " + telefono);

		return infoCliente.toString();
	}
}
