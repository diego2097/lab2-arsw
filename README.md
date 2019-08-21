# Part I - Before finishing class

1. La clase responsable es la clase Consumer ya que siempre esta verificando si el tamaño de la cola es mayor a cero y modifica la lista cada vez que puede. y esto provoca un consumo alto de cpu. 

![alt text](https://github.com/diego2097/lab2-arsw/blob/master/1.1.PNG "Imagen cpu 1")

2. Se modificaron las clases Consumer y Producer. Utilizando el metodo wait() para dormir el hilo de Consumer hasta que el hilo Producer lo active utilizando notifiAll() cuando agrega algo a la cola. 

![alt text](https://github.com/diego2097/lab2-arsw/blob/master/1.2.PNG "Imagen cpu 2")


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

