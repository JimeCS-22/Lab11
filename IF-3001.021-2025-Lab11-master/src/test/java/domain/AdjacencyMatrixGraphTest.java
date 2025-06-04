package domain;

import domain.list.ListException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdjacencyMatrixGraphTest {

    @Test
    void test1() {
        try{
        AdjacencyMatrixGraph graph = new AdjacencyMatrixGraph(50);
        for (int i = 0; i < 20; i++) {
            graph.addVertex(util.Utility.random(50)+1);

        }
        System.out.println(graph);
    }catch (GraphException | ListException e){
            throw new RuntimeException(e);

        }
    }
}