This project is a prototype of secure messaging developed in Java for my Professional Development module. 
The app demonstrates how encryption can protect private messages between three different users. 
**Technologies Used**
* Java 21.
* Java Swing to develop the graphical interface.
* AES-GCM to encrypt messages.
* RSA to protect the AES encryption key.
**Application Features**
* Three users: User 1, User 2 and User 3.
* Sender and recipient selection.
* Message encryption and decryption
* Access denied when an incorrect user attempts to decrypt a message
  **Project files**
* User.java: Create the users and generate their RSA keys.
* EncryptionService.java: Contains the encryption and decryption methods.
* Main.java: First version of the program used for testing from the console.
* SecureMessagingGUI.java: Contains the graphical interface of the application.
**How to run**
To run the application you need to have Java 21 installed. Open the terminal in the folder where the four Java files are located and run the following commands:
                    javac *.java
                    java SecureMessagingGUI

**Testing**
The repository includes four screenshots showing the results of the tests performed. 
These tests show that the right recipients can decrypt your messages, while the wrong users receive an access denied message. 

**limitations**
This app is an educational prototype. The messages are simulated within the program and are not actually transmitted over the internet.
The repository includes four screenshots showing the results of the tests performed. These tests show that the right recipients can decrypt your messages, while the wrong users receive an access denied message. This app is an educational prototype. The messages are simulated within the program and are not actually transmitted over the internet.
