package com.janmudra.gmail.operations;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.janmudra.gmail.config.MailConfig;

@Component
public class LabelList {

	private static final Logger logger = LoggerFactory.getLogger(LabelList.class);
	@Autowired
	Gmail gmail;
	
	@Autowired
	MailConfig mailConfig;
	
	
	  /**
	   * List all Messages of the user's mailbox with labelIds applied.
	   *
	   * @param service Authorized Gmail API instance.
	   * @param userId User's email address. The special value "me"
	   * can be used to indicate the authenticated user.
	   * @param labelIds Only return Messages with these labelIds applied.
	   * @throws IOException
	   */
	@Scheduled(fixedRate = 5000)
	  public  List<Message> listMessagesWithLabels() throws IOException {
	    ListMessagesResponse response = gmail.users().messages().list(mailConfig.getUserId())
	        .setQ("label:kuberbot is:unread").execute();

	    List<Message> messages = new ArrayList<Message>();
	    while (response.getMessages() != null) {
	      messages.addAll(response.getMessages());
	      if (response.getNextPageToken() != null) {
	        String pageToken = response.getNextPageToken();
	        response = gmail.users().messages().list(mailConfig.getUserId()).setPageToken(pageToken).execute();

	      } else {
	        break;
	      }
	    }

	    for (Message message : messages) {
	    	Message messageToProcess = gmail.users().messages().get(mailConfig.getUserId(), message.getId()).execute();
	    	Date date = new Date(messageToProcess.getInternalDate());
	        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
	        logger.debug(format.format(date)+ " " + messageToProcess.getSnippet());
	         
	    	String tradeDirection = messageToProcess.getSnippet();
	    	if(tradeDirection.startsWith("BUY"))
	    		tradeDirection = "long";
	    	else
	    		tradeDirection = "short";
	    	
	    	RestTemplate restTemplate = new RestTemplate();
	        String orderType = restTemplate.getForObject("http://localhost:8080/trade/?tradeDirection=" + tradeDirection, String.class);
	        System.out.println("orderType = " + orderType);
	    	modifyMessage(message.getId());
	    	
	    }

	    return messages;
	  }
	
	 public  void modifyMessage(String messageId) throws IOException {
		 ArrayList<String> labelsToRemove = new ArrayList<String>();
		 labelsToRemove.add(("UNREAD"));
		    ModifyMessageRequest mods = new ModifyMessageRequest().setRemoveLabelIds(labelsToRemove);
		    Message message = gmail.users().messages().modify(mailConfig.getUserId(), messageId, mods).execute();

		    System.out.println(" Marked as read Message id: " + message.getId());
		    System.out.println(message.toPrettyString());
		  }


}
