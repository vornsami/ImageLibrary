/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import dao.DatabaseAccess;
import dao.GlobalTaglist;
import image.ImageCharacter;
import image.ImageScene;
import image.Tag;
import image.TagType;
import image.Taggable;
import image.TaggedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javax.swing.JFileChooser;

/**
 *
 * @author Sami
 */
public class Main extends Application {

    /**
     * @param args the command line arguments
     */
    private Stage stage;
    private TaggedImage currentImage;
    
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage s) throws Exception {
        stage = s;
        Parameters params = this.getParameters();
        List<String> args = params.getUnnamed();
        
        TaggedImage img = null;
        
        if(args != null && !args.isEmpty()){
            img = DatabaseAccess.getDatabase().getImage(args.get(0));
        }
        
        switchToImageScene(img);
        stage.show();
    }
    
    private BorderPane buildEmptyLayout(){
        BorderPane layout = new BorderPane();
        layout.setMinSize(1000, 600);
        
        BorderPane top = new BorderPane();
        layout.setTop(top);
        
        top.setCenter(buildMenuBar());
        top.setLeft(buildSearchBar());
        return layout;
    }
    
    private Background buildBackground(Color col) {
        BackgroundFill bgEdge = new BackgroundFill(col.darker().darker(), new CornerRadii(0), new Insets(-3));
        BackgroundFill bgFrame = new BackgroundFill(col.darker(), new CornerRadii(0), new Insets(-1.5));
        BackgroundFill bgBase = new BackgroundFill(col, new CornerRadii(0), new Insets(0));
        return new Background(bgEdge, bgFrame, bgBase);
    }
    /*
    
    Scene for viewing image
    
    */
    
    /**
     *
     * @param img
     * @return
     */
    public void switchToImageScene(TaggedImage img) {
        currentImage = img;
        if (img != null) {
            try { 
                BorderPane layout = buildImageViewLayout(img);
                Scene scene = new Scene(layout);
                stage.setScene(scene);
                return;
            } catch (Exception e){System.out.println(e);}
        }
        
        stage.setScene(new Scene(this.buildEmptyLayout()));
    }
    
    private void placeImageTagsInContainer(TaggedImage img, VBox sidebar) {
        
        for(ImageCharacter character : img.getCharacters()) {
            VBox tagContainer = this.buildTagContainer();
            
            tagContainer.getChildren().add(new Label(character.getName()));
            
            placeTagsInContainer(character.getTags(), tagContainer);
            sidebar.getChildren().add(tagContainer);
        }
        
        for(ImageScene scene : img.getScenes()) {
            VBox tagContainer = this.buildTagContainer();
            placeTagsInContainer(scene.getTags(), tagContainer);
            sidebar.getChildren().add(tagContainer);
        }
    }
    private void placeTagsInContainer(List<Tag> tags, VBox container, TagOperation... operations){
        tags.forEach(t -> container.getChildren().add(this.buildTagElement(t, operations)));
    }
    
    private void placeEditableTagsInContainer(TaggedImage img, VBox sidebar) {
        
        for(ImageCharacter character : img.getCharacters()) {
            VBox tagContainer = getEditTagsInContainer(character);
            tagContainer.getChildren().add(0, buildNameEdit(character));
            
            sidebar.getChildren().add(tagContainer);
            
        }
        
        for(ImageScene scene : img.getScenes()) {
            VBox tagContainer = getEditTagsInContainer(scene);
            
            sidebar.getChildren().add(tagContainer);
        }
        sidebar.getChildren().addAll(buildAddTaggableButtons(img, sidebar), buildDoneButton(img));
    }
    
    private Button buildDoneButton(TaggedImage img) {
        Button button = new Button("Done");
        
        button.setOnAction(eh -> {
            DatabaseAccess.getDatabase().writeImage(img);
            
            switchToImageScene(img);
        });
        
        return button;
    }
    
    private HBox buildNameEdit(ImageCharacter character) {
        HBox base = new HBox();
        Label nameLabel = new Label(character.getName());
        Button editButton = new Button("Edit");
        
        base.getChildren().addAll(nameLabel, editButton);
        
        editButton.setOnAction(eh -> {characterNameEditButtonFunction(editButton, character, base);});
        
        return base;
    }
    
    private void characterNameEditButtonFunction(Button editButton, ImageCharacter character, HBox base){
        
        TextField field = new TextField();

        editButton.setText("Finish");
        base.getChildren().remove(0);
        base.getChildren().add(0, field);
            
        editButton.setOnAction(eh -> {
            character.setName(field.getCharacters().toString());
            base.getChildren().remove(0);
            base.getChildren().add(0, new Label(character.getName()));
            editButton.setText("Edit");
            editButton.setOnAction(e -> {characterNameEditButtonFunction(editButton, character, base);});
        });
    }
    
    private HBox buildAddTaggableButtons(TaggedImage img, VBox sidebar) {
        HBox base = new HBox();
        
        Button addCharacter = new Button("Add Character");
        Button addScene = new Button("Add Scene");
        
        addCharacter.setOnAction(eh -> {
            ImageCharacter newCharacter = new ImageCharacter("");
            img.getCharacters().add(newCharacter);
            
            VBox tagContainer = getEditTagsInContainer(newCharacter);
            tagContainer.getChildren().add(0, buildNameEdit(newCharacter));
            int numOfElements = sidebar.getChildren().size();
            
            sidebar.getChildren().add(numOfElements - 2, tagContainer);
        });
        
        base.getChildren().addAll(addCharacter, addScene);
        return base;
    }
            
    
    private VBox getEditTagsInContainer(Taggable taggable) {
        VBox tagContainer = this.buildTagContainer();
        taggable.getTags()
                .forEach(t -> tagContainer.getChildren()
                        .add(this.buildTagElement(t, addDeleteToTagElement(taggable, tagContainer)))
                );

        Button editButton = new Button("Add Tag");

        editButton.setOnAction(eh -> {     
            addTagButtonFunction(taggable);
        });
        
        tagContainer.getChildren().add(editButton);
        
        return tagContainer;
    } 
    
    private TagOperation addDeleteToTagElement(Taggable parent, VBox container){
        TagOperation operation = (BorderPane base) -> {
            Button deleteButton = new Button("X");
            base.setRight(deleteButton);
            deleteButton.setOnAction(eh -> {
                Tag tag = getTagFromTagElement(base);
                parent.getTags().remove(tag);
                container.getChildren().remove(base);
            });
        };
        return operation;
    }
    
    private Tag getTagFromTagElement(BorderPane element){
        Label tagLabel = (Label) element.getCenter();
        Tag tag = GlobalTaglist.getTag(tagLabel.getText());
        return tag;
    }
    
    private void addTagButtonFunction(Taggable taggable) {
        Popup popup = this.buildPopupBase();
        
        VBox base = this.buildPopupBackground();
        addTagAddFields(base, popup, taggable);
        
        popup.getContent().add(base);
        
        popup.show(stage);
    }
    
    private Popup buildPopupBase(){
        Popup popup = new Popup();
        
        popup.setAutoHide(true);
        popup.setAutoFix(true);
        popup.setHideOnEscape(true);
        
        return popup;
    }
    private VBox buildPopupBackground(){
        VBox base = new VBox();
        base.setSpacing(20);
        base.setPadding(new Insets(20, 20, 20, 20));
        base.setBackground(buildBackground(Color.LIGHTGRAY));
        return base;
    }
    
    private void addTagAddFields(VBox base, Popup popup, Taggable taggable){
        
        TextField field = buildTagRecommendingSearchBox(350);
        base.getChildren().addAll(field, buildTagAddButtons(taggable, popup, field));
        
    }
    
    private TextField buildTagRecommendingSearchBox(int length) {
        TextField field = new TextField();
        field.setMaxWidth(length);
        field.setMinWidth(length);
        return field;
    }
    
    private HBox buildTagAddButtons(Taggable taggable, Popup popup, TextField field){
        HBox base = new HBox();
        base.setSpacing(100);
        base.setPadding(new Insets(0, 80, 0, 80));
        
        Button back = new Button("Back");
        Button add = new Button("Add");
        
        back.setOnAction(eh -> {
            popup.hide();
        });
        add.setOnAction(eh -> {
            Tag tag = GlobalTaglist.getTag(field.getCharacters().toString());
            
            if(tag != null) {
                taggable.getTags().add(tag);
            }
            popup.hide();
            this.switchToTagAddition(currentImage);
        });
        
        base.getChildren().addAll(back, add);
        return base;
    }
    
    public BorderPane buildImageViewLayout(TaggedImage img){
        BorderPane layout = buildEmptyLayout();
        
        ScrollPane page = buildImagePage(img);
        layout.setCenter(page);
        
        ScrollPane sidebar = buildSideBar(img);
        layout.setLeft(sidebar);
        
        return layout;
    }
    
    private MenuBar buildMenuBar() {
        MenuBar menus = new MenuBar();
        
        menus.getMenus().addAll(buildFileMenu(), buildEditMenu());
        return menus;
    }
    private Menu buildFileMenu() {
        Menu file = new Menu("File");
        
        MenuItem open = new MenuItem("Open File");
        open.addEventHandler(EventType.ROOT, eh -> openFileDialog());
        
        MenuItem exit = new MenuItem("Exit");
        exit.addEventHandler(EventType.ROOT, eh -> System.exit(0));
        
        file.getItems().addAll(open,exit);
        
        return file;
    }
    
    private Menu buildEditMenu() {
        Menu file = new Menu("Edit");
        
        MenuItem open = new MenuItem("Edit Tags");
        
        if(this.currentImage == null){
            open.setDisable(true);
        } else {
            open.addEventHandler(EventType.ROOT, eh -> this.switchToTagAddition(this.currentImage));
        }
        
        MenuItem create = new MenuItem("Manage Tags");
        create.addEventHandler(EventType.ROOT, eh -> this.switchToTagEditing());
        
        file.getItems().addAll(open, create);
        
        return file;
    }
    
    public void openFileDialog(){
        try {
            JFileChooser filechs = new JFileChooser();

            int returnVal = filechs.showOpenDialog(JFileChooser.class.newInstance());

            if(returnVal == JFileChooser.APPROVE_OPTION){
                File file = filechs.getSelectedFile();
                TaggedImage img = DatabaseAccess.getDatabase().getImage(file.getAbsolutePath());
                this.switchToImageScene(img);
            }
        } catch (Exception e){System.out.println(e + " in openFileDialog");}
    }
    
    private ScrollPane buildImagePage(TaggedImage img) {
        ScrollPane pane = new ScrollPane();
        VBox base = new VBox();
        
        pane.setContent(base);
        
        base.getChildren().add(buildImageContainer(img));
        base.getChildren().add(new Label("Recommended:"));
        base.getChildren().add(buildRecommendations(img));
        
        return pane;
    }
    private ScrollPane buildSideBar(TaggedImage img){
        ScrollPane pane = new ScrollPane();
        VBox base = buildTagContainer();
        
        this.placeImageTagsInContainer(img, base);
        
        pane.setContent(base);
        return pane;
    }
    
    private HBox buildSearchBar() {
        HBox base = new HBox();
        
        TextField textfield = buildTagRecommendingSearchBox(250);
        textfield.setPromptText("Search here!");
        
        Button button = new Button("Search");
        
        button.setOnAction(eh -> {
            String text = textfield.getCharacters().toString();
            stage.setScene(this.buildSearchResultScene(text));
        });
        
        base.getChildren().addAll(textfield, button);
        
        return base;
    }
    
    private ImageView buildImageContainer(TaggedImage img){
        ImageView imgvw = new ImageView();
        
        Image image = new Image("file:///" + img.getFilepath());
        imgvw.setImage(image);
        
        // Mikäli haluaa zoomausta tai jotain muuta vastaavaa, se laitetaan tänne
        
        return imgvw;
    }
    
    private VBox buildTagContainer(){
        VBox vbox = new VBox();
        
        // Tag alustan väritys tänne
        
        vbox.setSpacing(5);
        return vbox;
    }
    
    private GridPane buildRecommendations(TaggedImage img){
        List<TaggedImage> recommendations = DatabaseAccess.getDatabase().getRecommendations(img, 36);
        GridPane gp = this.getGridWithContent(recommendations, 6);
        
        return gp;
    }
    
    private ImageView buildThumbnailImage(TaggedImage img){
        
        Image image = new Image("file:///" + img.getFilepath(), 200, 200, true, true);
        ImageView iv = new ImageView(image);
        
        iv.setOnMouseClicked(eh -> {
            this.switchToImageScene(img);
        });
        
        return iv;
    }
    
    
    private BorderPane buildTagElement(Tag tag, TagOperation... operations){
        BorderPane base = new BorderPane();
        
        base.setCenter(new Label(tag.getTagName()));
        base.setBackground(buildBackground(Color.AQUA));
        
        for (TagOperation operation : operations) {
            operation.operate(base);
        }
        return base;
    }
    
    
    
    /*
    
    Scene for search results
    
    */
    
    public Scene buildSearchResultScene(String search){
        currentImage = null;
        String[] terms = search.split(" ");
        
        ScrollPane layout = buildSearchLayout(DatabaseAccess.getDatabase().getWithTagNames(terms));
        Scene scene = new Scene(layout);
        
        return scene;
    }
    
    private ScrollPane buildSearchLayout(List<TaggedImage> results){
        ScrollPane base = new ScrollPane();
        BorderPane page = this.buildEmptyLayout();
         
        page.setCenter(getGridWithContent(results, 6));
        
        base.setContent(page);
        return base;
    }
    
    private GridPane getGridWithContent(List<TaggedImage> results, int width){
        GridPane grid = new GridPane();
        if(width <= 0) return grid;
        for (int i = 0; i < results.size(); i++) {
            for (int a = 0; a < width && i * width + a < results.size(); a++) {
                grid.add(this.buildThumbnailImage(results.get(i * width + a)), a, i);
            }
        }
        
        return grid;
    }
    
    /*
        
        Tag creation
    
    */
    
    public void switchToTagEditing(){
        BorderPane layout = this.buildEmptyLayout();
        
        layout.setCenter(buildEmptyTagEditingLayout());
        
        Scene scene = new Scene(layout);
        this.stage.setScene(scene);
    }
    
    private BorderPane buildEmptyTagEditingLayout(){
        BorderPane base = new BorderPane();
        VBox tagContainer = buildTagEditingSideBar(base);
        
        base.setTop(buildTagEditingTop(base, tagContainer));
        
        ScrollPane sidebar = new ScrollPane();
        sidebar.setContent(tagContainer);
        base.setLeft(sidebar);
        
        return base;
    }
    private BorderPane buildTagEditingTop(BorderPane base, VBox tagContainer){
        BorderPane top = new BorderPane();
        Button addNew = new Button("Add new Tag");
        addNew.setOnAction(eh -> {
            base.setCenter(buildNewTagBase(tagContainer, base));
        });
        top.setLeft(addNew);
        
        Button back = new Button("Return");
        back.setOnAction(eh -> {
            this.switchToImageScene(currentImage);
        });
        top.setRight(back);
        return top;
    }
    
    private VBox buildTagEditingSideBar(BorderPane layout){
        
        
        VBox tagContainer = this.buildTagContainer();
        this.placeTagsInContainer(GlobalTaglist.getTagListUnmodifiable(), tagContainer, giveTagsInContainerFunction(layout));
        
        return tagContainer;
    }
    
    private TagOperation giveTagsInContainerFunction(BorderPane page){
        TagOperation operation = (BorderPane base) -> {
            base.setOnMouseClicked(eh -> { 
                Tag tag = this.getTagFromTagElement(base);
                page.setCenter(buildTagEditBase(tag));
                page.setRight(buildTagUsageSideBar(tag));
            });
        };
        return operation;
    }
    
    private VBox buildNewTagBase(VBox tagContainer, BorderPane layout){
        TextField tagName = new TextField();
        ComboBox type = this.buildTagTypeComboBox(TagType.ID_FEATURE);    
        TextField rating = this.buildNumberOnlyTextField(0);
        layout.setRight(null);
        
        Button save = new Button("Save");
        save.setOnAction(eh -> {
            Tag newTag = new Tag(
                    tagName.getCharacters().toString(), 
                    (TagType) type.getValue(), 
                    Integer.parseInt(rating.getCharacters().toString())
            );
            GlobalTaglist.addTag(newTag);
            tagContainer.getChildren().add(
                    this.buildTagElement(newTag, this.giveTagsInContainerFunction(layout))
            );
            layout.setCenter(this.buildTagEditBase(newTag));
            layout.setRight(this.buildTagUsageSideBar(newTag));
        });
        return new VBox(tagName, type, rating, save);
    }
    
    private VBox buildTagEditBase(Tag tag){
        TextField tagName = new TextField(tag.getTagName());
        ComboBox type = this.buildTagTypeComboBox(tag.getType());    
        TextField rating = this.buildNumberOnlyTextField(tag.getRating());
        
        Button save = new Button("Save");
        save.setOnAction(eh -> {
            Tag newTag = new Tag(
                    tagName.getCharacters().toString(), 
                    (TagType) type.getValue(), 
                    Integer.parseInt(rating.getCharacters().toString())
            );
            GlobalTaglist.editTag(tag, newTag);
            this.switchToTagEditing();
        });
        return new VBox(tagName, type, rating, save);
    }
    
    private TextField buildNumberOnlyTextField(int defaultVal){
        TextField field = new TextField(defaultVal + "");
        field.setTextFormatter(new TextFormatter<String>(t -> {
            if (t.getText().isEmpty() || !t.getText().matches("\\d+")) {
                t.setText("");
            }
            return t;
        }));
        return field;
    }
    
    private VBox buildTagUsageSideBar(Tag tag) {
        VBox base = new VBox();
        Label title = new Label("Images with tag:");
        
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(getGridWithContent(DatabaseAccess.getDatabase().getWithTag(tag), 1));
        
        base.getChildren().addAll(title, scroll);
        
        return base;
    }
    
    private ComboBox buildTagTypeComboBox(TagType type){ 
        ComboBox box = new ComboBox();
        box.setMaxWidth(150);
        box.setMinWidth(150);
        
        box.setItems(
                FXCollections.observableArrayList(
                        Arrays.asList(TagType.values())
                )
        );
        box.setValue(type);
        return box;
    }
    
    /*
        
        Giving image tags
    
    */
    
    public void switchToTagAddition(TaggedImage img){
        currentImage = img;
        
        BorderPane layout = this.buildEmptyLayout();
        layout.setCenter(buildTagEditingPage(img));
        
        Scene scene = new Scene(layout);
        
        this.stage.setScene(scene);
    }
    
    private ScrollPane buildTagEditingPage(TaggedImage img){
        ScrollPane page = new ScrollPane();
        
        BorderPane base = new BorderPane();
        
        VBox tagSets = this.buildTagContainer();
        this.placeEditableTagsInContainer(img, tagSets);
        
        base.setLeft(tagSets);
        
        base.setRight(this.buildImageContainer(img));
        
        page.setContent(base);
        return page;
    }
    
    
}
