package com.mycompany.service.product_review.queue_configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

import com.mycompany.sdk.redis.RedisQueue;
import com.mycompany.service.product_review.queue.Queues;
import com.mycompany.service.product_review.queue.SubmittedQueue;
import com.mycompany.service.product_review.queue_processor.ArchivedQueueProcessor;
import com.mycompany.service.product_review.queue_processor.PublishedQueueProcessor;
import com.mycompany.service.product_review.queue_processor.SubmittedQueueProcessor;

@Configuration
@ComponentScan("com.mycompany")
@PropertySource("classpath:application.properties")
public class QueueConfiguration
{

	//-------------------------------------Redis Config---------------------------------------------------//
	@Bean
	public JedisConnectionFactory jedisConnectionFactory()
	{
		return new JedisConnectionFactory();
	}

	@Bean
	public RedisTemplate<String, Integer> redisTemplate()
	{
		RedisTemplate<String, Integer> template = new RedisTemplate<String, Integer>();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setValueSerializer(new GenericToStringSerializer<Integer>(Integer.class));
		return template;
	}

	//-------------------------------------Submitted Queue------------------------------------------------//
	@Bean
	public MessageListenerAdapter submittedQueueListener()
	{
		return new MessageListenerAdapter(submittedQueueProcessor());
	}

	@Bean
	public MessageListener submittedQueueProcessor()
	{
		return new SubmittedQueueProcessor();
	}

	@Bean
	public ChannelTopic submittedQueueTopic()
	{
		return new ChannelTopic(Queues.QUEUE_SUBMITTED);
	}

	//-------------------------------------Published Queue------------------------------------------------//
	@Bean
	public MessageListenerAdapter publishedQueueListener()
	{
		return new MessageListenerAdapter(publishedQueueProcessor());
	}

	@Bean
	public MessageListener publishedQueueProcessor()
	{
		return new PublishedQueueProcessor();
	}

	@Bean
	public ChannelTopic publishedQueueTopic()
	{
		return new ChannelTopic(Queues.QUEUE_PUBLISHED);
	}

	//-------------------------------------Archived Queue------------------------------------------------//
	@Bean
	public MessageListenerAdapter archivedQueueListener()
	{
		return new MessageListenerAdapter(archivedQueueProcessor());
	}

	@Bean
	public MessageListener archivedQueueProcessor()
	{
		return new ArchivedQueueProcessor();
	}

	@Bean
	public ChannelTopic archivedQueueTopic()
	{
		return new ChannelTopic(Queues.QUEUE_ARCHIVED);
	}
	//---------------------------------------------------------------------------------------------------//
}