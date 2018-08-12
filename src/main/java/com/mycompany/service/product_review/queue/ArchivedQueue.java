package com.mycompany.service.product_review.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import com.mycompany.sdk.redis.AbstractRedisQueue;

@Service
public class ArchivedQueue extends AbstractRedisQueue
{
	
	@Autowired
	private ChannelTopic archivedQueueTopic;

	@Override
	public ChannelTopic getChannelTopic()
	{
		return archivedQueueTopic;
	}

}
