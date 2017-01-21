import sys

import connectionhandler
import responsemanager

rM = responsemanager.ResponseManager()
cH = connectionhandler.ConnectionHandler(5000, rM)
 
if cH.start():
    cH.run()
else:
    sys.exit()



 