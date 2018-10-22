#!/usr/bin/python3

import spidev
import time as time
import RPi.GPIO as GPIO
import atexit

# import socket programming library 
import socket 

# import thread module 
from _thread import *
import threading 

#Define Variables
active = False
ldr_channel = 0
base = 0
HOST = '192.168.8.101'  # The server's hostname or IP address
PORT = 5555        # The port used by the server
delta = 50;
sock =  socket.socket(socket.AF_INET, socket.SOCK_STREAM)
lock = threading.Lock()
#Create SPI
spi = spidev.SpiDev()
spi.open(0, 0)
spi.max_speed_hz = 1350000 #setting the maximum frequency of the spi
sensitivity = 50

def exit_handler():
    print("Cleaning up")
    sock.close()
    GPIO.cleanup()

atexit.register(exit_handler)

def delay():
    time.sleep(.002)

#setting board and gpio pins
#GPIO.setmode(GPIO.BOARD)

def readadc(adcnum):
    # read SPI data from the MCP3008, 8 channels in total
    if adcnum > 7 or adcnum < 0:
        return -1
    r = spi.xfer2([1, 8 + adcnum << 4, 0])
    data = ((r[1] & 3) << 8) + r[2]
    return data

def readLdr():
    ldr_reading = readadc(0)
    return ldr_reading

def receiveData(soc):
    global delta
    global active
    global sensitivity
    while True: 
        # data received from client 
        data = soc.recv(4096).decode("UTF-8")
        tokens = data.split()
        if tokens[0] == 'power':
            if  str(tokens[1]) == 'start':
                print('here'+tokens[1])
                #write this new configuration to a file for loading next time
                active = True
            elif tokens[1]  == 'stop':
                print(tokens[1])
                active = False
        elif tokens[0] == 'sense':
            sensitivity = eval(tokens[1])
            print(tokens[2])
        
            
def sendData(time_elapsed):
    sock.sendall(((str(time_elapsed)+'\n')).encode())
    
        
def wavedetected():
    global base
    wave = True
    time_start = time.time()
    curr_val = readLdr()
    curr_val -= base
    print(curr_val)
    while(curr_val> 5):
        print(curr_val)
        time_end= time.time()
        if(time_end- time_start) > 5:
                wave = False
                base = readLdr()
                break
        
        curr_val = readLdr()
        curr_val -= base   
        
        
    if(wave):
        time_end= time.time()
        duration = time_end - time_start
        sendData(duration)
        print("Wave lasted " +str(duration))
            #send this to the server over a socket 

def main():
    global base
    sock.connect((HOST, PORT))
    start_new_thread(receiveData, (sock,)) 
    base = readLdr()
    while True:
        if active:
            ldr_value = readLdr()
            #print(ldr_value)            
            if(ldr_value - base > sensitivity):
                print('wave')
                wavedetected()
                
if __name__ == "__main__": main()
