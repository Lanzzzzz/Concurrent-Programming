package com.company;
/*****************************************************************
 * Memorial University of Newfoundland<br>
 * 7894 Concurrent Programming<br>
 * Assignment 3, Question 1 - Chopstick class
 *
 * @author Dennis Peters ($Author: dpeters $)
 * @version $Revision: 822 $ $Date: 2009-07-05 22:06:28 -0230 (Sun, 05 Jul 2009) $
 * 
 * $Id: Chopstick.java 822 2009-07-06 00:36:28Z dpeters $
 ****************************************************************/
class Chopstick {
  boolean inUse;

  Chopstick()
  {
    inUse = false;
  }

  public void up()
  {
    if (inUse) {
      System.out.println("ERROR: chopstick already up.");
    }
    inUse = true;
  }

  public void down()
  {
    if (!inUse) {
      System.out.println("ERROR: chopstick already down.");
    }
    inUse = false;
  }
}

