#include <iostream>
#include <stdio.h>
#include <ros/ros.h>
#include <geometry_msgs/Twist.h>
#include "geometry_msgs/PoseWithCovarianceStamped.h"
#include <geometry_msgs/PoseStamped.h>
#include <geometry_msgs/Vector3.h>
#include <geometry_msgs/PoseArray.h>
#include <tf/tf.h>
#include <fstream>
#include <math.h>
#include "PoseWithName.h"
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>
#include "eigen/Eigen/Dense"
#include <vector>
#include <stdlib.h>
#include "std_msgs/String.h"
#include <std_msgs/Bool.h>
#include <std_msgs/Float64.h>
#include <sstream>
#include <search.h>
#include <stdlib.h>
#include "VoronoiDiagramGenerator.h"
#include <tf2_msgs/TFMessage.h>
#include "CoMGenerator.h"

using namespace std;
double T=25;
bool gotPose=false;
bool freeBoundaryFlag=false;
bool trackingFlag=false;
const int maxNum=50;
int countD;
float xValues[maxNum];
float yValues[maxNum];   
int selectedIndices[maxNum]; 

float sigma=.15;
float lastX[5][maxNum];
float lastY[5][maxNum];
float delayX[5][maxNum];
float delayY[5][maxNum];
float muX=0;
float muY=0;

float minX = -.455, maxX = .455;    
float minY = -.515, maxY = .515;

float StandardminX = -.455, StandardmaxX = .455;    
float StandardminY = -.515, StandardmaxY = .515;

//float minX = -.335, maxX = .335;    
//float minY = -.2, maxY = .9;
float filterX[maxNum];
float filterY[maxNum];
int weirdReadings[maxNum];
//geometry_msgs::PoseArray centroidPositions;

// Turtlebot indexes
const int MICHELANGELO_INDEX = 2;
const int DONATELLO_INDEX = 3;
const int RAPHAEL_INDEX = 4;
const int LEONARDO_INDEX = 5;
const int BOTICELLI_INDEX = 6;
const int GIOTTO_INDEX = 7;
const int BELLINI_INDEX = 8;
const int GHIBERTI_INDEX = 9;
const int MASACCIO_INDEX = 10;
const int TITIAN_INDEX = 1;


const int PICASSO_INDEX = 41;
const int DALI_INDEX = 42;
const int GOYA_INDEX = 43;

// This message is an array of PoseStamped that contains the centroids
// to be published to each individual turtlebot topic
geometry_msgs::PoseStamped centroidPosesStamped[GOYA_INDEX + 1];
geometry_msgs::Twist cDotTurtles[GOYA_INDEX+1];

void boundaryCallback(const std_msgs::Bool::ConstPtr& bPtr){
	if (bPtr->data==false){
		freeBoundaryFlag=false;
	}
	else if(bPtr->data==true){
		freeBoundaryFlag=true;
	}
}

void poseCallback(const tf2_msgs::TFMessage::ConstPtr& pose)
{
	countD=0;
	//centroidPositions = *pose;
	gotPose=true;
	const tf2_msgs::TFMessage& msg=*pose;

	for (int i=0;i<maxNum;i++)	
	{
		if ( (msg.transforms[i].transform.translation.x!=0 || msg.transforms[i].transform.translation.y!=0) && msg.transforms[i].transform.translation.x > minX & msg.transforms[i].transform.translation.x < maxX && msg.transforms[i].transform.translation.y < maxY && msg.transforms[i].transform.translation.y > minY){
			xValues[i]=msg.transforms[i].transform.translation.x;	
			yValues[i]=msg.transforms[i].transform.translation.y;
			selectedIndices[countD]=i;	
			countD++;		
		}
	}
	 
}

void gCallback(const geometry_msgs::PoseArray::ConstPtr& gaussPtr)
{
		trackingFlag=false;
		if (gaussPtr->poses[0].position.z!=0){
		sigma=gaussPtr -> poses[0].position.z;
		muX=gaussPtr-> poses[0].position.x;
		muY=gaussPtr-> poses[0].position.y;
		if (gaussPtr->poses[0].orientation.w==1){
		trackingFlag=true;
		}
		}

	if (freeBoundaryFlag==true){
	minX=-1+muX;
	maxX=1+muX;

	minY=-1+muY;
	maxY=1+muY;
	}
	else{
	minX=StandardminX;
	maxX=StandardmaxX;

	minY=StandardminY;
	maxY=StandardmaxY;
	}

}

Matrix uniqueVertices(50,2);

Matrix::Matrix(int r, int c, float data) {
    rows = r;
    cols = c;
    elements= new float*[rows];
    for (int i=0; i<rows; i++)
    {
        elements[i] = new float[cols];
        for (int j=0; j < cols; j++)    {   elements[i][j]=data;    }
    }
}
Matrix::~Matrix(){
}
void Matrix::setMatrix(int r, int c, float data) {
    elements = new float*[r];
    for (int i=0; i<r; i++)
    {
        elements[i] = new float[c];
        for (int j=0; j<c; j++)
        {   elements[i][j]=data;  }
    }
    setRows(r);
    setCols(c);
}
void Matrix::printArray(string name) {
    cout << name << ": " << endl;
    for(int i=0;i<rows;i++) {
        for(int j=0;j<cols;j++) {   cout << elements[i][j] << "\t";   }
        cout << endl;
    }
    cout << endl;
}
int Matrix::setRows(int r) {
    rows = r;
    return rows;
}
int Matrix::setCols(int c) {
    cols = c;
    return cols;
}
void Matrix::setElement(int r, int c, float data){
    elements[r][c]=data;
}

