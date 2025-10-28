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

		Empleado empleadoNoAsignado = empleadosNoAsignados.poll();

		if (empleadoNoAsignado == null)
			throw new RuntimeException();

		proyecto.asignarResponsableEnTarea(titulo, empleadoNoAsignado);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void finalizarTarea(Integer numero, String titulo) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void finalizarProyecto(Integer numero, String fin) throws IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	@Override
	public void reasignarEmpleadoEnProyecto(Integer numero, Integer legajo, String titulo) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void reasignarEmpleadoConMenosRetraso(Integer numero, String titulo) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public double costoProyecto(Integer numero) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Tupla<Integer, String>> proyectosFinalizados() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Tupla<Integer, String>> proyectosPendientes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Tupla<Integer, String>> proyectosActivos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] empleadosNoAsignados() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean estaFinalizado(Integer numero) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int consultarCantidadRetrasosEmpleado(Integer legajo) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Tupla<Integer, String>> empleadosAsignadosAProyecto(Integer numero) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] tareasProyectoNoAsignadas(Integer numero) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] tareasDeUnProyecto(Integer numero) {
		// TODO Auto-generated method stub
		return null;
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
