#!/usr/bin/env python
import rospy
from std_msgs.msg import String
import numpy as np
import turtlesim.srv
from turtlesim.msg import Pose
import sys
import select
from geometry_msgs.msg import PoseStamped
from tf2_msgs.msg import TFMessage

#################################################
# Global Variables
#################################################

agents_limit = 7 	# Max number of agents allowed
n = 0		        # Number of turtles
dim = 2		        # Dimension of space
theta = 0		# Angle in radians
scale = 1.
form = -1
# Formations in 2D with 4 Turtles
SQUARE = np.array([[ .0,  .0],
                   [ .0,  .3],
                   [ .3,  .3],
                   [ .3,  .0]])

LINE = np.array([[ .0 ,  .0 ],
                 [ .05,  .05],
                 [ .1 ,  .1 ],
                 [ .15,  .15],
                 [ .2 ,  .2 ],
                 [ .25,  .25],
                 [ .3 ,  .3 ]])

TRIANGLE = np.array([[.0, .0],
                     [.0, .3],
                     [.3, .0]])

# TODO
PENAGON = 5
HEXAGON = 6


d = np.identity(n)	# d Matrix in the JOR formula
ps = np.array(np.random.rand(n,dim)) # initial positions of the turtles
#print("ps =\n" + str(a))

a = (1./(n-1.))*(np.ones((n,n)) - np.identity(n)) #a Matrix in the JOR formula
#print("a =\n" + str(a))

z = ps

#################################################
# finds the b Matrix in the JOR formula given 
# the formation configuration z
#################################################
def findB(z):
	global b
	z = z.reshape(1, n*dim, order='F')
	kron = np.kron(np.identity(dim), (d - a))
	b = scale * (np.inner(kron, z)).reshape(n,dim, order='F')
	
findB(z) # b Matrix in the JOR formula

it = 1			# Number of iteration before goal update
leader = -1		# TODO for leader turtle id of a formation


#################################################
# 2D rotation matrix for a given theta
#################################################
def R(theta):
	return np.array([[ np.cos(theta), np.sin(theta)],
			[-np.sin(theta), np.cos(theta)]])




#################################################
# Teleop Functions
#################################################
def rotate_absolute(rad):
	global theta
	diff = rad - theta;
	theta = rad;
	print( "Formation has been rotated by %f radians." % diff) 

# [<]     rotates the formation counter-clockwise
def rotate_left(rad=np.pi/10.):
	global theta
	theta += rad;
	print( "Formation has been rotated by %f radians." % rad) 

# [>]     rotates the formation clockwise
def rotate_right(rad=-np.pi/10.):
	global theta
	theta += rad;
	print( "Formation has been rotated by %f radians." % rad) 

# [+]     adds an agent
def spawn_turtle():
	global n, d, ps, a, z, b
	if( n < agents_limit ):
		n += 1		        # Number of turtles
		d = np.identity(n)	# d Matrix in the JOR formula
		tmp = np.array(np.random.rand(1, dim))
		ps = np.append(ps, tmp, axis=0) 
		if ( n > 1 ):
			a = (1./(n-1.))*(np.ones((n,n)) - np.identity(n))
			z = ps
			findB(z)
		print( "Added an agent" )
	else:
		print( "Cannot add any more agent (Limit = %d)" % agents_limit )
	
# [-]     removes an agent
def kill_turtle():
	global n, d, ps, a, z, b
	if( n > 0 ):
		n -= 1		        # Number of turtles
		d = np.identity(n)	# d Matrix in the JOR formula
		ps = np.delete(ps, n, 0) 
		if ( n > 1 ):
			a = (1./(n-1.))*(np.ones((n,n)) - np.identity(n))
			z = ps
			findB(z)
		print( "Removed an agent" )
	else:
		print( "There is no agent to remove" )

# [c]     removes all agents
def kill_all_turtles():
	for i in range(n):
		kill_turtle()
	print( "Removed all agents" )

# [i]     collapses the formation inward
def collapse_in(ratio=.9):
	global scale 
	scale *= .9
	findB(z)
	print( "Formation has collapsed inward by %d percent." % (100-ratio*100)) 
	
