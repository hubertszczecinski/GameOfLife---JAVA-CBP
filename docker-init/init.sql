CREATE TABLE IF NOT EXISTS board (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    rows INTEGER NOT NULL CHECK (rows > 0),
    columns INTEGER NOT NULL CHECK (columns > 0)
);

CREATE TABLE IF NOT EXISTS cell (
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    value BOOLEAN NOT NULL,
    board_id INTEGER NOT NULL,
    PRIMARY KEY (x, y, board_id),
    FOREIGN KEY (board_id) REFERENCES board(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_cell_board_id ON cell(board_id);
CREATE INDEX IF NOT EXISTS idx_board_name ON board(name);

