package com.jonet.eventbooking.components;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

@Component
public class MailExecutor {
	private final ExecutorService executorService = Executors.newFixedThreadPool(10);
	
	public void submitTask(Runnable task) {
        executorService.submit(task);
    }
}
