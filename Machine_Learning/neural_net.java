package victorious_secret.Machine_Learning;

/**
 * Created by peter on 05/05/2016.
 */
public class neural_net {
    double[] INPUT_LAYER;

    double[] HIDDEN_LAYER_1;
    int[][] HIDDEN_LAYER_1_CONNECTIONS;

    double[] OUTPUT_LAYER;

    public neural_net(){
        INPUT_LAYER = new double[]{ 0.58629629,  0.99510976,  0.73934298,  0.36032988,  0.93614847,
                0.54643643,  0.50120677,  0.60612685,  0.78293491,  0.60960467, 0.16403161};

        HIDDEN_LAYER_1 = new double[]{0.30239306,  0.9796161 ,  0.27558155,  0.43716384,  0.39062958,
                0.11049753,  0.85775946,  0.67021565,  0.02871146,  0.07785323, 0.22107331};
        HIDDEN_LAYER_1_CONNECTIONS = new int[][]{{0, 1}, {0, 1, 2}, {1, 2, 3}, {2, 3, 4}, {3, 4, 5}, {4, 5, 6}, {5, 6, 7},
                {6, 7, 8}, {7, 8, 9}, {8, 9, 10}, {9, 10}};

        OUTPUT_LAYER = new double[]{0.3135107};

    }

    public double feed_forward(double[] inputs){
        double[] l1 = softmax(inputs, INPUT_LAYER, 0.01);
        /*
        System.out.print("L1: ");
        for (double s : l1) {
            System.out.print(s);
            System.out.print(", ");
        }
        System.out.println();
        */
        double[] l2 = softmax(l1, HIDDEN_LAYER_1, HIDDEN_LAYER_1_CONNECTIONS, 0.01);
        /*System.out.print("L2: ");
        for (double s : l2) {
            System.out.print(s);
            System.out.print(", ");
        }
        System.out.println();
*/
        double[] l3 = softmax(l2, OUTPUT_LAYER, 0.01);
  /*      System.out.print("L3: ");
        for (double s : l3) {
            System.out.print(s);
            System.out.print(", ");
        }
        System.out.println();
*/
        return l3[0];
    }

    private double[] softmax(double[] input, double[] layer, int[][] connections, double bias){

        double[] out = new double[layer.length];
        double tot = 0;

        for(int i = 0; i < connections.length; i++){
            for(int j : connections[i]){
                out[i] += Math.exp(input[j] * layer[i] + bias);
            }
            tot += out[i];
        }

        for(int i = 0; i < connections.length; i++){
            out[i] /= tot;
        }
        return out;
    }

    private double[] softmax(double[] input, double[] layer, double bias){

        double[] out = new double[layer.length];
        double tot = 0;

        for(int i = 0; i < layer.length; i++){
            for(int j = 0; j < input.length; j++){
                out[i] += Math.exp(input[j] * layer[i] + bias);
            }
            tot += out[i];
        }

        if(layer.length > 1) {
            for (int i = 0; i < layer.length; i++) {
                out[i] /= tot;
            }
        }

        return out;
    }


}
