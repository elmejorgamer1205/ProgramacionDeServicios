package pasoporpuente;

import java.util.Random;

enum Sentido {
    NORTE_SUR("Norte -> Sur"),
    SUR_NORTE("Sur -> Norte");

    private final String etiqueta;

    Sentido(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}

class Puente {

    private static final int PESO_MAXIMO = 200;
    private static final int MAX_PERSONAS_TOTAL = 4;
    private static final int MAX_PERSONAS_SENTIDO = 3;

    private int peso = 0;
    private int personasNorteSur = 0;
    private int personasSurNorte = 0;

    public synchronized int getPeso() {
        return peso;
    }

    public synchronized int getTotalPersonas() {
        return personasNorteSur + personasSurNorte;
    }

    public synchronized int getPersonasNorteSur() {
        return personasNorteSur;
    }

    public synchronized int getPersonasSurNorte() {
        return personasSurNorte;
    }

    public synchronized boolean autorizacionPaso(Persona persona) {
        int totalActual = getTotalPersonas();
        boolean cabePeso = (this.peso + persona.getPeso()) <= PESO_MAXIMO;
        boolean cabeTotal = totalActual < MAX_PERSONAS_TOTAL;
        boolean cabeSentido = (persona.getSentido() == Sentido.NORTE_SUR)
                ? (this.personasNorteSur < MAX_PERSONAS_SENTIDO)
                : (this.personasSurNorte < MAX_PERSONAS_SENTIDO);

        if (cabePeso && cabeTotal && cabeSentido) {
            this.peso += persona.getPeso();
            if (persona.getSentido() == Sentido.NORTE_SUR) {
                this.personasNorteSur++;
            } else {
                this.personasSurNorte++;
            }
            return true;
        }
        return false;
    }

    public synchronized void terminaPaso(Persona persona) {
        this.peso -= persona.getPeso();
        if (persona.getSentido() == Sentido.NORTE_SUR) {
            this.personasNorteSur--;
        } else {
            this.personasSurNorte--;
        }
    }
}

class Persona implements Runnable {

    private final Puente puente;
    private final String idPersona;
    private final int peso;
    private final Sentido sentido;
    private final int tMinPaso;
    private final int tMaxPaso;

    public Persona(Puente puente, int peso, Sentido sentido, int tMinPaso, int tMaxPaso, String idPersona) {
        this.puente = puente;
        this.peso = peso;
        this.sentido = sentido;
        this.tMinPaso = tMinPaso;
        this.tMaxPaso = tMaxPaso;
        this.idPersona = idPersona;
    }

    public int getPeso() {
        return peso;
    }

    public Sentido getSentido() {
        return sentido;
    }

    @Override
    public void run() {
        synchronized (this.puente) {
            System.out.printf("- %s (%d kg, %s) quiere cruzar. Puente: %d kg, %d pers. [N->S: %d | S->N: %d]%n",
                    idPersona, peso, sentido, puente.getPeso(), puente.getTotalPersonas(),
                    puente.getPersonasNorteSur(), puente.getPersonasSurNorte());

            while (!this.puente.autorizacionPaso(this)) {
                try {
                    System.out.printf("# %s (%s) debe esperar.%n", idPersona, sentido);
                    this.puente.wait();
                } catch (InterruptedException ex) {
                    System.out.printf("Interrupción mientras %s esperaba para cruzar.%n", idPersona);
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            System.out.printf("> %s (%d kg, %s) ENTRA. Puente: %d kg, %d pers. [N->S: %d | S->N: %d]%n",
                    idPersona, peso, sentido, puente.getPeso(), puente.getTotalPersonas(),
                    puente.getPersonasNorteSur(), puente.getPersonasSurNorte());
        }

        // Cruzo el puente sin multiplicar por 1000 para acelerar la simulación.
        Random r = new Random();
        int tiempoPaso = tMinPaso + r.nextInt(tMaxPaso - tMinPaso + 1);
        try {
            Thread.sleep(tiempoPaso);
        } catch (InterruptedException ex) {
            System.out.printf("Interrupción mientras %s cruzaba el puente.%n", idPersona);
            Thread.currentThread().interrupt();
        }

        // Salgo del puente y aviso a todos los hilos que están esperando.
        synchronized (this.puente) {
            this.puente.terminaPaso(this);
            System.out.printf("< %s (%s) SALE. Puente: %d kg, %d pers. [N->S: %d | S->N: %d]%n",
                    idPersona, sentido, puente.getPeso(), puente.getTotalPersonas(),
                    puente.getPersonasNorteSur(), puente.getPersonasSurNorte());
            this.puente.notifyAll();
        }
    }
}

public class PasoPorPuenteAmbosSentidos {

    public static void main(String[] args) {
        final Puente puente = new Puente();

        int tMinLlegada = 1;
        int tMaxLlegada = 30;
        int tMinPaso = 10;
        int tMaxPaso = 50;
        int minPeso = 40;
        int maxPeso = 120;

        System.out.println(">>>>>>>>>>>> Comienza simulación de puente en ambos sentidos.");
        Random r = new Random();
        int idPersona = 1;

        while (true) {
            int tLlegada = tMinLlegada + r.nextInt(tMaxLlegada - tMinLlegada + 1);
            int pesoPersona = minPeso + r.nextInt(maxPeso - minPeso + 1);
            Sentido sentido = r.nextBoolean() ? Sentido.NORTE_SUR : Sentido.SUR_NORTE;

            try {
                Thread.sleep(tLlegada);
            } catch (InterruptedException ex) {
                System.out.println("Interrumpido proceso principal.");
                Thread.currentThread().interrupt();
                break;
            }

            Thread hiloPersona = new Thread(
                    new Persona(puente, pesoPersona, sentido, tMinPaso, tMaxPaso, "P" + idPersona));
            hiloPersona.start();
            idPersona++;
        }
    }
}
