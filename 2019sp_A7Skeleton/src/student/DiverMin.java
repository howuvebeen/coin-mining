package student;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import game.FindState;
import game.FleeState;
import game.Node;
import game.NodeStatus;
import game.SewerDiver;

public class DiverMin extends SewerDiver {
	HashSet<Long> set= new HashSet<>();

	/** Get to the ring in as few steps as possible. Once you get there, <br>
	 * you must return from this function in order to pick<br>
	 * it up. If you continue to move after finding the ring rather <br>
	 * than returning, it will not count.<br>
	 * If you return from this function while not standing on top of the ring, <br>
	 * it will count as a failure.
	 *
	 * There is no limit to how many steps you can take, but you will receive<br>
	 * a score bonus multiplier for finding the ring in fewer steps.
	 *
	 * At every step, you know only your pnode tile's ID and the ID of all<br>
	 * open neighbor tiles, as well as the distance to the ring at each of <br>
	 * these tiles (ignoring walls and obstacles).
	 *
	 * In order to get information about the pnode state, use functions<br>
	 * pnodeLocation(), neighbors(), and distanceToRing() in state.<br>
	 * You know you are standing on the ring when distanceToRing() is 0.
	 *
	 * Use function moveTo(long id) in state to move to a neighboring<br>
	 * tile by its ID. Doing this will change state to reflect your new position.
	 *
	 * A suggested first implementation that will always find the ring, but <br>
	 * likely won't receive a large bonus multiplier, is a depth-first walk. <br>
	 * Some modification is necessary to make the search better, in general. */
	@Override
	public void find(FindState state) {
		// TODO : Find the ring and return.
		// DO NOT WRITE ALL THE CODE HERE. DO NOT MAKE THIS METHOD RECURSIVE.
		// Instead, write your method elsewhere, with a good specification,
		// and call it from this one.
		dfsWalk(state);
	}

	/** The DiverMin is standing on a location given by FindState state. Visit every neighbors
	 * (priority on shortest distance to the ring) reachable along paths of unvisited neighbors from
	 * location. End with walker standing on location. Precondition: location is unvisited. */
	public void dfsWalk(FindState state) {
		// Helper Function for method find();
		// This function uses dfs to find the ring in the maze with minimum steps.
		long location= state.currentLocation();

		if (state.distanceToRing() == 0) return;
		set.add(location);

		Collection<NodeStatus> neighbors= state.neighbors();
		Heap<NodeStatus> heap= new Heap<>(true);
		for (NodeStatus n : neighbors) {
			int distance= n.getDistanceToTarget();
			heap.add(n, distance);
		}

		while (heap.size() != 0) {
			NodeStatus minn= heap.poll();
			if (set.contains(minn.getId()) == false) {
				if (state.distanceToRing() != 0) state.moveTo(minn.getId());
				dfsWalk(state);
				if (state.distanceToRing() != 0) state.moveTo(location);
			}
		}
	}

	/** Flee the sewer system before the steps are all used, trying to <br>
	 * collect as many coins as possible along the way. Your solution must ALWAYS <br>
	 * get out before the steps are all used, and this should be prioritized above<br>
	 * collecting coins.
	 *
	 * You now have access to the entire underlying graph, which can be accessed<br>
	 * through FleeState. pnodeNode() and getExit() will return Node objects<br>
	 * of interest, and getNodes() will return a collection of all nodes on the graph.
	 *
	 * You have to get out of the sewer system in the number of steps given by<br>
	 * getStepsRemaining(); for each move along an edge, this number is <br>
	 * decremented by the weight of the edge taken.
	 *
	 * Use moveTo(n) to move to a node n that is adjacent to the pnode node.<br>
	 * When n is moved-to, coins on node n are automatically picked up.
	 *
	 * You must return from this function while standing at the exit. Failing <br>
	 * to do so before steps run out or returning from the wrong node will be<br>
	 * considered a failed run.
	 *
	 * Initially, there are enough steps to get from the starting point to the<br>
	 * exit using the shortest path, although this will not collect many coins.<br>
	 * For this reason, a good starting solution is to use the shortest path to<br>
	 * the exit. */
	@Override
	public void flee(FleeState state) {
		// TODO: Get out of the sewer system before the steps are used up.
		// DO NOT WRITE ALL THE CODE HERE. Instead, write your method elsewhere,
		// with a good specification, and call it from this one.
		coinCollector(state);
	}

	/** Using Dijkstra's shortest algorithm, collect as many coins as possible with reasonably
	 * highest weights along the way back from the location given by FleeState state to the exit. */
	public void coinCollector(FleeState state) {
		// // Helper Function for method flee();
		// This function uses Dijkstra's shortest algorithm to score highest point.
		Node location= state.currentNode();
		Node end= state.getExit();

		Heap<Node> F= new Heap<>(false);
		Collection<Node> allnode= state.allNodes();
		for (Node n : allnode) {
			int coins= n.getTile().coins();
			if (coins > 0)
				F.add(n, coins);
		}

		List<Node> pathcoins= new LinkedList<>();
		int stepsleft= state.stepsLeft();
		int steps= 0;
		Node nc;
		Node prevnc= null;
		HashSet<Node> visit= new HashSet<>();
		while (F.size() != 0) {
			nc= F.poll();
			if (location != nc && !visit.contains(nc)) {
				List<Node> path1= Paths.shortest(prevnc == null ? location : prevnc, nc);
				List<Node> path2= Paths.shortest(nc, end);
				int dist1= Paths.pathSum(path1);
				int dist2= Paths.pathSum(path2);

				if (steps + dist1 + dist2 < stepsleft) {
					pathcoins.add(nc);
					steps+= dist1;
					prevnc= nc;
					for (Node n : path1) {
						visit.add(n);
					}
				}

			}
		}

		if (!pathcoins.isEmpty()) {
			pathcoins.add(end);
			prevnc= null;
			for (Node n2 : pathcoins) {
				List<Node> path= Paths.shortest(prevnc == null ? location : prevnc, n2);
				for (Node n : path) {
					if (location != n) {
						state.moveTo(n);
					}
				}
				prevnc= n2;
				location= state.currentNode();
			}
		} else {
			List<Node> path= Paths.shortest(location, end);
			for (Node n : path) {
				if (location != n) {
					state.moveTo(n);
				}
			}
		}
	}
}
