package entidades;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
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

		int idProyecto = proyecto.obtenerId();

		proyectos.put(idProyecto, proyecto);

//		asignarTareas(idProyecto); // Tendría sentido pensar que al crearse el proyecto, ya se intente asignar empleados libres a las tareas. Esto parece no ser correcto debido a que este comportamiento provoca que fallen varios tests de la test suite provista por la materia. Al no ser esto posible, el requisito de que se pueda consultar el costo total de un proyecto en cualquier momento se ve afectado, pues no puedo saber cuánto costará cada tarea si no conozco el sueldo del empleado asignado. Entonces, el costo del proyecto tendrá sentido a partir del momento en que todas las tareas estén asignadas.

	}

//	private void asignarTareas(int idProyecto) {
//		Object[] tareasNoAsignadas = tareasProyectoNoAsignadas(idProyecto);
//
//		for (Object t : tareasNoAsignadas) {
//			Tarea tarea = (Tarea) t;
//
//			String tituloTarea = tarea.obtenerTitulo();
//
//			try {
//				asignarResponsableEnTarea(idProyecto, tituloTarea);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

	@Override
	public void asignarResponsableEnTarea(Integer numero, String titulo) throws Exception {
		Proyecto proyecto = proyectos.get(numero);
		if (proyecto == null)
			throw new IllegalArgumentException("No existe ningún proyecto con código " + numero);

		Empleado empleadoNoAsignado = obtenerEmpleadoNoAsignado();
		
		if (empleadoNoAsignado == null)
			throw new RuntimeException("No hay empleados disponibles.");

		proyecto.asignarResponsableEnTarea(titulo, empleadoNoAsignado);
		
		int legajoEmpleado = empleadoNoAsignado.obtenerLegajo();

		removerEmpleadoDeRegistroDeNoAsignados(legajoEmpleado);
	}

	private Empleado obtenerEmpleadoNoAsignado() {
		List<Empleado> empleados = new ArrayList<>(this.empleados.values());

		for (Empleado empleado : empleados) {
			if (!empleado.estaAsignado())
				return empleado;
		}

		return null;
	}

	private void removerEmpleadoDeRegistroDeNoAsignados(int legajoEmpleado) {
		Empleado empleado = empleados.get(legajoEmpleado);

		empleadosNoAsignados.remove(empleado);
	}

	@Override
	public void asignarResponsableMenosRetraso(Integer numero, String titulo) throws Exception {
		Proyecto proyecto = proyectos.get(numero);

		Empleado empleadoNoAsignado = obtenerEmpleadoConMenosRetrasos();

		if (empleadoNoAsignado == null)
			throw new RuntimeException("No hay empleados disponibles.");

		proyecto.asignarResponsableEnTarea(titulo, empleadoNoAsignado);
	}

	private Empleado obtenerEmpleadoConMenosRetrasos() {
		return empleadosNoAsignados.poll();
	}

	@Override
	public void registrarRetrasoEnTarea(Integer numero, String titulo, double cantidadDias)
			throws IllegalArgumentException {
		if (cantidadDias <= 0)
			throw new IllegalArgumentException("La cantidad de días no puede ser menor o igual a 0.");

		Proyecto proyecto = proyectos.get(numero);
		if (proyecto == null)
			throw new IllegalArgumentException("No existe ningún proyecto con código " + numero);

		proyecto.registrarRetraso(titulo, cantidadDias);
	}

	@Override
	public void agregarTareaEnProyecto(Integer numero, String titulo, String descripcion, double dias)
			throws IllegalArgumentException {
		Proyecto proyecto = proyectos.get(numero);

		if (proyecto == null)
			throw new IllegalArgumentException("No existe ningún proyecto con código " + numero);
		if (estaFinalizado(proyecto))
			throw new IllegalArgumentException("El proyecto ya está finalizado.");

		proyecto.agregarTarea(titulo, descripcion, dias);
	}

	@Override
	public void finalizarTarea(Integer numero, String titulo) throws Exception {
		Proyecto proyecto = proyectos.get(numero);

		if (proyecto == null)
			throw new IllegalArgumentException("No existe ningún proyecto con código " + numero);
		if (estaFinalizado(proyecto))
			throw new IllegalArgumentException("El proyecto ya está finalizado.");

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
			throw new IllegalArgumentException("No existe ningún proyecto con código " + numero);
		if (estaFinalizado(proyecto))
			throw new IllegalArgumentException("El proyecto ya está finalizado.");

		List<Empleado> empleadosLiberados = proyecto.finalizarProyecto(fin);

		empleadosNoAsignados.addAll(empleadosLiberados);
	}

	@Override
	public void reasignarEmpleadoEnProyecto(Integer numero, Integer legajo, String titulo) throws Exception {
		if (!hayEmpleadosDisponibles())
			throw new IllegalArgumentException("No hay empleados disponibles.");

		Proyecto proyecto = proyectos.get(numero);

		if (proyecto == null)
			throw new IllegalArgumentException("No existe ningún proyecto con código " + numero);
		if (estaFinalizado(proyecto))
			throw new IllegalArgumentException("El proyecto ya está finalizado.");

		Empleado empleado = empleados.get(legajo);

		if (empleado == null)
			throw new IllegalArgumentException("No existe ningún empleado con legajo " + legajo);
		if (empleado.estaAsignado())
			throw new IllegalArgumentException(
					"El empleado que se desea asignar ya se encontraba asignado a una tarea.");

		Empleado empleadoAnterior = proyecto.reasignarEmpleado(titulo, empleado);

		liberarEmpleado(empleadoAnterior);
	}

	private boolean hayEmpleadosDisponibles() {
		return !empleadosNoAsignados.isEmpty();
	}

	@Override
	public void reasignarEmpleadoConMenosRetraso(Integer numero, String titulo) throws Exception {
		if (!hayEmpleadosDisponibles())
			throw new IllegalArgumentException("No hay empleados disponibles.");

		Empleado empleado = obtenerEmpleadoConMenosRetrasos();
		int legajo = empleado.obtenerLegajo();

		reasignarEmpleadoEnProyecto(numero, legajo, titulo);
	}

	@Override
	public double costoProyecto(Integer numero) {
		Proyecto proyecto = proyectos.get(numero);

		if (proyecto == null)
			throw new IllegalArgumentException("No existe ningún proyecto con código " + numero);

		return proyecto.costoProyecto();
	}