float CoMGenerator::truncate(float num, int precision)        //truncate a floating point number
{   num = num*pow(10,precision);
    num = round(num);
    num = num/pow(10,precision);
    return num;
}

float CoMGenerator::distance(float siteX, float siteY, float vertX, float vertY)      //calculate the distance between a site and a vertice
{   float dist = sqrt(pow((vertY-siteY),2)+pow((vertX-siteX),2));
    dist = truncate(dist,3);        //using 4 as precision was identified some errors in calculations. So use 3 or less as precision
    return dist;
}


CoMGenerator::~CoMGenerator(){}

void CoMGenerator::getUniqVertices(std::vector<float> allVerticesVector)
{
    for (int i=0; i<allVerticesVector.size(); i++)  //as numbers are type float, need to truncate them in order to work
    {   allVerticesVector[i]=truncate(allVerticesVector[i], 3); }
    
    Matrix allVerticesMatrix(50,2); //initialize the matrix with 50 rows, but it will be replaced by the next line. So instead of 50, it can be any number
    allVerticesMatrix.rows = allVerticesVector.size()/2;
    
    //transform vector "allVerticesVector into a matrix
    int k=0;
    for (int i=0; i<allVerticesMatrix.rows; i++)
    {
        for (int j=0; j<2; j++) {    allVerticesMatrix.setElement(i,j,allVerticesVector[k]); k++;    }
    }
    //allVerticesMatrix.printArray("All Vertices");
    
    std::vector<int> repeatIndex;    //vector to store the indices(rows) of repeated vertices
    
    //find indices(rows) that repeats its elements == vertices that appear more than once
    for (int i=0; i<allVerticesMatrix.rows; i++){
        for (int j=i+1; j<allVerticesMatrix.rows; j++) {
            if (allVerticesMatrix.elements[i][0] == allVerticesMatrix.elements[j][0] && allVerticesMatrix.elements[i][1] == allVerticesMatrix.elements[j][1])
            {
                repeatIndex.push_back(i);       //if elements of a row repeats, add its index in the vector
                j=allVerticesMatrix.rows;       //break loop once it's found, otherwise if it repeats more than once, it'll store same index twice
            }
        }
    }
    
    //store non repeated vertices in the "uniqueVertices" matrix
    uniqueVertices.setRows(allVerticesMatrix.rows-repeatIndex.size());
    int a=0;
    for (int i=0; i<allVerticesMatrix.rows; i++) {
        int count=0;
        for (int j=0; j<repeatIndex.size(); j++) //make sure the index i is different from all indices storage in repeatIndex
        {
            if (i!=repeatIndex[j])  count++;
        }
        if (count==repeatIndex.size())   //if index 'i' isn't storage in repeatIndex: we can store the values of row (i) in uniqueVertices
        {
            uniqueVertices.setElement(a, 0, allVerticesMatrix.elements[i][0]);
            uniqueVertices.setElement(a, 1, allVerticesMatrix.elements[i][1]);
            a++;
        }
    }
    //uniqueVertices.printArray("Unique Vertices");
}

void CoMGenerator::getIndices(Matrix indicesMatrix, Matrix Sites, int nSites, int nVertices)
{
    const float E = 0.002;  //error value
    
    //find out which vertice is part of which cell
    for (int i=1; i<nSites; i++) {
        for (int j=1; j<=nVertices; j++) {             //go through each vertice to find what site is nearer to that vertice
            if (indicesMatrix.elements[i-1][j-1]<0) {  //check if element of matrix Indices is still with initial value -1, avoiding unecessary calculations
                int p=-1;
                for (int k=i; k<nSites; k++){          //compare the site (i) to every other site
                    float a = distance(Sites.elements[i-1][0], Sites.elements[i-1][1], uniqueVertices.elements[j-1][0], uniqueVertices.elements[j-1][1]);
                    float b = distance(Sites.elements[k][0], Sites.elements[k][1], uniqueVertices.elements[j-1][0], uniqueVertices.elements[j-1][1]);
                    if (a < b-E)    //if distance from site (i) to vertice (j) is smaller than from site (k) to vertice (j): vertice is not part of cell (k)
                    {   indicesMatrix.setElement(k, j-1, 0);  }
                    else if (a <= b+E && a >= b-E)  //if distances between two sites and a vertice are equal: vertice is part of both cells
                    {
                        indicesMatrix.setElement(i-1, j-1, j);
                        indicesMatrix.setElement(k, j-1, j);
                        p=k;    //store the value of indice (k), in case that the distance is equal but the cell is not the closest to the vertice (j)
                    }
                    else        //if distance from site (i) to vertice (j) is bigger than from site (k): vertice is not part of cell (i)
                    {
                        indicesMatrix.setElement(i-1, j-1, 0);
                        if (p>0) indicesMatrix.setElement(p, j-1, 0);   //if two sites have same distance to a vertice, but their not the closest to that vertice, it is used to correct that failure
                        k=nSites+1; //break loop, otherwise it will go back and compare the site (i) again with other sites, which is unnecessary
                    }
                }
            }
        }
    }
    for (int i=0; i<nSites; i++)    //some elements will still have the value -1. It means that those vertices are actually the plane's edges.
    {
        for (int j=0; j<nVertices; j++)
        {   if (indicesMatrix.elements[i][j]<0) indicesMatrix.setElement(i, j, j+1); }
    }                          //(they're only part of one single cell)
    
    //indicesMatrix.printArray("Indices");
}

