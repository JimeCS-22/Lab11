package controller;

import domain.EdgeWeight;
import domain.GraphException;
import domain.SinglyLinkedListGraph;
import domain.Vertex;
import domain.list.ListException;
import domain.list.Node;
import domain.list.SinglyLinkedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class SinglyLinkedListGraphOperationsController {
    @FXML
    private Pane mainPain;
    @FXML
    private Pane pane2;
    @FXML
    private AnchorPane AP;
    @FXML
    private Pane pane3;
    @FXML
    private TextArea textResult;
    @FXML
    private Label edgeInfoLabel;

    private SinglyLinkedListGraph graph;
    private Map<Object, Circle> vertexCircles;
    private Map<Object, Text> vertexLabels;
    private Random random;
    private Line selectedLine = null;
    private Set<String> addedCharacters;

    private static final String[] HISTORICAL_CHARACTERS = {
            "Aristotle", "Marie Curie", "Leonardo da Vinci", "Isaac Newton",
            "Rosa Parks", "Nelson Mandela", "Abraham Lincoln", "Queen Elizabeth I",
            "Julius Caesar", "Ada Lovelace", "Albert Einstein", "Stephen Hawking",
            "Socrates", "Plato", "Galileo Galilei", "Charles Darwin",
            "George Washington", "Napoleon Bonaparte", "Queen Victoria", "Joan of Arc"
    };

    @FXML
    public void initialize() {
        graph = new SinglyLinkedListGraph();
        vertexCircles = new HashMap<>();
        vertexLabels = new HashMap<>();
        random = new Random();
        addedCharacters = new HashSet<>();
        drawGraph();
        updateTextResultWithGraphToString(); // Mostrar el toString del grafo inicial (vacío)
        if (edgeInfoLabel != null) {
            edgeInfoLabel.setText("Click an edge to see info");
            edgeInfoLabel.setTextFill(Color.RED);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Nuevo método auxiliar para actualizar textResult con el toString del grafo
    private void updateTextResultWithGraphToString() {
        textResult.setText(graph.toString());
    }

    @FXML
    public void addVertexAutoOnAction(ActionEvent actionEvent) {
        try {
            List<String> availableCharacters = new ArrayList<>();
            for (String character : HISTORICAL_CHARACTERS) {
                if (!graph.containsVertex(character)) {
                    availableCharacters.add(character);
                }
            }

            if (availableCharacters.isEmpty()) {
                showAlert("Añadir Vértice", "No hay más personajes disponibles para añadir.");
                return;
            }

            String newVertex = availableCharacters.get(random.nextInt(availableCharacters.size()));
            graph.addVertex(newVertex);
            addedCharacters.add(newVertex);
            drawGraph();
            updateTextResultWithGraphToString(); // Actualizar textResult
        } catch (GraphException | ListException e) {
            showAlert("Error al Añadir Vértice", e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    @FXML
    public void addEdgesWeightsAutoOnAction(ActionEvent actionEvent) {
        try {
            if (graph.size() < 2) {
                showAlert("Añadir Arista", "Se necesitan al menos 2 vértices para añadir una arista.");
                return;
            }

            List<Object> currentVertices = new ArrayList<>();
            for (int i = 1; i <= graph.size(); i++) {
                currentVertices.add(((Vertex) graph.getVertexList().getNode(i).data).data);
            }

            Object vertexA = null;
            Object vertexB = null;
            boolean edgeAdded = false;
            int attempts = 0;
            final int MAX_ATTEMPTS = 50;

            while (!edgeAdded && attempts < MAX_ATTEMPTS) {
                vertexA = currentVertices.get(random.nextInt(currentVertices.size()));
                vertexB = currentVertices.get(random.nextInt(currentVertices.size()));

                if (vertexA.equals(vertexB)) {
                    attempts++;
                    continue;
                }

                if (!graph.containsEdge(vertexA, vertexB)) {
                    Double weight = (double) ThreadLocalRandom.current().nextInt(100, 1001);
                    graph.addEdgeWeight(vertexA, vertexB, weight);
                    edgeAdded = true;
                }
                attempts++;
            }

            if (!edgeAdded) {
                showAlert("Añadir Arista", "No se pudo añadir una arista aleatoria (el grafo puede estar completo o casi completo).");
            }
            drawGraph();
            updateTextResultWithGraphToString(); // Actualizar textResult
        } catch (GraphException | ListException e) {
            showAlert("Error al Añadir Arista", e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    @FXML
    public void removeVertexAutoOnAction(ActionEvent actionEvent) {
        try {
            if (graph.isEmpty()) {
                showAlert("Eliminar Vértice", "El grafo está vacío. No hay vértices para eliminar.");
                return;
            }

            List<Object> currentVertices = new ArrayList<>();
            for (int i = 1; i <= graph.size(); i++) {
                currentVertices.add(((Vertex) graph.getVertexList().getNode(i).data).data);
            }

            Object vertexToRemove = currentVertices.get(random.nextInt(currentVertices.size()));
            graph.removeVertex(vertexToRemove);
            addedCharacters.remove(vertexToRemove);
            drawGraph();
            updateTextResultWithGraphToString(); // Actualizar textResult
        } catch (GraphException | ListException e) {
            showAlert("Error al Eliminar Vértice", e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    @FXML
    public void removeEdgesWeightsAutoOnAction(ActionEvent actionEvent) {
        try {
            if (graph.isEmpty()) {
                showAlert("Eliminar Arista", "El grafo está vacío. No hay aristas para eliminar.");
                return;
            }

            List<Pair<Object, Object>> existingEdges = new ArrayList<>();
            for (int i = 1; i <= graph.size(); i++) {
                Vertex sourceVertex = (Vertex) graph.getVertexList().getNode(i).data;
                Object sourceData = sourceVertex.data;
                SinglyLinkedList edges = sourceVertex.getEdgesList();
                if (edges != null && !edges.isEmpty()) {
                    Node currentEdgeNode = edges.getFirstNode();
                    while (currentEdgeNode != null) {
                        EdgeWeight ew = (EdgeWeight) currentEdgeNode.getData();
                        Object targetData = ew.getEdge();
                        if (!existingEdges.contains(new Pair<>(targetData, sourceData))) {
                            existingEdges.add(new Pair<>(sourceData, targetData));
                        }
                        currentEdgeNode = currentEdgeNode.next;
                    }
                }
            }

            if (existingEdges.isEmpty()) {
                showAlert("Eliminar Arista", "No hay aristas en el grafo para eliminar.");
                return;
            }

            Pair<Object, Object> edgeToRemove = existingEdges.get(random.nextInt(existingEdges.size()));
            graph.removeEdge(edgeToRemove.getKey(), edgeToRemove.getValue());
            drawGraph();
            updateTextResultWithGraphToString(); // Actualizar textResult
        } catch (GraphException | ListException e) {
            showAlert("Error al Eliminar Arista", e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    @FXML
    public void clearOnAction(ActionEvent actionEvent) {
        graph.clear();
        addedCharacters.clear();
        drawGraph();
        updateTextResultWithGraphToString(); // Actualizar textResult
        if (edgeInfoLabel != null) {
            edgeInfoLabel.setText("Click an edge to see info");
            edgeInfoLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    public void randomizeOnAction(ActionEvent actionEvent) {
        try {
            graph.clear();
            addedCharacters.clear();

            // Los mensajes de proceso ya no van al textResult, se manejan internamente o con showAlert si es necesario.
            // textResult.setText("Generando grafo aleatorio con 10 personajes históricos...\n"); // REMOVIDO

            List<String> characterList = new ArrayList<>();
            List<String> shuffledCharacters = new ArrayList<>(List.of(HISTORICAL_CHARACTERS));
            Collections.shuffle(shuffledCharacters);

            for (int i = 0; i < 10; i++) {
                String character = shuffledCharacters.get(i);
                graph.addVertex(character);
                characterList.add(character);
                addedCharacters.add(character);
            }
            // textResult.appendText("Se añadieron 10 personajes históricos aleatorios.\n"); // REMOVIDO

            int numVertices = graph.size();
            int minEdges = (int) (numVertices * (numVertices - 1) * 0.3 / 2);
            int actualMaxEdges = (int) (numVertices * (numVertices - 1) * 0.8 / 2);
            if (minEdges > actualMaxEdges) minEdges = actualMaxEdges;

            int numEdges = ThreadLocalRandom.current().nextInt(minEdges, actualMaxEdges + 1);
            if (numVertices < 2) numEdges = 0;

            int edgesAdded = 0;
            for (int i = 0; i < numEdges * 2; i++) {
                if (edgesAdded >= numEdges) break;
                if (numVertices < 2) break;

                try {
                    String vertexA = characterList.get(random.nextInt(characterList.size()));
                    String vertexB = characterList.get(random.nextInt(characterList.size()));

                    if (vertexA.equals(vertexB) || graph.containsEdge(vertexA, vertexB)) {
                        continue;
                    }

                    Double weight = (double) ThreadLocalRandom.current().nextInt(1000, 2001);
                    graph.addEdgeWeight(vertexA, vertexB, weight);
                    edgesAdded++;
                } catch (GraphException | ListException e) {
                    System.err.println("Error durante la creación aleatoria de aristas: " + e.getMessage());
                }
            }
            // textResult.appendText("Se añadieron " + edgesAdded + " aristas aleatorias con pesos entre 1000 y 2000.\n"); // REMOVIDO

            drawGraph();
            updateTextResultWithGraphToString(); // Actualizar textResult
            if (edgeInfoLabel != null) {
                edgeInfoLabel.setText("Click an edge to see info");
                edgeInfoLabel.setTextFill(Color.RED);
            }

        } catch (ListException | GraphException e) {
            showAlert("Error", "Error al generar grafo aleatorio: " + e.getMessage());
        }
    }

    @FXML
    public void nodeHeightOnAction(ActionEvent actionEvent) {
        showAlert("Función No Implementada", "La función 'Altura de Nodo' no está implementada en este momento para grafos.");
    }

    @FXML
    public void treeHeightOnAction(ActionEvent actionEvent) {
        showAlert("Función No Implementada", "La función 'Altura de Árbol' no está implementada en este momento para grafos.");
    }

    @FXML
    public void handleScrollZoom(Event event) {
        // Placeholder for FXML event
    }

    private void drawGraph() {
        pane3.getChildren().clear();
        vertexCircles.clear();
        vertexLabels.clear();
        selectedLine = null;

        try {
            if (graph.isEmpty()) {
                if (edgeInfoLabel != null) {
                    edgeInfoLabel.setText("Click an edge to see info");
                    edgeInfoLabel.setTextFill(Color.RED);
                }
                return;
            }

            SinglyLinkedList vertices = graph.getVertexList();
            if (vertices == null || vertices.isEmpty()) {
                return;
            }

            double centerX = pane3.getWidth() / 2;
            double centerY = pane3.getHeight() / 2;
            double radius = Math.min(centerX, centerY) * 0.8;
            int numVertices = vertices.size();

            Map<Object, javafx.geometry.Point2D> vertexPositions = new HashMap<>();

            for (int i = 1; i <= numVertices; i++) {
                Vertex currentVertex = (Vertex) vertices.getNode(i).data;
                Object vertexData = currentVertex.data;

                double angle = 2 * Math.PI * (i - 1) / numVertices;
                double x = centerX + radius * Math.cos(angle);
                double y = centerY + radius * Math.sin(angle);

                vertexPositions.put(vertexData, new javafx.geometry.Point2D(x, y));

                Circle circle = new Circle(x, y, 20);
                circle.setFill(Color.LIGHTBLUE);
                circle.setStroke(Color.DARKBLUE);
                circle.setStrokeWidth(2);

                Text label = new Text(String.valueOf(vertexData));
                label.setFont(Font.font("Arial", 14));
                label.setFill(Color.BLACK);
                label.setX(x - label.getLayoutBounds().getWidth() / 2);
                label.setY(y + label.getLayoutBounds().getHeight() / 4);

                pane3.getChildren().addAll(circle, label);
                vertexCircles.put(vertexData, circle);
                vertexLabels.put(vertexData, label);
            }

            for (int i = 1; i <= numVertices; i++) {
                Vertex sourceVertex = (Vertex) vertices.getNode(i).data;
                Object sourceData = sourceVertex.data;
                javafx.geometry.Point2D sourcePos = vertexPositions.get(sourceData);

                if (sourcePos == null || sourceVertex.getEdgesList() == null || sourceVertex.getEdgesList().isEmpty()) {
                    continue;
                }

                Node currentEdgeNode = sourceVertex.getEdgesList().getFirstNode();
                while (currentEdgeNode != null) {
                    EdgeWeight ew = (EdgeWeight) currentEdgeNode.getData();
                    Object targetData = ew.getEdge();
                    Object weight = ew.getWeight();

                    javafx.geometry.Point2D targetPos = vertexPositions.get(targetData);

                    if (targetPos != null) {
                        Line line = new Line(sourcePos.getX(), sourcePos.getY(),
                                targetPos.getX(), targetPos.getY());
                        line.setStroke(Color.DARKGRAY);
                        line.setStrokeWidth(1.5);
                        pane3.getChildren().add(0, line);

                        Object finalSourceData = sourceData;
                        Object finalTargetData = targetData;
                        Object finalWeight = weight;

                        line.setOnMouseClicked(event -> {
                            if (selectedLine != null) {
                                selectedLine.setStroke(Color.DARKGRAY);
                            }
                            line.setStroke(Color.BLUE);
                            selectedLine = line;

                            String info = "Edge between vertexes: " + finalSourceData + " ...... " + finalTargetData + ".";
                            if (finalWeight instanceof Double) {
                                info += " Weight: " + ((Double)finalWeight).intValue();
                            } else if (finalWeight != null) {
                                info += " Weight: " + finalWeight;
                            } else {
                                info += " Weight: N/A";
                            }
                            if (edgeInfoLabel != null) {
                                edgeInfoLabel.setText(info);
                                edgeInfoLabel.setTextFill(Color.BLUE);
                            }
                        });
                    }
                    currentEdgeNode = currentEdgeNode.next;
                }
            }
        } catch (ListException e) {
            showAlert("Drawing Error", "Error while drawing graph: " + e.getMessage());
        }
    }
}