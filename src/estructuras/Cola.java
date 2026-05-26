package estructuras;

public class Cola {
    Node temp, front=null,rear=null;
    int n;

    public void insert(int data) {
        n = data;
        temp = new Node();
        temp.data = n;
        temp.address = null;
        if(rear == null){
            rear = temp;
            front = temp;
        }else{
            rear.address = temp;
            rear = temp;
        }
    }

    public void remove(){
        if(front == rear)
            System.out.println("Queue is Empty");
        else{
            temp = front;
            front = front.address;
            temp = null;
        }
    }

    public void show(){
        for(temp=front;temp!=rear;temp=temp.address){
            System.out.print(temp.data+"  ");
        }
        System.out.print(temp.data+"  ");
    }
}
