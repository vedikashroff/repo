package com.hackerNews.rediscachedemo.service;

import com.hackerNews.rediscachedemo.entity.Items;
import com.hackerNews.rediscachedemo.entity.Story;
import com.hackerNews.rediscachedemo.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TopStoriesService{

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisRepository redisRepository;

    @Async
    public List<Story> getTopStories(){
        System.out.println("calling top");
        List<Story> topStories = redisRepository.findAll("STORIES");
        if(topStories.isEmpty()){
            final String uri = "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";
            String result = restTemplate.getForObject(uri, String.class);
            String[] split = result.substring(1, result.length() - 1).split(",");
            topStories = Stream.of(split).limit(10).map(x -> getStoryDetails(x.trim())).collect(Collectors.toList());
        }
        return topStories;
    }


    public Story getStoryDetails(String id){
        final String uri = "https://hacker-news.firebaseio.com/v0/item/"+id+".json?print=pretty";
        Items storyDetails = restTemplate.getForObject(uri, Items.class);
        Story story = new Story(storyDetails);
        redisRepository.saveWithExpiry("STORIES",story.getStoryId(),story);
        redisRepository.save("PAST_STORIES",story.getStoryId(),story);
        return story;
    }

    public List<Story> getPastStories() throws Exception {
        List<Story> topStories = redisRepository.findAll("PAST_STORIES");
        if(topStories.isEmpty()){
            throw new Exception("Past stories could not be found");
        }
        return topStories;
    }
}
