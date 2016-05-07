package victorious_secret.Machine_Learning;

//import org.apache.commons.math3.linear.*;
import javax.vecmath.GMatrix;
//import javax.vecmath.GVector;
import java.util.Random;

/**
 * Created by peter on 05/05/2016.
 */
class neural_net {
    private double[] INPUT_LAYER;

    private double[] HIDDEN_LAYER_1;
    private int[][] HIDDEN_LAYER_1_CONNECTIONS;

    private double[][] HIDDEN_LAYER_2;

    private double[] OUTPUT_LAYER;

    private GMatrix M_L1;
    private GMatrix M_L1_B;


    neural_net(){


        INPUT_LAYER = new double[]{ 0.58629629,  0.99510976,  0.73934298,  0.36032988,  0.93614847,
                0.54643643,  0.50120677,  0.60612685,  0.78293491,  0.60960467, 0.16403161};


        HIDDEN_LAYER_1 = new double[]{0.30239306,  0.9796161 ,  0.27558155,  0.43716384,  0.39062958,
                0.11049753,  0.85775946,  0.67021565,  0.02871146,  0.07785323, 0.22107331};
        HIDDEN_LAYER_1_CONNECTIONS = new int[][]{{0, 1}, {0, 1, 2}, {1, 2, 3}, {2, 3, 4}, {3, 4, 5}, {4, 5, 6}, {5, 6, 7},
                {6, 7, 8}, {7, 8, 9}, {8, 9, 10}, {9, 10}};

        HIDDEN_LAYER_2 = new double[][]{
                { 0.01106165,  0.50176905,  0.29577367,  0.533733  ,  0.76212816,
                    0.47764848,  0.44685077,  0.94483426,  0.510795  ,  0.33959837,
                    0.75047072},
                { 0.88317256,  0.25638538,  0.17111997,  0.06679579,  0.57157655,
                    0.7633007 ,  0.50795138,  0.04023469,  0.73057592,  0.78536714,
                    0.08782687},
                { 0.38200371,  0.86375222,  0.70221831,  0.50048875,  0.44981133,
                    0.18053837,  0.13299794,  0.73610755,  0.47264801,  0.09167934,
                    0.2678617 },
                { 0.57106781,  0.61353813,  0.38284525,  0.93544145,  0.7097687 ,
                    0.14754638,  0.14326293,  0.06465343,  0.05450546,  0.28015382,
                    0.24243911},
                { 0.29601739,  0.6967948 ,  0.29535981,  0.09740713,  0.55423598,
                    0.26314491,  0.26961815,  0.47162138,  0.07451416,  0.2862509 ,
                    0.02358123},
                { 0.76197589,  0.8789234 ,  0.93979499,  0.84400347,  0.4739628 ,
                    0.18924464,  0.62871325,  0.45126339,  0.75412716,  0.33308115,
                    0.71271775},
                { 0.93351539,  0.93544967,  0.76893158,  0.27169858,  0.56340337,
                    0.38022002,  0.84218797,  0.90691566,  0.68781954,  0.82603785,
                    0.89684146},
                { 0.6769132 ,  0.38010882,  0.07400137,  0.88520907,  0.60500418,
                    0.34928083,  0.26780704,  0.90038157,  0.60955802,  0.64947991,
                    0.61919937},
                { 0.74020544,  0.15282738,  0.77629983,  0.5764793 ,  0.28658519,
                    0.11063734,  0.5843323 ,  0.370381  ,  0.10495242,  0.5713613 ,
                    0.98641497},
                { 0.66920717,  0.05679593,  0.71465335,  0.32170222,  0.77763533,
                    0.19470277,  0.84059244,  0.23466251,  0.02225723,  0.67188782,
                    0.04889423},
                { 0.94783353,  0.65676904,  0.26910699,  0.98495042,  0.32771638,
                    0.02122439,  0.37132155,  0.76966553,  0.06516268,  0.65682672,
                    0.28394742}};

//        double[][] matrixData = { {1d,2d,3d}, {2d,5d,3d}};
//        RealMatrix m = MatrixUtils.createRealMatrix(matrixData);
//        System.out.println(m.toString());

        OUTPUT_LAYER = new double[]{0.3135107};

        double[] t_h = new double[INPUT_LAYER.length * INPUT_LAYER.length];
        double[] t_b = new double[INPUT_LAYER.length];
        Random r = new Random();
        for (int i = 0; i < t_h.length; i++) {
            t_h[i] = r.nextDouble();
        }
        for (int i = 0; i < t_b.length; i++) {
            t_b[i] = r.nextDouble();
        }

        M_L1 = new GMatrix(INPUT_LAYER.length, INPUT_LAYER.length, t_h);
        M_L1_B = new GMatrix(1, INPUT_LAYER.length, t_b);
    }

