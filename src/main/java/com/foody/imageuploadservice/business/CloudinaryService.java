package com.foody.imageuploadservice.business;

import com.foody.imageuploadservice.persistence.entity.Image;
import org.springframework.amqp.core.Message;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CloudinaryService {
//    List<Image> uploadFile(RecipeFilesEvent multipartFilesEvent);
    List<String> uploadFile(List<MultipartFile> multipartFiles, String folderName, Long recipeId);
    void handleMessage(Message message);
    void deleteImages(List<Image> images);
}