package com.hackerNews.rediscachedemo;

import com.hackerNews.rediscachedemo.config.HttpClientConfig;
import com.hackerNews.rediscachedemo.config.RestTemplateConfig;
import com.hackerNews.rediscachedemo.entity.Items;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RestTemplateConfig.class, HttpClientConfig.class })
public class ApplicationTests {

	@Autowired
	RestTemplate restTemplate;

	private HashOperations hashOperations;

	@Test
	public void getTopStories() {
		final String uri = "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";

		String result = restTemplate.getForObject(uri, String.class);
		String[] split = result.substring(1, result.length() - 1).split(",");
		//Stream.of(split).forEach(x-> System.out.println(x));

		System.out.println(split[0].trim());
		final String uri1= "https://hacker-news.firebaseio.com/v0/item/"+split[0].trim()+".json?print=pretty";
		Items storyDetails = restTemplate.getForObject(uri1, Items.class);
		System.out.println(storyDetails.getId());

	}

	@Test
	public void test() {
		ExecutorService executorService = Executors.newFixedThreadPool(25);
		Callable<String> callable = new Callable<String>() {
			@Override
			public String call() throws Exception {
				String uri = "http://localhost:8080/hackerNews/top-Story";
				String result = restTemplate.getForObject(uri, String.class);
				return result;
			}
		};
		for(int i =0;i<25;i++){
			executorService.submit(callable);
		}

	}


}
