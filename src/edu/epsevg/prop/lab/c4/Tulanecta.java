package edu.epsevg.prop.lab.c4;

import java.util.ArrayList;

/**
 * Tulanecta. Actividad de PROP
 * 
 * Win ratio (inicio/profundidad/modoEnemigo/veredicto)
 * 
 * p1 8 false win
 * p1 4 false win
 * p2 8 false win
 * p2 4 false win
 * 
 * p1 8 true lose
 * p1 4 true win
 * p2 8 true win
 * p2 4 true taules
 *
 * @author Mario Konstanty Kochan Chmielik
 * @author Alfredo Manresa Martínez
 */
public class Tulanecta
        implements Jugador, IAuto {

    /**
     * Nombre de nuestro robot
     */
    protected final String nom;
    
    /**
     * Color que nos ha tocado en la partida, 1 o -1
     */
    protected int color;
    
    /**
     * Profundidad a la que el minimax tendrá que explorar
     */
    protected final int profundidad;
    
    /**
     * Lista de direcciones en las que la heuristica tiene que explorar
     */
    protected final ArrayList<int[]> direcciones;
    
    /**
     * Indica si queremos o no utilizar la poda alfa-beta
     */
    protected final boolean alfabeta;
    
    /**
     * Output del tipo csv para poder hacer las graficas mas facilmente
     * true -> output csv (numJugada;numVecesHeur)
     * false -> output bonito (Todos los posibles movimientos con su valor heursitico, tiempo que ha tardado en calcular ese movimento, tiempo total que lleva el bot calculando)
     */
    protected final boolean csvOutput;
    
    /**
     * El numero del movimiento que hemos hecho
     * Se utilizar para el output en csv para 
     * hacer la grafica
     */
    protected int numJugada;
    
    /**
     * Cantidad de veces que se llama a la heuristica (se resetea cada nuevo movimiento)
     */
    protected int llamadasHeur;
    
    /**
     * Tiempo de calculo del movimiento actual
     */
    protected long timeTurn;
    
    /**
     * Tiempo de calculo total
     */
    protected long timeTotal;
    

    /**
     * Metodo de constructor del jugador "Tulanecta", donde se incializan 
     * las variables principales para el funcionamiento de este. 
     * 
     * @param profundidad se le pasa la profundidad máxima a la que se jugará
     * @param alfabeta indica si debemos utilizar la poda alfa-beta o no
     */
    public Tulanecta(int profundidad, boolean alfabeta) {
        this.nom = "Tulanecta";
        this.profundidad = profundidad;
        this.direcciones = new ArrayList<>();
        this.alfabeta = alfabeta;
        this.csvOutput = false;
        this.llamadasHeur = 0;
        this.numJugada = 0;
        
        if (this.csvOutput) {
            System.out.println("tirada;numheur");
        }

        direcciones.add(new int[] { 1, 0 });
        direcciones.add(new int[] { 1, 1 });
        direcciones.add(new int[] { 0, 1 });
        direcciones.add(new int[] { -1, 1 });
        direcciones.add(new int[] { -1, 0 });
    }

    /**
     * Metodo que se inicializa cada vez que nos toca elegir el movimiento. 
     * Este metodo llama a la función obternerCol para así obtener el mejor movimiento.
     * Según el numero de pasos y la evalucación de la heuristica del tablero
     * 
     * @param t es el tablero que esta actualmente en la partida.
     * @param color es el color con el que jugamos.
     * @return eleccion que es la eleccion que nos queda después de hacer el minimax con el heur.
     * 
     * @see obtenerCol(Tauler t, int auxProfundidad)
     * @see obtenerColSinAlfaBeta(Tauler t, int auxProfundidad)
     */
    @Override
    public int moviment(Tauler t, int color) {
        
        this.timeTurn = System.nanoTime();
        
        this.color = color;
        int depth = profundidad;
        
        this.numJugada++;
        
        int eleccion;
        if (this.alfabeta) {
            eleccion = obtenerCol(t, depth);
        }
        else {
            eleccion = obtenerColSinAlfaBeta(t, depth);
        }
        
        this.timeTurn = System.nanoTime() - this.timeTurn;
        this.timeTotal += this.timeTurn;
        if (!this.csvOutput) {
            System.out.println("Tiempo calculando en este movimiento: " + (this.timeTurn / 1000000) + "ms\nTiempo total calculando: " + (timeTotal/ 1000000) + "ms\n");
        }
        
        return eleccion;
    }
    
    
    /**
     * Metodo que realiza todas las primeras tiradas del tablero T.
     * Primera capa del minimax.
     * Apartir de estas tiradas llamamos al minimax para ver las jugadas con el Minimax
     * Este Metodo funciona sin la poda alfa-beta. 
     * 
     * @param t es el tablero que esta actualmente en la partida.
     * @param auxProfundidad es la profundidad a la que estamos jugando.
     * @return mejorJugada que es la mejor columna para tirar la ficha.
     * 
     * @see minimaxSinAlfaBeta(Tauler t, int profundidad, boolean isMax)
     */
    protected int obtenerColSinAlfaBeta(Tauler t, int auxProfundidad) {
        this.llamadasHeur = 0;
        int mejorHeur = Integer.MIN_VALUE;
        int mejorJugada = -1;
        for (int i = 0; i < t.getMida(); i++) {
            int heur = Integer.MIN_VALUE;
            if (t.movpossible(i)) {
                Tauler aux = new Tauler(t);
                aux.afegeix(i, this.color);
                if (aux.solucio(i, this.color)) {
                    return i;
                }
                else {
                    heur = minimaxSinAlfaBeta(aux, auxProfundidad - 1, false);
                    if (heur > mejorHeur || mejorJugada == -1) {
                        mejorJugada = i;
                        mejorHeur = heur;
                    }
                }
            }
            if (!this.csvOutput) {
                System.out.println("Columna " + i + " heur: " + heur);
            }
        }
        if (this.csvOutput) {
            System.out.println(this.numJugada + ";" + this.llamadasHeur);
        }
        else {
            System.out.println("Jugada elegida: " + mejorJugada + "\n" + "Cantidad de veces calculada la funcion heursitica: " + this.llamadasHeur);
        }
        return mejorJugada;
    }

    
    /**
     * Metodo que realiza el desarrollo del árbol MiniMax de forma recursiva.
     * Comprueva el valor máximo si isMax es verdadero y en caso
     * contrario mira el valor mínimo.
     * 
     * @param t es el tablero que esta actualmente en la partida.
     * @param profundidad es la profundidad a la que estamos jugando. 
     * @param isMax es un flag para indicar si es Max o Min.
     * @return heur que es la heurística de la jugada actual. 
     * 
     */
    protected int minimaxSinAlfaBeta(Tauler t, int profundidad, boolean isMax) {
                
        if (profundidad <= 0) {
            return heur(t);
        }

        if (isMax) {
            int maxHeur = Integer.MIN_VALUE;
            for (int i = 0; i < t.getMida(); i++) {
                if (t.movpossible(i)) {
                    Tauler aux = new Tauler(t);
                    aux.afegeix(i, this.color);
                    if (aux.solucio(i, this.color)) {
                        return Integer.MAX_VALUE;
                    }
                    else {
                        maxHeur = Math.max(maxHeur, minimaxSinAlfaBeta(aux, profundidad - 1, false));
                    }
                }
            }
            return maxHeur;
        } else {
            int minHeur = Integer.MAX_VALUE;
            for (int i = 0; i < t.getMida(); i++) {
                if (t.movpossible(i)) {
                    Tauler aux = new Tauler(t);
                    aux.afegeix(i, this.color*-1);
                    if (aux.solucio(i, this.color*-1)) {
                        return Integer.MIN_VALUE;
                    }
                    else {
                        minHeur = Math.min(minHeur, minimaxSinAlfaBeta(aux, profundidad - 1, true));
                    }
                }
            }
            return minHeur;
        }
    }
    
    
    /**
     * Metodo que realiza todas las primeras tiradas del tablero T. Apartir de 
     * estas tiradas llamamos al minimax para ver las jugadas con el Min. 
     * Este Metodo funciona con la poda alfa-beta. 
     * 
     * @param t es el tablero que esta actualmente en la partida
     * @param auxProfundidad es la profundidad a la que estamos jugando 
     * @return mejorJugada que es la mejor columna para tirar la ficha
     * 
     * @see obtenerCol(Tauler t, int auxProfundidad)
     */
    protected int obtenerCol(Tauler t, int auxProfundidad) {
        this.llamadasHeur = 0;
        int mejorHeur = Integer.MIN_VALUE;
        int mejorJugada = -1;
        for (int i = 0; i < t.getMida(); i++) {
            int alfa = Integer.MIN_VALUE;
            if (t.movpossible(i)) {
                Tauler aux = new Tauler(t);
                aux.afegeix(i, this.color);
                if (aux.solucio(i, this.color)) {
                    return i;
                }
                else {
                    alfa = minimax(aux, auxProfundidad - 1, mejorHeur, Integer.MAX_VALUE , false);
                    if (alfa > mejorHeur || mejorJugada == -1) {
                        mejorJugada = i;
                        mejorHeur = alfa;
                    }
                }
            }
            if (!this.csvOutput) {
                System.out.println("Columna " + i + " heur: " + alfa);
            }
        }
        if (this.csvOutput) {
            System.out.println(this.numJugada + ";" + this.llamadasHeur);
        }
        else {
            System.out.println("Jugada elegida: " + mejorJugada + "\n" + "Cantidad de veces calculada la funcion heursitica: " + this.llamadasHeur);
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
     * Este métode devuelve el valor de una casilla para una dirección en el tablero. La dirección 
     * siempre es hacía los lados o hacia arriba, y se mueve hasta 4 posiciones (al menos que se encuentre
     * con algun borde). El valor se calcula teniendo en cuenta el numero de casillas en blanco y ocupadas por 
     * algun color.
     * 
     * @param t
     * @param i
     * @param j
     * @param direccionX
     * @param direccionY
     * @return score representa el valor de la puntuación de una casilla para sus direcciones
     */
    protected int largo (Tauler t, int i, int j, int direccionX, int direccionY, int color) {
        int size = t.getMida();
        int score = 0;
        for (int k = 0; k < 4; k++) {
            i += direccionX;
            j += direccionY;
            if (i < 0 || i >= size || j < 0 || j >= size) {
                break;
            }
            int colorPos = t.getColor(i, j);
            if (colorPos == 0) {
                score += 3;
            }
            else {
                if (colorPos == color) {
                    score += 2;                    
                }
                else {
                    score -= 1;
                }
            }
        }
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
        
        this.llamadasHeur++;
        
        int puntosYo = 0;
        int puntosEnemigo = 0;
        int size = t.getMida();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (t.getColor(i, j) == this.color) {
                    for (int[] direc : direcciones) {
                        int dirX = direc[0];
                        int dirY = direc[1];
                        int puntos = largo(t, i, j, dirX, dirY, this.color);
                        puntosYo += puntos;
                    }
                }
                else if (t.getColor(i, j) == this.color*-1) {
                    for (int[] direc : direcciones) {
                        int dirX = direc[0];
                        int dirY = direc[1];
                        int puntos = largo(t, i, j, dirX, dirY, this.color*-1);
                        puntosEnemigo += puntos;
                    }
                }
            }
        }
        return puntosYo - puntosEnemigo;
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
