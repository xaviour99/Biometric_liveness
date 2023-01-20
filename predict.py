import glob
import numpy as np
from PIL import Image
from tensorflow.keras.models import load_model


records = sorted(glob.glob('./ecg-id-database-filter/Person_*/rec_*.png')) # to select the images from the folder path file
model =  load_model('model_ecg.h5')  #loding the trained model
w, h = 144, 224

def r_image(img):
  return np.array(Image.open(img).resize((h, w)))[:,:,0:1] / 255 #will read the images 

person_2 = r_image(records[600]) 
#person_2 = r_image(records[100])
person_2_test = r_image(records[60])
person_3 = r_image(records[200])
person_3_test = r_image(records[201])

prob = model.predict([person_2.reshape((1, w, h, 1)), person_2_test.reshape((1, w, h, 1))])
pred = (prob>0.5)[0][0]
if pred:
  print("Person verified [confidence: {:.2f}%]".format(100*prob[0][0]))
else: 
  print("Wrong person - [confidence: {:.2f}%]".format(100*(1-prob[0][0])))
