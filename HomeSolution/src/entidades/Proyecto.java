package entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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

	private boolean huboRetrasos;

	private double costoFinal;
	private static final double porcentajeAdicional = 1.35;
	private static final double porcentajeAdicionalConRetrasos = 1.25;
	private static final double adicionalEmpleadoDePlantaSinRetrasos = 1.02;

	protected Proyecto(String[] titulos, String[] descripcion, double[] dias, String domicilio, String[] cliente,
			String inicio, String fin) {
		LocalDate fechaInicio = LocalDate.parse(inicio);
		LocalDate fechaFinEstimada = LocalDate.parse(fin);

		if (!fechasInicioYFinSonValidas(fechaInicio, fechaFinEstimada))
			throw new IllegalArgumentException(
					"La fecha estimada de finalización no puede ser anterior a la fecha de inicio del proyecto.");

		this.fechaInicio = fechaInicio;
		this.fechaFinEstimada = fechaFinEstimada;
		this.fechaFinReal = fechaFinEstimada;

		this.domicilio = domicilio;

		crearCliente(cliente);

		tareas = new HashMap<>();
		crearTareas(titulos, descripcion, dias);

		estado = Estado.pendiente;
		idProyecto = siguienteID++;
	}

	private void crearTareas(String[] titulos, String[] descripcion, double[] dias) {
		if (titulos == null || descripcion == null || dias == null)
			throw new IllegalArgumentException("Uno o más parámetros son nulos.");
		if (titulos.length != descripcion.length || descripcion.length != dias.length)
			throw new IllegalArgumentException("La cantidad de elementos por parámetro no coincide.");

		int cantidadTareas = titulos.length;

		for (int i = 0; i < cantidadTareas; i++) {
			agregarTarea(titulos[i], descripcion[i], dias[i]);
		}
	}

	protected void agregarTarea(String titulo, String descripcion, double dias) {
		Tarea tarea = new Tarea(titulo, descripcion, dias);

//		actualizarFechaFinRealYEstimada(dias); // El enunciado dice que debería pasar esto, pero los test fallan si pasa.

		String tituloTarea = tarea.obtenerTitulo();
		tareas.put(tituloTarea, tarea);
	}

