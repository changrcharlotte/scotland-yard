package uk.ac.bris.cs.scotlandyard.model;


import com.google.common.collect.ImmutableList;
import java.util.*;
import com.google.common.collect.ImmutableSet;
import jakarta.annotation.Nonnull;

import org.checkerframework.checker.nullness.qual.NonNull;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;

/**
 * cw-model
 * Stage 2: Complete this class
 */
public final class MyModelFactory implements Factory<Model> {

	@Nonnull @Override public Model build(GameSetup setup,
	                                      Player mrX,
	                                      ImmutableList<Player> detectives) {
		return new MyModel(setup, mrX, detectives);

	}

	private final class MyModel implements Model{

		private ImmutableList <Board.GameState> Gamestates;
		private ImmutableSet<Observer> Observers;


		private MyModel (GameSetup setup, Player mrX, ImmutableList<Player> detectives) {
			Observers = ImmutableSet.of();
			MyGameStateFactory factory = new MyGameStateFactory();
			Board.GameState state = factory.build(setup,mrX, detectives);
			Gamestates = ImmutableList.<Board.GameState>builder().add(state).build();

		}

		@Override
		public @NonNull Board getCurrentBoard() {
			if(!Gamestates.isEmpty()){
				return Gamestates.get(Gamestates.size()-1);
			}
			return null;
		}

		@Override
		public void registerObserver(Model.Observer observer) {
			if(Observers.contains(observer)) throw new IllegalArgumentException("observer is already registered");
			this.Observers = ImmutableSet.<Observer>builder().addAll(Observers).add(observer).build();
		}

		@Override
		public void unregisterObserver(Model.Observer observer) {
			if(observer == null)throw new NullPointerException("observer is null");
			if(!Observers.contains(observer)) throw new IllegalArgumentException("observer was never registered");
			Set<Observer> obs = new HashSet<Observer>();
			for(Observer o : Observers){
				if (o != observer){
					obs.add(o);
				}
			}

			this.Observers = ImmutableSet.<Observer>builder().addAll(obs).build();
		}

		@Override
		public @NonNull ImmutableSet<Observer> getObservers() {
			return Observers;
		}

		@Override public void chooseMove(@Nonnull Move move){
			// TODO Advance the model with move, then notify all observers of what what just happened.
			//  you may want to use getWinner() to determine whether to send out Event.MOVE_MADE or Event.GAME_OVER
			Board.GameState gs;

			if(!Gamestates.isEmpty()){

				Board.GameState newgs = Gamestates.get(Gamestates.size()-1).advance(move);
				Gamestates = ImmutableList.<Board.GameState>builder().addAll(Gamestates).add(newgs).build();
				if(newgs.getWinner().size() > 0){ //contains(Piece.MrX.MRX)
					for(Observer o : Observers){
						o.onModelChanged(getCurrentBoard(), Observer.Event.GAME_OVER);
					}
				}
				else{
					for(Observer o : Observers){
						o.onModelChanged(getCurrentBoard(), Observer.Event.MOVE_MADE);
					}
				}

			}




		}

	}
}
