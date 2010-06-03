package aima.test.core.unit.search.uninformed;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import aima.core.agent.Action;
import aima.core.environment.nqueens.NQueensBoard;
import aima.core.environment.nqueens.NQueensFunctionFactory;
import aima.core.environment.nqueens.NQueensGoalTest;
import aima.core.search.framework.Problem;
import aima.core.search.framework.Search;
import aima.core.search.framework.SearchAgent;
import aima.core.search.framework.TreeSearch;
import aima.core.search.uninformed.BreadthFirstSearch;

public class BreadthFirstSearchTest {

	@Test
	public void testBreadthFirstSuccesfulSearch() throws Exception {
		Problem problem = new Problem(new NQueensBoard(8),
				NQueensFunctionFactory.getActionsFunction(),
				NQueensFunctionFactory.getResultFunction(),
				new NQueensGoalTest());
		Search search = new BreadthFirstSearch(new TreeSearch());
		SearchAgent agent = new SearchAgent(problem, search);
		List<Action> actions = agent.getActions();
		assertCorrectPlacement(actions);
		Assert.assertEquals("1665", agent.getInstrumentation().getProperty(
				"nodesExpanded"));

		problem = new Problem(new NQueensBoard(3), NQueensFunctionFactory
				.getActionsFunction(), NQueensFunctionFactory
				.getResultFunction(), new NQueensGoalTest());
		agent = new SearchAgent(problem, search);
		actions = agent.getActions();
		Assert.assertEquals(0, actions.size());
		Assert.assertEquals("6", agent.getInstrumentation().getProperty(
				"nodesExpanded"));
	}

	@Test
	public void testBreadthFirstUnSuccesfulSearch() throws Exception {
		Problem problem = new Problem(new NQueensBoard(3),
				NQueensFunctionFactory.getActionsFunction(),
				NQueensFunctionFactory.getResultFunction(),
				new NQueensGoalTest());
		Search search = new BreadthFirstSearch(new TreeSearch());
		SearchAgent agent = new SearchAgent(problem, search);
		List<Action> actions = agent.getActions();
		Assert.assertEquals(0, actions.size());
		Assert.assertEquals("6", agent.getInstrumentation().getProperty(
				"nodesExpanded"));
	}

	//
	// PRIVATE METHODS
	//
	private void assertCorrectPlacement(List<Action> actions) {
		Assert.assertEquals(8, actions.size());
		Assert.assertEquals("Action[name==placeQueenAt, x==0, y==0]", actions
				.get(0).toString());
		Assert.assertEquals("Action[name==placeQueenAt, x==1, y==4]", actions
				.get(1).toString());
		Assert.assertEquals("Action[name==placeQueenAt, x==2, y==7]", actions
				.get(2).toString());
		Assert.assertEquals("Action[name==placeQueenAt, x==3, y==5]", actions
				.get(3).toString());
		Assert.assertEquals("Action[name==placeQueenAt, x==4, y==2]", actions
				.get(4).toString());
		Assert.assertEquals("Action[name==placeQueenAt, x==5, y==6]", actions
				.get(5).toString());
		Assert.assertEquals("Action[name==placeQueenAt, x==6, y==1]", actions
				.get(6).toString());
		Assert.assertEquals("Action[name==placeQueenAt, x==7, y==3]", actions
				.get(7).toString());
	}
}