# [o]     spreads the formation outward
def spread_out(ratio=1.1):
	global scale
	scale *= 1.1
	findB(z)
	print( "Formation has spread outward by %d percent." % (ratio*100-100)) 

# [l]     changes the formation to a line     (2+ agents)
def line_formation():
	global z, theta, form
	if( 2 <= n ):
		z = LINE[0:n, :]
		findB(z)
		theta = 0
		form = 2
		print("Line formation!")
		
	else:
		print("You need at least 2 agents to form a line.")

# [s]     changes the formation to a square   (4 agents)
def square_formation():
	global z, theta, form
	if( n == 4):
		z = SQUARE
		findB(z)
		theta = 0
		form = 4
		print("Square formation!")
	else:
		print("Square formation is only availible for 4 agents.")

# [t]     changes the formation to a triangle (3 agents)
def triangle_formation():
	global z, theta, form
	if( n == 3 ):
		z = TRIANGLE
		findB(z)
		theta = 0
		form = 3
		print("Triangle formation!")
	else:
		print("Triangle formation is only availible for 3 agents.")

def custom_formation(c):
	global z, theta, form
	z = c
	findB(z)
	theta = 0
	form = -1
	print("Custom formation!")

#########################################################
# Call back to handle teleop codes
# Availiable opcodes are:  
#	[0-7]   changes the number of agents
#	[+]     adds an agent
#	[-]     removes an agent
#	[c]     removes all agents
#	[h]     shows all availialbe opcodes        (this list)
#	[i]     collapses the formation inward
#	[l]     changes the formation to a line     (2+ agents)
#	[o]     spreads the formation outward
#	[t]     changes the formation to a triangle (3 agents)
#	[s]     changes the formation to a square   (4 agents)
#	[<]     rotates the formation counter-clockwise
#	[>]     rotates the formation clockwise
#########################################################
def handleTeleopCB(msg):
	global theta
	op = msg.data
	opNum = ord(op) - ord('0')
	if( 0 <= opNum and opNum <= agents_limit ):
		diff = opNum - n
		if ( diff > 0 ):
			for i in range(diff):
				spawn_turtle()
		elif ( diff < 0):
			for i in range(-1*diff):
				kill_turtle()

	elif( op == '+' ):
		spawn_turtle()

	elif( op == '-' ):
		kill_turtle()

	elif( op == 'c' ):
		kill_all_turtles()

	elif ( op == 'l' ):
		line_formation()

	elif ( op == '<' ):
		rotate_left()

	elif ( op == ">" ):
		rotate_right()

	elif ( op == 's' ):
		square_formation()

	elif ( op == 't' ):
		triangle_formation()

	elif ( op == 'i' ):
		collapse_in()

	elif ( op == 'o' ):
		spread_out()

	elif ( op == 'h' ):
		print("Availiable opcodes are:\n"  
			"  [0-7]   changes the number of agents\n"
			"  [+]     adds an agent\n"
			"  [-]     removes an agent\n"
			"  [c]     removes all agents\n"
			"  [h]     shows all availialbe opcodes        (this list)\n"
			"  [i]     collapses the formation inward\n"
			"  [l]     changes the formation to a line     (2+ agents)\n"
			"  [o]     spreads the formation outward\n"
			"  [t]     changes the formation to a triangle (3 agents)\n"
			"  [s]     changes the formation to a square   (4 agents)\n"
			"  [<]     rotates the formation counter-clockwise\n"
			"  [>]     rotates the formation clockwise\n")
			
	else:
		print("Availiable opcodes are: {0-7,+,-,c,h,i,l,o,s,t,<,>}")



#################################################
# Ros Subscriber Callback for Current Positions
#################################################
def handleTurtlePoseCB(msg, turtleNum):
	global ps
	if( turtleNum < n ):
		ps[turtleNum][0] = msg.pose.position.x
		ps[turtleNum][1] = -msg.pose.position.y


