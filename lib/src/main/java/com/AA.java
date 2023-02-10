package com;

public class AA {

    static class Node {
        Node next;
        int value;
    }

    static Node create(int v) {
        Node n = new Node();
        n.value  = v;
        n.next = null;
        return n;
    }
    public static void main(String[] args) {
        Node root = create(0);
        Node current = root;
        for (int i = 0; i < 10; i++) {
            current.next = create(i + 1);
            current = current.next;
        }
        dumpNode(root);
        Node rev = rever(root);
        dumpNode(rev);
        System.out.println("complete!");
    }
    static void dumpNode(Node root) {
        StringBuilder b = new StringBuilder();
        while (root != null) {
            b.append(root.value).append(",");
            root = root.next;
        }
        System.out.println(b);
    }

    static Node rever(Node root) {
        Node tmp = null;
        Node end = null;
        Node current = root;
        Node after;
        while (current != null) {
            tmp = current;
            after = current.next;
            after.next = tmp;
            current = current.next;
        }
        end = tmp;
        return end;
    }
}
