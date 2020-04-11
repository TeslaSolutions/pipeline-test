package com.dream.configs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dream.dtos.DreamDTO;
import com.dream.exceptions.NotFoundException;
import com.dream.mappers.CommentMapper;
import com.dream.mappers.DreamMapper;
import com.dream.models.Comment;
import com.dream.models.Dream;
import com.dream.services.CommentService;
import com.dream.services.DreamService;
import com.dream.utils.ObjectType;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ObjectSSEEmitter{
	
	@Lazy
    @Autowired
    private DreamService dreamService;
	
	@Lazy
	@Autowired
    private CommentService commentService;
	
	private long lastSentObjectID;
	private long lastRun;
	
	@Value("${sse_timeout}")
	private long timeout;
	
	@Value("${sse_interval}")
	private long interval;

	private final List<MySSEEmitter> emitters = new CopyOnWriteArrayList<>();
	List<MySSEEmitter> timeoutedEmitters = new ArrayList<>();
	
	@Scheduled(fixedRate = 10000)
	public void sendTimeoutEventAndClose() {
		emitters.forEach(emitter -> {
			try {
				if(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - emitter.getAge()) > timeout) {
					emitter.send(MySSEEmitter.event().
							name("timeout").				
							data("Timeout occured. Reconnect the client!"));
					timeoutedEmitters.add(emitter);
				}
			} catch (Exception e) {
				timeoutedEmitters.add(emitter);
			}
		});
		emitters.removeAll(timeoutedEmitters);
		log.info("{} clients were diconnected from SSE after reaching timeout limit.", timeoutedEmitters.size());
		timeoutedEmitters.clear();
	}
	
	/**
	 * Prepares the object and sends it over SSE
	 * @param objectId
	 * @param objectType
	 */
	@Async
	public void prepareAndSend(long objectId, String objectType){
		//Get number of seconds since the last run
		long diff = (System.currentTimeMillis() - lastRun) / 1000;
		
		if(diff >= interval && objectId != lastSentObjectID) {
			Optional<Object> o = getObject(objectId, objectType);
			if(o.isPresent()) {
				List<MySSEEmitter> deadEmitters = new ArrayList<>();
				emitters.forEach(emitter -> {
					try {
						emitter.send(MySSEEmitter.event().
								id(String.valueOf(objectId)).
								data(o.get()));
					} catch (Exception e) {
						deadEmitters.add(emitter);
					}
				});
				emitters.removeAll(deadEmitters);
				lastSentObjectID = objectId;
				lastRun = System.currentTimeMillis();
				log.info("Object with ID {} and type of {} sent as last one to {} clients.", objectId, objectType, emitters.size());
			}
		}
	}

	/**
	 * Retrieves an Optional with object of a given type if found with given objectId
	 * @param objectId
	 * @param objectType
	 * @return Optional<Object>
	 */
	private Optional<Object> getObject(long objectId, String objectType){
		Optional<Object> o = Optional.empty();
		try {
			switch (objectType) {
			case ObjectType.DREAM:
				DreamDTO dream = dreamService.getDTOById(objectId);
				o = Optional.of(dream);
				break;
			case ObjectType.COMMENT:
				Comment comment = commentService.getById(objectId);
				o = Optional.of(CommentMapper.toDto(comment));
				break;
			}
		} catch (NotFoundException e) {
			log.info("Object of type {} and ID {} was not found. No object will be sent over SSE!", objectType, objectId);
		}
		return o;
	}
	
	public void addEmitter(final MySSEEmitter emitter) {
		emitters.add(emitter);
	}
	
	public void removeEmitter(final MySSEEmitter emitter) {
		emitters.remove(emitter);
	}

}
