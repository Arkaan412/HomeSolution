package entidades;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

public class HomeSolution implements IHomeSolution {

	private HashMap<Integer, Proyecto> proyectos;
	private HashMap<Integer, Empleado> empleados;
	private PriorityQueue<Empleado> empleadosNoAsignados;

	public HomeSolution() {
		proyectos = new HashMap<>();
		empleados = new HashMap<>();
		empleadosNoAsignados = new PriorityQueue<>(
				Comparator.comparingInt(empleado -> empleado.obtenerCantidadDeRetrasos()));
	}

	@Override
	public void registrarEmpleado(String nombre, double valor) throws IllegalArgumentException {
		Empleado empleado = new EmpleadoContratado(nombre, valor);

		int legajoEmpleado = empleado.obtenerLegajo();
		empleados.put(legajoEmpleado, empleado);

		empleadosNoAsignados.add(empleado);
	}

	@Override
	public void registrarEmpleado(String nombre, double valor, String categoria) throws IllegalArgumentException {
		Empleado empleado = new EmpleadoDePlanta(nombre, valor, categoria);

		int legajoEmpleado = empleado.obtenerLegajo();
		empleados.put(legajoEmpleado, empleado);

		empleadosNoAsignados.add(empleado);
	}

	@Override
	public void registrarProyecto(String[] titulos, String[] descripcion, double[] dias, String domicilio,
			String[] cliente, String inicio, String fin) throws IllegalArgumentException {
		Proyecto proyecto = new Proyecto(titulos, descripcion, dias, domicilio, cliente, inicio, fin);

		proyectos.put(proyecto.obtenerId(), proyecto);
	}

	@Override
	public void asignarResponsableEnTarea(Integer numero, String titulo) throws Exception {
		Empleado empleadoNoAsignado = obtenerEmpleadoNoAsignado();

		if (empleadoNoAsignado == null)
			throw new RuntimeException();

		Proyecto proyecto = proyectos.get(numero);

		int legajoEmpleado = empleadoNoAsignado.obtenerLegajo();
		actualizarEstadoEmpleadoEnRegistro(legajoEmpleado);

		proyecto.asignarResponsableEnTarea(titulo, empleadoNoAsignado);
	}

	private Empleado obtenerEmpleadoNoAsignado() {
		List<Empleado> empleados = new ArrayList<>(this.empleados.values());

		for (Empleado e : empleados) {
			if (!e.estaAsignado())
				return e;
		}

		return null;
	}

	private void actualizarEstadoEmpleadoEnRegistro(int legajoEmpleado) {
		Empleado empleado = empleados.get(legajoEmpleado);

		empleadosNoAsignados.remove(empleado);
	}

	@Override
	public void asignarResponsableMenosRetraso(Integer numero, String titulo) throws Exception {
		Proyecto proyecto = proyectos.get(numero);

		Empleado empleadoNoAsignado = obtenerEmpleadoConMenosRetrasos();

		if (empleadoNoAsignado == null)
			throw new RuntimeException();

		proyecto.asignarResponsableEnTarea(titulo, empleadoNoAsignado);
	}

	private Empleado obtenerEmpleadoConMenosRetrasos() {
		return empleadosNoAsignados.poll();
	}

	@Override
	public void registrarRetrasoEnTarea(Integer numero, String titulo, double cantidadDias)
			throws IllegalArgumentException {
		if (cantidadDias <= 0)
			throw new IllegalArgumentException();

		Proyecto proyecto = proyectos.get(numero);
		if (proyecto == null)
			throw new IllegalArgumentException();

		proyecto.registrarRetraso(titulo, cantidadDias);
	}

	@Override
	public void agregarTareaEnProyecto(Integer numero, String titulo, String descripcion, double dias)
			throws IllegalArgumentException {
		Proyecto proyecto = proyectos.get(numero);

		if (proyecto == null)
			throw new IllegalArgumentException();
		if (estaFinalizado(proyecto))
			throw new IllegalArgumentException();

		proyecto.agregarTarea(titulo, descripcion, dias);
	}

	@Override
	public void finalizarTarea(Integer numero, String titulo) throws Exception {
		Proyecto proyecto = proyectos.get(numero);

		if (proyecto == null)
			throw new IllegalArgumentException();
		if (estaFinalizado(proyecto))
			throw new IllegalArgumentException();

		proyecto.finalizarTarea(titulo);

		Empleado empleado = proyecto.obtenerTarea(titulo).obtenerEmpleado();

		liberarEmpleado(empleado);
	}

	private void liberarEmpleado(Empleado empleado) {
		empleadosNoAsignados.add(empleado);
		empleado.liberar();
	}

	@Override
	public void finalizarProyecto(Integer numero, String fin) throws IllegalArgumentException {
		Proyecto proyecto = proyectos.get(numero);

		if (proyecto == null)
			throw new IllegalArgumentException();
		if (estaFinalizado(proyecto))
			throw new IllegalArgumentException();

		proyecto.finalizarProyecto(fin);
	}

