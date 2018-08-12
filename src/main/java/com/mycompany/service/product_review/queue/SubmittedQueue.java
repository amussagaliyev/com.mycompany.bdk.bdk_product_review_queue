package com.mycompany.service.product_review.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.mycompany.sdk.redis.AbstractRedisQueue;

@Service
public class SubmittedQueue extends AbstractRedisQueue
{
	
	@Autowired
	private ChannelTopic submittedQueueTopic;

	@Override
	public ChannelTopic getChannelTopic()
	{
		return submittedQueueTopic;
	}

}
