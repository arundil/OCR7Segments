
# file: GoKitchen-serverRFCOMMBluetooth.py
# auth: Marc Bayon <mb154@hotmail.com>
# desc: Rfcomm server for GPIO handling.
#

from bluetooth import *
import RPi.GPIO as GPIO
import time
from time import sleep

GPIO_BT_CONNECTED = 21
GPIO_ON_OFF = 20
GPIO_POT5 = 16
GPIO_POT4 = 12
GPIO_POT3 = 25
GPIO_POT2 = 24
GPIO_POT1 = 23
GPIO_BUZZ = 18

HOB_POWER = 0
POWER_ON = False


def main():
    while True:
        
        HOB_POWER = 0
        POWER_ON = False
        GPIO.setmode(GPIO.BCM)
        GPIO.setwarnings(False)
        GPIO.setup(GPIO_BT_CONNECTED,GPIO.OUT)
        GPIO.setup(GPIO_ON_OFF,GPIO.OUT)
        GPIO.setup(GPIO_POT5,GPIO.OUT)
        GPIO.setup(GPIO_POT4,GPIO.OUT)
        GPIO.setup(GPIO_POT3,GPIO.OUT)
        GPIO.setup(GPIO_POT2,GPIO.OUT)
        GPIO.setup(GPIO_POT1,GPIO.OUT)
        GPIO.setup(GPIO_BUZZ,GPIO.OUT)
        
        clearGPIO(True)
    
        server_sock=BluetoothSocket( RFCOMM )
        server_sock.bind(("",PORT_ANY))
        server_sock.listen(1)
    
        port = server_sock.getsockname()[1]
    
        uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
     
        advertise_service( server_sock, "SampleServer",
                           service_id = uuid,
                           service_classes = [ uuid, SERIAL_PORT_CLASS ],
                           profiles = [ SERIAL_PORT_PROFILE ], 
        #                   protocols = [ OBEX_UUID ] 
                            )
                      
        print("Waiting for connection on RFCOMM channel %d" % port)
    
        client_sock, client_info = server_sock.accept()
        print("Accepted connection from ", client_info)
    
        try:
            while True:
                data = client_sock.recv(1024)
                if len(data) == 0: break
                print("received [%s]" % data)
                if data == "STATUS":
                    print ("Connected")
                    GPIO.output(GPIO_BT_CONNECTED,GPIO.HIGH)
                    #server_sock.send("OK")
                    print("Send [OK]")
                    
                if data == "ON":
                    print ("Vitro on")
                    GPIO.output(GPIO_ON_OFF,GPIO.HIGH)
                    POWER_ON = True
                    #server_sock.send("ON_OK")
                    print("Send [ON_OK]")
                    buzzer(1)
                    
                if data == "OFF":
                    print ("Vitro OFF")
                    clearGPIO(False)
                    HOB_POWER = 0
                    POWER_ON = False
                    #server_sock.send("OFF_OK")
                    print("Send [OFF_OK]")
                    buzzer(2)
                    
                if data == "PWUP":
                    print ("Power UP")
                    if POWER_ON:
                        HOB_POWER = HOB_POWER+1
                        if DisplayPower(HOB_POWER):
                            #server_sock.send("PWUP_OK")
                            print("Send [PWUP_OK]")
                            buzzer(HOB_POWER)
                        else :
                            HOB_POWER = HOB_POWER -1
                            #server_sock.send("PWUP_NOK")
                            print("Send [PWUP_NOK]")
                    else:
                        #server_sock.send("PWUP_NOK")
                        print("Send [PWUP_NOK]")
                        
                            
                if data == "PWDOWN":
                    if POWER_ON:
                        print ("Power DOWN")
                        HOB_POWER = HOB_POWER-1
                        if DisplayPower(HOB_POWER):
                            #server_sock.send("PWDOWN_OK")
                            print("Send [PWDOWN_OK]")
                            buzzer(HOB_POWER)
                        else :
                            HOB_POWER = HOB_POWER +1
                            #server_sock.send("PWDOWN_NOK")
                            print("Send [PWDOWN_NOK]")
                    else:
                        #server_sock.send("PWDOWN_NOK")
                        print("Send [PWDOWN_NOK]")
                    
        except IOError:
            pass
    
        print("disconnected")
        clearGPIO(True)
        
        client_sock.close()
        server_sock.close()
        print("all done")

def buzzer (times):
    for x in range(0,times):
        GPIO.output(GPIO_BUZZ,GPIO.HIGH)
        sleep(0.2)
        GPIO.output(GPIO_BUZZ,GPIO.LOW)
        sleep(0.2)

def clearGPIO(hardOff):
    if hardOff:
        GPIO.output(GPIO_BT_CONNECTED,GPIO.LOW)
        
    GPIO.output(GPIO_ON_OFF,GPIO.LOW)
    GPIO.output(GPIO_POT1,GPIO.LOW)
    GPIO.output(GPIO_POT2,GPIO.LOW)
    GPIO.output(GPIO_POT3,GPIO.LOW)
    GPIO.output(GPIO_POT4,GPIO.LOW)
    GPIO.output(GPIO_POT5,GPIO.LOW)
    HOB_POWER = 0
    POWER_ON = 0

def DisplayPower(power):
    if power == 1:
        GPIO.output(GPIO_POT1,GPIO.HIGH)
        GPIO.output(GPIO_POT2,GPIO.LOW)
        GPIO.output(GPIO_POT3,GPIO.LOW)
        GPIO.output(GPIO_POT4,GPIO.LOW)
        GPIO.output(GPIO_POT5,GPIO.LOW)
        return True
    
    if power == 2:
        GPIO.output(GPIO_POT1,GPIO.HIGH)
        GPIO.output(GPIO_POT2,GPIO.HIGH)
        GPIO.output(GPIO_POT3,GPIO.LOW)
        GPIO.output(GPIO_POT4,GPIO.LOW)
        GPIO.output(GPIO_POT5,GPIO.LOW)
        return True
    
    if power == 3:
        GPIO.output(GPIO_POT1,GPIO.HIGH)
        GPIO.output(GPIO_POT2,GPIO.HIGH)
        GPIO.output(GPIO_POT3,GPIO.HIGH)
        GPIO.output(GPIO_POT4,GPIO.LOW)
        GPIO.output(GPIO_POT5,GPIO.LOW)
        return True
    
    if power == 4:
        GPIO.output(GPIO_POT1,GPIO.HIGH)
        GPIO.output(GPIO_POT2,GPIO.HIGH)
        GPIO.output(GPIO_POT3,GPIO.HIGH)
        GPIO.output(GPIO_POT4,GPIO.HIGH)
        GPIO.output(GPIO_POT5,GPIO.LOW)
        return True
    
    if power == 5:
        GPIO.output(GPIO_POT1,GPIO.HIGH)
        GPIO.output(GPIO_POT2,GPIO.HIGH)
        GPIO.output(GPIO_POT3,GPIO.HIGH)
        GPIO.output(GPIO_POT4,GPIO.HIGH)
        GPIO.output(GPIO_POT5,GPIO.HIGH)
        return True
    
    if power > 5 or power < 0:
        return False


if __name__ == "__main__":
    main()
    