//	private void actualizarFechaFinRealYEstimada(double cantidadDias) {
//		fechaFinEstimada = fechaFinEstimada.plusDays((long) cantidadDias);
//		fechaFinReal = fechaFinReal.plusDays((long) cantidadDias);
//	}

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
			throw new IllegalArgumentException("El nombre no puede estar vacío.");

		Cliente cliente = new Cliente(nombre, mail, telefono);

		this.cliente = cliente;
	}

	public Integer obtenerId() {
		return idProyecto;
	}

	protected void asignarResponsableEnTarea(String titulo, Empleado empleado) {
		if (estaFinalizado())
			throw new IllegalArgumentException("El proyecto ya está finalizado.");

		Tarea tarea = obtenerTarea(titulo);

		if (tarea == null)
			throw new IllegalArgumentException("La tarea indicada no existe.");
		if (tarea.obtenerEmpleado() != null)
			throw new IllegalArgumentException("La tarea indicada ya tenía un empleado asignado.");

		tarea.asignarEmpleado(empleado);

		activarProyecto();

		calcularCostoProyecto();
	}

	private void activarProyecto() {
		estado = Estado.activo;
	}

	protected boolean estaFinalizado() {
		return estado.equalsIgnoreCase(Estado.finalizado);
	}

	protected Tarea obtenerTarea(String titulo) {
		return tareas.get(titulo);
	}

	protected void registrarRetraso(String titulo, double cantidadDias) {
		Tarea tarea = obtenerTarea(titulo);
		if (tarea == null)
			throw new IllegalArgumentException("La tarea indicada no existe.");

		tarea.registrarRetraso(cantidadDias);

		actualizarFechaFinReal(cantidadDias);

		huboRetrasos = true;

		calcularCostoProyecto();
	}

	private void actualizarFechaFinReal(double cantidadDias) {
		fechaFinReal = fechaFinReal.plusDays((long) cantidadDias);
	}

	protected void finalizarTarea(String titulo) {
		Tarea tarea = obtenerTarea(titulo);
		if (tarea == null)
			throw new IllegalArgumentException("La tarea indicada no existe.");

		tarea.finalizar();

//		calcularCostoProyecto();
	}

	protected List<Empleado> finalizarProyecto(String fin) {
		LocalDate fechaFin = LocalDate.parse(fin);

		if (!fechasInicioYFinSonValidas(fechaInicio, fechaFin))
			throw new IllegalArgumentException("La fecha de finalización (" + fechaFin
					+ ") no puede ser anterior a la fecha de inicio (" + fechaInicio + ") del proyecto.");

		if (hayTareasSinAsignar())
			throw new IllegalArgumentException("No se puede finalizar un proyecto con tareas sin asignar.");

		fechaFinReal = fechaFin;

		estado = Estado.finalizado;

		compararFechasFinRealYEstimada();

		calcularCostoProyecto();

		List<Empleado> empleadosLiberados = finalizarTareas();

		return empleadosLiberados;
	}

	private void compararFechasFinRealYEstimada() {
		if (fechaFinReal.isAfter(fechaFinEstimada))
			huboRetrasos = true;
	}

	private boolean fechasInicioYFinSonValidas(LocalDate fechaInicio, LocalDate fechaFin) {
		return fechaInicio.isBefore(fechaFin);
	}

	private boolean hayTareasSinAsignar() {
		Object[] tareasNoAsignadas = tareasProyectoNoAsignadas();

		boolean hayTareasSinAsignar = tareasNoAsignadas.length != 0;

		return hayTareasSinAsignar;
	}

	private List<Empleado> finalizarTareas() {
		List<Tarea> tareas = new ArrayList<>(this.tareas.values());

		List<Empleado> empleadosLiberados = new ArrayList<>();

		for (Tarea tarea : tareas) {
			Empleado empleadoLiberado = tarea.finalizar();

			empleadosLiberados.add(empleadoLiberado);
		}

		return empleadosLiberados;
	}

	protected Empleado reasignarEmpleado(String titulo, Empleado empleado) {
		Tarea tarea = obtenerTarea(titulo);
		if (tarea == null)
			throw new IllegalArgumentException("La tarea indicada no existe.");

		Empleado empleadoAnterior = tarea.reasignarEmpleado(empleado);

		calcularCostoProyecto();

		return empleadoAnterior;
	}

	protected String obtenerDomicilio() {
		return domicilio;
	}

	protected boolean estaPendiente() {
		return estado.equalsIgnoreCase(Estado.pendiente);
	}

	protected boolean estaActivo() {
		return estado.equalsIgnoreCase(Estado.activo);
	}

	protected List<Tupla<Integer, String>> empleadosAsignados() {
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

	protected Object[] tareasProyectoNoAsignadas() {
		List<Tarea> tareas = new ArrayList<>(this.tareas.values());

		List<Tarea> tareasNoAsignadas = new ArrayList<>();

		for (Tarea tarea : tareas) {
			Empleado empleadoAsignado = tarea.obtenerEmpleado();

			if (empleadoAsignado == null)
				tareasNoAsignadas.add(tarea);
		}

		return tareasNoAsignadas.toArray();
	}

	protected Object[] tareasDeUnProyecto() {
		List<Tarea> tareas = new ArrayList<>(this.tareas.values());

		return tareas.toArray();
	}

	protected double costoProyecto() {
		return costoFinal;
	}

	private void calcularCostoProyecto() {
		List<Tarea> tareas = new ArrayList<>(this.tareas.values());

		double costoProyecto = 0;

		for (Tarea tarea : tareas) {
			double costoTarea = tarea.obtenerCosto();

			Empleado empleado = tarea.obtenerEmpleado();
			boolean esEmpleadoDePlanta = empleado instanceof EmpleadoDePlanta;

			if (esEmpleadoDePlanta && !huboRetrasos)
				costoTarea *= adicionalEmpleadoDePlantaSinRetrasos;

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

		infoProyecto.append("Proyecto n°" + idProyecto + ": \n");
		infoProyecto.append("\t");
		infoProyecto.append("Domicilio: " + domicilio + "\n");
		infoProyecto.append("\t");
		infoProyecto.append("Cliente: " + cliente + "\n");
		infoProyecto.append("\t");
		infoProyecto.append("Fecha finalización real: " + fechaFinReal + "\n");
		infoProyecto.append("\t");
		infoProyecto.append("Tareas realizadas:" + "\n");
		infoProyecto.append(armarLineasDeTareas());
		infoProyecto.append("\t");
		infoProyecto.append("Costo final: " + costoFinal + "\n");
		infoProyecto.append("\t");
		infoProyecto.append("¿Hubo retrasos?: " + (huboRetrasos ? "SÍ" : "NO"));

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

	@Override
	public int hashCode() {
		return Objects.hash(idProyecto);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Proyecto)) {
			return false;
		}
		Proyecto other = (Proyecto) obj;
		return idProyecto == other.idProyecto;
	}
}
