package com.workfluffs.shortsai.domain.project.controller;

import com.workfluffs.shortsai.domain.asset.enums.AssetStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/api/v1/sse")
public class VideoSseController {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping(value = "/video-status/{projectId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeVideoStatus(@PathVariable Long projectId) {
        // Timeout: 30 minutes
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);
        emitters.put(projectId, emitter);
        
        emitter.onCompletion(() -> emitters.remove(projectId));
        emitter.onTimeout(() -> emitters.remove(projectId));
        emitter.onError(e -> emitters.remove(projectId));
        
        try {
            emitter.send(SseEmitter.event().name("INIT").data("Connected for project: " + projectId));
        } catch (IOException e) {
            emitters.remove(projectId);
        }
        
        return emitter;
    }
    
    public void notifyStatus(Long projectId, AssetStatus status, String fileUrl) {
        SseEmitter emitter = emitters.get(projectId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("VIDEO_STATUS")
                        .data("{\"status\":\"" + status.name() + "\", \"fileUrl\":\"" + (fileUrl != null ? fileUrl : "") + "\"}"));
                
                if (status == AssetStatus.SUCCESS || status == AssetStatus.FAILED) {
                    emitter.complete();
                    emitters.remove(projectId);
                }
            } catch (IOException e) {
                emitters.remove(projectId);
            }
        }
    }
}
