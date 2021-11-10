import chess

def main():
    fen = "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1"
    depth = 4
    perft(fen, depth)
    
    #open and read the file after the appending:
    f = open("fen_list.txt", "r")
    print(f.read())

def perft(fen, depth):
    board = chess.Board(fen)
	
    if(depth == 0):
        writeFenToFile(fen)
    else:
        for move in list(board.legal_moves):
            board.push_uci(move.uci())
            perft(board.fen(), depth - 1)
            board.pop()
	
def writeFenToFile(fen):
    f = open("fen_list.txt", "a+")
    data = f.readlines()

    board = chess.Board(fen)
    moves = str(board.legal_moves).split(" (")[1]
    moves = moves.split(">")[0]
    moves = moves[:-1:]
    f.write(fen + "--" + moves + "\n")
    
    f.close()
    
main()