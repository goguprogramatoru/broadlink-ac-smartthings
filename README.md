# broadlink-ac-smartthings

# Info
Smartthings handler for broadlink-based AC units (usually come with the AcFreedom app)


**WARNING: This is just a prototype project not a final product!**<br>
Also, it's my first interaction with smartthings. It works for me, may work for you, but it's not guaranteed. 

# Compatibility
 - Vortex VORTEX VAI-A1217FJW
 
The library that was modified from, supported also these devices (so it's a high chance for them to work):
 - Dunham bush
 - Rcool Solo
 - Akai 9000BTU
 - Rinnai
 - Kenwood
 - Tornado X (2019 and up)
 - AUX ASW-H09A4/DE-R1DI (Broadlink module)
 - Ballu BSUI/IN-12HN8 

In general, it should support the smart AC units that have the AcFreedom app. 

If you have tested this on other devices, please let me know, to update this readme.

# Pre-requirements
Install the broadlink-ac-rest-service: https://github.com/goguprogramatoru/broadlink-ac-rest-service

# Installing
1. copy-paste & save the broadlink-ac-dh.groovy in your groovy ide -> my device handler -> create new device handler -> from code
2. Add a new device. 
2.1. The device network id format: server-ip|server-port|ac-mac (ex: 192.168.1.2|9000|34:EA:34:12:34:56
2.2. type = broadlink-ac

# Tags: 
IOT, SmartHome
