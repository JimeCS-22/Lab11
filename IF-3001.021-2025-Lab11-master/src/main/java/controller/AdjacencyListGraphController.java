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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        try {
            int maxVertices = 10;
            graph = new AdjacencyListGraph(maxVertices);

            generateRandomValues();

            if (graph.size() > 1) { // Solo si hay al menos 2 vértices para conectar
                for (int i = 0; i < graph.size(); i++) {
                    for (int j = i + 1; j < graph.size(); j++) { // j = i + 1 para evitar duplicados (A-B y B-A)
                        graph.addEdge(graph.getVertexData(i), graph.getVertexData(j));
                    }
                }
            }

            // 4. Dibujar el grafo
            drawGraph();

        }catch (GraphException |ListException e ){

            throw new RuntimeException(e);

        }

    }

    //Metodo para crear los numeros aleatorios
    private void generateRandomValues(){
        try {

        int Count = 10;
        for (int i = 0; i < Count; i++) {

                graph.addVertex(util.Utility.RandomAlphabet()) ;

        }

        } catch (GraphException |ListException e ){

            throw new RuntimeException(e);

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
    }

    @javafx.fxml.FXML
    public void ContainsVextexOnAction(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void ToStringOnAction(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void BFSOnAction(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void containsEdgeOnAction(ActionEvent actionEvent) {
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
    }
}