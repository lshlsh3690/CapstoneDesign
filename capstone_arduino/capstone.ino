
#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <SoftwareSerial.h>

#define TXD D7 //블루투스모듈의 TXD핀임!
#define RXD D8 //블루투스모듈의 RXD핀임!
#define trig D13
#define echo D12

//초음파 : 29us에 1cm를 이동

SoftwareSerial hc06(TXD,RXD);
SoftwareSerial myserial(D1);

const char* ssid; //SK_WiFiGIGABE57
const char* password;  //1704047740
const char* mqtt_server = "211.37.54.49";//192.168.55.175
const char* clientName="980303Client";
int cnt=0;


WiFiClient espClient;
PubSubClient client(espClient);


void setup_wifi() {
  String Sid, Spw;
  hc06.listen();
  while(true){
    if(hc06.available()){
      Sid = hc06.readStringUntil('\n');
      Spw = hc06.readStringUntil('\n');
      if(Sid.length() > 0 && Spw.length() > 0 )
      {
        ssid = Sid.c_str();
        password = Spw.c_str();
        Serial.println();
        Serial.print("SSID : ");
        Serial.println(ssid);
        Serial.print("password : ");
        Serial.println(password);
        break;
      }
    }
    Serial.print(".");
    delay(1000);
  }  
 delay(10);
 // We start by connecting to a WiFi network
 Serial.println();
 Serial.print("Connecting to ");
 Serial.println(ssid);
 WiFi.mode(WIFI_STA);
 WiFi.begin(ssid, password);
 while(WiFi.status() != WL_CONNECTED) {
   delay(500);
   Serial.print(".");
 }
 Serial.println("");
 Serial.println("WiFi connected");
 Serial.println("IP address: ");
 Serial.println(WiFi.localIP());
}

void activate_moter(){  //uart 통신 
  myserial.write('1');
  Serial.println("moter_actiavted!");
}

void callback(char* topic, byte* payload, unsigned int uLen) {
 char pBuffer[uLen+1];
 int i;
 for(i=0;i<uLen;i++){
  pBuffer[i]=payload[i];
 }
 pBuffer[i]='\0';
 Serial.println(pBuffer); // 1, 2
 if(pBuffer[0]=='1'){
    activate_moter();
  }else if(digitalRead(12) == 0){//LOW
    digitalWrite(12, HIGH);
  }
  
}
void reconnect() {
 // Loop until we're reconnected
 while (!client.connected()) {
   Serial.print("Attempting MQTT connection...");
   // Attempt to connect
   if (client.connect(clientName)) {
     Serial.println("connected");
     // ... and resubscribe
     client.subscribe("moter");
   } else {
     Serial.print("failed, rc=");
     Serial.print(client.state());
     Serial.println(" try again in 5 seconds");
     // Wait 5 seconds before retrying
     delay(5000);
   }
 }
}

void setup() {
   //pinMode(12, OUTPUT); // Initialize the BUILTIN_LED pin as an output
   Serial.begin(115200);
   hc06.begin(9600);
   //hc06.listen();
   setup_wifi();
   myserial.listen();
   client.setServer(mqtt_server, 1883);
   client.setCallback(callback);
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();
  char message[64]="";
  if(digitalRead(12) == 1)
  {
   sprintf(message, "{\"moter\" : \"ON\"}");
  }else if(digitalRead(12) == 0)
  {
   sprintf(message, "{\"moter\" : \"OFF\"}");
  }
   
  Serial.print("Publish message: ");
  Serial.println(message);
  client.publish("MOTER", message);
  
  delay(3000); // 3초
}
