import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ParallelQuicksort extends RecursiveAction {
    private final int[] arreglo;
    private final int bajo;
    private final int alto;
    private static final int UMBRAL = 100; // Ajustar según sea necesario

    public ParallelQuicksort(int[] arreglo, int bajo, int alto) {
        this.arreglo = arreglo;
        this.bajo = bajo;
        this.alto = alto;
    }

    @Override
    protected void compute() {
        if (alto - bajo < UMBRAL) {
            Arrays.sort(arreglo, bajo, alto + 1);
        } else {
            int pivote = particionar(arreglo, bajo, alto);
            invokeAll(new ParallelQuicksort(arreglo, bajo, pivote - 1),
                      new ParallelQuicksort(arreglo, pivote + 1, alto));
        }
    }

    private int particionar(int[] arr, int bajo, int alto) {
        int pivote = arr[alto];
        int i = bajo - 1;
        for (int j = bajo; j < alto; j++) {
            if (arr[j] < pivote) {
                i++;
                intercambiar(arr, i, j);
            }
        }
        intercambiar(arr, i + 1, alto);
        return i + 1;
    }

    private void intercambiar(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
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
        ParallelQuicksort ordenador = new ParallelQuicksort(numeros, 0, numeros.length - 1);
        pool.invoke(ordenador);

        System.out.println("Arreglo ordenado: " + Arrays.toString(numeros));
    }
}
