package com.hackerNews.rediscachedemo.controller;

import com.hackerNews.rediscachedemo.entity.Comment;
import com.hackerNews.rediscachedemo.entity.Story;
import com.hackerNews.rediscachedemo.service.CommentsService;
import com.hackerNews.rediscachedemo.service.TopStoriesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/hackerNews")
public class NewsController {


    @Autowired
    private TopStoriesService topStoriesService;

    @Autowired
    private CommentsService commentsService;


    @GetMapping("/top-Story")
    public ResponseEntity<Story> getTopStories() throws Exception {
        List<Story> topStories = topStoriesService.getTopStories();
        if(topStories.isEmpty()){
            throw new Exception("stories not found");
        }
        return new ResponseEntity(topStories,new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/comments/{storyId}")
    public ResponseEntity<Comment> getComment(@PathVariable("storyId") String storyId) throws Exception {
        List<Comment> comments = commentsService.getTopComments(Integer.valueOf(storyId));
        return new ResponseEntity(comments,new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/past-story")
    public ResponseEntity<Story> getPastStories() throws Exception {
        List<Story> pastStories = topStoriesService.getPastStories();
        return new ResponseEntity(pastStories,new HttpHeaders(),HttpStatus.OK);
    }

}
