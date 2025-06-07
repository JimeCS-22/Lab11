package controller;

import domain.EdgeWeight;
import domain.GraphException;
import domain.SinglyLinkedListGraph;
import domain.Vertex;
import domain.list.ListException;
import domain.list.SinglyLinkedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class SinglyLinkedListGraphController {

    @FXML
    private Pane AP;
    @FXML
    private Pane mainPain;
    @FXML
    private Pane pane2;
    @FXML
    private Pane pane3; // This is the drawing canvas
    @FXML
    private TextArea texResult;
    @FXML
    private Label edgeInfoLabel;

    private SinglyLinkedListGraph graph;
    private Map<Object, Circle> vertexCircles;
    private Map<Object, Text> vertexLabels;
    private Random random;
    private Line selectedLine = null; // Para mantener la línea seleccionada

    // Fixed list of historical characters
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
        drawGraph(); // Initial drawing of an empty graph
        edgeInfoLabel.setText("Click an edge to see info"); // Initial text for the label
        edgeInfoLabel.setTextFill(Color.RED); // Ensure initial color is red
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void containsVertexOnAction(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Verificar Vértice");
        dialog.setHeaderText("Verificar si un vértice existe en el grafo");
        dialog.setContentText("Ingrese el valor del vértice:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().isEmpty()) {
            try {
                Object vertexToFind = result.get();

                boolean contains = graph.containsVertex(vertexToFind);
                if (contains) {
                    texResult.setText("El vértice '" + vertexToFind + "' EXISTE en el grafo.");
                } else {
                    texResult.setText("El vértice '" + vertexToFind + "' NO EXISTE en el grafo.");
                }
            } catch (GraphException | ListException e) {
                showAlert("Error del Grafo", e.getMessage());
            } catch (Exception e) {
                showAlert("Error", "Ocurrió un error inesperado: " + e.getMessage());
            }
        }
    }

    @FXML
    void containsEdgeOnAction(ActionEvent event) {
        TextInputDialog dialog1 = new TextInputDialog();
        dialog1.setTitle("Verificar Arista");
        dialog1.setHeaderText("Verificar si existe una arista entre dos vértices");
        dialog1.setContentText("Ingrese el primer vértice (ej. 'Aristotle'):");

        Optional<String> result1 = dialog1.showAndWait();
        if (!result1.isPresent() || result1.get().isEmpty()) return;
        String v1Str = result1.get();

        TextInputDialog dialog2 = new TextInputDialog();
        dialog2.setTitle("Verificar Arista");
        dialog2.setHeaderText("Verificar si existe una arista entre dos vértices");
        dialog2.setContentText("Ingrese el segundo vértice (ej. 'Marie Curie'):");

        Optional<String> result2 = dialog2.showAndWait();
        if (!result2.isPresent() || result2.get().isEmpty()) return;
        String v2Str = result2.get();

        try {
            Object v1 = v1Str;
            Object v2 = v2Str;

            boolean contains = graph.containsEdge(v1, v2);
            String resultText = "La arista entre '" + v1 + "' y '" + v2 + "' ";
            if (contains) {
                Object weight = graph.getEdgeWeight(v1, v2);
                resultText += "EXISTE.";
                if (weight instanceof Double) { // Check if weight is Double, cast and format
                    resultText += " Peso: " + ((Double)weight).intValue();
                } else if (weight != null) {
                    resultText += " Peso: " + weight;
                } else {
                    resultText += " Peso: N/A";
                }
            } else {
                resultText += "NO EXISTE.";
            }
            texResult.setText(resultText);

        } catch (GraphException | ListException e) {
            showAlert("Error del Grafo", e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    @FXML
    void dfsOnAction(ActionEvent event) {
        try {
            String dfsTraversal = graph.dfs();
            texResult.setText("DFS Traversal: " + dfsTraversal);
        } catch (Exception e) {
            showAlert("Error", "DFS Traversal Error: " + e.getMessage());
        }
    }

    @FXML
    void bfsOnAction(ActionEvent event) {
        try {
            String bfsTraversal = graph.bfs();
            texResult.setText("BFS Traversal: " + bfsTraversal);
        } catch (Exception e) {
            showAlert("Error", "BFS Traversal Error: " + e.getMessage());
        }
    }

    @FXML
    void toStringOnAction(ActionEvent event) {
        texResult.setText(graph.toString());
    }

    @FXML
    void randomizeOnAction(ActionEvent event) {
        try {
            graph.clear();
            texResult.setText("Generating random graph with 10 historical characters...\n");

            List<String> characterList = new ArrayList<>();
            List<String> shuffledCharacters = new ArrayList<>(List.of(HISTORICAL_CHARACTERS));
            Collections.shuffle(shuffledCharacters);

            for (int i = 0; i < 10; i++) {
                String character = shuffledCharacters.get(i);
                graph.addVertex(character);
                characterList.add(character);
            }
            texResult.appendText("Added 10 random historical characters.\n");

            int numVertices = graph.size();
            int maxEdges = numVertices * (numVertices - 1) / 2;
            int minEdges = (int) (numVertices * (numVertices - 1) * 0.3 / 2);
            int actualMaxEdges = (int) (numVertices * (numVertices - 1) * 0.8 / 2);
            if (minEdges > actualMaxEdges) minEdges = actualMaxEdges;

            int numEdges = ThreadLocalRandom.current().nextInt(minEdges, actualMaxEdges + 1);
            if (numVertices < 2) numEdges = 0;

            int edgesAdded = 0;
            for (int i = 0; i < numEdges * 2; i++) { // Intentamos añadir el doble de aristas para asegurar el número
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
                    System.err.println("Error during random edge creation: " + e.getMessage());
                }
            }
            texResult.appendText("Added " + edgesAdded + " random edges with weights between 1000 and 2000.\n");
            // REMOVIDO: texResult.appendText("Current Graph:\n" + graph.toString()); // No mostrar el toString aquí
            drawGraph(); // Volver a dibujar el grafo con las nuevas aristas
            edgeInfoLabel.setText("Click an edge to see info"); // Restablecer etiqueta de información
            edgeInfoLabel.setTextFill(Color.RED); // Restablecer color de etiqueta
        } catch (ListException | GraphException e) {
            showAlert("Error", "Failed to generate random graph: " + e.getMessage());
        }
    }

    @FXML
    void handleScrollZoom(ScrollEvent event) {
        // Placeholder for FXML event
    }

    private void drawGraph() {
        pane3.getChildren().clear(); // Limpiar todo el contenido anterior del panel de dibujo
        vertexCircles.clear();
        vertexLabels.clear();
        selectedLine = null; // Reiniciar la línea seleccionada

        try {
            if (graph.isEmpty()) {
                edgeInfoLabel.setText("Click an edge to see info");
                edgeInfoLabel.setTextFill(Color.RED);
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

            // Dibujar vértices
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

            // Dibujar aristas y añadir interactividad
            for (int i = 1; i <= numVertices; i++) {
                Vertex sourceVertex = (Vertex) vertices.getNode(i).data;
                Object sourceData = sourceVertex.data;
                javafx.geometry.Point2D sourcePos = vertexPositions.get(sourceData);

                if (sourcePos == null || sourceVertex.getEdgesList() == null || sourceVertex.getEdgesList().isEmpty()) {
                    continue;
                }

                domain.list.Node currentEdgeNode = sourceVertex.getEdgesList().getFirstNode();
                while (currentEdgeNode != null) {
                    EdgeWeight ew = (EdgeWeight) currentEdgeNode.getData();
                    Object targetData = ew.getEdge();
                    Object weight = ew.getWeight();

                    javafx.geometry.Point2D targetPos = vertexPositions.get(targetData);

                    if (targetPos != null) {
                        Line line = new Line(sourcePos.getX(), sourcePos.getY(),
                                targetPos.getX(), targetPos.getY());
                        line.setStroke(Color.DARKGRAY); // Color por defecto de la línea
                        line.setStrokeWidth(1.5);
                        pane3.getChildren().add(0, line); // Añadir línea en la posición 0 para que esté detrás de los círculos

                        Object finalSourceData = sourceData;
                        Object finalTargetData = targetData;
                        Object finalWeight = weight;

                        // Manejador de eventos para click en la línea
                        line.setOnMouseClicked(event -> {
                            // Si hay una línea previamente seleccionada, restablecer su color
                            if (selectedLine != null) {
                                selectedLine.setStroke(Color.DARKGRAY); // Volver al color por defecto
                            }
                            // Establecer el color de la línea actual a un color de resaltado
                            line.setStroke(Color.BLUE); // Color de resaltado
                            selectedLine = line; // Almacenar esta línea como la seleccionada

                            // Mostrar información en el edgeInfoLabel
                            String info = "Edge between vertexes: " + finalSourceData + " ...... " + finalTargetData + ".";
                            if (finalWeight instanceof Double) {
                                info += " Weight: " + ((Double)finalWeight).intValue();
                            } else if (finalWeight != null) {
                                info += " Weight: " + finalWeight;
                            } else {
                                info += " Weight: N/A";
                            }
                            edgeInfoLabel.setText(info);
                            edgeInfoLabel.setTextFill(Color.BLUE); // Cambiar color del label a juego con el resaltado
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