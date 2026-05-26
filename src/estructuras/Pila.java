package estructuras;

public class Pila {

    int capacity = 2;
    int pila[] = new int[capacity];
    int top = 0;

    private void expand() {
        int length = size();
        int newPila[] = new int[capacity*2];
        System.arraycopy(pila, 0, newPila, 0, pila.length);
        pila = newPila;
        capacity *= 2;
    }
    private void shrink() {
        int length = size();
        if(length<=(capacity/2)/2)
            capacity = capacity/2;

        int newPila[] = new int[capacity];
        System.arraycopy(pila, 0, newPila, 0, length);
        pila = newPila;
    }

    public void push(int data) {
        if(size() == capacity){
            expand();
        }
        pila[top] = data;
        top++;
    }

    public int pop(){
        int data = 0;

        if(isEmpty()){
            System.out.println("La pila esta vacia");
        }
        else {
            top--;
            data = pila[top];
            pila[top] = 0;
            shrink();
        }
        return data;
    }

    public int peek(){
        int data;
        data = pila[top-1];
        return data;
    }

    public int size(){
        return top;
    }

    public boolean isEmpty(){
        return top <= 0;
    }

    public void show(){
        for(int n : pila){
            System.out.print(n+" ");
        }
    }
}
