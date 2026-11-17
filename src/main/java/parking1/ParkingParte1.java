package parking1;

import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ParkingParte1 {

    private static final int NUM_PLAZAS = 10;
    private static final int NUM_COCHES = 40;

    public static void main(String[] args) {
        String[] plazas = new String[NUM_PLAZAS];
        Semaphore semPlazas = new Semaphore(NUM_PLAZAS, true); // Inicializo los 10 permisos de plaza disponibles.
        Semaphore semEntrada = new Semaphore(1, true);         // Reservo un único acceso al parking.
        Semaphore mutexPlazas = new Semaphore(1);              // Protejo el array de plazas.

        System.out.println("=== APERTURA DEL PARKING (" + NUM_PLAZAS + " plazas, " + NUM_COCHES + " coches) ===");

        for (int i = 1; i <= NUM_COCHES; i++) {
            String matricula = generarMatriculaUnica(i);
            Thread hiloCoche = new Thread(new Coche(matricula, plazas, semPlazas, semEntrada, mutexPlazas));
            hiloCoche.start();
        }
    }

    private static String generarMatriculaUnica(int id) {
        String letras = "BCDFGHJKLMNPQRSTVWXYZ";
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        char l1 = letras.charAt(rnd.nextInt(letras.length()));
        char l2 = letras.charAt(rnd.nextInt(letras.length()));
        char l3 = letras.charAt(rnd.nextInt(letras.length()));
        return String.format("%04d-%c%c%c", 1000 + id, l1, l2, l3);
    }
}

class Coche implements Runnable {

    private final String matricula;
    private final String[] plazas;
    private final Semaphore semPlazas;
    private final Semaphore semEntrada;
    private final Semaphore mutexPlazas;

    public Coche(String matricula, String[] plazas, Semaphore semPlazas,
                 Semaphore semEntrada, Semaphore mutexPlazas) {
        this.matricula = matricula;
        this.plazas = plazas;
        this.semPlazas = semPlazas;
        this.semEntrada = semEntrada;
        this.mutexPlazas = mutexPlazas;
    }

    @Override
    public void run() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        try {
            // 1. Circulo por la ciudad antes de llegar al centro comercial (entre 0,5 y 4 s).
            int tiempoCirculando = rnd.nextInt(500, 4001);
            System.out.printf("[Coche %s] Circulando por la ciudad...%n", matricula);
            Thread.sleep(tiempoCirculando);

            // 2. Llego al parking e intento conseguir una plaza (espero entre 10 y 20 s como máximo).
            int tiempoMaxEspera = rnd.nextInt(10000, 20001);
            System.out.printf("[Coche %s] Llega al parking (esperará máx. %.1f s). Plazas libres: %d%n",
                    matricula, tiempoMaxEspera / 1000.0, semPlazas.availablePermits());

            boolean plazaConseguida = semPlazas.tryAcquire(tiempoMaxEspera, TimeUnit.MILLISECONDS);

            if (!plazaConseguida) {
                System.out.printf("[Coche %s] ABANDONA el parking tras esperar %.1f s sin plaza libre.%n",
                        matricula, tiempoMaxEspera / 1000.0);
                return;
            }

            // 3. Cruzo la entrada y ocupo la primera plaza libre del array.
            int plazaAsignada = -1;
            semEntrada.acquire();
            try {
                mutexPlazas.acquire();
                try {
                    for (int i = 0; i < plazas.length; i++) {
                        if (plazas[i] == null) {
                            plazas[i] = matricula;
                            plazaAsignada = i;
                            break;
                        }
                    }
                    System.out.printf(">> [Coche %s] ENTRA por la barrera y aparca en PLAZA %d. Estado: %s%n",
                            matricula, plazaAsignada + 1, Arrays.toString(plazas));
                } finally {
                    mutexPlazas.release();
                }
            } finally {
                semEntrada.release();
            }

            // 4. Me quedo estacionado entre 1 y 3 segundos.
            int tiempoEstacionado = rnd.nextInt(1000, 3001);
            Thread.sleep(tiempoEstacionado);

            // 5. Libero mi plaza en el array y devuelvo el permiso al semáforo.
            mutexPlazas.acquire();
            try {
                plazas[plazaAsignada] = null;
                System.out.printf("<< [Coche %s] SALE de la PLAZA %d tras %.1f s.%n",
                        matricula, plazaAsignada + 1, tiempoEstacionado / 1000.0);
            } finally {
                mutexPlazas.release();
            }
            semPlazas.release();

        } catch (InterruptedException e) {
            System.err.printf("[Coche %s] Hilo interrumpido.%n", matricula);
            Thread.currentThread().interrupt();
        }
    }
}
