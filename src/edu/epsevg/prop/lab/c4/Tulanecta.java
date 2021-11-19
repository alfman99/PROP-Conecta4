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
        int eleccion = obtenerCol(t, depth);
        try {
            Thread.sleep((long)1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Tulanecta.class.getName()).log(Level.SEVERE, null, ex);
        }
        return eleccion;
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
                    aux.afegeix(i, auxColor);
                    if (aux.solucio(i, auxColor)) {
                        heur = Integer.MAX_VALUE;
                    }
                    else{
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
                    if (aux.solucio(i, auxColor)) {
                        heur = Integer.MIN_VALUE;
                    }
                    else{
                        int eval = minimaxSinAlfaBeta(aux, profundidad - 1, true);
                        heur = Math.min(eval, heur);
                    }
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
                if (aux.solucio(i, this.color)) {
                    return Integer.MAX_VALUE;
                }
                else {
                    int alfa = minimax(aux, auxProfundidad - 1, mejorHeur, Integer.MAX_VALUE , false);
                    if (alfa > mejorHeur || mejorJugada == -1) {
                        mejorJugada = i;
                        mejorHeur = alfa;
                    }
                }
                
            }
        }
        return mejorJugada;
    }
    
    private int minimax(Tauler t, int profundidad, int alfa, int beta, boolean isMax) {
        
        if (profundidad <= 0) {
            return heur(t);
        }

        if (isMax) {
            int nuevaAlfa = Integer.MIN_VALUE;
            for (int i = 0; i < t.getMida(); i++) {
                if (t.movpossible(i)) {
                    Tauler aux = new Tauler(t);
                    aux.afegeix(i, this.color);
                    if (aux.solucio(i, this.color)) {
                        return Integer.MAX_VALUE;
                    }
                    else {
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
                    aux.afegeix(i, this.color*-1);
                    if (aux.solucio(i, this.color*-1)) {
                        return Integer.MIN_VALUE;
                    }
                    else {
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

    private int largo (Tauler t, int i, int j, int direccionX, int direccionY, int color) {
        int size = t.getMida();
        int score = 0;
        for (int k = 0; k < 4; k++) {
            i += direccionX;
            j += direccionY;
            if (i < 0 || i >= size || j < 0 || j >= size) {
                return -4;
            }
            int colorPos = t.getColor(i, j);
            if (color == colorPos) {
                score += 2;
            }
            else if (color != colorPos && colorPos != 0) {
                return -8;
            }
        }
        /*if (newX < 0 || newX >= size || newY < 0 || newY >= size) {
            return -4;
        }
        if (t.getColor(i+direccionX, j+direccionY) == color || t.getColor(i+direccionX, j+direccionY) == 0) {
            int sum = 1;
            if (t.getColor(i+direccionX, j+direccionY) == 0) {
                sum = 0;
            }
            return sum + largo(t, newX, newY, direccionX, direccionY, color);
        }
        else {
            return 0;
        }*/
        return score;
    }
    
    private int heur(Tauler t) {
        int puntos = 0;
        for (int i = 0; i < t.getMida(); i++) {
            for (int j = 0; j < t.getMida(); j++) {
                if (t.getColor(i, j) == this.color) {
                    for (int[] direc : direcciones) {
                        int dirX = direc[0];
                        int dirY = direc[1];
                        int a = largo(t, i, j, dirX, dirY, this.color);
                        if (a >= 4)
                            puntos = Integer.MAX_VALUE;
                        else if (a > 1)
                            puntos += a*2;
                        else
                            puntos += a;
                    }
                }
                else if (t.getColor(i, j) == this.color*-1) {
                    for (int[] direc : direcciones) {
                        int dirX = direc[0];
                        int dirY = direc[1];
                        int a = largo(t, i, j, dirX, dirY, this.color * -1);
                        if (a >= 4)
                            puntos = Integer.MIN_VALUE;
                        else if (a > 1)
                            puntos -= a*2;
                        else
                            puntos -= a*2;
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
