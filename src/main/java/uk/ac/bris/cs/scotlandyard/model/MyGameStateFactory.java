package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

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

		private MyGameState(final GameSetup setup, final ImmutableSet<Piece> remaining, final ImmutableList<LogEntry> log, final Player mrX, final List<Player> detectives){

			if(setup == null) throw new IllegalArgumentException("setup is null");
			if(remaining == null) throw new IllegalArgumentException("remaining is null");
			if(log == null) throw new IllegalArgumentException("log is null");
			if(mrX == null) throw new NullPointerException("mrX is null");
			if(detectives == null) throw new IllegalArgumentException("detectives is null");

			this.setup = setup;
			this.remaining = remaining;
			this.log = log;
			this.mrX = mrX;
			this.detectives = detectives;


			//checks!!
			// all detectives have different locations
			ArrayList<Integer> locations = new ArrayList<Integer>();
			for (Player p : detectives){
				locations.add(p.location());
			}

			for (int i = 0 ; i < locations.size(); i++){
				for (int j = 0 ; j < locations.size(); j++){
					if ( i == j){
						continue;
					}
					if (Objects.equals(locations.get(i), locations.get(j))){
						throw new IllegalArgumentException("detectives have the same location");
				}
			}



			}
			// the detectives in the list are indeed detective pieces
			for(Piece p : remaining){
				if (!p.isDetective()){
					throw new IllegalArgumentException("remaining pieces include detective");
				}
			}
			//mrx is the black piece
			if (mrX.piece().webColour() != "#000") throw new IllegalArgumentException("wrong colour");
//			//no duplicate game pieces

		}
		@Override public GameSetup getSetup() {  return setup; }
		@Override  public ImmutableSet<Piece> getPlayers() { return remaining; }

		@Override
		public @NonNull Optional<Integer> getDetectiveLocation(Detective detective) {
			return Optional.empty();
		}

		@Override
		public @NonNull Optional<TicketBoard> getPlayerTickets(Piece piece) {
			return Optional.empty();
		}

		@Override
		public @NonNull ImmutableList<LogEntry> getMrXTravelLog() {
			return null;
		}

		@Override
		public @NonNull ImmutableSet<Piece> getWinner() {
			return null;
		}

		@Override
		public @NonNull ImmutableSet<Move> getAvailableMoves() {
			return null;
		}

		@Override public GameState advance(Move move) {  return null;  }
	}


}
