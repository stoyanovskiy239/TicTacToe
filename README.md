# Tic-Tac-Toe

## Features
- Multiplayer over HTTP
- Fully synchronized game state
- Auto recovery from disconnects
- Two-way resets & consistency validation

## How to Run
Start 2 instances on different ports with different opponent URLs.

## Endpoints
- `POST /game/move`
- `GET /game/state`
- `GET /game/consistent`
- `POST /game/restart`
