module com.worldlearn.backend {
    requires java.sql;
    requires io.javalin;
    exports com.worldlearn.backend.models;
    exports com.worldlearn.backend.services;
}