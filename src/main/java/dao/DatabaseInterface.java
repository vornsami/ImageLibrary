/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import image.ImageCharacter;
import image.ImageScene;
import image.Tag;
import image.TaggedImage;
import java.util.List;

/**
 *
 * @author Sami
 */
public interface DatabaseInterface {

    void deleteAllImageCharacters(TaggedImage img);
    
    void deleteCharacterTagReferences(int charId);

    void deleteAllImageScenes(TaggedImage img);

    void deleteSceneTagReferences(int sceneId);
    
    List<TaggedImage> getAllImages();
    
    List<Tag> getAllTags();
    
    TaggedImage getImage(String filepath);
    
    List<TaggedImage> getWithTagNames(String... terms);
    
    List<TaggedImage> getWithTag(Tag tag);

    void writeImage(TaggedImage img);

    void writeImageCharacter(ImageCharacter c, int imageId);

    void writeImageScene(ImageScene s, int imageId);

    void writeTag(Tag t);
    
    List<TaggedImage> getRecommendations(TaggedImage image, int amount);
}
