package ru.nsu.oop.graph.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

/**
 * Интерфейс ориентированного графа.
 *
 * @param <T> тип значения, хранящегося в вершинах графа
 */
public interface Graph<T> {

    /**
     * Добавляет новую вершину в граф.
     *
     * @param vertex добавляемая вершина
     * @throws IllegalArgumentException если вершина уже присутствует в графе
     */
    void addVertex(Vertex<T> vertex);

    /**
     * Удаляет вершину и все связанные с ней рёбра из графа.
     *
     * @param vertex удаляемая вершина
     * @throws IllegalArgumentException если вершина отсутствует в графе
     */
    void removeVertex(Vertex<T> vertex);

    /**
     * Добавляет ориентированное ребро между двумя вершинами.
     *
     * @param vertexFrom вершина-источник
     * @param vertexTo   вершина-назначение
     * @throws IllegalArgumentException если хотя бы одна из вершин отсутствует в графе
     */
    void addEdge(Vertex<T> vertexFrom, Vertex<T> vertexTo);

    /**
     * Удаляет ориентированное ребро между двумя вершинами.
     *
     * @param vertexFrom вершина-источник
     * @param vertexTo   вершина-назначение
     * @throws IllegalArgumentException если хотя бы одна из вершин отсутствует в графе
     */
    void removeEdge(Vertex<T> vertexFrom, Vertex<T> vertexTo);

    /**
     * Возвращает список всех исходящих соседей указанной вершины.
     *
     * @param vertex вершина графа
     * @return список соседних вершин
     * @throws IllegalArgumentException если вершина отсутствует в графе
     */
    List<Vertex<T>> neighbors(Vertex<T> vertex);

    /**
     * Считывает граф из файла фиксированного формата.
     *
     * @param path        путь к файлу
     * @param valueParser функция преобразования строкового значения вершины в тип {@code T}
     * @throws IOException если произошла ошибка чтения файла
     */
    void readFile(Path path, Function<String, T> valueParser) throws IOException;

    /**
     * Возвращает неизменяемый список всех вершин графа.
     *
     * @return список вершин
     */
    List<Vertex<T>> vertices();

    /**
     * Выполняет топологическую сортировку вершин графа.
     *
     * @return отсортированный список вершин
     * @throws IllegalStateException если граф содержит цикл
     */
    List<Vertex<T>> topologicalSorting();
}