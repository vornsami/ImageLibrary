/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image;

import java.util.List;

/**
 *
 * @author Sami
 */
public class ImageCharacter extends Taggable implements Comparable<Taggable> {
    
    String name;
    public ImageCharacter(String name){
        super();
        this.name = name;
    }
    public ImageCharacter(String name, List<Tag> taglist) {
        super(taglist);
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String newName) {
        name = newName;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o.getClass() == ImageCharacter.class) {
            return this.equals((ImageCharacter) o);
        }
        return false;
    }
    
    public boolean equals(ImageCharacter ic) {
        return 
                ((this.name == null || ic.name == null)
                        || this.name.equals(ic.name)
                ) && 
                this.countMatchesInType(ic, TagType.ID_FEATURE) == Math.min(this.getTagCountOfType(TagType.ID_FEATURE), ic.getTagCountOfType(TagType.ID_FEATURE));
    }

    @Override
    public int compareTo(Taggable t) {
        if(t.getClass() == ImageCharacter.class) return compareTo((ImageCharacter) t);
        return 1;
    }
    
    public int compareTo(ImageCharacter t) {
        if((this.name == null && t.name == null) || this.name.equals(t.name)) {
            return this.getTagCount() - t.getTagCount();
        }
        
        if(this.name == null) return -1;
        if(t.name == null) return 1;
        
        return this.name.compareTo(t.name);
    }

    @Override
    protected boolean isAcceptedTagType(TagType type) {
        return type != TagType.LOCATION;
    }
}
