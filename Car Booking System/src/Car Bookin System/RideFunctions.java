package project;

import java.sql.*;
import java.util.Scanner;

public class RideFunctions {

	Scanner cin = new Scanner(System.in);

    Node front, end, top = null;
    Node[] heap = new Node[5];
    int heapsize = 0;
    Connection con;

    public RideFunctions(Connection con) {
        this.con = con;
    }
public void book() {
        try {
            Node n = new Node();
            System.out.print("Enter Ride ID: ");
            n.id = cin.nextInt();
            System.out.print("Enter Customer Name: ");
            n.name = cin.next();
            System.out.print("Enter Phone: ");
            n.phone = cin.next();
            System.out.print("Enter Car Number: ");
            n.carNo = cin.next();
            System.out.print("Enter Car Model: ");
            n.carModel = cin.next();
            System.out.print("Enter Destination: ");
            n.dest = cin.next();
            if (end == null)
                front = end = n;
            else {
                end.next = n;
                end = n;
            }

            heapInsert(n);
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO Ridesdata VALUES (?, ?, ?, ?, ?, ?)"
            );
            ps.setInt(1, n.id);
            ps.setString(2, n.name);
            ps.setString(3, n.phone);
            ps.setString(4, n.carNo);
            ps.setString(5, n.carModel);
            ps.setString(6, n.dest);
            ps.executeUpdate();

            System.out.println("Ride booked with car assigned");

        } catch (Exception e) {
            System.out.println("Error booking ride " + e);
        }
    }
public void heapInsert(Node n) {
        heapsize++;
        heap[heapsize] = n;
        heapifyUp(heapsize);
    }

public void heapifyUp(int i) {
        while (i > 1 && heap[i].id > heap[i / 2].id) {
            Node temp = heap[i];
            heap[i] = heap[i / 2];
            heap[i / 2] = temp;
            i = i / 2;
        }
    }

public Node heapDelete() {
        if (heapsize == 0)
            return null;

        Node max = heap[1];
        heap[1] = heap[heapsize];
        heapsize--;
        heapifyDown(1);
        return max;
    }

 public void heapifyDown(int i) {
        int largest = i;
        int l = 2 * i;
        int r = 2 * i + 1;

        if (l <= heapsize && heap[l].id > heap[largest].id)
            largest = l;
        if (r <= heapsize && heap[r].id > heap[largest].id)
            largest = r;

        if (largest != i) {
            Node temp = heap[i];
            heap[i] = heap[largest];
            heap[largest] = temp;
            heapifyDown(largest);
        }
    }

    // ================= SERVE FIFO =================
public void serve() {
        if (front == null) {
            System.out.println("No ride to serve");
            return;
        }

        System.out.println("Ride Served (FIFO)");
        System.out.println("Customer: " + front.name);
        System.out.println("Car: " + front.carNo);

        pushUndo(front);

        try {
            PreparedStatement ps =
                con.prepareStatement("DELETE FROM Ridesdata WHERE id=?");
            ps.setInt(1, front.id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("SQL Delete Error " + e);
        }

        heapDelete();
        front = front.next;
        if (front == null)
        	end = null;
    }

public void serveHighestPriority() {

        Node n = heapDelete();   // heap se max priority

        if (n == null) {
            System.out.println("No ride to serve");
            return;
        }

        System.out.println("Ride Served (Highest Priority)");
        System.out.println("Customer: " + n.name);
        System.out.println("Car: " + n.carNo);

        pushUndo(n);

        // Queue se bhi remove
        if (front != null) {
            if (front.id == n.id) {
                front = front.next;
                if (front == null) end = null;
            } else {
                Node prev = front;
                Node curr = front.next;

                while (curr != null) {
                    if (curr.id == n.id) {
                        prev.next = curr.next;
                        if (curr == end) end = prev;
                        break;
                    }
                    prev = curr;
                    curr = curr.next;
                }
            }
        }
        try {
            PreparedStatement ps =
                con.prepareStatement("DELETE FROM Ridesdata WHERE id=?");
            ps.setInt(1, n.id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Heap Serve SQL Error " + e);
        }
    }

public void Undo() {
        if (top == null) {
            System.out.println("No ride to undo");
            return;
        }
        Node undo = top;
        top = top.next;
        Node n = new Node();
        n.id = undo.id;
        n.name = undo.name;
        n.phone = undo.phone;
        n.carNo = undo.carNo;
        n.carModel = undo.carModel;
        n.dest = undo.dest;
       if (end == null)
            front = end = n;
        else {
            end.next = n;
            end = n;
        }

        heapInsert(n);
         try {
            PreparedStatement ps =
                con.prepareStatement("INSERT INTO Ridesdata VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, n.id);
            ps.setString(2, n.name);
            ps.setString(3, n.phone);
            ps.setString(4, n.carNo);
            ps.setString(5, n.carModel);
            ps.setString(6, n.dest);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Undo Error " + e);
        }

        System.out.println("Ride restored");
    }
public void search() {
        try {
            System.out.print("Enter customer name: ");
            String s = cin.next();

            PreparedStatement ps =
                con.prepareStatement("SELECT * FROM Ridesdata WHERE customerName=?");
            ps.setString(1, s);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("ID: " + rs.getInt(1));
                System.out.println("Name: " + rs.getString(2));
                System.out.println("Phone: " + rs.getString(3));
                System.out.println("Car: " + rs.getString(4));
                System.out.println("Model: " + rs.getString(5));
                System.out.println("Destination: " + rs.getString(6));
            } else {
                System.out.println("No ride found");
            }
        } catch (Exception e) {
            System.out.println("Search Error " + e);
        }
    }

public void showAll() {
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Ridesdata");

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt(1));
                System.out.println("Customer: " + rs.getString(2));
                System.out.println("Phone: " + rs.getString(3));
                System.out.println("Car No: " + rs.getString(4));
                System.out.println("Model: " + rs.getString(5));
                System.out.println("To: " + rs.getString(6));
                System.out.println("-------------------");
            }
        } catch (Exception e) {
            System.out.println("Show Error " + e);
        }
    }

    // ================= UNDO STACK =================
public void pushUndo(Node ride) {
        Node t = new Node();
        t.id = ride.id;
        t.name = ride.name;
        t.phone = ride.phone;
        t.carNo = ride.carNo;
        t.carModel = ride.carModel;
        t.dest = ride.dest;
        t.next = top;
        top = t;
    }
public void countCars() {
        int count = 0;
        Node temp = front;

        while (temp != null) {
            
            temp = temp.next;
        }
        count++;
        System.out.println("Total active rides: " + count);
    }
}
