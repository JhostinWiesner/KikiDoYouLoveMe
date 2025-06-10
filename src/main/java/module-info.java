module org.example.tareaintegradora2apo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens org.example.tareaintegradora2apo.model.incidentes to javafx.base;
    opens org.example.tareaintegradora2apo.model.vehiculos to javafx.base;// Exporta el paquete view
    exports org.example.tareaintegradora2apo;
    exports org.example.tareaintegradora2apo.controller;
    opens org.example.tareaintegradora2apo.controller to javafx.fxml;  // Agrega esta línea
}


