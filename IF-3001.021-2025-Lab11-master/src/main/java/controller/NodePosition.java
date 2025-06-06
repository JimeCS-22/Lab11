package controller;

public class NodePosition {

    Object value; // Ahora almacena Object, no solo int
    double x;
    double y;

    public NodePosition(Object value, double x, double y) {
        this.value = value;
        this.x = x;
        this.y = y;
    }

}
