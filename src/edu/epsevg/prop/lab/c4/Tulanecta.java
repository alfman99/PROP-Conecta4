package edu.epsevg.prop.lab.c4;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Jugador aleatori "Alea jacta est"
 *
 * @author Profe
 */
public class Tulanecta
        implements Jugador, IAuto {

    private final String nom;
    private int color;
    private final int profundidad;
    private int[] alturas;
    private ArrayList<int[]> direcciones;

    public Tulanecta(int profundidad) {
        this.nom = "Tulanecta";
        this.profundidad = profundidad;
        this.direcciones = new ArrayList<>();

        direcciones.add(new int[] { 1, 0 });
        direcciones.add(new int[] { 1, 1 });
        direcciones.add(new int[] { 0, 1 });
        direcciones.add(new int[] { -1, 1 });
        
        direcciones.add(new int[] { -1, 0 });
        direcciones.add(new int[] { -1, -1 });
        direcciones.add(new int[] { 0, -1 });
        direcciones.add(new int[] { 1, -1 });
    }

    @Override
    public int moviment(Tauler t, int color) {
        this.color = color;
        int depth = profundidad;
        try {
            Thread.sleep((long)2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Tulanecta.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obtenerCol(t, depth);
    }
    
    private int obtenerColSinAlfaBeta(Tauler t, int auxProfundidad) {
        int mejorHeur = Integer.MIN_VALUE;
        int mejorJugada = -1;
        for (int i = 0; i < t.getMida(); i++) {
            if (t.movpossible(i)) {
                Tauler aux = new Tauler(t);
                aux.afegeix(i, this.color);
                int eval = minimaxSinAlfaBeta(aux, auxProfundidad - 1, false);
                if (eval > mejorHeur || mejorJugada == -1) {
                    mejorJugada = i;
                    mejorHeur = eval;
                }
            }
        }
        return mejorJugada;
    }

    private int minimaxSinAlfaBeta(Tauler t, int profundidad, boolean isMax) {
        int otherColor = this.color == 1 ? -1 : 1;
        int auxColor = isMax ? this.color : otherColor;

        if (profundidad <= 0) {
            return heur(t);
        }

        int heur;
        if (isMax) {
            heur = Integer.MIN_VALUE;
            for (int i = 0; i < t.getMida(); i++) {
                if (t.movpossible(i)) {
                    Tauler aux = new Tauler(t);
                    if (aux.solucio(i, auxColor)) {
                        if (auxColor == this.color) {
                            heur = Integer.MAX_VALUE;
                        }
                        else {
                            heur = Integer.MIN_VALUE;
                        }
                    }
                    else {
                        aux.afegeix(i, auxColor);
                        int eval = minimaxSinAlfaBeta(aux, profundidad - 1, false);
                        heur = Math.max(eval, heur);
                    }
                }
            }
        } else {
            heur = Integer.MAX_VALUE;
            for (int i = 0; i < t.getMida(); i++) {
                if (t.movpossible(i)) {
                    Tauler aux = new Tauler(t);
                    aux.afegeix(i, auxColor);
                    int eval = minimaxSinAlfaBeta(aux, profundidad - 1, true);
                    heur = Math.min(eval, heur);
                }
            }
        }

        return heur;
    }
    
    private int obtenerCol(Tauler t, int auxProfundidad) {
        int mejorHeur = Integer.MIN_VALUE;
        int mejorJugada = -1;
        for (int i = 0; i < t.getMida(); i++) {
            if (t.movpossible(i)) {
                Tauler aux = new Tauler(t);
                aux.afegeix(i, this.color);
                int alfa = minimax(aux, auxProfundidad - 1, mejorHeur, Integer.MAX_VALUE , false);
                if (alfa > mejorHeur || mejorJugada == -1) {
                    mejorJugada = i;
                    mejorHeur = alfa;
                }
            }
        }
        return mejorJugada;
    }
    
    private int minimax(Tauler t, int profundidad, int alfa, int beta, boolean isMax) {
        int otherColor = this.color == 1 ? -1 : 1;
        int auxColor = isMax ? this.color : otherColor;
        
        if (profundidad <= 0) {
            return heur(t);
        }

        if (isMax) {
            int nuevaAlfa = Integer.MIN_VALUE;
            for (int i = 0; i < t.getMida(); i++) {
                if (t.movpossible(i)) {
                    Tauler aux = new Tauler(t);
                    if (aux.solucio(i, auxColor)) {
                        nuevaAlfa = Integer.MAX_VALUE;
                    }
                    else {
                        aux.afegeix(i, auxColor);
                        nuevaAlfa = Math.max(nuevaAlfa, minimax(aux, profundidad - 1, alfa, beta, false));
                        alfa = Math.max(nuevaAlfa, alfa);
                        if (alfa >= beta) {
                            return alfa;
                        }
                    }
                }
            }
            return nuevaAlfa;
        } else {
            int nuevaBeta = Integer.MAX_VALUE;
            for (int i = 0; i < t.getMida(); i++) {
                if (t.movpossible(i)) {
                    Tauler aux = new Tauler(t);
                    if (aux.solucio(i, auxColor)) {
                        nuevaBeta = Integer.MIN_VALUE;
                    }
                    else {
                        aux.afegeix(i, auxColor);
                        nuevaBeta = Math.min(nuevaBeta, minimax(aux, profundidad - 1, alfa, beta, true));
                        beta = Math.min(nuevaBeta, beta);
                        if (alfa >= beta) {
                            return beta;
                        }
                    }
                }
            }
            return nuevaBeta;
        }
    }

    private int largo (Tauler t, int i, int j, int direccionX, int direccionY) {
        int size = t.getMida();
        int newX = i+direccionX;
        int newY = j+direccionY;
        if (newX < 0 || newX >= size || newY < 0 || newY >= size) {
            return 0;
        }
        if (t.getColor(i+direccionX, j+direccionY) == this.color) {
            return 1 + largo(t, newX, newY, direccionX, direccionY);
        }
        else {
            return 0;
        }
    }
    
    private int heur(Tauler t) {
        int puntos = 0;
        for (int i = 0; i < t.getMida(); i++) {
            for (int j = t.getMida() - 1; j >= 0; j--) {
                if (t.getColor(i, j) == this.color) {
                    for (int[] direc : direcciones) {
                        int dirX = direc[0];
                        int dirY = direc[1];
                        int a = largo(t, i, j, dirX, dirY);
                        if (a > 1)
                            puntos += a*2;
                        else
                            puntos += a;
                    }
                }
            }
        }
        return puntos;
    }

    // private int 
    public String nom() {
        return nom;
    }
}
