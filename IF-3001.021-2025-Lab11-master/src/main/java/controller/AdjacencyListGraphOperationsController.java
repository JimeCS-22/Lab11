package controller;

import domain.AdjacencyListGraph;
import domain.EdgeWeight;
import domain.GraphException;
import domain.Vertex;
import domain.list.ListException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import util.FXUtility;
import util.Utility;

import java.util.*;

public class AdjacencyListGraphOperationsController {
    @javafx.fxml.FXML
    private Pane mainPain;
    @javafx.fxml.FXML
    private Pane pane2;
    @javafx.fxml.FXML
    private AnchorPane AP;
    @javafx.fxml.FXML
    private Pane pane3;
    @FXML
    private TextArea textResult;
    @javafx.fxml.FXML
    private Label txtLabel;
    private AdjacencyListGraph graph;
    private Map<Object, Circle> nodeCircles = new HashMap<>();

    @FXML
    public void initialize() {

        int maxVertices = 10;
        graph = new AdjacencyListGraph(maxVertices);

        generateRandomGraph();

        drawGraph();

        pane3.setOnScroll(this::handleScrollZoomForPane3);

        textResult.setText(graph.toString());

    }

    private void generateRandomGraph(){

        try {
            Random rand = new Random();
            graph.clear();
            textResult.clear();

            int numVerticesToGenerate = 10;
            Set<Character> usedCharacters = new HashSet<>();
            List<Character> availableCharacters = new ArrayList<>();
            for (char c = 'A'; c <= 'Z'; c++) {
                availableCharacters.add(c);
            }
            Collections.shuffle(availableCharacters); // Mezclar una vez para obtener un orden aleatorio

            int currentVertexCount = 0;
            for (Character c : availableCharacters) {
                if (currentVertexCount >= numVerticesToGenerate) {
                    break;
                }

                try {
                    graph.addVertex(c);
                    usedCharacters.add(c);
                    currentVertexCount++;
                } catch (GraphException ge) {
                    System.err.println("Error al añadir vértice: " + ge.getMessage());
                }
            }

            // Conectar aristas con pesos aleatorios
            int numEdgesToAdd = 15;
            for (int k = 0; k < numEdgesToAdd; k++) {
                if (graph.size() < 2) break;

                Object v1 = graph.getVertexData(rand.nextInt(graph.size()));
                Object v2 = graph.getVertexData(rand.nextInt(graph.size()));

                if (!v1.equals(v2) && !graph.containsEdge(v1, v2)) {
                    int weight = rand.nextInt(50) + 1;
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
                    line.setStrokeWidth(2); // Un poco más gruesa para que sea más fácil interactuar

                    // *** Añadir los manejadores de eventos ***
                    line.setOnMouseEntered(event -> {
                        line.setStroke(Color.RED); // Se pone roja al pasar el mouse
                        line.setStrokeWidth(3); // Hacerla un poco más gruesa cuando está seleccionada
                    });

                    line.setOnMouseExited(event -> {
                        line.setStroke(Color.LIMEGREEN); // ¡Ahora se pone verde al salir el mouse!
                        line.setStrokeWidth(2); // Vuelve a su grosor original
                    });

                    targetPane.getChildren().add(line);
                    line.toBack();
                }
            }
        }
    }

