import threading

class ImageRecognize(threading.Thread):
	def __init__(self, cameraServer):
		threading.Thread.__init__(self)

		# Access the queue with self.cameraServer.queue
		self.cameraServer = cameraServer

	def process(self, image):
		print('Image processed!')

	def run(self):
		while True:
			current = self.cameraServer.queue.get()
			self.process(current)
