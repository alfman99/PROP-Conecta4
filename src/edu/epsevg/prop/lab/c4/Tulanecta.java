package edu.epsevg.prop.lab.c4;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tulanecta. Actividad de PROP
 *
 * @author Mario Konstanty Kochan Chmielik
 * @author Alfredo Manresa Martínez
 */
public class Tulanecta
        implements Jugador, IAuto {

    private final String nom;
    private int color;
    private final int profundidad;
    private int[] alturas;
    private ArrayList<int[]> direcciones;

    /**
     * Metodo de inicialización del jugador "Tulanecta", donde se incializan 
     * las variables principales para el funcionamiento de este. 
     * 
     * @param profundidad se le pasa la profundidad máxima a la que se jugará
     */
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

    /**
     * Metodo que se inicializa cada vez que nos toca elegir el movimiento. Este metodo
     * llama a la función minimax para así obtener el mejor movimiento.
     * 
     * @param t es el tablero que esta actualmente en la partida.
     * @param color es el color con el que jugamos.
     * @return eleccion que es la eleccion que nos queda después de hacer el minimax con el heur.
     * 
     * @see obtenerCol(Tauler t, int auxProfundidad)
     */
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
    
     /**
     * Metodo que realiza todas las primeras tiradas del tablero T. Apartir de 
     * estas tiradas llamamos al minimax para ver las jugadas con el Min. 
     * Este Metodo funciona sin el alfa y el beta. 
     * 
     * @param t es el tablero que esta actualmente en la partida.
     * @param auxProfundidad es la profundidad a la que estamos jugando.
     * @return mejorJugada que es la mejor columna para tirar la ficha.
     * 
     * @see minimaxSinAlfaBeta(Tauler t, int profundidad, boolean isMax)
     */
    protected int obtenerColSinAlfaBeta(Tauler t, int auxProfundidad) {
        int mejorHeur = Integer.MIN_VALUE;
        int mejorJugada = -1;
        for (int i = 0; i < t.getMida(); i++) {
            if (t.movpossible(i)) {
                Tauler aux = new Tauler(t);
                aux.afegeix(i, this.color);
                if (aux.solucio(i, this.color)) {
                    return Integer.MAX_VALUE;
                }
                else{
                    int eval = minimaxSinAlfaBeta(aux, auxProfundidad - 1, false);
                    if (eval > mejorHeur || mejorJugada == -1) {
                        mejorJugada = i;
                        mejorHeur = eval;
                    }
                }
            }
        }
        return mejorJugada;
    }

    /**
     * Metodo que realiza el desarrollo del árbol heurístico MiniMax de forma 
     * recursivas. Comprueva el valor máximo si isMax es verdadero y en caso
     * contrario mira el valor mínimo.
     * 
     * @param t es el tablero que esta actualmente en la partida.
     * @param profundidad es la profundidad a la que estamos jugando. 
     * @param isMax es un flag para indicar si es Max o Min.
     * @return heur que es la heurística de la jugada actual. 
     * 
     */
    protected int minimaxSinAlfaBeta(Tauler t, int profundidad, boolean isMax) {
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
    /**
     * Metodo que realiza todas las primeras tiradas del tablero T. Apartir de 
     * estas tiradas llamamos al minimax para ver las jugadas con el Min. 
     * Este Metodo funciona es con el alfa y el beta. 
     * 
     * @param t es el tablero que esta actualmente en la partida
     * @param auxProfundidad es la profundidad a la que estamos jugando 
     * @return mejorJugada que es la mejor columna para tirar la ficha
     * 
     * @see obtenerCol(Tauler t, int auxProfundidad)
     */
    protected int obtenerCol(Tauler t, int auxProfundidad) {
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
    
    /**
     * Metodo que realiza el desarrollo del árbol heurístico MiniMax de forma 
     * recursivas. Comprueva el valor máximo si isMax es verdadero y en caso
     * contrario mira el valor mínimo. Este lo realiza con la poda, para así 
     * poder evitar cercar ramas innecesarias del árbol. 
     * 
     * @param t es el tablero que esta actualmente en la partida.
     * @param profundidad es la profundidad a la que estamos jugando.
     * @param alfa es el valor heurístico minimo de la poda
     * @param beta es el valor heurístico máximo de la poda 
     * @param isMax es un flag para indicar si es Max o Min.
     * @return nuevaBeta, nuevaAlfa, Alfa, Beta que es la heurística que representa la jugada actual. 
     * 
     */
    protected int minimax(Tauler t, int profundidad, int alfa, int beta, boolean isMax) {
        
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
    
     /**
     * Posible modificaciones futuras, lo dejo para el final
     */
    protected int largo (Tauler t, int i, int j, int direccionX, int direccionY, int color) {
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
    
     /**
     * Metodo que calcula la heurística de un tablero entero. Para calcular la heurística, 
     * comprueva todas las fichas del tablero y le da prioridad aquellas fichas que esten
     * juntas. 
     * 
     * @param t es el tablero que esta actualmente en la partida.
     * 
     * @return puntos que indica cual de buena es la jugada que se está planteando. 
     * 
     */
    protected int heur(Tauler t) {
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

    /**
     * Metodo que devuelve el nombre del jugador.
     * 
     * @return nom que representa el nombre del jugador
     */
    public String nom() {
        return nom;
    }
}
