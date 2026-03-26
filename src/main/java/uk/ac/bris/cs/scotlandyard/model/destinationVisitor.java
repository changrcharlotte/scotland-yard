package uk.ac.bris.cs.scotlandyard.model;

public class destinationVisitor implements Move.Visitor<Integer>{


    //assuming this means final destination only and not intermediary positions

    @Override
    public Integer visit(Move.SingleMove move) {
        return move.destination;
    }

    @Override
    public Integer visit(Move.DoubleMove move) {
        return move.destination2;
    }
}