void CoMGenerator::setIndicesOrder(Matrix angleMatrix, Matrix indicesMatrix, int nVertices, int row)
{
    double a;
    int b;
    for (int i=0; i<nVertices; i++){
        for (int j=(i+1); j<nVertices; j++){
            if (angleMatrix.elements[row][i] > angleMatrix.elements[row][j]) {
                a = angleMatrix.elements[row][i];                       //This part make swaps, ordering least to greatest
                b = indicesMatrix.elements[row][i];                     //for the angles, and aligns corrsponding indices
                angleMatrix.elements[row][i] = angleMatrix.elements[row][j];
                indicesMatrix.elements[row][i] = indicesMatrix.elements[row][j];
                angleMatrix.elements[row][j] = a;
                indicesMatrix.elements[row][j] = b;
            }
        }
    }
}

void CoMGenerator::sortIndices(Matrix sitesPosition, Matrix indicesMatrix, int nSites, int nVertices, Matrix angleMatrix)
{
    double xyVector[2];
    for (int i=0; i<nSites; i++){
        for (int j=0; j<nVertices; j++){
            if (indicesMatrix.elements[i][j] != 0) {
                for (int k=0; k<2; k++) //xyVector[0] = xVertice-xSite  and xyVector[1] = yVertice-ySite
                {
                    xyVector[k] = ( uniqueVertices.elements[ int(indicesMatrix.elements[i][j]-1) ][k] - sitesPosition.elements[i][k] );
                }
                angleMatrix.elements[i][j] = atan2 (xyVector[1],xyVector[0]) * (180.00 / PI);
            }
        }
        setIndicesOrder(angleMatrix, indicesMatrix, nVertices, i);
    }
    //indicesMatrix.printArray("Indices in Order");
    //angleMatrix.printArray("Angle Matrix");
}

void CoMGenerator::getLocalIndex(Matrix indicesMatrix, int nVertices, int row, vector<int>& localIndex, int& noLocVertices)
{
    for (int j=0; j<nVertices; j++)     //store indices different from zero
    {
        if (indicesMatrix.elements[row][j] != 0) localIndex.push_back (indicesMatrix.elements[row][j]);
    }
    localIndex.push_back(localIndex.front());   //at the end, store the first element from the vector again
    noLocVertices = int(localIndex.size());
}

// Gets the center of mass for any triangle
void CoMGenerator::getTriangleCOM(double &Mass, double &c_x, double &c_y, double xpoints[3], double ypoints[3], int nGaussPoints)
{
    //Gauss Info
    double rGauss[6] = {.4459,.4459,.1081,.0916,.0916,.8168,};
    double sGauss[6] = {.4459,.1081,.4459,.0916,.8168,.0916};
    double w[6] 	       = {.2234,.2234,.2234,.1099,.1099,.1099};
    nGaussPoints=6;
    
    //Local Coordinates
    double r[3] = {0, 1, 0};
    double s[3] = {0, 0, 1};
    
    //cout << "X: [" << xpoints[0] << ", " << xpoints[1] << ", " << xpoints[2] << "]" << endl;
    //cout << "Y: [" << ypoints[0] << ", " << ypoints[1] << ", " << ypoints[2] << "]" << endl;
    
    //Determinant in local cooridnates (r,s)
    double detM = ( 1.0*(r[1]*s[2]-s[1]*r[2]) + r[0]*(1.0*s[2]-1.0*s[1]) + s[0]*(1.0*r[2]-r[1]*1.0) );
    //Determinant in parent coordiantes (x,y)
    double detK = ( 1.0*(xpoints[1]*ypoints[2]-ypoints[1]*xpoints[2]) + xpoints[0]*(1.0*ypoints[1]-1.0*ypoints[2]) +
                   ypoints[0]*(1.0*xpoints[2]-xpoints[1]*1.0) );
    
    //Set Loop Sums to Zero
    double E_x = 0;
    double E_y = 0;
    Mass = 0;
    
    //Declare Variables
    double N1, N2, N3;
    double x, y;
    double DENSITY;
    
    for (int i=0; i<nGaussPoints; i++){
        //Shape functions in local coordinates (r,s)
        N1 = detM*(r[1]*s[2] - r[2]*s[1] + (s[1]-s[2])*rGauss[i] + (r[2]-r[1])*sGauss[i]);
        N2 = detM*(r[2]*s[0] - r[0]*s[2] + (s[2]-s[0])*rGauss[i] + (r[0]-r[2])*sGauss[i]);
        N3 = detM*(r[0]*s[1] - r[1]*s[0] + (s[0]-s[1])*rGauss[i] + (r[1]-r[0])*sGauss[i]);
        //Mapping from local (r,s) to parent (x,y) coordinates
        x = N1*xpoints[0] + N2*xpoints[1] + N3*xpoints[2];
        y = N1*ypoints[0] + N2*ypoints[1] + N3*ypoints[2];
        
	        


        //Density Funtion!!!!!
        //||----------------||
        //||----------------||sigma*(3-sqrt((x-muX)*(x-muX)+(y-muY)*(y-muY)))/1000000000+
	DENSITY =1/(sigma*sqrt(2*3.14))  *  exp(-sqrt((x-muX)*(x-muX)+(y-muY)*(y-muY))/(2*pow(sigma,2))); 
	   //DENSITY = -1*(sqrt((x-muX)*(x-muX)+(y-muY)*(y-muY)));
	//DENSITY = 1/(sqrt((x-muX)*(x-muX)+(y-muY)*(y-muY)));
//cout << DENSITY << "\n\n";
		//DENSITY=1;
        //||----------------||
        //||----------------||
        
        //Apriori Expected Values and Mass
        E_x = E_x + w[i]*x*DENSITY;
        E_y = E_y + w[i]*y*DENSITY;
        Mass = Mass + w[i]*DENSITY;
    }
    //cout << "DETK: " << detK << endl;
    //Expected Values and Mass With Jacobian
    E_x = (detK/2.0)*E_x;
    E_y = (detK/2.0)*E_y;
    Mass = (detK/2)*Mass;
    
    //cout << "Ex, Ey: " << E_x << ", " << E_y << endl;
    //cout << "Mass: " << Mass << endl;
    
    //Center of Mass
    c_x = E_x/Mass;
    c_y = E_y/Mass;
    
    //cout << "\tCx, Cy: " << c_x << ", " << c_y << endl;
}

