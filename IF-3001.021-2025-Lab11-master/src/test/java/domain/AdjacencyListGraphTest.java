package domain;

import domain.list.ListException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdjacencyListGraphTest {

    @Test
    void test(){

        try {
            //Instanciar la clase
            AdjacencyListGraph graph = new AdjacencyListGraph(50);

            //Agregar 30 vertices
            for (int i = 0; i<30; i++){
                graph.addVertex(util.Utility.random(30));
            }

            //Conectar las aristas

            //Mostrar el contenido

            //Conectar en forma aleatoria

            //Recorridos

            //Suprimir 5 elementos

            //Mostrar el contenido

        }catch (GraphException | ListException e){
            throw new RuntimeException(e);
        }





    }

}