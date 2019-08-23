/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arst.concprg.prodcons;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StartProduction {

   // volatile static Queue<Integer> queue;
    private static int stockLimit=20;
    public static void main(String[] args) {

        Queue<Integer> queue = new LinkedBlockingQueue<>(stockLimit);
            
            for(int i=0;i<100000;i++){
                new Producer(queue, stockLimit).start();
            }
            
            
            //let the producer create products for 5 seconds (stock).
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(StartProduction.class.getName()).log(Level.SEVERE, null, ex);
            }

            new Consumer(queue).start();
        

    }

}
