module com.matteodri.electricity.cost.simulator {
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires java.sql;
    opens com.matteodri to spring.core, spring.beans, spring.context;
    opens com.matteodri.services to spring.core, spring.beans, spring.context;
}