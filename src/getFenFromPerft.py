import chess

def main():
    fen = "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8"
    depth = 3
    perft(fen, depth)
    
    #open and read the file after the appending:
    f = open("C:\\Users\\student\\Documents\\GitHub\\fyp-chessengine\\src\\fen_list.txt", "r")
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