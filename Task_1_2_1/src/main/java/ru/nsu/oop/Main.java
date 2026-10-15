package ru.nsu.oop;

import ru.nsu.oop.graph.AdjacencyMatrixGraph;
import ru.nsu.oop.graph.Graph;
import ru.nsu.oop.graph.Vertex;

/**
 * Главный класс для демонстрации работы с графом.
 */
public class Main {

    /**
     * Конструктор по умолчанию.
     */
    public Main() {
    }

    /**
     * Точка входа в программу.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        Graph<Integer> graph = new AdjacencyMatrixGraph<>();
        Vertex<Integer> a = new Vertex<>("A", 1);
        Vertex<Integer> b = new Vertex<>("B", 2);
        graph.addVertex(a);
        graph.addVertex(b);
        graph.addEdge(a, b);
        System.out.print(graph);
    }
}