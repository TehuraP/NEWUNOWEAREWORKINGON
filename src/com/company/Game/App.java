package com.company.Game;

import com.company.Cards.*;
import com.company.Players.HumanPlayer;
import com.company.Players.Player;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Scanner;

public class App {
    private Deck deck = new Deck();
    private ArrayList<Player> players = new ArrayList<Player>();
    private AblageStapel stapel = new AblageStapel();
    private final Scanner input;
    private final PrintStream output;
    private Player currentPlayer;
    private boolean exit = false;
    private int currentPlayerIndex = 0;
    private Card playedCard;

    public App(Scanner input, PrintStream output) {
        this.input = input;
        this.output = output;
    }


    // gameloop implementieren
    public void Run() {
        // alles was einmal stattfindet: handout(take card), namen eingeben,
        initialize();
        //karteAblegen();
        exit = false;
        //ganze spiel schleife
        while (!exit) {
//            printState();
//            playCard();
            for (Player p : players) {
                printState();
                playCard();
//                System.out.println("Player: " + currentPlayer.getName());
                currentPlayer = nextPlayer();

            }
//            currentPlayer = nextPlayer();

        }

        //TODO: Schleife draus machen! Darf/Muss noch verändert werden --> Doppelschleife

        //karte muss ausgewählt werden, dann gespielt, entfernt von die handout und im stapel hingefügt
        //spielrunden(game), draw/remove card
    }

    public void initialize() {
        deck.shuffle();
        addPlayer();
        for (Player sp : players) {
            handOut(sp);
        }

        stapel.ersteKarte(deck);
        if (stapel.obersteKarte().value == Value.SKIP) {
            nextPlayer();
        }

        Collections.shuffle(players);
        currentPlayer = players.get(0);
        System.out.println("The first player is: " + currentPlayer.getName());
    }

    public String readInput(Scanner eingabe) {
        System.out.println("Bitte Karte eingeben: ");
        String var = eingabe.nextLine();

        return var;
    }

    private void printState() {
        //welche karte gespielt wurde
        System.out.println("Das ist die Hand von player:  " + currentPlayer.getName());
        output.println(currentPlayer.printHand());
    }

