import chess

def main():
    fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    depth = 2
    perft(fen, depth)
    
    #open and read the file after the appending:
    f = open("demofile2.txt", "r")
    print(f.read())
    inp = input("Press Enter to exit")

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
    f = open("demofile2.txt", "a+")
    data = f.readlines()
    if fen in data:
        return
    else:
        board = chess.Board(fen)
        movecount = len(list(board.legal_moves))
        f.write(fen + ", " + str(movecount) + "\n")
    
    f.close()
    
main()