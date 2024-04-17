package com.foody.imageuploadservice.persistence;

import com.foody.imageuploadservice.persistence.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
    List<Image> findByRecipeId(Long recipeId);
    void deleteByRecipeId(Long recipeId);
}
