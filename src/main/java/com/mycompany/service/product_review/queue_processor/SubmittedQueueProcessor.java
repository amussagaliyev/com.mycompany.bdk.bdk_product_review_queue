package com.mycompany.service.product_review.queue_processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mycompany.model.product_review.ProductReview;
import com.mycompany.model.product_review.ProductReviewDao;
import com.mycompany.model.product_review.ProductReviewStatus;
import com.mycompany.sdk.redis.AbstractRedisQueue;
import com.mycompany.service.product_review.ProductReviewStatusService;

@Service
@Transactional
public class SubmittedQueueProcessor implements MessageListener
{

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ProductReviewStatusService productReviewStatusService;

	@Autowired
	private ProductReviewDao productReviewDao;

	@Autowired
	private AbstractRedisQueue publishedQueue;

	@Autowired
	private AbstractRedisQueue archivedQueue;

	@Override
	public void onMessage(Message message, byte[] pattern)
	{
		String sProductReviewID = new String(message.getBody());
		Integer nProductReviewID = Integer.valueOf(sProductReviewID);
		processProductReview(nProductReviewID);
		
	}

	private void processProductReview(Integer productReviewID)
	{
		ProductReview productReview = productReviewDao.getById(productReviewID);
		
		if (productReview != null)
		{
			try
			{
				ProductReviewStatus newProductReviewStatus = productReviewStatusService.buildProductReviewStatusProcessing("Review is being checked for inappropriate language");
				productReviewStatusService.setCurrentProductReviewStatus(productReview, newProductReviewStatus);
				
				boolean containsInappropriateLanguage = isProductReviewContainsInappropriateLanguage(productReview);
				
				if (containsInappropriateLanguage)
				{
					newProductReviewStatus = productReviewStatusService.buildProductReviewStatusArchived("Review has not been passed inappropriate language check");
				}
				else
				{
					newProductReviewStatus = productReviewStatusService.buildProductReviewStatusPublished("Review has been passed inappropriate language check");
				}
				
				productReviewStatusService.setCurrentProductReviewStatus(productReview, newProductReviewStatus);
				
				if (containsInappropriateLanguage)
				{
					archivedQueue.publish(productReview.getProductReviewID().toString());
					log.info("Review has not passed check. " + productReview.getProductReviewID());
					log.info(productReview.getComments());
				}
				else
				{
					publishedQueue.publish(productReview.getProductReviewID().toString());
					log.info("Review has passed check. " + productReview.getProductReviewID());
				}
			}
			catch (Throwable e)
			{
				log.error("Error while processing review", e);
				ProductReviewStatus newProductReviewStatus = productReviewStatusService.buildProductReviewStatusError("Review has not been processed due to system error.");
				productReviewStatusService.setCurrentProductReviewStatus(productReview, newProductReviewStatus);
			}
			productReviewDao.save(productReview);
		}
	}

	private boolean isCommentContainsInappropriateLanguage(String comments)
	{
		//TODO Hardcoded, but can also be stored and loaded from Db or Redis
		Pattern p = Pattern.compile("(.*\\bfee\\b.*)|(.*\\bnee\\b.*)|(.*\\bcruul\\b.*)|(.*\\bleent\\b.*)");
		Matcher m = p.matcher(comments);
		return m.matches();
	}
	
	private boolean isProductReviewContainsInappropriateLanguage(ProductReview productReview)
	{
		if (productReview != null)
			return isCommentContainsInappropriateLanguage(productReview.getComments());
		else
			return false;
	}
	
}