//Get the center of mass for any composite
void CoMGenerator::getCompositeCOM(double &c_x, double &c_y, double x[], double y[], double Mass[], int nLocalVertices)
{
    //Set to Zero
    double totalMass = 0;
    c_x = 0;
    c_y = 0;
    
    for(int i=0; i<nLocalVertices; i++){
        totalMass = totalMass + Mass[i];
        c_x = c_x + x[i]*Mass[i];
        c_y = c_y + y[i]*Mass[i];
    }
    
    //cout << "c_x, c_y: " << c_x << ", " << c_y << endl;
    
    c_x = c_x/totalMass;
    c_y = c_y/totalMass;
}

//Gets the center of mass for Cell
void CoMGenerator::getCenterOfMass(double &Mass, double &c_x, double &c_y, Matrix localVertices, double Site[2], int nLocalVertices, int nGaussPoints, Matrix centerOfMass, int row)
{
    //Declare Variables
    int k = (nLocalVertices-1);
    double MassVector[k], c_xVector[k], c_yVector[k];
    double xpoints[3], ypoints[3];
    
    //Collect COM's -MODDED BY AARON
    xpoints[2] = Site[0];   
    ypoints[2] = Site[1];
	double cXp=0;
	double cYp=0;
	
	for (int i=0;i<nLocalVertices;i++){
		cXp=cXp+localVertices.elements[i][0];
		cYp=cYp+localVertices.elements[i][1];
	}
	xpoints[2]=cXp/nLocalVertices;
	ypoints[2]=cYp/nLocalVertices;


    for(int i=0; i<k; i++){
        //Form Triangle
        xpoints[0] = localVertices.elements[i][0];  ypoints[0] = localVertices.elements[i][1];
        xpoints[1] = localVertices.elements[i+1][0];    ypoints[1] = localVertices.elements[i+1][1];
        
        //Get Each Triangle's Center of Mass,poseCallbac
        getTriangleCOM(Mass, c_x, c_y, xpoints, ypoints, nGaussPoints);
        MassVector[i] = Mass;
        c_xVector[i] = c_x;
        c_yVector[i] = c_y;
    }
    getCompositeCOM(c_x, c_y, c_xVector, c_yVector, MassVector, nLocalVertices);
    centerOfMass.elements[row][0] = c_x;
    centerOfMass.elements[row][1] = c_y;
}

Matrix CoMGenerator::generateCenterOfMass(std::vector<float> allVertices, Matrix sitesPosition, int nSites)
{
    getUniqVertices(allVertices);
    
    Matrix indicesMatrix(nSites, uniqueVertices.rows,-1); //initialize IndicesMatrix with value(-1). It'll store indices of vertices related to each site.
    getIndices(indicesMatrix, sitesPosition, nSites, uniqueVertices.rows);
    
    Matrix angleMatrix(nSites, uniqueVertices.rows);
    sortIndices(sitesPosition, indicesMatrix, nSites, uniqueVertices.rows, angleMatrix);
    
    Matrix centerOfMass(nSites,2);

	
    
    double Mass, c_x, c_y;
    int nGaussPoints = 3;
    
    int nLocalVertices = 0;
    for (int siteNumber=0; siteNumber<nSites; siteNumber++){
        vector<int> localIndex;
        
        //Get Local Index: Supporting function. Returns: (localIndex, noLocIndices)
        getLocalIndex(indicesMatrix, uniqueVertices.rows, siteNumber, localIndex, nLocalVertices);
        
        Matrix localVertPos(nLocalVertices,2);
        

        cout << endl;
        cout << "DICTIONARY FOR SITE " << (siteNumber + 1) << ":" << endl;
        cout << "noLocVertices = "<< nLocalVertices << endl;
        cout << "localIndex    = ";

        for (int i=0; i<nLocalVertices; i++){
            cout << localIndex[i] << ", ";
        }
        cout << endl;
      
        for (int i=0; i<nLocalVertices; i++){
            localVertPos.elements[i][0]=uniqueVertices.elements[localIndex[i]-1][0];
            localVertPos.elements[i][1]=uniqueVertices.elements[localIndex[i]-1][1];
        }
        //localVertPos.printArray("Local Vertices");
        
        //GET CENTER OF MASS: Returns (CenterOfMass)
        double Site[2]={sitesPosition.elements[siteNumber][0], sitesPosition.elements[siteNumber][1]};
		


        getCenterOfMass(Mass, c_x, c_y, localVertPos, Site, nLocalVertices, nGaussPoints, centerOfMass, siteNumber);
    }
    //cout << endl;
    
    //print CoM Matrix to work with Matlab
/*
    cout << "cmX=[";
    for (int i=0; i<centerOfMass.rows; i++){
    if (i==centerOfMass.rows-1) cout << centerOfMass.elements[i][0] << "];" << endl;
    else cout << centerOfMass.elements[i][0] << ", "; }
    cout << "cmY=[";
    for (int i=0; i<centerOfMass.rows; i++){
    if (i==centerOfMass.rows-1) cout << centerOfMass.elements[i][1] << "];" << endl;
    else cout << centerOfMass.elements[i][1] << ", "; }
    */
    
    //centerOfMass.printArray("Center of Mass");
    
    return centerOfMass;
}

