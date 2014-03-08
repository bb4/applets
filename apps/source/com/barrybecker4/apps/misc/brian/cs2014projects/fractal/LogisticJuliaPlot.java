package com.barrybecker4.apps.misc.brian.cs2014projects.fractal;


import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

/**
 * @author Period 5
 */
class LogisticJuliaPlot extends Canvas {

    private int maxcol = 399, maxrow = 399, max_colors = 16,
    max_iterations = 256, max_size = 4;
    private Color cmap[];
    public LogisticJuliaPlot() {
        cmap = new Color[max_colors];
        cmap[0] = Color.black;
        cmap[1] = new Color(0, 0, 168);
        cmap[2] = new Color(100, 50, 0);
        cmap[3] = new Color(0, 168, 168);
        cmap[4] = new Color(168, 0, 0);
        cmap[5] = new Color(168, 0, 168);
        cmap[6] = new Color(168, 84, 0);
        cmap[7] = new Color(168, 168, 168);
        cmap[8] = new Color(84, 84, 84);
        cmap[9] = new Color(84, 84, 255);
        cmap[10] = new Color(84, 255, 84);
        cmap[11] = new Color(84, 255, 255);
        cmap[12] = new Color(255, 84, 84);
        cmap[13] = new Color(255, 84, 255);
        cmap[14] = new Color(255, 255, 84);
        cmap[15] = Color.white;
    }

    private void plot(Graphics g, int x, int y, int color_index) {

        g.setColor(cmap[color_index]);
        g.drawLine(x, y, x, y);
    }

    public void paint(Graphics g) {

        float Q[] = new float[400];
        double Pmax = 1.5, Pmin = -.5, Qmax = .7, Qmin = -.7, A = 1.678, B = .95,
                P, deltaP, deltaQ, X, Y, Xfactor, Yfactor, Xsquare, Ysquare;

        int color, row, col;//(1.68,.95)
        deltaP = (Pmax - Pmin) / (double) (maxcol - 1);
        deltaQ = (Qmax - Qmin) / (double) (maxrow - 1);
        for (row = 0; row <= maxrow; row++) {
            Q[row] = (float) (Qmin + row * deltaQ);
        }

        for (col = 0; col <= maxcol; col++) {
            P = Pmin + col * deltaP;
            for (row = 0; row <= maxrow; row++) {
                for (color = 0; color <= max_iterations - 1; color++) {
                    X = P;
                    Y = Q[row];
                    color = 0;
                    Xfactor = X - X * X + Y * Y;
                    Yfactor = 2 * X * Y - Y;
                    X = A * Xfactor + B * Yfactor;
                    Y = B * Xfactor - A * Yfactor;
                    Xsquare = X * X;
                    Ysquare = Y * Y;

                    if ((Xsquare + Ysquare) > max_size) break;
                }
                plot(g, col, row, color % max_colors);
            }
        }
    }
}