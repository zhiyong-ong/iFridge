# Keras
from keras import backend as K
from keras.models import Sequential
from keras.layers.core import Dense, Dropout, Activation, Flatten
from keras.layers.convolutional import Convolution2D, MaxPooling2D
from keras.optimizers import SGD,RMSprop,adam
from keras.utils import np_utils

from PIL import Image, ImageFile

import matplotlib as mpl
mpl.use('TkAgg')
import matplotlib.pyplot as plt

import gc
import os
import theano
import numpy as np
import threading

# === Global Variables ===
# Number of channels
img_channels = 3

# Number of output classes
nb_classes = 3
# Number of convolutional filters to use
nb_filters = 32
# Size of pooling area for max pooling
nb_pool = 2
# Convolution kernel size
nb_conv = 3

# Filename of model
fname = "../Model/fridge-CNN-3items-more-data.hdf5"

#classes = ["Beer", "Chocolate", "Coke", "Iced lemon tea", "Nothing", "Waffle", "Yakult"]
classes = ["Iced Lemon Tea", "Nothing", "Yakult"]

# Input image dimensions
img_rows, img_cols = 100, 100

# Input information
full_img_cols, full_img_rows = 600, 400
new_img_cols, new_img_rows   = int(full_img_cols / 1), int(full_img_rows / 1)

# Model
model = Sequential()
model.add(Convolution2D(nb_filters, nb_conv, nb_conv,
                        border_mode='valid',
                        input_shape=(img_rows, img_cols, 3)))
convout1 = Activation('relu')
model.add(convout1)
model.add(Convolution2D(nb_filters, nb_conv, nb_conv))
convout2 = Activation('relu')
model.add(convout2)
model.add(MaxPooling2D(pool_size=(nb_pool, nb_pool)))
model.add(Dropout(0.5))

model.add(Flatten())
model.add(Dense(128))
model.add(Activation('relu'))
model.add(Dropout(0.5))
model.add(Dense(nb_classes))
model.add(Activation('softmax'))

model.load_weights(fname)


class ImageRecognize(threading.Thread):
	def __init__(self, cameraServer):
		threading.Thread.__init__(self)

		# Access the queue with self.cameraServer.queue
		self.cameraServer = cameraServer

	def process(self, image):
		p = ImageFile.Parser()

		p.feed(image)
		img = p.close()
		img = img.resize((new_img_rows,new_img_cols))
		img_array = np.array(img)[:,:,0:3]

		step_size = 50
		num_hori_steps  = int((new_img_rows - img_rows) / step_size)
		final_hori_step = new_img_rows - step_size * num_hori_steps
		num_vert_steps  = int((new_img_cols - img_cols) / step_size)
		final_vert_step = new_img_cols - step_size * num_vert_steps

		imgs = np.zeros((num_vert_steps * num_hori_steps, img_rows, img_cols, 3), dtype=np.uint8)

		for i in range(0, num_vert_steps):
		  for j in range(0, num_hori_steps):
		    current_row = i * step_size
		    current_col = j * step_size
		    #print("[{0}, {1}] [Row: {2}][Col: {3}]".format(i, j, current_row, current_col))
		    current_img = img_array[current_row:current_row + img_rows, current_col:current_col + img_cols,:]
		    
		    #if j % 2 == 0:
		    #  plt.imshow(current_img)
		    #  plt.show()
		    imgs[i * num_hori_steps + j,:,:,:] = current_img

		Y_pred = model.predict(imgs)

		# Choose the highest probability class
		pred_class = np.argmax(Y_pred, axis=1)

		# Count the number of times each class appeared
		count = [0] * nb_classes
		for i in range(0, len(Y_pred)):
		  count[pred_class[i]] += 1
		  #print(classes[pred_class[i]])
		  #plt.imshow(imgs[i,:,:,:])
		  #plt.show()

		# Statistics of image patches
		for i in range(0, nb_classes):
		  print(classes[i] + ": " + str(count[i]))

		'''
		f.write(image)
		f.close()
		Image.open("capture.jpg")
		'''
		print('Image processed!')

	def run(self):
		while True:
			current = self.cameraServer.queue.get()
			self.process(current)
