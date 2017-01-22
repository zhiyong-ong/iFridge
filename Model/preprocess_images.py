# Keras
from keras import backend as K
from keras.models import Sequential
from keras.layers.core import Dense, Dropout, Activation, Flatten
from keras.layers.convolutional import Convolution2D, MaxPooling2D
from keras.optimizers import SGD,RMSprop,adam
from keras.utils import np_utils

import gc
import os
import theano
import numpy as np
import matplotlib as mpl
mpl.use('TkAgg')
import matplotlib.pyplot as plt

from PIL import Image, ImageFile
ImageFile.LOAD_TRUNCATED_IMAGES = True

# SKLearn
from sklearn.utils import shuffle
from sklearn.cross_validation import train_test_split

# Input image dimensions
img_rows, img_cols = 100, 100

# Stores list of different categories of images
# [12, 34, 56] indicates 12 of A, 34 of B and 56 of C
#num_images = [387, 180, 333, 185, 495, 342, 160]
num_images = [37, 495, 28]
num_samples = sum(num_images)

# Number of channels
img_channels = 3

# Data
# (http://learnandshare645.blogspot.sg/2016/06/feeding-your-own-data-set-into-cnn.html)
#path1 = '/home/yewsiang/iFridge Dataset/Square Images'
#path2 = '/home/yewsiang/iFridge Dataset/Resized Images for Training'
path1 = '/home/yewsiang/iFridge Dataset/Square Centered Images'
path2 = '/home/yewsiang/iFridge Dataset/Resized Centered Images for Training'
'''
# Resizes the raw images (only need to do once)
imlist = os.listdir(path1)
for file in imlist:
    im = Image.open(path1 + '/' + file)   
    img = im.resize((img_rows,img_cols))
    #need to do some more processing here           
    img.save(path2 +'/' +  file, "PNG")
'''
# Training images
imlist = os.listdir(path2)
imlist.sort()

# create matrix to store flattened images
immatrix = np.array([np.array(Image.open(path2 + '/' + im2))[:,:,0:3].flatten()
              for im2 in imlist]) # Truncate the last bit of the image

label = np.ones((num_samples,), dtype = int)
start_idx = 0
for i, num in enumerate(num_images):
  for j in range(start_idx, start_idx + num):
    label[j] = i
  start_idx += num

data, Label = shuffle(immatrix, label, random_state=2)
train_data = [data, Label]

'''
# Verify that shuffling is done correctly
for i in range(0,20):
  img = data[i].reshape((img_cols, img_rows, img_channels))
  plt.imshow(img)
  print(Label[i])
  plt.show()
'''

print (train_data[0].shape)
print (train_data[1].shape)

# Batch_size to train
batch_size = [22, 27, 32, 37, 42]
# Number of output classes
nb_classes = 3
# Number of epochs to train
nb_epoch = 1


# Number of convolutional filters to use
nb_filters = 28
# Size of pooling area for max pooling
nb_pool = 2
# Convolution kernel size
nb_conv = 3

#%%
(X, y) = (train_data[0], train_data[1])

for i in range(5):
  # STEP 1: split X and y into training and testing sets
  X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=4)


  X_train = X_train.reshape(X_train.shape[0], img_rows, img_cols, 3)
  X_test  = X_test.reshape(X_test.shape[0], img_rows, img_cols, 3)

  X_train = X_train.astype('float32')
  X_test  = X_test.astype('float32')

  X_train /= 255
  X_test  /= 255

  print('X_train shape:', X_train.shape)
  print(X_train.shape[0], 'train samples')
  print(X_test.shape[0], 'test samples')

  # convert class vectors to binary class matrices
  Y_train = np_utils.to_categorical(y_train, nb_classes)
  Y_test = np_utils.to_categorical(y_test, nb_classes)

  #%%

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
  model.compile(loss='categorical_crossentropy', optimizer='adadelta', metrics=["accuracy"])

  #%%

  hist = model.fit(X_train, Y_train, batch_size=batch_size[i], nb_epoch=nb_epoch,
                verbose=1, validation_data=(X_test, Y_test))

'''            
hist = model.fit(X_train, Y_train, batch_size=batch_size, nb_epoch=nb_epoch,
              verbose=1, validation_split=0.2)
'''

# Saving weights
fname = "fridge-CNN-3items-centered.hdf5"
model.save_weights(fname,overwrite=True)

'''
# Visualizing losses and accuracy
train_loss=hist.history['loss']
val_loss=hist.history['val_loss']
train_acc=hist.history['acc']
val_acc=hist.history['val_acc']
xc=range(nb_epoch)

plt.figure(1,figsize=(7,5))
plt.plot(xc,train_loss)
plt.plot(xc,val_loss)
plt.xlabel('num of Epochs')
plt.ylabel('loss')
plt.title('train_loss vs val_loss')
plt.grid(True)
plt.legend(['train','val'])
print plt.style.available # use bmh, classic,ggplot for big pictures
plt.style.use(['classic'])

plt.figure(2,figsize=(7,5))
plt.plot(xc,train_acc)
plt.plot(xc,val_acc)
plt.xlabel('num of Epochs')
plt.ylabel('accuracy')
plt.title('train_acc vs val_acc')
plt.grid(True)
plt.legend(['train','val'],loc=4)
#print plt.style.available # use bmh, classic,ggplot for big pictures
plt.style.use(['classic'])


score = model.evaluate(X_test, Y_test, show_accuracy=True, verbose=0)
print('Test score:', score[0])
print('Test accuracy:', score[1])
print(model.predict_classes(X_test[1:5]))
print(Y_test[1:5])

def get_activations(model, layer_idx, X_batch):
    get_activations = K.function([model.layers[0].input, K.learning_phase()], [model.layers[layer_idx].output,])
    activations = get_activations([X_batch,0])
    return activations

# visualizing intermediate layers

output_layer = get_activations(model, 1, X_train)
output_fn = theano.function([model.layers[0].input], output_layer)

# the input image

input_image=X_train[0:1,:,:,:]
print(input_image.shape)

plt.imshow(input_image[0,0,:,:],cmap ='gray')
plt.imshow(input_image[0,0,:,:])


output_image = output_fn(input_image)
print(output_image.shape)

# Rearrange dimension so we can plot the result 
output_image = np.rollaxis(np.rollaxis(output_image, 3, 1), 3, 1)
print(output_image.shape)


fig=plt.figure(figsize=(8,8))
for i in range(32):
    ax = fig.add_subplot(6, 6, i+1)
    #ax.imshow(output_image[0,:,:,i],interpolation='nearest' ) #to see the first filter
    ax.imshow(output_image[0,:,:,i],cmap=matplotlib.cm.gray)
    plt.xticks(np.array([]))
    plt.yticks(np.array([]))
    plt.tight_layout()
plt

# Confusion Matrix

from sklearn.metrics import classification_report,confusion_matrix

Y_pred = model.predict(X_test)
print(Y_pred)
y_pred = np.argmax(Y_pred, axis=1)
print(y_pred)



p = model.predict_proba(X_test) # to predict probability

target_names = ['class 0(No bucket)', 'class 1(Bucket)']
print(classification_report(np.argmax(Y_test,axis=1), y_pred,target_names=target_names))
print(confusion_matrix(np.argmax(Y_test,axis=1), y_pred))
'''

# To solve problem of 'TF_DeleteStatus'
# (https://github.com/tensorflow/tensorflow/issues/3388)
gc.collect()
