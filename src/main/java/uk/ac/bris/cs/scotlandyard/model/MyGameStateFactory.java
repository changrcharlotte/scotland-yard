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
		return new MyGameState(setup, ImmutableSet.of(MrX.MRX), ImmutableList.of(), mrX, detectives);
//		throw new RuntimeException("Implement me!");
	}

	private final class MyGameState implements GameState {
		private GameSetup setup;
		private ImmutableSet<Piece> remaining; // all the players that are set to make their move. For example mrX first then all the detectives in the following round.
		private ImmutableList<LogEntry> log;
		private Player mrX;
		private List<Player> detectives;
		private ImmutableSet<Move> moves;
		private ImmutableSet<Piece> winner;


		private static Set<Move.SingleMove> makeSingleMoves(GameSetup setup, List<Player> detectives, Player player, int source) {

			// create an empty collection of some sort, say, HashSet, to store all the SingleMove we generate
			HashSet<Move.SingleMove> Moves = new HashSet<Move.SingleMove>();
			ArrayList<Integer> DetLocations = new ArrayList<Integer>();

			for (Player det : detectives){
				DetLocations.add(det.location());
			}
			for (int destination : setup.graph.adjacentNodes(source)) {
				Boolean taken = false;
				for (Integer location : DetLocations){
					if (destination == location){
						taken = true;
						break;
					}
				}
				// find out if destination is occupied by a detective
				//  if the location is occupied, don't add to the collection of moves to return
				if(!taken){
					for (Transport t : setup.graph.edgeValueOrDefault(source, destination, ImmutableSet.of())) {

						ImmutableMap<ScotlandYard.Ticket, Integer> tickets = player.tickets();
						int tk = tickets.getOrDefault(t.requiredTicket(), 0);

						if(tk >= 1){
							Move.SingleMove mv = new Move.SingleMove(player.piece(), source, t.requiredTicket(), destination);
							Moves.add(mv);
						}

						// find out if the player has the required tickets
						//  if it does, construct a SingleMove and add it the collection of moves to return
					}

					//  consider the rules of secret moves here
					//  add moves to the destination via a secret ticket if there are any left with the player

					if (player.tickets().getOrDefault(Ticket.SECRET, 0) >= 1){
						Move.SingleMove mv = new Move.SingleMove(player.piece(), source, Ticket.SECRET, destination);
						Moves.add(mv);
					}
				}
				}


			// return the collection of moves
			return Moves;
		}

		private static Set<Move.DoubleMove> makeDoubleMoves(GameSetup setup, List<Player> detectives, Player mrX, int source) {
			// create an empty collection of some sort, say, HashSet, to store all the SingleMove we generate
			HashSet<Move.DoubleMove> Moves = new HashSet<DoubleMove>();
			ArrayList<Integer> DetLocations = new ArrayList<Integer>();

			for (Player det : detectives){
				DetLocations.add(det.location());
			}


			for (int destination1 : setup.graph.adjacentNodes(source)) {
				for (int destination2 : setup.graph.adjacentNodes(destination1)){
					Boolean taken = false;
					for (Integer location : DetLocations){
						if (destination2 == location || destination1 == location){
							taken = true;
							break;
						}
					}
					// find out if destination is occupied by a detective
					//  if the location is occupied, don't add to the collection of moves to return
					if(!taken){
						for (Transport t1 : setup.graph.edgeValueOrDefault(source, destination1, ImmutableSet.of())) {

							ImmutableMap<ScotlandYard.Ticket, Integer> tickets1 = mrX.tickets();
							int tk1 = tickets1.getOrDefault(t1.requiredTicket(), 0);

							if(tk1 >= 1){
								for (Transport t2 : setup.graph.edgeValueOrDefault(destination1, destination2, ImmutableSet.of())) {

									ImmutableMap<ScotlandYard.Ticket, Integer> tickets2 = mrX.tickets();
									int tk2 = tickets2.getOrDefault(t2.requiredTicket(), 0);

									if((tk2 >= 1) && (t2.requiredTicket() != t1.requiredTicket()) || ((tk2 >= 2) && (t2.requiredTicket() == t1.requiredTicket()))){
										Move.DoubleMove mv = new Move.DoubleMove(mrX.piece(), source, t1.requiredTicket(), destination1, t2.requiredTicket() , destination2 );
										Moves.add(mv);
									}

								}
								int sect =mrX.tickets().getOrDefault(Ticket.SECRET, 0);
								if ( ((sect >= 1) && (t1 != Transport.FERRY)) || ((sect >=2 ) && ( t1 == Transport.FERRY)) ){
									Move.DoubleMove mv = new Move.DoubleMove(mrX.piece(), source,t1.requiredTicket(), destination1, Ticket.SECRET, destination2);
									Moves.add(mv);
								}
							}

						}


						//this is where secret is the first move.
						if(mrX.tickets().getOrDefault(Ticket.SECRET, 0) >=1){
							for (Transport t2 : setup.graph.edgeValueOrDefault(destination1, destination2, ImmutableSet.of())) {

								ImmutableMap<ScotlandYard.Ticket, Integer> tickets2 = mrX.tickets();
								int tk2 = tickets2.getOrDefault(t2.requiredTicket(), 0);

								if(tk2 >= 1){
									Move.DoubleMove mv = new Move.DoubleMove(mrX.piece(), source, Ticket.SECRET, destination1, t2.requiredTicket() , destination2 );
									Moves.add(mv);
								}

							}
							if (mrX.tickets().getOrDefault(Ticket.SECRET, 0) >= 2){
								Move.DoubleMove mv = new Move.DoubleMove(mrX.piece(), source, Ticket.SECRET, destination1, Ticket.SECRET, destination2);
								Moves.add(mv);
							}
						}


					}
				}

			}


			// return the collection of moves
			return Moves;
		}

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

			//checks!



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
			// the detectives in the list are indeed detective pieces or if detectives have double tickets
			//
			for (Player d : detectives) {
				if (!(d.isDetective())) {
					throw new IllegalArgumentException("detectives in the list aren't actually detective pieces");
				}
				if (d.tickets().getOrDefault(Ticket.DOUBLE, 0) >= 1){
					throw new IllegalArgumentException("detectives should not have double tickets");
				}
				if(d.tickets().getOrDefault(Ticket.SECRET,0 ) >= 1){
					throw new IllegalArgumentException("detectives should not have secret tickets");
				}
			}
			//mrx is the black piece
			if (mrX.piece().webColour() != "#000") throw new IllegalArgumentException("wrong colour");

//			//no duplicate game pieces .. I'm assuming this is covered by the fact that you can't put two duplicate pieces into a set

			HashSet<Move> mvs = new HashSet<>();
//			for (Player det : detectives){
//				mvs.addAll(makeSingleMoves(setup, detectives,det, det.location()) );
//			}
			mvs.addAll(makeSingleMoves(setup, detectives,mrX, mrX.location() ));

			if (mrX.tickets().getOrDefault(Ticket.DOUBLE, 0) >= 1){
				mvs.addAll(makeDoubleMoves(setup, detectives, mrX, mrX.location()));
			}
			this.moves = ImmutableSet.copyOf(mvs);
			if(moves.isEmpty()) throw new IllegalArgumentException("moves are empty");

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
			Player target = null;
			if (mrX.piece() == piece) {
				target = mrX;
			}
			else {
				for (Player d : detectives){
					if(d.piece() == piece){
						target = d;
						break;}
					}
				}

				if (target == null) return Optional.empty();
				ImmutableMap<Ticket, Integer> tickets = target.tickets();
				TicketBoard board = ticket -> tickets.getOrDefault(ticket, 0);
				return Optional.of(board);
			}



		@Override
		public GameState advance(Move move) {
			if (!moves.contains(move)) {
				throw new IllegalArgumentException("Illegal move: " + move);
			}

			if (move instanceof Move.SingleMove) {
				Move.SingleMove single = (Move.SingleMove) move;

				Player newMrX = mrX;
				List<Player> newDetectives = new ArrayList<>(detectives);
				ImmutableList<LogEntry> newLog = log;
				Set<Piece> newRemaining = new HashSet<>(remaining);

				if (single.commencedBy().isMrX()) {
					newMrX = mrX.use(single.ticket).at(single.destination);

					boolean reveal = setup.moves.get(log.size());
					LogEntry entry;
					if (reveal) {
						entry = LogEntry.reveal(single.ticket, single.destination);
					} else {
						entry = LogEntry.hidden(single.ticket);
					}

					ImmutableList.Builder<LogEntry> builder = ImmutableList.builder();
					builder.addAll(log);
					builder.add(entry);
					newLog = builder.build();

					newRemaining.clear();
					for (Player d : newDetectives) {
						if (!makeSingleMoves(setup, newDetectives, d, d.location()).isEmpty()) {
							newRemaining.add(d.piece());
						}
					}

					if (newRemaining.isEmpty()) {
						newRemaining.add(MrX.MRX);
					}

				} else {
					for (int i = 0; i < newDetectives.size(); i++) {
						Player d = newDetectives.get(i);
						if (d.piece() == single.commencedBy()) {
							Player updated = d.use(single.ticket).at(single.destination);
							newDetectives.set(i, updated);
							newMrX = newMrX.give(single.ticket);
							break;
						}
					}

					newRemaining.remove(single.commencedBy());

					if (newRemaining.isEmpty()) {
						newRemaining.add(MrX.MRX);
					}
				}

				return new MyGameState(
						setup,
						ImmutableSet.copyOf(newRemaining),
						newLog,
						newMrX,
						newDetectives
				);
			}

			if (move instanceof Move.DoubleMove) {
				Move.DoubleMove dbl = (Move.DoubleMove) move;

				Player newMrX = mrX.use(dbl.tickets()).at(dbl.destination2);
				List<Player> newDetectives = new ArrayList<>(detectives);

				int round1 = log.size();
				int round2 = log.size() + 1;

				LogEntry entry1;
				if (setup.moves.get(round1)) {
					entry1 = LogEntry.reveal(dbl.ticket1, dbl.destination1);
				} else {
					entry1 = LogEntry.hidden(dbl.ticket1);
				}

				LogEntry entry2;
				if (setup.moves.get(round2)) {
					entry2 = LogEntry.reveal(dbl.ticket2, dbl.destination2);
				} else {
					entry2 = LogEntry.hidden(dbl.ticket2);
				}

				ImmutableList.Builder<LogEntry> builder = ImmutableList.builder();
				builder.addAll(log);
				builder.add(entry1);
				builder.add(entry2);
				ImmutableList<LogEntry> newLog = builder.build();

				Set<Piece> newRemaining = new HashSet<>();
				for (Player d : newDetectives) {
					if (!makeSingleMoves(setup, newDetectives, d, d.location()).isEmpty()) {
						newRemaining.add(d.piece());
					}
				}

				if (newRemaining.isEmpty()) {
					newRemaining.add(MrX.MRX);
				}

				return new MyGameState(
						setup,
						ImmutableSet.copyOf(newRemaining),
						newLog,
						newMrX,
						newDetectives
				);
			}

			throw new IllegalArgumentException("Unknown move type: " + move);
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
				return moves;
			}

//			@Override public GameState advance (Move move){
//				return null;
//			}
		}

	}
