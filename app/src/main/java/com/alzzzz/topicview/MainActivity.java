package com.alzzzz.topicview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.alzzzz.topicview.lib.TopicView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TopicView topicView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        topicView = findViewById(R.id.tv_topic);
        List<String> topics = new ArrayList<>();
        topics.add("#百子湾dsafdsafdsfdsafds#");
        topics.add("#百子湾路广饭盒饭1#");
        topics.add("#百盒饭2#");
        topics.add("#百子湾路广盒饭3#");
        topics.add("#百子湾饭盒饭3#");
        topics.add("#百子湾路盒饭3#");
        topics.add("#百子湾路广饭盒饭3#");
        topics.add("#百子湾饭3#");
        topics.add("#百子饭3#");
        topics.add("#百子湾路广饭盒饭3#");
        topics.add("#百子湾路饭3#");
        topicView.setTopics(topics);
    }
}
