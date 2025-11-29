package com.marles.horarioappufps.dto.response;

import com.marles.horarioappufps.model.Pensum;
import com.marles.horarioappufps.model.Subject;
import com.marles.horarioappufps.model.User;
import com.marles.horarioappufps.util.SubjectGraph;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

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

        for(Subject subject : pensum.getSubjects()) {
            this.subjects.add(new SubjectInfoDto(subject));
        }
    }

    public PensumInfoDto(Pensum pensum, User user) {
        this.id = pensum.getId();
        this.name = pensum.getName();
        this.semesters = pensum.getSemesters();
        int credits = 0;
        for(Subject subject : pensum.getSubjects()) {
            if(user.containsSubject(subject)) {
                credits += subject.getCredits();
            }
        }

        for(Subject subject : pensum.getSubjects()) {
            this.subjects.add(new SubjectInfoDto(subject, user, credits));
        }
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
                if(!visited.contains(neighbor.getCode())){
                    startDFS(neighbor.getCode());
                }
            });
        }

        /**
         * Populate the adjacency list of the graph, as well as reversing the edges: <br>
         * Instead of Subject -> Prerequisite, it will save the edges as Prerequisite -> Subject
         * @param subjects The subjects of the Pensum
         */
        private void populateGraph(List<SubjectInfoDto> subjects) {
            subjects.forEach(subject -> {
                subjectMap.put(subject.getCode(), subject);
                for(SubjectItemDto prerequisite : subject.getRequisites()){
                    subjectGraph.addEdge(prerequisite.getCode(), subject.getCode());
                }
            });
        }

        public void sortByDFS(List<SubjectInfoDto> subjects) {
            for(SubjectInfoDto subject : subjects) {
                if(!visited.contains(subject.getCode())) {
                    startDFS(subject.getCode());
                }
            }
            subjects.clear();
            subjects.addAll(newOrder);
        }
    }

    public void sortByDepth(){
        DepthFirstSearch dfs = new DepthFirstSearch();
        dfs.populateGraph(subjects);
        dfs.sortByDFS(subjects);
    }
}
