package com.marles.horarioappufps.dto.response;

import com.marles.horarioappufps.model.Pensum;
import com.marles.horarioappufps.model.Subject;
import com.marles.horarioappufps.model.SubjectType;
import com.marles.horarioappufps.model.User;
import com.marles.horarioappufps.util.SubjectGraph;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Data
@NoArgsConstructor
public class PensumInfoDto {
    private Long id;
    private String name;
    private int semesters;
    private List<SubjectInfoDto> subjects = new LinkedList<>();

    public PensumInfoDto(Pensum pensum) {
        this.id = pensum.getId();
        this.name = pensum.getName();
        this.semesters = pensum.getSemesters();

        for (Subject subject : pensum.getSubjects()) {
            this.subjects.add(new SubjectInfoDto(subject));
        }
    }

    public PensumInfoDto(Pensum pensum, User user) {
        this.id = pensum.getId();
        this.name = pensum.getName();
        this.semesters = pensum.getSemesters();
        int credits = 0;
        for (Subject subject : pensum.getSubjects()) {
            if (user.containsSubject(subject)) {
                credits += subject.getCredits();
            }
        }

        for (Subject subject : pensum.getSubjects()) {
            this.subjects.add(new SubjectInfoDto(subject, user, credits));
        }
    }

    private static class TopologicalSort {
        SubjectGraph subjectGraph = new SubjectGraph();
        Set<String> visited = new HashSet<>();
        Map<String, SubjectInfoDto> subjectMap = new LinkedHashMap<>();
        Queue<String> order = new LinkedList<>();
        Map<String, Integer> depth = new HashMap<>();

        public void add(List<SubjectInfoDto> subjects) {
            List<SubjectInfoDto> info = subjects.stream().filter(e -> !e.isCompleted() && e.getType() == SubjectType.MANDATORY).toList();
            info.forEach(e -> {
                subjectMap.put(e.getCode(), e);
            });
            for (SubjectInfoDto infoDto : info) {
                for (SubjectItemDto req : infoDto.getRequisites()) {
                    subjectGraph.addEdge(req.getCode(), infoDto.getCode());
                }
            }
        }

        private void startDfs(String e) {
            visited.add(e);
            subjectGraph.getNode(e).forEachNeighbor(subject -> {
                if (!visited.contains(subject.getCode())) {
                    startDfs(subject.getCode());
                }
            });
            order.add(e);
        }

        public void processGraph() {
            for (Map.Entry<String, SubjectInfoDto> entry : subjectMap.entrySet()) {
                if (!visited.contains(entry.getValue().getCode())) {
                    startDfs(entry.getValue().getCode());
                }
            }
        }

        public void markAsCritical() {
            List<String> nodes = new LinkedList<>();
            int best = 0;
            while (!order.isEmpty()) {
                String e = order.remove();
                SubjectGraph.SubjectNode subject = subjectGraph.getNode(e);
                AtomicInteger actAtomic = new AtomicInteger(-1);
                subject.forEachNeighbor(c -> {
                    actAtomic.set(Math.max(actAtomic.get(), depth.get(c.getCode())));
                });
                int act = actAtomic.get() + 1;
                if(act > best){
                    nodes.clear();
                    nodes.add(e);
                    best = act;
                }else if(act == best){
                    nodes.add(e);
                }
                depth.put(e, act);
            }

            visited.clear();
            for(String node : nodes) {
                markAsCritical(node);
            }
        }

        private void markAsCritical(String e){
            visited.add(e);
            subjectMap.get(e).setCritical(true);
            int dep = depth.get(e);
            subjectGraph.getNode(e).forEachNeighbor(subject -> {
                if(!visited.contains(subject.getCode()) && depth.get(subject.getCode()) == dep-1 ){
                    markAsCritical(subject.getCode());
                }
            });
        }

    }

    public void updateIsCritical() {
        TopologicalSort tps = new TopologicalSort();
        tps.add(subjects);
        tps.processGraph();
        tps.markAsCritical();
    }

    public void filterInvalidSubjects() {
        setSubjects(subjects.stream().filter(e -> !e.getGroups().isEmpty()).collect(Collectors.toList()));
    }

    /**
     * Class for sorting the subjects in an order given by a Depth-First Search
     * of the Graph given by reversing the prerequisites of each subject. <br>
     * In this way, the subjects will appear in order of use, thus making it easier
     * for visualizing
     */
    private static class DepthFirstSearch {
        SubjectGraph subjectGraph = new SubjectGraph();
        List<SubjectInfoDto> newOrder = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, SubjectInfoDto> subjectMap = new LinkedHashMap<>();

        private void startDFS(String subject) {
            newOrder.add(subjectMap.get(subject));
            visited.add(subject);
            subjectGraph.getNode(subject).forEachNeighbor(neighbor -> {
                if (!visited.contains(neighbor.getCode())) {
                    startDFS(neighbor.getCode());
                }
            });
        }

        /**
         * Populate the adjacency list of the graph, as well as reversing the edges: <br>
         * Instead of Subject -> Prerequisite, it will save the edges as Prerequisite -> Subject
         *
         * @param subjects The subjects of the Pensum
         */
        private void populateGraph(List<SubjectInfoDto> subjects) {
            subjects.forEach(subject -> {
                subjectMap.put(subject.getCode(), subject);
                for (SubjectItemDto prerequisite : subject.getRequisites()) {
                    subjectGraph.addEdge(prerequisite.getCode(), subject.getCode());
                }
            });
        }

        public void sortByDFS(List<SubjectInfoDto> subjects) {
            for (SubjectInfoDto subject : subjects) {
                if (!visited.contains(subject.getCode())) {
                    startDFS(subject.getCode());
                }
            }
            subjects.clear();
            subjects.addAll(newOrder);
        }
    }

    public void sortByDepth() {
        DepthFirstSearch dfs = new DepthFirstSearch();
        dfs.populateGraph(subjects);
        dfs.sortByDFS(subjects);
    }
}
