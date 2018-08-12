package com.mycompany.service.product_review.queue_processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycompany.model.product_review.ProductReview;
import com.mycompany.model.product_review.ProductReviewDao;

@Service
@Transactional(readOnly=true)
public class PublishedQueueProcessor implements MessageListener
{

	@Autowired
	private ProductReviewDao productReviewDao;

	@Override
	public void onMessage(Message message, byte[] pattern)
	{
		String sProductReviewID = new String(message.getBody());
		Integer nProductReviewID = Integer.valueOf(sProductReviewID);
		
		ProductReview productReview = productReviewDao.getById(nProductReviewID);
		
		String notificationText = "Dear user, we glad to inform that your product review is published now. Related Product: %s, Submit Date: %tc";
		System.out.println(String.format(notificationText, productReview.getProduct().getName(), productReview.getReviewDate()));

	}

}
