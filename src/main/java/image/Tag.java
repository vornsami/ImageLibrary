/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image;

import java.util.Objects;

/**
 *
 * @author Sami
 */
public class Tag implements Comparable<Tag> {
    TagType type;
    String tagName;
    int rating;
    
    public Tag(String tag, TagType type, int rating) {
        this.tagName = tag;
        this.type = type;
        this.rating = rating;
    }
    public Tag(String tag, TagType type) {
        this.tagName = tag;
        this.type = type;
        this.rating = 0;
    }
    public String getTagName(){
        return tagName;
    }
    public TagType getType(){
        return type;
    }
    public int getRating(){
        return rating;
    }
    public void setTagName(String n) {
        tagName = n;
    }
    public void setRating(int i) {
        rating = i;
    }
    public void setType(TagType t){
        type = t;
    }
    
    @Override
    public boolean equals(Object o){
        if(o.getClass() != this.getClass()){
            return false;
        }
        return ((Tag) o).tagName.equals(tagName) && ((Tag) o).type == type && ((Tag) o).rating == rating;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.type);
        hash = 43 * hash + Objects.hashCode(this.tagName);
        hash = 43 * hash + this.rating;
        return hash;
    }
    
    @Override
    public int compareTo(Tag t) {
        return this.tagName.compareTo(t.tagName);
    }
}
