/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Sami
 */
public final class TaggedImage {
    
    String filepath;
    List<ImageCharacter> characters;
    List<ImageScene> scenes;
    
    public TaggedImage(String filepath) {
        this.filepath = filepath;
        this.characters = new ArrayList<>();
        this.scenes = new ArrayList<>();
    }
    
    public TaggedImage(String filepath, List<Taggable> tagSets) {
        this.filepath = filepath;
        this.characters = parseForImageCharacters(tagSets);
        this.scenes = parseForImageScenes(tagSets);
    }
    
    public TaggedImage(String filepath, List<ImageCharacter> ichars, List<ImageScene> iscenes) {
        this.filepath = filepath;
        this.characters = ichars;
        this.scenes = iscenes;
    }
    
    public void setTagSets(List<Taggable> tSet){
        this.characters = parseForImageCharacters(tSet);
        this.scenes = parseForImageScenes(tSet);
    }
    
    public void setTagSets(List<ImageCharacter> ichars, List<ImageScene> iscenes){
        this.characters = ichars;
        this.scenes = iscenes;
    }
    
    public String getFilepath(){
        return filepath;
    }
    public List<ImageCharacter> getCharacters(){
        return characters;
    }     
    
    public List<ImageScene> getScenes(){
        return scenes;
    }     
    public List<Tag> getAllTags(){
        List<Tag> tags = new ArrayList<>();
        
        characters.forEach(t -> tags.addAll(t.getTags()));
        scenes.forEach(t -> tags.addAll(t.getTags()));
        
        Collections.sort(tags);
        
        return tags;
    }
    public List<Taggable> getAllTagSets() {
        List<Taggable> tagsets = new ArrayList<>();
        
        characters.forEach(tagsets::add);
        scenes.forEach(tagsets::add);
        
        Collections.sort(tagsets);
        
        return tagsets;
    }
    
    public int getCharacterMatches(TaggedImage img){
        
        return (int)img.characters.stream().filter(p -> characters.contains(p)).count();
    }
    public int getTagMatches(TaggedImage img){
        List<Tag> tags = this.getAllTags();
        return (int)img.getAllTags().stream().filter(p -> tags.contains(p)).count();
    }
    
    private List<ImageCharacter> parseForImageCharacters(List<Taggable> list){
        return filterListToClass(list, ImageCharacter.class);
    }
    
    private List<ImageScene> parseForImageScenes(List<Taggable> list) {
        return filterListToClass(list, ImageScene.class);
    }
    
    private List filterListToClass(List<Taggable> list, Class c){
        return list.stream().filter(p -> c.isInstance(p)).map(p -> c.cast(p)).collect(Collectors.toList());
    }
    @Override
    public boolean equals(Object o) {
        if(o.getClass() != TaggedImage.class){
            return false;
        }
        return ((TaggedImage) o).filepath.equals(filepath);
    }
}
