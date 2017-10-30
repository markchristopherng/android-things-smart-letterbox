android-things-smart-letterbox
==============================

The smart letter box can be fitted to mailbox and can be used to detect and count the number of letters/parcels that it contains. The smart 
letter box stores the data in a firebase realtime database. This data can also be viewed remotely through a website hosted in firebase.
There is also a cloud function or trigger that can send updates to a user via email when the mailbox retrieves new mail. 

This project consists of the following components;

* app - android things app
* website - to display the firebase realtime database data
* firebase db & cloud function - cloud function to notify users when letters via email

# Architecture overiew
Rasperberry pi connect to Firebase and stores the letterbox data in firebase realtime database.
Use a angular website to read from the Firebase realtime database to display the information.

# Contact

email :  markchristopherng@gmail.com
