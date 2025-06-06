package domain;

import domain.list.ListException;
import domain.queue.QueueException;
import domain.stack.StackException;
import org.junit.jupiter.api.Test;

// Assuming all necessary classes are correctly implemented and available in these packages:
// domain.SinglyLinkedListGraph (the class under test)
// domain.GraphException, domain.list.ListException, domain.queue.QueueException, domain.stack.StackException
// util.Utility (though random() is no longer directly used for weights, it might be used elsewhere)

class SinglyLinkedListGraphTest {

    @Test
    void test() { // Method name 'test' as in the example

        try {

            SinglyLinkedListGraph graph = new SinglyLinkedListGraph();

            // Add vertices 'a' through 'j'
            for (char i = 'a'; i <= 'j'; i++) {
                graph.addVertex(i);
            }

            // Add edges with person names as weights
            graph.addEdgeWeight('a', 'b', "Alice");
            graph.addEdgeWeight('a', 'c', "Bob");
            graph.addEdgeWeight('a', 'd', "Charlie");
            graph.addEdgeWeight('b', 'f', "David");
            graph.addEdgeWeight('f', 'e', "Eve");
            graph.addEdgeWeight('f', 'j', "Frank");
            graph.addEdgeWeight('c', 'g', "Grace");
            graph.addEdgeWeight('g', 'j', "Heidi");
            graph.addEdgeWeight('d', 'h', "Ivan");
            graph.addEdgeWeight('h', 'i', "Judy");
            graph.addEdgeWeight('h', 'j', "Kevin");

            System.out.println("\nPrimer llamado de graph");
            System.out.println(graph);

            System.out.println("\nPrimer llamado de DFS y BFS");
            System.out.println("DFS Transversal Tour: " + graph.dfs());
            System.out.println("BFS Transversal Tour: " + graph.bfs());

            // Remove specified vertices
            System.out.println("\nVertex deleted: f");
            graph.removeVertex('f');
            System.out.println("\nVertex deleted: h");
            graph.removeVertex('h');
            System.out.println("\nVertex deleted: j");
            graph.removeVertex('j');
            System.out.println(graph);

            System.out.println("\nSegundo llamado de DFS y BFS");
            System.out.println("DFS Transversal Tour (after removal): " + graph.dfs());
            System.out.println("BFS Transversal Tour (after removal): " + graph.bfs());

            System.out.println("\nSegundo llamado de graph");
            System.out.println("\n"+graph);


        } catch (GraphException | ListException | StackException | QueueException e) {
            // Re-throw as RuntimeException, matching the example's error handling
            throw new RuntimeException(e);
        }
    }
}