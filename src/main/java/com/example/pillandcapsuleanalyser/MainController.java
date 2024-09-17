package com.example.pillandcapsuleanalyser;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

import static com.example.pillandcapsuleanalyser.BWController.find;

public class MainController {
    @FXML
    ImageView origImgView;
    @FXML
    AnchorPane pane;
    @FXML
    VBox imgVbox;
    @FXML
    Text nameText, numberText, sizeText, totalText, typeText;
    @FXML
    MenuItem opBlackMenuItem;
    @FXML
    ComboBox<String> pillCombo;
    public static Image origImg;
    public static double selectedHue;
    public static double selectedSaturation;
    private int[] disjointSet;
    private ArrayList<Pill> pills = new ArrayList<>();
    private int numPillTypes = -1;
    ArrayList<Integer> allRoots = new ArrayList<>();
    private boolean numsVisible = false;

    @FXML
    void open(){
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Image");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File file = fc.showOpenDialog(new Stage());

        origImg = new Image(file.getAbsolutePath(),origImgView.getFitWidth(),origImgView.getFitHeight(),false,true);
        origImgView.setImage(origImg);
        disjointSet = new int[(int) origImg.getWidth()* (int)origImg.getHeight()];
        opBlackMenuItem.setDisable(false);
    }

    @FXML
    void openBlackView() throws Exception{
        FXMLLoader loader = Application.openView("bc-view.fxml", "Black", true);
        BCController blackController = loader.getController();
        blackController.setMainController(this);
        blackController.setBWImage(origImg);
    }

    public ArrayList<Pill> getAllPills(){
        return pills;
    }

    public int getNumPillTypes(){
        return numPillTypes;
    }

    public void setNumPillTypes(int numPillTypes){
        this.numPillTypes = numPillTypes;
    }

    public boolean isNumsVisible() {
        return numsVisible;
    }

    public void setNumsVisible(boolean b){
        numsVisible=b;
    }

    public int[] getDisjointSet() {
        return disjointSet;
    }

    public int getDSNodeAtIndex(int index){
        return disjointSet[index];
    }

    public void setDisjointSet(int index, int node) {
        if(disjointSet[index] <1){
            disjointSet[index] = node;
        }
    }

    public void resetNode(int index) {
        disjointSet[index] = -1;
    }

    @FXML
    void quit(){
        System.exit(0);
    }

    @FXML
    private void restart() throws Exception{
        Application.openView("main-view.fxml", "Pill and Capsule Analyser", false);
    }

    public void drawRectangle(double x, double y, int width, int height){
        Rectangle r = new Rectangle(x+imgVbox.getLayoutX(), y+imgVbox.getLayoutY(), width, height);
        r.setFill(null);
        r.setStroke(Color.PURPLE);
        r.setStrokeWidth(4);
        pane.getChildren().add(r);
    }
    @FXML
    void imgViewPressed(MouseEvent mouseEvent) throws Exception{
        if (origImg != null) {
            int xPos = (int) (mouseEvent.getX());
            int yPos = (int) (mouseEvent.getY());

            int selectedPixel = (int) ((yPos - 1) * origImg.getWidth() + xPos - 1);
            int root = find(disjointSet, selectedPixel);
            Pill pill = getPillAtRoot(root);

            // if pill exists for selected pixel show details
            if (pill != null) {
                int size = pill.getSize();
                int num = pill.getNum();
                String name = pill.getName();

                sizeText.setText("Estimated Size (pixel units): " + size);
                numberText.setText("Number: " + num);
                nameText.setText("Name: " + name);
            }

            // else open BW view
            else colorSelect(mouseEvent);
        }
    }

    void colorSelect(MouseEvent mouseEvent) throws Exception {
        PixelReader pr = origImg.getPixelReader();

        Color color = pr.getColor((int) mouseEvent.getX(), (int) mouseEvent.getY());
        selectedHue = color.getHue();
        selectedSaturation = color.getSaturation();

        FXMLLoader loader = Application.openView("bw-view.fxml", "Black and white", true);
        BWController bwController = loader.getController();
        bwController.setMainController(this);
        bwController.setBWImage(origImg, 10);
    }

    public Pill getPillAtRoot(int root){
        for (Pill pill : pills) {
            if(pill.getRoot()==root) return pill;
        }
        return null;
    }

    public void addToPillCombo(String name){
        pillCombo.getItems().add(name);
    }

    @FXML
    private void setTypeValue(){
        String type=pillCombo.getValue();
        int counter=0;
        for (Pill pill : pills)
            if(pill.getName().equals(type))
                counter++;
        typeText.setText(String.valueOf(counter));
    }

    void setTotalText(){
        int counter = 0;
        for (Pill pill : pills) counter++;
        totalText.setText("Total: " + counter);
    }

    void addPill(int root, String name, int num, int size, int x, int y){
        if(!allRoots.contains(root)) {
            Pill pill = new Pill(root, name, num, size, x, y, numPillTypes);
            pills.add(pill);
            allRoots.add(root);
        }
    }

    @FXML
    void showPillNums(){
        if(numsVisible) numsVisible=false;
        else numsVisible = true;

        for(Pill pill : pills){
            pane.getChildren().remove(pill.numText);
            if (numsVisible) {
                int x = pill.getXPos();
                int y = pill.getYPos();
                int num = pill.getNum();
                Text t = new Text(x + imgVbox.getLayoutX() + 5, y + imgVbox.getLayoutY() + 15, Integer.toString(num));
                pill.numText = t;
                pane.getChildren().add(t);
            }
        }
    }

    int getDisjointSetSize(int index){
        int counter = 0;
        for(int i=0;i<disjointSet.length;i++)
            if(find(disjointSet,i)==index) counter++;
        return counter;
    }

    // for testing
    public void displayDSAsText(){
        for(int i=0;i<disjointSet.length;i++)
            System.out.print(find(disjointSet,i) + ((i+1)%origImg.getWidth()==0 ? "\n" : " "));
    }
}