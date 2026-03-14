package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableMap;
import jakarta.annotation.Nonnull;

import org.checkerframework.checker.nullness.qual.NonNull;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

import java.sql.Array;
import java.util.*;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;
import uk.ac.bris.cs.scotlandyard.model.Move.*;
import uk.ac.bris.cs.scotlandyard.model.Piece.*;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.*;

/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState> {

	@Nonnull @Override public GameState build(
			GameSetup setup,
			Player mrX,
			ImmutableList<Player> detectives) {
		// TODO
		return new MyGameState(setup, ImmutableSet.of(MrX.MRX), ImmutableList.of(), mrX, detectives);
//		throw new RuntimeException("Implement me!");
	}

	private final class MyGameState implements GameState {
		private GameSetup setup;
		private ImmutableSet<Piece> remaining;
		private ImmutableList<LogEntry> log;
		private Player mrX;
		private List<Player> detectives;
		private ImmutableSet<Move> moves;
		private ImmutableSet<Piece> winner;

		private MyGameState(final GameSetup setup, final ImmutableSet<Piece> remaining, final ImmutableList<LogEntry> log, final Player mrX, final List<Player> detectives) {

			if (setup == null) throw new NullPointerException("setup is null");
			if (remaining == null) throw new NullPointerException("remaining is null");
			if (log == null) throw new NullPointerException("log is null");
			if (mrX == null) throw new NullPointerException("mrX is null");
			if (detectives == null) throw new NullPointerException("detectives is null");

			this.setup = setup;
			this.remaining = remaining;
			this.log = log;
			this.mrX = mrX;
			this.detectives = detectives;

			//checks!!
			// all detectives have different locations
			ArrayList<Integer> locations = new ArrayList<Integer>();
			for (Player p : detectives) {
				locations.add(p.location());
			}

			for (int i = 0; i < locations.size(); i++) {
				for (int j = 0; j < locations.size(); j++) {
					if (i == j) {
						continue;
					}
					if (Objects.equals(locations.get(i), locations.get(j))) {
						throw new IllegalArgumentException("detectives have the same location");
					}
				}


			}
			// the detectives in the list are indeed detective pieces
			for (Piece p : remaining) {
				if (p.isDetective()) {
					throw new IllegalArgumentException("remaining pieces include detective");
				}
			}
			//mrx is the black piece
			if (mrX.piece().webColour() != "#000") throw new IllegalArgumentException("wrong colour");

//			//no duplicate game pieces .. I'm assuming this is covered by the fact that you can't put two duplicate pieces into a set

		}

		private static Set<SingleMove> makeSingleMoves(GameSetup setup, List<Player> detectives, Player player, int source) {

			// TODO create an empty collection of some sort, say, HashSet, to store all the SingleMove we generate
			HashSet<Move> Moves = new HashSet<Move>();
			ArrayList<Integer> DetLocations = new ArrayList<Integer>();
			for (Player det : detectives){
				DetLocations.add(det.location());
			}
			for (int destination : setup.graph.adjacentNodes(source)) {
				for (Integer location : DetLocations){
					if (destination == location){
						break;
					}
				}
				// TODO find out if destination is occupied by a detective
				//  if the location is occupied, don't add to the collection of moves to return

				for (Transport t : setup.graph.edgeValueOrDefault(source, destination, ImmutableSet.of())) {

					// TODO find out if the player has the required tickets
					//  if it does, construct a SingleMove and add it the collection of moves to return
				}

				// TODO consider the rules of secret moves here
				//  add moves to the destination via a secret ticket if there are any left with the player
			}

			// TODO return the collection of moves
			return null;
		}

			@Override public GameSetup getSetup () {
				return setup;
			}
			@Override public ImmutableSet<Piece> getPlayers () {
				return remaining;
			}

			@Override
			public @NonNull Optional<Integer> getDetectiveLocation (Detective detective){
				for (Player d : detectives){
					if(d.piece() == detective){
						return Optional.of(d.location());
					}
				}
				return Optional.empty();
			}

			@Override
			public @NonNull Optional<TicketBoard> getPlayerTickets (Piece piece){
//				for (Player d : detectives){
//					if(d.piece() == piece){
//						d.tickets()
//					}
//				}
				// TODO there must be a more efficient way to do this by implementing immutable board but there isn't a reference at the moment
				return Optional.empty();
			}

			@Override
			public @NonNull ImmutableList<LogEntry> getMrXTravelLog () {
				return log;
			}

			@Override
			public @NonNull ImmutableSet<Piece> getWinner () {
				return null;
			}

			@Override
			public @NonNull ImmutableSet<Move> getAvailableMoves () {
				return null;
			}

			@Override public GameState advance (Move move){
				return null;
			}
		}

	}
