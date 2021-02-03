/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import image.Tag;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sami
 */
public class GlobalTaglist {
    private static List<Tag> tagList;
    
    /** Returns a read-only list of all tags added to the database.
     *
     * @return A read-only version of the list of tags.
     */
    public static List<Tag> getTagListUnmodifiable(){
        if(tagList == null) {
            tagList = new ArrayList<>();
            tagList.addAll(DatabaseAccess.getDatabase().getAllTags());
        }
        
        List<Tag> copy = new ArrayList<>();
        tagList.forEach(c -> copy.add(new Tag(c.getTagName(), c.getType(), c.getRating())));
        
        return copy;
    }
    
    private static List<Tag> getTagList(){
        if(tagList == null) {
            tagList = new ArrayList<>();
            tagList.addAll(DatabaseAccess.getDatabase().getAllTags());
        }
        return tagList;
    }
    
    public static void addTag(Tag tag) {
        getTagList().add(tag);
        DatabaseAccess.getDatabase().writeTag(tag);
    }
    
    public static Tag getTag(String tag) {
        return getTagList().stream()
                .filter(p -> p.getTagName().equalsIgnoreCase(tag))
                .findFirst().orElse(null);
    }
    public static void editTag(Tag original, Tag target) {
        List<Tag> tags = getTagList();
        
        int index = tags.indexOf(original);
        Tag dbVer = tags.get(index);
        
        dbVer.setRating(target.getRating());
        dbVer.setTagName(target.getTagName());
        dbVer.setType(target.getType());
    }
}
