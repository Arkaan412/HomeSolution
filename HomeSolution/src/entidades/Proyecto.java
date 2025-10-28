package entidades;

import java.time.LocalDate;
import java.util.HashMap;

public class Proyecto {
	private static int siguienteID = 1;
	private int idProyecto;

	private HashMap<String, Tarea> tareas;

	private Cliente cliente;
	private String domicilio;
	private String estado; // Puede ser pendiente, activo o finalizado.

	private LocalDate fechaInicio;
	private LocalDate fechaFinEstimada;
	private LocalDate fechaFinReal;

	private double costoFinal;
	private static final int porcentajeAdicional = 35;
	private static final int porcentajeAdicionalConRetraso = 25;

	public Proyecto(String[] titulos, String[] descripcion, double[] dias, String domicilio, String[] cliente,
			String inicio, String fin) {
		tareas = new HashMap<>();
		crearTareas(titulos, descripcion, dias);

		this.domicilio = domicilio;

		crearCliente(cliente);

		fechaInicio = LocalDate.parse(inicio);
		fechaFinEstimada = LocalDate.parse(fin);
		fechaFinReal = fechaFinEstimada;

		estado = "Pendiente";
		idProyecto = siguienteID++;
	}

	private void crearTareas(String[] titulos, String[] descripcion, double[] dias) {
		if (titulos == null || descripcion == null || dias == null)
			throw new IllegalArgumentException();
		if (titulos.length != descripcion.length || descripcion.length != dias.length)
			throw new IllegalArgumentException();

		int cantidadTareas = titulos.length;

		for (int i = 0; i < cantidadTareas; i++) {
			agregarTarea(titulos[i], descripcion[i], dias[i]);
		}
	}

	public void agregarTarea(String titulo, String descripcion, double dias) {
		Tarea tarea = new Tarea(titulo, descripcion, dias);

		String tituloTarea = tarea.obtenerTitulo();
		tareas.put(tituloTarea, tarea);
	}

	private void crearCliente(String[] datosCliente) {
		if (datosCliente == null)
			throw new IllegalArgumentException();
		if (datosCliente.length != 3)
			throw new IllegalArgumentException();

		String nombre = datosCliente[0];
		String mail = datosCliente[1];
		String telefono = datosCliente[2];

		if (nombre == "" || mail == "" || telefono == "")
			throw new IllegalArgumentException();

		Cliente cliente = new Cliente(nombre, mail, telefono);

		this.cliente = cliente;
	}

	public Integer obtenerId() {
		return idProyecto;
	}

	public void asignarResponsableEnTarea(String titulo, Empleado empleado) {
		if (estaFinalizado())
			throw new RuntimeException();

		Tarea tarea = obtenerTarea(titulo);

		if (tarea.obtenerEmpleado() == null)
			throw new RuntimeException();

		tarea.asignarEmpleado(empleado);
	}

	public boolean estaFinalizado() {
		return estado == "Finalizado";
	}

	public Tarea obtenerTarea(String titulo) {
		return tareas.get(titulo);
	}

	public void registrarRetraso(String titulo, double cantidadDias) {
		Tarea tarea = obtenerTarea(titulo);
		if (tarea == null)
			throw new IllegalArgumentException();

		tarea.registrarRetraso(cantidadDias);

		actualizarFechaFinReal(cantidadDias);
	}

	private void actualizarFechaFinReal(double cantidadDias) {
		fechaFinReal = fechaFinReal.plusDays((long) cantidadDias);
	}

}
