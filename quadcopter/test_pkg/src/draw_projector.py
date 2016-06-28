#!/usr/bin/env python
# USAGE
# python match.py --template cod_logo.png --images images

# import the necessary packages
import numpy as np
import rospy
from std_msgs.msg import String
import time
import math
import sys
import glob
import cv2

import tf
import tf2_ros

from cv2 import __version__
from sensor_msgs.msg import Image
from nav_msgs.msg import Odometry
from cv_bridge import CvBridge, CvBridgeError
from tf.transformations import euler_from_quaternion
from geometry_msgs.msg import Quaternion
from geometry_msgs.msg import Twist
from geometry_msgs.msg import Pose
from geometry_msgs.msg import TransformStamped
from geometry_msgs.msg import PoseStamped

#Define a global localization class

template = np.zeros((100,100,3), np.uint8)
template = cv2.cvtColor(template, cv2.COLOR_BGR2GRAY)
template = cv2.Canny(template, 50, 200)
(tH, tW) = template.shape[:2]
cv2.imshow("Template", template)
cv2.waitKey(1)


# main
def main(args):
	rospy.init_node('image_converter', anonymous=True)
	startingX = rospy.get_param('~startingX')	
	startingY = rospy.get_param('~startingY')
	

	print str(startingX)
	gl = GL()
        gl.bucketLocX= rospy.get_param('~bucketLocX')
        gl.bucketLocY= rospy.get_param('~bucketLocY')
	gl.lastPoseAngle=rospy.get_param('~direction')
	gl.angle= rospy.get_param('~direction')
        gl.pose.position.x = rospy.get_param('~startingX')
        gl.pose.position.y = rospy.get_param('~startingY')
	
	gl.kalman()
	try:

		rospy.spin()
	except KeyboardInterrupt:
		print("Shutting down")


	cv2.destroyAllWindows()

if __name__ == '__main__':
	main(sys.argv)


