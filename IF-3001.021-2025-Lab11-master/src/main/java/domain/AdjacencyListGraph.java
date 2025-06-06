package domain;

import domain.list.ListException;
import domain.queue.LinkedQueue;
import domain.queue.QueueException;
import domain.stack.LinkedStack;
import domain.stack.StackException;

public class AdjacencyListGraph implements Graph {
    public Vertex[] vertexList; //arreglo de objetos tupo vértice
    private int n; //max de elementos
    private int counter; //contador de vertices

    //para los recorridos dfs, bfs
    private LinkedStack stack;
    private LinkedQueue queue;

    //Constructor
    public AdjacencyListGraph(int n) {
        if (n <= 0) System.exit(1); //sale con status==1 (error)
        this.n = n;
        this.counter = 0;
        this.vertexList = new Vertex[n];
        this.stack = new LinkedStack();
        this.queue = new LinkedQueue();

    }

    @Override
    public int size() throws ListException {
        return counter;
    }

    @Override
    public void clear() {
        this.vertexList = new Vertex[n];
        this.counter = 0; //inicializo contador de vértices

    }

    @Override
    public boolean isEmpty() {
        return counter == 0;
    }

    @Override
    public boolean containsVertex(Object element) throws GraphException, ListException {
        if (isEmpty()) throw new GraphException("Adjacency List Graph in Empty");

        //Opcion1
//        for (int i = 0; i < counter; i++) {
//            if (util.Utility.compare(vertexList[i].data, element) == 0) return true;
//        }
        // return false;

        //Opcion2
        return indexOf(element) != -1;

    }

    @Override
    public boolean containsEdge(Object a, Object b) throws GraphException, ListException {

        if (isEmpty()) throw new GraphException("Adjacency List Graph in Empty");

        return !vertexList[indexOf(a)].edgesList.isEmpty()
                && vertexList[indexOf(a)].edgesList.contains(new EdgeWeight(b , null));

    }

    @Override
    public void addVertex(Object element) throws GraphException, ListException {
        if (counter >= vertexList.length)
            throw new GraphException("Adjecency List Graph is Full");
        //no valida vertices repetidos
        vertexList[counter++] = new Vertex(element);

    }

    @Override
    public void addEdge(Object a, Object b) throws GraphException, ListException {
        if (!containsVertex(a) || !containsVertex(b))
            throw new GraphException("Cannot add edge between vertexes[" + a + "]" + " y [" + b + "]");

        vertexList[indexOf(a)].edgesList.add(new EdgeWeight(b , null));

        //grafo no dirigido
        vertexList[indexOf(b)].edgesList.add(new EdgeWeight(a , null));



    }

    private int indexOf(Object element) {
        for (int i = 0; i < counter; i++) {
            if (util.Utility.compare(vertexList[i].data, element) == 0) return i;

        }
        return -1;

    }

    @Override
    public void addWeight(Object a, Object b, Object weight) throws GraphException, ListException {
        if (!containsEdge(a, b)) throw new GraphException("There is no edge between the vertexes [" + a + "]");


    }

    @Override
    public void addEdgeWeight(Object a, Object b, Object weight) throws GraphException, ListException {

        if (!containsEdge(a, b))
            throw new GraphException("Cannot add edge between vertexes [ " + a + " , " + b + "]" );

        if (!containsEdge(a , b)){
            vertexList[indexOf((a))].edgesList.add(new EdgeWeight(b , weight));
            vertexList[indexOf(b)].edgesList.add(new EdgeWeight(a , weight));
        }
    }

    @Override
    public void removeVertex(Object element) throws GraphException, ListException {

        if (isEmpty())
            throw new GraphException("Adjacency List Graph is Empty");

        if (containsVertex(element)) {

            for (int i = 0; i < counter; i++) {

                if (util.Utility.compare(vertexList[i].data, element)==0){
                    //ya lo encontramos el vertice , ahora debemos de suprimir todas las aristas

                    for (int j = 0; j < counter; j++) {

                        if (containsEdge(vertexList[i].data, element)){
                            removeEdge(vertexList[i].data, element);
                        }

                    }
                }

                //ahora debemos de suprimir el vertice
                for (int j = i; j < counter-1; j++) {

                    vertexList[i] = vertexList[j+1];

                }

                counter--;//Decrementamos el contador de vertices

            }
        }

    }

    @Override
    public void removeEdge(Object a, Object b) throws GraphException, ListException {

        if (!isEmpty())
            throw new GraphException("There's no some of the vertexes");

        if (!vertexList[indexOf(a)].edgesList.isEmpty()){
            vertexList[indexOf(a)].edgesList.remove(new EdgeWeight(b , null));

        }if (!vertexList[indexOf(b)].edgesList.isEmpty()){
            vertexList[indexOf(b)].edgesList.remove(new EdgeWeight(a , null));
        }

    }

    // Recorrido en profundidad
    @Override
    public String dfs() throws GraphException, StackException, ListException {
        setVisited(false);//marca todos los vertices como no vistados
        // inicia en el vertice 0
        String info = vertexList[0].data + ", ";
        vertexList[0].setVisited(true); // lo marca
        stack.clear();
        stack.push(0); //lo apila
        while (!stack.isEmpty()) {
            // obtiene un vertice adyacente no visitado,
            //el que esta en el tope de la pila
            int index = adjacentVertexNotVisited((int) stack.top());
            if (index == -1) // no lo encontro
                stack.pop();
            else {
                vertexList[index].setVisited(true); // lo marca
                info += vertexList[index].data + ", "; //lo muestra
                stack.push(index); //inserta la posicion
            }
        }
        return info;
    }

    //Recorrido en amplitud
    @Override
    public String bfs() throws GraphException, QueueException, ListException {
        setVisited(false);//marca todos los vertices como no visitados
        // inicia en el vertice 0
        String info = vertexList[0].data + ", ";
        vertexList[0].setVisited(true); // lo marca
        queue.clear();
        queue.enQueue(0); // encola el elemento
        int v2;
        while (!queue.isEmpty()) {
            int v1 = (int) queue.deQueue(); // remueve el vertice de la cola
            // hasta que no tenga vecinos sin visitar
            while ((v2 = adjacentVertexNotVisited(v1)) != -1) {
                // obtiene uno
                vertexList[v2].setVisited(true); // lo marca
                info += vertexList[v2].data + ", "; //lo muestra
                queue.enQueue(v2); // lo encola
            }
        }
        return info;
    }

    //setteamos el atributo visitado del vertice respectivo
    private void setVisited(boolean value) {
        for (int i = 0; i < counter; i++) {
            vertexList[i].setVisited(value); //value==true o false
        }//for
    }

    private int adjacentVertexNotVisited(int index) throws ListException {

        Object vertexData = vertexList[index].data;

        for (int i = 0; i < counter; i++) {

            if (!vertexList[index].edgesList.isEmpty()&& vertexList[i].edgesList.contains(new EdgeWeight(vertexData , null))
                    && !vertexList[i].isVisited())
                return i;


        }

        return -1;
    }

    public Object getVertexData(int index) throws GraphException {
        if (index < 0 || index >= counter) {
            throw new GraphException("Invalid vertex index.");
        }
        return vertexList[index].data;
    }


}
