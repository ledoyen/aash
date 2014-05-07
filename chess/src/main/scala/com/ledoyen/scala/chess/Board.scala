package com.ledoyen.scala.chess

object Board {
  val RANK_RANGE = 0 to 7
  val FILE_RANGE = 0 to 7

  def initInitialBoard(player1: Player, player2: Player, innerRepresentation: Array[Array[Option[(Player, Piece)]]]) = {
    for (file <- FILE_RANGE) {
      innerRepresentation(RANK_RANGE.min + 1)(file) = Some((player1, Pawn))
      innerRepresentation(RANK_RANGE.max - 1)(file) = Some((player2, Pawn))
    }
    
    placePiece(Rook, 0, player1, player2, innerRepresentation)
    placePiece(Knight, 1, player1, player2, innerRepresentation)
    placePiece(Bishop, 2, player1, player2, innerRepresentation)
    placePiece(Queen, 3, player1, player2, innerRepresentation)
    placePiece(King, 4, player1, player2, innerRepresentation)
    placePiece(Bishop, 5, player1, player2, innerRepresentation)
    placePiece(Knight, 6, player1, player2, innerRepresentation)
    placePiece(Rook, 7, player1, player2, innerRepresentation)
  }
  
  def placePiece(piece: Piece, file: Int, player1: Player, player2: Player, innerRepresentation: Array[Array[Option[(Player, Piece)]]]) = {
    innerRepresentation(RANK_RANGE.min)(file) = Some((player1, piece))
    innerRepresentation(RANK_RANGE.max)(file) = Some((player2, piece))
  }
}

class Board {

  val player1 = Player(White)
  val player2 = Player(Black)

  val innerRepresentation = Array.fill[Array[Option[(Player, Piece)]]](Board.RANK_RANGE.max + 1)(Array.fill[Option[(Player, Piece)]](Board.FILE_RANGE.max + 1)(None))

  Board.initInitialBoard(player1, player2, innerRepresentation)

  def displayBoard = {
    for (rank <- Board.RANK_RANGE.reverse) {
      for (file <- Board.FILE_RANGE) {
    	  val piece = innerRepresentation(rank)(file).map(tuple => tuple._2.toChar(tuple._1.color))
    	  piece match {
    	    case Some(c) => print(c)
    	    case None => print(0x2610.toChar)
    	  }
      }
      print('\n')
    }
  }
}

