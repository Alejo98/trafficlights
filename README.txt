===============================
COMP9417 Traffic Lights Project
===============================

Han Li
hli[at]cse.unsw.edu.au
3299976

Lawrence Yao
lawry[at]cse.unsw.edu.au
2252726

Vaughan Rouesnel
vjro855[at]cse.unsw.edu.au
3188927

===============
Getting Started
===============

To compile and start the GUI, run:

    ant
  
Type in the desired traffic intensity setting in the 2nd text box near the bottom of the GUI, then
click on the menu item {File} > {Set Simulation Class}. Pick the simulation algorithm you want to
test, then click the {OK} button and a new traffic model will be initialised with a default random
number seed. This way we can pick different algorithms and test them using the same random number
seed so as to be consistent.

NOTE: You may need to resize the window to see the animation clearly.

Set the Step Duration (1st text box near bottom of GUI) to 100 if you want to see the traffic
animating, or set it to 0 if you want the result as soon as possible. With a time-step duration near
0, the GUI will `skip frames` to keep up with the traffic model changes.

Click the {File} > {Start Simulation} menu item and enter the number of timesteps to simulate
continuously. Anything more than 1000 will take a few minutes. Then click {OK} button and watch the
simulation take place. For debugging, we use the {Next Step} button, which simulate 1 timestep per
click.

For experimentation, the GUI is too slow and not required, so we run experiments via the command
line using the tf.api.experiment.Main class.

NOTE: This class is currently hard-coded to use the Ultimate algorithm. To see another algorithm
in action change the class on line 23.

===============
Troubleshooting
===============

If the ant build script does not work, try running in Eclipse, or building JAR files and running them
using one of the following commands.

    java -jar <project executable JAR file>

    java -cp <project normal JAR file> tf.gui.App/.