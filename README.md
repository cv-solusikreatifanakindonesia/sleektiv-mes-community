# SLEEKTIV (Manufacturing Execution System) Community
# System Requirement 

    1. Openjdk version "1.8.0_442"
    2. Apache Maven 3.6.3
    3. PostgreSQL

# Step by Step 

1. Build each pom folder with command below

mvn clean install -U -X -DskipTests

2. Before you can start application, you have to create database.

= Postgres installation =


Currently we're working on PostgreSQL. For installation manual depending on your platform follow: https://www.postgresql.org/download/.
By default mes will try to connect to database as user: postgres, using password: postgres123 and connecting to database mes. You can change this settings in file mes/mes-application/target/tomcat-archiver/mes-application/sleektiv/db.properties. 

= Configuration preserving=


If you want to make configuration and preserve it between mes-application builds, change file mes/mes-application/conf/tomcat/db.properties. 
If you want to make temporary configuration for current build only, change file mes/mes-application/target/tomcat-archiver/mes-application/sleektiv/db.properties (after first build!).

We no longer provide possibility to create clean database when starting Sleektiv MES Community. Instead, we attach schema dump, which You can find in the following path: 

    /mes/mes-application/src/main/resources/schema/mes_db_en.sql

Before You can run Sleektiv MES Community, all You have to do is to restore this dump using the following command (assuming, You have installed postgreSQL as the user postgres, and created database mes with password postgres123 and appropriate locale):

psql -U postgres mes < path/to/schema/mes_db_en.sql


= Schema version =


Schema is valid for current master branch. If You decide to build Sleektiv MES Community from branch different than current branch (e.g. dev, feature/xyz), You will fail to launch the application. In order to start Sleektiv MES Community built from branch other than current master, change "hibernateHbm2ddlAuto=validate" to "hibernateHbm2ddlAuto=update" in file:

    mes/mes-application/target/tomcat-archiver/mes-application/sleektiv/db.properties (created after command below is being executed)


Be aware - You may be missing some views, menu position etc., but You will be able to run Sleektiv MES Community!

3. Building MES application

Navigate to mes/mes-application and use:

    mvn clean install -Ptomcat -Dprofile=package

4. Start MES

In step III you created complete package with application in directory mes/mes-application/target/tomcat-archiver/mes-application.

To start local instance of MES:

    Navigate to directory with mes package:
    cd mes/mes-application/target/tomcat-archiver/mes-application

    Give execute rights to tomcat scripts:
    chmod a+x bin/*.sh

    Start MES:
    ./bin/setenv.sh
    ./bin/startup.sh

    Shutdown MES: 
    ./bin/shutdown.sh

Access to logs

Logs can be found in directory mes/mes-application/target/tomcat-archiver/mes-application/logs/.

Most of the time you will find information you need in file root.log, for example if your instance started correctly, details of found errors etc.

You should ignore aspects errors and log4j errors from catalina.out - these are just warnings.
Access to local instance of MES

To access started locally instance of MES, go to page (by default):
    http://localhost:8080

To Check Port Used
    sudo netstat -tulnp | grep java

If MES started correctly, you should see login page. Default user: admin, password: admin. Superadmin user: superadmin, password: superadmin (start with this user, and add access roles to groups!).

The procedure is as follows:

    Login as superadmin.
    Go to ‘Administration’ → ‘Groups’.
    Click the right group on a list - the one that is related to the user you want to log in. Next, go there.
    Select all available roles. Save your choice.
    Log out.
    Log in the account again.

If you have any problems with building MES with this guide, feel free to contact us: info@solusikreatifanakindonesia.com.

## Credits:
- [Qcadoo Limited Sp. z o.o.](https://www.qcadoo.com/)
