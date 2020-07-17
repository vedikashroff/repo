package com.hackerNews.rediscachedemo.service;

import com.hackerNews.rediscachedemo.entity.Comment;
import com.hackerNews.rediscachedemo.entity.Items;
import com.hackerNews.rediscachedemo.entity.User;
import com.hackerNews.rediscachedemo.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class CommentsService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RedisRepository redisRepository;

    @Async
    public List<Comment> getTopComments(Integer storyId) throws ExecutionException, InterruptedException {
        List<Comment> comment = redisRepository.findAll("COMMENT");
        if(CollectionUtils.isEmpty(comment)){
            String itemUri = "https://hacker-news.firebaseio.com/v0/item/" + storyId + ".json?print=pretty";
            Items itemDetails = restTemplate.getForObject(itemUri, Items.class);
            List<Integer> kids = itemDetails.getKids();
            List<CompletableFuture<Comment>> futures = new ArrayList<>();

            for (Integer kid : kids) {
                CompletableFuture<Comment> completableFuture = CompletableFuture.supplyAsync(() -> countOfCommentsForParent(kid));
                futures.add(completableFuture);
            }
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
            CompletableFuture<List<Comment>> allPageContentsFuture = allFutures.thenApply(v -> {
                return futures.stream()
                        .map(pageContentFuture -> pageContentFuture.join())
                        .collect(Collectors.toList());
            });
            comment = allPageContentsFuture.get().stream()
                    .sorted((o1,o2)-> o2.getCommentCount().compareTo(o1.getCommentCount()))
                    .limit(10)
                    .collect(Collectors.toList());

            comment.stream().forEach(x-> {
                if(!StringUtils.isEmpty(x.getUser()))
                    x.setUserHandleAge(getYears(x.getUser()));
                redisRepository.saveWithExpiry("COMMENT",x.getUser(),x);
                System.out.println(x.getCommentCount());
            });
        }
        return comment;
    }

    private Comment countOfCommentsForParent(Integer kid) {
        Queue<Integer> commentQueue = new LinkedList<>();
        int cumulativeKidsCount=0;
        commentQueue.add(kid);
        Comment comment = new Comment();
        while(commentQueue.size()>0){
            Integer topKid = commentQueue.remove();
            String itemUri = "https://hacker-news.firebaseio.com/v0/item/"+topKid+".json?print=pretty";
            Items itemDetails = restTemplate.getForObject(itemUri, Items.class);
            if(itemDetails!=null && itemDetails.getKids()!=null ){
                comment = new Comment(itemDetails.getText()!=null?itemDetails.getText():""
                        ,itemDetails.getBy()!=null?itemDetails.getBy():"");
                for(Integer parentKid: itemDetails.getKids() ){
                    String uri = "https://hacker-news.firebaseio.com/v0/item/"+parentKid+".json?print=pretty";
                    Items forObject = restTemplate.getForObject(uri, Items.class);
                    if(null!=forObject.getKids()){
                        commentQueue.add(parentKid);
                        cumulativeKidsCount++;
                    }else
                        cumulativeKidsCount++;
                }
            }
        }
        comment.setCommentCount(cumulativeKidsCount);
        return comment;
    }

    public String getYears(String userName){
        String uri = "https://hacker-news.firebaseio.com/v0/user/"+userName+".json?print=pretty";
        User user = restTemplate.getForObject(uri, User.class);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        String formattedDtm = Instant.ofEpochSecond(user.getCreated())
                .atZone(ZoneId.of("GMT-4"))
                .format(formatter);
        return formattedDtm;
    }



}
