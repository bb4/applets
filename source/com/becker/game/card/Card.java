package com.becker.game.card;

import java.util.*;

public class Card {

    public enum Suit {
        HEARTS, DIAMONDS, CLUBS, SPADES
    }

    private final Rank rank_;
    private final Suit suit_;

    public Card(Rank rank, Suit suit) {
        this.rank_ = rank;
        this.suit_ = suit;
    }

    public Card(String cardToken) {
        int len = cardToken.length();
        assert (len < 3);

        this.rank_ = Rank.getRankForSymbol(cardToken.substring(0, len-1));
        char suit = cardToken.charAt(len-1);
        switch (suit) {
            case 'H' : this.suit_ = Suit.HEARTS; break;
            case 'D' : this.suit_ = Suit.DIAMONDS; break;
            case 'C' : this.suit_ = Suit.CLUBS; break;
            case 'S' : this.suit_ = Suit.SPADES; break;
            default: this.suit_ = null;  assert false;
        }
    }


    public Rank rank() { return rank_; }

    public Suit suit() { return suit_; }

    public String toString() { return rank_ + " of " + suit_; }

    private static final List<Card> protoDeck = new ArrayList<Card>();

    // Initialize prototype deck
    static {
        for (Suit suit : Suit.values())
            for (Rank rank : Rank.values())
                protoDeck.add(new Card(rank, suit));
    }

    public static List newDeck() {
        List deck = new ArrayList<Card>(protoDeck); // Return copy of prototype deck
        Collections.shuffle(deck);
        return deck;
    }


    public static void main(String[] args) {

        List deck = newDeck();
        System.out.println("deck="+deck);
    }
}