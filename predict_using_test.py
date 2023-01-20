import glob
import numpy as np
from PIL import Image
import random
from tensorflow.keras.models import load_model
def read_image(img):
    w, h = 144, 224
    return np.array(Image.open(img).resize((h, w)))[:,:,0:1] / 255


def pred(uname):
    records = sorted(glob.glob("./ecg-id-database-filter/"+uname+"/rec_*.png"))
    test_record=sorted(glob.glob("./test/*"))
    model =  load_model('model_ecg.h5')
    w, h = 144, 224

    testlen=len(test_record)-1
    recordlen=len(records)-1
    val=0
    for i in range(0,4):
        testrec=random.randint(0,testlen)
        #recind=random.randint(0,recordlen)
        prob=model.predict([read_image(test_record[testrec]).reshape((1, w, h, 1)), read_image(records[testrec]).reshape((1, w, h, 1))])
        val=val+prob
    val=val/4.0

    pred = (val>0.5)[0][0]
    if pred:
        print("Person verified [confidence: {:.2f}%]".format(100*val[0][0]))
    else:

        print("Wrong person - [confidence: {:.2f}%]".format(100*(1-val[0][0])))
    if(val==0):
        return -1

    return val

