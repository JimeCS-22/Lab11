package domain.list;

public class SinglyLinkedList implements List{
    private Node first; //apuntador al inicio de la lista

    public SinglyLinkedList() {
        this.first = null; //la lista no existe
    }

    @Override
    public int size() throws ListException {
        if(isEmpty()){
            // It's generally better for size() to return 0 for an empty list
            // rather than throw an exception. This is a common pattern.
            return 0; // If empty, size is 0.
            // If you absolutely need to throw, then keep the original line,
            // but be aware it can make client code more complex.
            // throw new ListException("Singly Linked List is empty"); // Original
        }
        Node aux = first;
        int count=0;
        while(aux!=null){
            count++;
            aux = aux.next; //lo movemos al sgte nodo
        }
        return count;
    }

    @Override
    public void clear() {
        this.first = null; //anulamos la lista
    }

    @Override
    public boolean isEmpty() {
        return this.first == null; //si es nulo está vacía
    }

    @Override
    public boolean contains(Object element) throws ListException {
        // If size() doesn't throw on empty, this check needs to be adjusted
        // if(isEmpty()){ // No longer needed if size() returns 0 and other methods check for isEmpty() at start
        //     throw new ListException("Singly Linked List is empty");
        // }
        // The graph's containsVertex/containsEdge should check for graph.isEmpty() first.
        // For SinglyLinkedList.contains, returning false on empty list is usually fine.
        if(isEmpty()) return false; // Better to return false if list is empty and element not found

        Node aux = first;
        while(aux!=null){
            if(util.Utility.compare(aux.data, element)==0){
                return true;
            }
            aux = aux.next; //lo movemos al sgte nodo
        }
        return false; //indica q el elemento no existe
    }

    @Override
    public void add(Object element) {
        Node newNode = new Node(element);
        if(isEmpty()){
            first = newNode;
        }else{
            Node aux = first;
            //mientras no llegue al ult nodo
            while(aux.next!=null){
                aux=aux.next;
            }
            //una vez que se sale del while, quiere decir q
            //aux esta en el ult nodo, por lo q lo podemos enlazar
            //con el nuevo nodo
            aux.next = newNode;
        }

    }

    @Override
    public void addFirst(Object element) {
        Node newNode = new Node(element);
        if(isEmpty()){
            first = newNode;
        }else{
            newNode.next = first;
            first = newNode;
        }

    }

    @Override
    public void addLast(Object element) {
        add(element);
    }

    @Override
    public void addInSortedList(Object element) {
        // Not implemented, no change needed here.
    }