    double feed_forward(double[] inputs){
        double q = 0;
        for(int i = 0; i < 1000; i++){
            q += Math.exp(i);
        }
        System.out.println(q);
        return q;
        /*
        double[] l1 = softmax(inputs, INPUT_LAYER, 0.01);
        print_vector(l1, "L1");

        double[] l2 = softmax(l1, HIDDEN_LAYER_1, HIDDEN_LAYER_1_CONNECTIONS, 0.01);
        print_vector(l2, "L2");

        double[] l3 = softmax(l2, HIDDEN_LAYER_2, 0.01);
        print_vector(l3, "L3");

        double[] l4 = softmax(l3, OUTPUT_LAYER, 0.01);
        print_vector(l4, "L4");

        return l4[0];
*/
    }


    /**
     * Performs a softmax on a specially connected single layer
     * @param input  vector of inputs
     * @param layer matrix of weights
     * @param connections ints indicating which inputs connect to which layer
     * @param bias additional bias
     * @return softmax output, same shape as first dimension of layer
     */
    private double[] softmax(double[] input, double[] layer, int[][] connections, double bias){
        double[] out = new double[layer.length];
        double tot = 0;

        for(int i = 0; i < connections.length; i++){
            for(int j : connections[i]){
                //out[i] += Math.exp(input[j] * layer[i] + bias);
                out[i] += input[j] * layer[i] + bias;
            }
            out[i] = Math.exp(out[i]);
            tot += out[i];
        }

        for(int i = 0; i < connections.length; i++){
            out[i] /= tot;
        }
        return out;
    }

    /**
     * Performs a softmax on a fully connected multidepth layer
     * @param input vector of inputs
     * @param layer matrix of weights
     * @param bias additional bias
     * @return softmax output, same shape as first dimension of layer
     */
    private double[] softmax(double[] input, double[][] layer, double bias){
        double[][] t = mmult(layer, input);
        t = madd(t, bias);
        return _softmax(t);
    }

    /**
     * Performs a softmax on a singly connected layer
     * @param input vector of inputs
     * @param layer vector layer (assumed singly connected)
     * @param bias additional bias
     * @return softmax output, same shape as layer
     */
    private double[] softmax(double[] input, double[] layer, double bias){
        double [] g = mmult(layer, input);
        double[][] t = new double[1][g.length];
        for(int i = 0; i < g.length; i++){
            t[i][0] = g[i];
        }
        t = madd(t, bias);
        return _softmax(t);
    }

    /**
     * Converts a matrix into the softmax vector
     * @param in Matrix to convert
     * @return Converted vector
     */
    private double[] _softmax(double[][] in){
        double[] out = new double[in.length];
        double tot = 0;

        for(int i = 0; i < in.length; i++){
            for(double j : in[i]){
                out[i] += j;
            }
            out[i] = Math.exp(out[i]);
            tot += out[i];
        }

        for(int i = 0; i < in.length; i++){
            out[i] /= tot;
        }
        return out;
    }

    /**
     * Performs matrix multiplication on a matrix multiplied by a vector
     * @param m Matrix
     * @param v Vector
     * @return Matrix * Vector
     */
    public double[][] mmult(double[][] m, double[] v){
        double[][] out = new double[m.length][m[0].length];

        for (int i = 0; i < m.length; i++){
            for(int j = 0; j < m[0].length; j++){
                out[i][j] = m[i][j] * v[j];
            }
        }
        return out;
    }

    /**
     * Performs matrix multiplication on a vector by another vector
     * @param v1 Vector 1
     * @param v2 Vector 2 (to be transposed)
     * @return v1 * v2.T
     */
    public double[] mmult(double[] v1, double[] v2){
        double[] out = new double[v1.length];

        for (int i = 0; i < v1.length; i++){
                out[i] = v1[i] * v2[i];
        }
        return out;
    }

    /**
     * Performs matrix addition on a matrix by a scalar
     * @param m Matrix
     * @param s Scalar
     * @return Matrix + Scalar
     */
    public double[][] madd(double[][] m, double s){
        double[][] out = new double[m.length][m[0].length];

        for (int i = 0; i < m.length; i++){
            for(int j = 0; j < m[0].length; j++){
                out[i][j] = m[i][j] + s;
            }
        }
        return out;
    }

    /**
     * Prints the vector to the console "name: [values, ...]
     * @param v vector to be printed
     * @param name name of vector
     */
    private void print_vector(double[] v, String name){
        System.out.print(name.concat(": "));
        for (double s : v) {
            System.out.print(s);
            System.out.print(", ");
        }
        System.out.println();
    }
    /*private GMatrix g_softmax(GMatrix input, GMatrix weights, GMatrix bias){
        GMatrix j = new GMatrix(weights.getNumCol(), weights.getNumRow());
        j.mul(weights, input);
        j.add(bias);

        return j;
    }*/
}
