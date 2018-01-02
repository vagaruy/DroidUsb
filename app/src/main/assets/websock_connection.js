var connection = null;
var clientID = 0;

var WebSocket = WebSocket || MozWebSocket;

//function setUsername() {
//  var msg = {
//    name: document.getElementById("name").value,
//    date: Date.now(),
//    id: clientID,
//    type: "username"
//  };
//  connection.send(JSON.stringify(msg));
//}

$("#request_battery").click(function(){
    // Only function if the websocket is established
    if (connection != null && connection.readyState == 1)
    {
        var obj = new Object();
           obj.Client = "Backend";
           obj.Request  = "Battery";
           var jsonString= JSON.stringify(obj);
           connection.send(jsonString);
    }
});

$("#toggle_screen").click(function(){
    // Only function if the websocket is established
    if (connection != null && connection.readyState == 1)
    {
        var obj = new Object();
           obj.Client = "Backend";
           obj.Request  = "Screen";
           var jsonString= JSON.stringify(obj);
           connection.send(jsonString);
    }
});

$("#con_websocket").click(function(){
  var serverUrl = "ws://" + window.location.hostname + ":8080";

    if (connection != null)
    {
        if(connection.readyState == 1 | connection.readyState == 0)

        {
        console.log("Websocket already opened");
        $("#con_websocket").removeClass("btn-default").addClass("btn-success").text("Connected");
        return;
        }
    }
    connection = new WebSocket(serverUrl);
    connection.onopen = function(evt) {
      console.log("Websocket opened");
    $("#con_websocket").removeClass("btn-default").addClass("btn-success").text("Connected");
    };
    connection.onmessage = function(evt)
    {
      console.log("Message is "+ evt.data)


    };
    });