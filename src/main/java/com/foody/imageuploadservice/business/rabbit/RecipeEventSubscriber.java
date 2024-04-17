package com.foody.imageuploadservice.business.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RecipeEventSubscriber {
//    @Autowired
//    private RecipeRepository recipeRepository;

//    queues = RabbitMQConfig.SAVED_RECIPE_QUEUE
    @RabbitListener()
    public void receiveMultipartFilesEvent(RecipeFilesEvent multipartFilesEvent) {


        System.out.println("Received event: " + multipartFilesEvent.getRecipeId());
    }
}