	@Override
	public void reasignarEmpleadoEnProyecto(Integer numero, Integer legajo, String titulo) throws Exception {
		if (!hayEmpleadosDisponibles())
			throw new IllegalArgumentException();

		Proyecto proyecto = proyectos.get(numero);

		if (proyecto == null)
			throw new IllegalArgumentException();
		if (estaFinalizado(proyecto))
			throw new IllegalArgumentException();

		Empleado empleado = empleados.get(legajo);

		if (empleado.estaAsignado())
			throw new IllegalArgumentException();

		Empleado empleadoAnterior = proyecto.reasignarEmpleado(titulo, empleado);

		liberarEmpleado(empleadoAnterior);
	}

	private boolean hayEmpleadosDisponibles() {
		return !empleadosNoAsignados.isEmpty();
	}

	@Override
	public void reasignarEmpleadoConMenosRetraso(Integer numero, String titulo) throws Exception {
		if (!hayEmpleadosDisponibles())
			throw new IllegalArgumentException();

		Empleado empleado = obtenerEmpleadoConMenosRetrasos();
		int legajo = empleado.obtenerLegajo();

		reasignarEmpleadoEnProyecto(numero, legajo, titulo);
	}

	@Override
	public double costoProyecto(Integer numero) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Tupla<Integer, String>> proyectosFinalizados() {
		List<Proyecto> proyectos = new ArrayList<>(this.proyectos.values());

		List<Tupla<Integer, String>> proyectosFinalizados = new ArrayList<>();

		for (Proyecto proyecto : proyectos) {
			if (estaFinalizado(proyecto)) {
				int numero = proyecto.obtenerId();
				String domicilio = proyecto.obtenerDomicilio();

				Tupla<Integer, String> proyectoFinalizado = new Tupla<Integer, String>(numero, domicilio);

				proyectosFinalizados.add(proyectoFinalizado);
			}
		}

		return proyectosFinalizados;
	}

	@Override
	public List<Tupla<Integer, String>> proyectosPendientes() {
		List<Proyecto> proyectos = new ArrayList<>(this.proyectos.values());

		List<Tupla<Integer, String>> proyectosPendientes = new ArrayList<>();

		for (Proyecto proyecto : proyectos) {
			if (proyecto.estaPendiente()) {
				int numero = proyecto.obtenerId();
				String domicilio = proyecto.obtenerDomicilio();

				Tupla<Integer, String> proyectoPendiente = new Tupla<Integer, String>(numero, domicilio);

				proyectosPendientes.add(proyectoPendiente);
			}
		}

		return proyectosPendientes;
	}

	@Override
	public List<Tupla<Integer, String>> proyectosActivos() {
		List<Proyecto> proyectos = new ArrayList<>(this.proyectos.values());

		List<Tupla<Integer, String>> proyectosActivos = new ArrayList<>();

		for (Proyecto proyecto : proyectos) {
			if (proyecto.estaActivo()) {
				int numero = proyecto.obtenerId();
				String domicilio = proyecto.obtenerDomicilio();

				Tupla<Integer, String> proyectoActivo = new Tupla<Integer, String>(numero, domicilio);

				proyectosActivos.add(proyectoActivo);
			}
		}

		return proyectosActivos;
	}

	@Override
	public Object[] empleadosNoAsignados() {
		return empleadosNoAsignados.toArray();
	}

	@Override
	public boolean estaFinalizado(Integer numero) {
		Proyecto proyecto = proyectos.get(numero);

		if (proyecto == null)
			throw new IllegalArgumentException();

		return proyecto.estaFinalizado();
	}

	public boolean estaFinalizado(Proyecto proyecto) {
		if (proyecto == null)
			throw new IllegalArgumentException();

		return proyecto.estaFinalizado();
	}

	@Override
	public int consultarCantidadRetrasosEmpleado(Integer legajo) {
		Empleado empleado = empleados.get(legajo);

		if (empleado == null)
			throw new IllegalArgumentException();

		return empleado.obtenerCantidadDeRetrasos();
	}

	@Override
	public List<Tupla<Integer, String>> empleadosAsignadosAProyecto(Integer numero) {
		Proyecto proyecto = proyectos.get(numero);

		if (proyecto == null)
			throw new IllegalArgumentException();

		List<Tupla<Integer, String>> empleadosAsignados = proyecto.empleadosAsignados();

		return empleadosAsignados;
	}

	@Override
	public Object[] tareasProyectoNoAsignadas(Integer numero) {
		Proyecto proyecto = proyectos.get(numero);

		if (proyecto == null)
			throw new IllegalArgumentException();

		Object[] tareasProyectoNoAsignadas = proyecto.tareasProyectoNoAsignadas();

		return tareasProyectoNoAsignadas;
	}

	@Override
	public Object[] tareasDeUnProyecto(Integer numero) {
		Proyecto proyecto = proyectos.get(numero);

		if (proyecto == null)
			throw new IllegalArgumentException();

		Object[] tareasDeUnProyecto = proyecto.tareasDeUnProyecto();

		return tareasDeUnProyecto;
	}

	@Override
	public String consultarDomicilioProyecto(Integer numero) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean tieneRestrasos(Integer legajo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Tupla<Integer, String>> empleados() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String consultarProyecto(Integer numero) {
		// TODO Auto-generated method stub
		return null;
	}
}
