/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Sami
 */
public abstract class Taggable implements Comparable<Taggable> {
    List<Tag> TAGS = new ArrayList<>();
    public Taggable(){
        TAGS = new ArrayList<>();
    }
    public Taggable(List<Tag> taglist){
        TAGS = taglist;
    }
    
    abstract protected boolean isAcceptedTagType(TagType type);
    
    public List<Tag> getTags(){
        return TAGS;
    }
    public int getTagCount(){
        return TAGS.size();
    }
    
    public int countMatchesInType(Taggable t, TagType type) {
        
        List<Tag> tTags = t.getTagsOfType(type);
        return (int) getStreamWithCondition(tTags::contains).count();
    }
    
    public int getTagCountOfType(TagType type){
        return (int) getStreamWithCondition(p -> p.type == type).count();
    }
    public List<Tag> getTagsOfType(TagType type) {
        return getStreamWithCondition(p -> p.type == type).collect(Collectors.toList());
    }
    
    private Stream<Tag> getStreamWithCondition(Predicate<Tag> pred){
        return TAGS.stream().filter(pred);
    }

    @Override
    public int compareTo(Taggable t) {
        if(t.getClass() != this.getClass()) return -1;
        
        return this.getTagCount() - t.getTagCount();
    }
}