#################################################
# Ros Subscriber Callback for Current Positions
#################################################
def handleTransformCB(msg):

	translation = msg.transforms[0].transform.translation
	tmp_n = int(translation.x)
	tmp_form = int(translation.y)
	tmp_theta = translation.z

	tmp_scale = msg.transforms[0].transform.rotation.z
	
	if( 0 <= tmp_n and tmp_n <= agents_limit ):
		diff = tmp_n - n
		if ( diff > 0 ):
			for i in range(diff):
				spawn_turtle()
		elif ( diff < 0):
			for i in range(-1*diff):
				kill_turtle()
	if( tmp_form != form ):
		if( tmp_form == 0 ):
			# custom formation
			c = np.empty([0,dim])
			for i in range(tmp_n): 
				translation = msg.transforms[i+1].transform.translation
				p = [translation.x, -translation.y]
				print "c" + str(c)
				print "p" + str(p)
				c = np.append(c, [p], axis=0)
			custom_formation(c)


		elif( tmp_form == 2 ):
			line_formation()
		elif( tmp_form == 3):
			triangle_formation()
		elif( tmp_form == 4):
			square_formation()

	if( tmp_theta != theta ):
		rotate_absolute(tmp_theta)

	if( tmp_scale != scale ):
#		if( tmp_scale > 1):
#			spread_out(tmp_scale)
#		else:
#			collapse_in(tmp_scale)
		global scale
		scale=tmp_scale
		findB(z)
#	print(str(tmp_theta))	


#######################################################
# Algorithm for Updating the Goal Positions
# ps - nxd matrix representing position of all turtles
# b  - b matrix in JOR algorithm
# theta - angle of the matrix
# h - convergence rate [0-1] 
#     0 -> never 
#     1 -> instantly
#######################################################
def updateGoalPoses(ps, b, theta=0., h=.25):
    # declare an empty matrix of dim columns
	toReturn = np.empty([0,dim])
    # calculate Rb
	rb = np.dot(b, R(theta))
	
    # loop through position of each turtle
	for i in range(n): 
		#p_i = ps[i]
		#print p_i
		summ = np.zeros(dim)
		
		#p_r = np.inner(np.ones((n, 1)), np.transpose([ps[i]])) - ps
		#print( "ps  =\n" + str(ps)  )
		#print( "p_r =\n" + str(p_r) )

		summ = np.dot(a[i], ps)
		#summ = np.dot(a[i].transpose)
		#print ("summ = \n" + str(summ))
		#print ("rb[i]= \n" + str(rb[i]))


		p_new = (1.0-h)*ps[i] + (h/d[i][i])*(summ + rb[i])
		#print( p_new )
		toReturn = np.append(toReturn, [p_new], axis=0)
	
		#print( "ps  = " + str(ps))
		#print( "ps' = " + str(toReturn))
	return toReturn


#################################################
# Formation stabilization method
#################################################
def formation_stablization():
	global ps, b
	# Initialize initial condition

	#pub = rospy.Publisher('formation', String, queue_size=10)
	#sub = rospy.Subscriber('turtle1/pose', turtlesim.msg.Pose, Pose1Callback)

	rospy.init_node('formation', anonymous=True)

	rospy.Subscriber('/formation_teleop', String, handleTeleopCB)
	rospy.Subscriber('/formationSpecifications', TFMessage, handleTransformCB)

	# Subscriber for getting the position of each turtle
	for i in range(agents_limit):
		rospy.Subscriber('/turtle%d/pose' % i,
                         PoseStamped, # TODO change this to PoseWithName
                         handleTurtlePoseCB,
                         i)
	# Publishers
	pubs = [rospy.Publisher('/turtle%d/goal_pose'% i, PoseStamped, queue_size=1) for i in range(agents_limit)]

	rate = rospy.Rate(10) # 10hz

	while not rospy.is_shutdown():
		for i in range(it):
			if( n > 1 ):
				ps = updateGoalPoses(ps, b, theta)
			#print( ps )
		for i in range(n):
			if(i != leader):
				msg = PoseStamped() 
				msg.pose.position.x = ps[i][0]
				msg.pose.position.y = ps[i][1]
				pubs[i].publish(msg)

		#rate.sleep()
		rospy.sleep(0.1) # Update 10 times per second


#################################################
# Main Method - Entry point of the program
#################################################
if __name__ == '__main__':
    try:
        formation_stablization()
    except rospy.ROSInterruptException:
        pass

