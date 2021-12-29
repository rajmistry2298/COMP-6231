%% readme.txt
%% for DPSS (Distributed Player Status system) Final Project

Name:-     Software Failure Tolerant/Highly Available Distributed Player Status System (DPSS)

Author:-      Raj Mistry (40119206)		               
Date:- 		  August 12, 2020


* REQUIRED SYSTEMS
--------------------
-Windows 10 
-Eclipse IDE
-IDL


HOW TO RUN APPLICATION?
-----------------------------
1. Import the application into the eclipse workspace
2. Start ORBD using command "start orbd -ORBInitialPort 1050" in cmd(where 1050 is orbd port number)
3. In order to run Servers & their Replicas goto run configuration and select RM files(Replica Manager) and in its Argument part enter "-ORBInitialPort 1050 -ORBInitialHost localhost" 
   and apply it and Run files. Do this for all 3 RM files: 
	a. RM1.java
	b. RM2.java
	c. RM3.java
4. To Start Front End Run it with run configuration and in its arguments apply "-ORBInitialPort 1050 -ORBInitialHost localhost".
   FrontEndManager.java

5. In order to run clients goto run configuration and select client file and in its Argument part enter "-ORBInitialPort 1050 -ORBInitialHost localhost" and apply it
   and Run files. Do this for all Client files:
   For Player Operations Run       : PlayerClient.java
   For Administrator Operation Run : AdministratorClient.java
   For Multithread Test Run        : MultipleThreadClientTest.java

(First 2 files PlayerClient and AdministratorClient are for manually testing all operations and MultipleThreadClientTest file is Automated Test whic is for testing 
 concurrency and atomicity and for that what it does is described in that file)

-----------------------------------------
****** Important Note ********
---------------------------------
1. As Professor Said all servers should already have few Player Accounts, So I have Added 5 Accounts In each Server Which are as follows :

Username : "rajmistry2298"  Password: "R@mistry"
Username : "johncena54"     Password: "J@cena54"
Username : "paulheymanguy"  Password: "P@heyman"
Username : "username"  		Password: "password"
Username : "lanabelitchka"  Password: "L@belitchka"

All 3 Servers have this 5 accounts.

2. All the Log Files are there in their Particular folder.
-> Each Player Log file is named as SERVENAME_USERNAME.txt (eg. NA_rajmistry2298.txt)
-> Each Admin Log file is also Named Same as SERVERNAME_Admin.txt (eg. NA_Admin.txt)
-> Each Server Replica & FrontEnd has their own log files in respective folders

******** Test Cases Information ************
--------------------------------------------
-> In Report Test Cases are added for performing operations on one particular Server(not same in all cases) but you can find Same Operations for Other Servers in log files.
YOU CAN CHECK THE SAME OPERATIONS ON OTHER SERVER THAN SHOWN IN SERVER IN SCREENSHOTS.
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    Thank You!