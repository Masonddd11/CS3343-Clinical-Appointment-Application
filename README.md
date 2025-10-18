Clinical Appointment Console Application

This is a minimal console-based MVC Java application demonstrating a clinical appointment flow and a pathfinding abstraction.

How to build and run (Windows cmd.exe):

1) Compile:

javac -d bin -sourcepath src src\clinicalappointment\ClinicalAppointmentApplication.java src\clinicalappointment\**\*.java

2) Run:

java -cp bin clinicalappointment.ClinicalAppointmentApplication

Notes:
- The pathfinding interface is in `clinicalappointment.pathfinding.PathFinder`.
- `DStarLitePathFinder` currently delegates to Dijkstra as a placeholder. Replace it with a full D* Lite implementation when needed.
package clinicalappointment.controller;

import clinicalappointment.model.Appointment;
import clinicalappointment.model.Hospital;
import clinicalappointment.model.MapGraph;
import clinicalappointment.model.Patient;
import clinicalappointment.pathfinding.PathFinder;
import clinicalappointment.service.AppointmentService;
import clinicalappointment.service.HospitalService;
import clinicalappointment.view.ConsoleView;

import java.util.List;

public class AppointmentController {
	private final ConsoleView view;
	private final HospitalService hospitalService;
	private final AppointmentService appointmentService;
	private final PathFinder pathFinder;
	private final MapGraph map;

	public AppointmentController(ConsoleView view, HospitalService hospitalService, AppointmentService appointmentService, PathFinder pathFinder, MapGraph map) {
		this.view = view;
		this.hospitalService = hospitalService;
		this.appointmentService = appointmentService;
		this.pathFinder = pathFinder;
		this.map = map;
	}

	public void run() {
		view.welcome();
		boolean exit = false;
		while (!exit) {
			int choice = view.mainMenu();
			switch (choice) {
				case 1:
					findNearestHospitalFlow();
					break;
				case 2:
					bookAppointmentFlow();
					break;
				case 3:
					view.showAppointments(appointmentService.listAppointments());
					break;
				case 0:
					exit = true;
					break;
				default:
					view.showMessage("Unknown option");
			}
		}
		view.showMessage("Goodbye!");
	}

	private void findNearestHospitalFlow() {
		Patient p = view.readPatientInfo();
		int startNode = map.findNearestNode(p.getX(), p.getY());
		Hospital nearest = hospitalService.findNearestHospital(startNode, pathFinder, map);
		if (nearest == null) {
			view.showMessage("No hospitals available.");
			return;
		}
		List<Integer> path = pathFinder.findPath(map, startNode, nearest.getNodeId());
		double cost = pathFinder.getPathCost(map, path);
		view.showNearestHospital(nearest, path, cost, map);
	}

	private void bookAppointmentFlow() {
		Patient p = view.readPatientInfo();
		int startNode = map.findNearestNode(p.getX(), p.getY());
		Hospital nearest = hospitalService.findNearestHospital(startNode, pathFinder, map);
		if (nearest == null) {
			view.showMessage("No hospitals available to book.");
			return;
		}
		Appointment appt = appointmentService.bookAppointment(p, nearest);
		view.showMessage("Appointment booked: " + appt);
	}
}

