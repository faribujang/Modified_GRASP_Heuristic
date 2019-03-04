# Modified_GRASP_Heuristic
Modified GRASP Heuristic written in Java aimed to optimize routing for rescue teams

The project's purpose was to create an algorithm capable of optimizing transportation/delivery to an event location in the most efficient 
way possible. Through the implementation of a heuristic algorithm I also set out to devise a method to approximate an optimal solution to
the Travelling Salesman Problem. I started to research the problem and decided to create the heuristic with a starting base off of
Greedy Heuristic logic. I chose this logic because it yields locally optimal solutions that approximate a global optimal solution in a 
reasonable time and it is cleaner than select others. The difference between the classic Greedy Heuristic logic and my GRASP adaptation is 
that this version accounts for the heuristic's problem regarding getting stuck in local optimums, through a series of switching the order 
in which passengers are picked up in each carrier's list then swapping the passengers between different carriers.

Once tested, the algorithm created surpassed hypotheses and made travelling and carpooling faster and more efficient by about 65% 
improvement on average versus the base Greedy path, and which in some cases is an improvement of about 10% over another heuristic program, 
the 2-Opt model. Thus in the case of emergency evacuation, carriers would be able to save more time, energy, and most importantly: lives.
