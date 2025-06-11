package org.example.tareaintegradora2apo.model.estructuras;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class BST<T> {

    private Node<T> root;
    private Comparator<T> comparator;


    public BST(Comparator<T> comparator) {
        this.root = null;
        this.comparator = comparator;
    }


    public void insert(T data) {
        root = insertRec(root, data);
    }


    private Node<T> insertRec(Node<T> root, T data) {
        if (root == null) {
            return new Node<>(data);
        }

        int cmp = comparator.compare(data, root.getData());

        if (cmp < 0) {
            root.setLeft(insertRec(root.getLeft(), data));
        } else if (cmp > 0) {
            root.setRight(insertRec(root.getRight(), data));
        }

        return root;
    }


    public boolean search(T data) {
        return searchRec(root, data);
    }


    private boolean searchRec(Node<T> root, T data) {
        if (root == null) {
            return false;
        }

        int cmp = comparator.compare(data, root.getData());

        if (cmp == 0) {
            return true;
        } else if (cmp < 0) {
            return searchRec(root.getLeft(), data);
        } else {
            return searchRec(root.getRight(), data);
        }
    }

    public void delete(T data) {
        root = deleteRec(root, data);
    }


    private Node<T> deleteRec(Node<T> root, T data) {
        if (root == null) {
            return null;
        }

        int cmp = comparator.compare(data, root.getData());

        if (cmp < 0) {
            root.setLeft(deleteRec(root.getLeft(), data));
        } else if (cmp > 0) {
            root.setRight(deleteRec(root.getRight(), data));
        } else {
            // Nodo con un solo hijo o sin hijos
            if (root.getLeft() == null) {
                return root.getRight();
            } else if (root.getRight() == null) {
                return root.getLeft();
            }

            // Nodo con dos hijos: obtener el sucesor inorden (el menor en el subárbol derecho)
            root.setData(minValue(root.getRight()));

            // Eliminar el sucesor inorden
            root.setRight(deleteRec(root.getRight(), root.getData()));
        }

        return root;
    }


    private T minValue(Node<T> root) {
        T minValue = root.getData();
        while (root.getLeft() != null) {
            minValue = root.getLeft().getData();
            root = root.getLeft();
        }
        return minValue;
    }


    public List<T> inOrderTraversal() {
        List<T> result = new ArrayList<>();
        inOrderTraversalRec(root, result);
        return result;
    }


    private void inOrderTraversalRec(Node<T> root, List<T> result) {
        if (root != null) {
            inOrderTraversalRec(root.getLeft(), result);
            result.add(root.getData());
            inOrderTraversalRec(root.getRight(), result);
        }
    }


    public boolean isEmpty() {
        return root == null;
    }


    public T getHighestPriority() {
        if (isEmpty()) {
            return null;
        }

        Node<T> current = root;
        while (current.getRight() != null) {
            current = current.getRight();
        }

        return current.getData();
    }


    public T getLowestPriority() {
        if (isEmpty()) {
            return null;
        }

        Node<T> current = root;
        while (current.getLeft() != null) {
            current = current.getLeft();
        }

        return current.getData();
    }
}