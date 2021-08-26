# SensorLeakDetector

Automatically detect Sensor Leaks in Android Applications


## Setup
The following is required to set up Native2Java:
* MAC system
* Intellj
* Java SDK : 1.8

##### Step 1: Load repo
* git clone git@github.com:MobileSE/SEEKER.git
* cd SEEKER

##### Step 2: build package：
* ./res/loadDependencies.sh
* mvn clean install

##### Step 3: example of running SensorLeakDetector(2 parameters):
* Parameters are needed here: [your_apk_path.apk],[path of android platform]
* Example: your_path/xxx.apk, your_path/android-platforms/
       
   
## Output
* Refer to console logs for sensor leak details.
