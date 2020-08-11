# coin-mining



<!-- PROJECT LOGO -->
<br />
<p align="center">
    <a href="https://github.com/howuvebeen/coin-mining"><img src="gifs/preview.gif" alt="preview"></a>
</p>

<a href="https://github.com/howuvebeen/coin-mining"><h2 align="center">Coin Mining</h2></a>

<p align="center">
<strong>Applied Dijkstra’s algorithm to coin mining game using Java. Utilized HashMap and LinkedList to program the character to collect most coins by priority within given steps.</strong>
<br />
<br />
<code>Java</code>
·
<code>Hashmap</code>
·
<code>Linkedlist</code>
·
<code>DFS</code>
·
<code>BFS</code>
·
<code>Dijkstra's Algorithm</code>
</p>


<!-- TABLE OF CONTENTS -->
# Table of Contents

* [About the Project](#about-the-project)
  * [Built With](#built-with)
  * [Two Phases](#two-phases)
  * [Scoring](#scoring)
* [Getting Started](#getting-started)
  * [Prerequisites](#prerequisites)
  * [Creating the Project](#creating-the-project)
  * [Running the Program](#running-the-program)
* [Code](#code)
  * [Find Phase](#find-phase)
  * [Flee Phase](#flee-phase)
* [License](#license)


<!-- ABOUT THE PROJECT -->
# About The Project

Someone dropped a famous, expensive ring. With a special glasses, Diver Min is going into the swer system to find the ring. After he found the ring, he starts fleeing -running to the exit. Then, he sees coins all over the sewer system, and while fleeing, Min picks up as many coins as possible. 

This is the final project I've done for **CS 2100**, Object-Oriented Programming and Data Structure. I received 96.1 out of 100. 

## Built With

* Java

## Two Phases

* The Find Phase:

  On the way to the ring, the layout of the sewer system is unknown. **Min knows only the status of the place on which Min is standing and the immediately neighboring ones**. The goal is to make it to the ring in as few steps as possible. However, Min can see the ring shining in the distance. **The closer he gets, the stronger the light from the ring**.

* The Flee Phase:

  Luckily, underneath the ring is a map that reveals the full sewer system. Min rushes around, picking up as many coins as possible. But because the sewer system is a rather unhealthy environment, Min must flee to the exit within a prescribed number of steps. **The goal is to get back to the exit within a prescribed number of steps with the best score**. 

## Scoring

Min's score will be the product of these two quantities:
  - the value of the coins that Min picks up during the flee phase
  - the score multiplier from the find-the-ring phase


<!-- GETTING STARTED -->
# Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.


## Prerequisites

* Eclipse
* JDK

## Creating the Project

1. Click green Code button and download the zip file from github. Do not unzip the file!
2. Use menu item File, Open Project From File System.
3. In the window that opens: select "archive", navigate to the downloaded zip file, select it, and click Open.

## Running the Program

Run GUI.java to see an accompanying display.
By default, each of these runs a single map on a random seed. You can customize the map in two ways.

*  ```-n <count>``` : runs the program count times.
*  ```-s <seed>``` : runs the program with a predefined seed.

To set these arguments, in Eclipse click Run, Configurations, click on tab Arguments, and enter the arguments under Program Arguments.


<!-- CODE -->
# Code

These are codes of the key algorithms I built. To see more, refer to **DiverMin.java**, **Heap.java**, and **Paths.java**.


## Find Phase

```
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
```

## Flee Phase

```
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
```


<!-- LICENSE -->
# License

- Yubin Heo
- cs2110-2019sp
