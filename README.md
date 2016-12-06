# SimpleServer
A Simple Server with WebSocket chat service and http rest service.


### rest service:
If you want to use the rest service, connect to http://127.0.0.1:7777/user, and post a json format as following:  

  {  
        "api":"UserApi",  
        "param":"parameter"  
  }.  

In the SimpleServer, api is only defined as UserApi, so the "UserApi" is needed. But you can change "parameter" to anything you want. The Server will simply return the api and parameter information back to you.

---

If you want to use the rest service with file, connect to http://127.0.0.1:7777/user/upload, and post a file with key-value pairs as following:  

![json file format](https://github.com/LeoWolfLai/SimpleServer/blob/master/json%20file%20format.png)

The Server will simply return the file name back to you.


### chat service:
If you want to join a chat channel, use websocket client connect to ws://127.0.0.1:8888/YourIntRoomNumber/YourStringUserName. If there's another user in the same chatting room with you, you can start chatting.

