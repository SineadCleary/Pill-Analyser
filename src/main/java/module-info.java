module com.example.pillandcapsuleanalyser {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.pillandcapsuleanalyser to javafx.fxml;
    exports com.example.pillandcapsuleanalyser;
}