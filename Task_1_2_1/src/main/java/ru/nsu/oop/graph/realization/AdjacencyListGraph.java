package ru.nsu.oop.graph.realization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import ru.nsu.oop.graph.AbstractGraph;
import ru.nsu.oop.graph.Vertex;

/**
 * Реализация ориентированного графа на основе списка смежности.
 *
 * @param <T> тип значений вершин графа
 */
public class AdjacencyListGraph<T> extends AbstractGraph<T> {
    private final Map<Vertex<T>, Set<Vertex<T>>> adjacencyList;

    /**
     * Создаёт пустой граф на основе списка смежности.
     */
    public AdjacencyListGraph() {
        this.adjacencyList = new HashMap<>();
    }

    @Override
    public void addVertex(Vertex<T> vertex) {
        if (list.contains(vertex)) {
            throw new IllegalArgumentException("Вершина уже присутствует в графе");
        }

        list.add(vertex);
        adjacencyList.put(vertex, new HashSet<>());
    }

    @Override
    public void removeVertex(Vertex<T> vertex) {
        vertexIndex(vertex);

        adjacencyList.remove(vertex);

        for (Set<Vertex<T>> neighbors : adjacencyList.values()) {
            neighbors.remove(vertex);
        }

        list.remove(vertex);
    }

    @Override
    public void addEdge(Vertex<T> vertexFrom, Vertex<T> vertexTo) {
        vertexIndex(vertexFrom);
        vertexIndex(vertexTo);

        adjacencyList.get(vertexFrom).add(vertexTo);
    }

    @Override
    public void removeEdge(Vertex<T> vertexFrom, Vertex<T> vertexTo) {
        vertexIndex(vertexFrom);
        vertexIndex(vertexTo);

        adjacencyList.get(vertexFrom).remove(vertexTo);
    }

    @Override
    public List<Vertex<T>> neighbors(Vertex<T> vertex) {
        vertexIndex(vertex);

        return new ArrayList<>(adjacencyList.get(vertex));
    }
}