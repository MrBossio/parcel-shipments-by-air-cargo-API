#  parcel-shipments-by-air-cargo-API


![image](https://user-images.githubusercontent.com/88553229/210195328-c20dc441-b41a-41a3-b567-7599e1bf8cc1.png)


### Configure database on local:
#### Download and install:
#### https://dev.mysql.com/downloads/installer/
    1. Install MySQL Server 8.xx - x64
    2. Select Config Type: Developer Computer
        2.1 Port: 3306
        2.2 Pass: Admin

#### https://dev.mysql.com/downloads/workbench/
    1. After install create a connection to Local database:
        1.1 Connection Name: Localhost
        1.2 Hostname: 127.0.0.1
        1.3 Port: 3306
        1.4 Username: root
        1.5 Pass: Admin
        1.6 Inside connection:
            1.6.1 Create new schema: Enter to connection, and clic on 
                    "Create a new schema in the connected server"
            1.6.2 Name: localdb 
    2. In application.properties they are 4 files for config
        You have to edit aplication.yml and application-dev.yml
        3.1 Local database (application-dev.yml)
            url: jdbc:mysql://localhost:3306/localdb?connectionTimeZone=UTC
            username: root
            password: Admin
    3. Remember to activate dev profile in Maven menú.
