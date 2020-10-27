package socialnetwork.Algorithm;

import java.util.*;

public class Graph {

    private int V;
    private int numbersConnectedComponents=0;
    LinkedList<Integer>[] adjListArray;
    Map<Integer,ArrayList<Integer>> componenteConexe;

    // constructor
    public Graph(int V) {
        this.V = V;
        adjListArray = new LinkedList[V];
        for (int i = 0; i < V; i++) {
            adjListArray[i] = new LinkedList<Integer>();
        }
        componenteConexe = new HashMap<>();
    }
    public Iterable<ArrayList<Integer>> getAllConexComponents() {
        return componenteConexe.values();
    }

    public void addEdge(int src, int dest) {
        adjListArray[src].add(dest);
        adjListArray[dest].add(src);
    }

    public void DFSUtil(int v, boolean[] visited) {

        visited[v] = true;
        componenteConexe.get(this.numbersConnectedComponents).add(v);
        for (int x : adjListArray[v]) {
            if (!visited[x]) DFSUtil(x, visited);
        }

    }

    public int connectedComponents() {
        // Mark all the vertices as not visited
        boolean[] visited = new boolean[V];
        for (int v = 0; v < V; ++v) {
            if (!visited[v]) {
                numbersConnectedComponents++;
                componenteConexe.put(numbersConnectedComponents,new ArrayList<>());
                DFSUtil(v, visited);
            }
        }
        return numbersConnectedComponents;
    }
}

