module com.worldlearn.backend {
    requires java.sql;
    requires io.javalin;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    exports com.worldlearn.backend.models;
    exports com.worldlearn.backend.services;
}