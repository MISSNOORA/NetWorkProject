/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.libraryreservation;

/**
 *
 * @author gh111
 */

  import java.util.*;

public class Library {
    private String id;
    private String name;
    private Map<String, Topic> topics; 

    public Library(String id, String name) {
        this.id = id;
        this.name = name;
        this.topics = new HashMap<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }

    public void addTopic(Topic topic) {
        topics.put(topic.getId(), topic);
    }

    public Topic getTopicById(String topicId) {
        return topics.get(topicId);
    }

    public Collection<Topic> getAllTopics() {
        return topics.values();
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}