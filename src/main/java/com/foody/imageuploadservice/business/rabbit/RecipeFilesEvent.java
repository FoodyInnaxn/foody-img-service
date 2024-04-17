package com.foody.imageuploadservice.business.rabbit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecipeFilesEvent {
    private List<String> encodedImages;
    private String folderName;
    private Long recipeId;
}
