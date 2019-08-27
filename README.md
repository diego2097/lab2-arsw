## Empezando
se debe de clonar el proyecto, para esto utilizaremos el comando git clone. ubíquese la carpeta a guardar el proyecto y escriba el siguiente comando en la terminal:
 
 ### git clone https://github.com/diego2097/lab1-arsw
una vez clonado, ubicarse en la carpeta del proyecto el cual requiera ejecutar. al ingresar a BBP_formula podra ejecutarlo de forma inmediata mediante el comando mvn package, para el caso de Dogs race case este esta dividido en dos partes. se debera acceder a la parte que se quiera ejecutar y realizar el comando mvn package. 
## Prerrequisitos
Se debe tener instalados los siguientes programas en nuestro sistema operativo: 
- Maven 
- Git
- Java
## Construido en
- Maven: una herramienta de software para la gestión y construcción de proyectos java

## Autor  
- Diego Alejandro Corredor Tolosa https://github.com/diego2097
- Luis Fernando Pizza Gamba https://github.com/luis572

## Licencia 
- GNU General Public License v3.0

# Part I - Before finishing class

1. La clase responsable es la clase Consumer ya que siempre esta verificando si el tamaño de la cola es mayor a cero y modifica la lista cada vez que puede. y esto provoca un consumo alto de cpu. 

![alt text](https://github.com/diego2097/lab2-arsw/blob/master/1.1.PNG "Imagen cpu 1")

2. Se modificaron las clases Consumer y Producer. Utilizando el metodo wait() para dormir el hilo de Consumer hasta que el hilo Producer lo active utilizando notifiAll() cuando agrega algo a la cola. 

![alt text](https://github.com/diego2097/lab2-arsw/blob/master/1.2.PNG "Imagen cpu 2")

Como se puede observar en la imagen el consumo de CPU bajo drasticamente. 

- Metodo run() de la clase Consumer: 

```java
    @Override
    public void run() {
        while (true) {
            synchronized (queue) {
                if (queue.size() > 0) {

                    int elem = queue.poll();
                    System.out.println("Consumer consumes " + elem);
                }
                try {
                    queue.wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Consumer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
```

- Metodo run() de la clase Producer: 

```java
  @Override
    public void run() {
        while (true) {
            
            dataSeed = dataSeed + rand.nextInt(100);
            System.out.println("Producer added " + dataSeed);
            synchronized(queue){
                queue.add(dataSeed);
               queue.notifyAll();
            }
           try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
```

3. Para que el productor sea mas rapido que el consumidor, creamos muchos hilos productor(100000) los cuales insertan datos en la cola mas rapido de lo que el consumidor puede sacarlos. 

```java
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
```


# Part II

2. Para N jugadores la cantidad de vida total sera N multiplicado por la variable DEFAULT_IMMORTAL_HEALTH.

3. No es cumplida ya que la suma de vida total es mayor a la que deberia haber. por ejemplo: 

![alt text](https://github.com/diego2097/lab2-arsw/blob/master/2.3.PNG "Invariant")

La vida por defecto es 10, por lo tanto la suma total deberia ser de 30 pero como se observa esto no se cumple. 

4.  Pausar: 
```java
JButton btnPauseAndCheck = new JButton("Pause and check");
        btnPauseAndCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                    for (Immortal im : immortals) {
                        im.setIspausa(true);
                    }
                    int sum = 0;
                    for (Immortal im : immortals) {
                        sum += im.getHealth();
                    }
                    statisticsLabel.setText("<html>" + immortals.toString() + "<br>Health sum:" + sum);
            }

        });
 
```
dentro de la clase Inmortal: 
```java
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
    
```
reanudar: 

```java
  btnResume.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                synchronized (monitor) {
                    for (Immortal im : immortals) {
                        im.setIspausa(false);
                    }
                    monitor.notifyAll();
                }
            }
        });
```
5.  No es cumplida ya que la suma de vida total es mayor a la que deberia haber. 
6.   region critica: 
region critica en la clase Inmortal metodo run: : 
```java
   this.fight(im);
```
```java
   public void fight(Immortal i2) {

        if (i2.getHealth() > 0) {
            i2.changeHealth(i2.getHealth() - defaultDamageValue);
            this.health += defaultDamageValue;
            updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
        } else {
            updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
        }

    }
```
solucionando con locks’ simultaneously: 
```java
private void actualizarHealth(Immortal i2) {
        if (i2.getHealth() > 0) {
            i2.changeHealth(i2.getHealth() - defaultDamageValue);
            this.health += defaultDamageValue;
            updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
        } else {
            i2.setLive(false);

        }
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
 
```
9. 
private static final int DEFAULT_IMMORTAL_HEALTH =100;
private static final int DEFAULT_DAMAGE_VALUE = 5;
caso 100: 

![alt text](https://github.com/diego2097/lab2-arsw/blob/master/2.9.PNG "Imagen caso 100")

caso 1000: 

![alt text](https://github.com/diego2097/lab2-arsw/blob/master/2.92.PNG "Imagen caso 100")

caso 100000: 

![alt text](https://github.com/diego2097/lab2-arsw/blob/master/2.93.PNG "Imagen caso 100")

10.2:  para evitar que los inmortales vivos tengan peleas con los muertos  se creo en la clase Inmortal un atributo llamado "live"  de tipo boolea,  si el inmortal  llegara a morir se acaba la ejecucion de dicho hilo. 
En el metodo Run de cada Inmortal: 

```java
public void run() {

        while (!stop && live) {
        ...
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
```
11. se creo un atributo en la clase Inmortal "stop", que finalizara la ejecucion el hilo: 
```java
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                synchronized (monitor) {
                    for (Immortal im : immortals) {
                        im.setStop(true);
                    }
                    monitor.notifyAll();
                }
            }
        });
```
