Slithice: A system dependence graph based program slicer. [Experimental]
====

Features: 

+ side-effect analysis <br>
  various levels of heap abstraction<br>
  with escape analyses to filter results

+ reaching-definition analysis (with definitions to heaps under consideration)
  
+ procedure depdence graph construction

+ system dependence graph construction  <br>
  with various levels of implicit parameter abstraction<br>
  can restrict the analysis depth of library methods

+ intra-procedural slcing

+ inter-procedural slicing

+ bar diagram based global slice view

+ slice highlighting in editor view

+ show various graphs with dot (or jgraph)



### Possible related paper
```
Qian Ju, Chen Lin, Xu Baowen, Finding shrink critical section refactoring opportunities for the evolution of concurrent code in trustworthy software, SCIENCE CHINA Information Sciences, 56(1), pp. 1–20, 2013 (SCI, CCF B)

Binxian Tao, Ju Qian, Xiaoyu Zhou. Side-Effect Analysis with Fast Escape Filter. ACM SIGPLAN International Workshop on the State Of the Art in Java Program Analysis (SOAP), 2012.

Ju Qian, Lin Chen, Baowen Xu, Xiaofang Zhang. Contribution-Based Call Stack Abstraction for Call String Based Pointer Analysis. Information and Software Technology, 2011, 53(6): 654-665. (SCI, CCF B)

Zhenyu Chen, Yongwei Duan, Zhihong Zhao, Baowen Xu, Ju Qian. Using Program Slicing to Improve the Efficiency and Effectiveness of Cluster Test Selection. International Journal of Software Engineering and Knowledge Engineering, 2011, 21(6): 759-777.

Yongwei Duan, Zhenyu Chen, Zhihong Zhao, Ju Qian, Zhongjun Yang. Improving Cluster Selection Techniques of Regression Testing by Slice Filtering. In Proceedings of the 22nd International Conference on Software Engineering & Knowledge Engineering (SEKE), 2010, pp.253-258.

Ju Qian, Yuming Zhou, and Baowen Xu. Improving Side-Effect Analysis with Lazy Access Path Resolving. In 9th IEEE International Working Conference on Source Code Analysis and Manipulation (SCAM),2009, pp. 35-44.

Ju Qian, Zifeng Cui, Baowen Xu, Xiaofang Zhang. Contribution-Based Call Stack Abstraction and Its Application in Pointer Analysis of AspectJ Programs. In 16th Asia-Pacific Software Engineering Conference (APSEC), 2009, pp. 267-274.

Ju Qian, Baowen Xu, Xiaoyu Zhou, Lin Chen, and Liang Shi. Dependence Analysis for C Programs with Combinability of Dataflow Facts under Consideration. Wuhan University Journal of Natural Sciences, 14(4): 321-326, Aug. 2009.

Xiaofang Zhang, Huamao Shan and Ju Qian. Resource-Aware Test Suite Optimization. Proceedings of the 9th International Conference on Quality Software (QSIC), 2009, pp. 270-275.

Ju Qian, Baowen Xu, Yuming Zhou. Interstatement Must Alias and Its Application in Dependence Analysis of Java Programs. Chinese Journal of Computers (计算机学报), 2008, 31(3): 19-430.

Ju Qian and Baowen Xu. Scenario Oriented Program Slicing. In Proceedings of the 23rd Annual ACM Symposium on Applied Computing (SAC), 2008, pp. 748-752. 

Ju Qian, Baowen Xu and Hongbo Ming. Interstatement Must Aliases for Data Dependence Analysis of Heap Locations. In 7th ACM SIGPLAN-SIGSOFT Workshop on Program Analysis for Software Tools and Engineering (PASTE), 2007, pp. 17-24.

Baowen Xu, Ju Qian, Xiaofang Zhang, Zhongqiang Wu, Lin Chen. A brief survey of program slicing. ACM SIGSOFT Software Engineering Notes, March 2005, 30(2): 10-45.
```