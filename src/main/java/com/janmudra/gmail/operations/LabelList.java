package com.janmudra.gmail.operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.janmudra.gmail.config.MailConfig;

@Component
public class LabelList {

	@Autowired
	Gmail gmail;
	
	@Autowired
	MailConfig mailConfig;
	@Scheduled(fixedRate = 4000)
	public void listLabels() {

		 // Print the labels in the user's account.
       String user = "me";
       ListLabelsResponse listResponse;
	try {
		listResponse = gmail.users().labels().list(user).execute();
	
       List<Label> labels = listResponse.getLabels();
       if (labels.isEmpty()) {
           System.out.println("No labels found.");
       } else {
           System.out.println("Labels:");
           for (Label label : labels) {
        	   if(label.getName().equalsIgnoreCase(mailConfig.getKuberLabel())) {
        		   
        		   System.out.printf("- %s\n", label.getName());
        		   ArrayList<String> labelIds = new ArrayList<String>();
        		   labelIds.add(label.getId());
        		   listMessagesWithLabels(labelIds);
        	   }
               
           }
       }
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	
	  /**
	   * List all Messages of the user's mailbox with labelIds applied.
	   *
	   * @param service Authorized Gmail API instance.
	   * @param userId User's email address. The special value "me"
	   * can be used to indicate the authenticated user.
	   * @param labelIds Only return Messages with these labelIds applied.
	   * @throws IOException
	   */
	  public  List<Message> listMessagesWithLabels(List<String> labelIds) throws IOException {
//		  System.out.println("**********mailConfig.getUserId() = " + mailConfig.getUserId());
	    ListMessagesResponse response = gmail.users().messages().list(mailConfig.getUserId())
	        .setLabelIds(labelIds).setQ("is:unread").execute();

	    List<Message> messages = new ArrayList<Message>();
	    while (response.getMessages() != null) {
	      messages.addAll(response.getMessages());
	      if (response.getNextPageToken() != null) {
	        String pageToken = response.getNextPageToken();
	        response = gmail.users().messages().list(mailConfig.getUserId()).setLabelIds(labelIds)
	            .setPageToken(pageToken).execute();

	      } else {
	        break;
	      }
	    }

	    for (Message message : messages) {
	    	Message messageToProcess = gmail.users().messages().get(mailConfig.getUserId(), message.getId()).execute();
	      System.out.println(messageToProcess.getInternalDate() + " " + messageToProcess.getSnippet());
//	      messageToProcess.getInternalDate()
	    }

	    return messages;
	  }

}
