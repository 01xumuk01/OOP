package ru.nsu.oop.graph.model.impl;

import java.util.ArrayList;
import java.util.List;
import ru.nsu.oop.graph.model.AbstractGraph;
import ru.nsu.oop.graph.model.Vertex;

/**
 * Реализация ориентированного графа на основе матрицы инцидентности.
 *
 * @param <T> тип значений вершин графа
 */
public class IncidenceMatrixGraph<T> extends AbstractGraph<T> {
    private final List<List<Integer>> incidenceMatrix;

    /**
     * Создаёт пустой граф на основе матрицы инцидентности.
     */
    public IncidenceMatrixGraph() {
        this.incidenceMatrix = new ArrayList<>();
    }

    @Override
    public void addVertex(Vertex<T> vertex) {
        if (list.contains(vertex)) {
            throw new IllegalArgumentException("Вершина уже присутствует в графе");
        }

        int edgesCount = incidenceMatrix.isEmpty() ? 0 : incidenceMatrix.get(0).size();

        list.add(vertex);

        List<Integer> newRow = new ArrayList<>();
        for (int i = 0; i < edgesCount; i += 1) {
            newRow.add(0);
        }
        incidenceMatrix.add(newRow);
    }

    @Override
    public void removeVertex(Vertex<T> vertex) {
        int index = vertexIndex(vertex);

        int edgesCount = incidenceMatrix.get(index).size();
        for (int col = edgesCount - 1; col >= 0; col -= 1) {
            if (incidenceMatrix.get(index).get(col) != 0) {
                for (List<Integer> row : incidenceMatrix) {
                    row.remove(col);
                }
            }
        }

        incidenceMatrix.remove(index);
        list.remove(vertex);
    }

    @Override
    public void addEdge(Vertex<T> vertexFrom, Vertex<T> vertexTo) {
        int from = vertexIndex(vertexFrom);
        int to = vertexIndex(vertexTo);

        if (findEdgeColumn(from, to) != -1) {
            return;
        }

        for (int i = 0; i < list.size(); i += 1) {
            if (i == from && i == to) {
                incidenceMatrix.get(i).add(2);
            } else if (i == from) {
                incidenceMatrix.get(i).add(-1);
            } else if (i == to) {
                incidenceMatrix.get(i).add(1);
            } else {
                incidenceMatrix.get(i).add(0);
            }
        }
    }

    @Override
    public void removeEdge(Vertex<T> vertexFrom, Vertex<T> vertexTo) {
        int from = vertexIndex(vertexFrom);
        int to = vertexIndex(vertexTo);

        int edgeCol = findEdgeColumn(from, to);
        if (edgeCol != -1) {
            for (List<Integer> row : incidenceMatrix) {
                row.remove(edgeCol);
            }
        }
    }

    @Override
    public List<Vertex<T>> neighbors(Vertex<T> vertex) {
        int index = vertexIndex(vertex);

        final List<Vertex<T>> neighbors = new ArrayList<>();
        List<Integer> row = incidenceMatrix.get(index);

        for (int col = 0; col < row.size(); col += 1) {
            if (row.get(col) == 2) {
                neighbors.add(list.get(index));
            } else if (row.get(col) == -1) {
                for (int i = 0; i < incidenceMatrix.size(); i += 1) {
                    if (incidenceMatrix.get(i).get(col) == 1) {
                        neighbors.add(list.get(i));
                    }
                }
            }
        }

        return neighbors;
    }

    private int findEdgeColumn(int from, int to) {
        int edgesCount = incidenceMatrix.get(from).size();

        for (int col = 0; col < edgesCount; col += 1) {
            if (from == to) {
                if (incidenceMatrix.get(from).get(col) == 2) {
                    return col;
                }
            } else {
                if (incidenceMatrix.get(from).get(col) == -1
                        && incidenceMatrix.get(to).get(col) == 1) {
                    return col;
                }
            }
        }

        return -1;
    }
}