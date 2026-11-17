package parking2;

import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ParkingParte2 {

    // Defino estos parámetros para probar distintas capacidades y cargas.
    private static final int NUM_PLAZAS = 10;         // Numero las plazas del 1 al 10 (índices 0..9).
    private static final int PARES_FURGONETAS = 2;   // Reservo los pares (1,2) y (3,4) para las furgonetas.
    private static final int NUM_COCHES = 40;
    private static final int NUM_FURGONETAS = 10;

    public static void main(String[] args) {
        int plazasReservadasFurgos = PARES_FURGONETAS * 2;
        int plazasParaCoches = NUM_PLAZAS - plazasReservadasFurgos;

        String[] plazas = new String[NUM_PLAZAS];

        Semaphore semPlazasCoches = new Semaphore(plazasParaCoches, true);
        Semaphore semParesFurgonetas = new Semaphore(PARES_FURGONETAS, true);
        Semaphore semEntrada = new Semaphore(1, true); // Comparto una única entrada entre todos los vehículos.
        Semaphore mutexPlazas = new Semaphore(1);      // Protejo el array de plazas con exclusión mutua.

        System.out.printf("=== APERTURA DEL PARKING (%d plazas: %d pares furgonetas [1-%d] y %d plazas coches [%d-%d]) ===%n",
                NUM_PLAZAS, PARES_FURGONETAS, plazasReservadasFurgos,
                plazasParaCoches, plazasReservadasFurgos + 1, NUM_PLAZAS);

        for (int i = 1; i <= NUM_COCHES; i++) {
            String matricula = generarMatricula("C", i);
            new Thread(new Coche(matricula, plazas, plazasReservadasFurgos,
                    semPlazasCoches, semEntrada, mutexPlazas)).start();
        }

        for (int i = 1; i <= NUM_FURGONETAS; i++) {
            String matricula = generarMatricula("F", i);
            new Thread(new Furgoneta(matricula, plazas, plazasReservadasFurgos,
                    semParesFurgonetas, semEntrada, mutexPlazas)).start();
        }
    }

    private static String generarMatricula(String prefijo, int id) {
        String letras = "BCDFGHJKLMNPQRSTVWXYZ";
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        char l1 = letras.charAt(rnd.nextInt(letras.length()));
        char l2 = letras.charAt(rnd.nextInt(letras.length()));
        char l3 = letras.charAt(rnd.nextInt(letras.length()));
        return String.format("%s-%04d-%c%c%c", prefijo, 1000 + id, l1, l2, l3);
    }
}

class Coche implements Runnable {

    private final String matricula;
    private final String[] plazas;
    private final int inicioPlazasCoches;
    private final Semaphore semPlazasCoches;
    private final Semaphore semEntrada;
    private final Semaphore mutexPlazas;

    public Coche(String matricula, String[] plazas, int inicioPlazasCoches,
                 Semaphore semPlazasCoches, Semaphore semEntrada, Semaphore mutexPlazas) {
        this.matricula = matricula;
        this.plazas = plazas;
        this.inicioPlazasCoches = inicioPlazasCoches;
        this.semPlazasCoches = semPlazasCoches;
        this.semEntrada = semEntrada;
        this.mutexPlazas = mutexPlazas;
    }

    @Override
    public void run() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        try {
            Thread.sleep(rnd.nextInt(500, 4001));

            int tiempoMaxEspera = rnd.nextInt(10000, 20001);
            System.out.printf("[Coche %s] Llega al parking (espera máx. %.1f s). Plazas coche libres: %d%n",
                    matricula, tiempoMaxEspera / 1000.0, semPlazasCoches.availablePermits());

            if (!semPlazasCoches.tryAcquire(tiempoMaxEspera, TimeUnit.MILLISECONDS)) {
                System.out.printf("[Coche %s] ABANDONA tras esperar %.1f s sin plaza.%n",
                        matricula, tiempoMaxEspera / 1000.0);
                return;
            }

            int plazaAsignada = -1;
            semEntrada.acquire();
            try {
                mutexPlazas.acquire();
                try {
                    for (int i = inicioPlazasCoches; i < plazas.length; i++) {
                        if (plazas[i] == null) {
                            plazas[i] = matricula;
                            plazaAsignada = i;
                            break;
                        }
                    }
                    System.out.printf(">> [Coche %s] ENTRA y ocupa PLAZA %d. Parking: %s%n",
                            matricula, plazaAsignada + 1, Arrays.toString(plazas));
                } finally {
                    mutexPlazas.release();
                }
            } finally {
                semEntrada.release();
            }

            Thread.sleep(rnd.nextInt(1000, 3001));

            mutexPlazas.acquire();
            try {
                plazas[plazaAsignada] = null;
                System.out.printf("<< [Coche %s] SALE de PLAZA %d.%n", matricula, plazaAsignada + 1);
            } finally {
                mutexPlazas.release();
            }
            semPlazasCoches.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Furgoneta implements Runnable {

    private final String matricula;
    private final String[] plazas;
    private final int limitePlazasFurgos;
    private final Semaphore semParesFurgonetas;
    private final Semaphore semEntrada;
    private final Semaphore mutexPlazas;

    public Furgoneta(String matricula, String[] plazas, int limitePlazasFurgos,
                     Semaphore semParesFurgonetas, Semaphore semEntrada, Semaphore mutexPlazas) {
        this.matricula = matricula;
        this.plazas = plazas;
        this.limitePlazasFurgos = limitePlazasFurgos;
        this.semParesFurgonetas = semParesFurgonetas;
        this.semEntrada = semEntrada;
        this.mutexPlazas = mutexPlazas;
    }

    @Override
    public void run() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        try {
            Thread.sleep(rnd.nextInt(500, 4001));

            int tiempoMaxEspera = rnd.nextInt(10000, 20001);
            System.out.printf("[Furgoneta %s] Llega al parking (espera máx. %.1f s). Pares (impar,par) libres: %d%n",
                    matricula, tiempoMaxEspera / 1000.0, semParesFurgonetas.availablePermits());

            if (!semParesFurgonetas.tryAcquire(tiempoMaxEspera, TimeUnit.MILLISECONDS)) {
                System.out.printf("[Furgoneta %s] ABANDONA tras esperar %.1f s sin par de plazas libre.%n",
                        matricula, tiempoMaxEspera / 1000.0);
                return;
            }

            // Busco un par (índice par -> plaza impar, índice impar -> plaza par).
            int indiceImpar = -1;
            semEntrada.acquire();
            try {
                mutexPlazas.acquire();
                try {
                    for (int i = 0; i < limitePlazasFurgos; i += 2) {
                        if (plazas[i] == null && plazas[i + 1] == null) {
                            plazas[i] = matricula;
                            plazas[i + 1] = matricula;
                            indiceImpar = i;
                            break;
                        }
                    }
                    System.out.printf(">> [Furgoneta %s] ENTRA y ocupa PLAZAS (%d, %d). Parking: %s%n",
                            matricula, indiceImpar + 1, indiceImpar + 2, Arrays.toString(plazas));
                } finally {
                    mutexPlazas.release();
                }
            } finally {
                semEntrada.release();
            }

            Thread.sleep(rnd.nextInt(1000, 3001));

            mutexPlazas.acquire();
            try {
                plazas[indiceImpar] = null;
                plazas[indiceImpar + 1] = null;
                System.out.printf("<< [Furgoneta %s] SALE y libera PLAZAS (%d, %d).%n",
                        matricula, indiceImpar + 1, indiceImpar + 2);
            } finally {
                mutexPlazas.release();
            }
            semParesFurgonetas.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
