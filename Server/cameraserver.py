import socket
import threading
import queue

class CameraServer(threading.Thread):
	def __init__(self, port):
		threading.Thread.__init__(self)
		self.port = port
		self.queue = queue.Queue()
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
			l = conn.recv(1024)
			total = l
			l = conn.recv(1024)
			while (l):
				total += l
				l = conn.recv(1024)
			print('Done Receiving')
			conn.close()

			# Possible bug due to aliasing. If images keep
			# getting replaced, do a deep copy of 'total' before
			# putting it into queue
			self.queue.put(total)









