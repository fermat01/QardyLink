# QardyLink
A simple NFC tag management using  java Swing framework



test the code in bash not zsh


SystemPropertiesAdvanced




//*************************************

//  create classes
javac -d out src/*.java


// copy the image
xcopy /E /I images out\images

// create jar file ==> cd out
jar cfe app.jar QardylAppLink *


// Test the app before packing

	java -jar app.jar

//package the app 

jpackage --type exe --input out --dest "D:\QardyLinkApp" --temp "D:\QardyLinkTemp" --name QardyLink --main-jar app.jar --main-class QardylAppLink --icon appcon.ico --win-shortcut --win-menu --add-modules java.smartcardio,java.desktop



//*************************************
