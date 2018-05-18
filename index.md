---
#
# Use the widgets beneath and the content will be
# inserted automagically in the webpage. To make
# this work, you have to use › layout: frontpage
#
layout: frontpage
title: "Welcome"
---

Java OpenThesaurus Library
--------------------------

An application programming interface for the free thesaurus project 
[OpenThesaurus][1]. JOTL enables efficient and 
structured access to the information encoded OpenThesaurus, including 
synsets, senses, and semantic labels.


Publications and Citation Information
-------------------------------------

DKPro JOTL has been developed as part of the [UBY project][2]. You can read more 
about UBY in our scientific article:

> Iryna Gurevych, Judith Eckle-Kohler, Silvana Hartmann, Michael Matuschek, 
  Christian M. Meyer, and Christian Wirth: UBY – A Large-Scale Unified 
  Lexical-Semantic Resource Based on LMF, in: Proceedings of the 13th 
  Conference of the European Chapter of the Association for Computational 
  Linguistics (EACL), p. 580–590, April 2012. Avignon, France.
  [(download)][4]  

Please cite our EACL 2012 paper if you use the software in your scientific work. 


License and Availability
------------------------

The latest version of DKPro JOTL is available via Maven Central. 
If you use Maven as your build tool, then you can add DKPro JOTL 
as a dependency in your pom.xml file:

		<dependency>
		  <groupId>org.dkpro.jotl</groupId>
		  <artifactId>dkpro-jotl</artifactId>
		  <version>1.0.0</version>
		</dependency>

DKPro JOTL is available as open source software under the 
[Apache License 2.0 (ASL)][5]. The software thus comes "as is" without any 
warranty (see license text for more details).

About DKPro JOTL
----------------

Prior to being available as open source software, DKPro JOTL has been 
a research project at the Ubiquitous Knowledge Processing (UKP) Lab of 
the Technische Universität Darmstadt, Germany. The following people have 
mainly contributed to this project (in alphabetical order):

* Yevgen Chebotar
* Iryna Gurevych
* Christian M. Meyer


[1]: http://www.openthesaurus.org
[2]: http://www.ukp.informatik.tu-darmstadt.de/uby/
[4]: http://www.aclweb.org/anthology/E/E12/E12-1059.pdf
[5]: http://www.apache.org/licenses/LICENSE-2.0