//	@Override
//	public List<Tupla<Integer, String>> proyectosFinalizados() {
//		List<Proyecto> proyectos = new ArrayList<>(this.proyectos.values());
//		
//		List<Tupla<Integer, String>> proyectosFinalizados = new ArrayList<>();
//		
//		for (Proyecto proyecto : proyectos) {
//			if (estaFinalizado(proyecto)) {
//				int idProyecto = proyecto.obtenerId();
//				String domicilio = proyecto.obtenerDomicilio();
//				
//				Tupla<Integer, String> proyectoFinalizado = new Tupla<Integer, String>(idProyecto, domicilio);
//				
//				proyectosFinalizados.add(proyectoFinalizado);
//			}
//		}
//		
//		return proyectosFinalizados;
//	}
	@Override
	public List<Tupla<Integer, String>> proyectosFinalizados() {
		Iterator<Proyecto> it = this.proyectos.values().iterator();

		List<Tupla<Integer, String>> proyectosFinalizados = new ArrayList<>();

		while (it.hasNext()) {
			Proyecto proyecto = it.next();
			
			if (estaFinalizado(proyecto)) {
				int idProyecto = proyecto.obtenerId();
				String domicilio = proyecto.obtenerDomicilio();

				Tupla<Integer, String> proyectoFinalizado = new Tupla<Integer, String>(idProyecto, domicilio);

				proyectosFinalizados.add(proyectoFinalizado);
			}
		}

		return proyectosFinalizados;
	}

	@Override
	public List<Tupla<Integer, String>> proyectosPendientes() {
		Iterator<Proyecto> it = this.proyectos.values().iterator();

		List<Tupla<Integer, String>> proyectosPendientes = new ArrayList<>();

		while (it.hasNext()) {
			Proyecto proyecto = it.next();
			
			if (proyecto.estaPendiente()) {
				int idProyecto = proyecto.obtenerId();
				String domicilio = proyecto.obtenerDomicilio();

				Tupla<Integer, String> proyectoPendiente = new Tupla<Integer, String>(idProyecto, domicilio);

				proyectosPendientes.add(proyectoPendiente);
			}
		}

		return proyectosPendientes;
	}

	@Override
	public List<Tupla<Integer, String>> proyectosActivos() {
		Iterator<Proyecto> it = this.proyectos.values().iterator();

		List<Tupla<Integer, String>> proyectosActivos = new ArrayList<>();

		while (it.hasNext()) {
			Proyecto proyecto = it.next();
			
			if (proyecto.estaActivo()) {
				int idProyecto = proyecto.obtenerId();
				String domicilio = proyecto.obtenerDomicilio();

				Tupla<Integer, String> proyectoActivo = new Tupla<Integer, String>(idProyecto, domicilio);

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
			throw new IllegalArgumentException("No existe ningún proyecto con código " + numero);

		return proyecto.estaFinalizado();
	}

	public boolean estaFinalizado(Proyecto proyecto) {
		if (proyecto == null)
			throw new NullPointerException("El proyecto no existe.");

		return proyecto.estaFinalizado();
	}

	@Override
	public int consultarCantidadRetrasosEmpleado(Integer legajo) {
		Empleado empleado = empleados.get(legajo);

		if (empleado == null)
			throw new IllegalArgumentException("No existe ningún empleado con legajo " + legajo);

		return empleado.obtenerCantidadDeRetrasos();
	}

	@Override
	public List<Tupla<Integer, String>> empleadosAsignadosAProyecto(Integer numero) {
		Proyecto proyecto = proyectos.get(numero);

		if (proyecto == null)
			throw new IllegalArgumentException("No existe ningún proyecto con código " + numero);

		List<Tupla<Integer, String>> empleadosAsignados = proyecto.empleadosAsignados();

		return empleadosAsignados;
	}

	@Override
	public Object[] tareasProyectoNoAsignadas(Integer numero) {
		Proyecto proyecto = proyectos.get(numero);

		if (proyecto == null)
			throw new IllegalArgumentException("No existe ningún proyecto con código " + numero);

		Object[] tareasProyectoNoAsignadas = proyecto.tareasProyectoNoAsignadas();

		return tareasProyectoNoAsignadas;
	}

	@Override
	public Object[] tareasDeUnProyecto(Integer numero) {
		Proyecto proyecto = proyectos.get(numero);

		if (proyecto == null)
			throw new IllegalArgumentException("No existe ningún proyecto con código " + numero);

		Object[] tareasDeUnProyecto = proyecto.tareasDeUnProyecto();

		return tareasDeUnProyecto;
	}

	@Override
	public String consultarDomicilioProyecto(Integer numero) {
		Proyecto proyecto = proyectos.get(numero);

		if (proyecto == null)
			throw new IllegalArgumentException("No existe ningún proyecto con código " + numero);

		return proyecto.obtenerDomicilio();
	}

	@Override
	public boolean tieneRestrasos(Integer legajo) {
		int cantidadDeRetrasos = consultarCantidadRetrasosEmpleado(legajo);

		return (cantidadDeRetrasos > 0 ? true : false);
	}

	@Override
	public List<Tupla<Integer, String>> empleados() {
		List<Empleado> empleados = new ArrayList<>(this.empleados.values());

		List<Tupla<Integer, String>> datosEmpleados = new ArrayList<>();

		for (Empleado empleado : empleados) {
			int legajo = empleado.obtenerLegajo();
			String nombre = empleado.obtenerNombre();

			Tupla<Integer, String> datosEmpleado = new Tupla<>(legajo, nombre);

			datosEmpleados.add(datosEmpleado);
		}

		return datosEmpleados;
	}

	@Override
	public String consultarProyecto(Integer numero) {
		Proyecto proyecto = proyectos.get(numero);

		if (proyecto == null)
			throw new IllegalArgumentException("No existe ningún proyecto con código " + numero);

		return proyecto.toString();
	}

	@Override
	public String toString() {
		StringBuilder infoEmpresa = new StringBuilder();

		infoEmpresa.append("Empresa de servicios de mantenimiento 'HomeSolution'.\n");
//		infoEmpresa.append("Nuestros especialistas son: \n"); // Decidimos no imprimir estos, pues en los tests se espera que el toString de Empleado devuelva sólo su legajo y no tiene sentido mostrar acá legajos sin nombres.
//		infoEmpresa.append(armarLineasDeEmpleados());
		infoEmpresa.append("Proyectos: \n");
		infoEmpresa.append(armarLineasDeProyectos());

		String homeSolution = infoEmpresa.toString();

		return homeSolution;
	}

	private String armarLineasDeProyectos() {
		StringBuilder infoProyectos = new StringBuilder();
		ArrayList<Proyecto> proyectos = new ArrayList<>(this.proyectos.values());

		for (Proyecto proyecto : proyectos) {
			infoProyectos.append("	");
			infoProyectos.append(proyecto);
			infoProyectos.append("\n");
		}

		String lineasDeProyectos = infoProyectos.toString();

		return lineasDeProyectos;
	}

//	private String armarLineasDeEmpleados() {
//		StringBuilder infoEmpleados = new StringBuilder();
//		ArrayList<Empleado> empleados = new ArrayList<>(this.empleados.values());
//
//		for (Empleado empleado : empleados) {
//			infoEmpleados.append("	");
//			infoEmpleados.append(empleado);
//			infoEmpleados.append("\n");
//		}
//
//		String lineasDeEmpleados = infoEmpleados.toString();
//
//		return lineasDeEmpleados;
//	}
}
