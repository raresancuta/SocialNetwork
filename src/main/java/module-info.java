module ubb.scs.map.socialnetwork {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.desktop;


    opens ubb.scs.map.socialnetwork.controller to javafx.fxml;
    opens ubb.scs.map.socialnetwork to javafx.fxml;

    exports ubb.scs.map.socialnetwork;
    exports ubb.scs.map.socialnetwork.domain;
    exports ubb.scs.map.socialnetwork.repository;
    exports ubb.scs.map.socialnetwork.service;
    exports ubb.scs.map.socialnetwork.utils.events;
    exports ubb.scs.map.socialnetwork.utils.observer;
    exports ubb.scs.map.socialnetwork.utils.paging;
}