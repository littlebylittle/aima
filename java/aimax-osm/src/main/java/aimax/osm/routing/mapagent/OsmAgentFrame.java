package aimax.osm.routing.mapagent;

import aima.gui.applications.search.SearchFactory;
import aima.gui.applications.search.map.MapAgentFrame;

/** Simple frame for running map agents in maps defined by OSM data. */
public class OsmAgentFrame extends MapAgentFrame {

	/** Creates a new frame. */
	public OsmAgentFrame(OsmMapAdapter map) {
		setTitle("OMAS - the Osm Map Agent Simulator");
		setSelectors(new String[]{
				SCENARIO_SEL, AGENT_SEL,
				SEARCH_SEL, SEARCH_MODE_SEL, HEURISTIC_SEL},
				new String[]{
				"Select Scenario", "Select Agent",
				"Select Search Strategy", "Select Search Mode", "Select Heuristic"}
		);
		setSelectorItems(SCENARIO_SEL,
				new String[] {"Use any way", "Travel by car", "Travel by bicycle"}, 0);
		setSelectorItems(AGENT_SEL,
				new String[] {"Offline Search", "Online Search (LRTA*)"}, 0);
		setSelectorItems(SEARCH_SEL,
				SearchFactory.getInstance().getSearchStrategyNames(), 5);
		setSelectorItems(SEARCH_MODE_SEL, SearchFactory.getInstance()
				.getSearchModeNames(), 1); // change the default!
		setSelectorItems(HEURISTIC_SEL, new String[] { "H1 (=0)",
				"H2 (sld to goal)" }, 1);
	}
}
