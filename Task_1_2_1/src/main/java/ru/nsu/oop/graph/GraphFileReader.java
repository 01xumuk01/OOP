package ru.nsu.oop.graph;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Загрузчик графа из текстового файла фиксированного формата.
 *
 * @param <T> тип значений вершин графа
 */
public class GraphFileReader<T> {

    /**
     * Создаёт экземпляр загрузчика графа из файла.
     */
    public GraphFileReader() {
    }

    /**
     * Читает вершины и рёбра из файла и добавляет их в переданный граф.
     *
     * @param graph       граф для заполнения
     * @param path        путь к текстовому файлу
     * @param valueParser функция разбора значения вершины из строки
     * @throws IOException              при ошибке чтения файла
     * @throws IllegalArgumentException если в описании ребра указана неизвестная вершина
     */
    public void read(Graph<T> graph, Path path,
                     Function<String, T> valueParser) throws IOException {

        try (Scanner scanner = new Scanner(path, StandardCharsets.UTF_8)) {
            int countV = scanner.nextInt();
            int countE = scanner.nextInt();

            Map<String, Vertex<T>> verticesByName = new HashMap<>();

            for (int i = 0; i < countV; i += 1) {
                String name = scanner.next();
                String rawValue = scanner.next();
                T value = valueParser.apply(rawValue);

                Vertex<T> vertex = new Vertex<>(name, value);
                graph.addVertex(vertex);
                verticesByName.put(name, vertex);
            }

            for (int i = 0; i < countE; i += 1) {
                String fromName = scanner.next();
                String toName = scanner.next();

                Vertex<T> from = verticesByName.get(fromName);
                Vertex<T> to = verticesByName.get(toName);

                if (from == null || to == null) {
                    throw new IllegalArgumentException(
                            "В ребре указана неизвестная вершина: " + fromName + " -> " + toName
                    );
                }

                graph.addEdge(from, to);
            }
        }
    }
}