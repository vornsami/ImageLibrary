/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import image.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Sami
 */
public class InMemoryDatabase implements DatabaseInterface {
    
    public static InMemoryDatabase db;
    
    public static InMemoryDatabase getInstance(){
        if(db == null) {
            db = new InMemoryDatabase();
        }
        return db;
    }
    
    
    List<ImageCharacter> characters;
    List<ImageScene> scenes;
    List<Tag> tags;
    List<TaggedImage> images;
    HashMap<Integer, List<Integer>> tagsToCharacters;
    HashMap<Integer, List<Integer>> tagsToScenes;
    HashMap<Integer, Integer> characterToImage;
    HashMap<Integer, Integer> sceneToImage;
    
    private InMemoryDatabase(){
        characters = new ArrayList<>();
        scenes = new ArrayList<>();
        tags = new ArrayList<>();
        images = new ArrayList<>();
        tagsToCharacters = new HashMap<>();
        tagsToScenes = new HashMap<>();
        characterToImage = new HashMap<>();
        sceneToImage = new HashMap<>();
    }
    
    public void resetDatabase(){
        characters = new ArrayList<>();
        scenes = new ArrayList<>();
        tags = new ArrayList<>();
        images = new ArrayList<>();
        tagsToCharacters = new HashMap<>();
        tagsToScenes = new HashMap<>();
        characterToImage = new HashMap<>();
        sceneToImage = new HashMap<>();
    }
    
    @Override
    public void writeImageCharacter(ImageCharacter c, int imageId) {
        ImageCharacter dbVer = new ImageCharacter(c.getName(), new ArrayList<>());
        characters.add(dbVer);
        int cId = characters.indexOf(dbVer);
        if (cId < 0) {
            throw new UnsupportedOperationException("");
        }
        
        characterToImage.put(cId, imageId);
        c.getTags().stream().map((tag) -> {
            writeTag(tag);
            return tag;
        }).forEachOrdered((tag) -> {
            int i = tags.indexOf(tag);
            tagsToCharacters.putIfAbsent(i, new ArrayList<>());
            tagsToCharacters.get(i).add(cId);
        });
    }
    @Override
    public void writeImageScene(ImageScene s, int imageId) {
        scenes.add(new ImageScene(new ArrayList<>()));
        int sId = scenes.indexOf(s);
        sceneToImage.put(sId, imageId);
        s.getTags().stream().map((tag) -> {
            writeTag(tag);
            return tag;
        }).forEachOrdered((tag) -> {
            int i = tags.indexOf(tag);
            tagsToScenes.putIfAbsent(i, new ArrayList<>());
            tagsToScenes.get(i).add(sId);
        });
    }
    
    
    @Override
    public void writeTag(Tag t) {
        if(tags.stream().filter(tag -> tag.equals(t)).count() == 0) {
            tags.add(new Tag(t.getTagName(), t.getType(), t.getRating()));
        }
    }
    
    @Override
    public void writeImage(TaggedImage img) {
        if(images.stream().filter(i -> i.equals(img)).count() == 0){
            TaggedImage ti = new TaggedImage(img.getFilepath());
            images.add(ti);
        }
        rewriteImageCharacters(img);
        rewriteImageScenes(img);
    }
    
    private void rewriteImageCharacters(TaggedImage img){
        deleteAllImageCharacters(img);
        int i = images.indexOf(img);
        img.getCharacters().forEach((ic) -> {
            writeImageCharacter(ic, i);
        });
    }
    
    private void rewriteImageScenes(TaggedImage img){
        deleteAllImageScenes(img);
        int i = images.indexOf(img);
        img.getScenes().forEach((is) -> {
            writeImageScene(is, i);
        });
    }
    
    @Override
    public void deleteAllImageCharacters(TaggedImage img) {
        int imageId = images.stream().filter(i -> img.equals(i)).mapToInt(i -> images.indexOf(i)).findFirst().getAsInt();
        
        List list = characterToImage.entrySet().stream()
                .filter(p -> p.getValue() == imageId)
                .map(p -> {
                    deleteCharacterTagReferences(p.getKey());
                    characters.remove(p.getKey().intValue());
                    return p;
                })
                .collect(Collectors.toList());
        
        characterToImage.entrySet().removeAll(list);
    }
    
    @Override
    public void deleteCharacterTagReferences(int charId){
        for (int t = 0; t < tags.size(); t++){
            this.tagsToCharacters.putIfAbsent(t, new ArrayList<>());
            if(this.tagsToCharacters.get(t).contains(charId)) {
                tagsToCharacters.get(t).remove(Integer.valueOf(charId));
            }
        }
    }
    
