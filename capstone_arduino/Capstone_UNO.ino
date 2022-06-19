#include <Stepper.h>
#include <SoftwareSerial.h>

// 2048:한바퀴(360도), 1024:반바퀴(180도)...
const int stepsPerRevolution = 2048; 
// 모터 드라이브에 연결된 핀 IN4, IN2, IN3, IN1

Stepper myStepper(stepsPerRevolution,11,9,10,8);  
char data;         
void setup() {
  Serial.begin(115200);
  myStepper.setSpeed(14); 
}
void loop() {
  while(Serial.available()){
    data = (char) Serial.read();
    Serial.println(data);
    if(data == '1')
    {
      Serial.println("모터를 실행합니다.");
      myStepper.step(stepsPerRevolution);
      data = 0;
      delay(500);
    }
  }
}
