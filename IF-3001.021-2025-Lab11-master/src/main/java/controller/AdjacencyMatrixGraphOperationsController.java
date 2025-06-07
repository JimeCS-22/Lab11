package controller;

import domain.*;
import domain.list.ListException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import util.FXUtility;

import java.util.*;

public class AdjacencyMatrixGraphOperationsController {
    @javafx.fxml.FXML
    private Pane mainPain;
    @javafx.fxml.FXML
    private Pane pane2;
    @javafx.fxml.FXML
    private Pane pane3;
    @javafx.fxml.FXML
    private AnchorPane AP;
    @javafx.fxml.FXML
    private TextArea textResult;
    @javafx.fxml.FXML
    private Label txtLabel;
    private AdjacencyMatrixGraph graph;
    private Map<Object, Circle> nodeCircles = new HashMap<>();

    @FXML
    public void initialize() {

        int maxVertices = 10;
        graph = new AdjacencyMatrixGraph(maxVertices);

        generateRandomGraph();

        drawGraph();

        textResult.setText(graph.toString());
    }

    private void generateRandomGraph(){
        try {
            graph.clear();

            Random rand = new Random();
            int numVerticesToGenerate = 10;
            int verticesAddedCount = 0;

            while (verticesAddedCount < numVerticesToGenerate && graph.size() < graph.vertexList.length) { // Añadimos hasta 'numVerticesToGenerate' o hasta que el grafo esté lleno
                int randomVertexValue = util.Utility.random(99);
                try {
                    graph.addVertex(randomVertexValue);
                    verticesAddedCount++;
                } catch (GraphException ge) {
                }
            }

            if (verticesAddedCount < numVerticesToGenerate) {
                System.err.println("Advertencia: No se pudieron añadir " + numVerticesToGenerate + " vértices únicos. Se añadieron " + verticesAddedCount + " vértices.");
            }


            int numEdgesToAdd = 15;
            for (int k = 0; k < numEdgesToAdd; k++) {
                if (graph.size() < 2) break;

                Object v1 = graph.getVertexData(rand.nextInt(graph.size()));
                Object v2 = graph.getVertexData(rand.nextInt(graph.size()));

                if (!v1.equals(v2) && !graph.containsEdge(v1, v2)) {
                    int weight = util.Utility.random(50) + 1;
                    graph.addEdgeWeight(v1, v2, weight);
                }
            }

        } catch (GraphException | ListException e) {
            showAlert("Error de generación", "Ocurrió un error al generar el grafo aleatorio: " + e.getMessage());
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
                showAlert("Error al redibujar el grafo (width)", e.getMessage());
            }
        });

        drawingPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            try {
                double currentCenterX = drawingPane.getWidth() / 2;
                double currentCenterY = newVal.doubleValue() / 2;
                double currentRadius = Math.min(currentCenterX, currentCenterY) * 0.8;
                redrawGraphContent(currentCenterX, currentCenterY, currentRadius, drawingPane);
            } catch (ListException | GraphException e) {
                showAlert("Error al redibujar el grafo (height)", e.getMessage());
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

        // Dibujar vértices
        for (int i =  0; i < numNodes; i++) {
            Object vertexData = graph.getVertexData(i);
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

        // Dibujar aristas
        for (int i = 0; i < numNodes; i++) {
            NodePosition node1Pos = positions.get(i);
            Object node1Data = node1Pos.value;

            for (int j = i + 1; j < numNodes; j++) { // j = i + 1 para evitar duplicados en grafo no dirigido
                NodePosition node2Pos = positions.get(j);
                Object node2Data = node2Pos.value;

                if (graph.containsEdge(node1Data, node2Data)) {
                    Line line = new Line(node1Pos.x, node1Pos.y, node2Pos.x, node2Pos.y);
                    line.setStroke(Color.BLACK);
                    line.setStrokeWidth(2);

                    // Manejar eventos hover
                    line.setOnMouseEntered(event -> {
                        line.setStroke(Color.RED);
                        line.setStrokeWidth(4);
                        try {
                            Object weight = graph.getEdgeWeight(node1Data, node2Data);
                            txtLabel.setText(String.format("Edge between the vertexes: %s — %s | Weight: %s",
                                    node1Data, node2Data, weight));
                        } catch (GraphException | ListException e) {
                            txtLabel.setText(String.format("Edge between the vertexes: %s — %s",
                                    node1Data, node2Data));
                        }
                    });

                    line.setOnMouseExited(event -> {
                        line.setStroke(Color.LIMEGREEN);
                        line.setStrokeWidth(3);
                        txtLabel.setText(""); // Limpiar label al salir
                    });

                    targetPane.getChildren().add(line);
                    line.toBack();

                    // Dibujar peso si existe
                    try {
                        Object weight = graph.getEdgeWeight(node1Data, node2Data);
                        if (weight != null && !weight.equals(0)) {
                            double midX = (node1Pos.x + node2Pos.x) / 2;
                            double midY = (node1Pos.y + node2Pos.y) / 2;

//                            Text weightText = new Text(midX + 5, midY - 5, String.valueOf(weight));
//                            weightText.setFill(Color.BLACK);
//                            weightText.setFont(new Font(10));
//                            targetPane.getChildren().add(weightText);
                        }
                    } catch (GraphException e) {
                        System.err.println("Error getting edge weight: " + e.getMessage());
                    }
                }
            }
        }
    }

    @FXML
    public void addVertexOnAction(ActionEvent actionEvent) {
        try {
            // Generar un vértice con valor numérico aleatorio (0-99)
            int newVertexValue;
            boolean vertexExists;
            int attempts = 0;
            final int MAX_ATTEMPTS = 100;

            do {
                newVertexValue = util.Utility.random(100); // Números del 0 al 99
                vertexExists = graph.containsVertex(newVertexValue);
                attempts++;
            } while (vertexExists && attempts < MAX_ATTEMPTS);

            if (vertexExists) {
                FXUtility.showAlert("Error", "No se pudo generar un vértice único después de " + MAX_ATTEMPTS + " intentos",
                        Alert.AlertType.ERROR);
                return;
            }

            graph.addVertex(newVertexValue);
            drawGraph();

        } catch (GraphException | ListException e) {
            FXUtility.showAlert("Error", "Error al añadir vértice: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void removeVertexOnAction(ActionEvent actionEvent) {
        try {
            if (graph.isEmpty()) {
                FXUtility.showAlert("Grafo Vacío", "No hay vértices para eliminar",
                        Alert.AlertType.WARNING);
                return;
            }

            // Seleccionar un vértice aleatorio para eliminar
            int randomIndex = new Random().nextInt(graph.size());
            Object vertexToRemove = graph.getVertexData(randomIndex);

            graph.removeVertex(vertexToRemove);
            drawGraph();

        } catch (GraphException | ListException e) {
            FXUtility.showAlert("Error", "Error al eliminar vértice: " + e.getMessage(),
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
        textResult.clear();
        pane3.getChildren().clear();
        txtLabel.setText("");
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

            Random rand = new Random();
            // Seleccionar dos vértices aleatorios distintos
            int v1Index = rand.nextInt(graph.size());
            int v2Index;
            do {
                v2Index = rand.nextInt(graph.size());
            } while (v1Index == v2Index);

            Object vertex1 = graph.getVertexData(v1Index);
            Object vertex2 = graph.getVertexData(v2Index);

//            // Verificar si la arista ya existe
//            if (graph.containsEdge(vertex1, vertex2)) {
//                FXUtility.showAlert("Información", "Ya existe una arista entre " + vertex1 + " y " + vertex2, Alert.AlertType.INFORMATION);
//                return;
//            }

            // Generar peso aleatorio entre 1 y 50
            int weight = rand.nextInt(50) + 1;

            // Añadir la arista con peso
            graph.addEdgeWeight(vertex1, vertex2, weight);

            // Actualizar la visualización
            drawGraph();
            textResult.setText(graph.toString());

//            FXUtility.showAlert("Éxito", "Arista añadida entre " + vertex1 + " y " + vertex2 + " con peso " + weight, Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            FXUtility.showAlert("Error", "Error al añadir arista: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void removeEdgeWeOnAction(ActionEvent actionEvent) {
        try {
            // Obtener todas las aristas existentes
            List<Object[]> existingEdges = new ArrayList<>();
            for (int i = 0; i < graph.size(); i++) {
                for (int j = i + 1; j < graph.size(); j++) { // Solo mitad superior para no dirigido
                    Object v1 = graph.getVertexData(i);
                    Object v2 = graph.getVertexData(j);
                    if (graph.containsEdge(v1, v2)) {
                        existingEdges.add(new Object[]{v1, v2});
                    }
                }
            }

            // Verificar si hay aristas
            if (existingEdges.isEmpty()) {
                FXUtility.showAlert("Información", "El grafo no contiene aristas para eliminar", Alert.AlertType.INFORMATION);
                return;
            }

            // Seleccionar una arista aleatoria para eliminar
            Random rand = new Random();
            Object[] edgeToRemove = existingEdges.get(rand.nextInt(existingEdges.size()));
            Object vertex1 = edgeToRemove[0];
            Object vertex2 = edgeToRemove[1];

            // Eliminar la arista
            graph.removeEdge(vertex1, vertex2);

            // Actualizar la visualización
            drawGraph();
            textResult.setText(graph.toString());

//            FXUtility.showAlert("Éxito", "Arista eliminada entre " + vertex1 + " y " + vertex2, Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            FXUtility.showAlert("Error", "Error al eliminar arista: " + e.getMessage(), Alert.AlertType.ERROR);
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
