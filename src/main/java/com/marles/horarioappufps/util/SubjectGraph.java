package com.marles.horarioappufps.util;

import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Utility class representing a Directed Graph of subjects
 */
public class SubjectGraph {
    Map<String, SubjectNode> nodes = new HashMap<>();

    private SubjectNode addNode(String code) {
        SubjectNode node = new SubjectNode(code);
        nodes.put(code, node);
        return node;
    }

    public SubjectNode getNode(String code) {
        if(!nodes.containsKey(code)){
            return addNode(code);
        }
        return nodes.get(code);
    }

    /**
     * Adds a directed edge in the graph
     *
     * @param from the code of the subject
     * @param to   the code of the subject
     */
    public void addEdge(String from, String to) {
        SubjectNode subjectNode = nodes.get(from);
        SubjectNode requisiteNode = nodes.get(to);
        if (subjectNode == null) {
            subjectNode = addNode(from);
        }
        if (requisiteNode == null) {
            requisiteNode = addNode(to);
        }

        subjectNode.addEdge(requisiteNode);
    }

    @Getter
    public static class SubjectNode {
        String code;
        List<SubjectNode> neighbors;

        public SubjectNode(String code) {
            this.code = code;
            this.neighbors = new LinkedList<>();
        }

        public void addEdge(SubjectNode node) {
            this.neighbors.add(node);
        }

        public void forEachNeighbor(Consumer<SubjectNode> consumer) {
            if (consumer == null) return;
            for (SubjectNode neighbor : neighbors) {
                consumer.accept(neighbor);
            }
        }
    }
}

