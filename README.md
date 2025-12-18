# Instructions
### connect from a VM or another device
By using 
```java
ServerSocket serverSocket = new ServerSocket(PORT,50,InetAddress.getByName("0.0.0.0"));
```
It can connect from other clients at the ip obtained from running ```ifconfig``` in the terminal and 
using the corresponding ip of en0 to connect using: ```telenet <IP ADDRESS> 8888```. If you are using a 
VM to observe the realistic effects, as network adapter, you shouldn't use a NAS but a Bridged 
connection.
