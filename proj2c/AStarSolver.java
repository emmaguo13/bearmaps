package bearmaps.proj2c;

import bearmaps.proj2ab.DoubleMapPQ;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {
    private AStarGraph<Vertex> input;
    private Vertex start;
    private Vertex end;
    private DoubleMapPQ<Vertex> fringe;
    private HashMap<Vertex, Double> distTo;
    private HashMap<Vertex, Vertex> edgeTo;
    private ArrayList<Vertex> solution;
    private boolean solved;
    private boolean timedout;
    private int numStatesExplored;
    private double elapsedTime;

    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        Stopwatch stopwatch = new Stopwatch();
        this.input = input;
        this.start = start;
        this.end = end;
        this.distTo = new HashMap<Vertex, Double>();
        this.edgeTo = new HashMap<Vertex, Vertex>();
        this.solution = new ArrayList<Vertex>();
        this.solved = false;
        this.timedout = false;
        this.numStatesExplored = 0;
        this.elapsedTime = 0;

        fringe = new DoubleMapPQ<Vertex>();
        fringe.add(start, input.estimatedDistanceToGoal(start, end));
        distTo.put(start, 0.0);
        edgeTo.put(start, null);

        while (fringe.size() > 0) {
            Vertex curr = this.fringe.removeSmallest();
            this.numStatesExplored++;
            if (curr.equals(end)) {
                this.elapsedTime = stopwatch.elapsedTime();
                System.out.println("solved");
                solutionBuilder(curr);
                Collections.reverse(solution);
                this.solved = true;
                break;
            } else if (stopwatch.elapsedTime() / 1000 > timeout) {
                //System.out.println("timeout");
                this.timedout = true;
                break;
            }
            for (WeightedEdge<Vertex> x : input.neighbors(curr)) {
                if (!x.to().equals(curr)) {
                    relax(x);
                }
            }
        }
    }

    private void solutionBuilder(Vertex current) {
        while (current != null) {
            solution.add(current);
            current = edgeTo.get(current);
            System.out.println(current);
        }
    }

    private void relax(WeightedEdge<Vertex> e) {
        Vertex p = e.from();
        Vertex q = e.to();
        double w = e.weight();

        if (!distTo.containsKey(q)) {
            distTo.put(q, distTo.get(p) + w);
            edgeTo.put(q, p);
            fringe.add(q, distTo.get(q) + input.estimatedDistanceToGoal(q, end));
        }
        if (distTo.get(p) + w < distTo.get(q)) {
            distTo.put(q, distTo.get(p) + w);
            edgeTo.put(q, p);
            if (fringe.contains(q)) {
                fringe.changePriority(q, distTo.get(q) + input.estimatedDistanceToGoal(q, end));
            } else {
                fringe.add(q, distTo.get(q) + input.estimatedDistanceToGoal(q, end));
            }
        }

    }

    @Override
    public SolverOutcome outcome() {
        if (this.solved) {
            return SolverOutcome.SOLVED;
        } else if (this.timedout) {
            return SolverOutcome.TIMEOUT;
        } else {
            return SolverOutcome.UNSOLVABLE;
        }
    }

    @Override
    public List<Vertex> solution() {
        if (this.solved) {
            return this.solution;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public double solutionWeight() {
        return distTo.get(end);
    }

    @Override
    public int numStatesExplored() {
        return this.numStatesExplored;
    }

    @Override
    public double explorationTime() {
        return this.elapsedTime;
    }
}
