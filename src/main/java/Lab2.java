import commons.Complex;
import commons.MathHelper;
import commons.Timer;
import commons.VecCommons;
import services.FFT;

import java.util.ArrayList;
import java.util.List;

public class Lab2 extends VecCommons implements MathHelper {

    public static int sumMilisecDFT = 0;
    public static int sumMilisecFFT = 0;

    private List<Double> vectorX        = new ArrayList<>();

    private List<Double> vectorRe       = new ArrayList();
    private List<Double> vectorIm       = new ArrayList();

    private List<Double> vectorMkDFT    = new ArrayList();
    private List<Double> vectorMdkDFT   = new ArrayList();
    private List<Double> vectorFkDFT    = new ArrayList();

    private List<Double> vectorMkFFT    = new ArrayList();
    private List<Double> vectorMdkFFT   = new ArrayList();
    private List<Double> vectorFkFFT    = new ArrayList();

    private int nMax = 32;

    String vectorName = "";

    public void reCalculate(List vector) {
        this.vectorX = vector;
        reCalculate(vector.size());
    }

    public void reCalculate(List vector, String vecName) {
        this.vectorX = vector;
        this.vectorName = vecName;
        reCalculate(vector.size());
    }

    @Override
    protected void reCalculate(int nNewMax) {
        clear(
                vectorRe,
                vectorIm,
                vectorMkDFT,
                vectorMdkDFT,
                vectorFkDFT,
                vectorMkFFT,
                vectorMdkFFT,
                vectorFkFFT
        );

        nMax = nNewMax;


        Timer timer = new Timer();
        calculateDFT();
        double dftTime = timer.milisec(); timer.reset();
        vectorMkDFT = calculateMk();
        vectorMdkDFT = calculateDerivativeMk(vectorMkDFT);
        vectorFkDFT = calculateFk();

        calculateFFT();
        double fftTime = timer.milisec(); timer.reset();
        vectorMkFFT = calculateMk();
        vectorMdkFFT = calculateDerivativeMk(vectorMkFFT);
        vectorFkFFT = calculateFk();

        System.out.println(vectorName + " \tDFT = " + dftTime + "ms\t" + (dftTime > 99 ? "" : "\t") + "|\tFFT = " + fftTime + "ms |\t ilosc probek = " + vectorFkFFT.size());
        sumMilisecDFT += dftTime;
        sumMilisecFFT += fftTime;
    }

    void calculateFFT() {
        Timer timer = new Timer();
        Complex[] complexX = new Complex[vectorX.size()];
        for (int i = 0; i < vectorX.size(); i++) {
            complexX[i] = new Complex(vectorX.get(i), 0);
        }
        Complex[] reimArray = FFT.fft(complexX);

        vectorRe.clear();
        vectorIm.clear();
        for (int i = 0; i < reimArray.length; i++) {
            vectorRe.add(reimArray[i].re());
            vectorIm.add(reimArray[i].im());
        }
    }

    public void calculateDFT() {
        Timer timer = new Timer();
        for (int k = 0; k < nMax; k++) {
            double Re = 0;
            double Im = 0;

            for (int n = 0; n < nMax && n < vectorX.size(); n++) {
                double xn = (double) vectorX.get(n);
                double cosX = Math.cos(-1 * 2 * Math.PI * n * k / nMax);
                double sinX = Math.sin(-1 * 2 * Math.PI * n * k / nMax);

                Re += xn * cosX;
                Im += xn * sinX;
            }

            vectorRe.add(Re);
            vectorIm.add(Im);
        }
    }

    private List<Double> calculateMk() {
        List<Double> vector = new ArrayList<>();
        for (int k = 0; k < nMax / 2; k++) {
            double re = vectorRe.get(k);
            double im = vectorIm.get(k);
            double re2 = re * re;
            double im2 = im * im;
            double sum = re2 + im2;
            double result = Math.sqrt(sum);
            vector.add(result);
        }
        return vector;
    }

    public List<Double> calculateDerivativeMk(List<Double> mkVec) {
        List<Double> vector = new ArrayList<>();
        for (int k = 0; k < nMax / 2; k++) {
            double result = 10 * log(mkVec.get(k), 10);
            result = (Double.isInfinite(result) ? 0 : result);
            vector.add(result);
        }
        return vector;
    }

    public List<Double> calculateFk() {
        List<Double> vector = new ArrayList<>();
        for (int k = 0; k < nMax / 2; k++) {
            double result = (k * Lab1.Fn) / nMax;
            vector.add(result);
        }
        return vector;
    }

    private double getX(int k) {
        return checkVec(k, vectorRe);
    }

    private double getZ(double aRe, double bIm) {
        return Math.sqrt(Math.pow(aRe, 2) + Math.pow(bIm, 2));
    }

    private double getAlpha(double re, double im) {
        double tgAlpha = im / re;
        double alpha = Math.atan(tgAlpha);
        return alpha;
    }

    public List<Double> getVectorMkDFT() {
        return vectorMkDFT;
    }

    public List<Double> getVectorMdkDFT() {
        return vectorMdkDFT;
    }

    public List<Double> getVectorFkDFT() {
        return vectorFkDFT;
    }

    public List<Double> getVectorMkFFT() {
        return vectorMkFFT;
    }

    public List<Double> getVectorMdkFFT() {
        return vectorMdkFFT;
    }

    public List<Double> getVectorFkFFT() {
        return vectorFkFFT;
    }

    public List<Double> getVectorRe() {
        return vectorRe;
    }

    public List<Double> getVectorIm() {
        return vectorIm;
    }
}
