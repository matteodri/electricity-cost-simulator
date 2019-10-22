module com.matteodri.electricity.cost.simulator {
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires java.sql;
    requires org.apache.logging.log4j;
    opens com.matteodri to spring.core, spring.beans, spring.context;
    opens com.matteodri.services to spring.core, spring.beans, spring.context;
    exports com.matteodri.services;
    exports com.matteodri;
}