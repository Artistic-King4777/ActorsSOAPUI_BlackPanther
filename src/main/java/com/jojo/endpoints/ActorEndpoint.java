package com.jojo.endpoints;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import com.jojo.actor_ws.ActorInfo;
import com.jojo.actor_ws.AddActorRequest;
import com.jojo.actor_ws.AddActorResponse;
import com.jojo.actor_ws.DeleteActorRequest;
import com.jojo.actor_ws.DeleteActorResponse;
import com.jojo.actor_ws.GetActorByIdRequest;
import com.jojo.actor_ws.GetActorByIdResponse;
import com.jojo.actor_ws.GetAllActorsResponse;
import com.jojo.actor_ws.ServiceStatus;
import com.jojo.actor_ws.UpdateActorRequest;
import com.jojo.actor_ws.UpdateActorResponse;
import com.jojo.entity.Actor;
import com.jojo.service.IActorService;

@Endpoint
public class ActorEndpoint {

	private static final String NAMESPACE_URI = "http://www.jojo.com/actor_ws";

	@Autowired
	private IActorService actorService;
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getActorByIdRequest")
	@ResponsePayload
	public GetActorByIdResponse getActor(@RequestPayload GetActorByIdRequest request) {
		GetActorByIdResponse response = new GetActorByIdResponse();
		ActorInfo ActorInfo = new ActorInfo();
		BeanUtils.copyProperties(actorService.getActorById(request.getActorId()), ActorInfo);
		
		response.setActorInfo(ActorInfo);
		return response;
	} // end
	
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllActorsRequest")
	@ResponsePayload
	public GetAllActorsResponse getAllActors() {
		GetAllActorsResponse response = new GetAllActorsResponse();
		List<ActorInfo> ActorInfoList = new ArrayList<>();
		
		List<Actor> ActorList = actorService.getAllActors();
		
		for (int i = 0; i < ActorList.size(); i++) {
		     ActorInfo ob = new ActorInfo();
		     BeanUtils.copyProperties(ActorList.get(i), ob);
		     ActorInfoList.add(ob);    
		}
		
		response.getActorInfo().addAll(ActorInfoList);
		return response;
	} // end
	
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "addActorRequest")
	@ResponsePayload
	public AddActorResponse addActor(@RequestPayload AddActorRequest request) {
		AddActorResponse response = new AddActorResponse();		
    	ServiceStatus serviceStatus = new ServiceStatus();		
		Actor Actor = new Actor();
		
		Actor.setName(request.getName());
		Actor.setDescription(request.getDescription());		
        boolean flag = actorService.addActor(Actor);
        
        if (flag == false) {
        	serviceStatus.setStatusCode("CONFLICT");
        	serviceStatus.setMessage("Content Already Available");
        	
        	response.setServiceStatus(serviceStatus);
        } else {
        	ActorInfo ActorInfo = new ActorInfo();
        	BeanUtils.copyProperties(Actor, ActorInfo);
        	
        	response.setActorInfo(ActorInfo);
        	serviceStatus.setStatusCode("SUCCESS");
        	serviceStatus.setMessage("Content Added Successfully");
        	response.setServiceStatus(serviceStatus);
        } // end of if else
        
        return response;
	} // end
	
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateActorRequest")
	@ResponsePayload
	public UpdateActorResponse updateActor(@RequestPayload UpdateActorRequest request) {
		Actor Actor = new Actor();
		BeanUtils.copyProperties(request.getActorInfo(), Actor);
		
		actorService.updateActor(Actor);
    	ServiceStatus serviceStatus = new ServiceStatus();
    	
    	serviceStatus.setStatusCode("SUCCESS");
    	serviceStatus.setMessage("Content Updated Successfully");

    	UpdateActorResponse response = new UpdateActorResponse();
    	response.setServiceStatus(serviceStatus);
    	return response;
	} // end
	
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteActorRequest")
	@ResponsePayload
	public DeleteActorResponse deleteActor(@RequestPayload DeleteActorRequest request) {
		Actor actor = actorService.getActorById(request.getActorId());
    	ServiceStatus serviceStatus = new ServiceStatus();
		
    	if (actor == null ) {
	    	serviceStatus.setStatusCode("FAIL");
	    	serviceStatus.setMessage("Content Not Available");
		} else {
			actorService.deleteActor(actor);
	    	serviceStatus.setStatusCode("SUCCESS");
	    	serviceStatus.setMessage("Content Deleted Successfully");
		} // End of if else
    	
    	DeleteActorResponse response = new DeleteActorResponse();
    	
    	response.setServiceStatus(serviceStatus);
		return response;
	} // end
	
}
