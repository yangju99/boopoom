# Boopoom Setup Guide

## 1. Create the `boopoom` database in MySQL

### 1-1. Install the MySQL client

Example (macOS with Homebrew):

```bash
brew install mysql-client
```

### 1-2. Update MySQL password in project files

Before running the DB init script, replace the MySQL password in these files with your own:

- `src/main/resources/application.properties`
- `init_db.sh`

### 1-3. Grant execute permission to `init_db.sh` and run it

```bash
chmod +x init_db.sh
./init_db.sh
```

This script creates the `boopoom` database.

## 2. Seed sample JSON data into DB (via script)

### 2-1. JSON file locations

- Products:
  - `data/product/gpu.json`
  - `data/product/ssd.json`
  - `data/product/ram.json`
- Users:
  - `data/user/users.json`
- Trades:
  - `data/trade/trades.json`

### 2-2. Run the seed script

```bash
chmod +x seed_data.sh
./seed_data.sh
```

`seed_data.sh` starts the Spring context in non-web mode, loads JSON records, inserts them, and exits.
