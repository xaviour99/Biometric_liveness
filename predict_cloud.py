import firebase_admin
from firebase_admin import credentials,storage
from google.cloud import storage as st
from google.oauth2 import service_account
from firebase_admin import db

from predict_using_test import pred

cred = credentials.Certificate("biometric-f0717-firebase-adminsdk-kipn8-189af3fd9d.json")
firebase_admin.initialize_app(cred,{'storageBucket': 'biometric-f0717.appspot.com',
'databaseURL': 'https://biometric-f0717-default-rtdb.europe-west1.firebasedatabase.app'}) # connecting to firebase
cred = service_account.Credentials.from_service_account_file("biometric-f0717-firebase-adminsdk-kipn8-189af3fd9d.json")
storage_client=st.Client(credentials=cred)
bucket=storage_client.bucket(storage.bucket().name)
print(storage.bucket())

#----------------------------------------------------------------------------------------
files=storage_client.list_blobs(storage.bucket().name)
print(files)
j=0
for i in files:
    if("rec" in i.public_url): 
       
        i.download_to_filename("./test/rec"+str(j)+".png")
        j=j+1

#------------------------------------------------------------------------
#----------------------------------------------------

ref = db.reference('/')
data=dict(ref.get())
username=data["Username"]
print(username)

prob=pred(username)
print(prob)
ref.child("Value").set(str(prob[0][0]))