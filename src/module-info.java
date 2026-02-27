module parking.structure.demo {
    requires javafx.controls;
    requires javafx.graphics;

    exports demo;
    exports psms;
    exports psms.gui;
    exports psms.model;
    exports psms.sensors;
    exports psms.drivers;
    exports psms.display;

    opens psms.gui to javafx.graphics;
}