    public void addPlayer() {
        String name;
        for (int j = 0; j < 4; j++) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter your name");

            name = scanner.next();

            System.out.println("Hallo, " + name);
            HumanPlayer sp1 = new HumanPlayer(name);
            players.add(sp1);
        }
    }

    public void handOut(Player spieler) {

        for (int i = 0; i < 7; i++) {
            spieler.takeCard(deck.drawCard());

        }
    }


    public void playCard() {

        boolean exit = false;

//        //TODO: verbessern
        currentPlayer.getHand().contains(playedCard);
        // playedCard.setCard(input.toString());

        exit = false;
        while (!exit) {
            String cardInput = readInput(input);
            cardInput.toLowerCase(Locale.ROOT);
            System.out.println("Ihre Eingabe war: " + cardInput);
            Card validCard = currentPlayer.checkIfCardIsInHandCards(cardInput);
            System.out.println("checkIfCardIsInHandCards: return value: " + validCard);

            if (validCard == null) {
                System.out.println("Ihre Eingabe war falsch, bitte nochmal: " + currentPlayer.printHand() + "Erste Karte: " + stapel.obersteKarte());
                playCard();
            } else {
                if (cardCanBePlayed(validCard, stapel.obersteKarte())) {
                    System.out.println("old oberste karte : " + stapel.obersteKarte());
                    stapel.ablegen(validCard);
                    validCard = stapel.obersteKarte();
                    System.out.println("new oberste karte : " + validCard);
                    currentPlayer.removeCardFromHand(validCard.toString());

//                    System.out.println("Hand des Spielers: ");
//                    currentPlayer.printHand();
                    currentPlayer = nextPlayer();
                    System.out.println("Neuer Spieler: " + currentPlayer.getName() + " " + currentPlayer.printHand());
                }
            }

            assert validCard != null;
            if (validCard.value == Value.COLOR && validCard.type == Type.WILD) {
                System.out.println("Welche Farbe soll gewählt werden?");

                if (input.nextLine().equals("BLUE"))
                    validCard.setType(Type.BLUE);

                if (input.nextLine().equals("RED"))
                    validCard.setType(Type.RED);

                if (input.nextLine().equals("GREEN"))
                    validCard.setType(Type.GREEN);

                if (input.nextLine().equals("YELLOW"))
                    validCard.setType(Type.YELLOW);

                else {
                    playCard();
                }
            }


//            if (cardInput.equals("WILD COLOR") || cardInput.equals("WILD PLUS4")) {
//                System.out.println("Welche Farbe soll gewählt werden?");
//
//                String color = input.nextLine();
//                if (color.equals("BLUE") || color.equals("RED") || color.equals("GREEN") || color.equals("YELLOW")) {
//                    System.out.println("Karte kann gespielt werden");
//                } else {
//                    System.out.println("Bitte gültige Farbe wählen!");
//                }
//
//            }
            // TODO schleife implementieren
            if (validCard.value == Value.REVERSE) {
                System.out.println("METHODE REVERSE WIRD AUFGERUFEN");

                reverseDirection();
            }
            if (validCard.value == Value.SKIP) {
                System.out.println("METHODE SKIP WIRD AUFGERUFEN");
                skipPlayer();
            }
            if (validCard.value == Value.COLOR && validCard.type == Type.WILD) {
                changeColor(input);
            }
        }
    }

    public void reverseDirection() {
        currentPlayer = nextPlayer();
        currentPlayer = nextPlayer();
        currentPlayer = nextPlayer();
        System.out.println(currentPlayer.getName() + currentPlayer.printHand());
    }

    public void changeColor(Scanner input) {
        System.out.println("Welche Farbe willst du, oida?");
        switch (input.nextLine()) {
            case "RED":
                stapel.obersteKarte().setType(Type.RED);
                break;
            case "BLUE":
                stapel.obersteKarte().setType(Type.BLUE);
                break;
            case "YELLOW":
                stapel.obersteKarte().setType(Type.YELLOW);
                break;
            case "GREEN":
                stapel.obersteKarte().setType(Type.GREEN);
                break;
        }
        System.out.println("Neue Farbe ist: " + stapel.obersteKarte().type);
    }

    public void skipPlayer() {
        currentPlayer = nextPlayer();
        System.out.println("SKIP PLAYER : " + currentPlayer.getName() + currentPlayer.printHand());
    }

    public Player nextPlayer() {
        System.out.println("Momentan spielt: " + currentPlayerIndex + "NAME DES SPIELERS : " + currentPlayer.getName());
        currentPlayerIndex++;
        if (currentPlayerIndex > 3) {
            currentPlayerIndex = 0;
        }

        return players.get(currentPlayerIndex);
    }

    public boolean cardCanBePlayed(Card handCard, Card ablageStapelCard) {
        System.out.println("FUNKTION CARD CAN BE PLAYED");
        System.out.println("Ihre Eingabe war: " + handCard.toString());
        //handCard.setCard(input.toString());

        if (handCard.getType() == ablageStapelCard.getType()) { //z.B. beide sind rote Karten
            //  System.out.println("Type of hand card: " + handCard.getType());
            System.out.println("Die oberste Karte ist: " + handCard);

            return true;
        } else if (handCard.getValue() == ablageStapelCard.getValue()) { //z.B. beide haben den Wert 3
            System.out.println("Die oberste Karte ist: " + handCard);
            return true;
        } else if (handCard.type == Type.WILD) {
            return true;
        }
        System.out.println("Karte kann nicht gespielt werden. Bitte andere Karte wählen. Oberste Karte : " + stapel.obersteKarte());
        //cardCanBePlayed(handCard, ablageStapelCard);
        return false;


    }

}
