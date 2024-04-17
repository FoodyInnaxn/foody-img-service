package com.foody.imageuploadservice.business.rabbit;

import com.foody.imageuploadservice.persistence.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageEvent {
    private List<String> imagesUrls;
    private Long recipeId;
}
