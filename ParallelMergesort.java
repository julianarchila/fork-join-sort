import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelMergesort extends RecursiveAction {
    private final int[] arreglo;
    private final int inicio;
    private final int fin;
    private static final int UMBRAL = 100; // Ajustar según sea necesario

    public ParallelMergesort(int[] arreglo, int inicio, int fin) {
        this.arreglo = arreglo;
        this.inicio = inicio;
        this.fin = fin;
    }

    @Override
    protected void compute() {
        if (fin - inicio <= UMBRAL) {
            Arrays.sort(arreglo, inicio, fin + 1);
        } else {
            int medio = inicio + (fin - inicio) / 2;
            ParallelMergesort tareaIzquierda = new ParallelMergesort(arreglo, inicio, medio);
            ParallelMergesort tareaDerecha = new ParallelMergesort(arreglo, medio + 1, fin);
            invokeAll(tareaIzquierda, tareaDerecha);
            combinar(arreglo, inicio, medio, fin);
        }
    }

    private void combinar(int[] arr, int inicio, int medio, int fin) {
        int[] arregloIzquierdo = Arrays.copyOfRange(arr, inicio, medio + 1);
        int[] arregloDerecho = Arrays.copyOfRange(arr, medio + 1, fin + 1);
        int i = 0, j = 0, k = inicio;

        while (i < arregloIzquierdo.length && j < arregloDerecho.length) {
            if (arregloIzquierdo[i] <= arregloDerecho[j]) {
                arr[k++] = arregloIzquierdo[i++];
            } else {
                arr[k++] = arregloDerecho[j++];
            }
        }

        while (i < arregloIzquierdo.length) {
            arr[k++] = arregloIzquierdo[i++];
        }

        while (j < arregloDerecho.length) {
            arr[k++] = arregloDerecho[j++];
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Por favor, proporcione números como argumentos de línea de comando.");
            return;
        }

        int[] numeros = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            numeros[i] = Integer.parseInt(args[i]);
        }

        System.out.println("Arreglo original: " + Arrays.toString(numeros));

        ForkJoinPool pool = new ForkJoinPool();
        ParallelMergesort ordenador = new ParallelMergesort(numeros, 0, numeros.length - 1);
        pool.invoke(ordenador);

        System.out.println("Arreglo ordenado: " + Arrays.toString(numeros));
    }
}
