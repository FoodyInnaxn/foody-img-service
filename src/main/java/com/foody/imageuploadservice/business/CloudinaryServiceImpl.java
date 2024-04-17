package com.foody.imageuploadservice.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foody.imageuploadservice.business.rabbit.ImageEvent;
import com.foody.imageuploadservice.business.rabbit.ImageEventPublisher;
import com.foody.imageuploadservice.business.rabbit.RecipeFilesEvent;
import com.foody.imageuploadservice.configuration.RabbitMQConfig;
import com.foody.imageuploadservice.persistence.ImageRepository;
import com.foody.imageuploadservice.persistence.entity.Image;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@AllArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService{
    @Resource
    private Cloudinary cloudinary;
    private final ObjectMapper objectMapper;
    private final ImageEventPublisher imageEventPublisher;
    private final ImageRepository imageRepository;


    @Override
    @RabbitListener(queues = RabbitMQConfig.RECIPE_QUEUE)
    public void handleMessage(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        ImageEvent imageEvent = new ImageEvent();

        if (routingKey.equals(RabbitMQConfig.RECIPE_ROUTING_KEY_CREATE)) {
            RecipeFilesEvent recipeFilesEvent;
            try {
                recipeFilesEvent = objectMapper.readValue(message.getBody(), RecipeFilesEvent.class);

                // Convert Base64-encoded images to MultipartFile objects
                List<MultipartFile> multipartFiles = ImageUtil.convertBase64ToMultipartFile(recipeFilesEvent.getEncodedImages());

                // Upload MultipartFile objects and return urls with publicId
                List<String> imageUrls = uploadFile(multipartFiles, recipeFilesEvent.getFolderName(), recipeFilesEvent.getRecipeId());

                imageEvent.setRecipeId(recipeFilesEvent.getRecipeId());
                imageEvent.setImagesUrls(imageUrls);

                // sending to recipe server
                imageEventPublisher.publishImagesEvent(imageEvent);
                System.out.println("hey sending to recipe " + imageEvent);

                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        if (routingKey.equals(RabbitMQConfig.RECIPE_ROUTING_KEY_UPDATE)) {
            RecipeFilesEvent recipeFilesEvent;
            try {
                recipeFilesEvent = objectMapper.readValue(message.getBody(), RecipeFilesEvent.class);

                // delete the old images with recipe id
                List<Image> imgs = imageRepository.findByRecipeId(recipeFilesEvent.getRecipeId());

                if(!imgs.isEmpty()){
                    this.deleteImages(imgs);
                }

                // Convert Base64-encoded images to MultipartFile objects
                List<MultipartFile> multipartFiles = ImageUtil.convertBase64ToMultipartFile(recipeFilesEvent.getEncodedImages());

                // Upload MultipartFile objects and return urls with publicId
                List<String> imageUrls = uploadFile(multipartFiles, recipeFilesEvent.getFolderName(), recipeFilesEvent.getRecipeId());

                imageEvent.setRecipeId(recipeFilesEvent.getRecipeId());
                imageEvent.setImagesUrls(imageUrls);

                // sending to recipe server
                imageEventPublisher.publishImagesEvent(imageEvent);
                System.out.println("hey sending to recipe " + imageEvent);

                return;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<String> uploadFile(List<MultipartFile> multipartFiles, String folderName, Long recipeId) {
        List<String> imageUrls = new ArrayList<>();
        try {
            HashMap<Object, Object> options = new HashMap<>();
            options.put("folder", folderName);

            for (MultipartFile file : multipartFiles) {
                Map uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);
                String publicId = (String) uploadedFile.get("public_id");
                String url = cloudinary.url().secure(true).generate(publicId);

                Image image = new Image();
                image.setPublicId(publicId);
                image.setUrl(url);
                image.setRecipeId(recipeId);
                imageRepository.save(image);
                imageUrls.add(image.getUrl());
            }
            return imageUrls;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void deleteImages(List<Image> images){
        for (Image img : images) {
            try {
                cloudinary.uploader().destroy(img.getPublicId(), ObjectUtils.asMap("resource_type","image"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RabbitListener(queues = RabbitMQConfig.FANOUT_IMG_QUEUE)
    @Transactional
    public void handleDelete(Long recipeId) {
        List<Image> images = imageRepository.findByRecipeId(recipeId);
        if(!images.isEmpty()){
            this.deleteImages(images);
            imageRepository.deleteByRecipeId(recipeId);
        }
    }
}