    @javafx.fxml.FXML
    public void addVertexOnAction(ActionEvent actionEvent) {
        try {
            // Generar un vértice aleatorio (letra mayúscula)
            char newVertex = (char) ('A' + new Random().nextInt(26));

            // Verificar si el vértice ya existe
            while (graph.containsVertex(newVertex)) {
                newVertex = (char) ('A' + new Random().nextInt(26));
            }

            graph.addVertex(newVertex);
            drawGraph(); // Redibujar el grafo con el nuevo vértice
            textResult.setText(graph.toString());
            FXUtility.showAlert("Vértice Añadido",
                    "Se ha añadido el vértice: " + newVertex,
                    Alert.AlertType.INFORMATION);
        } catch (GraphException e) {
            FXUtility.showAlert("Error al Añadir",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (ListException e) {
            throw new RuntimeException(e);
        }
    }

    @javafx.fxml.FXML
    public void removeVertexOnAction(ActionEvent actionEvent) {
        try {
            if (graph.isEmpty()) {
                FXUtility.showAlert("Grafo Vacío",
                        "No hay vértices para eliminar",
                        Alert.AlertType.WARNING);
                return;
            }

            // Seleccionar un vértice aleatorio para eliminar
            int randomIndex = new Random().nextInt(graph.size());
            Object vertexToRemove = graph.getVertexData(randomIndex);

            graph.removeVertex(vertexToRemove);
            drawGraph(); // Redibujar el grafo sin el vértice eliminado
            textResult.setText(graph.toString());
            FXUtility.showAlert("Vértice Eliminado",
                    "Se ha eliminado el vértice: " + vertexToRemove,
                    Alert.AlertType.INFORMATION);
        } catch (GraphException | ListException e) {
            FXUtility.showAlert("Error al Eliminar",
                    e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @javafx.fxml.FXML
    public void randomizeOnAction(ActionEvent actionEvent) {
        try {
            generateRandomGraph(); // Generar un nuevo grafo aleatorio
            drawGraph(); // Redibujar el grafo
            textResult.setText(graph.toString());
        } catch (Exception e) {
            showAlert("Error al Aleatorizar", e.getMessage());
        }

    }

    @FXML
    private void handleScrollZoomForPane3(ScrollEvent event) {
        double zoomFactor = 1.1;
        if (event.getDeltaY() < 0) {
            zoomFactor = 1 / zoomFactor;
        }
        pane3.setScaleX(pane3.getScaleX() * zoomFactor);
        pane3.setScaleY(pane3.getScaleY() * zoomFactor);
        event.consume(); // Opcional: evita que el evento se propague
    }

    @FXML
    public void clearOnAction(ActionEvent actionEvent) {
        for (Node node : pane3.getChildren()) {
            if (node instanceof Line) {
                Line line = (Line) node;

                line.setStroke(Color.BLACK);
                line.setStrokeWidth(1);
            }
        }

    }

    @FXML
    public void addEdgeWeOnAction(ActionEvent actionEvent) {
        try {
            // Verificar que hay al menos 2 vértices
            if (graph.size() < 2) {
                FXUtility.showAlert("Error", "Se necesitan al menos 2 vértices para crear una arista", Alert.AlertType.WARNING);
                return;
            }

            // Seleccionar dos vértices aleatorios distintos
            Random rand = new Random();
            int v1Index = rand.nextInt(graph.size());
            int v2Index;
            do {
                v2Index = rand.nextInt(graph.size());
            } while (v1Index == v2Index);

            Object vertex1 = graph.getVertexData(v1Index);
            Object vertex2 = graph.getVertexData(v2Index);

            // Verificar si la arista ya existe
            if (graph.containsEdge(vertex1, vertex2)) {
                FXUtility.showAlert("Información", "Ya existe una arista entre " + vertex1 + " y " + vertex2, Alert.AlertType.INFORMATION);
                return;
            }

            // Generar peso aleatorio entre 1 y 50
            int weight = rand.nextInt(50) + 1;

            // Añadir la arista con peso (usando EdgeWeight internamente)
            graph.addEdgeWeight(vertex1, vertex2, weight);

            // Actualizar la visualización
            drawGraph();
            textResult.setText(graph.toString());

            FXUtility.showAlert("Éxito", "Arista añadida entre " + vertex1 + " y " + vertex2 + " con peso " + weight, Alert.AlertType.INFORMATION);

        } catch (GraphException | ListException e) {
            FXUtility.showAlert("Error", "No se pudo añadir la arista: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void removeEdgeWeOnAction(ActionEvent actionEvent) {
        try {
            // Verificar que hay aristas para eliminar
            boolean hasEdges = false;
            for (int i = 0; i < graph.size(); i++) {
                if (!graph.vertexList[i].edgesList.isEmpty()) {
                    hasEdges = true;
                    break;
                }
            }

            if (!hasEdges) {
                FXUtility.showAlert("Error", "El grafo no tiene aristas para eliminar", Alert.AlertType.WARNING);
                return;
            }

            // Buscar aleatoriamente un vértice que tenga aristas
            Random rand = new Random();
            int attempts = 0;
            int vertexIndex;
            Object vertex1 = null;
            Object vertex2 = null;

            while (attempts < graph.size() * 2) {
                vertexIndex = rand.nextInt(graph.size());
                Vertex vertex = graph.vertexList[vertexIndex];

                if (!vertex.edgesList.isEmpty()) {
                    // Seleccionar una arista aleatoria de este vértice
                    int edgeIndex = rand.nextInt(vertex.edgesList.size());
                    EdgeWeight edge = (EdgeWeight) vertex.edgesList.get(edgeIndex);

                    vertex1 = vertex.data;
                    vertex2 = edge.getEdge();
                    break;
                }
                attempts++;
            }

            if (vertex1 == null) {
                FXUtility.showAlert("Error", "No se encontró ninguna arista para eliminar", Alert.AlertType.WARNING);
                return;
            }

            // Eliminar la arista (bidireccional en grafo no dirigido)
            graph.removeEdge(vertex1, vertex2);

            // Actualizar la visualización
            drawGraph();
            textResult.setText(graph.toString());

            FXUtility.showAlert("Éxito", "Arista eliminada entre " + vertex1 + " y " + vertex2, Alert.AlertType.INFORMATION);

        } catch (GraphException | ListException e) {
            FXUtility.showAlert("Error", "No se pudo eliminar la arista: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
