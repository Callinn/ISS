module mpp.iss_iteratia1_ {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.graphics;

    opens mpp.iss_iteratia1_ to javafx.fxml;
    opens mpp.iss_iteratia1_.controller to javafx.fxml; // doar dacă ai controllere acolo

    exports mpp.iss_iteratia1_;
}
