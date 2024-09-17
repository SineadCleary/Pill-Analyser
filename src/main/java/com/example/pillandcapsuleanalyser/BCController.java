package com.example.pillandcapsuleanalyser;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Random;

public class BCController {
    @FXML
    ImageView blackImgView;
    MainController mc;
    private int[] disjointSet;
    private WritableImage wi;
    private PixelWriter pw;
    Image origImg;

    public void setMainController(MainController mc){
        this.mc = mc;
        disjointSet = mc.getDisjointSet();
    }

    public void setBWImage(Image origImg){
        this.origImg=origImg;
        wi = new WritableImage((int) origImg.getWidth(), (int) origImg.getHeight());
        pw = wi.getPixelWriter();
        setPillsWhite();
    }

    private void setPillsWhite(){
        int counter=0;
        for (int y=0; y<origImg.getHeight(); y++){
            for (int x=0; x< origImg.getWidth(); x++){
                //if pill
                if(disjointSet[counter]!=-1 && mc.getPillAtRoot(BWController.find(disjointSet,counter))!=null) {
                    pw.setColor(x, y, Color.WHITE);
                }
                //if no pill
                else {
                    pw.setColor(x, y, Color.BLACK);
                }
                counter++;
            }
        }
        blackImgView.setImage(wi);
    }

    @FXML
    private void blackButtonPressed(){
        setPillsWhite();
    }

    @FXML
    private void sampleButtonPressed(){
        //set all black (noise reduction)
        for (int y=0; y<origImg.getHeight(); y++)
            for (int x=0; x< origImg.getWidth(); x++)
                pw.setColor(x,y,Color.BLACK);

        //for each pill type
        for (int i = 0; i <= mc.getNumPillTypes(); i++) {
            int pixel=0;
            int divCounter=0;
            int sampleHue=0;

            //for each pixel
            for (int y=0; y<origImg.getHeight(); y++){
                for (int x=0; x< origImg.getWidth(); x++){
                    Pill pill = mc.getPillAtRoot(BWController.find(disjointSet, pixel));

                    if(pill != null && pill.getType() == i){
                        divCounter++;
                        sampleHue+=origImg.getPixelReader().getColor(x,y).getHue();
                    }
                    pixel++;
                }
            }

            sampleHue = sampleHue / divCounter;
            Color c = Color.hsb(sampleHue, 1.0, 1.0);

            pixel=0;
            //for each pixel
            for (int y=0; y<origImg.getHeight(); y++) {
                for (int x = 0; x < origImg.getWidth(); x++) {
                    Pill pill = mc.getPillAtRoot(BWController.find(disjointSet, pixel));

                    if(pill != null && pill.getType() == i) {
                        pw.setColor(x, y, c);
                    }
                    pixel++;
                }
            }
        }

        blackImgView.setImage(wi);
    }
    @FXML
    private void randButtonPressed() {
        //set all black (noise reduction)
        for (int y = 0; y < origImg.getHeight(); y++)
            for (int x = 0; x < origImg.getWidth(); x++)
                pw.setColor(x, y, Color.BLACK);

        Random rand = new Random();

        //for each pill
        for (Pill pill : mc.getAllPills()) {
            Color c = Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));

            int pixel = 0;
            //for each pixel
            for (int y = 0; y < origImg.getHeight(); y++) {
                for (int x = 0; x < origImg.getWidth(); x++) {
                    if (mc.getPillAtRoot(BWController.find(disjointSet,pixel)) == pill) {
                        pw.setColor(x, y, c);
                    }
                    pixel++;
                }
            }
        }

        blackImgView.setImage(wi);
    }
}
