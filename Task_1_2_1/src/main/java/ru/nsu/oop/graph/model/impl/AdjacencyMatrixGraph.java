package ru.nsu.oop.graph.model.impl;

import java.util.ArrayList;
import java.util.List;
import ru.nsu.oop.graph.model.AbstractGraph;
import ru.nsu.oop.graph.model.Vertex;

/**
 * Реализация ориентированного графа на основе матрицы смежности.
 *
 * @param <T> тип значений вершин графа
 */
public class AdjacencyMatrixGraph<T> extends AbstractGraph<T> {
    private final List<List<Integer>> adjacencyMatrix;

    /**
     * Создаёт пустой граф на основе матрицы смежности.
     */
    public AdjacencyMatrixGraph() {
        this.adjacencyMatrix = new ArrayList<>();
    }

    @Override
    public void addVertex(Vertex<T> vertex) {
        if (list.contains(vertex)) {
            throw new IllegalArgumentException("Вершина уже присутствует в графе");
        }

        list.add(vertex);

        for (List<Integer> row : adjacencyMatrix) {
            row.add(0);
        }

        adjacencyMatrix.add(new ArrayList<>());
        for (int i = 0; i < adjacencyMatrix.size(); i += 1) {
            adjacencyMatrix.get(adjacencyMatrix.size() - 1).add(0);
        }
    }

    @Override
    public void removeVertex(Vertex<T> vertex) {
        int index = vertexIndex(vertex);

        for (List<Integer> row : adjacencyMatrix) {
            row.remove(index);
        }

        adjacencyMatrix.remove(index);
        list.remove(vertex);
    }

    @Override
    public void addEdge(Vertex<T> vertexFrom, Vertex<T> vertexTo) {
        int from = vertexIndex(vertexFrom);
        int to = vertexIndex(vertexTo);

        adjacencyMatrix.get(from).set(to, 1);
    }

    @Override
    public void removeEdge(Vertex<T> vertexFrom, Vertex<T> vertexTo) {
        int from = vertexIndex(vertexFrom);
        int to = vertexIndex(vertexTo);

        adjacencyMatrix.get(from).set(to, 0);
    }

    @Override
    public List<Vertex<T>> neighbors(Vertex<T> vertex) {
        int index = vertexIndex(vertex);

        final List<Vertex<T>> neighbors = new ArrayList<>();
        List<Integer> row = adjacencyMatrix.get(index);

        for (int i = 0; i < row.size(); i += 1) {
            if (row.get(i) == 1) {
                neighbors.add(list.get(i));
            }
        }

        return neighbors;
    }
}