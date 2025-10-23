package com.marles.horarioappufps.dto.response;

import com.marles.horarioappufps.model.Pensum;
import com.marles.horarioappufps.model.Subject;
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
        //Adjacency List of Subjects, identified by the code of the Subject
        Map<String, List<String>> adjacencyList = new HashMap<>();
        Map<String, Integer> order = new HashMap<>();

        private void startDFS(String subject) {
            order.put(subject, order.size());
            if(!adjacencyList.containsKey(subject)) {
                //If it's not in the adjacency list, it means it doesn't unlock anything
                return;
            }
            for(String unlockedSubject : adjacencyList.get(subject)) {
                if(!order.containsKey(unlockedSubject)) {
                    startDFS(unlockedSubject);
                }
            }
        }

        /**
         * Populate the adjacency list of the graph, as well as reversing the edges: <br>
         * Instead of Subject -> Prerequisite, it will save the edges as Prerequisite -> Subject
         * @param subjects The subjects of the Pensum
         */
        private void populateAdjacencyList(List<SubjectInfoDto> subjects) {
            subjects.forEach(subject -> {
                for(SubjectItemDto prerequisite : subject.getRequisites()){
                    if(!adjacencyList.containsKey(prerequisite.getCode())) {
                        adjacencyList.put(prerequisite.getCode(), new LinkedList<>());
                    }
                    adjacencyList.get(prerequisite.getCode()).add(subject.getCode());
                }
            });
        }

        public void sortByDFS(List<SubjectInfoDto> subjects) {
            populateAdjacencyList(subjects);
            for(SubjectInfoDto subject : subjects) {
                if(!order.containsKey(subject.getCode())) {
                    startDFS(subject.getCode());
                }
            }
            subjects.sort((a,b) ->
                    order.get(a.getCode()).compareTo(order.get(b.getCode()))
            );
        }
    }

    public void sortByDepth(){
        DepthFirstSearch dfs = new DepthFirstSearch();
        dfs.sortByDFS(subjects);
    }
}
