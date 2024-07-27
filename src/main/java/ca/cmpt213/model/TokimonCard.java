package ca.cmpt213.model;

public class TokimonCard {
    private long tid; // Tokimon ID
    private String name;
    private ElementType elementType;
    static private int totalTokimons = 0;

    public enum ElementType {
        FIRE,
        WATER,
        ELECTRIC,
        ICE,
        FLYING,
        ROCK,
        GRASS,
        DRAGON,
        FAIRY,
        GHOST,
        PSYCHIC,
        NORMAL,
        FIGHTING,
        STEEL,
        GROUND,
        BUG
    }

    public TokimonCard() {}

    public TokimonCard(long tid, String name, ElementType elementType) {
        this.tid = tid;
        this.name = name;
        this.elementType = elementType;
        totalTokimons++;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }

    public static int getTotalTokimons() {
        return totalTokimons;
    }

    public static void incrementTotalTokimons() {
        totalTokimons++;
    }

    public static void decrementTotalTokimons() {
        totalTokimons--;
    }





}