// This function translates the list of transform messages that contain the centroids of the turtlebots
// into a list of PoseStamped messages required by move_base. This function uses a for loop that assumes
// the turtlebots are indexed in a consecutive and increasing order, with MICHELANGELO_INDEX being lowest
// and TITIAN_INDEX being highest.
void transform_to_pose(tf2_msgs::TFMessage centroidPositions, tf2_msgs::TFMessage cdots)
{
	for(int i = TITIAN_INDEX; i <= MASACCIO_INDEX; i++)
	{
		centroidPosesStamped[i].pose.position.x = centroidPositions.transforms[i].transform.translation.x;
		centroidPosesStamped[i].pose.position.y = -centroidPositions.transforms[i].transform.translation.y;
		centroidPosesStamped[i].pose.position.z = centroidPositions.transforms[i].transform.translation.z;
		centroidPosesStamped[i].pose.orientation = centroidPositions.transforms[i].transform.rotation;
		centroidPosesStamped[i].pose.orientation.w=-1;
		
		cDotTurtles[i].linear.x = cdots.transforms[i].transform.translation.x;
		cDotTurtles[i].linear.y = cdots.transforms[i].transform.translation.y;

	}

	for(int i = PICASSO_INDEX; i <= GOYA_INDEX; i++)
	{
		centroidPosesStamped[i].pose.position.x = -1;
		centroidPosesStamped[i].pose.position.y = -1;
		centroidPosesStamped[i].pose.position.z = -1;
		centroidPosesStamped[i].pose.orientation = centroidPositions.transforms[i].transform.rotation;
		centroidPosesStamped[i].pose.orientation.w=-1;
		
		cDotTurtles[i].linear.x = cdots.transforms[i].transform.translation.x;
		cDotTurtles[i].linear.y = cdots.transforms[i].transform.translation.y;
	}
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////
int main(int argc, char **argv)
{
    
    	//Store the position of the sites in a Matrix
	tf2_msgs::TFMessage centroidPositions;
	tf2_msgs::TFMessage lastCentroidPositions;
	tf2_msgs::TFMessage cDot;
    	centroidPositions.transforms.resize(maxNum);
	lastCentroidPositions.transforms.resize(maxNum);
	cDot.transforms.resize(maxNum);
	geometry_msgs::PoseStamped ps;
    	int nSites = Matrix_Size(xValues);
    	Matrix sitesPos(nSites,2);
    	for(int i=0; i<Matrix_Size(xValues);i++){
        	sitesPos.setElement(i, 0, xValues[i]);      //sitePos.elements[i][0] = xValues[i];
        	sitesPos.setElement(i, 1, yValues[i]);
    	}

	for (int i=0;i<5;i++){
		for (int j=0;j<maxNum;j++){
			lastX[i][j]=0;
			lastY[i][j]=0;
			
		}
	}

	for (int i=0;i<maxNum;i++){
		weirdReadings[i]=0;
	}
    
	ros::init(argc, argv, "Centroid_Generator"); 
	ros::start();
	ros::Rate loop_rate(T); //Set Ros frequency to 50/s (fast)
	ros::NodeHandle nh_;
	ros::Subscriber pos_sub_ ,g_sub_,boundary_sub_;
	ros::Publisher centroid_pub_ ;
	ros::Publisher cDot_pub_;
	ros::Publisher goal_for_android;

	// Declare turtlebot publishers
	ros::Publisher michel_pub,michel_pub_Android;
	ros::Publisher dona_pub,dona_pub_Android;
	ros::Publisher bern_pub,bern_pub_Android;
	ros::Publisher leo_pub,leo_pub_Android;
	ros::Publisher boti_pub,boti_pub_Android;
	ros::Publisher gio_pub,gio_pub_Android;
	ros::Publisher bel_pub, bel_pub_Android;
	ros::Publisher ghiber_pub, ghiber_pub_Android;
	ros::Publisher masa_pub,masa_pub_Android;
	ros::Publisher titi_pub, titi_pub_Android;
	ros::Publisher picasso_pub, picasso_pub_Android;
	ros::Publisher dali_pub, dali_pub_Android;
	ros::Publisher goya_pub, goya_pub_Android;
	

	ros::Publisher michel_pub_cdot;
	ros::Publisher dona_pub_cdot;
	ros::Publisher bern_pub_cdot;
	ros::Publisher leo_pub_cdot;
	ros::Publisher boti_pub_cdot;
	ros::Publisher gio_pub_cdot;
	ros::Publisher bel_pub_cdot;
	ros::Publisher ghiber_pub_cdot;
	ros::Publisher masa_pub_cdot;
	ros::Publisher titi_pub_cdot;
ros::Publisher picasso_pub_cdot;
ros::Publisher dali_pub_cdot;
ros::Publisher goya_pub_cdot;

	pos_sub_= nh_.subscribe<tf2_msgs::TFMessage>("/poseEstimationC", 1000,poseCallback);
	g_sub_= nh_.subscribe<geometry_msgs::PoseArray>("/gauss", 1000,gCallback);
	boundary_sub_= nh_.subscribe<std_msgs::Bool>("/gauss/boundaryFlag", 1000,boundaryCallback);
	centroid_pub_ = nh_.advertise<tf2_msgs::TFMessage>("Centroids", 1000, true);
	cDot_pub_ = nh_.advertise<tf2_msgs::TFMessage>("cDot", 1000, true);
	goal_for_android = nh_.advertise<geometry_msgs::PoseStamped>("/poseEstimation", 1000, true);
	// Initialize turtlebot publishers
	michel_pub = nh_.advertise<geometry_msgs::PoseStamped>("/michelangelo/move_base_simple/goal", 1000, true);
	dona_pub = nh_.advertise<geometry_msgs::PoseStamped>("/donatello/move_base_simple/goal", 1000, true);
	bern_pub = nh_.advertise<geometry_msgs::PoseStamped>("/raphael/move_base_simple/goal", 1000, true);
	leo_pub = nh_.advertise<geometry_msgs::PoseStamped>("/leonardo/move_base_simple/goal", 1000, true);
	boti_pub = nh_.advertise<geometry_msgs::PoseStamped>("/boticelli/move_base_simple/goal", 1000, true);
	gio_pub = nh_.advertise<geometry_msgs::PoseStamped>("/giotto/move_base_simple/goal", 1000, true);
	bel_pub = nh_.advertise<geometry_msgs::PoseStamped>("/bellini/move_base_simple/goal", 1000, true);
	ghiber_pub = nh_.advertise<geometry_msgs::PoseStamped>("/ghiberti/move_base_simple/goal", 1000, true);
	masa_pub = nh_.advertise<geometry_msgs::PoseStamped>("/masaccio/move_base_simple/goal", 1000, true);
	titi_pub = nh_.advertise<geometry_msgs::PoseStamped>("/titian/move_base_simple/goal", 1000, true);
picasso_pub = nh_.advertise<geometry_msgs::PoseStamped>("/picasso/move_base_simple/goal", 1000, true);
	dali_pub = nh_.advertise<geometry_msgs::PoseStamped>("/dali/move_base_simple/goal", 1000, true);
	goya_pub = nh_.advertise<geometry_msgs::PoseStamped>("/goya/move_base_simple/goal", 1000, true);

	michel_pub_Android = nh_.advertise<geometry_msgs::PoseStamped>("/poseEstimation", 1000, true);
	dona_pub_Android = nh_.advertise<geometry_msgs::PoseStamped>("/poseEstimation", 1000, true);
	bern_pub_Android = nh_.advertise<geometry_msgs::PoseStamped>("/poseEstimation", 1000, true);
	leo_pub_Android = nh_.advertise<geometry_msgs::PoseStamped>("/poseEstimation", 1000, true);
	boti_pub_Android = nh_.advertise<geometry_msgs::PoseStamped>("/poseEstimation", 1000, true);
	gio_pub_Android = nh_.advertise<geometry_msgs::PoseStamped>("/poseEstimation", 1000, true);
	bel_pub_Android = nh_.advertise<geometry_msgs::PoseStamped>("/poseEstimation", 1000, true);
	ghiber_pub_Android = nh_.advertise<geometry_msgs::PoseStamped>("/poseEstimation", 1000, true);
	masa_pub_Android = nh_.advertise<geometry_msgs::PoseStamped>("/poseEstimation", 1000, true);
	titi_pub_Android = nh_.advertise<geometry_msgs::PoseStamped>("/poseEstimation", 1000, true);
picasso_pub_Android = nh_.advertise<geometry_msgs::PoseStamped>("/poseEstimation", 1000, true);
	dali_pub_Android = nh_.advertise<geometry_msgs::PoseStamped>("/poseEstimation", 1000, true);
	goya_pub_Android = nh_.advertise<geometry_msgs::PoseStamped>("/poseEstimation", 1000, true);


	michel_pub_cdot = nh_.advertise<geometry_msgs::Twist>("/michelangelo/cdot", 1000, true);
	dona_pub_cdot = nh_.advertise<geometry_msgs::Twist>("/donatello/cdot", 1000, true);
	bern_pub_cdot = nh_.advertise<geometry_msgs::Twist>("/raphael/cdot", 1000, true);
	leo_pub_cdot = nh_.advertise<geometry_msgs::Twist>("/leonardo/cdot", 1000, true);
	boti_pub_cdot = nh_.advertise<geometry_msgs::Twist>("/boticelli/cdot", 1000, true);
	gio_pub_cdot = nh_.advertise<geometry_msgs::Twist>("/giotto/cdot", 1000, true);
	bel_pub_cdot = nh_.advertise<geometry_msgs::Twist>("/bellini/cdot", 1000, true);
	ghiber_pub_cdot = nh_.advertise<geometry_msgs::Twist>("/ghiberti/cdot", 1000, true);
	masa_pub_cdot = nh_.advertise<geometry_msgs::Twist>("/masaccio/cdot", 1000, true);
	titi_pub_cdot = nh_.advertise<geometry_msgs::Twist>("/titian/cdot", 1000, true);
picasso_pub_cdot = nh_.advertise<geometry_msgs::Twist>("/ghiberti/cdot", 1000, true);
	dali_pub_cdot = nh_.advertise<geometry_msgs::Twist>("/masaccio/cdot", 1000, true);
	goya_pub_cdot = nh_.advertise<geometry_msgs::Twist>("/titian/cdot", 1000, true);


    centroidPosesStamped[5].header.frame_id="leonardo";
    centroidPosesStamped[2].header.frame_id="michelangelo";
    centroidPosesStamped[3].header.frame_id="donatello";
    centroidPosesStamped[9].header.frame_id="ghiberti";
    centroidPosesStamped[6].header.frame_id="boticelli";
    centroidPosesStamped[8].header.frame_id="bellini";
    centroidPosesStamped[7].header.frame_id="giotto";
    centroidPosesStamped[4].header.frame_id="raphael";
    centroidPosesStamped[1].header.frame_id="titian";
    centroidPosesStamped[10].header.frame_id="masaccio";
    centroidPosesStamped[41].header.frame_id="picasso";
    centroidPosesStamped[42].header.frame_id="dali";
    centroidPosesStamped[43].header.frame_id="goya";

    while (ros::ok())
    {
	ros::spinOnce();
	if (gotPose==true)
	{	
		float xValuesT[countD];
		float yValuesT[countD];  
			
			
		for (int i=0;i<maxNum;i++){
			for (int j=0; j<countD;j++){
				if(selectedIndices[j]==i){
					xValuesT[j]=xValues[i];
					yValuesT[j]=yValues[i];	
				}
			}
		}

		nSites = Matrix_Size(xValuesT);
		Matrix sitesPos(nSites,2);
		for(int i=0; i<Matrix_Size(xValuesT);i++){
			sitesPos.setElement(i, 0, xValuesT[i]);      //sitePos.elements[i][0] = xValues[i];
			sitesPos.setElement(i, 1, yValuesT[i]);
		}
        	CoMGenerator cg;
        	VoronoiDiagramGenerator vdg;
				
        	vdg.generateVoronoi(xValuesT,yValuesT,countD, minX,maxX,minY,maxY,0.001);
		
        
        	vdg.resetIterator();
        
        	float x1,y1,x2,y2;
        	int a=1;
        	while(vdg.getNext(x1,y1,x2,y2))
        	{
            		a++;
            		if (x1!=x2 || y1!=y2)  //if condition necessary due to some unknown problem (Fortune's Algorithm generating vertices that shouldn't exist)
            		{
                		cg.posVertVector.push_back(x1); cg.posVertVector.push_back(y1);
                		cg.posVertVector.push_back(x2); cg.posVertVector.push_back(y2);
            		}
        	}

        
        	//After store position of all vertices, store the position of the edges of the plane (rectangular plane)
        	cg.posVertVector.push_back(minX); cg.posVertVector.push_back(minY);
        	cg.posVertVector.push_back(maxX); cg.posVertVector.push_back(minY);
        	cg.posVertVector.push_back(maxX); cg.posVertVector.push_back(maxY);
        	cg.posVertVector.push_back(minX); cg.posVertVector.push_back(maxY);
        
        
        	//Return the position of the Centers of Mass
        	sitesPos = cg.generateCenterOfMass(cg.posVertVector, sitesPos, nSites);
		float tempX=0;
		float tempY=0;
		string cfi;

		for (int i=0; i<maxNum; i++) {
			cfi = "OFF";
			for (int j=0; j<countD;j++){
				if(selectedIndices[j]==i){
					cfi="ON";
					tempX=sitesPos.elements[j][0];
					tempY=sitesPos.elements[j][1];	
					break;					
				}
			}

			if (tempX!=0&&tempY!=0){

				if ((abs(tempX-filterX[i])>.01 || abs(tempY-filterY[i])>.01) && weirdReadings[i]<10){
				cout << "SKIP!";
				weirdReadings[i]=weirdReadings[i]+1;

				}else{
					centroidPositions.transforms[i].transform.translation.x=tempX;
					centroidPositions.transforms[i].transform.translation.y=tempY;
					centroidPositions.transforms[i].transform.rotation.w=1;
					centroidPositions.transforms[i].child_frame_id=cfi;
					filterX[i]=tempX;
					filterY[i]=tempY;
					weirdReadings[i]=0;
				}

			}
			

			/*if (cfi.compare("ON")){
			ps.pose.position.x=tempX;
			ps.pose.position.y=tempY;
			ps.header.frame_id="goal";
			ps.pose.position.z=i;
			//goal_for_android.publish(ps);
			}*/
			


			if (trackingFlag==true){

			lastX[4][i]=lastX[3][i];
			lastX[3][i]=lastX[2][i];
			lastX[2][i]=lastX[1][i];
			lastX[1][i]=lastX[0][i];	
			lastX[0][i]=(centroidPositions.transforms[i].transform.translation.x-delayX[4][i])*30;
	
			delayX[4][i]=delayX[3][i];
			delayX[3][i]=delayX[2][i];
			delayX[2][i]=delayX[1][i];
			delayX[1][i]=delayX[0][i];
			delayX[0][i]=centroidPositions.transforms[i].transform.translation.x;

			lastY[4][i]=lastY[3][i];
			lastY[3][i]=lastY[2][i];
			lastY[2][i]=lastY[1][i];
			lastY[1][i]=lastY[0][i];
			lastY[0][i]=(centroidPositions.transforms[i].transform.translation.y-delayY[4][i])*30;

			delayY[4][i]=delayY[3][i];
			delayY[3][i]=delayY[2][i];
			delayY[2][i]=delayY[1][i];
			delayY[1][i]=delayY[0][i];
			delayY[0][i]=centroidPositions.transforms[i].transform.translation.y;

			cDot.transforms[i].transform.translation.x=(lastX[4][i]+lastX[3][i]+lastX[2][i]+lastX[1][i]+lastX[0][i])/5;
			cDot.transforms[i].transform.translation.y=(lastY[4][i]+lastY[3][i]+lastY[2][i]+lastY[1][i]+lastY[0][i])/5;
			cDot.transforms[i].transform.translation.z=(centroidPositions.transforms[i].transform.translation.z-lastCentroidPositions.transforms[i].transform.translation.z)*30;
			}
			else {
			cDot.transforms[i].transform.translation.x=0;
			cDot.transforms[i].transform.translation.y=0;
			cDot.transforms[i].transform.translation.z=0;
			}
		/*cout << "cDot: "<< cDot.transforms[i].transform << "\n";
		cout << "centroid: "<< centroidPositions.transforms[i].transform<<"\n";
		cout << "----------------------------------------------------------------------------------------------\n\n";*/
		}

		

		//cout << "min X: " << minX << "\n";
		//cout << "max X: " << maxX << "\n";
		//cout << "mean X: " << muX << "\n";

		//cout << "min Y: " << minY << "\n";
		//cout << "max Y: " << maxY << "\n";
		//cout << "mean Y: " << muY << "\n";
		//cout << "xdot: " << cDot.transforms[11].transform.translation.x << "\n\n";

		//cout << "mean X: " << muX<< "\n";
				//cout << "mean Y: " << muY<< "\n\n";
		lastCentroidPositions=centroidPositions;
		centroid_pub_.publish(centroidPositions);
		cDot_pub_.publish(cDot);
		
		
		transform_to_pose(centroidPositions,cDot);
		michel_pub.publish(centroidPosesStamped[MICHELANGELO_INDEX]);
		dona_pub.publish(centroidPosesStamped[DONATELLO_INDEX]);
		bern_pub.publish(centroidPosesStamped[RAPHAEL_INDEX]);
		leo_pub.publish(centroidPosesStamped[LEONARDO_INDEX]);
		boti_pub.publish(centroidPosesStamped[BOTICELLI_INDEX]);
		gio_pub.publish(centroidPosesStamped[GIOTTO_INDEX]);
		bel_pub.publish(centroidPosesStamped[BELLINI_INDEX]);
		ghiber_pub.publish(centroidPosesStamped[GHIBERTI_INDEX]);
		masa_pub.publish(centroidPosesStamped[MASACCIO_INDEX]);
		titi_pub.publish(centroidPosesStamped[TITIAN_INDEX]);
		picasso_pub.publish(centroidPosesStamped[PICASSO_INDEX]);
		dali_pub.publish(centroidPosesStamped[DALI_INDEX]);
		goya_pub.publish(centroidPosesStamped[GOYA_INDEX]);

		michel_pub_Android.publish(centroidPosesStamped[MICHELANGELO_INDEX]);
		dona_pub_Android.publish(centroidPosesStamped[DONATELLO_INDEX]);
		bern_pub_Android.publish(centroidPosesStamped[RAPHAEL_INDEX]);
		leo_pub_Android.publish(centroidPosesStamped[LEONARDO_INDEX]);
		boti_pub_Android.publish(centroidPosesStamped[BOTICELLI_INDEX]);
		gio_pub_Android.publish(centroidPosesStamped[GIOTTO_INDEX]);
		bel_pub_Android.publish(centroidPosesStamped[BELLINI_INDEX]);
		ghiber_pub_Android.publish(centroidPosesStamped[GHIBERTI_INDEX]);
		masa_pub_Android.publish(centroidPosesStamped[MASACCIO_INDEX]);
		titi_pub_Android.publish(centroidPosesStamped[TITIAN_INDEX]);
/*		picasso_pub_Android.publish(centroidPosesStamped[PICASSO_INDEX]);
		dali_pub_Android.publish(centroidPosesStamped[DALI_INDEX]);
        	goya_pub_Android.publish(centroidPosesStamped[GOYA_INDEX]);
*/

		michel_pub_cdot.publish(cDotTurtles[MICHELANGELO_INDEX]);
		dona_pub_cdot.publish(cDotTurtles[DONATELLO_INDEX]);
		bern_pub_cdot.publish(cDotTurtles[RAPHAEL_INDEX]);
		leo_pub_cdot.publish(cDotTurtles[LEONARDO_INDEX]);
		boti_pub_cdot.publish(cDotTurtles[BOTICELLI_INDEX]);
		gio_pub_cdot.publish(cDotTurtles[GIOTTO_INDEX]);
		bel_pub_cdot.publish(cDotTurtles[BELLINI_INDEX]);
		ghiber_pub_cdot.publish(cDotTurtles[GHIBERTI_INDEX]);
		masa_pub_cdot.publish(cDotTurtles[MASACCIO_INDEX]);
		titi_pub_cdot.publish(cDotTurtles[TITIAN_INDEX]);
		picasso_pub_cdot.publish(cDotTurtles[PICASSO_INDEX]);
		dali_pub_cdot.publish(cDotTurtles[DALI_INDEX]);
		goya_pub_cdot.publish(cDotTurtles[GOYA_INDEX]);
		
		
		gotPose=false;
	}

    loop_rate.sleep();
    }
        
}

