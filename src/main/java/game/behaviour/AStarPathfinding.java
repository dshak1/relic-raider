package game.behaviour;

import game.map.Map;
import game.map.Position;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Implementation of the A* pathfinding algorithm for finding the shortest path
 * from one position to another on a game map.
 * 
 * <p>This algorithm uses a heuristic function (Manhattan distance) to efficiently
 * explore the most promising paths first, making it optimal for grid-based maps.</p>
 */
public class AStarPathfinding implements PathfindingStrategy {
    
    /**
     * Represents a node in the A* search tree.
     * Contains position, cost to reach, estimated cost to goal, and path information.
     */
    private static class Node implements Comparable<Node> {
        private final Position position;
        private final int gCost;  // Cost from start to this node
        private final int hCost;  // Estimated cost from this node to goal (heuristic)
        private final int fCost;  // Total cost (g + h)
        private final Node parent;
        
        /**
         * Constructs a new node.
         * 
         * @param position the position of this node
         * @param gCost the actual cost from start to this node
         * @param hCost the estimated cost from this node to the goal
         * @param parent the parent node in the path tree
         */
        public Node(Position position, int gCost, int hCost, Node parent) {
            this.position = position;
            this.gCost = gCost;
            this.hCost = hCost;
            this.fCost = gCost + hCost;
            this.parent = parent;
        }
        
        @Override
        public int compareTo(Node other) {
            // Compare by fCost (total estimated cost)
            // If fCosts are equal, prioritize lower hCost (closer to goal)
            if (this.fCost != other.fCost) {
                return Integer.compare(this.fCost, other.fCost);
            }
            return Integer.compare(this.hCost, other.hCost);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return position.equals(node.position);
        }
        
        @Override
        public int hashCode() {
            return position.hashCode();
        }
    }
    
    /**
     * Finds the shortest path from start to target using the A* algorithm.
     * 
     * @param map the game map to navigate
     * @param start the starting position
     * @param target the target position
     * @return a list of positions representing the path from start to target,
     *         or null if no path exists
     */
    @Override
    public List<Position> findPath(Map map, Position start, Position target) {
        // Validate inputs
        if (map == null || start == null || target == null) {
            return null;
        }
        
        // Check if start and target are valid positions
        if (!map.inBounds(start) || !map.inBounds(target)) {
            return null;
        }
        
        // Check if start and target are passable
        if (!map.isPassable(start) || !map.isPassable(target)) {
            return null;
        }
        
        // If start equals target, return a path with just that position
        if (start.equals(target)) {
            List<Position> path = new ArrayList<>();
            path.add(start);
            return path;
        }
        
        // Initialize open set (nodes to explore) and closed set (explored nodes)
        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<Position> closedSet = new HashSet<>();
        Set<Position> openSetPositions = new HashSet<>();
        
        // Add start node to open set
        Node startNode = new Node(start, 0, heuristic(start, target), null);
        openSet.add(startNode);
        openSetPositions.add(start);
        
        // A* main loop
        while (!openSet.isEmpty()) {
            // Get node with lowest fCost
            Node current = openSet.poll();
            openSetPositions.remove(current.position);
            
            // Add to closed set
            closedSet.add(current.position);
            
            // If we reached the target, reconstruct and return the path
            if (current.position.equals(target)) {
                return reconstructPath(current);
            }
            
            // Explore neighbors
            for (Position neighbor : getNeighbors(current.position)) {
                // Skip if already explored or not passable
                if (closedSet.contains(neighbor) || !map.isPassable(neighbor)) {
                    continue;
                }
                
                // Calculate costs
                int tentativeGCost = current.gCost + 1; // Each step costs 1
                int hCost = heuristic(neighbor, target);
                
                // Check if this is a better path
                boolean foundInOpen = openSetPositions.contains(neighbor);
                if (!foundInOpen || tentativeGCost < getGCostFromOpenSet(openSet, neighbor)) {
                    // Create new node with better path
                    Node neighborNode = new Node(neighbor, tentativeGCost, hCost, current);
                    
                    if (!foundInOpen) {
                        openSet.add(neighborNode);
                        openSetPositions.add(neighbor);
                    } else {
                        // Update existing node in open set
                        openSet.remove(neighborNode);
                        openSet.add(neighborNode);
                    }
                }
            }
        }
        
        // No path found
        return null;
    }
    
    /**
     * Reconstructs the path from the goal node back to the start.
     * 
     * @param goal the goal node
     * @return a list of positions representing the path
     */
    private List<Position> reconstructPath(Node goal) {
        List<Position> path = new ArrayList<>();
        Node current = goal;
        
        while (current != null) {
            path.add(current.position);
            current = current.parent;
        }
        
        // Reverse to get path from start to goal
        Collections.reverse(path);
        return path;
    }
    
    /**
     * Gets the gCost of a node if it exists in the open set.
     * 
     * @param openSet the priority queue of open nodes
     * @param position the position to find
     * @return the gCost, or Integer.MAX_VALUE if not found
     */
    private int getGCostFromOpenSet(PriorityQueue<Node> openSet, Position position) {
        for (Node node : openSet) {
            if (node.position.equals(position)) {
                return node.gCost;
            }
        }
        return Integer.MAX_VALUE;
    }
    
    /**
     * Gets the neighboring positions (up, down, left, right) of a given position.
     * 
     * @param position the current position
     * @return a list of neighboring positions
     */
    private List<Position> getNeighbors(Position position) {
        List<Position> neighbors = new ArrayList<>();
        int row = position.getRow();
        int col = position.getCol();
        
        // Add four cardinal direction neighbors
        neighbors.add(new Position(row - 1, col));  // UP
        neighbors.add(new Position(row + 1, col));  // DOWN
        neighbors.add(new Position(row, col - 1));  // LEFT
        neighbors.add(new Position(row, col + 1));  // RIGHT
        
        return neighbors;
    }
    
    /**
     * Calculates the heuristic (estimated distance) from a position to the target.
     * Uses Manhattan distance (sum of absolute differences in rows and columns).
     * 
     * <p>This heuristic is admissible (never overestimates) for grid-based maps
     * where movement is restricted to cardinal directions.</p>
     * 
     * @param from the starting position
     * @param to the target position
     * @return the estimated distance
     */
    private int heuristic(Position from, Position to) {
        return Math.abs(from.getRow() - to.getRow()) + 
               Math.abs(from.getCol() - to.getCol());
    }
}
