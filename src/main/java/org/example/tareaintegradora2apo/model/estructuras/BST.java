package org.example.tareaintegradora2apo.model.estructuras;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementación genérica de un Árbol Binario de Búsqueda (BST).
 * @param <T> Tipo de datos que almacenará el árbol
 */
public class BST<T> {

    private Node<T> root;
    private Comparator<T> comparator;

    /**
     * Constructor para la clase BST
     * @param comparator Comparador para ordenar los elementos
     */
    public BST(Comparator<T> comparator) {
        this.root = null;
        this.comparator = comparator;
    }

    /**
     * Inserta un elemento en el árbol
     * @param data Elemento a insertar
     */
    public void insert(T data) {
        root = insertRec(root, data);
    }

    /**
     * Método recursivo para insertar un elemento
     * @param root Nodo raíz actual
     * @param data Elemento a insertar
     * @return Nuevo nodo raíz
     */
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

    /**
     * Busca un elemento en el árbol
     * @param data Elemento a buscar
     * @return true si el elemento existe, false en caso contrario
     */
    public boolean search(T data) {
        return searchRec(root, data);
    }

    /**
     * Método recursivo para buscar un elemento
     * @param root Nodo raíz actual
     * @param data Elemento a buscar
     * @return true si el elemento existe, false en caso contrario
     */
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

    /**
     * Elimina un elemento del árbol
     * @param data Elemento a eliminar
     */
    public void delete(T data) {
        root = deleteRec(root, data);
    }

    /**
     * Método recursivo para eliminar un elemento
     * @param root Nodo raíz actual
     * @param data Elemento a eliminar
     * @return Nuevo nodo raíz
     */
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

    /**
     * Encuentra el valor mínimo en un subárbol
     * @param root Raíz del subárbol
     * @return Valor mínimo
     */
    private T minValue(Node<T> root) {
        T minValue = root.getData();
        while (root.getLeft() != null) {
            minValue = root.getLeft().getData();
            root = root.getLeft();
        }
        return minValue;
    }

    /**
     * Obtiene todos los elementos del árbol en orden
     * @return Lista de elementos ordenados
     */
    public List<T> inOrderTraversal() {
        List<T> result = new ArrayList<>();
        inOrderTraversalRec(root, result);
        return result;
    }

    /**
     * Método recursivo para recorrer el árbol en orden
     * @param root Nodo raíz actual
     * @param result Lista donde se almacenan los resultados
     */
    private void inOrderTraversalRec(Node<T> root, List<T> result) {
        if (root != null) {
            inOrderTraversalRec(root.getLeft(), result);
            result.add(root.getData());
            inOrderTraversalRec(root.getRight(), result);
        }
    }

    /**
     * Verifica si el árbol está vacío
     * @return true si está vacío, false en caso contrario
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Obtiene el elemento con mayor prioridad según el comparador
     * @return Elemento con mayor prioridad o null si el árbol está vacío
     */
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

    /**
     * Obtiene el elemento con menor prioridad según el comparador
     * @return Elemento con menor prioridad o null si el árbol está vacío
     */
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