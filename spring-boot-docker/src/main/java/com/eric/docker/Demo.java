/**
 * Copyright 2017 - 2025 Evergrande Group
 */
package com.eric.docker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @Description:
 * @Author: qianliang
 * @Since: 2019-1-24 18:46
 */
public class Demo {
    public static void main(String[] args) {
        ConsumerGroup c1 = new ConsumerGroup("c1");
        ConsumerGroup c2 = new ConsumerGroup("c2");
        ConsumerGroup c3 = new ConsumerGroup("c3");
        List<ConsumerGroup> consumerGroups = Arrays.asList(c1, c2, c3);


        TopicGroup t1 = new TopicGroup("t1");
        TopicGroup t2 = new TopicGroup("t2");
        TopicGroup t3 = new TopicGroup("t3");
        List<TopicGroup> topicGroups = Arrays.asList(t1, t2, t3);


        List<String> consumerGroupList = populateList(consumerGroups);
        System.out.println(consumerGroupList);


        List<String> topicGroupList = populateList(topicGroups);
        System.out.println(topicGroupList);
    }

    //public List<String> populateList(Collection collection) {
    //    ArrayList<String> result = new ArrayList<>();
    //    collection.stream().filter(a -> {
    //       if (a instanceof )
    //    }).map();
    //}

    public static List<String> populateList(Collection collection) {
        ArrayList<String> result = new ArrayList<>();
        collection.stream().forEach(a -> {
            if (a instanceof ConsumerGroup) {
                result.add(((ConsumerGroup) a).getGroupId());
            } else if (a instanceof TopicGroup) {
                result.add(((TopicGroup) a).getTopicName());
            }
        });

        return result;
    }
}
