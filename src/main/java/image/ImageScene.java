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
public class ImageScene extends Taggable {
    public ImageScene() {
        super();
    }
    public ImageScene(List<Tag> taglist) {
        super(taglist);
    }

    @Override
    protected boolean isAcceptedTagType(TagType type) {
        return type != TagType.ACTION && type != TagType.ID_FEATURE;
    }
}
