import sys

import connectionhandler

cH = connectionhandler.ConnectionHandler(5000)
 
if cH.start():
    cH.run()
else:
    sys.exit()



 