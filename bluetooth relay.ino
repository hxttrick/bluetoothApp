#include <SoftwareSerial.h>

#define rxPin 2
#define txPin 3
SoftwareSerial BTSerial(rxPin, txPin);

#define relayPin 8

String data = "";

int relayStatus = 0;

void setup() {
  pinMode(relayPin, OUTPUT);

  Serial.begin(115200);
  Serial.println("init arduino serial");

  BTSerial.begin(9600);
  Serial.println("init bluetooth seial");

}

void loop() {
  while(BTSerial.available() > 0) {

    data = BTSerial.readString();
    Serial.println(data);

    data.toLowerCase() == "on" ? relayStatus = 1 : relayStatus = 0;

  }

  if(relayStatus == 0) {
    digitalWrite(relayPin, LOW);
  }
  else if (relayStatus == 1) {
    digitalWrite(relayPin, HIGH);
  }

}
