import socket
import threading

class CameraServer(threading.Thread):
	def __init__(self, port):
		threading.Thread.__init__(self)
		self.port = port
	def initialize(self):
		self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		try:
			self.server.bind(('', self.port))
		except:
			print('An error has occured while binding to port.')
			return False
		print('Socket bind complete')
		self.server.listen(10)
		print('Camera server listening on port ' + str(self.port))
		return True

	def run(self):
		if not self.initialize():
			return
		while True:
			conn, addr = self.server.accept()
			print('Connected with ' + addr[0] + ':' + str(addr[1]))
			f = open('capture.jpg','wb')
			l = conn.recv(1024)
			while (l):
				f.write(l)
				l = conn.recv(1024)
			f.close()
			print('Done Receiving')
			conn.close()








