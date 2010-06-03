package aima.gui.applications;

import aima.gui.applications.search.map.RouteFindingAgentApp;
import aima.gui.applications.vacuum.VacuumApp;
import aima.gui.demo.learning.LearningDemo;
import aima.gui.demo.logic.DPLLDemo;
import aima.gui.demo.logic.FolDemo;
import aima.gui.demo.logic.PLFCEntailsDemo;
import aima.gui.demo.logic.PLResolutionDemo;
import aima.gui.demo.logic.TTEntailsDemo;
import aima.gui.demo.logic.WalkSatDemo;
import aima.gui.demo.probability.ProbabilityDemo;
import aima.gui.demo.search.CSPDemo;
import aima.gui.demo.search.EightPuzzleDemo;
import aima.gui.demo.search.NQueensDemo;
import aima.gui.demo.search.TicTacToeDemo;

/**
 * The all-in-one demo application. Shows everything within one frame.
 * @author R. Lunde
 */
public class AimaDemoApp {

	/** Registers agent applications and console program demos. */
	public static void registerDemos(AimaDemoFrame frame) {
		frame.addApp(VacuumApp.class);
		frame.addApp(RouteFindingAgentApp.class);
		
		frame.addDemo(EightPuzzleDemo.class);
		frame.addDemo(TicTacToeDemo.class);
		frame.addDemo(NQueensDemo.class);
		frame.addDemo(CSPDemo.class);
		
		frame.addDemo(TTEntailsDemo.class);
		frame.addDemo(PLFCEntailsDemo.class);
		frame.addDemo(PLResolutionDemo.class);
		frame.addDemo(DPLLDemo.class);
		frame.addDemo(WalkSatDemo.class);
		frame.addDemo(FolDemo.class);
		
		frame.addDemo(ProbabilityDemo.class);
		
		frame.addDemo(LearningDemo.class);
	}
	
	/** Starts the demo. */
	public static void main(String[] args) {
		AimaDemoFrame frame = new AimaDemoFrame();
		registerDemos(frame);
		frame.setSize(1000, 600);
		frame.setVisible(true);
	}
}
