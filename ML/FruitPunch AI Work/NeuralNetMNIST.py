from sklearn.preprocessing import LabelBinarizer
from keras.datasets import mnist, fashion_mnist
from tensorflow import keras
from tensorflow.keras.layers import (Input, Conv2D, MaxPooling2D, Flatten, 
                                     Dense, Dropout, BatchNormalization, Activation)
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt


def define_cnn(input_shape, num_classes):
    # Build the architecture
    model = keras.Sequential()

    # first CONV => RELU => CONV => RELU 
    model.add(Conv2D(32, (5, 5), padding="same",
        input_shape=input_shape))
    model.add(Activation("relu"))
    model.add(Conv2D(32, (5, 5), padding="same"))
    model.add(Activation("relu"))
    # second CONV => RELU => CONV => RELU
    model.add(Conv2D(64, (3, 3), padding="same"))
    model.add(Activation("relu"))
    model.add(Conv2D(64, (3, 3), padding="same"))
    model.add(Activation("relu"))
    # third CONV => RELU => CONV => RELU
    model.add(Conv2D(128, (3, 3), padding="same"))
    model.add(Activation("relu"))
  
    # first (and only) set of FC => RELU layers
    model.add(Flatten())
    model.add(Dense(512))
    model.add(Activation("relu"))
    # softmax classifier
    model.add(Dense(num_classes))
    model.add(Activation("softmax"))

    model.summary()
    return model

(X_train, y_train), (X_test, y_test) = mnist.load_data()
#(X_train, y_train), (X_test, y_test) = fashion_mnist.load_data()
print(X_train.shape)
print(X_test.shape)

lb = LabelBinarizer()
y_train = lb.fit_transform(y_train)
y_test = lb.transform(y_test)


# Put it into suitable shape
X_train = X_train.reshape(60000,28,28,1)
X_test = X_test.reshape(10000,28,28,1)

num_epochs = 10
batch_size = 16
input_shape = X_train.shape[1:]
num_classes = y_train.shape[-1]

model = define_cnn(input_shape, num_classes)
model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['accuracy'])
# Train and test
H = model.fit(X_train, y_train, batch_size=batch_size, epochs=num_epochs, validation_data=(X_test, y_test))

score = model.evaluate(X_test, y_test, verbose=0)
print("Test loss:", score[0])
print("Test accuracy:", score[1])

# View example digit
index = np.random.randint(0,60000)
image = X_train[index]
plt.imshow(image.squeeze(), cmap='gray')
plt.show

# Get prediction for the image
prediction = model.predict(np.expand_dims(image, axis=0))[0]
predicted_label = np.argmax(prediction)

print(f"Predicted label: {predicted_label}")
