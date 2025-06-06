package controller;

import domain.AdjacencyListGraph;
import domain.Graph;
import domain.GraphException;
import domain.list.ListException;
import domain.list.SinglyLinkedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.*;

public class AdjacencyListGraphController {
    @javafx.fxml.FXML
    private Pane mainPain;
    @javafx.fxml.FXML
    private Pane pane2;
    @javafx.fxml.FXML
    private Label balanceLabel;
    @javafx.fxml.FXML
    private AnchorPane AP;
    private AdjacencyListGraph graph;
    private Map<Object, Circle> nodeCircles = new HashMap<>();
    @FXML
    private Pane pane3;
    @FXML
    private TextArea textResult;

    @FXML
    public void initialize() {

            int maxVertices = 10;
            graph = new AdjacencyListGraph(maxVertices);

            generateRandomGraph(); // Nuevo método para generar el grafo completo aleatorio

            drawGraph();


    }

    //Metodo para crear los numeros aleatorios
    private void generateRandomGraph(){
        try {
            Random rand = new Random();
            graph.clear(); // Limpiamos el grafo anterior
            textResult.clear(); // Limpiamos el área de texto de resultados

            int numVertices = 10;
            for (int i = 0; i < numVertices; i++) {
                Object randomChar;
                boolean vertexAdded = false;
                do {
                    randomChar = util.Utility.RandomAlphabet(); // Asumo que RandomAlphabet genera un char no repetido
                    try {
                        graph.addVertex(randomChar);
                        vertexAdded = true;
                    } catch (GraphException ge) {
                        // Si ya existe o el grafo está lleno, intenta de nuevo con otro caracter
                        System.err.println("Intentando añadir vértice ya existente: " + randomChar + ". Reintentando...");
                    }
                } while (!vertexAdded);
            }

            // Conectar aristas con pesos aleatorios
            int numEdgesToAdd = 15; // Número de aristas a agregar
            for (int k = 0; k < numEdgesToAdd; k++) {
                if (graph.size() < 2) break; // Necesitamos al menos 2 vértices para una arista

                Object v1 = graph.getVertexData(rand.nextInt(graph.size()));
                Object v2 = graph.getVertexData(rand.nextInt(graph.size()));

                // Asegurar que no sea el mismo vértice y que la arista no exista ya
                if (!v1.equals(v2) && !graph.containsEdge(v1, v2)) {
                    int weight = rand.nextInt(50) + 1; // Pesos entre 1 y 50
                    graph.addEdgeWeight(v1, v2, weight);
                }
            }

            textResult.setText("Grafo aleatorio generado con " + graph.size() + " vértices y aristas.");

        } catch (GraphException | ListException e) {
            showAlert("Error al generar grafo aleatorio", e.getMessage());
        }
    }

