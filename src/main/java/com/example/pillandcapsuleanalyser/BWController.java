package com.example.pillandcapsuleanalyser;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.nio.file.ClosedFileSystemException;
import java.util.ArrayList;

public class BWController {
    public MainController mainController;
    @FXML
    ImageView bwImgView;
    @FXML
    Slider tSlider;
    @FXML
    TextField nameField;
    @FXML
    Text promptText;
    int[] newDS;
    ArrayList<Integer> rootsToRemove = new ArrayList<Integer>();
    boolean test = true;

    Image origImg = MainController.origImg; //, bwImg = bwImgView.getImage();
    PixelReader origPR = origImg.getPixelReader();   //, bwPR = bwImg.getPixelReader();
    double selectedHue = MainController.selectedHue;

    public void setMainController(MainController mainController){
        this.mainController = mainController;
        newDS = new int[mainController.getDisjointSet().length];
    }

    public void setBWImage(Image origImg, double threshold){
        WritableImage wi = new WritableImage((int) origImg.getWidth(), (int) origImg.getHeight());
        PixelWriter pw = wi.getPixelWriter();

        for (int x=0; x<origImg.getWidth(); x++){
            for (int y=0; y< origImg.getHeight(); y++){
                double hue = origPR.getColor(x,y).getHue();

                //if not matching
                if(Math.abs(hue-selectedHue)<threshold) {
                    pw.setColor(x, y, Color.WHITE);
                }

                //if matching
                else {
                    pw.setColor(x, y, Color.BLACK);
                }
            }
        }
        bwImgView.setImage(wi);
        //displayDSAsText();
    }

    void setDSNums(){
        for (int x=0; x<origImg.getWidth(); x++){
            for (int y=0; y< origImg.getHeight(); y++){

                //if white
                if(bwImgView.getImage().getPixelReader().getColor(x,y).equals(Color.WHITE)) {
                    //set overall ds
                    mainController.setDisjointSet((int) (y*origImg.getWidth()+x), (int) (y*origImg.getWidth()+x));
                    //set this ds
                    newDS[(int) (y*origImg.getWidth()+x)] = (int) (y*origImg.getWidth()+x);
                }

                //if black
                else {
                    //set overall ds
                    mainController.setDisjointSet((int) (y*origImg.getWidth()+x), -1);
                    //set this ds
                    newDS[(int) (y*origImg.getWidth()+x)] = -1;
                }
            }
        }
    }

    @FXML
    void submitButtonPressed(){
        if(nameField.getText().equals("")) {
            promptText.setVisible(true);
            return;
        }
        mainController.setNumPillTypes(mainController.getNumPillTypes()+1);
        setDSNums();
        groupSets();
        int[] ds = mainController.getDisjointSet();
        ArrayList<Integer> allRoots=new ArrayList<>();
        for(int i = 0; i < ds.length; i++)
            if (find(ds, i) != -1 && !allRoots.contains(find(ds, i)))
                allRoots.add(find(ds, i));

        for(int root : allRoots) {
            // if root doesn't already have assigned pill
            if(mainController.getPillAtRoot(root)==null) {
                setPixelBoundary(root);
            }
        }

        mainController.addToPillCombo(nameField.getText());
        mainController.setTotalText();

        // close window
        Stage stage = (Stage) tSlider.getScene().getWindow();
        stage.close();
    }

    void groupSets() {
        for (int i = 0; i < mainController.getDisjointSet().length - 1; i++) {
            if (newDS[i] != -1) {
                // right
                if (newDS[i+1] != -1)
                    union(mainController.getDisjointSet(), i, i + 1);

                // down
                if (i + (int) (bwImgView.getImage().getWidth()) < mainController.getDisjointSet().length && newDS[i + (int) (bwImgView.getImage().getWidth())]!=-1)
                    union(mainController.getDisjointSet(), i, (int) (i + bwImgView.getImage().getWidth()));
            }
        }

    }

    // Calculates shape position and boundaries and draws a rectangle around pill
    void setPixelBoundary(int root){
        int recWidth = 0;
        int recHeight = 0;
        int xPos = 0;
        int yPos = 0;

        if (getDisjointSetSize(root) > 100) {
            boolean xFound = false;
            boolean yFound = false;

            //set Width and x Pos
            for (int i = 0; i < origImg.getWidth(); i++) {
                boolean nextCol = false;
                int j = 0;

                while (!nextCol) {
                    if((i+j)>=mainController.getDisjointSet().length) nextCol = true;
                    else if (find(mainController.getDisjointSet(), i + j) == root) {
                        if(!xFound) {
                            xPos = i;
                            xFound = true;
                        }
                        recWidth++;
                        nextCol = true;
                    }
                    else j += origImg.getWidth();
                }
            }
            //set Height and y Pos
            for (int i = 0; i < mainController.getDisjointSet().length; i+= origImg.getWidth()) {
                boolean nextRow = false;
                int j = 0;

                while (!nextRow) {
                    if((i+j)>=mainController.getDisjointSet().length) nextRow = true;
                    else if (j >= origImg.getWidth()) nextRow = true;
                    else if (find(mainController.getDisjointSet(), i + j) == root) {
                        if(!yFound) {
                            yPos = i/(int)(origImg.getWidth());
                            yFound = true;
                        }
                        recHeight++;
                        nextRow = true;
                    }
                    else j++;
                }
            }

            mainController.drawRectangle(xPos, yPos, recWidth, recHeight);
            if(mainController.isNumsVisible()){
                mainController.setNumsVisible(false);
                mainController.showPillNums();
            }
            createPillObj(root, xPos, yPos);
        }
    }

    //creates and returns Pill object
    void createPillObj(int root, int x, int y){
        //size
        int size = getDisjointSetSize(root);

        //name
        String name;
        if(nameField.getText().equals("")) name = "unnamed";
        else name = nameField.getText();

        mainController.addPill(root, name, 0, size, x, y);
        orderAllPills(root);    //sets num
    }

    // orders all pills and sets their numbers
    void orderAllPills(int currentRoot){
        int[] ds = mainController.getDisjointSet();

        // ArrayList of all roots
        ArrayList<Integer> allRoots=new ArrayList<>();
        for(int i = 0; i < ds.length; i++)
            if (find(ds, i) != -1 && !allRoots.contains(find(ds, i)))
                allRoots.add(find(ds, i));

        int counter = 1;
        for(int root : allRoots) {
            if(mainController.getPillAtRoot(root)!=null) {
                Pill pill = mainController.getPillAtRoot(root);
                pill.setNum(counter);
                counter++;
            }
        }
    }

    int getDisjointSetSize(int index){
        int counter = 0;
        for(int i=0;i<mainController.getDisjointSet().length;i++)
            if(find(mainController.getDisjointSet(),i)==index) counter++;
        return counter;
    }

    public static void union(int[] ds, int p, int q) {
        ds[find(ds,q)]=find(ds,p); //The root of q is made reference the root of p
    }

    public static int find(int[] ds, int id){
        if (id!=-1) {
            if (ds[id] == -1) return -1;
            //returns the root id
            while (ds[id] != id) id = ds[id];
        }
        return id;
    }
    @FXML
    void setHueThreshold() {
        setBWImage(origImg, tSlider.getValue());
    }

    // for testing
    public void displayDSAsText(){
        for(int i=0;i<mainController.getDisjointSet().length;i++)
            System.out.print(find(mainController.getDisjointSet(),i) + ((i+1)%origImg.getWidth()==0 ? "\n" : " "));
    }
}
