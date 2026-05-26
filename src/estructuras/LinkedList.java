package estructuras;

public class LinkedList {
    Node head;

    public void insert(int data){
        Node node = new Node();
        node.data = data;
        node.next = null;

        if (head == null){
            head = node;
        }
        else{
            Node n = head;
            while(n.next!=null){
                n = n.next;
            }
            n.next = node;
        }
    }

    public void insertAtStart(int data){
        Node node = new Node();
        node.data = data;
        node.next = null;
        node.next = head;
        head = node;
    }

    public void insertAt(int index, int data){
        Node node = new Node();
        node.data = data;
        node.next = null;

        if(index == 0){
            insertAtStart(data);
        }
        else {
            Node n = head;
            for (int i = 0; i < index - 1; i++) {
                n = n.next;
            }
            node.next = n.next;
            n.next = node;
        }
    }

    public void deleteAt(int index) {
        if(index == 0){
            head = head.next;
        }
        else{
            Node n = head;
            Node n1 = null;

            for (int i = 0; i < index - 1; i++) {
                n = n.next;
            }
            n1 = n.next;
            n.next = n1.next;
            n1 = null;
        }
    }

    //retorna el dato del nodo en la posicion index
    public int getAt(int index){
        Node n = head;

        for (int i = 0; i < index - 1; i++) {
            n = n.next;
        }
        return n.data;
    }

    //retorna posicion del nodo con el valor que se busca
    public int searchFor(int data){
        Node node = head;
        int contador = 0;

        while(node.data != data){
            node = node.next;
            contador++;
        }
        return contador;
    }

    public void show(){
        Node node = head;

        while(node.next!=null){
            System.out.println(node.data);
            node = node.next;
        }
        System.out.println(node.data);
    }
}
