package ejercicio3_1;

/** Cuenta compartida con operación insegura para ilustrar carreras y retirada segura. */
public class CuentaBancaria {
    private int saldo;

    public CuentaBancaria(int saldoInicial) { this.saldo = saldoInicial; }

    /** Retirada deliberadamente no sincronizada: puede perder actualizaciones concurrentes. */
    public boolean retirarSinSincronizar(int cantidad) {
        if (cantidad <= 0 || saldo < cantidad) return false;
        int saldoLeido = saldo;
        Thread.yield();
        saldo = saldoLeido - cantidad;
        return true;
    }

    /** Retirada atómica protegida por el monitor de esta cuenta. */
    public synchronized boolean retirar(int cantidad) {
        if (cantidad <= 0 || saldo < cantidad) return false;
        saldo -= cantidad;
        return true;
    }

    public synchronized int getSaldo() { return saldo; }

    public static void main(String[] args) throws InterruptedException {
        boolean segura = args.length == 0 || !args[0].equalsIgnoreCase("unsafe");
        CuentaBancaria cuenta = new CuentaBancaria(1000);
        Runnable persona = () -> {
            for (int i = 0; i < 10; i++) {
                if (segura) cuenta.retirar(50);
                else cuenta.retirarSinSincronizar(50);
            }
        };
        Thread persona1 = new Thread(persona, "Persona 1");
        Thread persona2 = new Thread(persona, "Persona 2");
        persona1.start(); persona2.start();
        persona1.join(); persona2.join();
        System.out.println("Modo " + (segura ? "sincronizado" : "sin sincronizar")
                + "; saldo final: " + cuenta.getSaldo() + " €");
    }
}
