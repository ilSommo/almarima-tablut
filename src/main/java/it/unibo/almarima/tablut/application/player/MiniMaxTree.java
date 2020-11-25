package it.unibo.almarima.tablut.application.player;

import java.util.List;

import it.unibo.almarima.tablut.application.domain.BoardState;
import it.unibo.almarima.tablut.application.domain.Move;
import it.unibo.almarima.tablut.application.domain.Valuation;
import it.unibo.almarima.tablut.application.heuristics.Heuristic;


public class MiniMaxTree {

    public class TimeLimitException extends Exception {

        public static final long serialVersionUID = 1L;

        public TimeLimitException(String errorMessage) {
            super(errorMessage);
        }
    }

    public int maxDepth;
    public BoardState headBoardState;
    public long endTime;
	public Heuristic h;
	public List<BoardState> history;

    public MiniMaxTree(int maxDepth, BoardState headBoardState, long endTime , Heuristic h, List<BoardState> history) {
        this.headBoardState = headBoardState;
        this.maxDepth = maxDepth;
        this.endTime = endTime;
		this.h = h ;
		this.history = history;
    }

    // This function differs from minimax as it needs to keep track of not just max and min
	// but also the corresponding moves that led to those values.
    public Move getBestMove() throws TimeLimitException {
        
        int depth = 0;
		Valuation alpha = new Valuation(0.0, maxDepth+1);      //TODO: magari depth+1
        Valuation beta = new Valuation(1.0, maxDepth+1);
        
        // if the player with the turn is a WHITE then maximize
		if (headBoardState.getTurnPlayer() == BoardState.WHITE) {
			Move maxMove = null;
			List<Move> nextMoves = this.headBoardState.getAllLegalMoves();
			// iterate through all possible moves
			for (Move move : nextMoves) {
				// clone the bs and process the move to generate a new board
				BoardState childBS = (BoardState) this.headBoardState.clone();
				childBS.processMove(move);

				/*TODO: Da controllare, controllo se la board processata è già presente nella history*/
				if(history.contains(childBS))
					continue;

				// recursively run minimax on the child board state
				Valuation childValuation = minimax(childBS, depth+1, alpha.clone(), beta.clone());
                
                //TODO: scegliere la mossa in base ai valori di alfa beta e h ( e anche la profondità se sono uguali)
                if (childValuation.gethVal()>alpha.gethVal() || (childValuation.gethVal()==alpha.gethVal() && childValuation.getDepthAttained() < alpha.getDepthAttained() ) ){
                    alpha = childValuation;
                    maxMove = move;
				}
			}
			System.out.println("alpha = "+alpha.toString());
			return maxMove;
		}
		else {
			// if the player with the turn is a BLACK then minimize
			Move minMove = null;
			List<Move> nextMoves = this.headBoardState.getAllLegalMoves();
			// iterate through all possible moves
			for (Move move : nextMoves) {
				// clone the bs and process the move to generate a new board
				BoardState childBS = (BoardState) this.headBoardState.clone();
				childBS.processMove(move);

				/*TODO: Da controllare, controllo se la board processata è già presente nella history*/
				if(history.contains(childBS))
					continue;
				// recursively run minimax on the child board state
                Valuation childValuation = minimax(childBS, depth+1, alpha.clone(), beta.clone());
                
                //TODO: scegliere la mossa in base ai valori di alfa beta e h ( e anche la profondità se sono uguali)
                if (childValuation.gethVal()<beta.gethVal() || (childValuation.gethVal()==beta.gethVal() && childValuation.getDepthAttained() < beta.getDepthAttained() ) ){
                    beta = childValuation;
                    minMove = move;
				}
			}
			System.out.println("beta = "+alpha.toString());
			return minMove;
		}
    }
    
    // Implementation of minmax alg with alpha beta pruning
	private Valuation minimax(BoardState nodeBS, int depth, Valuation alpha, Valuation beta) throws TimeLimitException {
		boolean updated = false;
		
        if (System.currentTimeMillis() > endTime) {
			throw new TimeLimitException("");
		}
		// if node is a leaf, then evaluate with h function
		if (depth == this.maxDepth) {
			return new Valuation(h.evaluate(nodeBS), depth);
		}
		// if there is a winner return the winner (corresponds to the max and min h values of 0 or 1)
		if (nodeBS.getWinner() == BoardState.BLACK || nodeBS.getWinner() == BoardState.WHITE) {
			return new Valuation(nodeBS.getWinner(), depth);
		}
        
        //maximizing heuristic 
        if (nodeBS.getTurnPlayer()== BoardState.WHITE){
			List<Move> nextMoves = nodeBS.getAllLegalMoves();
			// iterate through all possible moves
			for (Move move : nextMoves) {
				// clone the bs and process the move to generate a new board
				BoardState childBS = (BoardState) nodeBS.clone();
				childBS.processMove(move);
				/*TODO: Da controllare, controllo se la board processata è già presente nella history*/
				if(history.contains(childBS))
					continue;
			
				// recursively run minimax on the child board state
				Valuation childValuation = minimax(childBS, depth+1, alpha.clone(), beta.clone());
                
                //TODO: scegliere la mossa in base ai valori di alfa beta e h ( e anche la profondità se sono uguali)
                if (childValuation.gethVal()>alpha.gethVal() || (childValuation.gethVal()==alpha.gethVal() && childValuation.getDepthAttained() < alpha.getDepthAttained() ) ){
					alpha = childValuation;
					updated = true;

                }
                if (alpha.gethVal()>=beta.gethVal()){
                    break;
                }
			}
			if (!updated) {
				return new Valuation(alpha.gethVal(), maxDepth);
			}
			return alpha;
        }

        //minimizing heuristic 
        else {
			List<Move> nextMoves = nodeBS.getAllLegalMoves();
			// iterate through all possible moves
			for (Move move : nextMoves) {
				// clone the bs and process the move to generate a new board
				BoardState childBS = (BoardState) nodeBS.clone();
				childBS.processMove(move);

				/*TODO: Da controllare, controllo se la board processata è già presente nella history*/
				if(history.contains(childBS))
					continue;

				// recursively run minimax on the child board state
				Valuation childValuation = minimax(childBS, depth+1, alpha.clone(), beta.clone());
                
                //TODO: scegliere la mossa in base ai valori di alfa beta e h ( e anche la profondità se sono uguali)
                if (childValuation.gethVal()<beta.gethVal() || (childValuation.gethVal()==beta.gethVal() && childValuation.getDepthAttained() < beta.getDepthAttained() ) ){
					beta = childValuation;
					updated = true;

                }
                if (beta.gethVal()<=alpha.gethVal()){
                    break;
				}
			}
			if (!updated) {
				return new Valuation(beta.gethVal(), maxDepth);
			}
			return beta;
        }
    }
}