    @Override
    public void remove(Object element) throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        //Caso 1. El elemento a suprimir esta al inicio
        if(util.Utility.compare(first.data, element)==0){
            first = first.next; //saltamos el primer nodo
        }else{  //Caso 2. El elemento a suprimir puede estar al medio o final
            Node prev = first; //dejo un apuntador al nodo anterior
            Node aux = first.next;
            while(aux!=null && !(util.Utility.compare(aux.data, element)==0)){
                prev = aux;
                aux = aux.next;
            }
            //se sale cuando alcanza nulo o cuando encuentra el elemento
            if(aux!=null && util.Utility.compare(aux.data, element)==0){
                //ya lo encontro, procedo a desenlazar el nodo
                prev.next = aux.next;
            }
        }
    }

    @Override
    public Object removeFirst() throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        Object data = first.data;
        first = first.next;
        return data;
    }

    @Override
    public Object removeLast() throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        if(first.next == null){ // Only one element in the list
            Object data = first.data;
            first = null;
            return data;
        }
        Node aux = first;
        while(aux.next.next != null){ // Traverse until aux.next is the last node
            aux = aux.next;
        }
        Object data = aux.next.data;
        aux.next = null; // Remove the last node
        return data;
    }

    @Override
    public void sort() throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        // Using 1-based indexing for these loops as per requirement
        for (int i = 1; i <= size() ; i++) {
            for (int j = i+1; j <= size() ; j++) {
                // getNode(j).data will be compared with getNode(i).data
                if(util.Utility.compare(getNode(j).data, getNode(i).data)<0){
                    Object aux = getNode(i).data;
                    getNode(i).data = getNode(j).data;
                    getNode(j).data = aux;
                }
            }
        }
    }

    @Override
    public int indexOf(Object element) throws ListException {
        if(isEmpty()){
            // If empty, element cannot be found, return -1.
            // No need to throw an exception here unless element is expected to always exist.
            return -1;
            // throw new ListException("Singly Linked List is Empty"); // Original
        }
        Node aux = first;
        int index=1; // Start index at 1 for 1-based indexing
        while(aux!=null){
            if(util.Utility.compare(aux.data, element)==0){
                return index; // Return 1-based index if found
            }
            index++; //increment the 1-based index
            aux=aux.next; //muevo aux al sgte nodo
        }
        return -1; //indica q el elemento no existe
    }

    @Override
    public Object getFirst() throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        return first.data;
    }

    @Override
    public Object getLast() throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        Node aux = first;
        //mientras no llegue al ult nodo
        while(aux.next!=null){
            aux=aux.next;
        }
        //se sale del while cuando aux esta en el ult nodo
        return aux.data;
    }

    @Override
    public Object getPrev(Object element) throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        if(util.Utility.compare(first.data, element)==0){
            return "It's the first, it has no previous";
        }
        Node aux = first;
        //mientras no llegue al ult nodo
        while(aux.next!=null){
            if(util.Utility.compare(aux.next.data, element)==0){
                return aux.data; //retornamos la data del nodo actual
            }
            aux=aux.next;
        }
        return "Does not exist in Single Linked List";
    }

    @Override
    public Object getNext(Object element) throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        Node aux = first;
        while(aux!=null){
            if(util.Utility.compare(aux.data, element)==0){
                if(aux.next != null){
                    return aux.next.data;
                } else {
                    return "It's the last, it has no next";
                }
            }
            aux = aux.next;
        }
        return "Does not exist in Single Linked List";
    }

    @Override
    public Node getNode(int index) throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        // Validamos que el índice esté entre 1 and size() (inclusive) for 1-based indexing
        if (index < 1 || index > size()) {
            throw new ListException("Invalid index: " + index);
        }
        Node aux = first;
        int i = 1; // Start counter at 1 for 1-based indexing
        while(aux!=null){
            if(util.Utility.compare(i, index)==0) {  //ya encontro el indice
                return aux;
            }
            i++; //increment the 1-based local counter
            aux = aux.next; //muevo aux al sgte nodo
        }
        return null; // Should not reach here if bounds check is correct and list is not empty
    }

    public Node getNode(Object element) throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        Node aux = first;
        while(aux!=null){
            if(util.Utility.compare(aux.data, element)==0) {  //ya encontro el elemento
                return aux;
            }
            aux = aux.next; //muevo aux al sgte nodo
        }
        return null; //si llega aqui es xq no encontro el element
    }

    @Override
    public String toString() {
        //String result = "Singly Linked List Content\n\n";
        String result = "";
        Node aux = first;
        while(aux!=null){
            result+= "\n"+aux.data;
            aux = aux.next;
        }
        return result;
    }


    public Object get(int index) throws ListException {
        if(isEmpty()){
            throw new ListException("Singly Linked List is Empty");
        }
        // Validamos que el índice esté entre 1 and size() (inclusive) for 1-based indexing
        if (index < 1 || index > size()) {
            throw new ListException("Invalid index: " + index);
        }

        Node aux = first;
        int i = 1; // Start counter at 1 for 1-based indexing
        while(aux != null){
            if(util.Utility.compare(i, index) == 0) {
                return aux.data;
            }
            i++;
            aux = aux.next;
        }
        return null; // Should not reach here
    }
}