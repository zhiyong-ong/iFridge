import sys

import connectionhandler
import responsemanager
import cameraserver


cS = cameraserver.CameraServer(5001)
cS.start()
rM = responsemanager.ResponseManager()
cH = connectionhandler.ConnectionHandler(5000, rM)

 
if cH.start():
    cH.run()
else:
    sys.exit()



 