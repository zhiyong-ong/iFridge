

class ResponseManager():
	def __init__(self):
		pass
	def process(self, content):
		if 'recipe' in content:
			return {'FridgeItems':
				[
					{'itemName': 'pineapple',
					 'itemCount': 5},
					{'itemName': 'apple',
					'itemCount': 5}
				]
			}
		else:
			return 'Received input: ' + content