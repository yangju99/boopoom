# Boopoom Setup Guide

## 1. Create the `boopoom` database in MySQL

### 1-1. Install the MySQL client

Example (macOS with Homebrew):

```bash
brew install mysql-client
```

### 1-2. Grant execute permission to `init_db.sh` and run it

```bash
chmod +x init_db.sh
./init_db.sh
```

This script creates the `boopoom` database.
