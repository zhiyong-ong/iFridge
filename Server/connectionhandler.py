import socket
import _thread
import json

class ConnectionHandler():
	def __init__(self,port,responseManager):
		self.port = port
		self.responseManager = responseManager

	def start(self):
		self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		try:
			# Local IP address is 172.23.181.78
			self.server.bind(('', self.port))
		except:
			print('An error has occured while binding to port.')
			return False
		print('Socket bind complete')
		self.server.listen(10)
		print('Listening on port ' + str(self.port))
		return True

	def run(self):
		while True:
			conn, addr = self.server.accept()
			print('Connected with ' + addr[0] + ':' + str(addr[1]))
     
		    #start new thread takes 1st argument as a function name to be run, second is the tuple of arguments to the function.
			_thread.start_new_thread(self.clientthread ,(conn,))
	def clientthread(self, conn):
		conn.send('Welcome to the server.'.encode())
		try:
			while True:
				data = conn.recv(1024).decode()

				if not data:
					break
				print('Received input: ' + data)
				conn.sendall((json.dumps(self.responseManager.process(data))+'\r\n').encode())
			conn.close()
		except:
			conn.close()