

class ResponseManager():
	def __init__(self):
		pass
	def process(self, content):
		if content.startswith('recipe'):
			return {'pen':1,'pineapple':1,'apple':0}
		else:
			return 'Received input: ' + content