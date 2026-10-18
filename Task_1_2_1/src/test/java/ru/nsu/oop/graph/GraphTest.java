package ru.nsu.oop.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.nsu.oop.Main;
import ru.nsu.oop.graph.realization.AdjacencyListGraph;
import ru.nsu.oop.graph.realization.AdjacencyMatrixGraph;
import ru.nsu.oop.graph.realization.IncidenceMatrixGraph;

/**
 * Модульные тесты для всех реализаций графа, вершин, чтения из файла и сортировки.
 */
class GraphTest {

    static Stream<Graph<Integer>> graphProvider() {
        return Stream.of(
                new AdjacencyMatrixGraph<>(),
                new AdjacencyListGraph<>(),
                new IncidenceMatrixGraph<>()
        );
    }

    @Test
    void testVertexAndMain() {
        final Main mainInstance = new Main();
        assertNotEquals(null, mainInstance);
        Main.main(new String[]{});

        final Vertex<Integer> v1 = new Vertex<>("A", 10);
        final Vertex<Integer> v2 = new Vertex<>("A", 99);
        final Vertex<Integer> v3 = new Vertex<>("B", 10);

        assertEquals("A", v1.getName());
        assertEquals(10, v1.getValue());
        assertEquals("A", v1.toString());

        assertEquals(v1, v1);
        assertEquals(v1, v2);
        assertEquals(v1.hashCode(), v2.hashCode());
        assertNotEquals(v1, v3);
        assertFalse(v1.equals(null));
        assertFalse(v1.equals("NotAVertex"));
    }

    @ParameterizedTest
    @MethodSource("graphProvider")
    void testVerticesAndEdgesOperations(Graph<Integer> graph) {
        final Vertex<Integer> a = new Vertex<>("A", 1);
        final Vertex<Integer> b = new Vertex<>("B", 2);
        final Vertex<Integer> c = new Vertex<>("C", 3);
        final Vertex<Integer> missing = new Vertex<>("Z", 99);

        graph.addVertex(a);
        graph.addVertex(b);

        assertThrows(IllegalArgumentException.class, () -> graph.addVertex(a));

        graph.addEdge(a, b);
        graph.addVertex(c);

        graph.addEdge(a, b);
        graph.addEdge(b, b);
        graph.addEdge(b, b);
        graph.addEdge(b, c);

        assertEquals(List.of(a, b, c), graph.vertices());
        assertEquals(List.of(b), graph.neighbors(a));
        assertTrue(graph.neighbors(b).containsAll(List.of(b, c)));

        graph.removeEdge(a, b);
        graph.removeEdge(a, c);
        assertTrue(graph.neighbors(a).isEmpty());

        graph.addEdge(c, a);
        graph.removeVertex(b);
        assertEquals(List.of(a, c), graph.vertices());
        assertEquals(List.of(a), graph.neighbors(c));

        assertThrows(IllegalArgumentException.class, () -> graph.removeVertex(missing));
        assertThrows(IllegalArgumentException.class, () -> graph.addEdge(missing, a));
        assertThrows(IllegalArgumentException.class, () -> graph.addEdge(a, missing));
        assertThrows(IllegalArgumentException.class, () -> graph.removeEdge(missing, a));
        assertThrows(IllegalArgumentException.class, () -> graph.removeEdge(a, missing));
        assertThrows(IllegalArgumentException.class, () -> graph.neighbors(missing));
    }

    @Test
    void testEqualsHashCodeAndToStringAcrossImplementations() {
        final Graph<Integer> matrix = new AdjacencyMatrixGraph<>();
        final Graph<Integer> list = new AdjacencyListGraph<>();
        final Graph<Integer> incidence = new IncidenceMatrixGraph<>();

        final Vertex<Integer> a = new Vertex<>("A", 1);
        final Vertex<Integer> b = new Vertex<>("B", 2);

        for (Graph<Integer> g : List.of(matrix, list, incidence)) {
            g.addVertex(a);
            g.addVertex(b);
            g.addEdge(a, b);
        }

        assertEquals(matrix, matrix);
        assertEquals(matrix, list);
        assertEquals(list, incidence);
        assertEquals(matrix.hashCode(), list.hashCode());
        assertEquals(list.hashCode(), incidence.hashCode());
        assertEquals(matrix.toString(), incidence.toString());

        assertFalse(matrix.equals(null));
        assertFalse(matrix.equals("NotAGraph"));

        incidence.removeEdge(a, b);
        assertNotEquals(matrix, incidence);

        incidence.removeVertex(b);
        assertNotEquals(matrix, incidence);
    }

    @ParameterizedTest
    @MethodSource("graphProvider")
    void testTopologicalSortingSuccessAndCycle(Graph<Integer> graph) {
        final Vertex<Integer> a = new Vertex<>("A", 1);
        final Vertex<Integer> b = new Vertex<>("B", 2);
        final Vertex<Integer> c = new Vertex<>("C", 3);

        graph.addVertex(c);
        graph.addVertex(a);
        graph.addVertex(b);

        graph.addEdge(a, b);
        graph.addEdge(b, c);

        List<Vertex<Integer>> sorted = graph.topologicalSorting();
        assertEquals(List.of(a, b, c), sorted);

        graph.addEdge(c, a);
        assertThrows(IllegalStateException.class, graph::topologicalSorting);
    }

    @ParameterizedTest
    @MethodSource("graphProvider")
    void testReadFileValidAndInvalid(
            Graph<Integer> graph, @TempDir Path tempDir) throws IOException {
        Path validFile = tempDir.resolve("graph.txt");
        Files.writeString(validFile, "3 2\nA 10\nB 20\nC 30\nA B\nB C\n");

        graph.readFile(validFile, Integer::parseInt);
        assertEquals(3, graph.vertices().size());
        assertEquals(List.of(new Vertex<>("B", 20)), graph.neighbors(new Vertex<>("A", 10)));

        Path unknownFromFile = tempDir.resolve("bad_from.txt");
        Files.writeString(unknownFromFile, "1 1\nA 10\nZ A\n");
        Graph<Integer> g2 = new AdjacencyListGraph<>();
        assertThrows(IllegalArgumentException.class,
                () -> g2.readFile(unknownFromFile, Integer::parseInt));

        Path unknownToFile = tempDir.resolve("bad_to.txt");
        Files.writeString(unknownToFile, "1 1\nA 10\nA Z\n");
        Graph<Integer> g3 = new AdjacencyListGraph<>();
        assertThrows(IllegalArgumentException.class,
                () -> g3.readFile(unknownToFile, Integer::parseInt));
    }
}