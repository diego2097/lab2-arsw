package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback = null;

    private volatile int health;

    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private boolean isPausa;
    private boolean live;
    private static Object monitor = ControlFrame.monitor;
    private  volatile boolean turno;
    private final Random r = new Random(System.currentTimeMillis());
    private boolean stop;
    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback = ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue = defaultDamageValue;
        this.isPausa = false;
        this.live = true;
        turno = true;
        stop=false;
    }

    public void run() {

        while (!stop && live) {
            if (!isPausa ) {
                Immortal im;

                int myIndex = immortalsPopulation.indexOf(this);

                int nextFighterIndex = r.nextInt(immortalsPopulation.size());

                //avoid self-fight
                if (nextFighterIndex == myIndex) {
                    nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
                }
                
                int NInmortals = 0;
               
                    for (Immortal imo : ControlFrame.immortals) {
                        if (imo.getHealth() > 0) {
                            NInmortals++;
                        }
                    }
                if(NInmortals!=1){
                     im = immortalsPopulation.get(nextFighterIndex);
                }else{
                    im = immortalsPopulation.get(0);
                }
                if (NInmortals == 2) {
                    if (turno) {
                        this.fight(im);
                        im.setTurno(true);
                        this.setTurno(false);
                        try {
                            this.currentThread().sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        this.setTurno(true);
                        im.setTurno(false);
                    }
                } else {
                    this.fight(im);
                }

                //immortalsPopulation.notifyAll();
                try {

                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
            } else {
                pausar();
            }

        }

    }
    private void pausar(){
        synchronized (monitor) {
                    if (isPausa) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
    }

    public boolean isTurno() {
        return turno;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public void setTurno(boolean turno) {
        this.turno = turno;
    }

    public void fight(Immortal i2) {
        int thisHash = System.identityHashCode(this);
        int i2Hash = System.identityHashCode(i2);
        if (thisHash < i2Hash) {
            synchronized (this) {
                synchronized (i2) {
                    if (i2.getHealth() > 0 && this.getHealth() > 0) {
                        this.actualizarHealth(i2);
                    }

                }
            }
        } else if (thisHash > i2Hash) {
            synchronized (i2) {
                synchronized (this) {
                    if (i2.getHealth() > 0 && this.getHealth() > 0) {
                        this.actualizarHealth(i2);
                    }
                }
            }
        }
    }

    private void actualizarHealth(Immortal i2) {
        if (i2.getHealth() > 0) {
            i2.changeHealth(i2.getHealth() - defaultDamageValue);
            this.health += defaultDamageValue;
            updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
        } else {
            i2.setLive(false);

        }
    }

    public void changeHealth(int v) {
        health = v;
        if (v <= 0) {
            this.setLive(false);
            //immortalsPopulation.remove(this);

        }
        health = v;
    }

    public int getHealth() {
        return health;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

    public void setIspausa(boolean b) {
        isPausa = b;

    }

    public void setLive(boolean live) {
        this.live = live;
    }

}
