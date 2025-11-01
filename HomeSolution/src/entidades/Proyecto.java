package entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Proyecto {
	private static int siguienteID = 1;
	private int idProyecto;

	private HashMap<String, Tarea> tareas;

	private Cliente cliente;
	private String domicilio;
	private String estadoS; // Puede ser pendiente, activo o finalizado.

	private LocalDate fechaInicio;
	private LocalDate fechaFinEstimada;
	private LocalDate fechaFinReal;

	private boolean huboRetrasos;

	private double costoFinal;
	private static final double porcentajeAdicional = 1.35;
	private static final double porcentajeAdicionalConRetrasos = 1.25;

	public Proyecto(String[] titulos, String[] descripcion, double[] dias, String domicilio, String[] cliente,
			String inicio, String fin) {
		tareas = new HashMap<>();
		crearTareas(titulos, descripcion, dias);

		this.domicilio = domicilio;

		crearCliente(cliente);

		fechaInicio = LocalDate.parse(inicio);
		fechaFinEstimada = LocalDate.parse(fin);
		fechaFinReal = fechaFinEstimada;

		estadoS = Estado.pendiente;
		idProyecto = siguienteID++;
	}

	private void crearTareas(String[] titulos, String[] descripcion, double[] dias) {
		if (titulos == null || descripcion == null || dias == null)
			throw new IllegalArgumentException("Uno o m�s par�metros son nulos.");
		if (titulos.length != descripcion.length || descripcion.length != dias.length)
			throw new IllegalArgumentException("La cantidad de elementos por par�metro no coincide.");

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
			throw new IllegalArgumentException("Los datos del cliente no pueden ser nulos.");
		if (datosCliente.length != 3)
			throw new IllegalArgumentException("La cantidad de datos proporcionada es distinta de 3.");

		String nombre = datosCliente[0];
		String mail = datosCliente[1];
		String telefono = datosCliente[2];

//		if (nombre == "" || mail == "" || telefono == "")
		if (nombre == "")
			throw new IllegalArgumentException("El nombre no puede estar vac�o.");

		Cliente cliente = new Cliente(nombre, mail, telefono);

		this.cliente = cliente;
	}

	public Integer obtenerId() {
		return idProyecto;
	}

	public void asignarResponsableEnTarea(String titulo, Empleado empleado) {
		if (estaFinalizado())
			throw new IllegalArgumentException("El proyecto ya est� finalizado.");

		Tarea tarea = obtenerTarea(titulo);

		if (tarea.obtenerEmpleado() != null)
			throw new IllegalArgumentException("La tarea indicada ya ten�a un empleado asignado.");

		tarea.asignarEmpleado(empleado);

		activarProyecto();

		calcularCostoProyecto();
	}

	private void activarProyecto() {
		estadoS = Estado.activo;

	}

	public boolean estaFinalizado() {
		return estadoS.equalsIgnoreCase(Estado.finalizado);
	}

	public Tarea obtenerTarea(String titulo) {
		return tareas.get(titulo);
	}

	public void registrarRetraso(String titulo, double cantidadDias) {
		Tarea tarea = obtenerTarea(titulo);
		if (tarea == null)
			throw new IllegalArgumentException("La tarea indicada no existe.");

		tarea.registrarRetraso(cantidadDias);

		actualizarFechaFinReal(cantidadDias);

		calcularCostoProyecto();
	}

	private void actualizarFechaFinReal(double cantidadDias) {
		fechaFinReal = fechaFinReal.plusDays((long) cantidadDias);
	}

	public void finalizarTarea(String titulo) {
		Tarea tarea = obtenerTarea(titulo);
		if (tarea == null)
			throw new IllegalArgumentException("La tarea indicada no existe.");

		tarea.finalizar();

		calcularCostoProyecto();
	}

	public void finalizarProyecto(String fin) {
		LocalDate fechaFin = LocalDate.parse(fin);

		if (fechaFin.isBefore(fechaInicio))
			throw new IllegalArgumentException(
					"La fecha de finalizaci�n no puede ser anterior a la fecha de inicio del proyecto.");

		fechaFinReal = fechaFin;

		estadoS = Estado.finalizado;

		calcularCostoProyecto();
	}

	public Empleado reasignarEmpleado(String titulo, Empleado empleado) {
		Tarea tarea = obtenerTarea(titulo);
		if (tarea == null)
			throw new IllegalArgumentException("La tarea indicada no existe.");

		Empleado empleadoAnterior = tarea.reasignarEmpleado(empleado);

		calcularCostoProyecto();

		return empleadoAnterior;
	}

	public String obtenerDomicilio() {
		return domicilio;
	}

	public boolean estaPendiente() {
		return estadoS.equalsIgnoreCase(Estado.pendiente);
	}

	public boolean estaActivo() {
		return estadoS.equalsIgnoreCase(Estado.activo);
	}

	public List<Tupla<Integer, String>> empleadosAsignados() {
		List<Tarea> tareas = new ArrayList<>(this.tareas.values());

		List<Tupla<Integer, String>> empleadosAsignados = new ArrayList<>();

		for (Tarea tarea : tareas) {
			Empleado empleadoAsignado = tarea.obtenerEmpleado();

			if (empleadoAsignado != null) {
				int legajo = empleadoAsignado.obtenerLegajo();
				String nombre = empleadoAsignado.obtenerNombre();

				Tupla<Integer, String> datosEmpleado = new Tupla<Integer, String>(legajo, nombre);

				empleadosAsignados.add(datosEmpleado);
			}
		}

		return empleadosAsignados;
	}

	public Object[] tareasProyectoNoAsignadas() {
		List<Tarea> tareas = new ArrayList<>(this.tareas.values());

		List<Tarea> tareasNoAsignadas = new ArrayList<>();

		for (Tarea tarea : tareas) {
			Empleado empleadoAsignado = tarea.obtenerEmpleado();

			if (empleadoAsignado == null)
				tareasNoAsignadas.add(tarea);
		}

		return tareasNoAsignadas.toArray();
	}

	public Object[] tareasDeUnProyecto() {
		List<Tarea> tareas = new ArrayList<>(this.tareas.values());

		return tareas.toArray();
	}

	public double costoProyecto() {
		return costoFinal;
	}

	private void calcularCostoProyecto() {
		List<Tarea> tareas = new ArrayList<>(this.tareas.values());

		double costoProyecto = 0;

		for (Tarea tarea : tareas) {
			double costoTarea = tarea.obtenerCosto();

			huboRetrasos |= tarea.huboRetrasos();

			costoProyecto += costoTarea;
		}

		if (huboRetrasos)
			costoProyecto *= porcentajeAdicionalConRetrasos;
		else
			costoProyecto *= porcentajeAdicional;

		costoFinal = costoProyecto;
	}

	@Override
	public String toString() {
		StringBuilder infoProyecto = new StringBuilder();

		infoProyecto.append("Proyecto n�" + idProyecto + ": \n");
		infoProyecto.append("\t");
		infoProyecto.append("Domicilio:" + domicilio + "\n");
		infoProyecto.append("\t");
		infoProyecto.append("Cliente:" + cliente + "\n");
		infoProyecto.append("\t");
		infoProyecto.append("Fecha finalizaci�n real:" + fechaFinReal + "\n");
		infoProyecto.append("\t");
		infoProyecto.append("Tareas realizadas:" + "\n");
		infoProyecto.append(armarLineasDeTareas());
		infoProyecto.append("\t");
		infoProyecto.append("Costo final:" + costoFinal + "\n");
		infoProyecto.append("\t");
		infoProyecto.append("�Hubo retrasos?: " + (huboRetrasos ? "S�" : "NO"));

		return infoProyecto.toString();
	}

	private String armarLineasDeTareas() {
		StringBuilder infoTareas = new StringBuilder();
		ArrayList<Tarea> tareas = new ArrayList<>(this.tareas.values());

		for (Tarea tarea : tareas) {
			infoTareas.append("\t\t	");
			infoTareas.append(tarea);
			infoTareas.append("\n");
		}

		String lineasDeTareas = infoTareas.toString();

		return lineasDeTareas;
	}
}
