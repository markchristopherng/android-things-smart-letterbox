Smart Postal box
====================================

## Overview
This android things application is demoing a smart letterbox that could either be used at home or 
in the public. It uses a PIR sensor to detect movement to count the number of letters or parcles 
that have been delivered. This information is sent back to the cloud using firebase
so we know when the mail in the letterbox needs to be collected. We also monitor other information
such as the temperature.

# Hardware Used

* Raspberry Pi 3
* Rainbow HAT androidthing
* PIR motion detector
 
 
# Setup wifi
 
 $ am startservice \
     -n com.google.wifisetup/.WifiSetupService \
     -a WifiSetupService.Connect \
     -e ssid AndroidAP
     -e passphrase <PASSWORD>
 
 
# Connect to device
 
$ adb connect <ip-address>

OR

$ adb connect Android.local


# Architecture overiew
Rasperberry pi connect to Firebase and stores the letterbox data in firebase realtime database.
Use a angular website to read from the Firebase realtime database to display the information.


# Contact

email :  markchristopherng@gmail.com
