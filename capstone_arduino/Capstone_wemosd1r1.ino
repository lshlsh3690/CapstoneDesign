#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <SoftwareSerial.h>

#define TXD D7 //블루투스모듈의 TXD핀임!
#define RXD D8 //블루투스모듈의 RXD핀임!

#define trig D3
#define echo D4


//초음파 : 29us에 1cm를 이동

//delayMicroseconds(us)
//duration = pulseIn(pin, HIGH);




SoftwareSerial hc06(TXD,RXD);
//SoftwareSerial myserial(D1);

const char* ssid; 
const char* password;

const char* mqtt_server = "211.37.54.49";
const char* clientName="980303Client";
int cnt=0;
int led = 12;
int dog = 0; //dog
int ok = 0;//강아지가 6초동안 위에 있었는지

unsigned long past = 0;
unsigned long now = 0;
int count = -1;

float distance=0;




WiFiClient espClient;
PubSubClient client(espClient);



void callback(char* topic, byte* payload, unsigned int uLen) {
 char pBuffer[uLen+1];
 int i;
 for(i=0;i<uLen;i++){
  pBuffer[i]=payload[i];
 }
 pBuffer[i]='\0';
 Serial.println(pBuffer); // 1, 2
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

void setup_wifi() {
  String Sid, Spw;
  //hc06.listen();
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
 //Serial.println("IP address: ");
 //Serial.println(WiFi.localIP());
}




void setup() {
   pinMode(led, OUTPUT); // Initialize the BUILTIN_LED pin as an output
   Serial.begin(115200);
   hc06.begin(9600);
   
   //LED
   digitalWrite(led, HIGH);
   delay(2000);
   digitalWrite(led, LOW);

   //bluetooth
   //hc06.listen();
   setup_wifi();
   
   //supersonic
   pinMode(trig,OUTPUT); //trig 발사
   pinMode(echo,INPUT); //echo 받기
   
   //connect to mqtt server!
   //myserial.listen();
   client.setServer(mqtt_server, 1883);   
   client.setCallback(callback);
}

void loop(){
  if (!client.connected()) {
      reconnect();
  }
  client.loop();
  digitalWrite(trig,LOW);
  delayMicroseconds(2);
  digitalWrite(trig,HIGH);
  delayMicroseconds(10);
  digitalWrite(trig,LOW);
  
  distance = (pulseIn(echo, HIGH)/29.0)/2; //cm
  //Serial.print(distance);
  //Serial.println("CM");
  //강아지가 올라가있는지(올라가면 1 아니면 0)
  if(distance<=7)
    dog=1;
  else
    dog=0;

  if(dog==1){ //0.5초 지나면 카운트 1씩 증가. 10돼야 5초
    now=millis(); //현재시간 저장
    if(now-past>=500){
      past=now;
      count++;
    }
  }
  else
    count=0;

  if(count>=5)
    ok=1;

  if((dog==0)&&(ok==1)){
    Serial.print("good");
    ok=0;
    char message[64]="1";
    client.publish("CLASSIFY", message);
  
  }
  
}
