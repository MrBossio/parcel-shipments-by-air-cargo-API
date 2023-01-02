#  parcel-shipments-by-air-cargo-API

### Project specification:
    - Language: Java v17
    - Framework: Springboot
    - Database: MySQL v8

## The Problem:
A company is dedicated to sending packages between the main cities of America and Europe.
The company requires a computer tool that allows two main needs to be met: the reception of packages and the determination of the best route to follow.
For this, a simulation is carried out, whose input data are:

1. A shipment list for 10 months.
2. A flight schedule.
3. A list of 40 airports.

The shipment load will increase as the months progress, so that at some point the capacity of flights and airports will collapse:
1. The maximum capacity for continental flights varies between 200 and 300 packages depending on the flight.
2. The maximum capacity for intercontinental flights varies between 250 and 400 packages depending on the flight.
3. Storage capacity at each airport varies between 600 and 1,000 packages, depending on the airport. There is one airport per city.

The purpose of this API is to determine the most efficient algorithm for a Product Shipping Management System, seeking to avoid logistics collapse in the longest possible time.
Logistics collapse occurs for two reasons:
1. No route found for a shipment or part of a shipment.
2. An airport exceeds its capacity.

### Solution:

For the elaboration of the software solution, the A* algorithm was used in the search for routes.

![image](https://user-images.githubusercontent.com/88553229/210195716-490bd117-6752-4e17-9ca7-f531c5348edd.png)

In the following image we can see a simplified version of the proposed solution.

![image](https://user-images.githubusercontent.com/88553229/210195328-c20dc441-b41a-41a3-b567-7599e1bf8cc1.png)

*****************

## Set Up Project:

### Configure database on local:

#### Download and install MySql:
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
    
#### Download project
    1. In application.properties they are 2 files for config
        You have to edit aplication.yml and application-dev.yml
        3.1 Local database (application-dev.yml)
            url: jdbc:mysql://localhost:3306/localdb?connectionTimeZone=UTC
            username: root
            password: Admin
    2. Remember to activate dev profile in Maven menú.

#### Use Api documentation
#### https://documenter.getpostman.com/view/22885831/2s8Z6zzX9R
    1.  localhost:8080/api/startsimulation5d
        Need a start datetime in epoch milli format. 

        Example:
        Timestamp in milliseconds: 1659484800000
        Date and time (GMT): Wednesday, August 3, 2022 0:00:00

        Response:
        Simulation id, neccesary in some requests.

    2. localhost:8080/api/stopsimulation5d
        Need Simulation id.

        Response:
        Info about stopped simulation.

    3. localhost:8080/api/getsimulationbyday5d?datetime=1659484800000&simulation=2
        Need Simulation id and datetime in epoch milli

        Request have to be made in blocks of four hours, starting from datetime used in simulation start
        Request can be made to get info about current status of airports (by hour) and flights departing in 4 hour lapse.

    4. localhost:8080/api/shipment/register
        Need shipment info. 
        At the start of server, two dummy users are created to perform shipment registrations.

    Exists another api capacities for airports, shipments, Users.
    Check com.redex.application.algorithm.controller.api and com.redex.application.core.controller.api for more information.
    
When simulation runs in backend, each shipment and his route is printed.

![image](https://user-images.githubusercontent.com/88553229/210197413-a8115347-e993-40f5-b31f-d43ab9a02416.png)

In FrontEnd, all the process can be watched in the following videos (in spanish):
<sub>*Middle clic on image to see reproduction list on Youtube*</sub>
[![Watch the video](https://user-images.githubusercontent.com/88553229/210202123-03b2de0e-efa9-4f7f-a3f6-174e9f76893e.png)](https://youtube.com/playlist?list=PLT-m0aKjLeDTlkxEhZpNskGM6K3SqxLmz)

FrontEnd was developed by users https://github.com/sebas09-gcc, https://github.com/JeisonRomero, https://github.com/joSocAngel

This API was created as a Capstone project in course "Software development 1" by 
"Pontificia Universidad Católica del Perú" in Lima, Perú.
