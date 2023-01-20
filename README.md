# Biometric_liveness
The front-end comprises all of the user interface frames created in Android Studio with Java. 
The create_ecg_imag.py script should be run first to convert the raw singnal data to an image format. The train.py script is used to train the model, and it uses the data_generator.py file to build the training model. 
Predict.py is used to see if the prediction works well. 
predict_cloud.py connects to the cloud, which connects to the front-end. The predict_cloud.py file refers to the predic_using_test.py file, which processes the information from the front end and then sends the result back to the cloud to authenticate in the front end.
The ECG-ID database was utilised for this machine learning model. This database was chosen since it contains data with and without noise. The data without noise is used for this research.