    private void drawGraph() {
        if (graph == null || graph.isEmpty()) {
            showAlert("Grafo Vacío", "No hay vértices para dibujar.");
            return;
        }
        Pane drawingPane = pane3;

        double initialCenterX = drawingPane.getWidth() / 2;
        double initialCenterY = drawingPane.getHeight() / 2;
        double initialRadius = Math.min(initialCenterX, initialCenterY) * 0.8;

        drawingPane.getChildren().clear();
        nodeCircles.clear();

        drawingPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            try {
                double currentCenterX = newVal.doubleValue() / 2;
                double currentCenterY = drawingPane.getHeight() / 2;
                double currentRadius = Math.min(currentCenterX, currentCenterY) * 0.8;
                redrawGraphContent(currentCenterX, currentCenterY, currentRadius, drawingPane);
            } catch (ListException | GraphException e) {
                showAlert("Error al redibujar el grafo", e.getMessage());
            }
        });

        drawingPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            try {
                double currentCenterX = drawingPane.getWidth() / 2;
                double currentCenterY = newVal.doubleValue() / 2;
                double currentRadius = Math.min(currentCenterX, currentCenterY) * 0.8;
                redrawGraphContent(currentCenterX, currentCenterY, currentRadius, drawingPane);
            } catch (ListException | GraphException e) {
                showAlert("Error al redibujar el grafo", e.getMessage());
            }
        });

        try {
            redrawGraphContent(initialCenterX, initialCenterY, initialRadius, drawingPane);
        } catch (ListException | GraphException e) {
            showAlert("Error al dibujar el grafo inicial", e.getMessage());
        }

    }

    private void redrawGraphContent(double centerX, double centerY, double radius, Pane targetPane) throws ListException, GraphException {
        targetPane.getChildren().clear();
        nodeCircles.clear();

        List<NodePosition> positions = new ArrayList<>();
        int numNodes = graph.size();

        for (int i = 0; i < numNodes; i++) {
            Object vertexData = graph.vertexList[i].data;

            double angle = 2 * Math.PI * i / numNodes;
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);
            positions.add(new NodePosition(vertexData, x, y));

            Circle circle = new Circle(x, y, 20, Color.DEEPSKYBLUE);
            circle.setStroke(Color.BLACK);
            targetPane.getChildren().add(circle);
            nodeCircles.put(vertexData, circle);

            Text text = new Text(x - 10, y + 5, String.valueOf(vertexData));
            text.setFill(Color.RED);
            text.setFont(new Font(12));
            targetPane.getChildren().add(text);
        }

        for (int i = 0; i < numNodes; i++) {
            NodePosition node1Pos = positions.get(i);
            Object node1Data = node1Pos.value;

            for (int j = 0; j < numNodes; j++) {
                if (i == j) continue;

                NodePosition node2Pos = positions.get(j);
                Object node2Data = node2Pos.value;

                if (i < j && graph.containsEdge(node1Data, node2Data)) { // Solo dibuja una vez por par y verifica si existe la arista
                    Line line = new Line(node1Pos.x, node1Pos.y, node2Pos.x, node2Pos.y);
                    line.setStroke(Color.BLACK); // Color de línea por defecto

                    targetPane.getChildren().add(line);
                    line.toBack();
                }
            }
        }
    }




    @javafx.fxml.FXML
    public void handleScrollZoom(Event event) {
    }

    @javafx.fxml.FXML
    public void DFSOnAction(ActionEvent actionEvent) {

        try {
            if (graph.isEmpty()) {
                textResult.setText("El grafo está vacío, no se puede realizar el DFS.");
                return;
            }
            textResult.setText("DFS Tour: " + graph.dfs());
        } catch (GraphException | ListException | domain.stack.StackException e) {
            showAlert("Error en DFS", e.getMessage());
        }

    }

    @javafx.fxml.FXML
    public void ContainsVextexOnAction(ActionEvent actionEvent) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Verificar Vértice");
        dialog.setHeaderText("Verificar si un vértice existe en el grafo");
        dialog.setContentText("Ingrese el valor del vértice (una letra):");

        String vertexStr = dialog.showAndWait().orElse(null);
        if (vertexStr == null || vertexStr.isEmpty()) return;

        Object vertex = vertexStr.charAt(0); // Asumiendo que el vértice es un Character

        try {
            boolean contains = graph.containsVertex(vertex);
            if (contains) {
                textResult.setText("El vértice '" + vertex + "' EXISTE en el grafo.");
            } else {
                textResult.setText("El vértice '" + vertex + "' NO EXISTE en el grafo.");
            }
        } catch (GraphException | ListException e) {
            showAlert("Error del Grafo", e.getMessage());
        }

    }

    @javafx.fxml.FXML
    public void ToStringOnAction(ActionEvent actionEvent) {


            textResult.setText(graph.toString());

    }

    @javafx.fxml.FXML
    public void BFSOnAction(ActionEvent actionEvent) {

        try {
            if (graph.isEmpty()) {
                textResult.setText("El grafo está vacío, no se puede realizar el BFS.");
                return;
            }
            textResult.setText("BFS Tour: " + graph.bfs());
        } catch (GraphException | ListException | domain.queue.QueueException e) {
            showAlert("Error en BFS", e.getMessage());
        }

    }

    @javafx.fxml.FXML
    public void containsEdgeOnAction(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Verificar Arista");
        dialog.setHeaderText("Verificar si existe una arista entre dos vértices");
        dialog.setContentText("Ingrese el primer vértice (una letra):");

        String v1Str = dialog.showAndWait().orElse(null);
        if (v1Str == null || v1Str.isEmpty()) return;

        dialog.setContentText("Ingrese el segundo vértice (una letra):");
        String v2Str = dialog.showAndWait().orElse(null);
        if (v2Str == null || v2Str.isEmpty()) return;

        try {
            Object v1 = v1Str.charAt(0); // Asumiendo que los vértices son Character
            Object v2 = v2Str.charAt(0);

            boolean contains = graph.containsEdge(v1, v2);
            String result = "La arista entre '" + v1 + "' y '" + v2 + "' ";
            if (contains) {
                Object weight = graph.getEdgeWeight(v1, v2); // Obtener el peso
                result += "EXISTE. Peso: " + weight;
            } else {
                result += "NO EXISTE.";
            }
            textResult.setText(result);

        } catch (GraphException | ListException e) {
            showAlert("Error del Grafo", e.getMessage());
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void randomizeOnAction(ActionEvent actionEvent) {

        try {
            generateRandomGraph(); // Generar un nuevo grafo aleatorio
            drawGraph(); // Redibujar el grafo
            textResult.setText("Nuevo grafo aleatorio generado y dibujado.");
        } catch (Exception e) {
            showAlert("Error al Aleatorizar", e.getMessage());
        }

    }
}