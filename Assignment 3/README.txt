%% readme.txt
%% for DPSS (Distributed Player Status system)

Name:-        Distributed Player Status system
Author:-      Raj Mistry (40119206)		               
Date:- 		  July 12, 2020

* REQUIRED SYSTEMS
--------------------
-Windows 10 
-Eclipse IDE

* HOW TO RUN APPLICATION?
-----------------------------
1. Import the application into the eclipse workspace

2. Run the three servers named as NAServer.java, EUServer.java and ASServer.java 

3. To run Client files:
   For Player Operations Run       : PlayerClient.java
   For Administrator Operation Run : AdministratorClient.java
   For Multithread Test Run        : MultipleThreadClientTest.java

(First 2 files PlayerClient and AdministratorClient are for manually testing all operations and MultipleThreadClientTest file is Automated Test whic is for testing 
 concurrency and atomicity and for that what it does is described in that file)

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
-> Each Player Log file is named as SERVENAME_USERNAME_LOG.txt (eg. NA_rajmistry2298_Log.txt)
-> Each Admin Log file is also Named Same as SERVERNAME_Admin_Log.txt (eg. NA_Admin_Log.txt)
-> 3 Server log files are NA_Server_Log.txt , EU_Server_Log.txt , AS_Server_Log.txt

******** Test Cases Information ************
--------------------------------------------
-> In Report Test Cases are added for performing operations on one particular Server(not same in all cases) but you can find Same Operations for Other Servers in log files.
YOU CAN CHECK THE SAME OPERATIONS ON OTHER SERVER THAN SHOWN IN SERVER IN SCREENSHOTS.