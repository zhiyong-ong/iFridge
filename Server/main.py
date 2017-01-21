import socket
import sys

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

try:
	# Local IP address is 172.23.181.78
	s.bind(('', 5000))
except:
	print('An error has occured while binding to port.')
	sys.exit()
print('Socket bind complete')
s.listen(10)
print('Listening on port 5000')
def clientthread(conn):
    #Sending message to connected client
    conn.send('Welcome to the server. Type something and hit enter\n') #send only takes string
     
    #infinite loop so that function do not terminate and thread do not end.
    while True:
         
        #Receiving from client
        data = conn.recv(1024)
        reply = 'OK...' + data
        if not data: 
            break
     
        conn.sendall(reply)
     
    #came out of loop
    conn.close()
 
#now keep talking with the client
while 1:
    #wait to accept a connection - blocking call
    conn, addr = s.accept()
    print ('Connected with ' + addr[0] + ':' + str(addr[1]))
     
    #start new thread takes 1st argument as a function name to be run, second is the tuple of arguments to the function.
    start_new_thread(clientthread ,(conn,))
 
s.close()