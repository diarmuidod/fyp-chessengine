import chess

def main():
    fen = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -"
    depth = 3
    perft(fen, depth)
    
    #open and read the file after the appending:
    f = open("C:\\Users\\student\\Documents\\GitHub\\fyp-chessengine\\src\\fen_list.txt", "r")
    print("File written successfully")

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

    if(len(moves) != 0):
        f.write(fen + "--" + moves + "\n")
    
    f.close()
    
main()