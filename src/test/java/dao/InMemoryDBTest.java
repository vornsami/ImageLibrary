/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import image.ImageCharacter;
import image.ImageScene;
import image.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Sami
 */
public class InMemoryDBTest {
    private InMemoryDatabase db;
    
    @Before
    public void init(){
        db = InMemoryDatabase.getInstance();
    }
    
    @Test
    public void testWriteImage(){
        TaggedImage img = new TaggedImage("filepath/write");
        
        db.writeImage(img);
        assertTrue(db.images.contains(img));
    }
    
    @Test
    public void testWriteImageWithContent(){
        db.resetDatabase();
        
        ImageCharacter ic1 = new ImageCharacter("testchar1");
        ImageCharacter ic2 = new ImageCharacter("testchar2");
        
        ImageScene scene = new ImageScene();
        
        
        ic1.getTags().add(new Tag("a", TagType.ID_FEATURE,0));
        ic1.getTags().add(new Tag("b", TagType.NON_ID_FEATURE,0));
        ic2.getTags().add(new Tag("c", TagType.ID_FEATURE,0));
        
        scene.getTags().add(new Tag("d", TagType.LOCATION,0));
        
        List<Taggable> list = new ArrayList<>();
        
        list.add(ic1);
        list.add(ic2);
        list.add(scene);
        
        TaggedImage img = new TaggedImage("filepath/write", list);
        
        db.writeImage(img);
        
        assertTrue(db.images.contains(img));
        assertEquals(2, db.characters.size());
        
        assertTrue(db.characters.get(0).getName().equals("testchar1"));
        assertTrue(db.characters.get(1).getName().equals("testchar2"));
        assertEquals(1, db.scenes.size());
        
        // assertTrue(db.characters.indexOf(ic1) >= 0);
        assertEquals((long)db.characterToImage.get(db.characters.indexOf(ic1)), (long) db.images.indexOf(img));
        assertEquals((long)db.characterToImage.get(db.characters.indexOf(ic2)), (long) db.images.indexOf(img));
        assertEquals((long)db.sceneToImage.get(db.scenes.indexOf(scene)), (long) db.images.indexOf(img));
            
        assertEquals("a", db.tags.get(0).getTagName());
        assertEquals(4, db.tags.size());
        
    }
    
    @Test
    public void readImageWithContent(){
        db.resetDatabase();
        
        ImageCharacter ic1 = new ImageCharacter("testchar1");
        ImageCharacter ic2 = new ImageCharacter("testchar2");
        
        ImageScene scene = new ImageScene();
        
        
        ic1.getTags().add(new Tag("a", TagType.ID_FEATURE,0));
        ic1.getTags().add(new Tag("b", TagType.NON_ID_FEATURE,0));
        ic2.getTags().add(new Tag("c", TagType.ID_FEATURE,0));
        
        scene.getTags().add(new Tag("d", TagType.LOCATION,0));
        
        List<Taggable> list = new ArrayList<>();
        
        list.add(ic1);
        list.add(ic2);
        list.add(scene);
        
        TaggedImage img = new TaggedImage("filepath/write", list);
        
        db.writeImage(img);
        
        List<TaggedImage> images = db.getAllImages();
        
        assertEquals(1, images.size());
        assertEquals("Character count", 2, images.get(0).getCharacters().size());
        assertEquals("Scene count", 1, images.get(0).getScenes().size());
        
        assertEquals("Character 1 tag count",2, images.get(0).getCharacters().get(0).getTags().size());
        assertEquals("Character 2 tag count",1, images.get(0).getCharacters().get(1).getTags().size());
        assertEquals("Scene tag count",1, images.get(0).getScenes().get(0).getTags().size());
    }
    
    
}
