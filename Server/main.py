import sys

import connectionhandler
import responsemanager
import cameraserver
import imagerecognize



cS = cameraserver.CameraServer(5001)
cS.start()
iR = imagerecognize.ImageRecognize(cS)
iR.start()
rM = responsemanager.ResponseManager()
cH = connectionhandler.ConnectionHandler(5000, rM)

 
if cH.start():
    cH.run()
else:
    sys.exit()



 