    @Override
    public void deleteAllImageScenes(TaggedImage img) {
        int imageId = images.stream().filter(i -> img.equals(i)).mapToInt(i -> images.indexOf(i)).findFirst().getAsInt();
        
        List list = sceneToImage.entrySet().stream()
                .filter(p -> p.getValue() == imageId)
                .map(p -> {
                    deleteSceneTagReferences(p.getKey());
                    scenes.remove(p.getKey().intValue());
                    return p;
                })
                .collect(Collectors.toList());
        
        sceneToImage.entrySet().removeAll(list);
    }
    
    @Override
    public void deleteSceneTagReferences(int sceneId) {
        for (int t = 0; t < tags.size(); t++){
            this.tagsToScenes.putIfAbsent(t, new ArrayList<>());
            if(this.tagsToScenes.get(t).contains(sceneId)) {
                tagsToScenes.get(t).remove(Integer.valueOf(sceneId));
            }
        }
    }
    
    @Override
    public List<TaggedImage> getAllImages(){
        List<TaggedImage> tagImages = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            TaggedImage img = images.get(i);
            List<Taggable> taggables = listTaggables(i);
            
            tagImages.add(new TaggedImage(img.getFilepath(), taggables));
        };
        return tagImages;
    }
    
    private List<Taggable> listTaggables(int i){
        List<Taggable> taggables = new ArrayList<>();
            
        sceneToImage.entrySet().stream()
                .filter(p -> p.getValue() == i)
                .map(p -> p.getKey())
                .forEach(s -> taggables.add(new ImageScene(listSceneTags(s))));
        
        
        characterToImage.entrySet().stream()
                .filter(p -> p.getValue() == i)
                .map(p -> p.getKey())
                .forEach(c -> taggables.add(new ImageCharacter(characters.get(c).getName(), listCharacterTags(c))));
        
        return taggables;
    }
    private List<Tag> listSceneTags(int sceneId){
        List<Tag> tagList = new ArrayList<>();
        for (int t = 0; t < tags.size(); t++){
            this.tagsToScenes.putIfAbsent(t, new ArrayList<>());
            if(this.tagsToScenes.get(t).contains(sceneId)) {
                tagList.add(tags.get(t));
            }
        }
        return tagList;
    }
    private List<Tag> listCharacterTags(int charId){
        List<Tag> tagList = new ArrayList<>();
        for (int t = 0; t < tags.size(); t++){
            this.tagsToCharacters.putIfAbsent(t, new ArrayList<>());
            if(this.tagsToCharacters.get(t).contains(charId)) {
                tagList.add(tags.get(t));
            }
        }
        return tagList;
    }
    @Override
    public List<Tag> getAllTags(){
        List<Tag> taglist = new ArrayList<>();
        
        tags.forEach((tag) -> {
            taglist.add(new Tag(tag.getTagName(), tag.getType(), tag.getRating()));
        });
        return taglist;
    }

    @Override
    public TaggedImage getImage(String filepath) {
        TaggedImage ti = new TaggedImage(filepath, new ArrayList<>());
        int id = images.indexOf(ti);
        if (id >=0) {
            ti.setTagSets(this.listTaggables(id));
        }
        
        return ti;
    }
    
    @Override
    public List<TaggedImage> getWithTagNames(String... terms) {
        List<Tag> neededTags = new ArrayList<>();
        
        for(String term : terms) {
            neededTags.add(GlobalTaglist.getTag(term));
        }
        return this.getAllImages().stream()
                .filter(p -> p.getAllTags().containsAll(neededTags))
                .collect(Collectors.toList()
                );
        
    }

    @Override
    public List<TaggedImage> getWithTag(Tag tag) {
        
        int tagId = tags.indexOf(tag);
        
        List<Integer> charIds = tagsToCharacters.get(tagId);
        List<Integer> sceneIds = tagsToScenes.get(tagId);
        
        List<Integer> imageIds = new ArrayList<>();
        
        if(charIds != null)charIds.forEach(c -> imageIds.add(characterToImage.get(c)));
        if(sceneIds != null)sceneIds.forEach(s -> imageIds.add(sceneToImage.get(s)));
        
        return imageIds.stream().map(m -> getImage(images.get(m).getFilepath())).collect(Collectors.toList());
    }

    @Override
    public List<TaggedImage> getRecommendations(TaggedImage image, int amount) {
        List<TaggedImage> imgs = this.getAllImages();
        imgs.remove(image);
        imgs.sort(new TaggedImageComparator(image));
        if(imgs.size() < amount || imgs.isEmpty()) return imgs;
        return imgs.subList(0, amount);
        
    }
    
    private class TaggedImageComparator implements Comparator<TaggedImage> {
        private TaggedImage image;
        public TaggedImageComparator(TaggedImage img){
            image = img;
        }
        
        @Override
        public int compare(TaggedImage t, TaggedImage t1) {
            return calculateValue(t) - calculateValue(t1);
        }
        private int calculateValue(TaggedImage t){
            return t.getCharacterMatches(image) * 5 + t.getTagMatches(image);
        }
        
    }
}
